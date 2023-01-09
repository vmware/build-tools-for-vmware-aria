namespace vroapi {
    /**
     * vRO WorkflowInputItem intrinsic class representation
     */
    export class WorkflowInputItem {

        name: string;

        description: string;

        nextItem: any;

    }

    global.WorkflowInputItem = WorkflowInputItem as any;
}
