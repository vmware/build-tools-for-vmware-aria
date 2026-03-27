---
title: License Plugin
---

# License Plugin

## Overview

Build Tools for VMware Aria utilizes the **[license-maven-plugin](https://www.mojohaus.org/license-maven-plugin/index.html)** as a production-ready solution for managing licenses. The plugin is activated during the project build phase if the `license_data/licenses.properties` file exists in the project's root directory. Upon activation, it generates a license file and a license header using the templates provided in the `license_data/` folder.

---

## Usage

### Generating a Project with Custom Licensing

You must provide the following additional parameters when creating a new project:

* **`licenseUrl`**
    * This parameter has a default value of `null`.
    * When configured, the license URL is included in the new project's `pom.xml` description. The content found at the URL is saved as a license template file and utilized during the project's build phase.
* **`licenseHeader`**
    * **Valid values:** Either raw text or a valid URL.
    * **If set:** The provided text or the content from the URL is stored in a `license_header` template. The contents of this file are then used to populate the license header of each code file during the project's build phase.
    * **If not set:** The system defaults to using `"Copyright [yyyy] [name of copyright owner]"` for the `license_header`.
* **`licenseTechnicalPreview`**
    * **If set:** The default VMware Technical Preview license is applied to the generated project. When this flag is used, all previously mentioned properties (`licenseUrl` and `licenseHeader`) are ignored.

#### Command-Line Examples

**Standard Custom Licensing Example:**
```bash
mvn archetype:generate \
    -DinteractiveMode=false \
    -DarchetypeGroupId=com.vmware.pscoe.o11n.archetypes \
    -DarchetypeArtifactId=package-actions-archetype \
    -DarchetypeVersion={{ iac.latest_release }} # (1)! \
    -DgroupId={{ archetype.customer_project.group_id}} # (2)! \
    -DartifactId={{ archetype.customer_project.artifact_id}} # (3)! \
    -DlicenseUrl=https://example.com/license \
    -DlicenseHeader="Example License Header"
```

1.  {{ archetype.customer_project.archetype_version_hint }}
2.  {{ archetype.customer_project.group_id_hint }}
3.  {{ archetype.customer_project.artifact_id_hint }}

**Technical Preview License Example:**
```bash
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

---

### Generating Licenses Manually

If the `maven-license-plugin` is active, running `mvn install` automatically generates the license file and headers. You can also manually generate a third-party license list by executing `mvn license:add-third-party -Dlicense.useMissingFile`. For additional usage details, refer to the [official MojoHaus documentation](https://www.mojohaus.org/license-maven-plugin/usage.html).

#### Output Examples

**Generated Header in a `.js` File**:
```javascript
/*
* Example License
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

**Generated Content in `pom.xml`**:
```xml
...
<description>
  This package is licensed under https://example.com/license
</description>
...
<licenses>
  <license>
    <url>[https://example.com/license](https://example.com/license)</url>
  </license>
</licenses>
...
```

---

### Customizing the `maven-license-plugin`

You can customize the plugin's behavior by modifying its properties within the project's `pom.xml`. The default configuration values are as follows:

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
For more details, see the [maven-license-plugin page](https://www.mojohaus.org/license-maven-plugin).
