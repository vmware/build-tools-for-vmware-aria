# Getting Started

### Overview
vRA 8.x projects are called vRA NG (New Generation) projects in **Build Tools for VMware Aria**.
The new Maven Archetype that supports vRA 8.x content is *com.vmware.pscoe.vra-ng.archetypes*.
It is a representation of vRA 8.x content into human friendly YAML and/or JSON format.The project consist of content descriptor and content container.

- *Content Descriptor* defines what part vRA 8.x content will be part of this project - `content.yaml`
- *Content Container* holds the actual content representation -`./src` folder

## Table Of Contents:
1. [Maven Archetype](#Maven-Archetype)
2. [Configuring settings xml](#configuring-m2settingsxml-to-work-with-vra-ng)

### Maven Archetype
**Build Tools for VMware Aria** provides ready to use project templates (*maven archetypes*).

To create a new vRA 8.x project from archetype use the following command:

~~~Bash
mvn archetype:generate \
    -DinteractiveMode=false \
    -DarchetypeGroupId=com.vmware.pscoe.vra-ng.archetypes \
    -DarchetypeArtifactId=package-vra-ng-archetype \
    -DarchetypeVersion=<iac_for_vrealize_version> \
    -DgroupId=local.corp.it.cloud \
    -DartifactId=catalog
~~~

**Note**: *The specified <iac_for_vrealize_version> should be minimum 2.4.11*


#### Content Structure
The result of this command will produce the following project file structure:

~~~
catalog
├── README.md
├── content.yaml
├── pom.xml
├── release.sh
└── src
    └── main
        └── blueprints
            └── blueprint.yaml
            └── content.yaml
            └── versions.yaml
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
~~~

Content Descriptor is implemented by content.yaml file with the following defaults.

**Note**: *vRA NG Project supports only content types outlined into content descriptor.*

### Configuring `~/.m2/settings.xml` to work with vRA-NG

The following need to be added to the profile that you intend to use:
```xml
 <!--            VRA-NG    -->
<profile>
<!--    ..... OTHER DIRECTIVES .....  -->
    <vrang.host>example.vra.url.com</vrang.host>
    <vrang.csp.host>console.cloud.vmware.com</vrang.csp.host>
    <vrang.proxy>http://proxy.host:80</vrang.proxy>
    <vrang.port>443</vrang.port>
    <vrang.username>administrator</vrang.username>
    <vrang.password>someSecurePassword</vrang.password>
    <vrang.tenant>{tenant}</vrang.tenant>
    <vrang.project.id>{project+id}</vrang.project.id>
    <vrang.project.name>{project+name}</vrang.project.name>
    <vrang.org.id>{organization+id}</vrang.org.id>
    <vrang.org.name>{org+name}</vrang.org.name>
    <vrang.refresh.token>{refresh+token}</vrang.refresh.token>
    <vrang.bp.ignore.versions>true|false</bp.ignore.versions>
    <vrang.bp.release>true|false</vrang.bp.release>
    <vrang.vro.integration>{vro+integration+name}</vrang.vro.integration>
</profile>
```
* `vrang.refresh.token` - will use the given refresh token instead of credentials. **Note:** this will take precedence over
credentials.

* `vrang.bp.ignore.versions` - ignores blueprint versioning  (refer to the *Blueprint Versioning* section
  below). This option defaults to `false`. When dealing with blueprint development, you might want to set this to `true`
  in order to avoid unnecessary blueprint versions.

* `vrang.bp.release` - create a new version for already released blueprint (refer to the *Blueprint Versioning* section
  below). This option defaults to `true`. When dealing with blueprint development, you might want to set this to `false`
  in order to avoid unnecessary blueprint versions.

* `vrang.data.collection.delay.seconds` - Delay in seconds to wait for vRA data collection to pass before importing data. Can also be passed
  as an interactive parameter `-Dvrang.data.collection.delay.seconds=600`. useful when Dynamic types and custom resources are used in the projects and vRO content is imported,
  however vRA needs to then retrieve it in order to be able to create the custom Resource and use the Create/Delete Workflows.
  This only happens after a short delay and the vRA data collector scrapes vRO. Defaults to no delay.

#### Organizations
The `vrang.org.id` needs to be passed, as vRA-ng targets only a single organization. However we can extract the `org.id` from the
`org.name`. Meaning that in the end if `org.name` is present, you don't need to pass the `org.id`, however if `org.id` is passed, it will be
taken with priority.
