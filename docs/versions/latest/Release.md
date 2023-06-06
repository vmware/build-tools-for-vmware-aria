## Breaking Changes

## Deprecations

## Features

## Improvements
### Fixed pulling of vROps dashboards as managed content
#### Previous Behaviour

When pulling a list of vROps dashboards defined in the content.yaml, the automation was failing with a hidden error message.
The underlying cause was that the directory in which the data is exported didn't exist and wasn't created by the automation.

#### New Behaviour

When pulling a list of vROps dashboards defined in the content.yaml, the automation exports the content successfully along with
their metadata files.
### Fix error Command line is too long

#### Previous Behavior

If you build the project with lot of dependencies which exceeds the command line text limitation of 8191 the build will fail with Command line is too long error

#### New Behavior

If you build project with a large number of dependencies, the build will succeed since we are splitting the command in separate commands. Each command can have up to 7000 characters which is below the 8191 limitation.

### fix XMLManager definition

XMLManager is a class with static methods

#### Previous Behaviour XMLManager

XMLManager was defined as an interface

#### New Behaviour XMLManager

XMLManager is defined as a class with static methods

#### Relevant documentation XMLManager

[vro](https://vro/orchestration-ui/#/explorer?section=p&type=o&name=XMLManager&plugin=XML)

### fix RESTHost definition

RESTHost is a class that can be initiated (`const host = new RESTHost(name)`)

#### Previous Behaviour RESTHost

RESTHost was defined as an interface

#### New Behaviour RESTHost

RESTHost is defined as a class that can be constructed with new RESTHost(name)

#### Relevant documentation RESTHost

[vro](https://vro/orchestration-ui/#/explorer?section=p&type=o&name=RESTHost&plugin=REST)

### fix HTTPBasicAuthentication definition

#### Previous Behaviour HTTPBasicAuthentication

HTTPBasicAuthentication was defined as an interface

#### New Behaviour HTTPBasicAuthentication

HTTPBasicAuthentication is defined as a class with static methods

#### Relevant documentation HTTPBasicAuthentication

[vro](https://vro/orchestration-ui/#/explorer?section=p&type=o&name=HTTPBasicAuthentication&plugin=REST)

## Upgrade procedure
