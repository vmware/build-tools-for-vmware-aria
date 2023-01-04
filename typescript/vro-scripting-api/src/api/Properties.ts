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
