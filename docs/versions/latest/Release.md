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

### *Backup of vRO Packages During Import*
A new option has been added to back up the vRO packages during a package import.
The backup is triggered when the import is launched before the actual import is executed.
It currently backs up only the packages that are about to be imported.
It is controlled by setting the variable vro_enable_backup to true in the properties file or in the prompt window.
## Add Support for Activating / Deactivating of vROPs Dashboards for Users / Groups
If there is a metadata file for activating / deactivating of vROPs dashboards it will be used for activation / deactivation of the imported dashboards for certain users / groups.

#### Previous Behavior
There was no support for activating / deactivating of vROPs dashboards for specific users / groups.

#### New Behavior
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



## Upgrade procedure:
[//]: # (Explain in details if something needs to be done)

[//]: # (## Changelog:)
[//]: # (Pull request links)
