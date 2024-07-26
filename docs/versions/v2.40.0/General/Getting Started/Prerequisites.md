# Prerequisites

[//]: # (TODO: Update the links in the Use section after the files are moved)
[//]: # (TODO: More in Installation section?)
[//]: # (TODO: Replace all references of the setup-workstation.md to point to Prerequisites.md)

## Overview

Prerequisites for installation of Build Tools for VMware Aria.

## Table Of Contents

1. [Prerequisites](#prerequisites)
2. [Installation and Configuration](#installation-and-configuration)
3. [Usage](#usage)

### Prerequisites

- [vRealize Developer Tools](https://github.com/vmware/vrealize-developer-tools) (Optional)
- Java 17
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
  - [MacOS]( https://docs.microsoft.com/en-us/powershell/scripting/install/installing-powershell-on-macos )
  - [Linux]( https://docs.microsoft.com/en-us/powershell/scripting/install/installing-powershell-on-linux )

### Installation and Configuration

#### Configuration

- [Build Tools for VMware Aria Build System](Setting%20Up%20Artifactory.md)

### Usage

- [vRO Projects](use-workstation-vro-project.md)
- [vRA Projects](use-workstation-vra-project.md)
- [vRA NG Projects](use-workstation-vra-ng-project.md)
- [ABX Projects](use-workstation-abx-project.md)
- [vROps Projects](use-workstation-vrops-project.md)

## What's Next

See [Setting Up Artifactory](./Setting%20Up%20Artifactory.md).
