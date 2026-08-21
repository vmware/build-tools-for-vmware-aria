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

### *Fix issue with connection to external VRO for VCF 9*

So far, it wasn't possible to connect to external VRO using BTVA, because it always used external VRO host as authentication host. Now BTVA autodetects if this is external VRO, and in case it is, it replaces host with authhost, just for authentication.

### *Fix issue with asking for VRA authentication parameters twice, when embedded VRO is used for VCFA host*

This bug was observed only in interactive mode for the installer - it asked twice for same authentication parameters when both VCFA and embedded VRO are used.

### *Removed CSP host ask in interactive mode for VRA/VCFA packages*

For both VRA and VCFA packages, installer with stop asking for CSP (authentication host, a legacy coming from cloud VRA). Instead, CSP will be always same as VRA/VCFA host.

### *Removed Import mode ask in interactive mode for VRO packages*

For VRO packages, installer with stop asking for Import mode (a legacy coming from VRO 7). Instead, this value will be always set to SKIP.

## Upgrade procedure

[//]: # (Explain in details if something needs to be done)
