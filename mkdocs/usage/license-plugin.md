---
title: License Plugin
---

# License Plugin

## Overview

Build Tools for VMware Aria uses **[license-maven-plugin](https://www.mojohaus.org/license-maven-plugin/index.html)** as a production-ready solution for managing the licenses.
The plugin is actived during project build phase if **license_data/licenses.properties** exists in project's root folder. If activated it generates license file and license header from templates provided in **license_data/**.

## Usage

### Generate project with custom licensing

You need to provide the following additional parameters when creating new project:

- **licenseUrl**
    - The parameter has a default value of `null`.
    - If it is set, the license URL is included in the new project `pom.xml` description. The content of the URL is saved as a license template file and is used during project's build phase.

- **licenseHeader**
    - Valid value: Either text or URL.
    - If the param is set: The text, or the content of the provided URL is stored in a `licence_header` template. The content of that file is used to fill the license header of each code file during the build phase of the project.
    - If the param is not set: A default content of a `licence_header` is used: `"Copyright [yyyy] [name of copyright owner]"`.

- **licenseTechnicalPreview**
  - If set: The default VMware Technical Preview license is used in the generated project. All upper properties are ignored.

#### Examples

```Bash
mvn archetype:generate \
    -DinteractiveMode=false \
    -DarchetypeGroupId=com.vmware.pscoe.o11n.archetypes \
    -DarchetypeArtifactId=package-actions-archetype \
    -DarchetypeVersion={{ iac.latest_release }} # (1)! \
    -DgroupId={{ archetype.customer_project.group_id}} # (2)! \
    -DartifactId={{ archetype.customer_project.artifact_id}} # (3)! \
    -DlicenseUrl=https://example.com/license \
    -DlicenseHeader=Example Licence Header
```

1.  {{ archetype.customer_project.archetype_version_hint }}
2.  {{ archetype.customer_project.group_id_hint }}
3.  {{ archetype.customer_project.artifact_id_hint }}

```shell
mvn archetype:generate \
    -DinteractiveMode=false \
    -DarchetypeGroupId=com.vmware.pscoe.o11n.archetypes \
    -DarchetypeArtifactId=package-actions-archetype \
    -DarchetypeVersion={{ iac.latest_release }} # (1)! \
    -DgroupId={{ archetype.customer_project.group_id}} # (2)! \
    -DartifactId={{ archetype.customer_project.artifact_id}} # (3)! \
    -DlicenseTechnicalPreview
```

1.  {{ archetype.customer_project.archetype_version_hint }}
2.  {{ archetype.customer_project.group_id_hint }}
3.  {{ archetype.customer_project.artifact_id_hint }}

### Generate licenses

If the **maven-license-plugin** is active, it generates license file and license headers on `mvn install`. You can manually create 3rd party license list with `mvn license:add-third-party -Dlicense.useMissingFile`. For more information about usage, [click here](https://www.mojohaus.org/license-maven-plugin/usage.html)

#### Examples

- Generated header in `.js` file.

  ```jshelllanguage
  /*
  * Example Licence
  * %%
  * Copyright (C) 2020 VMWARE
  * %%
  */
  /**
  * Write a brief description of the purpose of the action.
  * @param {number} x - describe each parameter as in JSDoc format.
  * @param {number} y - you can use different vRO types.
  * @returns {number} - describe the return type as well
  */
  (function (x, y) {
      return x + y;
  });
  ```

- Generated content in `pom.xml`.

  ```xml
  ...

  <description>
    This package is licensed under https://example.com/license
  </description>

  ...

  <licenses>
    <license>
      <url>https://example.com/license</url>
    </license>
  </licenses>
  ...
  ```

### Customizing the `maven-license-plugin`

You can customize the plugin behaviour by editing its properties in project's `pom.xml`. The default values are:

```xml
<properties>
    <license.licenseName>_license</license.licenseName>
    <license.encoding>UTF-8</license.encoding>
    <license.licenseResolver>${project.baseUri}license_data</license.licenseResolver>
    <license.licenceFile>${basedir}/LICENSE</license.licenceFile>
    <license.thirdPartyFilename>THIRD-PARTY</license.thirdPartyFilename>
    <license.useMissingFile>true</license.useMissingFile>
    <license.organizationName>VMWARE</license.organizationName>
    <license.excludedScopes>test</license.excludedScopes>
    <license.excludedArtifacts>maven-surefire-plugin</license.excludedArtifacts>
    <license.excludeTransitiveDependencies>true</license.excludeTransitiveDependencies>
    <license.canUpdateCopyright>true</license.canUpdateCopyright>
    <license.canUpdateDescription>true</license.canUpdateDescription>
    <license.includes>**/*.js,**/*.ts</license.includes>
    <license.excludes></license.excludes>
</properties>
```

For more details see [https://www.mojohaus.org/license-maven-plugin](https://www.mojohaus.org/license-maven-plugin).
