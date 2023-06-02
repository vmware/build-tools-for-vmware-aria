# Codify

**Codify** is a CLI-based tool-set for interacting with vRealize content expressed as source code.


## Motivation

The following points serve as motivation for developing this tool-set.

- A team-wide way to develop and share content across different projects.
- Mechanism to distribute and share content between environments.
- Allow adopting of modern development practices into vRealize development in
    a consistent manner.

## Main Objectives

- Enable content sharing among team members
- Persistence, version control, and ability to review implementations
- Code/configuration transferability across environments
- Extensible framework for adding additional content types
- Static analysis enablement
- Human/developer-readable content and configuration

## Tool Requirements

- Support for content and configurations of the stack the ACoE team works with - initially vRO and vRA
- Support for the various execution run-times of vRO and vRA - JavaScript (Rhino), NodeJS, Python, PowerShell
- Decouple IDEs from implementation
- Extensibility - add new modules for various target systems, e.g. vRO, vRA, SaltStack, LCM, vROps, vRLI, vRNI, etc.

# Installation

## Prerequisites

**Required:**

- Node.js 14+

**Optional but recommended:**

The following prerequisites are needed for ABX bundle development (Python and PowerShell), testing and packaging.

- Python 3+
- Docker

## Installation procedure

```sh
# Install Codify globally
npm install -g @build-tools-for-vmware-aria/codify

# Verify Codify is installed
codify --version
```

# [Getting Started Guide](./docs/getting_started.md)

[Here](./docs/getting_started.md) you can find a Getting Started Guide to your first steps in using Codify.

# Codify Project Creator

To generate a new Codify project run the following command:

```sh
codify create
```

To generate a new Codify-supported ABX bundle project run the following command:

```sh
codify create --abx
```

The CLI will guide you through a series of questions for your project and will generate one for you.

# Codify Content Generator

Codify supports generating various types of content directly within your project. The content generator
supports the following commands:

```sh
# Generate Codify content from a vRO package available on the vRO server
codify create --from-package
# Alternatively you can set the default values directly using CLI options
codify create --from-package --package com.vmware.acoe.demo --target ./src
```

The CLI will guide you through a series of questions for your content and will generate it for you.

# Usage

In your **Codify** project create a **.env** file or export the following environment variables.

- The typical configuration for _on-prem vRA_ with _embedded_ vRO is:

    ```sh
    # .env
    VRO_HOST=<vra-host>
    VRO_USER=<username>
    VRO_PASS=<password>
    VRA_HOST=<vra-host>
    VRA_USER=<username>
    VRA_PASS=<password>
    ```

- If you are using a _standalone_ vRO with vRA-based authentication, you can specify a `VRO_AUTH_HOST` variable:

    ```sh
    # .env
    VRO_AUTH_HOST=<vra-host>
    VRO_HOST=<vro-host>
    VRO_USER=<username>
    VRO_PASS=<password>
    ```

- Instead of using username and password you can authenticate using a _refresh token_:

    ```sh
    # .env
    VRA_HOST=<vra-host>
    VRA_TOKEN=<refresh-token>
    VRO_HOST=<vro-host>
    VRO_TOKEN=<refresh-token>
    ```

- For _vRA Cloud_ you need to specify the vRA Cloud API host and pass an API TokenМ

    ```sh
    # .env
    VRA_HOST=api.mgmt.cloud.vmware.com
    VRA_TOKEN=<api-token>
    ```

    **_Note:_**: Authentication against vRA Cloud using username and password is not supported.

**_Note:_** _the `.env` file contain credentials and should not be committed to any code
repository. When you generate your project using the `codify create` command, the
`.gitignore` file already contains rules for the `.env` files, however if you create your project
from scratch, make sure you git-ignore this file._

Use the following commands to push / pull content from remote vRealize environment.

```sh
# To upload all objects from a directory and its children
codify upload --source "./src"

# To upload a single object
codify upload --source "./src/actions/com.vmware.acoe.demo/myAction.js"

# To download one or more actions (add an --action option for every action)
codify download --target "src/actions" --action "com.vmware.acoe.demo/myAction"

# To download one or more workflows (add a --workflow option for every workflow)
codify download --target "src/workflows" --workflow "ACoE/My Workflow"

# To download one or more configurations (add a --config option for every configuration)
codify download --target "src/configurations" --config "ACoE/My Config"

# To download one or more packages (add a --package option for every package)
codify download --target "packages" --package "com.vmware.acoe.mypackage"

# To download one or more cloud template (add a --template option for every VCT)
codify download --target "src/cloudtemplates" --template "My Template"

# To download one or more resource action (add a --day2 option for every resource action)
codify download --target "src/resourceactions" --day2 "myaction"

# To download one or more subscription (add a --subscription option for every subscription)
codify download --target "src/subscriptions" --subscription "My Subscription"

# To download one or more ABX actions (add an --abx option for every action)
codify download --target "src/abx" --abx "My Action"

# To run an action directly on the target environment
codify run --action "com.vmware.acoe.demo/myAction" --input=foo:bar --input baz:123

# To view the BOM of project
codify bom

# To assemble a vRO package from source content. You can specify multiple sources by
# adding a --source option for every source. The source can be a single object or a directory.
codify assemble --source src/actions --package com.vmware.acoe.mypackage --description "My package description"
```

