# Unit tests component

Enables creating a test bed and running unit tests for a TypeScript project.

## Configuration

### Unit test framework

The default options are Jasmine and Jest. You can specify which one to use via properties in the pom file of the TypeScript module, example:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project ...>
  ...
  <groupId>com.vmware.pscoe.CLIENT</groupId>
  <artifactId>SOLUTION.vro</artifactId>
  <version>1.0.0-SNAPSHOT</version>
  <packaging>package</packaging>
  ...
  <properties>
    <generated.from>package-typescript-archetype</generated.from>
    ...
    <test.framework.package>jasmine</test.framework.package>
    ...
  </properties>
  ...
</project>
```

Supported properties:
- `test.framework.package` - specifies which framework to use to run the unit tests, supported values: `jasmine`, `jest`, optional, defaults to `jasmine`.
- `test.framework.version` - the version of the framework npm package to use, should be one of the versions available for the specified framework npm package. If not provided will use:
  - `^4.0.2` for `jasmine` for backward comaptibility.
  - `latest` for `jest`.
- `test.framework.runner` - enables using a specific runner, currently only used for `jest`, optional, supported values: `swc`, if omitted will use the default runner.
- `test.framework.jasmine.reporters.version` - version of the Jasmine reporters package to use when using the `jasmine` option for the framework selection, optional, defaults to `^2.5.2` for backward compatibility if omitted.
- `test.ansicolors.version` - The `ansi-colors` npm package is used in the Jasmine reporters configured for the tests, optional, defaults to `^4.1.1` for backward compatibility if omitted.

#### Configuration examples
1. **Jasmine, backwards compatiblе.**  
No changes are needed. The configuration is fully aligned with the current behavior.
1. **Jasmine, custom version.**  
Example of property values for working with the latest package versions:
```xml
<test.framework.package>jasmine</test.framework.package>
<test.framework.version>latest</test.framework.version>
<test.framework.jasmine.reporters.version>latest</test.framework.jasmine.reporters.version>
<test.ansicolors.version>latest</test.ansicolors.version>
```
1. **Jest, basic usage.**  
Example of property values for working with the latest package versions:
```xml
<test.framework.package>jest</test.framework.package>
```
1. **Jest, custom version.**  
Example of property values for working with the latest package versions:
```xml
<test.framework.package>jest</test.framework.package>
<test.framework.version>latest</test.framework.version>
<test.framework.runner>swc</test.framework.runner>
```

**Custom configuration**
TypeScript project unit tests are triggered via `mvn clean test` or `mvn clean package -Pbundle-with-installer`. During this process a `target` folder is created. The actual tests folder is located at `target/vro-tests`. The user can copy any files from this folder to a `unit-tests.config` folder in the root project path. These files will be used in any next unit test runs.  
Example:
Copying the `package.json`, `jasmine.config.json`, `run-jasmine.js` and `.nycrc` files from the `target/vro-tests` into a `unit-tests.config` and modifying it any way desired.  
The next time the unit tests are triggered all these files will be copied from `unit-tests.config` to the `target/vro-tests` folder and used to bootstrap the unit tests execution.  

### Coverage

TBD

Example of supported properties in the pom file of a TypeScript project.
```xml
<!-- example pom.xml file -->
<?xml version="1.0" encoding="UTF-8"?>
<project ...>
  ...
  <groupId>com.vmware.pscoe.CLIENT</groupId>
  <artifactId>SOLUTION.vro</artifactId>
  <version>1.0.0-SNAPSHOT</version>
  <packaging>package</packaging>
  ...
  <properties>
    <generated.from>package-typescript-archetype</generated.from>
    ...
    <test.coverage.enabled>true</test.coverage.enabled>
    <test.coverage.reports>text,html,clover,cobertura,lcovonly</test.coverage.reports>
    <test.coverage.thresholds.error>0</test.coverage.thresholds.error>
    <test.coverage.thresholds.warn>0</test.coverage.thresholds.warn>
    <test.coverage.thresholds.branches.error>0</test.coverage.thresholds.branches.error>
    <test.coverage.thresholds.branches.warn>0</test.coverage.thresholds.branches.warn>
    <test.coverage.thresholds.lines.error>0</test.coverage.thresholds.lines.error>
    <test.coverage.thresholds.lines.warn>0</test.coverage.thresholds.lines.warn>
    <test.coverage.thresholds.functions.error>0</test.coverage.thresholds.functions.error>
    <test.coverage.thresholds.functions.warn>0</test.coverage.thresholds.functions.warn>
    <test.coverage.thresholds.statements.error>0</test.coverage.thresholds.statements.error>
    <test.coverage.thresholds.statements.warn>0</test.coverage.thresholds.statements.warn>
    <test.coverage.perfile>true</test.coverage.perfile>
    ...
  </properties>
  ...
</project>
```
