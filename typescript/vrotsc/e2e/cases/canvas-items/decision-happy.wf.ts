import { Workflow, DecisionItem } from "vrotsc-annotations";

@Workflow({
	name: "Decision Happy",
	path: "VMware/PSCoE",
	description: "decisionElement is the root, it will point with else to end and target to shouldGoHere. shouldGoHere will point to end too",
	attributes: {
		waitingTimer: {
			type: "Date"
		},
	}
})
export class HandleNetworkConfigurationBackup {
	@DecisionItem({
		target: "shouldGoHere",
		else: "end"
	})
	public decisionElement(waitingTimer: Date) {
		return waitingTimer !== null;
	}

	// This will point to end too, but no incoming
	public shouldGoHere() { }
}
