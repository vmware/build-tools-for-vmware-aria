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


### Created an extra package that contains all the necessary dependencies to work in offline mode
The packaging phase now creates a new package which is a zip that contains all dependencies so developers can use the vRBT tool in offline environment. The package is generated under the repository project (``maven/repository/pom.xml``) and the package is generated in the path ``maven\repository\target\iac-maven-repository-full.zip``.


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

### Fixed pulling of vROps dashboards as managed content

#### Previous Behaviour

When pulling a list of vROps dashboards defined in the content.yaml, the automation was failing with a hidden error message.
The underlying cause was that the directory in which the data is exported didn't exist and wasn't created by the automation.

#### New Behaviour

When pulling a list of vROps dashboards defined in the content.yaml, the automation exports the content successfully along with
their metadata files.

### *Content Sharing Policy supports Catalog Items and Content Source Items*

Content Sharing Policy supports both Catalog Item and Content Source Items association.

#### Previous Behavior

As of initial implementation, the Content Source Policy supported only Content
Source assignments.

#### Upgrade steps

As usual, the content for Content Sharing Policy needs to be pulled from the env.
Pull first, push the updated content.

## Upgrade procedure
[//]: # (Explain in details if something needs to be done)

[//]: # (## Changelog)
[//]: # (Pull request links)
