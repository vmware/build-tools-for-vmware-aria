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
