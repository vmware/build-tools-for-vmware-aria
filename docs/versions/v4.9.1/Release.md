# v4.9.1

## Breaking Changes


## Deprecations



## Features



## Improvements


### *Fix issue where organization name is mandatory even for embedded Orchestrator code push*

#### Previous Behavior
Organization name parameter was mandatory even when pushing to embedded Orchestrator. This resulted in `Installer` code push throwing nullpointer unless you manually added vrang_org_name parameter to `environment.properties` since the installer prompts do not ask you for it (as it should be).

#### New Behavior
Organization name is now consumed only in cases where it is explicitly provided or mandatory and `Installer` code push to embedded Orchestrator executes successfully without it.

## Upgrade procedure

