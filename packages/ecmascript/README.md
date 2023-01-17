# ECMAScript library
The purpose of the library is to provide runtime support for Typescript based projects by handling the export/import features of ECMA script as well as providing implementations for Set, Map and sum of the Array functions.

# Usage
This library is not to be used directly.

## Export

Named exports
```js
export class MyClass() {}
export function myFunction() {}
export var myVar = 5;

ESModule.export()
	.named("MyClass", MyClass)
	.named("myFunction", myFunction)
	.named("myVar", myVar)
	.build();
```

Default exports (function)
```js
export default function myFunction() {}

ESModule.export()
	.default(myFunction)
	.build();
```

Default exports (class)
```js
export default class MyClass {}

ESModule.export()
	.default(MyClass)
	.build();
```

## Import

Default import
```js
import defaultExport from "module-name";
var defaultExport = ESModule.import("default").from("module-name");
```

Namespace import
```js
import * as all from "module-name";
var all = ESModule.import("*").from("module-name");
```

Named imports
```js
import { MyClass, myFunction as myFunc } from "module-name";
var { MyClass, myFunc} = ESModule.import("MyClass", "myFunction").from("module-name");
```

Mixed imports
```js
import defaultExport, { MyClass, myFunction as myFunc } from "module-name";
var { defaultExport, MyClass, myFunc} = ESModule.import("default", "MyClass", "myFunction").from("module-name");
```
