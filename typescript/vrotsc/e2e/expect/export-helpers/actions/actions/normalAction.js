/**
 * @return {Any}
 */
(function () {
  const __global = System.getContext() || (function () {
    return this
  }.call(null))
  const VROES = __global.__VROES || (__global.__VROES = System.getModule('com.vmware.pscoe.library.ecmascript').VROES()); const exports = {}
  const helper_1 = VROES.importLazy('com.vmware.pscoe.vrotsc.actions/test.helper')
  exports.default = function () {
    return helper_1._.default()
  }
  return exports
})
