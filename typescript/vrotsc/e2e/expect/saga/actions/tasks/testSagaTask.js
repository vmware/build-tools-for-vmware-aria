/**
 * @return {Any}
 */
(function () {
  const exports = {}
  exports.default = function () { return print() }
  function print () {
    System.debug('Print message')
    return 'Printed this message'
  }
  return exports
})
