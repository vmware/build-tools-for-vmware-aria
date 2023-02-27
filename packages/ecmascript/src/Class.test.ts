/*-
 * #%L
 * ecmascript
 * %%
 * Copyright (C) 2023 VMware
 * %%
 * Build Tools for VMware Aria
 * Copyright 2023 VMware, Inc.
 * 
 * This product is licensed to you under the BSD-2 license (the "License"). You may not use this product except in compliance with the BSD-2 License.  
 * 
 * This product may include a number of subcomponents with separate copyright notices and license terms. Your use of these subcomponents is subject to the terms and conditions of the subcomponent's license, as noted in the LICENSE file.
 * #L%
 */
describe("Module", () => {
    const defineClass = System.getModule("com.vmware.pscoe.library.ecmascript").Class().default;

    it("Class have static functiuons", () => {
        const C1 = defineClass(function C1() {
        }, {}, {
                sum: function (x: number, y: number) {
                    return x + y;
                }
            });

        expect(C1.sum(1, 2)).toEqual(3);
    })

    it("Class have instance properties", () => {
        const C1 = defineClass(function C1(prop1: string) {
            this._prop1 = prop1;
        });
        Object.defineProperty(C1.prototype, "prop1", {
            get: function () {
                return this._prop1;
            },
            set: function (value) {
                this._prop1 = value;
            },
            enumerable: true,
            configurable: true
        });
        var c1 = new C1("test");
        expect(c1.prop1).toEqual("test");
        c1.prop1 = "changed";
        expect(c1.prop1).toEqual("changed");
    })

    it("Class have static properties", () => {
        const C1 = defineClass(function C1() {
        });
        Object.defineProperty(C1, "prop1", {
            get: function () {
                return C1._prop1;
            },
            set: function (value) {
                C1._prop1 = value;
            },
            enumerable: true,
            configurable: true
        });
        C1.prop1 = "test";
        expect(C1.prop1).toEqual("test");
        C1.prop1 = "changed";
        expect(C1.prop1).toEqual("changed");
    })

    it("Sub class can inherit functions", () => {
        const C1 = defineClass(function C1() {
        }, {
                func1() {
                    return "C1::func1";
                },
                func2() {
                    return "C1::func2";
                }
            });

        const C2 = defineClass(function C2() {
        }, {
                func1() {
                    return "C2::func1";
                },
                func2() {
                    return C1.prototype.func2.call(this) + " - C2::func2";
                }
            }, undefined, C1);

        let c2 = new C2();
        expect(c2.func1()).toEqual("C2::func1");
        expect(c2.func2()).toEqual("C1::func2 - C2::func2");
    })

    it("Sub class inherit properties", () => {
        const C1 = defineClass(function C1() {
        }, {
                get prop1() {
                    return "C1::prop1";
                },
                get prop2() {
                    return "C1::prop2";
                }
            });
        Object.defineProperty(C1.prototype, "prop1", {
            get: function () {
                return "C1::prop1";
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(C1.prototype, "prop2", {
            get: function () {
                return "C1::prop2";
            },
            enumerable: true,
            configurable: true
        });
        const C2 = defineClass(function C2() {
        }, {
                get prop1() {
                    return "C2::prop1";
                }
            }, undefined, C1);
        Object.defineProperty(C2.prototype, "prop1", {
            get: function () {
                return "C2::prop1";
            },
            enumerable: true,
            configurable: true
        });
        let c2 = new C2();
        expect(c2.prop1).toEqual("C2::prop1");
        expect(c2.prop2).toEqual("C1::prop2");
    })

    it("Class have toString", () => {
        const C1 = defineClass(function C1() {
        });

        expect(new C1().toString()).toEqual("[object C1]");
    })

    it("Class does not replace existing toString", () => {
        const C1 = defineClass(function C1() {
        }, {
                toString: function () {
                    return "C1 class";
                }
            });

        const C2 = defineClass(function C2() {
        }, {}, {}, C1);

        expect(new C1().toString()).toEqual("C1 class");
        expect(new C2().toString()).toEqual("C1 class");
    })

    it("Override class preserves toString function", () => {
        const C1 = defineClass(function C1() {
        }, {
                toString: function () {
                    return "C1 class";
                }
            });

        const C2 = defineClass(function C2() {
        }, {
                toString: function () {
                    return "C2 class";
                }
            }, {}, C1);

        expect(new C1().toString()).toEqual("C1 class");
        expect(new C2().toString()).toEqual("C2 class");
    })
})
