# Build Tools for VMware Aria for vRA NG Projects

Before you continue with this section validate that all of the prerequisites are met.
## Prerequisites
- Install and Configure [Build Tools for VMware Aria System](setup-workstation-maven.md)

# Use

vRA NG Project is a filesystem representation of vRA NG content into human friendly YAML and/or JSON format. The project consist of content descriptor and content container.

- *Content Descriptor* defines what part vRA NG content will be part of this project.
- *Content Container* holds the actual content representation.

## Create New vRA NG Project

**Build Tools for VMware Aria** provides ready to use project templates (*maven archetypes*).

To create a new vRA project from archetype use the following command:

```Bash
mvn archetype:generate \
    -DinteractiveMode=false \
    -DarchetypeGroupId=com.vmware.pscoe.vra-ng.archetypes \
    -DarchetypeArtifactId=package-vra-ng-archetype \
    -DarchetypeVersion=<iac_for_vrealize_version> \
    -DgroupId=local.corp.it.cloud \
    -DartifactId=catalog
```

**Note**: *The specified <iac_for_vrealize_version> should be minimum 2.4.11*

The result of this command will produce the following project file structure:

```
catalog
├── README.md
├── content.yaml
├── pom.xml
├── release.sh
└── src
    └── main
        └── blueprints
            └── blueprint.yaml
        └── content-sources
            └── source.json
        └── property-group
            └── property_group_name.json
        └── catalog-items
            └── forms
                └── source name__workflow one name with custom form.json
				└── source name__workflow one name with custom form__FormData.json
                └── source name__workflow three name with custom icon and form.json
				└── source name__workflow three name with custom icon and form__FormData.json
            └── icons
                └── source name__workflow two name with custom icon.png
                └── source name__workflow three name with custom icon and form.png
            └── source name__workflow one name with custom form.json
            └── source name__workflow two name with custom icon.json
            └── source name__workflow three name with custom icon and form.json
        └── entitlements
            └── Blueprint.yaml
            └── Workflow.yaml
            └── ABX Action.yaml       
        └── subscriptions
            └── subscription.json
        └── regions
            └── cloud-account-name~region-id
                └── flavor-mappings
                    └── small.json
                └── image-mappings
                    └── mapping.json
                └── storage-profiles
                    └── profile.json
                └── src-region-profile.json
        └── custom-resources
            └── customResource.json
        └── resource-actions
            └── resourceAction.json
```

Content Descriptor is implemented by content.yaml file with the following defaults.

**Note**: *vRA NG Project supports only content types outlined into content descriptor.*



### Export Rules for content types
Empty array [] - nothing is exported
List of items - the given items are exported.
Null (nothing given) - everything is being exported
* blueprints
* catalog-item
* content-source
* custom-resource
* catalog-entitlement
* property-group
* subscription

### Exporting regional content
To export regional content, cloud account(s) with given tag(s) should be given.
Empty array [] - nothing is exported 
List of items - the given items are exported in all regions linked to cloud accounts with given tag 
Null (nothing given) - everything is being exported in all regions linked to cloud accounts with given tag 
* flavor-mapping
* image-mapping
* storage-profile

### Export all content in all regions linked to cloud accounts with given tag 
To export all content in all regions linked to cloud accounts, the tag for export should be defined.
If not defined, nothing will be exported.


