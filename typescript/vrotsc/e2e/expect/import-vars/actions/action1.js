/**
 * @return {Any}
 */
(function () {
  const __global = System.getContext() || (function () {
    return this
  }.call(null))
  const VROES = __global.__VROES || (__global.__VROES = System.getModule('com.vmware.pscoe.library.ecmascript').VROES()); const exports = {}
  const action2_1 = VROES.importLazy('com.vmware.pscoe.vrotsc.action2')
  function test () {
    System.log('foo=' + action2_1._.foo + ', bar=' + action2_1._.bar)
  }
  exports.test = test
  return exports
})
