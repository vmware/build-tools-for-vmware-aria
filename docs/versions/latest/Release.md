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

### Fixed Errors related to dependency versions

Updated package.json and package-lock.json with newer dependency versions to avoid deprecation errors or vulnerabilities reported by ```npm audit```:
- ```glob@10.4.5``` is enforced as the only version used and is bundled with vropkg.
- ```jasmine``` is updated to 5.7.0
- ```vinyl-fs``` to 4.0.0
Due to issues with the updated ```glob``` dependency, the dependencies:
- ```gulp-jasmine``` and ```download-git-repo``` were replaced by code equivalents
- ```nyc``` was replaced by ```c8``` (this will affect the usage of ```istanbul ignore``` type of comments)
As a result, multiple other dependency versions were changed and will be shown in the output of ```npm ls --all``` as ```deduped``` or ```invalid``` (e.g. ```jackspeak@4.0.2```, ```minimatch@10.0.1```, ```path-scurry@2.0.0```, ```map-schema@0.3.0```), however the spawned processes do not output the related errors to the console.

#### Previous Behavior

When running vrotest, errors were displayed of the type:
```log
[ERROR] npm warn deprecated inflight@1.0.6: This module is not supported, and leaks memory. Do not use it. Check out lru-cache if you want a good and tested way to coalesce async requests by a key value, which is much more comprehensive and powerful.
[ERROR] npm warn deprecated glob@7.2.3: Glob versions prior to v9 are no longer supported
```

#### New Behavior

The ```inflight``` dependency hase been excluded, ```glob``` - updated, and as a result these errors are no longer displayed.

## Upgrade procedure

[//]: # (Explain in details if something needs to be done)
