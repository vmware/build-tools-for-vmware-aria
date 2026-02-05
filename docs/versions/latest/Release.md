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

### *Add VCF Operations for Logs documentation and archetype content*

### *Update VCF Operations documentation*

### *Add `WorkflowItemInfo` type definition for `o11n-core` plugin*

### *Add type definitions for `o11n-plugin-hms` (vSphere Replication) plugin*

### *Add type definitions for `o11n-plugin-srm` (Site Recovery Manager) plugin*

### *Add type definitions for `o11n-plugin-ovatransfer` (vSphere Replication) plugin*

### *Extend type definitions for `o11n-plugin-vc` plugin*

### *Update vRLI Archetype to be compatible with VCF9 Alert definitions*

New optional field is added to VCF Operations for Logs Alert DTO to make the archetype compatible with VCF9.

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

### *Fix `vrops` archetype project generation*

#### Previous Behavior
Project generation from `vrops` archetype fails because Maven tries to replace a variable inside a defined supermetric due to filtering functionality and `${}` symbol.

#### New Behavior
Project generation from `vrops` archetype executes successfully. Maven configuration is updated to not use variable replacement on `src/main/resources` since content there needs to be directly copied.

### *Fix `vrops` failure to push Alert Definitions with `alertConditions` set*

#### Previous Behavior
Pushing AlertDefinitions with `alertConditions` fails as the ID is not being cleaned up before pushing resulting in the following error thrown by the API:
```text
{"message":"Invalid request... #1 violations found.","validationFailures":[{"failureMessage":"must be null","violationPath":"alertDefinitionStates[0].symptoms.alertConditions[].id"}],"httpStatusCode":400,"apiErrorCode":400}
```

#### New Behavior
`alertConditions` ID is set to null before pushing AlertDefinitions with `alertDefinitions` and the operation completes successfully.
## Upgrade procedure

### *Add missing dependency to `o11n-plugin-aria` type definitions and fix static methods*

[//]: # (Explain in details if something needs to be done)
