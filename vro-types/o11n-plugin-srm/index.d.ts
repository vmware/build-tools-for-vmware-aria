// Site Recovery Manager plugin

/**
 * Represents SRM Callout object.
 */
declare class SRMCommand {
	/** Display name of this Object */
	name: string;
	/** Pre- or post-power on step */
	isPrePowerOnStep: boolean;
	/** Command to run while running a recovery */
	command: string;
	/** Timeout */
	timeout: number;
	/** Execution target: SRM server or in-guest */
	runInRecoveredVm: boolean;

	constructor();
}

/**
 * Represents the connection settings for the SRM plugin.
 */
declare class SRMConfigurationSettings {
	/** Display name of this Object */
	name: string;
	/** Extension of the SRM server */
	deploymentId: string;
	maxConnections: number;
	sessionTimeout: number;
	connectTimeout: number;

	constructor();
}

declare class SRMFactoryLifecycleManager {
	/** Display name of this Object */
	name: string;

	constructor();
}

/**
 * Represents a vVol fault domain.
 */
declare class SRMFaultDomain {
	/** Display name of this Object */
	name: string;
	/** Extension of the SRM server */
	deploymentId: string;

	constructor();
}

/**
 * Represents root of the inventory tree.
 */
declare class SRMInventory {
	/** Display name of this Object */
	name: string;
	/** Extension of the SRM server */
	deploymentId: string;

	constructor();
}

/**
 * IPv4 address definitions.
 */
declare class SRMIpv4Spec {
	/** Display name of this Object */
	name: string;
	/** Contains static address */
	address: string;
	/** Contains a list of gateways */
	gateways: string[];
	/** Subnet mask */
	subnetMask: string;

	constructor();
}

/**
 * IPv6 address definitions.
 */
declare class SRMIpv6Spec {
	/** Display name of this Object */
	name: string;
	/** Contains static address */
	address: string;
	/** Contains a list of gateways */
	gateways: string[];
	/** Subnet Prefix Length */
	subnetPrefixLength: number;

	constructor();
}

/**
 * Represents Local Datacenter.
 */
declare class SRMLocalDatacenter {
	/** Display name of this Object */
	name: string;
	/** Path to this Scripting Object */
	path: string;
	/** Type of this Scripting Object */
	morType: string;
	/** VC of this Scripting Object */
	vcUriHost: string;

	constructor();
}

/**
 * Represents Local Network.
 */
declare class SRMLocalNetwork {
	/** Display name of this Object */
	name: string;
	/** Path to this Scripting Object */
	path: string;
	/** Type of this Scripting Object */
	morType: string;
	/** VC of this Scripting Object */
	vcUriHost: string;

	constructor();
}

/**
 * Represents Local ResourcePool.
 */
declare class SRMLocalResourcePool {
	/** Display name of this Object */
	name: string;
	/** Path to this Scripting Object */
	path: string;
	/** Type of this Scripting Object */
	morType: string;
	/** VC of this Scripting Object */
	vcUriHost: string;

	constructor();
}

/**
 * Represents a VM network device.
 */
declare class SRMNetworkDevice {
	/** Display name of this Object */
	name: string;
	/** Extension of the SRM server */
	deploymentId: string;

	constructor();
}

/**
 * Contains all data, needed to perform IP customization for a network mapping.
 */
declare class SRMNetworkIPCustomizationSpec {
	/** Display name of this Object */
	name: string;
	/** Local subnet IP address */
	localSubnet: string;
	/** Remote subnet IP address */
	remoteSubnet: string;
	/** Subnet prefix number */
	subnetPrefix: number;
	/** Gateway IP address */
	gateway: string;
	/** List of DNS addresses separated by semicolon */
	dnsAddresses: string;
	/** List of DNS suffixes separated by semicolon */
	dnsSuffixes: string;
	/** Primary WINS server IP address */
	primaryWinsServer: string;
	/** Secondary WINS server IP address */
	secondaryWinsServer: string;

	constructor();
}

/**
 * Contains IP customization info for a specific network adapter.
 */
