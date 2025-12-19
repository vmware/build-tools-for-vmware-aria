import { SwitchItem, Workflow } from "vrotsc-annotations";

@Workflow({
    name: "Switch Boolean Cases",
    path: "VMware/PSCoE",
    description: "Switch test with boolean conditions and mixed types",
    attributes: {
        isEnabled: {
            type: "boolean"
        }
    }
})
export class SwitchBooleanCases {

    @SwitchItem({
        cases: [
            { condition: true, target: "enableFeature", variable: "isEnabled", type: "boolean", comparator: "is true" },
            { condition: false, target: "disableFeature", variable: "isEnabled", type: "boolean", comparator: "is false" }
        ],
        defaultTarget: "handleUndefined"
    })
    public switchByBoolean(isEnabled: boolean) {
        // Boolean switch logic
    }

    @Item({ target: "end" })
    public enableFeature() {
        System.log("Feature is enabled");
    }

    @Item({ target: "end" })
    public disableFeature() {
        System.log("Feature is disabled");
    }

    @Item({ target: "end" })
    public handleUndefined() {
        System.log("Boolean value is undefined");
    }
}
