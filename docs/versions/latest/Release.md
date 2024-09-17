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

### Fixed bug with exports

#### Previous Behavior

Running a workflow resulted in ERROR (com.vmware.pscoe.library.ecmascript/Module) Error in (Dynamic Script Module name : Module#18) ReferenceError: "exports" is not defined.

#### New Behavior

The bug in Module.ts is fixed and an error is no longer thrown.

### Resolved critical and high level vulnerabilities

Vulnerabilities were identified in artifact-manager using the following trivy command:
```bash
trivy fs --severity HIGH,CRITICAL --exit-code 1 --ignore-unfixed --skip-dirs "**/target/*"  common/artifact-manager
```

#### Previous Behavior

Running the trivy command above revealed the following critical and high level vulnerabilities in artifact-manager:
```log
┌───────────────────────────────────────┬──────────────────┬──────────┬───────────────────┬──────────────────────────────────────────────────────────────┐
│                Library                │  Vulnerability   │ Severity │ Installed Version │                            Title                             │
├───────────────────────────────────────┼──────────────────┼──────────┼───────────────────┼───────────────────────────────-──────────────────────────────┤
│ org.springframework:spring-beans      │ CVE-2022-22965   │ CRITICAL │ 4.3.25.RELEASE    │ spring-framework: RCE via Data Binding on JDK 9+             │
│                                       │                  │          │                   │ https://avd.aquasec.com/nvd/cve-2022-22965                   │
│                                       ├──────────────────┼──────────┤                   ├──────────────────────────────────────────────────────────────┤
│                                       │ CVE-2022-22970   │ HIGH     │                   │ springframework: DoS via data binding to multipartFile or    │
│                                       │                  │          │                   │ servlet part                                                 │
│                                       │                  │          │                   │ https://avd.aquasec.com/nvd/cve-2022-22970                   │
├───────────────────────────────────────┼──────────────────┤          │                   ├──────────────────────────────────────────────────────────────┤
│ org.springframework:spring-context    │ CVE-2022-22968   │          │                   │ Framework: Data Binding Rules Vulnerability                  │
│                                       │                  │          │                   │ https://avd.aquasec.com/nvd/cve-2022-22968                   │
├───────────────────────────────────────┼──────────────────┤          │                   ├──────────────────────────────────────────────────────────────┤
│ org.springframework:spring-expression │ CVE-2023-20863   │          │                   │ springframework: Spring Expression DoS Vulnerability         │
│                                       │                  │          │                   │ https://avd.aquasec.com/nvd/cve-2023-20863                   │
├───────────────────────────────────────┼──────────────────┼──────────┤                   ├──────────────────────────────────────────────────────────────┤
│ org.springframework:spring-web        │ CVE-2016-1000027 │ CRITICAL │                   │ spring: HttpInvokerServiceExporter readRemoteInvocation      │
│                                       │                  │          │                   │ method untrusted java deserialization                        │
│                                       │                  │          │                   │ https://avd.aquasec.com/nvd/cve-2016-1000027                 │
│                                       ├──────────────────┼──────────┤                   ├──────────────────────────────────────────────────────────────┤
│                                       │ CVE-2024-22243   │ HIGH     │                   │ springframework: URL Parsing with Host Validation            │
│                                       │                  │          │                   │ https://avd.aquasec.com/nvd/cve-2024-22243                   │
│                                       ├──────────────────┤          │                   ├──────────────────────────────────────────────────────────────┤
│                                       │ CVE-2024-22259   │          │                   │ springframework: URL Parsing with Host Validation            │
│                                       │                  │          │                   │ https://avd.aquasec.com/nvd/cve-2024-22259                   │
│                                       ├──────────────────┤          │                   ├──────────────────────────────────────────────────────────────┤
│                                       │ CVE-2024-22262   │          │                   │ springframework: URL Parsing with Host Validation            │
│                                       │                  │          │                   │ https://avd.aquasec.com/nvd/cve-2024-22262                   │
├───────────────────────────────────────┼──────────────────┤          ├───────────────────┼──────────────────────────────────────────────────────────────┤
│ org.yaml:snakeyaml                    │ CVE-2022-1471    │          │ 1.27              │ SnakeYaml: Constructor Deserialization Remote Code Execution │
│                                       │                  │          │                   │ https://avd.aquasec.com/nvd/cve-2022-1471                    │
│                                       ├──────────────────┤          │                   ├──────────────────────────────────────────────────────────────┤
│                                       │ CVE-2022-25857   │          │                   │ snakeyaml: Denial of Service due to missing nested depth     │
│                                       │                  │          │                   │ limitation for collections...                                │
│                                       │                  │          │                   │ https://avd.aquasec.com/nvd/cve-2022-25857                   │
└───────────────────────────────────────┴──────────────────┴──────────┴───────────────────┴──────────────────────────────────────────────────────────────┘
```
Some columns hidden to fit.

#### New Behavior

The vulnerabilities were resolved (the command above no longer displays them).
To resolve the vulnerabilities, org.springframework and com.fasterxml.jackson dependencies (of which snakeyaml is a transitive dependency) were updated to the latest released versions. The change required org.apache.httpcomponents to be updated to version 5.3, which affected:
- HttpClient initialization
- usage of HttpStatus
- BasicAuthorizationInterceptor (replaced by BasicAuthenticationInterceptor)
- handling of URISyntaxException and ConfigurationException

## Upgrade procedure

[//]: # (Explain in details if something needs to be done)
