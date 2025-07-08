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

### *Support for push of Orchestrator packages to vCF9

Push of different Orchestrator package types to vCF 9 is now supported. This includes:
* actions-package
* xml-package
* typescript-project-all

Since vCF 9 uses vCD based-authentication the username needs to contain domain as well, e.g.:
* <vro.username>admin@System</vro.username> - This results in code push via the Provider administrative user "admin"
* <vro.username>configurationadmin@Classic</vro.username> - This results in code push via the Classic organization administrative user "configurationadmin"

### *Support for push of `vra-ng` packages to Classic organization in vCF9

Push of `vra-ng` package types to vCF 9 Classic organization is now supported.

Since vCF 9 uses vCD based-authentication the username needs to contain domain as well. Currently only push through Classic organization administrator
is supported, e.g.:
* <vrang.username>configurationadmin@Classic</vrang.username>

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

## Upgrade procedure

[//]: # (Explain in details if something needs to be done)
