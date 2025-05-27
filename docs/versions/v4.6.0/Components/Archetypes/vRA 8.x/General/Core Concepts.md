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

All data that needs to be exported from vRA is defined in the `content.yaml` file ([read more here](Content.md)). The tool respects configuration in manifest when doing `vra-ng:pull` for the given objects:

- blueprints
- catalog-item
- content-source
- custom-resource
- catalog-entitlement
- property-group
- subscription
- policies
  - approval
  - content-sharing
  - day2-actions
  - deployment-limit
  - lease
  - resource-quota

It will throw an error if the given entities are not on the server.

All the data exported will be imported when running `vrealize:push`! Keep in mind that the `content.yaml` will not be taken into consideration.

### ID handling

Relying on ids exported by Build Tools for VMware Aria should not happen. Build Tools for VMware Aria will remove where needed such ids or in some cases data will be fetched from the remote server, modified in flight and pushed to the server ( in cases where an import is needed ).

### Single Project And Single Organization

When working with vRA-NG archetype, each generated archetype is intended to work with **only one project and one organization**. In a case where more are needed, you must generate multiple archetypes.

### Blueprint Versioning

When pushing a blueprint to a vRA server that contains previously released blueprint with the same name as the one being pushed, a new version will be created and released in order to maintain the intended state. A new version will  *not* be created if the content of the blueprint has not been modified since the latest released version in order to avoid unnecessary versioning.

If there's a custom form associated with the blueprint being imported and there's no previously released version, an initial blueprint version (1) will be created and released in order to import the custom form.

When creating a new version in the above-described cases, the new version will be auto-generated based on the latest version of the blueprint. The following version formats are supported with their respective incrementing rules:

| Latest version | New version         | Incrementing rules                                         |
|----------------|---------------------|------------------------------------------------------------|
| 1              | 2                   | Increment major version                                    |
| 1.0            | 1.1                 | Major and minor version - increment the minor              |
| 1.0.0          | 1.0.1               | Major, minor and patch version - incrementing the patch    |
| 1.0.0-alpha    | 2020-05-27-10-10-43 | Arbitrary version - generate a new date-time based version |

### Catalog Items Custom Forms

The catalog items in the vRA Service Broker consists of different type of content sources - Blueprint, Extensibility Actions, Pipelines, Workflows and AWS CloudFormation Template. As with the 8.12 release, the catalog items custom forms can be versioned. For all types, the same concepts replies - *only current versions for custom forms are de-serializing/serializing.*

Blueprint type catalog items have different versions that corelate to the released versions of the Blueprints. Only the current version of the latest blueprint version is targeted.

[//]: # (Optional Section)
[//]: # (## Previous:)

[//]: # (Optional Section)
[//]: # (## Next:)
