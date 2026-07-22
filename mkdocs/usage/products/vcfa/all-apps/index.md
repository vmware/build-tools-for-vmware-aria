---
title: All Apps Organization Project
---

## Overview

| Field                  | Value                                                 |
|------------------------|-------------------------------------------------------|
| Name                   | vcfa-all-apps                                         |
| Archetype Group ID     | com.vmware.pscoe.vcfa-all-apps.archetypes             |
| Archetype Artifact ID  | package-vcfa-all-apps-archetype                       |
| Package extension      | vcfaa                                                 |
| Product compatibility  | {{ extra.products.vra_9_all_apps_full_name }} (9.x)   |

{{ products.vra_9_full_name }} projects for All Apps organizations are called `vcfa-all-apps` projects in **Build Tools for VMware Aria**. The project type is a representation of {{ products.vra_9_short_name }} content into human friendly YAML and/or JSON format. The project consist of a content descriptor and a content container.

- The *Content Descriptor* defines what part of {{ products.vra_9_short_name }} content is part of the project in the `content.yaml` file.
- The *Content Container* holds the actual content representation in the `./src` directory.

## Supported Content

Following is a list of the supported content for All Apps organization projects.

- `blueprint`
- `property-group`
- `custom-resource`
- `resource-action`
- `workflow`
- `subscription`
- `policy`
- `scenario`

## Create New {{ products.vra_9_short_name }} Project for All Apps Organizations

{{ general.bta_name }} provides ready-to-use {{ products.vra_9_short_name }} project templates (*maven archetypes*) for All Apps organizations.

To create a new {{ products.vra_9_short_name }} project for All Apps from the archetype, use the following command.

{% include "../../reusables/archetype-generate.md" %}

### Content Structure

The command for creating a project produces the following project file structure.

```ascii
catalog
├── README.md
├── content.yaml
├── pom.xml
├── release.sh
└── src
    └── main
        └── blueprints
            └── Blueprint1
                └── content.yaml
                └── details.json
                └── Blueprint1__FormData.json
                └── styles.css
        └── custom-resources
            └── CustomResource1
                └── details.json
        └── policies
            └── approval
                └── ApprovalPolicy1.json
            └── day2-actions
                └── Day2ActionsPolicy1.json
            └── iaas-resource
                └── IaasResourcePolicy1.json
            └── lease
                └── LeasePolicy1.json
        └── property-groups
            └── PropertyGroup1.json
        └── resource-actions
            └── ResourceAction1
                └── details.json
                └── ResourceAction1__FormData.json
                └── styles.css
        └── scenarios
            └── Scenario1
                └── details.json
                └── template.html
        └── subscriptions
            └── Subscription1.json
        └── workflows
            └── Workflow1.json
```

## Project Content

The following sections give details about the project content.

### Content Descriptor

The Content Descriptor is implemented by a `content.yaml` file with the following structure.

```yaml
blueprint:
property-group:
custom-resource:
resource-action:
workflow:
subscription:
policy:
  approval:
  day2-actions:
  iaas-resource:
  lease:
scenario:
```

!!! note
    {{ products.vra_9_short_name }} Project for All Apps supports only the content types outlined in the Content Descriptor.

