import { In, Workflow } from "vrotsc-annotations";

@Workflow({
    name: "Test Workflow 7",
    path: "PS CoE/Test Workflows",
    description: "Test attributes with default value of type Array",
    attributes: {
        testStringAtt: {
            type: "Array/string",
            value: "foo,bar"
        },
        testNumeric: {
            type: "Array/number",
            value: "1,2,345"
        },
        testBool: {
            type: "Array/boolean",
            value: "true,false,true"
        }
    }
})

export class TestWorkflow7 {
    public test1(@In testStringAtt: string[], @In testNumeric: number[], @In testBool: boolean[]) {
        System.log("Starting workflow");
        System.log(`String length: ${testStringAtt.length}`);
        let resultStr = "";
        testStringAtt.forEach(e => {
            System.log(`Type ${typeof e}`);
            System.log(`Value ${e}`);
            resultStr += e;
        });
        System.log(`Result: ${resultStr}`)

        System.log(`Num length: ${testNumeric.length}`);
        let resultNum = 0;
        testNumeric.forEach(e => {
            System.log(`Type ${typeof e}`);
            System.log(`Value ${e}`);
            resultNum += e;
        });
        System.log(`Result: ${resultNum}`)

        System.log(`Boolean length: ${testBool.length}`);
        testBool.forEach(e => {
            System.log(`Type ${typeof e}`);
            System.log(`Value ${e}`);
            if (e) {
                System.log(`Is true`)
            } else {
                System.log(`Is false`)
            }
        });
    }
}
