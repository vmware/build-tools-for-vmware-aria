#!/bin/bash


function generate_plugin_arguments(){
  IFS=$'\n'
  for POM_PATH in $(find maven/plugins -mindepth 2 -maxdepth 2 -name pom.xml -type f -not -path "maven/plugins/common/*"); do # Not recommended, will break on whitespace
    echo "Processing $POM_PATH..."
    GROUP_ID=$(xmllint --xpath '/*[local-name()="project"]/*[local-name()="parent"]/*[local-name()="groupId"]/text()' $POM_PATH) 
    echo "Group ID: $GROUP_ID"

    ARTIFACT_ID=$(xmllint --xpath '/*[local-name()="project"]/*[local-name()="artifactId"]/text()' $POM_PATH) 
    echo "Artifact ID: $ARTIFACT_ID"

    PLUGIN_VERSION=$(xmllint --xpath '/*[local-name()="project"]/*[local-name()="parent"]/*[local-name()="version"]/text()' $POM_PATH)
    echo "Version: $PLUGIN_VERSION"

    PLUGIN_FOLDER=$(dirname $POM_PATH)
    PLUGIN_FOLDER="${PLUGIN_FOLDER##*/}"
    echo "Plugin Folder: $PLUGIN_FOLDER"

    mvn help:describe -D"groupId=$GROUP_ID" -D"artifactId=$ARTIFACT_ID" -D"version=$PLUGIN_VERSION" -Ddetail --batch-mode -f $POM_PATH | awk '!/INFO/ && !/WARNING/{ print $0 }' | tail -n +2 > docs/versions/latest/General/Cheatsheets/$PLUGIN_FOLDER-plugin-arguments.md
  done
}

set -e

function printHelp()
{
    cat << EOF
DESCRIPTION
Releases a Maven project using the official Maven Release plugin.

OPTIONS
-v <version>    Specify the version to be released, e.g. 1.4.2. Default - the current dev version without SNAPSHOT.
-u <username>   User to be used for SCM.
-i <email>      Impersonate with the provided email. Sets the git user.email temporary.
-r <url>        Repository URL. Assumes the POM property to be set to ${scmDeveloperConnection}
-d              Generate documentation
-h              Prints this help.
EOF
}

#	OSX sed is not working well, so we need to install gnu-sed ( aka gsed )
function ssed() {
 if [[ "$OSTYPE" == "darwin"* ]]; then
  gsed "$@"
 else
  sed "$@"
 fi
}

while getopts ":v:n:u:i:r:dh" opt; do
  case $opt in
    v)
      version=$OPTARG
      ;;
    u)
      username=$OPTARG
      ;;
    i)
      impersonate=$OPTARG
      ;;
    r)
      repo=$OPTARG
      ;;
    d)
      generate_plugin_arguments
      exit 0
      ;;
    h)
      printHelp
      exit 0
      ;;
    \?)
      echo "Invalid option: -$OPTARG" >&2
      exit 1
      ;;
    :)
      echo "Option -$OPTARG requires an argument." >&2
      exit 1
      ;;
  esac
done

maven_version=$(mvn -q \
    -Dexec.executable="echo" \
    -Dexec.args='${project.version}' \
    --non-recursive \
    --batch-mode \
    org.codehaus.mojo:exec-maven-plugin:1.6.0:exec)

ver=${version:-$maven_version}
ver=$(echo $ver | sed -e "s/-SNAPSHOT$//g")
workingDir=$(pwd)

# impersonate
if [[ $impersonate ]]; then
  original_user_email=$(git config --get user.email)
  echo Impersonating $impersonate, Original: $original_user_email
  git config user.email $impersonate
fi
if [[ $username ]]; then
    usernameArg=-Dusername=$username
fi

if [[ $repo ]]; then
  developerConnectionArg=-DscmDeveloperConnection=scm:git:$repo
fi

