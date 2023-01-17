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
     * vRO Credential intrinsic class representation
     */
    export class Credential {

        username: string;

        password: string;

        /**
         * @param otherPassword
         */
        checkPassword(otherPassword: string): boolean {
            throw new NotSupportedError();
        }
    }

    global.Credential = Credential as any;
}
