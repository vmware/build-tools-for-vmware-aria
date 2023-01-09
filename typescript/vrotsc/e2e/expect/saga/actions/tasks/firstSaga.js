/**
 * @return {Any}
 */
(function () {
    var exports = {};
    exports.default = (function () { return print(); });
    function print() {
        System.debug("First Saga");
        return "Printed this first message";
    }
    return exports;
});
