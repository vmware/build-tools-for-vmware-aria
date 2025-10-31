import { Workflow, SwitchItem } from "vrotsc-annotations";

@Workflow({
	name: "Switch Happy Path",
	path: "VMware/PSCoE",
	description: "Basic switch test with multiple numeric cases and default target",
	attributes: {
		operationType: {
			type: "number"
		},
	}
})
export class SwitchHappyPath {

	@SwitchItem({
		cases: [
			{ condition: 1, target: "createResource", variable: "operationType", type: "number", comparator: "===" },
			{ condition: 2, target: "updateResource", variable: "operationType", type: "number", comparator: "===" },
			{ condition: 3, target: "deleteResource", variable: "operationType", type: "number", comparator: "===" }
		],
		defaultTarget: "logUnknownOperation"
	})
	public switchElement(operationType: number) {
		// Switch logic will be generated automatically
	}

	public createResource() {
		System.log("Creating resource");
	}

	public updateResource() {
		System.log("Updating resource");
	}

	public deleteResource() {
		System.log("Deleting resource");
	}

	public logUnknownOperation() {
		System.log("Unknown operation type");
	}
}