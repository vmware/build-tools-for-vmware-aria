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

### *Added code coverage build step*

The build will now fail if the code coverage goes below 25%

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

### Add `VcStorageQueryManager` type definition

Added type definition for the `VcStorageQueryManager` class and it's related methods

### Enable System and Server level logging in the Run Script Workflow

The 'Run Script' XML Workflow in the 'exec' module is used by the 'vRealize Developer Tools' plugin to perform the 'Run vRo Action' command (accessible in VScode via the ... menu at the top-right corner of the editor for JS and TS files). The command is supported to run JS Actions - function closures without parameters or a return statement.

#### Previous Behavior

The System and Server level logs were not displayed in the vRealize developer tools OUTPUT console for Orchestrator versions above 7.3, regardless of the Vradev log level selected in the plugin settings.

Example output:
```log
# Running actionSamplePrintSth.js
# vRO Version: 8.14.0
# Execution ID: 6cee80ee-48cf-4198-8f28-8a82705e576f


# Completed after 0m 1s
```

#### New Behavior

With Vradev log configured to 'off' in the vRealize Developer Tools plugin settings, the output is unchanged.

Example output with Vradev log level 'info':
```log
# Running actionSamplePrintSth.js
# vRO Version: 8.14.0
# Execution ID: e23cdb94-9937-43e4-b9fc-a6a1995743de

[2024-10-10 18:01:46.362 +0300] [server] [info] Server.log printing something
[2024-10-10 18:01:46.364 +0300] [system] [info] System.log printing something
[2024-10-10 18:01:46.369 +0300] [server] [warning] Server.warn printing something
[2024-10-10 18:01:46.371 +0300] [system] [warning] System.warn printing something
[2024-10-10 18:01:46.376 +0300] [server] [error] Server.error printing something
[2024-10-10 18:01:46.378 +0300] [system] [error] System.error printing something
[2024-10-10 18:01:46.382 +0300] [system] [info] Testing duplicate logs (same message from System/Server):
[2024-10-10 18:01:46.384 +0300] [system] [info] Testing duplicate logs (same message from System/Server):
[2024-10-10 18:01:46.392 +0300] [system] [info] S.log printing something
[2024-10-10 18:01:46.394 +0300] [system] [info] S.log printing something
[2024-10-10 18:01:46.398 +0300] [system] [warning] S.warn printing something
[2024-10-10 18:01:46.402 +0300] [system] [warning] S.warn printing something
[2024-10-10 18:01:46.408 +0300] [system] [error] S.error printing something
[2024-10-10 18:01:46.409 +0300] [system] [error] S.error printing something
[2024-10-10 18:01:46.414 +0300] [system] [error] S.error printing something before error
[2024-10-10 18:01:46.416 +0300] [system] [error] S.error printing something before error
[2024-10-10 18:01:46.423 +0300] [system] [error] Error in (Workflow:Run Script / Scriptable task (item1)#24) Error: THIS IS THE ERROR!
```

Example output with Vradev log level 'info' (notice the additional System debug entry):
```log
# Running actionSamplePrintSth.js
# vRO Version: 8.14.0
# Execution ID: f61e30a4-b158-464f-90c8-d83317d30e39

[2024-10-10 17:47:21.642 +0300] [system] [debug] System.debug printing something
[2024-10-10 17:47:21.649 +0300] [server] [info] Server.log printing something
[2024-10-10 17:47:21.650 +0300] [system] [info] System.log printing something
[2024-10-10 17:47:21.654 +0300] [server] [warning] Server.warn printing something
[2024-10-10 17:47:21.655 +0300] [system] [warning] System.warn printing something
[2024-10-10 17:47:21.659 +0300] [server] [error] Server.error printing something
[2024-10-10 17:47:21.660 +0300] [system] [error] System.error printing something
[2024-10-10 17:47:21.663 +0300] [system] [info] Testing duplicate logs (same message from System/Server):
[2024-10-10 17:47:21.664 +0300] [system] [info] Testing duplicate logs (same message from System/Server):
[2024-10-10 17:47:21.665 +0300] [system] [debug] S.debug printing something
[2024-10-10 17:47:21.667 +0300] [system] [info] S.log printing something
[2024-10-10 17:47:21.668 +0300] [system] [info] S.log printing something
[2024-10-10 17:47:21.672 +0300] [system] [warning] S.warn printing something
[2024-10-10 17:47:21.673 +0300] [system] [warning] S.warn printing something
[2024-10-10 17:47:21.677 +0300] [system] [error] S.error printing something
[2024-10-10 17:47:21.678 +0300] [system] [error] S.error printing something
[2024-10-10 17:47:21.685 +0300] [system] [error] S.error printing something before error
[2024-10-10 17:47:21.686 +0300] [system] [error] S.error printing something before error
[2024-10-10 17:47:21.693 +0300] [system] [error] Error in (Workflow:Run Script / Scriptable task (item1)#24) Error: THIS IS THE ERROR!
```

### Change `011n-plugin-vc` type declarations from interfaces to classes

#### Previous Behavior

Some vCenter plugin types were declared as interfaces which leads to TS transpilation errors:

```log
[INFO] Typescript transpilation started
[INFO] src/workflows/TestWf.test.wf.ts:5:18 - error 'VcClusterComputeResource' only refers to a type, but is being used as a value here.
[INFO] Found 1 errors.
[INFO] Typescript transpilation finished
```

#### New Behavior

All vCenter plugin types are now declared as classes instead of interfaces. Private constructors are added to all of them. `readonly` access modifier is added to `sdkConnection` property in class definitions where it was not provided and defined as `Read-only` in API Explorer.

### Remove duplicate empty constructors in `011n-plugin-vc` type declarations

#### Previous Behavior

A lot of type definitions had duplicate constructors,e.g.:

```typescript
declare class VcHostDigestInfo {
	dynamicProperty: VcDynamicProperty[];
	digestValue: number[];
	digestMethod: string;
	objectName: string;
	dynamicType: string;
	constructor();
	constructor();
	/**
	 * @param digestMethod 
	 * @param digestValue 
	 * @param objectName 
	 */
	constructor(digestMethod: string, digestValue: number[], objectName: string);
}
```

#### New Behavior
Duplicate constructors are removed.

## Upgrade procedure

[//]: # (Explain in details if something needs to be done)
