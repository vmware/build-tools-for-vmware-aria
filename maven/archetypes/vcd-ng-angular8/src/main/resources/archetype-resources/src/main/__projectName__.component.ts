import { Component } from "@angular/core";
import { SampleService } from "./services/sample.service";
import { SampleMember } from "./model/sample-member";

@Component({
    selector: "${projectName}",
    templateUrl: "./${projectName}.component.html"
})
export class ${projectHeading}Component {
    members: SampleMember[];
    selectedMembers: SampleMember[] = [];
    membersLoading: boolean = false;

    constructor(private sampleService: SampleService) {}

    loadMembers() {
        this.membersLoading = true;
        this.members = this.sampleService.getSampleMembers();
        this.membersLoading = false;
    }

    openAddMembersForm() {

    }

    openRemoveMembersForm() {

    }

    ngOnInit() {
        this.loadMembers();
    }

    ngOnChanges() {
        this.loadMembers();
    }
}
