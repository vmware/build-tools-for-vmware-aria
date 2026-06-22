/**
 * @return {Any}
 */
(function () {
    var __global = System.getContext() || (function () {
        return this;
    }).call(null);
    var VROES = __global.__VROES || (__global.__VROES = System.getModule("com.vmware.pscoe.library.ecmascript").VROES()), exports = {};
    var _$action2_1 = VROES.importLazy("com.vmware.pscoe.vrotsc.action2");
    function test() {
        System.log("foo=".concat(_$action2_1._.foo, ", bar=").concat(_$action2_1._.bar));
    }
    exports.test = test;
    return exports;
});
