# (VERSION_PLACEHOLDER DO NOT DELETE)
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
### Enable support for binding workflow attributes to Configuration Element variables
Be able to bind attribute values to Configuration Element variables

To do that one have to provide the following annotation for the workflow ts file (SomeFile.wf.ts)
```json
@Workflow({
        id: "<Some Id>",
        name: "<Some Name>",
        path: "<Some Path>",
        attributes: {
            attributeName: {
            type: "string",
            bind: true,   
            value: "Some/Path/To/ConfigurationElement/variableName"
            }
        }
    })
```
Here
`bind: true` - means that we have to bind the value of the attribute to Configuration Element variable.
`value: "Some/Path/To/ConfigurationElement/variableName"` - points to the Configuration Element and variable inside the Configuration Element to bind to.


[//]: # (Improvements -> Bugfixes/hotfixes or general improvements)
## Improvements

### Add missing types to AD Plugin

Fix Issue #251

#### Previous Behavior

AD types were not implemented

#### Current Behavior

AD types were added

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
[//]: # (## Changelog:)
[//]: # (Pull request links)
