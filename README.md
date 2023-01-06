# Build Tools for VMware Aria
This repository contains the source code for the whole toolchain supporting Infrastructure-as-Code for vRealize (Orchestrator and Automation).

## Documentation
- [Installation and Operations Documents](./doc/markdown)
- [Build and Debug the VS Code Extension](./vscode)

## How to build
```shell
mvn clean install -f maven/npmlib/pom.xml 
mvn clean install -f pom.xml 
mvn clean install -f maven/base-package/pom.xml
mvn clean install -f packages/pom.xml
mvn clean install -f maven/typescript-project-all/pom.xml
mvn clean install -f maven/repository/pom.xml
```

## Package dependencies

### Ubuntu

```bash
    sudo apt install libxml2-utils
```
