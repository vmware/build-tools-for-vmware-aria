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

### *Add missing types to AD plugin*

AD plugin was missing some types that are now added.

#### Previous Behavior

The following were missing completely or partially:

* AD_Computer
* AD_OrganizationalUnit
* AD_UserGroup

#### Current Behavior

The following are now available:

* AD_Computer

* AD_OrganizationalUnit
  * Method  `createUser`
  * Method `createUserWithPassword`
  * Method `createUserWithDetails`
  * Method `createUserGroup`
  * Method `createOrganizationalUnit`
  * Method `createComputer`
  * Method `createComputerWithPassword`
  * Method `removeAttribute`
  * Method `destroy`
  * Method `setAttribute`
  * Method `getAttribute`
  * Method `addAttribute`
  * Method `getAttributeValueBytes`
  * Method `getArrayAttribute`
* AD_UserGroup:
  * JDOC for `removeAttribute`
  * JDOC for `destroy`
  * JDOC for `rename`
  * JDOC for `setAttribute`
  * JDOC for `getAttribute`
  * JDOC for `addAttribute`
  * JDOC for `getAttributeValueBytes`
  * JDOC for `getArrayAttribute`

## Upgrade procedure

[//]: # (Explain in details if something needs to be done)
