# Getting Started

## Overview

After code has been compiled to javascript ( from typescript ), a testbed is created. A VRO Runtime is inserted so you can use some modules natively (things like Workflow, Properties, LockingSystem, Server, System, etc.). After the test bed is created, jasmine is run either through IstanbulJS or directly( depending if code coverage is enabled or not ).

> **Jasmine version: 4.0.2**

## Table Of Contents

1. [Limitations](#limitations)
2. [Best Practices](#best-practices)
3. [FAQ](#faq)

### Limitations

> ***actions-package***

For actions-package projects, all test must be placed under `src/test` folder in order to be compiled and executed and packaged correctly.

> ***What types of files can I test***

Only file types that can be tested are Actions - filename.ts. Workflows can't be tested as well as config files. As a general rule of thumb, keep your Workflows as minimal as possible. Abstract the logic away from the workflows and put it in an Action that is easily testable.

### Best Practices

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

> ***Naming convention***

```txt
"**/?(*.)+(spec|test).[j|t]s(x)"
 
//example names for javascript file
MyTests.test.js
MyTests.spec.js
 
//example names for typescript file
MyTest.test.ts
MyTest.spec.ts
```

### FAQ
