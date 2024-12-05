# v3.1.1

## Breaking Changes


## Deprecations


## Features


## Improvements


### *vRA ContentSharingPolicy not pulled/pushed correctly with v2.39.0 and v2.40.0*

#### Previous Behavior

The `vRA ContentSharingPolicy` was not pulled/pushed correctly with v2.39.0 and v2.40.0 since filtering wasn't calling the resolve
method that would resolve the naming.

#### New Behavior

The `vRA ContentSharingPolicy` is now pulled/pushed correctly with v2.39.0 and v2.40.0 since filtering is now calling the resolve.

### *Better error handling when starting vRO workflow*

#### Previous Behavior
When starting a vro workflow, the error handling was not very clear and the error message was not very helpful when the params were missing the types.

#### New Behavior
The error handling has been improved and the error message is more helpful when the params are missing the types.

## Upgrade procedure

