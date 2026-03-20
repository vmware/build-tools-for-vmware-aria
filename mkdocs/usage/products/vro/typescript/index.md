---
title: Typescript
---

# Typescript Based Project

## Overview

| Field | Value |
|---|---|
| Name | `typescript` |
| Archetype Group ID | `com.vmware.pscoe.o11n.archetypes` |
| Archetype Artifact ID | `package-typescript-archetype` |
| Product compatibility | {{ extra.products.vro_7_full_name }} (7.x), {{ extra.products.vro_8_full_name }} (8.x) and {{ extra.products.vro_9_full_name }} (9.x) |
| Package extension | `package` |

Typescript project type is one of the available {{ products.vro_short_name }} project types in **Build Tools for VMware Aria**. The project type is a representation of {{ products.vro_short_name_short_name }} content into Typescript format. The project consist of content content container. During [build operation](#build-project) the contents of the container are transpiled into {{ products.vro_short_name }} Javascript and packaged into {{ products.vro_short_name }} native package (the same package that can be exported/imported from {{ products.vro_short_name }} UI -> Assets -> Packages).

The typescript project type also allows the user to write unit tests and has embedded code coverage.

!!! note
    Supported Typescript version is 5.4.5.

## Supported Content

- `Workflows`
- `Workflow Custom Forms`
- `User Interaction Custom Forms`
- `Actions`
- `Configuration Elements`
- `Resource Elements`
- `Policies`

## Create New {{ products.vro_short_name }} Project
{{ general.bta_name }} provides ready to use {{ products.vro_short_name }} project templates (*maven archetypes*).

To create a new {{ products.vro_short_name }} project from archetype use the following command:

```Bash
mvn archetype:generate \
    -DinteractiveMode=false \
    -DarchetypeGroupId=com.vmware.pscoe.o11n.archetypes \
    -DarchetypeArtifactId=package-typescript-archetype \
    -DarchetypeVersion={{ iac.latest_release }} # (1)! \
    -DgroupId={{ archetype.customer_project.group_id}} # (2)! \
    -DartifactId={{ archetype.customer_project.artifact_id}} # (3)!
```

1.  {{ archetype.customer_project.archetype_version_hint }}
2.  {{ archetype.customer_project.group_id_hint }}
3.  {{ archetype.customer_project.artifact_id_hint }}

### Content Structure

The result of this command will produce the following project file structure:

```ascii
service-automation
├── README.md
├── pom.xml
├── release.sh
├── tsconfig.json
└── src
    └── integration-service-1
        └── actions
            └── integrationAction.js
        └── classes
            └── IntegrationService1.ts
            └── IntegrationService1.test.ts            
        └── policies
            └── EventListener.pl.ts
        └── resources
            └── sample.txt
            └── sample_2.json
            └── sample_2.json.element_info.json
            └── sample_3.xml
            └── sample_3.xml.element_info.yaml
            └── sample_4.json
        └── types
            └── IntegrationService1.d.ts
        └── workflows
            └── CreateIntegration.wf.ts
            └── CreateIntegration.wf.form.json
        └── IntegrationService1.conf.ts            
        └── IntegrationService1Alternative.conf.yaml        
```

<!-- Environment Connection Parameters Section -->
{% include-markdown "../common/connection-parameters.md" %}

## Operations

<!-- Build Project Section -->
{% include-markdown "../../../../assets/docs/mvn/build-project.md" %}
The output of the command will result in **{{ archetype.customer_project.group_id}}.{{ archetype.customer_project.artifact_id}}-1.0.0-SNAPSHOT.package** file generated in the target folder of the project. This is an {{ products.vro_short_name }} native package that can be imported from {{ products.vro_short_name }} UI -> Assets -> Packages.

<!-- Bundle Project Section -->
{% include-markdown "../../../../assets/docs/mvn/bundle-project.md" %}

### Pull Content

!!! warning
    **Not supported!** Typescript development is unidirectional - local code is transpiled into {{ products.vro_short_name }} Javascript. Extracting {{ products.vro_short_name }} content into local Typescript files is not supported.

<!-- Push Content Section -->
{% include-markdown "../../../../assets/docs/mvn/push-content.md" %}

<!-- Push Content - Additional Parameters Section -->
{% include-markdown "../common/push-content-parameters.md" %}

* `vro.packageImportConfigurationAttributeValues` - if set to `true` pushes all configuration values besides SecureStrings.

* `vro.packageImportConfigSecureStringAttributeValues=true` - if set to `true` pushes all Secure String configuration values.

<!-- vroIgnore Section -->
{% include-markdown "../common/vroignore.md" %}

<!-- Clean Up Content Section -->
{% include-markdown "../../../../assets/docs/mvn/clean-up-content.md" %}

<!-- Troubleshooting Section -->
{% include-markdown "../../../../assets/docs/mvn/troubleshooting.md" %}

## Known issues

### Array functions are not transpiled to {{ products.vro_short_name }} code

If an `Array` variable is not explicitly defined or recognized as such, the transpiler does not convert the TS-specific `Array` prototype functions (`find()`, `some()`, etc.) into {{ products.vro_short_name }} compatible code which results in a runtime error.

Consider the following example:

```javascript
const testArray = [1, 2, 3, 4, 5];

let objectsForIteration = null;
if (true) {
    objectsForIteration = testArray;
}

// Fails to transpile correctly because type is resolved to "any"
const res = objectsForIteration.find(o => o === 2)
System.log(res + "")
```

The code above is be converted to the following {{ products.vro_short_name }} code, which during execution throws the error `TypeError: Cannot find function find in object 1,2,3,4,5.`

```javascript
var testArray = [1, 2, 3, 4, 5];

var objectsForIteration = null;
if (true) {
    objectsForIteration = testArray;
}

// Fails to transpile correctly because type is resolved to "any"
var res = objectsForIteration.find(function (o) { return o === 2; });
System.log(res + "");
```

Proper variable typization solves this problem. Let's revisit the example but this time we will explicitly define the type of values that we expect the `objectsForIteration` variable to receive.

```javascript
const testArray = [1, 2, 3, 4, 5];

let objectsForIteration: Array<number> = null;
if (true) {
    objectsForIteration = testArray;
}

// Transpiles correctly because of explicit typization
const res = objectsForIteration.find(o => o === 2)
System.log(res + "")
```

The code is transpiled correctly to {{ products.vro_short_name }} code and executes successfully.

```javascript
var __global = System.getContext() || (function () {
    return this;
}).call(null);
var VROES = __global.__VROES || (__global.__VROES = System.getModule("com.vmware.pscoe.library.ecmascript").VROES());
var testArray = [1, 2, 3, 4, 5];

var objectsForIteration = null;
if (true) {
    objectsForIteration = testArray;
}
// Transpiles correctly because of explicit typization
var res = VROES.Shims.arrayFind(objectsForIteration, function (o) { return o === 2; });
System.log(res + "");
```

#### How to prevent this issue

The recommended configuration to prevent such issues is to set the `strictNullChecks` property to `true` in your project's local `tsconfig.json` file. This allows for a type hint warning to be displayed in case the type is not explicitly defined.

!!! note
    The warning messages received are optional and are NOT blocking package build and push operations.

Let's revisit the example once again with `strictNullChecks` enabled:

```javascript
const testArray = [1, 2, 3, 4, 5];

let objectsForIteration = null;
if (true) {
    // The following warning message is displayed: Type 'number[]' is not assignable to type 'null'.ts(2322)
    objectsForIteration = testArray;
}

// The following warning message is displayed: 'objectsForIteration' is possibly 'null'.ts(18047)
const res = objectsForIteration.find(o => o === 2)
System.log(res + "")
```

Sample `tsconfig.json`:

```javascript
{
  "compilerOptions": {
    "target": "ES5",
    "module": "CommonJS",
    "moduleResolution": "Node",
    "lib": [
      "ES5",
      "ES2015.Core",
      "ES2015.Collection",
      "ES2015.Iterable",
      "ES2015.Promise",
      "ES2017.String"
    ],
    "experimentalDecorators": true,
    "strictNullChecks": true
  }
}
```

### VROTSC config

The presence of a `tsconfig.json` file in a directory indicates that the directory is the root of a TypeScript project and is being read by the code editor for *autocompletion*. Modifying `tsconfig.json` in the context of `vrotsc` can affect only the code editor autocompletion.

Because of the nature of extending the typescript compiler and the specific runtime of {{ products.vro_short_name }}, `vrotsc` package has a default `tsconfig` that is applied for all  `typescript-project-all` projects.

`/typescript/vrotsc/src/compiler/config.ts`

Example

```javascript
  return {
   module: ts.ModuleKind.ESNext,
   moduleResolution: ts.ModuleResolutionKind.NodeJs,
   target: ts.ScriptTarget.ES5,
   lib: [
    "lib.es5.d.ts",
    "lib.es2015.core.d.ts",
    "lib.es2015.collection.d.ts",
    "lib.es2015.iterable.d.ts",
    "lib.es2015.promise.d.ts",
    "lib.es2017.string.d.ts",
    "lib.es2016.array.include.d.ts"
   ],
   strict: false,
   allowUnreachableCode: true,
   stripInternal: false,
   removeComments: false,
   experimentalDecorators: true,
   emitDecoratorMetadata: true,
   importHelpers: true,
   suppressOutputPathCheck: true,
   rootDir: rootDir,
   baseUrl: rootDir,
   allowJs: true,
   declaration: true,
   sourceMap: true,
   declarationMap: false,
  // verbatimModuleSyntax: true,
  ignoreDeprecations: "5.0"
};
```
