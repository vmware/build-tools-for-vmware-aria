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
import { StringBuilderClass } from "../../../../../../utilities/stringBuilder";
import { WorkflowItemDescriptor, WorkflowItemType } from "../../../../../decorators";

export enum InputOutputBindings {
	IN_BINDINGS = "in-binding",
	OUT_BINDINGS = "out-binding",
}

/**
 * This will build the parameter bindings for the given item
 *
 * In case of a waiting timer, the `timer.date` parameter will be bound instead of the actual parameter name, as this is required by vRO
 *
 * @param canvasItemStrategy - The strategy for the canvas item
 * @param parameterType The name of the parent element. `in-bindings` or `out-bindings`
 * @returns void
 */
export function buildItemParameterBindings(
	itemInfo: WorkflowItemDescriptor,
	parameterType: InputOutputBindings
): string {
	const stringBuilder = new StringBuilderClass("", "");
	const parameters = parameterType === InputOutputBindings.IN_BINDINGS ? itemInfo.input : itemInfo.output;

	if (!parameters?.length) {
		return stringBuilder.toString();
	}

	stringBuilder.append(`<${parameterType}>`).appendLine();
	stringBuilder.indent();
	parameters.forEach(paramName => {
		const param = itemInfo.parent.parameters.find(p => p.name === paramName);
		if (param) {
			const isWaitingTimer = itemInfo.item.getDecoratorType() === WorkflowItemType.WaitingTimer;

			stringBuilder.append(`<bind name="${isWaitingTimer ? "timer.date" : param.name}" type="${param.type}" export-name="${param.name}" />`).appendLine();
		}
	});
	stringBuilder.unindent();
	stringBuilder.append(`</${parameterType}>`).appendLine();

	return stringBuilder.toString();
}
