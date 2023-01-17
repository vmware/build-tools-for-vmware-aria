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
     * vRO TagQuery intrinsic class representation
     */
    export class TagQuery {

        /**
         * Add new has tag conditions to query.
         * @param tag
         * @param value
         */
        hasTag(tag: string, value: string): void {
            throw new NotSupportedError();
        }

        /**
         * Add new has global tag conditions to query.
         * @param tag
         * @param value
         */
        hasGlobalTag(tag: string, value: string): void {
            throw new NotSupportedError();
        }

    }

    global.TagQuery = TagQuery as any;
}
