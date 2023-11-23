---
title: vRealize Automation 7.x
---

# vRealize Automation 7.x Project

## About
vRA 7 project is a filesystem representation of vRA content into human friendly YAML or JSON format. The project consist of content descriptor and content container.

- *Content Descriptor* defines what vRA content will be part of this project.
- *Content Container* holds the actual content representation.

## Prerequisites
Before you continue with this section validate that all of the prerequisites are met.
- Install and Configure #TO DO: link to workstation setup

## Crate New vRA 7 Project
{{ general.bta_name }} provides ready to use vRA 7 project templates using *maven archetypes*.

To create a new vRA 7 project use the following command:
```Bash
args=(
    "-DinteractiveMode=false"
    "-DarchetypeGroupId=com.vmware.pscoe.vra.archetypes"
    "-DarchetypeArtifactId=package-vra-archetype"
    "-DarchetypeVersion={{ iac.latest_release }}"
    "-DgroupId={{ archetype.customer_project.group_id}}" # (1)!
    "-DartifactId={{ archetype.customer_project.artifact_id}}" # (2)!
)
mvn archetype:generate "${args[@]}"
```

1.  {{ archetype.customer_project.group_id_hint }}
2.  {{ archetype.customer_project.artifact_id_hint }}
   
Content Descriptor is implemented by content.yaml file with the following defaults:
```
---
# Example describing for export Composite blueprints by their names
#
# composite-blueprint:
#   - SQL 2016 Managed
#   - Kubernates 1.9.0

property-group:
property-definition:
software-component:
composite-blueprint:
xaas-blueprint:
xaas-resource-action:
xaas-resource-type:
xaas-resource-mapping:
workflow-subscription:
...
```
!!! note
    vRA 7 Project supports only content types outlined into Content Descriptor.

To capture the state of your vRA environment simply fill in the names of the content objects and follow the [Pull Content](#pull-content) section.

<!-- Build Project Section -->
{% include-markdown "../../assets/docs/mvn/build-project.md" %}
The output of the command will result in **{{ archetype.customer_project.group_id}}.{{ archetype.customer_project.artifact_id}}-1.0.0-SNAPSHOT.vra** file generated in the target folder of the project.

<!-- Bundle Project Section -->
{% include-markdown "../../assets/docs/mvn/bundle-project.md" %}

## Environment Connection Parameters
There are two ways to pass the vRA connection parameters to maven pull/push command:
=== "Use Maven Profiles"
    Append the following profile section in your maven settings.xml file.

    ``` xml
    ...<!--# (1)! -->
    <profiles>
      ...
      <profile>
          <id>{{ archetype.customer_project.maven_profile_name}}</id>
          <properties>
              <vra.host>vra-fqdn</vra.host>
              <vra.port>443</vra.port>
              <vra.username>configurationadmin@vsphere.local</vra.username>
              <vra.password>*****</vra.password>
              <vra.tenant>vsphere.local</vra.tenant>
          </properties>
      </profile>
    </profiles>
    ```

    1.  {{ archetype.customer_project.maven_settings_location_hint}}

    Use the profile by passing it with:
    ``` bash
    mvn vra:pull -P{{ archetype.customer_project.maven_profile_name}}
    ```

=== "Directly Pass The Parameters"

    ``` sh
    mvn vra:pull -Dvra.host=vra-fqdn -Dvra.port=443 -Dvra.username=configurationadmin@vsphere.local -Dvra.password=***** -Dvra.tenant=vsphere.local
    ```

<!-- Pull Content Section -->
{% include-markdown "../../assets/docs/mvn/pull-content.md" %}

!!! note
    Pull command ```vra:pull``` will fail if the content.yaml is empty or it cannot find some of the described content on the target vRA server.

<!-- Push Content Section -->
{% include-markdown "../../assets/docs/mvn/push-content.md" %}

<!-- Clean Up Content Section -->
{% include-markdown "../../assets/docs/mvn/clean-up-content.md" %}

<!-- Troubleshooting Section -->
{% include-markdown "../../assets/docs/mvn/troubleshooting.md" %}
