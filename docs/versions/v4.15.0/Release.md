# v4.14.0

## Breaking Changes


## Deprecations



## Features


## Improvements

### *Bugfix for pushing pushing policies in Aria Operations*
#### Previous Behavior
It is not possible to push policies if they have missing dependencies (alert definitions, symptom definitions, recommendations) on the target server
#### New Behavior
There is an error message which shows the user when BTVA is trying to import policies if they have missing dependencies (alert definitions, symptom definitions, recommendations) on the target server

## Upgrade procedure

