---
title: Installer
---

## Overview

The Installer module allows you to package a project and all its dependencies into an all-in-one bundle that you can deploy to a target environment via a script that is included in the bundle.

When you bundle a project with the `-Pbundle-with-installer` Maven profile, you get an additional `***-bundle.zip` artifact that contains your project, all its dependencies, the bundle Installer CLI tool (script), and additional Java files that are required for the operation of the Installer CLI tool.

You can extract the ZIP bundle and run the Installer CLI tool in the `bin/` directory to import all project content packages into a target environment. Additionally, at the end of the process for importing project content, you can run an Orchestrator workflow that stores or performs additional configurations related to your projects.

## Process Flow

You can run the Installer CLI tool in one of two modes, interactive and unattended.

- In interactive mode, the tool shows prompts to collect the necessary properties for importing the project content (based on the bundle content).

    !!! Tip
        If you start the Installer tool in interactive mode, read carefully all prompts and specify the correct values for your environment. The default values in the prompts are set according to PSCoE best practices.

- In unattended mode, you provide a Java properties file (with the `.properties` extension) that contains all necessary properties for importing the content.

    !!! Tip
        To retrieve the list of required properties for a specific bundle, you can run the Installer tool in interactive mode first and store the collected properties to a file. Alternatively, you can prepare your own `.properties` file by using the sample listings of properties based on project type and operation under [List of Properties](#list-of-properties) section.

Based on the types of project content of the extracted bundle and the property values that you pass to it, the Installer CLI tool performs *all applicable* operations from the following list (in the specified order).

<!-- !!! Info

    For details about the required properties for each of the operations, see the corresponding sections under [List of Properties per Project Type and Operation](#list-of-properties-per-project-type-and-operation). -->

1. Read common properties.
2. Import the Orchestrator project content.
3. Import the {{ products.vra_8_full_name }} Code Stream/Pipelines project content.
4. Import the {{ products.vra_9_full_name }} ABX project content.
5. Import the {{ products.vra_9_full_name }} project content for a VM Apps organization.
6. Import the {{ products.vra_9_full_name }} project content for an All Apps organization.
7. Import the VMware Cloud Director project content.
8. Clean up the bundle versions of {{ products.vro_9_full_name }} project content packages.
9. Clean up the old versions of {{ products.vro_9_full_name }} project content packages (if applicable).
10. Clean up the {{ products.vra_9_full_name }} project content for a VM Apps organization from the bundle.
11. Clean up the {{ products.vra_9_full_name }} project content for an All Apps organization from the bundle.
12. Import the {{ products.vrops_9_full_name }} project content.
13. Import the {{ products.vrli_9_full_name }} project content.
14. Import the SSH (basic) project content.
15. Run an Orchestrator workflow.

The following diagram shows an overview of the process flow of the Installer CLI tool and the operations it performs.

```kroki-plantuml {display-width=100%}
@from_file:usage/installer/diagrams/installer-import-flow-overview.puml
```

## Usage

This section provides details about using the Installer CLI tool.

### Run the Installer

To run the Installer CLI tool, you first need extract the contents of the ZIP bundle that you created with the `-Pbundle-with-installer` Maven profile and open a terminal inside the extracted bundle directory. Then use one of the following options to start the Installer in the respective mode.

- To start the Installer in interactive mode, run the following command, read carefully all prompts, and specify the correct values for your environment.

    At the end of the interaction, before it starts any of the content management operations, the Installer will prompt you to store all your answers to an `environment.properties` file on the local filesystem disk. You can then use this file with the same answers for a different run of the Installer, for a different bundle, or for a different environment. Note that the Installer encodes the values of all password properties in the file but you should still make sure that the file is well protected.

    === "Linux / MacOS"

        ```bash
        ./bin/installer
        ```

    === "Windows (CMD)"

        ```cmd
        bin\installer.bat
        ```

- To start the Installer in unattended mode, add a Properties file with all applicable properties inside the extracted bundle directory (for example, in the `bin/` directory with the script), and run the following command in which you specify the path to the `.properties` file as the only argument of the command.

    !!! Tip
        The following command uses a file with the default name `environment.properties` in the `bin/` directory of the extracted bundle.

    === "Linux / MacOS"

        ```bash
        ./bin/installer ./bin/environment.properties
        ```

    === "Windows (CMD)"

        ```cmd
        bin\installer.bat bin\environment.properties
        ```

Following is a sample listing of an `environment.properties` file for a bundle that contains content for two project types, {{ products.vro_9_full_name }} and {{ products.vra_9_full_name }} for a VM Apps organization, but imports only the Orchestrator content (to a standalone Orchestrator instance) and then run the Orchestrator workflow with the ID `1944423533582937823496790834565483423`.

```properties
# Example environment.properties file
ignore_ssl_certificate_verification=true
ignore_ssl_host_verification=true
http_connection_timeout=360
http_socket_timeout=360
vrealize_ssh_timeout=300

vro_import_packages=true                                # Starts the import operation for the Orchestrator project content
vro_embedded=false
vro_enable_backup=true                                  # Specifies that this a standalone instance
vro_server=flt-orchestrator01.corp.internal
vro_port=443
vro_auth=basic
vro_tenant=vsphere.local
vro_username=administrator@vsphere.local
vro_password={PASS}Vk13YXJlMSE\=                        # Same as "VMware1\!"

vro_import_old_versions=false
skip_vro_import_old_versions=true                       # Set to the opposite value of "vro_import_old_versions"
vro_import_configuration_attribute_values=false
vro_import_configuration_secure_attribute_values=false
vro_force_import_latest_versions=false

vro_delete_old_versions=true                            # Starts the clean up operation for old versions of project packages

# Run vRO configuration workflow
vro_run_workflow=true                                   # Starts the Run Workflow operations
vro_run_workflow_id=1944423533582937823496790834565483423
# input.json contains JSON where each first class KEY represents
# the workflow input parameter name and its value are sent as value
# Supported types of workflow in/output parameters are string, number, boolean, Array/string
vro_run_workflow_input_file_path=./input.json
# output.json contains JSON where each first class KEY represents
# the workflow output parameter name and its value is a pretty printed value as JSON
vro_run_workflow_output_file_path=./output.json
vro_run_workflow_timeout=300

vra_ng_import_packages=false                            # Skips the import operations for the {{ products.vra_9_full_name }} for a VM Apps organization content
```

#### List of Properties

Following is an exhaustive list of all properties that you can use with the Installer tool. The properties are grouped in sections per project type (that is available in the bundle). Within each sections, the properties are grouped in subsections based on their purpose and the operation they are used for.

Note that the script uses the properties from the **Common properties** section for all operations.

```sh title="List of all Installer properties (grouped by project type)"
{% include "./content-blocks/all-props-list.properties" %}
```

### Logging configuration

The Installer tool supports detailed logging configuration for the execution of the script based on the [Logback project](https://logback.qos.ch).

When you generate a bundle with the `-Pbundle-with-installer` Maven profile, the Installer generates a `logback.xml` file inside the `etc/` directory of the bundle and you can use this file to define the logging configurations for the script, such as severity, appenders, etc. For detailed information about the available configurations, see the [official Logback documentation](https://logback.qos.ch/documentation.html).

#### Example

Following is a sample listing of the `logback.xml` file with the logging configuration.

```xml title="etc/logback.xml (Logging Configuration)"
<configuration>
    <!-- Console appender for INFO logs -->
    <appender name="INFO_CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>INFO</level>
            <onMatch>ACCEPT</onMatch>
            <onMismatch>DENY</onMismatch>
        </filter>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <!-- Console appender for WARN logs -->
    <appender name="WARN_CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>WARN</level>
            <onMatch>ACCEPT</onMatch>
            <onMismatch>DENY</onMismatch>
        </filter>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <!-- Console appender for DEBUG logs -->
    <appender name="DEBUG_CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>DEBUG</level>
            <onMatch>ACCEPT</onMatch>
            <onMismatch>DENY</onMismatch>
        </filter>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <!-- Root logger with level INFO -->
    <root level="INFO">
        <appender-ref ref="INFO_CONSOLE" />
        <appender-ref ref="WARN_CONSOLE" />
        <appender-ref ref="DEBUG_CONSOLE" />
    </root>

    <!-- Uncomment the below tag if you need to enable DEBUG logging as well -->
    <!--
    <root level="DEBUG">
        <appender-ref ref="INFO_CONSOLE" />
        <appender-ref ref="WARN_CONSOLE" />
        <appender-ref ref="DEBUG_CONSOLE" />
    </root>
    -->
</configuration>
```

### Run Workflow

You can use the Installer to run any existing Orchestrator workflow automatically at the end of the import (and clean up) process. You can provide the necessary values for the input parameters of the Workflow in a file in JSON or YAML format. Additionally, the script expects you to specify the paths to two more files, one for writing the standard output of the workflow (again in JSON or YAML format) and one for writing the error output (if applicable).

A common use case for this operation is the running of an installation workflow that prepares and stores the configurations and objects required for using the solution from your bundle projects, such as managing HTTP-REST Hosts and vCenter SDK Connections in the Orchestrator inventory, storing credentials as Secured Strings and other runtime configurations in Orchestrator configuration elements, etc.

Note that the operation for running an Orchestrator workflow is available for all project bundles, even the ones that do not contain any of the Orchestrator project types.

#### Supported Input Value Types for Input Parameters

You can use the following value types for the input parameters of an Orchestrator workflow. The Installer sends all other types as parameters of type `string`.

- number
- boolean
- string
- Array/string

##### Sample File with Workflow Input Parameter Values

Following is a sample listing (in both YAML and JSON format) of a file with inputs for a workflow with four input parameters.

??? "Expand for details of the workflow input parameters and how they are processed"

    Following is a list of the input parameters of the workflow with details of how they are defined in the Orchestrator workflow, what is their data types in the sample input file, and how the Installer passes them to the Orchestrator workflow.

    | Parameter Name | Workflow Definition Data Type | Input File Data Type | Actual Input Data Type |
    | --- | --- | --- | --- | 
    | jsonString | string | (JSON) object | string |
    | tags | Array/string | Array/string | Array/string |
    | blacklist | Array/string | Array/string | Array/string |
    | environment | string | `null` | `null` |

=== "YAML"
    ```YAML
    jsonString:
      scheduled-snapshot:
        evaluationTime: "18:00:00"
        retrainedSnapshotCount: 2
        manageSnapshotTimeoutMinutes: 120
      vsphere:
        authentication:
          - hostname: "vc-l-01a.corp.internal"
            port: 443
            domain: "vsphere.local"
            username: "administrator"
            password: "VMware1!VMware1!"
            apiCompatibilityVersion: 702
            createSdkConnection: true
            ignoreCertificateWarnings: true
            sessionPerUser: false
      vra:
        authentication:
          hostname: vcfa.corp.internal
          port: 443
          authHostname: vcfa.corp.internal
          authPort: 443
          username: admin
          password: VMware1!VMware1!
          domain: vm-apps
          projectName: Development
          orgName: vm-apps
          isPersistent: false
    tags: ["bak.scheduled-snapshot", "vsphere.authentication", "vra.authentication"]
    blacklist: []
    environment: null
    ```
=== "JSON"
    ```json
    {
      "jsonString": {
        "scheduled-snapshot": {
          "evaluationTime": "18:00:00",
          "retrainedSnapshotCount": 2,
          "manageSnapshotTimeoutMinutes": 120
        },
        "vsphere": {
          "authentication": [
            {
              "hostname": "vc-l-01a.corp.internal",
              "port": 443,
              "domain": "vsphere.local",
              "username": "administrator",
              "password": "VMware1!VMware1!",
              "apiCompatibilityVersion": 702,
              "createSdkConnection": true,
              "ignoreCertificateWarnings": true,
              "sessionPerUser": false
            }
          ]
        },
        "vra": {
          "authentication": {
            "hostname": "vcfa.corp.internal",
            "port": 443,
            "authHostname": "vcfa.corp.internal",
            "authPort": 443,
            "username": "admin",
            "password": "VMware1!VMware1!",
            "domain": "vm-apps",
            "projectName": "Development",
            "orgName": "vm-apps",
            "isPersistent": false
          }
        }
      },
      "tags": [
        "bak.scheduled-snapshot",
        "vsphere.authentication",
        "vra.authentication"
      ],
      "blacklist": [],
      "environment": null
    }
    ```

#### Configuration Properties for Running a Workflow

When you want to run an Orchestrator with the Installer tool, you need to provide the following properties together with the properties that specify the connection configurations for the Orchestrator host (either embedded or standalone/external).

!!! note
    Both `YAML` and `JSON` are supported file formats for Workflow input and output files.

```properties
# Flag for starting the operation for running a workflow. If the value is set to `false`, the script skips all the other properties.
vro_run_workflow=true

# UUID of the existing workflow in the Orchestrator instance
vro_run_workflow_id=1490692845582937823496790834565483423
# Path to the JSON or YAML file that contains the inputs for the Workflow. Each root attribute of this file
# must have the name of an input parameter of the target workflow
vro_run_workflow_input_file_path=./install.json
# Path to the JSON or YAML file in which the script stores the workflow execution outputs
vro_run_workflow_output_file_path=./output.json 
# Path to the file in which the script writes any errors encountered during the workflow execution
vro_run_workflow_err_file_path=./workflow.err
# workflow execution timeout
vro_run_workflow_timeout=300
```
