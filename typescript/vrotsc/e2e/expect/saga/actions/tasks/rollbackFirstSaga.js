/**
 * @return {Any}
 */
(function () {
    var exports = {};
    exports.default = (function () { return print(); });
    function print() {
        System.debug("Rolled back First Saga");
        return "Rolled back First Saga";
    }
    return exports;
});
