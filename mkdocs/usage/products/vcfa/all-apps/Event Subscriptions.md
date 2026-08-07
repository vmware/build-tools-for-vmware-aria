---
title: Even Subscriptions
---

## Overview

Event subscriptions (aka Event Broker Service subscriptions or EBS subscriptions) in {{ products.vra_9_full_name }} are configuration rules that link deployment lifecycle events to external actions. They listen for specific triggers, apply filters, and run custom code that allows administrators to modify provisioning properties or integrate with third-party tools when a system action occurs.

## Project Structure

{{ general.bta_name }} stores an event subscription object in a single JSON file with the name of the subscription under the `src/main/resources/subscriptions` directory in the project content on the local filesystem. The JSON file contains the full definition of the event subscription.

Following is a sample listing of the local filesystem for an event subscription with the name **Add user to deployment**.

```ascii title="Local Project Content"
src/
└── main/
    └── resources/
        └── subscriptions/
            └── Add user to deployment.json
```

### Sample Project File Content

Following is a sample listing of the content of an event subscription definition (as defined in the project archetype) that is stored in the `src/main/resources/subscriptions/<subscription name>.json`[^1] file in the project content on the local filesystem.

[^1]: The `<subscription name>` placeholder stands for the name of the event subscription.

??? "src/main/resources/subscriptions/Add user to deployment.json"
    ```json
    {% include "../../../../../maven/archetypes/vcfa-all-apps/src/main/resources/archetype-resources/src/main/resources/subscriptions/Add user to deployment.json" %}
    ```

## Export

To export the definition of an event subscription from the {{ products.vra_9_full_name }} server (pull the content), you need to add the subscription name as a list item of the `subscription` element in the `content.yaml` content descriptor file for the project.

!!! Tip
    Alternatively, if you want to export all workflow catalog item objects from the project on the {{ products.vra_9_full_name }} server, you can configure the `workflow` element with no value (i.e. its value is `null`). For details, see the [Content Filtering](../all-apps/index.md#content-filtering) section.

Following is a sample listing of the `content.yaml` file for a project that exports only the `Add user to deployment` event subscription from the project on the {{ products.vra_9_full_name }} server.

```yaml title="content.yaml"
subscription:
  - Add user to deployment
# ...
```

## Import

When you import an event subscription to a project on a {{ products.vra_9_full_name }} server (push operation), {{ general.bta_name }} matches the subscription object by its name (the name of the JSON definition file under `src/main/resources/subscriptions` in the project content on your local filesystem) and performs one of the following operations.

!!! Warning

    Note that for push operations for {{ products.vra_9_full_name }} projects for All Apps organizations, {{ general.bta_name }} uses the content filtering rules that you define in the `content.yaml` content descriptor file. For details, see the [Content Filtering](../all-apps/index.md#content-filtering) section.

- If an event subscription with the same name does not exist on the server, {{ general.bta_name }} creates a new event subscription with the details from the local project file.
- If an event subscription with the same name already exists on the server, {{ general.bta_name }} checks if there are any differences between the local copy and the server copy and performs one of the following operations.
    - If there are no differences between the local copy and the server copy, {{ general.bta_short_name }} skips the operation and does not update the event subscription on the server.
    - If there are differences between the local copy and the server copy, {{ general.bta_short_name }} updates the existing event subscription object on the server with the definition from the local project.
