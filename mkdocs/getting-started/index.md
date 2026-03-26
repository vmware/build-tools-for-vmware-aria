---
title: Getting Started
---

# Getting Started

A short guide to help you quickly setup your first project and deploy it to your environment.

## Prerequisites

* Java 17 - 24
* Node 22
* Maven 3.9

To validate if prerequisites are met you can run the following command:

=== "Linux / MacOS"

    ```bash
    curl -o- https://raw.githubusercontent.com/vmware/build-tools-for-vmware-aria/main/health.sh | bash
    ```

=== "Windows"

    ```ps1
    Set-ExecutionPolicy Bypass -Scope Process -Force; Invoke-Expression ((New-Object System.Net.WebClient).DownloadString('https://raw.githubusercontent.com/vmware/build-tools-for-vmware-aria/main/health.ps1'))
    ```

## Maven Configuration

Add the following `settings.xml` file to your local file system and replace the values under `dev` profile with your hostnames, credentials, project and organization name based on your target environment:

=== "Aria Suite 8"

    ```xml
      <?xml version="1.0" encoding="UTF-8"?>
      <settings xmlns="http://maven.apache.org/SETTINGS/1.1.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.1.0 http://maven.apache.org/xsd/settings-1.1.0.xsd">
        
          <profiles>
              <profile>
                  <id>packaging</id>
                  <properties>
                      <keystoreGroupId>com.vmware.pscoe.build</keystoreGroupId>
                      <keystoreArtifactId>keystore.example</keystoreArtifactId>
                      <keystoreVersion>4.19.0</keystoreVersion>
                      <vroPrivateKeyPem>target/${keystoreArtifactId}-${keystoreVersion}/private_key.pem</vroPrivateKeyPem>
                      <vroCertificatePem>target/${keystoreArtifactId}-${keystoreVersion}/cert.pem</vroCertificatePem>
                      <vroKeyPass>VMware1!</vroKeyPass>
                  </properties>
              </profile>
              <profile>
                  <id>dev</id>
                  <properties>
                    <vro.host>flt-auto01.corp.internal</vro.host>
                    <vro.auth>vra</vro.auth>
                    <vro.authHost>flt-auto01.corp.internal</vro.authHost>
                    <vro.authPort>443</vro.authPort>
                    <vro.port>443</vro.port>
                    <vro.username>configurationadmin</vro.username>
                    <vro.password>VMware1!VMware1!</vro.password>   

                    <vrang.host>flt-auto01.corp.internal</vrang.host>
                    <vrang.auth.host>flt-auto01.corp.internal</vrang.auth.host>
                    <vrang.username>configurationadmin</vrang.username>
                    <vrang.password>VMware1!VMware1!</vrang.password>
                    <vrang.port>443</vrang.port>
                    <vrang.project.name>Development</vrang.project.name>
                    <vrang.org.name>vidm-l-01a</vrang.org.name>
                    <vrang.import.timeout>6</vrang.import.timeout>
                    
                    <vrealize.ssl.ignore.hostname>true</vrealize.ssl.ignore.hostname>
                    <vrealize.ssl.ignore.certificate>true</vrealize.ssl.ignore.certificate>
                    <http.connection.timeout>10000000</http.connection.timeout>
                  </properties>
              </profile>
          </profiles>
          <activeProfiles>
              <activeProfile>packaging</activeProfile>
          </activeProfiles>
      </settings>
    ```

=== "VCF 9"

    ```xml
      <?xml version="1.0" encoding="UTF-8"?>
      <settings xmlns="http://maven.apache.org/SETTINGS/1.1.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.1.0 http://maven.apache.org/xsd/settings-1.1.0.xsd">
        
          <profiles>
              <profile>
                  <id>packaging</id>
                  <properties>
                      <keystoreGroupId>com.vmware.pscoe.build</keystoreGroupId>
                      <keystoreArtifactId>keystore.example</keystoreArtifactId>
                      <keystoreVersion>4.19.0</keystoreVersion>
                      <vroPrivateKeyPem>target/${keystoreArtifactId}-${keystoreVersion}/private_key.pem</vroPrivateKeyPem>
                      <vroCertificatePem>target/${keystoreArtifactId}-${keystoreVersion}/cert.pem</vroCertificatePem>
                      <vroKeyPass>VMware1!</vroKeyPass>
                  </properties>
              </profile>
              <profile>
                  <id>dev</id>
                  <properties>
                    <vro.host>flt-auto01.corp.internal</vro.host>
                    <vro.auth>vra</vro.auth>
                    <vro.authHost>flt-auto01.corp.internal</vro.authHost>
                    <vro.authPort>443</vro.authPort>
                    <vro.port>443</vro.port>
                    <vro.username>admin@System</vro.username>
                    <vro.password>VMware1!VMware1!</vro.password>   

                    <vrang.host>flt-auto01.corp.internal</vrang.host>
                    <vrang.auth.host>flt-auto01.corp.internal</vrang.auth.host>
                    <vrang.username>admin@System</vrang.username>
                    <vrang.password>VMware1!VMware1!</vrang.password>
                    <vrang.port>443</vrang.port>
                    <vrang.project.name>Development</vrang.project.name>
                    <vrang.org.name>vm-apps</vrang.org.name>
                    <vrang.import.timeout>6</vrang.import.timeout>
                    
                    <vrealize.ssl.ignore.hostname>true</vrealize.ssl.ignore.hostname>
                    <vrealize.ssl.ignore.certificate>true</vrealize.ssl.ignore.certificate>
                    <http.connection.timeout>10000000</http.connection.timeout>
                  </properties>
              </profile>
          </profiles>
          <activeProfiles>
              <activeProfile>packaging</activeProfile>
          </activeProfiles>
      </settings>
    ```

