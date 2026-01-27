# Getting Started

## Overview

The Maven Archetype supports Aria Operations 8.x and VCF Operations 9.x content. It is a representation of Operations content into human friendly format saved into different file types - JSON, XML, properties, ZIP. The project consist of content descriptor and content container.

- *Content Descriptor* defines what part of the Operations server content will be part of this project - `content.yaml`
- *Content Container* holds the actual content representation -`./src` folder

## Table Of Contents

1. [Maven Archetype](#maven-archetype)
2. [Configuring settings xml](#configuring-m2settingsxml-to-work-with-vrops)

### Maven Archetype

**Build Tools for VMware Aria** provides ready to use project templates (*maven archetypes*).

To create a new Operations project from archetype use the following command:

```Bash
mvn archetype:generate \
    -DinteractiveMode=false \
    -DarchetypeGroupId=com.vmware.pscoe.vrops.archetypes \
    -DarchetypeVersion=<build_tools_for_aria_version> \
    -DarchetypeArtifactId=package-vrops-archetype \
    -DgroupId=local.corp.it.cloud \
    -DartifactId=operations
```

**Note**: *If <build_tools_for_aria_version> is not specified a default value of 2.37.0 will be used.*

#### Content Structure

This command will produces the following project file structure:

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

**Note**: *vrops Project supports only content types outlined into content descriptor.*

To capture the state of your vROps / VCF Operations environment simply fill in the names of the content objects you would like to capture and look at the [Pulling operation](../Operations/Pulling.md) documentation.

### Configuring ~/.m2/settings.xml to work with vrops

The following need to be added to the profile that you intend to use:

```xml
 <!--            vrops    -->
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
    <vrops.restAuthProvider>BASIC)AUTH_N</vrops.restAuthProvider>
</profile>
```

Note that some of Operation content is managed via in guest CLI commands instead of REST API which requires credentials for REST API (`vrops.restUser` and `vrops.password`) and SSH communication (`vrops.username` and `vrops.password`) with sufficient privileges.

- `vrops.dashboardUser` - User account to which to assign the ownership of a dashboard when importing it.

- `vrops.importDashboardsForAllUsers` - If parameter is missing or set to *true*, the dashboards are imported to all users. If parameter is set to *false*, the dashboards are imported only for the user specified in vrops.dashboardUser.

- `vrops.restAuthSource` - Authentication source used for acquiring a token for REST API communication.

- `vrops.restAuthProvider` - Defines the type of authentication used for REST API communication.
  - Supported values: BASIC, AUTH_N (token based authentication)
  - Default value: AUTH_N

