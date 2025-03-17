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

## Upgrade procedure

[//]: # (Explain in details if something needs to be done)

### *Fix Build on Windows

#### Previous Behavior

The build on windows host failed due to path separator difference on windows an linux hosts.

#### New Behavior

The build on windows is fixed and passes. The license plugin path is corrected as well as methods that are platform dependent.
