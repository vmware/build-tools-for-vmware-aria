/**
 * A baseline contains a collection of one or more patches, extensions, service packs, or bug fixes.
 */
declare class VumBaseline {
	// Baseline ID
	id: number;
	// URI of the server	
	serverUri: string;
	// Baseline name
	name: string;
	// Baseline description
	description: string;
	// The last update time
	lastUpdateTime: Date;
	// Formatted string for the last update timestamp
	lastUpdateTimeAsStr: string;
	// Baseline type. See the BaselineType enumeration for valid values.
	baselineType: any;
	// Baseline content type. See the BaselineContentType enumeration for valid values.
	contentType: any;
	// Baseline target type. See the TargetType enumeration for valid values.
	targetType: any;

	constructor();
}

/**
 * A baseline group contains a collection of one or more baselines.
 */
declare class VumBaselineGroup {
	// BaselineGroup ID
	id: number;
	// URI of the server
	serverUri: string;
	// BaselineGroup name
	name: string;
	// BaselineGroup description
	description: string;
	// The last update time
	lastUpdateTime: Date;
	// Formatted string for the last update timestamp
	lastUpdateTimeAsStr: string;
	// BaselineGroup target type. See the TargetType enumeration for valid values.
	targetType: any;

	constructor();
}

/**
 * A baseline search criteria for the Update Manager plug-in.
 */
declare class VumBaselineSearchSpec {
	// IDs of the baselines to retrieve;
	ids: number;
	// Names or wildcard name expressions of the baselines to retrieve;
	nameExpressions: string[];
	// Types of the baselines to retrieve;
	baselineTypes: any[];
	// TargetTypes of the baselines to retrieve;
	targetTypes: any[];
	// ContentTypes of the baselines to retrieve;
	contentTypes: any[]
	// Specifies vSphere objects to which baselines should be attached;
	viInventories: VumVIInventory[];
	// Retrieve baselines containing this patch;
	patch: VumPatchInfo;

	constructor(serverURI: string);

	/**
	 * Utility method to parse and set BaselineType from a string value
	 * 
	 * @param baselineTypes 
	 */
	setBaselineTypesByStringArray(baselineTypes: string[]): void;
	/**
	 * Utility method to parse and set a target type from a string value
	 * 
	 * @param targetTypes 
	 */
	setTargetTypesByStringArray(targetTypes: string[]): void;
	/**
	 * Utility method to parse and set a content type from a string value
	 * 
	 * @param contentTypes 
	 */
	setContentTypesByStringArray(contentTypes: string[]): void;

}

/**
 * Compliance status for an entity.
 */
declare interface VumCompliance {
	// Baseline ID
	baselineId: number;
	// vSphere object compliance status
	status: string;
	// vSphere object whose status is reported
	entity: VumVIInventory;
}

/**
 * Configuration for a virtual machine remediation task.
 */
declare class VumGuestRemediationOption {
	// Indicate if you want to take a snapshot before initializing the remediation.;
	guestCreateSnapshot: boolean;
	// Specify how long (in hours) you want to keep the snapshot. If not set, the snapshots are never deleted.;
	guestKeepSnapshotHours: number;
	// Indicate if you want the guest OS to take a memory dump while creating a snapshot.;
	guestTakeMemoryDump: boolean;
	// Specify a name for the snapshot you want to create before initializing the remediation. This parameter is applicable only if the GuestCreateSnapshot parameter is set.;
	guestSnapshotName: string;
	// Specify a description for the snapshot you want to create before initializing the remediation. This parameter is valid only if the GuestCreateSnapshot parameter is set.;
	guestSnapshotDescription: string;

	constructor();
}

/**
 * This object represents a vCenter Server system with which an Update Manager instance is registered.
 */
declare interface VumHost {
	// Host ID
	id: number;
	// URI of the server
	serverUri: string;
	// The name of the host (URL)
	name: string;
	// Set to true if the Update Manager server is accessible
	isActive: boolean;
}

/**
 * Configuration for a host remediation task.
 */
