---
title: Resource Actions
---

## Overview

Resource actions in {{ products.vra_9_full_name }} allow you to create custom day-2 operation action for already provisioned resources or deployments that allow entitled users to modify, manage, or interact with that resource. Custom resource actions can be based on VCF Operations Orchestrator
workflows or extensibility actions.

## Project Structure

{{ general.bta_name }} stores resource action objects in several files in a directory with the resource action name under the `src/main/resources/resource-actions` directory in the project content on the local filesystem.

Following is a sample listing of the local filesystem for a resource action with the name **Add CD-ROM to Deployment**.

```ascii title="Local Project Content"
src/
└─ main/
   └─ resources/
      └─ resource-actions/
         └─ Add CD-ROM to Deployment/
            ├─ Add CD-ROM to Deployment__FormData.json
            ├─ details.json
            └─ styles.css
```

Following is a list of the files for each resource action object with a short description of their content and purpose.

- The `details.json` file contains the full definition of the resource action, including the metadata information.
- The `<resource action name>__FormData.json`[^1] file contains the custom form definition for the resource action.
- The `style.css` file is optional and contains the CSS for the custom form of the resource, if available.

[^1]: The `<resource action name>` placeholder stands for the name of the resource action that matches the name of the parent directory.

### Sample Project File Content

This section contains sample listings of the content of the files for each resource action object as they are stored in the `src/main/resources/resource-actions/<resource action name>/`[^2] directory in the project content on the local filesystem.

[^2]: The `<resource action name>` placeholder stands for the name of the resource action that determines the name of the directory.

Following is a sample listing of the content of the `details.json` file for the **Add CD-ROM to Deployment** resource action (as defined in the project archetype).

??? "src/main/resources/resource-actions/Add CD-ROM to Deployment/details.json"
    ```json
    {% include "../../../../../maven/archetypes/vcfa-all-apps/src/main/resources/archetype-resources/src/main/resources/resource-actions/Add CD-ROM to Deployment/details.json" %}
    ```

Following is a sample listing of the content of the `Add CD-ROM to Deployment__FormData.json` file (custom form) for the **Add CD-ROM to Deployment** resource action (as defined in the project archetype).

??? "src/main/resources/resource-actions/Add CD-ROM to Deployment/Add CD-ROM to Deployment__FormData.json"
    ```json
    {% include "../../../../../maven/archetypes/vcfa-all-apps/src/main/resources/archetype-resources/src/main/resources/resource-actions/Add CD-ROM to Deployment/Add CD-ROM to Deployment__FormData.json" %}
    ```

<!-- Remove comment once there is a sample CSS file to show
Following is a sample listing of the content of the `style.css` file for the **Add CD-ROM to Deployment** resource action (as defined in the project archetype).

??? "src/main/resources/resource-actions/Add CD-ROM to Deployment/style.css"
    ```css
    {% include "../../../../../maven/archetypes/vcfa-all-apps/src/main/resources/archetype-resources/src/main/resources/resource-actions/Add CD-ROM to Deployment/styles.css" %}
    ```
-->

## Export

To export the definition of a resource action from the {{ products.vra_9_full_name }} server (pull the content), you need to add the resource action name as a list item of the `resource-action` element in the `content.yaml` content descriptor file for the project.

!!! Tip
    Alternatively, if you want to export all resource action objects from the project on the {{ products.vra_9_full_name }} server, you can configure the `resource-action` element with no value (i.e. its value is `null`). For details, see the [Content Filtering](../all-apps/index.md#content-filtering) section.

Following is a sample listing of the `content.yaml` file for a project that exports only the `Add CD-ROM to Deployment` resource action from the project on the {{ products.vra_9_full_name }} server.

```yaml title="content.yaml"
resource-action:
  - Add CD-ROM to Deployment
# ...
```

## Import

When you import a resource action to a project on a {{ products.vra_9_full_name }} server (push operation), {{ general.bta_name }} matches the resource action object by its name (the name of the directory under `src/main/resources/resource-actions` in the project content on your local filesystem) and performs one of the following operations.

!!! Warning

    Note that for push operations for {{ products.vra_9_full_name }} projects for All Apps organizations, {{ general.bta_name }} uses the content filtering rules that you define in the `content.yaml` content descriptor file. For details, see the [Content Filtering](../all-apps/index.md#content-filtering) section.

- If a resource action with the same name does not exist on the server, {{ general.bta_name }} creates a new resource action with the details from the local project files.
- If a resource action with the same name already exists on the server, {{ general.bta_name }} checks if there are any differences between the local copy and the server copy and performs one of the following operations.
    - If there are no differences between the local copy and the server copy, {{ general.bta_short_name }} skips the operation and does not update the resource action on the server.
    - If there are differences between the local copy and the server copy, {{ general.bta_short_name }} deletes the existing resource action object on the server and creates it again with the definition from the local project.
