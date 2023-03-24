/**
 * @return {Any}
 */
(function () {
  const __global = System.getContext() || (function () {
    return this
  }.call(null))
  const VROES = __global.__VROES || (__global.__VROES = System.getModule('com.vmware.pscoe.library.ecmascript').VROES()); const exports = {}
  const action1_1 = VROES.importLazy('com.vmware.pscoe.vrotsc.action1')
  const action2_1 = VROES.importLazy('com.vmware.pscoe.vrotsc.sub/action2')
  exports.Class1 = action1_1._.Class1
  exports.Class2 = action2_1._.Class2
  return exports
})
