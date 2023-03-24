module.exports = function () {
  function MathClass () {}
  MathClass.prototype.sum = function (a, b) {
    return a + b
  }
  MathClass.prototype.multiply = function (a, b) {
    return a * b
  }
  return MathClass
}
