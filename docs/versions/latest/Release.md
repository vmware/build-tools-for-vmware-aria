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

### *vRA ContentSharingPolicy not pulled/pushed correctly with v2.39.0 and v2.40.0*

#### Previous Behavior

The `vRA ContentSharingPolicy` was not pulled/pushed correctly with v2.39.0 and v2.40.0 since filtering wasn't calling the resolve
method that would resolve the naming.

#### New Behavior

The `vRA ContentSharingPolicy` is now pulled/pushed correctly with v2.39.0 and v2.40.0 since filtering is now calling the resolve.

### *Better error handling when starting vro workflow*

#### Previous Behavior
When starting a vro workflow, the error handling was not very clear and the error message was not very helpful when the params were missing the types.

#### New Behavior
The error handling has been improved and the error message is more helpful when the params are missing the types.

## Upgrade procedure

[//]: # (Explain in details if something needs to be done)
