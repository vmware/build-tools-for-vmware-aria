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
describe("Module", () => {
    var ESModule = System.getModule("com.vmware.pscoe.library.ecmascript").Module();
    var exported;
    var modules = {
        "com.vmware.pscoe.library.class": {
            "Class": function () {
                return function Class() { };
            }
        },
        "com.vmware.pscoe": {
            "ExportClass": function () {
                return exported;
            }
        },
        "com.vmware.pscoe.test": {
            "TestClass": function () {
                return function TestClass() { };
            }
        }
    };
    let getModuleOriginal: any;
   
    beforeAll(() => {
        getModuleOriginal = System.getModule;
        System.getModule = function (moduleName: string) {
            let moduleInfo = modules[moduleName];
            if (moduleInfo && !moduleInfo.actionDescriptions) {
                moduleInfo.name = moduleName;
                moduleInfo.actionDescriptions = Object.keys(moduleInfo)
                    .map(actionName => <any>{
                        name: actionName,
                    });
            }

            return moduleInfo;
        }
    });

    afterAll(() => {
        System.getModule = getModuleOriginal
    });

    it("import class", () => {
        var Class = ESModule.import("default").from("com.vmware.pscoe.library.class.Class");
        expect(Class).toBeDefined();
    })
    it("export class", () => {
        var TestSubClass1 = function TestSubClass1(s) { };
        var TestSubClass2 = function TestSubClass2(s) { };
        var exp = ESModule.export().default(TestSubClass1).named("TestSubClass2", TestSubClass2).build();
        expect(exp.default).toBe(TestSubClass1);
        expect(exp["TestSubClass2"]).toBe(TestSubClass2);
    })
    it("import exported class", () => {
        var t1 = function TestSubClass1(s) { };
        var t2 = function TestSubClass2(s) { };
        exported = ESModule.export().default(t1).named("TestSubClass2", t2).build();
        var _a = ESModule.import("default", "TestSubClass2").from("com.vmware.pscoe.ExportClass"), TestSubClass1 = _a[0], TestSubClass2 = _a[1];
        expect(TestSubClass1).toBe(t1);
        expect(TestSubClass2).toBe(t2);
    })
    it("Compare imported classes", () => {
        const global = System.getContext() as Record<string, any>;
        expect(global).toBeDefined();
        expect((global.__classes__ || {})["com.vmware.pscoe.test/TestClass"]).not.toBeDefined();
        const TestClass = ESModule.import("default").from("com.vmware.pscoe.test.TestClass");
        expect(TestClass).toBeDefined();
        expect(global.__classes__).toBeDefined();
        expect(global.__classes__["com.vmware.pscoe.test/TestClass"]).toBeDefined();
        expect(global.__classes__["com.vmware.pscoe.test/TestClass"]).toBe(TestClass);
    })
})
