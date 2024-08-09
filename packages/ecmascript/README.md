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

Change error handling behavior on module import/load:
```js
import defaultExport, { MyClass, myFunction as myFunc } from "module-name";
// With predefined error handling options:

// [0] DefaultModuleErrorHandlers.SYS_ERROR - Creates a System ERROR level log entry for the error. Default.
ESModule.setModuleErrorHandler(DefaultModuleErrorHandlers.SYS_ERROR);
// [1] DefaultModuleErrorHandlers.SYS_WARN - Creates a System ERROR level log entry for the error.
ESModule.setModuleErrorHandler(DefaultModuleErrorHandlers.SYS_WARN);
// [2] DefaultModuleErrorHandlers.SYS_INFO - Creates a System INFO level log entry for the error
ESModule.setModuleErrorHandler(DefaultModuleErrorHandlers.SYS_INFO);
// [3] DefaultModuleErrorHandlers.SYS_DEBUG - Creates a System DEBUG level log entry for the error
ESModule.setModuleErrorHandler(DefaultModuleErrorHandlers.SYS_DEBUG);
// [4] DefaultModuleErrorHandlers.SILENT - Ignores the error
ESModule.setModuleErrorHandler(DefaultModuleErrorHandlers.SILENT);
// [5]DefaultModuleErrorHandlers.THROW_ERROR - Rethtows the error without handling it
ESModule.setModuleErrorHandler(DefaultModuleErrorHandlers.THROW_ERROR);


// With custom error handling:
ESModule.setModuleErrorHandler(function(error) {
  // custom error treatment - formatting, reporting, etc.
});
```
