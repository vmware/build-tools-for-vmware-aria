#!/bin/bash

# Required Versions Ranges
MIN_NODE_VERSION="14"
MAX_NODE_VERSION="16"
MIN_MAVEN_VERSION="3.9"
MIN_JAVA_VERSION="17"
MAX_JAVA_VERSION="21"

# Color Codes
GREEN="\033[0;32m"
RED="\033[0;31m"
NC="\033[0m" # No Color

# Success flags
all_checks_passed=true

# Helper function to compare versions
version_ge() { 
    # Returns 0 if $1 >= $2, 1 otherwise
    printf '%s\n%s\n' "$2" "$1" | sort -C -V
}

# Check Node.js Version Range (14.x to 16.x)
check_node_version() {
    node_version=$(node -v 2>/dev/null | sed 's/v//')
    node_major_version=$(echo "$node_version" | cut -d. -f1)

    if [ -z "$node_version" ]; then
        echo -e "${RED}✘ Node.js is not installed.${NC}"
        all_checks_passed=false
    elif [ "$node_major_version" -ge "$MIN_NODE_VERSION" ] && [ "$node_major_version" -le "$MAX_NODE_VERSION" ]; then
        echo -e "${GREEN}✔ Node.js version $node_version is within the required range ($MIN_NODE_VERSION - $MAX_NODE_VERSION).${NC}"
    else
        echo -e "${RED}✘ Node.js version $node_version is outside the required range ($MIN_NODE_VERSION - $MAX_NODE_VERSION).${NC}"
        all_checks_passed=false
    fi
}

# Check Maven Version (3.9.x or newer)
check_maven_version() {
    maven_version=$(mvn -v 2>/dev/null | awk '/Apache Maven/ {print $3}')

    if [ -z "$maven_version" ]; then
        echo -e "${RED}✘ Maven is not installed.${NC}"
        all_checks_passed=false
    elif version_ge "$maven_version" "$MIN_MAVEN_VERSION"; then
        echo -e "${GREEN}✔ Maven version $maven_version meets the minimum requirement (>= $MIN_MAVEN_VERSION).${NC}"
    else
        echo -e "${RED}✘ Maven version $maven_version is older than the required version (>= $MIN_MAVEN_VERSION).${NC}"
        all_checks_passed=false
    fi
}

# Check Java Version (must be between 17 and 21)
check_java_version() {
    java_version=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}')
    java_major_version=$(echo "$java_version" | cut -d. -f1)

    if [ -z "$java_version" ]; then
        echo -e "${RED}✘ Java is not installed.${NC}"
        all_checks_passed=false
    elif [ "$java_major_version" -ge "$MIN_JAVA_VERSION" ] && [ "$java_major_version" -le "$MAX_JAVA_VERSION" ]; then
        echo -e "${GREEN}✔ Java version $java_version is within the required range ($MIN_JAVA_VERSION - $MAX_JAVA_VERSION).${NC}"
    else
        echo -e "${RED}✘ Java version $java_version is outside the required range ($MIN_JAVA_VERSION - $MAX_JAVA_VERSION).${NC}"
        all_checks_passed=false
    fi
}

# Run all checks
echo "Starting VMware Aria Build Tools Checks..."

check_node_version
check_maven_version
check_java_version

# Display summary
if [ "$all_checks_passed" = true ]; then
    echo -e "${GREEN}All checks passed successfully.${NC}"
else
    echo -e "${RED}Some checks failed. Please review the above messages.${NC}"
fi
