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

### *Polyglot actions support VRO custom environments*

Support for custom environments was added to the Polyglot vRO projects.
The new features include:
- Custom environment definition may be added to vRO polyglot projects and included in import package
  - Custom environment definitions are stored in src/resources/environments/{environmentName}.json files
- Polyglot action can be set with a custom environment
  - The runtime and environment information is kept in two parameters: platform.runtime (existing) and platform.environment
  - If only platform.runtime is provided (platform.environment is null, empty string, or not provided) the action will compile and import to vRO as a zip bundle action
  - If both platform.runtime and platform.environment are provided, the action will be imported to vRO as a script action
    - The script will include only code from handler.ts|handler.py|handler.ps1, any other source files are ignored
    - Any module dependencies should be configured as part of custom environment definition, dependencies defined in the action files will not be packaged
- Sample vRO project from polyglot archetype was extended with: 
  - Empty platform.environment added to polyglot.json files
  - Sample environment definition src/resources/environments/environment.json.sample

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

### *Polyglot runtime option restored and fixed*

Runtime option powershell:7.4 was causing a vropkg error and was removed in a previous release.
Now the issue is fixed and the option is restored and available to use.

### *Polyglot not producing correct bundle fixed*

An issue is fixed that handler files were missing from polyglot package.

## Upgrade procedure

[//]: # (Explain in details if something needs to be done)