```yaml

---
# Example describes export of: 
#   Flavor mappings with names "small" and "medium" in all regions linked to cloud accounts with tag "env:dev"
# Export Rules for all content types:
#   flavor-mapping: []     - nothing will be exported
#   flavor-mapping: 
#        - small"
#        - meduim
#                          - only "small" and "meduim" will be exported 
#   flavor-mapping:        - everything will be exported in all regions linked to cloud accounts with tag "env:dev"
#
#   
# Example describes import of:
#   All blueprints in src/blueprints
#   All subscriptions in src/blueprints
#   All flavor mappings in regions/ into regions linked to cloud accounts with tags "env:dev" or "env:test"
# Example vra workflow catalog items ( Check notes down further on naming conventions )
# catalog-item:
#   - Content Source__Catalog Item 1
#   - Content Source__Catalog Item 2
# Example blueprints:
# blueprint:
#  - blueprint 1
#  - bluepring 2
#  - bluepring 3
# Example property groups::
# property-group:
#  - memory
#  - compute
# Example subscriptions:
# subscription:
#  - subscription 1
#  - subscription 2
#  - subscription 3

blueprint:
subscription:
flavor-mapping:
  - small
  - medium
image-mapping: []
storage-profile: []
region-mapping:
  cloud-account-tags:
    export-tag: "env:dev"
    import-tags: ["env:dev", "env:test"]
catalog-item:
  - Project Blueprints__WindowsVM
  - Project Blueprints__LinuxVm
  - Main Workflows__ConfigureVM
custom-resource:
resource-action:
property-group:
  - memory
catalog-entitlement:
  - Content source entitlement
content-source:
  - Project Blueprints
  - Main Workflows
  - Utility Workflows
  - Project Abx Actions
  - Project Code Stream pipelines

```

**Note**: *Unreleased blueprints that have custom form will be automatically released with version 1.*

To capture the state of your vRA NG environment simply fill in the names of the content objects you would like to capture and look at the Pull section of this document.

To import / export custom forms and/or icons you have to specify the associated catalog-item name in ```catalog-item``` tag. The naming convention for this is SOURCE_NAME__CATALOG_ITEM_NAME
The integration end point data for each workflow that is associated with the content source will be updated as well with the one fetched from the VRA server. 

## Building
You can build any vRA NG project from sources using Maven:

```bash
mvn clean package
```

This will produce a vRA NG package with the groupId, artifactId and version specified in the pom. For example:

```xml
<groupId>local.corp.it.cloud</groupId>
<artifactId>catalog</artifactId>
<version>1.0.0-SNAPSHOT</version>
<packaging>vra-ng</packaging>
```
will result in **local.corp.it.cloud.catalog-1.0.0-SNAPSHOT.vra-ng** generated in the target folder of your project.

## Pull
When working on a vRA NG project, you mainly make changes on a live server using the vRA NG Console and then you need to capture those changes in the maven project on your filesystem.

To support this use case, the toolchain comes with a custom goal "vra-ng:pull". The following command will "pull" the content outlined into *Content Descriptor* file to the current project from a specified server and expand its content in the local filesystem overriding any local content:

```bash
vra-ng:pull -Dvrang.host=api.mgmt.cloud.vmware.com -Dvrang.csp.host=console.cloud.vmware.com -Dvra.port=443 -Dvrang.project.id={project+id} -Dvrang.refresh.token={refresh+token}
```
A better approach is to have the different vRO/vRA development environments specified as profiles in the local
settings.xml file by adding the following snippet under "profiles":

### Example profile for vRA Cloud using refresh token for authentication

```xml
<profile>
    <id>corp-dev</id>
    <properties>
        <!--vRA NG Connection-->
        <vrang.host>api.mgmt.cloud.vmware.com</vrang.host>
        <vrang.csp.host>console.cloud.vmware.com</vrang.csp.host>
        <vrang.proxy>http://proxy.host:80</vrang.proxy>
        <vrang.port>443</vrang.port>
        <vrang.project.id>{project+id}</vrang.project.id>
        <!-- <vrang.project.name>{project+name}</vrang.project.name> -->
        <vrang.org.id>{org+id}</vrang.org.id>
		<vrang.org.name>{org+name}</vrang.org.name>
        <vrang.refresh.token>{refresh+token}</vrang.refresh.token>
        <vrang.bp.release>true</vrang.bp.release>
        <vrang.vro.integration>{vro+integration+name}</vrang.vro.integration>
        <vrang.bp.ignore.versions>true|false</bp.ignore.versions>
    </properties>
</profile>
```

