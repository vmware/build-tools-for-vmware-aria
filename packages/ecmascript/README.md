# ECMAScript library
The purpose of the library is to provide runtime support for Typescript based projects by handling the export/import features of ECMA script as well as providing implementations for Set, Map and sum of the Array functions.

## Usage
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

Import with relative and base path
```js
import defaultExport, { MyClass, myFunction as myFunc } from "module-name";
// Note: ./ and ../ are supported only at the start of the relative path. Relative paths without base path will result in error
var { myFunc} = ESModule.import("myFunction").from("../../relative/path", "module-name");
var { MyClass} = ESModule.import("MyClass").from("./relative/path", "module-name");
```

Import with custom error handling (default behavior is to log an Error and return null)
```js
import defaultExport, { MyClass, myFunction as myFunc } from "module-name";
// throw the error
var { myFunc} = ESModule.import("myFunction").from(
  "module-name",
  null, // no base path needed when module path is not relative
  function (error) { 
    throw typeof error === "string" ? new Error(error) : error;
  }
);
// log the error as warning
var { myFunc} = ESModule.import("myFunction").from(
  "module-name",
  null,
  function (error) { 
    System.warn(error.toString());
    return null; // required
  }
);
// do nothing - e.g. to avoid cluttering the logs when trying several potential module paths
var { myFunc} = ESModule.import("myFunction").from(
  "module-name",
  null,
  function (error) {
    
    return null; // required
  }
);
```
