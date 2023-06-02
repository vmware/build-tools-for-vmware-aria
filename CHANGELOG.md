## v2.33.0 - 02 Jun 2023

### Fixes
* [vrotest] IAC-777 / Package Build Failing - typescript folder build error due to forcing of newer version of the terser devDependency

* [maven-plugins] IAC-706 / Package Build Failing - Typescript transpilation failed since Command line is too long

## v2.32.0 - 11 May 2023

### Fixes
* [artifact-manager] 72 / Fixed domain detection to start from the last `@`, rather than the first.

### Enhancements
* [package-installer] IAC-728 / Deprecate ```vro_delete_include_dependencies``` flag  
* [package-installer] IAC-591 / Added backup functionalities for vRO packages that will be imported
* [maven] 87 / npm installation will now throw in case of a failure
* [artifact-manager] IAC-765 / Add Support for Blueprint Custom Form Serialization/Deserialization

## v2.31.2 - 20 Apr 2023

### Enhancements
* [artifact-manager] IAC-758 / Migrate build to JDK 17
### Fixes
* [vRA-NG] IAC-743 / Can't pull resource actions of different resource type with same name
* [artifact-manager] IAC-766 / Fix push fatal error: net.minidev.json.parser.ParseException: Malicious payload ...

## v2.31.1 - 05 Apr 2023

### Fixes
* [package-installer] IAC-732 / Installer Should Ask if vRO is Embedded
* [vrotsc] IAC-755 / *.helper.ts files will now be excluded from type definitions

## v2.31.0 - 29 Mar 2023

### Fixes
* [vrotest] IAC-742 / *.helper.[tj]s files will now be excluded from code coverage reports

### Enhancements
* [artifact-manager] IAC-693 / vRBT to support new content sharing policies.

## v2.30.0 - 10 Mar 2023

### Fixes
* [vropkg] vropkg-not-working-offline / vropkg had a missing dependency that was not bundled
* [artifact-manager] IAC-748 / Change project id between organizations to use restClient instead of config.

### Enhancements
* [polyglot] IAC-604 / vRBT to support downloading powershell modules through 'Ssl3' | 'Tls' | 'Tls11' | 'Tls12' | 'Tls13' .
* [polyglot] IAC-604 / Using Import-Module must always be in the format of Import-Module ```<module name>``` without -Name and ; at the end of the line to avoid confusion .
* [base-package] IAC-746 / Make base packages (ssh project) deployable to artifactory server and local maven repository
* [core] IAC-718 / All pom.xml should inherit a single pom.xml. All pom.xml-s are now children or grand-children of ./pom.xml. Project version is stored only in the 'revision' property.
* [core] IAC-719 / Prepare for maven central. Add needed plugins: maven-javadoc-plugin, maven-gpg-plugin, and maven-source-plugin
* [artifact-manager] IAC-733 / Add an Option for Overwriting Existing vRLI Content Packs.
* [artifact-manager] IAC-741 / mvn vrealize:clean now will not fail if not supported
* [artifact-manager] IAC-745 / VRLI Alerts Fallback Object Set to LogInsight Only During Push

## v2.29.2 - 24 Feb 2023

### Enhancements
* [maven-plugins-vrli] IAC-741 / Use new v2 API when working with vRLI 8.8+ content. The feature is marked as experimental because v2 API is Technical Preview.
## v2.29.1 - 15 Feb 2023

### Fixes
* [package-installer] IAC-713 / Installer doesn't ask for all required information about importing vRА
* [polyglotpkg] IAC-712 / Enabled unit testing phase for npm lib projects, fix abx and polyglot pkg to be backward compatible
* [polyglotpkg] IAC-712 / Updated project dependencies to not trigger build errors when combined with typescript-project-all projects.

## v2.29.0 - 27 Jan 2023

### Fixes
* [artifact-manager] IAC-708 / Updated commons-text version to 1.10.0
* [maven-plugins-vrli] IAC-711 / Pulling LogInsight content packs fails

## v2.28.0 - 23 Jan 2023

### Enhancements
* [artifact-manager] IAC-671 / vRBT to support sending UTF-8 content in regards to vRA-NG projects.
* [artifact-manager] IAC-671 / vRA-NG content (Content Sources, Custom Forms, Catalog Items .. ) now support "." in their names.
* [polyglot] IAC-626 / The Polyglot archetype now works with multiple packages instead of just one.

### Fixes
* [artifact-manager] IAC-553 / Fixed importing of property groups with different project scope.
* [vrotest] IAC-696 / Fixed Error massage while executing vro unit tests
* [release] IAC-682 / Removed unused bamboospec.java file, enable batch mode during release, update pom.xml structure to match the filesystem relative paths, update project dependencies.
* [vRA-NG] IAC-692 / vra-ng:pull throws NullPointerException for missing properties in the vra/content.yaml
* [vrotest] IAC-705 / Add implementation for System.sleep and System.waitUntil
* [MVN] IAC-686 / Add implementation for polyglot packages to be published in JFrog when deploy command is executed.

## v2.27.0 - 15 Dec 2022

### Enhancements
* [build-tools-for-aria] IAC-666 / Update documentation to use approved project name `Build Tools for VMware Aria`
* [build-tools-for-aria] IAC-622 / Added the ability to skip install-node-deps and cleaning node_modules during mvn clean lifecycle step by passing the flag skipInstallNodeDeps. If node_modules folder doesn't exist, then this flag is ineffective. This is done, to make sure that build shouldn't fail, because of this flag in any case.
* [ts-autogen] IAC-618 Removed the ts-autogen archtype, as this is causing the issue to make the toolchain opensource/public
* [documentation] IAC-675/ Added documentation for unit testing
* [artifact-manager] IAC-644 / Add support for the following ABX properties: memoryLimitMb, timeoutSec, provider(Faas provider), shared(Share with all projects in this organization), inputSecrets(Secret), inputConstants(Action constant).

### Fixes
* [installer] IAC-437 / Fixed Installer Skips Steps Under Mac OS
* [installer] IAC-584 / Fixed Interactive Installer Doesn't Ask if vRO is embedded
* [installer] IAC-669 / NPE in case user provides non-existing project name during installation
* [artifact-manager] IAC-663 / vRA NG fails to release existing blueprint version
* [vRA] IAC-633 / Fixed vRA authentication to respect refresh token in configuration

