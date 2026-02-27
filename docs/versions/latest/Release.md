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

### *Add support for Node:22 runtime environment for `polyglot` projects*

## Improvements

### *Bugfix for pushing pushing policies in Aria Operations*
#### Previous Behavior
It is not possible to push policies if they have missing dependencies (alert definitions, symptom definitions, recommendations) on the target server
#### New Behavior
There is an error message which shows the user when BTVA is trying to import policies if they have missing dependencies (alert definitions, symptom definitions, recommendations) on the target server

## Upgrade procedure

[//]: # (Explain in details if something needs to be done)
