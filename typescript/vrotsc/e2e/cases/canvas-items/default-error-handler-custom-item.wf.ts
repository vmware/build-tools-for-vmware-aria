import { Workflow, RootItem, In, Out, Item, DefaultErrorHandler, WorkflowEndItem } from "vrotsc-annotations";

@Workflow({
	name: "Default Error Handler Custom Item",
	path: "VMware/PSCoE",
	description: "Default error handler workflow with error handler redirecting to a workflow item",
	attributes: {
		errorMessage: {
			type: "string"
		}
	}
})
export class HandleDefaultError {

	@RootItem()
	public initiateWorkflow() {
		System.log("Initiating workflow execution");
	}

	@Item({
		target: "workflowEnd"
	})
	public processError(@In errorMessage: string) {
		System.log(`Processing error using custom task with message '${errorMessage}'`);
	}

	@DefaultErrorHandler({
		exceptionVariable: "errorMessage",
		target: "processError"
	})
	public defaultErrorHandler(@Out errorMessage: string) {
		// NOOP
	}

	@WorkflowEndItem({
		endMode: 0,
		exceptionVariable: "errorMessage",
	})
	public workflowEnd(@Out errorMessage: string) {
		System.log(`Terminating workflow with error ${errorMessage}`);
	}
}
