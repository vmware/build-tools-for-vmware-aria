# v2.29.2

## Breaking Changes



## Deprecations



## Features



## Improvements
### Support new v2 API for vRLI versions 8.8+ (Experimental)

#### Previous Behavior
When working with vRLI content, the package manager was not considering the version of the vRLI product
and was using the deprecated v1 API which is no longer maintained.

#### New Behavior
Managing of vRLI content is done using the appropriate API for the vRLI product - v1 for server version 8.7 and below
and v2 for newer versions.

#### Relevant Documentation:
**NONE**


## Upgrade procedure:

