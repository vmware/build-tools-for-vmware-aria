---
title: Typescript
---

# Typescript Based Project

## Overview

| Field | Value |
|---|---|
| Name | `typescript` |
| Archetype Group ID | `com.vmware.pscoe.o11n.archetypes` |
| Archetype Artifact ID | `package-typescript-archetype` |
| Product compatibility | {{ extra.products.vro_7_full_name }} (7.x), {{ extra.products.vro_8_full_name }} (8.x) and {{ extra.products.vro_9_full_name }} (9.x) |
| Package extension | `package` |

Typescript project type is one of the available {{ products.vro_short_name }} project types in **Build Tools for VMware Aria**. The project type enables the user to write Orchestrator code and define different Orchestrator objects in the form of typescript files. Typescript code is transpiled into {{ products.vro_short_name }} Javascript and packaged into {{ products.vro_short_name }} native package (the same package that can be exported/imported from {{ products.vro_short_name }} UI -> Assets -> Packages).

The typescript project type also allows the user to write unit tests and has embedded code coverage.

!!! note
    Supported Typescript version is 5.4.5.

## Supported Content

- `Workflows`
- `Workflow Custom Forms`
- `User Interaction Custom Forms`
- `Actions`
- `Configuration Elements`
- `Resource Elements`
- `Policies`

## Create New {{ products.vro_short_name }} Project
{{ general.bta_name }} provides ready to use {{ products.vro_short_name }} project templates (*maven archetypes*).

To create a new {{ products.vro_short_name }} project from archetype use the following command:

```Bash
mvn archetype:generate \
    -DinteractiveMode=false \
    -DarchetypeGroupId=com.vmware.pscoe.o11n.archetypes \
    -DarchetypeArtifactId=package-typescript-archetype \
    -DarchetypeArtifactId=package-vra-ng-archetype \
    -DarchetypeVersion={{ iac.latest_release }} \
    -DgroupId={{ archetype.customer_project.group_id}} # (1)! \
    -DartifactId={{ archetype.customer_project.artifact_id}} # (2)!
```

1.  {{ archetype.customer_project.group_id_hint }}
2.  {{ archetype.customer_project.artifact_id_hint }}

!!! note
    If <build_tools_for_aria_version> is not specified a default value of 2.38.1 will be used.

### Content Structure

The result of this command will produce the following project file structure:

```ascii
service-automation
├── README.md
├── pom.xml
├── release.sh
├── tsconfig.json
└── src
    └── integration-service-1
        └── actions
            └── integrationAction.js
        └── classes
            └── IntegrationService1.ts
            └── IntegrationService1.test.ts            
        └── policies
            └── EventListener.pl.ts
        └── resources
            └── sample.txt
            └── sample_2.json
            └── sample_2.json.element_info.json
            └── sample_3.xml
            └── sample_3.xml.element_info.yaml
            └── sample_4.json
        └── types
            └── IntegrationService1.d.ts
        └── workflows
            └── CreateIntegration.wf.ts
            └── CreateIntegration.wf.form.json
        └── IntegrationService1.conf.ts            
        └── IntegrationService1Alternative.conf.yaml        
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

- `vro.username` - For {{ extra.products.vro_9_full_name }} (9.x) you need to provide username in the following format: user@domain. E.g.:
  - admin@System - Provider admin
  - configurationadmin@Classic - Classic organization admin

- `vro.auth` - TODO

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

### Additional Parameters

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

<!-- Clean Up Content Section -->
{% include-markdown "../../../../assets/docs/mvn/clean-up-content.md" %}

<!-- Troubleshooting Section -->
{% include-markdown "../../../../assets/docs/mvn/troubleshooting.md" %}

## Known issues
