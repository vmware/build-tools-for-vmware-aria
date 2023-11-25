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

### Wrong unix file path separators when creating backup path

#### Previous Behaviour
The backup files/folder path on are always created with "\". This is cuasing wrong file names on unix.

#### Current Behaviour
Files and folders are created with the system dependent separator.

### Transpiler fails to convert Array functions to vRO compatible code

The transpilation issue is documented and a recommended fix together with a configuration that can prevent it is described.

### Updated `Array.from()` to create shallow clone and to properly handle `string`, `Map<K, V>` and `Set<T>` input according to official documentation

#### Previous Behavior
* Calling `Array.from()` doesn't create a shallow clone.
* Calling `Array.from()` with string input returns the same input instead of character array.
* Calling `Array.from()` with `Map<K, V>` and mapping function input throws `TypeError: Cannot find function map in object...`.
* Calling `Array.from()` with `Set<T>` and mapping function input throws `TypeError: Cannot find function map in object...`.

#### New Behavior
* Calling `Array.from()` creates a shallow clone.
* Calling `Array.from()` with string input returns an array of characters.
* Calling `Array.from()` with `Map<K, V>` and mapping function returns an array of key-value pairs.
* Calling `Array.from()` with `Set<T>` and mapping function returns an array of unique values.

### Fixed backup of vRO packages so that the all available version are backed up

#### Previous Behavior
Back up of vRO packages (using the flag in the environment.properties file: vro_enable_backup=true)
would only work if the currently imported packages (which are to back up), had the same version as the one in vRO.
Otherwise, the import would throw an '404 Not found' exception and break the import process,
due to not finding the same package and version to back up.

#### New Behavior
Back up of vRO packages now works by:
* backing up all available versions in vRO of the imported package,
* logging a message that back up is skipped for the package, if no versions of it are found in vRO, continuing with backup of next packages, and the import process.

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

[//]: # (## Changelog:)
[//]: # (Pull request links)
