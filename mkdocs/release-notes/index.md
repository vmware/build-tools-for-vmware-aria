[//]: # (VERSION_PLACEHOLDER DO NOT DELETE)
[//]: # (Used when working on a new release.)
[//]: # (Nothing here is optional. If a step must not be performed, it must be said so)
[//]: # (Do not fill the version, it will be done automatically)
[//]: # (Quick Intro to what is the focus of this release)

## Breaking Changes

[//]: # (### *Breaking Change*)
[//]: # (Describe the breaking change AND explain how to resolve it)
[//]: # (You can utilize internal links /e.g. link to the upgrade procedure, link to the improvement|deprecation that introduced this/)

### *vROps content.yaml filtering rules*

The `content.yaml` of `vrops` projects follows the same filtering rules as the other project types, for both `vrops:pull` and `vrops:push` (packaging):

- Empty array (`[]`) - nothing from the object type is processed.
- List of items - only the listed items are processed. If a listed item (without wildcard) is not present on the server (pull) or in the project (push), the build fails.
- Null (no value) - everything from the object type is processed.
- Missing category type - nothing from the object type is processed.

Previously a category type without value was treated as an empty array and the listed items that were not found were silently skipped. To keep the previous behavior, replace the category types without value with `[]` and remove the items that are not present.

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

### *Fix VCFA pull/push failure for provider admin users targeting a specific organization*

#### Previous Behavior

When a provider admin user (e.g. `admin@System`) executed a pull or push for a `vcfa-all-apps` project, the configured `org.name` was not passed to the VCF Automation authentication. As a result the acquired token was scoped to the `System` organization instead of the target organization and the operation failed with an error similar to: `403 Forbidden from GET https://project-service.prelude.svc.cluster.local:8080/rbac-service/api/auth-context`.

#### New Behavior

The `org.name` property is passed to the VCF Automation authentication, so a provider admin user acquires a token for the configured organization, the same way as for `vra-ng` projects on VCF 9.

### *Fix vROps pull and push of dashboards, views, reports, super metrics and metric configurations for VCF Operations 9.x*

#### Previous Behavior

`mvn vrops:pull` and `mvn vrops:push` exported / imported dashboards, views, reports, super metrics and metric configurations by running `ops-cli` over SSH. On VCF Operations 9.x every `ops-cli` command failed with `java.lang.NoClassDefFoundError: org/slf4j/LoggerFactory` (`failed with exit code: 3`), but the failure was only logged, so the build ended with `BUILD SUCCESS` while the content was not imported / exported.

#### New Behavior

On VCF Operations 9.0 and later, dashboards, views, reports, super metrics and metric configurations are exported and imported using the Content Management REST API (`/suite-api/api/content/operations/export` and `/suite-api/api/content/operations/import`), so SSH access is not needed for them. The files in the project keep the same format as the ones exported with `ops-cli`. The result of each operation is verified and the build fails if any content is not exported / imported. As before, the dashboards are exported for the user set in `dashboardUser` and imported for that user, or for all users when `importDashboardsForAllUsers` is `true` (the default).

The JSON files are written with the standard pretty printer and without the fields set by the server that change on every export (`uuid`, `userId`, `lastUpdateUserId`, `creationTime`, `lastUpdateTime`, `importAttempts`, `importComplete` of the dashboards, `modificationTime`, `modifiedBy` of the super metrics and the `id` of the custom groups), so the first pull reformats the existing dashboard, super metric and custom group files once and the next pulls do not produce changes for unchanged content.

VCF Operations completes the import of a dashboard when the dashboard is first opened in the UI by its owner and until then exports it without its widget configurations. `vrops:pull` fails for such dashboards instead of storing them without widget configurations.

Older vROps / Aria Operations versions keep using `ops-cli`, but the build now fails when an `ops-cli` command fails instead of only logging the error.

### *Fix vROps push of existing alert definitions, symptom definitions and recommendations*

#### Previous Behavior

Pushing an alert definition, symptom definition or recommendation that already exists on the target failed with `java.lang.ClassCastException: class java.lang.String cannot be cast to class ...`.

#### New Behavior

The existing definitions are updated.

### *Fix vROps policy priorities and packaging of policies*

#### Previous Behavior

Packaging a project with policies failed with `Unable to copy policiy metadata file 'policiesMetadata.vrops.json'` as the file is no longer created by `vrops:pull`. Pushing policies that are not applied to any object failed with `400 ... DB active policy count doesn't match input policy id count` while setting the policy priorities.

#### New Behavior

The policy metadata file is optional. The policy priorities are set only for the active policies (applied to objects), as required by the vROps API, and the inactive ones are skipped.

### *Fix the localization of vROps reports*

#### Previous Behavior

The localization (title, description) of the reports was not pushed.

#### New Behavior

The localization of the reports is pushed along with the reports.

## Upgrade procedure

[//]: # (Explain in details if something needs to be done)

For `vrops` projects, review the `content.yaml` as described in [vROps content.yaml filtering rules](#vrops-contentyaml-filtering-rules).
