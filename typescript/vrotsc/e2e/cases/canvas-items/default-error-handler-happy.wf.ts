import { Workflow, Out, RootItem, DefaultErrorHandler } from "vrotsc-annotations";

@Workflow({
	name: "Default Error Handler Happy",
	path: "VMware/PSCoE",
	description: "Default error handler workflow",
	attributes: {
		errorMessage: {
			type: "string"
		}
	}
})
export class HandleDefaultError {

	@RootItem()
	public initiateWorkflow() {
		// NOOP
	}

	@DefaultErrorHandler({
		exception: "errorMessage"
	})
	public defaultErrorHandler(@Out errorMessage: string) {
		// NOOP
	}
}
