/**
 * @return {Any}
 */
(function () {
  const __global = System.getContext() || (function () {
    return this
  }.call(null))
  const VROES = __global.__VROES || (__global.__VROES = System.getModule('com.vmware.pscoe.library.ecmascript').VROES()); const exports = {}
  const subaction1_1 = VROES.importLazy('com.vmware.pscoe.vrotsc.sub/subaction1')
  const subaction1_2 = VROES.importLazy('com.vmware.pscoe.vrotsc.sub/subaction1')
  const subaction1_3 = VROES.importLazy('com.vmware.pscoe.vrotsc.sub/subaction1')
  exports.TestClass2 = subaction1_1._.TestClass2
  exports.namedImport = subaction1_2._
  exports.default = subaction1_3._.default
  exports.def1 = subaction1_2._.default
  exports.def2 = subaction1_3._.default
  return exports
})
