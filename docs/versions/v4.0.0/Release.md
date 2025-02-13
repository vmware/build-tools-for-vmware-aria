# v4.0.0

## Breaking Changes

### *Upgrade to node.js 22 (22.13.1)*
BTVA is compiled for node.js 22 and may use features not available in older node.js versions. Make sure proper version of node.js is used for project build.
Projects that use older versions of @types/node should upgrade to the later versions compatible with node.js 22. Changes in code using obsolete functions may be required.

### *Upgrade to latest typescipt and ts-node*
The latest versions of typescipt have stricter code validation and may report errors where they were previously ignored.
Examples: not passing all mandatory function parameters, use of 'this' in workflow definitions (*.wf.ts).

### *Aria (vra-ng) policies are now handled differently.*

Previously, when pulling and pushing policies, duplicates were allowed, however this didn't allow us to map them correctly. So that being the case, now policies with the same name and of the same type on the environment will break the execution of the code.

## Deprecations


### *Angular 8 not supported for VCD projects*
The VCD projects based on Angular 8 templates are not supported anymore. Projects must be upgraded to Angular 15 templates.

### *Remove powershell runtime option*
The following option have been removed:
```text
- `powershell:7.4`
```

## Features



## Improvements


### *Fix documentation typo*

It is now possible to configure how the unit tests are being bootstrapped and executed.

#### Previous Behavior

Missing a close curly brace in the Workflow's User Interaction example.

#### New Behavior

The example now has a close curly brace.

#### Relevant Documentation

[Workflows.md](../../versions/latest/Components/Archetypes/typescript/Components/Workflows.md).

### *Fix unit test execution in Windows reporting error*

#### Previous Behavior

When maven build executes unit tests, it reports "Error occurred in unit tests execution: null"

#### New Behavior

The correct output of unit tests result is shown

## Upgrade procedure


For older vCD projects which are not buildable with Node 22 you can:
* Build them as a separate project
* Use Node version manager to dynamically switch between Node versions during build phases of the different subprojects
* Utilise Maven goals and plugins to dynamically switch to earlier Node version in the vCD project's pom.xml
