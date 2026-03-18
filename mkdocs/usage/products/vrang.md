---
title: VM Apps
---

# VM Apps / Classic Tenant Project

## Overview

| Field | Value |
|---|---|
| Name | `vra-ng` |
| Archetype Group ID | `com.vmware.pscoe.vra-ng.archetypes` |
| Archetype Artifact ID | `package-vra-ng-archetype` |
| Product compatibility | {{ extra.products.vra_8_full_name }} (8.x) and {{ extra.products.vra_9_classic_full_name }} (9.x) |
| Package extension | vrang |


{{ products.vra_9_short_name }} projects are called `vra-ng`(New Generation) projects in **Build Tools for VMware Aria**. The project type is a representation of {{ products.vra_9_short_name }} content into human friendly YAML and/or JSON format.The project consist of content descriptor and content container.

- *Content Descriptor* defines what part {{ products.vra_9_short_name }} content will be part of this project - `content.yaml`.
- *Content Container* holds the actual content representation -`./src` folder.

## Supported Content

- `blueprint`
- `content-sources`
- `property-group`
- `catalog-item`
- `catalog-entitlement`
- `subscription`
- `custom-resource`
- `resource-actions`
- `policies`
- `scenarios`

## Create New {{ products.vra_9_short_name }} Project
{{ general.bta_name }} provides ready to use {{ products.vra_9_short_name }} project templates (*maven archetypes*).

To create a new {{ products.vra_9_short_name }} project from archetype use the following command:

```Bash
mvn archetype:generate \
    -DinteractiveMode=false \
    -DarchetypeGroupId=com.vmware.pscoe.vra-ng.archetypes \
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
catalog
├── README.md
├── content.yaml
├── pom.xml
├── release.sh
└── src
    └── main
        └── blueprints
            └── blueprint.yaml
            └── content.yaml
            └── versions.yaml
        └── content-sources
            └── source.json
        └── property-group
            └── property_group_name.json
        └── catalog-items
            └── forms
                └── source name__workflow one name with custom form.json
                └── source name__workflow one name with custom form__FormData.json
                └── source name__workflow three name with custom icon and form.json
                └── source name__workflow three name with custom icon and form__FormData.json
            └── icons
                └── source name__workflow two name with custom icon.png
                └── source name__workflow three name with custom icon and form.png
            └── source name__workflow one name with custom form.json
            └── source name__workflow two name with custom icon.json
            └── source name__workflow three name with custom icon and form.json
        └── entitlements
            └── Blueprint.yaml
            └── Workflow.yaml
            └── ABX Action.yaml       
        └── subscriptions
            └── subscription.json
        └── custom-resources
            └── customResource.json
        └── resource-actions
            └── resourceAction.json
        └── policy
            └── approval
                └── aprovalPolicy1.json
            └── content-sharing
                └── contentSharingPolicy1.json
            └── day2-actions
                └── day2ActionsPolicy1.json
            └── deployment-limit
                └── deploymentLimitPolicy1.json
            └── lease
                └── leasePolicy1.json 
            └── resource-quota
                └── resourceQuotaPolicy1.json 
        └── scenarios
            └── Scenario Name.json
```

## Content

### Content Descriptor

Content Descriptor is implemented by `content.yaml` file with the following structure:

```yaml
blueprint:
subscription:
custom-resource:
resource-action:
catalog-entitlement:
catalog-item:
content-source:
property-group:
scenario:
policy:
  approval:
  content-sharing:
  day2-actions:
  deployment-limit:
  lease:
  resource-quota:
```

!!! note
    {{ products.vra_9_short_name }} Project supports only content types outlined into Content Descriptor.

To capture the state of your {{ products.vra_9_short_name }} environment simply fill in the names of the content objects and follow the [Pull Content](#pull-content) section.

For more information on each component please refer to the [Components](#components) section.

#### Content Filtering

Contents are managed by different rules.

##### Import Rules for content types

- All local objects available in `./src` folder are imported. The `content.yaml` is not taken into consideration.

##### Export Rules for content types

- Empty array [] - nothing is exported  
- List of items - the given items are exported. If they are not present on the server an Exception is thrown.  
- Null (nothing given) - everything is being exported

!!! note
    An error is thrown if the given entities are not found on the target server.

#### Example

??? Content Descriptor
    ```yaml
    blueprint: # will export all 
    subscription: # will export all 
    catalog-item:  # export according to filter
      - Project Blueprints__WindowsVM
      - Project Blueprints__LinuxVm
      - Main Workflows__ConfigureVM
    custom-resource: # will export all 
    resource-action: # will export all 
    property-group:  # export according to filter
      - memory
    catalog-entitlement: [] # will export none
    content-source:  # export according to filter
      - Project Blueprints
      - Main Workflows
      - Utility Workflows
      - Project Abx Actions
      - Project Code Stream pipelines
    policy:  # export according to filter
      approval: [] # will export none
      content-sharing:
        - cs policy 1
        - cs policy 2
      day2-actions: [] # will export none
      deployment-limit: [] # will export none
      lease: [] # will export none
      resource-quota: [] # will export none
    scenario: [] # will export none
    ```

!!! note
    Unreleased blueprints that have custom form will be automatically released with version 1.

!!! note
    To import / export custom forms and/or icons you have to specify the associated catalog-item name in ```catalog-item``` tag. The naming convention for this is SOURCE_NAME__CATALOG_ITEM_NAME
    The integration endpoint data for each workflow that is associated with the content source will be updated as well with the one fetched from the {{ products.vra_9_short_name }} server. 

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

## Environment Connection Parameters

The following need to be added to the profile that you intend to use:

``` xml
<!-- (1)! -->
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

#### Overview

When working on a {{ products.vra_9_short_name }} project, you mainly make changes on a live server using the {{ products.vra_9_short_name }} UI (Service Broker, Cloud Assembly, etc.) and then you need to capture those changes in the maven project on your filesystem to be able to store the content, track changes, collaborate, etc.

#### Usage

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

## Components

<!-- Blueprints -->
{% include-markdown "./vcfa-vmapps/Blueprints.md" %}

<!-- Content Policies -->
{% include-markdown "./vcfa-vmapps/Content Policies.md" %}

<!-- Custom Resources -->
{% include-markdown "./vcfa-vmapps/Custom Resources.md" %}

### Catalog Items Custom Forms

The catalog items in the {{ products.vra_9_short_name }} Service Broker consists of different type of content sources - Blueprint, Extensibility Actions, Pipelines, Workflows and AWS CloudFormation Template. As with the 8.12 release, the catalog items custom forms can be versioned. For all types, the same concepts replies - *only current versions for custom forms are de-serializing/serializing.*

Blueprint type catalog items have different versions that corelate to the released versions of the Blueprints. Only the current version of the latest blueprint version is targeted.

## Known issues
* There is an issue with svg icons, they will not be downloaded/uploaded (IAC-482).
