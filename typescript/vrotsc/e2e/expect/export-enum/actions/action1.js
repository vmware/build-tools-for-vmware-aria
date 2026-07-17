/**
 * @return {Any}
 */
(function () {
    var __global = System.getContext() || (function () {
        return this;
    }).call(null);
    var VROES = __global.__VROES || (__global.__VROES = System.getModule("com.vmware.pscoe.library.ecmascript").VROES()), _$tslib_1 = VROES.tslib, exports = {};
    var _$action2_1 = VROES.importLazy("com.vmware.pscoe.vrotsc.sub/action2");
    var TestClass2 = /** @class */ (function (_super) {
        _$tslib_1.__extends(TestClass2, _super);
        function TestClass2(s) {
            var _this = _super.call(this, s) || this;
            System.log("" + _$action2_1._.TestEnum.ALA);
            return _this;
        }
        return TestClass2;
    }(_$action2_1._.TestSubClass2));
    exports.TestClass2 = TestClass2;
    return exports;
});
