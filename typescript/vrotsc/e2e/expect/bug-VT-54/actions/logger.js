/**
 * @return {Any}
 */
(function () {
  const __global = System.getContext() || (function () {
    return this
  }.call(null))
  const VROES = __global.__VROES || (__global.__VROES = System.getModule('com.vmware.pscoe.library.ecmascript').VROES()); const exports = {}
  const appender_1 = VROES.importLazy('com.vmware.pscoe.vrotsc.appender')
  exports.default = appender_1._.BaseAppender
  return exports
})
