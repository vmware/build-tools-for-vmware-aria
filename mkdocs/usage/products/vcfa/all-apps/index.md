---
title: All Apps Tenant Project
---

## Overview
<!-- markdownlint-disable MD033 -->

| Field | Value |
|---|---|
| Name | vcfa-all-apps |
| Archetype Group ID | com.vmware.pscoe.vcfa-all-apps.archetypes |
| Archetype Artifact ID | package-vcfa-all-apps-archetype |
| Package extension | vcfaa |
| Product compatibility | {{ extra.products.vra_9_all_apps_full_name }} (9.x) |
<!-- markdownlint-enable MD033 -->

{{ products.vra_9_short_name }} projects for All-Apps tenants are called `vcfa-all-apps` projects in **Build Tools for VMware Aria**. The project type is a representation of {{ products.vra_9_short_name }} content into human friendly YAML and/or JSON format. The project consist of content descriptor and content container.

- *Content Descriptor* defines what part {{ products.vra_9_short_name }} content will be part of this project - `content.yaml`.
- *Content Container* holds the actual content representation - `./src` folder.

## Supported Content

- `blueprint`
- `property-group`
- `custom-resource`
- `resource-action`
- `workflow`
- `subscription`
- `policy`
- `scenario`

## Create New {{ products.vra_9_short_name }} Project
{{ general.bta_name }} provides ready to use {{ products.vra_9_short_name }} project templates (*maven archetypes*).

To create a new {{ products.vra_9_short_name }} project from archetype use the following command:

```Bash
mvn archetype:generate \
    -DinteractiveMode=false \
    -DarchetypeGroupId=com.vmware.pscoe.vcfa-all-apps.archetypes \
    -DarchetypeArtifactId=package-vcfa-all-apps-archetype \
    -DarchetypeVersion={{ iac.latest_release }} \ # (1)!
    -DgroupId={{ archetype.customer_project.group_id}} \ # (2)!
    -DartifactId={{ archetype.customer_project.artifact_id}} # (3)!
```

1. {{ archetype.customer_project.archetype_version_hint }}
2. {{ archetype.customer_project.group_id_hint }}
3. {{ archetype.customer_project.artifact_id_hint }}

!!! note
    If **build_tools_for_aria_version** is not specified a default value of 2.38.1 will be used.

!!! note
    Make sure to remove any trailing spaces after the backslashes (**\\**) otherwise the command will fail.

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
                └── AprovalPolicy1.json
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

## Content

### Content Descriptor

Content Descriptor is implemented by `content.yaml` file with the following structure:

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
  iass-resource:
  lease:
scenario:
```

!!! note
    {{ products.vra_9_short_name }} Project supports only content types outlined into Content Descriptor.

To capture the state of your {{ products.vra_9_short_name }} environment simply fill in the names of the content objects and follow the [Pull Content](#pull-content) section.

For more information on each component please refer to corresponding sub-section.

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
    property-group:  # export according to filter
      - memory
    custom-resource: # will export all
    resource-action: # will export all
    workflow: # export according to filter
      - Some Workflow
    subscription: # will export all
    policy:  # export according to filter
      approval: [] # export according to filter
        - blueprint_approval
      day2-actions: [] # will export none
      iaas-resource: [] # will export none
      lease: [] # will export none
    scenario: [] # will export none
    ```

!!! note
    There are two types of items in Catalog:
    - Blueprints, defined in blueprints folder with custom form and automatically published to Catalog upon import
    - Workflows, defined in a ts or xml project and published to Catalog if defined in workflows folder.

### ID handling

Relying on ids exported by Build Tools for VMware Aria should not happen. Build Tools for VMware Aria will remove where needed such ids or in some cases data will be fetched from the remote server, modified in flight and pushed to the server ( in cases where an import is needed ).

### Single Project And Single Organization

When working with vcfa-all-apps project type, each generated project is intended to work with **only one project and one organization**. In a case where more are needed, you must generate multiple projects.

For every object type that contains `organization` or `projectId` key in the JSON definition (e.g. Policies) the following behaviour applies:

- if the JSON definition contains `projectId` key and value defined, the value is replaced by the project defined in (based on import mechanism used):
    - `<vcfa.project.name>` from the selected `settings.xml` Maven profile.
    - `vcfa_project_name` provided to the `installer` script.
- if the JSON definition contains `organization` key and value defined, the object is imported with the option of `Available for any project` or `Organization` enabled for the specific organization (the exact option name differentiates in UI based on product version) defined in (based on import mechanism used):
    - `<vcfa.org.name>` from the selected `settings.xml` Maven profile.
    - `vcfa_org_name` provided to the `installer` script.

## Environment Connection Parameters

The following need to be added to the profile that you intend to use:

