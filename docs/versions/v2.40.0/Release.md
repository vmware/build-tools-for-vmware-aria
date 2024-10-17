# v2.40.0

## Breaking Changes


## Deprecations


## Features


### Support of ordering of Aria Operations (vROPs) policies by priority

Aria Operations (vROPs) since version 8.17.0 supports ordering policies by priority. If the target server has Aria Operations version 8.17 and later the policies will be ordered by priority during pushing of policies to the server as they appear in the content.yaml file with the first policy with highest priority and the last one with lowest priority, hence making possible to control the priority of the policies in the configuration file.
Note that the default policy will not be part of priority order list due to limitation of Aria Operations product.

Example content.yaml file with policies ordered by priority.

```yaml
policy:
- "Policy 1"
- "Policy 2"
- "Policy 3"
```

If the Aria Operations server is 8.17.0 and later during push the policies will be ordered by priority as follows:

1. "Policy 1" (top priority)
2. "Policy 2"
3. "Policy 3" (lowest priority)

#### Backwards Compatibility

This feature is backwards compatible with the previous version of the `Aria Operation (vROPs)` servers. If the `vROPs` server is older then the policies will not be ordered by priority.

### Support of project scope / organization during import of content sharing policies


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


### VROTSC Upgrade the ts version from 3.8.3 to 5.4.5

## Improvements

### Add support for global scope property-group export/import

Property groups of global scope can now be imported with vrealize:push

#### Previous Behavior

On export projectId and orgId values are removed from property-group data to facilitate import into a different VRA system.
On import projectId and orgId values are added to property-group data unconditionally. This prevented importing property-groups with global scope.

#### Current Behavior

On export projectId and orgId values are now saved with the rest of the property-group data.
On import orgId is always overridden. The projectId is overridden only if it already existed in the json file.
Thus property groups with global scope which do not have projectId can now be created or updated via vrealize:push command.

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

### Cloud Template folder name and details.json->name mismatch issue fixed

#### Previous Behavior

- When the Cloud-Template folder name does not match the name from details.json, folder name is used for validating if the record exists on the server and the name from details.json is used for creating/updating the cloud template on server.

#### Current Behavior

- When the Cloud-Template folder name does not match the name from details.json, now an error throwing explaining the mismatch.
- The name from details.json is now used for both validating and creating/updating cloud template on the server.

## Upgrade Procedure

