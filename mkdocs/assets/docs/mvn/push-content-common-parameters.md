{% set page_vars = page.meta.vars | default({}) %}
{% set page_maven = page_vars.maven | default({}) %}
{% set page_project = page_vars.project | default({}) %}

{% set maven_goal = page_maven.goal | default('vrealize') %}
{% set product_type = page_project.product_type | default('non-vro') %}

##### Include Dependencies

By default, the `{{ maven_goal }}:push` goal deploys all dependencies of the current project to the target environment. You can control this behavior by adding the `-DincludeDependencies` flag with the value `false` (to overwrite the default value `true`).

To skip deploying the project dependencies, run the following command.

```bash
mvn package {{ maven_goal }}:push -P{{ archetype.customer_project.maven_profile_name}} -DincludeDependencies=false
```

{% if product_type == "vro" %}
!!! note
    Note that dependencies packages are not deployed to the target environment if the Orchestrator server already has a newer version of the same package deployed.

    For example, if your project depends on `com.vmware.pscoe.example-2.4.0` but the target server already has `com.vmware.pscoe.example-2.4.2` deployed on it, the package on the server will not be downgraded and the dependency package will not be deployed. You can force the deployment of the older version of the dependency package on the remote server by adding the `-Dvro.importOldVersions` flag.

    The following command will forcefully deploy the exact versions of the dependent packages, downgrading anything it finds on the server.

    ```bash
    mvn package {{ maven_goal }}:push -P{{ archetype.customer_project.maven_profile_name}} -Dvro.importOldVersions
    ```
{% endif %}

##### Ignore Certificates

This section describes the flags that allow you to to bypass the security feature for verifying the certificate and certificate hostname of the remote server in a *development/testing* environment.

!!! Warning

    *Do not use the flags described in this section when targeting production servers.* 
    
    Instead, make sure that the certificates have the correct CN, use FQDN to access the servers, and add the certificates to the Java key store (i.e. `cacerts`).

=== "Use the Maven Profile"

    You can ignore certificate errors for all operations by adding the following parameters to the target profile in your Maven `settings.xml` file.

    - For the **the certificate is not trusted** error, use the `vrealize.ssl.ignore.certificate` parameter.
    - For the **the CN does not match the actual hostname** error, use the `vrealize.ssl.ignore.hostname` parameter.

    ```xml 
    <!-- Parameters in settings.xml file --> <!-- # (1)! -->
    ...
    <profiles>
      ...
      <profile>
          <id>{{ archetype.customer_project.maven_profile_name}}</id>
          <properties>
              ...
              <vrealize.ssl.ignore.hostname>true</vrealize.ssl.ignore.hostname>
              <vrealize.ssl.ignore.certificate>true</vrealize.ssl.ignore.certificate>
          </properties>
      </profile>
    </profiles>
    ```

    1.  {{ archetype.customer_project.maven_settings_location_hint}}

=== "Directly Pass The Parameters"

    You can ignore certificate errors for specific operations by adding the following flags to your command.

    - To ignore the **the certificate is not trusted** error, add the `-Dvrealize.ssl.ignore.certificate` flag to your command.
    
        ```bash
        mvn package {{ maven_goal }}:push -P{{ archetype.customer_project.maven_profile_name}} -Dvrealize.ssl.ignore.certificate
        ```

    - To ignore the **the CN does not match the actual hostname** error, add the flag `-Dvrealize.ssl.ignore.certificate`:

        ```bash
        mvn package {{ maven_goal }}:push -P{{ archetype.customer_project.maven_profile_name}} -Dvrealize.ssl.ignore.hostname
        ```
