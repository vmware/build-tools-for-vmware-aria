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
import { InputOutputBindings, buildItemParameterBindings } from "./helpers/presentation";
import { ScheduledWorkflowItemSourceFilePrinter } from "./helpers/sourceFile";
import BaseItemDecoratorStrategy from "./base/baseItemDecoratorStrategy";

/**
 *
 * Responsible for printing out the workflow item:
 * @example
 * ```xml
  <workflow-item name="item1" out-name="item0" type="task" launched-workflow-id="9e4503db-cbaa-435a-9fad-144409c08df0">
	<display-name><![CDATA[Other Workflow]]></display-name>
	<script encoded="false"><![CDATA[//Auto generated script, cannot be modified !
var workflowToLaunch = Server.getWorkflowWithId("9e4503db-cbaa-435a-9fad-144409c08df0");
if (workflowToLaunch == null) {
	throw "Workflow not found";
}

var workflowParameters = new Properties();
workflowParameters.put("first",first);
workflowParameters.put("second",second);
scheduledTask = workflowToLaunch.schedule(workflowParameters, workflowScheduleDate);
]]></script>
	<in-binding>
	  <bind name="workflowScheduleDate" type="Date" export-name="workflowScheduleDate"/>
	  <bind name="first" type="number" export-name="first"/>
	  <bind name="second" type="number" export-name="second"/>
	</in-binding>
	<out-binding>
	  <bind name="scheduledTask" type="Task"/>
	</out-binding>
	<description><![CDATA[Schedule a workflow and create a task.]]></description>
	<position y="60.0" x="260.0"/>
  </workflow-item>
 * ```
 */
export default class ScheduledWorkflowItemDecoratorStrategy extends BaseItemDecoratorStrategy {
	public constructor() {
		super();
		this.sourceFilePrinter = new ScheduledWorkflowItemSourceFilePrinter();
	}

	/**
	 * @returns The type of canvas item
	 */
	public getCanvasType(): string {
		return "task";
	}

	/**
	 * @returns The type of decorator
	 */
	public getDecoratorType(): WorkflowItemType {
		return WorkflowItemType.ScheduledWorkflow;
	}

	/**
	 * Registers the item arguments
	 *
	 * - `target` is the name of the item to call after the item is executed
	 * - `exception` is the exception to throw if the item fails
	 * - `linkedItem` is the id of the workflow to schedule
	 *
	 * @param itemInfo The item to register
	 * @param decoratorNode The decorator node
	 * @returns void
	 * @throws Error if an unsupported attribute is found
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
	 * There is no need to print the source file for the workflow item
	 */
	public printSourceFile(methodNode: MethodDeclaration, sourceFile: SourceFile, itemInfo: WorkflowItemDescriptor): string {
		return this.sourceFilePrinter.printSourceFile(methodNode, sourceFile, itemInfo);
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
		this.validateNeededParameters(itemInfo);

		const stringBuilder = new StringBuilderClass("", "");
		const targetItem = super.findTargetItem(itemInfo.target, pos, itemInfo);
		if (targetItem === null) {
			throw new Error(`Unable to find target item for ${this.getDecoratorType()} item`);
		}

		stringBuilder.append(`<workflow-item`
			+ ` name="item${pos}"`
			+ ` out-name="${targetItem}"`
			+ ` type="${this.getCanvasType()}"`
			+ ` launched-workflow-id="${itemInfo.canvasItemPolymorphicBag.linkedItem}" `
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
		stringBuilder.appendContent(buildItemParameterBindings(itemInfo, InputOutputBindings.IN_BINDINGS));
		stringBuilder.appendContent(buildItemParameterBindings(itemInfo, InputOutputBindings.OUT_BINDINGS));
		stringBuilder.append(super.formatItemPosition([x, y])).appendLine();
		stringBuilder.unindent();
		stringBuilder.append(`</workflow-item>`).appendLine();

		return stringBuilder.toString();
	}

	/**
	 * Validates that the item has all the required parameters
	 *
	 * Inputs:
	 * - {Date} workflowScheduleDate
	 *
	 * Outputs:
	 * - {Task} scheduledTask (optional) but if present, it should be the only output
	 *
	 * @param itemInfo The item to validate
	 * @throws Error if the item is missing required parameters
	 * @returns void
	 */
	private validateNeededParameters(itemInfo: WorkflowItemDescriptor): void {
		const inputs = itemInfo.input;

		["workflowScheduleDate"].forEach((input) => {
			if (!inputs.includes(input)) {
				throw new Error(`Decorator ${this.getDecoratorType()} is missing required input: ${input}`);
			}
		});

		const outputs = itemInfo.output;
		if (outputs.length && outputs.length !== 1 && !outputs.includes("scheduledTask")) {
			throw new Error(`Decorator ${this.getDecoratorType()} has an invalid output: ${outputs}, expected: scheduledTask`);
		}
	}
}

