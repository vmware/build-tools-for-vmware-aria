This workflow library provides functions that define and load classes, which can be used in vRO JavaScript tasks.

## Define a class in an action
The define function is used to define a class.
The function accepts the following parameters:
- name - the name of the class (optional, can be ommitted if a named function is passed as constructor function)
- constructor function
- prototype - an object, defining the prototype of the class
- extension class - the class that this class will extend

**Action:** *com.vmware.pscoe.example/Car*
```javascript
var Class = System.getModule("com.vmware.pscoe.library.class").Class();

return Class.define(function Car(model, color){
	this.model = model;
	this.color = color;
	this.purpose = "drive";
}, { drive: function () {// some driving code}});
```

Public functions can be defined in the prototype of the class.
Private and public functions can be specified in the constructor function.

The following example defines a private and a public function in the constructor.

**Action:** *com.vmware.pscoe.example/Plane*
```javascript
var Class = System.getModule("com.vmware.pscoe.library.class").Class();

return Class.define(function Plane(model){
	this.model = model;
	this.purpose = "fly";

	var privateAttribute = "i am private";
	var privateFunction = function () {
		// do some stuff
	};

	this.publicFunction = function () {
		// do some stuff
	};
}, { // no prototype in this example });
```

## Extend the class in another action
**Action:** *com.vmware.pscoe.example/Porsche*
```javascript
var Class = System.getModule("com.vmware.pscoe.library.class").Class();
var Car = System.getModule("com.vmware.pscoe.example").Car();

return Class.define(function Porsche(model, color){
	Car.call(this, model, color);
	this.purpose = "be cool";
}, null, Car)
```

The class name will be derived from the function name of the first parameter.

The class name can also be specified explicitly by passing the name as the first parameter.

**Action:** *com.vmware.pscoe.example/Volkswagen*
```javascript
var Class = System.getModule("com.vmware.pscoe.library.class").Class();
var Car = System.getModule("com.vmware.pscoe.example").Car();

return Class.define("Volkswagen", function (model, color){
	Car.call(this, model, color);
	this.purpose = "be useful";
}, null, Car)
```


## Load a class in a script
The class definition can be loaded by calling the action, but it is recommended to use the `Class.load` function.

This function uses caching to load a class and ensures that only one version of this class will be used accross all scripting context.

This is important in case you want to compare instances with `instanceof` operator.

It is a good practice to define an imports section at the beginning of the action that keeps the vRO action calls. This way, vRO can find the action as a reference.

```javascript
// Imports
// System.getModule("com.vmware.pscoe.example").Porsche();

var Class = System.getModule("com.vmware.pscoe.library.class").Class();
var Porsche = Class.load("com.vmware.pscoe.example", "Porsche");

var p = new Porsche("Panamera", "blue");
p.drive();
```

## Load multiple classes

The Class.find() method can be used to provide extensibility mechanisms to solutions by loading all classes which match a module/name patterns and implement a certain interface.

```javascript
// Find classes which name ends in Resolver and are located in .context modules.
// Then check if the found classes implement a resolveValue function.
Class.find(/\.context$/g, /.*Resolver$/g)
	.map(function (classCtor) {
		return new classCtor();
	})
	.filter(function (classInstance) {
		return typeof classInstance.resolveValue === "function";
	})
	.forEach(function (classInstance) {
		logger.info(classInstance.resolveValue())
	});
});
```

```javascript
// Finds all classes inside modules ending with ".context",
// then instantiates them and converts the result to a map { className: classInstance }
var contextResolvers = Class.find(/\.context$/)
	.map(function (clazz) {
		return new clazz();
	})
	.reduce(function (map, clazzInstance) {
		map[clazz.name] = clazzInstance;
		return map;
	}, {});
```

If order is important, an array of modules can be passed as parameter
```javascript
var contextResolvers = Class.find([
		"com.vmware.pscoe.library.context.workflow",
		"com.vmware.pscoe.library.vc.context",
		"com.vmware.pscoe.library.vra.context",
		"com.vmware.pscoe.customer.context"
	])
	.map(function (clazz) {
		return new clazz();
	})
```

## Class multi-import
As of ES6 a module (that is a JS file, or vRO action) can export multiple objects (e.g. functions, objects and primitives). In order to support those new constructs and to be able to distinguish from existing exports (that were based on single return) we are introducing following new mechanisms:

### Export of named objects
ES6 defines the syntax as:
```js
export { name1, name2, …, nameN };
export { variable1 as name1, variable2 as name2, …, nameN };
export let name1, name2, …, nameN; // also var, const
export let name1 = …, name2 = …, …, nameN; // also var, const
export function FunctionName(){...}
export class ClassName {...}
```

We are supporting this through the Class library by returning as special ModuleExports object as:
```js
// let's define some objects to export
var ClassVarRef = Class.define(function ClassName() {})
function FunctionName() {...}
var obj = { a: "a", b: 5 }

// returns an instance of ModuleExports object as result suitable for distructing assignment through Class.load() and Class.import()
return Class.export(ClassVarRef, FunctionName)
	.named({map: obj, CONST_7: 7});
```
As can be seen the `Class.export()` function accepts objects that are already named and if they are not an error will be produced. In this particular case the class referenced by `ClassVarRef` will be exported as the name of the constructor - `ClassName`. If the object name cannot be inferred we use the `.named()` method to export the object with a name that will be used in the import later.

