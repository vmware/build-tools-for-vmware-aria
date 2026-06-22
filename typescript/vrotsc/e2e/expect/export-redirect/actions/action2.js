/**
 * @return {Any}
 */
(function () {
    var __global = System.getContext() || (function () {
        return this;
    }).call(null);
    var VROES = __global.__VROES || (__global.__VROES = System.getModule("com.vmware.pscoe.library.ecmascript").VROES()), require = VROES.require, exports = {};
    var _$subaction2_1 = require("com.vmware.pscoe.vrotsc.sub/subaction2");
    exports.TestClass4 = _$subaction2_1.TestClass4;
    var _$subaction1_1 = require("com.vmware.pscoe.vrotsc.sub/subaction1");
    exports.default = _$subaction1_1.default;
    var _$subaction2_2 = require("com.vmware.pscoe.vrotsc.sub/subaction2");
    exports.TestCls3 = _$subaction2_2.default;
    return exports;
});
