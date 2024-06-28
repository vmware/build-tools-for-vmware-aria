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
import * as ts from "typescript";
import { getDecoratorNames, getIdentifierTextOrNull, getPropertyName } from "../../helpers/node";
import { PolyglotDescriptor, WorkflowDescriptor, WorkflowItemDescriptor, WorkflowItemType, WorkflowParameter, WorkflowParameterType } from "../../../decorators";
import { getVroType } from "../../helpers/vro";
import { DiagnosticCategory, FileTransformationContext } from "../../../../types";

/**
 * Fetches details from the decorators for the methods and adds the information to the Descriptors
 */
export function registerMethodDecorators(methodNode: ts.MethodDeclaration, itemInfo: WorkflowItemDescriptor) {
	const decorators = ts.getDecorators(methodNode);

	if (!decorators || decorators?.length === 0) {
		return;
	}

	for (const decoratorNode of decorators) {
		const decoratorExpressionNode = decoratorNode.expression as ts.CallExpression;
		const identifierText = getIdentifierTextOrNull(decoratorExpressionNode.expression);

		switch (identifierText) {
			case WorkflowItemType.RootItem:
				if (!ts.isIdentifier(methodNode.name) && !ts.isPrivateIdentifier(methodNode.name)) {
					throw new Error(`RootItem decorator must be applied to a method with an identifier name.`);
				}

				itemInfo.workflowDescriptorRef.rootItem = methodNode.name.escapedText as string;
				break;
			default:
				itemInfo.itemType = identifierText as WorkflowItemType;

				const objLiteralNode = decoratorExpressionNode.arguments[0] as ts.ObjectLiteralExpression;
				registerCanvasItemArguments(objLiteralNode, identifierText, itemInfo);
		}
	}
}

/**
 * Registers the canvas item arguments
 *
 * This is where information is extracted from the decorator and added to the infoObjects
 *
 * @param objLiteralNode The object literal node
 * @param identifierText The identifier text
 * @param itemInfo The item info
 */
function registerCanvasItemArguments(objLiteralNode: ts.ObjectLiteralExpression, identifierText: string, itemInfo: WorkflowItemDescriptor) {
	if (!objLiteralNode) {
		return;
	}

	objLiteralNode.properties.forEach((property: ts.PropertyAssignment) => {
		const propName = getPropertyName(property.name);
		const propValue = (<ts.StringLiteral>property.initializer).text;

		switch (identifierText) {
			case WorkflowItemType.Item:
				switch (propName) {
					case "target":
						itemInfo.target = propValue;
						break;
					case "exception":
						itemInfo.canvasItemPolymorphicBag.exception = propValue;
						break;
					default:
						throw new Error(`Item attribute '${propName}' is not suported for ${identifierText} item`);
				}
				break;
			case WorkflowItemType.Decision:
				switch (propName) {
					case "target":
						itemInfo.target = propValue;
						break;
					case "else":
						itemInfo.canvasItemPolymorphicBag.else = propValue;
						break;
					default:
						throw new Error(`Item attribute '${propName}' is not suported for ${identifierText} item`);
				}
				break;
			case WorkflowItemType.WaitingTimer:
				switch (propName) {
					case "target":
						itemInfo.target = propValue;
						break;
					default:
						throw new Error(`Item attribute '${propName}' is not suported for ${identifierText} item`);
				}
				break;
		}
	});
}

export function registerMethodArgumentDecorators(methodNode: ts.MethodDeclaration, itemInfo: WorkflowItemDescriptor) {
	if (methodNode.parameters.length) {
		methodNode.parameters.forEach(paramNode => {
			const name = getIdentifierTextOrNull(paramNode.name);
			if (name) {
				let parameterType = WorkflowParameterType.Default;
				const decorators = ts.getDecorators(paramNode);
				// Adds state of what decorators are present
				getDecoratorNames(decorators).forEach(decoratorName => {
					switch (decoratorName || "In") {
						case "In":
							parameterType |= WorkflowParameterType.Input;
							break;
						case "Out":
							parameterType |= WorkflowParameterType.Output;
							break;
						default:
							throw new Error(`Decorator '${decoratorName} is not supported'`);
					}
				});

				if (parameterType === WorkflowParameterType.Default) {
					parameterType = WorkflowParameterType.Input;
				}

				if (parameterType & WorkflowParameterType.Input) {
					itemInfo.input.push(name);
				}

				if (parameterType & WorkflowParameterType.Output) {
					itemInfo.output.push(name);
				}

				addParamToWorkflowParams(itemInfo.workflowDescriptorRef, paramNode, parameterType);
			}
		});
	}
}

