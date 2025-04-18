# Build Tools for VMware Aria for vRO Projects

Before you continue with this section validate that all of the prerequisites are met.

## Prerequisites

- Install and Configure [Build Tools for VMware Aria System](setup-workstation-maven.md)

## Usage

### Crate New vRO Project

You start by bootstraping a new project using one of the provided archetypes:

#### JS-Based Actions-Only Project

This project contains only actions as \*.js files. It does not handle an end-to-end functionality, but is an excellent choice as a dependency for xml-based projects containing workflows, configurations and resource elements.

To create a new project of this type, you use the following command:

```bash
mvn archetype:generate \
    -DinteractiveMode=false \
    -DarchetypeGroupId=com.vmware.pscoe.o11n.archetypes \
    -DarchetypeArtifactId=package-actions-archetype \
    -DarchetypeVersion=<iac_for_vrealize_version> \
    -DgroupId=local.corp.it.cloud \
    -DartifactId=dns
```

This will generate the following project file structure:

```txt
dns
├── README.md
├── pom.xml
├── release.sh
└── src
    ├── main
    │   └── resources
    │       ├── local
    │       │   └── corp
    │       │       └── it
    │       │           └── cloud
    │       │               └── dns
    │       │                   └── sample.js
    │       ├── log4j.xml
    │       └── log4j2.xml
    └── test
        └── resources
            └── local
                └── corp
                    └── it
                        └── cloud
                            └── dns
                                └── SampleTests.js
```

You can delete the example action and test and start developing your code.

Every \*.js file that follows the convention (see sample.js for reference) will be compiled into a vRO action where the action name is the name of the file (e.g. sample) and the module (namespace) is the path under **src/main/resources** (e.g. "local.corp.it.cloud.dns").

Actions in vRO are essentially functions that you can call by using **System.getModule**. In plain JavaScript, we model actions in the same way. Therefore, in the JS format it is required to have a single root function that represents the action. Since JavaScript is dynamically typed and vRO isn't, it is recommended to describe the parameter types and the return value type in JsDoc (see sample.js for reference). If you omit the JsDoc all types will be assumed to be be "Any".

#### Xml-Based Project

This is the standard vRO project that can cover all use cases. It contains workflows, resource elements, configuration elements, actions and policies and can have dependencies to any other vRO project type.

To create a new project of this type, you use the following command:

```bash
mvn archetype:generate \
    -DinteractiveMode=false \
    -DarchetypeGroupId=com.vmware.pscoe.o11n.archetypes \
    -DarchetypeArtifactId=package-xml-archetype \
    -DarchetypeVersion=<iac_for_vrealize_version> \
    -DgroupId=local.corp.it.cloud \
    -DartifactId=dns \
    -DworkflowsPath=integration-service-1/workflows
```

This will produce the following project file structure:

```txt
util
├── pom.xml
├── release.sh
└── src
    └── main
        └── resources
            ├── Workflow
            │   └── Corp
            │       └── Cloud
            │           └── Util
            │               ├── Install.element_info.xml
            │               └── Install.xml
            └── dunes-meta-inf.xml
```

You can delete the example elements.

You need to build and import the package in vRO and start developing your code there. Make sure you sync the content from the vRO to your local file system regularly and commit your changes to SCM.

The **Install** workflow is the recommended way of automating the configuration/reconfiguration of your solution, so you can use the workflow generated by the archetype as a starting point and develop your installation code from there.

#### Mixed Project

Mixed project acts as virtual project combining the both JS-Based Actions-Only and XML-based projects under single unified structure. Such project type is useful for initial onboarding of existing vRO code into the toolchain or when there is no need for XML-based and JS-Based Actions-Only projects to have separate lifecycle.

To create a new project of this type, you use the following command:

```bash
mvn archetype:generate \
    -DinteractiveMode=false \
    -DarchetypeGroupId=com.vmware.pscoe.o11n.archetypes \
    -DarchetypeArtifactId=package-mixed-archetype \
    -DarchetypeVersion=<iac_for_vrealize_version> \
    -DgroupId=local.corp.it.cloud \
    -DartifactId=services \
    -DworkflowsPath=integration-service-1/workflows
```

