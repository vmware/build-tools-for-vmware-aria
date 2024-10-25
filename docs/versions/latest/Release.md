[//]: # (VERSION_PLACEHOLDER DO NOT DELETE)
[//]: # (Used when working on a new release. Placed together with the Version.md)
[//]: # (Nothing here is optional. If a step must not be performed, it must be said so)
[//]: # (Do not fill the version, it will be done automatically)
[//]: # (Quick Intro to what is the focus of this release)

## Breaking Changes

[//]: # (### *Breaking Change*)
[//]: # (Describe the breaking change AND explain how to resolve it)
[//]: # (You can utilize internal links /e.g. link to the upgrade procedure, link to the improvement|deprecation that introduced this/)

### *Polyglot projects will not try to fix mistakes due to issues with the manifest*

Before, the `polyglot.json` could be defined like this:

```json5
{
  "platform": {
    "runtime": "nodejs", // Here, this makes sense only for `ABX` projects, but not `vro` ones, which this is, looking 5 rows down
    "action": "auto",  
    "tags": [],
    "entrypoint": "out/handler.handler"
  },
  "vro": {
    "module": "com.vmware.pscoe.templates.buildtoolsforvmwareariasamples",
    "inputs": {
      "limit": "number",
      "vraEndpoint": "CompositeType(host:string,base:string):VraEndpointType"
    },
    "outputType": "Array/string"
  },
  "files": ["%out", "!**/package.json", "!**/polyglot.json", "!**/tsconfig.json"]
}
```
That would result the build tools to compile this correctly and default to using `node:12`.

Now, however, you have to define the `runtime` correctly as `node:12` if you want this to work.

This applies for automatic fixes for `vro` and `abx` types.

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

## Upgrade procedure

[//]: # (Explain in details if something needs to be done)

### *Polyglot projects using `nodejs` as a runtime for a `vro` project need to be migrated*

1. Look for all of your polyglot projects made for `vro`.
2. Look for `nodejs` runtimes and change them to `node:12`.
3. Look for `powershell` runtimes and change them to `powercli:11-powershell-6.2`
4. Look for `python` runtimes and change them to `python:3.7`

Note:

You don't need to migrate projects if they are `abx` based. `nodejs` is the correct value for `abx` projects.

### *Polyglot projects using vro runtimes for abx projects need to be migrated*

1. Take a look at the documentation for the available runtimes
2. Correctly set the `vro` runtime you want to use instead of the `abx`
3. `nodejs`, `powershell` or `python` only
