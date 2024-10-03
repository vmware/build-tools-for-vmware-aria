[//]: # (VERSION_PLACEHOLDER DO NOT DELETE)
[//]: # (Used when working on a new release. Placed together with the Version.md)
[//]: # (Nothing here is optional. If a step must not be performed, it must be said so)
[//]: # (Do not fill the version, it will be done automatically)
[//]: # (Quick Intro to what is the focus of this release)

## Breaking Changes

[//]: # (### *Breaking Change*)
[//]: # (Describe the breaking change AND explain how to resolve it)
[//]: # (You can utilize internal links /e.g. link to the upgrade procedure, link to the improvement|deprecation that introduced this/)

## Deprecations

[//]: # (### *Deprecation*)
[//]: # (Explain what is deprecated and suggest alternatives)

[//]: # (Features -> New Functionality)

## Features

[//]: # (### *Feature Name*)
[//]: # (Describe the feature)
[//]: # (Optional But highly recommended Specify *NONE* if missing)
[//]: # (#### Relevant Documentation:)

[//]: # (Improvements -> Bugfixes/hotfixes or general improvements)

### *Add new Powershell and PowerCLI runtime options*

The following have been added:

```text
- `powercli:12-powershell-7.4`
- `powercli:13-powershell-7.4`
- `powershell:7.4`
```

## Improvements

[//]: # (### *Improvement Name* )
[//]: # (Talk ONLY regarding the improvement)
[//]: # (Optional But highly recommended)
[//]: # (#### Previous Behavior)
[//]: # (Explain how it used to behave, regarding to the change)
[//]: # (Optional But highly recommended)
[//]: # (#### New Behavior)
[//]: # (Explain how it behaves now, regarding to the change)
[//]: # (Optional But highly recommended Specify *NONE* if missing)
[//]: # (#### Relevant Documentation:)

### Restored missing 'exec' module

The error when running an Action in VRDT UI was resolved by restoring the 'exec' module in 'packages'.

#### Previous Behavior

In VS Code with the vRealize Developer Tools plugin installed, when the 'Run vRO Action' command was issued via the UI kebab menu of a JS/TS Action, an error appeared in the OUTPUT of the type:

```log
# Running getVmWithTag.js
# An error occurred: Could not import exec package into vRO: Command 'mvn dependency:copy -Dartifact=com.vmware.pscoe.o11n:exec::package -DoutputDirectory="/Users/user/Library/Application Support/Code/User/globalStorage/vmware-pscoe.vrealize-developer-tools" -Dmdep.stripVersion=true ' exited with code 1
```

The 'com.vmware.pscoe.o11n:exec' package was missing when building the build tools.

#### New Behavior

The 'com.vmware.pscoe.o11n:exec' package is no longer missing when built. The error above no longer appears when running an Action via VRDT UI.

## Upgrade procedure

[//]: # (Explain in details if something needs to be done)
