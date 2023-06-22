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

### Fixed vro push requests from vscode having multiline or no description in pom.xml
#### Previous Behaviour

When no description is specified description is taken from Aria Build Tools' pom.xml files.
Whenever description contained line breaks, the vrocmd command created got corrupted.

#### New Behaviour

when description contained linebreaks, line breaks are replaced with space

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

### Fixed create configuration element

#### Previous Behaviour

The mocking for configuration elements is incorrect. When there are no attributes in a config element vRO returns null when the attributes of a config element is accessed.

#### New Behaviour

When there are no attributes in a config element it now returns null.

### Added new type VcVirtualPCIPassthroughBackingInfo

#### Previous Behaviour

When trying to create / attache new PCI device (vGPU), the VcVirtualPCIPassthrough class doesn't support for VcVirtualPCIPassthroughVmiopBackingInfo.vgpu.

#### New Behaviour

Added support to class VcVirtualPCIPassthrough for VcVirtualPCIPassthroughVmiopBackingInfo.vgpu.