### Example profile for vRA On-Prem using refresh token for authentication

```xml
<profile>
    <id>corp-dev</id>
    <properties>
        <!--vRA NG Connection-->
        <vrang.host>vra-l-01a.corp.local</vrang.host>
        <vrang.port>443</vrang.port>
        <vrang.project.name>{project+name}</vrang.project.name>
        <vrang.org.id>{org+id}</vrang.org.id>
		<vrang.org.name>{org+name}</vrang.org.name>
        <vrang.refresh.token>{refresh+token}</vrang.refresh.token>
        <vrang.bp.release>true</vrang.bp.release>
        <vrang.bp.ignore.versions>true|false</bp.ignore.versions>
    </properties>
</profile>
```

### Example profile for vRA On-Prem using username and password for authentication (basic auth)

```xml
<profile>
    <id>corp-dev</id>
    <properties>
        <!--vRA NG Connection-->
        <vrang.host>vra-l-01a.corp.local</vrang.host>
        <vrang.port>443</vrang.port>
        <vrang.username>{username}</vrang.username>
        <vrang.password>{password}</vrang.password>
        <!--The id of maven element containing the encrypted password and username-->
        <vrang.serverId>${serverid}</vrang.serverId>
        <vrang.project.id>{project+id}</vrang.project.id>
        <!-- <vrang.project.name>{project+name}</vrang.project.name> -->
        <vrang.org.id>{org+id}</vrang.org.id>
		<vrang.org.name>{org+name}</vrang.org.name>
        <vrang.bp.release>true</vrang.bp.release>
        <vrang.vro.integration>{vro+integration+name}</vrang.vro.integration>
        <vrang.bp.ignore.versions>true|false</bp.ignore.versions>
    </properties>
</profile>
```

### Example profile for vRA On-Prem using username and password for authentication (basic auth) and with data collection timeout on import

```xml
<profile>
    <id>corp-dev</id>
    <properties>
        <!--vRA NG Connection-->
        <vrang.host>vra-l-01a.corp.local</vrang.host>
        <vrang.port>443</vrang.port>
        <vrang.username>{username}</vrang.username>
        <vrang.password>{password}</vrang.password>
        <vrang.project.id>{project+id}</vrang.project.id>
        <!-- <vrang.project.name>{project+name}</vrang.project.name> -->
        <vrang.org.id>{org+id}</vrang.org.id>
		<vrang.org.name>{org+name}</vrang.org.name>
        <vrang.bp.release>true</vrang.bp.release>
        <vrang.vro.integration>{vro+integration+name}</vrang.vro.integration>
        <vrang.data.collection.delay.seconds>{vro+data+collection+delay+seconds}</vrang.data.collection.delay.seconds>
        <vrang.bp.ignore.versions>true|false</bp.ignore.versions>
    </properties>
</profile>
```

Then, you can sync content back to your local sources by simply activating the profile:

```bash
mvn vra-ng:pull -Pcorp-env
```

> Note that ```vra-ng:pull``` will fail if the content.yaml is empty, or it cannot find some described content on the target vRA server.


**Note**: *As seen by the examples, you can specify project name or project id in the settings.xml or as command line
 parameters. At least one of those parameters must be present in the configuration. If you define both, project id takes
 precedence over project name. If you define only project name, the solution will search for a project with that name
 and use it for the content operations.*

**Note**: *When pushing property groups, the project ID specified in the configuration will be used to update the payload sent while creating/updating them in vRA.*

**Note**: *Check the **Authentication** section of this document for details on possible authentication methods.*

**Note**: *If a catalog item has a custom form and/or an icon they will be exported in subdirs of the catalog-items directory*

**Note**: *If no catalog entitlements are specified, all of the available entitlements will be exported.*

