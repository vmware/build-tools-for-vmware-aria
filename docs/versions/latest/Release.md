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

### *vrealize:clean will not fail if store does not support it* 
When we perform `mvn vrealize:clean -DincludeDependencies=true -DcleanUpOldVersions=true -DcleanUpLastVersion=false -PPROFILE_NAME`
some stores do not support cleaning or have not implemented it.

#### Previous Behavior
Previously in case of an unsupported store, the process would fail with either UnsupportedOperationException or NotImplementedException,
since the exception was never handled in the vrealize CleanMojo

#### New Behavior
UnsupportedOperationException is now being caught (NotImplementedException as well since it's a child) and a warning is logged instead.
The process is allowed to continue.

#### Relevant Documentation:

* [Vrealize Clean](./Components/Archetypes/General/Goals/Vrealize%20Clean.md)



### *Ability to deploy base package to artifactory*
It's expected that you can deploy all Build Tools for VMware Aria projects to artifactory and to local maven repository

#### Previous Behavior
When you trigger `mvn clean package install deploy` against base package, the build will fail

#### New Behavior
When you trigger `mvn clean package install deploy` against base package, the build will succeed and will deploy the package to artifactory server and to local maven repository.

#### Relevant Documentation:
None

## Upgrade procedure:
[//]: # (Explain in details if something needs to be done)

[//]: # (## Changelog:)
[//]: # (Pull request links)
