## Clean Up Content
To clean up a version of a package from the server use:

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
