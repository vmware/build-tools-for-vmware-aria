describe("ExportShapeValidationTests", function() {
	const Class = System.getModule("com.vmware.pscoe.library.class").Class();

	it("Class.load() should accept constructor function exports", function() {
		const SingleExport = Class.load("com.vmware.pscoe.library.classtests.mocks", "singleExport");
		expect(SingleExport).toBeDefined();
		expect(typeof SingleExport).toBe("function");
	});

	it("Class.load() should accept object exports with constructor functions", function() {
		const MultiExport = Class.load("com.vmware.pscoe.library.classtests.mocks", "multiExport");
		expect(MultiExport).toBeDefined();
		expect(typeof MultiExport).toBe("object");
		expect(MultiExport.Multi1Cls).toBeDefined();
		expect(MultiExport.Multi2Cls).toBeDefined();
	});

	it("Class.validateExportShape() should pass for constructor function", function() {
		const TestClass = Class.define(function TestClass() {}, {});
		const report = Class.validateExportShape(TestClass, "test.module", "TestClass");
		
		expect(report).toBeDefined();
		expect(report.exportType).toBe("function");
		expect(report.isFunction).toBe(true);
		expect(report.isConstructor).toBe(true);
		expect(report.issues.length).toBe(0);
	});

	it("Class.validateExportShape() should detect object export shapes", function() {
		const objExport = { 
			BaseClass: Class.define(function BaseClass() {}, {}),
			ExtendedClass: Class.define(function ExtendedClass() {}, {})
		};
		const report = Class.validateExportShape(objExport, "com.test.module", "MultiClass");
		
		expect(report).toBeDefined();
		expect(report.exportType).toBe("object");
		expect(report.isFunction).toBe(false);
		expect(report.exportKeys).toContain("BaseClass");
		expect(report.exportKeys).toContain("ExtendedClass");
		expect(report.issues.length).toBeGreaterThan(0);
		expect(report.issues[0]).toContain("E_EXPORT_SHAPE_MISMATCH");
	});

	it("Class.validateExportShape() should report on invalid exports", function() {
		const report = Class.validateExportShape(null, "com.test.module", "NullClass");
		
		expect(report).toBeDefined();
		expect(report.isFunction).toBe(false);
		expect(report.issues.length).toBeGreaterThan(0);
		expect(report.issues[0]).toContain("E_EXPORT_SHAPE_INVALID");
	});

	it("Class.validateExportShape() should detect non-function/non-object exports", function() {
		const report = Class.validateExportShape("string value", "com.test.module", "StringClass");
		
		expect(report).toBeDefined();
		expect(report.exportType).toBe("string");
		expect(report.isFunction).toBe(false);
		expect(report.issues.length).toBeGreaterThan(0);
		expect(report.issues[0]).toContain("E_EXPORT_SHAPE_INVALID");
	});

	it("Class.load() should log and still return the value when export is not a constructor", function() {
		// Verify Class.load() emits a diagnostic log but remains backward-compatible for object exports.
		const originalGetModule = System.getModule;
		const errorMessages = [];
		const originalError = System.error;
		try {
			System.getModule = function(moduleName) {
				if (moduleName === "com.vmware.pscoe.library.classtests.badexport") {
					return {
						name: moduleName,
						actionDescriptions: [{ name: "BadAction" }],
						BadAction: function() {
							return {
								SomeClass: Class.define(function SomeClass() {}, {})
							};
						}
					};
				}
				return originalGetModule(moduleName);
			};
			System.error = function(msg) { errorMessages.push(msg); };

			// Must clear the class cache so Class.load() actually invokes the action
			if (global.__classes__) {
				delete global.__classes__["com.vmware.pscoe.library.classtests.badexport/BadAction"];
			}

			const loaded = Class.load("com.vmware.pscoe.library.classtests.badexport", "BadAction");

			// Still returns the value (backward-compatible)
			expect(loaded).toBeDefined();
			expect(typeof loaded).toBe("object");
			expect(loaded.SomeClass).toBeDefined();

			// Diagnostic log was emitted
			expect(errorMessages.length).toBeGreaterThan(0);
			expect(errorMessages[0]).toContain("W_EXPORT_SHAPE_MISMATCH");
			expect(errorMessages[0]).toContain("com.vmware.pscoe.library.classtests.badexport");
			expect(errorMessages[0]).toContain("BadAction");
		} finally {
			System.getModule = originalGetModule;
			System.error = originalError;
		}
	});

	it("validateExportShape() report should include metadata", function() {
		const TestClass = Class.define(function TestClass() {}, {});
		const report = Class.validateExportShape(TestClass, "com.example.module", "ExampleClass");
		
		expect(report.timestamp).toBeDefined();
		expect(report.module).toBe("com.example.module");
		expect(report.name).toBe("ExampleClass");
		expect(report.exportKeys).toBeDefined();
		expect(Array.isArray(report.exportKeys)).toBe(true);
		expect(report.issues).toBeDefined();
		expect(Array.isArray(report.issues)).toBe(true);
	});
});
