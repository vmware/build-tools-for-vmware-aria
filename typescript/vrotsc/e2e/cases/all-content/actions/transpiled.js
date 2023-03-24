/**
 * @return {Any}
 */
(function () {
  const VROES = System.getModule('com.vmware.pscoe.library.ecmascript').VROES()
  const TestClass1 = VROES.class(function TestClass1 (s) {
  }, {
    test1: function () {
    },
    test2: function () {
    }
  })
  return VROES.export().named('TestClass1', TestClass1)
})
