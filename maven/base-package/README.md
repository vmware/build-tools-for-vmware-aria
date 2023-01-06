# base-package
This module contains 3 base POMs to be used as parents for package projects.

Note that there are a number of properties that must be set through profiles in the settings.xml file, as they are environment specific:
* keystorePassword - Required. This is the password for the datastore used for signing vRO packages.
* keystoreLocation - Required. This is the location of the datastore. You can either hardcode a location on the machine executing the build. 
    The other option is to package the development keystore as an artifact and distribute it through artifactory.
* keystoreGroupId - Optional. This is the groupId of the keystore artifact, if you choose to use this mechanism for distributing the keystore.
* keystoreArtifactId - Optional. See above.
* keystoreVersion - Optional. See above.
* installerGroupId - Required. GroupId of the artifact that will be used to seed the bundle.zip. It might contain installer scripts and tools for the bundle.
* installerArtifactId - Required. See above.
* installerVersion - Required. See above.
* snapshotRepositoryUrl - Required. This is the url of the snapshot maven repository.
* releaseRepositoryUrl - Required. This is the url of the release maven repository. Could be the same as snapshotRepositoryUrl.
* vro.javascript.globals - Optional. This will be used when submitting JS-based projects to Sonar for static code analysis. It should contain a list of global variables, i.e. vRO plugin objects or script context objects (e.g. VcVirtualMachine, System, workflow).

It makes sense to keep this settings XML file under SCM to be used by developers. You might have a modified version with credentials for the artifactory deployed on the CI server.

An example settings.xml file is shown below:
```
<?xml version="1.0" encoding="UTF-8"?>
<settings xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.1.0 http://maven.apache.org/xsd/settings-1.1.0.xsd" 
    xmlns="http://maven.apache.org/SETTINGS/1.1.0" 
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
    <profiles>
        <profile>
            <id>packaging</id>
            <properties>
                <keystoreGroupId>com.vmware.pscoe.build</keystoreGroupId>
                <keystoreArtifactId>archetype.keystore</keystoreArtifactId>
                <keystoreVersion>1.0.0</keystoreVersion>
                <keystorePassword>***</keystorePassword>
                <keystoreLocation>target/${keystoreArtifactId}-${keystoreVersion}/archetype.keystore</keystoreLocation>
                <!-- Or you can use a local file -->
                <!-- <keystoreLocation>/Users/example/o11n/developer.keystore</keystoreLocation> -->
                <assembly.skipAssembly>true</assembly.skipAssembly>
            </properties>
        </profile>
        <profile>
            <id>sonar</id>
            <properties>
                <vro.javascript.globals>System,Server...</vro.javascript.globals>
            </properties>
        </profile>
        <profile>
            <id>bundle</id>
            <properties>
                <assembly.skipAssembly>false</assembly.skipAssembly>
            </properties>
        </profile>
        <profile>
            <repositories>
                <repository>
                    <!-- <snapshots><enabled>false</enabled></snapshots> -->
                    <id>central</id>
                    <name>pscoe</name>
                    <url>${releaseRepositoryUrl}</url>
                </repository>
            </repositories>
            <pluginRepositories>
                <pluginRepository>
                    <!-- <snapshots><enabled>false</enabled></snapshots> -->
                    <id>central</id>
                    <name>pscoe</name>
                    <url>${releaseRepositoryUrl}</url>
                </pluginRepository>
            </pluginRepositories>
            <properties>
                <snapshotRepositoryUrl>http://artifactory-fqdn</snapshotRepositoryUrl>
                <releaseRepositoryUrl>http://artifactory-fqdn</releaseRepositoryUrl>
            </properties>
            <id>artifactory</id>
        </profile>
    </profiles>
    <activeProfiles>
        <activeProfile>artifactory</activeProfile>
        <activeProfile>packaging</activeProfile>
    </activeProfiles>
</settings>
```

## Signing
The base-package contains mechanisms for managing package signing. There are two ways to do that, and you must choose one of them:
### 1. Keystore located on the building machine
This approach is the recommendation for the CI server. You must have the keystore file accessible on the machine and set the keystoreLocation and keystorePassword properties through the settings.xml.
### 2. Keystore artifact
This approach involves less overhead for individual developers, as the keystore is resolved and distributed as a maven dependency. You must set the following properties through the settings.xml:
* keystorePassword
* keystoreLocation - Should look something like this: **target/${keystoreArtifactId}-${keystoreVersion}/archetype.keystore**, i.e. relative path in the target of the current project, but it depends on how you've bundled the keystore artifact.
* keystoreGroupId
* keystoreArtifactId
* keystoreVersion

## Bundling
If you want to create a bundle.zip containing the package and all its dependencies, you need the following:
* add **-Pbundle** as a maven CLI argument. This will activate the profile and produce the bundle. For example:
```
$ mvn clean deploy -Pbundle
```

## Bundling with installer
You can supply additional components to the bundle.zip like an installer executable and scripts. The toolchain comes with a prepackaged installer 
that you can be included in the bundle and used as a CLI to import the bundled artifacts in vRO and vRA.  

Furthermore, you can extend the default by providing your own zip artifact containing all the supplementary files for the bundle, e.g. scripts, executables etc.

In order to include an installer to your bundle, you need the following:
* (Optional) Package all your supplementary components in a zip and upload them to your maven repository.
* (Optional) Set installerGroupId, installerArtifactId and installerVersion through a profile in settings.xml
* add **-Pbundle-with-installer** as a maven CLI argument. This will activate the profile and produce the bundle with the provided installer. For example:
```
$ mvn clean deploy -Pbundle-with-installer
```
This will unzip the installer zip, add **vro/** and **vra/** folders to the root and that zip it again.

