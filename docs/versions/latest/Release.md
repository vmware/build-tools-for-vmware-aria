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

### Fix SSH Session additional methods type

 #### Previous Behavior

 When using SSH with typescript, the  `cmd`, `pty`, `terminal` methods has the type `void`. But technically, it returns a string. VSCode highlight it as an error and the complication failed. The same method is working in JS (obviously). Example from the built-in Workflow. Variable  `cmd`, `pty`, `terminal` has type `String`.

 #### Current Behavior

 Method  `cmd`, `pty`, `terminal` should return type `String` instead of type `void`

## Upgrade procedure
[//]: # (Explain in details if something needs to be done)

[//]: # (## Changelog:)
[//]: # (Pull request links)
