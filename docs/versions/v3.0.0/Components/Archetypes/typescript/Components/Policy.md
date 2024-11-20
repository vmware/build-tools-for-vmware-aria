# Policies

vRO Policies and how to define them in Aria Orchestrator

## Overview

Aria Orchestrator supports creating Policies with different elements and variables.

## Table of Contents

1. [Policy Template Versioning](#policy-template-versioning)
2. [Example Policy](#example-policy)
3. [Naming Convention](#naming-convention)
4. [Creating Variables in Policy](#creating-a-variable-in-policy)
5. [Creating Elements to a Policy](#creating-elements-to-a-policy)

### Policy Template Versioning

Policy template now has two versions for backward compatability. Template versions can be changed using the **templateVersion** attribute. Use v2 for full support of creating Policy with variables and multiple elements.

**NOTE:** templateVersion attribute is optional. if not provided, templateVersion will be considered as v1 by default.

```ts
import { PolicyTemplate } from "vrotsc-annotations";

@PolicyTemplate({
    name: "Sample Policy",
    path: "MyOrg/MyProject",
    description: "Some Description",
    templateVersion: "v2", // or v1
})
```

### Example Policy

A Example typescript policy with all posible configurations for Policy Template Version 2 (v2).

#### Version 1

```ts
import { PolicyTemplate } from "vrotsc-annotations";

@PolicyTemplate({
    name: "Policy Template Amqp",
    path: "MyOrg/MyProject",
    type: "AMQP:Subscription",
    templateVersion: "v1"
})
export class PolicyTemplateAmqp {
    onMessage(self: AMQPSubscription, event: any) {
        System.log("onMessage");
    }
}
```

#### Version 2

```ts
import { PolicyTemplate } from "vrotsc-annotations";

@PolicyTemplate({
    name: "Sample Policy",
    path: "MyOrg/MyProject",
    description: "Some Description",
    templateVersion: "v2",
    variables: {
        sample: {
            type: "string",
            value: "a string value with type mentioned",
            description: "A variable created with type and description"
        },
        config: {
            type: "Properties",
            configId: "8e2d3ba0-4e2c-4d4c-ad82-76de4967bf9f",
            configKey: "props",
            description: "A variable created with configuration binding"
        },
        sample2: "a direct string value"
    },
    elements: {
        ElementOne: {
            type: "AMQP:Subscription",
            events: {
                onMessage: "onMessage"
            }
        },
        ElementTwo: {
            type: "SNMP:SnmpDevice",
            events: {
                OnTrap: {
                    workflowId: "56652d1b-f797-3e1c-a108-7db9ddfb1f42",
                    bindings: {
                        workflowInputOne: {
                            type: "string",
                            variable: "sample"
                        }
                    }
                }
            }
        }
        ElementThree: {
            type: "Periodic Event",
            schedule: {
                periode: "every-minutes",
                when: "13,15",
                timezone: "Asia/Calcutta"
            }
        }
    }
})
```

### Naming Convention

Naming convention for creating variables, elements with different types of elements and events.

```ts
import { PolicyTemplate } from "vrotsc-annotations";

@PolicyTemplate({
    name: "Sample Policy",
    path: "MyOrg/MyProject",
    description: "Some Description",
    templateVersion: "v2", // Policy Template Version: v1 or v2
    variables: {
        sample: { // variable name
            type: "string", // variable type
            value: "a string value with type mentioned", // variable value
            description: "A variable created with type and description" // variable description
        },
        config: { // variable name
            type: "Properties", // variable type
            configId: "8e2d3ba0-4e2c-4d4c-ad82-76de4967bf9f", // configuration element ID
            configKey: "props", // configuration element key
            description: "A variable created with configuration binding" // variable description
        },
        sample2: "a direct string value" // directly assiged string value to a variable
    },
    elements: {
        // Policy with script for event
        ElementOne: { // Policy element name
            type: "AMQP:Subscription", // policy element type
            events: { // list of events for element
                onMessage: "onMessage" // event : method name in class
            }
        },
        // Policy with workflow execution
        ElementTwo: { // Policy element name
            type: "SNMP:SnmpDevice", // Policy element type
            events: { // list of events for element
                OnTrap: { // event name
                    workflowId: "56652d1b-f797-3e1c-a108-7db9ddfb1f42", // workflow id to execute
                    bindings: { // workflow input bindings
                        workflowInputOne: { // workflow input name
                            type: "string", // workflow input type
                            variable: "sample" // variable name for workflow input
                        }
                    }
                }
            }
        },
        // Policy with Periodic Event
        ElementThree: { // Policy element name
            type: "Periodic Event", // Policy element type
            schedule: { // Periodic event schedule - mandatory for periodic event
                periode: "every-minutes", // period of schedule
                when: "13,15", // time for schedule, can be multiple
                timezone: "Asia/Calcutta" // timezone for schedule
            }
        }
    }
})
```

### Creating a Variable in Policy

Variables can be created in a policy by adding them under **variables** attribute as shown in the example.

#### Creating a String variable

String variables can be added by directly assigning values to the them.

```ts
variables: {
    sample2: "a direct string value"
}
```

#### Creating Variables with other data types

Other data type variables can be added by providing the additional information to them.

```ts
variables: {
    sample: {
        type: "boolean",
        value: true,
        description: "A variable created with type and description"
    },
    sample2:{
        type: "number",
        value: 2,
        description: "A Numeric value"
    }
}
```

#### Creating Variables with Configuration binding

Variables can be binded to configurations by providing configuration ID and key.

```ts
variables: {
    config: {
        type: "Properties",
        configId: "8e2d3ba0-4e2c-4d4c-ad82-76de4967bf9f",
        configKey: "props",
        description: "A variable created with configuration binding"
    }
}
```

**Note:** Configuration binding will be considered if both value and configuration ID are provided to a variable.

### Creating Elements to a Policy

Elements can be created in a policy by adding them under **elements** attribute as shown in example

#### Supported Element types

- AMQP:Subscription
- MQTT:Subscription
- SNMP:SnmpDevice
- SNMP:TrapHost
- Periodic Event

#### Creating a Periodic Event Element with defined event Method

Periodic Event element can be added by providing the list of events and schedules as below. Schedules are mandatory for Periodic Event type elements

```ts
elements: {
    PeriodicEvent: {
        type: "Periodic Event",
        events:{
            OnExecute: "onExecuteEvent"
        },
        schedule: {
            periode: "every-minutes",
            when: "13,15",
            timezone: "Asia/Calcutta"
        }
    }
}

export class SamplePolicy {
    onExecuteEvent(self: AMQPSubscription, event: any) {
        let message = self.retrieveMessage(event);
        System.log(`Received message ${message.bodyAsText}`);
    }
}
```

#### Creating a Periodic Event Element with bound event Method

Events can either be a method defined in the typescipt Policy or can be bound to a workflow.

```ts
elements: {
    PeriodicEvent: {
        type: "Periodic Event",
        events:{
            OnExecute: {
                workflowId: "56652d1b-f797-3e1c-a108-7db9ddfb1f42",
                bindings: {
                    workflowInputOne: {
                        type: "string",
                        variable: "sample"
                    }
                }
            }
        },
        schedule: {
            periode: "every-minutes",
            when: "13,15",
            timezone: "Asia/Calcutta"
        }
    }
}
```

From the above example, the workflow ID and its input bindings are provided. The inputs are binded to variables created in the policy.

#### Creating Other Element Types

Other supported element types can be as below. Events can be a method defined in the policy or can be binded to a workflow.

```ts
elements: {
    ElementOne: {
        type: "AMQP:Subscription",
        events: {
            onMessage: "onMessage"
        }
    },
    ElementTwo: {
        type: "SNMP:SnmpDevice",
        events: {
            OnTrap: {
                workflowId: "56652d1b-f797-3e1c-a108-7db9ddfb1f42",
                bindings: {
                    sourceGroup: {
                        type: "string",
                        variable: "sample"
                    }
                }
            }
        }
    }
}

export class SamplePolicy {
    onMessage(self: AMQPSubscription, event: any) {
        let message = self.retrieveMessage(event);
        System.log(`Received message ${message.bodyAsText}`);
    }
}
```
