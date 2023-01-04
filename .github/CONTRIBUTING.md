**Thanks for taking the time to contribute!** 👍

## Table of Contents

-   [Certificate of Origin](#certificate-of-origin)
-   [Getting Started](#getting-started)
    -   [Getting the code](#getting-the-code)
    -   [Prerequisites](#prerequisites)
    -   [Guides](#guides)
    -   [Documentation](#documentation)
-   [Contributing Code](#contributing-code)
-   [Reporting Issues](#reporting-issues)
    -   [Look For an Existing Issue](#look-for-an-existing-issue)
    -   [Writing Good Issue Reports](#writing-good-issue-reports)

## Certificate of Origin

By contributing to this project you agree to the [Developer Certificate of Origin](https://cla.vmware.com/dco). All contributions to this repository must be signed as described on that page. Your signature certifies that you wrote the patch or have the right to pass it on as an open-source patch.

## Getting Started

Looking for places to contribute to the codebase? You can start by looking through the [`help-wanted`](https://github.com/vmware/{{ repo-url }}/labels/status:help-wanted) issues.

### Getting the code

```
git clone https://github.com/vmware/{{ repo-url }}.git (to be updated once the repo is created)
```

### Prerequisites

-   [Git](https://git-scm.com/)
-   [Node.js](https://nodejs.org/), `>= 16.0.0`
-   [Maven](https://maven.apache.org/), `>= 3.5`
-   [JDK](https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html), `1.8.0_xxx`

### Guides
-   [Setting up workstation](https://github.com/vmware/vrealize-developer-tools/wiki/Setup-Developer-Workstation)

### Documentation
Please navigate to [the following page](../docs/versions/latest/) for the latest version of Build Tools for VMware Aria documentation.
When contributing, please refer to our [How to write documentation](../docs/Documentation.md) guide.

## Contributing Code
When adding new code make sure to cover the following mandatory points:

1. Append information about the implemented functionality to [CHANGELOG.md](../CHANGELOG.md) and [Release.md](../docs/versions/latest/Release.md).
2. Satisfy the checklist in [PR template](./pull_request_template.md)
    -   If you skip any of the tasks from the checklist, add a comment explaining why that task might be irrelevant to your contribution.
3. Unit tests are mandatory.  
    -   If adding unit tests is not viable - other options are to be explored.
    -   If no unit tests are included justification should be provided.
4. Include usage information based on [As-Built](../As-built-template.md) template.

:scroll:**NOTE!** When doing a Pull Request review make sure that all points mentioned above are covered before approving the PR.

## Submitting a Pull Request

Please follow the instructions in the [PR template](./pull_request_template.md).

## Reporting Issues

If you have identified a reproducible problem or have a feature request, please [open an Issue](https://github.com/vmware/{{ repo-url }}/issues/new/choose).

### Look For an Existing Issue

Before you create a new issue, please do a search in [open issues](https://github.com/vmware/{{ repo-url }}/issues) to see if the issue or feature request has already been filed.

If you find your issue already exists, make relevant comments and add your [reaction](https://github.com/blog/2119-add-reactions-to-pull-requests-issues-and-comments). Use a reaction in place of a "+1" comment (👍 for upvote or 👎 for downvote).

If you cannot find an existing issue that describes your bug or feature, create a new issue using the guidelines below.

### Writing Good Issue Reports

-   File a single issue per problem and feature request. Do not enumerate multiple bugs or feature requests in the same issue.
-   Do not add your issue as a comment to an existing issue unless it's related. Many issues look similar, but have different causes.
-   When filing bugs, please follow the [bug template](./ISSUE_TEMPLATE/bug_report.md) and include as much information as possible. The more information you can provide, the more likely someone will be successful at reproducing the issue and finding a fix.