This will produce the following project file structure:

```txt
services
├── actions
│   ├── pom.xml
│   └── src
│       ├── main
│       │   └── resources
│       │       ├── local
│       │       │   └── corp
│       │       │       └── it
│       │       │           └── cloud
│       │       │               └── services
│       │       │                   └── sample.js
│       │       ├── log4j.xml
│       │       └── log4j2.xml
│       └── test
│           └── resources
│               └── local
│                   └── corp
│                       └── it
│                           └── cloud
│                               └── services
│                                   └── SampleTests.js
├── pom.xml
├── release.sh
└── workflows
    ├── pom.xml
    └── src
        └── main
            └── resources
                ├── Workflow
                │   └── Corp
                │       └── Cloud
                │           └── Services
                │               ├── Install.element_info.xml
                │               └── Install.xml
                └── dunes-meta-inf.xml
```

#### TypeScript-Based Project

This project contains actions as \*.ts, workflows as \*.wf.ts, configuration elements as \*.conf.ts and resource files.

To create a new project of this type, you use the following command:

```bash
mvn archetype:generate \
    -DinteractiveMode=false \
    -DarchetypeGroupId=com.vmware.pscoe.o11n.archetypes \
    -DarchetypeArtifactId=package-typescript-archetype \
    -DarchetypeVersion=<iac_for_vrealize_version> \
    -DgroupId=local.corp.it.cloud \
    -DartifactId=demo
```

This will generate the following project file structure:

```txt
demo
├── README.md
├── pom.xml
├── release.sh
└── src
    ├── actions
    │   ├── actionSample.js
    │   ├── sample.test.ts
    │   └── sample.ts
    ├── elements
    │   ├── config
    │   │   ├── sample.conf.ts
    │   │   └── sample2.conf.yaml
    │   └── resource
    │       ├── sample.txt
    │       ├── sample2.json
    │       ├── sample2.json.element_info.json
    │       ├── sample3.xml
    │       ├── sample3.xml.element_info.json
    │       └── sample4.son
    ├── policies
    │   └── sample.pl.ts
    ├── types
    │   └── sample.d.ts
    └── workflows
            ├── sample.wf.ts
            └── sample.wf.form.json
```

You can delete the examples and start developing your code.

Every \*.ts file will be compiled into one of the corresponding types.

- Actions (sample.ts) will be compiled into the module \<groupId>.\<artifactId>.\<the path under `src/` folder>.
- Tests (sample.test.ts) will not be added to the vRO package, but will be compiled into Javascript and triggered the same way as for the JS based projects
- Configuration Elements (sample.conf.ts), Policies (sample.pl.ts), Workflows (sample.wf.ts) will be placed and named depending on their deocrator "path" and "name" properties. Resource Elements placement is depending on \*.element_info.json descriptor. If those are missing form the decorator  description they are defined under the \<artifactId>.\<the path under `src/` folder> category.
- VRA 8 workflow form (sample.wf.form.json) will be attached to its corresponding Workflow (sample.wf.ts)
- Types (sample.d.ts) will not be added to the final package.

##### Additional Options

If in the workflow's XML file the description is populated with the one or more of the following placeholders: $CUSTOMER, $PROJECT or $RELEASE then during pushing of the workflow to VRO those values will be replaced automatically with following values (according to the values in the project's pom.xml):

1. $CUSTOMER - extracted from the project's name (i.e. from customer01.project.name -> customer01), pom.xml tag: `xml <artifactId>customer01.project.name</artifactId>.`
2. $PROJECT - extracted from the project's name (i.e. from customer01.project.name -> project.name), pom.xml tag: `xml <artifactId>customer01.project.name</artifactId>.`
3. $RELEASE - extracted from the project's version (i.e. 1.0.0-SNAPSHOT -> 1.0.0) pom.xml tag: `xml <version>1.0.0-SNAPSHOT</version>`

##### Examples

###### Configuration Element

```ts
@Configuration({
    name: "Sample Config",
    path: "PSCoE/my-project",
    attributes: {
        field1: { type: "sring" },
        field2: {
            type: "SecureString",
            description: "It is secured"
        },
        field3: {
            type: "string",
            value: "defaultValue"
        }
    }

})
class SampleConfig {
    field4: string
}
```

###### Workflow

```ts
import { Workflow, Out } from "tsc-annotations";

@Workflow({
  id: "",
  name: "Sample Workflow",
  path: "PSCoE/my-project",
  description: "Sample workflow description",
  attributes: {},
  input: {
    foo: { type: "string" },
    bar: { type: "string" },
  },
  output: {
    result: { type: "Any" },
  },
  presentation: "",
})
class SampleWorkflow {
  public install(foo: string, bar: string, @Out result: any): void {
    System.log(`foo=${foo}, bar=${bar}`);
    result = "result value";
  }
}
```

###### Class

```ts
export class SampleClass {
  public sum(x: number, y: number) {
    return x + y;
  }
}
```

###### Test

```ts
import { SampleClass } from "./sample";

describe("Tests", () => {
  it("should sum two numbers", () => {
    expect(new SampleClass().sum(1, 2)).toBe(3);
  });
});
```

###### Action

```ts
import { LoggerFactory } from "./LoggerFactory";
/**
 * Sample Action description
 *
 * @param name string
 * @param options any
 *
 * @return Any
 */
(function getLogger(name: string, options?: any) {
  return new LoggerFactory().getLogger(name, options);
});
```

###### Policy Template

```ts
@PolicyTemplate({
    name: "Sample Policy",
    path: "PSCoE/my-project",
    type: "AMQP:Subscription"
})
class SamplePolicy {
    onMessage(self: AMQPSubscription, event: any) {
        let message = self.retrieveMessage(event);
        System.log(`Received message ${message.bodyAsText}`);
    }
}
```

### Unit Testing

If you use **js-based actions-only** project, you can create unit tests to help you develop and verify your code.

The tests should be written using Jasmine, as one would do with any other JavaScript code. The only difference is that there are a number of vRO scriptable objects (e.g. ConfigurationElement) that are not present in the enulated vRO scripting context. You will have to mock those by overriding variables in the global scope.

For this project type, there is a Jasmine-JUnit adapter that is already configured. You place your tests under `src/test` following the same folder structure as with the actions (e.g. **src/test/local/corp/it/cloud/dns**).

An example test file is shown bellow. It tests a custom action for working with configuration elements and for that it overrides the Server and the ConfigurationElement scriptable object classes in the context:

```js
describe("Something", function() {
    ConfigurationElement = function (name, attributes) {
        this.attributes = attributes;
        this.configurationElementCategory = null;
        this.description = "";
        this.name = name;
        this.version = "0.0.0";
        this.getAttributeWithKey = function getAttributeWithKey (key) {
            return this.attributes[key];
        };
        this.removeAttributeWithKey = function removeAttributeWithKey (key) {
            delete this.attributes[key];
        };
        this.setAttributeWithKey = function setAttributeWithKey (key, value) {
            this.attributes[key] = {key: key, value: value};
        };

        this.reload = function() {
            // all up-to-date
        };
    };
    Server = {
        query: jasmine.createSpy('query').and.returnValue(new ConfigurationElement("test"))
    }

    it("should set attribute", function() {
        System.getModule("local.corp.it.cloud.dns").setAttribute("Corp/Cloud/Util/test", "value", 1);
        var value = System.getModule("local.corp.it.cloud.dns").getAttribute("Corp/Cloud/Util/test", "value");
        expect(value).toBe(1);
    });
    it("should ...", function() {
      ...
    });
});
```

Note that all tests in a single file are executed in a single context, i.e. any changes to the global scope are persisted. However, different js files are executed in separate scripting contexts.

The Jasmine-JUnit adapter is part of the standard Maven lifecycle's testing phase, therefore tests will be discovered and executed every time you build your project. However, you can trigger tests explicitly:

```bash
mvn test # This will run all tests in the project without packaging it.
mvn test -Dtest=SomethingTest # This will run only tests in the SomethingTest.js file.
```

### Building

You can build any vRO project from sources using Maven:

