
# Content

## Overview

The Operations elements that you want to work with are described in the `content.yaml`

## Table Of Contents

1. [Content.yaml](#contentyaml)

### Content.yaml

By default the `content.yaml` looks like this.

```yaml
view:
dashboard:
alert-definition:
symptom-definition:
policy:
default-policy:
recommendation:
super-metric:
metric-config:
report:
custom-group:
```

All object types are list of values except for `default-policy` which is a single policy name. 


#### Import Rules for content types

- Only explicitly defined objects are imported.
- `default-policy` does not actually import a policy - it only sets an existing policy with the given name as default.

#### Export Rules for content types

- Only explicitly defined objects are exported.
- `default-policy` is not exported. If you want to export the policy set as default it needs to be added to the `policy` list.

#### Examples

Example of `content.yaml` file.

```yaml
view:
  - Cluster Basic Inventory
dashboard:
  - Assess Cost
alert-definition:
  - Cluster is disbalanced
symptom-definition:
  - Automation service is down
  - Cloud Proxy is down
recommendation:
  - Allocate more disk space if required
super-metric:
  - Group CPU Average
report:
  - Cluster Cost Report
metric-config:
  - vSAN Savings
custom-group:
  - VCF World
policy:
  - Policy for Virtual Machines - Risk Profile 1
default-policy: Default Policy
```
