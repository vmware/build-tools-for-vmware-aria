# VRLI Projects

Before you continue with this section validate that all of the prerequisites are met.
## Prerequisites
- Install and Configure [Build Tools for VMware Aria System](setup-workstation-maven.md)

## Use

VRLI Project is a file system representation of VRLI content into human friendly JSON format. The project consist of content descriptor and content container.

- *Content Descriptor* defines what part VRLI content will be part of this project.
- *Content Container* holds the actual content representation.

## Cerate New VRLI Project

**Build Tools for VMware Aria** provides ready to use project templates (*maven archetypes*).

To create a new VRLI project from archetype use the following command:
```Bash
mvn archetype:generate \
    -DinteractiveMode=false \
    -DarchetypeGroupId=com.vmware.pscoe.vrli.archetypes \
    -DarchetypeArtifactId=package-vrli-archetype \
    -DgroupId=org.example \
    -DartifactId=sample
```

**Note**: *The specified <iac_for_vrealize_version> should be minimum 2.5.4*

The result of this command will produce the following project file structure:
```
.
├── content.yaml
├── pom.xml
├── release.sh
├── src
│   └── main
│       └── resources
│           └── alerts
│               └── alert.json
│           └── content-packs
│               └── content-pack.json

```

Content Descriptor is implemented by content.yaml file with the following defaults.

**Note**: *VRLI Project supports only content types outlined into content descriptor.*

```
---
# 
# Example describes export of:
#   Alerts in src/alerts
#   Content packs in src/content-packs
alerts:
  - example*alerts*
  - *example*
  - alert 1
  - alert 2
  - alert 3
content-pack:
  - *example*content-packs*
  - content-pack 1
  - content-pack 2
  - content-pack 3

```

To capture the state of your VRLI environment simply fill in the names of the content objects you would like to capture and look at the Pull section of this document.

## Wildcard Support
The content file supports wildcard. This means that if a wildcard is present in the asset name, all assets matching the wildcard expression will be exported to the local file system. 
The example above shows how to use wildcard in the asset names.

## Building
You can build any VRLI project from sources using Maven:
```bash
mvn clean package
```

This will produce a VRLI package with the groupId, artifactId and version specified in the pom. For example:
```xml
<groupId>org.example</groupId>
<artifactId>sample</artifactId>
<version>1.0.0-SNAPSHOT</version>
<packaging>vrli</packaging>
```
will result in **org.example.sample-1.0.0-SNAPSHOT.vrli** generated in the target folder of your project.

## Pull
When working on a VRLI project, you mainly make changes on a live server using the VRLI Console and then you need to capture those changes in the maven project on your filesystem.

To support this use case, the toolchain comes with a custom goal "vrli:pull". The following command will "pull" the content outlined into *Content Descriptor* file to the current project from a specified server and expand its content in the local filesystem overriding any local content:
```bash
vrli:pull -Dvrli.host=api.mgmt.cloud.vmware.com -Dvrli.port=9543 -Dvrli.provider=Local
```
A better approach is to have the different VRLI development environments specified as profiles in the local
settings.xml file by adding the following snippet under "profiles":
```xml
<profile>
    <id>iac-vrli</id>
    <properties>
        <vrli.host>192.168.1.2</vrli.host>
        <vrli.port>9543</vrli.port>
        <vrli.username>admin</vrli.username>
        <vrli.password>VMware1!</vrli.password>
        <vrli.provider>Local</vrli.provider>
    </properties>
</profile>
```
Then, you can sync content back to your local sources by simply activating the profile:
```bash
mvn vrli:pull -Piac-vrli
```

> Note that ```vrli:pull``` will fail if the content.yaml is empty or it cannot find some of the described content 
on the target VRLI server.

## Push
To deploy the code developed in the local project or checked out from source control to a live server, you can use
the ```vrealize:push``` command:
```bash
mvn package vrealize:push -Piac-vrli
```
This will build the package and deploy it to the environment described in the ```iac-vrli``` profile. There are a few
additional options.

## Authentication
When executing command use a profile that has username/password and provider set. 

```bash
vrli:pull -Dvrli.host=api.mgmt.cloud.vmware.com -Dvrli.port=9543 -Dvrli.provider=Local -Dvrli.username={username} -Dvrli.password={password}
```

> Note that you need to specify the authentication provider used to connect to the VRLI server in the ```vrli.provider``` parameter. Currently supported providers are Local, active directory and VIDM. In this example the "Local" provider is used.

## Include Dependencies
By default, the ```vrealize:push``` goal will deploy all dependencies of the current project to the target 
environment. You can control that by the ```-DincludeDependencies``` flag. The value is ```true``` by default, so you
skip the dependencies by executing the following:
```bash
mvn package vrealize:push -Piac-vrli -DincludeDependencies=false
```

### Ignore Certificate Errors (Not recommended)
> This section describes how to bypass a security feature in development/testing environment. **Do not use those flags when targeting production servers.** Instead, make sure the certificates have the correct CN, use FQDN to access the servers and add the certificates to Java's key store (i.e. cacerts).

You can ignore certificate errors, i.e. the certificate is not trusted, by adding the flag ```-Dvrealize.ssl.ignore.certificate```:
```bash
mvn package vrealize:push -Piac-vrli -Dvrealize.ssl.ignore.certificate
```

You can ignore certificate hostname error, i.e. the CN does not match the actual hostname, by adding the flag ```-Dvrealize.ssl.ignore.certificate```:
```bash
mvn package vrealize:push -Piac-vrli -Dvrealize.ssl.ignore.hostname
```

You can also combine the two options above.

The other option is to set the flags in your Maven's settings.xml file for a specific **development** environment.
```xml
<profile>
    <id>iac-vrli</id>
    <properties>
        <vrealize.ssl.ignore.hostname>true</vrealize.ssl.ignore.hostname>
        <vrealize.ssl.ignore.certificate>true</vrealize.ssl.ignore.certificate>

        <vrli.host>192.168.1.2</vrli.host>
        <vrli.port>9543</vrli.port>
        <vrli.username>admin</vrli.username>
        <vrli.password>VMware1!</vrli.password>
        <vrli.provider>Local</vrli.provider>
    </properties>
</profile>
```

## Bundling
To produce a bundle.zip containing the package and all its dependencies, use:
```
$ mvn clean deploy -Pbundle
```
Refer to [Build Tools for VMware Aria](setup-workstation-maven.md)/Bundling for more information.

## Clean Up
Clean up is currently not supported

## Troubleshooting
* If Maven error does not contain enough information rerun it with *-X* debug flag.
```Bash
mvn -X <rest of the command>
```
* Sometimes Maven might cache old artifacts. Force fetching new artifacts with *-U*. Alternatively remove *<home>/.m2/repository* folder.
```Bash
mvn -U <rest of the command>
```
