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

### *Exclude Javascript and Typescript test files during license header generation*

#### Previous Behavior

In most archetypes that generate projects with Javascript or Typescript code license headers are added for all `*.ts` and `*.js` files.

#### Current Behavior

All archetypes that generate projects with Javascript and Typescript code have exclusion rules that skip adding license headers for test files.

### *Re-enable license plugin `<excludes>` configuration inheritance through `pom.xml` property value*

#### Previous Behavior

License plugin `<excludes>` uses hardcoded value defined in the `base-package` -> `pom.xml`.

#### Current Behavior

The default value of license plugin's `<excludes>` configuration can be overwritten by providing `<license.excludes>` property in a projects `pom.xml` -> `<properties>` tag.

### Change default return Object of `Array.from()` Method to be empty array

#### Previous Behavior

In case the object type of the array-like Object does not match any of the expected types (e.g. is a Number) the default case of `Array.from()` returns a clone of the object. This does not match the desired behaviour according to the official documentation.

```js
Array.from(10) // Output: 10
```

#### Current Behavior
In case the object type of the array-like Object does not match any of the expected types (e.g. is a Number) the default case of `Array.from()` returns an empty array.

```js
Array.from(10) // Output: []
```

### *VROES.import from invalid package throws Unknown error*

Fixed an issue with VROES.import() in ecmascript Module.

#### Previous Behavior

VROES import from an invalid module path resulted in Unknown error.
The Unknown error could not be caught in a try/catch block and was not shown in the logs.

#### New Behavior

VROES import from a missing/invalid module path, or import of non-existing elements of a valid module,
result in more detailed errors in the logs. No exceptions are thrown by default (as per the existing behaviour)
but there is an option to alter the error handling behaviour via an optional parameter in import.from().

## Upgrade procedure

[//]: # (Explain in details if something needs to be done)
