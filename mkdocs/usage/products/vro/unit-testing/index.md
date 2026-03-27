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

Creates a mock object that spies on multiple methods at once. Pass the name of the object and an array of method string names.

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

---

## Code Coverage

### Enabling Code Coverage

Start by adding the testing profile to your `~/.m2/settings.xml` or project-specific `pom.xml`.

```xml
<profile>
    <id>pscoe-testing</id>
    <properties>
        <test.coverage.enabled>true</test.coverage.enabled>
        <test.coverage.reports>text,html,clover,cobertura,lcovonly</test.coverage.reports>
    </properties>
</profile>
```

Activate the profile by adding it to `<activeProfiles>`. Output files are generated in `<PROJECT_DIR>/target/vro-tests/coverage/`.

```xml
<activeProfiles>
    <activeProfile>pscoe-testing</activeProfile>
</activeProfiles>
```

### Reporters

The toolchain supports many different code coverage reporters. Internally we use a tool called IstanbulJS, so the supported reporters and their documentation can be found here: [Using Alternative Reporters](https://istanbul.js.org/docs/advanced/alternative-reporters/).

After enabling a reporter and running `mvn clean test`, you can see the output files in: `<PROJECT_DIR>/target/vro-tests/coverage/`

### Setting Thresholds

Using `<test.coverage.thresholds.error>` creates hard limits for code coverage in your local builds and CI/CD pipelines. If the specified percentage is not met, the tests are considered failed and build operations fail. This introduces a very good quality gate. It is suggested to start with a lower threshold for older projects and higher threshold for new projects. A good example of setting an error threshold would be around 60-70 and a possible warning threshold in the 80s.

Individual overrides can also be set for branches, lines, functions, and statements.

#### Per-file Configuration

It is possible to set code coverage per file basis. Set custom --coverage-thresholds, if any file in the project drops below those thresholds, the build fails. Enable by setting `<test.coverage.perfile>true</test.coverage.perfile>` in your `~/.m2/settings.xml` testing profile.
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

### File Exclusion

Files can be excluded from code coverage by naming them with the pattern `*.helper.[tj]s`. Custom patterns can also be defined via the `.vroignore` file. For more details refer to the [`vroIgnoreFile` section](../common/vroignore.md).

---

## Test Helpers

Helpers are testing files that are compiled and can be used in your testing setup, but they do not generate code coverage and are not be pushed to vRO. Mocks and repetitive test data setups are typically defined in these Helper files.

* **Naming Convention**: `filename.helper.ts`, `filename.helper.js`, or `filename_helper.js`.
* **Location**: Helper files must be located in any folder under `src/`, though the recommended place is `src/tests/helpers`.
* **Usage**: During testing, you can use these files by importing them normally (e.g., `import testHelper from "./testHelper.helper";`).
* **Exclusions**: You can also define custom patterns to ignore these files via the `.vroignore` file. For more details refer to the [`vroIgnoreFile` section](../common/vroignore.md).

---

## Useful Hints & FAQ

* **Where can I find execution logs?**
  You have access to the output from `System.info`, `error`, `warn`, and the logger in the `target/vro-tests/logs` folder.

* **Can I test workflows?**
  Testing workflows is currently not supported. As a general rule of thumb, keep your Workflows as minimal as possible. Abstract the logic away from the Workflows and put it in an Action that is easily testable.

* **What if VS Code is not showing intellisense for Jasmine keywords?**
  Execute this command in the VS Code terminal to install the necessary types:
  ```bash
  npm i @types/jasmine
  ```

* **How do I identify if code is running in the NodeJS test environment vs. vRO?**
  If you need to simulate different behavior specific to NodeJS, you can check the environment. The following statement will evaluate to false when running in Aria Automation Orchestrator:
  ```javascript
  if (typeof module !== "undefined" && module.exports) {
      // do the coding
  }
  ```

  * **Can I use Jasmine Helpers?**
  Native Jasmine helpers are not supported, though the toolchain does inject the vRO Runtime with a helper.
