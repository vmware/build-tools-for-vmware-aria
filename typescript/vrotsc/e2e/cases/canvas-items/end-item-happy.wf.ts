import { Workflow, RootItem, DecisionItem, WorkflowEndItem } from "vrotsc-annotations";

@Workflow({
	name: "Workflow End Happy",
	path: "VMware/PSCoE",
	description: "Workflow with root and end item",
	attributes: {
		endMode: {
			type: "number"
		}
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
		endMode: 0
	})
	public workflowEnd() {
		// NOOP
	}
}
