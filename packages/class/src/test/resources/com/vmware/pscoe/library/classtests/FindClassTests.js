describe("FindClassTests", function () {
	var Class = System.getModule("com.vmware.pscoe.library.class").Class();

	it("Loads all classes which module and class name match a pattern", function () {
		var classes = Class.find(/\.pkg\d$/, /^Act\d$/).map(function (clazz) {
			return new clazz().name
		});

		expect(classes.length).toBe(4);
		expect(classes.indexOf("Act1")).toBeGreaterThan(-1);
		expect(classes.indexOf("Act2")).toBeGreaterThan(-1);
		expect(classes.indexOf("Act3")).toBeGreaterThan(-1);
		expect(classes.indexOf("Act4")).toBeGreaterThan(-1);
	});

	it("Can load classes from single module", function () {
		var classes = Class.find("com.vmware.pscoe.library.classtests.mocks.find.pkg1")
			.map(function (clazz) {
				return new clazz().name
			});

		expect(classes).toEqual(["Act1", "Act2"]);
	});

	it("Can load classes from multiple modules, following the specified order", function () {
		var classes = Class.find([
			"com.vmware.pscoe.library.classtests.mocks.find.pkg1",
			"com.vmware.pscoe.library.classtests.mocks.find.pkg2"
		]).map(function (clazz) {
			return new clazz().name
		});

		expect(classes).toEqual(["Act1", "Act2", "Act3", "Act4"]);

		classes = Class.find([
			"com.vmware.pscoe.library.classtests.mocks.find.pkg2",
			"com.vmware.pscoe.library.classtests.mocks.find.pkg1"
		]).map(function (clazz) {
			return new clazz().name
		});

		expect(classes).toEqual(["Act3", "Act4", "Act1", "Act2"]);
	});

	it("Should return empty array if no modules or classes match", function () {
		expect(Class.find("com.vmware.pscoe.library.classtests.mocks.notfound")).toEqual([]);
		expect(Class.find(["com.vmware.pscoe.library.classtests.mocks.notfound"])).toEqual([]);
		expect(Class.find(/\.notfound$/)).toEqual([]);
		expect(Class.find(/\.pkg\d$/, /.*Act100$/)).toEqual([]);
	});
});
