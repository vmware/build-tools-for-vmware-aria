import { Configuration } from "vrotsc-annotations";

@Configuration({
    name: "Sample Config",
    path: "MyOrg/MyProject"
})
export class SampleConfig {
    field1: string;
    field2: number;
    field3: any;
}
