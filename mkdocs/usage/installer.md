---
title: Installer
---

# Installer

## Overview

Installer provides the ability to package the solution and all its dependencies into an all-in-one bundle deployable to target environment via script.

## Usage

When you package a project with the `-Pbundle-with-installer` Maven profile, you get an additional artifact `***-bundle.zip` that contains your project, all its dependencies, additional files and the bundle installer CLI.

You can extract the zip bundle and install all packages on a target environment.

### Run the Installer

Open a terminal and navigate inside the extracted bundle directory. Run the following command:

=== "Linux / MacOS"

    ```bash
    ./bin/installer
    ```

=== "Windows"

    ```batch
    bin\installer.bat
    ```

The command above runs the `installer` script in interactive mode and walk you through a set of questions - credentials, flags, etc. Read the questions carefully - the defaults are set according to PS CoE's best practices.

At the end of the interaction, before anything is done, you are be prompted to store all the answers to an `environment.properties` file on disk. This is helpful if you want to use the same answers for different bundles or you'd like to re-run it. All passwords in the file are encoded but you should still make sure that the file is well protected.

Following is a sample listing of an `environment.properties` file.

```bash
# Example properties file
$ cat environment.properties
ignore_ssl_certificate_verification=true
ignore_ssl_host_verification=true

vro_import_packages=true
vro_enable_backup=true
vro_server=vra-l-01a.corp.local
vro_port=443
vro_auth=basic
vro_tenant=vsphere.local
vro_username=administrator@vsphere.local
vro_password={PASS}Vk13YXJlMSE\=    # Same as "VMware1\!"

vro_import_old_versions=true
vro_import_configuration_attribute_values=false
vro_import_configuration_secure_attribute_values=false

vro_delete_old_versions=true

# Run vRO configuration workflow
vro_run_workflow=true
vro_run_workflow_id=1944423533582937823496790834565483423
# input.json contains JSON where each first class KEY represents 
# the workflow input parameter name and its value are sent as value
# Supported types of workflow in/output parameters are string, number, boolean, Array/string
vro_run_workflow_input_file_path=./input.json
# output.json contains JSON where each first class KEY represents 
# the workflow output parameter name and its value is a pretty printed value as JSON 
vro_run_workflow_output_file_path=./output.json
vro_run_workflow_timeout=300

vcd_import_packages=false

vra_ng_import_packages=false
```

To re-use the `environment.properties` file, you can pass its location as the only argument to the CLI command for running the `installer` script:

=== "Linux / MacOS"

    ```bash
    ./bin/installer ./bin/environment.properties
    ```

=== "Windows"

    ```cmd
    bin\installer.bat bin\environment.properties
    ```

#### List of Properties

Following is an exhaustive list of properties that can be used with the `installer` script. The properties are grouped in sections and some of these properties are available only for specific project types that are available in the bundle (such as VCF Automation, Orchestrator or Operations content) or specific VCF product versions.

