
# Content

## Overview

The Operations for Logs elements that you want to work with are described in the `content.yaml`

## Table Of Contents

1. [Content.yaml](#contentyaml)

### Content.yaml

By default the `content.yaml` looks like this.

```yaml
alerts:
content-packs:
```

All object types are list of values.


#### Import Rules for content types

- All local objects available in `./src` folder are imported.

#### Export Rules for content types

- Only explicitly defined objects are exported.

#### Examples

Example of `content.yaml` file.

```yaml
alerts:
  - VC stats query time-out occurred
content-packs:
  - VCF Operations
```
