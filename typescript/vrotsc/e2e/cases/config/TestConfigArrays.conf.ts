import { Configuration } from "vrotsc-annotations";

@Configuration({
    name: "Array-test",
    path: "config/array-test-ts",
    attributes: {
        stringArray: {
            type: "Array/string",
            value: ["world", "hello"],
            description: "describe me"
        },
        numArray: {
            type: "Array/number",
            value: [ 2, 9],
            description: "Numbers"
        }
    }
})

export class ArrayTest {
}