``` xml
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

<!-- this comment is used to properly visualise the unordered list below -->

- `vcfa.username` - For VCF 9 Automation - All-Apps organization
you need to provide username in the following format: user@domain. E.g.:
    - admin@System - Provider admin (the "System" domain is used to identify the user as Provider admin)
    - configurationadmin@AllApps - AllApps organization admin

- `vcfa.refresh.token` - uses the given refresh token instead of credentials.

!!! note
    Refresh token takes precedence over credentials.

- `vcfa.bp.unrelease.versions` - Defaults to `true`. Controls whether old versions of a blueprint sould be unreleased.

- `vcfa.import.timeout` - Timeout in miliseconds when syncing from Content Source for Catalog Items to appear before performing additional operations (e.g. attaching Custom Forms, Icons, etc.). Default value is 6000.

- `vcfa.data.collection.delay.seconds` - Delay in seconds to wait for vRA data collection to pass before importing data. Can also be passed as an interactive parameter `-Dvcfa.data.collection.delay.seconds=600`. useful when Dynamic types and custom resources are used in the projects and vRO content is imported, however vRA needs to then retrieve it in order to be able to create the custom Resource and use the Create/Delete Workflows. This only happens after a short delay and the vRA data collector scrapes vRO. Defaults to no delay.
    - if a value is provided data collection is forced via REST API and if it completes successfully the provided delay time is skipped. In case the data collection fails, the delay is triggered.

- `vcfa.org.name` - needs to be specified. The `vcfa-all-apps` project is scoped to a single organization.

Use the profile by passing it with `-P`, e.g.:
``` bash
mvn vcfa-all-apps:pull -P{{ archetype.customer_project.maven_profile_name}}
```

## Operations

<!-- Build Project Section -->
{% include-markdown "../../../../assets/docs/mvn/build-project.md" %}
The output of the command will result in **{{ archetype.customer_project.group_id}}.{{ archetype.customer_project.artifact_id}}-1.0.0-SNAPSHOT.vcfaa** file generated in the target folder of the project.

<!-- Bundle Project Section -->
{% include-markdown "../../../../assets/docs/mvn/bundle-project.md" %}

### Pull Content

#### Overview

When working on a {{ products.vra_9_short_name }} project, you mainly make changes on a live server using the {{ products.vra_9_short_name }} UI and then you need to capture those changes in the maven project on your filesystem to be able to store the content, track changes, collaborate, etc.

#### Usage

To support this use case, a custom maven goal `vcfa-all-apps:pull` is used. The following command will `pull` the content outlined into *Content Descriptor* file to the current project from a specified server and expand its content in the local filesystem overriding any local content:

```bash
mvn vcfa-all-apps:pull -P{{ archetype.customer_project.maven_profile_name}}
```

!!! warning
    The command will fail if the `content.yaml` is empty or it cannot find some of the described content on the target {{ products.vra_9_short_name }} server.

!!! note
    The value of the `<vcfa.vro.integration>` is used to change the integration endpoint of Workflow Content Sources and other resources that point to that type of integration. If the property is missing a default name "embedded-VRO" will be used.

#### Additional Parameters

Additional parameters that can be passed as flags to the maven command, e.g. `mvn vcfa-all-apps:pull -Dbp.ignore.versions=true`.

- `bp.ignore.versions` - ignores blueprint versioning  (refer to the *Blueprint Versioning* section). This option defaults to `false`. When dealing with blueprint development, you might want to set this to `true` in order to avoid unnecessary blueprint versions.

<!-- Push Content Section -->
{% include-markdown "../../../../assets/docs/mvn/push-content.md" %}

#### Additional Parameters

Additional parameters that can be passed as flags to the maven command, e.g. `mvn clean package -Dvcfa.bp.release=true`.

- `vcfa.bp.release` - create a new version for already released blueprint (refer to the *Blueprint Versioning* section). This option defaults to `true`. When dealing with blueprint development, you might want to set this to `false`
in order to avoid unnecessary blueprint versions.

### Release
To release a specific content uploaded on a live server, you can use the ```vrealize:release``` command:

```bash
mvn clean package vrealize:release -Pcorp-env -Dvcfa.contentType=blueprint -Dvcfa.contentNames=testBlueprint -Dvcfa.version=1 -DreleaseIfNotUpdated=false
```

Only parameter vcfa.version is required.
Defalut behavior for other parameters:
    - vcfa.contentType: default value "all". Releases all supported content types.
    - vcfa.contentNames: default value "[]". Releases all content of given types on server.
    - vcfa.releaseIfNotUpdated: default value "false". Skips content if there are no updates since latest version.

!!! note
    Nothing will be released if any of the content already has the given version existing.


<!-- Clean Up Content Section -->
{% include-markdown "../../../../assets/docs/mvn/clean-up-content.md" %}

<!-- Troubleshooting Section -->
{% include-markdown "../../../../assets/docs/mvn/troubleshooting.md" %}
