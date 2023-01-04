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
## Features:
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

### Updated vro unit test module

#### Previous Behavior
when executing unit tests, an error message was displayed

```
    Browserslist: caniuse-lite is outdated. Please run
    ...
``` 

#### New Behavior
The package browserslist was updated and the error message will not appear

### Introduced autogeneration for docs/versions/latest/General/Cheatsheets

#### Previous Behavior
plugin arguments were not up to date

#### New Behavior
plugin arguments will be generated during version release

### Installer fails to import a property group if a different project scope is provided
Added a warning that a property group would not be imported to vRA when such property group already exists in another project.
Added a fix for assigning a property group to the provided project (in the configuration file) during a first-time import of the property gtoup to vRA.

#### Previous Behavior
When running the installer script to import property groups to vRA, it fails to import the property group if it already exists in vRA and is bound to a different project. Another issue was that importing a non-existent in vRA property group would be created without a project even if one is provided in the configuration properties.

#### New Behavior
When iporting a property group that already exists in vRA to another project, a warning is shown that the import will not be performed. When importing a non-existent property group, it gets the correct project id assigned.

#### Relevant Documentation:
**NONE**


## Upgrade procedure:
[//]: # (Explain in details if something needs to be done)

[//]: # (## Changelog:)
[//]: # (Pull request links)
