/**
 * @return {Any}
 */
(function () {
  const exports = {}
  exports.default = function () { return print() }
  function print () {
    System.debug('First Saga')
    return 'Printed this first message'
  }
  return exports
})
