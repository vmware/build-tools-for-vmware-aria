/*
 * #%L
 * vro-scripting-api
 * %%
 * Copyright (C) 2023 VMware
 * %%
 * Build Tools for VMware Aria
 * Copyright 2023 VMware, Inc.
 * 
 * This product is licensed to you under the BSD-2 license (the "License"). You may not use this product except in compliance with the BSD-2 License.  
 * 
 * This product may include a number of subcomponents with separate copyright notices and license terms. Your use of these subcomponents is subject to the terms and conditions of the subcomponent's license, as noted in the LICENSE file.
 * #L%
 */
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
