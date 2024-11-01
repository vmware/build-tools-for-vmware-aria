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

### *Deprecation of vRA 7 archetype*

The vRA 7 Archetype and all related plugins/mojos/code are removed due to the fact that vRA 7 is Out Of Support.

The suggested alternative is to use version 2.44.0 of the toolchain. That is the last version that supports vRA7

### *Deprecation of vRA 7 types*

The `o11n-plugin-vcac` and `o11n-plugin-vcacafe` types are also removed.

### *Deprecated Regional Content*

The Regional content supported in previous versions of Build Tools for Aria has been removed. Unfortunately that part of the build tools never functioned the way we wanted it to, and managing the "infrastructure" tab in Assembler is no longer something we want to do as it contradicts the principles behind the `vra-ng` archetype.

As an alternative, we suggest you use some sort of install workflow to manage them.

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

## Upgrade procedure

[//]: # (Explain in details if something needs to be done)
