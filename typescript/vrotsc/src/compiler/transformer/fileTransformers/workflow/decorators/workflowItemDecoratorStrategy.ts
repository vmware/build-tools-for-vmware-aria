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
import { WorkflowItemDescriptor, WorkflowItemType } from "../../../../decorators";
import { getDecoratorProps } from "../../../helpers/node";
import CanvasItemDecoratorStrategy from "./canvasItemDecoratorStrategy";
import { InputOutputBindings, buildItemParameterBindings } from "./helpers/presentation";
import { findTargetItem } from "../helpers/findTargetItem";
import { StringBuilderClass } from "../../../../../utilities/stringBuilder";

export default class WorkflowItemDecoratorStrategy implements CanvasItemDecoratorStrategy {
	constructor(private readonly itemInfo: WorkflowItemDescriptor) {
		this.itemInfo.item = this;
	}

	getCanvasType(): string {
		return "link";
	}

	getDecoratorType(): WorkflowItemType {
		return WorkflowItemType.Workflow;
	}

	registerItemArguments(decoratorNode: Decorator): void {
		getDecoratorProps(decoratorNode).forEach((propTuple) => {
			const [propName, propValue] = propTuple;
			switch (propName) {
				case "target":
					this.itemInfo.target = propValue;
					break;

				case "exception":
					this.itemInfo.canvasItemPolymorphicBag.exception = propValue;
					break;

				case "linkedItem":
					this.itemInfo.canvasItemPolymorphicBag.linkedItem = propValue;
					break;

				default:
					throw new Error(`Item attribute '${propName}' is not supported for ${this.getDecoratorType()} item`);
			}
		});
	}

	/**
	 * There is no need to print the source file for the workflow item
	 */
	printSourceFile(methodNode: MethodDeclaration, sourceFile: SourceFile): string { return ""; }

	/**
	 * Prints out the item
	 *
	 * - `out-name` is the target canvas item to be called after the item is executed
	 */
	printItem(pos: number): string {
		const stringBuilder = new StringBuilderClass("", "");

		const targetItem = findTargetItem(this.itemInfo.target, pos, this.itemInfo);
		if (targetItem === null) {
			throw new Error(`Unable to find target item for ${this.getDecoratorType()} item`);
		}

		stringBuilder.append(`<workflow-item`
			+ ` name="item${pos}"`
			+ ` out-name="${targetItem}"`
			+ ` type="${this.getCanvasType()}"`
			+ ` linked-workflow-id="${this.itemInfo.canvasItemPolymorphicBag.linkedItem}"`
			+ ">").appendLine();
		stringBuilder.indent();
		stringBuilder.append(`<display-name><![CDATA[${this.itemInfo.name}]]></display-name>`).appendLine();
		stringBuilder.appendContent(buildItemParameterBindings(this.itemInfo, InputOutputBindings.IN_BINDINGS));
		stringBuilder.appendContent(buildItemParameterBindings(this.itemInfo, InputOutputBindings.OUT_BINDINGS));
		stringBuilder.append(`<position x="${225 + 160 * (pos - 1)}.0" y="55.40909090909091" />`).appendLine();
		stringBuilder.unindent();
		stringBuilder.append(`</workflow-item>`).appendLine();

		return stringBuilder.toString();
	}
}
