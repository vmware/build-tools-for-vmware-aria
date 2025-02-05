import { Workflow, Out } from "vrotsc-annotations";

@Workflow({
    name: "Sample Workflow",
    path: "MyOrg/MyProject",
    id: "",
    description: "Sample workflow description",
    attributes: {
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
    public install(foo: string, bar: string, @Out result: any): void {
        System.log(`foo=${foo}, bar=${bar}`);
        result = "result value";
    }
}
