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
import { DiagnosticCategory, FileTransformationContext } from "../../../../types";
import { PolyglotDescriptor, WorkflowDescriptor, WorkflowItemDescriptor, WorkflowItemType, WorkflowParameter, WorkflowParameterType } from "../../../decorators";
import { getDecoratorNames, getIdentifierTextOrNull, getPropertyName } from "../../helpers/node";
import { getVroType } from "../../helpers/vro";
import ActionItemDecoratorStrategy from "./decorators/actionItemDecoratorStrategy";
import AsyncWorkflowItemDecoratorStrategy from "./decorators/asyncWorkflowItemDecoratorStrategy";
import CanvasItemDecoratorStrategy from "./decorators/base/canvasItemDecoratorStrategy";
import DecisionItemDecoratorStrategy from "./decorators/decisionItemDecoratorStrategy";
import DefaultErrorHandlerDecoratorStrategy from "./decorators/defaultErrorHandlerDecoratorStrategy";
import EndItemDecoratorStrategy from "./decorators/endItemDecoratorStrategy";
import ItemDecoratorStrategy from "./decorators/itemDecoratorStrategy";
import RootItemDecoratorStrategy from "./decorators/rootItemDecoratorStrategy";
import ScheduledWorkflowItemDecoratorStrategy from "./decorators/scheduledWorkflowItemDecoratorStrategy";
import SwitchItemDecoratorStrategy from "./decorators/switchItemDecoratorStrategy";
import UserInteractionDecoratorStrategy from "./decorators/userInteractionDecoratorStrategy";
import WaitingTimerItemDecoratorStrategy from "./decorators/waitingTimerItemDecoratorStrategy";
import WorkflowItemDecoratorStrategy from "./decorators/workflowItemDecoratorStrategy";

/**
 * Fetches details from the decorators for the methods and adds the information to the Descriptors
 *
 * This method will assign a decorator by default to `WorkflowItemType.Item` if none is added with empty arguments
 *  This behavior will essentially make the method point to the next one in the workflow.
 */
export function registerMethodDecorators(methodNode: ts.MethodDeclaration, itemInfo: WorkflowItemDescriptor) {
	let decorators = ts.getDecorators(methodNode) as ts.Decorator[] || [];

	if (
		!decorators
		|| decorators?.length === 0
		|| (decorators.length === 1 && getItemStrategy(decorators[0]).getDecoratorType() === WorkflowItemType.RootItem)
	) {
		decorators.push(
			ts.factory.createDecorator(
				ts.factory.createCallExpression(
					ts.factory.createIdentifier(WorkflowItemType.Item),
					undefined,
					[
						ts.factory.createObjectLiteralExpression([])
					]
				)
			)
		);
	}

	for (const decoratorNode of decorators) {
		const itemStrategy = getItemStrategy(decoratorNode);
		if (itemStrategy.getDecoratorType() !== WorkflowItemType.RootItem) {
			itemInfo.strategy = itemStrategy;
		}

		itemStrategy.registerItemArguments(itemInfo, decoratorNode);
	}
}

/**
 * Determines the item strategy to use given the decorator node
 *
 * The itemInfo is passed, to be given to the strategy
 *
 * @param decoratorNode The decorator node
 * @returns The item strategy
 */
function getItemStrategy(decoratorNode: ts.Decorator): CanvasItemDecoratorStrategy {
	const decoratorExpressionNode = decoratorNode.expression as ts.CallExpression;
	const identifierText = getIdentifierTextOrNull(decoratorExpressionNode.expression);

	switch (identifierText) {
		case WorkflowItemType.Item:
			return new ItemDecoratorStrategy();
		case WorkflowItemType.Decision:
			return new DecisionItemDecoratorStrategy();
		case WorkflowItemType.WaitingTimer:
			return new WaitingTimerItemDecoratorStrategy();
		case WorkflowItemType.Workflow:
			return new WorkflowItemDecoratorStrategy();
		case WorkflowItemType.RootItem:
			return new RootItemDecoratorStrategy();
		case WorkflowItemType.Action:
			return new ActionItemDecoratorStrategy();
		case WorkflowItemType.ScheduledWorkflow:
			return new ScheduledWorkflowItemDecoratorStrategy();
		case WorkflowItemType.AsyncWorkflow:
			return new AsyncWorkflowItemDecoratorStrategy();
		case WorkflowItemType.DefaultErrorHandler:
			return new DefaultErrorHandlerDecoratorStrategy();
		case WorkflowItemType.UserInteractionItem:
			return new UserInteractionDecoratorStrategy();
		case WorkflowItemType.End:
			return new EndItemDecoratorStrategy();
		case WorkflowItemType.Switch:
			return new SwitchItemDecoratorStrategy();
		default:
			throw new Error(`Invalid decorator type: ${identifierText}`);
	}
}

