# Code Coverage

Details on how to enable, configure and read code coverage

## Overview

After code has been compiled to javascript (from typescript), a testbed is created. A VRO Runtime is inserted so you can use some modules natively (things like Workflow, Properties, LockingSystem, Server, System, etc.). After the test bed is created, jasmine is run either through IstanbulJS or directly (depending if code coverage is enabled or not).

> **Jasmine version: 4.0.2**

## Table Of Contents

1. [Enabling Code Coverage](#enabling-code-coverage)
2. [Reporters](#reporters)
3. [Setting Thresholds](#setting-thresholds)
4. [How to exclude files from code coverage](#how-to-exclude-files-from-code-coverage)
5. [Best Practices](#best-practices)
6. [Limitations](#limitations)
7. [FAQ](#faq)

### Enabling Code Coverage

Start by Adding the following profile to your `~/.m2/settings.xml` file

```xml
<profile>
    <id>pscoe-testing</id>
    <properties>
        <test.coverage.enabled>true</test.coverage.enabled>
        <test.coverage.reports>text,html,clover,cobertura,lcovonly</test.coverage.reports>
    </properties>
</profile>
```

Activate the profile by adding it to the `<activeProfiles></activeProfiles>`.

```xml
<activeProfiles>
    <activeProfile>pscoe-testing</activeProfile>
</activeProfiles>
```

### Reporters

The toolchain supports many different code coverage reporters. Internally we use a tool called IstanbulJS, so the supported reporters and their documentation can be found here: [Using Alternative Reporters](https://istanbul.js.org/docs/advanced/alternative-reporters/).

After enabling a reporter and running `mvn clean test`, you can see the output files in: `<PROJECT_DIR>/target/vro-tests/coverage/`

### Setting Thresholds

When setting the thresholds for code coverage if you set the `<test.coverage.thresholds.error>`, if the percentage is not met when running the tests, the tests will be considered as failed. Including such thresholds into your CI/CD pipeline will introduce hard limits that developers must follow when writing code. This way you can introduce a very good quality gate. It is suggested to start with a lower threshold for older projects and higher threshold for new projects. A good example of setting an error threshold would be around 60-70 and a possible warning threshold in the 80s.

Individual overwrites for thresholds can be set for branches, lines, functions and statements. These individually overwrite the default ones (`test.coverage.thrsholds.error|warn`).

#### Per-file

Code coverage per file bases. Set custom --coverage-thresholds, if any file in the project drops below those thresholds, the build will fail.

Enable by setting `<test.coverage.perfile>true</test.coverage.perfile>` in your `~/.m2/settings.xml` testing profile.

Refer to InstanbulJS documentation for more information: [https://github.com/istanbuljs/nyc](https://github.com/istanbuljs/nyc).

```xml
<profile>
    <id>pscoe-testing</id>
    <properties>
        <test.coverage.enabled>true</test.coverage.enabled>
        <test.coverage.reports>text,html,clover,cobertura,lcovonly</test.coverage.reports>

        <!--   Add these:-->
        <test.coverage.thresholds.error>70</test.coverage.thresholds.error>
        <test.coverage.thresholds.warn>80</test.coverage.thresholds.warn>

        <test.coverage.thresholds.branches.error>60</test.coverage.thresholds.branches.error>
        <test.coverage.thresholds.branches.warn>70</test.coverage.thresholds.branches.warn>
        <test.coverage.thresholds.lines.error>60</test.coverage.thresholds.lines.error>
        <test.coverage.thresholds.lines.warn>70</test.coverage.thresholds.lines.warn>
        <test.coverage.thresholds.functions.error>60</test.coverage.thresholds.functions.error>
        <test.coverage.thresholds.functions.warn>70</test.coverage.thresholds.functions.warn>
        <test.coverage.thresholds.statements.error>60</test.coverage.thresholds.statements.error>
        <test.coverage.thresholds.statements.warn>70</test.coverage.thresholds.statements.warn>
        <test.coverage.perfile>true</test.coverage.perfile>
    </properties>
</profile>
```

### How to exclude files from code coverage

Files can be excluded from code coverage by naming them following the pattern: `*.helper.[tj]s`.

During testing, you will be able to use these files by specifying them normally (`import testHelper from "./testHelper.helper";`).

### Best Practices

> **Naming convention**

```txt
"**/?(*.)+(spec|test).[j|t]s(x)"
 
//example names for javascript file
MyTests.test.js
MyTests.spec.js
 
//example names for typescript file
MyTest.test.ts
MyTest.spec.ts
```

### Limitations

> **actions-package**

For actions-package projects, all test must be placed under `src/test` folder in order to be compiled and executed and packaged correctly.

### FAQ

#### Can I use Jasmine Helpers?

Jasmine helpers are not supported. We are injecting the vRO Runtime with a helper tho.

#### Can I test workflows?

This is currently not supported. As a general rule of thumb, keep your Workflows as minimal as possible. Abstract the logic away from the workflows and put it in an Action that is easily testable.
