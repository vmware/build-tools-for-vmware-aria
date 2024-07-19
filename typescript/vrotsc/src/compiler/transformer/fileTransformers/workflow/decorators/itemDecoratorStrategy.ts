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
import { DefaultSourceFilePrinter, SourceFilePrinter } from "./helpers/sourceFile";
import { InputOutputBindings, buildItemParameterBindings } from "./helpers/presentation";
import { findTargetItem } from "../helpers/findTargetItem";
import { StringBuilderClass } from "../../../../../utilities/stringBuilder";

export default class ItemDecoratorStrategy implements CanvasItemDecoratorStrategy {
	constructor(private readonly sourceFilePrinter: SourceFilePrinter = new DefaultSourceFilePrinter()) { }

	getCanvasType(): string {
		return "task";
	}

	getDecoratorType(): WorkflowItemType {
		return WorkflowItemType.Item;
	}

	registerItemArguments(itemInfo: WorkflowItemDescriptor, decoratorNode: Decorator): void {
		getDecoratorProps(decoratorNode).forEach((propTuple) => {
			const [propName, propValue] = propTuple;
			switch (propName) {
				case "target":
					itemInfo.target = propValue;
					break;

				case "exception":
					itemInfo.canvasItemPolymorphicBag.exception = propValue;
					break;

				default:
					throw new Error(`Item attribute '${propName}' is not supported for ${this.getDecoratorType()} item`);
			}
		});
	}

	printSourceFile(methodNode: MethodDeclaration, sourceFile: SourceFile): string {
		return this.sourceFilePrinter.printSourceFile(methodNode, sourceFile);
	}

	/**
	 * Prints out the item
	 *
	 * - `out-name` is the target canvas item to be called after the item is executed
	 *
	 * @param itemInfo The item to print
	 * @param pos The position of the item in the workflow
	 *
	 * @returns The string representation of the item
	 */
	printItem(itemInfo: WorkflowItemDescriptor, pos: number): string {
		const stringBuilder = new StringBuilderClass("", "");

		const targetItem = findTargetItem(itemInfo.target, pos, itemInfo);
		if (targetItem === null) {
			throw new Error(`Unable to find target item for ${this.getDecoratorType()} item`);
		}

		stringBuilder.append(`<workflow-item`
			+ ` name="item${pos}"`
			+ ` out-name="${targetItem}"`
			+ ` type="${this.getCanvasType()}"`
			+ ">").appendLine();

		stringBuilder.indent();
		stringBuilder.append(`<script encoded="false"><![CDATA[${itemInfo.sourceText}]]></script>`).appendLine();

		stringBuilder.append(`<display-name><![CDATA[${itemInfo.name}]]></display-name>`).appendLine();
		stringBuilder.appendContent(buildItemParameterBindings(itemInfo, InputOutputBindings.IN_BINDINGS));
		stringBuilder.appendContent(buildItemParameterBindings(itemInfo, InputOutputBindings.OUT_BINDINGS));
		stringBuilder.append(`<position x="${225 + 160 * (pos - 1)}.0" y="55.40909090909091" />`).appendLine();
		stringBuilder.unindent();
		stringBuilder.append(`</workflow-item>`).appendLine();

		return stringBuilder.toString();
	}
}
