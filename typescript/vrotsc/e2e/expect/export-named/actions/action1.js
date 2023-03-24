/**
 * @return {Any}
 */
(function () {
  const exports = {}
  const TestClass1 = /** @class */ (function () {
    function TestClass1 () {
    }
    return TestClass1
  }())
  const TestClass2 = /** @class */ (function () {
    function TestClass2 () {
    }
    return TestClass2
  }())
  const TestVar = 10
  const TestConst = 10
  exports.TestClass1 = TestClass1
  exports.TestCls2 = TestClass2
  exports.TestVar = TestVar
  exports.TestConst = TestConst
  return exports
})
