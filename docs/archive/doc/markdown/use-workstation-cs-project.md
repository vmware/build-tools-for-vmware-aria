# Build Tools for VMware Aria for Code Stream Projects

Before you continue with this section validate that all of the prerequisites are met.
## Prerequisites
- Install and Configure [Build Tools for VMware Aria System](setup-workstation-maven.md)

# Use

Code Stream Project is a filesystem representation of Code Stream content into human friendly YAML and/or JSON format. The project consist of content descriptor and content container.

- *Content Descriptor* defines what part Code Stream content will be part of this project.
- *Content Container* holds the actual content representation.

## Cerate New Code Stream Project

**Build Tools for VMware Aria** provides ready to use project templates (*maven archetypes*).

To create a new Code Stream project from archetype use the following command:

```Bash
mvn archetype:generate \
    -DinteractiveMode=false \
    -DarchetypeGroupId=com.vmware.pscoe.cs.archetypes \
    -DarchetypeArtifactId=package-cs-archetype \
    -DarchetypeVersion=<iac_for_vrealize_version> \
    -DgroupId=local.corp.it.cloud \
    -DartifactId=codestream
```

**Note**: *The specified <iac_for_vrealize_version> should be minimum 2.8.1*

The result of this command will produce the following project file structure:

```
catalog
├── README.md
├── content.yaml
├── pom.xml
├── release.sh
└── src/main/resources
    ├── pipelines
    |   └── <pipelineName>.yaml
    ├── variable
    |   └── variables.yaml
    ├── custom-integrations
    |   └── <custom-integration>.yaml  
    ├── git-webhooks
    |   └── <git-webhook>.yaml  
    ├── docker-webhook
    |   └── <docker-webhook>.yaml  
    ├── gerrit-listeners
    |   └── <gerrit-listener>.yaml  
    ├── gerrit-triggers
    |   └── <gerrit-trigger>.yaml  
    └── endpoints
        └── <endpoint>.yaml 
```

Content Descriptor is implemented by content.yaml file with the following defaults.

**Note**: *Code Stream Project supports only content types outlined into content descriptor.*

```yaml

---
# Example describes export of: 
#   
# Example pipelines:
# pipeline:
#  - Pipeline

pipeline:
  - Pipeline Name
endpoint:
  - Endpoint Name
custom-integration:
  - Custom Integration Name
variable:
  - Variable name
git-webhook:
docker-webhook:
gerrit-listener:
gerrit-trigger:
```

To capture the state of your Code Stream environment simply fill in the names of the content objects you would like to capture and look at the Pull section of this document.


> **_Note:_** Variables are also extracted by scanning endpoints and pipelines 

## Building
You can build any Code Stream project from sources using Maven:

```bash
mvn clean package
```

This will produce a CS package with the groupId, artifactId and version specified in the pom. For example:

```xml
<groupId>local.corp.it.cloud</groupId>
<artifactId>codestream</artifactId>
<version>1.0.0-SNAPSHOT</version>
<packaging>cs</packaging>
```
will result in **local.corp.it.cloud.codestream-1.0.0-SNAPSHOT.cs** generated in the target folder of your project.

## Pull
When working on a CS project, you mainly make changes on a live server using the CS Console and then you need to capture those changes in the maven project on your filesystem.

To support this use case, the toolchain comes with a custom goal "cs:pull". The following command will "pull" the content outlined into *Content Descriptor* file to the current project from a specified server and expand its content in the local filesystem overriding any local content:

```bash
mvn cs:pull -Dvrang.host=vra-l-01a.corp.local -Dvrang.port=443 -vrang.username={username} -Dvrang.password={password} -Dvrang.project.id={project+id} -Dvrang.refresh.token={refresh+token}
```
> **Note:** Extraction works only in the scope of a project. Property project.id or project.name is required! 

A better approach is to have the different connection properties configured in development environments specified as profiles in the local settings.xml file by adding the following snippet under "profiles":

### Example profile for Code Stream

```xml
<profile>
    <id>corp-dev</id>
    <properties>
        <!--Code Stream Connection-->
        <vrang.host>api.mgmt.cloud.vmware.com</vrang.host>
        <vrang.csp.host>cloud.mgmt.cloud.vmware.com</vrang.csp.host>
        <vrang.port>443</vrang.port>
        <vrang.username>{username}</vrang.username>
        <vrang.password>{password}</vrang.password>
        <!--The id of maven element containing the encrypted password and username-->
        <vrang.serverId>${serverid}</vrang.serverId>
        <vrang.refresh.token>{refresh+token}</vrang.refresh.token>
        <!--Content scope given by project-->
        <vrang.project.id>{project+id}</vrang.project.id>
        <vrang.project.name>{project+name}</vrang.project.name>
        <!--push: update the endpoint cloud proxy in vRA Cloud-->
        <vrang.cloud.proxy.name>{cloud+proxy+name}</vrang.cloud.proxy.name>
        <vrang.org.id>{org+id}</vrang.org.id>
        <vrang.org.name>{org+name}</vrang.org.name>
     
        <vrang.proxy>{proxy+url}</vrang.proxy>
  
    </properties>
</profile>
```

