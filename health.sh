GREEN="\033[0;32m"
RED="\033[0;31m"
YELLOW="\033[0;33m"
NC="\033[0m"

if [[ $# -eq 1 ]] && [[ $1 != "--additional" ]]; then
	echo "Usage: '$0' to show mandatory prerequisites"
	echo "Usage: '$0 --additional' to show all, including non-mandatory prerequisites"
	exit 1
fi

additional=$1

all_checks_passed=true
regex="[[:digit:]]+\.[[:digit:]]+\.[[:digit:]]+"

declare -a prerequisites=(
	"Java       java    --version all      17     24"
	"Maven      mvn     --version all      3.9"
    "Node.js    nodejs  --version all      22"
	"OpenSSL    openssl version   polyglot 10     17.0"
	"Python     python  --version polyglot 3.2"
	"Pip        pip     --version polyglot 25.0   26.0"
	"PowerShell pwsh    --version polyglot 7.0"
)

v_compare() { 
	# Returns 0 if $1 >= $2, 1 otherwise
	printf '%s\n%s\n' "$2" "$1" | sort -C -V
}

echo "Starting Build Tools for VMware Aria Checks..."

required=""

for prerequisite in "${prerequisites[@]}"; do
	# uses default whitespace IFS
	read -a properties <<< "$prerequisite"

	name=${properties[0]}
	command=${properties[1]}
	action=${properties[2]}

	if [[ -z $additional ]] && [[ ${properties[3]} != "all" ]]; then
		break
	fi

	if [[ $required != "" ]] && [[ $required != ${properties[3]} ]]; then
		echo -e "\n${YELLOW}Required by '${properties[3]}' project${NC}"
	fi

	#if [[ $required != "" ]] && [[ $required != ${properties[3]} ]]; then
	#	echo -e "\n${YELLOW}Required by '${properties[3]}' project${NC}"
	#elif [[ ! -z $additional ]] && [[ ${properties[3]} != "all" ]]; then
	#	break
	#fi

	required=${properties[3]}
	min=${properties[4]}
	max=${properties[5]:-default}

	is_min_ok=false
	is_max_ok=false

	# first line is containing version
	actual=$($command $action 2>/dev/null | head -n 1)
	[[ $actual =~ $regex ]]
	actualVersion=${BASH_REMATCH[0]}

	if [ -z "${actualVersion}" ] && [ $required = "all" ]; then
		echo -e "${RED}✘ ${name} is not installed.${NC}"
		all_checks_passed=false
		continue
	fi
	
	if [ -z "${actualVersion}" ] && [ $required != "all" ]; then
		echo -e "${YELLOW}✘ ${name} is not installed.${NC}"
		continue
	fi

	if v_compare "$actualVersion" "$min"; then
		is_min_ok=true
	fi
	
	if v_compare "$max" "$actualVersion"; then
		is_max_ok=true
	fi
	
	if $is_min_ok && $is_max_ok; then
		echo -e "${GREEN}✔ ${name} version '${actualVersion}' is within the required range (${min} - ${max}).${NC}"
	elif [ $required != "all" ]; then
		echo -e "${YELLOW}✘ ${name} version '${actualVersion}' is outside of the range (${min} - ${max}).${NC}"
	else
		all_checks_passed=false
		echo -e "${RED}✘ ${name} version '${actualVersion}' is outside of the range (${min} - ${max}).${NC}"
	fi
done

echo -e ""
if [ "$all_checks_passed" = true ]; then
	echo -e "${GREEN}All mandatory checks passed successfully.${NC}"
else
	echo -e "${RED}Some checks failed. Please review the above messages.${NC}"
fi