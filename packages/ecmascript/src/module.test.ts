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

		[
			null, undefined, "", " ", "/start/separator", "end.separator.", "dupl..separator", "repative/in/../middle", "dis@llowedCharacters?!"
		].forEach(invalidPath =>
			it(`import from invalid path (${invalidPath})`, () => {
				expect(ESModule.import("default").from(invalidPath)).toBeNull();
				expect(loggedSystemErrors).toEqual([
					`Cannot import from module with path '${invalidPath}'! Error: Path is invalid!`
				]);
			})
		);
		[
			null, undefined, "", " ", "non/dot/separators", "duplicate..separators", ".start.separator", "end.separator.", "dis@llowedCharacters?!"
		].forEach(invalidBasePath =>
			it(`import with relative path with invalid base path '${invalidBasePath}'`, () => {
				const path = "./Class";
				expect(ESModule.import("default").from(path, invalidBasePath)).toBeNull();
				expect(loggedSystemErrors).toEqual([
					`Cannot import from module with relative path '${path}'! Error: Base path is invalid: '${invalidBasePath}'!`
				]);
			})
		);
		it("import with relative path invalid for given base path", () => {
			const basePath = "com.vmware.pscoe.library.class.Class";
			const invalidRelativePath = basePath.split(".").map(s => "../").join("") + "Class"; // too many steps back
			expect(ESModule.import("default").from(invalidRelativePath, basePath)).toBeNull();
			expect(loggedSystemErrors).toEqual([
				`Cannot import from module with relative path '${invalidRelativePath}'! Error: Too many steps back for base path '${basePath}'!`
			]);
		});
		it("import with invalid module path", () => {
			const wrongPath = "com.wrong.path.library.class";
			expect(ESModule.import("Class").from(wrongPath)).toBeNull();
			expect(loggedSystemErrors).toEqual([
				`Cannot import from module with path '${wrongPath}'! Error: No action or module found for paths: '${wrongPath}', '${wrongPath}/index'!`
			]);
		});
		it("import without valid specifiers", () => {
			const path = "com.vmware.pscoe.library.class";
			expect(ESModule.import(null, undefined, "", "whatever", "Class", "default", "*", "Class").from(path)).toBeNull();
			expect(loggedSystemErrors).toEqual([
				`Cannot import from module with path '${path}'! Error: ` +
				"Some of the specified elements for import are invalid:\n" +
				"[0]: 'null'\n" +
				"[1]: 'undefined'\n" +
				"[2]: ''\n" +
				"[3]: 'whatever' (module contains no such action or namespace)\n" +
				"[7]: 'Class' (duplicate)"
			]);
		});
	});

	describe("constructAbsolutePath", () => {
		const testedFn: (path: string, base: string) => string = ESModule.constructAbsolutePath;
		const invalidPaths = [
			"not#working!", // disallowed special characters
			"not/working/", // separator at end
			".not.working", // separator at start
			"not//working",
			"still..not\\working", // adjacent separators within
			"./../not.working", // combining same folder and parent folder relative path indicators
			"not/./working",
			"not/../working", // relative path indicators within
		]
		invalidPaths.forEach(invalidPath => it(`should throw on invalid path '${invalidPath}'`, () => {
			expect(() => testedFn(invalidPath, "valid.base.path")).toThrowError(`Path is invalid!`);
		}));
		const invalidBasePaths = [
			"not#working!", // disallowed special characters
			"does/not\\match", // separators other than '.'
			"not.working.", // separator at end
			".not.working", // separator at start
			"not..working", // duplicate separator
			"./../not.working",
			"not/../working",
			"not/./working", // any relative path indicators
		]
		invalidBasePaths.forEach(invalidBasePath => it(`should throw on invalid base path '${invalidBasePath}'`, () => {
			expect(() => testedFn("valid.path", invalidBasePath)).toThrowError(`Base path is invalid: '${invalidBasePath}'!`);
		}));
		const validPathsWithoutBase: [path: string, expected: string][] = [
			["some/path", "some.path"],
			["some\\other\\path", "some.other.path"],
			["and\\another/path", "and.another.path"],
		];
		validPathsWithoutBase.forEach(path => it(`should return the cleaned path '${path[1]}' when base is not provided`, () => {
			[null, undefined, ""].forEach(missingBasePath => expect(testedFn(path[0], missingBasePath)).toEqual(path[1]));
		}));
		const validPathsWithBase: [path: string, base: string, expected: string][] = [
			["my/package\\name", "my.base.path", "my.base.path.my.package.name"],
			["./my.package\\name", "my.base.path", "my.base.path.my.package.name"],
			["../my.package\\name", "my.base.path", "my.base.my.package.name"],
			["../../my.package\\name", "my.base.path", "my.my.package.name"],
		];
		validPathsWithBase.forEach(path => it(`should throw on too many steps back in relative path for '${path[1]}'`, () => {
			expect(() => testedFn("../../../sth", path[1])).toThrowError(`Too many steps back for base path '${path[1]}'!`);
		}));
		validPathsWithBase.forEach(path => it(`should construct package name form relative path '${path[0]}' and base '${path[1]}'`, () => {
			expect(testedFn(path[0], path[1])).toEqual(path[2]);
		}));
	})

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
