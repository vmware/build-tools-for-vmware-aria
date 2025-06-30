# Setting Up Artifactory

[//]: # (TODO: Modernize - validate or adapt content)
[//]: # (TODO: Replace all references of the setup-platform.md to point to Setting Up Artifactory.md)

Here you will learn the needed steps on how to configure the artifactory

## Overview

## Table Of Contents

1. [Configuring Artifactory](#configuring-artifactory)
2. [Configure permissions for local cache for the Anonymous user](#configure-permissions-for-local-cache-for-the-anonymous-user)
3. [Uploading toolchain artifacts to Artifactory](#uploading-toolchain-artifacts-to-artifactory)
4. [Upload vRO artifacts to Artifactory (if needed)](#upload-vro-artifacts-to-artifactory-if-needed)

### Configuring Artifactory

Note that the libs-release, libs-snapshot etc. are the default Maven repositories created by JFrog's **Quick Setup** shown at first login. This guide assumes that this **Quick Setup** has been executed.

1. Create a local repository in artifactory to contain the toolchain artifacts, e.g. **vrealize-build-tools** and add it to the virtual release repository (e.g. **libs-release**)
2. Create a remote repository and configure the maven central public repository ([https://repo1.maven.org/maven2/](https://repo1.maven.org/maven2/)). Add it to the virtual release repository (e.g. **libs-release**). This is needed so any public dependencies can be downloaded as well.

### Configure permissions for local cache for the Anonymous user

1. Login into Artifactory with admin privileges
2. Navigate to **Admin > Permissions**
3. Click the **New** button
4. Add a name for the permissions (for example: Anonymous Cache)
5. Add all repositories into the **Selected Repositories** list view
6. Skip the groups section
7. On the **Users** section add **Anonymous** user
8. Give **Deploy/Cache, Annotate, Read** permissions
9. Click **Save & Finish**

### Uploading toolchain artifacts to Artifactory

#### Via Cli

1. Unzip **iac-maven-repository.zip** found at **artifacts/maven/** path relative to the root of the toolchain bundle to a folder, e.g. **import/**
2. Go to the directory where you have unzipped the archive. Your working directory should contain the "com" folder and the **archetype-catalog.xml** file, e.g.:

   ```bash
   root@photon-G6H8GzV2j [ ~/toolchain/import ]# ls
   archetype-catalog.xml  com
   ```

3. Run `jfrog config add` to configure your local environment with the artifactory server.
4. Then, run the following command

   ```jfrog rt u --recursive=true --flat=false ./* vrealize-build-tools```

   where **vrealize-build-tools** should be the name of the repository you've created at step #1 in [Configuring Artifactory](#configuring-artifactory).

5. Examine the output of the command. It should look something similar to this:

   ```bash
   Uploading artifact: /path/to/artifact/some-artifact.jar
   {
      "status": "success",
      "totals": {
         "success": 1,
         "failure": 0
      }
   }
   ```

  **Note:** There is a chance that not all of the artifacts will be uploaded (you will be getting a lot of errors). If this happens, you can re-run the command.

#### Via UI

> **FINISH**

### Upload vRO artifacts to Artifactory (if needed)

First you need access to a supported version of vRO appliance to get the vRO dependencies for the toolchain in your artifactory.

1. Get all vRO artifacts on the local machine. Run:

   ```bash
   wget --no-check-certificate --recursive --no-parent --reject "index.html*" https://<vro_ip>:<vro_port>/vco-repo/com/
   wget --no-check-certificate --recursive --no-parent --reject "index.html*" https://<vro_ip>:<vro_port>/vco-repo/com/vmware/o11n/mojo/pkg/
   wget --no-check-certificate --recursive --no-parent --reject "index.html*" https://<vro_ip>:<vro_port>/vco-repo/com/vmware/o11n/pkg
   ```

2. Create a new local repository (e.g. **vro-local**) and add it to the virtual release repository (e.g. **libs-release**).

3. Navigate to the root folder of the downloaded repository on the local filesystem - at the same level as the **com** directory. E.g.:

   ```bash
   root@photon-G6H8GzV2j [ ~/192.168.71.1/vco-repo ]# ls
   com
   ```

4. Import the vro artifacts to the selected repository, for example:

   ```bash
   jfrog rt u --recursive true --flat false ./* vro-local
   ```

## Previous

See [Prerequisites](./Prerequisites.md).

## What's Next

See [Setting Up Local Environment](./Setting%20Up%20Local%20Environment.md).
