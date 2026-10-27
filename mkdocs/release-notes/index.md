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

### *VCFA All Apps respects the module's content.yaml when run from a parent project with -pl*

#### Previous Behavior

The VCFA All Apps stores read `content.yaml` from the current working directory. When running e.g. `mvn vcfa-all-apps:pull -pl vcfa-all-apps` from a parent project, no `content.yaml` was found and all content types were processed, including types not listed in `content.yaml`. This could lead to failures such as `403 FORBIDDEN "User doesn't have the necessary rights to get a form definition!"` for users without rights for custom resources.

#### New Behavior

The `content.yaml` of the module is used on pull, and the packaged `content.yaml` on push, regardless of the directory Maven is run from.

### *VCFA All Apps resolves blueprint and workflow form files against the module directory*

#### Previous Behavior

On pull, the cleanup of blueprint form files (when the server has no custom form) looked for the files under the current working directory, so the module files were not cleaned up when running from a parent project with `-pl`. Workflow form files (`.wf.form.json`) were placed in the `vro` module only when running from the parent project or from a module named `vra`.

#### New Behavior

Blueprint form files are cleaned up in the module directory, and workflow form files are placed in the sibling `vro` module, regardless of the directory Maven is run from.

## Upgrade procedure

[//]: # (Explain in details if something needs to be done)
