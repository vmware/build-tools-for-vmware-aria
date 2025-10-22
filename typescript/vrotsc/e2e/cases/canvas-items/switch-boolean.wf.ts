import { Workflow, SwitchItem } from "vrotsc-annotations";

@Workflow({
	name: "Switch Boolean Cases",
	path: "VMware/PSCoE",
	description: "Switch test with boolean conditions and mixed types",
	attributes: {
		isEnabled: {
			type: "boolean"
		},
		priority: {
			type: "number"
		},
	}
})
export class SwitchBooleanCases {

	@SwitchItem({
		cases: [
			{ condition: true, target: "enableFeature", variable: "isEnabled", type: "boolean", comparator: "===" },
			{ condition: false, target: "disableFeature", variable: "isEnabled", type: "boolean", comparator: "===" }
		],
		defaultTarget: "handleUndefined"
	})
	public switchByBoolean(isEnabled: boolean, priority: number) {
		// Boolean switch logic
	}

	public enableFeature() {
		System.log("Feature is enabled");
	}

	public disableFeature() {
		System.log("Feature is disabled");
	}

	public handleUndefined() {
		System.log("Boolean value is undefined");
	}
}