**Note**: *If catalog entitlement has a projectId in its configuration it will override the one specified in settings.xml during push of the entitlement on the target system.*

**Note**: *In the catalog entitlement yaml file you can specify also project name using the projectName tag instead of projectId.*


**Note**: The value of the <vrang.org.name> tag will take precedence over the value of the <vrang.org.id> tag in case both are present (either trough settings.xml or Installer) during filtering of the cloud accounts during pull action.

**Note**: The value of the <vrang.vro.integration> is used to change the integration endpoint of Workflow Content Sources and other resources that point to that type of integration. If the property is missing a default name "embedded-VRO" will be used.

* `bp.ignore.versions` - ignores blueprint versioning  (refer to the *Blueprint Versioning* section 
below). This option defaults to `false`. When dealing with blueprint development, you might want to set this to `true`
in order to avoid unnecessary blueprint versions.

## Push
To deploy the code developed in the local project or checked out from source control to a live server, you can use
the ```vrealize:push``` command:

```bash
mvn package vrealize:push -Pcorp-env
```

This will build the package and deploy it to the environment described in the ```corp-env``` profile. There are a few
additional options:

* `vrang.bp.release` - create a new version for already released blueprint (refer to the *Blueprint Versioning* section 
below). This option defaults to `true`. When dealing with blueprint development, you might want to set this to `false`
in order to avoid unnecessary blueprint versions.

* `vrang.data.collection.delay.seconds` - Delay in seconds to wait for vRA data collection to pass before importing data. Can also be passed
as an interactive parameter `-Dvrang.data.collection.delay.seconds=600`. useful when Dynamic types and custom resources are used in the projects and vRO content is imported,
however vRA needs to then retrieve it in order to be able to create the custom Resource and use the Create/Delete Workflows.
This only happens after a short delay and the vRA data collector scrapes vRO. Defaults to no delay.

**Note**: **If there are any custom forms or icons associated with a catalog-item they will also be imported. 

**Note**: *If there are custom forms in the custom-forms directory that are associated with workflows, they will be imported to the VRA server as well.*

**Note**: *If there are custom forms in the custom-forms directory that are associated with workflows, the content-sources that are associated with them will be imported as well (they will be read from the content-sources directory).*

### Blueprint versioning
When pushing a blueprint to a vRA server that contains previously released blueprint with the same name as the one
being pushed, a new version will be created and released in order to maintain the intended state. 
A new version will  *not* be created if the content of the blueprint has not been modified since the latest released 
version in order to avoid unnecessary versioning.

If there's a custom form associated with the blueprint being imported and there's no previously released version, 
an initial blueprint version (1) will be created and released in order to import the custom form.

When creating a new version in the above-described cases, the new version will be auto-generated based on the latest
version of the blueprint. The following version formats are supported with their respective incrementing rules:

| Latest version | New version         | Incrementing rules                                         |
|----------------|---------------------|------------------------------------------------------------|
| 1              | 2                   | Increment major version                                    |
| 1.0            | 1.1                 | Major and minor version - increment the minor              |
| 1.0.0          | 1.0.1               | Major, minor and patch version - incrementing the patch    |
| 1.0.0-alpha    | 2020-05-27-10-10-43 | Arbitrary version - generate a new date-time based version |

## Regional Content
The vRA 8.x philosophy is built around the concept of infrastructure definition capable of resource provisioning - 
compute, network, storage and other types of resources - that builds up an abstract model for resource description.
This allows workload placement to happen dynamically based on various explicit or implicit rules. Part of this abstract
model is the definition of various mappings and profiles that provide common higher-level definitions of underlying 
infrastructure objects. These definitions take the form of various mappings and profiles:
* flavor mappings - common designation of compute resource t-shirt or other sizing
* image mappings - common designation of VM images
* storage profiles - a set of storage policies and configurations used for workload placement
* network profiles - a set of network-related configurations used for network resource placement

