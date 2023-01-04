#!/bin/bash

CONFIG_START_SECTION="# vRA IaaC START SECTION"
CONFIG_END_SECTION="# vRA IaaC END SECTION"

NGINX_CONTAINER="nginx"
GITLAB_CONTAINER="gitlab-ce"
GITLAB_RUNNER_CONTAINER="gitlab-runner"
ARTIFACTORY_CONTAINER="artifactory"

LOG_DIR="/var/log/vmware/iac-for-vrealize"
LOG_FILE=$LOG_DIR/"iac-for-vrealize-configurator.log"
SLEEP_TIME=10
CURL_RETRY_COUNT=3

DOCKER_NETWORK="infranet"

ETC_HOSTS_FILE="/etc/hosts"
NGINX_FOLDER="nginx/"
JFROG_FILE="/bin/jfrog"

function pullDockerImages()
{
	log "Pulling Docker images..."

	declare -a services=(
					"nginx"
                	"gitlab"
                	"artifactory"
					)

	for service in "${services[@]}"
	do
		log "Pulling service '$service'..."
		docker-compose pull "$service" |& tee -a $LOG_FILE &
		wait
	done
}

function createDockerImages()
{
	log "Creating Docker image for GitLab CI Runner..."

	cd ubuntu
	docker build -t ubuntu-gitlab-runner:latest . |& tee -a $LOG_FILE
	cd -
}

function createDockerNetworking()
{
	log "Creating Docker network '$DOCKER_NETWORK'..."
	docker network create --subnet=172.18.0.0/24 $DOCKER_NETWORK |& tee -a $LOG_FILE
}

function removeDockerNetworking()
{
	log "Removing Docker network '$DOCKER_NETWORK'..."
	docker network rm $DOCKER_NETWORK |& tee -a $LOG_FILE
}

function removeDockerContainers()
{
	log "Removing Docker containers..."

	declare -a containers=(
					$NGINX_CONTAINER
					$GITLAB_CONTAINER
					$GITLAB_RUNNER_CONTAINER
					$ARTIFACTORY_CONTAINER
					)

	for container in "${containers[@]}"
	do
		log "Removing container '$container'..."
		docker rm -f "$container" |& tee -a $LOG_FILE &
		wait
	done
}

function appendHostsFile()
{
	checkIfConfigExists $ETC_HOSTS_FILE $CONFIG_START_SECTION
	local returnCode=$?

	if [ $returnCode -eq 0 ]; then
		log "/File etc/hosts contains custom configuration. Skipping..."
	else
		createBackupFile $ETC_HOSTS_FILE

		log "Apending /etc/hosts config..."
		cat "etc/hosts" >> $ETC_HOSTS_FILE
	fi
}

function setPermissionsToNginx()
{
	log "Setting 755 permissions to NGINX payload folder..."
	chmod -R 755 $NGINX_FOLDER
}

function copyDocsToNginx()
{
	log "Copying documentation files into NGINX payload folder..."
	cd ..
	cp -rv docs/. infrastructure/nginx/html/home |& tee -a $LOG_FILE
	cd -
}

function installClarity()
{
    log "Installing Clarity dependency into NGINX..."
    cd nginx/html/home
    npm install |& tee -a $LOG_FILE
    cd -
}
function copyPluginsToPayload()
{
	log "Copying plugins to NGINX payload..."
	cd ..
	cp -rv artifacts/vscode/. infrastructure/nginx/html/home/payload |& tee -a $LOG_FILE
	cd -
}

function installJFrogCLI()
{
	if [ -f $JFROG_FILE ]; then
		log "JFrog CLI already exists. Skipping installation..."
	else
		log "Installing JFrog CLI..."

		cd /bin
		curl --retry $CURL_RETRY_COUNT -fL https://getcli.jfrog.io | sh |& tee -a $LOG_FILE
		cd -
	fi
}

function setJFrogPermissions()
{
	log "Setting JFrog permissions..."

	mkdir -p /data/artifactory
	chown -R 1030:1030 /data/artifactory
}

function removeJFrogCLI()
{
	log "Removing JFrog CLI..."
	rm $JFROG_FILE
}

