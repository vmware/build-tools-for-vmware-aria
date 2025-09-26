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

### *Add `vRealize Developer Tools` as Workspace recommended extension to all archetypes*

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

[//]: # (Explain in details if something needs to be done)
