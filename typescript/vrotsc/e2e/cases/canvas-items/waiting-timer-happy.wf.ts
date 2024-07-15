import { Workflow, In, WaitingTimerItem } from "vrotsc-annotations";

@Workflow({
	name: "Waiting Timer Happy",
	path: "VMware/PSCoE",
	description: "Waiting timer will point with target to shouldGoHere",
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
	@WaitingTimerItem({
		target: "shouldGoHere"
	})
	public waitForEvent(@In waitingTimer: Date) {
	}

	public shouldGoHere() { }
}
