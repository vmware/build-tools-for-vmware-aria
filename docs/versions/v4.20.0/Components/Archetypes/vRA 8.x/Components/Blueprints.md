# Blueprints

Blueprint (aka `Cloud Templates`) architects build Software components, machine blueprints, and custom XaaS blueprints and assemble those components into the blueprints that define the items users request from the catalog.

## Table Of Contents

1. [Structure](#structure) - how are blueprints exported and what does each file mean?
2. Operations
   1. [Importing](#importing) blueprints to vRA
3. [Known Issues](#known-issues)

### Structure

Below is an example structure of a blueprint export.

Example `content.yaml`

```yaml
blueprint:
  - Example Blueprint
# ...
```

Structure

```ascii
src/
├─ main/
│  ├─ resources/
│  │  ├─ blueprints/
│  │  │  ├─ Example Blueprint/
│  │  │  │  ├─ content.yaml
│  │  │  │  ├─ details.json
│  │  │  │  ├─ versions.json
```

Each blueprint will be placed in a different folder.

- `content.yaml` contains the exported code from the blueprint's canvas.
- `details.json` contains metadata information
- `versions.json` contains version information. Note that this is just the metadata, for now [the version history is not preserved](#version-history-gets-lost).

### Importing

When importing a blueprint, it is matched by its `name`. If there is a blueprint with the same name on the server, an update will be performed. Otherwise, the blueprint will be created instead.

Also when importing an already existing blueprint, we will check for any differences between local copy and server copy. If there are differences, a new version will be released. New version is determined by the already existing versions. If a patter of MAJOR.MINOR.PATCH is detected, vRBT will try to continue the numbering, otherwise a date formatted version is released.

#### Version Management

By default all versions that are not the latest one will be unreleased.

To control this behavior you can set:

```xml
<bp.unrelease.versions>false</bp.unrelease.versions>
```

### Known Issues

#### Version history gets lost

This is known and currently there is no workaround for this.
