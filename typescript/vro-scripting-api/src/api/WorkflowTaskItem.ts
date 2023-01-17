namespace vroapi {
    /**
     * vRO WorkflowTaskItem intrinsic class representation
     */
    export class WorkflowTaskItem {

        name: string;

        description: string;

        nextItem: any;

        script: string;

        isActionCall: boolean | undefined;

        isStartWorkflowCall: boolean | undefined;

        linkedWorkflow: any;

        usedActions: any;

    }

    global.WorkflowTaskItem = WorkflowTaskItem as any;
}
