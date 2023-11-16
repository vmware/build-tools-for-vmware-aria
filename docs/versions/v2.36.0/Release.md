# v2.36.0

## Breaking Changes


## Deprecations

### Deprecating SQLDatabaseManager.getDatabase() function

SQLDatabaseManager.getDatabase() function is removed in vRA 7.6 / Aria Automation 8 and above. The function uses name as parameter to retrieve a database. Use getDatabaseById() or getDatabases() and filter by name instead.

## Features

### Setting Default Policy for vROPs 8.12.x

Added option for setting default policy for vROPs version 8.12.x by setting it in the default-policy configuration item in the content.yaml file.

#### Relevant Documentation
If in the content.yaml file for a vROPs project the following section is present:

```yaml
default-policy: Policy Name
```

Then the 'Policy Name' will be set to the default policy in vROPs.

### Adding Aria Automation 8.x vRO Plugin Types

Developers can use on their TS projects the vRAHost type properties and functions

#### Relevant Documentation
NONE

#### Example

`example.ts`:
```typescript
export default function (vraHost:VraHost):VraGenericRestClient {
    return vraHost.createRestClient();
}
```

## Improvements

### vRA Custom Form not Enabled with vRA Version 8.11.2

#### Previous Behaviour
When pushing vRA custom forms to vRA version 8.11.2 they are not get enabled by default, thus not visible in the blueprint section.

#### Current Behaviour
When pushing vRA custom forms to vRA version 8.11.2 they are get enabled by default and visible in the blueprint section.

### Improved Handling of Empty vRA Blueprint Versions for vRA 8.12.x

#### Previous Behaviour
If the vRA returns an empty versions array when fetching of latest vRA blueprint version would throw a null pointer exception.

#### Current Behaviour
If the vRA returns an empty versions array when fetching of latest vRA blueprint version it would not throw null pointer exception and add an empty string in the blueprint version string.

### Update Deprecated Policy APIs for vROPs 8.12.x

#### Previous Behaviour
When pushing vROPs policies to vROPs 8.12.0 and above the deprecated internal policy API in vROPs is used.

#### Current Behaviour
When pushing vROPs policies to vROPs 8.12.0 and above the new public policy API in vROPs is used. The older versions of vROPs is also supported.

### Fix SSH Session exitCode type

#### Previous Behaviour
When using SSH with typescript, the `exitCode` method has the type `void`. But technically, it returns an integer. VSCode highlight it as an error and the complication failed. The same method is working in JS (obviously). Example from the built-in Workflow. Variable `exitCode` has type `Number`.

#### Current Behaviour
Method `.exitCode` should return type `Number` instead of type `void`

#### Related issue
<https://github.com/vmware/build-tools-for-vmware-aria/issues/180>

## Upgrade procedure

