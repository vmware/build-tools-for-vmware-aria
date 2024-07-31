import { Workflow, In, Item, RootItem } from "vrotsc-annotations";

@Workflow({
	name: "Root When Set",
	path: "VMware/PSCoE",
	description: "root is shouldGoHere",
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
		target: "end"
	})
	public waitForEvent(@In waitingTimer: Date) {
		// NOOP
	}

	@RootItem()
	@Item({
		target: "waitForEvent"
	})
	public shouldGoHere() {
		// NOOP
	}
}
