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
import BaseItemDecoratorStrategy from "./base/baseItemDecoratorStrategy";
import { InputOutputBindings } from "./helpers/presentation";
import { ActionItemSourceFilePrinter } from "./helpers/sourceFile";

/**
 *
 * Responsible for printing out the workflow item:
 * @example
 * ```xml
<workflow-item name="item3" out-name="item4" type="task"
		script-module="com.vmware.print/PrintItem">
		<display-name><![CDATA[PritItem]]></display-name>
		<script encoded="false"><![CDATA[//Auto generated script, cannot be modified !
actionResult = System.getModule("com.vmware.action").printItem(a,b);
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
export default class ActionItemDecoratorStrategy extends BaseItemDecoratorStrategy {

	public constructor() {
		super();
        this.sourceFilePrinter = new ActionItemSourceFilePrinter();
    }

	public getCanvasType(): string {
		return "task";
	}

	public getDecoratorType(): WorkflowItemType {
		return WorkflowItemType.Action;
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
	public printSourceFile(methodNode: MethodDeclaration, sourceFile: SourceFile, itemInfo: WorkflowItemDescriptor): string {
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
	 * @param x position on X axis that will be used for UI display
	 * @param y position on Y axis that will be used for UI display
	 *
	 * @returns The string representation of the item
	 */
	public printItem(itemInfo: WorkflowItemDescriptor, pos: number, x: number, y: number): string {
		const stringBuilder = new StringBuilderClass("", "");

		this.validateNeededParameters(itemInfo);
		const targetItem = super.findTargetItem(itemInfo.target, pos, itemInfo);
		if (targetItem === null) {
			throw new Error(`Unable to find target item for ${this.getDecoratorType()} item`);
		}

		stringBuilder.append(`<workflow-item`
			+ ` name="item${pos}"`
			+ ` out-name="${targetItem}"`
			+ ` type="${this.getCanvasType()}"`
			+ ` script-module="${itemInfo.canvasItemPolymorphicBag.scriptModule}"`
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
		stringBuilder.appendContent(super.buildParameterBindings(itemInfo, InputOutputBindings.OUT_BINDINGS))
		stringBuilder.append(super.formatItemPosition([x, y])).appendLine();
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
