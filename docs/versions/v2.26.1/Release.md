# v2.26.1



## **Breaking Changes**

N/A

## Deprecations

N/A

## Features:



N/A

## Improvements

### Fixed pushing standalone polyglot actions

#### Previous Behavior
Pushing standalone polyglot actions that are not added as dependency to vRA/vRO projects fails 
with 'Port is not a number'. The reason is the related vRA/vRO configurations are not loaded
from the profile.

#### New Behavior

Pushing standalone polyglot actions is successful and the package is imported on the vRO server.

### Fixed SQL Plugin Definition Inconsistencies

#### Previous Behavior
The compiler fails on VSCode when we try to use the o11-sql-plugin. The main reason is the definition
of the classes and objects inside the module **'o11-sql-plugin'** are wrong, some objects can not be instantiated
other object the return type is not correct, also some methods are not present in the definition. 
```ts
    let connection = new JDBCConnection(); // Class can not be instantiated
    let preparedStatement = connection.prepareStatement('query to be executed');
    let result = preparedStatement.executeQuery(); // All the method associated with PreparedStatement object returns void
    let metadataResultSet = result.getMetaData();
    let metadata = metadataResultSet.getColumnCount(); // All the method associated with ResultSetMetaData object returns void
```

#### New Behavior
The issues related with the sql-plugin was fixed for vra7.6 and vra8.8.
Now is possible use this classes and objects without compilation issues
in VSCode. 
```ts
    let connection = new JDBCConnection(); // Class can be instantiated
    let preparedStatement = connection.prepareStatement('query to be executed');
    let result = preparedStatement.executeQuery(); // All the method associated with PreparedStatement object returns the expected
    let metadataResultSet = result.getMetaData();
    let metadata = metadataResultSet.getColumnCount(); // All the method associated with ResultSetMetaData object returns the expected
```

### Update list of installer properties

Update and reorder the list of properties in the [use-bundle-installer.md](./doc/markdown/use-bundle-installer.md) file and add comments with descriptions to some of them.






## Upgrade procedure:
N/A

## Changelog:
