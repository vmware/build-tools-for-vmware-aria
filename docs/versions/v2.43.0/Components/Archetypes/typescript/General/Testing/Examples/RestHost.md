# RestHost

Example on how to test RestHost

## Overview

## Table Of Contents

1. [Example](#example)

### Example

Test:

```typescript
describe("ApiCall", () => {
  it('should do an api call', function () {
    const restHostTestDouble = jasmine.createSpyObj<RESTHost>("RESTHost", ["createRequest"]);
    const restRequestTestDouble = jasmine.createSpyObj<RESTRequest>("RESTRequest", ["execute"]);
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

Code To Test:

```typescript
export default class RestHostExample {
  constructor(private restHost: RESTHost) {
  }

  doApiCall() {
    const restRequest: RESTRequest = this.restHost.createRequest("GET", "/api/v1/test", "");
    const restResponse: RESTResponse = restRequest.execute()
    return JSON.parse(restResponse.contentAsString);
  }
}
```
