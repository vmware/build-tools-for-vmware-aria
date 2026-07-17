# v4.24.0

## Breaking Changes


## Deprecations



## Features



### *Add CSS as separate file to request forms in `vra-ng` projects*

CSS is now stored as a separate file and removed from the original custom form JSON definition. If no CSS is present an empty file is created for better usability.

In case of legacy content (CSS is present as in-line content in the form and no separate file exists) the original behaviour is preserved. During the next `pull` operation the content is updated to the new format.

## Improvements


### *Improved Operation Logging*

#### Previous Behavior
Push, pull, and clean operations produced log output that was more difficult to read and analyze.

#### New Behavior
Updated log severity levels to provide clearer and more easily readable output when push, pull, and clean operations are executed.

### *Improved Store Execution Order*

#### Previous Behavior
The execution order of store operations could lead to dependency-related issues between object types during push, pull, and clean operations.

#### New Behavior
Updated the store execution sequence for all three operations to ensure dependencies are processed in the correct order, reducing the likelihood of object type dependency issues.

### *Fix problem with Typescript variable name generation in inner structures due to downleveling and hoising*

Due to the downleveling of const/let to var, the behavior of the variable declarations change because of Hoisting. This can clash with the generated name of an import.

The transpiler's logic generating these unique root-level import variables (tslib and *) is updated to generate names guaranteed not to collide with TypeScript's ES5 synthesized downstream identifiers. By prefixing the sequence with _$, effectively isolating transpiler module variables.

## Upgrade procedure

