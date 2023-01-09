/**
 * @return {Any}
 */
(function () {
    var __global = System.getContext() || (function () {
        return this;
    }).call(null);
    var VROES = __global.__VROES || (__global.__VROES = System.getModule("com.vmware.pscoe.library.ecmascript").VROES()), exports = {};
    var helper_1 = VROES.importLazy("com.vmware.pscoe.vrotsc.actions/test.helper");
    exports.default = function () {
        return helper_1._.default();
    };
    return exports;
});
