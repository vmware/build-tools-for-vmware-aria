/**
 * @return {Any}
 */
(function () {
  const __global = System.getContext() || (function () {
    return this
  }.call(null))
  const VROES = __global.__VROES || (__global.__VROES = System.getModule('com.vmware.pscoe.library.ecmascript').VROES()); const exports = {}
  const action3 = VROES.importLazy('com.vmware.pscoe.vrotsc.action3')
  exports.action3 = action3._
  return exports
})
