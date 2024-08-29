import { Workflow, In, Out, Item, RootItem, UserInteractionItem } from "vrotsc-annotations";

@Workflow({
	name: "User Interaction",
	path: "VMware/PSCoE",
	description: "Adding user interaction parameters"
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
	public userInteraction2Enter(@In security_assignees: LdapUser[], @In security_assignee_groups: LdapGroup[], @In security_group: LdapGroup, @In timeout_date?: Date, @In userInteractionAnswer?: string) {
		System.log(`User interaction component answered with '${userInteractionAnswer}'`);
	}

	@Item({ target: "end" })
	public userInteractionExit(@Out timeoutDate: Date) {
		System.log(`User Interaction exit on ${timeoutDate?.toUTCString()}`);
	}
}
