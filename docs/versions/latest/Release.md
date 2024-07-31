[//]: # (VERSION_PLACEHOLDER DO NOT DELETE)
[//]: # (Used when working on a new release. Placed together with the Version.md)
[//]: # (Nothing here is optional. If a step must not be performed, it must be said so)
[//]: # (Do not fill the version, it will be done automatically)
[//]: # (Quick Intro to what is the focus of this release)

## Breaking Changes

[//]: # (### *Breaking Change*)
[//]: # (Describe the breaking change AND explain how to resolve it)
[//]: # (You can utilize internal links /e.g. link to the upgrade procedure, link to the improvement|deprecation that introduced this/)

## Deprecations

[//]: # (### *Deprecation*)
[//]: # (Explain what is deprecated and suggest alternatives)

[//]: # (Features -> New Functionality)

## Features

[//]: # (### *Feature Name*)
[//]: # (Describe the feature)
[//]: # (Optional But higlhy recommended Specify *NONE* if missing)
[//]: # (#### Relevant Documentation:)

### Support of Objects in the VROES.Shims.arrayFrom() Method

Add support for objects in the `VROES.Shims.arrayFrom()` method so its behavior is similar to the standard `Array.from()` method.

### *Better ordering of the canvas items*

The canvas items are now ordered based on an tree algorithm.

Example:

From input:

```ts
const nodes = [
  { name: "A", targets: ["B"] },
  { name: "B", targets: ["C"] },
  { name: "C", targets: ["D", "G"] },
  { name: "D", targets: ["E", "F"] },
  { name: "E", targets: ["C"] },
  { name: "F", targets: ["O"] },
  { name: "G", targets: ["H"] },
  { name: "H", targets: ["I"] },
  { name: "I", targets: ["J", "K", "L", "M"] },
  { name: "J", targets: [] },
  { name: "K", targets: [] },
  { name: "L", targets: [] },
  { name: "M", targets: [] },
  { name: "O", targets: ["P"] },
  { name: "P", targets: ["Q"] },
  { name: "Q", targets: [] },

  // Second start?
  { name: "S", targets: ["T"] },
  { name: "T", targets: ["U", "W", "D"] },
  { name: "U", targets: [] },
  { name: "W", targets: [] },
  { name: "X", targets: ["Y"] },
  { name: "Y", targets: [] },
];
```

We get:
```log
....................................................................................................
....................................................................................................
....................................................................................................
..............................F....O....P....Q......................................................
....................................................................................................
....................................................................................................
....................D....E..........................................................................
....................................................................................................
.....A....B....C...................J................................................................
....................................................................................................
....................................................................................................
....................G....H....I.....................................................................
....................................................................................................
.....S....T....U...................K................................................................
....................................................................................................
....................................................................................................
....................................................................................................
....................................................................................................
...............W...................L................................................................
....................................................................................................
....................................................................................................
....................................................................................................
....................................................................................................
...................................M................................................................
....................................................................................................
....................................................................................................
```
### Support python 3.10 runtime

Add support for python 3.10 runtime in Orchestrator. This is now the default, since python 3.7 is deprecated.

### *New `@ActionItem` decorator for Workflows*

The new decorator gives you the ability to specify a canvas item that calls an action.

#### Supported Parameters

- `target` - The name of the next in line item. Same as `@Item`.
- `scriptModule` - The path of the action you want to call and the action name, separated by `/`. Example: `com.vmware.pscoe.library.general/echo`.

In order to bind inputs and outputs, you do it with the `@In` and `@Out` decorators. This is the same way we do it for other items.

#### Outputs

There is a requirement to have only one output, and it will be of type `ActionResult`.

#### Example

```typescript
import { Workflow, Out, In, Item, ActionItem } from "vrotsc-annotations";

@Workflow({
  name: "Example",
  path: "VMware/PSCoE",
  attributes: {
    first: {
      type: "number"
    },
    second: {
      type: "number"
    },
    actionResult: {
      type: "ActionResult"
    }
  }
})
export class Example {
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
}
```

### *New `DefaultErrorHandler` decorator for Workflows*

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

### *New `@WorkflowEndItem` decorator for Workflows*

The decorator is used to specify a custom workflow end item.

#### Supported Parameters

- `endMode` - End mode of the component, could be one of 0 or 1, where 0 is exit success and 1 is error.
- `exceptionVariable` - Exception variable that will hold the exception data when triggered.
- `businessStatus` - Value of the business status in the end component.

Example:

```typescript
import { Workflow, RootItem, WorkflowEndItem } from "vrotsc-annotations";

@Workflow({
  name: "Workflow End Exception",
  path: "VMware/PSCoE",
  description: "Workflow with root and end item with end mode 1",
  attributes: {
    errorMessage: {
      type: "string",
    },
    businessStatus: {
      type: "string",
    },
    endMode: {
      type: "number",
    },
  },
})
export class WorkflowEnd {
  @RootItem()
  public initiateWorkflow() {
    // NOOP
  }

  @WorkflowEndItem({
    endMode: 1,
    exceptionVariable: "errorMessage",
    businessStatus: "Bad",
  })
  public workflowEnd() {
    // NOOP
  }
}
```

### *New `AsyncWorkflowItem` decorator for Workflows*

The decorator is used to specify a canvas item that calls an asynchronous workflow.

- `@AsyncWorkflowItem({target: "", linkedItem: "" })`
  - `target` - The name of the next in line item.
  - `linkedItem` - The ID of the workflow to call

In order to bind inputs and outputs, you do it with the `@In` and `@Out` decorators. This is the same way we do it for other items.

#### Outputs

Special output is needed for the AsyncWorkflowItem.

- `wfToken` - {WorkflowToken} is required. The name **must** be `wfToken`. If this is missing an error is thrown. We don't check if the type is `WorkflowToken` but Aria Orchestrator will complain.

#### Inputs

No special inputs are needed for the AsyncWorkflowItem.


#### Example

```typescript
import { Workflow, Out, In, Item, RootItem, AsyncWorkflowItem } from "vrotsc-annotations";

@Workflow({
  name: "Async Workflow Test",
  path: "VMware/PSCoE",
  description: "Calling another workflow asynchronously and binding values correctly",
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
    wfToken: {
      type: "WorkflowToken"
    }
  }
})
export class HandleNetworkConfigurationBackup {
  @AsyncWorkflowItem({
    target: "printAsync",
    linkedItem: "9e4503db-cbaa-435a-9fad-144409c08df0"
  })
  public asyncCall(@In first: number, @In second: number, @Out wfToken: WorkflowToken) { }

  @Item({ target: "callAsyncWf" })
  public prepareItems(@In @Out first: number, @In @Out second: number) {
    first = 1;
    second = 2;
  }

  @Item({ target: "end" })
  public printAsync(@In wfToken: WorkflowToken) {
    System.log(`Workflow token: ${wfToken.id} and state: ${wfToken.state}`);
    System.log("Workflow finished");
  }

  @Item({ target: "prepareItems", exception: "" })
  @RootItem()
  public start() {
    System.log("Starting workflow");
  }
}
```

### *New `ScheduledWorkflowItem` decorator for Workflows*

The new decorator gives you the ability to specify a canvas item that schedules a Workflow.

- `@ScheduledWorkflowItem({target: "", linkedItem: "" })`
  - `target` - The name of the next in line item.
  - `linkedItem` - The ID of the workflow to schedule

In order to bind inputs and outputs, you do it with the `@In` and `@Out` decorators. This is the same way we do it for other items.

#### Inputs

Special input is needed for the ScheduledWorkflowItem.

- `workflowScheduleDate` - {Date} is required. The name **must** be `workflowScheduleDate`. If this is missing an error is thrown. We don't check if the type is `Date` but Aria Orchestrator will complain.

#### Outputs

Special output is needed for the ScheduledWorkflowItem.

- `scheduledTask` - {Task} is optional. If it's missing nothing will happen, if it's added, then the name **must** be `scheduledTask`. This is the task that is scheduled.

#### Example

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

### *New `WorkflowItem` decorator for Workflows*

The new Decorator gives you the ability to specify a canvas item that calls a Workflow.

- `@WorkflowItem({target: "", linkedItem: "" })`
  - `target` - The name of the next in line item.
  - `linkedItem` - The ID of the workflow to call

In order to bind inputs and outputs, you do it with the `@In` and `@Out` decorators. This is the same way we do it for other items.

Example:

```typescript
import {
  Workflow,
  Out,
  In,
  Item,
  RootItem,
  DecisionItem,
  WaitingTimerItem,
  WorkflowItem,
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
  ) {}

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
}
```

## Improvements

[//]: # (### *Improvement Name* )
[//]: # (Talk ONLY regarding the improvement)
[//]: # (Optional But higlhy recommended)
[//]: # (#### Previous Behavior)
[//]: # (Explain how it used to behave, regarding to the change)
[//]: # (Optional But higlhy recommended)
[//]: # (#### New Behavior)
[//]: # (Explain how it behaves now, regarding to the change)
[//]: # (Optional But higlhy recommended Specify *NONE* if missing)
[//]: # (#### Relevant Documentation:)


### *`for each` statements are now being converted by `vropkg` when pulling*

`for each` is valid syntax in the Java's Rhino engine, but not in normal JS.

#### Previous Behavior

When pulling a workflow with `for each` statements, the action would be pulled, but then would not be able to be pushed as the syntax is invalid.

#### New Behavior

`for each` statements are now being converted to `for` statements when pulling a workflow.

Example:

```js
var test = ["ya", "da"]

for each (var i in test) {
    for each (var y in test) {
        System.log(y)
        for each(var z in test){System.log(z)}
    }
  System.log(i)
}

for each (
var n in test
) {
    System.log(n)
}

for (var i in test) {
  System.log(i)
}

for (var $index in test) {
    var i = test[$index]
  System.log(i)
}
```

is converted to


```js
/**
 * @return {string}
 */
(function() {
  var test = ["ya", "da"]

  for (var $index_i in test) {
    var i = test[$index_i];
    for (var $index_y in test) {
      var y = test[$index_y];
      System.log(y)
      for (var $index_z in test) {
        var z = test[$index_z];
        System.log(z)
      }
    }
    System.log(i)
  }

  for (var $index_n in test) {
    var n = test[$index_n];
    System.log(n)
  }
  for (var i in test) {
    System.log(i)
  }

  for (var $index in test) {
    var i = test[$index]
    System.log(i)
  }
});
```

### *ABX archetype build issue, cannot compile*

Fixed an issue where the ABX archetype could not compile due to an old version of the `xmlbuilder2` package.

#### Previous Behavior

We were getting a build error when trying to compile the ABX archetype:

```log
info:    Error ts(1110) /root/vro/polyglot_test_project/node_modules/@types/node/crypto.d.ts (3569,17): Type expected.
info:    Error ts(1005) /root/vro/polyglot_test_project/node_modules/@types/node/events.d.ts (105,28): ',' expected.
...
info:    Error ts(1005) /root/vro/polyglot_test_project/node_modules/@types/node/util.d.ts (1763,26): ';' expected.
info:    Error ts(1128) /root/vro/polyglot_test_project/node_modules/@types/node/util.d.ts (1765,1): Declaration or statement expected.
info: Exit status: 1
info: Compilation complete
/root/vro/polyglot_test_project/node_modules/@vmware-pscoe/polyglotpkg/dist/strategies/nodejs.js:123
            throw new Error('Found compilation errors');
                  ^

Error: Found compilation errors
    at NodejsStrategy.compile (/root/vro/polyglot_test_project/node_modules/@vmware-pscoe/polyglotpkg/dist/strategies/nodejs.js:123:19)
    at NodejsStrategy.<anonymous> (/root/vro/polyglot_test_project/node_modules/@vmware-pscoe/polyglotpkg/dist/strategies/nodejs.js:52:18)
    at Generator.next (<anonymous>)
    at fulfilled (/root/vro/polyglot_test_project/node_modules/@vmware-pscoe/polyglotpkg/dist/strategies/nodejs.js:5:58)
[ERROR] Command execution failed.
org.apache.commons.exec.ExecuteException: Process exited with an error: 1 (Exit value: 1)
    at org.apache.commons.exec.DefaultExecutor.executeInternal (DefaultExecutor.java:404)
    at org.apache.commons.exec.DefaultExecutor.execute (DefaultExecutor.java:166)
...
    at java.util.concurrent.ThreadPoolExecutor.runWorker (ThreadPoolExecutor.java:1136)
    at java.util.concurrent.ThreadPoolExecutor$Worker.run (ThreadPoolExecutor.java:635)
    at java.lang.Thread.run (Thread.java:840)
[INFO] ------------------------------------------------------------------------
[INFO] BUILD FAILURE
[INFO] ------------------------------------------------------------------------
```

#### New Behavior

The ABX archetype now compiles successfully.

### Add missing classes to `o11n-plugin-aria` and add missing methods to the existing classes

#### Previous Behavior

Many classes are missing completely compared with vRO API and some existing classes were missing some methods

#### Current Behavior

The following classes were added to `o11n-plugin-aria`:

- VraInfrastructureClient
- VraCloudAccountService
- VraUpdateCloudAccountVsphereSpecification
- VraCloudAccountVsphereSpecification
- VraRegionSpecification
- VraCloudAccountVsphere
- VraCloudZoneService
- VraZone
- VraHref
- VraZoneSpecification
- VraTag
- VraDataCollectorService
- VraRequestService
- VraRequestTracker
  
The following missing methods were added to the exist classes:

- Class `VraHost`
  - `destroy`
  - `createInfrastructureClient`

#### Related issue

<https://github.com/vmware/build-tools-for-vmware-aria/issues/347>

## Upgrade procedure

[//]: # (Explain in details if something needs to be done)
