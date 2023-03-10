# Project Structure

## Overview
The polyglot archetype supports multiple actions in the src folder. Each has it's own structure depending on the type of the package.

## Table Of Contents:
1. [General](#general)
2. [Templates](#templates)
3. [NodeJS](#nodejs)
4. [Python](#python)
5. [Powershell](#powershell)

### General
- `polyglot.json` - marks a folder as a polyglot package. Contains information about the package.
    - If the `platform.action` is set to `auto` the name of the folder will be used as the package name.
    - If the `platform.protocolType` is set to one of possible values `'Ssl3' | 'Tls' | 'Tls11' | 'Tls12' | 'Tls13'` all external modules(Added through Import-Module in the code) will be downloaded using the selected encryption protcol, otherwise the system default is used.

### Templates
There are three templates:
* src/template-nodejs ([NodeJs](#nodejs))
* src/template-powershell ([Powershell](#powershell))
* src/template-python ([Python](#python))


Note: you do not need to delete template folders. Any folder starting with `template-` is ignored.

### NodeJS
- `handler.ts` - holds all the logic for the action
- `tsconfig.json` - used by typescript to compile the code during `mvn package`, since rootDirs and srcDirs are dynamic, there are
  placeholders in this file. **do not modify**

> Dependencies
> 
> NodeJS' dependencies are defined in the `package.json` file in the `dependencies` property. Specify only dependencies to 
> your code, there are no mandatory dependencies as well as no default ones.

### Python
- `handler.py` - holds all the logic for the action
- `requirements.txt` - holds dependency information e.g. requirements.txt:
    ```python
    requests==2.23.0
    ```

> Dependencies
>
> These follow the standard python `requirements.txt` structure. No default or mandatory dependencies. [More Info](https://learnpython.com/blog/python-requirements-file/)

### Powershell
- `handler.ps1` - holds all the logic for the action

