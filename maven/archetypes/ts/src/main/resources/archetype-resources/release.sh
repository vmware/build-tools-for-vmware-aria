#!/bin/bash

set -e

function printHelp()
{
    cat << EOF
DESCRIPTION
Releases a Maven project using the official Maven Release plugin.

OPTIONS
-v <version>    Specify the version to be released, e.g. 1.4.2. Default - the current dev version without SNAPSHOT.
-n <version>    Specify the next dev version. Default - maintenance increment of the released one with SNAPSHOT
-u <username>   User to be used for SCM.
-i <email>      Impersonate with the provided email. Sets the git user.email temporary.
-r <url>        Repository URL. Assumes the POM property to be set to ${scmDeveloperConnection}
-h              Prints this help.
EOF
}

while getopts ":v:n:u:i:r:h" opt; do
  case $opt in
    v)
      version=$OPTARG
      ;;
    n)
      next=$OPTARG
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
    org.codehaus.mojo:exec-maven-plugin:1.6.0:exec)

ver=${version:-$maven_version}
ver=$(echo $ver | sed -e "s/-SNAPSHOT$//g")

# impersonate
if [[ $impersonate ]]; then
  original_user_email=$(git config --get user.email)
  echo Impersonating $impersonate, Original: $original_user_email
  git config user.email $impersonate
fi
if [[ $username ]]; then
    usernameArg=-Dusername=$username
fi

if [[ $next ]]; then
    nextVersionArg=-DdevelopmentVersion=$next-SNAPSHOT
fi

if [[ $repo ]]; then
  developerConnectionArg=-DscmDeveloperConnection=scm:git:$repo
fi

echo Executing Maven Release:
echo mvn release:prepare --batch-mode -DreleaseVersion=$ver $nextVersionArg $usernameArg $developerConnectionArg -Dresume=false -DscmCommentPrefix="(release) "  -DpushChanges=false
mvn release:prepare --batch-mode -DreleaseVersion=$ver $nextVersionArg $usernameArg $developerConnectionArg -Dresume=false -DscmCommentPrefix="(release) "  -DpushChanges=false
git push origin $(git rev-list -n 1 v$ver):$(git rev-parse --abbrev-ref HEAD) --follow-tags
sleep 5
git push

# reset impersonation
if [[ $impersonate ]]; then
  echo Setting user.email back to $original_user_email
  git config user.email $original_user_email
fi