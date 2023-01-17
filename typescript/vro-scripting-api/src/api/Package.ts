namespace vroapi {
    /**
     * vRO Package intrinsic class representation
     */
    export class Package {

        name: string;

        description: string;

        policyTemplates: PolicyTemplate[] = [];

        actions: Action[] = [];

        workflows: Workflow[] = [];

        resourceElements: ResourceElement[] = [];

        configurationElements: ConfigurationElement[] = [];

        version: string;

        id: string;

        /**
         * Remove the package
         */
        remove(): void {
            throw new NotSupportedError();
        }

    }

    global.Package = Package as any;
}
