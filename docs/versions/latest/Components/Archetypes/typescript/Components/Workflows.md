# Workflows

How to use Aria Orchestrator Workflows and available decorators.

## Overview

## Table of Contents

1. [Workflow Decorator](#workflow-decorator)
2. [Available Method Decorators](#available-method-decorators)
   - [`@Item`](#item)
   - [`@WaitingTimerItem`](#waitingtimeritem)
   - [`@DecisionItem`](#decisionitem)
   - [`@RootItem`](#rootitem)
   - [`@WorkflowEndItem`](#workflowenditem)
3. [Example Workflow](#example-workflow)

### Workflow Decorator

Not implemented yet.

### \*New `DefaultErrorHandler` decorator for Workflows

This decorator is used to specify a default error handler. It can be bound either to a workflow item component or workflow end.

#### Supported Parameters

- `target` - target item to be attached to the default error handler, could be one of workflow item or workflow end.
- `exceptionVariable` - Exception variable that will hold the exception data when triggered.

In order to bind inputs and outputs, you do it with the `@In` and `@Out` decorators. This is the same way we do it for other items.

Example:

```typescript
import {
  Workflow,
  RootItem,
  In,
  Out,
  Item,
  DefaultErrorHandler,
  WorkflowEndItem,
} from "vrotsc-annotations";

@Workflow({
  name: "Default Error Handler Custom Item",
  path: "VMware/PSCoE",
  description:
    "Default error handler workflow with error handler redirecting to a workflow item",
  attributes: {
    errorMessage: {
      type: "string",
    },
  },
})
export class HandleDefaultError {
  @RootItem()
  public initiateWorkflow() {
    System.log("Initiating workflow execution");
  }

  @Item({
    target: "workflowEnd",
  })
  public processError(@In errorMessage: string) {
    System.log(
      `Processing error using custom task with message '${errorMessage}'`
    );
  }

  @DefaultErrorHandler({
    exceptionVariable: "errorMessage",
    target: "processError",
  })
  public defaultErrorHandler(@Out errorMessage: string) {
    // NOOP
  }

  @WorkflowEndItem({
    endMode: 0,
    exceptionVariable: "errorMessage",
  })
  public workflowEnd(@Out errorMessage: string) {
    System.log(`Terminating workflow with error ${errorMessage}`);
  }
}
```

### Available Method Decorators

#### `@WorkflowEndItem`

The decorator is used to specify a custom workflow end item.

##### Supported Parameters

- `endMode` - End mode of the component, could be one of 0 or 1, where 0 is exit success and 1 is error.
- `exceptionVariable` - Exception variable that will hold the exception data when triggered.
- `businessStatus` - Value of the business status in the end component.

#### `@Item`

This decorator is used to specify a scriptable task.

##### Supported Parameters

- `target` - The name of the next in line item. If this is set to `end`, it will point to the end of the workflow. If this is set to `null`, it will point to the next item or if none, the end of the wf. If this is set to a string, but it does not exist in the workflow, it will point to the end of the wf.
- `exception` - **Not implemented yet**

#### `@WaitingTimerItem`

This decorator is used to specify a waiting timer.

##### Supported Parameters

- `target` - The name of the next in line item. Same as `@Item`. This decorator expects an `@In` parameter with the name of the waiting timer. If one isn't added, the workflow will not work.

#### `@DecisionItem`

This decorator is used to specify a decision item.

##### Supported Parameters

- `target` - The name of the next in line item. Same as `@Item`.
- `else` - The name of the next in line item if the decision is false. If this is set to `end`, it will point to the end of the workflow. If this is set to `null`, it will point to the next item or if none, the end of the wf. If this is set to a string, but it does not exist in the workflow, it will point to the end of the wf.

#### `@WorkflowItem`

The decorator is used to specify a workflow item that will be called.

##### Supported Parameters

- `target` - The name of the next in line item. Same as `@Item`.
- `linkedItem` - The ID of the workflow to call.

In order to bind inputs and outputs, you do it with the `@In` and `@Out` decorators. This is the same way we do it for other items.

#### `@RootItem`

This is a meta decorator. Add this to whichever function you want to be the entry point of the workflow.

### Example Workflow

```ts
import {
  Workflow,
  Out,
  In,
  Item,
  RootItem,
  DecisionItem,
  WaitingTimerItem,
  WorkflowItem,
  WorkflowEndItem,
} from "vrotsc-annotations";

@Workflow({
  name: "Example Waiting Timer",
  path: "VMware/PSCoE",
  attributes: {
    waitingTimer: {
      type: "Date",
    },
    counter: {
      type: "number",
    },
    first: {
      type: "number",
    },
    second: {
      type: "number",
    },
    result: {
      type: "number",
    },
    errorMessage: {
      type: "string",
    },
  },
})
export class HandleNetworkConfigurationBackup {
  @DecisionItem({ target: "waitForEvent", else: "prepareItems" })
  public decisionElement(waitingTimer: Date) {
    return waitingTimer !== null;
  }

  @Item({ target: "callOtherWf" })
  public prepareItems(@In @Out first: number, @In @Out second: number) {
    first = 1;
    second = 2;
  }

  @WorkflowItem({
    target: "print",
    linkedItem: "9e4503db-cbaa-435a-9fad-144409c08df0",
  })
  public callOtherWf(
    @In first: number,
    @In second: number,
    @Out result: number
  ) {
    // NOOP
  }

  @Item({ target: "end" })
  public print(@In result: number) {
    System.log("Result: " + result);
  }

  @Item({ target: "decisionElement", exception: "" })
  public execute(@Out @In waitingTimer: Date, @Out @In counter: number): void {
    if (!counter) {
      counter = 0;
    }

    counter++;
    if (counter < 2) {
      const tt = Date.now() + 5 * 1000;
      waitingTimer = new Date(tt);
    } else {
      waitingTimer = null;
    }

    System.log("Counter: " + counter);
    System.log("Waiting Timer: " + waitingTimer);
  }

  @Item({ target: "execute", exception: "" })
  @RootItem()
  public start() {
    System.log("Starting workflow");
  }

  @WaitingTimerItem({ target: "execute" })
  public waitForEvent(@In waitingTimer: Date) {
    // NOOP
  }

  @WorkflowEndItem({
    endMode: 0,
    exception: "errorMessage",
    businessStatus: "Bad",
  })
  public workflowEnd() {
    // NOOP
  }
}
```