declare class SRMNicSpec {
	/** Display name of this Object */
	name: string;
	/** Identifier of the virtual network device */
	deviceId: number;
	/** IP customization information for IPv4 address */
	ipv4Spec: SRMIpv4Spec;
	/** IP customization information for IPv6 address */
	ipv6Spec: SRMIpv6Spec;
	/** List of server IP addresses to use for DNS lookup */
	dns: string[];
	/** DNS suffixes */
	dnsSufix: string[];
	/** IP addresses of the primary and secondary WINS servers */
	wins: string[];

	constructor();
}

/**
 * Represents a Placeholder Datastore.
 */
declare class SRMPlaceholderDatastore {
	/** Display name of this Object */
	name: string;
	/** Extension of the SRM server */
	deploymentId: string;

	constructor();
}

/**
 * Handles plugin connection configuration.
 */
declare class SRMPluginConfig {
	/** Display name of this Object */
	name: string;

	/** Refreshes cached information so the other workflow can see the new configuration */
	public refresh(): void;
	public listUnassignedReplicatedDatastores(deploymentId: string): SRMScriptingObject[];
	public listUnassignedReplicatedVms(deploymentId: string): SRMScriptingObject[];
	/**
	 * @param host 
	 * @param port 
	 * @param path 
	 */
	public getSrmUrls(host: string, port: number, path: string): string[];
	/**
	 * Validate credetials for local site.
	 * @param host 
	 * @param port 
	 * @param path 
	 * @param username 
	 * @param password 
	 */
	public validateConnection(host: string, port: number, path: string, username: string, password: string): void;
	/**
	 * Register local site information in SRM plugin configuration.
	 * @param host 
	 * @param port 
	 * @param path 
	 * @param username 
	 * @param password 
	 */
	public registerLocalSites(host: string, port: number, path: string, username: string, password: string): void;
	/**
	 * Unregister local site information from SRM plugin configuration.
	 * @param host 
	 */
	public unregisterLocalSites(host: string): void;
	public getAllRegisteredInstances(): string[];
	/**
	 * List Fault Domains.
	 * @param deploymentId 
	 */
	public listFaultDomains(deploymentId: string): SRMFaultDomain[];
	/**
	 * List Replication Groups within a Fault Domain.
	 * @param deploymentId 
	 * @param faultDomainId 
	 */
	listUnprotectedReplicationGroupsInFaultDomain(deploymentId: string, faultDomainId: string): SRMReplicationGroup[];
	/**
	 * List VMs in a vVol replication group.
	 * @param replicationGroup 
	 */
	listVMsInVvolReplicationGroup(replicationGroup: SRMReplicationGroup): SRMUnassignedReplicatedVm[];
	/** Retrieves the current configuration settings */
	getConfigurationSettings(): SRMConfigurationSettings;
	/**
	 * Saves the new configuration settings.
	 * @param newConfigurationSettings 
	 */
	saveConfigurationSettings(newConfigurationSettings: SRMConfigurationSettings): void;
}

/**
 * Represents SRM Callout object.
 */
declare class SRMPrompt {
	/** Display name of this Object */
	name: string;
	/** Pre- or post-power on step */
	isPrePowerOnStep: boolean;
	/** Message of the Callout */
	promptText: string;

	constructor();
}

/**
 * Represents Datastores that are part of a ProtectionGroup.
 */
declare class SRMProtectedDatastore {
	/** Display name of this Object */
	name: string;
	/** Extension of the SRM server */
	deploymentId: string;

	constructor();
}

/**
 * Represents VMs that are part of a ProtectionGroup.
 */
declare class SRMProtectedVm {
	/** Display name of this Object */
	name: string;
	/** Extension of the SRM server */
	deploymentId: string;

	constructor();
}

/**
 * Represents SRM ProtectionFolder object.
 */
declare class SRMProtectionFolder {
	/** Display name of this Object */
	name: string;
	/** Extension of the SRM server */
	deploymentId: string;

	constructor();

