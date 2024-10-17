# Build Tools for VMware Aria for ABX Projects

Before you continue with this section validate that all of the prerequisites are met.

## Prerequisites

- Install and Configure [Build Tools for VMware Aria System](setup-workstation-maven.md)

## Usage

ABX Project is a development project representation of ABX package content.

### Create New ABX Project

**Build Tools for VMware Aria** provides ready to use project templates (*maven archetypes*).

To create a new ABX project from archetype use the following commands for the respective runtime:

```Bash
# NodeJS
mvn archetype:generate \
    -DinteractiveMode=false \
    -DarchetypeGroupId=com.vmware.pscoe.polyglot.archetypes \
    -DarchetypeArtifactId=package-polyglot-archetype \
    -DarchetypeVersion=<iac_for_vrealize_version> \
    -DgroupId=local.corp.it.cloud \
    -DartifactId=abx \
    -Druntime=nodejs \
    -Dtype=abx
 
# Python
mvn archetype:generate \
    -DinteractiveMode=false \
    -DarchetypeGroupId=com.vmware.pscoe.polyglot.archetypes \
    -DarchetypeArtifactId=package-polyglot-archetype \
    -DarchetypeVersion=<iac_for_vrealize_version> \
    -DgroupId=local.corp.it.cloud \
    -DartifactId=abx \
    -Druntime=python \
    -Dtype=abx
 
# PowerShell
mvn archetype:generate \
    -DinteractiveMode=false \
    -DarchetypeGroupId=com.vmware.pscoe.polyglot.archetypes \
    -DarchetypeArtifactId=package-polyglot-archetype \
    -DarchetypeVersion=<iac_for_vrealize_version> \
    -DgroupId=local.corp.it.cloud \
    -DartifactId=abx \
    -Druntime=powershell \
    -Dtype=abx
```

**Note**: *The specified <iac_for_vrealize_version> should be minimum 2.11.2*

The generated project from the archetype is specific to the runtime, i.e. the src directory will contain .py files for Python projects, .ts files for NodeJS projects and .ps1 files for PowerShell projects.

The result of this command will produce the following project file structure (example for NodeJS):

```txt
.
├── README.md
├── handler.debug.yaml
├── license_data
│   ├── licenses.properties
│   └── tp_license
│       ├── header.txt
│       └── license.txt
├── package.json
├── pom.xml
├── release.sh
├── src
│   └── handler.ts
└── tsconfig.json
```

### Building

You can build any ABX project from sources using Maven:

```bash
mvn clean package
```

This will produce an ABX package with the groupId, artifactId and version specified in the pom. For example:

```xml
<groupId>local.corp.it.cloud</groupId>
<artifactId>abx</artifactId>
<version>1.0.0-SNAPSHOT</version>
<packaging>abx</packaging>
```

will result in **local.corp.it.cloud.abx-1.0.0-SNAPSHOT.abx** generated in the target folder of your project.

#### TypeScript project references (Node.js)

