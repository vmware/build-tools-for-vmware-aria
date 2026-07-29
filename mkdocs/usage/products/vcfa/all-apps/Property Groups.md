---
title: Property Groups
---

## Overview

Property groups are defined in {{ products.vra_9_full_name }} as sets of properties that always appear together and allow you to add these properties to different VCF Automation designs and save the time of adding the same multiple properties one by one. In addition, you have a single place to maintain or modify the set of properties, which ensures their consistent application.

In {{ products.vra_9_full_name }}, there are two types of property groups.

- **Input** property groups gather and apply a consistent set of properties at user request time.
- **Constant** property groups silently apply known properties and in effect are invisible metadata.

## Project Structure

{{ general.bta_name }} stores each property group object in a single JSON file under the `src/main/resources/property-groups` directory in the project content on the local filesystem. The JSON file contains the full property group definition.

Following is a sample listing of the local filesystem for a property group with the name **Custom VM Properties**.

```ascii title="Local Project Content"
src/
└─ main/
   └─ resources/
      └─ property-groups/
         └─ Custom VM Properties.json
```

### Sample Project File Content

Following is a sample listing of the content of a property group definition of type **Input** (as defined in the project archetype) that is stored in the `src/main/resources/property-groups/<property group name>.json`[^1] file in the project content on the local filesystem.

[^1]: The `<property group name>` placeholder stands for the name of the property group.

??? "src/main/resources/property-groups/Custom VM Properties.json"
    ```json
    {% include "../../../../../maven/archetypes/vcfa-all-apps/src/main/resources/archetype-resources/src/main/resources/property-groups/Custom\ VM\ Properties.json" %}
    ```

## Export

To export the definition of a property group from the {{ products.vra_9_full_name }} server (pull the content), you need to add the property group name as a list item of the `property-group` element in the `content.yaml` content descriptor file for the project.

!!! Tip
    Alternatively, if you want to export all property group objects from the project on the {{ products.vra_9_full_name }} server, you can configure the `property-group` element with no value (i.e. its value is `null`). For details, see the [Content Filtering](../all-apps/index.md#content-filtering) section.

Following is a sample listing of the `content.yaml` file for a project that exports only the `Custom VM Properties` property group from the project on the {{ products.vra_9_full_name }} server.

```yaml title="content.yaml"
property-group:
  - Custom VM Properties
# ...
```

## Import

When you import a property group to a project on a {{ products.vra_9_full_name }} server (push operation), {{ general.bta_name }} matches the property group object by its name (the name of the JSON definition file under `src/main/resources/property-groups` in the project content on your local filesystem) and performs one of the following operations.

!!! Warning

    Note that for push operations for {{ products.vra_9_full_name }} projects for All Apps organizations, {{ general.bta_name }} uses the content filtering rules that you define in the `content.yaml` content descriptor file. For details, see the [Content Filtering](../all-apps/index.md#content-filtering) section.

- If a property group with the same name does not exist on the server, {{ general.bta_name }} creates a new property group with the details from the local project files.
- If a property group with the same name already exists on the server, {{ general.bta_name }} checks if there are any differences between the local copy and the server copy and performs one of the following operations.
    - If there are no differences between the local copy and the server copy, {{ general.bta_short_name }} skips the operation and does not update the property group on the server.
    - If there are differences between the local copy and the server copy, {{ general.bta_short_name }} updates the existing property group object on the server with the definition from the local project (if possible).

        !!! Warning

            Note that {{ products.vra_9_full_name }} does not allow you to update the scope of a property group. As a result, if a property group already exists on the {{ products.vra_9_full_name }} server and you try to import a property group definition from your local project that has the same name as the existing property group but a different scope, the import operation will fail.
