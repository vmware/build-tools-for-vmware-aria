[//]: # (VERSION_PLACEHOLDER DO NOT DELETE)
[//]: # (Used when working on a new release.)
[//]: # (Nothing here is optional. If a step must not be performed, it must be said so)
[//]: # (Do not fill the version, it will be done automatically)
[//]: # (Quick Intro to what is the focus of this release)

## Breaking Changes

[//]: # (### *Breaking Change*)
[//]: # (Describe the breaking change AND explain how to resolve it)
[//]: # (You can utilize internal links /e.g. link to the upgrade procedure, link to the improvement|deprecation that introduced this/)

### *vROps content.yaml filtering rules*

The `content.yaml` of `vrops` projects follows the same filtering rules as the other project types, for both `vrops:pull` and `vrops:push` (packaging):

- Empty array (`[]`) - nothing from the object type is processed.
- List of items - only the listed items are processed. If a listed item (without wildcard) is not present on the server (pull) or in the project (push), the build fails.
- Null (no value) - everything from the object type is processed.
- Missing category type - nothing from the object type is processed.

Previously a category type without value was treated as an empty array and the listed items that were not found were silently skipped. To keep the previous behavior, replace the category types without value with `[]` and remove the items that are not present.

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

## Upgrade procedure

[//]: # (Explain in details if something needs to be done)
