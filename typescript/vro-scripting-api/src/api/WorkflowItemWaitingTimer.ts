namespace vroapi {
    /**
     * vRO WorkflowItemWaitingTimer intrinsic class representation
     */
    export class WorkflowItemWaitingTimer {

        name: string;

        description: string;

        nextItem: any;

    }

    global.WorkflowItemWaitingTimer = WorkflowItemWaitingTimer as any;
}
