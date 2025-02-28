# v4.2.1

## Breaking Changes


## Deprecations



## Features



## Improvements


### *Improve type definitions of Server class functions*

Improve type definitions of Server class functions by adding `| null` to the return type.

#### Previous Behavior

The return type of some function definitions in the Server class mismatched the actual return type.

#### New Behavior

The following function definitions have `| null` added to their return type:

- `Server.getWorkflowCategoryWithPath`
- `Server.getConfigurationElementCategoryWithPath`
- `Server.getResourceElementCategoryWithPath`
- `Server.getPackageWithName`
- `Server.getPolicyTemplateCategoryWithPath`

### *Fix Decision element transpilation issue*

In Typescript workflow classes, methods with the DecisionItem decorator were transpiled incorrectly into Javascript.

#### Previous Behavior

Closing brackets ('{'), e.g in if blocks, were missing from the contents of the transpiled Decision element.

#### New Behavior

Closing brackets are no longer missing from the contents of the transpiled Decision element.

## Upgrade procedure

