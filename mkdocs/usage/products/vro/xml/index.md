---
title: XML
---

# XML Based Project

## Overview

| Field | Value |
|---|---|
| Name | xml |
| Archetype Group ID | com.vmware.pscoe.o11n.archetypes |
| Archetype Artifact ID | package-xml-archetype |
| Package extension | package |
| Product compatibility | {{ extra.products.vro_9_full_name }} (9.x)<br>{{ extra.products.vro_8_full_name }} (8.x)<br>{{ extra.products.vro_7_full_name }} (7.x) |


XML project type is one of the available {{ products.vro_short_name }} project types in **Build Tools for VMware Aria**. The project type is a representation of {{ products.vro_short_name }} content into XML format. The project consist of local content container and remote content descriptor on the target server. During [build operation](#build-project) the contents of the container are packaged into {{ products.vro_short_name }} native package (the same package that can be exported/imported from {{ products.vro_short_name }} UI -> Assets -> Packages).

- *Content Descriptor* defines what {{ products.vro_short_name }} content will be part of this project when exporting content. The {{ products.vro_short_name }} package from UI -> Assets -> Packages plays this role and defines what content is exported. For more information refer to the [pull operation](#) section below.
- *Content Container* holds the actual content representation - `./src` folder.

## Supported Content

- `Workflows`
- `Workflow Custom Forms`
- `User Interaction Custom Forms`
- `Actions`
- `Configuration Elements`
- `Resource Elements`
- `Policy Templates`

## Create New {{ products.vro_short_name }} Project
{{ general.bta_name }} provides ready to use {{ products.vro_short_name }} project templates (*maven archetypes*).

To create a new {{ products.vro_short_name }} project from archetype use the following command:

```Bash
mvn archetype:generate \
    -DinteractiveMode=false \
    -DarchetypeGroupId=com.vmware.pscoe.o11n.archetypes \
    -DarchetypeArtifactId=package-xml-archetype \
    -DarchetypeVersion={{ iac.latest_release }} # (1)! \
    -DgroupId={{ archetype.customer_project.group_id}} # (2)! \
    -DartifactId={{ archetype.customer_project.artifact_id}} # (3)! \
    -DworkflowsPath=integration-service-1/workflows # (4)!
```

1.  {{ archetype.customer_project.archetype_version_hint }}
2.  {{ archetype.customer_project.group_id_hint }}
3.  {{ archetype.customer_project.artifact_id_hint }}
4.  Defines the base path (Workflow Category path or labels depending on {{ products.vro_short_name }} version) and local folder structure for all the Workflows inside your project. This describes the initial state and can be manually updated later.

### Content Structure

The result of this command will produce the following project file structure:

```ascii
service-automation
├── pom.xml
├── release.sh
└── src
    └── main
        └── resources
            └── META-INF
                └── dunes-meta-inf.xml
            └── Workflow
                └── Workflow
                    └── integration-service-1
                        └── workflows
                            └── Install.element_info.xml
                            └── Install.xml
            └── dunes-meta-inf.xml
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

#### Additional Parameters

Additional parameters that can be passed as flags to the maven command, e.g. `mvn vro:pull -Dvro.packageExportConfigurationAttributeValues=true`.

* `vro.packageExportConfigurationAttributeValues` - if set to `true` exports all configuration values besides SecureStrings.

* `vro.packageExportConfigSecureStringAttributeValues` - if set to `true` exports all Secure String configuration values.

<!-- Push Content Section -->
{% include-markdown "../../../../assets/docs/mvn/push-content.md" %}

<!-- Push Content - Additional Parameters Section -->
{% include-markdown "../common/push-content-parameters.md" %}

* `vro.packageImportConfigurationAttributeValues` - if set to `true` pushes all configuration values besides SecureStrings.

* `vro.packageImportConfigSecureStringAttributeValues=true` - if set to `true` pushes all Secure String configuration values.

<!-- vroIgnore Section -->
{% include-markdown "../common/vroignore.md" %}

<!-- Clean Up Content Section -->
{% include-markdown "../../../../assets/docs/mvn/clean-up-content.md" %}

<!-- Troubleshooting Section -->
{% include-markdown "../../../../assets/docs/mvn/troubleshooting.md" %}

## Known issues
