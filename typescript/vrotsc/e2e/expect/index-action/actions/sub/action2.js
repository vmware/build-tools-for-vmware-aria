/**
 * @return {Any}
 */
(function () {
    var __global = System.getContext() || (function () {
        return this;
    }).call(null);
    var VROES = __global.__VROES || (__global.__VROES = System.getModule("com.vmware.pscoe.library.ecmascript").VROES()), _$tslib_1 = VROES.tslib, exports = {};
    var _$action1_1 = VROES.importLazy("com.vmware.pscoe.vrotsc.action1");
    var Class2 = /** @class */ (function (_super) {
        _$tslib_1.__extends(Class2, _super);
        function Class2() {
            return _super !== null && _super.apply(this, arguments) || this;
        }
        return Class2;
    }(_$action1_1._.Class1));
    exports.Class2 = Class2;
    return exports;
});
