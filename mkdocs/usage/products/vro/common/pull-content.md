### Pull Content

#### Overview

When working on an {{ products.vro_short_name }} XML project, you mainly make changes on a live server using the {{ products.vro_short_name }} UI and then you can capture those changes in the maven project on your filesystem to be able to store the content, track changes, collaborate, etc.

#### Usage

To support this use case, a custom maven goal `vro:pull` is used. During this operation the package in {{ products.vro_short_name }} UI -> Assets -> Packages has the role of *Content Descriptor* and defines what content is exported. Everything added to that package is exported during pull operation.

The following command will `pull` the content outlined into the {{ products.vro_short_name }} package to the current project from a specified server and expand its content in the local filesystem overriding any local content:

```bash
mvn vro:pull -P{{ archetype.customer_project.maven_profile_name}}
```

!!! warning
    The command will fail with `404` error if the package does not exist on the target {{ products.vro_short_name }} server.

!!! note
    If the package does not exist in the target server it can be manually created via the UI by following the naming convention and the current values of parameters from your project's `pom.xml`: **{groupId}.{artifactId}-{version}.package**. However, before your first pull you need to have certain maven dependencies downloaded locally which happens during your first build operation.
    Thus it is recommended to first push your current content (which also triggers a build operation) to the target {{ products.vro_short_name }} server at least once before manually adding new content to the package from UI and pulling it locally.
