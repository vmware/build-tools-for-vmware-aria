import { Out, Workflow } from "vrotsc-annotations";

@Workflow({
	name: "Sample Workflow",
	path: "PSCoE/MyProject",
	id: "",
	description: "Sample workflow description",
	attributes: {
		field1: {
			type: string,
			bind: true,
			value: "PSCoE/MyProject/field1"
		}
	},
	input: {
		foo: {
			type: "string",
			availableValues: ["a", "b"],
			defaultValue: "а",
			description: "foo Value",
			required: true,
			title: "Foo"
		},
		bar: { type: "string" }
	},
	output: {
		result: { type: "Any" }
	},
	presentation: ""
})
export class SampleWorkflow {
	public install(foo: string, bar: string, field1: string, @Out result: any): void {
		System.log(`foo=${foo}, bar=${bar}, field1=${field1}`);
		result = "result value";
	}
}
