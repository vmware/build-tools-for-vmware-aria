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
import { CanvasItemPolymorphicBagForForeach, WorkflowItemDescriptor, WorkflowItemType } from "../../../../decorators";
import { getDecoratorProps } from "../../../helpers/node";
import BaseItemDecoratorStrategy from "./base/baseItemDecoratorStrategy";
import { InputOutputBindings } from "./helpers/presentation";

/**
 * Responsible for printing out foreach items in workflow definitions.
 *
 * Foreach elements iterate over an array and call a linked sub-workflow for each element.
 * This strategy handles the transformation of TypeScript foreach decorators into workflow XML elements
 * with proper binding configuration.
 *
 * @example
 * Generated XML structure:
 * ```xml
 * <workflow-item name="item1" type="foreach" alt-connections="2" x="100.0" y="20.0">
 *   <display-name><![CDATA[foreachElement]]></display-name>
 *   <subworkflow-id><![CDATA[9e4503db-cbaa-435a-9fad-144409c08df0]]></subworkflow-id>
 *   <in-binding>
 *     <bind name="items" type="Array/string" export-name="items" />
 *   </in-binding>
 *   <out-binding>
 *     <bind name="results" type="Array/string" export-name="results" />
 *   </out-binding>
 *   <next-item name="item2"/>
 * </workflow-item>
 * ```
 *
 * @extends BaseItemDecoratorStrategy
 */
export default class ForeachItemDecoratorStrategy extends BaseItemDecoratorStrategy {

	/**
	 * Gets the canvas type identifier for foreach items.
	 *
	 * @returns {string} The canvas type string "foreach"
	 */
	public getCanvasType(): string {
		return "foreach";
	}

	/**
	 * Gets the workflow item type that this strategy handles.
	 *
	 * @returns {WorkflowItemType} The foreach workflow item type
	 */
	public getDecoratorType(): WorkflowItemType {
		return WorkflowItemType.Foreach;
	}

	/**
	 * Registers and processes item arguments from the decorator node.
	 *
	 * Processes the following decorator properties:
	 * - `target`: The target item to navigate to after all iterations complete
	 * - `linkedItem`: The ID of the sub-workflow to call for each element in the array
	 * - `exception`: Exception handling configuration
	 *
	 * @param {WorkflowItemDescriptor} itemInfo - The workflow item descriptor to populate
	 * @param {Decorator} decoratorNode - The TypeScript decorator node containing the arguments
	 * @throws {Error} When an unsupported item attribute is encountered
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

				case "linkedItem":
					itemInfo.canvasItemPolymorphicBag.linkedItem = propValue;
					break;

				case "exception":
					itemInfo.canvasItemPolymorphicBag.exception = propValue;
					break;

				default:
					throw new Error(`Item attribute '${propName}' is not supported for ${this.getDecoratorType()} item`);
			}
		});
	}

	/**
	 * Prints out the complete XML representation of the foreach item.
	 *
	 * The foreach item iterates over an array and calls a linked sub-workflow for each element.
	 * Position is expressed as inline `x`/`y` attributes on the element tag.
	 * The linked sub-workflow is referenced via a `<subworkflow-id>` child element.
	 * Routing is expressed via `<next-item>` (normal flow) and optionally `<catch-item>` (exception).
	 * The `alt-connections` attribute reflects the total number of input + output bindings.
	 *
	 * @param {WorkflowItemDescriptor} itemInfo - The item descriptor containing foreach configuration
	 * @param {number} pos - The position of the item in the workflow (used for item naming)
	 * @param {number} x - X-axis position for UI display
	 * @param {number} y - Y-axis position for UI display
	 * @returns {string} The complete XML representation of the foreach item
	 */
	public printItem(itemInfo: WorkflowItemDescriptor, pos: number, x: number, y: number): string {
		const stringBuilder = new StringBuilderClass("", "");
		const foreachBag = itemInfo.canvasItemPolymorphicBag as CanvasItemPolymorphicBagForForeach;

		const targetItem = super.findTargetItem(itemInfo.target, pos, itemInfo);
		if (targetItem === null) {
			throw new Error(`Unable to find target item for ${this.getDecoratorType()} item`);
		}

		// alt-connections represents the total number of data connections (inputs + outputs)
		const altConnections = (itemInfo.input?.length || 0) + (itemInfo.output?.length || 0);

		// Position is expressed as inline attributes on the element (not a child <position> element)
		const xFormatted = Math.round(x || 0).toString() + ".0";
		const yFormatted = Math.round(y || 0).toString() + ".0";

		stringBuilder.append(
			`<workflow-item`
			+ ` name="item${pos}"`
			+ ` type="${this.getCanvasType()}"`
			+ ` alt-connections="${altConnections}"`
			+ ` x="${xFormatted}"`
			+ ` y="${yFormatted}"`
			+ `>`
		);

		stringBuilder.indent();
		stringBuilder.append(`<display-name><![CDATA[${itemInfo.name}]]></display-name>`).appendLine();
		// Sub-workflow is referenced as a CDATA child element, not an attribute
		stringBuilder.append(`<subworkflow-id><![CDATA[${foreachBag.linkedItem}]]></subworkflow-id>`).appendLine();
		stringBuilder.appendContent(super.buildParameterBindings(itemInfo, InputOutputBindings.IN_BINDINGS));
		stringBuilder.appendContent(super.buildParameterBindings(itemInfo, InputOutputBindings.OUT_BINDINGS));

		// Exception path (catch-item) if configured
		if (foreachBag.exception) {
			const exceptionTarget = super.findTargetItem(foreachBag.exception, pos, itemInfo);
			stringBuilder.append(`<catch-item name="${exceptionTarget}"/>`).appendLine();
		}

		// Normal exit path via next-item child element (not out-name attribute)
		stringBuilder.append(`<next-item name="${targetItem}"/>`).appendLine();
		stringBuilder.unindent();
		stringBuilder.append(`</workflow-item>`).appendLine();

		return stringBuilder.toString();
	}
}