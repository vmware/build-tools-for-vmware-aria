[//]: # (VERSION_PLACEHOLDER DO NOT DELETE)
[//]: # (Used when working on a new release.)
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

### *Push / pull to external vRO in VCF 9.x as provider admin fails with HTTP 500*

#### Previous Behavior

When `vro.auth` was `vra` and `vro.username` was a provider admin (`user@System`), BTVA authenticated against VCF Automation with a provider (System) token. External vRO instances in VCF 9.x reject provider tokens, so package operations such as `mvn vrealize:push` failed with `500 Internal Server Error` and an HTML error page.

#### New Behavior

A new optional property, `vro.org.name`, sets the VCF Automation organization the external vRO is bound to. When it is set, the provider token is exchanged for an organization-scoped token. The token exchange calls now go to `vro.authHost` instead of `vro.host`.

#### Relevant Documentation:

- [vRO Connection Parameters](../usage/products/vro/common/connection-parameters.md)

## Upgrade procedure

[//]: # (Explain in details if something needs to be done)
