# v2.26.2

## **Breaking Changes**
**NONE**



## Deprecations
**NONE**



## Features:
**NONE**



## Improvements

### Blueprint is released without versions file
When performing `vrealize:push` without _versions.json_ the blueprint is now versioned and released to the catalog.

#### Previous Behavior
If a blueprint is pushed without versions file existing it is not released to the catalog.

#### New Behavior
When pushing a new blueprint without versions file it is never released to the catalog.

#### Relevant Documentation:
**NONE**

### *Custom Resource Day-2 Actions Failing on vRA 8.8.2* 
When importing a Custom Resource with a Day-2 Action defined the custom resource would fail to be imported ( in vra 8.8.2 ).

#### Previous Behavior
The Importing of the CR would fail due to us deleting the `tenant` from the `formDefinition` of the `additionalActions`.
This behavior was ok in previous versions, but not in later ones ( 8.8.2 ).

#### New Behavior
The `tenant` is no longer being deleted, but is rather updated with the `orgId` from the configuration.

#### Relevant Documentation:
**NONE**



## Upgrade procedure:
**NONE**