The corresponding import for named objects looks like:
```js
// import the named map of all module exports
var module = Class.import("com/vmware/pscoe/module/vroAction", "*");

System.log(JSON.stringify(module)); // results in: {
//	  ClassName: function ClassName() {} - the class constructor
//    FunctionName: function FunctionName() {} - the function instance
//	  map: { a: "a", b: 5 }
//    CONST_7: 7
// }
```
Import a specific named export:
```js
// import the map object
var map = Class.import("com/vmware/pscoe/module/vroAction", "map");
```
You might also want to use Destructing Assignment for the import as:
```js
// import all but the CONST_7 exports and assign to alias variables
var {Cls:ClassName, Func:FunctionName, map:map} = Class.import("com/vmware/pscoe/module/vroAction", "*");

// call function directly by its referencing alias
Func();
```

### Default Export
ES6 defines default exports as follows:
```js
export default expression;
export default function (…) { … } // also class, function*
export default function name1(…) { … } // also class, function*
export { name1 as default, … };
```
Using `Class.export()` this looks like:
```js
var ClassVarRef = Class.define(function ClassName() {})
function FunctionName() {...}
var obj = { a: "a", b: 5 }

// named-export as before but make ClassName the default
return Class.export(FunctionName)
	.default(ClassVarRef)
	.named({map: obj, CONST_7: 7});

// or if we want to export the 'obj' as default:
return Class.export(ClassVarRef, FunctionName)
	.default(obj)
	.named({CONST_7: 7});

// or if we have only a single default export we can use the good old as before
return ClassVarRef;
```
Import the default export as:
```js
var def = Class.import("com/vmware/pscoe/module/vroAction");

System.log(JSON.stringify(def)); // results in: { a: "a", b: 5 } if we are referring to the second example above (or ClassName in the first)
```
Or you can import both the default and named map as:
```js
var [def, module] = Class.import("com/vmware/pscoe/module/vroAction", ["*"]);

System.log(JSON.stringify(def)); // results in: { a: "a", b: 5 }
```
Or you can import the default along with specified named exports:
```js
var [def, Func, m] = Class.import("com/vmware/pscoe/module/vroAction", ["FunctionName", "map"]);
```
`Note` if default is not defined the above imports will return `undefined` for the first positional parameter.

### Redirect Export
ES6 allows for a module to export directly from other modules as:
```js
export * from …;
export { name1, name2, …, nameN } from …;
export { import1 as name1, import2 as name2, …, nameN } from …;
export { default } from …;
```
This translates to:
```js
var ClassVarRef = Class.define(function ClassName() {})
function FunctionName() {...}
var obj = { a: "a", b: 5 }

// named-export as before but make ClassName the default
return Class.export(ClassVarRef, FunctionName)
	.named({map: obj, CONST_7: 7});
	.from("com/vmware/pscoe/module/anotherVroAction", "*") // re-export full named map
	.from("com/vmware/pscoe/module/anotherVroAction") // re-export default
	.from("com/vmware/pscoe/module/anotherVroAction", [obj1, obj2]) // re-export only specific named exports
	.from("com/vmware/pscoe/module/anotherVroAction", {alias: name}) // re-export specific exports with aliases
```

### Import syntax
The following is a distilled comparisson between ES6 syntax and Class import syntax:
```js
// ES6 - import the default from module and assign to defaultExport
import defaultExport from "module-name";
// Class.import()
var defaultExport = Class.import("module-name");

// ES6 - import the named map from module and assign to name
import * as name from "module-name";
// Class.import()
var name = Class.import("module-name", "*");

// ES6 - import only the named export and assign to an alias
import { export } from "module-name";
import { export as alias } from "module-name";
// Class.import()
var alias = Class.import("module-name", "export");

// ES6 - import multiple specific named exports
import { foo , bar } from "module-name/path/to/specific/un-exported/file";
// Class.import() - array destructing assignment and skipping default
var [, foo, bar] = Class.import("module-name/path/to/specific/un-exported/file", ["foo", "bar"]);

// ES6 - import multiple specific named exports and assign alias
import { export1 , export2 as alias2 , [...] } from "module-name";
// Class.import()
var [, export1, alias2] = Class.import("module-name", ["export1", "export2"]);

// ES6 - import default and specified named exports
import defaultExport, { export [ , [...] ] } from "module-name";
// Class.import()
var [defaultExport, export] = Class.import("module-name", ["export"]);

// ES6 - import default and the full named map
import defaultExport, * as name from "module-name";
// Class.import()
var [defaultExport, name] = Class.import("module-name", ["*"]);
```

### Compatibility notes
Existing vRO actions return a simple value (i.e. not ModuleExports) thus when `Class.import` is called it will treat any non-ModuleExports as if a ModuleExports instance was loaded with single default export.