/**
 * Registers all the argument decorators for the method
 *
 * Adds `input`, `output` and `canvasItemPolymorphicBag.exceptionBinding`
 *
 * @param methodNode The method node
 * @param itemInfo The item info
 * @returns void
 */
export function registerMethodArgumentDecorators(methodNode: ts.MethodDeclaration, itemInfo: WorkflowItemDescriptor) {
	if (!methodNode.parameters.length) {
		return;
	}
	methodNode.parameters.forEach(paramNode => {
		const name = getIdentifierTextOrNull(paramNode.name);
		if (!name) {
			return;
		}
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

				case "Err":
					parameterType |= WorkflowParameterType.Err;
					break;

				default:
					throw new Error(`Decorator '${decoratorName}' is not supported'`);
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
		if (parameterType & WorkflowParameterType.Err) {
			itemInfo.canvasItemPolymorphicBag.exceptionBinding = name;
		}

		addParamToWorkflowParams(itemInfo.parent, paramNode, parameterType);
	});
}

/**
 * This function adds the parameter to the workflow parameters
 *
 * It ensures that the parameter is not already present in the workflow parameters.
 * This is needed since transitive variables must be defined as attributes
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
		workflowInfo?.parameters?.push(parameter);
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
				case "id": {
					workflowInfo.id = (<ts.StringLiteral>property.initializer).text;
					break;
				}
				case "name": {
					workflowInfo.name = (<ts.StringLiteral>property.initializer).text;
					break;
				}
				case "path": {
					workflowInfo.path = (<ts.StringLiteral>property.initializer).text;
					break;
				}
				case "version": {
					workflowInfo.version = (<ts.StringLiteral>(property.initializer)).text;
					break;
				}
				case "presentation": {
					workflowInfo.presentation = (<ts.StringLiteral>property.initializer).text;
					break;
				}
				case "description": {
					workflowInfo.description = (<ts.StringLiteral>property.initializer).text;
					break;
				}
				case "restartMode": {
					workflowInfo.restartMode = parseInt((<ts.NumericLiteral>property.initializer).text);
					break;
				}
				case "resumeFromFailedMode": {
					workflowInfo.resumeFromFailedMode = parseInt((<ts.NumericLiteral>property.initializer).text);
					break;
				}
				case "input": {
					buildWorkflowDecoratorParameters(
						workflowInfo.parameters,
						<ts.ObjectLiteralExpression>property.initializer,
						WorkflowParameterType.Input,
						context,
						sourceFile
					);
					break;
				}
				case "output": {
					buildWorkflowDecoratorParameters(
						workflowInfo.parameters,
						<ts.ObjectLiteralExpression>property.initializer,
						WorkflowParameterType.Output,
						context,
						sourceFile
					);
					break;
				}
				case "attributes": {
					buildWorkflowDecoratorParameters(
						workflowInfo.parameters,
						<ts.ObjectLiteralExpression>property.initializer,
						WorkflowParameterType.Default,
						context,
						sourceFile,
						true
					);
					break;
				}
				default: {
					throw new Error(`Workflow attribute '${propName}' is not supported.`);
				}
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
		if (objectLiteralNode === null) {
			return;
		}
		const parameter = <WorkflowParameter>{
			name: name,
			parameterType: parameterType,
			isAttribute: isAttribute,
		};
		objectLiteralNode.properties.forEach((property: ts.PropertyAssignment) => {
			const propName = getPropertyName(property.name);
			switch (propName) {
				case "type": {
					parameter.type = (<ts.StringLiteral>property.initializer).text;
					break;
				}
				case "title": {
					parameter.title = (<ts.StringLiteral>property.initializer).text;
					break;
				}
				case "required": {
					parameter.required = property.initializer.kind === ts.SyntaxKind.TrueKeyword;
					break;
				}
				case "description": {
					parameter.description = (<ts.StringLiteral>property.initializer).text;
					break;
				}
				case "multiLine": {
					parameter.multiLine = property.initializer.kind === ts.SyntaxKind.TrueKeyword;
					break;
				}
				case "hidden": {
					parameter.hidden = property.initializer.kind === ts.SyntaxKind.TrueKeyword;
					break;
				}
				case "maxStringLength": {
					parameter.maxStringLength = parseInt((<ts.NumericLiteral>property.initializer).text);
					break;
				}
				case "minStringLength": {
					parameter.minStringLength = parseInt((<ts.NumericLiteral>property.initializer).text);
					break;
				}
				case "numberFormat": {
					parameter.numberFormat = (<ts.StringLiteral>property.initializer).text;
					break;
				}
				case "defaultValue":
				case "value": {
					parameter.defaultValue = getWorkflowParamValue(property.initializer);
					if (parameter.defaultValue === undefined) {
						context.diagnostics.addAtNode(
							sourceFile,
							property.initializer,
							`Workflow parameter default value should be of type string, number, boolean or an Array of them.`,
							DiagnosticCategory.Error);
					}
					break;
				}
				case "availableValues": {
					parameter.availableValues = (<ts.ArrayLiteralExpression>property.initializer).elements.map(getWorkflowParamValue);
					if (parameter.availableValues.some(v => v === undefined)) {
						context.diagnostics.addAtNode(
							sourceFile,
							property.initializer,
							`Workflow parameter available values should be of type string, number or boolean.`,
							DiagnosticCategory.Error);
						parameter.availableValues = undefined;
					}
					break;
				}
				case "bind": {
					parameter.bind = property.initializer.kind === ts.SyntaxKind.TrueKeyword;
					break;
				}
				default: {
					throw new Error(`Workflow parameter attribute '${propName}' is not suported.`);
				}
			}
		});

		parameters.push(parameter);
	});
}

function getWorkflowParamValue(node: ts.Node): string {
	switch (node.kind) {
		case ts.SyntaxKind.StringLiteral:
			return (<ts.StringLiteral>node).text;
		case ts.SyntaxKind.NumericLiteral:
			let value = (<ts.NumericLiteral>node).text;
			if (value.indexOf(".") < 0) {
				value += ".0";
			}
			return value;
		case ts.SyntaxKind.TrueKeyword:
			return "true";
		case ts.SyntaxKind.FalseKeyword:
			return "false";
		// Array
		case ts.SyntaxKind.ArrayLiteralExpression:
			return (<ts.ArrayLiteralExpression>node).elements.map(getWorkflowParamValue).join(",");
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
	if (decorators?.length < 1) {
		return;
	}
	decorators?.forEach(decoratorNode => {
		const callExpNode = decoratorNode.expression as ts.CallExpression;
		const identifierText = getIdentifierTextOrNull(callExpNode.expression);
		if (identifierText === "Polyglot") {
			// Extract the information stored in the @Polyglot decorator
			buildPolyglotInfo(polyglotInfo, callExpNode);
			itemInfo.polyglot = polyglotInfo;
		}
	});
}

/**
 * Contains code related to workflow polyglot logic.
 */
export function buildPolyglotInfo(polyglotInfo: PolyglotDescriptor, decoratorCallExp: ts.CallExpression) {
	const objLiteralNode = decoratorCallExp.arguments[0] as ts.ObjectLiteralExpression;
	if (objLiteralNode === null) {
		return;
	}
	objLiteralNode.properties?.forEach((property: ts.PropertyAssignment) => {
		const propName = getPropertyName(property.name);
		switch (propName) {
			case "package": {
				polyglotInfo.package = (<ts.StringLiteral>property.initializer).text;
				break;
			}
			case "method": {
				polyglotInfo.method = (<ts.StringLiteral>property.initializer).text;
				break;
			}
			default: {
				throw new Error(`Polyglot attribute '${propName}' is not supported.`);
			}
		}
	});
}

/**
 * Represents the polyglot info extracted from the code
 */
function createPolyglotDescriptor(): PolyglotDescriptor {
	return { method: "", package: "" };
}