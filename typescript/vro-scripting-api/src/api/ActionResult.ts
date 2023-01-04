namespace vroapi {
    /**
     * vRO ActionResult intrinsic class representation
     */
    export class ActionResult {
        /**
         * Returns the actual result returned by the asynchronous executed action. Blocks until the result is available.
         */
        getResult(): any {
            throw new NotSupportedError();
        }

        /**
         * Attempts to cancel the execution of the asynchronously executed action.
         * @param mayInterruptIfRunning
         */
        cancel(mayInterruptIfRunning: boolean): boolean {
            throw new NotSupportedError();
        }

        /**
         * Returns true if the asynchronous action has completed.
         */
        isDone(): boolean {
            throw new NotSupportedError();
        }

    }

    global.ActionResult = ActionResult;
}
