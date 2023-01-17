# v2.26.3

## Breaking Changes

## Deprecations

## Features:

## Improvements

### Blueprints with versions cannot be pulled on vRA 8.9
Pulling blueprints with multiple versions on vRA 8.9 results in error.

#### Previous Behavior
When pulling a blueprint, the dates of some versions failed the parsing because they have
inconsistent format - `yyyy-MM-dd'T'HH:mm:ss.SSS'Z'` and `yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'`

#### New Behavior
Parsing of the dates for the blueprint versions is now done via Instant class which handles
all cases automatically, without the need to have custom logic and different formats.

#### Relevant Documentation:
**NONE**

### Catalog items that have custom forms cannot be pushed
When you have custom forms for the exported catalog items, when you try to import the vRA content it will fail
with error `Error reading from file: ~.\catalog-items\forms`.

#### Previous Behavior
When you have custom forms for the catalog items, there is a `forms` folder created under the `catalog-item` dir.
The installer tries to read this folder as a catalog item definition files and fails since it is a directory.

#### New Behavior
When importing catalog items with custom forms, the folder `forms` under the `catalog-items` folder is not processed as
a catalog item. The folder is filtered and processed later when importing the actual forms.

#### Relevant Documentation:
**NONE**

## Upgrade procedure:
