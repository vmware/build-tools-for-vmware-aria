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

### *Update type definitions for `o11n-plugin-activedirectory` plugin*

### *Bugfix for Shimming `Object.values`*

#### Previous Behavior

Using `Object.values` would not be replaced by a Shim call and would result in a runtime error when ran inside of vRO's Rhino JavaScript engine.

#### New Behavior

Using `Object.values` now correctly gets replaced by a Shim call (`VROES.Shims.objectValues`), preventing runtime errors when executed inside of vRO's Rhino JavaScript engine.

## Upgrade procedure

[//]: # (Explain in details if something needs to be done)
