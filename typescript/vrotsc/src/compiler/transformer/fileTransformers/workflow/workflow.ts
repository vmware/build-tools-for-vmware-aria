import { FileDescriptor, FileTransformationContext, FileType } from "../../../../types";
import { system } from "../../../../system/system";
import { printElementInfo } from "../../../elementInfo";
import { transformSourceFile } from "../../scripts/scripts";
import { collectFactsBefore } from "../../metaTransformers/facts";
import { transformShimsBefore, transformShims } from "../../codeTransformers/shims";
import { printSourceFile } from "../../helpers/source";
import { generateElementId } from "../../../../utilities/utilities";
import { getPropertyName } from "../../helpers/node";
import { WorkflowDescriptor, WorkflowItemDescriptor, PolyglotDescriptor, WorkflowItemType } from "../../../decorators";
import { remediateTypeScript } from "../../codeTransformers/remediate";
import { transformModuleSystem } from "../../codeTransformers/modules";
import { printPolyglotCode, registerPolyglotDecorators } from "./polyglot";
import { prepareHeaderEmitter } from "../../codeTransformers/header";
import { createWorkflowItemPrologueStatements } from "../../codeTransformers/prologueStatements";
import { buildWorkflowDecorators, registerMethodArgumentDecorators, registerMethodDecorators } from "./decorators";
import { mergeWorkflowXml, printWorkflowXml } from "./presentation";

import * as ts from "typescript";

/**
 * Workflow transformer is responsible from transforming a TypeScript `wf.ts` file into a vRO workflow.
 *
 * It relies on Decorators to extract the workflow information and the workflow items.
 */
export function getWorkflowTransformer(file: FileDescriptor, context: FileTransformationContext) {
	const sourceFile = ts.createSourceFile(file.filePath, system.readFile(file.filePath).toString(), ts.ScriptTarget.Latest, true);
	const workflows: WorkflowDescriptor[] = [];
	const actionSourceFiles: ts.SourceFile[] = [];

	sourceFile.statements
		.filter(node => node.kind === ts.SyntaxKind.ClassDeclaration)
		.forEach(classNode => {
			const workflowInfo = createWorkflowDescriptor(classNode as ts.ClassDeclaration);
			registerWorkflowClass(workflowInfo, classNode as ts.ClassDeclaration);
		});

	actionSourceFiles.forEach(sourceFile => context.sourceFiles.push(sourceFile));

	return function() {
		transpileActionItems(workflows, actionSourceFiles, context, file);

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
	};

	/**
	 * Handles parsing the decorators of a class node and registering the information in the workflowInfo object.
	 *
	 * This is the entry point for the workflow transformation.
	 *
	 * @param workflowInfo The workflow descriptor object.
	 * @param classNode The class node to extract information from.
	 * @returns void
	 */
	function registerWorkflowClass(workflowInfo: WorkflowDescriptor, classNode: ts.ClassDeclaration): void {
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
				const itemInfo = createWorkflowItemDescriptor(methodNode.name);
				workflowInfo.items.push(itemInfo);

				registerWorkflowItem(workflowInfo, itemInfo, methodNode);

				const actionSourceFilePath = system.changeFileExt(sourceFile.fileName, `.${itemInfo.name}.wf.ts`, [".wf.ts"]);
				let actionSourceText = getActionSourceText(methodNode, itemInfo, sourceFile);

				// @TODO: "Unstupify" me
				if (itemInfo.polyglot) {
					actionSourceText = decorateSourceFileTextWithPolyglot(actionSourceText, itemInfo.polyglot, itemInfo);
				}

				actionSourceFiles.push(ts.createSourceFile(
					actionSourceFilePath,
					actionSourceText,
					ts.ScriptTarget.Latest,
					true));
			});

		workflowInfo.name = workflowInfo.name || classNode.name.text;
		workflowInfo.path = workflowInfo.path || system.joinPath(context.workflowsNamespace || "", system.dirname(file.relativeFilePath));
		workflowInfo.id = workflowInfo.id || generateElementId(FileType.Workflow, `${workflowInfo.path}/${workflowInfo.name}`);

		workflows.push(workflowInfo);
	}
}

/**
 * Responsible for decorating the source file text with the Polyglot decorator.
 *
 * This should be called only if the item has a Polyglot decorator.
 *
 * @NOTE: No idea why this is needed... did not test polyglot decorators, tests are passing tho. Anyway, this
 *        should be refactored to adhere to the rest of the canvas items
 */
