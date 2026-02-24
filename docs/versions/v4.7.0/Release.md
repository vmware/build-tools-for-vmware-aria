# v4.7.0

## Breaking Changes


## Deprecations



## Features



### *Support of notification scenarios by vrang*

Added support for Service broker Notification scenarios to vrang projects.
New scenarios: category added to content.yaml supporting options for none, all, or listed scenarios by name.
Archetype was changed to contain the new category. Use vra-ng:pull to read scenarios to be customized.
Packaging of scenarios is supported as well as the following operations:
- vra-ng:pull - read scenarios from vRA. Reading customized scenario if available, default configuration otherwise.
- vrealize:push - create or update scenario customization.
- vrealize:clean - delete scenario customization and reset to the default.

### *Support for .vroignore file*

Added support for .vroignore file (default name) for the purpose of excluding files from being processed by certain operations during build.
A custom file name can be specified in the "vroIgnoreFile" property in the vRO project's pom.xml file:

example excerpt from vRO project pom.xml:
```xml
  <properties>
    <vroIgnoreFile>custom vroignore file</vroIgnoreFile>
    <!-- other properties -->
  </properties>

```

The file has a fixed structure that will be  (re-)generated on build, consisting of:
- categories (rows, prefixed by a single #) - e.g. ```# General```
- glob patterns (each on separate row, no leading/trailing spaces) - e.g. ```**/*.helper.ts```
- comments (rows prefixed by ##) - e.g. ```## The following pattern was added to exclude files with specific extensions from the package```

Patterns and comments are resolved by the category they are under.
On build, any additional patterns or comments will be moved after the default comments/patterns for the respective category (or General, if not under one of the predefined categories).

The predefined categories are:
- General - files matching patterns under this category will be ignored by all operations
- Packaging - files matching patterns under this category will be excluded from the vRO package
- Compilation - files matching patterns under this category will be excluded from TS definitions and compilation
- Testing - files matching patterns under this category will be excluded from test coverage
- TestHelpers - files matching patterns under this category will be included in the tests but will not have TS definitions and will not be included in the test coverage or the vro package.

## Improvements


## Upgrade procedure

