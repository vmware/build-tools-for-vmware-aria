# v4.8.0

## Breaking Changes


## Deprecations



### *Remove `go-btva` reference

The `go-btva` tool has been archived so references in the documentation are removed.

## Features


### *Support for push/pull of Orchestrator packages to VCF9

Push/pull of different Orchestrator package types to VCF 9 is now supported. This includes:
* actions-package
* xml-package
* typescript-project-all (only push)

Since VCF 9 uses vCD based-authentication the username needs to contain domain as well, e.g.:
* <vro.username>admin@System</vro.username> - This results in code push via the Provider administrative user "admin"
* <vro.username>configurationadmin@Classic</vro.username> - This results in code push via the Classic organization administrative user "configurationadmin"

### *Support for push/pull of `vra-ng` packages to Classic organization in VCF9

Push/pull of `vra-ng` package types to/from VCF 9 Classic organization is now supported.

Since VCF 9 uses vCD based-authentication the username needs to contain domain as well. Currently only push through Classic organization administrator
is supported, e.g.:
* <vrang.username>configurationadmin@Classic</vrang.username>
* <vrang.org.name>Classic</vrang.org.name>

## Improvements


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