```bash
mvn clean package
```

This will produce a vRO package with the groupId, artifactId and version specified in the pom. For example:

```xml
<groupId>local.corp.it.cloud</groupId>
<artifactId>dns</artifactId>
<version>1.0.0-SNAPSHOT</version>
<packaging>package</packaging>
```

will result in **local.corp.it.cloud.dns-1.0.0-SNAPSHOT.package** generated in the target folder of your project.

In case you want to produce a bundle.zip containing the package and all its dependencies, you use:

```bash
mvn clean pacakge -Pbundle
```

In case you want to include only a subset of the project tree you can use the "include" property specifying list of project relative locations to the files to include during packaging. Currently suported for both JS and XML type projects as well as for TS files producing actions (i.e. base .ts files).

```bash
mvn clean package -Dinclude="src/main/resources/com/vmware/package/action1.js,src/main/resources/com/vmware/package/action1.js"
```

This is useful in the case when multiple developers are working against same vRO and in order to limit the overlapping content updates. This way you can also get latest changes according to git diff:

```bash
mvn clean package -Dinclude="$(git diff --name-only origin/master | tr '\n' ',')"
```

### Pull

When working on a vRO project, sometimes you might need to make changes on a live server using the vRO Client, e.g. when working on workflows.

Although this is applicable for both xml and js-based projects, it is more suitable for xml-based ones where you have the need to work on workflows, configuration and resource elements.

To support this use case, the toolchain comes with a custom goal "vro:pull". The following command will "pull" the package corresponding to the current project from a specified server and expand its content in the local filesystem overriding any local content:

```bash
vro:pull -Dvro.host=10.29.26.18 -Dvro.port=8281 -Dvro.username=administrator@vsphere.local -Dvro.password=***
```

Sometimes you want to pull the content of another package into your project. This can be achieved by providing the remote package name as parameter.

```bash
vro:pull -DpackageName=com.vmware.pscoe.library.ssh -Dvro.host=10.29.26.18 -Dvro.port=8281 -Dvro.username=administrator@vsphere.local -Dvro.password=***
```

A better approach is to have the different vRO/vRA development environments specified as profiles in the local `settings.xml` file by adding the following snippet under "profiles":

```xml
<servers>
    <server>
        <username>administrator@vsphere.local</username>
        <password>{native+maven+encrypted+pass}</password>
        <id>corp-dev-vro</id>
    </server>
</servers>
....
<profile>
    <id>corp-dev</id>
    <properties>
        <!--vRO Connection-->
        <vro.host>10.29.26.18</vro.host>
        <vro.port>8281</vro.port>
        <vro.serverId>corp-dev-vro</vro.serverId>
        <vro.auth>vra</vro.auth>
        <vro.tenant>vsphere.local</vro.tenant>
    </properties>
</profile>
```

Then you can sync content back to your local sources by simply activating the profile:

```bash
mvn vro:pull -Pcorp-env
```

### Push

To deploy the code developed in the local project or checked out from source control to a live server, you can use the `vrealize:push` command:

```bash
mvn package vrealize:push -Pcorp-env
```

This will build the package and deploy it to the environment described in the `corp-env` profile. There are a few
additional options.

### Include Dependencies

By default, the `vrealize:push` goal will deploy all dependencies of the current project to the target environment. You can control that by the `-DincludeDependencies` flag. The value is `true` by default, so you skip the dependencies by executing the following:

```bash
mvn package vrealize:push -Pcorp-env -DincludeDependencies=false
```

Note that dependencies will not be deployed if the server has a newer version of the same package deployed. For example, if the current project depends on `com.vmware.pscoe.example-2.4.0` and on the server there is `com.vmware.pscoe.example-2.4.2`, the package will not be downgraded. You can force that by adding the ``-Dvro.importOldVersions` flag:

```bash
mvn package vrealize:push -Pcorp-env -Dvro.importOldVersions
```

The command above will forcefully deploy the exact versions of the dependent packages, downgrading anything it finds on the server.

#### Push only the files that changes from one commit to another

The `vrealize:push` goal will deploy all dependencies of the current project to the target environment. You can control what files need to be refreshed wit the `-Dfiles` flag. The value of this property is a list with the names of the files that change from one commit to another.  

In Windows PowerShell console:

```bash
 mvn clean compile package vrealize:push -D"files"=$($((git diff --name-only master | where{($_ -match ".*js*") -or ($_ -match ".*ts*")} | get-item | select -ExpandProperty basename) -join ',')))
