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

### Fixed pulling of vROps dashboards as managed content
#### Previous Behaviour

When pulling a list of vROps dashboards defined in the content.yaml, the automation was failing with a hidden error message.
The underlying cause was that the directory in which the data is exported didn't exist and wasn't created by the automation.
#### New Behaviour

When pulling a list of vROps dashboards defined in the content.yaml, the automation exports the content successfully along with
their metadata files.

## Upgrade procedure:
[//]: # (Explain in details if something needs to be done)

[//]: # (## Changelog)
[//]: # (Pull request links)
