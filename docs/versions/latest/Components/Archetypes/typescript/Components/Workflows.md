# Workflows

How to use Aria Orchestrator Workflows and available decorators.

## Overview

## Table of Contents

1. [Argument Decorators](#argument-decorators)
2. [Workflow Decorators](#workflow-decorators)
3. [Available Method Decorators](#available-method-decorators)
   - [`@Item`](#item)
   - [`@WaitingTimerItem`](#waitingtimeritem)
   - [`@DecisionItem`](#decisionitem)
   - [`@RootItem`](#rootitem)
   - [`@DefaultErrorHandler`](#defaulterrorhandler)
   - [`@WorkflowEndItem`](#workflowenditem)
4. [Example Workflow](#example-workflow)

### Argument Decorators

- `@In` - Used to bind an input to a function.
- `@Out` - Used to bind an output to a function.
- `@Err` - Used to bind an error to a function.

### Workflow Decorators

Not implemented yet.

#### `@DefaultErrorHandler`

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

#### `@ScheduledWorkflowItem`

The decorator is used to specify a scheduled workflow item that will be called.

##### Supported Parameters

- `target` - The name of the next in line item. Same as `@Item`.
- `linkedItem` - The ID of the workflow to schedule.

In order to bind inputs and outputs, you do it with the `@In` and `@Out` decorators. This is the same way we do it for other items.

##### Inputs

Special input is needed for the ScheduledWorkflowItem.

- `workflowScheduleDate` - {Date} is required. The name **must** be `workflowScheduleDate`. If this is missing an error is thrown. We don't check if the type is `Date` but Aria Orchestrator will complain.

##### Outputs

Special output is needed for the ScheduledWorkflowItem.

- `scheduledTask` - {Task} is optional. If it's missing nothing will happen, if it's added, then the name **must** be `scheduledTask`. This is the task that is scheduled.

#### `@RootItem`

This is a meta decorator. Add this to whichever function you want to be the entry point of the workflow.


#### `@AsyncWorkflowItem`

##### Supported Parameters

- `@AsyncWorkflowItem({target: "", linkedItem: "" })`
  - `target` - The name of the next in line item.
  - `linkedItem` - The ID of the workflow to call


##### Outputs

Special output is needed for the AsyncWorkflowItem.

- `wfToken` - {WorkflowToken} is required. The name **must** be `wfToken`. If this is missing an error is thrown. We don't check if the type is `WorkflowToken` but Aria Orchestrator will complain.

##### Inputs

No special inputs are needed for the AsyncWorkflowItem.

#### `@ActionItem`

##### Supported Parameters

- `@ActionItem({target: "", scriptModule: "" })`
  - `target` - The name of the next in line item. Same as `@Item`.
  - `scriptModule` - The path of the action you want to call and the action name, separated by `/`. Example: `com.vmware.pscoe.library.general/echo`.

In order to bind inputs and outputs, you do it with the `@In` and `@Out` decorators. This is the same way we do it for other items.

##### Outputs

There is a requirement to have only one output, and it will be of type `ActionResult`.


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
  ScheduledWorkflowItem
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
    workflowScheduleDate: {
      type: "Date"
    },
    scheduledTask: {
      type: "Task"
    }
    errorMessage: {
      type: "string",
    },
    wfToken: {
      type: "WorkflowToken"
    },
    actionResult: {
      type: "ActionResult"
    }
  },
})
export class HandleNetworkConfigurationBackup {
  @DecisionItem({ target: "waitForEvent", else: "prepareItems" })
  public decisionElement(waitingTimer: Date) {
    return waitingTimer !== null;
  }

  @Item({ target: "callOtherWf" })
  public prepareItems(@In @Out first: number, @In @Out second: number, @In @Out workflowScheduleDate: Date) {
    first = 1;
    second = 2;
    workflowScheduleDate = System.getDate("1 minute from now", undefined);
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


  @Item({ target: "scheduleOtherWf" })
  public print(@In result: number) {
    System.log("Result: " + result);
  }

  @ScheduledWorkflowItem({
    target: "printScheduledDetails",
    linkedItem: "9e4503db-cbaa-435a-9fad-144409c08df0"
  })
  public scheduleOtherWf(@In first: number, @In second: number, @In workflowScheduleDate: Date, @Out scheduledTask: Task) {
  }

  @Item({ target: "asyncCall" })
  public printScheduledDetails(@In scheduledTask: Task) {
    System.log(`Scheduled task: ${scheduledTask.id}, [${scheduledTask.state}]`);
  }


  @AsyncWorkflowItem({
    target: "printAsync",
    linkedItem: "9e4503db-cbaa-435a-9fad-144409c08df0"
  })
  public asyncCall(@In first: number, @In second: number, @Out wfToken: WorkflowToken) {
  }

  @Item({ target: "printActionResult" })
  public printAsync(@In wfToken: WorkflowToken) {
    System.log(`Workflow token: ${wfToken.id} and state: ${wfToken.state}`);
  }

  @ActionItem({
    target: "printActionResult",
    scriptModule: "com.vmware.pscoe.onboarding.sgenov.actions/test"
  })
  public callTestAction(@In first: number, @In second: number, @Out actionResult: ActionResult) {
  }

  @Item({ target: "end" })
  public printActionResult(@In actionResult: ActionResult) {
    System.log(`Action result: ${actionResult.getResult()}`);
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