Then, you can sync content back to your local sources by simply activating the profile:

```bash
mvn cs:pull -Pcorp-env
```

> Note that ```cs:pull``` will fail if the content.yaml is empty, or it cannot find some described content on the target vRA server.

**Note**: *Check the **Authentication** section of this document for details on possible authentication methods.*

## Push
To deploy the code developed in the local project or checked out from source control to a live server, you can use
the ```vrealize:push``` command:

```bash
mvn package vrealize:push -Pcorp-env
```

This will build the package and deploy it to the environment described in the ```corp-env``` profile.

> **Note:** project context is required and in case of vRA Cloud the cloud proxy name used by the endpoints is required

Content pushing principles:
- Variables are pushed first. Variable project parameter is updated. Variable values are not forced on update and thus configured values remain. If a Secret or Restricted variable is updated (changes type or description) the secret value will be lost.
- Custom integrations are pushed with all their versions. If custom integration exists the latest code is updated. Versions already existing in target environment will not be updated (as they may be locked in pipelines). Only new versions will be imported.
- Endpoints : Endpoints are imported in configured project. Endpoint cloud proxy is updated. 
- Pipelines : Pipelines are imported in configured project.
Pipelines status is not changed.


> **Note:** Pushing/Pulling content between  vRA Cloud and on premise. vRA Cloud manage cloud proxy configuration. Also some tasks types like (REST, POLL) require you to configure an Agent endpoint. Agent endpoint is direct representation of the cloud proxy.  vRA on premise dos not support creation of agent endpoints and addition of agents in the tasks.

## Authentication
Use one of the two possible authentication mechanisms: refresh token or basic auth. When executing command use a profile that has either username/password set or refreshToken parameter.

```bash
cs:pull -Dvrang.host=vra-l-01a.corp.local -Dvrang.port=443 -Dvrang.project.id={project+id} -Dvrang.refresh.token={refresh+token}
```

```bash
cs:pull -Dvrang.host=vra-l-01a.corp.local -Dvra.port=443 -Dvrang.project.id={project+id} -Dvrang.username={username} -Dvrang.password={password}
```

>**Note**: Basic authentication is performed against an endpoint that communicates with vIDM as authentication backend.

**Note**: Username parameter accepts usernames in the form of `<username>` as well as `<username>@<domain>`. When no `<domain>` is provided, the authentication automatically assumes **System Domain**. Otherwise the provided domain will be used. 
E.g. `administrator@corp.local` will perform authentication against the `corp.local` domain, whereas `configurationadmin` will perfom authentication agains `System Domain`.

## Include Dependencies
By default, the ```vrealize:push``` goal will deploy all dependencies of the current project to the target 
environment. You can control that by the ```-DincludeDependencies``` flag. The value is ```true``` by default, so you
skip the dependencies by executing the following:

```bash
mvn package vrealize:push -Pcorp-env -DincludeDependencies=false
```

Note that dependencies will not be deployed if the server has a newer version of the same package deployed. For example,
if the current project depends on ```com.vmware.pscoe.example-2.4.0``` and on the server there is ```com.vmware.pscoe.example-2.4.2```,
the package will not be downgraded. You can force that by adding the ````-Dvra.importOldVersions``` flag:
```bash
mvn package vrealize:push -Pcorp-env -Dvra.importOldVersions
```
The command above will forcefully deploy the exact versions of the dependent packages, downgrading anything it finds on the server.

### Ignore Certificate Errors (Not recommended)
> This section describes how to bypass a security feature in development/testing environment. **Do not use those flags when targeting production servers.** Instead, make sure the certificates have the correct CN, use FQDN to access the servers and add the certificates to Java's key store (i.e. cacerts).

You can ignore certificate errors, i.e. the certificate is not trusted, by adding the flag ```-Dvrealize.ssl.ignore.certificate```:

```bash
mvn package vrealize:push -Pcorp-env -Dvrealize.ssl.ignore.certificate
```

You can ignore certificate hostname error, i.e. the CN does not match the actual hostname, by adding the flag ```-Dvrealize.ssl.ignore.certificate```:
```bash
mvn package vrealize:push -Pcorp-env -Dvrealize.ssl.ignore.hostname
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
