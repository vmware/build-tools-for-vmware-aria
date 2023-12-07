# Policies
vRO Policies and how to define them in a typescript project

## Table of Contents
1. [Example Policy](#exmaple-policy)
2. [Creating Variables in Policy](#creating-a-variable-in-policy)
3. [Creating Elements to a Policy](#creating-a-elements-to-a-policy)

## Exmaple Policy
A Example typescript policy with all posible configurations.

~~~ts
import { PolicyTemplate } from "vrotsc-annotations";

@PolicyTemplate({
    name: "Sample Policy",
    path: "MyOrg/MyProject",
    description: "Some Description",
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
~~~

## Creating a Variable in Policy
Variables can be created in a policy by adding them under **variables** attribute as shown in the example.

### Creating a String variable
String varibales can addes by directly assigning values to the them.
~~~ts
variables: {
    sample2: "a direct string value"
}
~~~

### Creating Variables with other data types
Other data type variables can be added by providing the additional information to them.
~~~ts
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
~~~

### Creating Variables with Configuration binding
Variables can be binded to configurations by providing configuration ID and key.

~~~ts
variables: {
    config: {
        type: "Properties",
        configId: "8e2d3ba0-4e2c-4d4c-ad82-76de4967bf9f",
        configKey: "props",
        description: "A variable created with configuration binding"
    }
}
~~~

**Note:** Configuration binding will be considered if both value and configuration ID are provided to a variable.

## Creating a Elements to a Policy
Elements can be created in a policy by adding them under **elements** attribute as shown in example

### Supported Element types
1. AMQP:Subscription
2. MQTT:Subscription
3. SNMP:SnmpDevice
4. SNMP:TrapHost
5. Periodic Event

### Creating a Periodic Event Element with defined event Method
Periodic Event element can be added by providing the list of events and schedules as below. Schedules are mandatory for Periodic Event type elements

~~~ts
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
~~~

### Creating a Periodic Event Element with defined event Method
Events can either be a method defined in the typescipt Policy or can be binded to a workflow. 

~~~ts
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
~~~

From the above example, the workflow ID and its input bindings are provided. The inputs are binded to variables created in the policy.

### Creating Other Element Types
Other supported element types can be as below. Events can be a method defined in the policy or can be binded to a workflow.

~~~ts
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
~~~