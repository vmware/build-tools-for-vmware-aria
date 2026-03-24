# v4.19.0

## Breaking Changes


## Deprecations



## Features



### *Add type definitions for `o11n-plugin-vcfa` plugin*

### *Change `SNMPSnmpQuery` type definition from `interface` to `class` in `o11n-plugin-snmp` plugin*

### *Update type definitions for `o11n-plugin-vc` plugin*
Add new parameters and constructor to `VcHostNasVolumeSpec`.

### *Update type definitions for `KeyPairManager` from `o11n-plugin-ssh` plugin*

### *Add support for Node:22 runtime environment for `polyglot` projects*

## Improvements


### *Improve TypeScript workflow decorator documentation*

#### Previous Behavior

Workflow decorator typings in `vrotsc-annotations` provided limited IDE documentation, and the written workflow documentation could drift from the actual decorator/type surface (for example, older decorator naming and stale parameter lists).

#### New Behavior

Workflow-related decorators and configuration types now include richer, more consistent JSDoc (including readable links), and the latest workflow documentation is aligned with the typings (updated decorator naming and supported parameters).

### *Bugfix for failure to find imported module of Powershell 7 polyglot action*

#### Previous Behavior

Executing a PowerShell 7 polyglot action with an imported module results in an error of the type "The specified module '...' was not loaded because no valid module file was found in any module directory."

#### New Behavior

When building a PowerShell polyglot action with imported modules the build process downloads modules to out/Modules/ via Save-Module and the ZIP bundle now correctly includes all files from Modules/ with proper folder structure.

### Improve healthcheck script, add healthcheck script and instructions for Windows

## Upgrade procedure

