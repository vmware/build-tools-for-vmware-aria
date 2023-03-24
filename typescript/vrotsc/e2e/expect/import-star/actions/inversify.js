/**
 * @return {Any}
 */
(function () {
  const __global = System.getContext() || (function () {
    return this
  }.call(null))
  const VROES = __global.__VROES || (__global.__VROES = System.getModule('com.vmware.pscoe.library.ecmascript').VROES()); const exports = {}
  const metadata_keys_1 = VROES.importLazy('com.vmware.pscoe.vrotsc.constants/metadata_keys')
  exports.METADATA_KEY = metadata_keys_1._
  const METADATA_KEY = exports.METADATA_KEY
  return exports
})