declare class VumHostRemediationOption {
	// Specify how long (in seconds) to wait for the host to enter maintenance mode.
	hostRetryDelaySeconds: number;
	// Specify how many times to retry to put the host into maintenance mode.
	hostNumberOfRetries: number;
	// Specify an action in case the host cannot be put into maintenance mode. This might happen due to running virtual machines that cannot be automatically migrated to another host. The valid values are FailTask, Retry, PowerOffVMsAndRetry, and SuspendVMsAndRetry.
	hostFailureAction: string;
	// Specify an action before trying to put the host into maintenance mode. The valid values are PowerOffVMs, SuspendVMs, and DoNotChangeVMsPowerState.
	hostPreRemediationPowerAction: string;
	// Indicate if you want to temporarily deactivate any media devices that could prevent the hosts from entering maintenance mode.
	hostDisableMediaDevices: boolean;
	// Indicate if you want to ignore the installed third-party software that is incompatible with the upgrade.
	hostIgnoreThirdPartyDrivers: boolean;
	// Indicate if you want to enable PXE booted ESXi hosts patching.
	hostEnablePXEbootHostPatching: boolean;
	// Indicate if you want to temporarily deactivate Distributed Power Management (DPM) for the specified clusters. After the remediation, DPM is automatically re-enabled.
	clusterDisableDPM: boolean;
	// Indicate if you want to temporarily deactivate High Availability (HA) for the specified clusters. After the remediation, HA is automatically re-enabled.
	clusterDisableHAC: boolean;
	// Indicate if you want to temporarily deactivate Fault Tolerance (FT) for the specified clusters. After the remediation, FT is automatically re-enabled.
	clusterDisableFT: boolean;
	// Indicate if you want to enable parallel remediation for the specified clusters.
	clusterEnableParallelRemediation: boolean;

	constructor()
}

/**
 * Update Manager Object Manager Type
 */
