### Push Content

#### Overview

Maven goal for packaging and deploying all local content from `./src` folder to the remote server.

#### Usage

```bash
mvn clean package vrealize:push -P{{ archetype.customer_project.maven_profile_name}}
```

<!-- Push Common Section -->
{% include-markdown "./push-content-common-parameters.md" %}
