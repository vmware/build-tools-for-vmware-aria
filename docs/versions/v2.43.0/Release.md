# v2.43.0

## Breaking Changes


## Deprecations



## Features



### *Added new polyglot runtimes*

The following runtimes have been added to the polyglot archetype:

- `node:18`
- `node:20`

## Improvements

### Update ZipWriter class interface

Added constructor to ZipWriter class interface. Updated input parameter types of ZipWriter functions.


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

