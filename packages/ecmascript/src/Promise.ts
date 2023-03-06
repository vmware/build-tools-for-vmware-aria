/*-
 * #%L
 * ecmascript
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
﻿function sentinelExecutor(fulfill: (value: any) => void, reject: (reason: Error) => void): void { }

function isPromise(value: any): boolean {
    return value instanceof Promise;
}

enum PromiseState {
    Pending,
    Fulfilled,
    Rejected,
}

interface Handler<T, TResult> {
    onFulfilled: FulfillmentHandler<T, TResult>;
    onRejected: RejectionHandler<TResult>;
    child: Promise<TResult>;
}

interface FulfillmentHandler<T, TResult> {
    (value: T): TResult | PromiseLike<TResult>;
}

interface RejectionHandler<TResult> {
    (reason: any): TResult | PromiseLike<TResult>;
}

export default class Promise<T> {
    private result: any;
    private state: PromiseState = PromiseState.Pending;
    private handlers: Handler<T, any>[] = [];

    constructor(executor?: (resolve: (value?: any | PromiseLike<any>) => void, reject: (reason?: any) => void) => void) {
        if (executor === sentinelExecutor) {
            return;
        }

        if (typeof executor !== "function") {
            throw new TypeError("Promise executor is not a function");
        }

        try {
            executor(
                (value: any) => this.fulfill(value),
                (reason: any) => this.reject(reason)
            );
        }
        catch (e) {
            this.reject(e);
        }
    }

    static await<T>(promise: Promise<T>, pullTimeInSeconds?: number): T {
        while (promise.state == PromiseState.Pending) {
            System.sleep((pullTimeInSeconds || 1) * 1000);
        }

        if (promise.state == PromiseState.Fulfilled) {
            return promise.result;
        }
        else {
            let error = promise.result;
            throw error instanceof Error ? error : new Error(`Promise was rejected with reason: ${error}`);
        }
    }

    static all<T>(values: (T | PromiseLike<T>)[]): Promise<T[]> {
        let promise = Promise.resolve<any>(),
            result: any[];

        if (values && values.length) {
            for (let value of values) {
                promise = promise.then((v: any) => {
                    if (result) {
                        result.push(v);
                    }
                    else {
                        result = [];
                    }

                    return value;
                });
            }
        }

        promise = promise.then((v: any) => {
            if (!result) {
                result = [];
            }

            result.push(v);

            return result;
        });

        return promise;
    }

    static resolve<T>(value?: T | PromiseLike<T>): Promise<T> {
        if (isPromise(value)) {
            let promise: PromiseLike<T> = <any>value;
            return new Promise<T>((resolve, reject) => {
                promise.then(
                    (value: any) => resolve(value),
                    (reason: any) => reject(reason)
                );
            });
        }
        else {
            return new Promise<T>((resolve, reject) => {
                resolve(value);
            });
        }
    }

    static reject<T>(reason: any): Promise<T> {
        return new Promise<T>((resolve, reject) => {
            reject(reason);
        });
    }

    always(callback?: ((value?: T) => T | PromiseLike<T>)): Promise<T> {
        return this.then(
            (value: T) => {
                let result: any = callback(value);
                if (result === undefined) {
                    result = value;
                }

                if (!isPromise(result)) {
                    result = Promise.resolve(result);
                }

                return result;
            },
            (reason: any) => {
                let result: any = callback(reason);
                if (result === undefined) {
                    result = reason;
                }

                if (!isPromise(result)) {
                    result = Promise.reject(result);
                }

                return result;
            });
    }

    then(onfulfilled?: ((value: T) => T | PromiseLike<T>) | undefined | null, onrejected?: ((reason: any) => T | PromiseLike<T>) | undefined | null): Promise<T> {
        let child = new Promise<any>(sentinelExecutor);
        this.enqueue(onfulfilled, onrejected, child);
        return child;
    }

    catch(onrejected?: ((reason: any) => any | PromiseLike<any>) | undefined | null): Promise<T> {
        return this.then(undefined, onrejected);
    }

    private fulfill(value: any) {
        if (this.state === PromiseState.Pending) {
            if (isPromise(value)) {
                let promise: Promise<any> = value;
                promise.then(
                    (value: any) => this.fulfill(value),
                    (reason: any) => this.reject(reason));
            }
            else {
                this.state = PromiseState.Fulfilled;
                this.result = value;
                this.flush();
            }
        }
    }

    private reject(reason: any) {
        if (this.state === PromiseState.Pending) {
            this.state = PromiseState.Rejected;
            this.result = reason;
            this.flush();
        }
    }

    private enqueue(onFulfilled: FulfillmentHandler<T, any>, onRejected: RejectionHandler<any>, child: Promise<any>): void {
        this.handlers.push({
            onFulfilled,
            onRejected,
            child
        });

        if (this.state !== PromiseState.Pending) {
            this.flush();
        }
    }

    private flush(): void {
        if (this.handlers.length) {
            while (this.handlers.length) {
                let handler = this.handlers.shift();
                let result = this.result;
                let failed = this.state === PromiseState.Rejected;
                let callback = this.state === PromiseState.Fulfilled ? handler.onFulfilled : handler.onRejected;

                if (callback) {
                    try {
                        result = callback(this.result);
                        failed = false;
                    }
                    catch (e) {
                        result = e;
                        failed = true;
                    }
                }

                if (isPromise(result)) {
                    let promise: Promise<any> = result;
                    promise.then(
                        (value: any) => handler.child.fulfill(value),
                        (reason: any) => handler.child.reject(reason));
                }
                else if (failed) {
                    handler.child.reject(result);
                }
                else {
                    handler.child.fulfill(result);
                }
            }
        }
    }
}
