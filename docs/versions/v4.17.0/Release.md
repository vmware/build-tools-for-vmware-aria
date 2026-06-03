# v4.17.0

## Breaking Changes


## Deprecations



## Features



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

### *Fix Push and Pull with refresh token only*

#### Previous Behavior
Push and pull operations failed with a `NullPointerException` when authenticating using only a refresh token:
```text
Execution default-cli of goal com.vmware.pscoe.maven.plugins:vra-ng-package-maven-plugin:4.16.1:pull failed.: NullPointerException
```

#### New Behavior
Push and pull operations now complete successfully when using a refresh token. Additionally, sensitive token values are now automatically redacted from the execution logs.

## Upgrade procedure

### *Add missing dependency to `o11n-plugin-aria` type definitions and fix static methods*