	/**
	 * Create ABR protection group.
	 * @param name 
	 * @param description 
	 * @param datastores 
	 */
	createAbrGroup(name: string, description: string, datastores: SRMUnassignedReplicatedDatastore[]): SRMProtectionGroup;
	/**
	 * Create HBR protection group.
	 * @param name 
	 * @param description 
	 * @param vms 
	 */
	createHbrGroup(name: string, description: string, vms: SRMUnassignedReplicatedVm[]): SRMProtectionGroup;
	/**
	 * Create VVol protection group.
	 * @param name
	 * @param description 
	 * @param replicationGroups 
	 */
	createVvolGroup(name: string, description: string, replicationGroups: SRMReplicationGroup[]): SRMProtectionGroup;
	/**
	 * Create a new folder with @this as root.
	 * @param newFolderName 
	 */
	createProtectionFolder(newFolderName: string): void;
	/**
	 * Moves this folder to the destination folder.
	 * @param destinationFolder 
	 */
	moveProtectionFolder(destinationFolder: SRMProtectionFolder): void;
	/** Destroys this folder */
	destroyProtectionFolder(): void;
}

/**
 * Represents SRM ProtectionGroup object.
 */
declare class SRMProtectionGroup {
	/** Display name of this Object */
	name: string;
	/** Extension of the SRM server */
	deploymentId: string;
	/** This protection group's replication type */
	readonly replicationType: string;

	constructor();

	/** Return remote Datacenters in this protection group's Site */
	getRemoteDatacenters(): SRMScriptingObject[];
	/** Return remote Networks in this protection group's Site */
	getRemoteNetworks(): SRMScriptingObject[];
	/** Return remote Resource Pools in this protection group's Site */
	getRemoteResourcePools(): SRMScriptingObject[];
	/**
	 * Return network devices for this VM.
	 * @param vmId 
	 */
	getVMNetworkDevices(vmId: string): SRMScriptingObject[];
	/** Remove this ProtectionGroup */
	removeProtectionGroup(): void;
	/**
	 * Protect VM.
	 * @param vmId 
	 */
	protectVm(vmId: string): void;
	/**
	 * Protect VM with custom inventory mappings.
	 * @param vmId 
	 * @param remoteFolder 
	 * @param remoteNetwork 
	 * @param remoteResourcePool 
	 * @param protectPerNetworkDevice 
	 * @param networkDevices 
	 * @param remoteNetworks 
	 */
	protectVmWithCustomInventoryMappings(vmId: string, remoteFolder: SRMRemoteDatacenter, remoteNetwork: SRMRemoteNetwork, remoteResourcePool: SRMRemoteResourcePool, protectPerNetworkDevice: boolean, networkDevices: SRMNetworkDevice[], remoteNetworks: SRMRemoteNetwork[]): void;
	/** Protect all VMs inside the group which can be protected */
	protectAllVms(): void;
	/**
	 * Unprotect VM.
	 * @param vms 
	 */
	unprotectVm(vms: SRMProtectedVm[]): void;
	/** Return all ProtectedVms in this ProtectionGroup */
	getProtectedVm(): SRMProtectedVm[];
	/** Return all ProtectedDatastores in this ProtectionGroup */
	getProtectedDatastore(): SRMProtectedDatastore[];
	/**
	 * Update ProtectionGroup Datastore.
	 * @param datastoresToRemove 
	 * @param datastoresToAdd 
	 */
	updateGroupDatastore(datastoresToRemove: SRMProtectedDatastore[], datastoresToAdd: SRMUnassignedReplicatedDatastore[]): void;
	/**
	 * Add Replicated VM to this ProtectionGroup.
	 * @param vm 
	 */
	addReplicatedVmToVrGroup(vm: SRMUnassignedReplicatedVm): void;
	/**
	 * Remove Replicated VM From VR Group.
	 * @param vmId 
	 */
	removeReplicatedVmFromVrGroup(vmId: string): void;
	/**
	 * Move this group to the destination folder.
	 * @param destinationFolder 
	 */
	moveProtectionGroup(destinationFolder: SRMProtectionFolder): void;
}

/**
 * Represents SRM RecoveryFolder object.
 */
declare class SRMRecoveryFolder {
	/** Display name of this Object */
	name: string;
	/** Extension of the SRM server */
	deploymentId: string;

