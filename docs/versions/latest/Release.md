## Breaking Changes


## Deprecations


## Features


## Improvements

### Fixed importing of vRA content items on vRA 8.13.x

#### Previous Behavior

When pushing vRA content items to vRA 8.13.x the pushing fails with the following error:
Cannot invoke "com.vmware.pscoe.iac.artifact.model.vrang.VraNgCatalogItemType.getId()" because "this.type" is null
When the content item naming case in the content.yaml file is different from the file names that are in the content-items directory
they are not pushed to the target environment (skipped).

#### New Behavior

When pushing the vRA content items to  vRA 8.13.x it no longer fails. The content naming in the content.yaml is no longer case sensitive
againist the file names in the content-items directory, thus the content-items names in the content.yaml file are no longer case sensitive.

### Fixed the compiled SAGA workflow crashes when no imports are defined in saga yaml

#### Previous Behaviour

When no imports were defined in typescript SAGA workflow file format, the compiled vRO workflow failed when being executied in vRO on Initilize scriptable task
with null object error.

#### New Behaviour

When no imports are defined in typescript SAGA workflow file format, the compiled vRO workflow runs successfully.

### Fixed push to vCD failing if API version is >= 38.0

#### Previous Behaviour

When using vCD 10.5 there is a breaking change affecting the way authentication works and as a consequence the push to vCD is failing.

#### New Behaviour

This introduces a quick fix to the problem: if API version 38.0 or later is detected, simply use 37.0 which we know works as expected.

## Upgrade procedure
