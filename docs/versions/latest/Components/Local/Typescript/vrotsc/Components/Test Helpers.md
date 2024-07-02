# Test Helpers

Test helpers are compiled to a different directory than the one specified for actions.

## Overview

`vrotsc` gives you the ability to specify a different directory for the test helpers. Usually these files need to be excluded from vRO packaging since they are used only for testing purposes.

## Table Of Contents

1. [How is a test helper defined?](#how-is-a-test-helper-defined)
2. [How is a test helper compiled?](#how-is-a-test-helper-compiled)
3. [Why is there no transformer file for a testHelper](#why-is-there-no-transformer-file-for-a-testhelper)
4. [How to import a test helper](#how-to-import-a-test-helper)

### How is a test helper defined?

A test helper is defined by following the pattern: `*.helper.[tj]s`.

### How is a test helper compiled?

Due to VROES limitations, during compilation, the file name is changed from : `example.helper.ts` to `example_helper.js`. VROES works in a way where all dots AFTER the final slash will be replaced with underscores (e.g. `com.vmware.pscoe.vrotsc.actions/test.helper` gets compiled to `com.vmware.pscoe.vrotsc.actions.test_helper` ).

### Why is there no transformer file for a testHelper?

A Test Helper is essentially an action. Where the test helper is put is decided inside the action transformer.

### How to import a test helper?

The same way you would another action.

#### Example

`test.helper.ts`:

```typescript
export default function (){
    return Math.random();
}
```

`example.ts`:

```typescript
import testHelper from "./test.helper";

export default function () {
    return testHelper();
}
```
