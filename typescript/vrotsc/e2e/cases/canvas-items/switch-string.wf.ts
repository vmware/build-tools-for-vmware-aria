import { Workflow, SwitchItem } from "vrotsc-annotations";

@Workflow({
	name: "Switch String Cases",
	path: "VMware/PSCoE",
	description: "Switch test with string conditions and exception handling",
	attributes: {
		status: {
			type: "string"
		},
	}
})
export class SwitchStringCases {

	@SwitchItem({
		cases: [
			{ condition: "active", target: "processActive", variable: "status", type: "string", comparator: "===" },
			{ condition: "pending", target: "processPending", variable: "status", type: "string", comparator: "===" },
			{ condition: "inactive", target: "processInactive", variable: "status", type: "string", comparator: "===" }
		],
		defaultTarget: "handleUnknownStatus",
		exception: "handleError"
	})
	public switchByStatus(status: string) {
		// Switch logic for string conditions
		if (status === null || status === undefined) {
			throw new Error("Status cannot be null");
		}
	}

	public processActive() {
		System.log("Processing active status");
	}

	public processPending() {
		System.log("Processing pending status");
	}

	public processInactive() {
		System.log("Processing inactive status");
	}

	public handleUnknownStatus() {
		System.log("Unknown status encountered");
	}

	public handleError() {
		System.log("Error occurred in switch processing");
	}
}