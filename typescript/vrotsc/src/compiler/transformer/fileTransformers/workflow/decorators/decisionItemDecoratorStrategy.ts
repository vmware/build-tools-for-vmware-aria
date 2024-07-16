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
import { CanvasItemPolymorphicBagForDecision, WorkflowItemDescriptor, WorkflowItemType } from "../../../../decorators";
import { getDecoratorProps } from "../../../helpers/node";
import CanvasItemDecoratorStrategy from "./canvasItemDecoratorStrategy";
import { SourceFilePrinter, WrapperSourceFilePrinter } from "./helpers/sourceFile";
import { StringBuilderClass } from "../../../../../utilities/stringBuilder";
import { findTargetItem } from "../helpers/findTargetItem";
import { InputOutputBindings, buildItemParameterBindings } from "./helpers/presentation";

export default class DecisionItemDecoratorStrategy implements CanvasItemDecoratorStrategy {
	constructor(
		private readonly itemInfo: WorkflowItemDescriptor,
		private readonly sourceFilePrinter: SourceFilePrinter = new WrapperSourceFilePrinter()
	) {
		this.itemInfo.item = this;
	}

	getDecoratorType(): WorkflowItemType {
		return WorkflowItemType.Decision;
	}

	getCanvasType(): string {
		return "custom-condition";
	}

	registerItemArguments(decoratorNode: Decorator): void {
		getDecoratorProps(decoratorNode).forEach((propTuple) => {
			const [propName, propValue] = propTuple;
			switch (propName) {
				case "target": {
					this.itemInfo.target = propValue;
					break;
				}
				case "else": {
					this.itemInfo.canvasItemPolymorphicBag.else = propValue;
					break;
				}
				default: {
					throw new Error(`Item attribute '${propName}' is not supported for ${this.getDecoratorType()} item`);
				}
			}
		});
	}

	printSourceFile(methodNode: MethodDeclaration, sourceFile: SourceFile): string {
		return this.sourceFilePrinter.printSourceFile(methodNode, sourceFile);
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
	 * @param pos The position of the item in the workflow
	 * @returns The string representation of the decision item
	 */
	printItem(pos: number): string {
		const stringBuilder = new StringBuilderClass("", "");

		const targetItem = findTargetItem(this.itemInfo.target, pos, this.itemInfo);
		if (targetItem === null) {
			throw new Error(`Unable to find target item for ${this.getDecoratorType()} item`);
		}

		this.itemInfo.sourceText = this.clearWrapperFunction(this.itemInfo.sourceText);
		stringBuilder.append(`<workflow-item`
			+ ` name="item${pos}"`
			+ ` out-name="${targetItem}"`
			+ ` type="${this.getCanvasType()}"`
			+ ` alt-out-name="${findTargetItem((this.itemInfo.canvasItemPolymorphicBag as CanvasItemPolymorphicBagForDecision).else, pos, this.itemInfo)}"`
			+ ">").appendLine();

		stringBuilder.indent();
		stringBuilder.append(`<script encoded="false"><![CDATA[${this.itemInfo.sourceText}]]></script>`).appendLine();
		stringBuilder.append(`<display-name><![CDATA[${this.itemInfo.name}]]></display-name>`).appendLine();
		stringBuilder.appendContent(buildItemParameterBindings(this.itemInfo, InputOutputBindings.IN_BINDINGS));
		stringBuilder.appendContent(buildItemParameterBindings(this.itemInfo, InputOutputBindings.OUT_BINDINGS));
		stringBuilder.append(`<position x="${225 + 160 * (pos - 1)}.0" y="55.40909090909091" />`).appendLine();
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
