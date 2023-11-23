## Push Content
To push local content to the target environment execute the following maven command:
```bash
mvn package vrealize:push -P{{ archetype.customer_project.maven_profile_name}}
```
### Include Dependencies
By default, the ```vrealize:push``` goal will deploy all dependencies of the current project to the target environment. You can control that by the ```-DincludeDependencies``` flag. The value is ```true``` by default, so you skip the dependencies by executing the following:
```bash
mvn package vrealize:push -P{{ archetype.customer_project.maven_profile_name}} -DincludeDependencies=false
```

!!! note
    Dependencies will not be deployed if the server has a newer version of the same package deployed. For example, if the current project depends on ```com.vmware.pscoe.example-2.4.0``` and on the server there is ```com.vmware.pscoe.example-2.4.2```, the package will not be downgraded. You can force that by adding the ````-Dvra.importOldVersions``` flag:
    ```bash
    mvn package vrealize:push -P{{ archetype.customer_project.maven_profile_name}} -Dvra.importOldVersions
    ```
    The command above will forcefully deploy the exact versions of the dependent packages, downgrading anything it finds on the server.

### Ignore Certificates
This section describes how to bypass a security feature in development/testing environment. **Do not use those flags when targeting production servers.** Instead, make sure the certificates have the correct CN, use FQDN to access the servers and add the certificates to Java's key store (i.e. cacerts).


=== "Use Maven Profiles"
    You can ignore certificate errors **the certificate is not trusted** and **the CN does not match the actual hostname** by appending the following parameters to the target profile in your maven settings.xml file:

    ``` xml
    ...<!--# (1)! -->
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
    You can ignore certificate error, i.e. the certificate is not trusted, by adding the flag ```-Dvrealize.ssl.ignore.certificate```:
    ```bash
    mvn package vrealize:push -P{{ archetype.customer_project.maven_profile_name}} -Dvrealize.ssl.ignore.certificate
    ```

    You can ignore certificate hostname error, i.e. the CN does not match the actual hostname, by adding the flag ```-Dvrealize.ssl.ignore.certificate```:
    ```bash
    mvn package vrealize:push -P{{ archetype.customer_project.maven_profile_name}} -Dvrealize.ssl.ignore.hostname
    ```

