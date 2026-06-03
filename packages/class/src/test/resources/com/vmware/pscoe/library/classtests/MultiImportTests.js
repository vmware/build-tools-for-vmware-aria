describe("Class Define/Load for multi-import", function () {
	var Class = System.getModule("com.vmware.pscoe.library.class").Class();

	it("single import of singleExport", function () {
		var imported = Class.load("com.vmware.pscoe.library.classtests.mocks", "singleExport");

		expect(typeof imported.constructor).toBe("function");
		expect(imported.name).toBe("SingleExport");
		expect(imported.constructor.name).toBe("Function");

		var inst = new imported();
		expect(inst.constructor.name).toBe("SingleExport");
	});

	it("single import of multiExport", function () {
		var imported = Class.load("com.vmware.pscoe.library.classtests.mocks", "multiExport");

		expect(typeof imported).toBe("object");
		expect(imported.Multi1Cls.constructor.name).toBe("Function");
		expect(imported.Multi1Cls.name).toBe("Multi1Cls");
		expect(imported.Multi2Cls.constructor.name).toBe("Function");
		expect(imported.Multi2Cls.name).toBe("Multi2Cls");

		var multi1 = new imported.Multi1Cls();
		var multi2 = new imported.Multi2Cls();
		expect(multi1.constructor.name).toBe("Multi1Cls");
		expect(multi2.constructor.name).toBe("Multi2Cls");
	});

	it("class descriptor not propagated to instance", function () {
		var SingleExport = Class.load("com.vmware.pscoe.library.classtests.mocks", "singleExport");
		var classDescr = SingleExport.descriptor;

		expect(typeof classDescr).toBe("object");

		expect(classDescr.module).toBe("com.vmware.pscoe.library.classtests.mocks");
		expect(classDescr.action).toBe("singleExport");
		expect(classDescr.name).toBe("SingleExport");
		expect(classDescr.fullName).toBe("com.vmware.pscoe.library.classtests.mocks.singleExport/SingleExport");

		var inst = new SingleExport();
		expect(typeof inst.descriptor).toBe("undefined");
	});

});
