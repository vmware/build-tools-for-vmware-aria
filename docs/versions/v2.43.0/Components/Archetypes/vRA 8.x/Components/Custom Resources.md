# Custom Resources

They are items that can be used in the Blueprints/Cloud Templates when the existing Resource Types are not enough.

## Overview

Custom Resources are items that are defined inside of Cloud Assembly. They extend the capabilities of Cloud Templates by giving you the ability to define your own Resource, that calls either a vRO Workflow or an ABX Action (in the latest version of vRA). Custom Resources are defined in the `content.yaml` file under `custom-resource` ([See content.yaml](../General/Content.md)).

## Table Of Contents

1. [Known Issues](#known-issues)

### Known Issues

#### Updating A Custom Resource When In Use By Deployment

When we try to update a CR that is in use by a deployment and if the deletion fails, now we will attempt to update the CR by pre-fetching it's ID. Once we have the ID, we would remove it from the CR and re-assigned it again before the importing process is initiated. This would allow us to create an updated CR that would be imported to vRA no matter if it is used by a deployment. However, update capabilities are limited and may not allow you to update all fields.
