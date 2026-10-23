[//]: # (VERSION_PLACEHOLDER DO NOT DELETE)
[//]: # (Used when working on a new release.)
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

### *Orchestrator | Add support for "Foreach element" in workflow creation in TypeScript project*

Added a new `@ForeachItem` decorator for TypeScript-based vRO workflows. This decorator generates a `foreach` canvas item that iterates over an array and calls a linked sub-workflow once for each element in the array — equivalent to the **"Foreach element"** available in the vRO Orchestrator UI.

Key properties:

- `linkedItem` — the ID of the sub-workflow to call for each array element.
- `target` — the item to navigate to after all iterations complete.
- `exception` (optional) — the item to route to if the sub-workflow throws an exception; generates a `<catch-item>` in the workflow XML.

Input and output array bindings are declared with the standard `@In` / `@Out` parameter decorators. The `alt-connections` attribute in the generated XML is automatically computed as the total number of input + output bindings.

#### Relevant Documentation

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
