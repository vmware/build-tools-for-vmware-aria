/**
 * @return {Any}
 */
(function () {
  const __global = System.getContext() || (function () {
    return this
  }.call(null))
  const VROES = __global.__VROES || (__global.__VROES = System.getModule('com.vmware.pscoe.library.ecmascript').VROES()); const exports = {}
  const enum_1 = VROES.importLazy('com.vmware.pscoe.vrotsc.enum')
  const var_1 = VROES.importLazy('com.vmware.pscoe.vrotsc.var')
  System.log('TestEnum=' + enum_1._.TestEnum.VAL2)
  System.log('TestVar=' + var_1._.TestVar)
  return exports
})
