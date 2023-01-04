namespace vroapi {
    /**
     * vRO WorkflowCategory intrinsic class representation
     */
    export class WorkflowCategory {

        name: string;

        description: string;

        path: string;

        workflows: Workflow[] = [];

        allWorkflows: Workflow[] = [];

        subCategories: WorkflowCategory[] = [];

        parent: WorkflowCategory | undefined;

    }

    global.WorkflowCategory = WorkflowCategory as any;
}
