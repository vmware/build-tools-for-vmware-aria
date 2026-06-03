# Jasmine Spies

## Overview

A Spy is a feature that allows you to simulate the behavior of existing code and track calls to it back. It’s used to mock a function or an object.

## Table Of Contents

1. [createSpy()](#createspy)
2. [createSpyObj()](#createspyobj)

### createSpy()

Can be used when there is no function to spy on. Takes two arguments - name of the Service, method we want to mock

```typescript
let testDouble = jasmine.createSpy("Name holder.", "method");
```

### createSpyObj()

createSpyObj() creates a mock object that will spy on one or more methods. It returns an object that has a property for each string that is a spy. It takes as first argument the name of a Service and as a second an array of strings of all the methods that we want to mock.

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
