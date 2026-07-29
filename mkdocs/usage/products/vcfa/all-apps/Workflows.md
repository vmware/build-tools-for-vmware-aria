---
title: Workflows
---

## Overview

You can publish workflows that are defined in {{ products.vro_9_full_name }} to the {{ products.vra_9_full_name }} catalog to make them available as self-service catalog items to project users in your organization. When publishing an Orchestrator workflow to the {{ products.vra_9_full_name }} catalog, you can configure a custom icon and a name for the catalog item that is different from the name of the default workflow name.

!!! Warning

    Note that if you want to add a custom form to the catalog item for an Orchestrator workflow, you need to create it in {{ products.vro_9_full_name }}.

## Project Structure

{{ general.bta_name }} stores each object for a workflow that you publish as a catalog item in a single JSON file with the name of the workflow under the `src/main/resources/workflows` directory in the project content on the local filesystem. The JSON file contains the full definition of the workflow catalog item.

Following is a sample listing of the local filesystem for a workflow catalog item with the name **Add a user to a user group**.

```ascii title="Local Project Content"
src/
└─ main/
   └─ resources/
      └─ workflows/
         └─ Add a user to a user group.json
```

### Sample Project File Content

Following is a sample listing of the content of a workflow catalog item definition (as defined in the project archetype) that is stored in the `src/main/resources/workflows/<workflow name>.json`[^1] file in the project content on the local filesystem.

[^1]: The `<workflow name>` placeholder stands for the name of the workflow catalog item.

??? "src/main/resources/workflows/Add a user to a user group.json"
    ```json
    {% include "../../../../../maven/archetypes/vcfa-all-apps/src/main/resources/archetype-resources/src/main/resources/workflows/Add a user to a user group.json" %}
    ```

## Export

To export the definition of a workflow catalog item from the {{ products.vra_9_full_name }} server (pull the content), you need to add the workflow catalog item name as a list item of the `workflow` element in the `content.yaml` content descriptor file for the project.

!!! Tip
    Alternatively, if you want to export all workflow catalog item objects from the project on the {{ products.vra_9_full_name }} server, you can configure the `workflow` element with no value (i.e. its value is `null`). For details, see the [Content Filtering](../all-apps/index.md#content-filtering) section.

Following is a sample listing of the `content.yaml` file for a project that exports only the `Add a user to a user group` workflow catalog item from the project on the {{ products.vra_9_full_name }} server.

```yaml title="content.yaml"
workflow:
  - Add a user to a user group
# ...
```

## Import

When you import a workflow catalog item to a project on a {{ products.vra_9_full_name }} server (push operation), {{ general.bta_name }} matches the workflow object by its name (the name of the JSON definition file under `src/main/resources/workflows` in the project content on your local filesystem) and performs one of the following operations.

!!! Warning

    Note that for push operations for {{ products.vra_9_full_name }} projects for All Apps organizations, {{ general.bta_name }} uses the content filtering rules that you define in the `content.yaml` content descriptor file. For details, see the [Content Filtering](../all-apps/index.md#content-filtering) section.

- If a workflow catalog item with the same name does not exist on the server, {{ general.bta_name }} creates a new workflow catalog item with the details from the local project files.
- If a workflow catalog item with the same name already exists on the server, {{ general.bta_name }} updates the existing workflow catalog item object on the server with the definition from the local project.
