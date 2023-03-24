/**
 * @return {Any}
 */
(function () {
  const __global = System.getContext() || (function () {
    return this
  }.call(null))
  const VROES = __global.__VROES || (__global.__VROES = System.getModule('com.vmware.pscoe.library.ecmascript').VROES()); const exports = {}
  const Map = VROES.Map; const Set = VROES.Set
  // String
  const str = 'the brown fox jumped over the lazy dog'
  VROES.Shims.stringStartsWith(str, 'the brown')
  VROES.Shims.stringEndsWith(str, 'lazy dog')
  VROES.Shims.stringPadStart(str, 50, '*')
  VROES.Shims.stringPadEnd(str, 50, 'lazy')
  // Array
  const arr = [1, 2, 3, 4, 5]
  VROES.Shims.arrayFind(arr, function (x) { return x > 3 })
  VROES.Shims.arrayFindIndex(arr, function (x) { return x > 3 })
  // Set
  const set = new Set()
  set.add('key1')
  set.add('key1')
  set.add('key2')
  set.forEach(function (k) {
    console.log(k)
  })
  // Map
  const map = new Map()
  map.set('key1', 'value1')
  map.set('key1', 'value1')
  map.set('key2', 'value2')
  map.forEach(function (k, v) {
    console.log(k + '=' + v)
  })
  // Object
  const obj = {
    startsWith: function (x) { return true },
    find: function (x) { },
    findIndex: function (x) { return 0 }
  }
  obj.startsWith('test')
  obj.find('test')
  obj.findIndex('test')
  return exports
})
