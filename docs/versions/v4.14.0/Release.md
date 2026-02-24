# v4.14.0

## Breaking Changes


## Deprecations



## Features



### *Support import vrops dashboards for specific user*
Added a new parameter to vrops configuration in settings.xml:
- vrops.importDashboardsForAllUsers=true(default)|false
If parameter is missing or set to *true*, the dashboards are imported to all users (the current behaviour)
If parameter is set to *false*, the dashboards are imported only for the user specified in vrops.dashboardUser
Changes in package installer parameters:
- added vrops_importDashboardsForAllUsers
- changed default for vrops_restAuthProvider to *AUTH_N*

## Improvements


## Upgrade procedure

