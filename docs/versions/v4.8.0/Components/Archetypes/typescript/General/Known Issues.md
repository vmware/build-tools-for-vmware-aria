# Known Issues

A list of known typescript archetype issues.

## Table Of Contents

1. [Array functions are not transpiled to vRO code](#array-functions-are-not-transpiled-to-vro-code)

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
