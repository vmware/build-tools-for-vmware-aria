# Custom Resources
They are items that can be used in the Blueprints/Cloud Templates when the existing Resource Types are not enough.

## Overview
Custom Resources are items that are defined inside of Cloud Assembly. They extend the capabilities of Cloud Templates by 
giving you the ability to define your own Resource, that calls either a vRO Workflow or an ABX Action ( in the latest version of vRA ).
Custom Resources are defined in the `content.yaml` file under `custom-resource` ( [See content.yaml](../General/Content.md) ).

## Table Of Contents:
1. [Known Issues](#known-issues)

### Known Issues

#### Updating A Custom Resource When In Use By Deployment
We are aware this is an issue, but there is currently no workaround for this. In the case where the Custom Resource is 
in use, then we skip importing it, and we log an error, but continue execution.