## v2.26.4 - 13 Oct 2022

### Fixes
* [artifact-manager, store] IAC-637 / Fixed VraNgCustomResourceStore when updating Custom Resource after initial deletion failed
* [vrotsc] IAC-606 / Constants are being exported correctly now
* [vro-types] Added distinguishedName property to the AD_Computer and AD_OrganizationalUnit interfaces
* [artifact-manager] IAC-639 / Blueprints cannot be imported when they have "." in thier names
* [custom-resources] Fix Custom Resource Second Day action name validation
* [artifact-manager] IAC-638 / Property groups are now correctly updated
* [artifact-manager] CustomResources now respect if orgName is set instead of orgId

### Enhancements
* [artifact-manager,package-installer] IAC-564 / Add prompt for vrang_proxy when package-installer is run without environment.properties file

## v2.26.3 - 24 Aug 2022

### Fixes
* [artifact-manager] IAC-623 / Fixed pushing of catalog items that had custom forms defined.
* [vRA-NG] IAC-625 / Pulling blueprints with multiple versions on vRA 8.9 results in error.

## v2.26.2 - 16 Aug 2022

### Fixes
* [vRA-NG] IAC-620 vRA-NG push will now release the blueprint if no versions.json is present
* [documentation] IAC-621/Fixed the Release.md template so it's more concise when sed is used to remove the Comments.
* [artifact-manager] IAC-621/ Fixed vRA Custom Resources With Day-2 actions importing in vRA 8.8.2

## v2.26.1 - 29 Jul 2022

### Fixes

* [polyglot] IAC-619 Pushing polyglot projects fails with 'Port is not a number'

### Doc updates

* [package-installer] IAC-428 / Added descriptions for some package installer and maven settings.xml configuration options

## v2.26.0 - 19 Jul 2022


### Enhancements

* [artifact-manager] IAC-613 / Pulling Non Present Content needs to error
* [artifact-manager] IAC-615 / Pushing content to Server to filter only content in content.yaml

## v2.25.3 - 15 Jul 2022

### Fixes

* [installer] IAC-617 / Introduce installer exit code `0` in case of successful import.
* [vro-types] IAC-607 / SQL Plugin Definition Inconsistencies

## v2.25.2 - 11 Jul 2022

### Enhancements
* [artifact-manager] IAC-595 / Improved error message when action name is too long

### Fixes
* [polyglot] IAC-611 / Executing mvn vrealize:push fails for polyglot projects
* [polyglotpkg] IAC-602 / Updated old PowerShell 5 executable to PowerShell 6 and above
* [vrotest] IAC-610 / Code coverage thresholds are now set correctly
* [vrotest] IAC-556 / Tests when dependency has a policy template or other unknown types of elements now work
* [vropkg] IAC-539 / Removed Certificates from vropkg tests
* [vropkg] IAC-609 / Fixed EOL for Windows OS certificate signature
* [vrotest/vro-scripting-api] IAC-598 / Properties.get now returns null instead of undefined in case where the property does not exists ( only in tests, behavior is now the same as the actual Properties object )
* [vrotest/vro-scripting-api] IAC-608 / ConfigurationElement.getAttributeWithKey now returns null instead of undefined in case where the property does not exists ( only in tests, behavior is now the same as the actual ConfigurationElement object )
* [vRA-NG] IAC-587 / Added the diskType to storage profile json files to distinguish First Class Disks and removed diskMode from json when disk is FCD, so it can be pushed via vRBT
* [typescript] IAC-537 / Bumped nodejs version from 12 to 16 when building
* [installer] IAC-601 / Fixed static version inside dependencies of package installer component
## v2.25.1 - 14 Jun 2022

### Fixes
* IAC-592 VROES fails to import actions under src

### Enhancements
* [vropkg] @param now supports parameter with properties
* IAC-464 added startup guide markdown in the main folder

## v2.25.0 - 01 Jun 2022

### Enhancements
* [vRA-NG] IAC-567 / Added the capability to not use versions.json for blueprints via a maven property vrang.bp.ignore.versions
* [vRLI] IAC-588 / Added support for VRLI 8.8 content packs.
* [package-installer] IAC-430 / Enable different types of input parameters for installation workflow (Array/string, number, boolean)
* [polyglotpkg] IAC-582 / Bundle dependency modules for PowerShell Polyglot projects

### Fixes
* [vRA-NG] IAC-561, IAC-498 / When releasing a new version of an existing blueprint that has been released and imported it doesn't fail, rather increments the version.

## v2.24.0 - 20 May 2022

### Enhancements
* [vRA-NG] IAC-552 / Updated storage format for property groups
* [Code Stream] IAC-540 / added new content (git,docker,gerrit). Code imporvement, storage imporvement (yaml in yaml formatting) and unit tests
* [Documentation] IAC-464 / added new startup guide in a markdown file in the main folder
## v2.23.0 - 04 Apr 2022

### Added
* [vRO] IAC-536 / Composite type values
* [Installer] IAC-572 / Removed SSL v 1.3 support from the supported SSL algorithms due to JDK bug: JDK-8221253
* [artifact-manager, vrotsc, installer] IAC-145 / Enabled selection of custom files to transpile based on a git branch.

### Fixes
* [artifact-manager] IAC-534 / Fixed Unable to use refresh token for vRO authentication without username/password.
* [artifact-manager] IAC-488 / Fixed NullPointerException when the name of the storage profile is not defined.
* [artifact-manager] IAC-488 / Fixed NullPointerException by empty json files from vra during pull process.
* [MVN] IAC-566 / Allow property serverId to replace username password for all project types (previously only vra and vro)

## v2.22.2 - 28 Mar 2022

### Fixes
* [artifact-manager] IAC-521 / Fixed NullPointerException when the property "formFormat" don't exists in the custom form.
* [vRA-NG] IAC-533 / Fixed disappearing of custom resource, when ID is provided, during the import in multi-tenant environment. Re-enabled the surefire plugin.

## v2.22.1 - 07 Mar 2022

