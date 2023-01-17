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
     * Represents an action in the system
     */
    export class Action {

        name: string;

        /**
         * Returns the description of the Action.
         */
        description: string;

        version?: string;

        returnType?: string;

        script: string;

        versionHistoryItems?: VersionHistoryItem[] = [];

        parameters: Parameter[] = [];

        module: Module = new Module();
    }

    global.Action = Action;
}