/**
 * This function adds the parameter to the workflow parameters
 *
 * It ensures that the parameter is not already present in the workflow parameters.
 * This is needed since transitive variables must be defined as attirubtes
 */
function addParamToWorkflowParams(workflowInfo: WorkflowDescriptor, paramNode: ts.ParameterDeclaration, parameterType: WorkflowParameterType): void {
	const name = (<ts.Identifier>paramNode.name).text;
	let parameter = workflowInfo.parameters.find(p => p.name === name);
	if (!parameter) {
		parameter = {
			name: name,
			type: getVroType(paramNode.type),
			parameterType: WorkflowParameterType.Default,
			required: !paramNode.questionToken
		};
		workflowInfo.parameters.push(parameter);
	}

	parameter.parameterType |= parameterType;

	if (parameter.type == null) {
		parameter.type = getVroType(paramNode.type);
	}
}

export function buildWorkflowDecorators(
	workflowInfo: WorkflowDescriptor,
	classNode: ts.ClassDeclaration,
	context: FileTransformationContext,
	sourceFile: ts.SourceFile
): void {
	const decorators = ts.getDecorators(classNode);
	decorators
		.filter(decoratorNode => {
			const callExpNode = decoratorNode.expression as ts.CallExpression;
			if (callExpNode && callExpNode.expression.kind === ts.SyntaxKind.Identifier) {
				return (<ts.Identifier>callExpNode.expression).text === "Workflow";
			}
		})
		.forEach(decoratorNode => {
			buildWorkflowDecorator(
				workflowInfo,
				<ts.CallExpression>decoratorNode.expression,
				context,
				sourceFile
			);
		});
}

function buildWorkflowDecorator(
	workflowInfo: WorkflowDescriptor,
	decoratorCallExp: ts.CallExpression,
	context: FileTransformationContext,
	sourceFile: ts.SourceFile
): void {
	const objLiteralNode = decoratorCallExp.arguments[0] as ts.ObjectLiteralExpression;
	if (objLiteralNode) {
		objLiteralNode.properties.forEach((property: ts.PropertyAssignment) => {
			const propName = getPropertyName(property.name);
			switch (propName) {
				case "id":
					workflowInfo.id = (<ts.StringLiteral>property.initializer).text;
					break;
				case "name":
					workflowInfo.name = (<ts.StringLiteral>property.initializer).text;
					break;
				case "path":
					workflowInfo.path = (<ts.StringLiteral>property.initializer).text;
					break;
				case "version":
					workflowInfo.version = (<ts.StringLiteral>(property.initializer)).text;
					break;
				case "presentation":
					workflowInfo.presentation = (<ts.StringLiteral>property.initializer).text;
					break;
				case "description":
					workflowInfo.description = (<ts.StringLiteral>property.initializer).text;
					break;
				case "input":
					buildWorkflowDecoratorParameters(
						workflowInfo.parameters,
						<ts.ObjectLiteralExpression>property.initializer,
						WorkflowParameterType.Input,
						context,
						sourceFile
					);
					break;
				case "output":
					buildWorkflowDecoratorParameters(
						workflowInfo.parameters,
						<ts.ObjectLiteralExpression>property.initializer,
						WorkflowParameterType.Output,
						context,
						sourceFile
					);
					break;
				case "attributes":
					buildWorkflowDecoratorParameters(
						workflowInfo.parameters,
						<ts.ObjectLiteralExpression>property.initializer,
						WorkflowParameterType.Default,
						context,
						sourceFile,
						true
					);
					break;
				default:
					throw new Error(`Workflow attribute '${propName}' is not suported.`);
			}
		});
	}
}

