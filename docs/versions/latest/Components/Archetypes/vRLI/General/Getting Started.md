# Getting Started

## Overview

The Maven Archetype supports vRLI / Aria Operations for Logs 8.x / VCF Operations for Logs 9.x content. It is a representation of Operations for Logs content into human friendly JSON format. The project consist of content descriptor and content container.

- *Content Descriptor* defines what part of the Operations for Logs server content will be part of this project - `content.yaml`
- *Content Container* holds the actual content representation -`./src` folder

## Table Of Contents

1. [Maven Archetype](#maven-archetype)
2. [Configuring settings.xml](#configuring-m2settingsxml-to-work-with-vrli)

### Maven Archetype

**Build Tools for VMware Aria** provides ready to use project templates (*maven archetypes*).

To create a new Operations for Logs project from archetype use the following command:

```Bash
mvn archetype:generate \
    -DinteractiveMode=false \
    -DarchetypeGroupId=com.vmware.pscoe.vrli.archetypes \
    -DarchetypeArtifactId=package-vrli-archetype \
    -DarchetypeVersion=<build_tools_for_aria_version> \
    -DgroupId=org.example \
    -DartifactId=sample
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
            └── alerts
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
```

**Note**: *vrli Project supports only content types outlined into content descriptor.*

To capture the state of your Operations for Logs environment simply fill in the names of the content objects you would like to capture and look at the [Pulling operation](../Operations/Pulling.md) documentation.

### Configuring ~/.m2/settings.xml to work with vrli

The following need to be added to the profile that you intend to use:

```xml
 <!--            vrli    -->
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

- `vrli.vrops*` parameters are used for retrieving or updating data related to VCF Operations enabled alerts.

- `vrli.provider` - specifyies the authentication provider used to connect to the vRLI / VCF Operations for Logs server. Currently supported providers are Local, active directory and VIDM.
