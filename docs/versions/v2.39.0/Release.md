# v2.39.0


## Breaking Changes


## Deprecations



## Features

### Support of project scope / organization during import of content sharing policies

Content sharing policies now supports different project for their scope/organization parameters. If a scope project is defined in the content
sharing JSON file it will be used as a project during push, hence allowing more granular content sharing across different projects.
If there is no scope / organization defined in the content sharing JSON file then the one defined in the global settings.xml file will be used.

```yaml
policy:
  content-sharing:
    - contentSharingPolicy
```

Example content sharing policy JSON file with scope / organization parameters.

```JSON
{
  "id": "1",
  "name": "contentSharingPolicy",
  "typeId": "com.vmware.policy.catalog.entitlement",
  "projectId": "1",
  "scope": "projectId1",
  "organization": "organization1",
  "enforcementType": "HARD",
  "description": "TEST",
  "definition": {
    "entitledUsers": []
  }
}
```

In the example above the organization and scope of the content sharing policy is different than the one defined in settings xml. They will be used when
importing the policy in the target system in order to assign the organization and scope of the content sharing policy.

Example content sharing policy JSON file without scope / organization parameters.

```JSON
{
  "id": "1",
  "name": "contentSharingPolicy",
  "typeId": "com.vmware.policy.catalog.entitlement",
  "projectId": "1",
  "orgId": "1",
  "enforcementType": "HARD",
  "description": "TEST",
  "definition": {
    "entitledUsers": []
  }
}
```

In the example above the organization and scope of the content sharing policy is not defined, hence the ones defined in the settings.xml configuration file
will be used for assignment of the project / organization on the target system during import.

### New strategy when importing packages in Orchestrator `StrategyForceLatestVersions`

This strategy will force you to upload the same or newer version of a package, otherwise it will fail the build, allowing us for
better CI/CD pipelines, where we can ensure that the latest versions are always used on the server.

The new strategy can be triggered by passing `-Dvro.forceImportLatestVersions=true`. It is by default set to `false`.

Example usage:

```bash
mvn clean package vrealize:push -DincludeDependencies=true -Dvro.forceImportLatestVersions=true -DskipTests -PDevLab
```

### Implement `Object.setPrototypeOf()` function

Add support for native `Object.setPrototypeOf()` function that can be used to properly setup prototype chain during inheritance.

E.g. with the following code the prototype chain is setup properly and the log message is `Constructor name: NsxtError`.

```typescript
class NsxtError extends Error {
    public readonly cause: string
    constructor(message: string, service: string) {
        super(`Received error ${message} from NSX-T Service ${service}.`);
        Object.setPrototypeOf(this, new.target.prototype)
    }
}

const testError = new NsxtError("Object not found", "SecurityPolicyService");
System.log(`Constructor name: ${testError.constructor.name}`);
```

Without the `Object.setPrototypeOf` the NsxtError is not properly added to the prototype chain and the log message is `Constructor name: Error`.

```typescript
class NsxtError extends Error {
    public readonly cause: string
    constructor(message: string, service: string) {
        super(`Received error ${message} from NSX-T Service ${service}.`);
    }
}

const testError = new NsxtError("Object not found", "SecurityPolicyService");
System.log(`Constructor name: ${testError.constructor.name}`);
```

### Pretty formatted JSON for Custom Forms when storing them together with Custom Form Metadata

When Custom Forms are pulled from Aria Automation, they are stored on the file system (the repo) in a form similar to

```json
{
  "id": "e694a748-7067-47d1-91a4-614da73dda03",
  "name": "Test",
  "form": "{\"layout\": {...},\"schema\": {...},\"options\": {...}}",
  "styles": null,
  "sourceType": "com.vmw.blueprint",
  "sourceId": "71ac6ebc-6a94-3c5a-8c00-2a44ddf81bce",
  "type": "requestForm",
  "status": "ON",
  "formFormat": "JSON"
}
```

Here, please note that the form field is a double serialized JSON as string : "{"layout": {...},"schema": {...},"options": {...}}"
which makes it goes in one line and very difficult for a human to work with. If there is any commits and. changes in the form in the repo, the diffs are very difficult to find (when reviewing pull requests).
As a whole it is not human friendly and very difficult for a human to deal with.

The current pull request, makes it so that the format will become a properly formatted JSON object like:

```json
{
  "id": "e694a748-7067-47d1-91a4-614da73dda03",
  "name": "Test",
  "form": {
    "layout": {
    ...
    },
    "schema": {
    ...
    },
    "options": {
    ...
    }
  },
  "styles": null,
  "sourceType": "com.vmw.blueprint",
  "sourceId": "71ac6ebc-6a94-3c5a-8c00-2a44ddf81bce",
  "type": "requestForm",
  "status": "ON",
  "formFormat": "JSON"
}
```

