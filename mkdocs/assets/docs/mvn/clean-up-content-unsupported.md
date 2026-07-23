{% set page_vars = page.meta.vars | default({}) %}
{% set page_project = page_vars.project | default({}) %}

{% set project_type = page_project.type | default('the current type of') %}

### Clean Up Content

The cleaning up of project content from a target environment is not supported for the {{ project_type }} project. For details, see the [Project Type Support for Content Clean Up](#project-type-support-for-content-clean-up).

<!-- Clean Up Support Table -->
{% include-markdown "./clean-up-content-support-table.md" %}
