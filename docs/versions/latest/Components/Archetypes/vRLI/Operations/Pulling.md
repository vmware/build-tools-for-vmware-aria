# Pulling

Maven command for pulling Operations for Logs Content

## Overview

When working on a Operations for Logs project, you mainly make changes on a live server using the Operations for Logs UI and then you need to capture those changes in the maven project on your filesystem to be able to store the content, track changes, collaborate, etc.

## Table Of Contents

1. [Usage](#usage)
2. [Wildcard Support](#wildcard-support)

### Usage

To support this use case, the toolchain comes with a custom goal `vrli:pull`. The following command will `pull` the content outlined into *Content Descriptor* file to the current project from a specified server and expand its content in the local filesystem overriding any local content:

```bash
mvn vrli:pull -P{profile}
```

> Note that `vrli:pull` will fail if the content.yaml is empty or it cannot find some of the described content on the target VRLI server.


### Wildcard Support

The content descriptor supports wildcard. This means that if a wildcard is present in the asset name, all assets matching the wildcard expression will be exported to the local file system. The example above shows how to use wildcard in the asset names. E.g.

```yaml
alerts:
  - VC*
content-packs:
  - VMware *
```
