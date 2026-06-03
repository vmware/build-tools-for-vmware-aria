import { In, Item, Out, RootItem, UserInteractionItem, Workflow } from "vrotsc-annotations";

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
        target: "userInteraction3Enter"
    })
    public userInteraction2Enter(@In security_assignees: LdapUser[], @In security_assignee_groups: LdapGroup[], @In security_group: LdapGroup, @In timeout_date?: Date, @Out userInteractionAnswer?: string) {
        System.log(`User interaction component answered with '${userInteractionAnswer}'`);
    }

    @UserInteractionItem({
        target: "exit",
        exception: "exitOnException"
    })
    public userInteraction3Enter() {
        System.log(`Start user interaction 3`);
    }

    @Item({ target: "end" })
    public exit(@Out timeoutDate: Date) {
        System.log(`User Interaction exit at ${timeoutDate?.toUTCString()}`);
    }

    @Item({ target: "end" })
    public exitOnException(@Out timeoutDate: Date) {
        System.log(`User Interaction exit on exception at ${timeoutDate?.toUTCString()}`);
    }
}
