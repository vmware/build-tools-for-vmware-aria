/**
 * @return {Any}
 */
(function () {
    var exports = {};
    exports.default = (function () { return print(); });
    function print() {
        System.debug("Rolled back Second Saga");
        return "Rolled back Second Saga";
    }
    return exports;
});
