# v3.1.0

## Breaking Changes


## Deprecations



## Features



### *Ability to add more files to installer bundle*

New properties have been added:

```xml
installer.included.item1
installer.included.item2
installer.included.item3
installer.included.item4
installer.included.item5
installer.included.item6
installer.included.item7
installer.included.item8
installer.included.item9
```

These properties allow you to specify files and directories to be included in the installer bundle. The files and directories will be copied to the root of the installation directory.

#### Example

```xml
    <properties>
        <installer.included.item1>old/**</installer.included.item1>
        <installer.included.item2>scripts/**</installer.included.item2>
    </properties>
```

#### Why?

You may want to include Terraform scripts, or state, ansible playbooks, install json, etc.

### *Trivy security reporting added*

This is a build improvement. Now security vulnerabilities will be reported to Github Security. Note that this will just report on the security vulnerabilities, but not fail the build. It's up to the maintainers to act upon the reported information.

### *Add Support for YAML Input and Output in Workflow Run with Installer*

Add support for YAML data in the workflow inputs / outputs during workflow run triggered by the installer.

## Improvements


### *License file is no longer part of the vro package*

#### Previous Behavior

The `THIRD-PARTY.properties` file was generated in `src/license` and it was bundled with the rest of the code by `vropkg`.

#### Current Behavior

The `THIRD-PARTY.properties` file is generated directly in the project basedir and it is not bundled with the rest of the code by `vropkg`.

## Upgrade procedure