### Fixes
* [vrotsc] IAC-547 / Fixed export with alias from ES6 spec e.g. export * as foo from ...

## v2.22.0 - 07 Mar 2022

### Fixes
* [vro-types] IAC-546 / Change interface to class, port from vro-type-defs

### Added
* [VRO] IAC-545 / Type definitions for MQTT vRO plugin

## v2.21.0 - 02 Mar 2022

### Enhancements
* [vRA-NG] IAC-532 / Enhance Custom Resource actions imports
* [vrotsc] IAC-530 / Generates JavaScript Source Maps from vRO TypeScript compiler
* [VRO] IAC-493 | IAC-543 / NodeJS-based test framework for vRO
* [VRO] IAC-328 / Remove vRO hint plugin from vRBT
* [cloud-client] IAC-329 / Remove Cloud Client from vRBT

### Fixes
* [Code Stream] IAC-525 / Import pipeline in released state
* [polyglotpkg] IAC-529 / Fixed an issue that required polyglot-cache to be present when building Node.js ABX actions
* [VRO] IAC-487 / Installer removing imported files

## v2.20.0 - 02 Feb 2022

### Enhancements
* [vRA-NG] IAC-512 / Enable extraction of multiple ABX actions and importing them with package-installer
* [vRA] IAC-524 / Improved CR importing mechanism, for push among different vRA instances

### Fixes
* [vRA-NG] IAC-513 / Fixed infinite loop when fetching projects

## v2.19.0 - 21 Jan 2022

### Fixes
* [vrotsc] IAC-510 / vrotsc issue when packaging native content

## v2.18.2 - 12 Jan 2022

### Enhancements
* [VRA] IAC-511 / Made extracted custom forms human readable
* [Code Stream] IAC-509 / fix push of pipelines and custom integrations + logging
  & [vRA-NG] IAC-508 Improved documentation
* [MVN] IAC-507 Updated log4j version to 2.17.1

## v2.18.1 - 17 Dec 2021

### Fixes
* [vRA-NG] IAC-505 / primitive call for projects now returns all entires even if more than 500

## v2.18.0 - 17 Dec 2021

## v2.17.2 - 06 Dec 2021

### Added
* [Code Stream] IAC-236 New project type
## v2.17.1 - 06 Dec 2021

### Added
* [vra-NG] IAC-500 / Added some extra documentation and examples in the archetype
* [vra-NG] IAC-499 / iac-for-vrealize IAC-499
  vro.refresh.token not propagated in maven plugin

## v2.17.0 - 17 Nov 2021

### Enhancements
* [polyglotpkg] IAC-491 / Support building ABX actions with TypeScript project references

## v2.16.2 - 12 Nov 2021

### Fixes
* [vRA-NG] IAC-483 / Exporting subscriptions only takes first 20 - now fetching all available
* [vRA-NG] IAC-484 / Cannot import flavor and image mappings if there are none in the profiles
* [vRA-NG] IAC-482 / SVG icons are no longer uploaded/downloaded

### Known Issues
* [vRA-NG] SVG icons cannot be uploaded since vRA does not recognize it's own format when submitted back

## v2.16.1 - 09 Nov 2021

### Fixes
* [vRA-NG] PropertyGroups project ids are now patched before creating/updating
* [vRA-NG] Fixed missing catalog items in content sources for new blueprints
* [vRA-NG] Blueprints import and export - extended details, versioning and release status support
* [vRA-NG] Importing a flavor mapping, when flavor mapping does not exist
* [vRA-NG] Importing an image mapping, when image mapping does not exist
* [vRA-NG] Catalog Item ids are now fetched before importing

### Enhancements
* [vRA-NG] Improved logging on importing custom resources with active attachments

## v2.16.0 - 02 Nov 2021

### Introduces
* [vRA-NG] Property groups support as vRA package contents

### Fixes
* [vRA-NG] Catalog item icon extension fix
* [vRA-NG] Removed custom forms logic from blueprints storage logic
* [vRA-NG] NullPointerException on importing flavour mappings - enhanced logging

## v2.15.0 - 29 Oct 2021

### Introduces
* [vRO-Polyglot] Move dependency resolution from NPM to Artifactory
* [vRA-NG] Catalog items support with custom forms and icons

### Enhancements
* [vRO] Improve logging when vropkg fails to parse JS

### Fixes
* [vRO] Signing issue due to vRA dependency

## v2.14.18 - 27 Oct 2021

### Introduces
* [vRA-NG] Configuration to wait for data collection during import
* [vRA-NG] Added a new vrang.data.collection.delay.seconds properties to force a wait of
  a variable amount of time for vRA data collection to pass before importing

### Enhancements
* [vRO-Polyglot] "Memory limit" and "Timeout" attributes support for polyglot actions
* [vRA-NG] Default timeout configuration for import content process

### Fixes
* [vRO] Add charset-detector to bundled dependencies
* [vRO] Resource elements support fix for 7.X

## v2.14.17 - 13 Oct 2021

### Introduces
* [vRO-Polyglot] Support for polyglot actions invocation
* [vRO-Polyglot] Extend vrotsc to support referencing polyglot actions in TypeScript workflows
  using decorators

### Enhancements
* [vRA-NG] Timeout configuration for import content process

### Fixes
* [vRA-NG] Resource action custom forms support
* [vRA-NG] Authentication changes for multi-tenant setup
* [vRO] Ignoring node_modules in XML, JavaScript and mixed projects
* [vRO] Fixed a JSON structure logging issue which caused error "Not a valid package file, dunes-meta-inf is missing !"
  when pushing to vRO
* [vRO] Fixed non-latin character support on vRO content import
* [vRA-NG] Subscriptions import support for multi-tenant setup
* [vRA-NG] Entitlements import support for multi-tenant setup
* [vRA-NG] Content sources import support for multi-tenant setup
* [vRA-NG] Custom resource day2 actions support for multi-tenant setup
* [vRA-NG] Support custom resources updates via vrealize:push
* [vRA-NG] Failure of vrealize:push on creating an entitlement without custom from
* [vRO] Empty JavaScript actions cleanup

## v2.14.16 - 08 Sep 2021

## v2.14.15 - 25 Aug 2021

