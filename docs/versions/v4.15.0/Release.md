# v4.15.0

## Breaking Changes


## Deprecations


## Features


## Improvements

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

