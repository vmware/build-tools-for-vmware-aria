namespace vroapi {
    /**
     * vRO Task intrinsic class representation
     */
    export class Task {

        state: string;

        operation: string;

        percentCompleted: number | undefined;

        error: string;

        executionDate: Date | undefined;

        parameters: Properties | undefined;

        restartInPast: boolean | undefined;

        isRecurrent: boolean | undefined;

        name: string;

        workflow: Workflow | undefined;

        executions: WorkflowToken[] = [];

        id: string;

        /**
         * Cancels the task.
         */
        cancel(): void {
            throw new NotSupportedError();
        }

        /**
         * Suspends the task.
         */
        suspend(): void {
            throw new NotSupportedError();
        }

        /**
         * Resumes the task.
         */
        resume(): void {
            throw new NotSupportedError();
        }

        /**
         * Sets the execution parameters.
         * @param key
         * @param value
         */
        addParameter(key: string, value: any): void {
            throw new NotSupportedError();
        }

    }

    global.Task = Task as any;
}