## v2.14.14 - 23 Aug 2021

## v2.14.13 - 03 Aug 2021

## v2.14.12 - 19 Jul 2021

## v2.14.11 - 13 Jul 2021

## v2.14.10 - 09 Jul 2021

## v2.14.9 - 30 Jun 2021

## v2.14.8 - 28 Jun 2021

## v2.14.7 - 25 Jun 2021

## v2.14.6 - 22 Jun 2021

## v2.14.5 - 21 Jun 2021

## v2.14.4 - 17 Jun 2021

## v2.14.3 - 15 Jun 2021

## v2.14.2 - 14 Jun 2021

## v2.14.1 - 28 May 2021

## v2.13.8 - 28 May 2021

## v2.13.7 - 21 May 2021

## v2.13.6 - 20 May 2021

## v2.13.5 - 14 May 2021


* [vRA-NG] Fixed issue when importing subscription in multi-tenant vRA environments
## v2.13.4 - 12 May 2021

## v2.13.3 - 10 May 2021

## v2.13.2 - 05 May 2021

## v2.13.1 - 20 Apr 2021

## v2.13.1 - 20 Apr 2021

## v2.13.1 - 20 Apr 2021

## v2.13.1 - 20 Apr 2021

* [vRO] Changed vRO module dependencies to version 7.3.1
* [vRO] Fixed issue with packaging new configuration element value format introduced in patches of vRO 7.5 and 7.6
## v2.12.7 - 05 Apr 2021

## v2.12.6 - 26 Mar 2021

## v2.12.5 - 16 Mar 2021

## v2.12.4 - 13 Feb 2021

## v2.12.3 - 11 Feb 2021

## v2.12.2 - 21 Jan 2021

### Enhancement
* [MVN] Fixed ussue with installer timeouts

## v2.12.1 - 10 Jan 2021

## v2.12.0 - 08 Jan 2021

### Enhancement
* [TS] vRO pkg - Adds support for slash in workflow path or name
* [vRBT] vRBT installer - updated documentation, added checking of workflow input, writing of workflow error message to
  file, setting of installer exit code when executing of a workflow


## v2.11.1 - 01 Dec 2020

## v2.11.0 - 26 Nov 2020

### Enhancement
* [TS] Allow additional trigger events for policies trigered by the vcd mqtt plugin

## v2.10.2 - 17 Nov 2020

### Enhancement
* [MVN] Fix Missing vRA Tenant After Successful package import
* [MVN] Fix vROPS imoport fails on certain assets

## v2.10.1 - 06 Nov 2020

## v2.10.0 - 02 Nov 2020

### Enhancement
* [MVN] Improve Package-Installer and fix project versions and dependencies

## v2.9.0 - 26 Oct 2020

### Enhancement
* [MVN] Polyglot archetype - regex support in yaml defs for vrli
* [MVN] Regex support in YAML defs for vROPS archetype

## v2.8.8 - 24 Sep 2020

## v2.8.7 - 23 Sep 2020

## v2.8.6 - 02 Sep 2020

### Enhancement
* [MVN] Support SHA1 checksum generation for JS,TS,XML,vRA,vRANG project types.
* [MVN] Include the pom.xml description content as description of the built vRO package artifact
* [TS-AutoGen] Define a <swagger.apiVersion> property for storing the version of the API for which this project is generated.
* [TS-AutoGen] Store the API version as part of the vRO package description.
* [vRA-NG] Adds support for import/export of custom resources and resource actions.

## v2.8.5 - 30 Jul 2020

### Enhancement
* [MVN] Fixed problem with vROPs project build after generating it using vROPs archetype

## v2.8.4 - 23 Jul 2020

## v2.8.3 - 21 Jul 2020

## v2.8.2 - 21 Jul 2020

### Enhancement
* [vRLI] Fixed bug in rewriting of the alert vCops intergation
* [vRO] Added vro configuration validation for SSO authentication when vro tenant is present in settings.xml
* [vRBT] Fixed bug when vRBT fails to find vro port configuration on embedded vro

## v2.8.1 - 02 Jul 2020

## v2.8.0 - 30 Jun 2020

## v2.7.1 - 16 Jun 2020

### Enhancement
* [TS] Typescript projects for vRO, now support syntax for specifying a description for a configuration element attribute.
* [vRA-NG] Adds support for using project name when managing vRA-NG content.
* [MVN] License-management

## v2.7.0 - 04 Jun 2020

### Enhancement
* [vROps] Adds support for vROps 8.1
* [vROps] Change default authentication provider to Token-based Authentication
* [TS] Extend vropkg tool to support Polyglot bundle
* [TS] Support for skipping unmapped dependencies, e.g. --skipUnmappedDeps
* [vRBT] Use prebuild gitlab-runner image from pscoelab repository

## v2.6.1 - 29 May 2020

## v2.6.0 - 21 May 2020

## v2.5.12 - 13 May 2020

### Enhancement
* [TS] Bumped up Typescript version to 3.8.3
* [TS] Added support for tsconfig file override using the project option of the tsc executable.

## v2.5.11 - 13 May 2020

### Enhancement
* [MVN] Updated vRBT infrastructure project with latest dependencies and improved installation robustness

### Fixes
* [vROps] Fixes a problem with resource kind during alert definition import
* [TS] Use fixed node package versions
* [TS] Fixed vCD build process

## v2.5.10 - 07 May 2020

### Enhancement
* [vROps] Support for policy assignment to custom groups

### Fixes
* [vRA] Fixes a problem with vra-ng authentication always setting System Domain and users not being able to authenticate
  with different domain
* [vROps] Removed sshHost from Installer. Use host instead.
## v2.5.9 - 05 May 2020

## v2.5.8 - 30 Apr 2020

## v2.5.7 - 24 Apr 2020

## v2.5.6 - 21 Apr 2020

## v2.5.5 - 15 Apr 2020

## v2.5.4 - 13 Apr 2020

## v2.5.3 - 08 Apr 2020

## v2.5.2 - 07 Apr 2020

### Enhancements
* [TS] Make dependency:go-offline execution conditional

## v2.5.1 - 02 Apr 2020

## v2.5.1 - 02 Apr 2020

