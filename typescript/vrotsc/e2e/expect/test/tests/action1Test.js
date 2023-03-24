const __global = System.getContext() || (function () {
  return this
}.call(null))
const VROES = __global.__VROES || (__global.__VROES = System.getModule('com.vmware.pscoe.library.ecmascript').VROES())
const action1_1 = VROES.importLazy('com.vmware.pscoe.tests/action1')
describe('Test Case', function () {
  it('Can add 1 and 2', function () {
    expect(1 + 2).toBe(3)
  })
  it('Can create imported classes', function () {
    new action1_1._.default()
    new action1_1._.TestClass2()
  })
})
