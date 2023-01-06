# Build Tools for VMware Aria

**Build Tools for VMware Aria** provides set of infrastructure components supporting source control, artifact managment, build system and wiki to the vRrealize engineers.

## Table of Contents
1. [Photon](#Photon)
2. [NGINX](#NGINX)
3. [Installation](#Installation)
4. [Uninstallation](#Uninstallation)
5. [GitLab CE](#GitLab)
6. [JFrog Artifactory OSS](#Artifactory)

## Platform Requirements

* Platform Server
    - Hardware
        - CPU Cores 4
        - Memory 8 GB
        - Storage 40 GB
    - Operating System
        - Photon OS
    - Software
        - Docker
            - GitLab CE (Docker Container)
            - GitLab Runner (Docker Container)
            - Artifactory OSS (Docker Container)
            - Nginx (Docker Container)
    - Firewall
        - Inbound
            - 80
            - 443
        - Outbound
            - vRSCLM:[443:80]
            - vRA:[443:80]
            - vRO:[443:80]

* Development Workstation
    - Could be developer laptop, desktop or jumphost
    - Firewall
        - Outbound
            - Build Tools for VMware Aria Server:[80, 443]
            - vRSCLM:[443:80]
            - vRA:[443:80]
            - vRO:[443:80]


## Photon
PhotonOS is an open source minimalist Linux container host optimized for cloud-native applications, cloud platforms
and VMware infrastructure. Photon OS provides a secure run-time environment for efficiently running containers.

### Requirements
Preprovisioned [Photon OS 2.0](https://vmware.github.io/photon/) with the following software packages pre-installed:
* [Docker-compose](https://docs.docker.com/compose/install/#install-compose) (>= 1.18.0)
  * It is important to configure docker host to automatically start after reboot
    ```
    systemctl start docker
    systemctl enable docker
    ```
* NPM (>= 5.3.0)
* ZIP, UNZIP and TAR (```tdnf install zip unzip tar```)
* Preconfigured networking
* Resolvable hostnames for GitLab, Artifactory, CI Runner (check **Install**) section)
* Temporary access to internet during the installation
* Propper resource sizing based on the load (number of users, projects, pipelines, artifacts, etc)

## NGINX
NGINX is a web server that is used as a reverse proxy that exposes all of the needed services to the outside world.
The use of NGINX reverse proxy simplifies service management configuration, adds security and central SSL management (SSL termination occurs on the proxy) and provides unified access logging and a simple user portal. Certain caching performance benefits may also apply even with default configuration.

The default configured ports for NGINX are **80/443**.

### NGINX and Network Configuration
The default NGINX configuration file can be found in **nginx/conf.d/nginx.conf**.

The networking stack is constructed as follows:
Docker network **infranet 172.18.0.0/24**:
* 172.18.0.10 - NGINX proxy server
* 172.18.0.11 - gitlab.corp.local (GitLab CE)
* 172.18.0.12 - gitlab-runner.corp.local (GitLab CI Runner)
* 172.18.0.13 - artifactory.corp.local (JFrog Artifactory)

On the internal network, the vRA IaaC Web Portal will respond to **infra.corp.local**.
The services are available on the following URLs:
* [Gitlab](http://infra.corp.local/gitlab)
* [Artifactory](http://infra.corp.local/artifactory)
* [VSCode Extension](http://infra.corp.local/vscode)
* [vRo Extension](http://infra.corp.local/vro)
* [Help](http://infra.corp.local/help)

The NGINX reverse proxy is used in this situation to forward the traffic to the specified container. None of the services and their corresponding ports are exposed to the outside world - they "live" inside the Docker container network.

The hostnames and IPs are fully configurable. If the hostnames or IPs change you should update **etc/hosts** file, **docker-compose.yml** and the NGINX config file located in ``nginx/conf.d/nginx.conf`` as they use and inject the hostnames and IP addresses into the containers internal DNS resolver.

## Installation

### Infrastructure Folder Contents
The infrastructure package contains the following items:
* **etc** folder: contains the needed configuration files for various services
* **nginx** folder: contains the needed files for the NGINX web server
* **docker-compose.yml** file that is used to describe the Docker services
* **iac-for-vrealize-configurator.sh** bootstrap script for the infrastructure

### Installation Instructions
1. Deploy the package into the PhotonOS appliance.
2. Open the **docker-compose.yml** file and locate the following string: ``infra.corp.local:<DOCKER_HOST_IP>``
3. Change the value of **<DOCKER_HOST_IP>** with the IP address of the Docker host.
3.1 In case Photon OS needs to use http proxy add on top of the **docker-compose.yml** file the following line  ``` ARG http_proxy=<proxy>```. This will make the proxy available during the build phase but not container runtime.
4. Navigate to the installation script ```iac-for-vrealize-configurator.sh``` located at: ```/path/to/package/infrastructure/```
5. Assign execution permissions to the script
6. Execute the script with the following options: ```iac-for-vrealize-configurator.sh --install```. This will trigger the installation process.
7. If the script finishes succesfully execute the following command to validate if all docker containers have started: ```docker ps```
8. There should be containers named **nginx, gitlab-ce, gitlab-runner, artifactory**
9. Some of the services (for example the GitLab portal) require couple of minutes for starting so they would not be imidiatelly available after the startup procedure.
10. You can check the log file of the installer located in:   ```/var/log/vmware/iac-for-vrealize/iac-for-vrealize-configurator.log```

## GitLab CE
When you first open the GitLab you will be prompted to set a password for the **root** account.
After that you will be able to login as root, create users, groups, repositories and manage the portal.

### Register GitLab CI Runner
1. Login with administrator privileges in Gitlab
2. Navigate to **Admin Area > Runners**
3. Copy the registration token for the runners
4. Login to the PhotonOS Docker host and execute the following command:
```docker exec -it gitlab-runner gitlab-runner register```
5. Follow the instruction to register the runner with the Gitlab instance
6. When prompted with **Please enter the executor** use **shell** as a type of runner
7. Use the internal gitlab URL, e.g. http://infra.corp.local/gitlab by default

## JFrog Artifactory OSS
JFrog Artifactory is a Universal Repository Manager supporting all major packaging formats, build tools and CI servers.

### Create Default Password and Maven Repo
When you first open JFrog Artifactory you will be prompted to create a password for the default **admin** user and a default repo.
Choose **Maven** as a type of repo to create and Artifactory will create the default Maven repositories.

### Configuring JFrog CLI for artifact deployment

* JFrog CLI will already be installed by the installation script.
* Run the command ``jfrog rt config`` to initiate the initial configuration of the CLI and follow the steps:
  * ``Artifactory server ID:`` - Leave this empty
  * ``Artifactory URL`` - Provide the internal artifactory url, e.g. http://infra.corp.local/artifactory by default
  * ``API key (leave empty for basic authentication):`` - Either enter an API key for authentication or leave empty
  * ``User: `` - Enter user for basic authentication (default user **admin**)
  * ``Password: `` - Enter password for basic authentication (password configured in **Create Default Password**)

### Uploading toolchain artifacts to Artifactory
_Note that the libs-release, libs-snapshot etc. are the default Maven repositories created by JFrog's **Quick Setup** shown at 
first login. This guide assumes that this **Quick Setup** has been executed._
1. Create a local repository in artifactory to contain the toolchain artifacts, e.g. **pscoe-local** and add it to the virtual release repository (e.g. **libs-release**) 
3. Unzip **iac-maven-repository.zip** found at **artifacts/maven/** path relative to the root of the toolchain bundle to a folder, e.g. **import/**
4. Go to the directory where you have unzipped the archive. Your working directory should contain the "com" folder and the **archetype-catalog.xml** file, e.g.:
```bash
root@photon-G6H8GzV2j [ ~/toolchain/import ]# ls
archetype-catalog.xml  com
```
5. Then, run the following command ``jfrog rt u --recursive=true --flat=false ./ pscoe-local``, where **pscoe-local** should be the name of the repository you've created at step #1.
6. Examine the output of the command. It should look something similar to this:
```
Uploading artifact: /path/to/artifact/some-artifact.jar
{
  "status": "success",
  "totals": {
    "success": 1,
    "failure": 0
  }
}
```

## Upload vRO artifacts to Artifactory
First you need access to a vRO appliance to get the vRO dependencies for the toolchain in your artifactory.
1. Get all vRO artifacts on the local machine. Run: 
```
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
```
jfrog rt u --recursive true --flat false ./ vro-local
```

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

## Managing the Docker services
The services are managed with docker-compose.

Initial startup is trigered from the installation script with the command: ```docker-compose up -d```.
This will kickstart the creation of the containers and will boot them up.

After that you can use the following management commands (executed from the directory of the docker-compose file):
* ```docker-compose start``` - will start all containers for all services in the docker-compose file
* ```docker-compose stop``` - will stop all containers for all services in the docker-compose file
* ```docker-compose restart```- will restart all containers for all services in the docker-compose file

* ```docker-compose up -d``` - this will recreate all Docker containers
* ```docker-compose down``` - this will stop all containers and delete them

## Uninstalling the solution
To uninstall the solution run the following command:
```../infrastructure/iac-for-vrealize-configurator.sh --clean```

This will remove:
* All running containers for the solution - NGINX, GitLab, GitLab CI Runner, Artifactory
* Docker network **infranet**
* JFrog CLI
* Custom configuration from **/etc/hosts** file

No persistent container data is deleted - if you want to fully delete all data you should
delete the files located in the following locations:

NGINX:
* ./nginx/conf.d
* ./nginx/vhost.d
* ./nginx/html
* ./nginx/certs
* ./nginx/proxy.conf
* /var/log/nginx

GitLab:
* /srv/gitlab/config
* /srv/gitlab/logs
* /srv/gitlab/data

GitLab Runner:
* /srv/gitlab-runner/config

Artifactory:
* /data/artifactory

# Next step
- Configure developer **[Build Tools for VMware Aria Workstation](./setup-workstation.md)**