declare class VumObjectManager {
	/**
	 * Returns the default Update Manager host.
	 */
	static getDefaultVumHost(): VumHost[];
	/**
	 * Retrieves the baselines specified by the provided parameters. There are two default dynamic patch baselines
	 * (Critical Host Patches (Predefined) and Non-Critical Host Patches (Predefined)) and three upgrade baselines
	 * (VMware Tools Upgrade to Match Host (Predefined), VM Hardware Upgrade to Match Host (Predefined),
	 * and VA Upgrade to Latest (Predefined)). You cannot edit or delete the default baselines.
	 * 
	 * @param vcUri
	 * @param name
	 */
	static getBaselines(vcUri: string, name: string): VumBaseline[];
	/**
	 * Retrieves the baseline groups.
	 * 
	 * @param vcUri
	 */
	static getAllBaselineGroups(vcUri: string): VumBaselineGroup[];
	/**
	 * Get EULA content related for the provided array of baselines.
	 * 
	 * @param baselines
	 */
	static getEulasContent(baselines: VumBaseline[]): string;
	/**
	 * Filter baselines and return only those that are related to a Virtual Appliance update.
	 * 
	 * @param baselines
	 */
	static getVirtualApplianceBaselines(baselines: VumBaseline[]): VumBaseline[];
	/**
	 * @deprecated
	 * This method will be removed in next plugin release. Please use the new one instead "getVirtualApplianceBaselines".
	 * Filter baselines and return only those that are related to a Virtual Appliance update.
	 * 
	 * @param baselines
	 */
	static getVirutalApplicaneBaselines(baselines: VumBaseline[]): VumBaseline[];
	/**
	 * Accept all software update EULAs for specified virtual appliance related baselines.
	 * 
	 * @param baselines
	 */
	static acceptAllEulaContracts(baselines: VumBaseline[]): void;
	/**
	 * Get the Update Manager URL assigned to the vCenter Server instance.
	 * Returns null if there is no Update Manager instance assigned to vCenter Server.
	 * The method throws an exception if there is no vCenter Server instance at the specified URL.
	 * 
	 * @param url
	 */
	static getVumUri(url: string): string;
	/**
	 * @deprecated
	 * This method will be removed in the next plugin release.
	 * It is not needed anymore due to the new plugin logic that synchronizes all currently registered
	 * vCenter instances vSphere plugin with VUM plugin. Remove all vCenter Server instances with the Update Manager host.
	 */
	static removeAllVcenterWithVum(): void;
	/**
	 * Retrieves the registered vCenter Server instances with extensions from the Update Manager plug-in configuration.
	 */
	static getVumHosts(): VumHost[];
	/**
	 * Retrieves the URL of registered vCenter Server instances with extensions from the Update Manager plug-in configuration.
	 */
	static getVumHostsURLs(): string[];
	/**
	 * Retrieves the baselines specified by the provided parameters.
	 * 
	 * @param searchSpec 
	 */
	static getFilteredBaselines(searchSpec: VumBaselineSearchSpec): VumBaseline[];
	/**
	 * Retrieves patches specified by the provided parameters.
	 * 
	 * @param vcUri 
	 * @param spec 
	 * @param patchIds 
	 * @param baselineIds 
	 * @param bundleType 
	 * @param installationImpact 
	 */
	static getPatches(vcUri: string, spec: VumPatchSearchSpec, patchIds: number[], baselineIds: number[], bundleType: string[], installationImpact: string[]): VumPatch[];
	/**
	 * @deprecated
	 * This method will be removed in the next plugin release. It is not needed anymore due to the new plugin logic
	 * that synchronizes all currently registered vCenter instances vSphere plugin with VUM plugin.
	 * Remove a vCenter Server instance with Update Manager from the Update Manager plug-in configuration.
	 * 
	 * @param url 
	 */
	static removeVcenterWithVum(url: string): void;
	/**
	 * @deprecated
	 * This method will be removed in the next plugin release. It is not needed anymore due to the new plugin logic
	 * that synchronizes all currently registered vCenter instances vSphere plugin with VUM plugin.
	 * Bulk remove of vCenter Server instances with Update Manager from the Update Manager plug-in configuration.
	 * 
	 * @param urls 
	 */
	static removeVcenterInstancesWithVum(urls: string[]): void;
	/**
	 * Set default the default vCenter Server instance with assigned Update Manager server.
	 * 
	 * @param url 
	 */
	static changeDefaultServerTo(url: string): void;
	/**
	 * Register a vCenter Server instance with assigned Update Manager server.
	 * 
	 * @param url 
	 */
	static registerVcenterWithVum(url: string): void;
	/**
	 * @deprecated
	 * This method will be removed in the next plugin release. It is not needed anymore due to the new plugin logic
	 * that synchronizes all currently registered vCenter instances vSphere plugin with VUM plugin.
	 * Bulk registration of vCenter Server instances with assigned Update Manager server.
	 * 
	 * @param urls 
	 */
	static registerVcenterInctencesWithVum(urls: string[]): void;
	/**
	 * Utility method returning the default vCenter Server URI for the plug-in as set from its configuration.
	 */
	static getDefaultVcUri(): string;
	/**
	 * Return a list of vCenter Server instances set in the plug-in configuration
	 * 
	 * @param vcPluginVCs 
	 */
	static getVcServerUri(vcPluginVCs: string[]): string[];
	/**
	 * Creates a new patch baseline. Patch baselines can be applied to hosts or virtual machines.
	 * Depending on the patch criteria you select, patch baselines can be either dynamic or static (fixed).
	 * You can specify the patches that you want to include in the baseline by using the includePatch parameter.
	 * You can also use the searchSpec attribute to filter the patches that you want to include in a baseline.
	 * You can filter patches by their properties, such as product, vendor, severity, and release date.
	 * The patches that have been excluded by using the excludePatch parameter will never be included
	 * even if they correspond to the filter defined by the searchSpec attribute.
	 * 
	 * @param vcUri 
	 * @param name 
	 * @param desciption 
	 * @param contentType 
	 * @param targetType 
	 * @param includePatch 
	 * @param excludePatch 
	 * @param isExtension 
	 * @param searchSpec 
	 */
	static createPatchBaseline(vcUri: string, name: string, desciption: string, contentType: string, targetType: string, includePatch: VumPatchInfo[], excludePatch: VumPatchInfo[], isExtension: boolean, searchSpec: VumPatchSearchSpec): VumBaseline;
	/**
	 * Connect to a vCenter Server and retrieve all entities from it.
	 * 
	 * @param serverUri 
	 */
	static getEntities(serverUri: string): VumVIInventory[];
	/**
	 * Attaches baselines to a vSphere object that you select.
	 * The object can be a template, virtual machine, vApp, ESX/ESXi host, folder, cluster, or a datacenter.
	 * Attaching a baseline to a container object such as a folder or datacenter transitively attaches the baseline to all objects in the container.
	 * 
	 * @param baselines 
	 * @param entities 
	 */
	static attachBaselines(baselines: VumBaseline[], entities: VumVIInventory[]): void;
	/**
	 * Detaches baselines from the specified vSphere objects. To detach inherited baselines, you must detach them from the parent object.
	 * 
	 * @param baselines 
	 * @param entities 
	 */
	static detachBaselines(baselines: VumBaseline[], entities: VumVIInventory[]): void;
	/**
	 * Utility method to retrieve all patch products.
	 * 
	 * @param vcUri
	 */
	static getPatchProducts(vcUri: string): string[];
	/**
	 * Utility method to retrieve all patch languages.
	 * 
	 * @param vcUri
	 */
	static getPatchLanguages(vcUri: string): string[];
	/**
	 * Utility method to retrieve all patch vendors.
	 * 
	 * @param vcUri
	 */
	static getPatchVendors(vcUri: string): string[];
	/**
	 * Utility method to retrieve all bundle types.
	 * 
	 * @param vcUri
	 */
	static getBundleTypes(vcUri: string): string[];
	/**
	 * Utility method to retrieve the installation impacts of all patches.
	 * 
	 * @param vcUri 
	 */
	static getPatchInstallationImpacts(vcUri: string): string[];
	/**
	 * Deletes the specified baseline. Before the removal, the baseline is detached from all objects it has been attached to.
	 * 
	 * @param baseline 
	 */
	static deleteBaseline(baseline: VumBaseline): boolean;
	/**
	 * Deletes the specified baselines. Before the removal, the baselines are detached from all objects they have been attached to.
	 * 
	 * @param baselines 
	 */
	static deleteBaselines(baselines: VumBaseline[]): boolean;
	/**
	 * Checks for new patches and depending on the availability downloads them to the Update Manager repository.
	 * 
	 * @param vcUri 
	 * @param languages 
	 */
	static downloadPatchesByLanguages(vcUri: string, languages: string[]): boolean;
	/**
	 * Asynchronously checks for new patches and depending on the availability downloads them to the Update Manager repository.
	 * 
	 * @param vcUri 
	 * @param languages 
	 */
	static downloadPatchesByLanguagesAsync(vcUri: string, languages: string[]): void;
	/**
	 * Creates a trigger used by the asynchronous workflows.
	 * 
	 * @param timeout 
	 * @param sdkType 
	 */
	static createTrigger(timeout: number, sdkType: string): any;
	/**
	 * Modifies the properties of a patch baseline.
	 * 
	 * @param baseline 
	 */
	static updatePatchBaseline(baseline: VumBaseline): void;
	/**
	 * Scans vSphere objects against patches included in the baselines attached to them.
	 * The object can be a template, virtual machine, vApp, ESX/ESXi host, folder, cluster, or a datacenter.
	 * If the objects are of different types, the method starts a separate vCenter Server task for each object.
	 * 
	 * @param entities 
	 * @param types 
	 */
	static scanInventory(entities: VumVIInventory[], types: string[]): void;
	/**
	 * Retrieve baseline compliance data for the specified vSphere object of type template, virtual machine,
	 * vApp, ESX/ESXi host, folder, cluster, or a datacenter.
	 * The method returns information about the compliance of the specified object against the baselines that are attached to it.
	 * If the object is a container, the cmdlet returns compliance data for all objects in the container.
	 * 
	 * @param entities 
	 * @param complianceStatus 
	 * @param baselines 
	 */
	static getCompliance(entities: VumVIInventory[], complianceStatus: string, baselines: VumBaseline[]): VumCompliance[];
	/**
	 * Stages patches. Staging allows you to download patches from the Update Manager server to the ESX/ESXi hosts,
	 * without applying the patches immediately. You can stage patches to hosts or container objects such as clusters or datacenters.
	 * 
	 * @param entities 
	 * @param baselines 
	 * @param patches 
	 */
	static stage(entities: VumVIInventory[], baselines: VumBaseline[], patches: VumPatchInfo[]): boolean;
	/**
	 * Stages patches. Staging allows you to download patches from the Update Manager server to the ESX/ESXi hosts,
	 * without applying the patches immediately. You can stage patches to hosts or container objects such as clusters or datacenters.
	 * The method returns array with task keys for all of the started vCenter Server tasks.
	 * 
	 * @param entities 
	 * @param baselines 
	 * @param patches 
	 */
	static stageAsync(entities: VumVIInventory[], baselines: VumBaseline[], patches: VumPatchInfo[]): string[];
	/**
	 * Remediates an inventory object against the specified baselines.
	 * You can remediate a template, virtual machine, vApp, ESX/ESXi host, folder, cluster, or a datacenter.
	 * 
	 * @param entities 
	 * @param baselines 
	 * @param excludedPatches 
	 * @param guestOption 
	 * @param hostOption 
	 */
	static remediate(entities: VumVIInventory[], baselines: VumBaseline[], excludedPatches: VumPatchInfo[], guestOption: VumGuestRemediationOption, hostOption: VumHostRemediationOption): boolean;
	/**
	 * Remediates an inventory object against the specified baselines. Returns an array of vCenter Server task keys.
	 * You can remediate a template, virtual machine, vApp, ESX/ESXi host, folder, cluster, or a datacenter.
	 * 
	 * @param entities 
	 * @param baselines 
	 * @param excludedPatches 
	 * @param guestOption 
	 * @param hostOption 
	 */
	static remediateAsync(entities: VumVIInventory[], baselines: VumBaseline[], excludedPatches: VumPatchInfo[], guestOption: VumGuestRemediationOption, hostOption: VumHostRemediationOption): string[];
	/**
	 * Export baselines in .xml format.
	 * 
	 * @param baselines 
	 */
	static exportBaselines(baselines: VumBaseline[]): string;
	/**
	 * Import baselines from .xml that you have generated by using exportBaselines.
	 * 
	 * @param vcUri 
	 * @param xml 
	 */
	static importBaselines(vcUri: string, xml: string): VumBaseline[];
	/**
	 * Export Compliance report to external file.
	 * 
	 * @param compliances 
	 * @param format 
	 * @param serverOutFile 
	 */
	static exportCompliance(compliances: VumCompliance[], format: any, serverOutFile: string): void;
	/**
	 * Format a finder (dunesUri) from the vSphere plug-in as VIInventory finder.
	 * 
	 * @param vcObject 
	 * @param schema 
	 * @param port 
	 */
	static formatAsVumObject(vcObject: string, schema: string, port: number): string;
	/**
	 * Validates final state of a trigger. Should be called after the trigger is fired.
	 * 
	 * @param trigger 
	 */
	static validateTriggerSuccess(trigger: any): void;
	/**
	 * Retrieves the vCenter Server entities to which these baseline groups are attached.
	 * 
	 * @param vcUri 
	 * @param baselineGroups 
	 */
	static getEntitiesByBaselineGroup(vcUri: string, baselineGroups: VumBaselineGroup[]): VumBaselineGroup[];
	/**
	 * Retrieves the vCenter Server entities to which these baselines are attached
	 * 
	 * @param vcUri 
	 * @param baselines 
	 */
	static getEntitiesByBaseline(vcUri: string, baselines: VumBaseline[]): VumBaseline[];
}

