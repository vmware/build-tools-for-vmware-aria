## Table of Contents

-   [Confluence reference](#confluence-page-reference)
-   [Pre-release](#pre-release)
-   [Release](#release)
-   [Post-release](#post-release)
-   [Versioning semantics](#versioning-semantics)
-   [Public Release (Fling)](#public-release-(fling))

## Confluence Page Reference
The guide can be found in the following page: https://confluence.pscoe.vmware.com/display/KB/Build+Tools+for+Aria+Release+Procedure

## Pre-release
1. Announce release intention and desired release version in #pscoe-iac slack channel and tag any interested parties.
2. For all tickets marked with `Current` fix version in [Jira Releases](https://jira.pscoe.vmware.com/projects/IAC?selectedItem=com.atlassian.jira.jira-projects-plugin:release-page):
    -   Validate all tickets intended as part of the release are marked as `Done` and with merged PRs.
    -   Validate if any tickets marked as `In Progress` can or need to be included in the release.
    -   Remove `Current` fix version from all `Cancelled` tickets.
3. Validate whether any ticket marked with `Near term` fix version can or needs to be included in the release.
4. Repeat steps 3 and 4 for tickets on the [Build Tools for VMware Aria Roadmap](https://jira.pscoe.vmware.com/secure/PortfolioPlanView.jspa?id=13&sid=14#backlog).
5. For all tickets included in the release make sure in [Stash](https://stash.pscoe.vmware.com/projects/IFV/repos/toolchain/browse):
    -   PRs are merged.
    -   Tickets are linked to corresponding PR.
    -   An entry is added to [CHANGELOG.md](https://stash.pscoe.vmware.com/projects/IFV/repos/toolchain/browse/CHANGELOG.md).
    -   Documentation is available as part of the PR and/or updated in the Jira ticket.
6. For OSX users, you need to have `gsed` installed ( `brew install gsed` ).

## Release
Recommended approach is to use the dedicated [Build Tools for VMware Aria release pipeline](https://bamboo.pscoe.vmware.com/browse/IAC-TCRELEASE).

1. Use `Run customized...` option to trigger the build
2. Do not specify anything in the `Revision` field. Even if you do, the information will be **ignored** as all releases are performed from the **master** branch
3. Override `release_version` variable with the desired version. Provide the version string only without prefixing it with "v".

:bulb:**Tip!** The release script automatically appends details about the release version and time to the CHANGELOG.md, e.g. https://stash.pscoe.vmware.com/projects/IFV/repos/toolchain/commits/88d71d1ae25a754d5f1e990ed0cc689d22d5f3bc#CHANGELOG.md. 

## Post-release
1. For all tickets part of the release, remove `Current` fix version label and assign the released version.
2. Initiate release of the version in [Jira Releases](https://jira.pscoe.vmware.com/projects/IAC?selectedItem=com.atlassian.jira.jira-projects-plugin%3Arelease-page&status=no-filter).
3. Create placeholder releases for next subsequent bugfix and minors build (e.g. you have just release 2.19.0, create placeholder releases 2.19.1 and 2.20.0).
4. Archive any unused previous placeholder releases.
5. Add the released version to active filters in [Build Tools for VMware Aria Roadmap](https://jira.pscoe.vmware.com/secure/PortfolioPlanView.jspa?id=13&sid=14&vid=71#plan/backlog).
6. Announce the release in [#pscoe slack channel](https://vmware.slack.com/archives/C38NCDB27/p1642775363116100) and provide release notes/changelog.

## Versioning semantics
**Bugfix version** - includes only bugfixes
**Minor version** - includes backwards compatible enhancements and extensions.

## Public Release (Fling)
Flings updates are published through the Submission page on https://submissions.eng.vmware.com/flings-updates

1.  Make sure you have active SSO session (through login.vmware.com). No additional registration or permissions needed.
2. Go to https://submissions.eng.vmware.com/flings-updates
3. Click on the Create button (top left corner)
4. Fill in the Fling Name, e.g. `vRealize Build Tools`
5. Add the usernames of the people that contributed to the update. These are usually shown on the Fling page.
6. Fill the URL of the Fling, e.g https://flings.vmware.com/vrealize-build-tools
7. Add the version number being published, e.g. **2.20.0**
8. Describe the changes in this release, relative to the previous public version
    -   Use the [CHANGELOG.md](https://changelog.md/) from the toolchain repo as reference
    -   Remove any non-relevant information and everything that should remain private
    -   Make sure there are no references in the changelog to JIRA ticket numbers
9. Provide a link with the toolchain release artifact
    -   This could be OneDrive link or direct link from the CoE Artifactory
    -   Make sure the link is accessible by everybody within VMware, otherwise the Flings Team will not be able to download and publish the file
10. Submit the ticket
11. Add the ticket link to the section below for future reference

:scroll:**NOTE!** Note that after opening the submission ticket, it takes a day or two for the new version to appear on the Flings page. Usually all people added as contributors will receive a notification.
