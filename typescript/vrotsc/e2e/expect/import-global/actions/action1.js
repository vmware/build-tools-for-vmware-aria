/**
 * @return {Any}
 */
(function () {
    var __global = System.getContext() || (function () {
        return this;
    }).call(null);
    var VROES = __global.__VROES || (__global.__VROES = System.getModule("com.vmware.pscoe.library.ecmascript").VROES()), exports = {};
    var tests_1 = VROES.importLazy("com.vmware.pscoe.tests");
    var sub_1 = VROES.importLazy("com.vmware.pscoe.tests/sub");
    var action1_1 = VROES.importLazy("com.vmware.pscoe.tests/action1");
    var action2_1 = VROES.importLazy("com.vmware.pscoe.tests/action2");
    var action1_2 = VROES.importLazy("com.vmware.pscoe.tests/action1");
    var subaction1_1 = VROES.importLazy("com.vmware.pscoe.tests/sub/subaction1");
    var subaction2_1 = VROES.importLazy("com.vmware.pscoe.tests/sub/subaction2");
    var subaction1_2 = VROES.importLazy("com.vmware.pscoe.tests/sub/subaction1");
    // new TestClass1();
    new action1_1._.default();
    // new TestClass2();
    new action1_1._.TestClass2();
    // new TestClass3;
    new action2_1._.default;
    // new TestClass4();
    new action2_1._.TestClass4();
    // new TestSubClass1();
    new subaction1_1._.default();
    // new TestSubClass2();
    new subaction1_1._.TestSubClass2();
    // new TestSubClass3();
    new subaction2_1._.default();
    // new TestSubClass4();
    new subaction2_1._.TestSubClass4();
    // new TC1();
    new action1_1._.default();
    // new TC2();
    new action1_1._.TestClass2();
    // new TC3();
    new action2_1._.default();
    // new TC4();
    new action2_1._.TestClass4();
    // new TSC1();
    new subaction1_1._.default();
    // new TSC2();
    new subaction1_1._.TestSubClass2();
    // new TSC3();
    new subaction2_1._.default();
    // new TSC4();
    new subaction2_1._.TestSubClass4();
    // new all.action1.default();
    new tests_1._.action1.default();
    // new all.action1.TestClass2();
    new tests_1._.action1.TestClass2();
    // new all.action2.default();
    new tests_1._.action2.default();
    // new all.action2.TestClass4();
    new tests_1._.action2.TestClass4();
    // new all.sub.subaction1.default();
    new tests_1._.sub.subaction1.default();
    // new all.sub.subaction1.TestSubClass2();
    new tests_1._.sub.subaction1.TestSubClass2();
    // new all.sub.subaction2.default();
    new tests_1._.sub.subaction2.default();
    // new all.sub.subaction2.TestSubClass4();
    new tests_1._.sub.subaction2.TestSubClass4();
    // new subAll.subaction1.default();
    new sub_1._.subaction1.default();
    // new subAll.subaction1.TestSubClass2();
    new sub_1._.subaction1.TestSubClass2();
    // new subAll.subaction2.default();
    new sub_1._.subaction2.default();
    // new subAll.subaction2.TestSubClass4();
    new sub_1._.subaction2.TestSubClass4();
    // new action1All.TestClass2();
    new action1_2._.TestClass2();
    // new action1All.default();
    new action1_2._.default();
    // new subaction1All.default();
    new subaction1_2._.default();
    // new subaction1All.TestSubClass2();
    new subaction1_2._.TestSubClass2();
    return exports;
});
