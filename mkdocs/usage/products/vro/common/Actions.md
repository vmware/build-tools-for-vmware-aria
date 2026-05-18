# Actions

## Overview

{{ products.vro_short_name }} Actions can essentially be viewed as Javascript modules that can be used inside workflows and other actions. This
is where classes, functions, objects, etc. are defined.

!!! note
    `actions` project type supports only `.js` files whilte `typescript` project type supports both `.js` and `.ts`.

## Import

- Only actions that are under `PROJECT_ROOT/src` will be imported to {{ products.vro_short_name }}.
- Only actions that end in `.js` and `.ts` will be imported.
- Action name can contain only letters, numbers and the symbols "_" and "$".
- Action name cannot start with a number.
- Action names must not end in '.helper.js', '.helper.ts' or '_helper.js' - these suffixes are reserved for [Test Helpers](../unit-testing/index.md#test-helpers).

## Action Parameters

Action Parameters are **NOT** mandatory, but they will be taken into consideration if passed.

JSDoc Parameters in {{ products.vro_short_name }} native actions are used when you want to define the parameter type. It will be taken into consideration, otherwise it will be Any. You must follow the way parameter types are defined in {{ products.vro_short_name }}. The argument name must match the regex: `^[a-zA-Z0-9_$]+$`. If it does not match, the parameter is ignored

If the action is a typescript one, then typescript hints will be used instead.

### Action parameter description

You can define descriptions per parameter by adding a `-`.

Example:

```javascript
/**
 * @param {Any} arugment - This is the description
 */
```

### Action parameters with properties

You can define action parameters with properties like:

```javascript
/**
 * @param {Any} args
 * @param {string} args.url
 */
(function (args) {
  return args.url;
})
```

In this example, the args.url will be ignored.

[JSDoc documentation](https://jsdoc.app/tags-param.html#parameters-with-properties)

### Unsupported Parameters

- Optional `@param {string} [optional=123]` or `@param {string=} optional`
- Union type `@param {(string|string[])} union`
- Repeated parameter `@param {...number} num`

## Action Return Type

Action return type is **NOT** mandatory, but it will be taken into consideration if passed.

You can add `@return` or `@returns` to define the return type of the action, or alternatively if the action is typescript,
the typescript hints will be used.

## Examples

!!! note
    Typescript examples are only valid for `typescript` project type.

### Native {{ products.vro_short_name }} Action

```javascript
/**  
 * @param {Any} args  
 * @param {number} test  
 */  
(function (args, test, willBeAnyType) {
   return args.url;
});

//......

// Will be transpiled to
return args.url;
```

In this example:

- Param `args` of type Any
- Param `test` of type number
- Param `willBeAnyType` of type Any
- Return type: Any

### Typescript {{ products.vro_short_name }} Action

``` typescript
/**
 * @param {string} name
 * @returns {string} Generated timespan name
 */
(function (name: string): string {
    return `${name} ${System.formatDate(new Date(), "dd.MM.yyyy HH:mm:ss")}`;
})

//......

// Will be transpiled to
return "".concat(name, " ").concat(System.formatDate(new Date(), "dd.MM.yyyy HH:mm:ss"));
```

### {{ products.vro_short_name }} Action containing Typescript class and an enumeration

``` typescript
export class VirtualMachine {
    private readonly vm: VcVirtualMachine;

    constructor(vm: VcVirtualMachine) {
        this.vm = vm;
    }

    public getPowerState(): string {
        return this.vm.runtime.powerState.name;
    }

    public static getAll(): VirtualMachine[] {
        return VcPlugin.getAllVirtualMachines([], "").map(vm => new VirtualMachine(vm));
    }
}

export enum PowerState {
    POWERED_ON = "poweredOn",
    POWERED_OFF = "poweredOff",
    SUSPENDED = "suspended"
}

//......

// Will be transpiled to
var exports = {};
var VirtualMachine = /** @class */ (function () {
    function VirtualMachine(vm) {
        this.vm = vm;
    }
    VirtualMachine.prototype.getPowerState = function () {
        return this.vm.runtime.powerState.name;
    };
    VirtualMachine.getAll = function () {
        return VcPlugin.getAllVirtualMachines([], "").map(function (vm) { return new VirtualMachine(vm); });
    };
    return VirtualMachine;
}());
exports.VirtualMachine = VirtualMachine;
var PowerState = {};
exports.PowerState = PowerState;
var PowerState = exports.PowerState;
(function (PowerState) {
    PowerState["POWERED_ON"] = "poweredOn";
    PowerState["POWERED_OFF"] = "poweredOff";
    PowerState["SUSPENDED"] = "suspended";
})(PowerState || (PowerState = {}));
return exports;
```