## v2.5.0 - 22 Mar 2020

### Enhancements
[TS] All version of Node are supported from 10.x and above
[TS] npm repository is no longer needed
[TS] Support for RequireJS imports/exports
[TS] Support for yaml configurations
[TS] Improved workflows and policy templates transpilation
[TS] Improved diagnostic messages
[TS] Improved handling of cycle references
[TS] Extended workflow support
[TS] Support for ES2017.String
[TS] Emitting a warning header at the top of each file (controllable through vrotsc.emitHeader=true)

## v2.4.20 - 06 Mar 2020

## v2.4.19 - 02 Mar 2020

## v2.4.18 - 21 Feb 2020

## v2.4.17 - 11 Feb 2020

## v2.4.16 - 07 Feb 2020

## v2.4.15 - 20 Jan 2020

### Enhancements
* [JS] Support persistent Actions IDs pulled from server

## v2.4.14 - 16 Jan 2020

### Enhancements
* [MVN] Unify vRO packaging mechanism for TS/XML/JS based projects.

## v2.4.13 - 13 Jan 2020

## v2.4.12 - 09 Jan 2020

## v2.4.11 - 20 Dec 2019

## v2.4.10 - 18 Dec 2019

## v2.4.9 - 16 Dec 2019

## v2.4.8 - 16 Dec 2019

### Enhancements
* [MVN] Add support for generating and using a TS Autogen project based on @vmware-pscoe/ts-swagger-generator NPM package

## v2.4.6 - 16 Dec 2019

### Fixes
* [TS] Error during .ts local imports metadata collection

## v2.4.5 - 11 Dec 2019

### Fixes
* [MVN] Normalize filename for vra:pull-ed catalog item icons

## v2.4.4 - 09 Dec 2019

## v2.4.3 - 02 Dec 2019

### Fixes
* [TS] Fix interface and type declarations imports imposed that do not exist at runtime, which causes errors resolving vrotsc-annotations and in some cases cyrcular dependencies issues.

## v2.4.2 - 28 Nov 2019

### Enhancements
* [MVN] Add support for running workflows against vRO version 7.6 and above

### Fixes
* [MVN] Fix missing vra-ng-package-maven-plugin to repository
* [MVN] Fix failure in TS Tests executed on Windows by increasing default JVM Heap size for background jobs.
* [TS] Fix Tests failing if TS project contains dash in artifactId.


## v2.4.1 - 27 Nov 2019
### Fixes
[MVN] fix installDeps when .m2 repo has not cached the required dependencies. Use dependency:go-offline to ensure they are cached.


## v2.4.0 - 13 Nov 2019

## v2.0.3 - 12 Nov 2019

## v2.0.2 - 12 Nov 2019

### Enhancements
* [TS] Add npmconv utility for converting pure TS based NPM-based projects to vRO project structure.
* [MVN] Add support for vRA 8 user/pass authentication for on-prem instance.
* [MVN] Add support for flavor profiles to exported from vRA 8

## v2.0.1 - 12 Nov 2019

## v2.0.0 - 07 Nov 2019

### Enhancements
* [TS] **BREAKING CHANGE** .d.ts packages are now under different @types/ module name that uses dot notation. For example, when importing you need to use "com.vmware.pscoe.npm.inversify" instead of previously output "com-vmware-pscoe-npm-inversify" module name.

## v1.7.4 - 07 Nov 2019

### Fixes
* [internal] Ensure consistent build and that all vrotsc tests are passing

## v1.7.3 - 06 Nov 2019

### Enhancements
* [TS] Support cyclic dependencies when they are variable ref is in scoped context

## v1.7.2 - 01 Nov 2019

### Fixes
* [TS] Fix .d.ts is not generated if .ts contains only type definitions
* [MVN] Fix support vRO8 auth with short username when on-prem vRA8

## v1.7.1 - 10 Oct 2019

### Fixes
* [TS] Ensure .d.ts parts of src/ folder are visited by underlying tsc program as well.

## v1.7.0 - 09 Oct 2019

### Enhancements
* [TS] Add support for module import
* [TS] typescript 3.6.3 updated with support for EmitHelpers and __spreadArray by default.
* [TS] Migrate to using tsc native class genration and super execution to support default decorators and reflect-metadata

### Fixes
* [TS] Fix Promise.await ignored for async class-based method declarations
* [TS] Fix reference tracking for variable re-exporting with rename
* [TS] rework ESShim.merge implementation and fallback on default __asign EmitHelper provided by tsc.
* [TS] Update archetype tsConfig to limit runtime libs to only those supported by vRO
* [TS] remove custom implementation for .d.ts generation and fallback to tsc instetad to resolve numerous problems with declaration files
* [TS] fix number of issues with inaccessible properties being exported in transpiled code.
* [TS] reduce code optimizaiton efforts and ensure empty .d.ts & .js files are preserved as those might be referenced exernally.
* [TS] Fix support of empty files (e.g. pure-interfacees) on import/export transpiled code.
* [TS] Fix source file traversal and transformation and thus simplifying literal/identifier/comments handling
* [TS] Remove custom super call handling and leave default tsc handling.
* [TS] Remove import optimizations as impacting correctness of imports.

## v1.6.0 - 11 Sep 2019

### Enhancements
* [MVN] vrealize:push goal now supports vCloud Director Angular UI extension projects
* [PI] Package installer now supports vCloud Director Angular UI extension projects
* [MVN] Support for XML workflow representation in TypeScript projects
* [MVN] Support for decorators in TypeScript projects.
* [MVN] Implement support for vCloud Director Angular UI extension projects
* [TS] Support for vRO policies in TypeScript projects. Files ending with .pl.ts will be transpiled as a vRO policy template.
* [TS] Enhanced support for vRO resource elements in TypeScript projects.
* [TS] Enhanced support for vRO configurations and workflows in TypeScript projects.
* [TS] Windows support for TypeScript projects.

## v1.5.11 - 09 May 2019

### Enhancements
* Add ```generated.from``` Maven property to the root POM of all archetypes. This can be used to differentiate which "template" was used to generate the project, for example in the context of a CI pipeline.

## v1.5.10 - 19 Apr 2019

### Enhancements
* Include CHANGELOG.md in the final tool chain bundle.

