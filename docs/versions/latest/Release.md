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

## Upgrade procedure:
