#!/usr/bin/bash

# Depends on `git`, `curl`
# This will checkout BTVA locally in `/opt`
# If you want to authenticate to docker, to raise your pull limits, pass exactly 2 arguments, first being the username, second the PAT

######################### Utils

# ANSI color codes
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No color

# Function to print colored text
print_color() {
    local color="$1"
    local message="$2"
    echo -e "${color}${message}${NC}"
}

# Function to check if a command is available
# https://stackoverflow.com/questions/592620/how-to-check-if-a-program-exists-from-a-bash-script
command_exists() {
  command -v "$1" >/dev/null 2>&1
}

######################## Funcs

# Am I root
if [ "$EUID" -ne 0 ]; then
	print_color "$RED" "Please run as root"
	exit 1
fi

TMP_DIR=/tmp
BTVA_CHECKOUT_DIR=/opt
BTVA_NAME=build-tools-for-vmware-aria
INFRASTRUCTURE_DIR=infrastructure
BTVA_DIR=$BTVA_CHECKOUT_DIR/$BTVA_NAME
BTVA_INFRASTRUCTURE_DIR=$BTVA_CHECKOUT_DIR/$BTVA_NAME/$INFRASTRUCTURE_DIR

checkoutBTVA() {
	if [ -d $BTVA_DIR ]; then
			print_color "$YELLOW" "$BTVA_DIR exists, skipping"
	else 
			print_color "$GREEN" "$BTVA_DIR does not exist, checking the repository out"
			pushd $BTVA_CHECKOUT_DIR
				git clone https://github.com/vmware/build-tools-for-vmware-aria.git || exit 1
			popd
	fi
}

installDocker() {
	pushd $TMP_DIR
		if command_exists docker; then
			print_color "$YELLOW" "Docker is already installed, skipping"
			return
		else
			print_color "$GREEN" "Docker is not installed, installing"
			curl -fsSL https://get.docker.com -o get-docker.sh && sh get-docker.sh
		fi
	popd
}

dockerLogin() {
	if [ $# -eq 2 ]; then
		print_color "$GREEN" "Docker login"
		docker login -u $1 -p $2
	else
		print_color "$RED" "Did not pass exactly 2 arguments, will not do docker login"
	fi
}

createInfra() {
	pushd $BTVA_INFRASTRUCTURE_DIR
		docker compose up -d --wait
	popd
}

######################## Funcs

checkoutBTVA
installDocker
dockerLogin $1 $2
createInfra

