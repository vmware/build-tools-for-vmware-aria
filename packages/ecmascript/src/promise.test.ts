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
describe("Promise", () => {
    const VROES = System.getModule("com.vmware.pscoe.library.ecmascript").VROES();
    const Promise: PromiseConstructor = VROES.Promise;

    const error = new Error("Uh-oh!");

    it("executor function is called on initialization", () => {
        let executorSpy = jasmine.createSpy("executor");
        new Promise(executorSpy);
        expect(executorSpy).toHaveBeenCalled();
    });

    it("then function is called", () => {
        let thenSpy = jasmine.createSpy("then");
        let catchSpy = jasmine.createSpy("catch");
        new Promise((resolve) => resolve(10)).then(thenSpy).catch(catchSpy);
        expect(thenSpy).toHaveBeenCalledWith(10);
        expect(catchSpy).not.toHaveBeenCalled();
    });

    it("catch function is called", () => {
        let thenSpy = jasmine.createSpy("then");
        let catchSpy = jasmine.createSpy("catch");
        new Promise((resolve, reject) => reject("test")).then(thenSpy).catch(catchSpy);
        expect(thenSpy).not.toHaveBeenCalled();
        expect(catchSpy).toHaveBeenCalledWith("test");
    });

    it("catch function is called when thrown", () => {
        let thenSpy = jasmine.createSpy("then");
        let catchSpy = jasmine.createSpy("catch");
        new Promise(() => { throw error; }).then(thenSpy).catch(catchSpy);
        expect(thenSpy).not.toHaveBeenCalled();
        expect(catchSpy).toHaveBeenCalledWith(error);
    });

    it("throw error when catch function is not present", () => {
        let thenSpy = jasmine.createSpy("then");
        let promise = new Promise(() => { throw error; }).then(thenSpy);
        expect(() => (<any>Promise).await(promise)).toThrowError(error.message);
        expect(thenSpy).not.toHaveBeenCalled();
    });

    it("Promise.all resolves with an array of results ", () => {
        let thenSpy = jasmine.createSpy("then");
        let catchSpy = jasmine.createSpy("catch");
        Promise.all([Promise.resolve(1), Promise.resolve(2), Promise.resolve(3)]).then(thenSpy).catch(catchSpy);
        expect(thenSpy).toHaveBeenCalledWith([1, 2, 3]);
        expect(catchSpy).not.toHaveBeenCalled();
    });

    it("chain then", () => {
        let thenSpy = jasmine.createSpy("then");
        let catchSpy = jasmine.createSpy("catch");
        Promise.resolve(1)
            .then(x => x + 1)
            .then(x => x + 2)
            .then(x => x + 3)
            .then(thenSpy)
            .catch(catchSpy);
        expect(thenSpy).toHaveBeenCalledWith(7);
        expect(catchSpy).not.toHaveBeenCalled();
    });

    it("chain then throws an error", () => {
        let thenSpy = jasmine.createSpy("then");
        let catchSpy = jasmine.createSpy("catch");
        Promise.resolve(1)
            .then(x => x + 1)
            .then(x => { throw error; })
            .then(x => x + 3)
            .then(thenSpy)
            .catch(catchSpy);
        expect(thenSpy).not.toHaveBeenCalled();
        expect(catchSpy).toHaveBeenCalledWith(error);
    });
});
