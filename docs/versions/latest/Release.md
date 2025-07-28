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

### *Remove `go-btva` reference

The `go-btva` tool has been archived so references in the documentation are removed.

## Features

[//]: # (### *Feature Name*)
[//]: # (Describe the feature)
[//]: # (Optional But higlhy recommended Specify *NONE* if missing)
[//]: # (#### Relevant Documentation:)

### *Support for push of Orchestrator packages to VCF9

Push of different Orchestrator package types to VCF 9 is now supported. This includes:
* actions-package
* xml-package
* typescript-project-all

Since VCF 9 uses vCD based-authentication the username needs to contain domain as well, e.g.:
* <vro.username>admin@System</vro.username> - This results in code push via the Provider administrative user "admin"
* <vro.username>configurationadmin@Classic</vro.username> - This results in code push via the Classic organization administrative user "configurationadmin"

### *Support for push of `vra-ng` packages to Classic organization in VCF9

Push of `vra-ng` package types to VCF 9 Classic organization is now supported.

Since VCF 9 uses vCD based-authentication the username needs to contain domain as well. Currently only push through Classic organization administrator
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

### Polyglot missing/invalid dependency validation

Added validation that the listed Powershell module imports have been downloaded.

#### Previous Behavior

When a dependency could not be downloaded (either due to invalid Module name or other reason), the building completed successfully.

#### New Behavior

When an imported Powershell module is not downloaded, the build fails with an error of the type:

```log
Error downloading modules VMware.vSphere.SsoAdmin,VMware.vSphere.SsoAdminAsdasd! Verify that:
1. The default PSGallery repository is registered and accessible
2. All listed modules are valid and can be fetched from PSGallery!
```

## Upgrade procedure

[//]: # (Explain in details if something needs to be done)
