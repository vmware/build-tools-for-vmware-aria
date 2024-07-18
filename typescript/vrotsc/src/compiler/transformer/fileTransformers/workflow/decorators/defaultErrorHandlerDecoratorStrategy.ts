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
import { Decorator, MethodDeclaration, SourceFile } from "typescript";
import { StringBuilderClass } from "../../../../../utilities/stringBuilder";
import { WorkflowItemDescriptor, WorkflowItemType } from "../../../../decorators";
import { getDecoratorProps } from "../../../helpers/node";
import { findTargetItem } from "../helpers/findTargetItem";
import CanvasItemDecoratorStrategy from "./canvasItemDecoratorStrategy";
import { buildItemParameterBindings, InputOutputBindings } from "./helpers/presentation";

// UI positioning constants in the output XML file.
const xBasePosition = 180;
const yBasePosition = 110;
const offSet = 20;

/**
 * Responsible for printing out a default error handler
 * Important Note: The name of the error handler component should match the name of the end workflow element.
 * @example
 * ```xml
	<error-handler name="item1">
		<position x="415.0" y="65.40"/>
	</error-handler>
	<workflow-item name="item1" type="end" end-mode="0">
		<position x="405.0" y="55.40"/>
	</workflow-item>
 * ```
 */
export default class DefaultErrorHandlerDecoratorStrategy implements CanvasItemDecoratorStrategy {

	/**
	 * Return XML tag for the error handler workflow item.
	 * 
	 * @returns XML tag name.
	 */
	public getCanvasType(): string {
		return "error-handler";
	}

	/**
	 * Return the workflow item type supported by this decorator.
	 * 
	 * @returns type of the workflow element.
	 */
	public getDecoratorType(): WorkflowItemType {
		return WorkflowItemType.DefaultErrorHandler;
	}

	/**
	 * Register the canvas item arguments. For the default error handler only "target" and "exception" are supported.
	 * 
	 * @param itemInfo item info for that properties should be fetched.
	 * @param decoratorNode decorator node handle.
	 */
	public registerItemArguments(itemInfo: WorkflowItemDescriptor, decoratorNode: Decorator): void {
		const decoratorProperties: [string, any][] = getDecoratorProps(decoratorNode);
		if (!decoratorProperties?.length) {
			return;
		}
		decoratorProperties.forEach((propTuple) => {
			const [propName, propValue] = propTuple;
			switch (propName) {
				case "target": {
					itemInfo.target = propValue;
					break;
				}
				case "exception": {
					itemInfo.canvasItemPolymorphicBag.exception = propValue;
					break;
				}
				default: {
					throw new Error(`Item attribute '${propName}' is not supported for ${this.getDecoratorType()} item`);
				}
			}
		});
	}

	/**
	 * There is no need to print the source file for the default error handler.
	 */
	public printSourceFile(methodNode: MethodDeclaration, sourceFile: SourceFile): string {
		return "";
	}

	/**
	 * Prints out the default handler item. Note that it needs to be connected with an end item and
	 * both must have identical name.
	 *
	 * @param itemInfo The item to print.
	 * @param pos The position of the item in the workflow.
	 *
	 * @returns The string representation of the item.
	 */
	public printItem(itemInfo: WorkflowItemDescriptor, pos: number): string {
		const stringBuilder = new StringBuilderClass("", "");

		const targetItem = findTargetItem(itemInfo.target, pos, itemInfo);
		if (targetItem === null) {
			throw new Error(`Unable to find target item for ${this.getDecoratorType()} item`);
		}
		const exceptionVariable = itemInfo?.canvasItemPolymorphicBag?.exception;
		// attach error handler with attaching of exception variable if defined
		if (exceptionVariable !== null && exceptionVariable !== undefined && exceptionVariable) {
			stringBuilder.append(`<error-handler name="item${pos}" throw-bind-name="${exceptionVariable}">`).appendLine();
		} else {
			stringBuilder.append(`<error-handler name="item${pos}">`).appendLine();
		}
		stringBuilder.indent();
		stringBuilder.append(`<position x="${xBasePosition + offSet * pos}" y="${yBasePosition}"/>`).appendLine();
		stringBuilder.unindent();
		stringBuilder.append("</error-handler>").appendLine();

		// attach a default end item to the error handler, note that the name of the end item and the error handler should be the same
		const defaultEndItem = this.buildDefaultEndItem(itemInfo, pos, exceptionVariable);
		stringBuilder.append(defaultEndItem);
		stringBuilder.unindent();

		return stringBuilder.toString();
	}

	private buildDefaultEndItem(itemInfo: WorkflowItemDescriptor, pos: number, exceptionVariable: string): string {
		const stringBuilder = new StringBuilderClass("", "");

		if (exceptionVariable !== null && exceptionVariable !== undefined && exceptionVariable) {
			stringBuilder.append(`<workflow-item name="item${pos}" type="end" end-mode="1" throw-bind-name="${exceptionVariable}">`).appendLine();
		} else {
			stringBuilder.append(`<workflow-item name="item${pos}" type="end" end-mode="0">`).appendLine();
		}
		stringBuilder.indent();
		stringBuilder.appendContent(buildItemParameterBindings(itemInfo, InputOutputBindings.IN_BINDINGS));
		stringBuilder.appendContent(buildItemParameterBindings(itemInfo, InputOutputBindings.OUT_BINDINGS));
		stringBuilder.unindent();
		stringBuilder.indent();
		stringBuilder.append(`<position x="${xBasePosition + offSet * (pos + 10)}" y="${yBasePosition}"/>`).appendLine();
		stringBuilder.unindent();
		stringBuilder.append(`</workflow-item>`).appendLine();

		return stringBuilder.toString();
	}
}
