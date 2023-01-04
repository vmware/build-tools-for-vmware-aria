/**
 * @returns {Any}
 */
(function () {
	function PhysicsUtil() { }
	PhysicsUtil.getObjectEnergy = function (mass) {
		const Calculator = System.getModule("com.vmware.pscoe.vrbt.tests.calculator").Calculator()
		const Constants = System.getModule("com.vmware.pscoe.vrbt.tests.calculator").Constants()
		const lightSpeedSquare = Calculator.multiply(Constants.LightSpeed, Constants.LightSpeed);
		return Calculator.multiply(mass, lightSpeedSquare);
	};
	return PhysicsUtil;
});
