# v4.9.0

## Breaking Changes


## Deprecations



## Features

### *Support for push/pull of `vra-ng` packages to VCF 9 Classic organization via provider admin user

Push/pull of `vra-ng` package types to/from VCF 9 Classic organization via provider admin user is now supported, e.g.:

* <vrang.username>admin@System</vrang.username>
* <vrang.org.name>Classic</vrang.org.name>

Note that the "System" domain is used to identify the user as Provider admin.



## Improvements


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

