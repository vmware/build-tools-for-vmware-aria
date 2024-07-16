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
import { StringBuilderClass } from "../../../../../utilities/stringBuilder";
import { findTargetItem } from "../helpers/findTargetItem";
import { InputOutputBindings, buildItemParameterBindings } from "./helpers/presentation";

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
export default class WaitingTimerItemDecoratorStrategy implements CanvasItemDecoratorStrategy {
	constructor(private readonly sourceFilePrinter: SourceFilePrinter = new DefaultSourceFilePrinter()) { }

	getCanvasType(): string {
		return "waiting-timer";
	}

	getDecoratorType(): WorkflowItemType {
		return WorkflowItemType.WaitingTimer;
	}

	registerItemArguments(itemInfo: WorkflowItemDescriptor, decoratorNode: Decorator): void {
		getDecoratorProps(decoratorNode).forEach((propTuple) => {
			const [propName, propValue] = propTuple;
			switch (propName) {
				case "target":
					itemInfo.target = propValue;
					break;

				default:
					throw new Error(`Item attribute '${propName}' is not supported for ${this.getDecoratorType()} item`);
			}
		});
	}


	/**
	 * There is no need to print the source file for a waiting timer item
	 */
	printSourceFile(methodNode: MethodDeclaration, sourceFile: SourceFile): string { return ""; }

	/**
	 * Prints the waiting timer to the workflow file
	 *
	 * - `out-name` is the name of the item that the waiting timer will transition to after the timer is complete
	 *
	 * @param itemInfo The item to print
	 * @param pos The position of the item in the workflow
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

		stringBuilder.append(`<display-name><![CDATA[${itemInfo.name}]]></display-name>`).appendLine();
		stringBuilder.appendContent(buildItemParameterBindings(itemInfo, InputOutputBindings.IN_BINDINGS));
		stringBuilder.appendContent(buildItemParameterBindings(itemInfo, InputOutputBindings.OUT_BINDINGS));
		stringBuilder.append(`<position x="${225 + 160 * (pos - 1)}.0" y="55.40909090909091" />`).appendLine();
		stringBuilder.unindent();
		stringBuilder.append(`</workflow-item>`).appendLine();

		return stringBuilder.toString();
	}
}
