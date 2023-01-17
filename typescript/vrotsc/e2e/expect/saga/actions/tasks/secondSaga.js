/**
 * @return {Any}
 */
(function () {
    var exports = {};
    exports.default = (function () { return print(); });
    function print() {
        System.debug("Second saga");
        return "Printed this second message";
    }
    return exports;
});
