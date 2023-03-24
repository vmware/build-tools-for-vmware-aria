/**
 * @return {Any}
 */
(function () {
  const __global = System.getContext() || (function () {
    return this
  }.call(null))
  const VROES = __global.__VROES || (__global.__VROES = System.getModule('com.vmware.pscoe.library.ecmascript').VROES()); const tslib_1 = VROES.tslib; const exports = {}
  const Container = /** @class */ (function () {
    function Container () {
    }
    Container.prototype.loadAsync = function () {
      const modules = []
      for (let _i = 0; _i < arguments.length; _i++) {
        modules[_i] = arguments[_i]
      }
      return tslib_1.__awaiter(this, void 0, void 0, function () {
        let getHelpers, _a, modules_1, currentModule, containerModuleHelpers
        return tslib_1.__generator(this, function (_b) {
          switch (_b.label) {
            case 0:
              getHelpers = function (id) { }
              _a = 0, modules_1 = modules
              _b.label = 1
            case 1:
              if (!(_a < modules_1.length)) return [3 /* break */, 4]
              currentModule = modules_1[_a]
              containerModuleHelpers = getHelpers(currentModule.id)
              return [4 /* yield */, currentModule.registry()]
            case 2:
              _b.sent()
              _b.label = 3
            case 3:
              _a++
              return [3 /* break */, 1]
            case 4: return [2]
          }
        })
      })
    }
    return Container
  }())
  exports.Container = Container
  return exports
})
