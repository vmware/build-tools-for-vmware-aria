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

### *Fixed: Failed to push TS project to Orchestrator 9.0 #836*

#### Previous Behavior
Push and pull operations failed with a `...push failed: Expected to find an object with property ['versionInfo']...` when authenticating against standalone Orchestrator 9.
Reason the version of VCF Automation is checked regardless the selected auth type. In environments with standalone Orchestrator the error above occures.

#### New Behavior
Authentication strategies have been updated to better support standalone Orchestrator 9 environments.

* **Basic Auth:** When `<vro.auth>basic</vro.auth>` is configured in `settings.xml`, the authentication strategy now executes without performing a VCFA version check.
* **vRA Auth:** The VCFA version check is now strictly limited to configurations using `<vro.auth>vra</vro.auth>`.

### *Add missing `URI` type definition in `o11n-plugin-hms`*

#### Previous Behavior
Build of an Orchestrator project type might fail if the `URI` type definitions are not available in local Maven repository.

#### New Behavior
Build of Orchestrator projects completes successfully.

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
