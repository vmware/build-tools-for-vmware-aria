---
title: VCF Operations
---

# VCF Operations

## Overview

| Field | Value |
|---|---|
| Archetype | `package-vrops-archetype` |
| Name | vrops |
| Product compatibility | {{ extra.products.vrops_8_full_name }} and {{ extra.products.vrops_9_full_name }} |

{{ products.vrops_9_short_name }} projects are called `vrops` projects in **Build Tools for VMware Aria**. The project type is a representation of Operations content into human friendly format saved into different file types - JSON, XML, properties, ZIP. The project consist of content descriptor and content container.

- *Content Descriptor* defines what part of the Operations server content will be part of this project - `content.yaml`.
- *Content Container* holds the actual content representation -`./src` folder.

## Supported Content

- `view`
- `dashboard`
- `alert-definition`
- `symptom-definition`
- `policy`
- `default-policy`
- `recommendation`
- `super-metric`
- `metric-config`
- `report`
- `custom-group`

## Create New {{ products.vrops_9_short_name }} Project
{{ general.bta_name }} provides ready to use {{ products.vrops_9_short_name }} project templates (*maven archetypes*).

To create a new {{ products.vrops_9_short_name }} project from archetype use the following command:
```Bash
mvn archetype:generate \
    -DinteractiveMode=false \
    -DarchetypeGroupId=com.vmware.pscoe.vrops.archetypes \
    -DarchetypeArtifactId=package-vrops-archetype \
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
            └── alert_definitions
                └── Cluster is disbalanced.json
            └── custom_groups
                └── vCenter Folder Tag.json
            └── dashboards
                └── metadata
                    └── dashboardGroupActivationMetadata.vrops.json
                    └── dashboardSharingMetadata.vrops.json
                    └── dashboardUserActivationMetadata.vrops.json
                └── resources
                    └── resources.properties
                └── Assess Cost.json
            └── metricconfigs
                └── vSAN Savings
            └── policies
                └── policiesMetadata.vrops.json            
                └── Policy for Virtual Machines - Risk Profile 1.zip
            └── recommendations
                └── Bring the VMware Cloud Foundation Operations Node back online.json
            └── reports
                └── Cluster Cost Report
                    └── resources
                        └── resources.properties                
                    └── content.xml
            └── supermetrics
                └── Group CPU Average.json
            └── symptom_definitions
                └── Cloud Proxy is down.json
            └── views
                └── resources
                    └── content.properties
                └── Cluster Basic Inventory.xml
```

## Content

### Content Descriptor

Content Descriptor is implemented by content.yaml file with the following structure:

```yaml
view:
dashboard:
alert-definition:
symptom-definition:
policy:
default-policy:
recommendation:
super-metric:
metric-config:
report:
custom-group:
```

!!! note
    {{ products.vrops_9_short_name }} Project supports only content types outlined into Content Descriptor.

