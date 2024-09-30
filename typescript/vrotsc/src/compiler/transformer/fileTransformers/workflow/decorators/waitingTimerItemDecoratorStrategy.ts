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
import { InputOutputBindings } from "./helpers/presentation";
import { GraphNode } from "./helpers/graph";
import BaseItemDecoratorStrategy from "./base/baseItemDecoratorStrategy";

/**
 * Responsible for printing out the waiting timer item
 *
 * @example
 * ```xml
 <workflow-item name="item2" out-name="item0" type="waiting-timer">
	  <display-name>
			<![CDATA[waitForEvent]]>
	  </display-name>
	  <in-binding>
			<bind name="waitingTimer" type="Date" export-name="waitingTimer" />
	  </in-binding>
	  <position x="385.0" y="55.40909090909091" />
 </workflow-item>
 * ```
 */
export default class WaitingTimerItemDecoratorStrategy extends BaseItemDecoratorStrategy {

	public getCanvasType(): string {
		return "waiting-timer";
	}

	public getDecoratorType(): WorkflowItemType {
		return WorkflowItemType.WaitingTimer;
	}

	/**
	 * @see CanvasItemDecoratorStrategy.getGraphNode
	 */
	public getGraphNode(itemInfo: WorkflowItemDescriptor, pos: number): GraphNode {
		return super.getGraphNode(itemInfo, pos, [40, -10]);
	}

	/**
	 * Prints the waiting timer to the workflow file
	 *
	 * - `out-name` is the name of the item that the waiting timer will transition to after the timer is complete
	 *
	 * @param itemInfo The item to print
	 * @param pos The position of the item in the workflow
	 * @param x position on X axis that will be used for UI display
	 * @param y position on Y axis that will be used for UI display
	 *
	 * @returns The string representation of the item
	 */
	public printItem(itemInfo: WorkflowItemDescriptor, pos: number, x: number, y: number): string {
		const stringBuilder = new StringBuilderClass("", "");

		const targetItem = super.findTargetItem(itemInfo.target, pos, itemInfo);
		if (targetItem === null) {
			throw new Error(`Unable to find target item for ${this.getDecoratorType()} item`);
		}

		stringBuilder.append(`<workflow-item`
			+ ` name="item${pos}"`
			+ ` out-name="${targetItem}"`
			+ ` type="${this.getCanvasType()}" `
		);
		if (itemInfo.canvasItemPolymorphicBag.exception) {
			stringBuilder.append(` catch-name="${super.findTargetItem(itemInfo.canvasItemPolymorphicBag.exception, pos, itemInfo)}" `);
		}
		if (itemInfo.canvasItemPolymorphicBag.exceptionBinding) {
			stringBuilder.append(` throw-bind-name="${itemInfo.canvasItemPolymorphicBag.exceptionBinding}" `);
		}
		stringBuilder.append(">");

		stringBuilder.append(`<display-name><![CDATA[${itemInfo.name}]]></display-name>`).appendLine();
		stringBuilder.appendContent(super.buildParameterBindings(itemInfo, InputOutputBindings.IN_BINDINGS));
		stringBuilder.appendContent(super.buildParameterBindings(itemInfo, InputOutputBindings.OUT_BINDINGS));
		stringBuilder.append(super.formatItemPosition([x, y])).appendLine();
		stringBuilder.unindent();
		stringBuilder.append(`</workflow-item>`).appendLine();

		return stringBuilder.toString();
	}
}
