/**
 * @return {Any}
 */
(function () {
  const exports = {}
  exports.default = function () { return print() }
  function print () {
    System.debug('Second saga')
    return 'Printed this second message'
  }
  return exports
})
