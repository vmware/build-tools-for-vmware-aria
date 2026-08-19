{% set page_vars = page.meta.vars | default({}) %}
{% set page_project = page_vars.project | default({}) %}
{% set page_maven = page_vars.maven | default({}) %}

{% set project_artifact_id = page_project.artifact_id | default('project_artifact_id') %}
{% set product_type = page_project.product_type | default('non-vro') %}
{% set installer_directory_name = page_maven.installer_directory_name | default('') %}
{% set installer_file_extension = page_maven.installer_file_extension | default('') %}

### Bundle the Project

This section describes the operation for bundling the project into an installation bundle (ZIP file).

#### Overview

A Maven profile for producing an installation bundle that contains the solution package, all its dependencies, and scripts for deploying them to a target environment.

#### Usage

To bundle the project, use the following command.

```bash
mvn clean package -P{{ extra.general.installer_bundle_profile }}
```

The command produces a ZIP file (installation bundle) with a name that follows the pattern `<project.groupId>.<project.artifactId>-<project.version>-bundle.zip`, where the values for the placeholders are retrieved from the respective properties of the project `pom.xml` file.

Following is a sample listing of the structure of the installation bundle ZIP file for the {{ project_type }} project in which the project version is `0.1.0-SNAPSHOT`.

<!-- markdownlint-disable MD033 -->
<a id="bundle_default_structure"></a>
<!-- markdownlint-enable MD033 -->
```ascii title="Sample structure of an installation bundle"
{{ archetype.customer_project.group_id }}.{{ page.meta.vars.project.artifact_id }}-0.1.0-SNAPSHOT-bundle.zip
└── bin
    └── installer
    └── installer.bat
└── etc
    └── logback.xml
└── repo/
└── {{ page.meta.vars.maven.installer_directory_name }} {% if product_type == "vro" %}
    └── {{ archetype.customer_project.group_id }}.dependency-package-1-1.1.0.{{ page.meta.vars.maven.installer_file_extension }}  
    └── {{ archetype.customer_project.group_id }}.dependency-package-2-1.2.0.{{ page.meta.vars.maven.installer_file_extension }}  {% endif %}
    └── {{ archetype.customer_project.group_id }}.{{ page.meta.vars.project.artifact_id }}-0.1.0-SNAPSHOT.{{ page.meta.vars.maven.installer_file_extension }}
```

Following is a list of the directories in the installation bundle with a short description of their contents.

- The `bin` directory contains the `installer` script (CLI tool) for importing the solution components from the bundle in two formats, Bash (for Linux/Mac OS) and `.bat` (for Windows). For more information about using the CLI tool, see the documentation of the [Installer](../../../usage/installer/index.md).
- The `etc` directory contains configuration files of the Java binaries for the `installer` script, such as the logging configurations in the `logback.xml` file. For more information about the logging configuration file, see [Logging Configuration](../../../usage/installer/index.md#logging-configuration).
- The `repo` directory contains the Java binaries for the `installer` script.
- The `{{ page.meta.vars.maven.installer_directory_name }}` directory contains the project file(s) with the components that you can import to the remote server.

    !!! Note
        Based on your configurations and dependencies, you can have multiple Maven modules in a single installation bundle. So in addition to the `{{ page.meta.vars.maven.installer_directory_name }}` directory for the {{ project_type }} project, you can additional modules for projects of other types.

##### Bundle Additional Files

By default, the `-P{{ extra.general.installer_bundle_profile }}` Maven profile packages only the files and directories that are strictly necessary for importing the project components to the target system (as shown in the [Sample structure of an installation bundle](#bundle_default_structure) listing). However, it also allows you to package additional files or directories from you repository as part of the bundle ZIP and copy them to root of the installation bundle.

You can use such additional files and directories to bundle supplementary version-controlled content (such as integration tests, files with sample inputs for Orchestrator workflows, properties files for the `installer` script, Ansible playbooks, etc.) in a single deliverable file.

You specify the additional content that you want to add to the bundle in the `<properties>` element of the `pom.xml` file of the project. You can use up to nine `<installer.included.item*>` elements (in which `*` stands for a number between 1 and 9) to specify the paths to the files and directories that you want to include.

For example, the following sample configuration of a `pom.xml` file includes a file with the name `environment.properties` as `item1` and the contents of the `integration-tests/` directory as `item2`.

```xml title="Sample pom.xml file"
<!-- Parameters for including additional files in installation bundles in the pom.xml file --> 
<project>
    ...
    <properties>
        <installer.included.item1>environment.properties</installer.included.item1>
        <installer.included.item2>integration-tests/**</installer.included.item2>
    </properties>
    ...
</project>
```
