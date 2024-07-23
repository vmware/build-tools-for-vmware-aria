import { Workflow, Out, In, Item, RootItem, AsyncWorkflowItem } from "vrotsc-annotations";

@Workflow({
	name: "Async Workflow Test",
	path: "VMware/PSCoE",
	description: "Calling another workflow asynchronously and binding values correctly",
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
		wfToken: {
			type: "WorkflowToken"
		}
	}
})
export class HandleNetworkConfigurationBackup {
	@AsyncWorkflowItem({
		target: "printAsync",
		linkedItem: "9e4503db-cbaa-435a-9fad-144409c08df0"
	})
	public asyncCall(@In first: number, @In second: number, @Out wfToken: WorkflowToken) { }

	@Item({ target: "callAsyncWf" })
	public prepareItems(@In @Out first: number, @In @Out second: number) {
		first = 1;
		second = 2;
	}

	@Item({ target: "end" })
	public printAsync(@In wfToken: WorkflowToken) {
		System.log(`Workflow token: ${wfToken.id} and state: ${wfToken.state}`);
		System.log("Workflow finished");
	}

	@Item({ target: "prepareItems", exception: "" })
	@RootItem()
	public start() {
		System.log("Starting workflow");
	}
}