`Class.load` will not be extended with support ModuleExports results from vRO Action as it is frequently used interchangeably with `System.getModule` which we cannot extend. Here is why the `Class.import` and `Class.load` will not be compatible in their behavoir. `Class.load` will be responsible for loading and tracking vRO actions (modules) but the returning result will be unchecked - i.e. it will not make distinction between simple vs. ModuleExports results. `Class.import` will be responsible for ES6 (TypeScript) compatible diferentiation between default vs named exports. Here is an example of what each return:


| Method                      | Simple Return | ModuleExports return              |
|-----------------------------|---------------|-----------------------------------|
|`Class.load("path","module")`| Simple Return | ModuleExports Return              |
|`Class.import("path/module")`| Simple Return | default export from ModuleExports |
|`Class.import("path/module", "*")`| {} | Named map from ModuleExports            |
|`Class.import("path/module", ["*"])`| [Simple Return, {}]| [default, named map]  |

## Export Shape Validation and Diagnostics

### Overview
As of v4.21.0+, the Class library includes helper diagnostics to detect and report invalid export shapes. This is especially useful in scenarios with module path misconfiguration (e.g., when loading from `com.vmware.pscoe.library.ts.*` aliases instead of canonical module paths), which can cause `[object Object] is not a function` errors later at call sites in Rhino 1.8.0+ strict environments.

### Class.load() Compatibility
`Class.load()` remains backward-compatible with historical behavior: it loads and caches the raw action return value without enforcing constructor-only semantics. This preserves support for existing actions that return object exports such as ModuleExports-style maps or singleton objects.

If you need to inspect whether a loaded value is actually a constructor, use `Class.validateExportShape()` explicitly:

```javascript
var Class = System.getModule("com.vmware.pscoe.library.class").Class();

var rawExport = Class.load("com.vmware.pscoe.library.logging.appenders", "AppenderClass");
var report = Class.validateExportShape(rawExport, "com.vmware.pscoe.library.logging.appenders", "AppenderClass");

if (!report.isFunction) {
	System.warn(JSON.stringify(report));
}
```

### Manual Export Shape Inspection
The `Class.validateExportShape()` function provides explicit inspection of export shapes for debugging:

```javascript
var Class = System.getModule("com.vmware.pscoe.library.class").Class();

// Validate a constructor function
var report = Class.validateExportShape(MyConstructor, "com.example.module", "MyClass");
System.log("Report: " + JSON.stringify(report));
// Output: {
//   timestamp: "2026-05-17T12:34:56.789Z",
//   module: "com.example.module",
//   name: "MyClass",
//   exportType: "function",
//   isFunction: true,
//   isConstructor: true,
//   exportKeys: [],
//   issues: []
// }

// Validate an object export (problematic)
var objExport = { 
	BaseClass: function BaseClass() {},
	ExtendedClass: function ExtendedClass() {}
};
var report = Class.validateExportShape(objExport, "com.example.appenders", "AppenderClass");
// Output: {
//   ...
//   exportType: "object",
//   isFunction: false,
//   exportKeys: ["BaseClass", "ExtendedClass"],
//   issues: [
//     "[Class][E_EXPORT_SHAPE_MISMATCH] Expected a constructor function, but got an 
//      object with keys: [BaseClass, ExtendedClass]. This typically occurs when module 
//      loading returns a wrapper object instead of the target constructor..."
//   ]
// }
```

### Diagnostic Error Codes
| Error Code | Meaning | Common Cause |
|-----------|---------|--------------|
| `E_EXPORT_SHAPE_MISMATCH` | Module returned an object instead of a constructor | Path misconfiguration (e.g., `com.vmware.pscoe.library.ts.*` aliases) |
| `E_EXPORT_SHAPE_INVALID` | Module returned an unexpected type (not function/object) | Action returns primitive (string, number, etc.) or null |

### Troubleshooting Module Path Issues
When you encounter `[object Object] is not a function` errors after using `Class.load()`:

1. **Verify canonical module paths**: Ensure actions are loaded from consistent, canonical paths (not alternate paths)
2. **Check export shapes**: Run `Class.validateExportShape()` on suspected exports
3. **Inspect action returns**: Log the return value of actions to confirm they export constructors directly
4. **Review library configuration**: If using libraries like logging, verify they are configured to load from canonical module paths

Example diagnostic workflow:
```javascript
var Class = System.getModule("com.vmware.pscoe.library.class").Class();

// Step 1: Load the action result as usual
var AppenderClass = Class.load("com.vmware.pscoe.library.logging.appenders", "SystemAppender");

// Step 2: Validate the export shape when troubleshooting
var report = Class.validateExportShape(AppenderClass,
	"com.vmware.pscoe.library.logging.appenders", "SystemAppender");
System.log("Diagnostic Report: " + JSON.stringify(report));

// Step 3: Check for path misconfigurations
var tsPathModule = System.getModule("com.vmware.pscoe.library.ts.logging.appenders");
if (tsPathModule) {
	System.warn("WARNING: Both canonical and ts.* path modules are loaded. " +
		"This indicates path misconfiguration. Use only canonical paths.");
}
```
