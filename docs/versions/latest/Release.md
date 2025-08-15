[//]: # (VERSION_PLACEHOLDER DO NOT DELETE)
[//]: # (Used when working on a new release. Placed together with the Version.md)
[//]: # (Nothing here is optional. If a step must not be performed, it must be said so)
[//]: # (Do not fill the version, it will be done automatically)
[//]: # (Quick Intro to what is the focus of this release)

## Breaking Changes

[//]: # (### *Breaking Change*)
[//]: # (Describe the breaking change AND explain how to resolve it)
[//]: # (You can utilize internal links /e.g. link to the upgrade procedure, link to the improvement|deprecation that introduced this/)

## Deprecations

[//]: # (### *Deprecation*)
[//]: # (Explain what is deprecated and suggest alternatives)

[//]: # (Features -> New Functionality)

## Features

[//]: # (### *Feature Name*)
[//]: # (Describe the feature)
[//]: # (Optional But higlhy recommended Specify *NONE* if missing)
[//]: # (#### Relevant Documentation:)

### *Add SSH timeout common configuration*

Common configuration for timeout on SSH operations is added that can be used by any archetype that needs it.
The new timeout has the following configuration options:
* <vrealize.ssh.timeout> - in `settings.xml` profile
* vrealize_ssh_timeout - as `installer` script input
* default value of 300 if not provided

[//]: # (Improvements -> Bugfixes/hotfixes or general improvements)

## Improvements

[//]: # (### *Improvement Name* )
[//]: # (Talk ONLY regarding the improvement)
[//]: # (Optional But higlhy recommended)
[//]: # (#### Previous Behavior)
[//]: # (Explain how it used to behave, regarding to the change)
[//]: # (Optional But higlhy recommended)
[//]: # (#### New Behavior)
[//]: # (Explain how it behaves now, regarding to the change)
[//]: # (Optional But higlhy recommended Specify *NONE* if missing)
[//]: # (#### Relevant Documentation:)

### *Add SSH timeout to vROps operations*

Use the newly introduced SSH timeout common configuration in vROps push/pull operations.

#### Previous Behavior

SSH operations have no timeout which in certain scenarios results in hanging push/pull operation.

#### New Behavior

SSH operations use the configured (or default) timeout value and if reached - the push/pull operation fails.

### *Update documention for import strategies*

Add documentation about `importOldVersions` import strategy and update documentation for `forceImportLatestVersions`. Add missing log in case of skipped import of package when using `forceImportLatestVersions`.

## Upgrade procedure

[//]: # (Explain in details if something needs to be done)
