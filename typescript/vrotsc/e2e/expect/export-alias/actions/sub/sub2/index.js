/**
 * @return {Any}
 */
(function () {
  const __global = System.getContext() || (function () {
    return this
  }.call(null))
  const VROES = __global.__VROES || (__global.__VROES = System.getModule('com.vmware.pscoe.library.ecmascript').VROES()); const require = VROES.require; const tslib_1 = VROES.tslib; const exports = {}
  tslib_1.__exportStar(require('com.vmware.pscoe.vrotsc.sub/sub2/test2'), exports)
  return exports
})
