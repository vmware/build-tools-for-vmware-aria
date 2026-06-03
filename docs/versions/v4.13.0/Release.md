# v4.13.0

## Breaking Changes


## Deprecations



## Features

### *Shim for Array.prototype.includes*

Added a shim for `Array.prototype.includes`, which is not natively supported by the current version of Rhino in VMware Aria.

> Include "ES2016.Array.Include" in the "lib" key in the project's tsconfig.json

#### Relevant Documentation

[MDN Array.prototype.includes](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Array/includes)


### *Add `vRealize Developer Tools` as Workspace recommended extension to all archetypes*


## Improvements


### *Fix transpilation of Workflow attributes with default value of type `Array/*`*

In Typescript project if Workflow attribute contains default value with `Array` type Orchestrator expects it to be in a specific format.

```typescript
@Workflow({
    name: "Define attribute",
    path: "Service/Test",
    attributes: {
        security_assignees: {
            type: "Array/LdapUser",
            value: "configurationadmin,System Domain\\admin"
        }
    }
})
```

#### Previous Behavior

The attribute definition from the above Workflow is transpiled into a vRO7 XML format which is no longer supported in newer Orchestrator versions and makes the whole Workflow inaccessible via UI:

```xml
<attrib name="security_assignees" type="Array/LdapUser" read-only="false">
  <value encoded="n"><![CDATA[#{#LdapUser#configurationadmin#;#LdapUser#System Domain\admin#}#"]]></value>
</attrib>
```

#### New Behavior

The attribute definition from the above Workflow is properly transpiled into the expected XML format:

```xml
<attrib name="security_assignees" type="Array/LdapUser" read-only="false">
  <value encoded="n"><![CDATA[[25:string#configurationadmin,26:string#System Domain\admin]]]></value>
</attrib>
```

## Upgrade procedure

