/**
 * @return {Any}
 */
(function () {
    var exports = {};
    exports.foo = 5;
    var foo = exports.foo;
    exports.bar = "test1";
    var bar = exports.bar;
    exports.x = 4;
    var x = exports.x;
    exports.y = 5;
    var y = exports.y;
    exports.z = "this is a test";
    var z = exports.z;
    return exports;
});
