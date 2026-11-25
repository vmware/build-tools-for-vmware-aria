import { ForeachItem, RootItem, Workflow } from "vrotsc-annotations";

@Workflow({
    name: "Foreach Minimal",
    path: "VMware/PSCoE",
    description: "Minimal foreach test without input or output bindings"
})
export class ForeachMinimal {

    @ForeachItem({
        linkedItem: "9e4503db-cbaa-435a-9fad-144409c08df0",
        target: "end"
    })
    @RootItem()
    public foreachElement() {
        // Foreach logic is handled automatically by the linked workflow
    }
}
