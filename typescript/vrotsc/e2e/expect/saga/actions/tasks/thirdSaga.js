/**
 * @return {Any}
 */
(function () {
  const exports = {}
  exports.default = function () { return print() }
  function print () {
    System.debug('Third saga')
    throw new Error('Error message')
  }
  return exports
})
