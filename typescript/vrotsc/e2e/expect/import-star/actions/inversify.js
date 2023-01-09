/**
 * @return {Any}
 */
(function () {
    var __global = System.getContext() || (function () {
        return this;
    }).call(null);
    var VROES = __global.__VROES || (__global.__VROES = System.getModule("com.vmware.pscoe.library.ecmascript").VROES()), exports = {};
    var metadata_keys_1 = VROES.importLazy("com.vmware.pscoe.vrotsc.constants/metadata_keys");
    exports.METADATA_KEY = metadata_keys_1._;
    var METADATA_KEY = exports.METADATA_KEY;
    return exports;
});
