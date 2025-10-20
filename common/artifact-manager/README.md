## About
vRealize Artifact Manager is a java library providing set of class for easy management of vRealize Suite content (vRealize Automation content bundles, vRealize Orchestrator packages, vRealize Operations Manager content bundles.

### What is vRealize Automation bundle?
vRA bundle is a filesystem representation of an vRA Content Service Package. Such package can be created using vRA API and populated with content from vRA. Later this package can be exported as bundle containing filesystem representation of initially configured content plus all its referenced dependenceis.

### What is vRealize Orchestrator package?
vRO package is a way to reuse workflows, actions, policies, and configuration elements from one Orchestrator server on another server. Such packages can be export as filesystem objects, transferred and then imported into destination vRO server.

### What is vRealize Orchestrator content bundle?
vROps content bundle is an archive containing filesystem representations of vROps content like dashboards, views, alert definitions, super metrics, etc.

## vRealize Automation Artifact Management
vRealize Artifact Manager provides easy way to create, import and export vRA bundles.

### Configure
vRealize Artifact Manager can be configured to use filesystem folder as source for import operations and vRA server as destination for such and vise versa.
#### Parameters
- Host - vRA hostname
- Port - vRA port
- Tenant - vRA Tenant name
- Username - username for selected vRA tenant
- Password - password for selected username
- ImportOldVersion
  - true, to overwrite all server pacakges regardless of their version.
  - false, to import only packages that have higher version than the version on the server.
```java
ConfigurationVro configVro = new ConfigurationVro.Builder()
    .withHost("vra-l-01a.corp.local")
    .withPort(443)
    .withUsername("administrator@vsphere.local")
    .withPassword("VMware1!")
    .withPackageImportOldVersions(true)
    .build();
```
### Initialize
vRA Package Store is the main consumer interface. The store can be created out of the configuration object, thus all operations will be performed in this configuration context.
```java
PackageStore vraStore = PackageStoreFactory.getInstance(configVra);
```
### Use
#### List available vRA packages on the server
```java
List<Package> packages = vraStore.getPackages();
```
#### Export vRA package out of vRA Package descriptor
As vRA bundle can only be created through API and its purpose is to be exported, thus the create operation is not explicitly available.
Ex. /home/jhon/vra-nsx-xaas/content.yaml (content type: list of case sensitive names)
```yaml
---
property-group:
property-definition:
software-component:
composite-blueprint:
xaas-blueprint:
    - Create Loadbalancer
xaas-resource-action:
    - Create VIP
    - Destroy VIP
    - Destroy Loadbalancer
xaas-resource-type:
xaas-resource-mapping:
workflow-subscription:
...
```
Build vRA package descriptor out of the file content and perform export operations with the descriptor.
The export operations accepts boolean flag DRYRUN. If set, vRealize Artifact Manager will perform all the filter and check operations but will not actually export the content.
Export of vRA Workflow Subscriptions is supported only in vRA projects containing at least one Composite or XaaS Blueprint or Resource Action.
```java
Package vraPackage = PackageFactory.getInstance(PackageType.VRA, new File("/home/jhon/nsx-0.0.1.package"));
vraStore.exportPackage(vraPackage, new File("/home/jhon/vra-nsx/content.yaml"), dryrun);
```
#### Export an existing package
```java
vraStore.exportPackage(PackageFactory.getInstance(PackageType.VRA, new File("/home/jhon/vra-nsx-xaas/target/com.vmware.pscoe.nsx-1.0.0.bundle")), dryrun);
```
#### Import vRA bundle
```java
vraStore.importPackage(PackageFactory.getInstance(PackageType.VRA, new File("/home/jhon/vra-nsx-xaas/target/com.vmware.pscoe.nsx-1.0.0.bundle")), dryrun);
```

## vRealize Orchestrator Artifact Management
vRealize Artifact Manager provides easy way to create, import and export vRO packages.

### Configure
vRealize Artifact Manager can be configured to use filesystem folder as source for import operations and vRO server as destination for such and vise versa.
#### Parameters
- Host - vRA hostname
- Port - vRA port
- Tenant - vRA Tenant name
- Username - username for selected vRA tenant
- Password - password for selected username
- ImportOldVersion
  - true, to overwrite all server packages regardless of their version.
  - false, to import only packages that have higher version than the version on the server.
- ExportVersionHistory - export vRO package version history
- ExportConfigurationAttributeValues - export vRO package Configuration Elements attributes
- ExportConfigSecureStringAttributeValues - export vRO package Configuration Elements SecureString attributes
- ExportGlobalTags - exports vRO global tags
- ExportAsZip - exports as zip
```java
ConfigurationVro configVro = new ConfigurationVro.Builder()
    .withHost("vra-l-01a.corp.local")
    .withPort(443)
    .withUsername("administrator@vsphere.local")
    .withPassword("VMware1!")
    .withPackageImportOldVersions(true)
    .build();
```
### Initialize
vRO Package Store is the main consumer interface. The store can be created out of the configuration object, thus all operations will be performed in this configuration context.
```java
PackageStore vroStore = PackageStoreFactory.getInstance(configVro);
```
### Use
#### List available vRO packages on the server
```java
List<Package> packages = vroStore.getPackages();
```
#### Export an existing package
```java
Package vroPackage = new PackageFactory().getInstance(PackageType.VRO, new File("/home/jhon/com.jhon.library.nsx-2.0.9.package"));
vroStore.exportPackage(vroPackage, dryrun);
```
#### Import vRO package
```java
Package vroPackage = new PackageFactory().getInstance(PackageType.VRO, new File("/home/jhon/com.jhon.library.nsx-2.0.9.package"));
vraStore.importPackage(vroPackage, dryrun);
```
## vRealize Operations Manager Artifact Management
vRealize Artifact Manager provides easy way to create, import and export vROps bundles.

### Configure
vRealize Artifact Manager can be configured to use filesystem folder as source for import operations and vROps server as destination for such and vise versa.

#### Parameters
- host - vROps server FQDN or IP address
- port - vROps port for SSH access
- httpPort - vROps port for HTTP/HTTPS access
- auth - Authentication mechanism. Supported values: BASIC
- username - Administrator username for SSH access.
- password - Password for SSH user
- restUser - Administrator username for REST API access
- restPassword - Password for REST user
- dashboardUser - User account to which to assign the ownership of a dashboard when importing it.
- importDashboardsForAllUsers - if true, dashboard is imported to all users, if false, it's imported to dashboardUser only

Notes

======

As in the next versions of vROPs the BASIC authentication method will be deprecated, the vROPs archetype will support two authentication methods:
- BASIC - for backward compatibility (default)
- AUTH_N - token based authentication.

The configuration of the authentication method is done via the settings.xml file using the following key:

   <vrops.restAuthProvider>BASIC|AUTH_N</vrops.restAuthProvider>

If the authentication provider is set to AUTH_N you may configure the authentication source using the following key:

   <vrops.restAuthSource>local</vrops.restAuthSource>

The default authentication source is set to 'local'

### Initialize
vROps Package Store is the main consumer interface. The store can be created out of the configuration object, thus all operations will be performed in this configuration context.
```java
PackageStore vropsStore = PackageStoreFactory.getInstance(configVrops);
```
### Use
#### Export an existing package
```java
vropsStore.exportPackage(pkg, new File(pkg.getFilesystemPath()), dryrun)
```
#### Import vROps package
```java
File pkgFile = new File(directory, pkgInfoProvider.getPackageName() + "." + PackageType.VROPS.getPackageExtention());
Package vropsPackage = PackageFactory.getInstance(PackageType.VROPS, pkgFile);
vropsStore.importPackage(vropsPackage, dryrun);
```
## Resources
- Import and Export vRA Content API (<https://docs.vmware.com/en/vRealize-Automation/7.1/com.vmware.vra.programming.doc/GUID-BEA80621-CDE3-4E07-96E9-23B507FEED47.html>)
- Import and Export vROps Content CLI (<https://docs.vmware.com/en/vRealize-Operations-Manager/7.5/com.vmware.vcom.cli.doc/GUID-685AC89B-25A0-4648-A25D-97454D5B0346.html>)
