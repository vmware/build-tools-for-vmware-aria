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

### *Aria (vra-ng) policies are now handled differently.*

Previously, when pulling and pushing policies, duplicates were allowed, however this didn't allow us to map them correctly. So that being the case, now policies with the same name and of the same type on the environment will break the execution of the code.

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

## Upgrade procedure

[//]: # (Explain in details if something needs to be done)

For older vCD projects which are not buildable with Node 22 you can:
* Build them as a separate project
* Use Node version manager to dynamically switch between Node versions during build phases of the different subprojects
* Utilise Maven goals and plugins to dynamically switch to earlier Node version in the vCD project's pom.xml