Although Codify does not enforce a specific repository structure, it is a good practice to organize
the content in logical categories which would allow for anyone looking at the project source code
to easily find their way.

## Additional CLI options

The following options are available for the `upload` and `download` commands:
```
--sequential      - Perform sequential download or upload to avoid race conditions.
                    By default download and upload are parallel operations.
--dryrun          - Do not upload or download objects, rather just perform parsing
                    or remote resolution.
--debug           - Print debug logs. Useful when troubleshooting.
--reauthenticate  - Ignore credentials cache and perform re-authentication.
                    The access token will still be cached after the re-authentication.
```

# Content Support

| File         | vRO Objet Type                       | Parsing | Remote Resolution | Upload | Download |
| ------------ | ------------------------------------ | ------- | ----------------- | ------ | -------- |
| \*.js        | vRO Action                           | [x]     | [x]               | [x]    | [x]      |
| \*.js        | vRO Workflow (limited JS format)     | [ ]     | [ ]               | [ ]    | [ ]      |
| \*.workflow  | vRO Workflow (native format)         | [x]     | [x]               | [x]    | [x]      |
| \*.conf.yaml | vRO Config Element                   | [x]     | [x]               | [x]    | [x]      |
| \*.env       | vRO environment                      | [ ]     | [ ]               | [ ]    | [ ]      |
| \*.js        | Polyglot action (NodeJS runtime)     | [x]     | [x]               | [x]    | [x]      |
| \*.js        | ABX action (NodeJS runtime)          | [x]     | [x]               | [x]    | [x]      |
| \*.py        | Polyglot action (Python runtime)     | [x]     | [x]               | [x]    | [x]      |
| \*.py        | ABX action (Python runtime)          | [x]     | [x]               | [x]    | [x]      |
| \*.ps1       | Polyglot action (PowerShell runtime) | [x]     | [x]               | [x]    | [x]      |
| \*.ps1       | ABX action (PowerShell runtime)      | [x]     | [x]               | [x]    | [x]      |
| \*.pgt.zip   | Polyglot action bundle \*            | [ ]     | [ ]               | [ ]    | [ ]      |
| \*.abx.zip   | ABX action bundle \*                 | [ ]     | [ ]               | [ ]    | [ ]      |
| \*.vct.yaml  | VMware Cloud Template                | [x]     | [x]               | [x]    | [x]      |
| \*.sub.yaml  | vRA Subscription                     | [x]     | [x]               | [x]    | [x]      |
| \*.day2.yaml | vRA Resource Action                  | [x]     | [x]               | [x]    | [x]      |
| \*.res.yaml  | vRA Custom Resource                  | [ ]     | [ ]               | [ ]    | [ ]      |
| \*.form.yaml | vRA Custom Form                      | [ ]     | [ ]               | [ ]    | [ ]      |
| \*.package   | vRO Package                          | [ ]     | [x]               | [ ]    | [x]      |

- (\*) Polyglot and ABX action bundles are developed and produced externally
- YAML files can have either `.yaml` or `.yml` extension
- Limited JS format for workflows allows expression of uni-branch sequence of steps

## Supported JSDoc annotations

The following annotations are applicable to any vRO action type, regardless of the runtime.

- **@vro_type** - _(required)_ `polyglot` or `action`
- **@vro_id** - _(optional)_ vRO object ID, defaulting to a UUIDv5 hash from the unique identifier of the object
- **@vro_name** - _(optional)_ vRO object name, defaulting to the file name
- **@vro_module** - _(optional)_ module for the action, defaulting to `com.vmware.acoe.temp`
- **@vro_version** - _(optional)_ object version, defaulting to `1.0.0`
- **@vro_input** - _(optional)_ input parameter in the format of `{type} name [description]`
- **@vro_output** - _(optional)_ output paramter in the format of `{type} [name] [description]`
- **@vro_entrypoint** - _(optional)_ Polyglot action entrypoint, defaulting to `handler.handler`
- **@vro_timeout** - _(optional)_ Polyglot action timeout (in seconds), defaulting to `600`
- **@vro_memory** - _(optional)_ Polyglot action memory limit (in bytes), defaulting to `256000000`

The following annotations are applicable to any ABX action type, regardless of the runtime.

- **@abx_type** - _(required)_ `abx`
- **@abx_id** - _(optional)_ ABX object ID, defaulting to a UUIDv5 hash from the unique identifier of the object
- **@abx_name** - _(optional)_ ABX object name, defaulting to the file name
- **@abx_project** - _(optional)_ project for the action, defaulting to the existing action's project
- **@abx_input** - _(optional)_ input parameter in the format of `{string|constant|encryptedConstant} name [value]`
- **@abx_entrypoint** - _(optional)_ action entrypoint, defaulting to `handler.handler`
- **@abx_timeout** - _(optional)_ action timeout (in seconds), defaulting to `600`
- **@abx_memory** - _(optional)_ action memory limit (in bytes), defaulting to `256000000`
- **@abx_dependencies** - _(optional)_ action dependencies in native runtime format, defaulting to empty string
- **@abx_shared** - _(optional)_ shared action accress projects, `false` if not specified
- **@abx_provider** - _(optional)_ `on-prem`, `aws` or `azure`, defaulting to 'Auto Select' (blank value)
- **@abx_configuration** - _(optional)_ provider-specific configuration in JSON format, defaulting to `{}`

# How to contribute

Read our [Contributing](./docs/contributing.md) guideline.