function buildWorkflowDecoratorParameters(
	parameters: WorkflowParameter[],
	objLiteralNode: ts.ObjectLiteralExpression,
	parameterType: WorkflowParameterType,
	context: FileTransformationContext,
	sourceFile: ts.SourceFile,
	isAttribute?: boolean,
): void {
	objLiteralNode.properties.forEach((property: ts.PropertyAssignment) => {
		const name = getPropertyName(property.name);
		const objectLiteralNode = <ts.ObjectLiteralExpression>property.initializer;
		if (objectLiteralNode) {
			const parameter = <WorkflowParameter>{
				name: name,
				parameterType: parameterType,
				isAttribute: isAttribute,
			};

			objectLiteralNode.properties.forEach((property: ts.PropertyAssignment) => {
				const propName = getPropertyName(property.name);
				switch (propName) {
					case "type":
						parameter.type = (<ts.StringLiteral>property.initializer).text;
						break;
					case "title":
						parameter.title = (<ts.StringLiteral>property.initializer).text;
						break;
					case "required":
						parameter.required = property.initializer.kind === ts.SyntaxKind.TrueKeyword;
						break;
					case "description":
						parameter.description = (<ts.StringLiteral>property.initializer).text;
						break;
					case "multiLine":
						parameter.multiLine = property.initializer.kind === ts.SyntaxKind.TrueKeyword;
						break;
					case "hidden":
						parameter.hidden = property.initializer.kind === ts.SyntaxKind.TrueKeyword;
						break;
					case "maxStringLength":
						parameter.maxStringLength = parseInt((<ts.NumericLiteral>property.initializer).text);
						break;
					case "minStringLength":
						parameter.minStringLength = parseInt((<ts.NumericLiteral>property.initializer).text);
						break;
					case "numberFormat":
						parameter.numberFormat = (<ts.StringLiteral>property.initializer).text;
						break;
					case "defaultValue":
					case "value":
						{
							parameter.defaultValue = getWorkflowParamValue(property.initializer);
							if (parameter.defaultValue === undefined) {
								context.diagnostics.addAtNode(
									sourceFile,
									property.initializer,
									`Workflow parameter default value should be of type string, number or boolean.`,
									DiagnosticCategory.Error);
							}
						}
						break;
					case "availableValues":
						{
							parameter.availableValues = (<ts.ArrayLiteralExpression>property.initializer).elements.map(getWorkflowParamValue);
							if (parameter.availableValues.some(v => v === undefined)) {
								context.diagnostics.addAtNode(
									sourceFile,
									property.initializer,
									`Workflow parameter available values should be of type string, number or boolean.`,
									DiagnosticCategory.Error);
								parameter.availableValues = undefined;
							}
						}
						break;
					case "bind":
						{
							parameter.bind = property.initializer.kind === ts.SyntaxKind.TrueKeyword;
							break;
						}
					default:
						throw new Error(`Workflow parameter attribute '${propName}' is not suported.`);
				}
			});

			parameters.push(parameter);
		}
	});
}

function getWorkflowParamValue(node: ts.Node): string {
	switch (node.kind) {
		case ts.SyntaxKind.StringLiteral:
			return (<ts.StringLiteral>node).text;
		case ts.SyntaxKind.NumericLiteral:
			{
				let value = (<ts.NumericLiteral>node).text;
				if (value.indexOf(".") < 0) {
					value += ".0";
				}
				return value;
			}
		case ts.SyntaxKind.TrueKeyword:
			return "true";
		case ts.SyntaxKind.FalseKeyword:
			return "false";
	}
};

// ------------------------------------- Polyglot -------------------------------------

/**
 * Registers the Polyglot decorator information
 *
 * @param methodNode The method node
 * @param itemInfo The item info
 */
export function registerPolyglotDecorators(methodNode: ts.MethodDeclaration, itemInfo: WorkflowItemDescriptor) {
	const polyglotInfo: PolyglotDescriptor = createPolyglotDescriptor();

	const decorators = ts.getDecorators(methodNode);
	if (decorators && decorators.length >= 1) {

		decorators.forEach(decoratorNode => {
			const callExpNode = decoratorNode.expression as ts.CallExpression;

			const identifierText = getIdentifierTextOrNull(callExpNode.expression);

			if (identifierText === "Polyglot") {
				//Extract the information stored in the @Polyglot decorator
				buildPolyglotInfo(polyglotInfo, callExpNode);
				itemInfo.polyglot = polyglotInfo;
			}
		});
	}
}

/**
 * Contains code related to workflow polyglot logic.
 */
export function buildPolyglotInfo(polyglotInfo: PolyglotDescriptor, decoratorCallExp: ts.CallExpression) {
	const objLiteralNode = decoratorCallExp.arguments[0] as ts.ObjectLiteralExpression;
	if (objLiteralNode) {
		objLiteralNode.properties.forEach((property: ts.PropertyAssignment) => {
			const propName = getPropertyName(property.name);
			switch (propName) {
				case "package":
					polyglotInfo.package = (<ts.StringLiteral>property.initializer).text;

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
					break;
				case "method":
					polyglotInfo.method = (<ts.StringLiteral>property.initializer).text;
					break;
				default:
					throw new Error(`Polyglot attribute '${propName}' is not suported.`);
			}
		});
	}
}

/**
 * Represents the polyglot info extracted from the code
 */
function createPolyglotDescriptor(): PolyglotDescriptor {
	return { method: "", package: "" };
}
