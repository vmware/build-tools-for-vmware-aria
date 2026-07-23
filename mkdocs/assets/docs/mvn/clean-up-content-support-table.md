{% set page_vars = page.meta.vars | default({}) %}
{% set page_maven = page_vars.maven | default({}) %}

{% set maven_goal = page_maven.goal | default('vrealize') %}

#### Project Type Support for Content Clean Up

The following table provides details about the support of the cleaning up of project content via the `{{ maven_goal }}:clean` command per project type (archetype).

| Archetype     | Supported | Comment                                                                 |
|---------------|-----------|-------------------------------------------------------------------------|
| vro           | Yes       | -                                                                       |
| vcd           | Partial   | Does not support dry run mode.                                          |
| abx           | No        | Not implemented.                                                        |
| vrops         | No        | Not implemented.                                                        |
| vra-ng        | Partial   | Does not support dry run mode.                                          |
| vrli          | No        | Product does not provide native package support.                        |
| cs            | No        | Product (Code Stream Services) does not provide native package support. |
| vcfa-all-apps | Partial   | Does not support dry run mode.                                          |
