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

### *Remove obsolete `vrealize` archetype*

#### Previous Behavior

The "vrealize" archetype is an obsolete archetype that was a mix between vRA 7 archetype and a vRO JS/XML project, as the release 3.0.0 the vRA 7 archetype is removed, hence the logic of this archetype is the same as the "mixed" archetype.

#### New Behavior

The "vrealize" is removed as the logic is covered by the "mixed" archetype.

### *Remove obsolete `vcd-ng-angular8` archetype*

#### Previous Behavior

The "vcd-ng-angular8" archetype is no longer supported since Node JS (a direct dependency of Angular) was upgraded in Build Tools for Aria releases 3.x.x and 4.x.x. This makes the archetype unusable with newer versions and is described in the suggested upgrade path for 4.0.0.

#### New Behavior

The "vcd-ng-angular8" leftover is removed.

### *Improve SSHSession class*

Improve type definitions of SSHSession class functions according to the API. Add missing methods. Fix types. Fix descriptions. Fix class attributes.
  
#### Previous Behavior

The return type of some function definitions in the SSHSession class was not correctly defined and few methods were missing.

#### New Behavior

The following methods were added to the SSHSession class:

- `setConnectTimeout`
- `getConnectTimeout`
- `setTimeout`
- `getTimeout`
- `setKeepAliveInterval`
- `getKeepAliveInterval`
- `setKeepAliveCountMax`
- `getKeepAliveCountMax`

### *Fix Formatting in the Generated Javascript Code*

Apply formatting rules to the generated javascript code from the transpiler, based on the configuration of the prettier plugin.

#### Previous Behavior

The formatting in the generated javascript code was incorrect.

#### New Behavior

The formatting in the generated script is now correct and based on the prettier plugin configuration file (*.prettierrc*).

## Upgrade procedure

[//]: # (Explain in details if something needs to be done)

### *Fix Build on Windows

#### Previous Behavior

The build on windows host failed due to path separator difference on windows and linux hosts.

#### New Behavior

The build on windows is fixed and passes. The license plugin path is corrected as well as methods that are platform dependent.
