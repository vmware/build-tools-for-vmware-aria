namespace vroapi {
    /**
     * vRO WorkflowMultipleCallItem intrinsic class representation
     */
    export class WorkflowMultipleCallItem {

        name: string;

        description: string;

        nextItem: any;

        linkedWorkflows: any[] = [];

    }

    global.WorkflowMultipleCallItem = WorkflowMultipleCallItem as any;
}
