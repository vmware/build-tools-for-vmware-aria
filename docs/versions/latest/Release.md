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
### *Simplified Minimal Infra Setup*

There is a new script now that can be executed and it will setup the minimal infra.

### *New Tool For Simplified Setup*

Added link to `go-btva` that can be used to get a sandbox up and running, fast.

### *Support of notification scenarios by vrang*

Added support for Service broker Notification scenarios to vrang projects.
New scenarios: category added to content.yaml supporting options for none, all, or listed scenarios by name. 
Archetype was changed to contain the new category and a sample scenario.
Packaching of scenarios is supported as well as the following operations:
- vrealize:pull - read scenarios from vRA. Reading customized scenario if available, default configuration otherwise
- vrealize:push - create or update scenario customization.
- vrealize:clean - delete scenario customization and reset to the default.

## Improvements

### Fixed Error Running Unit Tests on Linux and Windows

An error affected unit test execution in versions after 4.3.0 without causing the build to fail.

#### Previous Behavior

An error of the type "Error occurred in unit tests execution:" was shown instead of unit test results.

#### New Behavior

Unit test results are shown as expected.

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

## Upgrade procedure

[//]: # (Explain in details if something needs to be done)
