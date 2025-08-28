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

### *Add support for push/pull of different aria operation types with the same name*

#### Previous Behavior
It is not possible to have different resource types with the same name in Aria Operations. For instance a View and a Dashboard with name "Test", which is requirement in some projects

#### New Behavior
Users are able to create different resource types with the same name in Aria Operations, the same way as they are doing it from the UI.
## Upgrade procedure

### *Pull Resource Action Custom Form as prettified JSON instead of double serialized JSON*

#### Previous Behavior
The Custom Form is pulled as double serialized JSON which is unreadable and very hard to update. E.g.:

```json
{
  "id": "Cloud.vSphere.Machine.custom.add_disk_to_vm",
  "name": "add_disk_to_vm",
  "runnableItem": {...},
  "formDefinition": {
    "name": "add_disk_to_vm",
    "form": "{\"layout\":{{\"pages\":[...]},\"schema\":{...},\"options\":{...}",
    "sourceType": "resource.action",
    "sourceId": "Cloud.vSphere.Machine.custom.add_disk_to_vm",
    "type": "requestForm",
    "status": "ON",
    "formFormat": "JSON"
  }
}
```
#### New Behavior
The Custom Form is pulled and stored on the file system as a prettified JSON Object.
Note that the functionality is backwards compatible - during push operation the Custom Form definition is evaluated:
* if the field conatins double serialized JSON (expected by the API) the content is not changed
* if the field contains prettified JSON Object it is properly convered to a serialized String as expected by the API

```json
{
  "id": "Cloud.vSphere.Machine.custom.add_disk_to_vm",
  "name": "add_disk_to_vm",
  "runnableItem": {...},
  "formDefinition": {
    "name": "add_disk_to_vm",
    "form": {
      "layout": {...},
      "schema": {...},
      "options": {
        "externalValidations": [...]
      }
    },
    "sourceType": "resource.action",
    "sourceId": "Cloud.vSphere.Machine.custom.add_disk_to_vm",
    "type": "requestForm",
    "status": "ON",
    "formFormat": "JSON"
  }
}
```

[//]: # (Explain in details if something needs to be done)
