# Additional files

## Overview

The `-Pbundle-with-installer` maven profile has the ability to package additional files or directories as part of the installer bundle. The files and directories will be copied to the root of the installation directory.

## Table Of Contents

1. [Usage](#usage)

### Usage

File and directory paths can be configured in the project's `pom.xml` -> `<properties>` tag by providing up to 9 `<installer.included.item*>` values. This can be used for multiple purposes such as integration tests, environment properties for the `installer` script, JSON or YAML file containing inputs for Workflow executed with `installer` script, Ansible playbooks, etc.

E.g.

```xml
    <properties>
        <installer.included.item1>environment.properties</installer.included.item1>
        <installer.included.item2>integration-tests/**</installer.included.item2>
    </properties>
```

The above example will include the `environment.properties` file and `integration-tests` directory with all files located inside of it.
