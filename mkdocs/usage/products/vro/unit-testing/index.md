---
title: Unit Testing Framework
---

# Unit Testing Framework

## Overview

After code has been compiled to Javascript (for `typescript` project type) and before it is packaged to an {{ products.vro_short_name }} `.package`, a testbed is created. An {{ products.vro_short_name }} Runtime is inserted so you can use some modules natively (things like Workflow, Properties, LockingSystem, Server, System, etc.). After the test bed is created, Тasmine is run either through IstanbulJS or directly (depending if code coverage is enabled or not).

!!! note
    By default Jasmine 4.0.2 is used as unit test framework unless explicitly specified otherwise.

## Limitations

> ***actions-package***

For `actions` type projects, all test must be placed under `src/test` folder in order to be compiled and executed and packaged correctly.

> ***What types of files can I test***

Only file types that can be tested are Actions - `filename.js` or `filename.ts`. Workflows can't be tested as well as configuration element and resource element files. As a general rule of thumb, keep your Workflows as minimal as possible. Abstract the logic away from the Workflows and put it in an Action that is easily testable.

> ***Naming Convention***

Testing file names must be of the type `filename.test.ts`, `filename.test.js`, `filename_test.js`  - see <!-- [Best Practices](./Code%20Coverage.md#best-practices) -->

```txt
"**/?(*.)+(spec|test).[j|t]s(x)"
 
//example names for javascript file
MyTests.test.js
MyTests.spec.js
 
//example names for typescript file
MyTest.test.ts
MyTest.spec.ts
```

## Best Practices

> ***Unit testing***

Testing individual components of software. A unit test should test one thing.

Label your test suites (describe blocks) and specs (it blocks) in a way that clearly conveys the intention of each unit test. Note that the name of each test is the title of its it preceded by all its parent describe names. Favor assertive verbs and avoid ones like "should."

Test file contains one describe() block containing multiple describes with common functionality. Each describe() must have a meaningful name. Do not include logic or mocks in describe blocks.

> ***beforeEach()***

We can execute some pieces of code before execution of each spec. For example you can create new instances here. Don't use any logic in the describe() block, only in beforeEach(). Use "this" to share variables between it and before/after blocks.

```typescript
beforeEach(() => {
    this.someClass = new SomeClass();
});

describe("Different Methods of Expect Block",function() { 
   it("Returns even or optional ", function() {
      expect(this.someClass.evenOrOdd()).toBe("even");     
   });
});
```

> ***Write Minimum Passable Tests***

If appropriate, use Jasmine's built-in matchers (such as toContain, jasmine.any, jasmine.stringMatching, ...etc) to compare arguments and results. You can also create your own matcher via the asymmetricMatch function.

```typescript
describe('Array.prototype', function() {
  describe('.push(x)', function() {
    beforeEach(function() {
      this.initialArray = [];

      this.initialArray.push(1);
    });

    it('appends x to the Array', function() {
      expect(this.initialArray).toContain(1);
    });
  });
});
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

## Jasmine Spies

A Spy is a feature that allows you to simulate the behavior of existing code and track calls to it back. It’s used to mock a function or an object.

> ***createSpy()***

Can be used when there is no function to spy on. Takes two arguments - name of the Service, method we want to mock

```typescript
let testDouble = jasmine.createSpy("Name holder.", "method");
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

## FAQ

> ***Can I use Jasmine Helpers?***

Jasmine helpers are not supported. We are injecting the vRO Runtime with a helper tho.

> ***Can I test workflows?***

This is currently not supported. As a general rule of thumb, keep your Workflows as minimal as possible. Abstract the logic away from the workflows and put it in an Action that is easily testable.