## v1.5.9 - 19 Apr 2019

### Enhancements
* ```vro:pull```, ```vra:pull``` and ```vra:auth``` Maven goals now support the SSL verification flags to be set as properties in a Maven profile, similarly to the ```vrealize:push``` goal.

### Fixes
* When using the Bundle Installer with a properties file the value of ```vro_delete_old_versions``` used to be ignored - if the property was present, the installer would do the cleanup. Now, if the property is not present it is considered false. If it is present, however, its value will be used to opt-in for the cleanup.

## v1.5.8 - 15 Apr 2019

### Enhancements
* Support for gradual migration from JS-based projects to TypeScript ones by allowing .js files in src/ folder to be respected at TS copilation stage.
* Support for vRO resource elements to be included in package. TS projects can contain any files that are not .ts and .js under src/ directory and those will carried over.
* (internal) Improvement of unit-test parallel execution and sub-suite instantiation for easier debugging/testing purposes.

## v1.5.7 - 03 Apr 2019

### Enhancements
* Installer CLI now prompts for SSL verification flags. Default is still to verify the certificate against Java's key store (i.e. cacerts) and to verify the hostname. Those flags can be persisted and controlled via the environment's ```.properties``` file.
* vrealize:push Maven goal now supports the SSL verification flags to be set as properties in a Maven profile, i.e. you can add ```<vrealize.ssl.ignore.certificate>true</vrealize.ssl.ignore.certificate>``` under ```<properties>``` in an active Maven profile to skip the certificate verification against JAVA's key store (i.e. cacerts). You can also add ```<vrealize.ssl.ignore.hostname>true</vrealize.ssl.ignore.hostname>``` to skip the hostname verification. **WARNING** this is intended for use with production endpoints. For those cases, register vRA/vRO certificate in Java's key store and access the endpoint using its FQDN.

## v1.5.6 - 27 Mar 2019

### Enhancements
* **BREAKING** All certificates are now verified as part of API calls from the toolchain to vRA/vRO:
  * Verify hostname - the hostname if the vRO/vRA server should match the CN of the SSL certificate. *For development environments, this can be skipped by a flag described in documentation.*
  * Verify certificate - the SSL certificate used by vRO/vRA is verified against the Java default keystore, i.e. ```cacerts```. Self-signed or third-party certificates have to either be addded to the trusted store (or their CA) or the check can be ignored for development environments by a flag described in documentation.

* Improved logging when installing packages - logs will report which package will be included (pass) and which will be excluded (skip).
* ```vrealize:push``` will import all packages per type in a single batch, reporting what will be included (pass) and excluded (skip).

### Fixes
* Installer overwrites newer versions of packages found on the server if a concrete source package, e.g. v1.0.2 is not found.
* ```vrealize:push``` downgrades dependent packages, i.e. it will always forcefully install the concrete versions of the dependencies regardless of the state of the target vRO/vRA server. This is still possible if an additianal flag is passed to the command: ```-Dvro.importOldVersions``` and respectively ```-Dvra.importOldVersions```.

## v1.5.5 - 14 Mar 2019

### Fixes
* Pulling a vRO actions project from a Windows workstation leads to wrong identation in actions JavaScript files.

## v1.5.4 - 13 Mar 2019

### Fixes
* Issue related to vRO multi-tenantcy authentication. When toolchain worked with a vRO in a multi-tenant mode it tooked tenant name as domain name instead of the real domain name for login which caused authentication issues for non-default tenants.
* Issue related to the unit-tests executor of vRO actions-based projects on Windows. Inability to run unit-tests on Windows development workstation.
* Regression issue related to import/export of vRA composite blueprints for vRA versions before 7.4, as custom forms API is not supported in these versions.

### Known Issues
* VS Code Extension and Maven plugins cannot work against default tenant (vsphere.local) and custom tenant at the same time, when vRO is configured in a multi-tenant mode. This limitations comes from multi-tenant implementation in vRO where Resource Elements created in the default tenant are read-only for all other tenants.
  This issue could be worked around by not usting the toolchain against default tenant in multi-tenant environment.

## v1.5.3 - 05 Mar 2019

## v1.5.2 - 28 Feb 2019

## v1.5.1 - 11 Feb 2019

### Fixes
* Excessive collection triggering and 100 percent CPU usage for several minutes when VSCode auto-saving is enabled or frequent saves are used
  * Collection will be triggered only if files are created or deleted instead of on each change
  * Collection will be delayed with 10 seconds - that way when pulling many files the multiple change events will trigger only one colelction
* Run Action command now supports vRO 7.3 and lower
* Untitled files and files without IIFE now have correct autocompletion

### Known Issues
* Cannot build project generated with groupId or artifactId that contain special characters.
  * Cause: The Jasmine tests are unable to compile if the folder hierarchy contains characters that are not allowed in Java packages.
  * Workaround: If a generated project contains special characters in its groupId or artifactId, rename all subfolders in test/ and src/ to not include any non-compatible with the Java package convention characters.
* Exporting vRA blueprints without custom forms logs error message "null". This is a bug in underling REST client library.

## v1.5.0 - 04 Feb 2019

### Enhancements
* New command in the vscode extension - **vRO: Run Action**
  * Allows running an action JavaScript file in vRO while seeing the logs in VSCode.
  * Available both in the Command Palette and as `zap` icon on the editor's tab bar.
* Implemented code coverage report produced by running Jasmin unit tests. The report is in lcov.info format, which is readable by Sonar.

### Known Issues
* Cannot build project generated with groupId or artifactId that contain special characters.
  * Cause: The Jasmine tests are unable to compile if the folder hierarchy contains characters that are not allowed in Java packages.
  * Workaround: If a generated project contains special characters in its groupId or artifactId, rename all subfolders in test/ and src/ to not include any non-compatible with the Java package convention characters.
* Exporting vRA blueprints without custom forms logs error message "null". This is a bug in underling REST client library.

## v1.4.1 - 01 Feb 2019

