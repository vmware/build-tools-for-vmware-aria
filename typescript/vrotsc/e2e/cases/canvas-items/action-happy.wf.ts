import { Workflow, Out, In, Item, ActionItem } from "vrotsc-annotations";

@Workflow({
	name: "Action Happy Path",
	path: "VMware/PSCoE",
	attributes: {
		first: {
			type: "number"
		},
		second: {
			type: "number"
		},
		actionResult: {
			type: "ActionResult"
		}
	}
})
export class Example {
	@ActionItem({
		target: "printActionResult",
		scriptModule: "com.vmware.pscoe.onboarding.sgenov.actions/test"
	})
	public callTestAction(@In first: number, @In second: number, @Out actionResult: ActionResult) {
	}

	@Item({ target: "end" })
	public printActionResult(@In actionResult: ActionResult) {
		System.log(`Action result: ${actionResult.getResult()}`);
	}
}
