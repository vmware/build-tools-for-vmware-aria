namespace vroapi {
    /**
     * vRO Policy intrinsic class representation
     */
    export class Policy {

        name: string;

        description: string;

        taggedObjects: any[] = [];

        tags: string[] = [];

        owner: any;

        status: string;

        autostart: boolean | undefined;

        currentVersion: string;

        credential: any;

        logEvents: LogEvent[] = [];

        /**
         * Returns the object referenced by 'tag' in the policy (Virtual machine or Hosts)
         * @param value
         */
        getObjectByTag(value: string): any {
            throw new NotSupportedError();
        }

        /**
         * Returns the object referenced by 'tag' in the policy (Virtual machine or Hosts)
         * @param value
         */
        forTag(value: string): any {
            throw new NotSupportedError();
        }

        /**
         * Exits the policy immediatlly.
         * @param reason
         */
        exit(reason: string): void {
            throw new NotSupportedError();
        }

        /**
         * Returns all tags and their corresponding events.
         */
        getEventsByTag(): Properties[] {
            throw new NotSupportedError();
        }

        /**
         * Start the policy
         */
        start(): void {
            throw new NotSupportedError();
        }

        /**
         * Stop the policy
         * @param reason
         */
        stop(reason: string): void {
            throw new NotSupportedError();
        }

        /**
         * Remove the policy
         */
        remove(): void {
            throw new NotSupportedError();
        }

    }

    global.Policy = Policy as any;
}
