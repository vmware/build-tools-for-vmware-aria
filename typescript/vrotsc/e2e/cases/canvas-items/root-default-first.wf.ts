import { Workflow, In, Item } from "vrotsc-annotations";

@Workflow({
	name: "Root Default First",
	path: "VMware/PSCoE",
	description: "default is first item",
	attributes: {
		waitingTimer: {
			type: "Date"
		},
		counter: {
			type: "number"
		}
	}
})
export class Complex {

	@Item({
		target: "shouldGoHere"
	})
	public waitForEvent(@In waitingTimer: Date) {
		// NOOP
	}

	public shouldGoHere() {
		// NOOP
	}
}
