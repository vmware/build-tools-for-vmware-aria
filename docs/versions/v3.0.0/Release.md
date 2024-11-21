# v3.0.0

## Breaking Changes


### *`vrang.project.id` has been removed in favor of `vrang.project.name`*

Moving forward, `vrang.project.id` will not be accepted as part of the configuration. Instead, use `vrang.project.name`.

`project.name` is more flexible as it will automatically find out the `project.id`.

### *`vrang.org.id` has been removed in favor of `vrang.org.name`*

Moving forward, `vrang.org.id` will not be accepted as part of the configuration. Instead, use `vrang.org.name`.

`org.name` is more flexible as it will automatically find out the `org.id`.

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

### *Policy Templates's `templateVersion` is now mandatory*

`@PolicyTemplate` decorators now **must** specify a `templateVersion`.

The possible options are `v1` and `v2` (you can also see this in the definitions).

```ts
@PolicyTemplate({
  name: "Sample Policy",
  path: "MyOrg/MyProject",
  templateVersion: "v2", // This is now mandatory
  variables: { /* ... */ },
  elements: { /* ... */}
})
```

## Deprecations


### *Deprecation of vRA 7 archetype*

The vRA 7 Archetype and all related plugins/mojos/code are removed due to the fact that vRA 7 is Out Of Support.

The suggested alternative is to use version 2.44.0 of the toolchain. That is the last version that supports vRA7

### *Deprecation of vRA 7 types*

The `o11n-plugin-vcac` and `o11n-plugin-vcacafe` types are also removed.

### *Deprecated Regional Content*

The Regional content supported in previous versions of Build Tools for Aria has been removed. Unfortunately that part of the build tools never functioned the way we wanted it to, and managing the "infrastructure" tab in Assembler is no longer something we want to do as it contradicts the principles behind the `vra-ng` archetype.

As an alternative, we suggest you use some sort of install workflow to manage them.

## Features


### *Added a health check script*

To check if the dependencies are met, you can now run:

```sh
curl -o- https://raw.githubusercontent.com/vmware/build-tools-for-vmware-aria/main/health.sh | bash
```

Works for Linux,Mac and Windows with Git Bash



## Improvements

### *Flexible unit tests setup*
It is now possible to configure how the unit tests are being bootstrapped and executed.
#### Previous Behavior
Unit tests were locked to a specific version of Jasmine.
#### New Behavior
There are two out-of-the-box options for test frameworks: Jasmine and Jest.  
The default behavior will be fully backwards compatible. Optionally the user is able to specify which
framework should be used as well as its version or even completely override how the unit tests are being ran.
#### Relevant Documentation
More in-depth documentation is available in the [vrotest](./../../../typescript/vrotest/) component.


## Upgrade procedure

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

### *Migrate PolicyTemplates*

Search your projects that use `@PolicyTemplate` decorator. The `templateVersion` property is now required. Check on top for possible values

### *Migrate away from `vrang.project.id`*

1. Open your `settings.xml`.
2. Search for `vrang.project.id`.
3. If found, replace it with the name of the project as seen in Aria

### *Migrate away from `vrang.org.id`*

1. Open your `settings.xml`.
2. Search for `vrang.org.id`.
3. If found, replace it with the name of the organization as seen in Aria

### *Modify your `environment.properties` files if in use (installer)*

1. If you have `environment.properties` that rely on `vrang_project_id`, they should be modified to use the `vrang_project_name`
2. If you have `environment.properties` that rely on `vrang_org_id`, they should be modified to use the `vrang_org_name`
