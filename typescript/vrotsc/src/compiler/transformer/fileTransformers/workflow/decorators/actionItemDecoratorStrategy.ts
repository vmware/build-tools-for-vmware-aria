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
import { WorkflowItemDescriptor, WorkflowItemType } from "../../../../decorators";
import { getDecoratorProps } from "../../../helpers/node";
import { findTargetItem } from "../helpers/findTargetItem";
import CanvasItemDecoratorStrategy from "./canvasItemDecoratorStrategy";
import { InputOutputBindings, buildItemParameterBindings } from "./helpers/presentation";
import { ActionItemSourceFilePrinter, SourceFilePrinter } from "./helpers/sourceFile";

/**
 *
 * Responsible for printing out the workflow item:
 * @example
 * ```xml
<workflow-item name="item3" out-name="item4" type="task"
		script-module="com.vmware.stef/PrintStef">
		<display-name><![CDATA[PrintStef]]></display-name>
		<script encoded="false"><![CDATA[//Auto generated script, cannot be modified !
actionResult = System.getModule("com.vmware.stef").PrintStef(a,b);
]]></script>
		<in-binding>
			<bind name="a" type="number" export-name="a" />
			<bind name="b" type="number" export-name="b" />
		</in-binding>
		<out-binding>
			<bind name="actionResult" type="number" export-name="actionResult" />
		</out-binding>
		<description><![CDATA[Add a note to the workflow schema.]]></description>
		<position y="50.0" x="260.0" />
	</workflow-item>
 * ```
 */
export default class ActionItemDecoratorStrategy implements CanvasItemDecoratorStrategy {
	constructor(private readonly sourceFilePrinter: SourceFilePrinter = new ActionItemSourceFilePrinter()) { }

	getCanvasType(): string {
		return "task";
	}

	getDecoratorType(): WorkflowItemType {
		return WorkflowItemType.Action;
	}

	registerItemArguments(itemInfo: WorkflowItemDescriptor, decoratorNode: Decorator): void {
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

				case "scriptModule":
					itemInfo.canvasItemPolymorphicBag.scriptModule = propValue;
					break;

				default:
					throw new Error(`Item attribute '${propName}' is not supported for ${this.getDecoratorType()} item`);
			}
		});
	}

	/**
	 * There is no need to print the source file for the workflow item
	 */
	printSourceFile(methodNode: MethodDeclaration, sourceFile: SourceFile, itemInfo: WorkflowItemDescriptor): string {
		return this.sourceFilePrinter.printSourceFile(methodNode, sourceFile, itemInfo);
	}

	/**
	 * Prints out the item
	 *
	 * - `out-name` is the target canvas item to be called after the item is executed
	 * - `script-module` is the script module to be called
	 *
	 * @param itemInfo The item to print
	 * @param pos The position of the item in the workflow
	 *
	 * @returns The string representation of the item
	 */
	printItem(itemInfo: WorkflowItemDescriptor, pos: number): string {
		const stringBuilder = new StringBuilderClass("", "");

		this.validateNeededParameters(itemInfo);

		const targetItem = findTargetItem(itemInfo.target, pos, itemInfo);
		if (targetItem === null) {
			throw new Error(`Unable to find target item for ${this.getDecoratorType()} item`);
		}

		stringBuilder.append(`<workflow-item`
			+ ` name="item${pos}"`
			+ ` out-name="${targetItem}"`
			+ ` type="${this.getCanvasType()}"`
			+ ` script-module="${itemInfo.canvasItemPolymorphicBag.scriptModule}"`
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

	/**
	 * Validates that the item has all the required parameters
	 *
	 * Outputs:
	 * There must be exactly one output
	 *
	 * @param itemInfo The item to validate
	 * @throws Error if the item is missing required parameters
	 * @returns void
	 */
	private validateNeededParameters(itemInfo: WorkflowItemDescriptor): void {
		const outputs = itemInfo.output;

		if (outputs.length && outputs.length !== 1) {
			throw new Error(`Decorator ${this.getDecoratorType()} needs exactly one output, but actually: ${outputs.length}`);
		}
	}
}
