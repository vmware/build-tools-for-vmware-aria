# v2.38.1

## Breaking Changes

## Deprecations

## Features

## Improvements
### *Fixed an issue with Arira Pipelines import*
You can now import pipelines with dependencies to other pipelines in their rollback section definition.

### Add missing attribute to VcVirtualDeviceBackingInfo class

There is a single use case where the vGPU can be attached to the VM as a backing device, which is a `VcVirtualDeviceBackingInfo` class. This property doesn't exist and is not documented in API, but it works in Javascript, Python SDK, and PowerCLI.

#### Previous Behavior

The `//@ts-ignore` should be used to skip the error. The compiled JS is working

#### Current Behavior

`spec.deviceChange[ 0 ].device.backing.vgpu = vGPUProfile`  should work as expected


## Upgrade procedure


