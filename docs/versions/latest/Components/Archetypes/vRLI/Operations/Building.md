# Building

## Overview

Maven command for building vRLI / VCF Operations for Logs Content.

## Table Of Contents

1. [Usage](#usage)

### Usage

You can build any vRLI / VCF Operations for Logs project from sources using Maven:

```bash
mvn clean package
```

This will produce a VRLI / VCF Operations for Logs package with the groupId, artifactId and version specified in the pom. For example:

```xml
<project>
    <groupId>local.corp.it.cloud</groupId>
    <artifactId>sample</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <packaging>vrli</packaging>
</project>
```

will result in **local.corp.it.cloud.sample-1.0.0-SNAPSHOT.vrli** generated in the target folder of your project.
