# Native Actions

Example on how to test native actions

## Overview

When it comes to native actions items, using the old syntax of creating actions allow us to call the actions directly when needed. Mapping inputs and outputs according to the documentation/arguments.

## Table Of Contents

1. [Example](#example)

### Example

Test:

```typescript
const sumAction = System.getModule("com.vmware.pscoe.actions").sumAction;
 
describe("Sum Action test", () => {
    it("should add two numbers", () => {
        expect( sumAction( 1,5 ) ).toBe( 6 );
    });
})
```

Code To Test:

```typescript
(function ( numberOne: number, numberTwo: number ):number {
    return numberOne + numberTwo;
});
```
