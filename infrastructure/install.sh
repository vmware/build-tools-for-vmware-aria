#!/usr/bin/bash

# Depends on `git`, `curl`

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

checkoutBTVA() {
	BTVA_NAME=build-tools-for-vmware-aria
	BTVA_DIR=/tmp/$BTVA_NAME

	if [ -d $BTVA_DIR ]; then
			print_color "$YELLOW" "$BTVA_DIR exists, skipping"
	else 
			print_color "$GREEN" "$BTVA_DIR does not exist, checking the repository out"
			pushd /tmp
				git clone https://github.com/vmware/build-tools-for-vmware-aria.git || exit 1

				# @TODO: This is temporary, remove it later
				cd $BTVA_NAME && git checkout refactor/minimal-infra-simplified-setup || exit 1
			popd
	fi
}

installDocker() {
	pushd /tmp
		curl -fsSL https://get.docker.com -o get-docker.sh && sh get-docker.sh
	popd
}

createInfra() {
	pushd /tmp/build-tools-for-vmware-aria/infrastructure
		docker compose up -d
	popd
}

######################## Funcs


checkoutBTVA
installDocker
createInfra