	constructor();

	/**
	 * Create recovery plan.
	 * @param name 
	 * @param description 
	 * @param groups 
	 */
	createRecoveryPlan(name: string, description: string, groups: SRMProtectionGroup[]): SRMRecoveryPlan;
	/**
	 * Create a new folder with @this as root.
	 * @param newFolderName 
	 */
	createRecoveryFolder(newFolderName: string): void;
	/**
	 * Moves this folder to the destination folder.
	 * @param destinationFolder 
	 */
	moveRecoveryFolder(destinationFolder: SRMRecoveryFolder): void;
	/** Destroys this folder */
	destroyRecoveryFolder(): void;
}

/**
 * Represents SRM RecoveryPlan object.
 */
declare class SRMRecoveryPlan {
	/** Display name of this Object */
	name: string;
	/** Extension of the SRM server */
	deploymentId: string;

	/** Return remote Networks in this Site */
	getRemoteNetworks(): SRMScriptingObject[];
	/** Return RecoverySettings of this RecoveryPlan */
	getRecoverySettings(vmId: string): SRMRecoverySettings;
	/**
	 * Set RecoverySettings of this RecoveryPlan.
	 * @param vmId 
	 * @param settings 
	 */
	setRecoverySettings(vmId: string, settings: SRMRecoverySettings): void;
	/** Delete this Recovery Plan */
	deleteRecoveryPlan(): void;
	/**
	 * Add ProtectionGroups to this RecoveryPlan.
	 * @param protectionGroup 
	 */
	addProtectionGroup(protectionGroup: SRMProtectionGroup): void;
	/**
	 * Delete Callouts of this RecoveryPlan.
	 * @param vmId 
	 * @param commands 
	 * @param prompts 
	 */
	deleteCallouts(vmId: string, commands: SRMCommand[], prompts: SRMPrompt[]): void;
	/** Return the current state of this RecoveryPlan */
	getCurrentState(): string;
	/**
	 * Run test recovery for this RecoveryPlan.
	 * @param syncData 
	 */
	testRecoveryPlan(syncData: boolean): void;
	/** Run planned migration for this RecoveryPlan */
	migrateRecoveryPlan(): void;
	/** Run failover recovery for this RecoveryPlan */
	failoverRecoveryPlan(): void;
	/** Run cleanup for this RecoveryPlan */
	cleanupRecoveryPlan(): void;
	/** Run reprotect for this RecoveryPlan */
	reprotectRecoveryPlan(): void;
	/** Run cancel for this RecoveryPlan */
	cancelRecoveryPlan(): void;
	/**
	 * Add TestNetworkMapping to this RecoveryPlan.
	 * @param remote 
	 * @param testNetwork 
	 */
	addTestNetworkMappingToPlan(remote: SRMRemoteNetwork, testNetwork: SRMRemoteNetwork): void;
	/**
	 * Remove TestNetworkMapping from this RecoveryPlan.
	 * @param remote 
	 */
	removeTestNetworkMappingFromPlan(remote: SRMRemoteNetwork): void;
	/**
	 * Move this plan to the destination folder.
	 * @param destinationFolder 
	 */
	moveRecoveryPlan(destinationFolder: SRMRecoveryFolder): void;
	/** Return all ProtectionGroups in this RecoveryPlan */
	getProtectionGroups(): SRMProtectionGroup[];
	/**
	 * Remove ProtectionGroup from this RecoveryPlan.
	 * @param protectionGroup 
	 */
	removeProtectionGroup(protectionGroup: SRMProtectionGroup): void;
	/** Return all ProtectedVms in this RecoveryPlan */
	getProtectedVm(): SRMProtectedVm[];
}

/**
 * Represents PriorityGroup.
 */
declare class SRMRecoveryPriority {
	/** Display name of this Object */
	name: string;

	private constructor();

	public static get(id: string): SRMRecoveryPriority;
}

/**
 * Represents SRM RecoverySettings object.
 */
