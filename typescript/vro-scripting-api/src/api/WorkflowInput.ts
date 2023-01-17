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
     * vRO WorkflowInput intrinsic class representation
     */
    export class WorkflowInput {

        name: string;

        state: string;

        startDateAsString: string;

        startDate: any;

        isStillValid: boolean | undefined;

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

    }

    global.WorkflowInput = WorkflowInput as any;
}
