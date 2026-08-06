/**
 * @return {Any}
 */
(function () {
    var __global = System.getContext() || (function () {
        return this;
    }).call(null);
    var VROES = __global.__VROES || (__global.__VROES = System.getModule("com.vmware.pscoe.library.ecmascript").VROES()), exports = {};
    var _$sub_1 = VROES.importLazy("com.vmware.pscoe.vrotsc.sub");
    new _$sub_1._.Test1();
    new _$sub_1._.sub2.Test2();
    return exports;
});
