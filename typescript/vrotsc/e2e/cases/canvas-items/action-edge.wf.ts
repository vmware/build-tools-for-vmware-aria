import { Workflow, Out, In, Item, ActionItem } from "vrotsc-annotations";

@Workflow({
	name: "Action Edge",
	description: "Goes to end by itself",
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
		scriptModule: "com.vmware.pscoe.onboarding.sgenov.actions/test"
	})
	public callTestAction(@In first: number, @In second: number, @Out actionResult: ActionResult) {
	}
}
