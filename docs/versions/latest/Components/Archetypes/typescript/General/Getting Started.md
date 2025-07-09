[//]: # (DEFAULT TEMPLATE, Used if no others match)

[//]: # (Remove Comments when you are done)
[//]: # (What is this?)

# Getting Started

[//]: # (Additional Information on the topic goes here)

[//]: # (What will you learn)
[//]: # (Optional)

## Overview

[//]: # (Internal navigation)
[//]: # (Navigational links may have a short description after them separated by a `-`)

## Table Of Contents

1. [Maven Archetype](#maven-archetype)
3. [Configuring settings.xml](#configuring-m2settingsxml-to-work-with-orchestrator-project-types)
2. [Properties](#properties)

### Maven Archetype

**Build Tools for VMware Aria** provides ready to use project templates (*maven archetypes*).

To create a new vRA 8.x project from archetype use the following command:

```Bash
mvn archetype:generate \
    -DinteractiveMode=false \
    -DarchetypeGroupId=com.vmware.pscoe.o11n.archetypes \
    -DarchetypeArtifactId=package-typescript-archetype \
    -DarchetypeVersion=<build_tools_for_aria_version> \
    -DgroupId=local.corp.it.cloud \
    -DartifactId=service-automation
```

**Note**: *If <build_tools_for_aria_version> is not specified a default value of 2.38.1 will be used.

### Configuring ~/.m2/settings.xml to work with Orchestrator project types

The following need to be added to the profile that you intend to use:

```xml
<profile>
    <!--    ..... OTHER DIRECTIVES .....  -->
    <vro.host>vra-l-01a.corp.local</vro.host>
    <vro.auth>vra</vro.auth>
    <vro.authHost>vra-l-01a.corp.local</vro.authHost>
    <vro.authPort>443</vro.authPort>
    <vro.port>443</vro.port>
    <vro.username>configurationadmin</vro.username>
    <vro.password>someSecurePassword</vro.password>
</profile>
```

- `vro.username` - For vCF 9 Orchestrator you need to provide username in the following format: user@domain. E.g.:
  admin@System - Provider admin
  configurationadmin@Classic - Classic organization admin

### Properties

#### skipInstallNodeDeps

Add the `skipInstallNodeDeps` flag to skip the deletion and re-installation of node-deps. Ex: `mvn clean package -DskipInstallNodeDeps=true`.

Note: If node_modules folder doesn't exist, then this flag is ineffective.

#### -Dvro.forceImportLatestVersions

This strategy will force you to upload the same or newer version of a package, otherwise it will fail the build, allowing us for better CI/CD pipelines, where we can ensure that the latest versions are always used on the server.

#### vroIgnoreFile

This property can be added in the pom.xml file of a vRO project to specify a custom vRO ignore file.
If left unspecified, the default name .vroignore will be used.
Note that in Linux dot files have a special status, use a custom name in case of issues.
If the file with the given name doesn't exist, it will be generated with default content.
If it esists but does not match the expected file structure, it will be modified.

Example excerpt from vRO project pom.xml:
```xml
  <properties>
    <vroIgnoreFile>customVroignoreFile</vroIgnoreFile>
    <!-- other properties -->
  </properties>

```

Default .vroignore file structure:
```md
# General
## This file contains glob patterns for file paths to be ignored during compilation, packaging and testing.
## The file will be (re)generated when the project is rebuilt to maintain the default categories and patterns, ignoring blank lines and repeating comments.
## The default categories, defined as rows starting with a single '#', are 'General', 'Packaging', 'Compilation', 'Testing', 'TestHelpers'.
## Patterns not under one of those categories will be considered in the 'General' category and ignored during all operations.
## Patterns must be listed on separate rows (without in-line comments), can be negated with a single '!' at the start and will be trimmed before processing.
## The 'TestHelpers'category will contain these two patterns by default: '**/*_helper.js', '**/*.helper.[tj]s'
## Rows starting with '##' are considered as comments and will be ignored on processing.
# Packaging
## Files with these paths will not be included in the vro package.
## For Workflows, Configurations, Resources and Policies: it is recommended to use the 'General' category (will skip xml element generation).
## Otherwise the patterns below must be based on the _element name_.element_info.xml files in target/vro-sources/xml.
# Compilation
## Files with these paths will be compiled without TS definitions
# Testing
## Files with these paths will be excluded from test coverage
# TestHelpers
## Files with these paths will be included in the tests but will not have TS definitions and will not be included in the test coverage or the vro package
**/*_helper.js
**/*.helper.[tj]s
```
