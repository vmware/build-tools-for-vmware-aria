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
     * vRO Command intrinsic class representation
     */
    export class Command {

        output: string;

        result: number;

        input: string;

        /**
         * Executes the command. <p>Standard output is redirected to the <b>output</b> attribute.</p>
         * @param wait
         */
        execute(wait?: any): number {
            throw new NotSupportedError();
        }

        /**
         * Executes the command and logs the standard output to a file.
         * @param filename
         */
        executeAndLog(filename: string): number {
            throw new NotSupportedError();
        }

    }

    global.Command = Command;
}
