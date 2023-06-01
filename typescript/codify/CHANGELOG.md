# Changelog

All notable changes to Codify will be documented in this file.

## [v1.8.0]

- [Added] - Added support for inspecting vRO packages using `codify inspect` command.
- [Added] - Added support for UPN-based authentication.

## [v1.7.0]

- [Added] - Added project content generation from existing vRO package in a remote vRO server using `codify generate` command.

## [v1.6.3]

- [Fixed] - Fixed wrong `id` notation for ABX actions.
- [Fixed] - Fixed `.gitignore` files not being placed within the generated project.

## [v1.6.2]

- [Added] - Added more intelligent handling of vRO action uploads with conflicting IDs (due to in-vro action movement)
    with suggestions how to resolve those conflics. Look for the red messages in your terminal.
- [Added] - Added `--reauthenticate` option to ignore cached access token and always perform a fresh authentication.
    The new access token is still cached.
- [Fixed] - Removed default value for the `--source` option to avoid accidental bulk codebase upload, resulting in an
    inadvertent content overwrite. Previously, if not specified or if misspelled, the current working directory and all
    its children (without node_modules) was considered to be the source.
- [Fixed] - Fixed long-standing issue with `--sequential` option not working for workflows, resulting in concurrent
    requests for ad-hoc folder creation, only the first succeeded and the rest failing. Now initial project uploads
    should not result in failures caused by improper ad-hoc folder creation for workflows.

## [v1.6.1]

- [Added] - Added additional logs when using cached credentials to allow better troubleshooting authentication issues.
- [Added] - Workflow metadata is now persisted as YAML for improved readability / comparability. This change is
    backward compatible with the previous format for the workflow metadata sa JSON files.

## [v1.6.0]

- [Added] - Added support for package assembly from sources. You can specify the package name and the package
    description along with explicit or impliocit source objects. Assemblying a package will _always_ overwrite
    existing package definitions in the vRO environment. You can assemble a package by using the `codify assemble`
    command. Full syntax: `codify assemble --source <dir> [--source <dir>] --package <name> [--description <desc>]`
- [Added] - Added support for vRO package downloading.
    Select the packages you want to download by providing the `--package <name>` option.
- [Added] - Added support for uploading custom resource actions.

## [v1.5.0]

- [Added] - Added support for authenticating against vRA Cloud using API token. Refer to README for examples.
- [Added] - Added REST client lazy-loading, i.e. a REST client is created only when needed. This fully separates
    vRO from vRA when uploading/downloading content and also allows for vRA-only or vRO-only configurations.
- [Fixed] - Several minor improvements for referential resolution and colorized outputs.

## [v1.4.3]

- [Added] - Added support for using different vRO Auth Host than the vRO Host.
    Use `VRO_AUTH_HOST` environment or `--vroAuthHostname` option.
- [Added] - Added support for specifying provider-specific configuration in ABX using `@abx_configuration`.
- [Fixed] - Incorrect provider value when downloading ABX actions with provider other than `auto`.
- [Fixed] - Missing `.gitignore` in generated projects using `codify create`.

## [v1.4.2]

- [Added] - Version field to BOM output
- [Fixed] - Workflow presentation download and upload

## [v1.4.1]

- [Fixed] - Bug with improper response handling for Axios errors

## [v1.4.0]

- [Added] - Refresh token for vRA and vRO authentication
- [Added] - Solution BOM in tabular format: `codify bom [--source=<directory>]`
- [Added] - Contribution and tutorial documentation
- [Fixed] - Upload sequence for dependent objects using static object order
- [Fixed] - Better REST error handling and error output

## [v1.3.1]

- [Added] - `--sequential` option to disable parallel operations, avoiding possible race conditions.
- [Added] - Refrence to new vro types package (`@types/vro-types`) in generated projects
- [Fixed] - Missing global vRO objects in `.eslintrc` of generated projects

## [v1.3.0]

- [Added] - Support for downloading and uploading single-script NodeJS-based ABX actions
- [Added] - Support for downloading and uploading single-script Python-based ABX actions
- [Added] - Support for downloading and uploading single-script PowerShell-based ABX actions

## [v1.2.2]

- [Added] - Spport for ABX building to allow single action building(`--action=<action-name>`) and skipping public dependencies (`--nodeps`) for all ABX runtimes.

## [v1.2.1]

- [Fixed] - Problem with file extension when downloading Polyglot-based actions.

## [v1.2.0]

- [Fixed] - Context request in NodeJS, Python and PowerShell ABX action runners not matching actual function signature. Missing `privileged` flag in the arguments.
- [Added] - Support for downloading and uploading vRO PowerShell-based Polyglot actions.
- [Added] - Support for downloading and uploading vRO Python-based Polyglot actions.

## [v1.1.3]

- [Fixed] - Launch configuration for NodeJS ABX projects.
- [Fixed] - Missing dependecy for Python ABX projects.

## [v1.1.2]

- [Added] - Action runner for PowerShell-based ABX multi-action projects.
- [Added] - Build automation for PowerShell-based ABX multi-action projects.
- [Added] - Generator for PowerShell-based ABX multi-action projects using `codify create --abx`.

## [v1.1.1]

- [Added] - Unit testing and test coverage for Python-based ABX multi-action projects.
- [Added] - Action runner for Python-based ABX multi-action projects.
- [Added] - Build automation for Python-based ABX multi-action projects.
- [Added] - Generator for Python-based ABX multi-action projects using `codify create --abx`.
- [Added] - Unit testing and test coverage for NodeJS-based ABX multi-action projects.
- [Added] - Action runner for NodeJS-based ABX multi-action projects.
- [Added] - Build automation for NodeJS-based ABX multi-action projects.
- [Added] - Generator for NodeJS-based ABX multi-action projects using `codify create --abx`.

## [v1.1.0]

- [Added] - Support for downloading and uploading vRA resource actions.
- [Added] - Support for downloading and uploading vRA subscriptions.
- [Added] - Support for downloading and uploading vRA cloud templates.
- [Added] - vRA Authentication using local credentials caching.

## [v1.0.1]

- [Added] - Automatic dependency installation for generated Codify projects.

## [v1.0.0] - Initial Release

- [Added] - Codify project generation using `codify create`
- [Added] - Support for vRO action remote invocation.
- [Added] - Support for downloading and uploading vRO resource elements.
- [Added] - Support for downloading and uploading vRO config elements.
- [Added] - Support for downloading and uploading vRO workflows.
- [Added] - Support for downloading and uploading vRO JS-based actions.
- [Added] - Support for downloading and uploading vRO NodeJS-based Polyglot actions.
- [Added] - Example content in the project repository.
- [Added] - vRO Authentication using local credentials caching.
- [Added] - CLI interface.
