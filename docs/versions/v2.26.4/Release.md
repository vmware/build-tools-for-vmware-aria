# v2.26.4

## Breaking Changes



## Deprecations



## Features:



## Improvements

### Add Support for vROPs Dashboard Sharing
Add support for sharing of vROPs dashboards with specific vROPs groups.

#### Previous Behavior
There was no support for sharing certain dashboards with specific vROPs groups.

#### New Behavior
When there is a dashboardSharingMetadata.vrops.json file in the dashboards directory of vROps project it will read an sharing will be applied
for all of the listed dashboards within 'share' section of the file with the groups listed for each of the dashboard during vrealize:push action
(or installer).

#### Relevant Documentation:
The dashboardSharingMetadata.vrops.json needs to be adjusted manually prior making vrealize:push of vROPs dashboards.

The file format is as follows:

```typescript
{
	"share": {
		"dashboard 1": [ "group1", "group2" ],
		"dashboard 2": [ "group1", "group2" ]
	},
	"unshare" [
		"dashboard 1": [ "group3" ],
		"dashboard 2": [ "group3" ]
	]
}
```
During vrops:pull action a file dashboardSharingMetadata.vrops.json will be created in the dashboard directory with all listed dashboards
in it for convenience.
Groups with that dashboards will be shared with must exist on the target vROPs server, otherwise the command will throw an error.

### VraNgCustomResourceStore try to update the resource if initial delete failed
When we attempt to upload a Custom Resource ( CR ) currently we first delete the CR and then we upload it again.
This has allowed us to circumvent the limitations when it comes to updating the custom resource.
However, in the case where a Custom Resource is in use by a deployment, deletion of the CR is impossible ( vRA will return an error ).

#### Previous Behavior
When we try to update a CR but the CR is in use by a deployment, the deletion will fail, an error will be displayed and the creation of an updated CR would be skipped.

#### New Behavior
When we try to update a CR that is in use by a deployment and if the deletion fails, now we will attempt to update the CR by pre-fetching it's ID.
Once we have the ID, we would remove it from the CR and re-assigned it again before the importing process is initiated. This would
allow us to create an updated CR that would be imported to vRA no matter if it is used by a deployment.

#### Relevant Documentation:
**NONE**

### *Exports now have local variables so they can be used in the module* 
Typescript export variables are not available locally.

#### Previous Behavior
Given:
```typescript
import * as keys from "./constants/metadata_keys";

export const METADATA_KEY = keys;

export const META_TEST = {
	metaTestKey: METADATA_KEY
}
```
After compilation, the code would look like: 
```javascript
(function () {
	var __global = System.getContext() || (function () {
		return this;
	}).call(null);
	var VROES = __global.__VROES || (__global.__VROES = System.getModule("com.vmware.pscoe.library.ecmascript").VROES()), exports = {};
	var metadata_keys_1 = VROES.importLazy("com.vmware.pscoe.test/import-star/constants/metadata_keys");
	exports.METADATA_KEY = metadata_keys_1._;
	exports.META_TEST = {
		metaTestKey: METADATA_KEY
	};
	return exports;
});
```
Effectively making it so `METADATA_KEY` referenced in the `META_TEST` would NOT be resolved.

#### New Behavior
Given:
```typescript
import * as keys from "./constants/metadata_keys";

export const METADATA_KEY = keys;

export const META_TEST = {
	metaTestKey: METADATA_KEY
}
```
Is now compiled to:
```javascript
(function () {
    var __global = System.getContext() || (function () {
        return this;
    }).call(null);
    var VROES = __global.__VROES || (__global.__VROES = System.getModule("com.vmware.pscoe.library.ecmascript").VROES()), exports = {};
    var metadata_keys_1 = VROES.importLazy("com.vmware.pscoe.onboarding.sgenov.toolchain/import-star/constants/metadata_keys");
    exports.METADATA_KEY = metadata_keys_1._;
    var METADATA_KEY = exports.METADATA_KEY;
    exports.META_TEST = {
        metaTestKey: METADATA_KEY
    };
    var META_TEST = exports.META_TEST;
    return exports;
});
```
Notice the local variables METADATA_KEY and META_TEST.

#### Relevant Documentation:
* **NONE**

### *Custom Resources will now correctly set the orgId if the orgId is not passed in the configuration but orgName is* 
Custom Resources attempt to set the correct orgId when importing/exporting. If orgId is not present in the configuration, 
orgName can be passed instead.

#### Previous Behavior
`orgName` was not respected.

#### New Behavior
`orgName` is now respected and you can pass it without passing the orgId, the orgId will be retrieved from the name.

#### Relevant Documentation:
* **NONE**

### Property groups are now correctly updated
Property groups are not being updated. There is no error message and the `vrealize:push` passes successfully.

#### Previous Behavior
PGs were not being updated because the already existing PG was being passed.

#### New Behavior
When updating a PG, the existing PG id is taken and inserted in the rawData of the PG being updated.

#### Relevant Documentation:
*NONE*

### [vro-types] Added distinguishedName property to the AD_Computer and AD_OrganizationalUnit interfaces
* [vro-types] Added distinguishedName property to the AD_Computer and AD_OrganizationalUnit interfaces

#### Previous Behavior
distinguishedName was not present in the interface so in order to use it, it was needed to do a cast `as any` to the instance of the object interfaces

#### New Behavior
the property is present hence the casting `as any` or similar techniques are not needed.

#### Relevant Documentation:
*NONE*

### Blueprints cannot be imported when they have "." in their names

#### Previous Behavior
Blueprints CANNOT be imported when they have "." in their names

#### New Behavior
Blueprints CAN be imported when they have "." in their names

#### Relevant Documentation:
*NONE*

### Package-installer default prompts do not include prompt on vrang_proxy
Currently when package-installer is run without environment.properties there is no prompt for vrang_proxy to be filed in.
It has to be added to the default questions since the end user has no knowledge of such property existing.

#### Previous Behavior
When running the package-installer without environment.properties file, no value for vrang_proxy can be set as there is no prompt for it.

#### New Behavior
When running the package-installer without environment.properties file, the user is asked whether they want to set a value for vrang_proxy.
If so, the user shall enter value for it and the value gets set in the generated environment.properties file.
Otherwise, no record is saved for the vrang_proxy and the property is not contained in the environment.properties file.
Either way, a new property (vrang_proxy_required) is added to the environment.properties with the respective value of true/false.

#### Relevant Documentation:
**NONE**

## Upgrade procedure:


### Custom Resource Second Day Action Name validation added

#### Previous Behavior
Custom Resource Second Day action CAN be imported when they have "_" in the strart of their names
Custom Resource Second Day action CAN be imported when they have "_" in the end of their names
Custom Resource Second Day action CANNOT be imported when they have "." in the strart of their names
Custom Resource Second Day action CANNOT be imported when they have "." in the strart of their names
Custom Resource Second Day action CANNOT be imported when they have " " in their names
Custom Resource Second Day action CANNOT be imported when they have any special characters exept ". : - _" in their names

#### New Behavior
Custom Resource Second Day action CANNOT be imported when they have "_" in the strart of their names
Custom Resource Second Day action CANNOT be imported when they have "_" in the end of their names
Custom Resource Second Day action CANNOT be imported when they have "." in the strart of their names
Custom Resource Second Day action CANNOT be imported when they have "." in the strart of their names
Custom Resource Second Day action CANNOT be imported when they have " " in their names
Custom Resource Second Day action CANNOT be imported when they have any special characters exept ". : - _" in their names

#### Relevant Documentation:
*NONE*
