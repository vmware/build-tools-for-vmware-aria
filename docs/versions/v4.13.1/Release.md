# v4.13.1

## Breaking Changes


## Deprecations



## Features


### *Enable authentication capabilities for VCFA 9.0.1*

Previously VCFA used the VCD API versioning where 9.0.0 matched `40.0`. With VCF 9.0.1, this behavior is changed and API version is `9.0.0`. Code is updated to be able to handle this change.


## Improvements


### *Failed unit tests result in build failure*

#### Previous Behavior
Failure in a unit test leads to a failure log but the build still completes successfully.

#### New Behavior
Failure in a unit test leads to failing the whole build.

## Upgrade procedure

