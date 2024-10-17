import { Workflow, DecisionItem } from "vrotsc-annotations";

@Workflow({
	name: "Decision Edge 2",
	path: "VMware/PSCoE",
	description: "decisionElement is the root, it will point with target and else to shouldGoHere. shouldGoHere points to end",
	attributes: {
		waitingTimer: {
			type: "Date"
		},
	}
})
export class HandleNetworkConfigurationBackup {
	
	@DecisionItem({})
	public decisionElement(waitingTimer: Date) {
		return waitingTimer !== null;
	}

	public shouldGoHere() { 
		// NOOP
	}
}
