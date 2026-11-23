# v4.26.0

## Breaking Changes


## Deprecations



## Features



### *Add support for 9.0.0 PowerShell runtime environments  for `polyglot` projects*

Added support for:

- powercli:9.0.0-powershell-7.4
- powercli:9.1.1-powershell-7.6

## Improvements


### Fix parsing of custom JSDoc `@tag` tag in Orchestrator Actions

#### Previous Behavior

If an action contains the `@tag` JSDoc tag the resulting package throws 400 error during package import.

#### New Behavior

If an action contains the `@tag` JSDoc tag the package is importable in Orchestrator and the provided `@tag` value is added to the corresponding Actions's Tags.

## Upgrade procedure

