# Usage

## Overview
The `{{ extra.general.bta_name }}` ecosystems consists of archetypes, Maven goals, and helper tools that cover every product combination. This page keeps the most common operations, product-specific notes, packaging options, licensing helpers, and IDE guidance in one place.

## Operational quick reference
### Command cheat sheet
```bash
mvn clean package
mvn clean package -Pbundle-with-installer
mvn clean package vrealize:push -P{profile}
mvn vrealize:clean -DincludeDependencies=true -DcleanUpOldVersions=true -DcleanUpLastVersion=false -Ddryrun=true -P{profile}
```

### Environment tweaks
- `-DincludeDependencies=false` keeps the push focused on your artifacts.
- `-Dfiles=Name1,Name2` lets you refresh only the changed JavaScript/TypeScript sets.
- `-Dvrealize.ssl.ignore.certificate` / `-Dvrealize.ssl.ignore.hostname` are safe for dev labs but never used in production.
- Use `-X` to diagnose Maven failures and `-U` to force dependency downloads when caches drift.

!!! caution
    The clean/push/pull commands always respect whatever profile you pass via `-P`. Keep credentials encrypted and use `settings-security.xml` for CI.

## Product pathways
=== "Orchestrator (JS/XML/TS)"

    #### Archetypes & structure
    Actions, TypeScript, and XML archetypes emit a `pom.xml`, `release.sh`, and the `src` tree that mirrors the vRO inventory. All Orchestrator archetypes target every supported vRO release (on-prem or embedded) and expose the `vro` Maven goals you will reuse every day.

    #### Tests & ignore rules
    JavaScript actions expect Jasmine tests under `src/test`. TypeScript tests follow the `*.test.ts` / `*.spec.ts` naming (see `docs/vrotest/readme.md` and `docs/versions/latest/Components/Archetypes/typescript/General/Testing/Getting Started.md`). Tests live in the same package tree so the Maven lifecycle picks them up via `mvn clean test` or `mvn clean package`.

    #### .vroignore
    The default `.vroignore` contains packaging, compilation, and test helpers that you can tweak via the `vroIgnoreFile` property. The default file content is:

    {% include "snippets/vroignore-default.md" %}

    #### Operations
    - Pull from a live environment with `mvn vro:pull -P{profile}`.
    - Push with `mvn clean package vrealize:push -P{profile}`.
    - Bundles ignore `content.yaml`, so keep the descriptor updated only to drive pulls.

=== "{{ extra.products.vra_8_short_name }} / VCF Automation"

    #### Archetype & descriptor
    `package-vra-ng-archetype` creates `content.yaml`, `src/main/blueprints`, and catalog artifacts for `{{ extra.products.vra_8_short_name }}` (Aria Automation 8.x) and VCF Automation (Classic). The descriptor drives imports and the IDE representation.

    #### Connections
    Provide the usual `vrang.host`, `vrang.port`, `vrang.username`, `vrang.password`, `vrang.tenant`, `vrang.org.name`, `vrang.project.name`, and optional `vrang.vro.integration`. The importer also honors:
    - `vrang.bp.unrelease.versions`
    - `vrang.import.timeout` (milliseconds for catalog sync)
    - `vrang.data.collection.delay.seconds` when dynamic types/extensions require more time.

    <br>
    !!! note
        Legacy transcripts remind us that catalog entitlements were used in Aria Automation 8.0-8.8.1 and replaced by content sharing policies in 8.8.2 (see `docs/ppt_transcript.md`).

    #### Operations
    - Fetch server state with `mvn vra-ng:pull -P{profile}`.
    - Deploy via `mvn clean package vrealize:push -P{profile}`.

=== "Aria Operations & VCF Operations"

    #### Archetype & descriptor
    `package-vrops-archetype` produces a `content.yaml`, `src/main/resources`, and `content.yaml` entries for dashboards, alerts, policies, and more. Import targets are `.vrops` packages that vROps or VCF Operations understands.

    #### Profiles
    Provide `vrops.host`, `vrops.port`, and both REST (`vrops.restUser`, `vrops.restPassword`) and SSH (`vrops.username`, `vrops.password`) credentials. Additional knobs:
    - `vrops.dashboardUser` / `vrops.importDashboardsForAllUsers`
    - `vrops.restAuthSource` / `vrops.restAuthProvider` (AUTH_N or BASIC)

    #### Operations
    - Use `mvn vrops:pull -P{profile}` (wildcards are supported except for dashboards and metric-configs).
    - Push via `mvn clean package vrops:push -P{profile}`.

=== "Operations for Logs"

    #### Archetype & descriptor
    The `package-vrli-archetype` structures alerts, content packs, and subscriptions into `content.yaml` plus `src/main/resources`.

    #### Profiles
    Connect with `vrli.host`, `vrli.port`, `vrli.username`, `vrli.password`, and the desired `vrli.provider` (Local, Active Directory, VIDM). When the project is part of a VCF deployment also provide `vrli.vrops*` credentials.

    #### Operations
    - Capture the environment with `mvn vrli:pull -P{profile}` (wildcards are supported in the descriptor).
    - Deploy by running `mvn clean package vrealize:push -P{profile}`.

=== "Polyglot / ABX"

    #### Archetype & descriptor
    The `package-polyglot-archetype` uses `-Dtype=vro` for Orchestrator polyglot actions or `-Dtype=abx` for ABX modules. The resulting project mirrors the selected runtime and reuses the standard `src` layout.

    #### Operations
    - Push artifacts with `mvn clean package vrealize:push -P{profile}`.

## Multi-module & packaging
- `ts-vra-ng` is tailored for combined TypeScript + vRA NG solutions (`maven/archetypes/ts-vra-ng/create.sh`).
- The `mixed` archetype (`maven/archetypes/mixed/create.sh`) lets you graft in custom workflows, licenses, and workflow paths without rewriting every command.
- The `bsc-package-maven-plugin` (see `docs/versions/latest/General/Cheatsheets/bsc-plugin-arguments.md`) packages arbitrary content into a ZIP that you can deploy via SSH. Configure `vroIgnoreFile` when you need a nonstandard ignore list (`.vroignore` is the default).

## Bundling & distribution
Create installation-ready bundles with Maven and use the bundled CLI to push them to any target environment.

```bash
mvn clean package -Pbundle-with-installer
./bin/installer
./bin/installer ./bin/environment.properties
```

Repeatable deployments reuse the trimmed `environment.properties` sample shown on the Getting Started page, which contains just the SSL flags and the common vRO settings. The bundle installer will prompt for workflow inputs unless you keep `vro_run_workflow=false` in that file.

{% include \"snippets/installer-environment.md\" %}

## Licensing plugin
- Add `licenseUrl`, `licenseHeader`, or `licenseTechnicalPreview` when you generate a project to preseed the Maven license plugin.
- Maven will populate `license_data/licenses.properties` and inject headers while running `mvn install`.
- Generate third-party notices with `mvn license:add-third-party -Dlicense.useMissingFile`.

## IDE & developer tools
- The vRealize Developer Tools (vRDT) VS Code extension exposes environment profiles, IntelliSense for scriptable objects, Run Action / inventory views, and build tasks for push/pull operations (see `docs/Using-the-VS-Code-Extension.md`).
- The `vrdev.tasks.exclude` setting lets you hide library projects from the task list when you only push a subset of artifacts.
- Inventory caching is available but must be disabled or refreshed when you update packages on the server.

!!! note
    The legacy transcript warns that the extension's `vRealize: New Project` command is limited to a handful of archetypes, so prefer `mvn archetype:generate` for full control (`docs/ppt_transcript.md`).
