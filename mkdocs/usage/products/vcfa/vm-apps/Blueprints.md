---
title: Blueprints
---

## Overview

A blueprint is a declarative template that is used to define and automate the deployment of infrastructure, applications, and services with the option to expose the functionality to entitled users via the {{ products.vra_9_full_name }} Catalog.

## Project Structure

{{ general.bta_name }} stores blueprint objects in several files in a directory with the blueprint name under the `src/main/resources/blueprints` directory in the project content on the local filesystem.

Following is a sample listing of the local filesystem for a blueprint with the name **Example Blueprint**.

Following is a sample listing of the project content in the `src` directory for that blueprint.

```ascii title="Local Project Content"
src/
└── main/
    └── resources/
        └── blueprints/
            └── Example Blueprint/
                ├── content.yaml
                ├── details.json
                └── versions.json
```

Each blueprint is placed in a separate directory and the data is split in the following files.

- The `content.yaml` file contains the exported blueprint code (in YAML format).
- The `details.json` file contains the metadata information for the blueprint.
- The `versions.json` file contains the version information. Note that this is just the metadata and for now the version history is not preserved.

### Sample Project File Content

This section contains sample listings of the content of the files for each blueprint object as they are stored in the `src/main/resources/blueprints/<blueprint name>/`[^1] directory in the project content on the local filesystem.

[^1]: The `<blueprint name>` placeholder stands for the name of the blueprint that determines the name of the directory.

Following is a sample listing of the content of the `content.yaml` file for the **Small VM** blueprint (as defined in the project archetype).

??? "src/main/resources/blueprints/Small VM/content.yaml"
    ```yaml
    {% include "../../../../../maven/archetypes/vra-ng/src/main/resources/archetype-resources/src/main/resources/blueprints/Small VM/content.yaml" %}
    ```

Following is a sample listing of the content of the `details.json` file for the **Small VM** blueprint (as defined in the project archetype).

??? "src/main/resources/blueprints/Small VM/details.json"
    ```json
    {% include "../../../../../maven/archetypes/vra-ng/src/main/resources/archetype-resources/src/main/resources/blueprints/Small VM/details.json" %}
    ```

## Export

To export a blueprint from the {{ products.vra_9_full_name }} server (pull the content), you need to add the blueprint name as a list item of the `blueprint` element in the `content.yaml` content descriptor file for the project.

!!! Tip
    Alternatively, if you want to export all blueprint objects from the project on the {{ products.vra_9_full_name }} server, you can configure the `blueprint` element with no value (i.e. its value is `null`). For details, see the [Content Filtering](../vm-apps/index.md#content-filtering) section.

Following is a sample listing of the `content.yaml` file for a project that exports only the `Example Blueprint` blueprint from the project on the {{ products.vra_9_full_name }} server.

```yaml
blueprint:
  - Example Blueprint
# ...
```

## Importing

When you import a blueprint to a project on a {{ products.vra_9_full_name }} server (push operation), {{ general.bta_name }} matches the blueprint object by its name (the name of the directory under `src/main/resources/blueprints` in the project content on your local filesystem) and performs one of the following operations.

- If a blueprint with the same name does not exist on the server, {{ general.bta_name }} creates a new blueprint with the details from the local project files.
- If a blueprint with the same name already exists on the server, {{ general.bta_name }} updates the content on the server and releases a new version of the blueprint. For details about the versioning that {{ general.bta_short_name }} uses, see [Version Management](#version-management).

## Version Management

When you push a blueprint to a {{ products.vra_9_short_name }} server that contains a previously-released version of a blueprint with the same name, {{ general.bta_name }} creates and releases a new version of the blueprint to maintain the intended state. Note, however, that if the content of the blueprint has not been modified since the latest released version, {{ general.bta_short_name }} *does not* create a new version to avoid unnecessary versioning.

If there is a custom form associated with the blueprint that you are importing and there is no previously-released version, {{ general.bta_name }} creates and releases an initial blueprint version (1) in order to import the custom form.

When creating a new version of the blueprint, {{ general.bta_short_name }} auto-generates the new version based on the latest existing version of the blueprint and tries to continue the numbering. If it fails to detect a version that is based on semantic versioning (`MAJOR.MINOR.PATCH`), {{ general.bta_short_name }} releases a date-formatted version.

The following table lists the supported version formats that {{ general.bta_short_name }} detects with their respective incrementing rules.

| Latest version | New version         | Incrementing rules                                                         |
|----------------|---------------------|----------------------------------------------------------------------------|
| 1              | 2                   | Major version detected - increment the major version.                      |
| 1.0            | 1.1                 | Major and minor version detected - increment the minor version.            |
| 1.0.0          | 1.0.1               | Major, minor, and patch version detected - increment the patch version.    |
| 1.0.0-alpha    | 2026-07-27-14-36-42 | Arbitrary version detected - generate a new version based on date-time.    |

### Handling of Previously-Released Blueprint Versions

By default, {{ general.bta_short_name }} unreleases all released versions of the blueprint that are not the latest one.

To control this behavior, you can use the `{{ page.meta.vars.maven.property_prefix }}.bp.unrelease.versions` property of the Maven profile configuration in the `settings.xml` file.

```xml
<{{ page.meta.vars.maven.property_prefix }}.bp.unrelease.versions>false</{{ page.meta.vars.maven.property_prefix }}.bp.unrelease.versions>
```

!!! warning
    Version history gets lost. This is known behavior and currently there is no workaround for it.
