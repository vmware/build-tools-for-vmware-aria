import { In, Item, RootItem, UserInteractionItem, Workflow } from "vrotsc-annotations";

@Workflow({
    name: "Test Workflow 8",
    path: "PS CoE/Test Workflows",
    description: "Test attributes with default value of type Array and User interaction binding for LdapUsers",
    attributes: {
        security_assignees: {
            type: "Array/LdapUser",
            value: "configurationadmin,System Domain\\admin"
        }
    }
})

export class TestWorkflow8 {
    @Item({ target: "userInteraction1Enter", exception: "" })
    @RootItem()
    public start(@In security_assignees: LdapUser[]) {
        System.log(`Users length: ${security_assignees.length}.`);
        System.log(`User 1: ${security_assignees[0].userPrincipalName}.`);
    }

    @UserInteractionItem({
        target: "end"
    })
    public userInteraction1Enter(@In security_assignees: LdapUser[]) { }
}
