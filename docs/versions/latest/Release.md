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

### *Add restart and resume behaviour configuration to @Workflow decorator*

Workflow decorator now supports passing values for *Server restart behavior* and *Resume workflow from failed behavior*.

```typescript
    @Workflow({
        ...,
        restartMode: 1,
        resumeFromFailedMode: 0
    })
```

The following values are allowed:
- restartMode
  - 0: Do not resume workflow run
  - 1: Resume workflow run (default)
- resumeFromFailedMode
  - 0: System default (default)
  - 1: Enabled
  - 2: Disabled

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

### *Moved Aria Code Stream components to own folder*

This is just an internal restructuring effort, no functionality was changed.

### *Moved Basic archetype components to own folder*

This is just an internal restructuring effort, no functionality was changed.

### *Moved common components to own folder*

This is just an internal restructuring effort, no functionality was changed.

## Upgrade procedure

[//]: # (Explain in details if something needs to be done)
