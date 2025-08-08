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

### *Support for push/pull of `vra-ng` packages to VCF 9 Classic organization via provider admin user

Push/pull of `vra-ng` package types to/from VCF 9 Classic organization via provider admin user is now supported, e.g.:

* <vrang.username>admin@System</vrang.username>
* <vrang.org.name>Classic</vrang.org.name>

Note that the "System" domain is used to identify the user as Provider admin.

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

### *Add `Workflow` to XML based archetypes path for Workflows content*

#### Previous Behavior
The XML based archetypes were missing `Workflow` folder in their path which results in creating duplicate content in separate folder in source code after pushing to Orchestrator and after that pulling the same package.

#### New Behavior
The XML based archetypes now create proper folder path and after pulling from Orchestrator content is updated and not duplicated in a separate folder.

### *Fix `Installer - run Workflow` operation on VCF 9*

#### Previous Behavior
Orchestrator code push to VCF 9 via `Installer` was working but `run Workflow` operation was failing to authenticate because of domain duplication when parsing credentials.

#### New Behavior
Credentials are now properly parsed and both code push and running a Workflow are working as expected.

## Upgrade procedure

[//]: # (Explain in details if something needs to be done)
