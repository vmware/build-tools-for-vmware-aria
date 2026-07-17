# Build Tools for VMware Aria
This repository contains the source code for the Build Tools for VMware Aria, formally known as vRealize Build Tools.
Build Tools for VMware Aria provides advanced development and release management tools for implementing automation solutions for VMware Cloud Foundation 9 components (VCF Automation, VCF Operations Orchestrator, VCF Operations, VCF Operations for Logs). The solution enables Virtual Infrastructure Administrators and Automation Developers to use standard DevOps practices for managing and deploying content.


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

### Consumption dependencies

The following dependencies are required to consume BTVA - to create projects, build, push and pull content:

- npm: 6.14.x (One compatible with your node version)
- node: 22.x.x (only 14 is supported for vcd-ng)
- maven: 3.9.x
- jdk: 17, 21, 24

Additional dependencies may be required polyglot/ABX project types based on your chosen runtime environment (Node, Python, PowerShell).

### Developer dependencies

The following additional dependencies are required to build the BTVA project itself, debug it and contribute to it:

- Python: >= 3.10
- PowerShell: 7.1 - 7.4
- OpenSSL: >= 3.0

### Dependency validation scripts

To check if the dependencies are met on Linux/MacOS, you can run:

```sh
curl -o- https://raw.githubusercontent.com/vmware/build-tools-for-vmware-aria/main/health.sh | bash
```

To check if the dependencies are met on Windows, you can run:

```ps
Set-ExecutionPolicy Bypass -Scope Process -Force; Invoke-Expression ((New-Object System.Net.WebClient).DownloadString('https://raw.githubusercontent.com/vmware/build-tools-for-vmware-aria/main/health.ps1'))
```

By default the healtcheck script validates dependencies for consuming Build Tools for Aria via one of the available project types. To validate all dependencies required for contributing to Build Tools for Aria pass the --contribute flag, e. g.:

```sh
./health.sh --contribute
```

or for Windows

```ps
./health.ps1 -contribute
```

## Encoding (UTF-8)

Some development environments default to a platform-specific file encoding (for example, Cp1252 on some Windows systems). Build Tools for VMware Aria reads and writes files using UTF-8 where possible, but JVM and toolchain defaults may still affect behavior. If you encounter issues where non-ASCII characters are replaced by question marks (for example emoji in CSS/form styles), ensure Java uses UTF-8 by one of the following:

- Start the JVM with the system property: `-Dfile.encoding=UTF-8`.
- Set the environment variable for JVM launches: `JAVA_TOOL_OPTIONS="-Dfile.encoding=UTF-8"`.
- When running Maven, you can pass the property: `mvn -Dfile.encoding=UTF-8 <goal>`.

## Support

You can find detailed support statement [here](./SUPPORT.md)
