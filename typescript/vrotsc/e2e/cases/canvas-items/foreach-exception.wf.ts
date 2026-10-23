import { ForeachItem, Item, RootItem, Workflow, WorkflowEndItem, In, Out, Err } from "vrotsc-annotations";

@Workflow({
    name: "Foreach Exception",
    path: "VMware/PSCoE",
    description: "Foreach test with exception handling that routes to a workflow end item on error",
    attributes: {
        items: {
            type: "Array/string"
        },
        results: {
            type: "Array/string"
        },
        errorMessage: {
            type: "string"
        }
    }
})
export class ForeachException {

    @ForeachItem({
        linkedItem: "9e4503db-cbaa-435a-9fad-144409c08df0",
        target: "logSuccess",
        exception: "workflowEnd"
    })
    @RootItem()
    public foreachElement(@In items: string[], @Out results: string[]) {
        // Foreach logic is handled automatically by the linked workflow
    }

    @Item({ target: "end" })
    public logSuccess() {
        System.log("All iterations completed successfully.");
    }

    @WorkflowEndItem({
        endMode: 1,
        businessStatus: "Bad"
    })
    public workflowEnd(@Err errorMessage: string) {
        // NOOP
    }
}