### Enhancements
* New setting to *exclude* certain projects from the list of build tasks (`Cmd+Shift+B`) by using glob patterns
```javascript
"o11n.tasks.exclude" : [
    "com.vmware.pscoe.library*", // Exclude all PSCoE libraries
    "!com.vmware.pscoe.library*", // Exclude everything, except PSCoE libraries
    "com.vmware.pscoe.!(library*)", // Exclude everything PSCoE, except libraries
    "com.vmware.pscoe.library:{nsx,vra,vc}", // Exclude nsx, vra and vc libraries
    "com.vmware.pscoe.library:util" // Exclude util library (<groupId>:<artifactId>)
]
```

### Known Issues
* Cannot build project generated with groupId or artifactId that contain special characters.
  * Cause: The Jasmine tests are unable to compile if the folder hierarchy contains characters that are not allowed in Java packages.
  * Workaround: If a generated project contains special characters in its groupId or artifactId, rename all subfolders in test/ and src/ to not include any non-compatible with the Java package convention characters.
* Exporting vRA blueprints without custom forms logs error message "null". This is a bug in underling REST client library.

## v1.4.0 - 25 Jan 2019

### Enhancements
* Support for [Multi-root Workspaces](https://code.visualstudio.com/docs/editor/multi-root-workspaces) that allow opening more than one vRO project into single vscode window.
* Dynamically create build tasks (`Cmd+Shift+B`) based on project's type and modules.
* New pom.xml diagnostics
  * Show inline warning, if toolchain version in pom.xml file is lower than the vscode extension's version.
  * Provide quick fix action in pom.xml that replaces the parent version with the vscode extension's version.
* Support for export/import of vRA custom forms.
* Support for clean up task of vRA/vRO packages from server.
  * Clean up of the current version and/or old versions and their dependencies.
  * Supported via "mvn" command or package installer

### Known Issues
* Cannot build project generated with groupId or artifactId that contain special characters.
  * Cause: The Jasmine tests are unable to compile if the folder hierarchy contains characters that are not allowed in Java packages.
  * Workaround: If a generated project contains special characters in its groupId or artifactId, rename all subfolders in test/ and src/ to not include any non-compatible with the Java package convention characters.
* Exporting vRA blueprints without custom forms logs error message "null". This is a bug in underling REST client library.

## v1.3.10 - 01 Nov 2018

### Fixes
* The vscode extension cannot load when the project location contains spaces or other characters that are percent-encoded in URIs
* Push does not work for vRA packages built on Windows

### Known Issues
* Cannot build project generated with groupId or artifactId that contain special characters.
  * Cause: The Jasmine tests are unable to compile if the folder hierarchy contains characters that are not allowed in Java packages.
  * Workaround: If a generated project contains special characters in its groupId or artifactId, rename all subfolders in test/ and src/ to not include any non-compatible with the Java package convention characters.

## v1.3.8 - 24 Oct 2018

### Fixes
* The vscode extension cannot generate projects with spaces in the workflows path parameter.

### Known Issues
* Cannot build project generated with groupId or artifactId that contain special characters.
  * Cause: The Jasmine tests are unable to compile if the folder hierarchy contains characters that are not allowed in Java packages.
  * Workaround: If a generated project contains special characters in its groupId or artifactId, rename all subfolders in test/ and src/ to not include any non-compatible with the Java package convention characters.

## v1.3.7 - 19 Oct 2018

### Enhancements
* Autocomplete modules and actions in `Class.load()` statements
* Add a new task command (`vRO: Push Changes`) for pushing only the diff between current branch and origin/master
* Support specifying different command for windows in the vRO task definitions (.vscode/tasks.json)
* New Project wizard will reuse the current VSCode window if no other folder is opened.

### Known Issues
* Cannot build project generated with groupId or artifactId that contain special characters.
  * Cause: The Jasmine tests are unable to compile if the folder hierarchy contains characters that are not allowed in Java packages.
  * Workaround: If a generated project contains special characters in its groupId or artifactId, rename all subfolders in test/ and src/ to not include any non-compatible with the Java package convention characters.

## v1.3.6 - 03 Oct 2018

### Fixes
* New project functionality works only from the context of existing Build Tools for VMware Aria project. Now projects can be created from an empty VSCode window.

### Known Issues
* Cannot build project generated with groupId or artifactId that contain special characters.
  * Cause: The Jasmine tests are unable to compile if the folder hierarchy contains characters that are not allowed in Java packages.
  * Workaround: If a generated project contains special characters in its groupId or artifactId, rename all subfolders in test/ and src/ to not include any non-compatible with the Java package convention characters.

## v1.3.5 - 25 Sep 2018

### Enhancements
* Include package installer in the toolchain to enable -Pbundle-with-installer. When a package is build with ```mvn package -Pbundle-with-installer``` this will produce a zip file with all the dependencies and a bin/ and repo/ folders. The bundle can be installed by unziping it and calling ./bin/installer.
* Archetype generated projects now work with release.sh immediately, i.e. without further modifying the pom.xml file of the root project. You can still have the SCM remote written in the POM and not specify it every time, but OOTB after you add the project to SCM and add your remote (origin) you can use the ```-r``` option of the **release.sh** script: ```sh ./release.sh -r $(git remote get-url origin)```

### Fixes
* vRealize archetype project's install workflow category path contains placeholders

### Known Issues
* Cannot build project generated with groupId or artifactId that contain special characters.
  * Cause: The Jasmine tests are unable to compile if the folder hierarchy contains characters that are not allowed in Java packages.
  * Workaround: If a generated project contains special characters in its groupId or artifactId, rename all subfolders in test/ and src/ to not include any non-compatible with the Java package convention characters.
* New project functionality works only from the context of existing Build Tools for VMware Aria project.

## v1.3.3 - 21 Sep 2018

### Fixes
* vRealize archetype produces a root pom with placeholders

### Known Issues
* Cannot build project generated with groupId or artifactId that contain special characters.
  * Cause: The Jasmine tests are unable to compile if the folder hierarchy contains characters that are not allowed in Java packages.
  * Workaround: If a generated project contains special characters in its groupId or artifactId, rename all subfolders in test/ and src/ to not include any non-compatible with the Java package convention characters.
* New project functionality works only from the context of existing Build Tools for VMware Aria project.

## v1.3.2 - 19 Sep 2018

### Fixes
* Actions with `-SNAPSHOT` in the version cannot be overridden in vRO version 7.3 or lower

### Known Issues
* Cannot build project generated with groupId or artifactId that contain special characters.
  * Cause: The Jasmine tests are unable to compile if the folder hierarchy contains characters that are not allowed in Java packages.
  * Workaround: If a generated project contains special characters in its groupId or artifactId, rename all subfolders in test/ and src/ to not include any non-compatible with the Java package convention characters.

## v1.3.1 - 13 Sep 2018

### Fixes
* The New Project wizard shouldn't ask for Workflows Path when bootstrapping vRA YAML projects
* vRA YAML projects could not be created because of wrong archetype group ID

### Known Issues
* Cannot build project generated with groupId or artifactId that contain special characters.
  * Cause: The Jasmine tests are unable to compile if the folder hierarchy contains characters that are not allowed in Java packages.
  * Workaround: If a generated project contains special characters in its groupId or artifactId, rename all subfolders in test/ and src/ to not include any non-compatible with the Java package convention characters.

## v1.3.0 - 11 Sep 2018

### Enhancements
* `vRO: New Project` command for bootstrapping vRA and vRO projects
* New maven archetype for vRA YAML projects
* Option to edit the profiles in maven's settings.xml file from the Pick Profile dialog (located at the bottom left corner of the status bar)
* Reduced the number of parameters needed for generating a project when using Maven archetype commands
  * **All types of projects**
    * Removed the parameters `-Dtop`, `-Dcompany`, `-Ddepartment`, `-Dtopic`, `-Dname`
    * Added the parameters `-DgroupId` and `-DartifactId`
  * **Projects containing workflows**
    * Removed the parameters `-DbaseCategory`, `-DsubCategory` and `-Dtitle`
    * Added the parameter `-DworkflowsPath`
* Jasmine tests no longer fail with cryptic error when there is an empty/invalid js action file

### Fixes
* `release.sh` cannot release vRA and mixed projects
* Package installer does not support special characters in the password field

### Known Issues
* Cannot build project generated with groupId or artifactId that contain special characters.
  * Cause: The Jasmine tests are unable to compile if the folder hierarchy contains characters that are not allowed in Java packages.
  * Workaround: If a generated project contains special characters in its groupId or artifactId, rename all subfolders in test/ and src/ to not include any non-compatible with the Java package convention characters.

## v1.2.0 - 30 Jul 2018

### Enhancements
* All components in the toolchain are now capable of using vRA SSO as authentication mechanism towards vRO
* New options in the Maven profiles for vRO (located in user's settings.xml)
  * `<vro.auth>vra</vro.auth>` - use vRA SSO authentication
  * `<vro.auth>basic</vro.auth>` - use Basic authentication
  * `<vro.tenant>vsphere.local</vro.tenant>` - specify the tenant to be used for SSO authentication
* There is no longer separate vRO connection configuration for the toolchain Maven plugins and the VSCode extension. All components of the toolchain now use the connection settings defined in the Maven profile at `~/.m2/settings.xml`. The exact profile to be used by the VSCode extension is provided by a new setting `o11n.maven.profile`.
* More build and deploy tasks are available in the `Cmd+Shift+B` palette in VSCode. The actual command behind each of these tasks can be overwritten by a project local `.vscode/tasks.json` file (`Cmd+Shift+B` -> click the cogwheel icon for a task -> change the command in the generated tasks.json)

### Migration Steps
* Since v1.2.0, all mixed projects that have to use vRA SSO authentication, should have the following added to their root **pom.xml** file.
```xml
<parent>
    <groupId>com.vmware.pscoe.o11n</groupId>
    <artifactId>base-package</artifactId>
    <version>1.2.0</version>
</parent>
```
* `o11n.maven.profile` setting is now required by the VSCode extension

### Fixes
* Print a warning when `push` is executed for unsupported artifact types, instead of throwing an exception
* Fix the 'Extension 'vmw-pscoe.o11n-vscode-extension' uses a document selector without scheme.' error visible in vscode when activating the vRO extension

### Removed
* The `o11n.server.*` configuration properties for the VSCode extension are no longer used. They are replaced by the settings defined `o11n.maven.profile`
* Removed Maven-based hint collection

### Known Issues
* Cannot build project generated with groupId or artifactId that contain special characters.
  * Cause: The Jasmine tests are unable to compile if the folder hierarchy contains characters that are not allowed in Java packages.
  * Workaround: If a generated project contains special characters in its groupId or artifactId, rename all subfolders in test/ and src/ to not include any non-compatible with the Java package convention characters.

## v1.1.5 - 19 Jul 2018

### Fixes
* When trying to pull a package from vRO 7.3 with v1.1.4 of the toolchain, it reports the package is not found even though the package is there.

## v1.1.4 - 17 Jul 2018

### Enhancements
* Include empty .o11n directory in the generated vRO projects. Such projects will trigger the vRO extension activation when opened in VSCode
* Provide the ability to fetch contents from any vRO package by using `mvn vro:pull -DpackageName=com.vmware.pscoe.library.ssh`

### Fixes
* Pull does not work for vRO 7.4
* Other minor bug fixes and improvements

## v1.1.3 - 13 Jun 2018

### Enhancements
* Support pulling and pushing configuration values

### Fixes
* Installer cannot import bundle with both vRO and vRA content
* Maven-based hint collection fails for mixed project

## v1.1.2 - 23 May 2018

### Enhancements
* Support component profiles in vRA projects

## v1.1.1 - 14 May 2018

### Enhancements
* Support encrypted passwords in the active maven profile
* Include stack trace information in jasmine test failures

### Fixes
* Minor bug fixes and improvements

## v1.1.0 - 03 May 2018

### Enhancements
* Support pushing content to vRO/vRA without dependencies
* Support for bulding packages (patches) with only a subset of the project's actions.

## v1.0.2 - 18 Apr 2018

### Enhancements
* Support hint collection for dependencies present only on the local machine

### Fixes
* Log4j2 logs an error that configuration is missing when building/testing packages

## v1.0.1 - 13 Mar 2018

### Fixes
* Cloud Client could not import bundles

## v1.0.0 - 02 Mar 2018
* Initial version