/**
 * Container object containing specific information and details about a patch.
 */
declare interface VumPatch {
	// URI of the server;
	serverUri: string;
	// Specifies additional details for a patch;
	detail: VumPatchDetail;
	// Specifies additional information about a patch;
	info: VumPatchInfo;
}

declare class VumPatchBaseline {
	// Baseline ID
	id: number;
	// URI of the server
	serverUri: string;
	// Baseline name
	name: string;
	// Baseline description
	description: string;
	// The last update time
	lastUpdateTime: Date;
	// Formatted string for the last update timestamp
	lastUpdateTimeAsStr: string;
	// Baseline type. See the BaselineType enumeration for valid values.
	baselineType: any;
	// Baseline content type. See the BaselineContentType enumeration for valid values.
	contentType: any;
	// Baseline target type. See the TargetType enumeration for valid values.
	targetType: any;
	// Patches included in this baseline
	inclPatches: VumPatchInfo[];
	// Patches excluded from this baseline
	exclPatches: VumPatchInfo[];
	// Patches part of this baseline
	currentPatches: VumPatchInfo[];
	// Search criteria for dynamic patches
	patchSearchSpec: VumPatchSearchSpec;

	constructor();

	/**
	 * Utility method to parse and set content type from string value.
	 * 
	 * @param contentType 
	 */
	setContentTypeFromString(contentType: string): void;
}

