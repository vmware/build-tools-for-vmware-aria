# Pulling

Maven command for pulling VCF Operations for Logs Content

## Overview

When working on a VCF Operations for Logs project, you mainly make changes on a live server using the VCF Operations for Logs UI and then you need to capture those changes in the maven project on your filesystem to be able to store the content, track changes, collaborate, etc.

## Table Of Contents

1. [Usage](#usage)

### Usage

To support this use case, the toolchain comes with a custom goal `vrli:pull`. The following command will `pull` the content outlined into *Content Descriptor* file to the current project from a specified server and expand its content in the local filesystem overriding any local content:

```bash
mvn vrli:pull -P{profile}
```
