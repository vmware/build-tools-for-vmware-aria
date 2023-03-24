/**
 * @return {Any}
 */
(function () {
  const __global = System.getContext() || (function () {
    return this
  }.call(null))
  const VROES = __global.__VROES || (__global.__VROES = System.getModule('com.vmware.pscoe.library.ecmascript').VROES()); const exports = {}
  const action1 = VROES.importLazy('com.vmware.pscoe.vrotsc.action1')
  function action3 (str) {
  }
  exports.action3 = action3
  exports.default = action1._
  return exports
})