function decorateSourceFileTextWithPolyglot(actionSourceText: string, polyglotDescriptor: PolyglotDescriptor, itemInfo: WorkflowItemDescriptor): string {
	//Exists a declaration of a Polyglot decorator
	if (itemInfo.input.length > 0 && itemInfo.output.length > 0) {
		const polyglotCall = printPolyglotCode(polyglotDescriptor.package, polyglotDescriptor.method, itemInfo.input, itemInfo.output);

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
		actionSourceText = polyglotCall + actionSourceText;
	}

	return actionSourceText;
}

/**
 * Responsible for extracting the information from a method node and registering data in the workflowInfo object.
 *
 * @param workflowInfo The workflow descriptor object.
 * @param itemInfo The workflow item descriptor that will be populated with information
 * @param methodNode The method node to extract information from.
 * @returns void
 */
function registerWorkflowItem(workflowInfo: WorkflowDescriptor, itemInfo: WorkflowItemDescriptor, methodNode: ts.MethodDeclaration): void {
	registerPolyglotDecorators(methodNode, itemInfo);
	registerMethodDecorators(methodNode, workflowInfo, itemInfo);
	registerMethodArgumentDecorators(methodNode, workflowInfo, itemInfo);
}

/**
 * This function is responsible for returning the source text of the action item.
 *
 * If the item is a decision, it will wrap the method body in a function and register it as a decision.
 *  This wrapping is necessary because "return"s are not allowed in the root of a typescript file, but are allowed in the case of vRO.
 *  The "wrapper" function will later be removed in the transpilation process.
 *  @NOTE: This is 100% due to a typescript limitation, and not a vRO limitation.
 *
 * @param methodNode The method node to extract the source text from.
 * @param itemInfo The item descriptor object.
 * @returns string - The source text of the action item.
 */
function getActionSourceText(methodNode: ts.MethodDeclaration, itemInfo: WorkflowItemDescriptor, sourceFile: ts.SourceFile): string {
	switch (itemInfo.itemType) {
		case WorkflowItemType.Decision:
			itemInfo.itemType = WorkflowItemType.Decision;

			const wrapperFunction = ts.factory.createFunctionDeclaration(
				undefined,
				undefined,
				"wrapper",
				undefined,
				[],
				undefined,
				methodNode.body
			);
			return printSourceFile(
				ts.factory.updateSourceFile(
					sourceFile,
					[
						...sourceFile.statements.filter(n => n.kind !== ts.SyntaxKind.ClassDeclaration),
						...createWorkflowItemPrologueStatements(methodNode),
						wrapperFunction
					]
				)
			);
		default:
			return printSourceFile(
				ts.factory.updateSourceFile(
					sourceFile,
					[
						...sourceFile.statements.filter(n => n.kind !== ts.SyntaxKind.ClassDeclaration),
						...createWorkflowItemPrologueStatements(methodNode),
						...methodNode.body.statements
					]
				)
			);
	}
}

/**
 * Transpiles the action items.
 *
 * This function is responsible for transforming the source files of the action items.
 * It will collect the source text of the action items and store it in the workflow descriptor.
 *
 * @param workflows The workflow descriptors.
 * @param actionSourceFiles The source files of the action items.
 * @param context The file transformation context.
 * @param file The file descriptor.
 * @returns void
 */
function transpileActionItems(
	workflows: WorkflowDescriptor[],
	actionSourceFiles: ts.SourceFile[],
	context: FileTransformationContext,
	file: FileDescriptor
): void {
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

// ---------------------------------------- Utility functions ----------------------------------------

/**
 * Represents the workflow item descriptor information extracted from the property name node
 */
function createWorkflowItemDescriptor(propertyNameNode: ts.PropertyName): WorkflowItemDescriptor {
	return {
		name: getPropertyName(propertyNameNode),
		input: [],
		output: [],
		sourceText: "",
		itemType: WorkflowItemType.Item,
		target: null,
		canvasItemPolymorphicBag: {},
	};
}

/**
 * Represents the workflow information extracted from the TypeScript file.
 */
function createWorkflowDescriptor(classNode: ts.ClassDeclaration): WorkflowDescriptor {
	return {
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
}
