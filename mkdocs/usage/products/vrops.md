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

!!! note
    Due to limitation of the {{ products.vrops_9_short_name }} CLI the import / export of report definitions is not currently supported.

!!! note
    The import of the symptoms definitions defined for all the adapter types is not currently supported.

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
## Environment Connection Parameters

The following need to be added to the profile that you intend to use:

``` xml
<!-- (1)! -->
<profile>
<!--    ..... OTHER DIRECTIVES .....  -->
    <vrops.host>flt-ops01a.corp.internal</vrops.host>
    <vrops.port>443</vrops.port>
    <vrops.sshPort>22</vrops.sshPort>
    <vrops.username>admin</vrops.username>
    <vrops.password>someSecurePassword</vrops.password>
    <vrops.restUser>admin</vrops.restUser>
    <vrops.restPassword>someSecurePassword</vrops.restPassword>
    <vrops.dashboardUser>admin</vrops.dashboardUser>
    <vrops.importDashboardsForAllUsers>true|false</vrops.importDashboardsForAllUsers>
    <vrops.restAuthSource>local</vrops.restAuthSource>
    <vrops.restAuthProvider>BASIC|AUTH_N</vrops.restAuthProvider>
</profile>
```

1.  {{ archetype.customer_project.maven_settings_location_hint}}

!!! note
    Some {{ products.vrops_9_short_name }} content is managed via in-guest CLI commands instead of REST API which requires credentials for REST API (`vrops.restUser` and `vrops.password`) and SSH communication (`vrops.username` and `vrops.password`) with sufficient privileges.

- `vrops.dashboardUser` - User account to which to assign the ownership of a dashboard when importing it.

- `vrops.importDashboardsForAllUsers` - If parameter is missing or set to *true*, the dashboards are imported to all users. If parameter is set to *false*, the dashboards are imported only for the user specified in vrops.dashboardUser.

- `vrops.restAuthSource` - Authentication source used for acquiring a token for REST API communication.

- `vrops.restAuthProvider` - Defines the type of authentication used for REST API communication.
  - Supported values: BASIC, AUTH_N (token based authentication - supported since version 2.8.0)
  - Default value: AUTH_N

Use the profile by passing it with `-P`, e.g.:
``` bash
mvn vrops:pull -P{{ archetype.customer_project.maven_profile_name}}
```

## Operations

<!-- Build Project Section -->
{% include-markdown "../../assets/docs/mvn/build-project.md" %}
The output of the command will result in **{{ archetype.customer_project.group_id}}.{{ archetype.customer_project.artifact_id}}-1.0.0-SNAPSHOT.vrops** file generated in the target folder of the project.

<!-- Bundle Project Section -->
{% include-markdown "../../assets/docs/mvn/bundle-project.md" %}

### Pull Content

#### Overview

When working on a {{ products.vrops_9_short_name }} project, you mainly make changes on a live server using the {{ products.vrops_9_short_name }} UI and then you need to capture those changes in the maven project on your filesystem to be able to store the content, track changes, collaborate, etc.

#### Usage

To support this use case, the a custom maven goal `vrops:pull` is used. The following command will `pull` the content outlined into *Content Descriptor* file to the current project from a specified server and expand its content in the local filesystem overriding any local content:

```bash
mvn vrops:pull -P{{ archetype.customer_project.maven_profile_name}}
```

!!! note
    The command will fail if the `content.yaml` is empty or it cannot find some of the described content on the target {{ products.vrops_9_short_name }} server.

#### Wildcard Support

The content descriptor supports wildcard for most of the asset types. This means that you can specify a wildcard (*) symbol in the asset names defined in the `content.yaml` file exporting all assets matching the wildcard expression. E.g.

```yaml
report:
  - "*reports"
```

!!! note
    Due to limitation of {{ products.vrops_9_short_name }} REST API wildcard is currently NOT supported for the dashboard and metric-config asset types.
!!! note
    If you specify a wildcard in the asset name defined in the content.yaml file, it needs to be enclosed with quotes ("). You can also enclose the asset name with quotes (") in the content.yaml file, even if you specify it with its full name.

### Push Content

#### Overview

Maven goal for deploying the solution to target environment.

#### Usage

```bash
mvn clean package vrops:push -P{{ archetype.customer_project.maven_profile_name}}
```

<!-- Push Common Section -->
{% include-markdown "../../assets/docs/mvn/push-content-common-parameters.md" %}

<!-- Clean Up Content Section -->
{% include-markdown "../../assets/docs/mvn/clean-up-content-unsupported.md" %}

<!-- Troubleshooting Section -->
{% include-markdown "../../assets/docs/mvn/troubleshooting.md" %}
 