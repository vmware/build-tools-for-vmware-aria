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
### `vrealize:push` is no longer supported for vCloud Director for Service Providers 9.7 (API version 32.0) - officially unsupported
The new authorization endpoint cloudapi/1.0.0/sessions/provider/post is available after API Version 33.0
Reference:
[VMware Cloud Director API Programming Guide](https://developer.vmware.com/docs/14143/vmware-cloud-director-api-programming-guide)
[VMware Cloud Director OpenAPI  Sessions](https://developer.vmware.com/apis/vmware-cloud-director/latest/cloudapi/1.0.0/sessions/provider/post/)

### Deprecating SQLDatabaseManager.getDatabase() function

SQLDatabaseManager.getDatabase() function is removed in vRA 7.6 / Aria Automation 8 and above. The function uses name as parameter to retrieve a database. Use getDatabaseById() or getDatabases() and filter by name instead.

[//]: # (Features -> New Functionality)
## Features

### *Support subscription with ABX action*
Start support import/export operation for subscription with ABX action.\
Example of definition

~~~JSON
{
    "id": "sub_1615990481058",
    "type": "RUNNABLE",
    "eventTopicId": "compute.provision.pre",
    "name": "Tagging VM",
    "ownerId": "Administrator",
    "subscriberId": "vro-gateway-lbcAbah7LZP1JTKZ",
    "blocking": true,
    "description": "",
    "criteria": "",
    "constraints": {
       "projectId": null
    },
    "timeout": 0,
    "broadcast": false,
    "priority": 10,
    "disabled": false,
    "system": false,
    "runnableType": "extensibility.vro",
    "runnableName": "TaggingVM"
}
~~~

[//]: # (Optional But higlhy recommended Specify *NONE* if missing)
[//]: # (#### Relevant Documentation:)

[//]: # (Improvements -> Bugfixes/hotfixes or general improvements)
## Improvements

### Blueprints with the same name in a single project will give out a better error message now

#### Previous Behaviour

Misleading error was thrown along the lines of "Duplicate Key@VraNgBluerpintSHA was given", since we tried to assign a value to a map
when it already existed.

#### Current Behaviour

Now we'll get a meaningful message, outlying that we can't have duplicate Blueprints in a single project.

### Wrong unix file path separators when creating backup path

#### Previous Behaviour
The backup files/folder path on are always created with "\". This is causing wrong file names on unix.

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

### Installer now uses ConfigurationVroNg instead of ConfigurationVro when embedded
During cleanup or deletion of previous package versions of an embedded vRO package was trying to use basic authentication as this is the mechanism provided by an standalone vRO. Now uses vRA authentication when it is embedded.

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

### Add support for custom interaction component forms for vRO workflows
#### Previous Behavior

When pulling vRO workflows that contain custom interaction components and their UI forms, they are not present in the output XML tree.
When manually creating JSON representation of custom interaction components forms they are not part of the vRO package and not pushed to the target vRO.

#### New Behavior
When pulling vRO workflows that contain custom interaction components and their UI forms, they are are present in the output XML tree as files with the the form.json suffix. Each custom interaction component form will be stored in a separate file.
When pushing a project that has custom interaction component forms, they are part of the vRO package as well and get pushed to the target vRO and visible in the vRO UI.

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

### Support `vrealize:push` for VMware Cloud Director 10.5 (API version 38.0)

#### Previous Behavior
The /api/sessions API login endpoint is deprecated since VMware Cloud Director API version 33.0. For version 38.0 and later, the /api/sessions API login endpoint is no longer supported. You can use the VMware Cloud Director OpenAPI login endpoints to access VMware Cloud Director.

#### New Behavior
Service provider access to the system organization- POST cloudapi/1.0.0/sessions/provider
Tenant access to all other organizations apart from the system organization- POST cloudapi/1.0.0/sessions

As per the backward compatibility commitment of VMware Cloud Director, versions 37.2 and earlier continue to support the /api/sessions API login endpoint.
Reference: [VMware Cloud Director 10.5 Release Notes](https://docs.vmware.com/en/VMware-Cloud-Director/10.5/rn/vmware-cloud-director-105-release-notes/index.html)

### Fix vRA Catalog Items Paging Issue when Fetching Catalog Items from Server

#### Previous Behavior
* When fetching catalog items from vRA server and the page size is below the available items, in the result list some of the catalog items appear twice.

#### New Behavior
* When fetching catalog items from vRA server and the page size is below the available items, the result contains unique items only.

### Fix Install vro package fails with 404 not found in case vro_server=vro-l-01a is used not FQDN

#### Previous Behavior
* When installing a package on a standalone vRO with environment.properties file with priperty "vro_server" containing hostname instead of FQDN an error "404 Not Found 404 page not found" appears.

#### New Behavior
* When installing a package on a standalone vRO with environment.properties file with priperty "vro_server" containing hostname instead of FQDN an error "Invalid/Unreachable FQDN or IP address" appears.

## Upgrade procedure
[//]: # (Explain in details if something needs to be done)

[//]: # (## Changelog:)
[//]: # (Pull request links)

