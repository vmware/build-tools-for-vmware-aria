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



### Implemented Push/Pull logic for new Content Sharing Policy from vRA 8.8.2

Content Sharing Policies can now be fetched for newer versions of vRA.

It can be done by adding:

```yaml
# ...
policy:
  content-sharing:
```

to your `content.yaml` file


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

### *Ability to cover cases when project.id is not set, but project.name is set when pushing Custom Resources*

#### Previous Behavior
Project id was being fetched from the configuration, which means in cases where the project id was not set, but project name was, there would be an error.

#### New Behavior
The project id is now fetched from the restClient, which checks if we have project id or name set and tries to find the correct id if the name is the only thing that is set

#### Relevant Documentation:
None

## Upgrade procedure:
[//]: # (Explain in details if something needs to be done)

[//]: # (## Changelog:)
[//]: # (Pull request links)
