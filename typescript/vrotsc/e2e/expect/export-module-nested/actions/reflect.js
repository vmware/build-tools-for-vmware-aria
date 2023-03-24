/**
 * @return {Any}
 */
(function () {
  const exports = {}
  var MyNamespace = {}
  exports.MyNamespace = MyNamespace
  var MyNamespace = exports.MyNamespace;
  (function (MyNamespace) {
    function decorate (decorators, target) { }
    MyNamespace.decorate = decorate
    function decorate2 (decorators, target) { }
    MyNamespace.hasOwn = 'val1'
    const hasOwn2 = 'val2'
    const Mirror2 = /** @class */ (function () {
      function Mirror2 () {
      }
      Mirror2.prototype.reflect = function (params) { }
      return Mirror2
    }())
    const Mirror = /** @class */ (function () {
      function Mirror () {
      }
      Mirror.prototype.reflect = function (params) { }
      return Mirror
    }())
    MyNamespace.Mirror = Mirror
  })(MyNamespace || (MyNamespace = {}))
  function somethingElse () { }
  exports.somethingElse = somethingElse
  return exports
})
