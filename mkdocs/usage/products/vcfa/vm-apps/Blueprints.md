---
title: Blueprints
---

## Overview

Architects of blueprints (a.k.a. Cloud Templates) build software components, machine blueprints, and custom XaaS blueprints and assemble those components into the blueprints that define the items users request from the catalog.

## Structure

Following is a sample structure of a blueprint export.

The `content.yaml` file contains the following content.

```yaml
blueprint:
  - Example Blueprint
# ...
```

Following is a sample listing of the project content in the `src` directory for that blueprint.

```ascii
src/
├─ main/
│  ├─ resources/
│  │  ├─ blueprints/
│  │  │  ├─ Example Blueprint/
│  │  │  │  ├─ content.yaml
│  │  │  │  ├─ details.json
│  │  │  │  ├─ versions.json
```

Each blueprint is placed in a separate directory and the data is split in the following files.

- The `content.yaml` file contains the exported code from the blueprint canvas.
- The `details.json` file contains the blueprint metadata information.
- The `versions.json` file contains the version information. Note that this is just the metadata and for now the version history is not preserved.

## Importing

When importing a blueprint, it is matched by its name. If there is a blueprint with the same name on the server, an update will be performed. Otherwise, the blueprint will be created instead.

Also when importing an already existing blueprint, we will check for any differences between the local copy and the server copy. If there are differences, a new version will be released. The new version is determined by the already-existing versions. If a pattern of MAJOR.MINOR.PATCH is detected, {{ general.bta_name }} will try to continue the numbering, otherwise a date formatted version is released.

## Version Management

When pushing a blueprint to a {{ products.vra_9_short_name }} server that contains a previously-released blueprint with the same name as the one being pushed, a new version will be created and released in order to maintain the intended state. A new version will *not* be created if the content of the blueprint has not been modified since the latest released version in order to avoid unnecessary versioning.

If a custom form that is associated with the blueprint is being imported and there is no previously-released version, an initial blueprint version (1) will be created and released in order to import the custom form.

When creating a new version in the above-described cases, the new version will be auto-generated based on the latest version of the blueprint. The following version formats are supported with their respective incrementing rules.

| Latest version | New version         | Incrementing rules                                                 |
|----------------|---------------------|--------------------------------------------------------------------|
| 1              | 2                   | Increment major version.                                           |
| 1.0            | 1.1                 | Major and minor version - increment the minor version.             |
| 1.0.0          | 1.0.1               | Major, minor and patch version - incrementing the patch version.   |
| 1.0.0-alpha    | 2020-05-27-10-10-43 | Arbitrary version - generate a new version based on date-time.     |

By default, all versions that are not the latest one will be unreleased.

To control this behavior, you can set the `vrang.bp.unrelease.versions` property to `false` in the Maven profile configuration in the `settings.xml` file.

```xml
<{{ page.meta.vars.maven.property_prefix }}.bp.unrelease.versions>false</{{ page.meta.vars.maven.property_prefix }}.bp.unrelease.versions>
```

!!! warning
    Version history gets lost. This is known and currently there is no workaround for this.
