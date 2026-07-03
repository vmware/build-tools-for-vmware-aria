---
title: Custom Resources
---

## Overview

Custom Resources are items that are defined inside of {{ products.vra_9_full_name }}. They extend the capabilities of blueprints by giving you the ability to define your own resource that calls an Orchestrator workflow. Custom Resources are defined in the `details.json` file under `custom-resources`.

## Known Issues

When we try to update a custom resource that is in use by a deployment, and if the deletion fails, now we will attempt to update the custom resource by pre-fetching its ID. Once we have the ID, we would remove it from the custom resource and re-assigned it again before the import process is initiated. This would allow us to create an updated custom resource that would be imported to {{ products.vra_9_full_name }} no matter if it is used by a deployment. However, update capabilities are limited and may not allow you to update all fields.
