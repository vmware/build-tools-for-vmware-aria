# Workflows

How to use Aria Orchestrator Workflows and available decorators.

## Overview

## Table Of Contents

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

##### Supported parameters

- `target` - The name of the next in line item. If this is set to `end`, it will point to the end of the workflow. If this is set to `null`, it will point to the next item or if none, the end of the wf. If this is set to a string, but it does not exist in the workflow, it will point to the end of the wf.
- `exception` - **Not implemented yet**

#### `@WaitingTimerItem`

This decorator is used to specify a waiting timer.

##### Supported parameters

- `target` - The name of the next in line item. Same as `@Item`. This decorator expects an `@In` parameter with the name of the waiting timer. If one isn't added, the workflow will not work.

#### `@DecisionItem`

This decorator is used to specify a decision item.

##### Supported parameters

- `target` - The name of the next in line item. Same as `@Item`.
- `else` - The name of the next in line item if the decision is false. If this is set to `end`, it will point to the end of the workflow. If this is set to `null`, it will point to the next item or if none, the end of the wf. If this is set to a string, but it does not exist in the workflow, it will point to the end of the wf.

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
    },
})
export class HandleNetworkConfigurationBackup {
    @DecisionItem({
        target: "waitForEvent",
        else: null, // null means end
    })
    public decisionElement(waitingTimer: Date) {
        return waitingTimer !== null;
    }

    @Item({
        target: "decisionElement",
        exception: "",
    })
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

    @Item({
        target: "execute",
        exception: "",
    })
    @RootItem()
    public start() {
        System.log("Starting workflow");
    }

    @WaitingTimerItem({
        target: "execute",
    })
    public waitForEvent(@In waitingTimer: Date) { }
}
```
