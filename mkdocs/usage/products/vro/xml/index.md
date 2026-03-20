---
title: XML
---

# XML Based Project

## Overview

| Field | Value |
|---|---|
| Name | `xml` |
| Archetype Group ID | `com.vmware.pscoe.o11n.archetypes` |
| Archetype Artifact ID | `package-xml-archetype` |
| Product compatibility | {{ extra.products.vro_7_full_name }} (7.x), {{ extra.products.vro_8_full_name }} (8.x) and {{ extra.products.vro_9_full_name }} (9.x) |
| Package extension | `package` |

XML project type is one of the available {{ products.vro_short_name }} project types in **Build Tools for VMware Aria**. The project type is a representation of {{ products.vro_short_name_short_name }} content into XML format. The project consist of content content container. During [build operation](#build-project) the contents of the container are packaged into {{ products.vro_short_name }} native package (the same package that can be exported/imported from {{ products.vro_short_name }} UI -> Assets -> Packages).

- *Content Container* holds the actual content representation -`./src` folder.

## Supported Content

- `Workflows`
- `Workflow Custom Forms`
- `User Interaction Custom Forms`
- `Actions`
- `Configuration Elements`
- `Resource Elements`

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

## Environment Connection Parameters

The following need to be added to the profile that you intend to use:

``` xml
<!-- (1)! -->
<profile>
    <!--    ..... OTHER DIRECTIVES .....  -->
    <vro.host>flt-auto01.corp.internal</vro.host>
    <vro.auth>vra|basic</vro.auth>
    <vro.authHost>flt-auto01.corp.internal</vro.authHost>
    <vro.authPort>443</vro.authPort>
    <vro.port>443</vro.port>
    <vro.username>configurationadmin</vro.username>
    <vro.password>someSecurePassword</vro.password>
</profile>
```

1.  {{ archetype.customer_project.maven_settings_location_hint}}

Configuration:

- `vro.username` - For {{ extra.products.vro_9_full_name }} (9.x) you need to provide username in the following format: user@domain.
    - admin@System - Provider admin.
    - configurationadmin@Classic - Classic organization admin.

- `vro.auth` - Defines the authentication type used for REST API communication.
      - Supported values: `vra`, `basic` (depending on {{ products.vro_short_name }} version might need to be explicitly enabled in the product).
      - If set to `vra`, `vro.authHost` and `vro.authPort` need to be provided with the hostname and port of the target {{ products.vra_9_short_name }} server.

Use the profile by passing it with `-P`, e.g.:
``` bash
mvn vrealize:push -P{{ archetype.customer_project.maven_profile_name}}
```

## Operations

<!-- Build Project Section -->
{% include-markdown "../../../../assets/docs/mvn/build-project.md" %}
The output of the command will result in **{{ archetype.customer_project.group_id}}.{{ archetype.customer_project.artifact_id}}-1.0.0-SNAPSHOT.package** file generated in the target folder of the project. This is an {{ products.vro_short_name }} native package that can be imported from {{ products.vro_short_name }} UI -> Assets -> Packages.

<!-- Bundle Project Section -->
{% include-markdown "../../../../assets/docs/mvn/bundle-project.md" %}

<!-- Push Content Section -->
{% include-markdown "../../../../assets/docs/mvn/push-content.md" %}

#### Additional Parameters

Additional parameters that can be passed as flags to the maven command, e.g. `mvn clean package -DskipInstallNodeDeps=true`.

* `skipInstallNodeDeps` - skip the deletion and re-installation of node-deps.

!!! note
    If node_modules folder doesn't exist, then this flag is ineffective.

* `vro.forceImportLatestVersions` - This strategy will force you to upload the same or newer version of a package, otherwise it will fail the build, allowing us for better CI/CD pipelines, where we can ensure that the latest versions are always used on the server. Default value is `false`.

!!! note
    Snapshot versions are considered newer if they are the same as the version on the server.

* `vro.importOldVersions` - This strategy will upload a version of the package even if it is older than the version on the server.
!!! note
    Snapshot versions are considered newer if they are the same as the version on the server.
!!! note
    If `forceImportLatestVersions` is set to `true` this configuration is ignored.

<!-- vroIgnore Section -->
{% include-markdown "../common/vroignore.md" %}

<!-- Clean Up Content Section -->
{% include-markdown "../../../../assets/docs/mvn/clean-up-content.md" %}

<!-- Troubleshooting Section -->
{% include-markdown "../../../../assets/docs/mvn/troubleshooting.md" %}

## Known issues
