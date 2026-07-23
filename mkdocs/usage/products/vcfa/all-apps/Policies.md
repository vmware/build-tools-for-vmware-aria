---
title: Policies
---

## Overview

There are four types of policies.

- Approval
- Day 2 Actions
- IaaS Resource
- Lease

## Structure

Below is an example structure of policies export.

Example `content.yaml`

```yaml
policy:
  approval:
    - examplePolicy1
  day2-actions:
    - examplePolicy2
  iaas-resource:
    - examplePolicy3
  lease:
    - examplePolicy4

# ...
```

Policy objects have the following structure in the `./src` directory.

```ascii
src/
├─ main/
│  ├─ resources/
│  │  ├─ policies/
│  │  │  ├─ approval/
│  │  │  │  ├─ examplePolicy1.json
│  │  │  ├─ day2-actions/
│  │  │  │  ├─ examplePolicy2.json
│  │  │  ├─ iaas-resource/
│  │  │  │  ├─ examplePolicy3.json
│  │  │  ├─ lease/
│  │  │  │  ├─ examplePolicy4.json
```

## Operations

### Import

When importing policies, files are read form the filesystem and the `content.yaml` filter is by filename. All non-hidden files are read from the directory and if the name of the file, without the extension, matches the list in the `content.yaml` file, the policy is imported. The filename is only important for filtering. Actual policy fields are read from the file contents. If there is a policy with the same ID on the server, an update will be performed. Otherwise, the policy will be created instead, using the same ID that is found in the file.

### Export

When exporting a policy, a JSON file is created on the filesystem. The filename uses the format `policyName[-index].json`, where an index is added only if there are multiple policies with the same name.
