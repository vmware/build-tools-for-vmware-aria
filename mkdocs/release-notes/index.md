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

### *Add support for 9.0.0 PowerShell runtime environments  for `polyglot` projects*

Added support for:

- powercli:9.0.0-powershell-7.4
- powercli:9.1.1-powershell-7.6

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

### Fix parsing of custom JSDoc `@tag` tag in Orchestrator Actions

#### Previous Behavior

If an action contains the `@tag` JSDoc tag the resulting package throws 400 error during package import.

#### New Behavior

If an action contains the `@tag` JSDoc tag the package is importable in Orchestrator and the provided `@tag` value is added to the corresponding Actions's Tags.

### *Add Support for CSS Form Styles for Resource Actions*

#### Previous Behavior

Resource actions don't generate separate CSS file for form styles.

#### New Behavior

Add support for CSS file for form styles for resource actions similar to the request forms for `vra-ng` projects.

## Upgrade procedure

[//]: # (Explain in details if something needs to be done)
