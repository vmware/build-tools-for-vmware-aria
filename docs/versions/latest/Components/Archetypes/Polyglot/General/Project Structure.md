# Project Structure

## Overview

The polyglot archetype supports multiple actions in the src folder. Each has it's own structure depending on the type of the package.

## Table Of Contents

- [General](#general)
- [Templates](#templates)
  - [NodeJS](#nodejs)
  - [Python](#python)
  - [Powershell](#powershell)

### General

- `polyglot.json` - marks a folder as a polyglot package. Contains information about the package.
  - If the `platform.action` is set to `auto` the name of the folder will be used as the package name.
  - If the `platform.protocolType` is set to one of possible values `'Ssl3' | 'Tls' | 'Tls11' | 'Tls12' | 'Tls13'` all external modules(Added through Import-Module in the code) will be downloaded using the selected encryption protcol, otherwise the system default is used.

#### `polyglot.json`

The `polyglot.json` file is used to define the package. It is a JSON file with the following properties:

```json5
{
    "platform": {
        "action": "auto", // The name of the action, if set to auto, the folder name will be used, otherwise the value will be used
        "entrypoint": "handler.handler", // The entrypoint for the action. First part is the file name, second part is the function name
        "runtime": "python", // The runtime for the action. Check below for possible values
        "tags": [], // Tags for the action... TBD
        "memoryLimitMb": 64, // The memory limit for the action
        "timeoutSec": 60, // The timeout for the action
        "protocolType": "Tls12", // Use to download external modules using a specific protocol. Check below for possible values
    }
    // If VRO
    "vro": {
      "module": "${groupId}",
      "inputs": { // These are the inputs and their type
        "limit": "number",
        "vraEndpoint": "CompositeType(host:string,base:string):VraEndpointType"
      },
      "outputType": "Array/string"
    },
    // If ABX
    "abx": {
        "inputs": {
            "someKey": "someValue"
            // ...
        }
    },
    "files": ["%out", "!**/package.json", "!**/polyglot.json", "!**/requirements.txt"] // Needed for the build, don't edit
}
```

##### `runtime`

###### VRO

- `node:12`
- `node:14`
- `node:18`
- `node:20`
- `powercli:11-powershell-6.2`
- `powercli:12-powershell-7.1`
- `powercli:12-powershell-7.4`
- `powercli:13-powershell-7.4`
- `powershell:7.4`
- `python:3.7`
- `python:3.10`

###### ABX

- `nodejs`
- `powershell`
- `python`

##### `protocolType`

- `Ssl3`
- `Tls`
- `Tls11`
- `Tls12`
- `Tls13`

### Templates

There are three templates:

- src/template-nodejs ([NodeJs](#nodejs))
- src/template-powershell ([Powershell](#powershell))
- src/template-python ([Python](#python))

Note: you do not need to delete template folders. Any folder starting with `template-` is ignored.

#### NodeJS

- `handler.ts` - holds all the logic for the action
- `tsconfig.json` - used by typescript to compile the code during `mvn package`, since rootDirs and srcDirs are dynamic, there are placeholders in this file. **do not modify**

> Dependencies
>
> NodeJS' dependencies are defined in the `package.json` file in the `dependencies` property. Specify only dependencies to your code, there are no mandatory dependencies as well as no default ones.

#### Python

- `handler.py` - holds all the logic for the action
- `requirements.txt` - holds dependency information e.g. requirements.txt:

    ```python
    requests==2.23.0
    ```

> Dependencies
>
> These follow the standard python `requirements.txt` structure. No default or mandatory dependencies. [More Info](https://learnpython.com/blog/python-requirements-file/)

#### Powershell

- `handler.ps1` - holds all the logic for the action
