/**
 * @return {Any}
 */
(function () {
  const __global = System.getContext() || (function () {
    return this
  }.call(null))
  const VROES = __global.__VROES || (__global.__VROES = System.getModule('com.vmware.pscoe.library.ecmascript').VROES()); const exports = {}
  const sub_1 = VROES.importLazy('com.vmware.pscoe.vrotsc.sub')
  new sub_1._.Test1()
  new sub_1._.sub2.Test2()
  return exports
})
