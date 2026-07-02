[//]: # (VERSION_PLACEHOLDER DO NOT DELETE)
[//]: # (Used when working on a new release.)
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

### *Support for VCF 9 Automation All-Apps Organizations*

New type of project was added that supports VCFA content for All-Apps organizations.
The project pom.xml should be configured with the following parameters:
- parent.groupId=*com.vmware.pscoe.vcf*
- parent.artifactId=*all-apps-package*
- packaging=*vcfa-all-apps*
The project has structure similar to vra-ng and supports the following content types:
- `blueprint` (including custom form, publishing to Catalog, organization sharing)
- `property-group`
- `custom-resource`
- `resource-action`
- `workflow` (publishing Orchestrator workflows to Catalog, including organization sharing)
- `subscription`
- `policy` (supports Approval policy, Day 2 Actions policy, IaaS Resource policy, Lease policy)
- `scenario` (notification scenarios customization)

New maven plugin `vcfa-all-apps` supporting operations:
- `package` (the package get extension *.vcfaa*)
- `vcfa-all-apps:pull`
- `vcfa-all-apps:push`
- `vcfa-all-apps:clean`

New archetype for creating vcfa-all-apps projects
- archetypeGroupId=com.vmware.pscoe.vcfa-all-apps.archetypes
- archetypeArtifactId=package-vcfa-all-apps-archetype
The created project contains content samples.

Added support for *.vcfaa* packages to *package-installer*

Added new set of parameters to settings.xml dedicated to vcfa-all-apps projects


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

### *Change type of `Import Dashboard for All Users` Installer input to boolean*

#### Previous Behavior
The input is parsed as boolean but the input type is string which is confusing for the user and does not have auto suggested values.

```txt
Import Dashboards For All Users:
Invalid value.
Expected a string with at least 1 character.

Import Dashboards For All Users: yes
```

#### New Behavior

Input type is boolean with suggested values and default value of true.

```txt
Import Dashboards For All Users (Y/N) [Y]:
```

## Upgrade procedure

[//]: # (Explain in details if something needs to be done)
