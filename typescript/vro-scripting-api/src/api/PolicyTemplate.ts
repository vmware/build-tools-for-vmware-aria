namespace vroapi {
    /**
     * vRO PolicyTemplate intrinsic class representation
     */
    export class PolicyTemplate {

        name: string;

        description: string;

        version: string;

        versionHistoryItems: VersionHistoryItem[] = [];

        currentVersion: string;

        policyTemplateCategory: any = "";

        id: string;

        /**
         * Apply the policy template and create a new policy with it.
         * @param name
         * @param properties
         */
        apply(name: string, properties: any): any {
            throw new NotSupportedError();
        }

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

    }

    global.PolicyTemplate = PolicyTemplate as any;
}
