import { Workflow, In } from "vrotsc-annotations";

@Workflow({
	name: "Example Waiting Timer",
	path: "VMware/PSCoE",
	description: "default is first item, points to end"
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
	}

	public shouldNotGoHere() { }
}
