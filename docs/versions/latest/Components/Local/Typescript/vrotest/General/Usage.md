# Usage

Provides detailed instructions on how to use vrotest

## Overview

The usage is divided in 2 steps

1. `build` -> Builds the test bed
2. `run` -> Runs the unit tests

If `mvn clean test` is run inside of the created project, build will be executed after which run will be called.

## Table Of Contents

1. [Building](#building) - how to build the testbed
2. [Running](#running) - how to run the tests

[//]: # (Fill As many of these as you need. Use h4 and further here, do not include h1s, h2s or h3s.)

### Building

#### Usage

`vrotest build [options]`

#### CLI options

The following commandline options are accepted when building:

```txt
  --actions                     Path to the vRO JavaScript folder.
  --testHelpers                 Path to the vRO JavaScript test helpers folder.
  --tests                       Path to the folder containing all tests.
  --maps                        Path to the folder containing source maps when using TypeScript.
  --resources                   Path to the folder containing vRO resources.
  --configurations              Path to the folder containing vRO configurations.
  --dependencies                Path to the folder containing dependent vRO packages.
  --helpers                     Path to the folder containing vRO scripting API.
  --ts-src                      Path to the original TypeScript code.
  --ts-namespace                Namespace for the TypeScript project e.g. com.example.myproject
  --output                      Folder where testbed will be created.
  --coverage-thresholds         Defines the threshold (in %) for the test coverage. Tests will fail if coverage is below the threshold.
                                Format: <error>:<warn>:<type>
                                Types:
                                  * all
                                  * branches
                                  * lines
                                  * functions
                                  * statements
  --coverage-reports            Specify test coverage reports to use.
                                Availale values:
                                  * text
                                  * html
                                  * json
                                  * clover
                                  * cobertura
                                  * lcov
                                  * lcovonly
  --per-file                    Code coverage per file bases. Set custom --coverage-thresholds, if any file in the project drops below those thresholds, the build will fail
```

#### Examples

```bash
vrotest build --actions src --testHelpers src --tests test --output target/vro-test --coverage-thresholds 50:60:all,65:70:statements --coverage-reports text,html,lcovonly
```

### Running

#### Usage

```bash
vrotest run [testbed path] [options]
```

#### CLI options

The following commandline options are accepted when running tests:

```plaintext
  --instrument                        Instrument code and generate code coverage reports.
```

#### Examples

`vrotest run target/vro-test --instrument`