This way it is more easy to work with.

### Enable support for binding workflow attributes to Configuration Element variables

Be able to bind attribute values to Configuration Element variables

To do that one have to provide the following annotation for the workflow ts file (SomeFile.wf.ts)

```json
@Workflow({
        id: "<Some Id>",
        name: "<Some Name>",
        path: "<Some Path>",
        attributes: {
            attributeName: {
            type: "string",
            bind: true,   
            value: "Some/Path/To/ConfigurationElement/variableName"
            }
        }
    })
```

Here
`bind: true` - means that we have to bind the value of the attribute to Configuration Element variable.
`value: "Some/Path/To/ConfigurationElement/variableName"` - points to the Configuration Element and variable inside the Configuration Element to bind to.

## Improvements

### `string[]`, `Test[]` and such are now supported in the @params documentation

Fix Issue #278
Wider support for types in the @params documentation

#### Previous Behavior

The @params documentation did not support `string[]`, `Test[]` and such and was not transforming it at all and leaving it as is. This was causing linting issues

#### Current Behavior

The @params documentation now supports `string[]`, `Test[]` and such and transforms it to `Array/string`, `Array/Test` and such

### Update the package.json template for generating abx actions

Fix Issue #220

#### Previous Behavior

The package.json template for generating abx actions was missing some of the recently implemented parameters.

#### Current Behavior

The package.json template for generating abx actions now contains the recently implemented parameters: base, memoryLimitMb, timeoutSec, provider and abx (inputSecrets, inputConstants, etc.).

### Add missing types to AD Plugin

Fix Issue #251

#### Previous Behavior

AD types were not implemented

#### Current Behavior

AD types were added

### Add missing attribute to SSHSession

Add `soTimeout` attribute to `SSHSession`

#### Previous Behavior

This attribute was missing

#### Current Behavior

This attributed is added


### Republish the same tenants on VCD Plugin upgrade

Republish the same tenants on VCD plugin upgrade instead of publish to all tenants.

#### Previous Behavior

* On vcd plugin upgrade, the plugin will published to all tenants.

#### New Behavior

* On vcd plugin upgrade, the plugin will published only for already published tenants.

### Fix on legacy archetype failing with vro:pull (when workflow folder path name contains special characters(&))

#### Previous Behaviour

When executing a vro:pull command on a legacy archetype, the command will fail without proper error if the workflow paths contains special characters such as '&'.

#### Current Behaviour

When executing a vro:pull command on a legacy archetype, if the workflows paths contains special character(&), the command will fail but will provide descriptive error message.

### Fix SSH Session methods type

#### Previous Behavior

When using SSH with typescript, the `error` and `state` methods has the type `void`. But technically, it returns a string. VSCode highlight it as an error and the complication failed. The same method is working in JS (obviously). Example from the built-in Workflow. Variable `error` and `state` has type `String`.

#### Current Behavior

Method `error` and `state` should return type `String` instead of type `void`

### Upgrade VCD archetype to support Angular 15

VMware Cloud Director v10.6 is going to drop support for Angular v9 or less.

#### Previous Behaviour

VMware Cloud Director archetype is using:

* node v12
* angular v8
* clarity v2
* rxjs v6
* webpack v4

The old archetype can still be bootstrapped with:
`mvn archetype:generate \
    -DinteractiveMode=false \
    -DarchetypeGroupId=com.vmware.pscoe.vcd.archetypes \
    -DarchetypeArtifactId=package-vcd-ng-angular8-archetype \
    -DgroupId=org.example \
    -DartifactId=sample \
    -DlicenseUrl= \
    -DlicenseHeader= \
    -DlicenseTechnicalPreview=false`

#### Current Behaviour

VMware Cloud Director archetype is using:

* node v16+
* angular v15
* clarity v15
* rxjs v7
* webpack v5

The new archetype can be bootstrapped with:
`mvn archetype:generate \
    -DinteractiveMode=false \
    -DarchetypeGroupId=com.vmware.pscoe.vcd.archetypes \
    -DarchetypeArtifactId=package-vcd-ng-archetype \
    -DgroupId=org.example \
    -DartifactId=sample \
    -DlicenseUrl= \
    -DlicenseHeader= \
    -DlicenseTechnicalPreview=false`

#### Related issue

<https://github.com/vmware/build-tools-for-vmware-aria/issues/180>

### Convert VcKeyAnyValue from interface to class

#### Previous Behaviour

It was an interface, because in JS API it is an interface, but actually, it is acting as a class and must to be initialized to work

#### Current Behaviour

Converted to class and constructor is added

## Upgrade procedure

