# Building

## Overview

Maven command for building vROps / VCF Operations Content.

## Table Of Contents

1. [Usage](#usage)

### Usage

You can build any vROps / VCF Operations project from sources using Maven:

```bash
mvn clean package
```

This will produce a vROps / VCF Operations package with the groupId, artifactId and version specified in the pom. For example:

```xml
<project>
    <groupId>local.corp.it.cloud</groupId>
    <artifactId>sample</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <packaging>vrops</packaging>
</project>
```

will result in **local.corp.it.cloud.sample-1.0.0-SNAPSHOT.vrops** generated in the target folder of your project.
