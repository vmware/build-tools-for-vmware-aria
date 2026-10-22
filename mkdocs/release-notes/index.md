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

### *Change log4j2 configuration to reduce non-relevant logs*

#### Previous Behavior

When running commands, some non-relevant log messages were generated (like thread pool information) due to the default log4j2 configuration, making it harder to focus on important information.

#### New Behavior

The log4j2 configuration has been updated to reduce non-relevant log messages.

## Upgrade procedure

[//]: # (Explain in details if something needs to be done)

No additional steps are required.
