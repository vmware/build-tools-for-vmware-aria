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
import { system } from "../../../../system/system";
import { FileDescriptor, FileTransformationContext, FileType } from "../../../../types";
import { generateElementId } from "../../../../utilities/utilities";
import { PolyglotDescriptor, WorkflowDescriptor, WorkflowItemDescriptor } from "../../../decorators";
import { printElementInfo } from "../../../elementInfo";
import { prepareHeaderEmitter } from "../../codeTransformers/header";
import { transformModuleSystem } from "../../codeTransformers/modules";
import { remediateTypeScript } from "../../codeTransformers/remediate";
import { transformShims, transformShimsBefore } from "../../codeTransformers/shims";
import { getPropertyName } from "../../helpers/node";
import { collectFactsBefore } from "../../metaTransformers/facts";
import { transformSourceFile } from "../../scripts/scripts";
import { buildWorkflowDecorators, registerMethodArgumentDecorators, registerMethodDecorators, registerPolyglotDecorators } from "./decorators";
import { mergeWorkflowXml, printPolyglotCode, printWorkflowXml } from "./presentation";

import * as ts from "typescript";
import UserInteractionDecoratorStrategy from "./decorators/userInteractionDecoratorStrategy";

const defaultUserInteractionJson = '{"schema":{},"layout":{"pages":[]},"itemId":"{{itemId}}"}';
const userInteractionFormFileNameTemplate = "{{workflowName}}_input_form_{{itemId}}.form.json";
const userInteractionFormSourceFileNameByIdTemplate = "{{workflowFileName}}_input_form_{{itemId}}.wf.form.json";
const userInteractionFormSourceFileNameByFunctionTemplate = "{{workflowFileName}}_input_form_{{functionName}}.wf.form.json";

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

    return function () {
        transpileActionItems(workflows, actionSourceFiles, context, file);
        workflows.forEach(workflowInfo => {
            generateWorkflowXmlAndForms(workflowInfo);
        });
    };

    /**
     * Handles generating workflow xml files and form json files.
     *
     * @param workflowInfo The workflow descriptor object.
     * @returns void
     */
    function generateWorkflowXmlAndForms(workflowInfo: WorkflowDescriptor): void {
        const workflowName = workflowInfo.name;
        const targetFilePath = system.changeFileExt(
            system.resolvePath(context.outputs.workflows, workflowInfo.path, workflowName),
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
            name: workflowName,
            type: "Workflow",
            id: workflowInfo.id,
        }));

        const workflowFormFilePath = system.changeFileExt(file.filePath, ".form.json"); // e.g. /usr/me/projects/integration/src/workflows/CreateIntegration.wf.form.json
        const workflowFileName = file.fileName; // e.g. CreateIntegration.wf.ts
        const workflowFormFilePrefix = workflowFileName.replace(".wf.ts", ""); // e.g. CreateIntegration
        const workflowRootDir = file.filePath.replace(workflowFileName, ""); // e.g. /usr/me/projects/integration/src/workflows/
        const workflowJsonForm = system.fileExists(workflowFormFilePath) ? system.readFile(workflowFormFilePath).toString() : undefined;

        if (workflowJsonForm) {
            console.debug(`Found existing Custom Form for Workflow: '${workflowFileName}'. Attaching.`);
            context.writeFile(`${targetFilePath}.form.json`, workflowJsonForm);
        }

        console.debug(`Attaching User Interaction Custom Forms for Workflow '${workflowFileName}'.`);

        // attach the custom interaction component form json file (if any)
        workflowInfo.items?.forEach(item => {
            const isCustomInteractionComponent = item.strategy instanceof UserInteractionDecoratorStrategy;
            if (isCustomInteractionComponent) {
                const itemId = (item.strategy as UserInteractionDecoratorStrategy).getItemId();

                const fileName = userInteractionFormFileNameTemplate.replace("{{workflowName}}", workflowName).replace("{{itemId}}", itemId);
                const path = system.resolvePath(context.outputs.workflows, workflowInfo.path);

                const sourceFileName = userInteractionFormSourceFileNameByIdTemplate.replace("{{workflowFileName}}", workflowFormFilePrefix).replace("{{itemId}}", itemId);
                const fullFormPath = workflowRootDir.concat(sourceFileName);

                const userInteractionDisplayName = item.name;
                const sourceFileNameByFunction = userInteractionFormSourceFileNameByFunctionTemplate.replace("{{workflowFileName}}", workflowFormFilePrefix).replace("{{functionName}}", item.name);
                const fullFormPathByFunction = workflowRootDir.concat(sourceFileNameByFunction);

                // Check for file using item ID convention
                if (system.fileExists(fullFormPath)) {
                    console.debug(`Found existing User Interaction Custom Form: '${sourceFileName}' for item with ID '${itemId}'. Attaching to Workflow.`)
                    const userInteractionFormJson = system.readFile(fullFormPath).toString();
                    context.writeFile(system.resolvePath(path, fileName), userInteractionFormJson);
                    // Check for file using function name convention
                } else if (system.fileExists(fullFormPathByFunction)) {
                    console.debug(`Found existing User Interaction Custom Form: '${sourceFileNameByFunction}' for item with display name '${userInteractionDisplayName}'. Attaching to Workflow.`)
                    const userInteractionFormJson = system.readFile(fullFormPathByFunction).toString();
                    context.writeFile(system.resolvePath(path, fileName), userInteractionFormJson);
                } else {
                    console.debug(`No User Interaction Custom Form found for item with ID '${itemId}' matching the template '${userInteractionFormSourceFileNameByIdTemplate}' or dispay name '${userInteractionDisplayName}' matching the template '${userInteractionFormSourceFileNameByFunctionTemplate}'. Attaching default empty Custom Form.`);
                    context.writeFile(system.resolvePath(path, fileName), defaultUserInteractionJson.replace("{{itemId}}", itemId));
                }
            }
        });
    }

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
        if (decorators?.length) {
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
                const itemInfo = createWorkflowItemDescriptor(methodNode.name, workflowInfo);
                workflowInfo.items.push(itemInfo);

                registerWorkflowItem(itemInfo, methodNode);

                const actionSourceFilePath = system.changeFileExt(sourceFile.fileName, `.${itemInfo.name}.wf.ts`, [".wf.ts"]);
                let actionSourceText = itemInfo.strategy.printSourceFile(methodNode, sourceFile, itemInfo);
                // @TODO: "Unstupify" me
                if (itemInfo.polyglot) {
                    actionSourceText = decorateSourceFileTextWithPolyglot(actionSourceText, itemInfo.polyglot, itemInfo);
                }

                const newSourceFile = ts.createSourceFile(
                    actionSourceFilePath,
                    actionSourceText,
                    ts.ScriptTarget.Latest,
                    true
                );

                actionSourceFiles.push(newSourceFile);
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
    // Exists a declaration of a Polyglot decorator
    if (itemInfo.input.length > 0 && itemInfo.output.length > 0) {
        const polyglotCall = printPolyglotCode(polyglotDescriptor.package, polyglotDescriptor.method, itemInfo.input, itemInfo.output);
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
function registerWorkflowItem(itemInfo: WorkflowItemDescriptor, methodNode: ts.MethodDeclaration): void {
    registerPolyglotDecorators(methodNode, itemInfo);
    registerMethodDecorators(methodNode, itemInfo);
    registerMethodArgumentDecorators(methodNode, itemInfo);
}

/**
 * Transpiles the action items.
 *
 * This function is responsible for transforming the source files of the action items.
 * It will collect the source text of the action items and store it in the workflow descriptor.
 *
 * This method sets the sourceText property in the workflow item descriptors.
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
 *
 * @TODO: See which logic you can move to the strategies
 */
function createWorkflowItemDescriptor(propertyNameNode: ts.PropertyName, workflowInfo: WorkflowDescriptor): WorkflowItemDescriptor {
    return {
        name: getPropertyName(propertyNameNode),
        input: [],
        output: [],
        sourceText: "",
        target: null,
        strategy: null,
        canvasItemPolymorphicBag: {},
        parent: workflowInfo
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
