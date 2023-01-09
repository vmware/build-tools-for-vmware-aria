/**
 * @return {Any}
 */
(function () {
    var exports = {};
    var TestClass1 = /** @class */ (function () {
        function TestClass1() {
        }
        return TestClass1;
    }());
    var TestClass2 = /** @class */ (function () {
        function TestClass2() {
        }
        return TestClass2;
    }());
    var TestVar = 10;
    var TestConst = 10;
    exports.TestClass1 = TestClass1;
    exports.TestCls2 = TestClass2;
    exports.TestVar = TestVar;
    exports.TestConst = TestConst;
    return exports;
});
