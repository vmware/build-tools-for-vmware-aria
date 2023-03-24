/**
 * @return {Any}
 */
(function () {
  const __global = System.getContext() || (function () {
    return this
  }.call(null))
  const VROES = __global.__VROES || (__global.__VROES = System.getModule('com.vmware.pscoe.library.ecmascript').VROES()); const exports = {}
  // String
  const str = 'the brown fox jumped over the lazy dog'
  VROES.Shims.stringStartsWith(str, 'the brown')
  return exports
})
