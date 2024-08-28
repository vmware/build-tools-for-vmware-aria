import { Workflow, In, Out, Item, RootItem, UserInteractionItem, AuthorizedEntity } from "vrotsc-annotations";

@Workflow({
	name: "User Interaction",
	path: "VMware/PSCoE",
	description: "Adding user interaction parameters",
	attributes: {
		timeoutDate: {
			type: "Date"
		},
		first: {
			type: "number"
		}
	}
})
export class UserInteractionWorkflow {
	@Item({ target: "userInteraction1Enter", exception: "" })
	@RootItem()
	public start() {
		System.log("Starting workflow");
	}

	@UserInteractionItem({
		target: "userInteraction2Enter"
	})
	public userInteraction1Enter() {
		System.log(`Start user interaction 1`);
	}

	@UserInteractionItem({
		target: "userInteractionExit"
	})	
	public userInteraction2Enter(@In security_assignees: AuthorizedEntity[], @In security_assignee_groups: AuthorizedEntity[], @In security_group: AuthorizedEntity, @In timeout_date?: Date, @Out result?: string) {
		System.log(`Start user interaction 2`);
	}

	@Item({ target: "end" })
	public userInteractionExit(@Out timeoutDate: Date) {
		System.log(`User Interaction exit on ${timeoutDate?.toUTCString()}`);
	}
}
