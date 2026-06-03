# Install workflow

Most common case of today is to use  Install workflow from PsCoE installer library.

## Overview

We can run Install workflow automatically and pass to this workflow all necessary parameters:

- jsonString
- tags
- blacklist

## Table Of Contents

1. [vRO workflow run configuration](#vro-workflow-run-configuration)
2. [Installation/configuration values](#installationconfiguration-values)
3. [Installation/configuration file example](#installjson-example)

### vRO workflow run configuration

```ts
vro_run_workflow=true
vro_run_workflow_id=1490692845582937823496790834565483423
vro_run_workflow_input_file_path=./install.json
vro_run_workflow_output_file_path=./output.json
vro_run_workflow_timeout=300
```

### Installation/configuration values

The installation/configuration values are passed as a json file (or yaml). Each root property of this file should have the name of an input value of the targeted workflow.

### install.json example

```JSON
{
  "tags": [
    "conn"
  ],
  "jsonString": {
    "conn": {
      "username": "admin",
      "password": "p@$$w0rd"
    }
  }
}
```
