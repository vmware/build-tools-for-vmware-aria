{% set page_vars = page.meta.vars | default({}) %}
{% set page_maven = page_vars.maven | default({}) %}
{% set page_project = page_vars.project | default({}) %}

{% set maven_goal = page_maven.goal | default('vrealize') %}
{% set project_type = page_project.type | default('the current type of') %}
{% set product_type = page_project.product_type | default('non-vro') %}

### Clean Up Content

This section describes the operation for cleaning up project content from a target environment.

<!-- markdownlint-disable MD051 -->
!!! Note
    Note that the supported functionality for the cleaning up of project content from a target environment depends on the project type. For details, see the [Project Type Support for Content Clean Up](#project-type-support-for-content-clean-up).
<!-- markdownlint-enable MD051 -->

#### Overview

A dedicated goal for cleaning up project packages from a given environment (server).

#### Usage

To clean up content from the target environment (defined in the Maven build profile that you specify in the `-P${PROFILE}` argument), use the following command.

```bash
mvn {{ maven_goal }}:clean -DincludeDependencies=true -DcleanUpOldVersions=true -DcleanUpLastVersion=false -Ddryrun=true -P{{ archetype.customer_project.maven_profile_name}}
```

Following is a list of the properties that you define via the `-D` command line arguments in the command with short description of their values.

- The `includeDependencies` flag specifies whether the package dependencies should be deleted together with the project package.
- The `cleanUpOldVersions` flag specifies whether old versions of the package should be deleted. When combined with the `includeDependencies` flag, the operation is extended to the dependencies of the project package as well.
- The `cleanUpLastVersion` flag specifies whether the latest version of the project package should be deleted before importing.
- The `dryrun` flag specifies whether the operation should be run in dry run mode. {% if product_type != "vro" %} <!-- markdownlint-disable MD051 -->

    !!! Note
        Note that the {{ project_type }} project does not support running the clean up operation in dry run mode. For details, see the [Project Type Support for Content Clean Up](#project-type-support-for-content-clean-up).
<!-- markdownlint-enable MD051 -->
{% endif %}

##### Examples

Following is a list of usage examples for cleaning up content from an environment.

- To clean up only the current project package version from the server and keep the older package versions and dependencies, use the command with the following argument values.

    ```bash
    mvn {{ maven_goal }}:clean -DcleanUpLastVersion=true -DcleanUpOldVersions=false -DincludeDependencies=false -P{{ archetype.customer_project.maven_profile_name}}
    ```

- To clean up the current project package version and its dependencies from the server but keep the older package versions, use the command with the following argument values. Note that this is a force removal operation.

    ```bash
    mvn {{ maven_goal }}:clean -DcleanUpLastVersion=true -DcleanUpOldVersions=false -DincludeDependencies=true -P{{ archetype.customer_project.maven_profile_name}}
    ```

- To clean up the old project package versions and the old versions of package dependencies but keep the current (latest) package version, use the command with the following argument values.

    ```bash
    mvn {{ maven_goal }}:clean -DcleanUpLastVersion=false -DcleanUpOldVersions=true -DincludeDependencies=true -P{{ archetype.customer_project.maven_profile_name}}
    ```

<!-- Clean Up Support Table -->
{% include-markdown "./clean-up-content-support-table.md" %}
