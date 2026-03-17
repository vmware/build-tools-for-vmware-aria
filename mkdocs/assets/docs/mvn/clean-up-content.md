### Clean Up Content

#### Overview

A dedicated goal is implemented to clean up project packages on a given environment.

#### Usage

```bash
mvn vrealize:clean -DincludeDependencies=true -DcleanUpOldVersions=true -DcleanUpLastVersion=false -Ddryrun=true -P${PROFILE}
```

- includeDependencies - a flag depicting if dependencies should also be deleted
- cleanUpOldVersions - a flag depicting if old versions of the package (and dependencies in case of includeDependencies) should be deleted
- cleanUpLastVersion - a flag depicting if the latest version should also be deleted before importing
- dryrun - Dryrun or not

##### Examples

- Clean up only current package version from the server
  ```
  mvn vrealize:clean -DcleanUpLastVersion=true -DcleanUpOldVersions=false -DincludeDependencies=false
  ```
- Clean up current package version from the server and its dependencies. This is a force removal operation.
  ```
  mvn vrealize:clean -DcleanUpLastVersion=true -DcleanUpOldVersions=false -DincludeDependencies=true
  ```
- Clean up old package versions and the old version of package dependencies.
  ```
  mvn vrealize:clean -DcleanUpLastVersion=false -DcleanUpOldVersions=true -DincludeDependencies=true
  ```

<!-- Clean Up Support Table -->
{% include-markdown "./clean-up-content-support-table.md" %}
