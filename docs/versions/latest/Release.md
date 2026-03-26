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

#### Previous Behavior

Running vropkg on Windows for packages with many elements fails with:

Error: EMFILE: too many open files

#### New Behavior

The Promise now resolves only when the `output` stream emits the `'close'` event, ensuring all data is written and file handles are released, hence avoiding the error for a package with many elements.

## Upgrade procedure

[//]: # (Explain in details if something needs to be done)
