---
title: Policies
---

## Overview

A policy (or a policy template) in {{ products.vra_9_full_name }} is a set of rules or parameters that are applied to VCF Automation projects to govern deployment requests from entitled users and impose guardrails around provisioning requests.

In {{ products.vra_9_full_name }} All Apps organizations, there are four types of policy templates.

- **Approval** policies allow you to exercise control over deployment and day-2 action requests before they are run by reviewing requests before resources are consumed or destroyed.
- **Day 2 Actions** policies allow you to control what changes users can make to deployments and their component resources.
- **IaaS Resource** policies allow you to validate and control what namespace resources deployments can consume when users request catalog items or create resources using the services console in {{ products.vra_9_full_name }}.
- **Lease** policies allow you to control the amount of time that a deployment is available to your users and help you reduce the need to intervene manually to reclaim resources

## Project Structure

{{ general.bta_name }} stores a policy template object in a single JSON file that contain the full policy template definition. For ease of management, {{ general.bta_name }} stores the JSON definition files from each policy type in a separate directory with the name of the policy type under the `src/main/resources/policies` directory in the project on the local filesystem. The name of each JSON file is the name of the policy template that is configured in the project and is used only for filtering purposes in push and pull operations. The actual policy name as it appears in {{ products.vra_9_full_name }} is an attribute in the file contents.

!!! Note

    Since {{ products.vra_9_full_name }} allows you to configure multiple policy templates with the same name, when you are exporting policy templates from the remote server, {{ general.bta_name }} stores the policy template definition on your local filesystem as a JSON file that uses the format `policyName[-index].json`, where `-index` is an optional numerical index that  {{ general.bta_name }} adds only if there are multiple policy templates with the same name.

Following is a sample listing of the local filesystem for a four policies (one of each supported type).

```ascii title="Local Project Content"
src/
└─ main/
   └─ resources/
      └─ policies/
         └─ approval/
            └─ vm_resize_approval.json
         └─ day2-actions/
            └─ vm_remove_disk.json
         └─ iaas-resource/
            └─ large-namespace-policy.json
         └─ lease/
            └─ short_lease.json
```

### Sample Project File Content

This section contains sample listings of the content of the policy template definition files from each type as they are stored in the `src/main/resources/policies/` directory in the project content on the local filesystem.

Following is a sample listing of the content of the `vm_resize_approval.json` file for the **vm_resize_approval** approval policy (as defined in the project archetype).

??? "src/main/resources/policies/approval/vm_resize_approval.json"
    ```json
    {% include "../../../../../maven/archetypes/vcfa-all-apps/src/main/resources/archetype-resources/src/main/resources/policies/approval/vm_resize_approval.json" %}
    ```

Following is a sample listing of the content of the `vm_remove_disk.json` file for the **vm_remove_disk** day-2 action policy (as defined in the project archetype).

??? "src/main/resources/policies/day2-actions/vm_remove_disk.json"
    ```json
    {% include "../../../../../maven/archetypes/vcfa-all-apps/src/main/resources/archetype-resources/src/main/resources/policies/day2-actions/vm_remove_disk.json" %}
    ```

Following is a sample listing of the content of the `large-namespace-policy.json` file for the **large-namespace-policy** day-2 action policy (as defined in the project archetype).

??? "src/main/resources/policies/iaas-resource/large-namespace-policy.json"
    ```json
    {% include "../../../../../maven/archetypes/vcfa-all-apps/src/main/resources/archetype-resources/src/main/resources/policies/iaas-resource/large-namespace-policy.json" %}
    ```

Following is a sample listing of the content of the `short_lease.json` file for the **short_lease** day-2 action policy (as defined in the project archetype).

??? "src/main/resources/policies/lease/short_lease.json"
    ```json
    {% include "../../../../../maven/archetypes/vcfa-all-apps/src/main/resources/archetype-resources/src/main/resources/policies/lease/short_lease.json" %}
    ```

## Export

To export a policy template from the {{ products.vra_9_full_name }} server (pull the content), you need to add the name of the policy template definition file (the name of the JSON definition file without the extension) as a list item of the element with the name of the policy type under the `policy` element in the `content.yaml` content descriptor file for the project.

!!! Tip
    Alternatively, if you want to export all policy template objects from a specific type from the project on the {{ products.vra_9_full_name }} server, you can configure the policy type element under the `policy` element with no value (i.e. its value is `null`). For details, see the [Content Filtering](../all-apps/index.md#content-filtering) section.

Following is a sample listing of the `content.yaml` file for a project that exports only the specified policy templates (one from each type) from the project on the {{ products.vra_9_full_name }} server.

```yaml title="content.yaml"
policy:
  approval:
    - vm_resize_approval
  day2-actions:
    - vm_remove_disk
  iaas-resource:
    - large-namespace-policy
  lease:
    - short_lease
# ...
```

## Import

When you import policy templates from any type to a project on a {{ products.vra_9_full_name }} server (push operation), {{ general.bta_name }} uses the content filtering rules to create a list of policy templates to import to the project, matches each policy template object by its name and ID (the name and the UUID of the policy template that are configured as JSON attributes in the content of the definition file on the local filesystem), and performs one of the following operations.

!!! Warning

    Note that for push operations for {{ products.vra_9_full_name }} projects for All Apps organizations, {{ general.bta_name }} uses the content filtering rules that you define in the `content.yaml` content descriptor file. For details, see the [Content Filtering](../all-apps/index.md#content-filtering) section.

- If a policy with the same name and ID does not exist on the server, {{ general.bta_name }} creates a new policy template with the definition from the local project file.
- If a policy with the same name and ID already exists on the server, {{ general.bta_name }} checks if there are any differences between the local copy and the server copy and performs one of the following operations.
    - If there are no differences between the local copy and the server copy, {{ general.bta_short_name }} skips the operation and does not update the policy template object on the server.
    - If there are differences between the local copy and the server copy, {{ general.bta_short_name }} updates the existing policy template object on the server with the definition from the local project.
