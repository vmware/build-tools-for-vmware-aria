/**
 * @return {Any}
 */
(function () {
  const __global = System.getContext() || (function () {
    return this
  }.call(null))
  const VROES = __global.__VROES || (__global.__VROES = System.getModule('com.vmware.pscoe.library.ecmascript').VROES()); const tslib_1 = VROES.tslib; const exports = {}
  const action2_1 = VROES.importLazy('com.vmware.pscoe.vrotsc.sub/action2')
  const TestClass2 = /** @class */ (function (_super) {
    tslib_1.__extends(TestClass2, _super)
    function TestClass2 (s) {
      const _this = _super.call(this, s) || this
      System.log('' + action2_1._.TestEnum.ALA)
      return _this
    }
    return TestClass2
  }(action2_1._.TestSubClass2))
  exports.TestClass2 = TestClass2
  return exports
})
