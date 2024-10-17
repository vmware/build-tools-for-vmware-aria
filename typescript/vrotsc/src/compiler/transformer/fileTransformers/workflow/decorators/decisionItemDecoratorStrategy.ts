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
import { CanvasItemPolymorphicBagForDecision, WorkflowItemDescriptor, WorkflowItemType } from "../../../../decorators";
import { getDecoratorProps } from "../../../helpers/node";
import BaseItemDecoratorStrategy from "./base/baseItemDecoratorStrategy";
import { GraphNode } from "./helpers/graph";
import { InputOutputBindings } from "./helpers/presentation";
import { WrapperSourceFilePrinter } from "./helpers/sourceFile";


/**
 * Responsible for printing out decision items.
 * Note: Decision element in UI supports "else" instead of "exception".
 * Any exceptions that occur in the condition script are thrown and may be captured in a Default Error Handler element for further handling.
 *
 * @example
 <workflow-item name="item3" out-name="item4" type="custom-condition" alt-out-name="item2">
   <display-name><![CDATA[Decision]]></display-name>
   <script encoded="false"><![CDATA[return waitingTimer !== null]]></script>
   <in-binding>
	 <bind name="waitingTimer" type="Date" export-name="waitingTimer"/>
   </in-binding>
   <out-binding/>
   <description><![CDATA[Custom decision based on a custom script.]]></description>
   <position y="40.0" x="380.0"/>
 </workflow-item>
 */
export default class DecisionItemDecoratorStrategy extends BaseItemDecoratorStrategy {
	constructor() {
		super();
		this.sourceFilePrinter = new WrapperSourceFilePrinter();
	}

	public getDecoratorType(): WorkflowItemType {
		return WorkflowItemType.Decision;
	}

	public getCanvasType(): string {
		return "custom-condition";
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

				case "else":
					itemInfo.canvasItemPolymorphicBag.else = propValue;
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
		const node: GraphNode = super.getGraphNode(itemInfo, pos, [0, -10]);
		node.targets.push(super.findTargetItem((itemInfo.canvasItemPolymorphicBag as CanvasItemPolymorphicBagForDecision).else, pos, itemInfo));

		return node;
	}

	public printSourceFile(methodNode: MethodDeclaration, sourceFile: SourceFile, itemInfo: WorkflowItemDescriptor): string {
		return this.sourceFilePrinter.printSourceFile(methodNode, sourceFile, itemInfo);
	}

	/**
	 * Prints out the decision item
	 *
	 * - `out-name` is the target if the condition is met
	 * - `alt-out-name` is the target if the `else` condition is met
	 *
	 * The `sourceText` is cleared of the `wrapper` function, if it exists
	 *  it was only added to the sourceText to make the code valid for typescript
	 *
	 * @param itemInfo The item to print
	 * @param pos The position of the item in the workflow
	 * @param x position on X axis that will be used for UI display
	 * @param y position on Y axis that will be used for UI display
	 *
	 * @returns The string representation of the decision item
	 */
	public printItem(itemInfo: WorkflowItemDescriptor, pos: number, x: number, y: number): string {
		const stringBuilder = new StringBuilderClass("", "");

		const targetItem = super.findTargetItem(itemInfo.target, pos, itemInfo);
		if (targetItem === null) {
			throw new Error(`Unable to find target item for ${this.getDecoratorType()} item`);
		}

		itemInfo.sourceText = this.clearWrapperFunction(itemInfo.sourceText);
		stringBuilder.append(`<workflow-item`
			+ ` name="item${pos}"`
			+ ` out-name="${targetItem}"`
			+ ` type="${this.getCanvasType()}"`
			+ ` alt-out-name="${super.findTargetItem((itemInfo.canvasItemPolymorphicBag as CanvasItemPolymorphicBagForDecision).else, pos, itemInfo)}" `
		);
		if (itemInfo.canvasItemPolymorphicBag.exception) {
			stringBuilder.append(` catch-name="${super.findTargetItem(itemInfo.canvasItemPolymorphicBag.exception, pos, itemInfo)}" `);
		}
		if (itemInfo.canvasItemPolymorphicBag.exceptionBinding) {
			stringBuilder.append(` throw-bind-name="${itemInfo.canvasItemPolymorphicBag.exceptionBinding}" `);
		}
		stringBuilder.append(">");

		stringBuilder.indent();
		stringBuilder.append(`<script encoded="false"><![CDATA[${itemInfo.sourceText}]]></script>`).appendLine();
		stringBuilder.append(`<display-name><![CDATA[${itemInfo.name}]]></display-name>`).appendLine();
		stringBuilder.appendContent(super.buildParameterBindings(itemInfo, InputOutputBindings.IN_BINDINGS));
		stringBuilder.appendContent(super.buildParameterBindings(itemInfo, InputOutputBindings.OUT_BINDINGS));
		stringBuilder.append(super.formatItemPosition([x, y])).appendLine();
		stringBuilder.unindent();
		stringBuilder.append(`</workflow-item>`).appendLine();

		return stringBuilder.toString();
	}

	/**
	 * Clears the `wrapper` function from the sourceText
	 *
	 * @param sourceText The source text to clear the `wrapper` function from
	 * @returns The source text without the `wrapper` function
	 */
	private clearWrapperFunction(sourceText: string): string {
		return sourceText.replace(/function wrapper\(\) \{|}$/gm, '').trim();
	}
}
