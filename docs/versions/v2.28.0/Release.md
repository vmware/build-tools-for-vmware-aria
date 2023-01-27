# v2.28.0

## Breaking Changes



## Deprecations

### Single Action Polyglot Archetype

The Single Action Polyglot Archetype has been deprecated for a Multi Action one. The change, while using the same archetype, is
fully backwards compatible, however in the future this will be removed. Check the [Upgrade Procedure](#upgrade-procedure)
section to find out how to migrate away from the old polyglot archetype.



## Features:

### Added documentation for unit testing**
Added general documentation for Unit Testing, including examples. Check the Relevant Documentation subsection

#### Relevant Documentation:
[UnitTesting](Components/Archetypes/typescript/General/Testing/Getting%20Started.md)



## Improvements

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

### Polyglot packages are not published in JFrog artifactory when the deploy command is executed

#### Previous Behavior
When executing the mvn clean deploy command - the package produced by polyglot archetype is not uploaded to JFrog artifactory.

#### New Behavior
As part of the build process, polyglot archetype package is now uploaded in Jfrog artifactory just like any other vrbt package.

#### Relevant Documentation:
**NONE**

### New Polyglot Multi Action Capabilities
This change allows the polyglot archetype to be used to package more than one action at the same time.

#### Previous Behavior
The old polyglot relied on a structure like:

```ascii
src/
├─ handler.py
package.json
pom.xml
```

And relied on package.json for everything.

#### New Behavior
The new Multi Action Has a structure like:

```ascii
src/
├─ pythonActionOne/
│  ├─ handler.py
│  ├─ polyglot.json
│  ├─ requirements.txt
├─ nodeJsActionOne/
│  ├─ handler.ts
│  ├─ polyglot.json
│  ├─ package.json
│  ├─ tsconfig.json
├─ powershellActionOne/
│  ├─ handler.ps1
│  ├─ polyglot.json
pom.xml
package.json
```

Where the package.json in the root contains some dependencies and commands to be able to package the actions and does not
actually configure the actions.

The details for the actions have been moved to the polyglot.json files that are contained within the actions' folders.

#### Relevant Documentation
1. [Polyglot Archetype Documentation](./Components/Archetypes/Polyglot/README.md)



## Upgrade procedure

### Migrating from single action to multi action polyglot 

1. Generate a new archetype.
2. Pick a template from the generated structure
3. Replace the handler file
4. Open the `polyglot.json` and replace it with data from your package.json (not all, just the relevant information)
   1. The `files` attribute will define what will be included in the final package and what should be filtered. 
      - Do not modify the attribute, but if you do, don't touch the "%out" as that is dynamically set by polyglotpkg.
   2. Leave `"action": "auto",` if you want to use the name from the folder