These abstractions are related to the regions within the cloud accounts and their capabilities. They utilize the various
underlying resources which are automatically collected and organized into "fabrics" by vRA. As such, they contain
information about resources in the various connected regions and for the purpose of this project are collectively called 
**regional content**.

Exporting (pulling) and importing (pushing) of regional content is achieved using a mapping definition specified in the
content manifest (content.yaml): `region-mappping`. It contains a set of mapping criteria used for exporting and
importing of content. The vRA-NG package manager handles the `export-tag` and `import-tags` entries of the
`cloud-account-tags` section of `region-mapping`.

### Export Regional Content
When exporting regional content defined in the respective content categories - `image-mapping`, `flavor-mapping`, 
`storage-profile`, etc., the vRA-NG package manager takes into account the tag that is defined in the `export-tag`
entry and exports content that is related to a cloud account(s) containing this tag. The content is stored in a
directory within a unique regional directory bearing the name of the cloud account and the cloud zone id. The cloud
account and zone combination are persisted for reference to the originating environment. 

### Import Regional Content
The vRA-NG package manager uses the `import-tags` entry from the content manifest (content.yaml) to (re)create regional
content targeting cloud accounts that contain one or more of the import tags. The content is taken from all of the
regional folders and regardless of its origin, it is imported to the target environment based on the `import-tags`, i.e. 
related to cloud accounts possessing one or more of the import tags list.


## Release
To release a specific content uploaded on a live server, you can use the ```vrealize:release``` command:

```bash
mvn clean package vrealize:release -Pcorp-env -Dvrang.contentType=blueprint -Dvrang.contentNames=testBlueprint -Dvrang.version=1 -DreleaseIfNotUpdated=false
```

Only parameter vrang.version is required. 
Defalut behavior for other parameters:
    - vrang.contentType: default value "all". Releases all supported content types.
    - vrang.contentNames: default value "[]". Releases all content of given types on server.
    - vrang.releaseIfNotUpdated: default value "false". Skips content if there are no updates since latest version.

**Note**: Nothing will be released if any of the content already has the given version existing.

## Authentication
Use one of the two possible authentication mechanisms: refresh token or basic auth. When executing command use a profile that has either username/password set or refreshToken parameter.

```bash
vra-ng:pull -Dvrang.host=api.mgmt.cloud.vmware.com -Dvrang.csp.host=console.cloud.vmware.com -Dvra.port=443 -Dvrang.project.id={project+id} -Dvrang.refresh.token={refresh+token}
```

```bash
vra-ng:pull -Dvrang.host=api.mgmt.cloud.vmware.com -Dvrang.csp.host=console.cloud.vmware.com -Dvra.port=443 -Dvrang.project.id={project+id} -Dvrang.username={username} -Dvrang.password={password}
```

**Note**: Basic authentication is performed against an endpoint that communicates with vIDM as authentication backend.

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

You can also combine the two options above.

The other option is to set the flags in your Maven's settings.xml file for a specific **development** environment.

```xml
<profile>
    <id>corp-dev</id>
    <properties>
        <!--vRO Connection-->
        <vro.host>10.29.26.18</vro.host>
        <vro.port>8281</vro.port>
        <vro.username>administrator@vsphere.local</vro.username>
        <vro.password>*****</vro.password>
        <vro.auth>vra</vro.auth>
        <vro.tenant>vsphere.local</vro.tenant>
        <vro.authHost>{auth_host}</vro.authHost>
        <vro.authPort>{auth_port}</vro.authPort> 
        <vro.refresh.token>{refresh_token}</vro.refresh.token> 
        <vro.proxy>http://proxy.host:80</vro.proxy>
        <vrealize.ssl.ignore.hostname>true</vrealize.ssl.ignore.hostname>
        <vrealize.ssl.ignore.certificate>true</vrealize.ssl.ignore.certificate>
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

## Known issues:
* There is an issue with svg icons, they will not be downloaded/uploaded (IAC-482).
