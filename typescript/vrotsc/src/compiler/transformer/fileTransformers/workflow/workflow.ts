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

	/**
	 * Responsible for extracting the information from a method node and registering data in the workflowInfo object.
	 * It will also transpile the method body
	 *
	 * @param workflowInfo The workflow descriptor object.
	 * @param methodNode The method node to extract information from.
	 * @returns void
	 */
	function registerWorkflowItem(workflowInfo: WorkflowDescriptor, methodNode: ts.MethodDeclaration): void {
		const itemInfo: WorkflowItemDescriptor = {
			name: getPropertyName(methodNode.name),
			input: [],
			output: [],
			sourceText: "",
			itemType: WorkflowItemType.Item,
			target: null,
			canvasItemPolymorphicBag: {}
		};

		// @TODO: This can be "unstupified" by moving the logic with the rest of the decorators.
		const polyglotInfo: PolyglotDescriptor = { method: "", package: "" };
		const decoratorPolyglot = registerPolyglotDecorators(methodNode, polyglotInfo);

		registerMethodDecorators(methodNode, workflowInfo, itemInfo);
		registerMethodArgumentDecorators(methodNode, workflowInfo, itemInfo);

		const actionSourceFilePath = system.changeFileExt(sourceFile.fileName, `.${itemInfo.name}.wf.ts`, [".wf.ts"]);
		let actionSourceText = getActionSourceText(methodNode, itemInfo);

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
	function getActionSourceText(methodNode: ts.MethodDeclaration, itemInfo: WorkflowItemDescriptor): string {
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
						]));
		}
	}
}
