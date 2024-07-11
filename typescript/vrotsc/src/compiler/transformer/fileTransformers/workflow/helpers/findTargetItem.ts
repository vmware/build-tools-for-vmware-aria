import { WorkflowItemDescriptor } from "../../../../decorators";
import { findItemByName } from "./findItemByName";

/**
 * Helper function to find the target item for the given item
 *
 * If the `item.target` is "end", it will return "item0"
 * If the `item.target` is not null, it will return the item with the given name, or 0 if not found
 * If the `item.target` is null, it will return the next item in the workflow, or "item0" if it is the last item
 *
 *
 * @param item The item to find the target for
 * @param pos The position of the item in the workflow
 * @param items The list of all items in the workflow
 * @returns The name of the target item
 */
export function findTargetItem(target: any, pos: number, item: WorkflowItemDescriptor): string {
	if (target === "end") {
		return "item0";
	}
	if (target != null) {
		return `item${findItemByName(item.parent.items, target) || 0}`;
	}

	return pos < item.parent.items.length ? `item${pos + 1}` : "item0";
}
