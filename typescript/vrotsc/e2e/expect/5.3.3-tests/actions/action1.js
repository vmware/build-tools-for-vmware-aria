/**
 * @return {Any}
 */
(function () {
    var exports = {};
    function tail(arg) {
        var _ = arg[0], result = arg.slice(1);
        return result;
    }
    exports.tail = tail;
    function foo(x) {
        // ...
    }
    exports.foo = foo;
    // Short-Circuiting Assignment Operators
    var a = undefined;
    var b = 3;
    a || (a = b);
    a && (a = b);
    a !== null && a !== void 0 ? a : (a = b);
    var as = { prop: "hello" };
    return exports;
});
