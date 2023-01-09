# v2.27.0

## Breaking Changes



## Deprecations


### Removed package-typescript-autogen-archetype maven archetype
The archetype "package-typescript-autogen-archetype" is having a hard dependency on a CoE Internal library. Since it is not possible to keep that dependency when opensourcing Build Tools for VMware Aria, we need to remove the archetype from Build Tools for VMware Aria and place it in its own separate repository.

#### Previous Behavior
The package-typescript-autogen-archetype was part of the big toolchain package, even though it was not possible to use it outside VMware.

#### New Behavior
The package-typescript-autogen-archetype and the typescript-autogen-project base packages are moved to a new `ts-autogen` repository which will be maintained separately.

#### Relevant Documentation:
**NONE**

## Features:
### *Added an archetype for a multi-module project*

#### *Command for archetype generation*
**Windows OS**
```commandline
    mvn archetype:generate "-DinteractiveMode=false"
        "-DarchetypeGroupId=com.vmware.pscoe.o11n.archetypes"
        "-DarchetypeArtifactId=package-ts-vra-ng-archetype"
        "-DgroupId=<group-id>"
        "-DartifactId=<artifact-id>"
        "-DlicenseUrl=<license-url>"
        "-DlicenseHeader=<license-header>"
        "-DlicenseTechnicalPreview=false"
```
**Linux/Mac OS**
```commandline
    mvn archetype:generate -DinteractiveMode=false \
        -DarchetypeGroupId=com.vmware.pscoe.o11n.archetypes \
        -DarchetypeArtifactId=package-ts-vra-ng-archetype \
        -DgroupId=<group-id> \
        -DartifactId=<artifact-id> \
        -DlicenseUrl=<license-url> \
        -DlicenseHeader=<license-header> \
        -DlicenseTechnicalPreview=false
```

#### Relevant Documentation:
**NONE**


## Improvements

### Installer Skips Steps Under Mac OS

#### Previous Behavior
When we run the installer from a folder different from the package bundle bin folder, numerous steps include the "Import vRO packages" are skipped

#### New Behavior
When we run the installer from a folder different from the package bundle bin folder, we follow all steps of the interactive mode.