To capture the state of your {{ products.vra_9_short_name }} environment, fill in the names of the content objects and use the commands described in the [Pull Content](#pull-content) section.

For more information on each component, please refer to the corresponding sub-section.

#### Content Filtering

Following is a list of the rules for managing project contents based on the contents of the content descriptor for each category type. Note that these rules apply to both types operation types, export (pulling contents) and import (pushing contents).

- Empty array (`[]`) - nothing from the object type is processed.
- List of items - only the listed items from the object type are processed. If an object is not present on the server, an exception is thrown.
- Null (no value) - everything from the object type is processed.

!!! note
    If the given objects are not found on the server, an error is thrown.

##### Content Filtering Rule Example

Following is a sample listing of a content descriptor for a project with examples of how the filtering rules apply for each category type.

??? "Content Descriptor"
    ```yaml
    blueprint:                # exports and imports all blueprint/template objects
    property-group:           # exports and imports property group objects listed in the filter
      - memory
    custom-resource:          # exports and imports all custom resource objects
    resource-action:          # exports and imports all resource action objects
    workflow:                 # exports and imports workflow objects listed in the filter
      - Some Workflow
    subscription:             # exports and imports all EBS subscription objects
    policy:                   # exports and imports policy objects listed in the filter
      approval:               # exports and imports approval policy objects listed in the filter
        - blueprint_approval
      day2-actions: []        # does not export or import day-2 action policy objects
      iaas-resource: []       # does not export or import IaaS resource policy objects
      lease: []               # does not export or import lease policy objects
    scenario: []              # does not export or import scenario/notification objects
    ```

### ID Handling

You should not rely on IDs exported by Build Tools for VMware Aria. Where needed, Build Tools for VMware Aria removes such IDs or in some cases it fetches data from the remote server, modifies it in flight, and pushes to the server (in case an import is needed).

### Single Project And Single Organization

When you are working with the `vcfa-all-apps` project type, each project that you generate is intended to work with *only a single project and and a single organization*. In case you need to work with more that one project or organization, you need to generate a separate Maven project for each of them.

For every object type that contains the `organization` or the `projectId` key in the JSON definition (such as {{ products.vra_9_short_name }} policies), the following behavior applies.

- If the JSON definition contains the `projectId` key with a value, the value is replaced by the project ID value that you defined in one of the following sources (based on the import mechanism that you use).

    - The `<vcfa.project.name>` attribute from the `settings.xml` Maven profile that you are using.
    - The `vcfa_project_name` property that you provide to the `installer` script.

- If the JSON definition contains the `organization` key with a value, the value is replaced by the organization ID value that you defined in one with the following sources (based on the import mechanism that you use) and content object is imported with the scope option to make it available for any project within the organization or the whole organization.

    - The `<vcfa.org.name>` attribute from the `settings.xml` Maven profile that you are using.
    - The `vcfa_org_name` property that you provide to the `installer` script.

## Environment Connection Parameters

You need to add the following configurations to the Maven profile from the `settings.xml` file that you intend to use for a project.

```xml
<!-- (1)! -->
<profile>
    <!--    ..... OTHER DIRECTIVES .....  -->
    <vcfa.host>flt-auto01.corp.internal</vcfa.host>
    <vcfa.csp.host>cloud.corp.internal</vcfa.csp.host>
    <vcfa.proxy>http://proxy.host:80</vcfa.proxy>
    <vcfa.port>443</vcfa.port>
    <vcfa.username>administrator@tenant</vcfa.username>
    <vcfa.password>someSecurePassword</vcfa.password>
    <vcfa.project.name>{project+name}</vcfa.project.name>
    <vcfa.org.name>{tenant}</vcfa.org.name>
    <vcfa.refresh.token>{refresh+token}</vcfa.refresh.token>
    <vcfa.bp.unrelease.versions>true|false</vcfa.bp.unrelease.versions>
    <vcfa.vro.integration>{vro+integration+name}</vcfa.vro.integration>
    <vcfa.import.timeout>{import+timeout}</vcfa.import.timeout>
    <vcfa.data.collection.delay.seconds>{data+collection+delay}</vcfa.data.collection.delay.seconds>
</profile>
```

1. {{ archetype.customer_project.maven_settings_location_hint}}

To specify the Maven profile that you want to use, passing its name with the `-P` options as shown in the following example.

``` bash
mvn vcfa-all-apps:pull -P{{ archetype.customer_project.maven_profile_name}}
```

<!-- this comment is used to properly visualize the unordered list below -->

Following is a list of elements from the profile configuration that provide further description of their values and the behavior they cause.

- In the `vcfa.username` element for VCF Automation All Apps organizations, you need to provide the user name in the format `user@domain`, or more precisely, `user@organization`. For example, use `admin@System` for the `admin` user from the Provider organization (the "System" domain identifies the Provider organization) or use `configurationadmin@AllApps` for a `configurationadmin` user from an organization with the name `AllApps`.

- In the `vcfa.refresh.token` element, provide a refresh token that you want to use instead of user credentials.

    !!! note
        Refresh token takes precedence over credentials.

- In the `vcfa.bp.unrelease.versions` optional element, specify whether old versions of a blueprint should be unreleased. The default value is `true`.

- In the `vcfa.import.timeout` optional element, specify the timeout in milliseconds for syncing Catalog Item objects before performing additional operations (such as attaching Custom Forms, Icons, etc.). The default value is `6000`.

- In the `vcfa.data.collection.delay.seconds` optional element, specify the delay in seconds for waiting for the data collection from {{ products.vro_9_full_name }} to {{ products.vra_9_short_name }} before importing data. You can also pass this configuration as an interactive parameter `-Dvcfa.data.collection.delay.seconds=600`.

    Use this configuration when the {{ products.vra_9_short_name }} project contains references to Orchestrator workflows and dynamic types that you are importing together with the {{ products.vra_9_short_name }} content for the project since {{ products.vra_9_full_name }} needs to retrieve the details of the Orchestrator content in order to be able to create the {{ products.vra_9_short_name }} objects that point to the Orchestrator content. The default value is `0` or no delay.

    !!! note
        Note that if you provide a delay value, Build Tools for VMware Aria forces data collection via REST API and if it completes successfully, the delay is skipped and the provided delay time is disregarded. In case the data collection fails, however, the delay time is used and a wait timer is triggered.

- In the `vcfa.org.name` element, you always need to specify the organization name. The `vcfa-all-apps` project is scoped to a single organization.

## Operations

This section describes the operations that you can perform with the {{ products.vra_9_full_name }} project for an All Apps organization.

<!-- Build Project Section -->
{% include-markdown "../../../../assets/docs/mvn/build-project.md" %}
The output of the command will result in **{{ archetype.customer_project.group_id}}.{{ archetype.customer_project.artifact_id}}-1.0.0-SNAPSHOT.vcfaa** file generated in the target folder of the project.

<!-- Bundle Project Section -->
{% include-markdown "../../../../assets/docs/mvn/bundle-project.md" %}

### Pull Content

This section describes the pulling of content for the project.

#### Overview

When working with a {{ products.vra_9_full_name }} project for an All Apps organization, you mainly make content changes on a live server using the {{ products.vra_9_short_name }} user interface and then you capture those changes in the Maven project on your filesystem so that you can store the content, track changes, collaborate, etc.

#### Usage

To support this use case, a custom Maven goal `vcfa-all-apps:pull` is used. The following command pulls the content defined in the *Content Descriptor* file to the current project from a specified server and expands its content in the local filesystem by overriding any local content.

```bash
mvn vcfa-all-apps:pull -P{{ archetype.customer_project.maven_profile_name}}
```

!!! warning
    The command fails if the `content.yaml` content descriptor file is empty or if Build Tools for VMware Aria cannot find any of the described content on the target {{ products.vra_9_short_name }} server.

!!! note
    Use the `<vcfa.vro.integration>` element in the Maven profile to specify the integration endpoint that you want to use for Orchestrator resources in the project (such as workflows exposed as catalog items). If the property is missing, the default name `embedded-VRO` is used.

#### Additional Parameters

In the Maven command, you can pass additional parameters as flags with the `-D` option to override profile properties, such as `mvn vcfa-all-apps:pull -P{{ archetype.customer_project.maven_profile_name}} -Dbp.ignore.versions=true`, where the value of the `bp.ignore.versions` parameter ignores blueprint versioning (for details, see the *Version Management* section in [Blueprints](Blueprints.md#version-management)). This option defaults to `false` but when dealing with blueprint development, you can set this option to `true` to avoid unnecessary blueprint versions.

<!-- Push Content Section -->
{% include-markdown "../../../../assets/docs/mvn/push-content.md" %}

#### Additional Parameters

In the Maven command, you can pass additional parameters as flags with the `-D` option, such as `mvn clean package -P{{ archetype.customer_project.maven_profile_name}} -Dvcfa.bp.release=true`, where the `vcfa.bp.release` parameter creates a new version of an already-released blueprint (see the *Version Management* section in [Blueprints](Blueprints.md#version-management)). This option defaults to `true` but when dealing with blueprint development, you can set this option to `false` to avoid unnecessary blueprint versions.

### Release

To release specific content that is available on a live server, you can use the ```vrealize:release``` command as shown in the following example.

```bash
mvn clean package vrealize:release -P{{ archetype.customer_project.maven_profile_name}} -Dvcfa.contentType=blueprint -Dvcfa.contentNames=testBlueprint -Dvcfa.version=1 -DreleaseIfNotUpdated=false
```

Note that the only required parameter in this command is `vcfa.version`. The following list explains the default behavior for other parameters from the command.

- The `vcfa.contentType` parameter defaults to the value `all` that releases all supported content types.
- The `vcfa.contentNames` parameter defaults to the value `[]` that releases all content of the given types on the server.
- The `vcfa.releaseIfNotUpdated` parameter defaults to the value `false` that skips content if there are no updates since latest version.

!!! note
    Nothing will be released if any of the content on the server already has a release with the version that you specify in `vcfa.version`.

<!-- Clean Up Content Section -->
{% include-markdown "../../../../assets/docs/mvn/clean-up-content.md" %}

<!-- Troubleshooting Section -->
{% include-markdown "../../../../assets/docs/mvn/troubleshooting.md" %}
