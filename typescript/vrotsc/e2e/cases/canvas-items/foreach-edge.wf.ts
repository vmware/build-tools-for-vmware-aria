import { ForeachItem, Item, RootItem, Workflow, In, Out } from "vrotsc-annotations";

@Workflow({
    name: "Foreach Edge Cases",
    path: "VMware/PSCoE",
    description: "Foreach test covering edge cases - no output bindings, chained foreach items, and foreach as non-root target",
    attributes: {
        inputItems: {
            type: "Array/string"
        },
        secondaryItems: {
            type: "Array/string"
        },
        results: {
            type: "Array/string"
        }
    }
})
export class ForeachEdgeCases {

    /**
     * Edge case 1: Foreach as a non-root item targeted by a regular task.
     * Input only, no output bindings -> alt-connections="1"
     */
    @Item({ target: "processEach" })
    @RootItem()
    public prepareItems(@Out inputItems: string[]) {
        inputItems = ["item-a", "item-b", "item-c"];
    }

    /**
     * Edge case 2: Foreach with only input bindings (no output) -> alt-connections="1".
     * Targets another foreach (chained foreach items).
     */
    @ForeachItem({
        linkedItem: "9e4503db-cbaa-435a-9fad-144409c08df0",
        target: "processEachSecondary"
    })
    public processEach(@In inputItems: string[]) {
        // No output binding - alt-connections should be 1 (input only)
    }

    /**
     * Edge case 3: Chained foreach - second foreach with both input and output bindings -> alt-connections="2".
     * Demonstrates sequential foreach operations on different arrays.
     */
    @ForeachItem({
        linkedItem: "9e4503db-cbaa-435a-9fad-144409c08df0",
        target: "logCompletion"
    })
    public processEachSecondary(@In secondaryItems: string[], @Out results: string[]) {
        // Input + Output binding - alt-connections should be 2
    }

    @Item({ target: "end" })
    public logCompletion() {
        System.log("All foreach iterations completed.");
    }
}
