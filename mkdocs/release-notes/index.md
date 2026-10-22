[//]: # (VERSION_PLACEHOLDER DO NOT DELETE)
[//]: # (Used when working on a new release.)
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

### *Fix VCFA pull/push failure for provider admin users targeting a specific organization*

#### Previous Behavior

When a provider admin user (e.g. `admin@System`) executed a pull or push for a `vcfa-all-apps` project, the configured `org.name` was not passed to the VCF Automation authentication. As a result the acquired token was scoped to the `System` organization instead of the target organization and the operation failed with an error similar to: `403 Forbidden from GET https://project-service.prelude.svc.cluster.local:8080/rbac-service/api/auth-context`.

#### New Behavior

The `org.name` property is passed to the VCF Automation authentication, so a provider admin user acquires a token for the configured organization, the same way as for `vra-ng` projects on VCF 9.

### *Add Validation of local content.yml File Items during Push for VCF Ops for Logs, VCFA VM Apps and VCF Ops*

#### Previous Behavior

VCF Ops for Logs (VRLI) and VCFA VM Apps (CS) project types were pushing all local content and ignoring content.yaml, while VCF Ops (VROPS) and VCFA All Apps push only what's explicitly defined in `content.yaml`. This inconsistency made it impossible to control what content gets deployed.

#### New Behavior

Added bidirectional content.yaml validation that throws a clear error if local files and content.yaml don't match. Both the package Maven goal and the Installer (import path) enforce this.

## Upgrade procedure

[//]: # (Explain in details if something needs to be done)

No additional steps are required.
