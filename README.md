# Build Tools for VMware Aria
This repository contains the source code for the Build Tools for VMware Aria, formally knows as vRealize Build Tools.
Build Tools for VMware Aria provides development and release management tools for implementing automation solutions based on the VMware Aria Suite (VMware Aria Automation, VMware Aria Automation Orchestrator, VMware Aria Operations, VMware Aria Automation Pipelines, Aria Operations for Logs) and VMware Cloud Director. The solution enables Virtual Infrastructure Administrators and Automation Developers to use standard DevOps practices for managing and deploying content.


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
                <keystoreVersion>4.6.0</keystoreVersion>
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
    mvn clean install -D modules.plugins
    mvn clean install -D modules.tools
    mvn clean package -D modules.repository
    ```

## Package dependencies
- npm: 6.14.x (One compatible with your node version)
- node: 22.x.x (only 14 is supported for vcd-ng)
- maven: 3.9.x
- jdk: 17, 21, 24
- openssl

To check if the dependencies are met on Linux, you can run:

```sh
curl -o- https://raw.githubusercontent.com/vmware/build-tools-for-vmware-aria/main/health.sh | bash
```

To check if the dependencies are met on Windows, you can run:
```ps
Set-ExecutionPolicy Bypass -Scope Process -Force; Invoke-Expression ((New-Object System.Net.WebClient).DownloadString('https://raw.githubusercontent.com/vmware/build-tools-for-vmware-aria/main/health.ps1'))
```

## Support

You can find detailed support statement [here](./SUPPORT.md)
