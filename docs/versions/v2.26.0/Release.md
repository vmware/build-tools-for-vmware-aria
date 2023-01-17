# v2.26.0



## **Breaking Changes**

### *vRBT Package Manager needs to respect what is in the content.yaml and filter the entities from the package based on the configuration*

#### Given

When we push data that is in the package, but not present in the content.yaml for entities like:

* blueprints
* catalog-item
* content-source
* custom-resource
* catalog-entitlement
* property-group
* subscription
#### Previous Behavior

The Package Manager does not take into account what is described in the content.yaml but pushes all that is in the package.

#### New Behavior

The Package Manager takes into account what is described in the content.yaml and pushes only these entries form the content.yaml.

### *vRBT Package Manager does not check if the configuration entity is present on the server*

#### Given

When we export data that is added to the content.yaml, but not present on the server for entities like:

* blueprints
* catalog-item
* content-source
* custom-resource
* catalog-entitlement
* property-group
* subscription
#### Previous Behavior

The Package Manager is skipping it.

#### New Behavior

The Package Manager is throwing an error.




## Deprecations


## Features:





## Improvements







## Upgrade procedure:

## Changelog:
