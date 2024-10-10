# Setting Up Local Environment

[//]: # (TODO: Modernize - validate or adapt content)
<!-- # (TODO: Update link in line 214 after the file is moved [Build Tools for VMware Aria - Bundle Installer](use-bundle-installer.md)) -->
[//]: # (TODO: Replace all references of the setup-workstation-maven.md to point to Setting Up Local Environment.md)

Here you will learn the needed steps on how to configure your environment

## Overview

**Build Tools for VMware Aria** are built on top of the Maven build automation ecosystem. vRealize Automation and vRealize Orchestrator content are described as Maven Project Object Models which enable huge amount of options like automated build going through phases like validate, compile, test, package, verify, install and deploy; dependency management, versioning, etc.

## Table Of Contents

1. [Installation](#installation)
2. [Configuration](#configuration)
    1. [Java Keystore](#java-keystore)
    2. [Global Configuration](#global-configuration-settingsxml)
    3. [Signing](#signing)
    4. [Bundling](#bundling)
    5. [Security](#security)
    6. [Timeouts](#timeouts)
    7. [Delays](#delays)
    8. [Checksums](#checksums)

### Installation

#### Prerequisites

- Build Tools for VMware Aria Platform
  - [Build Tools for VMware Aria Platform](Setting%20Up%20Artifactory.md) ready to use
  - Workstation can access Build Tools for VMware Aria Platform services
- Java 17
- Maven 3.5+ ([official installation guide](https://maven.apache.org/install.html))

### Configuration

There are several things that need to be in place before you can use the Build Tools for VMware Aria to work with vRO content.

#### Java Keystore

Java keystore is used for signing packages build time.

#### Create private key and certificate

The process creates an archive called **archetype.keystore-1.0.0** (artifact name + version) containing the generated files (**archetype.keystore**, **cert.pem**, **private_key.pem** ). The archive needs to be deployed on the artifact manager.

```sh
mkdir -p ~/cert/archetype.keystore-1.0.0
cd ~/cert/archetype.keystore-1.0.0

## Create the certificates and fill in the required country,state,location,organization details ...
openssl req -newkey rsa:2048 -new -x509 -days 3650 -keyout private_key.pem -out cert.pem
keytool -genkey -v -keystore archetype.keystore -alias _dunesrsa_alias_ -keyalg RSA -keysize 2048 -validity 10000

cd ~/cert
zip archetype.keystore-1.0.0.zip -r archetype.keystore-1.0.0
```
`Note:` Its very important to note that "Email" field should be EMPTY, otherwise the vRO import will break with 400 OK error

`Note:` JKS is a propriatary format specific to the particular JVM provider. When running above commands, ensure the keytool used is the one under the JVM that Maven would use (check with `mvn -v`).

#### Deploy the keystore artifact

The artifact should be deployed to any path as long as the **settings.xml** file points to it.

Example:
- artifact group ID: com.clientname.build
- artifact ID: archetype.keystore
- artifact version: 1.0.0
- **keystorePassword** and **vroKeyPass** passwords need to be replaced with the values used during the key generation process above
- settings section:
```xml
<properties>
    <keystoreGroupId>com.clientname.build</keystoreGroupId>
    <keystoreArtifactId>archetype.keystore</keystoreArtifactId>
    <keystoreLocation>target/${keystoreArtifactId}-${keystoreVersion}/archetype.keystore</keystoreLocation>
    <keystoreVersion>1.0.0</keystoreVersion>
    <keystorePassword>{{keystorePassword}}</keystorePassword>
    <vroPrivateKeyPem>target/${keystoreArtifactId}-${keystoreVersion}/private_key.pem</vroPrivateKeyPem>
    <vroCertificatePem>target/${keystoreArtifactId}-${keystoreVersion}/cert.pem</vroCertificatePem>
    <vroKeyPass>{{vroKeyPass}}</vroKeyPass>
</properties>
```

#### Global Configuration (*settings.xml*)

Firstly, you will need to configure Maven.

There are a number of properties that must be set through profiles in the settings.xml file, as they are environment specific:

- keystorePassword - Required. This is the password for the keystore used for signing vRO packages.
- keystoreLocation - Required. This is the location of the keystore. You can either hardcode a location on the machine executing the build.
- snapshotRepositoryUrl - Required. This is the url of the snapshot maven repository.
- releaseRepositoryUrl - Required. This is the url of the release maven repository. Could be the same as snapshotRepositoryUrl.

The recommended approach is to keep a settings XML file under SCM to be used by developers and a modified version with credentials for the Artifactory deployed on the CI server directly (i.e. not accessible by everyone).

Furthermore, in the example, bundling (i.e. should the bundle.zip be produced upon build) is moved to a separate profile and developers/CI can choose whether to create the bundle or not by including the "-Pbundle" command line argument to the maven invocation.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<settings xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.1.0 http://maven.apache.org/xsd/settings-1.1.0.xsd"
    xmlns="http://maven.apache.org/SETTINGS/1.1.0"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
    <servers>
        <server>
            <username>{vro_username}</username>
            <password>{native+maven+encrypted+pass}</password>
            <id>corp-dev-vro</id>
        </server>
        <server>
            <username>{vra_username}</username>
            <password>{native+maven+encrypted+pass}</password>
            <id>corp-dev-vra</id>
        </server>
    </servers>
    <profiles>
        <profile>
            <id>packaging</id>
            <properties>
        <!-- Version < 2.14
                <keystorePassword>{keystore_password}</keystorePassword>
                <keystoreLocation>{keystore_location}</keystoreLocation>
        -->

        <!-- Version >= 2.14 -->
        <keystoreGroupId>com.vmware.pscoe.build</keystoreGroupId>
        <keystoreArtifactId>archetype.keystore</keystoreArtifactId>
        <keystoreVersion>2.0.0</keystoreVersion>
        <vroPrivateKeyPem>target/${keystoreArtifactId}-${keystoreVersion}/private_key.pem</vroPrivateKeyPem>
        <vroCertificatePem>target/${keystoreArtifactId}-${keystoreVersion}/cert.pem</vroCertificatePem>
        <vroKeyPass>VMware1!</vroKeyPass>
            </properties>
        </profile>
        <profile>
            <id>bundle</id>
            <properties>
                <assembly.skipAssembly>false</assembly.skipAssembly>
            </properties>
        </profile>
        <profile>
            <id>artifactory</id>
            <repositories>
                <repository>
                    <snapshots><enabled>false</enabled></snapshots>
                    <id>central</id>
                    <name>central</name>
                    <url>http://{artifactory-hostname}/artifactory/{release_repository}</url>
                </repository>
                <repository>
                    <snapshots><enabled>true</enabled></snapshots>
                    <id>central-snapshots</id>
                    <name>central-snapshots</name>
                    <url>http://{artifactory-hostname}/artifactory/{snapshot_repository}</url>
                </repository>
            </repositories>
            <pluginRepositories>
                <pluginRepository>
                    <snapshots><enabled>false</enabled></snapshots>
                    <id>central</id>
                    <name>central</name>
                    <url>http://{artifactory-hostname}/artifactory/{release_repository}</url>
                </pluginRepository>
                <pluginRepository>
                    <snapshots><enabled>true</enabled></snapshots>
                    <id>central-snapshots</id>
                    <name>central-snapshots</name>
                    <url>http://{artifactory-hostname}/artifactory/{snapshot_repository}</url>
                </pluginRepository>
            </pluginRepositories>
            <properties>
                <releaseRepositoryUrl>http://{artifactory-hostname}/artifactory/{release_repository}</releaseRepositoryUrl>
                <snapshotRepositoryUrl>http://{artifactory-hostname}/artifactory/{snapshot_repository}</snapshotRepositoryUrl>
            </properties>
        </profile>
        <profile>
            <!--Environment identifier. Multiple environments are allowed by configuring multiple profiles -->
            <id>corp-dev</id>
            <properties>
                <!--vRO Connection-->
                <vro.host>{vro_host}</vro.host>
                <vro.port>{vro_port}</vro.port>
                <vro.username>{vro_username}</vro.username> <!--NOT RECOMMENDED USE vro.serverId and encrypted credentials-->
                <vro.password>{vro_password}</vro.password> <!--NOT RECOMMENDED USE vro.serverId and encrypted credentials-->
                <vro.serverId>corp-dev-vro</vro.serverId>
                <vro.auth>{basic|vra}</vro.auth> <!-- If "basic" is selected here, ensure com.vmware.o11n.sso.basic-authentication.enabled=true System Property is set in vRO -->
                <vro.authHost>{auth_host}</vro.authHost> <!-- Required for external vRO instances when vra auth is used -->
                <vro.authPort>{auth_port}</vro.authPort> <!-- Required for external vRO instances when vra auth is used -->
                <vro.refresh.token>{refresh_token}</vro.refresh.token> <!-- login with tokenwhen vra auth is used -->
                <vro.proxy>http://proxy.host:80</vro.proxy>
                <vro.tenant>{vro_tenant}</vro.tenant>
                <!--vRA Connection-->
                <vra.host>{vra_host}</vra.host>
                <vra.port>{vra_port}</vra.port>
                <vra.tenant>{vra_tenant}</vra.tenant>
                <vra.serverId>corp-dev-vra</vra.serverId>
                <vra.username>{vra_username}</vra.username> <!--NOT RECOMMENDED USE vra.serverId and encrypted credentials-->
                <vra.password>{vra_password}</vra.password> <!--NOT RECOMMENDED USE vra.serverId and encrypted credentials-->
            </properties>
        </profile>
    </profiles>
    <activeProfiles>
        <activeProfile>artifactory</activeProfile>
        <activeProfile>packaging</activeProfile>
    </activeProfiles>
</settings>
```

> **Note**: {vro_username} is usually taking the form of `username@domain`. For vRO8 embedded in vRA8 with BASIC for {vro_auth} it will be required that only `username` part is specified for successful authentication.

#### Signing

vRO packages are signed. In order to be able to use the toolchain, you have to have a keystore and configure it in the settings.xml file both for the developers and the CI.

##### Keystore located on the building machine

You must have the keystore file accessible on the machine and set the **keystoreLocation** and **keystorePassword** properties through the settings.xml.

#### Bundling

There is a built-in bundling capabilities that are described in a Maven profile. You can decide to not only package a vRO/vRA project, but also to create a `*-bundle.zip` with all its dependencies. This will create an archive with the following structure:

```sh
vro/ # all vRO packages. If the current project is vRO, its package will be here as well.
vra/ # all vRA packages. IF the current project is vRA, its package will be here as well.
repo/ # JARs that comprise the bundle installer - a CLI tool that is capable of importing the whole bundle to a target environment.
bin/ # shells for invoking the bundle installer CLI.
    installer # Bash executable version of the installer for Linux/Unix/macOS
    intasller.bat # Batch exectable version of the installer for Windows
```

The bundle is produced as a separate artifact during `mvn package`. To produce it, you need to add the `-Pbundle-with-installer` profile:

```bash
mvn clean deploy -Pbundle-with-installer
```

To learn more about the bundle installer, check [Build Tools for VMware Aria - Bundle Installer](use-bundle-installer.md) for more information.

#### Security

All API calls from the toolchain (i.e. the client) verify the SSL certificate returned by vRO/vRA (i.e. the server). If you are using self-signed or third-party signed certificates, you may need to add those certificates or their CA certificates to the default JAVA keystore, i.e. `JAVA_HOME/lib/security/cacerts`. **This is the recommended approach.**

The other option, **applicable ONLY for development environments**, is to ignore certificate checks by passing a flag.

#### Timeouts

Controlling timeouts is done through the following System Properties:

- the Connection Timeout ("vrealize.connection.timeout") – the time to establish the connection with the remote host (Defaults to 360 seconds (6 minutes))
- the Socket Timeout ("vrealize.socket.timeout") – the time waiting for data – after establishing the connection maximum time of inactivity between two data packets (Defaults to 360 seconds(6 minutes))
- the vra 8.x content import timeout ("vrang.import.timeout") – (in miliseconds) the time out waiting for import of custom forms / content sources to complete (Defaults to 6 seconds)

#### Delays

- the vra 8.x data collection delay ("vrang.data.collection.delay.seconds") – (in seconds) the amount of time to way before running the import job. The vRA data collection usually takes around 10 minutes ( 600 seconds ) to complete. Defaults to no delay.

  You can set these as JVM Options as specified [here](https://maven.apache.org/configure.html)

- Timeouts can be set up to be used by the installer too using the following settings (in the environment properties file):
  - http_connection_timeout - for the connection timeout (in seconds), default is 360 seconds (6 minutes)
  - http_socket_timeout - for the socket timeout (in seconds), default is 360 seconds (6 minutes)
  - vrang_import_timeout - for the vra 8.x content import timeout (custom forms, content sources).

#### Checksums

Checksums are supported for the following project types - vRO JS, TS, XML and vRA 7/8. By defaults the target folder will also contain .sha1 checksums for the different file types, e.g. js.sha1, ts.sha1, xml.sha1, content.sha1 and packages.sha1. These are specified per project type in the base pom.xml using following definition:

```xml
<properties>
    ...
    <checksum.definitions>js,packages</checksum.definitions>
    ...
</properties>
```

To add more checksum definitions for your project, just redefine this property in your pom.xml. For example, add the "docs" checksum to the build:

```xml
<properties>
    ...
    <checksum.definitions>js,packages,docs</checksum.definitions>
    ...
</properties>
```

With the "docs" prefix you can now specify if the phase is enabled, readmes location directory, files glob filter and as well list of excludes RegExp patterns of files to ommit. For example like this:

```xml
<properties>
    ...
    <checksum.definitions>js,packages,docs</checksum.definitions>

    <checksum.docsEnabled>true</checksum.docsEnabled> <!-- Required and allows for children to disable this step also -->
    <checksum.docsDir>docs/markdown</checksum.docsDir>
    <checksum.docsGlob>*.md</checksum.docsGlob>
    <checksum.docsExcludes>\.docx$,\.pdf$</checksum.docsExcludes>
    ...
</properties>
```

## Previous

See [Setting Up Artifactory](./Setting%20Up%20Artifactory.md).
