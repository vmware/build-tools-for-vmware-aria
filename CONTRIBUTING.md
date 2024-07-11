**Thanks for taking the time to contribute!** 👍

## Table of Contents

- [Table of Contents](#table-of-contents)
- [Certificate of Origin](#certificate-of-origin)
- [Getting Started](#getting-started)
  - [Getting the code](#getting-the-code)
  - [Prerequisites](#prerequisites)
  - [Guides](#guides)
  - [Documentation](#documentation)
- [Contributing Code](#contributing-code)
- [Submitting a Pull Request](#submitting-a-pull-request)
- [Reporting Issues](#reporting-issues)
  - [Look For an Existing Issue](#look-for-an-existing-issue)
  - [Writing Good Issue Reports](#writing-good-issue-reports)

## Certificate of Origin

By contributing to this project you agree to the [Developer Certificate of Origin](https://cla.vmware.com/dco). All contributions to this repository must be signed as described on that page. Your signature certifies that you wrote the patch or have the right to pass it on as an open-source patch.

## Getting Started

Looking for places to contribute to the codebase? You can start by looking through the [`triage/accepted`](https://github.com/vmware/build-tools-for-vmware-aria/issues?q=is%3Aissue+is%3Aopen+label%3Atriage%2Faccepted) issues or [`pr/need-help`](https://github.com/vmware/build-tools-for-vmware-aria/pulls?q=is%3Apr+is%3Aopen+label%3Apr%2Fneed-help) Pull Requests.

### Getting the code

``` shell
git clone https://github.com/vmware/build-tools-for-vmware-aria.git
```

### Prerequisites

- [Git](https://git-scm.com/)
- [Node.js](https://nodejs.org/), `~14.17.1`
- [Npm](https://www.npmjs.com), `~6.14.13`
- [Maven](https://maven.apache.org/), `~3.8.6`
- [JDK](https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html), `1.8.0_xxx`


### Guides
1. [Setting up local environment](docs/versions/latest/General/Getting%20Started/Setting%20Up%20Local%20Environment.md)
2. [Setting up workstation](docs/archive/doc/markdown/setup-workstation.md)
   - vRealize-Developer-Tools [related documentation](https://github.com/vmware/vrealize-developer-tools/wiki/Setup-Developer-Workstation)
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

### Documentation
Please navigate to [the following page](./docs/versions/latest/) for the latest version of Build Tools for VMware Aria documentation.
When contributing, please refer to our [How to write documentation](./docs/Documentation.md) guide.

## Contributing Code
When adding new code make sure to cover the following mandatory points:

1. Append information about the implemented functionality to [Release.md](./docs/versions/latest/Release.md).
2. Satisfy the checklist in [PR template](./.github/pull_request_template.md)
    - If you skip any of the tasks from the checklist, add a comment explaining why that task might be irrelevant to your contribution.
3. Unit tests are mandatory.  
    - If adding unit tests is not viable - other options are to be explored.
    - If no unit tests are included justification should be provided.

:scroll:**NOTE!** When doing a Pull Request review make sure that all points mentioned above are covered before approving the PR.

### Naming Convention For Branches

When creating a branch, please follow the naming convention: 

Format: `<type>/#<issueNumber>-<name>`

Types can be:

- `feature` / `feat` - for new features
- `bugfix` / `fix` - for bug fixes
- `hotfix` - for hotfixes
- `docs` - for documentation changes
- `style` - for changes that do not affect the meaning of the code (white-space, formatting, missing semi-colons, etc)
- `refactor` - for changes that neither fixes a bug nor adds a feature
- `test` - for adding missing tests or correcting existing tests
- `chore` - for changes to the build process or auxiliary tools and libraries such as documentation generation

If none of the above types fit, you should review your code, it probably encompasses more than one type and needs to be split into multiple branches. Exceptions can be made if given a good reason why.

### Commits Message Convention

When creating a branch, please follow the naming convention: [Semantic Commits](https://www.conventionalcommits.org/en/v1.0.0/)

## Submitting a Pull Request

Please follow the instructions in the [PR template](./.github/pull_request_template.md).

## Reporting Issues

If you have identified a reproducible problem or have a feature request, please [open an Issue](https://github.com/vmware/build-tools-for-vmware-aria/issues/new/choose).

### Look For an Existing Issue

Before you create a new issue, please do a search in [open issues](https://github.com/vmware/build-tools-for-vmware-aria/issues) to see if the issue or feature request has already been filed.

If you find your issue already exists, make relevant comments and add your [reaction](https://github.com/blog/2119-add-reactions-to-pull-requests-issues-and-comments). Use a reaction in place of a "+1" comment (👍 for upvote or 👎 for downvote).

If you cannot find an existing issue that describes your bug or feature, create a new issue using the guidelines below.

### Writing Good Issue Reports

- File a single issue per problem and feature request. Do not enumerate multiple bugs or feature requests in the same issue.
- Do not add your issue as a comment to an existing issue unless it's related. Many issues look similar, but have different causes.
- When filing bugs, please follow the [bug template](./.github/ISSUE_TEMPLATE/bug_report.md) and include as much information as possible. The more information you can provide, the more likely someone will be successful at reproducing the issue and finding a fix.

### Labels

If you create a new issue, you should add a label to it `triage/needed`, then someone from the maintainers team will decide if that issue is relevant and if it will be done. If you don't add a label, then it will be up to the maintainers to do so, but your issue may take longer to be resolved.

Overall before an issue is resolved, it should have a `triage`, `kind` and `version` labels.

There are other labels that can be added optionally if you think they are relevant. Check down below for more information.

#### Triaging Labels

The main labels used for triaging are:

- `triage/needed` - This label is used to mark issues that need to be triaged.
- `triage/accepted` - This label is used to mark issues that are relevant and will be done.
- `triage/rejected` - This label is used to mark issues that are not relevant and will not be done.

There are others that can be used to convey different information, for more info check [the labels](https://github.com/vmware/build-tools-for-vmware-aria/labels?q=triage)

#### Kind Labels

The main labels used for kind are:

- `kind/bug` - This label is used to mark issues that are bugs.
- `kind/feature` - This label is used to mark issues that are feature requests.

There are others that can be used to convey different information, for more info check [the labels](https://github.com/vmware/build-tools-for-vmware-aria/labels?q=kind)

#### Version Labels

The main labels used for version are:

- `version/patch` - This label is used to mark issues that are patches.
- `version/minor` - This label is used to mark issues that are minor versions.
- `version/major` - This label is used to mark issues that are major versions.

