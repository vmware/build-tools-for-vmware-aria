[//]: # (VERSION_PLACEHOLDER DO NOT DELETE)
[//]: # (Used when working on a new release)
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

### *Migrate documentation to mkdocs*

Documentation approach is changed to use `github-pages` with `mkdocs` (for the documentation itself) and `mike` (for version management). All new documentation must be placed in an appropriate location under the `mkdocs` folder. The old `docs` folder must not be used anymore and is to be archived.

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

### *Workflow execution on embedded vRO with local vRA package fix*

Fixed an issue where executing a workflow on an embedded vRO environment with local vRA NG packages failed due to missing authentication properties. Additionally, added error logging when workflow lookups fail to help diagnose such issues more easily.

#### Previous Behavior

When a workflow execution was run after pushing packages to an embedded vRO alongside vRA NG packages, the authentication defaulted incorrectly because `vro_auth=vra` wasn't explicitly set. In addition, when workflow lookups failed, errors were swallowed and a misleading message was emitted.

#### New Behavior

The Installer now automatically tracks vRO embedded properties and defaults `vro_auth` to `vra` if `VRO_EMBEDDED` is true and vRA NG packages are being processed. Error logs have also been added to `isWorkflowExisting()` returning accurate error outputs for missing workflows.

## Upgrade procedure

[//]: # (Explain in details if something needs to be done)
