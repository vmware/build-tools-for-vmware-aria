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
### Pretty formatted JSON for Custom Forms when storing them together with Custom Form Metadata
When Custom Forms are pulled from Aria Automation, they are stored on the file system (the repo) in a form similar to
```json
{
  "id": "e694a748-7067-47d1-91a4-614da73dda03",
  "name": "Test",
  "form": "{\"layout\": {...},\"schema\": {...},\"options\": {...}}",
  "styles": null,
  "sourceType": "com.vmw.blueprint",
  "sourceId": "71ac6ebc-6a94-3c5a-8c00-2a44ddf81bce",
  "type": "requestForm",
  "status": "ON",
  "formFormat": "JSON"
}
```
Here, please note that the form field is a double serialized JSON as string : "{"layout": {...},"schema": {...},"options": {...}}"
which makes it goes in one line and very difficult for a human to work with. If there is any commits and. changes in the form in the repo, the diffs are very difficult to find (when reviewing pull requests).
As a whole it is not human friendly and very difficult for a human to deal with.

The current pull request, makes it so that the format will become a properly formatted JSON object like:
```json
{
  "id": "e694a748-7067-47d1-91a4-614da73dda03",
  "name": "Test",
  "form": {
    "layout": {
    ...
    },
    "schema": {
    ...
    },
    "options": {
    ...
    }
  },
  "styles": null,
  "sourceType": "com.vmw.blueprint",
  "sourceId": "71ac6ebc-6a94-3c5a-8c00-2a44ddf81bce",
  "type": "requestForm",
  "status": "ON",
  "formFormat": "JSON"
}
```
This way it is more easy to work with.

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


### vrotsc 3.8.3 to 5.4.5 typescript

#### Related issue
<https://github.com/vmware/build-tools-for-vmware-aria/pull/245>


## Upgrade procedure
[//]: # (Explain in details if something needs to be done)
[//]: # (## Changelog:)
[//]: # (Pull request links)
