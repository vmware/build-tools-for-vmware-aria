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

## Features:

### Added documentation for unit testing**
Added general documentation for Unit Testing, including examples. Check the Relevant Documentation subsection

#### Relevant Documentation:
[UnitTesting](Components/Archetypes/typescript/General/Testing/Getting%20Started.md)



[//]: # (Improvements -> Bugfixes/hotfixes or general improvements)
## Improvements
[//]: # (### *Improvement Name* )
[//]: # (Talk ONLY regarding the improvement)
[//]: # (Optional But highly recommended)
[//]: # (#### Previous Behavior)
[//]: # (Explain how it used to behave, regarding to the change)
[//]: # (Optional But highly recommended)
[//]: # (#### New Behavior)
[//]: # (Explain how it behaves now, regarding to the change)
[//]: # (Optional But highly recommended Specify *NONE* if missing)
[//]: # (#### Relevant Documentation:)

### vrotest support for System.sleep and System.waitUntil functions

#### Previous Behavior

Previously System.sleep and System.waitUntil were returning immediately without waiting the specified amount of time.

#### New Behavior

Now System.sleep and System.waitUntil emplement basic sleep functionality. This functionallity is using busy waiting implementation which is cpu intensive.

### vRBT to support sending UTF-8 content in regards to vRA-NG projects.

#### Previous Behavior

When we run the push command with content containing UTF-8 characters the installation process failed.
e.g. if there are values in the BPs, Custom Form default data which has a utf-8 character or data in the post
installation WF json -  the REST request for calling the rest execution of the workflow is failing.

#### New Behavior

When we run the push command with content containing UTF-8 characters the installation process succeeds.
The fix enables us to use non ASCI but UTF-8 characters in the Blueprints, Custom forms and and post install WFs json
inputs

### Updated vro unit test module

#### Previous Behavior
when executing unit tests, an error message was displayed

```
    Browserslist: caniuse-lite is outdated. Please run
    ...
``` 

#### New Behavior
The package browserslist was updated and the error message will not appear

### Introduced autogeneration for docs/versions/latest/General/Cheatsheets

#### Previous Behavior
plugin arguments were not up to date

#### New Behavior
plugin arguments will be generated during version release

### Installer fails to import a property group if a different project scope is provided
Added a warning that a property group would not be imported to vRA when such property group already exists in another project.
Added a fix for assigning a property group to the provided project (in the configuration file) during a first-time import of the property gtoup to vRA.

#### Previous Behavior
When running the installer script to import property groups to vRA, it fails to import the property group if it already exists in vRA and is bound to a different project. Another issue was that importing a non-existent in vRA property group would be created without a project even if one is provided in the configuration properties.

#### New Behavior
When iporting a property group that already exists in vRA to another project, a warning is shown that the import will not be performed. When importing a non-existent property group, it gets the correct project id assigned.

#### Relevant Documentation:
**NONE**

### Pull command is failing if we don't pass any value for content-source (content.yaml)

#### Previous Behavior
When executing the Pull command, it fails with NullPointerException if we don't pass anything for content-source

#### New Behavior
We are able to execute pull command successfully even if we don't pass anything for content-source

#### Relevant Documentation:
**NONE**


## Upgrade procedure:
[//]: # (Explain in details if something needs to be done)

[//]: # (## Changelog:)
[//]: # (Pull request links)
