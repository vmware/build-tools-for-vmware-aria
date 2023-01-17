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
