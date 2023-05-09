# Build Tools for VMware Aria - Bundle Installer

Before you continue with this section validate that all of the prerequisites are met.
## Prerequisites
- Install and Configure [Build Tools for VMware Aria System](setup-workstation-maven.md)

## Use

When you package a vRO/vRA project with the ```-Pbundle-with-installer``` Maven profile, you will get an additional artifact ```***-bundle.zip``` that contains your project, all its dependencies (both vRA and vRO) plus the bundle installer CLI.

You can extract the zip bundle and install all packages on a target environment.

## Run the Installer
Open a terminal and navigate inside the extracted bundle directory. Run the following command:
```bash
./bin/installer
```
The command above will run the ```installer``` script in interactive mode and walk you through a set of questions - credentials, flags, etc. Read the questions carefully - the defaults are set according to PS CoE's best practices.

At the end of the interaction, before anything is done, you will be prompted to store all the answers to an ```environment.properties``` file on disk. This is helpful if you want to use the same answers for different bundles or you'd like to re-run it. All passwords in the file are encoded but you should still make sure that the file is well protected.

Following is a sample listing of an ```environment.properties``` file.
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
# the workflow input parameter name and its value will be sent as value
# Supported types of workflow in/output parameters are string, number, boolean, Array/string
vro_run_workflow_input_file_path=./input.json
# output.json contains JSON where each first class KEY represents 
# the workflow output parameter name and its value is a pretty printed value as JSON 
vro_run_workflow_output_file_path=./output.json
vro_run_workflow_timeout=300

vcd_import_packages=false

vra_import_packages=false
vra_delete_old_versions=false
```

To re-use the ```environment.properties``` file, you can pass its location as the only argument to the CLI command for running the ```installer``` script:

```bash
./bin/installer ./bin/environment.properties
```
## List of Properties

Following is an exhaustive list of properties that can be used with the ```installer``` script. The properties are grouped in sections and some of these properties are available only for specific project types that are available in the bundle (such as vRA, vRO, or vROps content) or specific vRealize Suite product versions (such as vRA7 or vRA8).
```bash
######################
### CONFIGURATIONS ###
######################

### Common connectivity properties ###
http_connection_timeout
http_socket_timeout
ignore_ssl_certificate_verification
ignore_ssl_host_verification

### vRA7 connection properties ###
vra_server
vra_port
vra_tenant
vra_username
vra_password

### vRA8 and VCS connection properties ###
vrang_host
vrang_csp_host
vrang_proxy_required
vrang_proxy  
vrang_port
vrang_auth_with_refresh_token
vrang_refresh_token
vrang_username
vrang_password
vrang_org_id
# **Note**: The value of the vrang_org_name tag will take precedence over the value of the vrang_org_id tag in case both are present (either trough settings.xml or Installer) during filtering of the cloud accounts during pull action.
vrang_org_name
vrang_project.id
vrang_project_name
vrang_cloud_proxy_name
vrang_import_overwrite_mode
# vrang_vro_integration_name: The value of the <vrang.vro.integration> is used to change the integration endpoint of Workflow Content Sources and other resources that point to that type of integration. If the property is missing a default name "embedded-VRO" will be used. Additional info in ticket IAC-419
vrang_vro_integration_name
# When pushing content to the vRA, the value of vrang_import_timeout sets the timeout (in milliseconds) for the synchronization of the content source and VRA custom forms. It can be configure via both the settings.xml and installer configuration. Ticket: IAC-440
vrang_import_timeout
# When importing content to vRA, the value of vrang_data.collection.delay.seconds sets the timeout (in seconds) for the data collection of the vRO integration in vRA in order for the import of the vRA content that depends on vRO elements to succeed. Effectively, this property sets a wait time on the importing of vRA content.
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
vra_import_packages
vra_ng_import_packages
vro_import_packages
vro_enable_backup
vcd_import_packages
vrops_import_packages
vrli_import_packages
ssh_import_packages
cs_import_packages
#When importing ABX content, we unpack /target/${export}.zip in a temporary folder and unpack all the abx modules then they are iteratively imported.  IAC-512
abx_import_packages

#### vRO import configurations ####
vro_import_old_versions
skip_vro_import_old_versions
vro_import_configuration_attribute_values
vro_import_configuration_secure_attribute_values
vro_delete_old_versions
vro_delete_last_version
@Deprecated
vro_delete_include_dependencies

#### vRA import configurations ####
vra_import_overwrite_mode
# The following vRA configurations are applicable only to vRA 7
vra_import_old_versions
skip_vra_import_old_versions
vra_delete_old_versions
vra_delete_last_version
vra_delete_include_dependencies

#### vRO workflow run configuration ####
# Note that these properties are available for bundles that do not have vRO content as well.
vro_run_workflow
vro_run_workflow_id
vro_run_workflow_input_file_path
vro_run_workflow_output_file_path
vro_run_workflow_err_file_path
vro_run_workflow_timeout

#### vCD import configurations ####
vcd_delete_old_versions
vcd_import_old_versions
skip_vcd_import_old_versions
vcd_import_overwrite_mode

```

