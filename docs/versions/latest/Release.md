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
[//]: # (#### Relevant Documentation)

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
[//]: # (#### Relevant Documentation)

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
[//]: # (Explain in details if something needs to be done)

[//]: # (## Changelog)
[//]: # (Pull request links)
