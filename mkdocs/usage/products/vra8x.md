---
title: vRealize Automation 8.x
---

# {{ products.vra_8_full_name }} Project

## About
{{ products.vra_8_short_name }} project is a filesystem representation of vRA content into human friendly YAML or JSON format. The project consist of content descriptor and content container.

- *Content Descriptor* defines what vRA content will be part of this project.
- *Content Container* holds the actual content representation.

## Prerequisites
Before you continue with this section validate that all of the prerequisites are met.
- Install and Configure #TO DO: link to workstation setup


## Create New {{ products.vra_8_short_name }} Project
{{ general.bta_name }} provides ready to use {{ products.vra_8_short_name }} project templates *maven archetypes*.

To create a new {{ products.vra_8_short_name }} project from archetype use the following command:
```Bash
args=(
    "-DinteractiveMode=false"
    "-DarchetypeGroupId=com.vmware.pscoe.vra-ng.archetypes"
    "-DarchetypeArtifactId=package-vra-ng-archetype"
    "-DarchetypeVersion={{ iac.latest_release }}"
    "-DgroupId={{ archetype.customer_project.group_id}}" # (1)!
    "-DartifactId={{ archetype.customer_project.artifact_id}}" # (2)!
)
mvn archetype:generate "${args[@]}"
```

1.  {{ archetype.customer_project.group_id_hint }}
2.  {{ archetype.customer_project.artifact_id_hint }}

Content Descriptor is implemented by content.yaml file with the following defaults:

```yaml
---
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
custom-resource:
resource-action:
catalog-entitlement:
content-source:
property-group:
policy:
  content-sharing:
```

!!! note
    {{ products.vra_8_short_name }} Project supports only content types outlined into Content Descriptor.

