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

### New Class library - Export shape validation for dynamic module loading

A new **Class package** (`com.vmware.pscoe.library:class`) has been added to the build tools repository. Previously available only as a private library, this library is now open-source and integrated into the public build.

The Class library provides:
- **Class.load()**: Core utility for dynamically loading vRO classes with automatic export shape validation
- **Class.validateExportShape()**: Diagnostic function to inspect module exports and detect shape mismatches
- **Comprehensive documentation**: Troubleshooting guide for module path misconfiguration issues

This library is essential for scenarios where actions/modules export objects with named functions instead of constructor functions, and for diagnosing the `[object Object] is not a function` error pattern.

#### Relevant Documentation

- [Class Package README](../packages/class/README.md) - Full reference with examples and troubleshooting workflows

### Constructor identity diagnostics and export shape validation for module loading issues

#### Previous Behavior

When actions/modules were loaded from inconsistent sources or had export shape mismatches, failures were hard to triage. Developers typically saw runtime `instanceof`-style mismatches, `[object Object] is not a function` errors, or other cryptic messages without direct guidance on what to check.

#### New Behavior

The ECMAScript runtime now emits developer-oriented diagnostics with stable error/warning codes, concise summaries, and actionable remediation steps. Diagnostics cover:

- **Constructor identity mismatches**: When `instanceof` fails due to constructor identity splits from parallel module aliases
- **Export shape validation**: New helper diagnostics make it easier to inspect dynamically loaded module exports when call sites encounter unexpected types (e.g., objects instead of constructors)
- **Module configuration validation**: Consistency checks for module resolution across repeated imports
- **Action invocation errors**: Enhanced error messages with specific hints for module loading issues

New helper functions:
- `__vroes__.validateExportShape(moduleExport, modulePath, exportName)` - Detects when loaded modules have wrong export shape
- `__vroes__.validateModuleConfiguration()` - Checks for unstable module identity issues
- `__vroes__.diagnoseModule(instance)` - Reports constructor registry and identity information

## Upgrade procedure

[//]: # (Explain in details if something needs to be done)

No migration steps are required. Existing `Class.load()` behavior is preserved; export-shape inspection is available through additive diagnostics.