documentation() {
	echo "Generating Documentation"
	DOC_VERSION="v$ver"
	DOC_DATE_VERSION="$DOC_VERSION - $(date +"%d %b %Y")"
	VERSIONS_FOLDER="./docs/versions"
	LATEST_VERSION_FOLDER="$VERSIONS_FOLDER/latest"
	LATEST_VERSION_RELEASE_MD="$LATEST_VERSION_FOLDER/Release.md"
	NEW_VERSION_FOLDER="$VERSIONS_FOLDER/$DOC_VERSION"
	NEW_VERSION_RELEASE_MD="$NEW_VERSION_FOLDER/Release.md"
	NEW_VERSION_README_MD="$NEW_VERSION_FOLDER/README.md"
	VERSION_PLACEHOLDER_TEXT="VERSION_PLACEHOLDER DO NOT DELETE"

#	Create a new version folder
	[ -d "$NEW_VERSION_FOLDER" ] && rm -rf $NEW_VERSION_FOLDER
	mkdir $NEW_VERSION_FOLDER

#	Copy latest to the new release folder
	cp -rf $LATEST_VERSION_FOLDER/* $NEW_VERSION_FOLDER

#	Insert the latest version in Release.md and README.md for the new release
	ssed -i "/$VERSION_PLACEHOLDER_TEXT/c\\# $DOC_VERSION" $NEW_VERSION_RELEASE_MD

	ssed -i "/$VERSION_PLACEHOLDER_TEXT/c\\# $DOC_VERSION" $NEW_VERSION_README_MD

#	Remove comments from the Release file ( potential future improvements remove all comments recursively in all files )
	ssed -i '/^\[\/\/\]:\s#/d' $NEW_VERSION_RELEASE_MD

#	Delete latest Release.md and replace it with a blank one
	rm -rf $LATEST_VERSION_RELEASE_MD
	cp -rf ./docs/Templates/Release.md $LATEST_VERSION_RELEASE_MD

	printf "## $DOC_DATE_VERSION\n\n$(cat CHANGELOG.md)" > CHANGELOG.md

	echo "Done Generating Documentation"

  generate_plugin_arguments
}

# check everything is commited
git add .
uncommited_files=$(git diff --cached --numstat | wc -l)
if [ $uncommited_files -gt 0 ]; then
    echo CANNOT RELEASE WHILE HAVING UNCOMMITED CHANGES
    exit 1
else
    #RELEASE

    # Handle Documentation and CHANGELOG.md
    documentation


    # npmlib is a dependency of vropkg and polyglotpkg and it needs to be handled separately
    cd maven/npmlib
    mvn versions:set -DnewVersion=$ver  -DgenerateBackupPoms=false -DprocessAllModules=true -Dmaven.repo.local=$workingDir/.m2/repository -s $workingDir/.m2/settings.xml -DoldVersion='*' --batch-mode
    mvn versions:set-property -Dproperty=iac.version -DnewVersion=$ver  -DgenerateBackupPoms=false -Dmaven.repo.local=$workingDir/.m2/repository -s $workingDir/.m2/settings.xml --batch-mode
    mvn install -DskipTests -Dmaven.repo.local=$workingDir/.m2/repository -s $workingDir/.m2/settings.xml --batch-mode
    cd ../../

    mvn versions:set -DnewVersion=$ver -DgenerateBackupPoms=false -DprocessAllModules=true -Dmaven.repo.local=$workingDir/.m2/repository -s $workingDir/.m2/settings.xml -DoldVersion='*' --batch-mode
    mvn install -DskipTests -Dmaven.repo.local=$workingDir/.m2/repository -s $workingDir/.m2/settings.xml --batch-mode

    cd maven/base-package
    mvn versions:set -DnewVersion=$ver  -DgenerateBackupPoms=false -DprocessAllModules=true -Dmaven.repo.local=$workingDir/.m2/repository -s $workingDir/.m2/settings.xml -DoldVersion='*' --batch-mode
    mvn versions:update-property -Dproperty=iac.version -DnewVersion=$ver  -DgenerateBackupPoms=false -Dmaven.repo.local=$workingDir/.m2/repository -s $workingDir/.m2/settings.xml --batch-mode
    mvn install -DskipTests -Dmaven.repo.local=$workingDir/.m2/repository -s $workingDir/.m2/settings.xml --batch-mode

    cd ../npmconv
    mvn versions:set -DnewVersion=$ver  -DgenerateBackupPoms=false -DprocessAllModules=true -Dmaven.repo.local=$workingDir/.m2/repository -s $workingDir/.m2/settings.xml -DoldVersion='*' --batch-mode
    mvn versions:set-property -Dproperty=iac.version -DnewVersion=$ver  -DgenerateBackupPoms=false -Dmaven.repo.local=$workingDir/.m2/repository -s $workingDir/.m2/settings.xml --batch-mode
    mvn install -DskipTests -Dmaven.repo.local=$workingDir/.m2/repository -s $workingDir/.m2/settings.xml --batch-mode

    cd ../polyglot
    mvn versions:set-property -Dproperty=iac.version -DnewVersion=$ver  -DgenerateBackupPoms=false -Dmaven.repo.local=$workingDir/.m2/repository -s $workingDir/.m2/settings.xml --batch-mode

    cd ../../package-installer
    mvn versions:set-property -Dproperty=iac.version -DnewVersion=$ver  -DgenerateBackupPoms=false -Dmaven.repo.local=$workingDir/.m2/repository -s $workingDir/.m2/settings.xml --batch-mode
    mvn install -DskipTests -Dmaven.repo.local=$workingDir/.m2/repository -s $workingDir/.m2/settings.xml --batch-mode


		cd ../packages/ecmascript
		mvn versions:use-latest-versions -DallowSnapshots=false -DgenerateBackupPoms=false -Dincludes="com.vmware.pscoe.*" -Dmaven.repo.local=$workingDir/.m2/repository -s $workingDir/.m2/settings.xml --batch-mode

    cd ../
    mvn versions:set -DnewVersion=$ver  -DgenerateBackupPoms=false -DprocessAllModules=true -Dmaven.repo.local=$workingDir/.m2/repository -s $workingDir/.m2/settings.xml -DoldVersion='*' --batch-mode
    mvn versions:update-parent -DgenerateBackupPoms=false -Dmaven.repo.local=$workingDir/.m2/repository -s $workingDir/.m2/settings.xml --batch-mode
    mvn versions:use-latest-versions -DallowSnapshots=false -DgenerateBackupPoms=false -Dincludes="com.vmware.pscoe.iac:vrotsc" -Dmaven.repo.local=$workingDir/.m2/repository -s $workingDir/.m2/settings.xml --batch-mode
    mvn install -DskipTests -Dmaven.repo.local=$workingDir/.m2/repository -s $workingDir/.m2/settings.xml --batch-mode

    cd ../maven/typescript-project-all
    mvn versions:update-parent -DgenerateBackupPoms=false -Dmaven.repo.local=$workingDir/.m2/repository -s $workingDir/.m2/settings.xml --batch-mode
    mvn versions:use-latest-versions -DallowSnapshots=false -DgenerateBackupPoms=false -Dincludes="com.vmware.pscoe.*" -Dmaven.repo.local=$workingDir/.m2/repository -s $workingDir/.m2/settings.xml --batch-mode
    mvn install -DskipTests -Dmaven.repo.local=$workingDir/.m2/repository -s $workingDir/.m2/settings.xml --batch-mode

    cd ../repository
    mvn versions:set -DnewVersion=$ver  -DgenerateBackupPoms=false -DprocessAllModules=true -Dmaven.repo.local=$workingDir/.m2/repository -s $workingDir/.m2/settings.xml -DoldVersion='*' --batch-mode

		cd ../../typescript/vropkg
		mvn versions:update-parent -DgenerateBackupPoms=false -Dmaven.repo.local=$workingDir/.m2/repository -s $workingDir/.m2/settings.xml --batch-mode

    cd test/com.vmware.pscoe.toolchain-expand
    mvn versions:update-parent -DallowSnapshots=true -DgenerateBackupPoms=false -Dmaven.repo.local=$workingDir/.m2/repository -s $workingDir/.m2/settings.xml --batch-mode
    cd ../../

		cd ../polyglotpkg
		mvn versions:update-parent -DgenerateBackupPoms=false -Dmaven.repo.local=$workingDir/.m2/repository -s $workingDir/.m2/settings.xml --batch-mode

    cd ../../vro-types
    mvn versions:update-parent -DallowSnapshots=true -DgenerateBackupPoms=false -Dmaven.repo.local=$workingDir/.m2/repository -s $workingDir/.m2/settings.xml --batch-mode
    cd ../

		cd packages/polyglot-wrapper
		mvn versions:use-latest-versions -DallowSnapshots=false -DgenerateBackupPoms=false -Dincludes="com.vmware.pscoe.*" -Dmaven.repo.local=$workingDir/.m2/repository -s $workingDir/.m2/settings.xml --batch-mode
		cd ../../

		

    git add .
    git commit -m "(release) v$ver"
    git tag v$ver
    git push --tags
    git push


    # NEXT DEV ITERATION
    # npmlib is a dependency of vropkg and polyglotpkg and it needs to be handled separately
    cd maven/npmlib
    mvn versions:set -DnextSnapshot=true -DgenerateBackupPoms=false -DprocessAllModules=true -Dmaven.repo.local=$workingDir/.m2/repository -s $workingDir/.m2/settings.xml -DoldVersion='*' --batch-mode
    # get next snapshot version after it is generated by -DnextSnapshot=true
		next_version=$(mvn -q \
        -Dexec.executable="echo" \
        -Dexec.args='${project.version}' \
        --non-recursive \
        --batch-mode \
        org.codehaus.mojo:exec-maven-plugin:1.6.0:exec)
		mvn versions:set-property -Dproperty=iac.version -DnewVersion=$next_version  -DgenerateBackupPoms=false -Dmaven.repo.local=$workingDir/.m2/repository -s $workingDir/.m2/settings.xml -DallowSnapshots=true --batch-mode
    mvn install -DskipTests -Dmaven.repo.local=$workingDir/.m2/repository -s $workingDir/.m2/settings.xml --batch-mode
    cd ../../

    mvn versions:set -DnextSnapshot=true -DgenerateBackupPoms=false -DprocessAllModules=true -Dmaven.repo.local=$workingDir/.m2/repository -s $workingDir/.m2/settings.xml -DoldVersion='*' --batch-mode
    mvn install -DskipTests -Dmaven.repo.local=$workingDir/.m2/repository -s $workingDir/.m2/settings.xml --batch-mode

    cd maven/base-package
    mvn versions:set -DnextSnapshot=true -DgenerateBackupPoms=false -DprocessAllModules=true -Dmaven.repo.local=$workingDir/.m2/repository -s $workingDir/.m2/settings.xml -DoldVersion='*' --batch-mode
    mvn versions:update-property -Dproperty=iac.version -DnewVersion=$next_version  -DgenerateBackupPoms=false -Dmaven.repo.local=$workingDir/.m2/repository -s $workingDir/.m2/settings.xml -DallowSnapshots=true --batch-mode
    mvn install -DskipTests -Dmaven.repo.local=$workingDir/.m2/repository -s $workingDir/.m2/settings.xml --batch-mode

    cd ../npmconv
    mvn versions:set-property -Dproperty=iac.version -DnewVersion=$next_version  -DgenerateBackupPoms=false -Dmaven.repo.local=$workingDir/.m2/repository -s $workingDir/.m2/settings.xml -DallowSnapshots=true --batch-mode
    mvn install -DskipTests -Dmaven.repo.local=$workingDir/.m2/repository -s $workingDir/.m2/settings.xml --batch-mode

    cd ../polyglot
    mvn versions:set-property -Dproperty=iac.version -DnewVersion=$next_version  -DgenerateBackupPoms=false -Dmaven.repo.local=$workingDir/.m2/repository -s $workingDir/.m2/settings.xml -DallowSnapshots=true --batch-mode

		cd ../../packages/ecmascript
		mvn versions:use-latest-versions -DallowSnapshots=true -DgenerateBackupPoms=false -Dincludes="com.vmware.pscoe.*" -Dmaven.repo.local=$workingDir/.m2/repository -s $workingDir/.m2/settings.xml --batch-mode

    cd ../
    mvn versions:set -DnextSnapshot=true -DgenerateBackupPoms=false -DprocessAllModules=true -Dmaven.repo.local=$workingDir/.m2/repository -s $workingDir/.m2/settings.xml -DoldVersion='*' --batch-mode
    mvn versions:update-parent -DallowSnapshots=true -DgenerateBackupPoms=false -Dmaven.repo.local=$workingDir/.m2/repository -s $workingDir/.m2/settings.xml --batch-mode
    mvn versions:use-latest-versions -DallowSnapshots=true -DgenerateBackupPoms=false -Dincludes="com.vmware.pscoe.iac:vrotsc" -Dmaven.repo.local=$workingDir/.m2/repository -s $workingDir/.m2/settings.xml --batch-mode
    mvn install -DskipTests -Dmaven.repo.local=$workingDir/.m2/repository -s $workingDir/.m2/settings.xml --batch-mode

    cd ../maven/typescript-project-all
    mvn versions:update-parent -DallowSnapshots=true -DgenerateBackupPoms=false -Dmaven.repo.local=$workingDir/.m2/repository -s $workingDir/.m2/settings.xml --batch-mode
    mvn versions:use-latest-versions -DallowSnapshots=true -DgenerateBackupPoms=false -Dincludes="com.vmware.pscoe.*" -Dmaven.repo.local=$workingDir/.m2/repository -s $workingDir/.m2/settings.xml --batch-mode
    mvn install -DskipTests -Dmaven.repo.local=$workingDir/.m2/repository -s $workingDir/.m2/settings.xml --batch-mode

    cd ../repository
    mvn versions:set -DnextSnapshot=true -DgenerateBackupPoms=false -DprocessAllModules=true -Dmaven.repo.local=$workingDir/.m2/repository -s $workingDir/.m2/settings.xml -DoldVersion='*' --batch-mode

		cd ../../typescript/vropkg
		mvn versions:update-parent -DallowSnapshots=true -DgenerateBackupPoms=false -Dmaven.repo.local=$workingDir/.m2/repository -s $workingDir/.m2/settings.xml --batch-mode

    cd test/com.vmware.pscoe.toolchain-expand
    mvn versions:update-parent -DallowSnapshots=true -DgenerateBackupPoms=false -Dmaven.repo.local=$workingDir/.m2/repository -s $workingDir/.m2/settings.xml --batch-mode
    cd ../../

		cd ../polyglotpkg
		mvn versions:update-parent -DallowSnapshots=true -DgenerateBackupPoms=false -Dmaven.repo.local=$workingDir/.m2/repository -s $workingDir/.m2/settings.xml --batch-mode

    cd ../../vro-types
    mvn versions:update-parent -DallowSnapshots=true -DgenerateBackupPoms=false -Dmaven.repo.local=$workingDir/.m2/repository -s $workingDir/.m2/settings.xml --batch-mode
    cd ../

		cd packages/polyglot-wrapper
		mvn versions:use-latest-versions -DallowSnapshots=true -DgenerateBackupPoms=false -Dincludes="com.vmware.pscoe.*" -Dmaven.repo.local=$workingDir/.m2/repository -s $workingDir/.m2/settings.xml --batch-mode
		cd ../../

    git add .
    git commit -m "(release) start development iteration v$next_version"
    git push
fi

# reset impersonation
if [[ $impersonate ]]; then
  echo Setting user.email back to $original_user_email
  git config user.email $original_user_email
fi