/**
 * A patch detail from the Update Manager plug-in.
 */
declare interface VumPatchDetail {
	// Patch status
	status: number;
	// Patch ID as provided by the vendor
	idByVendor: string[];
	// URL for additional details
	detailsUrl: string;
	// Patch description
	description: string;
	// The name of the vendor that provides the update
	vendor: string;
	// Whether or not the update requires to be installed separately
	installSeparately: boolean;
	// The affected sub-components
	affectedComponent: string[];
	// The path or URL of the installable module if exists
	binaryPath: string;
	// The command line switches to install the update
	installFlags: string;
	// Size of the patch
	patchSize: number;
	// The bugtraq identifiers that this update applies to
	bugtraqId: string[];
	// The CVE identifiers that this update applies to
	cveId: string[];
}

declare class VumPatchInfo {
	// Patch id
	id: number;
	// URI of the server
	serverUri: string;
	// Specifies the patch name
	name: string;
	// Specifies the ID provided by the vendor
	idByVendor: string;
	// Specifies the patch vendor
	vendor: string;
	// Specifies the patch release date
	releaseDate: Date;
	// Specifies the patch severity level. See the Severity enumeration for valid values.
	severity: any;
	// Specifies the patch target type. See the TargetType enumeration for valid values.
	targetType: any;
	// Specifies the patch impact level for the vSphere object. The list with valid values is retrieved with a call to VumObjectManager.getInstallationImpacts().
	impactLevel: string[];
	// Specifies the patch bundle type. The list with valid values is retrieved with a call to VumObjectManager.getBundleTypes().
	bundleType: string;
	// Specifies the patch update type. See the UpdateType enumeration for valid values.
	updateType: any;

