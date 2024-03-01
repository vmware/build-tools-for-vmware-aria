interface TestInterface {
    prop1?: {
        prop2?: {
            prop3?: string;
        }
    }
}

var obj1: TestInterface = {};
System.log(`obj.prop1.prop2.prop3=${obj1.prop1?.prop2?.prop3}`);
