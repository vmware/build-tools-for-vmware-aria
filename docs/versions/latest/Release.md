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

### Fix on legacy archetype failing with vro:pull (when workflow folder path name contains special characters(&))

#### Previous Behaviour

When executing a vro:pull command on a legacy archetype, the command will fail if the workflow path names contain
special characters such as '&'.

When executing a vro:pull command on JavaScript archetype, it succeeds, ignoring the
special characters in the folder name.

#### Current Behaviour

When executing a vro:pull command on either Legacy or JS archetype, if the workflows paths contains special character(&),
the command will successfully execute, ignoring folders with special characters(&).

/test1/test&/test2 -> /test1/test2

Additionally, a warning message will be printed on the console highlighting the issue with unsupported characters.

### Fix SSH Session methods type

#### Previous Behavior

When using SSH with typescript, the `error` and `state` methods has the type `void`. But technically, it returns a string. VSCode highlight it as an error and the complication failed. The same method is working in JS (obviously). Example from the built-in Workflow. Variable `error` and `state` has type `String`.

#### Current Behavior

Method `error` and `state` should return type `String` instead of type `void`


## Upgrade procedure
[//]: # (Explain in details if something needs to be done)

[//]: # (## Changelog:)
[//]: # (Pull request links)
