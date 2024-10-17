# Global Classes

Example on how to test global classes

## Overview

When it comes to Static Global Classes (like vCACCAFEFilterParam for example), they need to be defined before each test, with the methods that you want to use inside your test.

## Table Of Contents

1. [Example](#example)

### Example

Example Class:

```typescript
export default function ( date: string): string {
  Server.log( "Getting date" );
  return vCACCAFEFilterParam.date( date );
}
```

Example test:

```typescript
import testOne from "./testOne";

describe('', function () {
  beforeEach(()=>{
    (<any> vCACCAFEFilterParam)   = {
      date: function ( date: string ) {
        throw new Error( "Not Supported" );
      }
    };
  });

  afterEach(() => {
    (<any> vCACCAFEFilterParam)   = undefined;
  });

  it('should ', function () {
    spyOn( Server, "log" ).and.callFake( () => {} );
    spyOn( vCACCAFEFilterParam, "date" ).and.returnValue( "spy" );

    expect( testOne( 'test' ) ).toBe( "spy" );

    expect( vCACCAFEFilterParam.date ).toHaveBeenCalledTimes( 1 );
    expect( Server.log ).toHaveBeenCalledTimes( 1 );
  });
});
```
