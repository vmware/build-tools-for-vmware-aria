---
title: Actions
---

# Actions/Javascript Based Project

## Overview

| Field | Value |
|---|---|
| Name | `actions` |
| Archetype Group ID | `com.vmware.pscoe.o11n.archetypes` |
| Archetype Artifact ID | `package-actions-archetype` |
| Product compatibility | {{ extra.products.vro_7_full_name }} (7.x), {{ extra.products.vro_8_full_name }} (8.x) and {{ extra.products.vro_9_full_name }} (9.x) |
| Package extension | `package` |

Actions (Javascript) project type is one of the available {{ products.vro_short_name }} project types in **Build Tools for VMware Aria**. The project type is a representation of {{ products.vro_short_name }} Actions into JS format. The project consist of local content container and remote content descriptor on the target server. During [build operation](#build-project) the contents of the container are packaged into {{ products.vro_short_name }} native package (the same package that can be exported/imported from {{ products.vro_short_name }} UI -> Assets -> Packages).

- *Content Descriptor* defines what {{ products.vro_short_name }} content will be part of this project when exporting content. The {{ products.vro_short_name }} package from UI -> Assets -> Packages plays this role and defines what content is exported. For more information refer to the [pull operation](#) section below.
- *Content Container* holds the actual content representation - `./src` folder.

The actions project type also allows the user to write unit tests and has embedded code coverage.


## Supported Content

- `Actions`

## Create New {{ products.vro_short_name }} Project
{{ general.bta_name }} provides ready to use {{ products.vro_short_name }} project templates (*maven archetypes*).

To create a new {{ products.vro_short_name }} project from archetype use the following command:

```Bash
mvn archetype:generate \
    -DinteractiveMode=false \
    -DarchetypeGroupId=com.vmware.pscoe.o11n.archetypes \
    -DarchetypeArtifactId=package-actions-archetype \
    -DarchetypeVersion={{ iac.latest_release }} # (1)! \
    -DgroupId={{ archetype.customer_project.group_id}} # (2)! \
    -DartifactId={{ archetype.customer_project.artifact_id}} # (3)!
```

1.  {{ archetype.customer_project.archetype_version_hint }}
2.  {{ archetype.customer_project.group_id_hint }}
3.  {{ archetype.customer_project.artifact_id_hint }}

### Content Structure

The result of this command will produce the following project file structure:

    group_id: com.company.bu
      group_id_hint: The groupId in Maven is a distinctive identifier specifically used for a project. It acts as a namespace, ensuring that project names don't clash with each other. It is recommended to choose a groupId that reflects your organization or project.
      artifact_id: project.type

```ascii
service-automation
├── README.md
├── pom.xml
├── release.sh
└── src
    └── main
        └── resources
            └── com
                └── company
                    └── bu
                        └── project
                            └── type
                                └── sample.js
    └── test
        └── resources
            └── com
                └── company
                    └── bu
                        └── project
                            └── type
                                └── SampleTests.js
```

<!-- Environment Connection Parameters Section -->
{% include-markdown "../common/connection-parameters.md" %}

## Operations

<!-- Build Project Section -->
{% include-markdown "../../../../assets/docs/mvn/build-project.md" %}
The output of the command will result in **{{ archetype.customer_project.group_id}}.{{ archetype.customer_project.artifact_id}}-1.0.0-SNAPSHOT.package** file generated in the target folder of the project. This is an {{ products.vro_short_name }} native package that can be imported from {{ products.vro_short_name }} UI -> Assets -> Packages.

<!-- Bundle Project Section -->
{% include-markdown "../../../../assets/docs/mvn/bundle-project.md" %}

<!-- Pull Content Section -->
{% include-markdown "../common/pull-content.md" %}

!!! warning
    During pull operation any content added to the {{ products.vro_short_name }} package is exported to the local file system but only Actions is exported as Javascript files - other content types are exported as XML. Any non-Javascript content is ignored during [build](#build-project), [bundle](#bundle-project) and [push](#push-content) operations.

<!-- Push Content Section -->
{% include-markdown "../../../../assets/docs/mvn/push-content.md" %}

<!-- Push Content - Additional Parameters Section -->
{% include-markdown "../common/push-content-parameters.md" %}

<!-- vroIgnore Section -->
{% include-markdown "../common/vroignore.md" %}

<!-- Clean Up Content Section -->
{% include-markdown "../../../../assets/docs/mvn/clean-up-content.md" %}

<!-- Troubleshooting Section -->
{% include-markdown "../../../../assets/docs/mvn/troubleshooting.md" %}

## Known issues
