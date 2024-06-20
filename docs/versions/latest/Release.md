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

### Support of ordering of Aria Operations (vROPs) policies by priority

Aria Operations (vROPs) since version 8.17.0 supports ordering policies by priority. If the target server has Aria Operations version 8.17 and later the policies will be ordered by priority during pushing of policies to the server as they appear in the content.yaml file with the first policy with highest priority and the last one with lowest priority, hence making possible to control the priority of the policies in the configuration file. If the Aria Operation server version is older no ordering by priority will be done during pushing of policies (in order to maintain backwards compatibility).
Note that the default policy will not be part of priority order list due to limitation of Aria Operations product.

Example content.yaml file with policies ordered by priority.

```yaml
policy:
- "Policy 1"
- "Policy 2"
- "Policy 3"
```

If the Aria Operations server is 8.17.0 and later during push the policies will be ordered by priority as follows:

1. "Policy 1" (top priority)
2. "Policy 2"
3. "Policy 3" (lowest priority)

### Support of project scope / organization during import of content sharing policies

[//]: # (Improvements -> Bugfixes/hotfixes or general improvements)

### VROTSC Upgrade the ts version from 3.8.3 to 5.4.5

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

## Upgrade procedure

[//]: # (Explain in details if something needs to be done)
