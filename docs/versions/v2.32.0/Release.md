# v2.32.0

## Breaking Changes


## Deprecations


## Features


### *Support Blueprint Versions Custom Forms*

As of 8.12 Custom Forms for Blueprint Catalog Items was broken as vRA introduced
versions for Blueprint Catalog Items.

#### Previous Behavior

Catalog Item Custom Forms were serialized to keep and upload only latest (current version) of custom forms.

#### New Behavior

Catalog Item Custom Forms, including these of blueprint type, will be serialized/de-serialized with their current versions.
Data structure is not changed on the file system in order to be backwards compatible.
Blueprint catalog item version custom form will be serialized/de-serialized to support, as well, latest versions of the custom forms.

#### Upgrade steps

The new version of Build Tools for Aria can process old or new content.
If the content of the custom form does not have previous changes it can be pushed straight away to the 8.12 or cloud instance.
If there is a change on the current version of the custom form of the blueprint version - then a pull needs to be performed
and then pushed to the respective env.

Configurations in the content.yaml *do not* change.

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


## Improvements


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


