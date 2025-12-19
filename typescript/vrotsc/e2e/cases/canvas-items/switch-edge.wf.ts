import { SwitchItem, Workflow } from "vrotsc-annotations";

@Workflow({
    name: "Switch Edge Cases",
    path: "VMware/PSCoE",
    description: "Switch test covering edge cases - various operators, no default target, and complex conditions",
    attributes: {
        errorCode: {
            type: "number"
        },
        priority: {
            type: "number"
        },
        status: {
            type: "string"
        },
    }
})
export class SwitchEdgeCases {

    @SwitchItem({
        cases: [
            { condition: 404, target: "handleNotFound", variable: "errorCode", type: "number", comparator: "equals" },
            { condition: 500, target: "handleServerError", variable: "errorCode", type: "number", comparator: "different" }
        ],
        target: "switchPriority"
    })
    public switchErrorCodes(errorCode: number) {
        // Error code switch without default using various equality operators
        System.log("Processing error code: " + errorCode);
    }

    @SwitchItem({
        cases: [
            { condition: 1, target: "lowPriority", variable: "priority", type: "number", comparator: "smaller" },
            { condition: 5, target: "mediumPriority", variable: "priority", type: "number", comparator: "greater" }
        ],
        target: "switchStatus"
    })
    public switchPriority(priority: number) {
        // Priority switch using comparison operators
        System.log("Processing priority: " + priority);
    }

    @SwitchItem({
        cases: [
            { condition: "active", target: "processActive", variable: "status", type: "string", comparator: "equals" },
            { condition: "pending", target: "processPending", variable: "status", type: "string", comparator: "contains" }
        ],
        target: "handleNotFound"
    })
    public switchStatus(status: string) {
        // String status switch with mixed operators
        System.log("Processing status: " + status);
    }

    public handleNotFound() {
        System.log("404 - Resource not found");
    }

    public handleServerError() {
        System.log("500 - Internal server error");
    }

    public handleForbidden() {
        System.log("403 - Access forbidden");
    }

    public lowPriority() {
        System.log("Low priority task (<=1)");
    }

    public mediumPriority() {
        System.log("Medium priority task (>5)");
    }

    public highPriority() {
        System.log("High priority task (>=10)");
    }

    public handleInvalidPriority() {
        System.log("Invalid priority level");
    }

    public processActive() {
        System.log("Processing active status");
    }

    public processPending() {
        System.log("Processing pending status (not equal to 'pending')");
    }

    public processInactive() {
        System.log("Processing inactive status (not strictly equal to 'inactive')");
    }

    public handleUnknownStatus() {
        System.log("Unknown status - using default handler");
    }

    public fallbackHandler() {
        System.log("Unhandled error code - using fallback");
    }
}
