# v2.25.0

## **Breaking Changes**
*NONE*

## Deprecations
*NONE*

## Features:


[//]: # (Used when working on a new release. Placed together with the Version.md)
[//]: # (Nothing here is optional. If a step must not be performed, it must be said so)
[//]: # (Do not fill the version, it will be done automatically)

## **Breaking Changes**
*NONE*

## Deprecations
*NONE*

## Features:

### *Permit omission of versions for blueprints*
A new flag has been added in order to ignore the usage of blueprint versioning. It deffaults to `false`, so behaviour
is preserved if the flag is omitted. If set to `true` there will be no need to keep track of `versions.json` and the
file can even not exist and still release what is residing in `content.yaml`

#### Example:
`mvn vra-ng:pull -Dvrang.ignore.blueprint.versions=true -${profileName}`

#### Relevant Documentation:
- [Getting Started](Components/Archetypes/vRA%208.x/General/Getting%20Started.md#configuring-m2settingsxml-to-work-with-vra-ng)
- [Blueprints](./Components/Archetypes/vRA%208.x/Components/Blueprints.md#ignoring-versions)


#### Example:
`mvn vra-ng:pull -Dvrang.ignore.blueprint.versions=true -${profileName}`

#### Relevant Documentation:
- [Getting Started](Components/Archetypes/vRA%208.x/General/Getting%20Started.md#configuring-m2settingsxml-to-work-with-vra-ng)
- [Blueprints](./Components/Archetypes/vRA%208.x/Components/Blueprints.md#ignoring-versions)

### *PowerShell Polyglot type of projects support dependencies*
The dependencies modules used in the handler are now automatically downloaded and bundled into the final
zip file.

#### Previous Behavior
If you use external PowerShell modules, they were ignored during the packaging process. If you download and save them
manually in the Modules folder, the packaging fails with an error.

#### New Behavior
The vRBT checks all Import-Module references in the handler.ps1 and automatically downloads and saves them in the
Modules sub-directory. When the package is zipped, the Modules directory is included.

#### Relevant Documentation:
*NONE*

## Improvements

### *Blueprint versions are no longer in reverse order when pulled from vRA*
Blueprint `versions.json` is now ordered during `vrang:pull` and `vrealize:push` based on the `createdAt` attribute of
the versions of a blueprint.

This used to result in two problems - versions of an imported blueprint are in the wrong order,
which in turn caused an error when trying to release next version via `vrealize:push` due to the version already existing.

#### Previous Behavior
Pulling from vRA via `mvn vrang:pull -P${profileName}` creates a `versions.json` in reverse order and when pushing a new
blueprint version via vRBT an error was thrown due to the version already existing.

#### New Behavior
Pulling from vRA via `mvn vrang:pull -P${profileName}` creates a `versions.json` in correct order based on `createdAt`
timestamp and when pushing a new blueprint version via vRBT no error is thrown, rather a new version is released.

Pushing to vRA via `mvn vrealize:push -P${profileName}` will also sort the versions array to fix already existing issues.

#### Relevant Documentation:
*NONE*

### *Enable different types of input parameters for installation workflow*


## Improvements

### *Blueprint versions are no longer in reverse order when pulled from vRA*
Blueprint `versions.json` is now ordered during `vrang:pull` and `vrealize:push` based on the `createdAt` attribute of
the versions of a blueprint.

This used to result in two problems - versions of an imported blueprint are in the wrong order,
which in turn caused an error when trying to release next version via `vrealize:push` due to the version already existing.

#### Previous Behavior
Pulling from vRA via `mvn vrang:pull -P${profileName}` creates a `versions.json` in reverse order and when pushing a new
blueprint version via vRBT an error was thrown due to the version already existing.

#### New Behavior
Pulling from vRA via `mvn vrang:pull -P${profileName}` creates a `versions.json` in correct order based on `createdAt`
timestamp and when pushing a new blueprint version via vRBT no error is thrown, rather a new version is released.

Pushing to vRA via `mvn vrealize:push -P${profileName}` will also sort the versions array to fix already existing issues.

#### Relevant Documentation:
*NONE*

### *Enable different types of input parameters for installation workflow*

#### Previous Behavior
Installer passes all parameters as type string to the installation workflow.
Most common case of today is to use **Install** workflow from PsCoE installer library.
This workflow has 3 parameters :
* jsonString
* tags
* blacklist


The latter two are of type **Array/string**    
The installation/configuration values are passed as a json file (or yaml). Each root property of this file should have the name of an input value of the targeted workflow.
The values of those root keys are treated as strings. The installer converts them as string parameters in vro workflow execution rest request.
This makes impossible usage of any type of parameters such as Arrays, number and boolean.


#### Current Behavior
Different types of inputs for the installation workflow are supported:
* string
* number
* boolean
* array/string


#### Relevant Documentation:
[Example of using Install workflow](./Components/Local/Installer/Components/Install%20Workflow.md)
