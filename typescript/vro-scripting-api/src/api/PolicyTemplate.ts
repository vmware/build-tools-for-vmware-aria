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
