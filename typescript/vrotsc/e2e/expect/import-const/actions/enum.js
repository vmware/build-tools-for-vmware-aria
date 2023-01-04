/**
 * @return {Any}
 */
(function () {
    var exports = {};
    var TestEnum = {};
    exports.TestEnum = TestEnum;
    var TestEnum = exports.TestEnum;
    (function (TestEnum) {
        TestEnum[TestEnum["VAL1"] = 0] = "VAL1";
        TestEnum[TestEnum["VAL2"] = 1] = "VAL2";
        TestEnum[TestEnum["VAL3"] = 2] = "VAL3";
    })(TestEnum || (TestEnum = {}));
    return exports;
});
