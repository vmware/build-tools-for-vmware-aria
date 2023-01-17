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
     * vRO AuthorizationElement intrinsic class representation
     */
    export class AuthorizationElement {

        name: string;

        description: string;

        ldapElementDn: string;

        ldapElement: LdapGroup | undefined;

        status: string;

        references: AuthorizationReference[] = [];

        /**
         * add an authorization reference on any inventory object and return it
         * @param object
         * @param relation
         */
        addReference(object: any, relation: any): AuthorizationReference {
            throw new NotSupportedError();
        }

        /**
         * remove all reference in this authorization element
         */
        removeAllReferences(): void {
            throw new NotSupportedError();
        }

        /**
         * remove all reference for a given object and relation.
         * @param jsObject
         * @param relation
         */
        removeReference(jsObject: any, relation: any): void {
            throw new NotSupportedError();
        }

    }

    global.AuthorizationElement = AuthorizationElement;
}
