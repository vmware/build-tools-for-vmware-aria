## Breaking Changes


## Deprecations


## Features


## Improvements

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
