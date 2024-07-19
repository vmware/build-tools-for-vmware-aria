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
3. [Example Workflow](#example-workflow)

### Workflow Decorator

Not implemented yet.

### Available Method Decorators

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

### Example Workflow

```ts
import { Workflow, Out, In, Item, RootItem, DecisionItem, WaitingTimerItem, WorkflowItem, ScheduledWorkflowItem } from "vrotsc-annotations";

@Workflow({
  name: "Example Waiting Timer",
  path: "VMware/PSCoE",
  attributes: {
    waitingTimer: {
      type: "Date"
    },
    counter: {
      type: "number"
    },
    first: {
      type: "number"
    },
    second: {
      type: "number"
    },
    result: {
      type: "number"
    },
    workflowScheduleDate: {
      type: "Date"
    },
    scheduledTask: {
      type: "Task"
    }
  }
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
    linkedItem: "9e4503db-cbaa-435a-9fad-144409c08df0"
  })
  public callOtherWf(@In first: number, @In second: number, @Out result: number) {
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

  @Item({ target: "end" })
  public printScheduledDetails(@In scheduledTask: Task) {
    System.log(`Scheduled task: ${scheduledTask.id}, [${scheduledTask.state}]`);
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
  }
}
```
