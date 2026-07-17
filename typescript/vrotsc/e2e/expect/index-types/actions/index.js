/**
 * @return {Any}
 */
(function () {
    var __global = System.getContext() || (function () {
        return this;
    }).call(null);
    var VROES = __global.__VROES || (__global.__VROES = System.getModule("com.vmware.pscoe.library.ecmascript").VROES()), exports = {};
    var _$action1_1 = VROES.importLazy("com.vmware.pscoe.vrotsc.action1");
    var _$action2_1 = VROES.importLazy("com.vmware.pscoe.vrotsc.sub/action2");
    exports.Class1 = _$action1_1._.Class1;
    exports.Class2 = _$action2_1._.Class2;
    return exports;
});
