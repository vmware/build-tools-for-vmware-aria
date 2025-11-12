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
### *Force usage of UTF-8 encoding when writing files to local system during `vra-ng` pull*

#### Previous Behavior
The local system encoding is used which sometimes results in malformed data during pull operation. E.g.:
Expected result:
``` {    content: \u0027⚠️\u0027;    float: left;    margin: 5px 10px 5px 5px;    font-size: 1rem;} ```
Actual result:
``` {    content: \u0027??\u0027;    float: left;    margin: 5px 10px 5px 5px;    font-size: 1rem;} ```

#### New Behavior
All `vra-ng` content is pulled correctly regardless of the target system encoding.

### *vRA data collection is now forcefully triggered*

#### Previous Behavior
During `vra-ng` push operation **vrang.data.collection.delay.seconds** property is used to wait a defined period for automatic data collection to happen in VCFA for Orchestrator objects (through Orchestrator integration in VCFA). 

Waiting mechanism is executed only in case **vrang.data.collection.delay.seconds** is defined and it is more than 0 seconds.

#### New Behavior
During `vra-ng` push operation data collection is executed via REST API instead of waiting for given period. If the data collection fails, the old waiting mechanism is triggered.

Data collection is executed only in case **vrang.data.collection.delay.seconds** is defined and it is more than 0 seconds.

## Upgrade procedure

[//]: # (Explain in details if something needs to be done)
