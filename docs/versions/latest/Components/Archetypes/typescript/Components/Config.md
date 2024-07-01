# Config elements

vRO Configuration Elements and how to define them in a typescript project

## Overview

Configuration Elements are vRO constructs that allow you to store configuration data. Build Tools for VMware Aria supports a few ways of defining configuration elements as well as different types of values.

## Table Of Contents

- [Pushing Configuration Values](#pushing-configuration-values)
- [Secure Strings](#secure-strings)
- [CompositeTypes](#compositetypes)
- [Examples](#examples)

### Pushing Configuration Values

When doing `mvn clean package vrealize:push` you need to include some extra arguments if you want to push configuration values

- `-Dvro.packageImportConfigurationAttributeValues=true` - Pushes all configuration values besides SecureStrings.
- `-Dvro.packageImportConfigSecureStringAttributeValues=true` - Pushes all Secure String configuration values.

### Secure Strings

Up to version 2.23.0

Secure strings in configuration elements are defined as shown below:

```ts
import { Configuration } from "vrotsc-annotations";

@Configuration({
  name: "Test",
  path: "PSCoE/Test",
  attributes: {
    password:
      {
        type: "SecureString",
        value: "36BJ62U35V34T31R64I31H38Q39L2DH62K37Q63T61M2DK34O62R66K35U2DY38Y32S66Q35G2DQ63K66U64Z62X33Q37T31R34Q37W31Z66M31K",
        description: "some number"
      }
  }
})
export class Test {
  password: SecureString;
}
```

When pushing them to vRO, you have to add `-Dvro.packageImportConfigSecureStringAttributeValues=true` to indicate to the Build Tools for VMware Aria that the SecureStrings should be imported as well.

The value is encoded, and the best way to know what to set there would be to manually add it to vRO, then fetch it using the VSCode vRDT /vRealize Developer tools/ plugin by clicking on the extension in the sidebar and navigating to it. After you find it you will be able to extract the value from the retrieved `xml` and put it here.

### CompositeTypes

Currently, composite types are implemented for both vRO7 and vRO8. vRO8 is compatible with the old vRO7 way of importing composite types, so that format is used to import composite types in both systems.

Composite types conform to these constraints:

```ts
type PossibleAttributeValues = string | boolean | number;

type AttributeValue  = PossibleAttributeValues | PossibleAttributeValues[];
```

Meaning that a Composite type can be a string, boolean, number or an array of each.

### Examples

Example Typescript Configuration Element:

```ts
import { Configuration } from "vrotsc-annotations";

@Configuration({
  name: "GeneralConfig",
  path: "PSCoE/MyProject",
  attributes: {
    composite: {
      type: "CompositeType(field1:number,field2:boolean,field3:string,field4:Array/string):ITest",
      value: {
        field1: 1,
        field2: true,
        field3: '2222',
        field4: ['test',"test2", `test3`]
      },
      description: "A composite value with all the basics"
    },
    numericAttr: {
      type: "number",
      value: 123,
      description: "Represents a numeric variable"
    },
    stringAttr: {
      type: "string",
      value: "Some String",
      description: "A string variable"
    },
    stringArray: {
      type: "Array/string",
      value: ["first", "second"],
      description: "This is an array of strings"
    },
    host: {
      type: "REST:RESTHost",
      description: "A RESTHost without any value ( placeholder )"
      //  Does not support values
    },
    vm: {
      type: "VC:VirtualMachine",
      description: "A VirtualMachine without any value ( placeholder )"
      //  Does not support values
    },
    password: {
      type: "SecureString",
      value: "36BJ62U35V34T31R64I31H38Q39L2DH62K37Q63T61M2DK34O62R66K35U2DY38Y32S66Q35G2DQ63K66U64Z62X33Q37T31R34Q37W31Z66M31K",
      description: "some number"
    }
  }
})
export class CompositeValuesTest {
}
```

Example YAML configuration element

```yaml
name: CompositeConfigYaml
path: PSCoE/MyProject
attributes:
  field1:
    type: CompositeType(field1:number,field2:boolean,field3:string,field4:Array/string):ITest
    description: "A composite type with primitives"
    value:
      field1: 1
      field2: true
      field3: Test
      field4:
        - test1
        - test2
        - test3
  field2: 
    type: number
    value: 123
    description: "Some number"
  field3: 
    type: boolean
    value: true
    description: "Some boolean"
  field4: 
    type: string
    description: "Some string"
```
