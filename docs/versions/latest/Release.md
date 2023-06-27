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

### *Cloud Template Versioning*

Cloud template no longer supports versioning. There are no alternatives.

[//]: # (Features -> New Functionality)
## Features
[//]: # (### *Feature Name*)
[//]: # (Describe the feature)
[//]: # (Optional But higlhy recommended Specify *NONE* if missing)
[//]: # (#### Relevant Documentation:)


[//]: # (Improvements -> Bugfixes/hotfixes or general improvements)
## Improvements

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

### *Deprecated Cloud Template Versioning*

Cloud templates used to have versioning information attached to them but it served little purpose, as it was metadata and they were not real versions.

#### Previous Behavior

- Cloud Templates would be released only if there was a change
  - this was configurable with a flag, whether we should always realese or not
- Versions were being saved locally
- Version metadata was pushed when the blueprint was released.

#### New Behavior

- Cloud Templates would be released only if there was a change
  - Not configurable
- Versions are not stored locally
- Older versions of the cloud template are unreleased
  - Configurable with a new flag "vrang.bp.unrelease.versions" (Defaults to true)

#### Relevant Documentation

- [Getting Started](./Components/Archetypes/vRA%208.x/General/Getting%20Started.md)


### *Content Sharing Policy supports Catalog Items and Content Source Items*

Content Sharing Policy supports both Catalog Item and Content Source Items association.

#### Previous Behavior

As of initial implementation, the Content Source Policy supported only Content
Source assignments.

## Upgrade procedure

### Deprecated Cloud Template Versioning

1. Remove flags if used "vrang.bp.release", "vrang.bp.ignore.versions"

## Content Shareing Policy Supports Catalog Items and Content Source Items

As usual, the content for Content Sharing Policy needs to be pulled from the env.
Pull first, push the updated content.

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
