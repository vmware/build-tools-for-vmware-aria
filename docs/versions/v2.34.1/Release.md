# v2.34.1

## Breaking Changes


## Deprecations


## Features


## Improvements

### Fixed pull operations, which were failing on Windows
#### Previous Behavior

Pull operation was failing on Windows for abx, ssh, vra-ng, vra, vrli.
Whenever a pull was initiated, it was failing with:

pull failed: 'posix:permissions' not supported as initial attribute

when trying to create a temporary directory.
This is due to PosixPermissions which can be used only with POSIX compatible operating systems.

#### New Behavior
Using another library for temp dir creation which checks if POSIX is supported.

## Upgrade procedure

