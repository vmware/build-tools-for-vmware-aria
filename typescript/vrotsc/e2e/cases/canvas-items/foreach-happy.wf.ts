import { ForeachItem, Item, RootItem, Workflow, In, Out } from "vrotsc-annotations";

@Workflow({
    name: "Foreach Happy Path",
    path: "VMware/PSCoE",
    description: "Basic foreach test that iterates a string array and calls a linked workflow for each element",
    attributes: {
        items: {
            type: "Array/string"
        }
    }
})
export class ForeachHappyPath {

    @ForeachItem({
        linkedItem: "9e4503db-cbaa-435a-9fad-144409c08df0",
        target: "end"
    })
    @RootItem()
    public foreachElement(@In items: string[]) {
        // Foreach logic is handled automatically by the linked workflow
    }
}
