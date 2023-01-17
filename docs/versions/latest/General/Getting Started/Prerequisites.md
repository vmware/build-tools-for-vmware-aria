# Prerequisites

## Table Of Contents:
1. [Prerequisites](#prerequisites)

### Prerequisites
- [vRealize Developer Tools](https://github.com/vmware/vrealize-developer-tools) (Optional)
- Java 8 ([official installation guide](https://www.java.com/en/download/help/download_options.xml))
- Maven 3.5+ ([official installation guide](https://maven.apache.org/install.html))
- NodeJS 16.x.x (use `nvm` if possible) [official downloads](https://nodejs.org/en/download/releases/)
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

## Next: [Setting Up Artifactory](./Setting Up Artifactory.md)

