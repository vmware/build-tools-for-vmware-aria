# Build Tools for VMware Aria
This repository contains the source code for the Build Tools for VMware Aria, formally knows as vRealize Build Tools.
Build Tools for VMware Aria provides tools to develop, manage and deploy content for the following VMware products: 
- VMware Aria Automation
- VMware Aria Automation Orchestrator
- VMware Aria Operations
- VMware Aria Suite Lifecycle
- VMware Cloud Director


## Documentation
- [Installation and Operations Documents](docs/archive/doc/markdown)
- [Workstation Setup Guide](docs/archive/doc/markdown/setup-workstation.md)

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
- npm: 6.14.13
- node: 16.x.x
- maven: 3.6.3
