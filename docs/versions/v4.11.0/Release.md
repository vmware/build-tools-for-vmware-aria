# v4.11.0

## Breaking Changes


## Deprecations


## Features


### *Add support for User Interaction Custom Form files named based on User Interaction item name instead of ID in Typescript projects*

This feature extends the support for Typescript push for User Interaction Custom Form files with one more naming convention. Originally we support `{{workflowFileName}}_input_form_{{userInteractionItemId}}.wf.form.json` as naming convention.

The current feature enables also the following convention: `{{workflowFileName}}_input_form_{{userInteractionName}}.wf.form.json` where:
* `workflowFileName` - is the actual file name of the `.wf.ts` definition file (which might be different from the `Workflow` name defined in the `name` key inside the `@Workflow` decorator parameters).
* `userInteractionName` - the name of the `User Interaction` component - corresponds to the decorated function name in the `.wf.ts` file and is translated to the display name of the `User Interaction` component in the Orchestrator UI. Please note that the function name (and thus - display name) cannot contain whitespaces and comes with any other function naming limitation of Typescript functions.

E.g. for `CreateIntegration.wf.ts` and following User Interaction definition with an item ID `item2` and Typescript code:
```typescript
   @UserInteractionItem({
        target: "end",
    })
    public userApproval() {
    }
```
During push the code searches for one of the following 2 files:
* CreateIntegration_input_form_item2.wf.form.json
* CreateIntegration_input_form_userApproval.wf.form.json

If neither are found the default empty form is used.

### *Add exception handling to User Interaction in Typescript projects*

User interaction decorator now supports an exception handling parameter similar to other decorators. The parameter receives the name of the Workflow item which should be executed in case an exception or a timeout is encountered during the User Interaction component execution.

```typescript
    @UserInteractionItem({
        target: "end",
        exception: "errorHandlingScript"
    })
    public userApproval() {}
```

## Improvements


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
### *Add support for special characters in supermetric names*

#### Previous Behavior
It is not possible to pull super metrics with special characters in the names, eg Supermetric (Cluster Storage usage)

#### New Behavior
Users are able to push and pull super metrics that has special characters in the names. Also to use wildcards with special characters, eg Supermetric(*
### *Bugfix for pulling/pushing symtptom definitions in Aria Operations*

#### Previous Behavior
A symptom definition which is comparing 2 metrics (of numeric type) is pulled with a "condition.value" property set to "null" (as string) and therefore when the symptom definition is about to get pushed it is returning a HTTP 400 (Bad Request) response

#### New Behavior
Users are able to push symptom definitions, which are comparing 2 numeric metrics, to Aria Operations
