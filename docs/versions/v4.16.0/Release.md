# v4.16.0

## Breaking Changes


## Deprecations



## Features

### *Add type definitions for `o11n-plugin-vum` plugin*

### *Add type definitions for `o11n-plugin-crypto` plugin*

### *Extend type definitions for `o11n-plugin-vc` plugin*

Extend `o11n-plugin-vc` type definitions with:
- VcCustomizationCloudinitPrep
- VcVirtualTPM
- VcVirtualTPMOption
- VcVirtualDeviceDeviceGroupInfo

### *Add Workflow Canvas Item for Switch Component*

This decorator is used to specify a switch item that routes workflow execution to different paths based on the value of a variable or expression. E.g.

```typescript
@SwitchItem({
    cases: [
        { condition: 1, target: "createResource", variable: "operationType", type: "number", comparator: "equals" },
        { condition: 2, target: "updateResource", variable: "operationType", type: "number", comparator: "equals" },
        { condition: 3, target: "deleteResource", variable: "operationType", type: "number", comparator: "equals" }
    ],
    target: "logUnknownOperation"
})
public switchElement(operationType: number) {
    // Switch logic will be generated automatically
}
```

For more details please refer to [Workflows documentation](https://github.com/vmware/build-tools-for-vmware-aria/blob/main/docs/versions/latest/Components/Archetypes/typescript/Components/Workflows.md#switchitem).



## Improvements

### *Add missing `id` Workflow property in `o11n-core` plugin*


## Upgrade procedure

