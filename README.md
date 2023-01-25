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
1. Update ~/.m2/settings.xml with:
```xml
<profiles>
    <profile>
        <id>packaging</id>
        <properties>
            <keystoreGroupId>com.vmware.pscoe.build</keystoreGroupId>
            <keystoreArtifactId>keystore.example</keystoreArtifactId>
            <keystoreVersion>1.0.0</keystoreVersion>
            <vroPrivateKeyPem>target/${keystoreArtifactId}-${keystoreVersion}/private_key.pem</vroPrivateKeyPem>
            <vroCertificatePem>target/${keystoreArtifactId}-${keystoreVersion}/cert.pem</vroCertificatePem>
            <vroKeyPass>VMware1!</vroKeyPass>
        </properties>
    </profile>
</profiles>
<activeProfiles>
	<activeProfile>packaging</activeProfile>
</activeProfiles>
```
2. Execute:
```shell
mvn clean install -f common/keystore-example/pom.xml
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
- JDK: 8
