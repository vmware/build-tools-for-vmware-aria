[//]: # (VERSION_PLACEHOLDER DO NOT DELETE)
[//]: # (Used when working on a new release. Placed together with the Version.md)
[//]: # (Nothing here is optional. If a step must not be performed, it must be said so)
[//]: # (Do not fill the version, it will be done automatically)
[//]: # (Quick Intro to what is the focus of this release)

## Breaking Changes

[//]: # (### *Breaking Change*)
[//]: # (Describe the breaking change AND explain how to resolve it)
[//]: # (You can utilize internal links /e.g. link to the upgrade procedure, link to the improvement|deprecation that introduced this/)

## Deprecations

[//]: # (### *Deprecation*)
[//]: # (Explain what is deprecated and suggest alternatives)
[//]: # (Features -> New Functionality)

## Features

[//]: # (### *Feature Name*)
[//]: # (Describe the feature)
[//]: # (Optional But higlhy recommended Specify *NONE* if missing)
[//]: # (#### Relevant Documentation:)
[//]: # (Improvements -> Bugfixes/hotfixes or general improvements)

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

#### Related issue

<https://github.com/vmware/build-tools-for-vmware-aria/issues/297>

## Upgrade procedure

[//]: # (Explain in details if something needs to be done)
