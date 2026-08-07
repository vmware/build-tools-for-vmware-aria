---
title: VM Apps Organization Project
---

## Overview
<!-- markdownlint-disable MD033 -->

| Field                   | Value                                                                                             |
|-------------------------|---------------------------------------------------------------------------------------------------|
| Name                    | vra-ng                                                                                            |
| Archetype Group ID      | com.vmware.pscoe.vra-ng.archetypes                                                                |
| Archetype Artifact ID   | package-vra-ng-archetype                                                                          |
| Package extension       | vrang                                                                                             |
| Product compatibility   | {{ extra.products.vra_9_classic_full_name }} (9.x)<br>{{ extra.products.vra_8_full_name }} (8.x)  |
<!-- markdownlint-enable MD033 -->

{{ products.vra_9_full_name }} projects (for VM Apps organizations and in {{ products.vra_8_full_name }}) are called `vra-ng` (New Generation) projects in **{{ general.bta_name }}**. The project type is a representation of {{ products.vra_9_short_name }} content in human-friendly YAML and/or JSON format. The project consist of a content descriptor and a content container.

- The *Content Descriptor* defines what part of {{ products.vra_9_short_name }} content is part of the project in the `content.yaml` file.
- The *Content Container* holds the actual content representation in the `./src` directory.

## Supported Content

Following is a list of the supported content for {{ products.vra_9_full_name }} VM Apps organization (and {{ products.vra_8_full_name }}) projects.

