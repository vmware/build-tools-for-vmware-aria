---
title: Minimal Infrastructure Template
hide:
  - navigation
---

## Introduction

This guide walks you through creating a minimal infrastructure environment using containers. We use [Docker Compose](https://docs.docker.com/compose/) to orchestrate the following services:

- **[Nginx](https://hub.docker.com/_/nginx)** (Reverse Proxy)
- **[Nexus 3](https://hub.docker.com/r/sonatype/nexus3/)** (Artifact Repository)
- **[GitLab CE](https://hub.docker.com/r/gitlab/gitlab-ce)** (Source Control)
- **[GitLab Runner](https://hub.docker.com/r/gitlab/gitlab-runner)** (CI/CD Execution)

Using these applications, the infrastructure will support source control, running build pipelines, and hosting/serving artifact packages. These are the minimal requirements to run a [Build Tools for VMware Aria](https://github.com/vmware/build-tools-for-vmware-aria/blob/main/README.md) project.

This guide also covers configuring the applications, setting up a new project, and building it end-to-end.

!!! tip "Contributions Welcome"
    Please contribute back to this document if you find out-of-date contents or have ways to improve it.

---

## Prerequisites

### Installation

Before proceeding, ensure you have the following installed on your system:

1. **[Docker Engine](https://docs.docker.com/engine/install/)**
   Ensure the Docker host is configured to automatically start after a reboot and add Docker to your PATH.
   ```bash
   sudo systemctl start docker
   sudo systemctl enable docker
   export PATH=$PATH:~/.docker/bin
   ```
2. **[Docker Compose](https://docs.docker.com/compose/install)**
3. **[OpenJDK 17](https://openjdk.org/install/)**
4. **[Maven 3.9+](https://maven.apache.org/)**
5. **[Node.js 22.x](https://nodejs.org/en/download/package-manager)** (Using [fnm](https://github.com/Schniz/fnm) to manage Node versions is recommended).

!!! note "Linux Environments"
    For Linux, besides Docker, you can use the GitLab Runner's [Dockerfile](https://github.com/vmware/build-tools-for-vmware-aria/blob/main/infrastructure/gitlab-runner/Dockerfile) `RUN` commands to set up your environment automatically.

### Validation

Validate that all prerequisites are available in your terminal:

```bash
docker -v
node -v
mvn -v
java --version
```

---

## Running the Infrastructure

### Simple Automated Setup

This is a fully automated script to bring the infrastructure up. It will not edit your host files, but you can use the machine's IP.

```bash
curl -o- https://raw.githubusercontent.com/vmware/build-tools-for-vmware-aria/refs/heads/main/infrastructure/install.sh | bash
```

To avoid rate limits, you can optionally perform a Docker login by passing your credentials:

```bash
curl -o- https://raw.githubusercontent.com/vmware/build-tools-for-vmware-aria/refs/heads/main/infrastructure/install.sh | bash -s -- <dockerUsername> <dockerPAT>
```

If you do not edit your host files, adjust the external URL in the `docker-compose.yml` to use `localhost` (or your machine's IP) and run the compose file:

```bash
sed -i "s|external_url 'http://infra.corp.local/gitlab'|external_url 'http://localhost:8082/gitlab'|" /opt/build-tools-for-vmware-aria/infrastructure/docker-compose.yml
docker compose -f /opt/build-tools-for-vmware-aria/infrastructure/docker-compose.yml up -d --wait
```

### Advanced Manual Setup

For a manual installation, follow the steps below:

1. **Clone the repository:**
   ```bash
   git clone https://github.com/vmware/build-tools-for-vmware-aria.git
   cd build-tools-for-vmware-aria/infrastructure
   ```

2. **Configure Docker Compose:**
   Open the [docker-compose.yml](https://github.com/vmware/build-tools-for-vmware-aria/blob/main/infrastructure/docker-compose.yml) file. Check the IPs and port forwarding options for each container. The defaults should work unless you have port collisions. If you change ports, update the Nginx configuration file ([nginx/conf.d/main.conf](https://github.com/vmware/build-tools-for-vmware-aria/blob/main/infrastructure/nginx/conf.d/main.conf)) accordingly.

3. **Update your hosts file:**
   Docker provides an internal DNS server in user-defined networks (`infranet`). To access the containers via the Nginx reverse proxy from your host machine, map the local IP to the infrastructure domain.
   Add the following record to your `/etc/hosts` (Linux/Mac) or `C:\Windows\System32\drivers\etc\hosts` (Windows) file:
   ```text
   127.0.0.1 infra.corp.local
   ```

4. **Start the containers:**
   ```bash
   docker compose up -d
   ```

5. **Verify the containers are running:**
   ```bash
   docker ps
   ```
   You should see `nginx`, `nexus`, `gitlab-ce`, and `gitlab-runner` listed as **Up**.

6. **Wait for initialization:**
   It may take a few minutes for all services to become fully available at:
   - **Nginx:** [http://infra.corp.local](http://infra.corp.local)
   - **Nexus:** [http://infra.corp.local/nexus](http://infra.corp.local/nexus)
   - **GitLab:** [http://infra.corp.local/gitlab](http://infra.corp.local/gitlab)

---

## Application Setup

### GitLab

1. **Retrieve the initial root password:**
   ```bash
   sudo docker exec -it gitlab-ce grep 'Password:' /etc/gitlab/initial_root_password
   ```
   *(Note: This file is automatically deleted 24 hours after the container starts).*

2. **Log in:** Navigate to [http://infra.corp.local/gitlab/users/sign_in](http://infra.corp.local/gitlab/users/sign_in) and log in with the username `root` and the password retrieved above.
3. **Change the password:** Go to [http://infra.corp.local/gitlab/admin/users/root/edit](http://infra.corp.local/gitlab/admin/users/root/edit) and update the root password.

### GitLab Runner

1. **Navigate to the Runners page:** [http://infra.corp.local/gitlab/admin/runners](http://infra.corp.local/gitlab/admin/runners)
2. **Create a new runner:** Click **New instance runner**. Add a tag (e.g., `maven`), check the **Run untagged jobs** box, and click **Create runner**.
3. **Register the runner:** Copy the registration command snippet (it contains your `<AUTH_TOKEN>`). Execute it inside the runner container:
   ```bash
   docker exec -it gitlab-runner gitlab-runner register --url http://infra.corp.local/gitlab --token <AUTH_TOKEN>
   ```
4. **Follow the setup prompts:**
   - **GitLab instance URL:** Leave default and press Enter.
   - **Runner name:** Enter `Maven` or leave default.
   - **Executor:** Type `shell` and press Enter.
5. **Verify:** Go back to the GitLab admin runners page and ensure the new runner is marked as **Online**.

### Nexus

1. **Retrieve the initial admin password:**
   ```bash
   docker exec nexus sh -c 'cat /nexus-data/admin.password && echo'
   ```
2. **Log in:** Navigate to [http://infra.corp.local/nexus/](http://infra.corp.local/nexus/) and log in with the username `admin` and the password retrieved above.
3. **Complete the setup wizard:** Follow the prompts to set a new admin password and **disable** anonymous access.

---

## Environment Setup

1. **Configure Maven:** Follow the [Getting Started](../getting-started/index.md) guide to set up your local environment.
2. **Update `settings.xml`:** Replace your local `~/.m2/settings.xml` with the provided [infrastructure settings.xml](https://github.com/vmware/build-tools-for-vmware-aria/blob/main/infrastructure/.m2/settings.xml). This file configures:
   - Nexus server authentication.
   - A Maven Central mirror routing through Nexus.
   - A `nexus` profile defining the `releases` and `snapshots` repositories.
   
   To quickly copy the file:
   ```bash
   mkdir -p ~/.m2
   cp .m2/settings.xml ~/.m2/settings.xml
   ```

---

## Project Setup

1. **Create a GitLab Repository:** Create a new blank project called `demo` at [http://infra.corp.local/gitlab/projects/new#blank_project](http://infra.corp.local/gitlab/projects/new#blank_project).
2. **Clone the repository:** Follow the command-line instructions provided by GitLab to clone it locally, and navigate into the directory.
3. **Generate a project:** Run the following Maven Archetype command (replace `<VERSION>` with the latest release, e.g., `2.42.0`):
   ```bash
   mvn archetype:generate \
       -DinteractiveMode=false \
       -DarchetypeGroupId=com.vmware.pscoe.o11n.archetypes \
       -DarchetypeArtifactId=package-ts-vra-ng-archetype \
       -DarchetypeVersion=<VERSION> \
       -DgroupId=local.corp \
       -DartifactId=demo \
       -DlicenseTechnicalPreview=false \
       -DoutputDirectory=../
   ```
4. **Test locally:** Validate the generated project files and test the build:
 
    ```bash
    mvn clean package
    mvn test
    ```

    !!! tip "Note"
        You may need to comment out the `vro` package dependency in the `vra` module's `pom.xml` to successfully build without a live server connection.

5. **Configure the CI/CD Pipeline:** Create a `.gitlab-ci.yml` file in the repository root:
   ```yaml
   stages:
     - setup
     - build
     - test
     - install

   variables:
     LOCAL_REPO: >-
       -Dmaven.repo.local=$CI_PROJECT_DIR/.m2/repository    
     BUILD_OPTS: >-
       -Dhttps.protocols=TLSv1.2
       $LOCAL_REPO
       -DskipTests=true
     DEPLOY_OPTS: >-
       -Dhttps.protocols=TLSv1.2
       $LOCAL_REPO
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
       - echo "GROUP_ID=$(mvn help:evaluate $LOCAL_REPO -Dexpression=project.groupId -q -DforceStdout)" >> build.env
       - echo "ARTIFACT_ID=$(mvn help:evaluate $LOCAL_REPO -Dexpression=project.artifactId -q -DforceStdout)" >> build.env
       - echo "PROJECT_VERSION=$(mvn help:evaluate $LOCAL_REPO -Dexpression=project.version -q -DforceStdout)" >> build.env
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
       - mvn $LOCAL_REPO test

   install:
     stage: install
     needs: ["dynamic_variables"]
     script:
       - mvn $DEPLOY_OPTS clean package deploy
     artifacts:
       paths:
         - vra/target/*.zip
         - vro/target/*.zip
       name: "$GROUP_ID.$ARTIFACT_ID-$PROJECT_VERSION-$CI_PIPELINE_IID"
       expire_in: 1 month
   ```
6. **Trigger the pipeline:** Commit and push your changes to GitLab.
7. **Validate the pipeline:** Navigate to **CI/CD > Pipelines** in GitLab. Wait for the `setup`, `build`, `test`, and `install` jobs to succeed. 
8. **Download the artifact:** Once completed, download the `install:archive` artifact. It will contain the `local.corp.demo-1.0.0-SNAPSHOT-1.zip` installation bundle.
9. **Deploy:** You can now deploy this bundle using the [Bundle Installer Guide](../usage/installer.md) or by adding an Aria profile to your `settings.xml` and running `mvn package vrealize:push -P<PROFILE_NAME>`.

---

## Conclusion

This concludes the infrastructure setup. You now have a fully operational, end-to-end architecture to support **Build Tools for VMware Aria**. You can create, build, test, and push projects through automated pipelines that produce robust installation bundles.

!!! tip "Note"
    This template is intended as an educational sandbox. You should set up your persistent development and production environments following similar principles with proper security and high-availability configurations.
