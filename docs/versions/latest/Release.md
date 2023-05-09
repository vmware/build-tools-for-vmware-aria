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

## Features

[//]: # (Features -> New Functionality)
[//]: # (### *Feature Name*)
[//]: # (Describe the feature)
[//]: # (Optional But higlhy recommended Specify *NONE* if missing)
[//]: # (#### Relevant Documentation:)

### *Support Blueprint Versions Custom Forms*

As of 8.12 Custom Forms for Blueprint Catalog Items was broken as vRA introduced
versions for Blueprint Catalog Items.

#### Previous Behavior

Catalog Item Custom Forms were serialized to keep and upload only latest (current version) of custom forms.

#### New Behavior

Catalog Item Custom Forms, including these of blueprint type, will be serialized/de-serialized with their current versions.
Data structure is not changed on the file system in order to be backwards compatible.
Blueprint catalog item version custom form will be serialized/de-serialized to support, as well, latest versions of the custom forms.

### *npm installation will now throw in case of a failure*

Added .throwIfError to the Node installation process to exit the maven build

### *Backup of vRO Packages During Import*

A new option has been added to back up the vRO packages during a package import.
The backup is triggered when the import is launched before the actual import is executed.
It currently backs up only the packages that are about to be imported.
It is controlled by setting the variable vro_enable_backup to true in the properties file or in the prompt window.

### Add Support for Activating / Deactivating of vROPs Dashboards for Users / Groups

If there is a metadata file for activating / deactivating of vROPs dashboards it will be used for activation / deactivation of the imported dashboards for certain users / groups.
There is support for activating / deactivating of vROPs dashboards for specific users / groups.

#### Relevant Documentation

In order activating of vROPs dashboards for specific users / groups the following files should be present in the dashboards/metadata directory:

* dashboards/metadata/dashboardUserActivationMetadata.vrops  - activate / deactivate dashboards for specific users
* dashboards/metadata/dashboardGroupActivationMetadata.vrops - activate / deactivate dashboards for specific groups
With the following content:
* dashboards/metadata/dashboardUserActivationMetadata.vrops - activation for specific users
{
 "activate": {
  "dashboard name": ["user1", "user2" ]
 },
 "deactivate": {
  "dashboard name": ["user3, user4" ]
 }
}

* dashboards/metadata/dashboardGroupActivationMetadata.vrops - activation for specific groups
{
 "activate": {
  "dashboard name": ["group1", "group2" ]
 },
 "deactivate": {
  "dashboard name": ["group3, group4" ]
 }
}
The users / groups must exist on the target system, otherwise an error will be thrown stating that the users / groups do not exist on the target vROPs system.
For convenience during pulling of dashboard from a vROPs system a set of activation metadata files will be generated with list of dashboards and an empty array of users / groups.

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

### *Domain Detection in configuration*

When attempting authentication, we take the domain name from the `username`

Example:

```xml
<server>
 <id>vro</id>
 <username>configurationadmin@test@System Domain</username>
 <password>{5a593XJmccIHejrppq19g1VIqtn3I34KFryPXJ7mhaw=}</password>
</server>
```

Will detect `configurationadmin` as username and `System Domain` as domain.

#### Previous Behavior

When parsing the username, we used to remove everything after the first `@` sign, which lead to usernames with more than
one `@` to be mishandled. Same issue was detected in the domain fetching logic.

#### New Behavior

Now the detection happens from the **LAST** `@` sign.

Exception to this is that in case of BASIC auth in Aria Automation Orchestrator, we take the username **AS IS**.

#### Relevant Documentation

* None

### Remove prompt and property for vro_delete_include_dependencies
The `installer` script shows a prompt "Clean up vRO dependent packages as well?" about deleting "dependent packages" and stores the answer in the `vro_delete_include_depenedencies` property but does nothing regardless of the answer.

#### Previous Behavior
There was prompt in interactive mode for vRO regarding the deletion of dependent packages.

#### New Behavior
The unnecessary question regarding dependent packages in vRO projects is removed.

#### Relevant Documentation
* None

## Upgrade procedure

[//]: # (Explain in details if something needs to be done)

[//]: # (## Changelog:)
[//]: # (Pull request links)
