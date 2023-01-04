/**
 * @return {Any}
 */
(function () {
    var exports = {};
    exports.default = (function () { return print(); });
    function print() {
        System.debug("Rolled back Third Saga");
        return "Rolled back Third Saga";
    }
    return exports;
});
