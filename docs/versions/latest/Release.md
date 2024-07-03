[//]: # "VERSION_PLACEHOLDER DO NOT DELETE"
[//]: # "Used when working on a new release. Placed together with the Version.md"
[//]: # "Nothing here is optional. If a step must not be performed, it must be said so"
[//]: # "Do not fill the version, it will be done automatically"
[//]: # "Quick Intro to what is the focus of this release"

## Breaking Changes

[//]: # "### *Breaking Change*"
[//]: # "Describe the breaking change AND explain how to resolve it"
[//]: # "You can utilize internal links /e.g. link to the upgrade procedure, link to the improvement|deprecation that introduced this/"

## Deprecations

[//]: # "### *Deprecation*"
[//]: # "Explain what is deprecated and suggest alternatives"
[//]: # "Features -> New Functionality"

## Features

[//]: # "### *Feature Name*"
[//]: # "Describe the feature"
[//]: # "Optional But highly recommended Specify *NONE* if missing"
[//]: # "#### Relevant Documentation:"
[//]: # "Improvements -> Bugfixes/hotfixes or general improvements"

### [vrotsc] Enhanced Workflows to Support more Canvas Elements

All of the `wf.ts` files now have the ability to add method Decorators to their functions giving you the ability
to add more Canvas elements to your workflows.

The following Decorators are available:

- `@Item({target:'test', exception: ''})` - Specifies a scriptable task. `target` will be the name of the next in line item, `exception` is not implemented yet.
- `WaitingTimerItem({target:'test'})` - Specifies a waiting timer. `target` will be the name of the next in line item. Expects an `@In`
  parameter with the name of the waiting timer.
- `@DecisionItem({target:'test', else: 'otherTest'})` - Specifies a decision item. `target` will be the name of the next in line item, `else` will be
  the name of the next in line item if the decision is false.
- `@RootItem()` - This is a meta decorator. Add this to whichever function you want to be the entry point of the workflow.

Example:

```ts
import {
  Workflow,
  Out,
  In,
  Item,
  RootItem,
  DecisionItem,
  WaitingTimerItem,
} from "vrotsc-annotations";

@Workflow({
  name: "Example Waiting Timer",
  path: "VMware/PSCoE",
  attributes: {
    waitingTimer: {
      type: "Date",
    },
    counter: {
      type: "number",
    },
  },
})
export class HandleNetworkConfigurationBackup {
  @DecisionItem({
    target: "waitForEvent",
    else: null, // null means end
  })
  public decisionElement(waitingTimer: Date) {
    return waitingTimer !== null;
  }

  @Item({
    target: "decisionElement",
    exception: "",
  })
  public execute(@Out @In waitingTimer: Date, @Out @In counter: number): void {
    if (!counter) {
      counter = 0;
    }

    counter++;
    if (counter < 2) {
      const tt = Date.now() + 5 * 1000;
      waitingTimer = new Date(tt);
    } else {
      waitingTimer = null;
    }

    System.log("Counter: " + counter);
    System.log("Waiting Timer: " + waitingTimer);
  }

  @Item({
    target: "execute",
    exception: "",
  })
  @RootItem()
  public start() {
    System.log("Starting workflow");
  }

  @WaitingTimerItem({
    target: "execute",
  })
  public waitForEvent(@In waitingTimer: Date) {}
}
```

#### `@Item`

This decorator is used to specify a scriptable task.

##### Supported Parameters

- `target` - The name of the next in line item. If this is set to `end`, it will point to the end of the workflow.
  If this is set to `null`, it will point to the next item or if none, the end of the wf.
  If this is set to a string, but it does not exist in the workflow, it will point to the end of the wf.
- `exception` - **Not implemented yet**

#### `@WaitingTimerItem`

This decorator is used to specify a waiting timer.

##### Supported Parameters

- `target` - The name of the next in line item. Same as `@Item`.
  This decorator expects an `@In` parameter with the name of the waiting timer. If one isn't added, the workflow will not work.

#### `@DecisionItem`

This decorator is used to specify a decision item.

##### Supported Parameters

- `target` - The name of the next in line item. Same as `@Item`.
- `else` - The name of the next in line item if the decision is false. If this is set to `end`, it will point to the end of the workflow.
  If this is set to `null`, it will point to the next item or if none, the end of the wf.

#### `@RootItem`

This is a meta decorator. Add this to whichever function you want to be the entry point of the workflow.

#### Backwards Compatibility

This feature is backwards compatible with the previous version of the `vrotsc` package. If no decorators are added, will assume an empty `@Item`.

#### Relevant Documentation

- [Workflows](./Components/Archetypes/typescript/Components/Workflows.md)

## Improvements

[//]: # "### *Improvement Name* "
[//]: # "Talk ONLY regarding the improvement"
[//]: # "Optional But highly recommended"
[//]: # "#### Previous Behavior"
[//]: # "Explain how it used to behave, regarding to the change"
[//]: # "Optional But highly recommended"
[//]: # "#### New Behavior"
[//]: # "Explain how it behaves now, regarding to the change"
[//]: # "Optional But highly recommended Specify *NONE* if missing"
[//]: # "#### Relevant Documentation"

### VROTSC Upgrade the ts version from 3.8.3 to 5.4.5

## Improvements

### Add support for global scope property-group export/import

Fix issue #250
Property groups of global scope can now be imported with vrealize:push

#### Previous Behavior

On export projectId and orgId values are removed from property-group data to facilitate import into a different VRA system.
On import projectId and orgId values are added to property-group data unconditionally. This prevented importing property-groups with global scope.

#### Current Behavior

On export projectId and orgId values are now saved with the rest of the property-group data.
On import orgId is always overridden. The projectId is overridden only if it already existed in the json file.
Thus property groups with global scope which do not have projectId can now be created or updated via vrealize:push command.

### `string[]`, `Test[]` and such are now supported in the @params documentation

Fix Issue #278
Wider support for types in the @params documentation

#### Previous Behavior

The @params documentation did not support `string[]`, `Test[]` and such and was not transforming it at all and leaving it as is. This was causing linting issues

#### Current Behavior

The @params documentation now supports `string[]`, `Test[]` and such and transforms it to `Array/string`, `Array/Test` and such

### Update the package.json template for generating abx actions

Fix Issue #220

#### Previous Behavior

The package.json template for generating abx actions was missing some of the recently implemented parameters.

#### Current Behavior

The package.json template for generating abx actions now contains the recently implemented parameters: base, memoryLimitMb, timeoutSec, provider and abx (inputSecrets, inputConstants, etc.).

### Add missing types to AD Plugin

Fix Issue #251

#### Previous Behavior

AD types were not implemented

#### Current Behavior

AD types were added

### Add missing attribute to SSHSession

Add `soTimeout` attribute to `SSHSession`

#### Previous Behavior

This attribute was missing

#### Current Behavior

This attributed is added

[//]: # (### *Improvement Name* )
[//]: # (Talk ONLY regarding the improvement)
[//]: # (Optional But higlhy recommended)
[//]: # (#### Previous Behavior)
[//]: # (Explain how it used to behave, regarding to the change)
[//]: # (Optional But higlhy recommended)
[//]: # (#### New Behavior)
[//]: # (Explain how it behaves now, regarding to the change)
[//]: # (Optional But higlhy recommended Specify *NONE* if missing)
[//]: # (#### Relevant Documentation:)

### Updated documentation to specify Java 17 as the required version

#### Previous Behavior

Required Java version mentioned in the documentation was Java 8.

#### Current Behavior

Required Java version updated to Java 17 in the documentation.

### Add missing properties to VcComputeResourceConfigSpec and related class definitions in vRO interfaces

#### Previous Behavior

The `VcComputeResourceConfigSpec` class is missing a few attributes and related class interfaces.

#### Current Behavior

The `enableConfigManager`, `maximumHardwareVersionKey` and `desiredSoftwareSpec` attributes are added to the `VcComputeResourceConfigSpec` class interface. Related class interfaces for `VcDesiredSoftwareSpec`, `VcDesiredSoftwareSpecBaseImageSpec`, `VcDesiredSoftwareSpecVendorAddOnSpec` and `VcDesiredSoftwareSpecComponentSpec` are added vCenter plugin interfaces.

#### Related Issue

<https://github.com/vmware/build-tools-for-vmware-aria/issues/297>

## Upgrade Procedure

[//]: # "Explain in details if something needs to be done"
