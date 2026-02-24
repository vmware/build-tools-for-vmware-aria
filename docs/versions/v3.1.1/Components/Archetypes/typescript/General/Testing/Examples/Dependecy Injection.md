# Dependecy Injection

Example on how to test Dependency Injection

## Overview

Dependency injection is providing the objects that an object needs (its dependencies) instead of having it construct them itself. It's a very useful technique for testing, since it allows dependencies to be mocked or stubbed out.

Dependencies can be injected into objects by constructor injection or setter injection.

## Table Of Contents

1. [Example](#example)

### Example

Test

```typescript
export class NoDependencyInjection {
  protected restHost: RESTHost;

  // We can retrieve the RESTHost from a config element, but we have no way of creating mocks/spies/expectations
  init() {
    const restHostConfig = new ConfigElementAccessor('ing/testing/dependencyInjectionRestHost');
    this.restHost = restHostConfig.get<RESTHost>('restHost');
  }

  doApiCall() {
    const request = this.restHost.createRequest("GET", "/ping", "");

    return request.execute();
  }
}

// To test this, create a mock for restHostConfig
export class SomeDependencyInjection {
  private restHost: RESTHost;

  // Retrieve the RESTHost from a config element
  constructor(restHostConfig: ConfigElementAccessor) {
    this.restHost = restHostConfig.get<RESTHost>('restHost');
  }

  doApiCall() {
    const request = this.restHost.createRequest("GET", "/ping", "");

    return request.execute();
  }
}

// Even Better, we can test this by creating a mock for restHost directly, always delegate logic to the parent, not the child!
// Let the action/workflow that needs this class, fetch everything that is needed.
export class GoodDependencyInjection {
  constructor(private restHost: RESTHost) {
  }

  doApiCall() {
    const request = this.restHost.createRequest("GET", "/ping", "");

    return request.execute();
  }
}

// Test oriented dependency injection. Useful in a LOT of cases. In your normal execution, you want to retrieve the value
// from some config element, but in test cases, you have an out
export class TestRelatedDependencyInjection {
  // When we create this class in our workflows or actions, do not pass a parameter, but when writing tests, create a mocked restHost
  constructor(private restHost?: RESTHost) {
    if (!restHost) {
      const restHostConfig = new ConfigElementAccessor('ing/testing/dependencyInjectionRestHost');
      this.restHost = restHostConfig.get<RESTHost>('restHost');
    }
  }

  doApiCall() {
    const request = this.restHost.createRequest("GET", "/ping", "");

    return request.execute();
  }
}

export class NoDependencyInjectionTestDouble extends NoDependencyInjection {
  // @ts-ignore
  constructor(protected restHost: RESTHost) {
  }
}
```
