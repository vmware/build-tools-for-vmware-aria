# Polyglot wrapper library

The purpose of the library is to provide a wrapper when calling polyglot actions.
Also provide the definition of **@polyglot**, **@polyglotIn**, **@polyglotOut** decorator.
This decorators can be used in actions.

The **@Polyglot** decorator can be used combined with the **@Workflow** decorator inside a workflow definition on typescript code.

## Usage

This library can be used added the next dependency to the pom of the project

```xml
<dependency>
  <groupId>com.vmware.pscoe.library</groupId>
  <artifactId>polyglot-wrapper</artifactId>
  <version>1.0.0</version>
  <type>tgz</type>
  <scope>compile</scope>
</dependency>

<dependency>
    <groupId>com.vmware.pscoe.library</groupId>
    <artifactId>polyglot-wrapper</artifactId>
    <type>package</type>
    <version>1.1.0</version>
</dependency>
```
### Example of how to use the wrapper library

```js
import { Polyglot } from "com.vmware.pscoe.library.polyglot-wrapper/polyglot";

export class Sample {
    public sum() {
        let polyglotInstance = new Polyglot<any>();

            polyglotInstance.execute(
				      "com.vmware.pscoe.customer", // package where is the polyglot action
				      "nodejs", // name of the method to be invoked
				      // Parameters of the polyglot action -- max number of parameters 10
				      1000,
              { limit: 1, vraEndpoint: { host: "temp", base: "temp" } },
            );
    }
}
```

### Example of how to use the @polyglot decorator on actions

```js
import { polyglot, polyglotIn, polyglotOut } from "com.vmware.pscoe.library.polyglot-wrapper/decorator";

export class PolyglotDecoratorExample {

    @polyglot({
        package: "com.vmware.pscoe.customer",
        method: "nodejs"
    })
    public executeNodeJSPolyglotAction(
        @polyglotIn p1: any,
        @polyglotIn p2: any,
        @polyglotOut p3: any): void {
        System.log(`Result of polyglot action: ${p3}`);
    }

    @polyglot({
        package: "com.vmware.pscoe.customer",
        method: "python"
    })
    public executePythonPolyglotAction(
        @polyglotIn p1: any,
        @polyglotIn p2: any,
        @polyglotOut p3: any): void {
        System.log(`Result of polyglot action: ${p3}`);
    }
}
```

### Example of how to use the @Polyglot decorator combined with @Workflow decorator

```js
import { Workflow, Out, Polyglot } from "vrotsc-annotations";
@Workflow({
    name: "Sample Workflow",
    path: "PSCoE/MyProject",
    description: "Sample workflow description"
})
export class SampleWorkflow {

    @Polyglot({
        package: "com.vmware.pscoe.customer",
        method: "nodejs"
    })
    public executePolyglotAction(@In p1: number, @In p2: number, @Out p3: any): void {
        System.log(`Result of polyglot action: ${p3}`);
    }
}

```
**Note**:  
The **@Polyglot** decorator is not equal to **@polyglot** decorator.
Differences:  
1. **@polyglot** depends of **polyglot-wrapper** can only be used in actions definitions.
2. **@Polyglot** is internal, the code is inside **vrotsc-annotation** and the source code corresponding to the decorator is generated on compilation time.
