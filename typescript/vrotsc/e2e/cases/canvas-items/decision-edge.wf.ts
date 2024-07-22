import { Workflow, DecisionItem } from "vrotsc-annotations";

@Workflow({
	name: "Decision Edge",
	path: "VMware/PSCoE",
	description: "decisionElement is the root, it will point with target to end and shouldGoHere with else. shouldGoHere points to end",
	attributes: {
		waitingTimer: {
			type: "Date"
		},
	}
})
export class HandleNetworkConfigurationBackup {

	@DecisionItem({
		target: "waitForEvent",
	})
	public decisionElement(waitingTimer: Date) {
		return waitingTimer !== null;
	}

	public shouldGoHere() {
		// NOOP
	}
}
