# v4.23.0

## Breaking Changes


## Deprecations



## Features


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



## Improvements


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

