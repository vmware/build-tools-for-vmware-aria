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

### *Workflow execution on embedded vRO with local vRA package fix*

Fixed an issue where executing a workflow on an embedded vRO environment with local vRA NG packages failed due to missing authentication properties. Additionally, added error logging when workflow lookups fail to help diagnose such issues more easily.

#### Previous Behavior

When a workflow execution was run after pushing packages to an embedded vRO alongside vRA NG packages, the authentication defaulted incorrectly because `vro_auth=vra` wasn't explicitly set. In addition, when workflow lookups failed, errors were swallowed and a misleading message was emitted.

#### New Behavior

The Installer now automatically tracks vRO embedded properties and defaults `vro_auth` to `vra` if `VRO_EMBEDDED` is true and vRA NG packages are being processed. Error logs have also been added to `isWorkflowExisting()` returning accurate error outputs for missing workflows.
