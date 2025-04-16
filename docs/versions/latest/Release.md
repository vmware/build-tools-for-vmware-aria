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

[//]: # (Improvements -> Bugfixes/hotfixes or general improvements)
### *Simplified Minimal Infra Setup*

There is a new script now that can be executed and it will setup the minimal infra.

## Improvements

### Fixed Errors related to dependency versions

Older dependency versions were used due to committed package-lock.json files.

#### Previous Behavior

When running vrotest, errors were displayed of the type:
```log
[ERROR] npm warn deprecated inflight@1.0.6: This module is not supported, and leaks memory. Do not use it. Check out lru-cache if you want a good and tested way to coalesce async requests by a key value, which is much more comprehensive and powerful.
[ERROR] npm warn deprecated glob@7.2.3: Glob versions prior to v9 are no longer supported
```

#### New Behavior

Errors related to dependency versions are no longer shown when running vrotest.

### Fixed Error Running Unit Tests on Linux and Windows

An error affected unit test execution in versions after 4.3.0 without causing the build to fail.

#### Previous Behavior

An error of the type "Error occurred in unit tests execution:" was shown instead of unit test results.

#### New Behavior

Unit test results are shown as expected.

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

## Upgrade procedure

[//]: # (Explain in details if something needs to be done)
