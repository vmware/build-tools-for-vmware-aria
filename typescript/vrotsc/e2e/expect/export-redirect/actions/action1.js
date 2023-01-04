/**
 * @return {Any}
 */
(function () {
    var __global = System.getContext() || (function () {
        return this;
    }).call(null);
    var VROES = __global.__VROES || (__global.__VROES = System.getModule("com.vmware.pscoe.library.ecmascript").VROES()), require = VROES.require, tslib_1 = VROES.tslib, exports = {};
    tslib_1.__exportStar(require("com.vmware.pscoe.vrotsc.sub/subaction1"), exports);
    tslib_1.__exportStar(require("com.vmware.pscoe.vrotsc.sub/subaction2"), exports);
    return exports;
});
