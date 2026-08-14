### Bundle the Project

This section describes the operation for bundling the project.

#### Overview

A Maven goal for producing an installation bundle that contains the solution package, all its dependencies, and scripts for deploying them to a target environment.

#### Usage

To bundle the project, use the following command.

```bash
mvn clean package -P{{ extra.general.installer_bundle_profile }}
```

For more information about using the bundle, see the documentation of the [Installer](../../../usage/installer.md) CLI tool.
