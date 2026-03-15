# Getting Started

## Overview
The `{{ extra.general.bta_name }}` workflow begins with a clean workstation, a configured Maven profile, and a sample project that can be built and pushed in a few commands. This page captures the most common steps that need to happen before you dive into a product-specific archetype.

## Platform foundation
### Prerequisites
- Build Tools for VMware Aria Platform and a dedicated [Workstation guide](docs/archive/doc/markdown/setup-workstation.md) (Java 17, Maven 3.9+, Node.js 22.x and the companion system setup described in `setup-platform.md`).
- Live vRealize Automation and vRealize Orchestrator tenants with administrator users, reachable on their standard ports (443, 8281/8283). The Workstation must resolve both hosts and have network access to them.
- PowerShell 7+ when working with the polyglot or PowerShell-based archetypes.

### Workstation checklist
- Confirm `JAVA_HOME`, `MVN`, and `NODE` are on your `PATH`.
- Keep a copy of `settings.xml` under source control with placeholders and store the real credentials in a secure profile per CI/Dev environment.
- Mind the `vro.serverId` and `vra.serverId` values - those tell the build which Artifactory credentials to reuse.

## Configure Maven profiles
Every project uses a Maven profile that encodes the target environment. Add a profile like the one below to `~/.m2/settings.xml`, then call it with `-P{profile}` when you build/push.

```xml
<profile>
  <id>dev</id>
  <properties>
    <vro.host>vra-l-01a.corp.local</vro.host>
    <vro.port>443</vro.port>
    <vro.username>configurationadmin@vsphere.local</vro.username>
    <vro.password>...</vro.password>
    <vro.auth>basic</vro.auth>
    <vra.host>vra-l-01a.corp.local</vra.host>
    <vra.port>443</vra.port>
    <vra.username>administrator@vsphere.local</vra.username>
    <vra.password>...</vra.password>
  </properties>
</profile>
```

### Key settings to keep in the profile
- `vro.serverId` / `vra.serverId` point to servers defined inside `~/.m2/settings.xml` so you can store encrypted credentials in `servers`.
- `vrang.*`, `vrops.*`, and `vrli.*` properties tie into the specific archetype you are building - copy the relevant block from the product docs once the connection is ready.
- Use `vro.authHost` / `vro.authPort` when an external Orchestrator uses vRA credentials.

!!! note "Protect credentials"
    Never check the profile with live passwords into source control. Prefer `settings-security.xml` or encrypted servers for CI jobs.

## Generate a project template
All archetypes are generated via `mvn archetype:generate`. Start with a command such as:

```bash
mvn archetype:generate \
  -DinteractiveMode=false \
  -DarchetypeGroupId=com.vmware.pscoe.o11n.archetypes \
  -DarchetypeArtifactId=package-typescript-archetype \
  -DarchetypeVersion={{ extra.iac.latest_release }} \
  -DgroupId=com.example \
  -DartifactId=my-first-project
```

- Use `package-actions-archetype`, `package-xml-archetype`, `package-typescript-archetype`, `package-vra-ng-archetype`, `package-vrops-archetype`, or `package-vrli-archetype` depending on the product.
- Run `maven/archetypes/ts-vra-ng/create.sh` when you need a TypeScript + vRA NG multi-module starter, and copy/paste the `maven/archetypes/mixed/create.sh` script whenever a custom mixed project is required. Always append `-DarchetypeVersion={{ extra.iac.latest_release }}` to pin the toolchain release.

## Build a first payload
Edit the generated `src` tree, update `content.yaml` (if the archetype exposes one), and run `mvn clean package` to produce packages and verify the local build. That command also compiles unit tests (see the Usage page for testing details) and validates that your dependencies are available in Artifactory.

## Upload content - Push
Deploy the local code with the standard push goal:

```bash
mvn clean package vrealize:push -P{profile}
```

By default the goal uploads every dependency. To push only your project use `-DincludeDependencies=false`. You can also filter the files that trigger a refresh with `-Dfiles=Name1,Name2`, and for development environments add `-Dvrealize.ssl.ignore.certificate` / `-Dvrealize.ssl.ignore.hostname` while you sort out TLS. Keep in mind `vrealize:push` always sends the content under `./src`; it ignores any `content.yaml`.

## Download content - Pull
To capture live assets back into a workspace you rely on the product-specific pull goals.

| Target | Goal | Notes |
|---|---|---|
| Orchestrator | `mvn vro:pull -P{profile}` | Downloads the package tied to the current project. |
| `{{ extra.products.vra_8_short_name }}` & VCF Automation | `mvn vra-ng:pull -P{profile}` | Pulls the assets defined in `content.yaml`. |
| Aria/VCF Operations | `mvn vrops:pull -P{profile}` | Supports wildcards (dashboards/metric configs excluded). Fails if `content.yaml` is empty. |
| Operations for Logs | `mvn vrli:pull -P{profile}` | Wildcards are supported; fails when the descriptor is missing content. |

Each goal evaluates the archetype-specific descriptor and overwrites the local `./src` tree. Re-run with `-X` for debugging and `-U` to force fresh dependencies when Maven caches stale artifacts.

## Package with Installer
Build the bundle that ships with the installer CLI:

```bash
mvn clean package -Pbundle-with-installer
```

The resulting `*-bundle.zip` contains a `bin/installer` script. Run it interactively, answer the prompts, and save the generated `environment.properties` for reuse. Avoid re-requesting the workflow run parameters by keeping `vro_run_workflow=false` in that file and reusing the credentials to skip manual entry.

{% include "snippets/installer-environment.md" %}

Re-run the installer with the saved answers:

```bash
./bin/installer ./bin/environment.properties
```

## Distribute
Copy the bundle to other environments and re-run `./bin/installer` with the shared properties file. The CLI automatically reuses the connection settings you captured and can import the same package set to development, staging, and production with a single command sequence.
