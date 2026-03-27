---
title: Unit Testing Framework
---

# Unit Testing Framework

## Overview

After code has been compiled to Javascript (for `typescript` project types) and before it is packaged to an {{ products.vro_short_name }} `.package`, a testbed is created. An {{ products.vro_short_name }} Runtime is inserted so you can use some modules natively (things like Workflow, Properties, LockingSystem, Server, System, etc.). After the test bed is created, Jasmine is run either through IstanbulJS or directly (depending on if code coverage is enabled).

Jasmine 4.0.2 is integrated into Build Tools for VMware Aria. The testing framework is supported by three main components:

* **`vro-scripting-api`**: Simulates Aria Automation Orchestrator inside the NodeJS environment.
* **`vro-types`**: Provides type information and intellisense in VS Code for TypeScript projects.
* **`vrotest`**: Orchestrates and executes the unit tests.

!!! note
    Unit test framework is configurable. Jasmine is used by default. Default configuration is also available for Jest.

---

## Folder Structure & Naming Conventions

The location and naming of your test files depend on your project type:

* **TypeScript Projects**: Unit tests can be placed in any folder or subfolder relative to the `/src/` directory. The file name **must** end with `*.test.ts`. The test will execute, but the test code will not be added to the target package.
* **JavaScript (Actions) Projects**: All unit test files must be placed under the `/src/test/resources/` directory (or subfolders). File names **must** end with `*Test.js` or `*Tests.js`.

---

## Executing Unit Tests

Unit tests are automatically built and run by Maven as part of the regular packaging phase.

* **Run all tests and package:** `mvn clean package`
* **Run tests explicitly:** `mvn clean test`
* **Skip tests:** Append the `-DskipTests` flag to your Maven command.

**Logs & Reporting:** * Execution logs (including output from `System.log`/`warn`/`error`) can be found in `target/vro-tests/logs`.
* During the build process, test results and coverage data are automatically reported to Bamboo and made available to SonarQube for further analysis.

---

## Limitations & Specific Behaviors

* **Workflows Cannot Be Tested**: The only file types that can be tested are Actions. Workflows, configuration elements, and resource elements are not supported. Keep your Workflows as minimal as possible and abstract logic into testable Actions.
* **Shared JavaScript Context**: All unit tests within a project are executed in the *same* Javascript context. **It is critical** to use `beforeAll` and `afterAll` to prepare and clean up your environment. Leftover state from one test will affect the execution of subsequent tests.

---

## Best Practices

> ***Unit testing***

A unit test should test one specific thing. Label your test suites (`describe` blocks) and specs (`it` blocks) clearly so they read as full sentences. Do not include logic or mocks directly inside `describe` blocks; instead, use `beforeEach()`.

> ***Setup and Teardown***

Use `beforeEach()`, `afterEach()`, `beforeAll()`, and `afterAll()` to keep your test suite DRY. You can use the `this` keyword to safely share variables between the `beforeEach` and `it` blocks. 

```javascript
describe("A spec", function() {
  beforeEach(function() {
    this.foo = 0;
  });

  it("can use the `this` to share state", function() {
    expect(this.foo).toEqual(0);
  });
});
```

> ***Write Minimum Passable Tests***

Utilize Jasmine's built-in matchers (e.g., `toContain`, `jasmine.any`, `jasmine.stringMatching`, etc.) to compare arguments and results efficiently. You can also create your own matcher via the `asymmetricMatch` function.

```typescript
describe('Array.prototype', function() {
  describe('.push(x)', function() {
    beforeEach(function() {
      this.initialArray = [];
    });

    it('appends x to the Array', function() {
      this.initialArray.push(1);
      expect(this.initialArray).toContain(1);
    });
  });
});
```

---

## Jasmine Spies (Mocking)

A Spy simulates the behavior of existing code (like DB calls, Web Services, or external systems) and tracks calls made to it. Spies only exist in the `describe` or `it` block in which they are defined and are removed after each spec.

> ***createSpy()***

Creates a "bare" spy when there is no existing function to mock. It tracks calls and arguments but has no implementation behind it.

```javascript
var readFromDB = jasmine.createSpy('readFromDB');
readFromDB('some', 'fake', 'data'); 
expect(readFromDB).toHaveBeenCalledWith("some", "fake", "data");
```

> ***createSpyObj()***

`createSpyObj()` creates a mock object that will spy on one or more methods. It returns an object that has a property for each string that is a spy. It takes as first argument the name of a Service and as a second an array of strings of all the methods that we want to mock.

```typescript
let testDouble = jasmine.createSpyObj<T>("Name holder. Same as the type, in this case T", ["Array of strings with all functions that will be overwritten"]);
```

```typescript
describe("ApiCall", () => {
  it('should do an api call', function () {
    const restHostTestDouble = jasmine.createSpyObj<RESTHost>("RESTHost", ["createRequest"]);

    const restRequestTestDouble = jasmine.createSpyObj<RESTRequest>("RESTRequest", ["execute"]);

        // Properties mock
    const restResponseTestDouble = jasmine.createSpyObj<RESTResponse>("RESTResponse", [], {contentAsString: JSON.stringify({test: 2})});

    restHostTestDouble.createRequest.and.returnValue(restRequestTestDouble);
    restRequestTestDouble.execute.and.returnValue(restResponseTestDouble);

    const restHostExample = new RestHostExample(restHostTestDouble);

    const response = restHostExample.doApiCall();

    expect(response.test).toBe(2);
    expect(restHostTestDouble.createRequest).toHaveBeenCalledTimes(1);
    expect(restHostTestDouble.createRequest).toHaveBeenCalledWith("GET", "/api/v1/test", "");
    expect(restRequestTestDouble.execute).toHaveBeenCalledTimes(1);
  });
})
```

## Code Coverage

Details on how to enable, configure and read code coverage.

### Enabling Code Coverage

Start by Adding the following profile to your `~/.m2/settings.xml` file.

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

#### Per-file Configuration

It is possible to set code coverage per file basis. Set custom --coverage-thresholds, if any file in the project drops below those thresholds, the build will fail.

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
You can also define custom patterns via the `.vroignore` file. For more details please review the [`vroIgnoreFile` section](../common/vroignore.md).

## Test Helpers

Helpers are testing files. Naming convention is - `filename.helper.ts`, `filename.helper.js`, `filename_helper.js`. They are compiled, can be used in testing, no code coverage and will not be pushed to vRO. Mocks are defined in Helper files.

You can also define custom patterns via the `.vroignore` file. For more details please review the [`vroIgnoreFile` section](../common/vroignore.md).

During testing, you will be able to use these files by specifying them normally (`import testHelper from "./testHelper.helper";`).

### Known Issues

Helper files must be located in any folder under `src/`, recommended place is `src/tests/helpers`.

## FAQ

> ***Can I use Jasmine Helpers?***

Jasmine helpers are not supported. We are injecting the vRO Runtime with a helper tho.

> ***Can I test workflows?***

This is currently not supported. As a general rule of thumb, keep your Workflows as minimal as possible. Abstract the logic away from the workflows and put it in an Action that is easily testable.
