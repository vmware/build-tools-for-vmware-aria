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

### Remove prompt and property for vro_delete_include_dependencies
The `installer` script shows a prompt "Clean up vRO dependent packages as well?" about deleting "dependent packages" and stores the answer in the `vro_delete_include_depenedencies` property but does nothing regardless of the answer.

#### Previous Behavior
There was prompt in interactive mode for vRO regarding the deletion of dependent packages.

#### New Behavior
The unnecessary question regarding dependent packages in vRO projects is removed.

#### Relevant Documentation
**NONE**

### **.helper.ts files will now be excluded from type definitions* 
Currently the *.helper.ts files are being transpiled, and definitions are being generated. This shouldn't be happening, 
as there is no reason to have helper files as part of the definitions, since they are test related only.

#### Previous Behavior
The helper.ts files had type definitions generated for them since vrotsc did not detect them correctly.

#### New Behavior
The helper.ts are now being correctly filtered out by vrtosc, by excluding files that end with: `.helper.ts`.

#### Relevant Documentation:
* None

### *Installer Should Ask if vRO is Embedded*

#### Previous Behavior
Installer won't ask if "vRO is embedded" if it does not contain VRA NG Packages.

#### New Behavior
Installer will ask if "vRO is embedded" in all the cases. This would potentially help us for easier support to deploy to extensibility proxies without vRA content deployment in the bundle.

#### Relevant Documentation:
None

## Upgrade procedure:
[//]: # (Explain in details if something needs to be done)

[//]: # (## Changelog:)
[//]: # (Pull request links)
