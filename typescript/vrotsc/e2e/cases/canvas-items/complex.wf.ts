import { Workflow, Out, In, Item, RootItem, DecisionItem, WaitingTimerItem } from "vrotsc-annotations";

@Workflow({
	name: "Complex",
	path: "VMware/PSCoE",
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
	
	@DecisionItem({
		target: "waitForEvent",
		else: null
	})
	public decisionElement(waitingTimer: Date) {
		return waitingTimer !== null;
	}

	@Item({
		target: "decisionElement",
		exception: ""
	})
	public execute(
		@Out @In waitingTimer: Date,
		@Out @In counter: number
	): void {
		if (!counter) {
			counter = 0;
		}

		counter++;

		if (counter < 2) {
			const tt = Date.now() + 5 * 1000;
			waitingTimer = new Date(tt);
		} else {
			waitingTimer = null;
		}

		System.log("Counter: " + counter);
		System.log("Waiting Timer: " + waitingTimer);
	}

	@Item({
		target: "execute",
		exception: ""
	})
	@RootItem()
	public start() {
		System.log("Starting workflow");
	}

	@WaitingTimerItem({
		target: "execute"
	})
	public waitForEvent(@In waitingTimer: Date) {
		// NOOP
	}
}
