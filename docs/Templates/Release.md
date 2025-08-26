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

### *Moved Aria Code Stream components to own folder*

This is just an internal restructuring effort, no functionality was changed.

### *Moved Basic archetype components to own folder*

This is just an internal restructuring effort, no functionality was changed.

### *Moved common components to own folder*

This is just an internal restructuring effort, no functionality was changed.

## Upgrade procedure

[//]: # (Explain in details if something needs to be done)