declare class SRMRecoverySettings {
	priorityGroup: SRMRecoveryPriority;
	powerState: SRMVirtualMachinePowerState;
	command: SRMCommand[];
	prompt: SRMPrompt[];
	vmIpCustomizationData: SRMVmIpCustomizationSpec;

	constructor();
}

/**
 * Represents Remote Datacenter.
 */
declare class SRMRemoteDatacenter {
	/** Display name of this Object */
	name: string;
	/** Path to this Scripting Object */
	path: string;
	/** Type of this Scripting Object */
	morType: string;
	/** VC of this Scripting Object */
	vcUriHost: string;

	constructor();
}

/**
 * Represents Local Network.
 */
declare class SRMRemoteNetwork {
	/** Display name of this Object */
	name: string;
	/** Path to this Scripting Object */
	path: string;
	/** Type of this Scripting Object */
	morType: string;
	/** VC of this Scripting Object */
	vcUriHost: string;

	constructor();
}

/**
 * Represents Remote ResourcePool.
 */
declare class SRMRemoteResourcePool {
	/** Display name of this Object */
	name: string;
	/** Path to this Scripting Object */
	path: string;
	/** Type of this Scripting Object */
	morType: string;
	/** VC of this Scripting Object */
	vcUriHost: string;

	constructor();
}

/**
 * Represents a replication group within a fault domain.
 */
declare class SRMReplicationGroup {
	/** Display name of this Object */
	name: string;
	/** Extension of the SRM server */
	deploymentId: string;

	constructor();
}

/**
 * Represents Scripting Object Task.
 */
declare class SRMScriptingObjectTask {
	/** Display name of this Object */
	name: string;

	constructor();

	getState(): string;
	getErrorMessage(): string;
}

/**
 * Represents SRM Site object.
 */
declare class SRMSite {
	/** Display name of this Object */
	name: string;
	/** Extension of the SRM server */
	deploymentId: string;

	constructor();

