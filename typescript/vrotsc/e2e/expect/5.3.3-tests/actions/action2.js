/**
 * @return {Any}
 */
(function () {
    var __global = System.getContext() || (function () {
        return this;
    }).call(null);
    var VROES = __global.__VROES || (__global.__VROES = System.getModule("com.vmware.pscoe.library.ecmascript").VROES()), exports = {};
    var action1_1 = VROES.importLazy("com.vmware.pscoe.vrotsc.action1");
    action1_1._.tail([1, 2, 3, 4], [5, 6, 7, 8]);
    action1_1._.foo([1, 32]); // works error
    action1_1._.setAlignment("top-left"); // works!
    return exports;
});
