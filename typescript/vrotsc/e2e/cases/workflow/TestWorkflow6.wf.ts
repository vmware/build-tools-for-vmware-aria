import { Workflow } from "vrotsc-annotations";

@Workflow({
	name: "Test Workflow 6",
	path: "PS CoE/Test Workflows",
	restartMode: 0,
	resumeFromFailedMode: 1
})
export class TestWorkflow1 {
	test1(foo: string, bar: string): void {
		System.log(`foo=${foo}, bar=${bar}`);
	}
}
