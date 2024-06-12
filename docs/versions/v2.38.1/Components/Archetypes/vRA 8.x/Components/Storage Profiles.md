# Storage Profiles
They are items that can be used in the Blueprints/Cloud Templates to use different types of storage options.

## Overview
**Finish**

## Table Of Contents:
1. [Structure](#structure) - how are storage profiles exported and what is in the file?
2. Operations
   1. [Importing](#importing) storage profiles to vRA
3. [Known Issues](#known-issues)

### Structure
Below is an example structure of a storage-profile export.

Example `content.yaml`
```yaml
storage-profile:
  - Example Storage Profile
# ...
```

Structure
```
src/
├─ main/
│  ├─ resources/
│  │  ├─ regions/
│  │  │  ├─ example region name/
│  │  │  │  ├─ storage-profiles/
│  │  │  │  │  ├─ Example Storage Profile.json
```

`Example Storage Profile.json` contains disk metadata information.

### Importing
When importing a storage profile, it is matched by its `name`. If there is a storage profile with the same name on the server,
an update will be performed. Otherwise, the storage profile will be created instead. Note that a Storage Profile has to contain the proper tags in the metadata json that also exist under the `region-mapping` section in `content.yaml` in order to do import and export.


### Known Issues
