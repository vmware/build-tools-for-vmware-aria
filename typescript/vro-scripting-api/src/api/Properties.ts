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
    export interface Properties {
        readonly keys: readonly string[];
        get(key: string): any;
        put(key: string, value: any): void;
        remove(key: string): void;
        load(input: Record<string, any>): void;
    }

    /**
     * vRO Properties intrinsic class representation
     * Properties implemention in vRO supports operators overloading, a feature not officially supported in ECMAScript.
     * The implementation will proxy all get accessors to the underlying data except ones with a key matching the API.
     * We cannot support statements such as <code>props["put"] = value</code> since the "put" key is part of the API.
     */
    export function Properties(values?: Record<string, any>) {
        const data = { ...values };
        const api = {
            get: (key: string) => data.hasOwnProperty( key ) ? data[key] : null,
            put: (key: string, value: any) => {
                data[key] = value;
            },
            remove: (key: string) => {
                delete data[key];
            },
            load: (input: Record<string, any>) => {
                Object.entries(input || {}).forEach(([key, value]) => {
                    data[key] = value;
                });
            }
        } as Properties;
        Object.defineProperty(api, "keys", {
            get: () => Object.keys(data),
            enumerable: true,
            configurable: true
        });
        // Returning different instance from a constructor function is officially supported in ECMAScript - https://262.ecma-international.org/5.1/#sec-13.2.2
        return new Proxy(data, {
            get: (_, key: string) => api[key] || data[key],
        });
    }

    global.Properties = Properties as any;
}
