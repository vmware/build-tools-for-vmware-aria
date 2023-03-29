# v2.31.0

## Breaking Changes



## Deprecations



## Features



### Implemented Push/Pull logic for new Content Sharing Policy from vRA 8.8.2

Content Sharing Policies can now be fetched for newer versions of vRA.

It can be done by adding:

```yaml
# ...
policy:
  content-sharing:
```

to your `content.yaml` file


## Improvements

### *Ability to cover cases when project.id is not set, but project.name is set when pushing Custom Resources*

#### Previous Behavior
Project id was being fetched from the configuration, which means in cases where the project id was not set, but project name was, there would be an error.

#### New Behavior
The project id is now fetched from the restClient, which checks if we have project id or name set and tries to find the correct id if the name is the only thing that is set

#### Relevant Documentation:
None

### *.helper.[tj]s files will now be excluded from code coverage reports*
Helper files are meant to contain fixture or other helper methods and they are not supposed to be part of the code coverage report.

#### Previous Behavior
The `.nycrc` configuration that controls the code coverage did not contain an exclude for the helpers

#### New Behavior
`"**/*.helper.[tj]s"` was added to the configuration to facilitate exclusion of helpers

#### Relevant Documentation:
* None



## Upgrade procedure:

