namespace vroapi {
    /**
     * vRO WorkflowLinkItem intrinsic class representation
     */
    export class WorkflowLinkItem {

        name: string;

        description: string;

        nextItem: any;

        linkedWorkflow: any;

    }

    global.WorkflowLinkItem = WorkflowLinkItem as any;
}
