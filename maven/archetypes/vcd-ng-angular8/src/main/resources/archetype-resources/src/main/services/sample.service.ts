import { Injectable, Inject } from "@angular/core";
import { VcdApiClient } from "@vcd/sdk";
import { SESSION_ORGANIZATION } from "@vcd/sdk/common";
import { SampleMember } from "../model/sample-member";

@Injectable()
export class SampleService {
    constructor(
        private client: VcdApiClient,
        @Inject(SESSION_ORGANIZATION) private orgId: string) {
    }

    getSampleMembers(): SampleMember[] {
		return [new SampleMember("Peter", "Jackson", "Sofia, Bulgaria"), 
			new SampleMember("John", "Doe", "Istanbul, Turkey"), 
			new SampleMember("Mark", "Spencer", "Amsterdam, Netherlands")];
    }
}
