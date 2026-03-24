# Bundling

## Overview

Maven command for creating and installation bundle.

## Table Of Contents

1. [Usage](#usage)

### Usage

To produce an installation bundle containing all dependencies and a script for importing to target environment use:

```bash
mvn clean package -Pbundle-with-installer
```

For more information refer to [Installer documentation](../../../Local/Installer/README.md)/.
