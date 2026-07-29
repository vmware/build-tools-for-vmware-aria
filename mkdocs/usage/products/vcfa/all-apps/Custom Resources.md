---
title: Custom Resources
---

## Overview

Custom resources are defined in {{ products.vra_9_full_name }} to expand the list of resource types that are available by default in blueprints. Custom resources allow you to define your own resource types based on existing VCF Operations orchestrator inventory objects or Orchestrator workflows with any kind of input and output.

## Project Structure

{{ general.bta_name }} stores a custom resource object in a single file with the name `details.json` in a directory with the name of the custom resource under the `src/main/resources/custom-resources` directory in the project content on the local filesystem. The file contains the full custom resource definition.

Following is a sample listing of the local filesystem for a custom resource with the name **Custom REST Host**.

```ascii title="Local Project Content"
src/
└─ main/
   └─ resources/
      └─ custom-resources/
         └─ Custom REST Host/
            └─ details.json
```

### Sample Project File Content

Following is a sample listing of the content of a custom resource definition (as defined in the project archetype) that is stored in the `src/main/resources/custom-resources/<custom resource name>/details.json`[^1] file in the project content on the local filesystem.

[^1]: The `<custom resource name>` placeholder stands for the name of the custom resource that serves as the directory name.

??? "src/main/resources/custom-resources/Custom REST Host/details.json"
    ```json
    {% include "../../../../../maven/archetypes/vcfa-all-apps/src/main/resources/archetype-resources/src/main/resources/custom-resources/Custom REST Host/details.json" %}
    ```

## Export

To export a custom resource from the {{ products.vra_9_full_name }} server (pull the content), you need to add the custom resource name as a list item of the `custom-resource` element in the `content.yaml` content descriptor file for the project.

!!! Tip
    Alternatively, if you want to export all custom resource objects from the project on the {{ products.vra_9_full_name }} server, you can configure the `custom-resource` element with no value (i.e. its value is `null`). For details, see the [Content Filtering](../all-apps/index.md#content-filtering) section.

Following is a sample listing of the `content.yaml` file for a project that exports only the `Custom REST Host` custom resource from the project on the {{ products.vra_9_full_name }} server.

```yaml title="content.yaml"
custom-resource:
  - Custom REST Host
# ...
```

## Import

When you import a custom resource to a project on a {{ products.vra_9_full_name }} server (push operation), {{ general.bta_name }} matches the custom resource object by its name (the name of the directory under `src/main/resources/custom-resources` in the project content on your local filesystem) and performs one of the following operations.

!!! Warning

    Note that for push operations for {{ products.vra_9_full_name }} projects for All Apps organizations, {{ general.bta_name }} uses the content filtering rules that you define in the `content.yaml` content descriptor file. For details, see the [Content Filtering](../all-apps/index.md#content-filtering) section.

- If a custom resource with the same name does not exist on the server, {{ general.bta_name }} creates a new custom resource with the details from the local project files.
- If a custom resource with the same name already exists on the server, {{ general.bta_name }} checks if there are any differences between the local copy and the server copy and performs one of the following operations.
    - If there are no differences between the local copy and the server copy, {{ general.bta_short_name }} skips the operation and does not update the custom resource on the server.
    - If there are differences between the local copy and the server copy, {{ general.bta_short_name }} deletes the existing custom resource definition on the server and creates it again with the definition from the local project.

        !!! Warning

            Note that {{ products.vra_9_full_name }} does not allow you to delete an existing custom resource definition if there is an instance if this resource in a {{ products.vra_9_short_name }} deployment. For details, see [Known Issues](#known-issues).

## Known Issues

This section contains a list of known issues with regards to custom resources in a {{ page.meta.vars.project.type }} project.

- **Issue**: Update of a custom resource definition fails if the custom resource is used in a {{ products.vra_9_short_name }} deployment.

    **Explanation**: When {{ general.bta_name }} tries to update a custom resource that is in use by a {{ products.vra_9_short_name }} deployment, and if it fails to delete the custom resource definition in order to re-create it, {{ general.bta_short_name }} now attempts to update the custom resource by pre-fetching its ID. Once {{ general.bta_short_name }} has the ID, it removes it from the custom resource and re-assigns it again before the import process is initiated. This would allows {{ general.bta_short_name }} to create an updated custom resource that is imported to {{ products.vra_9_full_name }} regardless of whether it is used by a deployment. However, the update capabilities are limited and may not allow you to update all fields.
