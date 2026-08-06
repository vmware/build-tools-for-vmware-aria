/**
 * @return {Any}
 */
(function () {
    var __global = System.getContext() || (function () {
        return this;
    }).call(null);
    var VROES = __global.__VROES || (__global.__VROES = System.getModule("com.vmware.pscoe.library.ecmascript").VROES()), exports = {};
    var _$logger_1 = VROES.importLazy("com.vmware.pscoe.vrotsc.logger");
    function testAction(param) {
        var logger = new _$logger_1._.Logger();
        logger.log("outer");
        if (param === "test") {
            var logger_1 = new _$logger_1._.Logger();
            logger_1.log("inner");
        }
    }
    exports.default = testAction;
    return exports;
});
