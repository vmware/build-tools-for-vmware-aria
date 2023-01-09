# Getting Strarted

## Table Of Contents:
1. [Overview](#Overview)
2. [Maven Archetype](#Maven-Archetype)


### Overview
vRA 8.x projects are called vRA NG (New Generation) projects in **vRealize Build Tools**. 
The new Maven Archetype that supports vRA 8.x content is *com.vmware.pscoe.vra-ng.archetypes*.
It is a representation of vRA 8.x content into human friendly YAML and/or JSON format.The project consist of content descriptor and content container.

- *Content Descriptor* defines what part vRA 8.x content will be part of this project - `content.yaml`
- *Content Container* holds the actual content representation -`./src` folder


### Maven Archetype

**vRealize Build Tools** provides ready to use project templates (*maven archetypes*).

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


