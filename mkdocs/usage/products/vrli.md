---
title: VCF Operations for Logs
---

# VCF Operations for Logs

## Overview

| Field | Value |
|---|---|
| Name | `vrli` |
| Archetype Group ID | `com.vmware.pscoe.vrli.archetypes` |
| Archetype Artifact ID | `package-vrli-archetype` |
| Product compatibility | {{ extra.products.vrli_8_full_name }} (8.x) and {{ extra.products.vrli_9_full_name }} (9.x) |
| Package extension | `vrli` |

{{ products.vrli_9_full_name }} projects are called `vrli` projects in **Build Tools for VMware Aria**. The project type is a representation of {{ products.vrli_9_full_name }} content into human friendly JSON format. The project consist of content descriptor and content container.

- *Content Descriptor* defines what part of the {{ products.vrli_9_full_name }} server content will be part of this project - `content.yaml`.
- *Content Container* holds the actual content representation -`./src` folder.

## Supported Content

- `alerts`
- `content-packs`

## Create New {{ products.vrli_9_full_name }} Project
{{ general.bta_name }} provides ready to use {{ products.vrli_9_full_name }} project templates (*maven archetypes*).

To create a new {{ products.vrli_9_full_name }} project from archetype use the following command:

```Bash
mvn archetype:generate \
    -DinteractiveMode=false \
    -DarchetypeGroupId=com.vmware.pscoe.vrli.archetypes \
    -DarchetypeArtifactId=package-vrli-archetype \
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
operations
├── README.md
├── content.yaml
├── pom.xml
├── release.sh
└── src
    └── main
        └── resources
            └── alerts
                └── Cluster is disbalanced.json
            └── content-packs
                └── VCF Operations.json
```

## Content

### Content Descriptor

Content Descriptor is implemented by `content.yaml` file with the following structure:

```yaml
alerts:
content-packs:
```

!!! note
    {{ products.vrli_9_full_name }} Project supports only content types outlined into Content Descriptor.

To capture the state of your {{ products.vrli_9_full_name }} environment simply fill in the names of the content objects and follow the [Pull Content](#pull-content) section.

!!! note
    All object types are list of string values.

#### Content Filtering

Contents are managed by different rules.

##### Import Rules for content types

- All local objects available in `./src` folder are imported. The `content.yaml` is not taken into consideration.

##### Export Rules for content types

- Only objects explicitly defined in the `content.yaml` are exported.

#### Example

??? Content Descriptor
    ```yaml
    alerts:
      - VC stats query time-out occurred
    content-packs:
      - VCF Operations
    ```

## Environment Connection Parameters

The following need to be added to the profile that you intend to use:

``` xml
<!-- (1)! -->
<profile>
    <!--    ..... OTHER DIRECTIVES .....  -->
    <vrli.host>flt-logs01.corp.internal</vrli.host>
    <vrli.port>9543</vrli.port>
    <vrli.username>admin</vrli.username>
    <vrli.password>VMware1!VMware1!</vrli.password>
    <vrli.provider>Local</vrli.provider>
    <vrli.vropsHost>flt-ops01a.corp.internal</vrli.vropsHost>
    <vrli.vropsPort>443</vrli.vropsPort>
    <vrli.vropsUser>admin</vrli.vropsUser>
    <vrli.vropsPassword>VMware1!VMware1!</vrli.vropsPassword>
    <vrli.vropsAuthSource>local</vrli.vropsAuthSource> 
</profile>
```

1.  {{ archetype.customer_project.maven_settings_location_hint}}

- `vrli.vrops*` parameters are used for retrieving or updating data related to VCF Operations enabled alerts.

- `vrli.provider` - specifyies the authentication provider used to connect to the vRLI / VCF Operations for Logs server. Currently supported providers are Local, active directory and VIDM.

Use the profile by passing it with `-P`, e.g.:
``` bash
mvn vrli:pull -P{{ archetype.customer_project.maven_profile_name}}
```

## Operations

<!-- Build Project Section -->
{% include-markdown "../../assets/docs/mvn/build-project.md" %}
The output of the command will result in **{{ archetype.customer_project.group_id}}.{{ archetype.customer_project.artifact_id}}-1.0.0-SNAPSHOT.vrli** file generated in the target folder of the project.

<!-- Bundle Project Section -->
{% include-markdown "../../assets/docs/mvn/bundle-project.md" %}

### Pull Content

#### Overview

When working on a {{ products.vrli_9_full_name }} project, you mainly make changes on a live server using the {{ products.vrli_9_full_name }} UI and then you need to capture those changes in the maven project on your filesystem to be able to store the content, track changes, collaborate, etc.

#### Usage

To support this use case, the a custom maven goal `vrli:pull` is used. The following command will `pull` the content outlined into *Content Descriptor* file to the current project from a specified server and expand its content in the local filesystem overriding any local content:

```bash
mvn vrli:pull -P{{ archetype.customer_project.maven_profile_name}}
```

!!! note
    The command will fail if the `content.yaml` is empty or it cannot find some of the described content on the target {{ products.vrli_9_full_name }} server.

#### Wildcard Support

The content descriptor supports wildcard. This means that if a wildcard is present in the asset name, all assets matching the wildcard expression will be exported to the local file system. The example above shows how to use wildcard in the asset names. E.g.

```yaml
alerts:
  - "*HA*"
content-packs:
  - VMware *
```

<!-- Push Content Section -->
{% include-markdown "../../assets/docs/mvn/push-content.md" %}

<!-- Clean Up Content Section -->
{% include-markdown "../../assets/docs/mvn/clean-up-content-unsupported.md" %}

<!-- Troubleshooting Section -->
{% include-markdown "../../assets/docs/mvn/troubleshooting.md" %}
 