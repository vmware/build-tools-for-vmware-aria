/**
 * @return {Any}
 */
(function () {
    var __global = System.getContext() || (function () {
        return this;
    }).call(null);
    var VROES = __global.__VROES || (__global.__VROES = System.getModule("com.vmware.pscoe.library.ecmascript").VROES()), require = VROES.require, exports = {};
    var subaction2_1 = require("com.vmware.pscoe.vrotsc.sub/subaction2");
    exports.TestClass4 = subaction2_1.TestClass4;
    var subaction1_1 = require("com.vmware.pscoe.vrotsc.sub/subaction1");
    exports.default = subaction1_1.default;
    var subaction2_2 = require("com.vmware.pscoe.vrotsc.sub/subaction2");
    exports.TestCls3 = subaction2_2.default;
    return exports;
});
