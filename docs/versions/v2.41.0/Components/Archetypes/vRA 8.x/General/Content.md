
# Content

## Overview

The vRA 8.x elements that you want to work with are described in the `content.yaml`

## Table Of Contents

1. [Content.yaml](#contentyaml)

### Content.yaml

By default the `content.yaml` looks like this.

```yaml
blueprint:
subscription:
flavor-mapping:
  - small
  - medium
image-mapping:
storage-profile:
region-mapping:
  cloud-account-tags:
    export-tag: "env:dev"
    import-tags: ["env:dev", "env:test"]
custom-resource:
resource-action:
catalog-entitlement:
catalog-item:
content-sources:
property-group:
```

#### Content Filtering

Contents are exported by different rules.

#### Import Rules for content types

- Empty array [] - nothing is imported  
- List of items - the given items are imported. If they are not present on the server an Exception is thrown.  
- Null (nothing given) - everything is being imported  
  - blueprints
  - catalog-item
  - content-source
  - custom-resource
  - catalog-entitlement
  - property-group
  - subscription
  - policies

Importing regional content doesn't use the `content.yaml` when pushing content it is working as previously pushes whatever there is the package.

#### Export Rules for content types

- Empty array [] - nothing is exported  
- List of items - the given items are exported. If they are not present on the server an Exception is thrown.  
- Null (nothing given) - everything is being exported
  - blueprints
  - catalog-item
  - content-source
  - custom-resource
  - catalog-entitlement
  - property-group
  - subscription
  - policies

#### Exporting regional content

To export regional content, cloud account(s) with given tag(s) should be given.

- Empty array [] - nothing is exported  
- List of items - the given items are exported in all regions linked to cloud accounts with given tag.

  If they are added to the manifest, but not present on the server - they are skipped.

- Null (nothing given) - everything is being exported in all regions linked to cloud accounts with given tag  
  - flavor-mapping
  - image-mapping
  - storage-profile

#### Export all content in all regions linked to cloud accounts with given tag

To export all content in all regions linked to cloud accounts, the tag for export should be defined. If not defined, nothing will be exported.

#### Examples

Example of `content.yaml` file.

```yaml
blueprint: # will export all 
subscription: # will export all 
flavor-mapping: # only "small" and "meduim" will be exported in all regions linked to cloud accounts with given tag 
  - small
  - medium
image-mapping: [] # nothing will be exported 
storage-profile: [] # nothing will be exported 
region-mapping:  # export only items linked to cloud accounts with given tags 
  cloud-account-tags:
    export-tag: "env:dev"
    import-tags: ["env:dev", "env:test"]
catalog-item:  # export according to filter
  - Project Blueprints__WindowsVM
  - Project Blueprints__LinuxVm
  - Main Workflows__ConfigureVM
custom-resource: # will export all 
resource-action: # will export all 
property-group:  # export according to filter
  - memory
catalog-entitlement:  # export according to filter
  - Content source entitlement
content-source:  # export according to filter
  - Project Blueprints
  - Main Workflows
  - Utility Workflows
  - Project Abx Actions
  - Project Code Stream pipelines
policy:  # export according to filter
  approval: []
  content-sharing:
    - cs policy 1
    - cs policy 2
  day2-actions: []
  deployment-limit: []
  lease: []
  resource-quota: []
```
