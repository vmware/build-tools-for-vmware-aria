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

### *Add type definitions for `o11n-plugin-vcfa` plugin*

### *Add support for Node:22 runtime environment for `polyglot` projects*

## Improvements

### *Bugfix for pushing pushing policies in Aria Operations*
#### Previous Behavior
It is not possible to push policies if they have missing dependencies (alert definitions, symptom definitions, recommendations) on the target server
#### New Behavior
There is an error message which shows the user when BTVA is trying to import policies if they have missing dependencies (alert definitions, symptom definitions, recommendations) on the target server

### *Improve TypeScript workflow decorator documentation*

#### Previous Behavior

Workflow decorator typings in `vrotsc-annotations` provided limited IDE documentation, and the written workflow documentation could drift from the actual decorator/type surface (for example, older decorator naming and stale parameter lists).

#### New Behavior

Workflow-related decorators and configuration types now include richer, more consistent JSDoc (including readable links), and the latest workflow documentation is aligned with the typings (updated decorator naming and supported parameters).

### *Bugfix for failure to find imported module of Powershell 7 polyglot action*

#### Previous Behavior

Executing a PowerShell 7 polyglot action with an imported module results in an error of the type "The specified module '...' was not loaded because no valid module file was found in any module directory."

#### New Behavior

When building a PowerShell polyglot action with imported modules the build process downloads modules to out/Modules/ via Save-Module and the ZIP bundle now correctly includes all files from Modules/ with proper folder structure.

## Upgrade procedure

[//]: # (Explain in details if something needs to be done)
