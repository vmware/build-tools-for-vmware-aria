namespace vroapi {
    /**
     * vRO PolicyTemplateCategory intrinsic class representation
     */
    export class PolicyTemplateCategory {

        name: string;

        description: string;

        path: string;

        policyTemplates: PolicyTemplate[] = [];

        subCategories: PolicyTemplateCategory[] = [];

        parent: PolicyTemplateCategory | undefined;

        allPolicyTemplates: PolicyTemplate[] = [];

    }

    global.PolicyTemplateCategory = PolicyTemplateCategory as any;
}
