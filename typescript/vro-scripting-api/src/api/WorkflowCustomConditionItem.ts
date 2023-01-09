namespace vroapi {
    /**
     * vRO WorkflowCustomConditionItem intrinsic class representation
     */
    export class WorkflowCustomConditionItem {

        name: string;

        description: string;

        nextItem: any;

        nextItemTrue: any;

        nextItemFalse: any;

        script: string;

    }

    global.WorkflowCustomConditionItem = WorkflowCustomConditionItem as any;
}