??? Installer Properties
    ```bash
    ######################
    ### CONFIGURATIONS ###
    ######################

    ### Common connectivity properties ###
    http_connection_timeout
    http_socket_timeout
    ignore_ssl_certificate_verification
    ignore_ssl_host_verification
    vrealize_ssh_timeout

    ### vRA8 and VCS connection properties ###
    vrang_host
    vrang_csp_host
    vrang_proxy_required
    vrang_proxy  
    vrang_port
    vrang_auth_with_refresh_token
    vrang_delete_content # can be used to delete all content from the vRA8 environment specified in the content.yaml
    vrang_refresh_token
    vrang_username
    vrang_password
    vrang_org_name
    vrang_project_name
    vrang_cloud_proxy_name
    vrang_import_overwrite_mode
    # vrang_vro_integration_name: The value of the <vrang.vro.integration> is used to change the integration endpoint of Workflow Content Sources and other resources that point to that type of integration. If the property is missing a default name "embedded-VRO" is be used. Additional info in ticket IACCOE-419
    vrang_vro_integration_name
    # When pushing content to the vRA, the value of vrang_import_timeout sets the timeout (in milliseconds) for the synchronization of the content source and VRA custom forms. It can be configure via both the settings.xml and installer configuration. Ticket: IACCOE-440
    vrang_import_timeout
    # When importing content to vRA, the value of vrang_data.collection.delay.seconds sets the timeout (in seconds) for the data collection of the vRO integration in vRA in order for the import of the vRA content that depends on vRO elements to succeed. Effectively, this property sets a wait time on the importing of vRA content. If the property is provided a data collection is forced via API and only if that data collection fails the timeout is used.
    vrang_data.collection.delay.seconds
    # Note that the import of vRA8 content also requires the operation property "vrang_import_overwrite_mode".

    ### vRLI connection properties ###
    vrli_server
    vrli_port
    vrli_provider
    vrli_username
    vrli_password
    vrli_vrops_server
    vrli_vrops_server_port
    vrli_vrops_server_user
    vrli_vrops_server_password
    vrli_vrops_server_auth_source

    ### VCD connection properties ###
    vcd_server
    vcd_port
    vcd_username
    vcd_password

    ### vRO connection properties ###
    vro_server
    vro_port
    vro_auth
    vro_username
    vro_password
    vro_refresh_token
    vro_tenant
    vro_authHost
    vro_authPort
    vro_proxy
    vro_embedded

    ### vROps connection properties ###
    vrops_httpHost
    vrops_httpPort
    vrops_restUser
    vrops_restPassword
    vrops_restAuthSource
    vrops_restAuthProvider
    vrops_sshPort
    vrops_sshUsername
    vrops_sshPassword
    vrops_dashboardUser
    vrops_importDashboardsForAllUsers

    ### SSH server connection properties ###
    ssh_server
    ssh_port
    ssh_username
    ssh_password
    ssh_directory

    ##################
    ### OPERATIONS ###
    ##################

    ### Flags whether to import certain type of packages in multi type project ###
    vra_ng_import_packages
    vro_import_packages
    vro_enable_backup
    vcd_import_packages
    vrops_import_packages
    vrli_import_packages
    ssh_import_packages
    cs_import_packages
    #When importing ABX content, we unpack /target/${export}.zip in a temporary folder and unpack all the abx modules then they are iteratively imported.  IACCOE-512
    abx_import_packages

    #### vRO import configurations ####
    vro_import_old_versions
    skip_vro_import_old_versions
    vro_import_configuration_attribute_values
    vro_import_configuration_secure_attribute_values
    vro_delete_old_versions
    vro_delete_last_version

    #### vRO workflow run configuration ####
    # Note that these properties are available for bundles that do not have vRO content as well.
    vro_run_workflow
    vro_run_workflow_id
    vro_run_workflow_input_file_path
    vro_run_workflow_output_file_path
    vro_run_workflow_err_file_path
    vro_run_workflow_timeout

    #### VCD import configurations ####
    vcd_delete_old_versions
    vcd_import_old_versions
    skip_vcd_import_old_versions
    vcd_import_overwrite_mode
    ```

### Logging configuration

Installer supports detailed logging configuration for the execution of the `installer` script.

Once a bundle is generated with `-Pbundle-with-installer` maven profile, a `logback.xml` file is generated inside the bundle's `./etc` directory which can be used for logging configurations such as severity, appenders, etc. For detailed information feel free to refer to the official documentation of the [Logback project](https://logback.qos.ch/documentation.html).

#### Example

??? Logging Configuration
    ```xml
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

Installer supports executing any existing Workflow automatically at the end of the installation process and providing to the Workflow all necessary parameters of different types.

#### Configuration

Executing a Workflow is done by providing the following configuration:

```bash
vro_run_workflow=true # defines whether or not to execute a Workflow. If value if set to `false` all the other configurations are skipped. Corresponds to "Run vRO workflow?" installer prompt.
vro_run_workflow_id=1490692845582937823496790834565483423 # Existing Workflow ID
vro_run_workflow_input_file_path=./install.json # JSON or YAML file that contains inputs for the executed Workflow. Each root property of this file should have the name of an input value of the targeted Workflow.
vro_run_workflow_output_file_path=./output.json # JSON or YAML file in which the Workflow execution outputs are stored.
vro_run_workflow_err_file_path=./workflow.err # file in which error encountered during the Workflow execution is written.
vro_run_workflow_timeout=300  # Workflow execution timeout
```

!!! note
    Both `YAML` and `JSON` are supported file formats for Workflow input and output files.

#### Supported input value types

Supported Workflow input types are:

- string
- number
- boolean
- Array/string

All other types except the above mentioned are send as parameter of type `string`.

#### Example

A common use case is to run an Installation Workflow which is meant to prepare and store configurations and objects required for consuming the solution such as REST Hosts, vCenter SDK Connection / Endpoint, credentials stored as Secured Strings in Configuration Elements, etc.

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

### Bundle Additional files

The `-Pbundle-with-installer` maven profile has the ability to package additional files or directories as part of the installer bundle. The files and directories are be copied to the root of the installation directory.

File and directory paths can be configured in the project's `pom.xml` -> `<properties>` tag by providing up to 9 `<installer.included.item*>` values. This can be used for multiple purposes such as integration tests, environment properties for the `installer` script, JSON or YAML file containing inputs for Workflow executed with `installer` script, Ansible playbooks, etc.

#### Example

```xml
    <properties>
        <installer.included.item1>environment.properties</installer.included.item1>
        <installer.included.item2>integration-tests/**</installer.included.item2>
    </properties>
```

The above example includes the `environment.properties` file and `integration-tests` directory with all files located inside of it.
