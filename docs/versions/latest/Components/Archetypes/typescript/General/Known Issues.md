# Known Issues

A list of known typescript archetype issues.

## Table Of Contents

1. [Array functions are not transpiled to vRO code](#array-functions-are-not-transpiled-to-vro-code)
2. [VROTSC config](#vrotsc-config)

### Array functions are not transpiled to vRO code

If an `Array` variable is not explicitly defined or recognized as such, the transpiler does not convert the TS-specific `Array` prototype functions (`find()`, `some()`, etc.) into vRO compatible code which results in a runtime error.

Consider the following example:

```javascript
const testArray = [1, 2, 3, 4, 5];

let objectsForIteration = null;
if (true) {
    objectsForIteration = testArray;
}

// Fails to transpile correctly because type is resolved to "any"
const res = objectsForIteration.find(o => o === 2)
System.log(res + "")
```

The code above is be converted to the following vRO code, which during execution throws the error `TypeError: Cannot find function find in object 1,2,3,4,5.`

```javascript
var testArray = [1, 2, 3, 4, 5];

var objectsForIteration = null;
if (true) {
    objectsForIteration = testArray;
}

// Fails to transpile correctly because type is resolved to "any"
var res = objectsForIteration.find(function (o) { return o === 2; });
System.log(res + "");
```

Proper variable typization solves this problem. Let's revisit the example but this time we will explicitly define the type of values that we expect the `objectsForIteration` variable to receive.

```javascript
const testArray = [1, 2, 3, 4, 5];

let objectsForIteration: Array<number> = null;
if (true) {
    objectsForIteration = testArray;
}

// Transpiles correctly because of explicit typization
const res = objectsForIteration.find(o => o === 2)
System.log(res + "")
```

The code is transpiled correctly to vRO code and executes successfully.

```javascript
var __global = System.getContext() || (function () {
    return this;
}).call(null);
var VROES = __global.__VROES || (__global.__VROES = System.getModule("com.vmware.pscoe.library.ecmascript").VROES());
var testArray = [1, 2, 3, 4, 5];

var objectsForIteration = null;
if (true) {
    objectsForIteration = testArray;
}
// Transpiles correctly because of explicit typization
var res = VROES.Shims.arrayFind(objectsForIteration, function (o) { return o === 2; });
System.log(res + "");
```

#### How to prevent this issue

The recommended configuration to prevent such issues is to set the `strictNullChecks` property to `true` in your project's local `tsconfig.json` file. This allows for a type hint warning to be displayed in case the type is not explicitly defined.

> **NOTE!** The warning messages received are optional and are NOT blocking package build and push operations.

Let's revisit the example once again with `strictNullChecks` enabled:

```javascript
const testArray = [1, 2, 3, 4, 5];

let objectsForIteration = null;
if (true) {
    // The following warning message is displayed: Type 'number[]' is not assignable to type 'null'.ts(2322)
    objectsForIteration = testArray;
}

// The following warning message is displayed: 'objectsForIteration' is possibly 'null'.ts(18047)
const res = objectsForIteration.find(o => o === 2)
System.log(res + "")
```

Sample `tsconfig.json`:

```javascript
{
  "compilerOptions": {
    "target": "ES5",
    "module": "CommonJS",
    "moduleResolution": "Node",
    "lib": [
      "ES5",
      "ES2015.Core",
      "ES2015.Collection",
      "ES2015.Iterable",
      "ES2015.Promise",
      "ES2017.String"
    ],
    "experimentalDecorators": true,
    "strictNullChecks": true
  }
}
```

### VROTSC config

The presence of a `tsconfig.json` file in a directory indicates that the directory is the root of a TypeScript project and is being read by the code editor for *autocompletion*. Modifying `tsconfig.json` in the context of `vrotsc` can affect only the code editor autocompletion.

Because of the nature of extending the typescript compiler and the specific runtime of Orchestrator, `vrotsc` package has a default `tsconfig` that is applied for all  `typescript-project-all` projects.

`/typescript/vrotsc/src/compiler/config.ts`

e.g.

```javascript
  return {
   module: ts.ModuleKind.ESNext,
   moduleResolution: ts.ModuleResolutionKind.NodeJs,
   target: ts.ScriptTarget.ES5,
   lib: [
    "lib.es5.d.ts",
    "lib.es2015.core.d.ts",
    "lib.es2015.collection.d.ts",
    "lib.es2015.iterable.d.ts",
    "lib.es2015.promise.d.ts",
    "lib.es2017.string.d.ts",
    "lib.es2016.array.include.d.ts"
   ],
   strict: false,
   allowUnreachableCode: true,
   stripInternal: false,
   removeComments: false,
   experimentalDecorators: true,
   emitDecoratorMetadata: true,
   importHelpers: true,
   suppressOutputPathCheck: true,
   rootDir: rootDir,
   baseUrl: rootDir,
   allowJs: true,
   declaration: true,
   sourceMap: true,
   declarationMap: false,
  // verbatimModuleSyntax: true,
  ignoreDeprecations: "5.0"
};
```
