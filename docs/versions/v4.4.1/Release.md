# v4.4.1

## Breaking Changes


## Deprecations



## Features



## Improvements

### Updated keystore-example version

The versions of keystore-example (keystoreVersion) in pom.xml files and examplers were updated to 4.4.0.

#### Previous Behavior

There were errors building BTVA due to the versions of keystore-example used longer being present in the maven repository.

#### New Behavior

There are no more errors related to keystoreVersion during building.

### CSP Now correctly work cross env

Project id is set at the correct time and also logs are correct.


## Upgrade procedure

