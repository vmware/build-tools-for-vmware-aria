### Configuring `.vroIgnore` File

Custom file can be added to the project containing glob patterns for file paths to be ignored during compilation, packaging and testing.

The `vroIgnoreFile` property can be added in the `pom.xml` file of an {{ products.vro_short_name }} project to specify a custom {{ products.vro_short_name }} ignore file.

* If left unspecified, the default name `.vroignore` will be used.
* If the file with the given name doesn't exist, it will be generated with default content. If it exists but does not match the expected file structure, it will be modified.

!!! note
    In Linux dot files have a special status, use a custom name in case of issues.

Example excerpt from {{ products.vro_short_name }} project `pom.xml`:
```xml
  <properties>
    <vroIgnoreFile>customVroignoreFile</vroIgnoreFile>
    <!-- other properties -->
  </properties>

```

Default .vroignore file structure:
```md
# General
## This file contains glob patterns for file paths to be ignored during compilation, packaging and testing.
## The file will be (re)generated when the project is rebuilt to maintain the default categories and patterns, ignoring blank lines and repeating comments.
## The default categories, defined as rows starting with a single '#', are 'General', 'Packaging', 'Compilation', 'Testing', 'TestHelpers'.
## Patterns not under one of those categories will be considered in the 'General' category and ignored during all operations.
## Patterns must be listed on separate rows (without in-line comments), can be negated with a single '!' at the start and will be trimmed before processing.
## The 'TestHelpers'category will contain these two patterns by default: '**/*_helper.js', '**/*.helper.[tj]s'
## Rows starting with '##' are considered as comments and will be ignored on processing.
# Packaging
## Files with these paths will not be included in the vro package.
## For Workflows, Configurations, Resources and Policies: it is recommended to use the 'General' category (will skip xml element generation).
## Otherwise the patterns below must be based on the _element name_.element_info.xml files in target/vro-sources/xml.
# Compilation
## Files with these paths will be compiled without TS definitions
# Testing
## Files with these paths will be excluded from test coverage
# TestHelpers
## Files with these paths will be included in the tests but will not have TS definitions and will not be included in the test coverage or the vro package
**/*_helper.js
**/*.helper.[tj]s
```
