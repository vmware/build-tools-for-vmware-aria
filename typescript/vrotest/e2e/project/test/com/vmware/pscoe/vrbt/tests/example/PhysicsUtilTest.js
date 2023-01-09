describe("Physics utils", () => {
	it("Calculate energy from mass", () => {
		var PhysicsUtil = System.getModule("com.vmware.pscoe.vrbt.tests.example").PhysicsUtil();
        var massInKilos = 15;
		var energyInJoules = PhysicsUtil.getObjectEnergy(massInKilos);
		System.log("15 kilograms can be converted to " + energyInJoules + " joules");
		expect(energyInJoules).toBe(1348132768105226500);
	});
});
