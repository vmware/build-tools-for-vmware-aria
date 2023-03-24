/**
 * @return {Any}
 */
(function () {
  const __global = System.getContext() || (function () {
    return this
  }.call(null))
  const VROES = __global.__VROES || (__global.__VROES = System.getModule('com.vmware.pscoe.library.ecmascript').VROES()); const exports = {}
  const action2_1 = VROES.importLazy('com.vmware.pscoe.vrotsc.action2')
  const Action1 = /** @class */ (function () {
    function Action1 () {
    }
    Action1.prototype.print = function (str) {
      action2_1._.action2(str)
    }
    return Action1
  }())
  exports.Action1 = Action1
  return exports
})