	/**
	 * Login SRM site via user and password.
	 * @param username 
	 * @param password 
	 */
	login(username: string, password: string): void;
	/**
	 * Create folder mapping.
	 * @param local 
	 * @param remote 
	 */
	addFolderMapping(local: SRMLocalDatacenter, remote: SRMRemoteDatacenter): void;
	/**
	 * Create network mapping.
	 * @param local 
	 * @param remote 
	 */
	addNetworkMapping(local: SRMLocalNetwork, remote: SRMRemoteNetwork): void;
	/**
	 * Create test network mapping.
	 * @param remote 
	 * @param testNetwork 
	 */
	addTestNetworkMapping(remote: SRMRemoteNetwork, testNetwork: SRMRemoteNetwork): void;
	/**
	 * Remove folder mapping.
	 * @param local 
	 */
	removeFolderMapping(local: SRMLocalDatacenter): void;
	/**
	 * Remove network mapping.
	 * @param local 
	 */
	removeNetworkMapping(local: SRMLocalNetwork): void;
	/**
	 * Remove test network mapping.
	 * @param remote 
	 */
	removeTestNetworkMapping(remote: SRMRemoteNetwork): void;
	/** Returns all NetworkMappings in this Site */
	getNetworkMappings(): SRMLocalNetwork[];
	/** Returns all TestNetworkMappings in this Site */
	getTestNetworkMappings(): SRMRemoteNetwork[];
	/** Returns all ResourcePoolMappings in this Site */
	getResourcePoolMappings(): SRMLocalResourcePool[];
	/** Return local Datacenters in this Site */
	getLocalDatacenters(): SRMScriptingObject[];
	/** Return remote Datacenters in this Site */
	getRemoteDatacenters(): SRMScriptingObject[];
	/** Return local Networks in this Site */
	getLocalNetworks(): SRMScriptingObject[];
	/** Return remote Networks in this Site */
	getRemoteNetworks(): SRMScriptingObject[];
	/** Return local Resource Pools in this Site */
	getLocalResourcePools(): SRMScriptingObject[];
	/** Return remote Resource Pools in this Site */
	getRemoteResourcePools(): SRMScriptingObject[];
	/** Get all Placeholder Datastores */
	getPlaceholderDatastores(): SRMScriptingObject[];
	/** Discover Replicated Devices */
	discoverDevices(): SRMScriptingObjectTask;
	/** URL of the remote lookup service */
	getRemoteLsUrl(): string;
	/** URL of the remote VC */
	getRemoteVcUrl(): string;
	/**
	 * Create resource mapping.
	 * @param local 
	 * @param remote 
	 */
	addResourceMapping(local: SRMLocalResourcePool, remote: SRMRemoteResourcePool): void;
	/**
	 * Remove resource mapping.
	 * @param local 
	 */
	removeResourceMapping(local: SRMLocalResourcePool): void;
	/** Returns all ProtectionGroups in this Site */
	getProtectionGroups(): SRMProtectionGroup[];
	/** Returns all RecoveryPlans in this Site */
	getRecoveryPlans(): SRMRecoveryPlan[];
	/** Returns all NetworkMapping pairs in this Site */
	getNetworkMappingPairs(): any[];
	/** Returns all TestNetworkMapping pairs in this Site */
	getTestNetworkMappingPairs(): any[];
	/** Returns all ResourcePoolMapping pairs in this Site */
	getResourcePoolMappingPairs(): any[];
	/** Returns all DatacenterMappings in this Site */
	getDatacenterMappings(): SRMLocalDatacenter[];
	/** Returns all DatacenterMapping pairs in this Site */
	getDatacenterMappingPairs(): any[];
	/** Return protection root folder in this Site */
	getProtectionRootFolder(): SRMProtectionFolder;
	/** Return recovery root folder in this Site */
	getRecoveryRootFolder(): SRMRecoveryFolder;
	/**
	 * Add IP customization.
	 * @param local 
	 * @param ipCustomizationSpec 
	 */
	addIpCustomization(local: SRMLocalNetwork, ipCustomizationSpec: SRMNetworkIPCustomizationSpec): void;
	/**
	 * Remove IP customization.
	 * @param local 
	 */
	removeIpCustomization(local: SRMLocalNetwork): void;
	/**
	 * Assigns the supplied datastores as Placeholder Datastores
	 * @param datastores 
	 */
	addPlaceHolderDatastores(datastores: any): void;
	/** Get all datastores suitable for Placeholder Datastores */
	getUnreplicatedDatastores(): SRMScriptingObject[];
	/**
	 * Removes the supplied datastores from the placeholder datastores pool
	 * @param datastores 
	 */
	removePlaceHolderDatastores(datastores: any): void;
}

/**
 * Represents Unassigned Replicated Datastore.
 */
declare class SRMUnassignedReplicatedDatastore {
	/** Display name of this Object */
	name: string;
	/** Extension of the SRM server */
	deploymentId: string;

	constructor();
}

/**
 * Represents Unassigned Replicated Vm.
 */
declare class SRMUnassignedReplicatedVm {
	/** Display name of this Object */
	name: string;
	/** Extension of the SRM server */
	deploymentId: string;

	constructor();
}

/**
 * Represents an Unreplicated Datastore.
 */
declare class SRMUnreplicatedDatastore {
	/** Display name of this Object */
	name: string;
	/** Extension of the SRM server */
	deploymentId: string;

	constructor();
}

/**
 * Represents final power state for this VirtualMachine after recovery.
 */
declare class SRMVirtualMachinePowerState {
	/** Display name of this Object */
	name: string;

	private constructor();

	public static get(id: string): SRMVirtualMachinePowerState;
}

/**
 * Contains all data, needed to perform IP customization for a virtual machine
 */
declare class SRMVmIpCustomizationSpec {
	/** Display name of this Object */
	name: string;
	/** IP customization mode */
	ipCustomizationMode: string;
	/** IP customization data for protection site */
	protNic: SRMNicSpec[];
	/** IP customization data for recovery site */
	recNic: SRMNicSpec[];
	/** If VM OS is windows or not */
	isWindowsOs: boolean;

	constructor();
}

/**
 * Contains the common logic for all scripting objects.
 */
declare interface SRMScriptingObject {
	/** Display name of this Object */
	name: string;
}
