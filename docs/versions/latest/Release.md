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

### *`vrealize:clean` support for vra-ng*

The `vrealize:clean` command now supports the `vra-ng` profile.

> [!WARNING]
> It does NOT support regional resource deletion as those are expected to be refactored in the future.

Also a new flag is added to the installer `vrang_delete_content` that will default to `false` if not set. If this flag is set, the environment will be cleaned.
The installer will also prompt you if you want to delete the content if you select false to importing the content.

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


### Pushing Fails when Pushing ABX action

Fixed problem with pushing ABX actions from a project created by the ABX archetype.

#### Previous Behavior

When pushing ABX actions from a project created by the ABX archetype it fails with the following error:

```log
[ERROR] Failed to execute goal com.vmware.pscoe.maven.plugins:vrealize-package-maven-plugin:push (default-cli) on project abx-project-1: You need to have the package goal as well when pushing vRealize projects.
```
#### New Behavior

Pushing of ABX action now works with ABX actions created by the ABX archetype. Furthermore the `runtimeVersion` property was added to the `package.json` file where you can specify the runtime version where the action will be executed at. If not specified the lowest available runtime version will be used (i.e. for `node.js` action the default runtime version is `14.0` that is deprecated). With the new property you can workaround deprecated runtimes.

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
