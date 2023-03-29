# Core Concepts

Laid out are some Core Concepts to follow when working with vRA 8.x ( vRA-NG ) archetype.

## Overview

vRA-NG archetype uses a pull and push approach to handling data.

## Table Of Contents

1. [Data Handling](#data-handling)
2. [ID handling](#id-handling)
3. [Single Project And Organization](#single-project-and-single-organization)
4. [Blueprint Versioning](#blueprint-versioning)

### Data Handling

All data that needs to be exported from vRA is defined in the `content.yaml` file ( [read more here](Content.md) ).  
The tool respects configuration in manifest when doing `vra-ng:pull` for the given objects:

* blueprints
* catalog-item
* content-source
* custom-resource
* catalog-entitlement
* property-group
* subscription

It will throw an error if the given entities are not on the server.

All the data exported will be imported when running `vrealize:push`! Keep in mind that the `content.yaml` will not be  
taken into consideration.

### ID handling

Relying on ids exported by Build Tools for VMware Aria should not happen. Build Tools for VMware Aria will remove where needed such ids or in some cases data 
will be fetched from the remote server, modified in flight and pushed to the server ( in cases where an import is needed ).

### Single Project And Single Organization

When working with vRA-NG archetype, each generated archetype is intended to work with **only one project and one organization**.
In a case where more are needed, you must generate multiple archetypes.

### Blueprint Versioning

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

### Regional Content

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

[//]: # (Optional Section)
[//]: # (## Previous:)

[//]: # (Optional Section)
[//]: # (## Next:)