	constructor();
}

/**
 * A patch search criteria for the Update Manager plug-in.
 */
declare class VumPatchSearchSpec {
	// URI of the server;
	serverUri: string;
	// Comma separated list of search keywords. Match is case insensitive.
	phrase: string;
	// Beginning of the search interval
	startDate: Date;
	// End of the search interval
	endDate: Date;
	// Specify the vendors of the patches that you want to retrieve.
	vendor: string[];
	// Specify the names of the software products for which you want to retrieve patches.
	product: string[];
	// Specifies the patch severity level. See the Severity enumeration for valid values.
	severity: any;
	// Specifies patch target type. See the TargetType enumeration for valid values.
	targetType: any;
	// Specifies patch update type. See the UpdateType enumeration for valid values.
	updateType: any;
	// Patch language. The list with valid values is retrieved with a call to VumObjectManager.getPatchLanguages().
	language: string[];
	// If set to true, and if the result set is too big, the Update Manager server reports an error.
	failOnManyUpdates: boolean;

	constructor(serverURI: string);

	/**
	 * Utility method to parse and set severity from a string value.
	 * 
	 * @param strSeverity 
	 */
	setSeverityFromStrings(strSeverity: string[]): void;
	/**
	 * Utility method to parse and set a target type from a string value.
	 * 
	 * @param strTargetType 
	 */
	setTargetTypeFromStrings(strTargetType: string[]): void;
	/**
	 * Utility method to parse and set an update type from string value.
	 * 
	 * @param strUpdateType 
	 */
	setUpdateTypeFromStrings(strUpdateType: string[]): void;
	/**
	 * Utility method to parse and set an host update category from string value.
	 * 
	 * @param strHostUpdateCategory 
	 */
	setHostUpdateCategoryFromStrings(strHostUpdateCategory: string[]): void;
}

/**
 * Represents a vSphere object used in the Update Manager plug-in.
 */
declare class VumVIInventory {
	// vSphere object ID
	id: string;
	// vSphere object type
	type: string;
	// vSphere object name
	name: string;

	constructor(serverURI: string);
}
