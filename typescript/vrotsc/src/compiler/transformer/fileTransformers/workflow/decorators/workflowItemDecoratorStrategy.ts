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
import { InputOutputBindings } from "./helpers/presentation";
import BaseItemDecoratorStrategy from "./base/baseItemDecoratorStrategy";

/**
 *
 * Responsible for printing out the workflow item:
 * @example
 * ```xml
 <workflow-item name="item1" out-name="item2" throw-bind-name="" type="link" linked-workflow-id="72676355-8293-4758-bdeb-18d2896e8318">
	<display-name><![CDATA[WfItem]]></display-name>
	<in-binding>
	  <bind name="test" type="string" export-name="test"/>
	  <bind name="secureString" type="SecureString" export-name="secureString"/>
	  <bind name="host" type="REST:RESTHost" export-name="host"/>
	</in-binding>
	<out-binding>
	  <bind name="message" type="string" export-name="message"/>
	</out-binding>
	<description><![CDATA[ ]]></description>
	<position y="50.0" x="220.0"/>
  </workflow-item>
 * ```
 */
export default class WorkflowItemDecoratorStrategy extends BaseItemDecoratorStrategy {

	public getCanvasType(): string {
		return "link";
	}

	public getDecoratorType(): WorkflowItemType {
		return WorkflowItemType.Workflow;
	}

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

				case "exception":
					itemInfo.canvasItemPolymorphicBag.exception = propValue;
					break;

				case "linkedItem":
					itemInfo.canvasItemPolymorphicBag.linkedItem = propValue;
					break;

				default:
					throw new Error(`Item attribute '${propName}' is not supported for ${this.getDecoratorType()} item`);
			}
		});
	}

	/**
	 * Prints out the item
	 *
	 * - `out-name` is the target canvas item to be called after the item is executed
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
			+ ` type="${this.getCanvasType()}"`
			+ ` linked-workflow-id="${itemInfo.canvasItemPolymorphicBag.linkedItem}" `
		);

		if (itemInfo.canvasItemPolymorphicBag.exception) {
			stringBuilder.append(` catch-name="${super.findTargetItem(itemInfo.canvasItemPolymorphicBag.exception, pos, itemInfo)}" `);
		}
		if (itemInfo.canvasItemPolymorphicBag.exceptionBinding) {
			stringBuilder.append(` throw-bind-name="${itemInfo.canvasItemPolymorphicBag.exceptionBinding}" `);
		}
		stringBuilder.append(">");

		stringBuilder.indent();
		stringBuilder.append(`<display-name><![CDATA[${itemInfo.name}]]></display-name>`).appendLine();
		stringBuilder.appendContent(this.buildParameterBindings(itemInfo, InputOutputBindings.IN_BINDINGS));
		stringBuilder.appendContent(this.buildParameterBindings(itemInfo, InputOutputBindings.OUT_BINDINGS));
		stringBuilder.append(super.formatItemPosition([x, y])).appendLine();
		stringBuilder.unindent();
		stringBuilder.append(`</workflow-item>`).appendLine();

		return stringBuilder.toString();
	}
}
