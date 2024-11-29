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

### *Ability to add more files to installer bundle*

New properties have been added:

```xml
installer.included.item1
installer.included.item2
installer.included.item3
installer.included.item4
installer.included.item5
installer.included.item6
installer.included.item7
installer.included.item8
installer.included.item9
```

These properties allow you to specify files and directories to be included in the installer bundle. The files and directories will be copied to the root of the installation directory.


#### Example

```xml
    <properties>
        <installer.included.item1>old/**</installer.included.item1>
        <installer.included.item2>scripts/**</installer.included.item2>
    </properties>
```

#### Why?

You may want to include Terraform scripts, or state, ansible playbooks, install json, etc.

### *Trivy security reporting added*

This is a build improvement. Now security vulnerabilities will be reported to Github Security. Note that this will just report on the security vulnerabilities, but not fail the build. It's up to the maintainers to act upon the reported information.

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

### *License file is no longer part of the vro package*

#### Previous Behavior

The `THIRD-PARTY.properties` file was generated in `src/license` and it was bundled with the rest of the code by `vropkg`.

#### Current Behavior

The `THIRD-PARTY.properties` file is generated directly in the project basedir and it is not bundled with the rest of the code by `vropkg`.

## Upgrade procedure

[//]: # (Explain in details if something needs to be done)
