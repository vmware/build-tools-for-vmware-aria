# v4.2.0

## Breaking Changes

### Remove .vra-ng artifacts and replace them with .vrang

This change is required because of issues when using vra-ng artifacts as dependencies to other projects.
Backward compatibility should be preserved with the installer, which should be capable of installing both vra-ng and vrang packages. Anyway, for newly created projects, artifacts and versions, the `dependency.type` property must be updated from `vra-ng` to `vrang`

## Deprecations



## Features



## Improvements


## Upgrade procedure

