---
title: Content Policies
---

## Overview

There are 4 types of content policies:

- Approval
- Day 2 Actions
- IaaS Resource
- Lease

## Structure

Below is an example structure of content policies export.

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

Structure

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

When importing policies, files are read form the filesystem, and the content.yaml filter is by filename. All non-hidden files are read from the folder, and if the name of the file, without the extension matches the list in content.yaml, the policy will be imported. The filename is only important for filtering. Actual policy fields are read from the file contents. If there is a policy with the same id on the server, an update will be performed. Otherwise, the policy will be created instead, using the same id, that is found in the file.

### Export

When exporting a policy, a json file will be created on the filesystem. The filename will be the policyName[-index].json.
Index will be added only if there are multiple policies with the same name.
