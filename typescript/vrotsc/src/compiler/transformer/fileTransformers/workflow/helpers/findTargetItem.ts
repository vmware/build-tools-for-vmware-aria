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
import { WorkflowItemDescriptor } from "../../../../decorators";
import { Graph } from "../decorators/helpers/graph";
import { findItemByName } from "./findItemByName";

/**
 * Helper function to find the target item for the given item
 *
 * @param target The name of the target item (optional)
 * @param pos The position of the current item in the workflow.
 *  If target isn't specified, the element in the next position will be targeted.
 * @param item The current item. Used to get the list of all items in the workflow.
 * @returns The name of the target item:
 * If the `item.target` is "end", it will return the default end item name (see {@link Graph.DEFAULT_END})
 * If the `item.target` is defined, it will return the item with the given name, throw if not found
 * If the `item.target` is null/undefined, it will return the next item in the workflow
 * or the default end item name when there is no such item or it cannot be targeted
 * If the resulting element cannot be targeted:
 * - if explicitly targeted (target != null), logs a WARNING before returning the resulting element.
 * - if not explicitly targeted, returns the default end item name instead and logs a corresponding DEBUG level message;
 * @throws Error if the specified target is missing.
 */
export function findTargetItem(target: any, pos: number, item: WorkflowItemDescriptor): string {
	const DEFAULT_END_ITEM_NAME = Graph.DEFAULT_END.name;
	const wfItems = item.parent.items;
	const nextInd = target == null ? pos + 1 : findItemByName(wfItems, target);
	const nextItem = wfItems[nextInd - 1];
	const isExplicitTarget = target != null && target !== "end";
	if (!nextItem) {
		if (!isExplicitTarget) {
			return DEFAULT_END_ITEM_NAME;
		}
		throw new Error(`Could not find next workflow element after '${item.name}': `
			+ `there is no element [${target == null ? nextInd - 1 : target}] in: [${wfItems.map(i => i.name)}]`);
	}
	if (nextItem.strategy.isNotTargetable) {
		const errMsg = `'${item.name}' cannot target '${nextItem.name}' with type '${nextItem.strategy.getCanvasType()}'`;
		if (!isExplicitTarget) {
			console.debug(`${errMsg}. Targeting the default end element (${DEFAULT_END_ITEM_NAME}) instead.`);
			return DEFAULT_END_ITEM_NAME;
		}
		console.warn(`${errMsg}!`);
	}
	return `item${nextInd}`;
}
