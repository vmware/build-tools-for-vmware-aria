/**
 * @return {Any}
 */
(function () {
  const __global = System.getContext() || (function () {
    return this
  }.call(null))
  const VROES = __global.__VROES || (__global.__VROES = System.getModule('com.vmware.pscoe.library.ecmascript').VROES()); const exports = {}
  let action2 = VROES.importLazy('com.vmware.pscoe.vrotsc.action2')
  const action3 = VROES.importLazy('com.vmware.pscoe.vrotsc.action3')
  const obj1 = {
    action2: action2._,
    action3: action3._
  }
  action2 = {
    action2: action2._
  }
  const obj2 = {
    action2,
    action3: action3._
  }
  exports.default = action3._
  exports.action3 = action3._
  exports.obj1 = obj1
  exports.obj2 = obj2
  return exports
})
