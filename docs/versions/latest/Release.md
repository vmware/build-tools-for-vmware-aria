[//]: # (VERSION_PLACEHOLDER DO NOT DELETE)
[//]: # (Used when working on a new release. Placed together with the Version.md)
[//]: # (Nothing here is optional. If a step must not be performed, it must be said so)
[//]: # (Do not fill the version, it will be done automatically)
[//]: # (Quick Intro to what is the focus of this release)

## Breaking Changes
[//]: # (### *Breaking Change*)
[//]: # (Describe the breaking change AND explain how to resolve it)
[//]: # (You can utilize internal links /e.g. link to the upgrade procedure, link to the improvement|deprecation that introduced this/)



## Deprecations
[//]: # (### *Deprecation*)
[//]: # (Explain what is deprecated and suggest alternatives)



[//]: # (Features -> New Functionality)
## Features
[//]: # (### *Feature Name*)
[//]: # (Describe the feature)
[//]: # (Optional But higlhy recommended Specify *NONE* if missing)
[//]: # (#### Relevant Documentation:)



[//]: # (Improvements -> Bugfixes/hotfixes or general improvements)

## Improvements

### fix XMLManager definition

XMLManager is a class with static methods

#### Previous Behaviour

XMLManager was defined as an interface

#### New Behaviour

XMLManager is defined as a class with static methods

#### Relevant documentation

https://<vro>/orchestration-ui/#/explorer?section=p&type=o&name=XMLManager&plugin=XML

### fix RESTHost definition

RESTHost is a class that can be initiated (`const host = new RESTHost(name)`)

#### Previous Behaviour

RESTHost was defined as an interface

#### New Behaviour

RESTHost is defined as a class that can be constructed with new RESTHost(name)

#### Relevant documentation

https://<vro>/orchestration-ui/#/explorer?section=p&type=o&name=RESTHost&plugin=REST

### fix HTTPBasicAuthentication definition

#### Previous Behaviour

HTTPBasicAuthentication was defined as an interface

#### New Behaviour

HTTPBasicAuthentication is defined as a class with static methods

#### Relevant documentation

https://<vro>/orchestration-ui/#/explorer?section=p&type=o&name=HTTPBasicAuthentication&plugin=REST

[//]: # (### *Improvement Name* )
[//]: # (Talk ONLY regarding the improvement)
[//]: # (Optional But higlhy recommended)
[//]: # (#### Previous Behavior)
[//]: # (Explain how it used to behave, regarding to the change)
[//]: # (Optional But higlhy recommended)
[//]: # (#### New Behavior)
[//]: # (Explain how it behaves now, regarding to the change)
[//]: # (Optional But higlhy recommended Specify *NONE* if missing)
[//]: # (#### Relevant Documentation:)


## Upgrade procedure:
[//]: # (Explain in details if something needs to be done)

[//]: # (## Changelog:)
[//]: # (Pull request links)
