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
import { InputOutputBindings } from "./helpers/presentation";

/**
 * Responsible for printing out the workflow end item.
 * The following decorator properties are supported:
 * 1. exceptionVariable - exception variable used within the component.
 * 2. endMode - end mode of the component (0 means success, 1 means error).
 * 3. businessStatus - business status of the end component.
 * @example
 * ```xml
  <workflow-item name="item8" throw-bind-name="errorMessage" type="end" end-mode="1" business-status="Bad">
	<in-binding/>
	<position y="110.0" x="280.0"/>
  </workflow-item>
 * ```
 */
export default class EndItemDecoratorStrategy extends BaseItemDecoratorStrategy {
	/**
	 * Return XML tag for the end workflow item.
	 *
	 * @returns XML tag name.
	 */
	public getCanvasType(): string {
		return "end";
	}

	/**
	 * Return the workflow item type supported by this decorator.
	 *
	 * @returns type of the workflow element.
	 */
	public getDecoratorType(): WorkflowItemType {
		return WorkflowItemType.End;
	}

	/**
	 * Register the canvas item arguments. For the default error handler only "endMode" and "exception" are supported.
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
				case "endMode":
					itemInfo.canvasItemPolymorphicBag.endMode = propValue;
					break;

				case "businessStatus":
					itemInfo.canvasItemPolymorphicBag.businessStatus = propValue;
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
		const node: GraphNode = super.getGraphNode(itemInfo, pos, [40, -10]);
		// end item should have no targets
		node.targets = [];

		return node;
	}

	/**
	 * Prints out the end item.
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

		const endMode = itemInfo?.canvasItemPolymorphicBag?.endMode ?? 0;
		const businessStatus = itemInfo?.canvasItemPolymorphicBag?.businessStatus;

		stringBuilder.append(`<workflow-item name="item${pos}" type="end" end-mode="${endMode}" `);
		if (itemInfo.canvasItemPolymorphicBag.exceptionBinding) {
			stringBuilder.append(` throw-bind-name="${itemInfo.canvasItemPolymorphicBag.exceptionBinding}" `);
		}
		if (businessStatus) {
			stringBuilder.append(`business-status="${businessStatus}" `);
		}
		stringBuilder.append(">").appendLine();
		stringBuilder.indent();
		stringBuilder.appendContent(super.buildParameterBindings(itemInfo, InputOutputBindings.OUT_BINDINGS));
		stringBuilder.append(super.formatItemPosition([x, y])).appendLine();
		stringBuilder.unindent();
		stringBuilder.append(`</workflow-item>`).appendLine();

		return stringBuilder.toString();
	}
}
