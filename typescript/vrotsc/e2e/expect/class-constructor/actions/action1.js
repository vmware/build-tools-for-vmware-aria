/**
 * @return {Any}
 */
(function () {
  const __global = System.getContext() || (function () {
    return this
  }.call(null))
  const VROES = __global.__VROES || (__global.__VROES = System.getModule('com.vmware.pscoe.library.ecmascript').VROES()); const tslib_1 = VROES.tslib; const exports = {}
  const Class1 = /** @class */ (function () {
    function Class1 () {
      this.field1 = 'test'
      this.field2 = 5
      this.field3 = []
      this.field4 = {
        f: this.field1,
        f2: this.field2
      }
      this.field2++
      System.log('Class1.ctor')
    }
    Class1.staticField1 = 'test1'
    return Class1
  }())
  exports.Class1 = Class1
  const Class2 = /** @class */ (function (_super) {
    tslib_1.__extends(Class2, _super)
    function Class2 () {
      const _this = _super !== null && _super.apply(this, arguments) || this
      _this.field1 = 'test2'
      _this.field2 = 10
      _this.field5 = true
      return _this
    }
    Class2.staticField1 = 'test2'
    Class2.staticField2 = 10
    return Class2
  }(Class1))
  exports.Class2 = Class2
  const Class3 = /** @class */ (function (_super) {
    tslib_1.__extends(Class3, _super)
    function Class3 (field7, field8) {
      if (field7 === void 0) { field7 = 'test7' }
      if (field8 === void 0) { field8 = 'test8' }
      const _this = _super.call(this) || this
      _this.field7 = field7
      _this.field1 = 'test3'
      _this.field6 = 123
      System.log('Class3.ctor')
      return _this
    }
    return Class3
  }(Class2))
  exports.Class3 = Class3
  return exports
})
