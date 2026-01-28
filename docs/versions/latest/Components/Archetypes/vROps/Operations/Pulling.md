# Pulling

Maven command for pulling VCF Operations Content

## Overview

When working on a VCF Operations project, you mainly make changes on a live server using the VCF Operations UI and then you need to capture those changes in the maven project on your filesystem to be able to store the content, track changes, collaborate, etc.

## Table Of Contents

1. [Usage](#usage)
2. [Wildcard Support](#wildcard-support)

### Usage

To support this use case, the toolchain comes with a custom goal `vrops:pull`. The following command will `pull` the content outlined into *Content Descriptor* file to the current project from a specified server and expand its content in the local filesystem overriding any local content:

```bash
mvn vrops:pull -P{profile}
```

> Note that `vrops:pull` will fail if the content.yaml is empty or it cannot find some of the described content on the target vROps / VCF Operations server.

### Wildcard Support

The content descriptor supports wildcard for most of the asset types. This means that you can specify a wildcard (*) symbol in the asset names defined in the `content.yaml` file exporting all assets matching the wildcard expression. E.g.

```yaml
report:
  - "*reports"
```

> Due to limitation of vROPs REST API wildcard is currently NOT supported for the dashboard and metric-config asset types.

> If you specify a wildcard in the asset name defined in the content.yaml file, it needs to be enclosed with quotes (").

> You can also enclose the asset name with quotes (") in the content.yaml file, even if you specify it with its full name.
