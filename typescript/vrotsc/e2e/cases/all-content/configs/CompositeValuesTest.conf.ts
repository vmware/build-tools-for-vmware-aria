import { Configuration } from "vrotsc-annotations";

@Configuration({
    name: "CompositeValuesTest",
    path: "MyOrg/MyProject",
    id: "b3345b2d-6a36-37c6-8410-7234c2e0a0f7",
    attributes: {
        host: {
            type: "CompositeType(field1:number,field2:boolean,field3:string,field4:Array/string):ITest",
            value: {
                field1: 1,
                field2: true,
                field3: '2222',
                field4: ['test',"test2",`test3`],
            },
            description: "A composite value with all the basics"
        }
    }
})
export class CompositeValuesTest {
}
