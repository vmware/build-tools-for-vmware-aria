import { Workflow, RootItem, DefaultErrorHandler, Err } from "vrotsc-annotations";

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
		target: "end"
	})
	public defaultErrorHandler(@Err errorMessage: string) {
		// NOOP
	}
}
