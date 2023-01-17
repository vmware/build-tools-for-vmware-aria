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
     * vRO WorkflowToken intrinsic class representation
     */
    export class WorkflowToken {

        name: string;

        rootWorkflow: any;

        currentWorkflow: any;

        state: string;

        exception: string;

        businessState: string;

        workflowInputId: string;

        startDate: string;

        endDate: string;

        startDateAsDate: any;

        endDateAsDate: any;

        logEvents: LogEvent[] = [];

        isStillValid: boolean | undefined;

        attributesStack: Attribute[] = [];

        id: string;

        /**
         * Save the picture of the workflowToken in .png format.
         * @param file
         */
        saveSchemaImageToFile(file: string): void {
            throw new NotSupportedError();
        }

        /**
         * Cancels the execution of this workflow.
         */
        cancel(): void {
            throw new NotSupportedError();
        }

        /**
         * Generates a URL usable for answering input in running workflows in VS-O web front end version 3.0.x.
         * @param secure
         * @param inAutologon
         * @param inHost
         * @param inPort
         * @param inUrl
         */
        getAnswerUrl(secure: any, inAutologon: any, inHost: any, inPort: any, inUrl: any): any {
            throw new NotSupportedError();
        }

        /**
         * Generates a URL usable for interacting with running workflows in VS-O web front end version 3.0.x.
         * @param secure
         * @param inHost
         * @param inPort
         */
        getInteractionUrl(secure: any, inHost: any, inPort: any): any {
            throw new NotSupportedError();
        }

        /**
         * Generates a URL usable for answering input in running workflows in VS-O web front end.
         * @param inWebviewUrlFolder
         * @param inPage
         * @param secure
         * @param inAutologon
         * @param inHost
         * @param inPort
         * @param inUrl
         */
        getWebviewAnswerUrl(inWebviewUrlFolder: any, inPage: any, secure: any, inAutologon: any, inHost: any, inPort: any, inUrl: any): any {
            throw new NotSupportedError();
        }

        /**
         * Gets the input parameters of the workflow token (if any).
         */
        getInputParameters(): Properties {
            throw new NotSupportedError();
        }

        /**
         * Gets the output parameters of the workflow token (if any).
         */
        getOutputParameters(): Properties {
            throw new NotSupportedError();
        }

        /**
         * Gets the attributes of the workflow token (if any).
         */
        getAttributes(): Properties {
            throw new NotSupportedError();
        }

        /**
         * Change the running credential for this workflow execution. The credential will be effective in the next workflow's exectution box.
         * @param credential
         */
        changeCredential(credential: any): void {
            throw new NotSupportedError();
        }

    }

    global.WorkflowToken = WorkflowToken as any;
}
