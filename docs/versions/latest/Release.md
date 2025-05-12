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

Polyglot actions can be set with custom environments in vRO.
Two new features were added to vRO polyglot projects:
- Polyglot action can be set with a custom environment. In addition to platform.runtime, a new parameter platform.environment may be provided containing environment id. 
- Custom environment definition may be added to vRO polyglot projects and included in import package. Custom environment definitions must be stored in src/resources/environments/{environmentName}.json files.

Sample vRO project from polyglot archetype was extended with:
- empty platform.environment added to polyglot.json files
- sample environment definition src/resources/environments/environment.json.sample

### *Polyglot runtime option restored and fixed*

Runtime option powershell:7.4 was causing a vropkg error and was removed in a previous release.
Now the issue is fixed and the option is restored and available to use.

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

## Upgrade procedure

[//]: # (Explain in details if something needs to be done)
