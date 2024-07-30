import { Workflow, Out, In, Item, RootItem, ScheduledWorkflowItem } from "vrotsc-annotations";

@Workflow({
	name: "Scheduled Workflow Test",
	path: "VMware/PSCoE",
	description: "Scheduling another workflow and binding values correctly",
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
		workflowScheduleDate: {
			type: "Date"
		},
		scheduledTask: {
			type: "Task"
		}
	}
})
export class HandleNetworkConfigurationBackup {
	@ScheduledWorkflowItem({
		target: "printScheduledDetails",
		linkedItem: "9e4503db-cbaa-435a-9fad-144409c08df0"
	})
	public scheduleOtherWf(@In first: number, @In second: number, @In workflowScheduleDate: Date, @Out scheduledTask: Task) {
	}

	@Item({ target: "scheduleOtherWf" })
	public prepareItems(@In @Out first: number, @In @Out second: number, @In @Out workflowScheduleDate: Date) {
		first = 1;
		second = 2;
		workflowScheduleDate = System.getDate("1 minute from now", undefined);
	}

	@Item({ target: "end" })
	public printScheduledDetails(@In scheduledTask: Task) {
		System.log(`Scheduled task: ${scheduledTask.id}, [${scheduledTask.state}]`);
	}


	@Item({ target: "prepareItems", exception: "" })
	@RootItem()
	public start() {
		System.log("Starting workflow");
	}
}
