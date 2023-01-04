namespace vroapi {
    /**
     * vRO WorkflowItemWaitingEvent intrinsic class representation
     */
    export class WorkflowItemWaitingEvent {

        name: string;

        description: string;

        nextItem: any;

    }

    global.WorkflowItemWaitingEvent = WorkflowItemWaitingEvent as any;
}
