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

### *Shim for Object.entries*

Added a shim for `Object.entries`, which is not natively supported by the current version of the JavaScript Engine Rhino in vRO.

#### Relevant Documentation

[MDN Object.entries](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Object/entries)

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

### *Improve TypeScript workflow decorator documentation*

#### Previous Behavior

Workflow decorator typings in `vrotsc-annotations` provided limited IDE documentation, and the written workflow documentation could drift from the actual decorator/type surface (for example, older decorator naming and stale parameter lists).

#### New Behavior

Workflow-related decorators and configuration types now include richer, more consistent JSDoc (including readable links), and the latest workflow documentation is aligned with the typings (updated decorator naming and supported parameters).

### *Bugfix for failure to find imported module of Powershell 7 polyglot action*

#### Previous Behavior

Executing a PowerShell 7 polyglot action with an imported module results in an error of the type "The specified module '...' was not loaded because no valid module file was found in any module directory."

#### New Behavior

When building a PowerShell polyglot action with imported modules the build process downloads modules to out/Modules/ via Save-Module and the ZIP bundle now correctly includes all files from Modules/ with proper folder structure.

### Improve healthcheck script, add healthcheck script and instructions for Windows

### *Update type definitions for `RESTOperation` from `o11n-plugin-rest` plugin*

### *Extend type definitions for `o11n-plugin-vc` plugin*

Update `VcToolsConfigInfo` type definition.
Add the following new definitions:
* `VcVirtualPCIPassthroughAllowedDevice`
* `VcVirtualPCIPassthroughDynamicBackingInfo`
* `VcVirtualPCIPassthroughDvxBackingInfo`
* `VcVirtualPCIPassthroughDvxBackingOption`
* `VcVirtualPCIPassthroughDynamicBackingOption`

### *Update type definitions for `FileReader` from `o11n-core` plugin*

Add `close()` method.

### *Update type definitions for `VraInfrastructureClient` from `o11n-plugin-aria` plugin*

### *Bugfix for Shimming `Object.values`*

#### Previous Behavior

Using `Object.values` would not be replaced by a Shim call and would result in a runtime error when ran inside of vRO's Rhino JavaScript engine.

#### New Behavior

Using `Object.values` now correctly gets replaced by a Shim call (`VROES.Shims.objectValues`), preventing runtime errors when executed inside of vRO's Rhino JavaScript engine.

### *Bugfix for too many opened files on Windows*

#### Previous Behavior

Running vropkg on Windows for packages with many elements fails with:

Error: EMFILE: too many open files

#### New Behavior

The Promise now resolves only when the `output` stream emits the `'close'` event, ensuring all data is written and file handles are released, hence avoiding the error for a package with many elements.

## Upgrade procedure

### *Fix `vrops` issue 1108 failing to push alert Definitions with alertConditions set*
#### Previous Behavior
Pushing AlertDefinitions from one environment to anther is failing becuause of wrong DTO structure. Updating alert definitions (PUT request) taken from one environment to another was failing due to mismatch of the alert conditions id's
#### New Behavior
Fixed AlertDefinitionDTO.java
Before pushing the same alert condition again the id's of the alert condition are set to null.

[//]: # (Explain in details if something needs to be done)
