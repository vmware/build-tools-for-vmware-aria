import { Item, SwitchItem, Workflow } from "vrotsc-annotations";

@Workflow({
    name: "Switch String Cases",
    path: "VMware/PSCoE",
    description: "Switch test with string conditions",
    attributes: {
        status: {
            type: "string"
        },
    }
})
export class SwitchStringCases {

    @SwitchItem({
        cases: [
            { condition: "active", target: "processActive", variable: "status", type: "string", comparator: "equals" },
            { condition: "pending", target: "processPending", variable: "status", type: "string", comparator: "contains" },
            { condition: "in progress", target: "processInProgress", variable: "status", type: "string", comparator: "match" },
            { condition: "", target: "processInactive", variable: "status", type: "string", comparator: "is defined" }
        ],
        target: "handleUnknownStatus"
    })
    public switchByStatus(status: string) {
        // Switch logic for string conditions
        if (status === null || status === undefined) {
            throw new Error("Status cannot be null");
        }
    }

    @Item({ target: "end" })
    public processActive() {
        System.log("Processing active status");
    }
    @Item({ target: "end" })
    public processPending() {
        System.log("Processing pending status");
    }

    @Item({ target: "end" })
    public processInProgress() {
        System.log("Processing in progress status");
    }

    @Item({ target: "end" })
    public processInactive() {
        System.log("Processing inactive status");
    }

    @Item({ target: "end" })
    public handleUnknownStatus() {
        System.log("Unknown status encountered");
    }
}
