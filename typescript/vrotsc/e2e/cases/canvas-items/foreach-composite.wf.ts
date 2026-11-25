import { ForeachItem, RootItem, Workflow, In, Out } from "vrotsc-annotations";

@Workflow({
    name: "Foreach Composite Types",
    path: "VMware/PSCoE",
    description: "Foreach test with composite type bindings where sub-workflow parameter names differ from workflow variable names",
    input: {
        globalUserList: {
            type: "Array/CompositeType(name:string,age:number):UserData",
            description: "Array of composite structures passed to the loop",
            bindName: "inputComposite"
        }
    },
    output: {
        globalResultList: {
            type: "Array/CompositeType(status:string,id:string):ResultData",
            bindName: "outputComposite"
        }
    }
})
export class ForeachCompositeTypes {

    @ForeachItem({
        linkedItem: "a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d",
        target: "end"
    })
    @RootItem()
    public processingHandler(@In globalUserList: any, @Out globalResultList: any) {
        // Processing is handled by the linked sub-workflow for each element
    }
}
