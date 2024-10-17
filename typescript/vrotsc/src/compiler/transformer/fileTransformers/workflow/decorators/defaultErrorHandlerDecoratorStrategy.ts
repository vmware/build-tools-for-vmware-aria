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
import { Decorator } from "typescript";
import { StringBuilderClass } from "../../../../../utilities/stringBuilder";
import { WorkflowItemDescriptor, WorkflowItemType } from "../../../../decorators";
import { getDecoratorProps } from "../../../helpers/node";
import BaseItemDecoratorStrategy from "./base/baseItemDecoratorStrategy";
import { GraphNode } from "./helpers/graph";

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
export default class DefaultErrorHandlerDecoratorStrategy extends BaseItemDecoratorStrategy {
	/** Marks the element type as not targetable by other elements (see in {@link findTargetItem}) */
	public readonly isNotTargetable = true;

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
		const decoratorProperties = getDecoratorProps(decoratorNode);
		if (!decoratorProperties?.length) {
			return;
		}
		decoratorProperties.forEach((propTuple) => {
			const [propName, propValue] = propTuple;
			switch (propName) {
				case "target":
					itemInfo.target = propValue;
					break;
				case "exceptionBinding":
					itemInfo.canvasItemPolymorphicBag.exceptionBinding = propValue;
					break;
				default:
					throw new Error(`Item attribute '${propName}' is not supported for ${this.getDecoratorType()} item`);
			}
		});
	}

	/**
	 * @see CanvasItemDecoratorStrategy.getGraphNode
	 */
	public getGraphNode(itemInfo: WorkflowItemDescriptor, pos: number): GraphNode {
		return super.getGraphNode(itemInfo, pos, [40, -10]);
	}

	/**
	 * Prints out the default handler item. Note that it needs to be connected with an end item and
	 * both must have identical name.
	 *
	 * @param itemInfo The item to print.
	 * @param pos The position of the item in the workflow.
	 * @param x position on X axis that will be used for UI display
	 * @param y position on Y axis that will be used for UI display
	 *
	 * @returns The string representation of the item.
	 */
	public printItem(itemInfo: WorkflowItemDescriptor, pos: number, x: number, y: number): string {
		const stringBuilder = new StringBuilderClass("", "");

		const targetItemName = super.findTargetItem(itemInfo.target, pos, itemInfo);
		if (targetItemName === null) {
			throw new Error(`Unable to find target item for ${this.getDecoratorType()} item`);
		}
		// it is important that the name of the error handler should be the same as the pointing target item name
		stringBuilder.append(`<error-handler name="${targetItemName}" `);

		if (itemInfo.canvasItemPolymorphicBag.exceptionBinding) {
			stringBuilder.append(` throw-bind-name="${itemInfo.canvasItemPolymorphicBag.exceptionBinding}" `);
		}

		stringBuilder.append(">").appendLine();
		stringBuilder.indent();
		stringBuilder.append(super.formatItemPosition([x, y])).appendLine();
		stringBuilder.unindent();
		stringBuilder.append("</error-handler>").appendLine();
		stringBuilder.unindent();

		return stringBuilder.toString();
	}

	/**
	 * Extracts the name (item#) of the Default error handler Workflow item.
	 * @param {WorkflowItemDescriptor[]} items - workflow items
	 * @returns {string} node name (item#) of the found Default error handler element, or NULL if there is none.
	 * @throws Error if there are more than 1 Default Error Handler items
	 */
	public getDefaultErrorHandlerNode(items: WorkflowItemDescriptor[]): string {
		const errorHandlerItems = items
			.map((item, i) => item.strategy.getCanvasType() !== "error-handler" ? null : `item${i + 1}`)
			.filter(item => !!item);
		if (errorHandlerItems.length > 1) {
			throw new Error(`There are more than 1 Default Error Handler elements: [${errorHandlerItems}]!`);
		}
		return errorHandlerItems.shift() || null;
	}
}