To capture the state of your vRA environment simply fill in the names of the content objects and follow the [Pull Content](#pull-content) section.


| Method            | Description                          |
| -----------       | ------------------------------------ |
| blueprints        | Empty array [] - nothing is exported |
|                   | List of items - the given items are exported.|
| `PUT`             | :material-check-all: Update resource |
| `DELETE`          | :material-close:     Delete resource |


### Export Rules for Content Types
Use the following formats for 
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

### Regional Content
The {{ products.vra_8_short_name }} philosophy is built around the concept of infrastructure definition capable of resource provisioning - 
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

#### Export Regional Content
When exporting regional content defined in the respective content categories - `image-mapping`, `flavor-mapping`, 
`storage-profile`, etc., the vRA-NG package manager takes into account the tag that is defined in the `export-tag`
entry and exports content that is related to a cloud account(s) containing this tag. The content is stored in a
directory within a unique regional directory bearing the name of the cloud account and the cloud zone id. The cloud
account and zone combination are persisted for reference to the originating environment. 

#### Import Regional Content
The vRA-NG package manager uses the `import-tags` entry from the content manifest (content.yaml) to (re)create regional
content targeting cloud accounts that contain one or more of the import tags. The content is taken from all of the
regional folders and regardless of its origin, it is imported to the target environment based on the `import-tags`, i.e. 
related to cloud accounts possessing one or more of the import tags list.


!!! note
    Unreleased blueprints that have custom form will be automatically released with version 1.

To capture the state of your vRA NG environment simply fill in the names of the content objects you would like to capture and look at the Pull section of this document.

To import / export custom forms and/or icons you have to specify the associated catalog-item name in ```catalog-item``` tag. The naming convention for this is SOURCE_NAME__CATALOG_ITEM_NAME
The integration end point data for each workflow that is associated with the content source will be updated as well with the one fetched from the VRA server. 


<!-- Build Project Section -->
{% include-markdown "../../assets/docs/mvn/build-project.md" %}
The output of the command will result in **{{ archetype.customer_project.group_id}}.{{ archetype.customer_project.artifact_id}}-1.0.0-SNAPSHOT.vra-ng** file generated in the target folder of the project.

<!-- Bundle Project Section -->
{% include-markdown "../../assets/docs/mvn/bundle-project.md" %}

## Environment Connection Parameters
There are few ways to pass the vRA connection parameters to maven pull/push command:
=== "Use Maven Profiles"

    For {{ products.vra_8_short_name }} **basic** authentication append the following profile section in your maven settings.xml file.

    ``` xml
    ...<!--# (1)! -->
    <profiles>
      ...
      <profile>
          <id>{{ archetype.customer_project.maven_profile_name}}</id>
          <properties>
              <vrang.host>vra-fqdn</vrang.host>
              <vrang.port>443</vrang.port>
              <vrang.username>configurationadmin</vrang.username>
              <vrang.password>*****</vrang.password>
              <vrang.project.id>{project_id}</vrang.project.id>
              <vrang.org.id>{org_id}</vrang.org.id>
              <vrang.bp.release>true</vrang.bp.release>
              <vrang.vro.integration>{vro_integration_name}</vrang.vro.integration>
              <vrang.bp.ignore.versions>true|false</bp.ignore.versions>
              <vrang.data.collection.delay.seconds>{optional_vro_data_collection_delay_seconds}</vrang.data.collection.delay.seconds>
          </properties>
      </profile>
    </profiles>
    ```

    1.  {{ archetype.customer_project.maven_settings_location_hint}}

    For {{ products.vra_cloud_short_name }} **token** authentication append the following profile section in your maven settings.xml file.

    ``` xml
    ...<!--# (1)! -->
    <profiles>
      ...
      <profile>
          <id>{{ archetype.customer_project.maven_profile_name}}</id>
          <properties>
              <vrang.host>api.mgmt.cloud.vmware.com</vrang.host>
              <vrang.csp.host>console.cloud.vmware.com</vrang.csp.host>
              <vrang.proxy>http://proxy.host:80</vrang.proxy>
              <vrang.port>443</vrang.port>
              <vrang.project.id>{project_id}</vrang.project.id>
              <vrang.org.id>{org_id}</vrang.org.id>
              <vrang.refresh.token>{refresh_token}</vrang.refresh.token>
              <vrang.bp.release>true</vrang.bp.release>
              <vrang.vro.integration>{vro_integration_name}</vrang.vro.integration>
              <vrang.bp.ignore.versions>true|false</bp.ignore.versions>
          </properties>
      </profile>
    </profiles>
    ```

    1.  {{ archetype.customer_project.maven_settings_location_hint}}

    For {{ products.vra_8_short_name }} **token** authentication append the following profile section in your maven settings.xml file.

    ``` xml
    ...<!--# (1)! -->
    <profiles>
      ...
      <profile>
          <id>{{ archetype.customer_project.maven_profile_name}}</id>
          <properties>
              <vrang.host>vra-fqdn</vrang.host>
              <vrang.port>443</vrang.port>
              <vrang.project.id>{project_id}</vrang.project.id>
              <vrang.org.id>{org_id}</vrang.org.id>
              <vrang.refresh.token>{refresh_token}</vrang.refresh.token>
              <vrang.bp.release>true</vrang.bp.release>
              <vrang.vro.integration>{vro_integration_name}</vrang.vro.integration>
              <vrang.bp.ignore.versions>true|false</bp.ignore.versions>
          </properties>
      </profile>
    </profiles>
    ```

    1.  {{ archetype.customer_project.maven_settings_location_hint}}

    Use the profile by passing it with:
    ``` bash
    mvn vra:pull -P{{ archetype.customer_project.maven_profile_name}}
    ```

=== "Directly Pass {{ products.vra_cloud_short_name }} Token Parameters"

    ``` sh
    mvn vra-ng:pull -Dvrang.host=api.mgmt.cloud.vmware.com -Dvrang.csp.host=console.cloud.vmware.com -Dvra.port=443 -Dvrang.project.id={project_id} -Dvrang.refresh.token={refresh_token}
    ```


## Pull Content
To pull the content on the target environment execute the following maven command:
```bash
mvn vra-ng:pull -P{{ archetype.customer_project.maven_profile_name}}
```

!!! note
    Pull command ```vra-ng:pull``` will fail if the content.yaml is empty or it cannot find some of the described content on the target vRA server.

!!! note
    As seen by the examples, you can specify project name or project id in the settings.xml or as command line
    parameters. At least one of those parameters must be present in the configuration. If you define both, project id takes
    precedence over project name. If you define only project name, the solution will search for a project with that name
    and use it for the content operations.

!!! note
    When pushing property groups, the project ID specified in the configuration will be used to update the payload sent while creating/updating them in vRA.

!!! note
    Check the **Authentication** section of this document for details on possible authentication methods.

!!! note
    If a catalog item has a custom form and/or an icon they will be exported in subdirs of the catalog-items directory

!!! note
    If no catalog entitlements are specified, all of the available entitlements will be exported.

!!! note
    If catalog entitlement has a projectId in its configuration it will override the one specified in settings.xml during push of the entitlement on the target system.

!!! note
    In the catalog entitlement yaml file you can specify also project name using the projectName tag instead of projectId.

!!! note
    The value of the <vrang.org.name> tag will take precedence over the value of the <vrang.org.id> tag in case both are present (either trough settings.xml or Installer) during filtering of the cloud accounts during pull action.

!!! note
    The value of the <vrang.vro.integration> is used to change the integration endpoint of Workflow Content Sources and other resources that point to that type of integration. If the property is missing a default name "embedded-VRO" will be used.

### Additional Parameters
* `bp.ignore.versions` - ignores blueprint versioning  (refer to the *Blueprint Versioning* section below). This option defaults to `false`. When dealing with blueprint development, you might want to set this to `true` in order to avoid unnecessary blueprint versions.

<!-- Push Content Section -->
{% include-markdown "../../assets/docs/mvn/push-content.md" %}

### Additional Parameters

* `vrang.bp.release` - create a new version for already released blueprint (refer to the *Blueprint Versioning* section 
below). This option defaults to `true`. When dealing with blueprint development, you might want to set this to `false`
in order to avoid unnecessary blueprint versions.

* `vrang.data.collection.delay.seconds` - Delay in seconds to wait for vRA data collection to pass before importing data. Can also be passed
as an interactive parameter `-Dvrang.data.collection.delay.seconds=600`. useful when Dynamic types and custom resources are used in the projects and vRO content is imported,
however vRA needs to then retrieve it in order to be able to create the custom Resource and use the Create/Delete Workflows.
This only happens after a short delay and the vRA data collector scrapes vRO. Defaults to no delay.

!!! note
    If there are any custom forms or icons associated with a catalog-item they will also be imported. 

!!! note
    If there are custom forms in the custom-forms directory that are associated with workflows, they will be imported to the VRA server as well.

!!! note
    If there are custom forms in the custom-forms directory that are associated with workflows, the content-sources that are associated with them will be imported as well (they will be read from the content-sources directory).

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

!!! note
    Nothing will be released if any of the content already has the given version existing.


<!-- Clean Up Content Section -->
{% include-markdown "../../assets/docs/mvn/clean-up-content.md" %}

<!-- Troubleshooting Section -->
{% include-markdown "../../assets/docs/mvn/troubleshooting.md" %}

## Known issues:
* There is an issue with svg icons, they will not be downloaded/uploaded (IAC-482).
