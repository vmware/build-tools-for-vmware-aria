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

	describe("Errors on import.from()", () => {
		let systemErrorOriginal: any;
		const loggedSystemErrors = [];
		beforeAll(() => {
			systemErrorOriginal = System.error;
			System.error = function (error) {
				loggedSystemErrors.push(error);
			}
		});
		afterAll(() => {
			System.error = systemErrorOriginal;
		});
		beforeEach(() => loggedSystemErrors.length = 0);

		[null, undefined, ""].forEach(invalidPath => it(`import from invalid path (${invalidPath})`, () => {
			expect(ESModule.import("default").from(invalidPath)).toBeNull();
			expect(loggedSystemErrors).toEqual([
				`Failed to load action or module with path '${invalidPath}'! `,
				`Cannot import from module with path '${invalidPath}'!`
			]);
		}));
		it("import with relative path without base path", () => {
			const path = "./Class";
			expect(ESModule.import("default").from(path)).toBeNull();
			expect(loggedSystemErrors).toEqual([`Cannot resolve relative path '${path}' without a base path!`]);
		});
		it("import with relative path invalid for given base path", () => {
			const basePath = "com.vmware.pscoe.library.class.Class";
			const invalidRelativePath = basePath.split(".").map(s => "../").join("") + "Class"; // too many steps back
			expect(ESModule.import("default").from(invalidRelativePath, basePath)).toBeNull();
			expect(loggedSystemErrors).toEqual([`Relative path '${invalidRelativePath}' is not valid for base path '${basePath}'!`]);
		});
		it("import with invalid module path", () => {
			const wrongPath = "com.wrong.path.library.class";
			expect(ESModule.import("Class").from(wrongPath)).toBeNull();
			expect(loggedSystemErrors).toEqual([
				`Failed to load action or module with path '${wrongPath}'! No action or module found for paths: '${wrongPath}', '${wrongPath}/index'!`,
				`Cannot import from module with path '${wrongPath}'!`
			]);
		});
		it("import without valid specifiers", () => {
			const path = "com.vmware.pscoe.library.class";
			expect(ESModule.import(null, undefined, "", "whatever", "Class", "default", "*", "Class").from(path)).toBeNull();
			expect(loggedSystemErrors).toEqual([
				"Some of the specified elements for import are invalid:\n" +
				"[0]: 'null'\n" +
				"[1]: 'undefined'\n" +
				"[2]: ''\n" +
				"[3]: 'whatever' (module contains no such action or namespace)\n" +
				"[7]: 'Class' (duplicate)"
			]);
		});
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
