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

#### `@RootItem`

This is a meta decorator. Add this to whichever function you want to be the entry point of the workflow.

### Example Workflow

```ts
import { Workflow, Out, In, Item, RootItem, DecisionItem, WaitingTimerItem, WorkflowItem } from "vrotsc-annotations";

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
		}
	}
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
		linkedItem: "9e4503db-cbaa-435a-9fad-144409c08df0"
	})
	public callOtherWf(@In first: number, @In second: number, @Out result: number) {
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
	}
}
```
