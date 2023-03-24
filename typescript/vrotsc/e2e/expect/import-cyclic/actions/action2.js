/**
 * @return {Any}
 */
(function () {
  const __global = System.getContext() || (function () {
    return this
  }.call(null))
  const VROES = __global.__VROES || (__global.__VROES = System.getModule('com.vmware.pscoe.library.ecmascript').VROES()); const exports = {}
  const action3_1 = VROES.importLazy('com.vmware.pscoe.vrotsc.sub/action3')
  function action2 (str) {
    action3_1._.action3(str)
  }
  exports.action2 = action2
  return exports
})