If you have Node.js ABX projects that must depend on a shared (and locally-developed) Node.js library, you can use [TypeScript project references](https://www.typescriptlang.org/docs/handbook/project-references.html) to make sure your Node.js library will be built together with the ABX action (and will be included in the produced bundle).

For example, you can have the following project structure

```txt
.
├── my-lib/
│   ├── src/
│   ├── package.json
│   ├── tsconfig.json # inherits the base ../tsconfig.json
│   ├── pom.xml       # parent should be `com.vmware.pscoe.npm:lib`
├── abx1/
│   ├── package.json  # must have `dependencies: [ "my-lib": "file:../my-lib" ]`
│   ├── tsconfig.json # inherits the base ../tsconfig.json
│   ├── pom.xml       # parent should be `com.vmware.pscoe.serverless:serverless-project`
├── abx2/
│   ├── package.json  # must have `dependencies: [ "my-lib": "file:../my-lib" ]`
│   ├── tsconfig.json # inherits the base ../tsconfig.json
│   ├── pom.xml       # parent should be `com.vmware.pscoe.serverless:serverless-project`
├── pom.xml           # 3 modules - my-lib, abx1 and abx2
└── tsconfig.json     # base tsconfig, must have `composite: true` and `paths` proeprty that maps my-lib to its src folder

### my-lib/tsconfig.json
{
  "extends": "../tsconfig.json",
  "compilerOptions": {
    "baseUrl": "./",
    "rootDir": "src",
    "outDir": "out"
  },
  "include": ["src"]
}

### abx1/package.json
...
  "dependencies": {
    "my-lib": "file:../my-lib"
  },
...

### abx1/tsconfig.json
{
  "extends": "../tsconfig.json",
  "compilerOptions": {
    "baseUrl": "./",
    "rootDir": "src",
    "outDir": "out"
  },
  "include": [ "src" ],
  "references": [ { "path": "../my-lib"} ]
}

### tsconfig.json
{
  "compilerOptions": {
    "baseUrl": "./",
    "paths": { "my-lib": ["my-lib/src"] },
    "composite": true,
    ...other compileOptions, shared by all projects inheriting from this tsconfig 
  },
  "include": [],
}

### pom.xml
<?xml version="1.0" encoding="UTF-8"?>
<project>
  <groupId>local.corp.it.cloud</groupId>
  <artifactId>my-abx-project</artifactId>
  <version>1.0.0-SNAPSHOT</version>
  <packaging>pom</packaging>

  <modules>
    <module>my-lib</module>
    <module>abx1</module>
    <module>abx2</module>
  </modules>
</project>
```

A `mvn package` command executed in the root folder of a project (with the above structure) will take care of building the ABX actions and all their project references in the correct order.

### Configuration

The ABX project uses the same configuration definition and semantics as the **VRA-NG** project. Refer to the configuration definition of the **VRA-NG** project. The rationale for this is that the ABX service is embedded within the larger vRA8 ecosystem and any application interaction uses unified authentication and authorization methodology, as well as locating the target scope for content distribution, e.g. projects and organizations.

### Pull

ABX content pulling is not supported yet.

### Push

To deploy the code developed in the local project or checked out from source control to a live server, you can use the `vrealize:push` command:

```bash
mvn package vrealize:push -Pcorp-env 
```

This will build the package and deploy it to the environment described in the `corp-env` profile.

### Release

To release a an ABX action uploaded on a live server, you can use the `vrealize:release` command:

```bash
mvn vrealize:release -Pcorp-env -Dvrang.version=1
```

The parameter `vrang.version` is required. The possible values of the parameter are: - `vrang.version=auto` - use auto-versioning with next version being generated by inferring the version pattern  and generating a new version following the same sequence (refer to the ABX auto-versioning section below).

- `vrang.version=project` - use the version set in the project.
- `vrang.version=<version>` - use an explicit version.

#### ABX auto-versioning

When releasing an ABX action to a vRA server that contains previously released action with the same name as the one being released, a new version will be created and released.

When creating a new version and the versioning strategy is set to 'auto', the new version will be auto-generated based on the latest version of the action. The following version formats are supported with their respective incrementing rules:

| Latest version | New version         | Incrementing rules                                         |
|----------------|---------------------|------------------------------------------------------------|
| 1              | 2                   | Increment major version                                    |
| 1.0            | 1.1                 | Major and minor version - increment the minor              |
| 1.0.0          | 1.0.1               | Major, minor and patch version - incrementing the patch    |
| 1.0.0-alpha    | 2020-05-27-10-10-43 | Arbitrary version - generate a new date-time based version |

### Clean Up

In order to clean up auto-generated content, you can use the following Maven goal:

```bash
mvn clean
```

This will remove the `target`, `out` and `dist` directories of the project.

### Troubleshooting

- If Maven error does not contain enough information rerun it with *-e* debug flag. This will output the stack trace at the point where the error is encountered.

  ```bash
  mvn -e <rest of the command>
  ```

- Additionally, debug information can be really helpful when troubleshooting a particular scenario. In order to increase the verbosity of the logs, you can use the *-X* debug flag.

  ```bash
  mvn -X <rest of the command>
  ```

- Sometimes Maven might cache old artifacts. Force fetching new artifacts with *-U*.

  Alternatively remove `<home>/.m2/repository` folder.

  ```bash
  mvn -U <rest of the command>
  ```
