/*-
 * #%L
 * vropkg
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
import { EventEmitter } from 'events';

/**
 * Wraps a non-EventEmitter resource in a manageable interface
 */
export class ResourceWrapper extends EventEmitter {
    private resource: any;
    private cleanup?: () => void;

    constructor(resource: any, cleanup?: () => void) {
        super();
        this.resource = resource;
        this.cleanup = cleanup;
    }

    /**
     * Get the wrapped resource
     */
    public getResource<T>(): T {
        return this.resource as T;
    }

    /**
     * Clean up the resource and emit close event
     */
    public close(): void {
        if (this.cleanup) {
            this.cleanup();
        }
        this.emit('close');
    }

    /**
     * Signal an error occurred with the resource
     */
    public error(error: Error): void {
        this.emit('error', error);
    }
}