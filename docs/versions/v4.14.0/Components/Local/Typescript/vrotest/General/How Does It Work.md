# How does it work

Maintainer specific details on how vrotest works

## Overview

`vrotest` gets executed by different Maven Mojos depending on the project archetype. The Mojos of interest are: `maven/plugins/typescript/src/main/java/com/vmware/pscoe/maven/plugins/TypescriptTestMojo.java`

## Table Of Contents

1. [Process](#process)
2. [Where is the test bed created](#where-is-the-test-bed-created)
3. [How is testing configured](#how-is-testing-configured)
4. [How is the vRO Runtime injected](#how-is-the-vro-runtime-injected)
5. [How are test helpers used?](#how-are-test-helpers-used)
6. [Where are the code coverage files](#where-are-the-code-coverage-files)

### Process

When `mvn clean test` or other maven lifecycle command that calls `test`, the `TypescriptTestMojo` is going to get called. The mojo will execute vrotest with `build` and then with `run`.

### Where is the test bed created?

The testbed is created under `PROJECT_ROOT/target/vro-tests`

### How is testing configured?

Testing is configured by adding `.nycrc` file in the `vro-tests` folder to configure code coverage and `jasmine.json` to configure the jasmine test runner.

### How is the vRO Runtime injected?

The vRO Scripting Api/vRO Runtime is injected as a jasmine helper in the `vro-tests/helpers` folder. The runtime injects a bunch of intrinsic classes as globals.

More information on Jasmine Helpers, read the official documentation [here](https://jasmine.github.io/pages/docs_home.html)

### How are test helpers used?

Test helpers are put in the same folder as the actions, giving you the ability to import them directly. vrotsc gives us the ability to specify the location, and even tho while packaging, they go to a different folder, during testing they go to the same folder

### Where are the code coverage files?

THe code coverage are located under `PROJECT_ROOT/target/vro-tests/coverage`.
