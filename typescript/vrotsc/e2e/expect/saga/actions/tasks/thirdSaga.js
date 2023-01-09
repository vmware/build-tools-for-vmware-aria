/**
 * @return {Any}
 */
(function () {
    var exports = {};
    exports.default = (function () { return print(); });
    function print() {
        System.debug("Third saga");
        throw new Error("Error message");
    }
    return exports;
});
