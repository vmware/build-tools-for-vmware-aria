/**
 * @return {Any}
 */
(function () {
    var exports = {};
    var Logger = /** @class */ (function () {
        function Logger() {
        }
        Logger.prototype.log = function (msg) {
            System.log(msg);
        };
        return Logger;
    }());
    exports.Logger = Logger;
    return exports;
});
