# v4.18.0

## Breaking Changes


## Deprecations



## Features



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


## Upgrade procedure

