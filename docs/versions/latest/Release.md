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

### Updated keystore-example version

The versions of keystore-example (keystoreVersion) in pom.xml files and examplers were updated to 4.4.0.

#### Previous Behavior

There were errors building BTVA due to the versions of keystore-example used longer being present in the maven repository.

#### New Behavior

There are no more errors related to keystoreVersion during building.

### CSP Now correctly work cross env

Project id is set at the correct time and also logs are correct.

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

### *Content Sharing Policies Import*

CSPs could not be imported due to a forgotten fetch before creation.

#### Previous Behavior

When importing we were getting an error that id is expected of type UUID.String but was null

#### New Behavior

CSPs are no longer fetched prior to updating to set their ID.

### *Fixed Error with Pulling vROPs policies*

Fixed error during pulling of vROPs policies due to missing *policiesMetadata.vrops.json* file.

#### Previous Behavior

Due to error while creating of the *policiesMetadata.vrops.json* file when exporting vROPs policies they cannot be exported.

#### New Behavior

Policies can be exported successfully, the *policiesMetadata.vrops.json* file creation and reading is removed, as it is no longer needed. When custom groups that are dependent on policies are exported the name of the policy is stored in the custom group JSON file. When importing those custom groups their policies are searched in the target system prior import, if they cannot be found an error will be thrown.

## Upgrade procedure

[//]: # (Explain in details if something needs to be done)
