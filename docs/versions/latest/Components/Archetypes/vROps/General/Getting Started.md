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

The following represents sample project file structure:

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
                └── VCF World.json
            └── dashboards
                └── metadata
                    └── dashboardGroupActivationMetadata.vrops.json
                    └── dashboardSharingMetadata.vrops.json
                    └── dashboardUserActivationMetadata.vrops.json
                └── resources
                    └── resources.properties
                └── Assess Cost.json
            └── metricconfigs
                └── vSAN Savings.xml
            └── policies
                └── Policy for Virtual Machines - Risk Profile 1.zip
                └── policiesMetadata.vrops.json
            └── recommendations
                └── Allocate more disk space if required.json
            └── reports
                └── Cluster Cost Report
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

### Configuring ~/.m2/settings.xml to work with vrops

The following need to be added to the profile that you intend to use:

```xml
 <!--            vrops    -->
<profile>
<!--    ..... OTHER DIRECTIVES .....  -->
    <vrops.host>vcfop-master.corp.internal</vrops.host>
    <vrops.sshHost>vcfop-master.corp.internal</vrops.sshHost>
    <vrops.port>443</vrops.port>
    <vrops.username>admin</vrops.username>
    <vrops.password>someSecurePassword</vrops.password>
    <vrops.restUser>admin</vrops.restUser>
    <vrops.restPassword>someSecurePassword</vrops.restPassword>
    <vrops.dashboardUser>admin</vrops.dashboardUser>
    <vrops.sshPort>22</vrops.sshPort>
    <vrops.httpPort>443</vrops.httpPort>
    <vrops.restAuthSource>local</vrops.restAuthSource>
</profile>
```
