import { FileDescriptor, FileTransformationContext, FileType } from "../../../../types";
import { system } from "../../../../system/system";
import { printElementInfo } from "../../../elementInfo";
import { transformSourceFile } from "../../scripts/scripts";
import { collectFactsBefore } from "../../metaTransformers/facts";
import { transformShimsBefore, transformShims } from "../../codeTransformers/shims";
import { printSourceFile } from "../../helpers/source";
import { generateElementId } from "../../../../utilities/utilities";
import { getPropertyName } from "../../helpers/node";
import { WorkflowDescriptor, WorkflowItemDescriptor, PolyglotDescriptor, WorkflowItemType } from "../../../../decorators";
import { remediateTypeScript } from "../../codeTransformers/remediate";
import { transformModuleSystem } from "../../codeTransformers/modules";
import { printPolyglotCode, registerPolyglotDecorators } from "./polyglot";
import { prepareHeaderEmitter } from "../../codeTransformers/header";
import { createWorkflowItemPrologueStatements } from "../../codeTransformers/prologueStatements";
import { buildWorkflowDecorators, registerMethodArgumentDecorators, registerMethodDecorators } from "./decorators";
import { mergeWorkflowXml, printWorkflowXml } from "./presentation";

import * as ts from "typescript";

//@TODO: Take a look at this

export function getWorkflowTransformer(file: FileDescriptor, context: FileTransformationContext) {
	const sourceFile = ts.createSourceFile(file.filePath, system.readFile(file.filePath).toString(), ts.ScriptTarget.Latest, true);
	const workflows: WorkflowDescriptor[] = [];
	const actionSourceFiles: ts.SourceFile[] = [];

	sourceFile.statements.filter(n => n.kind === ts.SyntaxKind.ClassDeclaration).forEach(classNode => {
		registerWorkflowClass(classNode as ts.ClassDeclaration);
	});
	actionSourceFiles.forEach(sf => context.sourceFiles.push(sf));

	return transform;

	function transform() {
		transpileActionItems();

		workflows.forEach(workflowInfo => {
			const targetFilePath = system.changeFileExt(
				system.resolvePath(context.outputs.workflows, workflowInfo.path, workflowInfo.name),
				"",
				[".wf.ts"]);

			const xmlTemplateFilePath = system.changeFileExt(file.filePath, ".xml");
			const xmlTemplate = system.fileExists(xmlTemplateFilePath) ? system.readFile(xmlTemplateFilePath).toString() : undefined;

			context.writeFile(
				`${targetFilePath}.xml`,
				xmlTemplate
					? mergeWorkflowXml(workflowInfo, xmlTemplate)
					: printWorkflowXml(workflowInfo, context)
			);

			context.writeFile(`${targetFilePath}.element_info.xml`, printElementInfo({
				categoryPath: workflowInfo.path.replace(/(\\|\/)/g, "."),
				name: workflowInfo.name,
				type: "Workflow",
				id: workflowInfo.id,
			}));

			const workflowFormFilePath = system.changeFileExt(file.filePath, ".form.json");
			const workflowJsonForm = system.fileExists(workflowFormFilePath) ? system.readFile(workflowFormFilePath).toString() : undefined;
			if (workflowJsonForm) {
				context.writeFile(`${targetFilePath}.form.json`, workflowJsonForm);
			}
		});
	}

	function transpileActionItems(): void {
		const actionItems = workflows.reduce((items, wf) => items.concat(wf.items), <WorkflowItemDescriptor[]>[]);
		actionSourceFiles.forEach((actionSourceFile, i) => {
			const [sourceText] = transformSourceFile(
				actionSourceFile,
				context,
				{
					before: [
						collectFactsBefore,
						transformShimsBefore,
					],
					after: [
						transformShims,
						remediateTypeScript,
						transformModuleSystem,
						prepareHeaderEmitter(context)
					],
				},
				file);
			actionItems[i].sourceText = sourceText;
		});
	}

	function registerWorkflowClass(classNode: ts.ClassDeclaration): void {
		const workflowInfo: WorkflowDescriptor = {
			id: undefined,
			name: classNode.name.text,
			path: undefined,
			version: "1.0.0",
			parameters: [],
			rootItem: null,
			items: [],
			presentation: undefined,
			description: undefined
		};
		const decorators = ts.getDecorators(classNode);
		if (decorators && decorators.length) {
			buildWorkflowDecorators(
				workflowInfo,
				classNode,
				context,
				sourceFile
			);
		}

		classNode.members
			.filter(member => member.kind === ts.SyntaxKind.MethodDeclaration)
			.forEach((methodNode: ts.MethodDeclaration) => {
				registerWorkflowItem(workflowInfo, methodNode);
			});

		workflowInfo.name = workflowInfo.name || classNode.name.text;
		workflowInfo.path = workflowInfo.path || system.joinPath(context.workflowsNamespace || "", system.dirname(file.relativeFilePath));
		workflowInfo.id = workflowInfo.id || generateElementId(FileType.Workflow, `${workflowInfo.path}/${workflowInfo.name}`);
		workflows.push(workflowInfo);
	}

	function registerWorkflowItem(workflowInfo: WorkflowDescriptor, methodNode: ts.MethodDeclaration): void {
		const itemInfo: WorkflowItemDescriptor = {
			name: getPropertyName(methodNode.name),
			input: [],
			output: [],
			sourceText: "",
			itemType: WorkflowItemType.Item,
			target: null
		};

		const polyglotInfo: PolyglotDescriptor = { package: "", method: "" };

		/*-
		 * #%L
		 * vrotsc
		 * %%
		 * Copyright (C) 2023 - 2024 VMware
		 * %%
		 * Build Tools for VMware Aria
		 * Copyright 2023 VMware, Inc.
		 *
		 * This product is licensed to you under the BSD-2 license (the "License"). You may not use this product except in compliance with the BSD-2 License.
		 *
		 * This product may include a number of subcomponents with separate copyright notices and license terms. Your use of these subcomponents is subject to the terms and conditions of the subcomponent's license, as noted in the LICENSE file.
		 * #L%
		 */


		const decoratorPolyglot = registerPolyglotDecorators(methodNode, polyglotInfo);
		registerMethodDecorators(methodNode, workflowInfo, itemInfo);
		registerMethodArgumentDecorators(methodNode, workflowInfo, itemInfo);

		const actionSourceFilePath = system.changeFileExt(sourceFile.fileName, `.${itemInfo.name}.wf.ts`, [".wf.ts"]);
		let actionSourceText = printSourceFile(
			ts.factory.updateSourceFile(
				sourceFile,
				[
					...sourceFile.statements.filter(n => n.kind !== ts.SyntaxKind.ClassDeclaration),
					...createWorkflowItemPrologueStatements(methodNode),
					...methodNode.body.statements
				]));

		//Exists a declaration of a Polyglot decorator
		if (itemInfo.input.length > 0 && itemInfo.output.length > 0 && decoratorPolyglot) {
			const polyglotCall = printPolyglotCode(polyglotInfo.package, polyglotInfo.method, itemInfo.input, itemInfo.output);
			actionSourceText = polyglotCall + actionSourceText;
		}
		const actionSourceFile = ts.createSourceFile(
			actionSourceFilePath,
			actionSourceText,
			ts.ScriptTarget.Latest,
			true);

		actionSourceFiles.push(actionSourceFile);
		workflowInfo.items.push(itemInfo);
	}
}
