describe("MockClassTests", function() {
	var Class = System.getModule("com.vmware.pscoe.library.class").Class();
	var ActualClass = Class.define(function ActualClass() {
		// example
	}, {});
	var origGetModule = System.getModule;

	beforeAll(function() {
		// setup class loading mock of vRO api
		System.getModule = function(module) {
			return module == "test" && {
				"ActualClass": function() {
					return ActualClass;
				}
			}
		}
	});

	afterAll(function(){
		System.getModule = origGetModule;
	})

	it("Mock class can be overriden", function() {
		var Act = Class.load("test", "ActualClass");
		var Mock = Class.mock("test", "ActualClass", function MockedClass() {
			// nothing here
		}, {});
		
		var mockInst = new Mock();
		
		expect(ActualClass).toBeDefined();
		expect(Act).toBeDefined();
		expect(Mock).toBeDefined();
		expect(Act).toEqual(ActualClass, "actualClass");
		expect(mockInst instanceof Mock).toEqual(true, "Mock");
		expect(mockInst instanceof Act).toEqual(true, "Mock=Act");
		
		var Reload = Class.load("test", "ActualClass");
		
		expect(Mock).toEqual(Reload);
	});
});
