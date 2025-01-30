[//]: # (VERSION_PLACEHOLDER DO NOT DELETE)
[//]: # (Used when working on a new release. Placed together with the Version.md)
[//]: # (Nothing here is optional. If a step must not be performed, it must be said so)
[//]: # (Do not fill the version, it will be done automatically)
[//]: # (Quick Intro to what is the focus of this release)

## Breaking Changes

[//]: # (### *Breaking Change*)
[//]: # (Describe the breaking change AND explain how to resolve it)
[//]: # (You can utilize internal links /e.g. link to the upgrade procedure, link to the improvement|deprecation that introduced this/)
### *Upgrade to node.js 22 (22.13.1)*
BTVA is compiled for node.js 22 and may use features not available in older node.js versions. Make sure proper version of node.js is used for project build.
Projects that use older versions of @types/node should upgrade to the later versions compatible with node.js 22. Changes in code using obsolete functions may be required.

### *Upgrade to latest typescipt and ts-node*
The latest versions of typescipt have stricter code validation and may report errors where they were previously ignored.
Examples: not passing all mandatory function parameters, use of 'this' in workflow definitions (*.wf.ts).

## Deprecations

[//]: # (### *Deprecation*)
[//]: # (Explain what is deprecated and suggest alternatives)

[//]: # (Features -> New Functionality)
### *Angular 8 not supported for VCD projects*
The VCD projects based on Angular 8 templates are not supported anymore. Projects must be upgraded to Angular 15 templates.

### *Remove powershell runtime option*
The following option have been removed:
```text
- `powershell:7.4`
```

## Features

[//]: # (### *Feature Name*)
[//]: # (Describe the feature)
[//]: # (Optional But higlhy recommended Specify *NONE* if missing)
[//]: # (#### Relevant Documentation:)

[//]: # (Improvements -> Bugfixes/hotfixes or general improvements)

## Improvements

### *Custom Resources are now updated correctly if in use*

When a custom resource is in use, we attempt to do an update after a failed deletion (default behavior as we want to recreate it).

#### Previous Behavior

This used to fail since the exception was not caught and was also burried inside of another exception.

#### New Behavior

The exception is now caught and the update is attempted.

[//]: # (### *Improvement Name* )
[//]: # (Talk ONLY regarding the improvement)
[//]: # (Optional But higlhy recommended)
[//]: # (#### Previous Behavior)
[//]: # (Explain how it used to behave, regarding to the change)
[//]: # (Optional But higlhy recommended)
[//]: # (#### New Behavior)
[//]: # (Explain how it behaves now, regarding to the change)
[//]: # (Optional But higlhy recommended Specify *NONE* if missing)
[//]: # (#### Relevant Documentation:)

### *Plugin arguments cheatsheets generation fixed*

#### Previous Behavior

The cheatsheet documents contained similar error:
```text
[ERROR] version: '3.1.1-SNAPSHOT': Plugin com.vmware.pscoe.maven.plugins:abx-package-maven-plugin:3.1.1-SNAPSHOT or one of its dependencies could not be resolved: Could not find artifact
```

#### New Behavior

The cheatsheet documents are properly generated.

### *Unreleasing BP versions would cause an exception*

An exception was thrown like: `The request was rejected because the URL contained a potentially malicious String "//"","path":"/blueprint/api/blueprints/b355c354-a4b6-4d55-800a-8b4232f29b83/versions/2024-12-06-18-34-42//actions/unrelease`

This was because of the `//` after the version id.

#### New Behavior

The extra `/` is removed when forming the url now

### *Unable to package ABX project*

#### Previous Behavior

We were getting an error when trying to package ABX project:
```log
[ERROR] Failed to execute goal com.vmware.pscoe.maven.plugins:abx-package-maven-plugin:2.44.0:package (default-package) on project anothertest: Error creating ABX bundle: duplicate entry: node_modules/run-script-os/index.js -> [Help 1]
```

#### New Behavior

The ABX package created successfully.

### *Add interactive installer option for importing ABX package*

#### Previous Behavior

ABX packages are ignored in the bundle. The Installer completes the task without installing the packages.

#### New Behavior

The Installer asks the user whether to install ABX packages and in case of positive response installs the packages.

### *Change interactive installer default value of `Run vRO workflow?` option to `N`*

#### Previous Behavior

The default value was `Y` which often results in the user having to provide explicitly `N` as value.
In interactive mode, it is assumed that the Java Installer is run manually by a user. In that case if the user wants to run a workflow it is more common to login into the Aria Automation Orchestrator UI and run the workflow from there benefiting from all of the tracking and monitoring offered by Aria Automation Orchestrator.

#### New Behavior

The default value is set to `N` enabling the user to just proceed to the next option without providing explicit value for the most common use cases.

### *Fix interactive installer value hints for `Is single tenant environment?`*

#### Previous Behavior

There was a `(Y/N)` hardcoded in the question which resulted in double entry:
`Is single tenant environment (Y/N)? (Y/N) [Y]:`

#### New Behavior

The question is updated:
`Is single tenant environment? (Y/N) [Y]:`

### *Moved Aria Automation components to own folder*

This is jut an internal restructuring effort, no functionality was changed.

## Upgrade procedure

### *Dynamic type definitions for the unit tests framework.*

The package name and version of the type definitions are being installed dynamically based on the configuration of the solution.  
This ensures the type definitions available for type hinting / autocomplete in the IDE and for the transpilation are matching the actual code definitions of the unit test framework.

#### Previous Behavior

The type definitions installed were always the same package and version - the [BTVA built-in Jasmine definitions](https://github.com/vmware/build-tools-for-vmware-aria/tree/v3.1.1/vro-types/jasmine) (link is to v3.1.1, might be outdated at later point in time).

#### New Behavior

The configuration in the pom file affects both the framework being used and its type definitions being made available.

[//]: # (Explain in details if something needs to be done)
