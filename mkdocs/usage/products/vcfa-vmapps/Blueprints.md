### Blueprints

Blueprint (aka `Cloud Templates`) architects build Software components, machine blueprints, and custom XaaS blueprints and assemble those components into the blueprints that define the items users request from the catalog.

#### Structure

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
- `details.json` contains metadata information.
- `versions.json` contains version information. Note that this is just the metadata, for now the version history is not preserved.

#### Importing

When importing a blueprint, it is matched by its `name`. If there is a blueprint with the same name on the server, an update will be performed. Otherwise, the blueprint will be created instead.

Also when importing an already existing blueprint, we will check for any differences between local copy and server copy. If there are differences, a new version will be released. New version is determined by the already existing versions. If a patter of MAJOR.MINOR.PATCH is detected, vRBT will try to continue the numbering, otherwise a date formatted version is released.

#### Version Management

When pushing a blueprint to a {{ products.vra_9_short_name }} server that contains previously released blueprint with the same name as the one being pushed, a new version will be created and released in order to maintain the intended state. A new version will *not* be created if the content of the blueprint has not been modified since the latest released version in order to avoid unnecessary versioning.

If there's a custom form associated with the blueprint being imported and there's no previously released version, an initial blueprint version (1) will be created and released in order to import the custom form.

When creating a new version in the above-described cases, the new version will be auto-generated based on the latest version of the blueprint. The following version formats are supported with their respective incrementing rules:

| Latest version | New version         | Incrementing rules                                         |
|----------------|---------------------|------------------------------------------------------------|
| 1              | 2                   | Increment major version                                    |
| 1.0            | 1.1                 | Major and minor version - increment the minor              |
| 1.0.0          | 1.0.1               | Major, minor and patch version - incrementing the patch    |
| 1.0.0-alpha    | 2020-05-27-10-10-43 | Arbitrary version - generate a new date-time based version |

By default all versions that are not the latest one will be unreleased.

To control this behavior you can set:

```xml
<bp.unrelease.versions>false</bp.unrelease.versions>
```

!!! warning
    Version history gets lost. This is known and currently there is no workaround for this.
