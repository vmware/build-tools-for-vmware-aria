import { Workflow, RootItem, WorkflowEndItem } from "vrotsc-annotations";

@Workflow({
	name: "Workflow End Exception",
	path: "VMware/PSCoE",
	description: "Workflow with root and end item with end mode 1",
	attributes: {
		errorMessage: {
			type: "string"
		},
		businessStatus: {
			type: "string"
		},
		endMode: {
			type: "number"
		}
	}
})
export class WorkflowEnd {

	@RootItem()
	public initiateWorkflow() {
		// NOOP
	}

	@WorkflowEndItem({
		endMode: 1,
		exceptionVariable: "errorMessage",
		businessStatus: "Bad"
	})
	public workflowEnd() {
		// NOOP
	}
}
