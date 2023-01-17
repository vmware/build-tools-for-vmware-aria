# Build Tools for VMware Aria Workstation
**Build Tools for VMware Aria Workstation** represents a vRealize engineer development machine configured to work with **Build Tools for VMware Aria** toolchain.

# Install and Configure

## Prerequisites
- [Build Tools for VMware Aria Platform](setup-platform.md) ready to use
- [vRealize Developer Tools](https://github.com/vmware/vrealize-developer-tools)
- Java 8 ([official installation guide](https://www.java.com/en/download/help/download_options.xml))
- Maven 3.5+ ([official installation guide](https://maven.apache.org/install.html))
- Development vRealize Automation Tenant configured with development vRealize Orchestrator
    - Tenant administrator user
    - Workstation can access vRA server on port 443
- Development vRealize Orchestrator
    - vRO administrator user
    - vRO appliance root user
    - Workstation can access vRO server on ports [443 or 8281], 8283
- Powershell Core for Linux/MacOS if working with the polyglot powershell archetype.
    - MacOS: https://docs.microsoft.com/en-us/powershell/scripting/install/installing-powershell-on-macos
    - Linux: https://docs.microsoft.com/en-us/powershell/scripting/install/installing-powershell-on-linux

## Installation and Configuration

### Configuration
- [Build Tools for VMware Aria Build System](setup-workstation-maven.md)

## Use
- [vRO Projects](use-workstation-vro-project.md)
- [vRA Projects](use-workstation-vra-project.md)
- [vRA NG Projects](use-workstation-vra-ng-project.md)
- [ABX Projects](use-workstation-abx-project.md)
- [vROps Projects](use-workstation-vrops-project.md)