#### Relevant Documentation:
[appassembler-maven-plugin](https://www.mojohaus.org/appassembler/appassembler-maven-plugin/usage-script.html)

### Interactive Installer Doesn't Ask if vRO is embedded

#### Previous Behavior
When running the installer script in interactive mode to import vRA and vRO content simultaneously, the script doesn't show a prompt to ask whether vRO is embedded. And in the case of vRA Cloud, vRO cannot be embedded, which makes it impossible to use the interactive mode of the script.

#### New Behavior
When we run the installer to import vRA and vRO content simultaneously, the script shows a prompt to ask whether vRO is embedded. 
If the answer is yes, the vRealize Automation NG Configuration is run. Otherwise, the script asks vRealize Orchestrator Configuration set of questions.

#### Relevant Documentation:
**NONE**

### Add support for the following ABX properties: memoryLimitMb, timeoutSec, provider(Faas provider), shared(Share with all projects in this organization), inputSecrets(Secret), inputConstants(Action constant)

#### Previous Behavior
Properties: memoryLimitMb, timeoutSec, provider(Faas provider), shared(Share with all projects in this organization), inputSecrets(Secret), inputConstants(Action constant) are ignored during the installation process.

#### New Behavior
Correct value will be taken from package.json file for properties: memoryLimitMb, timeoutSec, provider(Faas provider), shared(Share with all projects in this organization), inputSecrets(Secret), inputConstants(Action constant). 
Possible values for property **Faas provider** are: "aws", "azure", "on-prem". File **package.json** should be placed to the root of the project folder.

**package.json**
```
{
	"name": "nic.abx",
	"version": "1.0.1-SNAPSHOT",
	"description": "Find Tags Action",
	"private": false,
	"scripts": {
		"build": "polyglotpkg --env abx",
		"clean": "run-script-os",
		"clean:win32": "rmdir /S /Q out dist tmp || echo Nothing to clean",
		"clean:default": "rm -Rf out dist tmp",
		"test": "echo \"Error: no test specified\""
	},
	"keywords": [
		"polyglot",
		"package",
		"abx",
		"python"
	],
	"author": "VMware WWCoE",
	"license": "VMware Confidential",
	"devDependencies": {
		"@types/node": "^14.0.13",
		"@vmware-pscoe/polyglotpkg": "https://artifactory-fqdn/artifactory/com/vmware/pscoe/iac/polyglotpkg/2.18.1/polyglotpkg-2.18.1.tgz",
		"@vmware-pscoe/vropkg": "https://artifactory-fqdn/artifactory/com/vmware/pscoe/iac/vropkg/2.18.1/vropkg-2.18.1.tgz",
		"run-script-os": "^1.1.6",
		"sinon": "^11.1.2",
		"ts-node": "^8.10.2",
		"typescript": "^3.9.5"
	},
	"dependencies": {},
	"platform": {
		"runtime": "python",
		"action": "findTags",
		"tags": [],
		"entrypoint": "handler.handler",
		"base": "out",
		"memoryLimitMb": 79,
		"timeoutSec": 22,
		"provider": "azure"
	},
	"abx": {
		"shared": true,
		"inputSecrets": [
			"Secret1",
			"Secret2"
		],
		"inputConstants": [
			"Constant1",
			"Constant2"
		],
		"inputs": {
			"tagKey": ""
		}
	},
	"files": [
		"out",
		"!package.json"
	]
}
```

#### Relevant Documentation:
**NONE**

### NPE in case user provides non-existing project name during installation 

#### Previous Behavior
We get a null pointer exception if we specify a non-existing project name during the installation process.

#### New Behavior
We get an error that the specified project doesn't exist, and information with a list of existing project if we specify the name of non-existing project during the installation process.

#### Relevant Documentation:
**NONE**

### Ability to Skip the node_modules reinstallation
Added the Ability to Skip the node_modules cleaning and installation

#### Previous Behavior
The "mvn clean" command (for typescript projects) used to clean/delete the node_modules folder and "mvn package" or "mvn install" command creates the node_modules and downloads node-deps every time. This extends build time.

#### New Behavior
Adding "skipInstallNodeDeps=true" flag to the mvn commands , skips the deletion and re installation of node-deps.

#### Relevant Documentation:
[skipInstallNodeDeps](Components/Archetypes/typescript/General/Getting%20Started.md#skipInstallNodeDeps)

### Update documentation to use approved project name **Build Tools for VMware Aria**
Update all documentation references and guideliness to use **Build Tools for VMware Aria** as name when refering to the current project. Code references (e.g. temp folder path, package paths, e2e test paths) are not updated. Only code comments are updated.

#### Previous Behavior
Project references used **vRealize Build Tools** or **vRBT** as name.

#### New Behavior
Project references used `Build Tools for VMware Aria` or `Build Tools for Aria` as name. No abbreviations or acronyms are used

### Blueprint release will try a date formatted version if it fails on MAJOR.MINOR.PATCH

#### Previous Behavior
When changes are detected on blueprint imports and versions are not ignored vRBT tries to release a new blueprint version. If
however, the exported versions were stored in reverse order and imported that way, new release failed due to trying to release 
an existing version.

#### New Behavior
Added error handling that will try to release a date formatted version (which shouldn't have any duplicates on the server) in
case the MAJOR.MINOR.PATCH fails.

#### Relevant Documentation:
[importing](Components/Archetypes/VRA%208.x/Components/Blueprints.md#importing)

### vRA authentication with refresh token

#### Previous Behavior
Authentication is possible only by specifing username and password in profile configuration in settings.xml

#### New Behavior
Authentication is possible by specifing either refresh token or username and password in profile configuration in settings.xml
```xml
<profile>
	<id>corp-dev</id>
	<properties>
		<!--vRO Connection-->
		<vro.host>vra-l-01a.corp.local</vro.host>
		<vro.port>443</vro.port>
		<vro.auth>vra</vro.auth>
		<vro.refresh.token>{{refreshToken}}</vro.refresh.token>
	</properties>
</profile>
```

## Upgrade procedure:

