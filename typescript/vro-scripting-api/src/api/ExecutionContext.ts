namespace vroapi {
    /**
     * vRO ExecutionContext intrinsic class representation
     */
    export class ExecutionContext {

        /**
         * @param {string} name
         */
        contains(name: string): boolean {
            throw new NotSupportedError();
        }

        /**
         * @param {string} name
         */
        getParameter(name: string): Object {
            throw new NotSupportedError();
        }

        /**
         * @param {string} name
         */
        parameterNames(name: string): any[] {
            throw new NotSupportedError();
        }

    }

    global.ExecutionContext = ExecutionContext;
}
