describe("LoadClassTests", function() {
	var Class = System.getModule("com.vmware.pscoe.library.class").Class();

	it("Load Singleton twice and expect to have value incremented", function() {
		var Singleton = Class.load("com.vmware.pscoe.library.classtests.mocks", "Singleton");
		expect(Singleton).toBeDefined();
		var inst = Singleton.getInstance();
		expect(inst.getNextValue()).toEqual(0);
		//reloading
		var Singleton = Class.load("com.vmware.pscoe.library.classtests.mocks", "Singleton");
		expect(Singleton).toBeDefined();
		var inst = Singleton.getInstance();
		expect(inst.getNextValue()).toEqual(1);
	});
});
