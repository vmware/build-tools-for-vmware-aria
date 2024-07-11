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
	const index = items.findIndex(item => item.name === name);
	return index === -1 ? 0 : index + 1;;
}
