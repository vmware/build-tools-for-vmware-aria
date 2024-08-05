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

### Add missing classes to `o11n-plugin-aria` and add missing methods to the existing classes

#### Previous Behavior

Many classes are missing completely compared with vRO API and some existing classes were missing some methods

#### Current Behavior

The following classes were added to `o11n-plugin-aria`:

- VraCloudAccount
- VraCloudAccountNsxT
- VraCloudAccountNsxTResult
- VraCloudAccountNsxV
- VraCloudAccountNsxVResult
- VraCloudAccountRegions
- VraRegion
- VraCloudAccountResult
- VraCloudAccountVsphereRegionEnumerationSpecification
- VraCloudAccountVsphereResult
- VraDataCollector
- VraDataCollectorResult
- VraDiskAttachmentSpecification
- VraDiskService
- VraDiskSnapshotSpecification

#### Related issue

<https://github.com/vmware/build-tools-for-vmware-aria/issues/347>

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
