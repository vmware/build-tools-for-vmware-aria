/**
 * @return {Any}
 */
(function () {
    var __global = System.getContext() || (function () {
        return this;
    }).call(null);
    var VROES = __global.__VROES || (__global.__VROES = System.getModule("com.vmware.pscoe.library.ecmascript").VROES()), exports = {};
    var _$appender_1 = VROES.importLazy("com.vmware.pscoe.vrotsc.appender");
    exports.default = _$appender_1._.BaseAppender;
    return exports;
});
