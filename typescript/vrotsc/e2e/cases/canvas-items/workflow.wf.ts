import { Workflow, Out, In, Item, RootItem, DecisionItem, WorkflowEndItem, WaitingTimerItem, WorkflowItem } from "vrotsc-annotations";

@Workflow({
	name: "Workflow Test",
	path: "VMware/PSCoE",
	description: "Calling another workflow and binding values correctly",
	attributes: {
		waitingTimer: {
			type: "Date"
		},
		counter: {
			type: "number"
		},
		first: {
			type: "number"
		},
		second: {
			type: "number"
		},
		result: {
			type: "number"
		},
		errorMessage: {
			type: "string"
		}
	}
})
export class HandleNetworkConfigurationBackup {
	@DecisionItem({ target: "waitForEvent", else: "prepareItems" })
	public decisionElement(waitingTimer: Date) {
		return waitingTimer !== null;
	}

	@Item({ target: "callOtherWf" })
	public prepareItems(@In @Out first: number, @In @Out second: number) {
		first = 1;
		second = 2;
	}

	@WorkflowItem({
		target: "print",
		linkedItem: "9e4503db-cbaa-435a-9fad-144409c08df0"
	})
	public callOtherWf(@In first: number, @In second: number, @Out result: number) {
		// NOOP
	}

	@Item({ target: "end" })
	public print(@In result: number) {
		System.log("Result: " + result);
	}

	@Item({ target: "decisionElement", exception: "" })
	public execute(@Out @In waitingTimer: Date, @Out @In counter: number): void {
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

	@Item({ target: "execute", exception: "" })
	@RootItem()
	public start() {
		System.log("Starting workflow");
	}

	@WaitingTimerItem({ target: "execute" })
	public waitForEvent(@In waitingTimer: Date) {
		// NOOP
	}

	@WorkflowEndItem({
		endMode: 0,
		exceptionVariable: "errorMessage"
	})
	public workflowEnd(@In endMode: number, @Out errorMessage: string) {
		// NOOP
	}
}
