# v4.4.0

## Breaking Changes


## Deprecations



## Features



## Improvements


### *Fixed Missing Logs During Installer Run*

#### Previous Behavior

During package deployment via package installer, the logs were missing, hence making it hard to trace errors.

#### New Behavior

Log messages are now appearing during package deployment via the installer. Additional logging configuration file called *logback.xml* was added to the *etc* directory in the installer that configures the logging severity.

### *Content Sharing Policies Import*

CSPs could not be imported due to a forgotten fetch before creation.

#### Previous Behavior

When importing we were getting an error that id is expected of type UUID.String but was null

#### New Behavior

CSPs are no longer fetched prior to updating to set their ID.

### *Transform module paths for jest mocks*

Some module mocking functions of jest get module path parameter, that should be transformed to match vro-test actual file locations

#### Previous Behavior

The path is not transformed and functions fail to find the referenced file

#### New Behavior

During transpilation the paths to be transformed to the actual vro-test file location
*Note*: Path transformation is only supported for paths provided as string literals.

### *Add missing classes to VC plugin*

Add missing classes to VC plugin
  
#### Previous Behavior

`VcVirtualMachineVirtualNuma` classes were missing

#### New Behavior

The following classes were added to VC plugin:

- `VcVirtualMachineVirtualNuma`
- `VcVirtualMachineVirtualNumaInfo`

## Upgrade procedure

