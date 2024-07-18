import { Workflow, In, Out, RootItem, WorkflowEndItem } from "vrotsc-annotations";

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
	public initiateWorkflow() {
		// NOOP
	}

	@WorkflowEndItem({
		endMode: 0
	})
	public workflowEnd(@In endMode: number) {
		// NOOP
	}
}
