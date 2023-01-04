@Workflow({
    id: "c9be5b70-a650-43d0-a8a3-e39f731b055b",
    version: "1.0.0",
})
export class TestWorkflow2 {
    public install(foo: string, bar: string, @Out result: any): void {
        System.log(`foo=${foo}, bar=${bar}`);
        result = "test result2";
    }
}