To capture the state of your {{ products.vrops_9_short_name }} environment simply fill in the names of the content objects and follow the [Pull Content](#pull-content) section.

!!! note
    All object types are list of string values except for `default-policy` which is a single policy name string.

#### Content Filtering

Contents are managed by different rules.

##### Import Rules for content types

- Only objects explicitly defined in the `content.yaml` are imported.
- `default-policy` does not actually import a policy - it only sets an existing policy with the given name as default.

##### Export Rules for content types

- Only objects explicitly defined in the `content.yaml` are exported.
- `default-policy` is not exported. If you want to export the policy set as default it needs to be added to the `policy` list.

#### Example

??? Content Descriptor
    ```yaml
    view:
      - Cluster Basic Inventory
    dashboard:
      - Assess Cost
    alert-definition:
      - Cluster is disbalanced
    symptom-definition:
      - Automation service is down
      - Cloud Proxy is down
    recommendation:
      - Bring the VMware Cloud Foundation Operations Node back online
    super-metric:
      - Group CPU Average
    report:
      - Cluster Cost Report
    metric-config:
      - vSAN Savings
    custom-group:
      - VCF World
    policy:
      - Policy for Virtual Machines - Risk Profile 1
    default-policy: Default Policy
    ```

///////////////////////

### ID handling

Relying on ids exported by Build Tools for VMware Aria should not happen. Build Tools for VMware Aria will remove where needed such ids or in some cases data will be fetched from the remote server, modified in flight and pushed to the server ( in cases where an import is needed ).

### Single Project And Single Organization

When working with vra-ng project type, each generated project is intended to work with **only one project and one organization**. In a case where more are needed, you must generate multiple projects.

For every object type that contains `organization` or `projectId` key in the JSON definition (e.g. Policies) the following behaviour applies:
- if the JSON definition contains `projectId` key and value defined, the value is replaced by the project defined in (based on import mechanism used):
  - `<vrang.project.name>` from the selected `settings.xml` Maven profile.
  - `vrang_project_name` provided to the `installer` script.
- if the JSON definition contains `organization` key and value defined, the object is imported with the option of `Available for any project` or `Organization` enabled for the specific organization (the exact option name differentiates in UI based on product version) defined in (based on import mechanism used):
  - `<vrang.org.name>` from the selected `settings.xml` Maven profile.
  - `vrang_org_name` provided to the `installer` script.

### Components

<!-- Blueprints -->
{% include-markdown "./vcfa-vmapps/Blueprints.md" %}

<!-- Content Policies -->
{% include-markdown "./vcfa-vmapps/Content Policies.md" %}

<!-- Custom Resources -->
{% include-markdown "./vcfa-vmapps/Custom Resources.md" %}

#### Catalog Items Custom Forms

The catalog items in the {{ products.vra_9_short_name }} Service Broker consists of different type of content sources - Blueprint, Extensibility Actions, Pipelines, Workflows and AWS CloudFormation Template. As with the 8.12 release, the catalog items custom forms can be versioned. For all types, the same concepts replies - *only current versions for custom forms are de-serializing/serializing.*

Blueprint type catalog items have different versions that corelate to the released versions of the Blueprints. Only the current version of the latest blueprint version is targeted.

## Environment Connection Parameters

The following need to be added to the profile that you intend to use:

``` xml
... # (1)!
<!--    vra-ng    -->
<profile>
<!--    ..... OTHER DIRECTIVES .....  -->
    <vrang.host>example.vra.url.com</vrang.host>
    <vrang.csp.host>console.cloud.vmware.com</vrang.csp.host>
    <vrang.proxy>http://proxy.host:80</vrang.proxy>
    <vrang.port>443</vrang.port>
    <vrang.username>administrator</vrang.username>
    <vrang.password>someSecurePassword</vrang.password>
    <vrang.tenant>{tenant}</vrang.tenant>
    <vrang.project.name>{project+name}</vrang.project.name>
    <vrang.org.name>{org+name}</vrang.org.name>
    <vrang.refresh.token>{refresh+token}</vrang.refresh.token>
    <vrang.bp.unrelease.versions>true|false</vrang.bp.unrelease.versions>
    <vrang.vro.integration>{vro+integration+name}</vrang.vro.integration>
    <vrang.import.timeout>{import+timeout}</vrang.import.timeout>
    <vrang.data.collection.delay.seconds>{data+collection+delay}</vrang.data.collection.delay.seconds>
</profile>
```
1.  {{ archetype.customer_project.maven_settings_location_hint}}

- `vrang.username` - For VCF 9 Automation - Classic organization instead of using <vrang.tenant>
you need to provide username in the following format: user@domain. E.g.:
  - admin@System - Provider admin (the "System" domain is used to identify the user as Provider admin)
  - configurationadmin@Classic - Classic organization admin

- `vrang.refresh.token` - uses the given refresh token instead of credentials.

!!! note
    Refresh token takes precedence over credentials.

- `vrang.bp.unrelease.versions` - Defaults to `true`. Controls whether old versions of a blueprint sould be unreleased.

- `vrang.import.timeout` - Timeout in miliseconds when syncing from Content Source for Catalog Items to appear before performing additional operations (e.g. attaching Custom Forms, Icons, etc.). Default value is 6000.

- `vrang.data.collection.delay.seconds` - Delay in seconds to wait for vRA data collection to pass before importing data. Can also be passed as an interactive parameter `-Dvrang.data.collection.delay.seconds=600`. useful when Dynamic types and custom resources are used in the projects and vRO content is imported, however vRA needs to then retrieve it in order to be able to create the custom Resource and use the Create/Delete Workflows. This only happens after a short delay and the vRA data collector scrapes vRO. Defaults to no delay.
  - if a value is provided data collection is forced via REST API and if it completes successfully the provided delay time is skipped. In case the data collection fails, the delay is triggered.

- `vrang.org.name` - needs to be specified. The `vra-ng` project is scoped to a single organization.

Use the profile by passing it with `-P`, e.g.:
``` bash
mvn vra-ng:pull -P{{ archetype.customer_project.maven_profile_name}}
```

## Operations

<!-- Build Project Section -->
{% include-markdown "../../assets/docs/mvn/build-project.md" %}
The output of the command will result in **{{ archetype.customer_project.group_id}}.{{ archetype.customer_project.artifact_id}}-1.0.0-SNAPSHOT.vra-ng** file generated in the target folder of the project.

<!-- Bundle Project Section -->
{% include-markdown "../../assets/docs/mvn/bundle-project.md" %}

### Pull Content

When working on a {{ products.vra_9_short_name }} project, you mainly make changes on a live server using the {{ products.vra_9_short_name }} UI (Service Broker, Cloud Assembly, etc.) and then you need to capture those changes in the maven project on your filesystem to be able to store the content, track changes, collaborate, etc.

To support this use case, the a custom maven goal `vra-ng:pull` is used. The following command will `pull` the content outlined into *Content Descriptor* file to the current project from a specified server and expand its content in the local filesystem overriding any local content:

```bash
mvn vra-ng:pull -P{{ archetype.customer_project.maven_profile_name}}
```

!!! note
    The command will fail if the `content.yaml` is empty or it cannot find some of the described content on the target {{ products.vra_9_short_name }} server.

!!! note
    If a catalog item has a custom form and/or an icon they will be exported in subdirs of the catalog-items directory

!!! note
    The value of the `<vrang.vro.integration>` is used to change the integration endpoint of Workflow Content Sources and other resources that point to that type of integration. If the property is missing a default name "embedded-VRO" will be used.

#### Additional Parameters
* `bp.ignore.versions` - ignores blueprint versioning  (refer to the *Blueprint Versioning* section). This option defaults to `false`. When dealing with blueprint development, you might want to set this to `true` in order to avoid unnecessary blueprint versions.

<!-- Push Content Section -->
{% include-markdown "../../assets/docs/mvn/push-content.md" %}

#### Additional Parameters

* `vrang.bp.release` - create a new version for already released blueprint (refer to the *Blueprint Versioning* section). This option defaults to `true`. When dealing with blueprint development, you might want to set this to `false`
in order to avoid unnecessary blueprint versions.

!!! note
    If there are any custom forms or icons associated with a catalog-item they will also be imported. 

!!! note
    If there are custom forms in the custom-forms directory that are associated with workflows, they will be imported to the {{ products.vra_9_short_name }} server as well.

!!! note
    If there are custom forms in the custom-forms directory that are associated with workflows, the content-sources that are associated with them will be imported as well (they will be read from the content-sources directory).

### Release
To release a specific content uploaded on a live server, you can use the ```vrealize:release``` command:

```bash
mvn clean package vrealize:release -Pcorp-env -Dvrang.contentType=blueprint -Dvrang.contentNames=testBlueprint -Dvrang.version=1 -DreleaseIfNotUpdated=false
```

Only parameter vrang.version is required. 
Defalut behavior for other parameters:
    - vrang.contentType: default value "all". Releases all supported content types.
    - vrang.contentNames: default value "[]". Releases all content of given types on server.
    - vrang.releaseIfNotUpdated: default value "false". Skips content if there are no updates since latest version.

!!! note
    Nothing will be released if any of the content already has the given version existing.


<!-- Clean Up Content Section -->
{% include-markdown "../../assets/docs/mvn/clean-up-content.md" %}

<!-- Troubleshooting Section -->
{% include-markdown "../../assets/docs/mvn/troubleshooting.md" %}

## Known issues
* There is an issue with svg icons, they will not be downloaded/uploaded (IAC-482).
