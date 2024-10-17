import { Workflow, In, WaitingTimerItem } from "vrotsc-annotations";

@Workflow({
	name: "Waiting Timer Edge",
	path: "VMware/PSCoE",
	description: "Waiting timer will point to shouldGoHere with target",
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

	@WaitingTimerItem({})
	public waitForEvent(@In waitingTimer: Date) {
		// NOOP
	}

	public shouldGoHere() {
		// NOOP
	}
}
