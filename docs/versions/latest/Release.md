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
### Support new v2 API for vRLI versions 8.8+ (Experimental)

#### Previous Behavior
When working with vRLI content, the package manager was not considering the version of the vRLI product
and was using the deprecated v1 API which is no longer maintained.

#### New Behavior
Managing of vRLI content is done using the appropriate API for the vRLI product - v1 for server version 8.7 and below
and v2 for newer versions.

#### Relevant Documentation:
**NONE**


## Upgrade procedure:
[//]: # (Explain in details if something needs to be done)

[//]: # (## Changelog:)
[//]: # (Pull request links)