```

In Windows Git Bash console:

```bash
 mvn clean compile package vrealize:push -D"files"=$(git diff --name-only origin/master | grep -E -i "\.(js|ts)$" | xargs -I {} basename {} | tr '\n' ',')
```

Other approach should be just import some independt files. To do this the parameter "files" needs to be populated.

In Windows PowerShell console:

```bash
 mvn clean compile package vrealize:push -D"files"=BearerTokenFetcher.ts,ImailBox.ts,Test.js
```

In Windows Git Bash console:

```bash
 mvn clean compile package vrealize:push -D"files"=BearerTokenFetcher.ts,ImailBox.ts,Test.js
```

#### Ignore Certificate Errors (Not recommended)

> This section describes how to bypass a security feature in development/testing environment. **Do not use those flags when targeting production servers.** Instead, make sure the certificates have the correct CN, use FQDN to access the servers and add the certificates to Java's key store (i.e. cacerts).

You can ignore certificate errors, i.e. the certificate is not trusted, by adding the flag `-Dvrealize.ssl.ignore.certificate`:

```bash
mvn package vrealize:push -Pcorp-env -Dvrealize.ssl.ignore.certificate
```

You can ignore certificate hostname error, i.e. the CN does not match the actual hostname, by adding the flag `-Dvrealize.ssl.ignore.certificate`:

```bash
mvn package vrealize:push -Pcorp-env -Dvrealize.ssl.ignore.hostname
```

You can also combine the two options above.

The other option is to set the flags in your Maven's settings.xml file for a specific **development** environment.

```xml
<profile>
    <id>corp-dev</id>
    <properties>
        <!--vRO Connection-->
        <vro.host>10.29.26.18</vro.host>
        <vro.port>8281</vro.port>
        <vro.username>administrator@vsphere.local</vro.username>
        <vro.password>***</vro.password>
        <vro.auth>vra</vro.auth>
        <vro.tenant>vsphere.local</vro.tenant>
        <vro.authHost>{auth_host}</vro.authHost>
        <vro.authPort>{auth_port}</vro.authPort> 
        <vro.refresh.token>{refresh_token}</vro.refresh.token> 
        <vro.proxy>http://proxy.host:80</vro.proxy>
        <vrealize.ssl.ignore.hostname>true</vrealize.ssl.ignore.hostname>
        <vrealize.ssl.ignore.certificate>true</vrealize.ssl.ignore.certificate>
    </properties>
</profile>
```

### Bundling

To produce a bundle.zip containing the package and all its dependencies, use:

```bash
mvn clean deploy -Pbundle
```

Refer to [Build Tools for VMware Aria](setup-workstation-maven.md)/Bundling for more information.

### Clean Up

To clean up a version of vRO package from the server use:

- Clean up only curent package version from the server

  ```bash
  mvn vrealize:clean -DcleanUpLastVersion=true -DcleanUpOldVersions=false -DincludeDependencies=false
  ```

- Clean up curent package version from the server and its dependencies. This is a force removal operation.

  ```bash
  mvn vrealize:clean -DcleanUpLastVersion=true -DcleanUpOldVersions=false -DincludeDependencies=true
  ```

- Clean up old package versions and the old vertions of package dependencies.

  ```bash
  mvn vrealize:clean -DcleanUpLastVersion=false -DcleanUpOldVersions=true -DincludeDependencies=true
  ```

### Troubleshooting

- If Maven error does not contain enough information rerun it with _-X_ debug flag.

  ```Bash
  mvn -X <rest of the command>
  ```

- Sometimes Maven might cache old artifats. Force fetching new artifacts with _-U_. Alternativelly remove `<home>/.m2/repository` folder.

  ```Bash
  mvn -U <rest of the command>
  ```
