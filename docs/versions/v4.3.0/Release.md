# v4.3.0

## Breaking Changes


## Deprecations



## Features



## Improvements


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


### *Fix Build on Windows

#### Previous Behavior

The build on windows host failed due to path separator difference on windows and linux hosts.

#### New Behavior

The build on windows is fixed and passes. The license plugin path is corrected as well as methods that are platform dependent.
