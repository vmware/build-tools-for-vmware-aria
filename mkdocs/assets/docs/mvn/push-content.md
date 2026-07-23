{% set page_vars = page.meta.vars | default({}) %}
{% set page_maven = page_vars.maven | default({}) %}

{% set maven_goal = page_maven.goal | default('vrealize') %}

### Push Content

This section describes the operation for pushing project content to the target environment.

#### Overview

A Maven goal for packaging and deploying all local content from `./src` directory to the remote server.

#### Usage

To push the project content to the target environment (defined in the Maven build profile that you specify in the `-P${PROFILE}` argument), use the following command.

```bash
mvn clean package {{ maven_goal }}:push -P{{ archetype.customer_project.maven_profile_name}}
```

<!-- Push Common Section -->
{% include-markdown "./push-content-common-parameters.md" %}
