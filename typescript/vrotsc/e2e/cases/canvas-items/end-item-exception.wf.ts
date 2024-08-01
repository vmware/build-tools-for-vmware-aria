import { Workflow, RootItem, DecisionItem, WorkflowEndItem } from "vrotsc-annotations";

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
	}
})
export class WorkflowEnd {

	@RootItem()
	@DecisionItem({
		target: "end",
		else: "workflowEnd"
	})
	public initiateWorkflow() {
		return true;
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