| Content Type                  | Attribute key in [Content Descriptor](#content-descriptor)    | Comment                                               |
|-------------------------------|---------------------------------------------------------------|-------------------------------------------------------|
| Cloud Templates (Blueprints)  | `blueprint`                                                   | See [Blueprint](./Blueprints.md) page.                |
| Content (Catalog) Items       | `catalog-item`                                                | See [Catalog Items](./Catalog%20Items.md) page.       |
| Content Sources               | `content-source`                                              | N/A                                                   |
| Custom Resources              | `custom-resource`                                             | See [Custom Resources](./Custom%20Resources.md) page. |
| Entitlements (Catalog)        | `catalog-entitlement`                                         | Starting with version 8.8 of {{ products.vra_8_full_name }}, catalog entitlements are replaced by content sharing policies. |
| Policies                      | `policy`                                                      | See [Policies](./Policies.md) page.                   |
| Property Groups               | `property-group`                                              | N/A                                                   |
| Resource Actions              | `resource-action`                                             | N/A                                                   |
| Notification Scenarios        | `scenario`                                                    | N/A                                                   |
| (Event) Subscriptions         | `subscription`                                                | N/A                                                   |

## Create New {{ products.vra_9_short_name }} Project for VM Apps Organizations

{{ general.bta_name }} provides ready-to-use {{ products.vra_9_short_name }} project templates (*maven archetypes*) for VM Apps organizations (and {{ products.vra_8_full_name }}).

To create a new {{ products.vra_9_short_name }} project for VM Apps from the archetype, use the following command.

{% include "../../../../assets/docs/mvn/archetype-generate.md" %}

### Content Structure

The command for creating a project produces the following project file structure.

```ascii
{{ page.meta.vars.project.artifact_id }}
├── README.md
├── content.yaml
├── pom.xml
├── release.sh
└── src
    └── main
        └── resources
            └── blueprints
                └── Example Blueprint/
                    └── blueprint.yaml
                    └── content.yaml
                    └── versions.yaml
            └── content-sources
                └── source.json
            └── property-group
                └── property_group_name.json
            └── catalog-items
                └── forms
                    └── content source name__workflow one name with custom form.json
                    └── content source name__workflow one name with custom form__FormData.json
                    └── content source name__workflow three name with custom icon and form.json
                    └── content source name__workflow three name with custom icon and form__FormData.json
                └── icons
                    └── content source name__workflow two name with custom icon.png
                    └── content source name__workflow three name with custom icon and form.png
                └── content source name__workflow one name with custom form.json
                └── content source name__workflow two name with custom icon.json
                └── content source name__workflow three name with custom icon and form.json
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
                    └── approvalPolicy1.json
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

## Project Content

The following sections give details about the project content management.

### Content Descriptor

The Content Descriptor is implemented by a `content.yaml` file with the following structure.

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
    {{ products.vra_9_short_name }} Project for VM Apps and {{ products.vra_8_full_name }} supports only the content types outlined into Content Descriptor.

To capture the state of your {{ products.vra_9_short_name }} environment, fill in the names of the content objects and use the commands described in the [Pull Content](#pull-content) section.

For more information on each component, see the corresponding sub-section page.

#### Content Filtering

The rules for project contents management depend on the operation.

##### Import Rules for Content Types

For import operations (pushing content), all local objects that are available in the `./src` directory of the project are imported and the content descriptor from the `content.yaml` file is not taken into consideration.

##### Export Rules for Content Types

For export operations (pulling content), remote objects are exported based on the following rules for each category in the content descriptor.

- Empty array (`[]`) - nothing from the object type is exported.
- List of items - only the listed items from the object type are exported. If an object is not present on the server, an exception is thrown.
- Null (no value) - everything from the object type is exported.

###### Export Rule Example

Following is a sample listing of a content descriptor for a project with examples of how the filtering rules apply for each category type.

??? "Content Descriptor"
    ```yaml
    blueprint:                # exports all blueprint/cloud template objects
    subscription:             # exports all EBS subscription objects
    catalog-item:             # exports catalog item objects listed in the filter
      # note that the notation is <content source name>__<catalog item name>
      - Project Blueprints__WindowsVM
      - Project Blueprints__LinuxVm
      - Main Workflows__ConfigureVM
    custom-resource:          # exports all custom resource objects
    resource-action:          # exports all resource action objects
    property-group:           # exports property group objects listed in the filter
      - memory
    catalog-entitlement: []   # does not export entitlement objects
    content-source:           # exports content source objects listed in the filter
      - Project Blueprints
      - Main Workflows
      - Utility Workflows
      - Project Abx Actions
      - Project Code Stream pipelines
    policy:                   # export policy objects listed in the filter per type
      approval: []            # does not export approval policy objects
      content-sharing:        # exports content sharing policy objects listed in the filter
        - cs policy 1
        - cs policy 2
      day2-actions: []        # does not export day-2 action policy objects
      deployment-limit: []    # does not export deployment limit policy objects
      lease: []               # does not export lease policy objects
      resource-quota: []      # does not export resource quota policy objects
    scenario: []              # does not export scenario objects
    ```

!!! note
    Unreleased blueprints that have a custom form are automatically released with version 1.

!!! note
    To export custom forms and/or icons, you need to specify the associated catalog item name in the `catalog-item` element. The naming convention for this is `<content source name>__<catalog item name>`.

    The integration endpoint data for each Orchestrator workflow that is associated with the content source is also updated with the one fetched from the {{ products.vra_9_short_name }} server.

### ID handling

You should not rely on IDs exported by {{ general.bta_name }}. Where needed, {{ general.bta_name }} removes such IDs or in some cases, it fetches data from the remote server, modifies it in flight, and pushes it to the server (in cases where an import is needed).

### Single Project And Single Organization

When you are working with the `vra-ng` project type, each project that you generate is intended to work with *a single project and a single organization only*. In case you need to work with more that one project or organization, you need to generate a separate Maven project for each of them.

For every object type that contains the `organization` or the `projectId` key in the JSON definition (such as {{ products.vra_9_short_name }} policies), the following behavior applies.

- If the JSON definition contains the `projectId` key with a value, the value is replaced by the project defined in one of the following sources (based on import mechanism that you use).

    - The `<vrang.project.name>` attribute from the `settings.xml` Maven profile that you are using.
    - The `vrang_project_name` property that you provide to the `installer` script.

- If the JSON definition contains the `organization` key with a value, the object is imported with the scope option **Available for any project** or **Organization** (the exact option name differs in the user interface based on product version) that you defined in one of the following sources (based on import mechanism that you use).

    - The `<vrang.org.name>` attribute from the `settings.xml` Maven profile that you are using.
    - The `vrang_org_name` property that you provide to the `installer` script.

## Environment Connection Parameters

You need to add the following configurations to the Maven profile from the `settings.xml` file that you intend to use for a project.

``` xml
<!-- (1)! -->
<profile>
    <!--    ..... OTHER DIRECTIVES .....  -->
    <vrang.host>flt-auto01.corp.internal</vrang.host>
    <vrang.csp.host>cloud.corp.internal</vrang.csp.host>
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

1. {{ archetype.customer_project.maven_settings_location_hint}}

To specify the Maven profile that you want to use for an operation, pass its name with the `-P` options as shown in the following example.

```bash
mvn {{ page.meta.vars.maven.goal }}:pull -P{{ archetype.customer_project.maven_profile_name}}
```

Following is a list of elements from the profile configuration with further description of their values and the behavior they cause.

- In the `vrang.username` element for VCF Automation VM Apps organizations, instead of using the `<vrang.tenant>` element for the organization, you can provide the user name in the format `user@domain` (or more precisely, `user@organization`). For example, use `admin@System` for the `admin` user from the Provider organization (the `System` domain identifies the Provider organization) or use `configurationadmin@Classic` for a `configurationadmin` user from an organization with the name `Classic`.

- In the `vrang.refresh.token` element, provide a refresh token that you want to use instead of user credentials for authentication.

    !!! note
        Refresh token takes precedence over credentials.

- In the `vrang.bp.unrelease.versions` optional element, specify whether old versions of a blueprint should be unreleased. The default value is `true`.

- In the `vrang.import.timeout` optional element, specify the timeout in milliseconds for syncing Catalog Item objects before performing additional operations (such as attaching Custom Forms, Icons, etc.). The default value is `6000`.

- In the `vrang.data.collection.delay.seconds` optional element, specify the delay in seconds for waiting for the data collection from the Orchestrator instance to {{ products.vra_9_short_name }} before importing data. You can also pass this configuration as an interactive parameter with the `-D` option as `-Dvrang.data.collection.delay.seconds=600`.

    Use this configuration when the {{ products.vra_9_short_name }} project contains references to Orchestrator workflows and dynamic types that you are importing together with the {{ products.vra_9_short_name }} content for the project since {{ products.vra_9_full_name }} needs to retrieve the details of the Orchestrator content first before it can create the {{ products.vra_9_short_name }} objects that point to the Orchestrator content. The default value is `0` or no delay.

    !!! Note

        Note that if you provide a delay value, {{ general.bta_name }} forces data collection via REST API and if it completes successfully, the delay is skipped and the provided delay time is disregarded. In case the data collection fails, however, the delay time is used and a wait timer is triggered.

- In the `vrang.org.name` element, you always need to specify the organization name. The `vra-ng` project is scoped to a single organization.

## Operations

This section describes the operations that you can perform with the VCF Automation project for a VM Apps organization.

<!-- Build Project Section -->
{% include-markdown "../../../../assets/docs/mvn/build-project.md" %}

The result of the command is a file with the name **{{ archetype.customer_project.group_id}}.{{ archetype.customer_project.artifact_id}}-1.0.0-SNAPSHOT.vra-ng** that is generated in the `target` directory of the project.

### Pull Content

This section describes the pulling of content for the project.

#### Overview

When working with a {{ products.vra_9_full_name }} project for a VM Apps organization, you mainly make content changes on a live server using the {{ products.vra_9_short_name }} user interface and then you capture those changes in the Maven project on your filesystem so that you can store the content, track changes, collaborate, etc.

#### Usage

To pull content from a remote server, use the `vra-ng:pull` custom Maven goal. The following command pulls the content defined in the *Content Descriptor* file to the current project from a specified server and expands its content in the local filesystem by overriding any local content.

```bash
mvn {{ page.meta.vars.maven.goal }}:pull -P{{ archetype.customer_project.maven_profile_name}}
```

!!! warning
    The command fails if the `content.yaml` content descriptor file is empty or if {{ general.bta_name }} cannot find any of the described content on the target {{ products.vra_9_short_name }} server.

!!! note
    If a catalog item has a custom form and/or an icon, they are exported in subdirectories of the `catalog-items` directory of the project.

!!! note
    Use the value of the `<vrang.vro.integration>` element in the Maven profile to specify the integration endpoint that you want to use for Orchestrator resources in the project (such as workflows exposed as catalog items). If the property is missing, {{ general.bta_name }} uses the default name `embedded-VRO`.

#### Additional Parameters

In the Maven command, you can pass additional parameters as flags with the `-D` option to override profile properties, such as `mvn {{ page.meta.vars.maven.goal }}:pull -P{{ archetype.customer_project.maven_profile_name}} -Dbp.ignore.versions=true`, where the value of the `bp.ignore.versions` parameter ignores blueprint versioning (for details, see the *Version Management* section in [Blueprints](Blueprints.md#version-management)). This option defaults to `false` but when dealing with blueprint development, you can set this option to `true` to avoid unnecessary blueprint versions.

<!-- Push Content Section -->
{% include-markdown "../../../../assets/docs/mvn/push-content.md" %}

#### Additional Parameters

In the Maven command, you can pass additional parameters as flags with the `-D` option, such as `mvn clean package -P{{ archetype.customer_project.maven_profile_name}} -Dvrang.bp.release=false`, where the `Dvrang.bp.release` parameter creates a new version of an already-released blueprint (see the *Version Management* section in [Blueprints](Blueprints.md#version-management)). This option defaults to `true` but when dealing with blueprint development, you can set this option to `false` to avoid unnecessary blueprint versions.

!!! note
    If there are custom forms in the `custom-forms` directory that are associated with workflows and catalog items, they are imported to the {{ products.vra_9_short_name }} server as well along with the content sources that are associated with them (they are read from the `content-sources` directory).

<!-- Bundle Project Section -->
{% include-markdown "../../../../assets/docs/mvn/bundle-project.md" %}

### Release

To release specific content that is available on a live server, you can use the `vrealize:release` command as shown in the following example.

```bash
mvn clean package vrealize:release -P{{ archetype.customer_project.maven_profile_name}} -Dvrang.contentType=blueprint -Dvrang.contentNames=testBlueprint -Dvrang.version=1 -DreleaseIfNotUpdated=false
```

Note that the only required parameter in this command is `vrang.version`. The following list explains the default behavior for other parameters from the command.

- The `vrang.contentType` parameter defaults to the value `all` that releases all supported content types.
- The `vrang.contentNames` parameter defaults to the value `[]` that releases all content of the given types on the server.
- The `vrang.releaseIfNotUpdated` parameter defaults to the value `false` that skips content if there are no updates since latest version.

!!! note
    Nothing will be released if any of the content on the server already has a release with the version that you specify in the `vrang.version` parameter.

<!-- Clean Up Content Section -->
{% include-markdown "../../../../assets/docs/mvn/clean-up-content.md" %}

<!-- Troubleshooting Section -->
{% include-markdown "../../../../assets/docs/mvn/troubleshooting.md" %}
