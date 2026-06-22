/**
 * @return {Any}
 */
(function () {
    var __global = System.getContext() || (function () {
        return this;
    }).call(null);
    var VROES = __global.__VROES || (__global.__VROES = System.getModule("com.vmware.pscoe.library.ecmascript").VROES()), _$tslib_1 = VROES.tslib, exports = {};
    var _$decorators_1 = VROES.importLazy("com.vmware.pscoe.vrotsc.decorators");
    /** Some leading class comment */
    var TestClass1 = /** @class */ (function () {
        /** Some leading constructor comment */
        function TestClass1(someVar) {
            /** in-constructor comment */
            console.log("some stuff");
        }
        /** Some leading instance method comment */
        TestClass1.prototype.method1 = function () {
            /** in-inst member comment */
            console.log("some stuff");
        };
        /** Some leading static method comment */
        TestClass1.method2 = function () {
            /** in-staic member comment */
            console.log("some stuff");
        };
        _$tslib_1.__decorate([
            _$decorators_1._.FieldDecorator(1),
            _$tslib_1.__metadata("design:type", String)
        ], TestClass1.prototype, "field1", void 0);
        _$tslib_1.__decorate([
            _$decorators_1._.MethodDecorator("bar"),
            _$tslib_1.__metadata("design:type", Function),
            _$tslib_1.__metadata("design:paramtypes", []),
            _$tslib_1.__metadata("design:returntype", void 0)
        ], TestClass1.prototype, "method1", null);
        _$tslib_1.__decorate([
            _$decorators_1._.StaticFieldDecorator,
            _$tslib_1.__metadata("design:type", Number)
        ], TestClass1, "field2", void 0);
        _$tslib_1.__decorate([
            _$decorators_1._.StaticMethodDecorator,
            _$tslib_1.__metadata("design:type", Function),
            _$tslib_1.__metadata("design:paramtypes", []),
            _$tslib_1.__metadata("design:returntype", void 0)
        ], TestClass1, "method2", null);
        TestClass1 = _$tslib_1.__decorate([
            _$decorators_1._.ClassDecorator,
            _$decorators_1._.ClassDecorator2("foo")
            /** More leading class comment */
            ,
            _$tslib_1.__param(0, _$decorators_1._.inject()),
            _$tslib_1.__metadata("design:paramtypes", [Object])
        ], TestClass1);
        return TestClass1;
    }());
    exports.TestClass1 = TestClass1;
    return exports;
});
