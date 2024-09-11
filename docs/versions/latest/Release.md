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

## Improvements

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

### *Exclude Javascript and Typescript test files during license header generation*

#### Previous Behavior

In most archetypes that generate projects with Javascript or Typescript code license headers are added for all `*.ts` and `*.js` files.

#### Current Behavior

All archetypes that generate projects with Javascript and Typescript code have exclusion rules that skip adding license headers for test files.

### *Re-enable license plugin `<excludes>` configuration inheritance through `pom.xml` property value*

#### Previous Behavior

License plugin `<excludes>` uses hardcoded value defined in the `base-package` -> `pom.xml`.

#### Current Behavior

The default value of license plugin's `<excludes>` configuration can be overwritten by providing `<license.excludes>` property in a projects `pom.xml` -> `<properties>` tag.

### Change default return Object of `Array.from()` Method to be empty array

#### Previous Behavior

In case the object type of the array-like Object does not match any of the expected types (e.g. is a Number) the default case of `Array.from()` returns a clone of the object. This does not match the desired behaviour according to the official documentation.

```js
Array.from(10) // Output: 10
```

#### Current Behavior
In case the object type of the array-like Object does not match any of the expected types (e.g. is a Number) the default case of `Array.from()` returns an empty array.

```js
Array.from(10) // Output: []
```

### *VROES.import from invalid package throws Unknown error*

Fixed an issue with VROES.import() in ecmascript Module.

#### Previous Behavior

VROES import from an invalid module path resulted in Unknown error.
The Unknown error could not be caught in a try/catch block and was not shown in the logs.

#### New Behavior

VROES import from a missing/invalid module path, or import of non-existing elements of a valid module,
result in more detailed errors in the logs. No exceptions are thrown by default (as per the existing behaviour)
but there is an option to alter the error handling behaviour via an optional parameter in import.from().

### Improvements to Item presentation structure in Workflow Schema

#### Previous Behavior

In the schema of Typescript workflows with multiple elements, the element icons were not aligned with the snap-grid.
There was no validation of Default error handler elements.
Misspelled targets were silently redirected to the default End element.
There was no validation for self-targeting or isolated items.

#### Current Behavior

The items in the schema are aligned in rows and columns with the snap-grid.
Validating that Default error handler (if present) is only one.
A warning is logged when a target doesn't match a WF element.
An error is thrown when an elements targets itself or is isolated (not targeted by any other element or the default start element).

#### Related issues

<https://github.com/vmware/build-tools-for-vmware-aria/issues/383>
<https://github.com/vmware/build-tools-for-vmware-aria/issues/318>

### Add missing classes to `o11n-plugin-aria` and add missing methods to the existing classes

#### Previous Behavior

Many classes are missing completely compared with vRO API and some existing classes were missing some methods

#### Current Behavior

The following classes were added to `o11n-plugin-aria`:

- VraEntitiesFinder
- VraFabricNetwork
- VraFlavorProfile
- VraFlavorMapping
- VraFabricFlavor
- VraStorageProfile
- VraProject
- VraUser
- VraZoneAssignment
- VraProjectResourceMetadata
- VraImageProfile
- VraImageMapping
- VraImageMappingDescription
- VraDiskSnapshot
- VraSnapshot
- VraNetworkProfile
- VraNetwork
- VraNetworkInterface
- VraMachine
- VraSaltConfiguration
- VraMachineBootConfig

#### Related issue

<https://github.com/vmware/build-tools-for-vmware-aria/issues/347>

### Better errors for Policy Template, Workflow and Configuration files with incorrect extensions

#### Previous Behavior

A non-descriptive error was thrown when a typescript Policy Template, Workflow or Configuration file was missing either .ts or its respective file extension prefix (.pl, .wf, .conf).

#### Current Behavior

A more specific error is thrown when a file ends in .pl, .wf, .conf and contains a corresponding typescript decorator (@PolicyTemplate, @Workflow, @Configuration), with instructions to add the omitted .ts extension.
A more specific error is thrown when a .ts file contains a @PolicyTemplate, @Workflow or @Configuration decorator without the corresponding file extension prefix (.pl, .wf, .conf).

#### Related issues

<https://github.com/vmware/build-tools-for-vmware-aria/issues/301>

### Fix vGPU property in VcVirtualDeviceBackingInfo class

#### Previous Behavior

The vgpu property in VcVirtualDeviceBackingInfo should be optional, because this class is referenced in other backing classes, which are not related to GPUs.

#### Current Behavior

The vgpu property set to optional.
Duplicated `constructor()` was removed.

#### Related issues

<https://github.com/vmware/build-tools-for-vmware-aria/issues/406>

## Upgrade procedure

[//]: # (Explain in details if something needs to be done)
