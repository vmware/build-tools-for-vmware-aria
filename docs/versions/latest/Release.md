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
### *Enforce TLS 1.2 when Downloading PowerShell Modules*
Added support for downloading modules in powershell polyglot project handlers through 'Ssl3' | 'Tls' | 'Tls11' | 'Tls12' | 'Tls13'

#### Previous Behavior
Using Import-Module inside powershell polyglot projects downloads modules with system default encryption
#### New Behavior
Using Import-Module inside powershell polyglot projects downloads modules through SSL/TLS

[//]: # (Improvements -> Bugfixes/hotfixes or general improvements)
## Improvements

### *Allow only one format of using the Import-Module inside powershell* 
#### Previous Behavior
Using Import-Module -Name <example_module_name> will result in error since current code is considering only lines with Import-Module <example_module_name>
meaning that -Name parameter is not considered. Same for ';' - if added at the end of the line it will throw error currently
#### New Behavior
Using Import-Module -Name <example_module_name> will translate to Import-Module <example_module_name> and current behaviour error will be suppressed.
Same for ';' - if added at the end of the line it will not throw error currently, but it will be removed before downloading the modules

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

### Support overwrite of existing vRLI content packs if they exsist on the target vRLI

#### Previous Behavior
When importing a vRLI content pack on a target system where the content pack already exists the vRBT returned
a warning message stating that the content pack already exists and has to be manually uninstalled.

#### New Behavior
When importing a vRLI content pack on a target system where the content pack already exists and the
packageImportOverwriteMode (installer setting: vrli_package_import_overwrite_mode) is set to true or OVERWRITE
then the content pack is forcedly upgraded regardless whether it exsits on the target system.
If the flag is set to false or SKIP then a warning message is displayed stating that the content pack already
exsits and the flag is not set to true / OVERWRITE.

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
