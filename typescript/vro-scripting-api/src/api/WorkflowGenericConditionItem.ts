namespace vroapi {
    /**
     * vRO WorkflowGenericConditionItem intrinsic class representation
     */
    export class WorkflowGenericConditionItem {

        name: string;

        description: string;

        nextItem: any;

        nextItemTrue: any;

        nextItemFalse: any;

        script: string;

    }

    global.WorkflowGenericConditionItem = WorkflowGenericConditionItem as any;
}
