---
title: Getting Started
hide:
  - navigation
  - title
---

Welcome! This quick-start guide will walk you through validating your local environment, creating your first VMware Aria automation project, and deploying it to your server.

---

## Step 1: Check Prerequisites

Before you begin, ensure your workstation meets the following minimum requirements:

* **Java:** 17 - 24
* **Node.js:** 22
* **Maven:** 3.9+

To easily validate if your system meets these prerequisites, run the following health-check script in your terminal:

=== "Linux / MacOS"

    ```bash
    curl -o- https://raw.githubusercontent.com/vmware/build-tools-for-vmware-aria/main/health.sh | bash
    ```

=== "Windows (PowerShell)"

    ```powershell
    Set-ExecutionPolicy Bypass -Scope Process -Force; Invoke-Expression ((New-Object System.Net.WebClient).DownloadString('https://raw.githubusercontent.com/vmware/build-tools-for-vmware-aria/main/health.ps1'))
    ```

---

## Step 2: Configure Maven

You need to define your environment connection parameters in your local Maven configuration. Create or edit your `settings.xml` file located at:

* **Linux / MacOS:** `~/.m2/settings.xml`
* **Windows:** `C:\Users\<YourUsername>\.m2\settings.xml`

Copy the configuration below that matches your target environment. Be sure to replace the placeholder values under the `dev` profile with your actual hostnames, credentials, and project/organization names.

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
                    <keystoreVersion>4.6.0</keystoreVersion>
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
                    <keystoreVersion>4.6.0</keystoreVersion>
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

!!! note "VCF 9 Authentication"
    For VCF 9, you must provide your username in the `username@domain` format. The domain may be the same as the Organization name (if authenticating via an organization admin) or different (e.g., `admin@System` if authenticating as a provider admin on behalf of an organization).

!!! tip "Enterprise Repositories"
    The configurations above assume direct access to Maven Central. If your organization uses a mirror repository (like Artifactory or Nexus), you will need additional configurations. Refer to the [`settings.xml` example](https://github.com/vmware/build-tools-for-vmware-aria/blob/main/infrastructure/.m2/settings.xml) in the [Minimal Infrastructure Template](../enterprise-setup/minimal-infra.md).

---

## Step 3: Create a New Project

To scaffold your first project, run the following Maven Archetype command. This example creates a `mixed` project (containing both JavaScript actions and XML content):

```bash
mvn archetype:generate \
    -DinteractiveMode=false \
    -DarchetypeGroupId=com.vmware.pscoe.o11n.archetypes \
    -DarchetypeArtifactId=package-mixed-archetype \
    -DarchetypeVersion={{ iac.latest_release }} \
    -DgroupId={{ archetype.customer_project.group_id }} \
    -DartifactId=hello-world \
    -DworkflowsPath=hello-world/workflows
```

This generates a project structure with two submodules:
1. **`actions`**: Handles {{ products.vro_short_name }} Actions as local JavaScript files.
2. **`workflows`** (XML-based): Handles other {{ products.vro_short_name }} content like Workflows and Configuration Elements.

---

## Step 4: Deploy Content (Push)

To deploy your newly created project to your target environment, navigate to the root of your project directory and execute:

```bash
mvn clean package vrealize:push -Pdev
```

---

## Step 5: Export Content (Pull)

When you make changes directly in the {{ products.vro_short_name }} UI, you can pull those changes back into your local project. 

### Pulling Actions
To export Actions as JavaScript files:

1. Navigate to **Orchestrator UI** -> **Assets** -> **Packages**.
2. Open the `com.bu.hello-world.actions-1.0.0-SNAPSHOT` package.
3. Add your modified Action(s) to the package and click **Save**.
4. Run the following command locally:
    ```bash
    mvn vro:pull -Pdev -pl actions 
    ```

### Pulling Other Content (Workflows, Configs, etc.)
To export Workflows, Configuration Elements, Resource Elements, and Policy Templates as XML files:

1. Navigate to **Orchestrator UI** -> **Assets** -> **Packages**.
2. Open the `com.bu.hello-world-1.0.0-SNAPSHOT` package.
3. Add the content to the package and click **Save**.
4. Run the following command locally:
    ```bash
    mvn vro:pull -Pdev -pl workflows
    ```
    *(**Note:** Append `-Dvro.packageExportConfigurationAttributeValues=true` to the command if you also want to pull Configuration Element attribute values).*

---

## Step 6: Create an Installation Bundle

You can package your project, its dependencies, and a deployment script into a single distributable bundle. 

From the project root, run:

```bash
mvn clean package -Pbundle-with-installer
```

This generates a `.zip` file under `/workflows/target/com.bu.hello-world-1.0.0-SNAPSHOT-local-bundle.zip`.

### Deploying from the Bundle

1. Extract the `.zip` file.
2. Navigate into the extracted `com.bu.hello-world-1.0.0-SNAPSHOT-local-bundle` folder.
3. Execute the installer script (you will be prompted to provide required data):

    === "Linux / MacOS"

        ```bash
        ./bin/installer
        ```

    === "Windows"

        ```cmd
        bin\installer.bat
        ```

#### Unattended Installation
To bypass the manual prompts, you can create an `environment.properties` file inside the `/bin` folder:

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

Then, pass the file to the script:

=== "Linux / MacOS"

    ```bash
    ./bin/installer ./bin/environment.properties
    ```

=== "Windows"

    ```cmd
    bin\installer.bat bin\environment.properties
    ```

For more advanced options, refer to the [Installer Documentation](../usage/installer.md).