function startServices()
{
	log "Starting services..."
	docker-compose up -d |& tee -a $LOG_FILE

	log "Running containers:"
	docker ps
}

function clean()
{
	log "Start cleaning old configuration..."

	# Check network config
	checkIfConfigExists $ETC_HOSTS_FILE $CONFIG_START_SECTION

	local returnCode=$?
	if [ $returnCode -eq 0 ]; then
		log "Hosts file contains custom configuration. Removing custom configuration..."
		removeLinesFromFile $ETC_HOSTS_FILE $CONFIG_START_SECTION $CONFIG_END_SECTION
	else
		log "Hosts file does not contain custom configuration. Skipping..."
	fi

	# Clean Docker stuff
	removeDockerContainers
	removeDockerNetworking

	# Clean third-party software
	removeJFrogCLI

	log "Old configuration cleaned. Data folders and Docker images remain. Please clean them manually."
}

function install()
{
	log "Start installation of vRA Infrastructure as a Code solution..."

	pullDockerImages
	createDockerImages
	createDockerNetworking

	appendHostsFile
	copyDocsToNginx
	installClarity
	copyPluginsToPayload
	setPermissionsToNginx

	installJFrogCLI
	setJFrogPermissions
	startServices

	# Wait for containers to be ready
	log "Sleeping for ${SLEEP_TIME} seconds..."
	sleep $SLEEP_TIME

	log "Infrastructure setup completed."
}

# ----------HELPER METHODS----------

# Creates a backup file for the specified filename
# param1 - file name with path
function createBackupFile()
{
	local fileToBackup=$1
	local backupFile="$fileToBackup"".backup"

	log "Backing up file '$fileToBackup' to '$backupFile'..."
	cp $fileToBackup $backupFile
}

# Checks if the provided string is contained in the file
# param1 - file name
# param2 - string to search for
checkIfConfigExists()
{
	local fileName=$1
	local searchString=$2

	grep -iw "$fileName" -e "$searchString" > /dev/null
	local returnCode=$?

	if  [ $returnCode -eq 0 ]; then
		log "Config in file $fileName exists."
		return 0;
	else
		log "Config in file $fileName does not exist."
		return 1;
	fi
}

# Remove lines from file enclosed between [start-line, end-line]
# param3 - File
# param1 - Start line
# param2 - End line
function removeLinesFromFile()
{
	local file=$1
    local startLine=$2
    local endLine=$3

	sed -i "/$startLine\b/,/$endLine\b/d" "$file"
}

function printHelp()
{
	cat << EOF
DESCRIPTION
Install and configure vRA infrastructure as a Code solution

OPTIONS
--install		Cleans any previous installations and installs the solution.
--clean			Cleans the appliance from previous installations. Data folders and downloaded images will remain.
--help			Prints this help.
EOF
}

function createLogFile()
{
	mkdir -p $LOG_DIR

	if [ ! -f "$LOG_FILE" ]; then
		log "Creating log file '$LOG_FILE'..."
		touch $LOG_FILE
	fi
}

log()
{
    local text=$@
    local COLOR='\033[0;33m'
    local NO_COLOR='\033[0m'

    echo -e "${COLOR}[$(date)]: ${COLOR}$text${NO_COLOR}"
    echo -e "[$(date)]: $text" >> $LOG_FILE
}

# Main
createLogFile
PARAMETER=$1

if [ -z "$PARAMETER" ]; then
	log "Empty parameter'. Please use --help for more info."
	exit 1
fi

if [ "$PARAMETER" = "--install" ]; then
	log "----------Starting script with '$PARAMETER' parameter----------"
	clean
	install
	exit 0
elif [ "$PARAMETER" = "--clean" ]; then
	log "----------Starting script with '$PARAMETER' parameter----------"
	clean
	exit 0
elif [ "$PARAMETER" = "--help" ]; then
	log "----------Starting script with '$PARAMETER' parameter----------"
	printHelp
	exit 0
else
	log "Invalid parameter '$PARAMETER'. Please use --help for more info."
	exit 1
fi