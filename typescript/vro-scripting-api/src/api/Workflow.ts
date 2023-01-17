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
     * vRO Workflow intrinsic class representation
     */
    export class Workflow {

        name: string;

        description: string;

        workflowCategory: any;

        version: string;

        firstItem: any = "";

        numberOfItem: number | undefined;

        items: WorkflowItem[] = [];

        inParameters: Parameter[] = [];

        outParameters: Parameter[] = [];

        attributes: Attribute[] = [];

        versionHistoryItems: VersionHistoryItem[] = [];

        parameterInfos: Properties | undefined;

        executions: WorkflowToken[] = [];

        logEvents: LogEvent[] = [];

        /**
         * Save the picture of the workflow in .png format.
         * @param file
         */
        saveSchemaImageToFile(file: string): void {
            throw new NotSupportedError();
        }

        /**
         * Executes the workflow.
         * @param properties
         * @param uname
         * @param pwd
         */
        execute(properties: any, uname?: any, pwd?: string): WorkflowToken {
            throw new NotSupportedError();
        }

        /**
         * Schedules recurrently the workflow.
         * @param properties
         * @param recurrencePattern
         * @param recurrenceCycle
         * @param startDate
         * @param endDate
         * @param uname
         * @param pwd
         */
        scheduleRecurrently(properties: any, recurrencePattern: any, recurrenceCycle: any, startDate: any, endDate: any, uname: any, pwd: string): Task {
            throw new NotSupportedError();
        }

        /**
         * Schedules the workflow.
         * @param properties
         * @param startDate
         * @param uname
         * @param pwd
         */
        schedule(properties: any, startDate: any, uname: any, pwd: string): Task {
            throw new NotSupportedError();
        }

        /**
         * Generates a URL usable for interacting with workflows in VS-O or its web front end version 3.0.x.
         * @param secure
         * @param inAutologon
         * @param inHost
         * @param inPort
         * @param inUrl
         */
        getExecutionUrl(secure: any, inAutologon: any, inHost: any, inPort: any, inUrl: any): any {
            throw new NotSupportedError();
        }

        /**
         * Generates a URL usable for interacting with workflows in VS-O or its web front end.
         * @param inWebviewUrlFolder
         * @param inPage
         * @param secure
         * @param inAutologon
         * @param inHost
         * @param inPort
         * @param inUrl
         */
        getWebviewExecutionUrl(inWebviewUrlFolder: any, inPage: any, secure: any, inAutologon: any, inHost: any, inPort: any, inUrl: any): any {
            throw new NotSupportedError();
        }

        /**
         * Creates a URL usable to access Web-Operator "Schedule Workflow operation" page (version 3.0.x).
         * @param secure
         * @param inAutologon
         * @param inHost
         * @param inPort
         * @param inUrl
         */
        getScheduleUrl(secure: any, inAutologon: any, inHost: any, inPort: any, inUrl: any): any {
            throw new NotSupportedError();
        }

        /**
         * Creates a URL usable to access Web-Operator "Schedule Workflow operation" page.
         * @param inWebviewUrlFolder
         * @param inPage
         * @param secure
         * @param inAutologon
         * @param inHost
         * @param inPort
         * @param inUrl
         */
        getWebviewScheduleUrl(inWebviewUrlFolder: any, inPage: any, secure: any, inAutologon: any, inHost: any, inPort: any, inUrl: any): any {
            throw new NotSupportedError();
        }

    }

    global.Workflow = Workflow as any;
}
