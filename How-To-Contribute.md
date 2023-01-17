**Thanks for taking the time to contribute!** 👍

## Table of Contents

- [Table of Contents](#table-of-contents)
- [Getting Started](#getting-started)
  - [Naming Convention](#naming-convention)
  - [Getting the code](#getting-the-code)
  - [Prerequisites](#prerequisites)
  - [Guides](#guides)
- [Jira](#jira)
- [Stash](#stash)
- [Documentation](#documentation)
- [Getting help](#getting-help)

## Getting Started

### Naming Convention

The officially approved name for this project is **Build Tools for VMware Aria**. According to the latest VMware shortened naming guidelines, it's only the full name and the short name **Build Tools for Aria** that should be used when referring to the project in official documentation and customer conversations. Please refrain from using any acronyms and abbreviations.

### Getting the code

```
git clone https://github.com/vmware/build-tools-for-vmware-aria
```

### Prerequisites

-   [Git](https://git-scm.com/)
-   [Node.js](https://nodejs.org/), `>= 16.0.0`
-   [Maven](https://maven.apache.org/), `>= 3.5`
-   [JDK](https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html), `1.8.0_xxx`

### Guides

1. [Setting up development environment](https://confluence.pscoe.vmware.com/display/PCDO/Setup+Development+Environment)
2. [Setting up workstation](docs/archive/doc/markdown/setup-workstation.md)
   - Publicly available: https://github.com/vmware/vrealize-developer-tools/wiki/Setup-Developer-Workstation
3. Adding new functionalities - here are few guidelines that could be followed to ease the development process:
   - Read the ticket description to understand which is the mvn plugin that needs to be updated. All plugin executions (`mvn package`, `pull` and `push`) start from a `Mojo` class under the plugin's directory `/maven/plugins/PLUGIN-XXX/src/main/java/com/vmware/pscoe/maven/plugins/`.
     - Example: `PullMojo.java` under `/maven/plugins/vra-ng` will be executed when you do the `mvn pull` command in VMware Aria Automation 8 (vRA 8\) context.
   - The Mojo class will create and work with an instance of one of the Package Store classes located under `/common/artifact-manager/src/main/java/com/vmware/pscoe/iac/artifact/`.
     - Example: The VMware Aria Automation 8 Package Store class is `VraNgPackageStore.java`.
   - Most of the Package Store classes will use a REST client for communication. REST clients are located in `/common/artifact-manager/src/main/java/com/vmware/pscoe/iac/artifact/rest/`.
     - Example: `VraNgPackageStore.java` uses `RestClientVraNg.java`, which itself uses `RestClientVraNgPrimitive.java`.
4. Testing guide - follow the steps bellow to do a local test of a new functionality or a bugfix.
   - Create or reuse an existing project with the type of archetype that you need.
     - Example: For VMware Aria Automation 8 you will need a `vra-ng` project archetype.
   - Bump the test project's Aria Build Tools version to the latest **SNAPSHOT** version. The version number is located in the `pom.xml` file/s between the `<parent>` tags. Each project can have multiple `pom.xml` files. If that's the case, change all of them.
   - Run `mvn clean package` from the main test project directory so you can download the latest Aria Build Tools artifacts.
   - After you make the proper changes under your local Aria Build Tools source, you will need to install the modified artifacts (locally), so you can test them. This is done by running the `mvn clean install` command from a *particular modified* Aria Build Tools directory (this will save you time, so you will not need to install all artifacts locally).
     - Example: If you have modified the `VraNgPackageStore.java` or the `RestClientVraNg.java` class, you can run the `mvn clean install` command from the `/common/artifact-manager` directory.
     - Example: If you have modified a `PullMojo.java` class, you can run the `mvn clean install` command from the `/maven/plugins/` directory.
   - Now test the modifications by executing `mvn clean package/pull/push` command from the test project.


## Documentation

Documentation needs to be updated in one or multiple of the following locations:
-   Mandatory:
    -   [Repository documentation](./docs/Documentation.md)
    -   Include usage information - Jira `As Built` field should be populated following the template and example here: https://onevmw.sharepoint.com/:f:/r/sites/sof-coe/Shared%20Documents/Initiatives/C30-452%20As-built%20field%20template?csf=1&web=1&e=2GfHwp
        -   Template is also available as [part of the repository](./As-built-template.md)

## Getting help

If you are contributing and in need of advice you can refer to the following options:
-   Consult with your mentor.
-   Ask in [pscoe-iac](https://vmware.slack.com/archives/C8PFL582U) Slack channel.
    -   In case you don't have access, request access from your mentor or any of the CoE architects.
-   In case none of the options above can help you reach a solution, consult with CoE architects.