!!! note
    For VCF 9 authentication it is required that you provide username in the following format: `username@domain`. The user domain may be the same as the Organization name (if you are authenticating via an organization admin) or it might be different (if you are authenticating as provider admin on behalf of an organization, e.g. `admin@System`).

!!! note
    The `settings.xml` file configuration above assumes you have direct access to Maven central. Access via a mirror repository requires additional configurations. For sample configuration refer to the [`settings.xml`](https://github.com/vmware/build-tools-for-vmware-aria/blob/main/infrastructure/.m2/settings.xml) in the [minimal infrastructure template](../enterprise-setup/minimal-infra.md).

The file must be added to:

=== "Linux / MacOS"

    ```bash
    ~/.m2/settings.xml
    ```

=== "Windows"

    ```text
    C:\Users\<YourUsername>\.m2\settings.xml
    ```

## Create new project

Too create your first project execute the following command:

```Bash
mvn archetype:generate \
    -DinteractiveMode=false \
    -DarchetypeGroupId=com.vmware.pscoe.o11n.archetypes \
    -DarchetypeArtifactId=package-mixed-archetype \
    -DarchetypeVersion={{ iac.latest_release }} \
    -DgroupId={{ archetype.customer_project.group_id}} \
    -DartifactId=hello-world \
    -DworkflowsPath=hello-world/workflows
```

This creates a project with two submodules - one [`Action`](../usage/products/vro/actions/index.md) based that can handle Actions as Javascript files and one [`XML`](../usage/products/vro/xml/index.md) based that can handle other {{ products.vro_short_name }} content as XML files.

## Deploy Content

To deploy the project to your environment execute the following command from the root of your project:

```bash
mvn clean package vrealize:push -Pdev
```

## Export content from {{ products.vro_short_name }}

### Actions

To export `Actions` from {{ products.vro_short_name }} to your local file system as Javascript files execute the following steps:

1. Navigate to Orchestrator UI -> `Assets` -> `Packages`.
2. Open `com.bu.hello-world.actions-1.0.0-SNAPSHOT` package.
3. Add any `Action` to the package and click `Save`.
4. Execute the following command:

```bash
mvn vro:pull -Pdev -pl actions 
```

### Other Content

To export `Workflows`, `Configuration Elements`, `Resource Elements` and `Policy Templates` from {{ products.vro_short_name }} to your local file system as XML files execute the following steps:

1. Navigate to Orchestrator UI -> `Assets` -> `Packages`.
2. Open `com.bu.hello-world-1.0.0-SNAPSHOT` package.
3. Add content to the package and click `Save`.
4. Execute the following command:

```bash
mvn vro:pull -Pdev -pl workflows
```

!!! note
    If you want to export Configuration Element attribute values append `-Dvro.packageExportConfigurationAttributeValues=true` to the command above.

## Installation Bundle

### Create Installation Bundle
To create a bundle that contains all project artifacts, their dependencies and a script for deploying them to target environment execute the following command from the project root:

```bash
mvn clean package -Pbundle-with-installer
```

The combined bundle (containing both submodules - `actions` and `xml`) is created under project root `/workflows/target/com.bu.hello-world-1.0.0-SNAPSHOT-local-bundle.zip`.

### Deploy from Installation Bundle

To deploy from the installation bundle extract its contents and from the `com.bu.hello-world-1.0.0-SNAPSHOT-local-bundle` folder execute the following command and provide the required data when prompted:

=== "Linux / MacOS"

    ```bash
    ./bin/installer
    ```

=== "Windows"

    ```batch
    bin\installer.bat
    ```

As an alternative to manually providing installer inputs you can create the following `environment.properties` file under `com.bu.hello-world-1.0.0-SNAPSHOT-local-bundle/bin`:

=== "Aria Suite 8"
    ```conf
    http_connection_timeout=360
    http_socket_timeout=360
    ignore_ssl_certificate_verification=true
    ignore_ssl_host_verification=true
    skip_vro_import_old_versions=true
    vrang_auth_with_refresh_token=false
    vrang_host=flt-auto01.corp.internal
    vrang_password=VMware1!VMware1!
    vrang_port=443
    vrang_username=configurationadmin
    vrealize_ssh_timeout=300
    vro_delete_old_versions=false
    vro_embedded=true
    vro_enable_backup=false
    vro_force_import_latest_versions=false
    vro_import_configuration_attribute_values=false
    vro_import_configuration_secure_attribute_values=false
    vro_import_old_versions=false
    vro_import_packages=true
    vro_run_workflow=false
    ```

=== "VCF 9"
    ```conf
    http_connection_timeout=360
    http_socket_timeout=360
    ignore_ssl_certificate_verification=true
    ignore_ssl_host_verification=true
    skip_vro_import_old_versions=true
    vrang_auth_with_refresh_token=false
    vrang_host=flt-auto01.corp.internal
    vrang_password=VMware1!VMware1!
    vrang_port=443
    vrang_username=admin@System
    vrealize_ssh_timeout=300
    vro_delete_old_versions=false
    vro_embedded=true
    vro_enable_backup=false
    vro_force_import_latest_versions=false
    vro_import_configuration_attribute_values=false
    vro_import_configuration_secure_attribute_values=false
    vro_import_old_versions=false
    vro_import_packages=true
    vro_run_workflow=false
    ```

After that you can feed the properties to the installation script by executing:

=== "Linux / MacOS"

    ```bash
    ./bin/installer ./bin/environment.properties
    ```

=== "Windows"

    ```cmd
    bin\installer.bat bin\environment.properties
    ```

!!! note
    For more information please refer to the [Installer documentation](../usage/installer.md).
