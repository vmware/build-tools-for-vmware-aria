---
title: Notifications
---

## Overview

{{ products.vro_9_full_name }} includes a number of default email notifications that it uses to send email notifications in a number of event types, called scenarios. An organization administrator can customize these notification definitions, including the text and the branding elements of the email.

## Project Structure


{{ general.bta_name }} stores notification objects in several files in a directory with the notification configuration name under the `src/main/resources/scenarios` directory in the project content on the local filesystem.

Following is a sample listing of the local filesystem for a notification with the name **Deployment Lease Expired**.

```ascii title="Local Project Content"
src/
└─ main/
    └── resources/
        └── scenarios/
            └── Deployment Lease Expired/
                ├── details.json
                └── template.html
```

Following is a list of the files for each blueprint object with a short description of their content and purpose.

- The `details.json` file contains the metadata information for the notification.
- The `template.html` file contains the HTML definition for the notification.

## Export

To export the definition of a notification from the {{ products.vra_9_full_name }} server (pull the content), you need to add the notification name as a list item of the `scenario` element in the `content.yaml` content descriptor file for the project.

!!! Tip
    Alternatively, if you want to export all resource action objects from the project on the {{ products.vra_9_full_name }} server, you can configure the `resource-action` element with no value (i.e. its value is `null`). For details, see the [Content Filtering](../all-apps/index.md#content-filtering) section.

Following is a sample listing of the `content.yaml` file for a project that exports only the `Deployment Lease Expired` notification configuration the {{ products.vra_9_full_name }} server.

```yaml title="content.yaml"
scenario:
  - Deployment Lease Expired
# ...
```

## Import

When you import a scenario to a {{ products.vra_9_full_name }} server (push operation), {{ general.bta_name }} matches the notification object by its name (the name of the directory under `src/main/resources/scenarios` in the project content on your local filesystem) and performs one of the following operations.

!!! Warning

    Note that for push operations for {{ products.vra_9_full_name }} projects for All Apps organizations, {{ general.bta_name }} uses the content filtering rules that you define in the `content.yaml` content descriptor file. For details, see the [Content Filtering](../all-apps/index.md#content-filtering) section.

- If a notification with the same name does not exist on the server, {{ general.bta_name }} creates a new notification with the details from the local project files.
- If a notification with the same name already exists on the server, {{ general.bta_name }} checks if there are any differences between the local copy and the server copy and performs one of the following operations.
    - If there are no differences between the local copy and the server copy, {{ general.bta_short_name }} skips the operation and does not update the notification on the server.
    - If there are differences between the local copy and the server copy, {{ general.bta_short_name }} deletes the existing notification object on the server and creates it again with the definition from the local project.
