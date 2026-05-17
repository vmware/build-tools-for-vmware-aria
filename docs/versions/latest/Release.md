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

### New Class library - Export shape validation for dynamic module loading

A new **Class package** (`com.vmware.pscoe.library:class`) has been added to the build tools repository. Previously available only as a private library, this library is now open-source and integrated into the public build.

The Class library provides:
- **Class.load()**: Core utility for dynamically loading vRO classes with automatic export shape validation
- **Class.validateExportShape()**: Diagnostic function to inspect module exports and detect shape mismatches
- **Comprehensive documentation**: Troubleshooting guide for module path misconfiguration issues

This library is essential for scenarios where actions/modules export objects with named functions instead of constructor functions, and for diagnosing the `[object Object] is not a function` error pattern.

#### Relevant Documentation

- [Class Package README](../../packages/class/README.md) - Full reference with examples and troubleshooting workflows

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

#### Relevant Documentation

- NONE

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

No migration steps are required. Existing `Class.load()` behavior is preserved; export-shape inspection is available through additive diagnostics.

[//]: # (Explain in details if something needs to be done)
