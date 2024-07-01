# Usage

Provides detailed instructions on how to use vrotsc

## Overview

`vrotsc` has only one command to run, with different options. Detailed instructions can be found below on the possible options as well as an example.

## Table Of Contents

1. [Running](#running)

### Running

#### Usage

```bash
vrotsc [SRC_DIR] [OPTIONS]
```

#### CLI options

The following commandline options are accepted:

```plaintext
  -h, --help                           Print this message.
  -v, --version                        Print the compiler's version.
  -p, --project                        Path to the tsconfig.json file to use.
  --emitHeader                         Emit auto-generated header on top of each file.
  --actionsOut                         Specify the output directory for actions.
  --testHelpersOut                     Specify the output directory for test helpers.
  --workflowsOut                       Specify the output directory for workflows.
  --configsOut                         Specify the output directory for configuration elements.
  --resourcesOut                       Specify the output directory for resource elements.
  --testsOut                           Specify the output directory for tests.
  --typesOut                           Specify the output directory for types.
  --mapsOut                            Specify the output directory for source maps.
  --actionsNamespace                   Specify actions namespace.
  --workflowsNamespace                 Specify workflows namespace.
```

#### Examples

```bash
vrotsc src \
 --actionsNamespace com.vmware.pscoe.example \
 --workflowsNamespace example \
 --files \
 --typesOut target/vro-types \
 --testsOut target/vro-sources/test/com/vmware/pscoe/example \
 --mapsOut target/vro-sources/map/com/vmware/pscoe/example \
 --actionsOut target/vro-sources/js/src/main/resources/com/vmware/pscoe/example \
 --testHelpersOut target/vro-sources/testHelpers/src/main/resources/com/vmware/pscoe/example \
 --workflowsOut target/vro-sources/xml/src/main/resources/Workflow \
 --policiesOut target/vro-sources/xml/src/main/resources/PolicyTemplate \
 --resourcesOut target/vro-sources/xml/src/main/resources/ResourceElement \
 --configsOut target/vro-sources/xml/src/main/resources/ConfigurationElement
```
