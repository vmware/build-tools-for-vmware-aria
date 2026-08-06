describe("DefineClassTests", function() {
	var Class = System.getModule("com.vmware.pscoe.library.class").Class();

	it("Define class with name and constructor", function() {
		var C1 = Class.define("C1", function(){});
		expect(C1).toBeDefined();
		var c = new C1();
		expect(c instanceof C1).toBeTruthy();
	});
	it("Define class with named constructor", function() {
		var C1 = Class.define(function C1(){});
		expect(C1).toBeDefined();
		var c = new C1();
		expect(c instanceof C1).toBeTruthy();
	});
	it("Define class with named constructor with instance methods", function() {
		var C1 = Class.define(function C1(){
			this.instanceFunc = function() {
				return 10;
			}
		});
		expect(C1).toBeDefined();
		var c = new C1();
		expect(c instanceof C1).toBeTruthy();
		expect(c.instanceFunc()).toEqual(10);
	});
	it("Define class with named constructor with instance and static methods", function() {
		var C1 = Class.define(function C1(){
			this.instanceFunc = function() {
				return "inst";
			}
		});
		C1.staticfunc = function() {
			return "static"
		}
		expect(C1).toBeDefined();
		var c = new C1();
		expect(c instanceof C1).toBeTruthy();
		expect(c.instanceFunc()).toEqual("inst");
		expect(C1.staticfunc()).toEqual("static");
	});
	it("Get class name, instance", function() {
		var C1 = Class.define(function C1(){
			this.instanceFunc = function() {
				return this.constructor.name;
			}
		});
		expect(C1).toBeDefined();
		var c = new C1();
		expect(c instanceof C1).toBeTruthy();
		expect(c.instanceFunc()).toEqual("C1");
	});
	it("Get class name, static", function() {
		var C1 = Class.define(function C1(){
		});
		C1.staticfunc = function() {
			return this.name
		}
		expect(C1).toBeDefined();
		expect(C1.staticfunc()).toEqual("C1");
	});
	it("Can define methods with additional obj", function() {
		var C1 = Class.define("C1", function C1(){
			this.instanceFunc = function() {
				return this.constructor.name;
			}
		},{
			instance2 : function() {
				return 10;
			}
		});
		expect(C1).toBeDefined();
		var c = new C1();
		expect(c instanceof C1).toBeTruthy();
		expect(c.instanceFunc()).toEqual("C1");
		expect(c.instance2).toBeDefined();
		expect(c.instance2()).toEqual(10);
	});
	it("Inheritance", function () {
		var C1 = Class.define(function C1(){
			this.instanceFunc = function() {
				return this.constructor.name;
			}
		},{
			instance2 : function() {
				return 10;
			}
		});
		C1.staticfunc = function() {
			return this.name;
		}
		C1.staticfunc2 = function () {
			return "original";
		}
		expect(C1).toBeDefined();

		// first create the constructor
		var C2ctor = function C2() {
			C1.call(this);
		};
		// assign static member
		C2ctor.staticfunc2 = function () {
			return "overridden";
		}
		// then define the class
		var C2 = Class.define(C2ctor, null, C1);
		var c = new C2();
		expect(c instanceof C1).toBeTruthy();
		expect(c instanceof C2).toBeTruthy();
		expect(c.instanceFunc()).toEqual("C2");
		expect(c.instance2).toBeDefined();
		expect(c.instance2()).toEqual(10);
		expect(C2.staticfunc()).toEqual("C2");
		expect(C2.staticfunc2()).toEqual("overridden");
	});
	it("Overriding", function() {
		var C1 = Class.define(function C1(){
			this.instanceFunc = function() {
				return this.constructor.name;
			}
		},{
			instance2 : function() {
				return 10;
			}
		});
		C1.staticfunc = function() {
			return this.name;
		}
		C1.staticfunc2 = function() {
			return "PARENT" + this.staticfunc();
		}
		expect(C1).toBeDefined();

		var C2 = Class.define(function C2(){
			C1.call(this);
			this.instanceFunc = function() {
				return "CHILD" + this.constructor.name;
			}
		}, null, C1);
		C2.staticfunc2 = function() {
			return "CHILD" + this.staticfunc();
		}

		var c = new C2();
		expect(c instanceof C1).toBeTruthy();
		expect(c instanceof C2).toBeTruthy();
		expect(c.instanceFunc()).toEqual("CHILDC2");
		expect(c.instance2).toBeDefined();
		expect(c.instance2()).toEqual(10);
		expect(C2.staticfunc2()).toEqual("CHILDC2");

	});

	it("Define class with name retains the name", function() {
		var name = "C1";
		var C1 = Class.define(name, function(){});
		var c = new C1();
		expect(c.constructor.name).toEqual(name);
	});

	it("Define class with function retains funciton name", function() {
		var name = "C1";
		var Class1 = Class.define(function C1(){});
		var c = new Class1();
		expect(c.constructor.name).toEqual(name);
	});

	it("Override class preserves toString function", function() {
		var BaseClass = Class.define(function BaseClass() {}, {
			toString: function() {
				return "BaseClass.toString";
			}
		});
		var Class1 = Class.define(function Class1() {}, {}, BaseClass);
		var Class2 = Class.define(function Class2() {}, {
			toString: function() {
				return "Class2.toString";
			}
		}, BaseClass);
		expect(new Class1().toString()).toEqual("BaseClass.toString");
		expect(new Class2().toString()).toEqual("Class2.toString");
	});

	it("Define class retain constructor prototype", function () {
		var Class1Ctor = function Class1() { };
		Class1Ctor.prototype.func1 = function () {  
			return "instance func1";
		};
		Class1Ctor.prototype.func2 = function () {  
			return "instance func2";
		};
		var Class1 = Class.define(Class1Ctor);
		expect(new Class1().func1()).toEqual("instance func1");
		expect(new Class1().func2()).toEqual("instance func2");
	});

	it("Define class retain constructor prototype with override", function () {
		var Class1Ctor = function Class1() { };
		Class1Ctor.prototype.func1 = function () {  
			return "instance func1";
		};
		Class1Ctor.prototype.func2 = function () {  
			return "instance func2";
		};
		var Class1 = Class.define(Class1Ctor, {
			func2: function() {
				return "overridden instance func2"
			},
			func3: function() {
				return "instance func3"
			}
		});
		expect(new Class1().func1()).toEqual("instance func1");
		expect(new Class1().func2()).toEqual("overridden instance func2");
		expect(new Class1().func3()).toEqual("instance func3");
	});
});
