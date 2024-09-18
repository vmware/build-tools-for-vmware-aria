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

[//]: # (Improvements -> Bugfixes/hotfixes or general improvements)

### *Added new polyglot runtimes*

The following runtimes have been added to the polyglot archetype:

- `node:18`
- `node:20`

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

### `for each` transformer does not respect comments

We have a transformer in `vropkg` as a way to automatically convert 'Aria Orchestrator' supported `for each` loops to normal `for` loops.

#### Previous Behavior

The transformer did not respect comments and would remove them.

```js
// for each (var i in [1, 2, 3]) { }
/*

for each (var i in [1, 2, 3]) { }

*/

/* for each (var i in [1, 2, 3]) { } */
```

Would all get transformed and the comments would be removed.

#### New Behavior

The transformer now respects comments and will not remove them.

```js
// for each (var i in [1, 2, 3]) { }
/*

for (var i in [1, 2, 3]) { }

*/

/* for (var i in [1, 2, 3]) { } */
```

The comments will be preserved and no transformation will be done.

> Our overall recommendation would be to avoid writing `for each` loops in comments in the first place.

### Fixed bug with exports

#### Previous Behavior

Running a workflow resulted in ERROR (com.vmware.pscoe.library.ecmascript/Module) Error in (Dynamic Script Module name : Module#18) ReferenceError: "exports" is not defined.

#### New Behavior

The bug in Module.ts is fixed and an error is no longer thrown.

## Upgrade procedure

[//]: # (Explain in details if something needs to be done)
