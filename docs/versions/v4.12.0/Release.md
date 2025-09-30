# v4.12.0

## Breaking Changes


## Deprecations



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



### *Add support for Java 24*
Java 24 is now included in the build matrix of `build` Workflow. Java version is dynamically populated to the `maven-compiler-plugin` based on the build matrix value.

## Improvements


### *Moved Aria Code Stream components to own folder*

This is just an internal restructuring effort, no functionality was changed.

### *Moved Basic archetype components to own folder*

This is just an internal restructuring effort, no functionality was changed.

### *Moved common components to own folder*

This is just an internal restructuring effort, no functionality was changed.

### *Fixed UserInteractionItem decorator options binding

When a function is decorated by @UserInteractionItem, it may receive the following 4 parameters:
security_assignees, security_assignee_groups, security_group, timeout_date.
If passed, they will be bound to the following options respectively:
security.assignees, security.assignee.groups, security.group, timeout.date.


## Upgrade procedure

