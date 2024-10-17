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

/**
 * This function will find the index of the item with the given name, or return 0 if not found
 *
 * The index is incremented by 1, as the first item in the workflow is always the end item
 *
 * @param items The list of items to search
 * @param name The name of the item to find
 * @returns The index of the item with the given name, or 0 if not found
 */
export function findItemByName(items: WorkflowItemDescriptor[], name: string): number {
	return 1 + items.findIndex(item => item.name === name);
}
