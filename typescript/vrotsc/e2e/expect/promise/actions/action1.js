/**
 * @return {Any}
 */
(function () {
    var __global = System.getContext() || (function () {
        return this;
    }).call(null);
    var VROES = __global.__VROES || (__global.__VROES = System.getModule("com.vmware.pscoe.library.ecmascript").VROES()), exports = {};
    var _$action2_1 = VROES.importLazy("com.vmware.pscoe.vrotsc.action2");
    _$action2_1._.asyncTest1()
        .then(function (value) { return console.log(value); })
        .then(function (_) { return _$action2_1._.asyncTest2(); })
        .then(function (value) { return console.log(value); })
        .then(function (_) { return _$action2_1._.asyncTest3(); })
        .then(function (value) { return console.log(value); })
        .then(function (_) { return _$action2_1._.asyncThrowAnError(); })
        .catch(function (value) { return console.log(value); })
        .then(function (_) { return _$action2_1._.all(); })
        .then(function (values) { return console.log(JSON.stringify(values)); });
    return exports;
});
