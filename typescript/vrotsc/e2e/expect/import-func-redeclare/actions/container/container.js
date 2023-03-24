/**
 * @return {Any}
 */
(function () {
  const __global = System.getContext() || (function () {
    return this
  }.call(null))
  const VROES = __global.__VROES || (__global.__VROES = System.getModule('com.vmware.pscoe.library.ecmascript').VROES()); const exports = {}
  const resolver_1 = VROES.importLazy('com.vmware.pscoe.vrotsc.resolution/resolver')
  const Container = /** @class */ (function () {
    function Container () {
    }
    Container.prototype.resolve = function (constructorFunction) {
    }
    // Planner creates a plan and Resolver resolves a plan
    // one of the jobs of the Container is to links the Planner
    // with the Resolver and that is what this function is about
    Container.prototype._planAndResolve = function () {
      return function (args) {
        // resolve plan
        const result = resolver_1._.resolve({})
        return result
      }
    }
    return Container
  }())
  exports.Container = Container
  return exports
})
