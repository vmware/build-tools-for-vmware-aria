namespace vroapi {
    /**
     * vRO WorkflowItem intrinsic class representation
     */
    export class WorkflowItem {

        name: string;

        description: string;

        nextItem: any;

    }

    global.WorkflowItem = WorkflowItem as any;
}
