/**
 * @return {Any}
 */
(function () {
  const exports = {}
  exports.foo = 5
  const foo = exports.foo
  exports.bar = 'test1'
  const bar = exports.bar
  exports.x = 4
  const x = exports.x
  exports.y = 5
  const y = exports.y
  exports.z = 'this is a test'
  const z = exports.z
  return exports
})
