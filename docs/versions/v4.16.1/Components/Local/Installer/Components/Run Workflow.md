# Run Workflow

## Overview

Installer supports executing any existing Workflow automatically at the end of the installation process and providing to the Workflow all necessary parameters of different types.

## Table Of Contents

1. [Configuration](#configuration)
2. [Supported input value types](#supported-input-value-types)
3. [Example](#example)

### Configuration

Executing a Workflow is done by confirming by providing the following configuration:

```bash
vro_run_workflow=true # defines whether or not to execute a Workflow. If value if set to `false` all the other configurations are skipped. Corresponds to "Run vRO workflow?" installer prompt.
vro_run_workflow_id=1490692845582937823496790834565483423 # Existing Workflow ID
vro_run_workflow_input_file_path=./install.json # JSON or YAML file that contains inputs for the executed Workflow. Each root property of this file should have the name of an input value of the targeted Workflow.
vro_run_workflow_output_file_path=./output.json # JSON or YAML file in which the Workflow execution outputs are stored.
vro_run_workflow_err_file_path=./workflow.err # file in which error encountered during the Workflow execution is written.
vro_run_workflow_timeout=300  # Workflow execution timeout
```

### Supported input value types

Supported Workflow input types are:

- string
- number
- boolean
- Array/string

All other types except the above mentioned are send as parameter of type `string`.

### Example

A common use case is to run an Installation Workflow which is meant to prepare and store configurations and objects required for consuming the solution such as REST Hosts, vCenter SDK Connection / Endpoint, credentials stored as Secured Strings in Configuration Elements, etc.

```YAML
jsonString:
  scheduled-snapshot:
    evaluationTime: "18:00:00"
    retrainedSnapshotCount: 2
    manageSnapshotTimeoutMinutes: 120
  vcenter-plugin:
    endpoints:
      - hostname: "vc-l-01a.corp.local"
        username: "administrator@vsphere.local"
        password: "VMware1!"
        ignoreCertificateWarnings: true
        sessionPerUser: false
        port: 443
  aria:
    automation:
      rest:
        hostname: "vra-l-01a.corp.local"
        port: 443
        username: "configurationadmin"
        password: "VMware1!"
tags: ["bak.scheduled-snapshot", "library.ts.vcenter", "library.ts.vra.authentication"]
blacklist: []
environment: null 

```
