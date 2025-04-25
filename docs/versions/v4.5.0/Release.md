# v4.5.0

## Breaking Changes


## Deprecations



## Features


### *Simplified Minimal Infra Setup*

There is a new script now that can be executed and it will setup the minimal infra.

### *New Tool For Simplified Setup*

Added link to `go-btva` that can be used to get a sandbox up and running, fast.

## Improvements

### Fixed Error Running Unit Tests on Linux and Windows

An error affected unit test execution in versions after 4.3.0 without causing the build to fail.

#### Previous Behavior

An error of the type "Error occurred in unit tests execution:" was shown instead of unit test results.

#### New Behavior

Unit test results are shown as expected.


### *Fixed Error with Pulling vROPs policies*

Fixed error during pulling of vROPs policies due to missing *policiesMetadata.vrops.json* file.

#### Previous Behavior

Due to error while creating of the *policiesMetadata.vrops.json* file when exporting vROPs policies they cannot be exported.

#### New Behavior

Policies can be exported successfully, the *policiesMetadata.vrops.json* file creation and reading is removed, as it is no longer needed. When custom groups that are dependent on policies are exported the name of the policy is stored in the custom group JSON file. When importing those custom groups their policies are searched in the target system prior import, if they cannot be found an error will be thrown.

## Upgrade procedure

