/**
 * @return {Any}
 */
(function () {
    var __global = System.getContext() || (function () {
        return this;
    }).call(null);
    var VROES = __global.__VROES || (__global.__VROES = System.getModule("com.vmware.pscoe.library.ecmascript").VROES()), tslib_1 = VROES.tslib, exports = {};
    var Promise = VROES.Promise;
    function asyncTest1() {
        return tslib_1.__awaiter(this, void 0, void 0, function () {
            var _a;
            return tslib_1.__generator(this, function (_b) {
                switch (_b.label) {
                    case 0:
                        _a = "test1.".concat;
                        return [4 /*yield*/, asyncTest2()];
                    case 1: return [2 /*return*/, _a.apply("test1.", [_b.sent()])];
                }
            });
        });
    }
    exports.asyncTest1 = asyncTest1;
    function asyncTest2() {
        return tslib_1.__awaiter(this, void 0, void 0, function () {
            var _a;
            return tslib_1.__generator(this, function (_b) {
                switch (_b.label) {
                    case 0:
                        _a = "test2.".concat;
                        return [4 /*yield*/, asyncTest3()];
                    case 1: return [2 /*return*/, _a.apply("test2.", [_b.sent()])];
                }
            });
        });
    }
    exports.asyncTest2 = asyncTest2;
    function asyncTest3() {
        return tslib_1.__awaiter(this, void 0, void 0, function () {
            return tslib_1.__generator(this, function (_a) {
                return [2 /*return*/, new Promise(function (resolve, reject) {
                        resolve("test3");
                    })];
            });
        });
    }
    exports.asyncTest3 = asyncTest3;
    function asyncThrowAnError() {
        return tslib_1.__awaiter(this, void 0, void 0, function () {
            return tslib_1.__generator(this, function (_a) {
                throw new Error("test error");
            });
        });
    }
    exports.asyncThrowAnError = asyncThrowAnError;
    function all() {
        return Promise.all([
            Promise.resolve("test1"),
            new Promise(function (resolve) {
                resolve("test2");
            }),
            Promise.resolve("test3")
        ]);
    }
    exports.all = all;
    return exports;
});
