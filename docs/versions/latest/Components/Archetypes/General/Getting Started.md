# Getting Started

## Overview

This section contains common information valid for all available archetypes unless stated explicitly under a specific archetype's documentation.

## Table Of Contents

1. [Include Dependencies](#include-dependencies)
2. [Ignore Certificate Errors](#ignore-certificate-errors-not-recommended)
3. [Troubleshooting](#troubleshooting)

### Include Dependencies

By default, the `push` goal (e.g. `vrealize:push`, `vrops:push`, etc) deploys all dependencies of the current project to the target environment. This can be controlled that by the `-DincludeDependencies` flag. The value is `true` by default, so in order to skip the dependencies the flag can be provided in the following way:

```bash
mvn package vrealize:push -Pdev -DincludeDependencies=false
```

### Ignore Certificate Errors (Not recommended)

This section describes how to bypass a security feature in development/testing environment during pull and push operations. **Do not use those flags when targeting production servers.** Instead, make sure the certificates have the correct CN, use FQDN to access the servers and add the certificates to Java's key store (i.e. cacerts).

You can ignore certificate errors, i.e. the certificate is not trusted, by adding the flag `-Dvrealize.ssl.ignore.certificate` to the Maven command, e.g.:

```bash
mvn package vrealize:push -Pdev -Dvrealize.ssl.ignore.certificate
```

You can ignore certificate hostname error, i.e. the CN does not match the actual hostname, by adding the flag `-Dvrealize.ssl.ignore.certificate` to the Maven command, e.g.:

```bash
mvn package vrealize:push -Pdev -Dvrealize.ssl.ignore.hostname
```

You can also combine the two options above.

The other option is to set the flags in your Maven's `settings.xml` file for a specific **development** environment.

```xml
<profile>
    <id>dev</id>
    <properties>
        <!--    ..... Common Configuration .....  -->
        <vrealize.ssl.ignore.hostname>true</vrealize.ssl.ignore.hostname>
        <vrealize.ssl.ignore.certificate>true</vrealize.ssl.ignore.certificate>
        <!--    ..... VCFA Configuration .....  -->
        <vrang.host>flt-auto01.corp.internal</vrang.host>
        <!--    ..........  -->
        <!--    ..... VCF Operations Configuration .....  -->
        <vrops.host>flt-ops01a.corp.internal</vrops.host>
        <!--    ..........  -->
        <!--    ..... Other Product Configuration .....  -->
    </properties>
</profile>
```
### Troubleshooting

- If a build/push/pull operations fails and the Maven error does not contain enough information rerun it with *-X* debug flag.

  ```Bash
  mvn -X <rest of the command>
  ```

- Sometimes Maven might cache old artifacts. Force fetching new artifacts with *-U*. Alternatively remove `<home>/.m2/repository` folder.

  ```Bash
  mvn -U <rest of the command>
  ```
