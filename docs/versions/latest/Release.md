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

### *Exclude Javascript and Typescript test files during license header generation*

#### Previous Behavior

In most archetypes that generate projects with Javascript or Typescript code license headers are added for all `*.ts` and `*.js` files.

#### Current Behavior

All archetypes that generate projects with Javascript and Typescript code have exclusion rules that skip adding license headers for test files.

#### Related issue

https://github.com/vmware/build-tools-for-vmware-aria/issues/379

### *Re-enable license plugin `<excludes>` configuration inheritance through `pom.xml` property value *

#### Previous Behavior

License plugin `<excludes>` uses hardcoded value defined in the `base-package` -> `pom.xml`.

#### Current Behavior

The default value of license plugin's `<excludes>` configuration can be overwritten by providing `<license.excludes>` property in a projects `pom.xml` -> `<properties>` tag.

## Upgrade procedure

[//]: # (Explain in details if something needs to be done)
