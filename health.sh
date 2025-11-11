GREEN="\033[0;32m"
RED="\033[0;31m"
NC="\033[0m"

all_checks_passed=true
regex="[[:digit:]]+\.[[:digit:]]+\.[[:digit:]]+"

# OpenSSL is not following the guidelines
declare -a prerequisites=(
    "Java     java     --version  17     24"
	"Maven    mvn      --version  3.2"
    "Node.js  nodejs   --version  12     24"
	"Python   python   --version  3.2    3.14"
	"Pip      pip      --version  25.0   26.0"
	"OpenSSL  openssl  version    10     17.0"
)

v_compare() { 
	# Returns 0 if $1 >= $2, 1 otherwise
	printf '%s\n%s\n' "$2" "$1" | sort -C -V
}

echo "Starting Build Tools for VMware Aria Checks..."

for prerequisite in "${prerequisites[@]}"; do
	# uses default whitespace IFS
	read -a properties <<< "$prerequisite"

	name=${properties[0]}
	command=${properties[1]}
	action=${properties[2]}
	min=${properties[3]}
	max=${properties[4]:-default}

	# first line is containing version
	actual=$($command $action 2>/dev/null | head -n 1) 

	if [[ $actual =~ $regex ]]; then
		actualVersion=${BASH_REMATCH[0]}
		is_min_ok=false
		is_max_ok=false
		
		if v_compare "$actualVersion" "$min"; then
			is_min_ok=true
		fi
		
		if v_compare "$max" "$actualVersion"; then
			is_max_ok=true
		fi

		if $is_min_ok && $is_max_ok; then
			echo -e "${GREEN}✔ ${name} version '${actualVersion}' is within the required range (${min} - ${max}).${NC}"
		else
			all_checks_passed=false
			echo -e "${RED}✘ ${name} version '${actualVersion}' is outside of the range (${min} - ${max}).${NC}"
		fi
	else
		echo -e "${RED}✘ ${name} is not installed.${NC}"
		all_checks_passed=false
	fi
done

if [ "$all_checks_passed" = true ]; then
	echo -e "${GREEN}All checks passed successfully.${NC}"
else
	echo -e "${RED}Some checks failed. Please review the above messages.${NC}"
fi
