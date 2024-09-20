# Contents
- [Introduction](#introduction)
- [Prerequisites](#prerequisites)
    - [Installation](#installation)
    - [Validation](#validation)
- [Running the Infrastructure](#running-the-infrastructure)
- [Application Setup](#application-setup)
    - [GitLab](#gitlab)
    - [GitLab Runner](#gitlab-runner)
    - [Nexus](#nexus)
- [Environment Setup](#environment-setup)
- [Project Setup](#project-setup)
- [Conclusion](#conclusion)

# Introduction

This file serves as a guide to create a minimal infrastructure example using containers. We will be using [Docker Compose](https://docs.docker.com/compose/) to orchestrate the following containers:

- [nginx](https://hub.docker.com/_/nginx)
- `Nexus`
    - [sonatype/nexus3](https://hub.docker.com/r/sonatype/nexus3/) for Linux.
    - [klo2k/nexus3](https://hub.docker.com/r/klo2k/nexus3) for M1+ Mac.
- `GitLab`
    - [gitlab/gitlab-ce](https://hub.docker.com/r/gitlab/gitlab-ce) for Linux.
    - [yrzr/gitlab-ce-arm64v8](yrzr/gitlab-ce-arm64v8) for M1+ Mac.
- [gitlab/gitlab-runner](https://hub.docker.com/r/gitlab/gitlab-runner)

Using these applications, the infrastructure will support source control, running the build pipeline as well as hosting and serving the artifact packages, which are all the minimal requirements to have an [Build Tools for VMware Aria](../README.md) project.

We will also be guiding you through configuring the applications and setting up a new project and building it end-to-end.

Please contribute back to this document if you find out of date contents or have other ways to improve it.

# Prerequisites

## Installation
Before proceeding, make sure you have the following installed on your system:
- [Docker Engine](https://docs.docker.com/engine/install/) - Important to configure the docker host to automatically start after reboot and add docker to PATH.
    ```
    systemctl start docker
    systemctl enable docker
    export PATH=$PATH:~/.docker/bin
    ```
- [Docker Compose](https://docs.docker.com/compose/install)
- [Open JDK 17](https://openjdk.org/install/)
- [Maven](https://maven.apache.org/)
- [NodeJS 14.21.03](https://nodejs.org/en/download/package-manager) - Use the nvm version for your OS.
- [npm 6.14.18](https://nodejs.org/en/download/package-manager) - Should be bundled with `NodeJS`.

For Linux, besides Docker, you can use GitLab Runner's [Dockerfile](/infrastructure/gitlab-runner/Dockerfile) RUN commands to setup your environment.

## Validation
Validate all of the prerequisites are available in the Terminal:
```
docker -v
node -v
npm -v
mvn -v
java --version
```

The latest versions used to test this guide on Ubuntu are as follows:
```
Docker version 27.0.3
NodeJS version 14.21.3
npm version 6.14.18
Apache Maven 3.6.3
Maven home: /usr/share/maven
Javaversion: 17.0.12, vendor:Ubuntu,runtime: /usr/lib/jvm/java-17-openjdk-arm64
Default locale:en_US, platform encoding:ANSI_X3.4-1968
OS name: "linux",version: "6.6.32-linuxkit",arch:"aarch64", family: "unix"
openjdk 17.0.12 2024-07-16
OpenJDK Runtime Environment (build 17.0.12+7-Ubuntu-1ubuntu220.04)
OpenJDK 64-Bit Server VM (build 17.0.12+7-Ubuntu-1ubuntu220.04, mixed mode, sharing)
```
# Running the Infrastructure

To get started, follow the steps below:

1. Clone the repository containing the existing resources:
    ```
    git clone https://github.com/vmware/build-tools-for-vmware-aria.git
    ```

2. Navigate to the `infrastructure` folder:
    ```
    cd build-tools-for-vmware-aria/infrastructure
    ```

3. Create the custom Maven GitLab Runner image by executing:
    ```
    docker build -t gitlab-runner ./gitlab-runner
    ```

4. Open the [docker-compose.yml](docker-compose.yml) file:
    - Depending on your host OS uncomment the `image` property under the `gitlab` and `nexus` services either tagged with `# Mac` or `# Linux`.
    - `OPTIONAL` Check the IPs and port forwarding options for each of the containers and make sure they work for your specific setup. Leaving them as-is should work, provided you don't have port collisions with other applications. In case you change the ports, you will also need to make the changes in the nginx configuration file [nginx/conf.d/main.conf](./nginx/conf.d/main.conf).
    
5. Add the nginx container and the docker internal host endpoints to your `hosts` file.  
    - Docker provides an internal DNS server in user-defined networks (infranet) to resolve container names to their internal IP addresses. Since your nginx and GitLab services are part of the infranet network, they can communicate using their Docker defined hostnames.

    - We are going to be accessing the containers from the nginx reverse proxy. For this you need to manually edit the /etc/hosts file on your host machine:

    - Add the following records to the `/etc/hosts` file.
        ```
        127.0.0.1 infra.corp.local
        ```

6. Run the [docker-compose.yml](docker-compose.yml) file:
    ```bash
    docker compose up -d
    ```

7. Validate the containers are created:
    ```bash
    docker ps
    ```

   The results should look something like this:
    ```
    CONTAINER ID   IMAGE                    COMMAND                  CREATED         STATUS                   PORTS                                                 NAMES
    eec4f06c5e88   nginx                    "/docker-entrypoint.…"   5 minutes ago   Up 5 minutes             0.0.0.0:80->80/tcp                                    nginx
    dc46763483f0   klo2k/nexus3             "/__cacert_entrypoin…"   5 minutes ago   Up 5 minutes             0.0.0.0:8081->8081/tcp                                nexus
    42630f6121ad   yrzr/gitlab-ce-arm64v8   "/assets/wrapper"        5 minutes ago   Up 5 minutes             0.0.0.0:8022->22/tcp, 0.0.0.0:8082->80/tcp            gitlab-ce
    17ba02a491e8   gitlab/gitlab-runner     "/usr/bin/dumb-init …"   5 minutes ago   Up 5 minutes             0.0.0.0:2811->2811/tcp                                gitlab-runner
    ```

8. Wait until all containers are up and running, which might take a few minutes:
    - nginx - [infra.corp.local](http://infra.corp.local)
    - Nexus - [infra.corp.local/nexus](http://infra.corp.local/nexus)
    - GitLab - [infra.corp.local/gitlab](http://infra.corp.local/gitlab)
    - GitLab Runner - [infra.corp.local/gitlab-runner](http://infra.corp.local/gitlab-runner) (*no http web interface*)

# Application Setup

## GitLab
1. Grab the GitLab `root` password:
    ```
    sudo docker exec -it gitlab-ce grep 'Password:' /etc/gitlab/initial_root_password
    ```
    *This file will be deleted 24 hours after a container restart*

2. Login at [http://infra.corp.local/gitlab/users/sign_in](http://infra.corp.local/gitlab/users/sign_in) with:
    ```
    account: root
    password: *password from previous step*
    ```
3. Change the root user password at [http://infra.corp.local/gitlab/admin/users/root/edit](http://infra.corp.local/gitlab/admin/users/root/edit)

## GitLab Runner
1. Go to [http://infra.corp.local/gitlab/admin/runners](http://infra.corp.local/gitlab/admin/runners)
2. Click on `New instance runner`
3. Enter any tag, for instance `maven` and optionally select `Run untagged jobs` and press `Create runner`
4. Copy the code snippet in `Step 1`, which contains your AUTH_TOKEN and should look something like the following:
    ```
    gitlab-runner register --url http://infra.corp.local/gitlab --token <AUTH_TOKEN>
    ```
5. Append it to `docker exec -it gitlab-runner` and execute on your host:
    ```
    docker exec -it gitlab-runner gitlab-runner register --url http://infra.corp.local/gitlab --token <AUTH_TOKEN>
    ```
6. Follow the setup process by providing the following:
    - GitLab instance URL: leave `default` and press Enter
    - Enter a name for the runner: input `Maven` or leave `default` and press Enter
    - Enter an executor - input `shell` and press Enter
7. Go back to [http://infra.corp.local/gitlab/admin/runners](http://infra.corp.local/gitlab/admin/runners) and validate the runner is `Online`

## Nexus
1. Grab the Nexus `admin` password:
    ```
    docker exec nexus sh -c 'cat /nexus-data/admin.password && echo'
    ```
2. Login at [http://infra.corp.local/nexus/](http://infra.corp.local/nexus/) with:
    ```
    account: admin
    password: *password from previous step*
    ```
3. Follow the initial setup wizard instructions:
    - Enter new `admin` password
    - Disable anonymous access

# Environment Setup
1. Follow the [Getting Started](../docs/versions/latest/General/Getting%20Started/) guides to setup your local environment.
2. Edit your local `~/.m2/settings.xml` by using the repository provided [settings.xml](./.m2/settings.xml). It should contain:
    - A nexus server authentication under `servers` with id `nexus` with your Nexus username and password.
    - A `Maven Central` mirror under `mirrors` with id `nexus`.
    - A profile under `profiles` with id `nexus` with `releases` and `snapshots` repositories.
    - A profile under `profiles` with id `packaging`.

# Project Setup
1. Create a repository called `demo` at [http://infra.corp.local/gitlab/projects/new#blank_project](http://infra.corp.local/gitlab/projects/new#blank_project).
2. Setup your local git environment and follow the `Command line instructions` listed in your new repo to clone it.
3. Open a terminal and `cd` to the repository directory.
4. Generate a project by running:
    ```
    mvn archetype:generate -DinteractiveMode=false -DarchetypeGroupId=com.vmware.pscoe.o11n.archetypes -DarchetypeArtifactId=package-ts-vra-ng-archetype -DarchetypeVersion=<VERSION> -DgroupId=local.corp -DartifactId=demo -DlicenseTechnicalPreview=false -DoutputDirectory=../
    ```
    where `<VERSION>` is the last released version or any specific version you want, i.e. `2.42.0`.

    You might also want to change the specific archetype to best fit your specific use-case. Check out the archetype templates available at [com.vmware.pscoe.o11n.archetypes](https://central.sonatype.com/namespace/com.vmware.pscoe.o11n.archetypes).
5. Validate the command generates the appropriate project files.
6. Validate you can build and test the project locally:
    ```
    mvn clean package
    mvn test
    ```
    You might need to comment:
    ```
    <dependency>
        <groupId>local.corp</groupId>
        <artifactId>vro</artifactId>
        <version>1.0.0-SNAPSHOT</version>
        <type>package</type>
    </dependency>
    ```
    in `vra`'s `pom.xml` in order to successfully build and test.

7. Configure the GitLab pipeline by creating a new file in the root of the repository named `.gitlab-ci.yml`:
    ```
    stages:
      - setup
      - build
      - test
      - install

    variables:
      BUILD_OPTS: >-
        -Dhttps.protocols=TLSv1.2
        -Dmaven.repo.local=$CI_PROJECT_DIR/.m2/repository
        -DskipTests=true
      DEPLOY_OPTS: >-
        -Dhttps.protocols=TLSv1.2
        -Dmaven.repo.local=$CI_PROJECT_DIR/.m2/repository
        -DskipTests=true
        -Dbuild.number=$CI_PIPELINE_IID
        -Dsurefire.useSystemClassLoader=false
        -Pbundle-with-installer
        -DoutputDirectory=target
        -DartifactName=artifact.zip
        -U
        --batch-mode

    dynamic_variables:
      stage: setup
      script:
        - echo "GROUP_ID=$(mvn help:evaluate -Dexpression=project.groupId -q -DforceStdout)" >> build.env
        - echo "ARTIFACT_ID=$(mvn help:evaluate -Dexpression=project.artifactId -q -DforceStdout)" >> build.env
        - echo "PROJECT_VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)" >> build.env
      artifacts:
        expire_in: 3 hours
        reports:
          dotenv: build.env

    build:
      stage: build
      script:
        - mvn $BUILD_OPTS clean package

    test:
      stage: test
      script:
        - mvn test

    install:
      stage: install
      needs: ["dynamic_variables"]
      script:
        - mvn $DEPLOY_OPTS clean package
      artifacts:
        paths:
          - vra/target/*.zip
          - vro/target/*.zip
        name: "$GROUP_ID.$ARTIFACT_ID-$PROJECT_VERSION-$CI_PIPELINE_IID"
        expire_in: 1 month
    ```
    *For more information about the GitLab pipelines, see the pipeline documentation at [https://docs.gitlab.com/ee/ci/](https://docs.gitlab.com/ee/ci/)*

8. Commit and push your git repository changes
9. Validate `GitLab` runs the pipelines successfully:
    - Head to [http://infra.corp.local/gitlab/root/demo/-/pipelines](http://infra.corp.local/gitlab/root/demo/-/pipelines) and wait for all of the `setup`, `build`, `test` and `install` steps to run successfully on the the pipeline run you just triggered by pushing in Step #8.
    - After done, check that you can see the `install:archive` artifact from the successful pipeline outputs (*download button on the right side of the run*) or by going to the artifacts page at [http://infra.corp.local/gitlab/root/demo/-/artifacts](http://infra.corp.local/gitlab/root/demo/-/artifacts)
    - Pressing the `install:archive` artifact or downloading the *.zip* directly should start a download of the `local.corp.demo-1.0.0-SNAPSHOT-1.zip` install bundle.
10. You can continue by:
    - following the [Bundle Installer Guide](../docs/archive/doc/markdown/use-bundle-isntaller.md) to push your package to your `Aria` instance manually.
    - adding a new `profile` in the [settings.xml](./.m2/settings.xml) for your `Aria` instance and then executing `mvn package vrealize:push -P<NEW_PROFILE_NAME>` which will push your changes directly to a life environment. Follow the [Push](../docs/archive/doc/markdown/use-workstation-vra-ng-project.md#Push) section of your specific archetype documentation at [docs/archive/doc/markdown](../docs/archive/doc/markdown/).
        
# Conclusion
This concludes the setup. You now have a fully operational end-to-end architecture to support the [Build Tools for Aria](../README.md), create, build and push projects and run pipelines that produce install bundles.

*This is not intended as a production environment, but as an educational sandbox. You should setup your proper and persistent development and production environments in a similar fashion.*

***Please contribute to keeping this up to date!***
