// vSphere Replication plugin

/**
 * Represents the connection settings for the HMS plugin.
 */
declare class VRConfigurationSettings {
	// Display name of this Object.
	name: string
	maxConnections: number;
	sessionTimeout: number;
	connectTimeout: number;

	constructor();
}

/**
 * DiskType.
 */
declare interface VRDiskType {
	// Display name of this Object.
	name: string;
}

declare class VRFactoryLifecycleManager {
	// Display name of this Object.
	name: string

	constructor();
}

/**
 * Handles plugin connection configuration.
 */
declare class VRPluginConfig {
	// Display name of this Object.
	name: string

	constructor();

	public static getSites(): VRSite[];
	/**
	 * 
	 * @param remoteSite 
	 * @param username 
	 * @param password 
	 */
	public static registerRemoteVcSite(remoteSite: VRVcRemoteSite, username: string, password: string): void;
	/**
	 * 
	 * @param remoteSite 
	 */
	public static unregisterRemoteVcSite(remoteSite: VRVcRemoteSite): void;
	/**
	 * 
	 * @param lsAddress 
	 */
	public static listVCsForRemoteLS(lsAddress: string): VRVcEndpoint[];
	public static getSupportedDiskFormats(): VRDiskType[];
	public static refreshCache(): void;
	/**
	 * Retrieves the current configuration settings.
	 */
	public static getConfigurationSettings(): VRConfigurationSettings;
	/**
	 * Saves the new configuration settings.
	 * 
	 * @param newConfigurationSettings 
	 */
	public static saveConfigurationSettings(newConfigurationSettings: VRConfigurationSettings): void;
}

/**
 * Represents Remote Datastore.
 */
declare interface VRRemoteDatastore {
	// Display name of this Object.
	name: string;
	// Path to this Scripting Object.
	path: string;
	// Type of this Scripting Object.
	morType: string;
	// VC of this Scripting Object.
	vcUriHost: string;
}

/**
 * Represents Replication Issue details.
 */
declare interface VRReplicationIssue {
	// Display name of this Object.
	name: string;

	getSeverity(): string;
	getFault(): any;
	/**
	 * 
	 * @param fault
	 */
	setFault(fault: any): void;
	/**
	 * 
	 * @param severity 
	 */
	setSeverity(severity: string): void;
	/**
	 * 
	 * @param targetObject
	 */
	setTargetObject(targetObject: any): void;
	/**
	 * 
	 * @param targetObjectName 
	 */
	setTargetObjectName(targetObjectName: string): void;
	/**
	 * 
	 * @param triggeredTime 
	 */
	setTriggeredTime(triggeredTime: any): void;
	getIssueType(): string;
	/**
	 * 
	 * @param issueType 
	 */
	setIssueType(issueType: string): void;
	/**
	 * 
	 * @param sourceSiteUuid 
	 */
	setSourceSiteUuid(sourceSiteUuid: string): void;
	/**
	 * 
	 * @param destinationSiteUuid 
	 */
	setDestinationSiteUuid(destinationSiteUuid: string): void;
	getTargetObject(): any;
	getTargetObjectName(): string;
	getTriggeredTime(): any;
	getSourceSiteUuid(): string;
	getDestinationSiteUuid(): string;
}



/**
 * Replication settings.
 */
declare class VRReplicationSettings {
	// Display name of this Object.
	name: string;
	newDisksDatastoreId: string;
	remoteDatastore: VRRemoteDatastore;
	rpo: number;
	quiesceGuestEnabled: boolean;
	networkCompressionEnabled: boolean;
	encryptionEnabled: boolean;
	perDiskEnabled: boolean
	includedDisks: VRVmDisk[];
	// Whether to retrieve and include all existing disks. Ignores existingDisks property.
	includeAllDisks: boolean;
	// Whether to use existing disk seeds or create a new folder for replicated disk.
	useDefaultSeed: boolean;
	excludedDisks: VRVmDisk[];
	retentionPolicyTiers: VRRetentionPolicyTier[];
	diskType: VRDiskType;
	diskTypes: VRDiskType[];
	storageProfile: VRStorageProfile;
	storageProfiles: VRStorageProfile[];
	remoteDatastores: VRRemoteDatastore[];
	autoReplicateNewDisks: boolean;
	dataSetsReplicationEnabled: boolean;

	constructor();

	public isDataSetsReplicationEnabled(): boolean;
	/**
	 * 
	 * @param dataSetsReplicationEnabled 
	 */
	public setDataSetsReplicationEnabled(dataSetsReplicationEnabled: boolean): void;
	public getDiskType(): VRDiskType;
	/**
	 * 
	 * @param diskType 
	 */
	public setDiskType(diskType: VRDiskType): void;
	public getStorageProfile(): VRStorageProfile;
	/**
	 * 
	 * @param storageProfile 
	 */
	public setStorageProfile(storageProfile: VRStorageProfile): void;
	/**
	 * 
	 * @param replicationRef 
	 */
	public setReplicationRef(replicationRef: any): void;
	public getReplicationRef(): any;
	public getIncludedDisks(): VRVmDisk[];
	/**
	 * 
	 * @param includedDisks 
	 */
	public setIncludedDisks(includedDisks: VRVmDisk[]): void;
	public getExcludedDisks(): VRVmDisk[];
	/**
	 * 
	 * @param includedDisks 
	 */
	public setExcludedDisks(includedDisks: VRVmDisk[]): void;
	public isUseDefaultSeed(): boolean;
	/**
	 * 
	 * @param useDefaultSeed 
	 */
	public setUseDefaultSeed(useDefaultSeed: boolean): void;
	public isEncryptionEnabled(): boolean;
	/**
	 * 
	 * @param encryptionEnabled 
	 */
	public setEncryptionEnabled(encryptionEnabled: boolean): void;
	public isMpitEnabled(): boolean;
	/**
	 * 
	 * @param mpitEnabled 
	 */
	public setMpitEnabled(mpitEnabled: boolean): void;
	public getMpitInstances(): number;
	/**
	 * 
	 * @param mpitInstances 
	 */
	public setMpitInstances(mpitInstances: number): void;
	public getMpitDays(): number;
	/**
	 * 
	 * @param mpitDays 
	 */
	public setMpitDays(mpitDays: number): void;
	public getNewDisksDatastoreId(): string;
	/**
	 * 
	 * @param newDisksDatastoreId 
	 */
	public setNewDisksDatastoreId(newDisksDatastoreId: string): void;
	public isPerDiskEnabled(): boolean;
	/**
	 * 
	 * @param perDiskEnabled 
	 */
	public setPerDiskEnabled(perDiskEnabled: boolean): void;
	public getDiskTypes(): VRDiskType[];
	/**
	 * 
	 * @param diskTypes 
	 */
	public setDiskTypes(diskTypes: VRDiskType[]): void;
	public getStorageProfiles(): VRStorageProfile[];
	/**
	 * 
	 * @param storageProfiles 
	 */
	public setStorageProfiles(storageProfiles: VRStorageProfile[]): void;
	public getRemoteDatastores(): VRRemoteDatastore[];
	/**
	 * 
	 * @param remoteDatastores 
	 */
	public setRemoteDatastores(remoteDatastores: VRRemoteDatastore[]): void;
	public getRemoteDatastore(): VRRemoteDatastore;
	/**
	 * 
	 * @param remoteDatastore 
	 */
	public setRemoteDatastore(remoteDatastore: VRRemoteDatastore): void;
	public isAutoReplicateNewDisks(): boolean;
	/**
	 * 
	 * @param autoReplicateNewDisks 
	 */
	public setAutoReplicateNewDisks(autoReplicateNewDisks: boolean): void;
}

/**
 * Rettention policy for the replication.
 */
declare class VRRetentionPolicyTier {
	granularityMinutes: number;
	numSlots: number;

	constructor();
}

/**
 * Represents HMS Site object.
 */
declare interface VRSite {
	// Display name of this Object.
	name: string;
	vcInstanceUuid: string;

	getVcRemoteSites(): VRVcRemoteSite[];
	/**
	 * 
	 * @param vcUuid 
	 * @param username 
	 * @param password 
	 * @param remoteLsAddress 
	 * @param localLsAddress 
	 */
	pairWithVcRemoteSite(vcUuid: string, username: string, password: string, remoteLsAddress: string, localLsAddress: string): VRTask;
	/**
	 * 
	 * @param pairVcUuid 
	 * @param username 
	 * @param password 
	 * @param remoteLsAddress 
	 * @param localLsAddress 
	 */
	reconnectVcToVcPair(pairVcUuid: string, username: string, password: string, remoteLsAddress: string, localLsAddress: string): VRTask;
	/**
	 * 
	 * @param isOutgoing 
	 * @param remoteSite 
	 */
	getReplicationIssues(isOutgoing: boolean, remoteSite: VRVcRemoteSite): VRReplicationIssue[];
	/**
	 * 
	 * @param vmName 
	 */
	getReplicationIds(vmName: string): string[];
	/**
	 * Gets VirtualMachine disks.
	 * 
	 * @param vmId 
	 */
	getVmDisks(vmId: string): VRVmDisk[];
}

/**
 * StorageProfile.
 */
declare interface VRStorageProfile {
	// Display name of this Object.
	name: string;
}

/**
 * Represents HMS Task.
 */
declare class VRTask {
	// Display name of this Object.
	name: string;

	constructor();

	public getState(): string;
	public getProgress(): number;
	public getErrotMessage(): string;
}

/**
 * Service Endpoint description for a vCenter server.
 */
declare interface VRVcEndpoint {
	// Display name of this Object.
	name: string;
	uri: URI;
	// Lookup Service URI.
	lsUri: URI;
	uuid: string;
	sslTrust: string[];
}

/**
 * Represents VSphere Remote Site object.
 */
declare interface VRVcRemoteSite {
	// Display name of this Object.
	name: string;
	// URI of the remote VC server.
	uri: URI;
	// URI of the remote Lookup Service.
	lsUri: URI;
	// URI of the local VC server.
	localSiteUri: URI;

	/**
	 * Retrieve datastores at this site.
	 */
	getDatastores(): VRRemoteDatastore[];
	/**
	 * Retrieve storage profiles for this site.
	 */
	getStorageProfiles(): VRStorageProfile[];
	/**
	 * Login remote Spbm server.
	 */
	loginSpbmServer(): void;
	/**
	 * Login remote VSphere site.
	 */
	loginRemoteSite(): void;
	/**
	 * Login remote vSphere site with user name and password.
	 * 
	 * @param userName 
	 * @param password 
	 */
	loginRemoteSiteWithCredentials(userName: string, password: string): void;
	/**
	 * Logout remote VC Site.
	 */
	logoutRemoteSite(): void;
	/**
	 * Retrieve replication groups targeting this site.
	 */
	getReplicationsTo(): VRVcToVcSourceGroup[];
	/**
	 * Retrieve replication groups originating from this site.
	 */
	getReplicationsFrom(): VRVcToVcTargetGroup[];
	/**
	 * Retrieve datastores at this site which support the provided storage profile.
	 * 
	 * @param storageProfile 
	 */
	getDatastoresFilteredByProfile(storageProfile: VRStorageProfile): VRRemoteDatastore[];
	/**
	 * Create a new host based replication group.
	 * 
	 * @param vmId 
	 * @param settings 
	 */
	configureReplicationTo(vmId: string, settings: VRReplicationSettings): VRTask;
	/**
	 * Get the group created as a result of executing the task.
	 * 
	 * @param task 
	 */
	getConfigureReplicationTaskResult(task: VRTask): VRVcToVcSourceGroup;
	/**
	 * Collects all replication data for given vm.
	 * 
	 * @param vmId 
	 */
	getVMReplicationData(vmId: string): VRVrReplicationDetailsData;
	/**
	 * Gets VirtualMachine replication group.
	 * 
	 * @param vmId 
	 */
	getVMGroup(vmId: string): any;
}

/**
 * Represents VSphere Replication Group object.
 */
declare interface VRVcToVcSourceGroup {
	// Display name of this Object.
	name: string;
	readonly rpo: number;

	/**
	 * Stop replication.
	 * 
	 * @param leaveReplicaDisks 
	 */
	unconfigure(leaveReplicaDisks: boolean): VRTask;
	/**
	 * Retrieve datastores at this site.
	 * 
	 * @param storageProfile 
	 */
	getDatastores(storageProfile: VRStorageProfile): VRRemoteDatastore[];
	/**
	 * Reconfigures a replication group.
	 * 
	 * @param settings 
	 */
	reConfigureReplicationTo(settings: VRReplicationSettings): VRTask;
	/**
	 * Collects all data for given replication.
	 */
	getReplicationData(): VRVrReplicationDetailsData;
	/**
	 * Retrieve VM home datastore.
	 */
	getTargetDatastoreVmHome(): VRRemoteDatastore;
	/**
	 * Retrieve the common disk type.
	 */
	getCommonDiskType(): VRDiskType;
	/**
	 * Retrieve the common storage profile..
	 */
	getCommonStorageProfile(): VRStorageProfile;
	/**
	 * Retrieve the recovery solution of this replication..
	 * 
	 * @param userName 
	 * @param password 
	 */
	getRecoverySolution(userName: string, password: string): string;
	/**
	 * 
	 * @param userName 
	 * @param password 
	 */
	getReplicationTestBubbleStatus(userName: string, password: string): string;
	/**
	 * Get the group created as a result of executing the task.
	 * 
	 * @param task 
	 */
	getConfigureReplicationTaskResult(task: VRTask): VRVcToVcSourceGroup;
	/**
	 * Resume replication.
	 */
	resume(): VRTask;
	/**
	 * Get replication status.
	 */
	getStatus(): string;
	/**
	 * Full sync.
	 */
	fullSync(): VRTask;
	/**
	 * Pause replication.
	 */
	pause(): VRTask;
	/**
	 * Get replication error.
	 */
	getReplicationError(): string;
	/**
	 * Get recovery state.
	 */
	getRecoveryState(): string;
	/**
	 * Get recovery error.
	 */
	getRecoveryError(): string;
	/**
	 * Get test recovery state.
	 */
	getTestRecoveryState(): string;
	/**
	 * Get test recovery error.
	 */
	getTestRecoveryError(): string;
	/**
	 * Get current RPO violation in minutes.
	 */
	getCurrentRpoViolation(): number;
	/**
	 * Online sync.
	 */
	onlineSync(): VRTask;
	/**
	 * Offline sync.
	 */
	offlineSync(): VRTask;
}

/**
 * Represents VSphere Replication Group object.
 */
declare interface VRVcToVcTargetGroup {
	// Display name of this Object.
	name: string;
	readonly rpo: number;

	/**
	 * Resume replication.
	 */
	resume(): VRTask;
	/**
	 * Get replication status.
	 */
	getStatus(): string;
	/**
	 * Full sync.
	 */
	fullSync(): VRTask;
	/**
	 * Pause replication.
	 */
	pause(): VRTask;
	/**
	 * Get replication error.
	 */
	getReplicationError(): string;
	/**
	 * Get recovery state.
	 */
	getRecoveryState(): string;
	/**
	 * Get recovery error.
	 */
	getRecoveryError(): string;
	/**
	 * Get test recovery state.
	 */
	getTestRecoveryState(): string;
	/**
	 * Get test recovery error.
	 */
	getTestRecoveryError(): string;
	/**
	 * Get current RPO violation in minutes.
	 */
	getCurrentRpoViolation(): number;
	/**
	 * Online sync.
	 */
	onlineSync(): VRTask;
	/**
	 * Offline sync.
	 */
	offlineSync(): VRTask;
}

/**
 * Represents virtual machine replication disks.
 */
declare class VRVmDisk {
	// Display name of this Object.
	name: string;

	constructor();

	public getLabel(): string;
	/**
	 * 
	 * @param deviceKey 
	 */
	public setDeviceKey(deviceKey: number): void;
	public getDeviceKey(): number;
	public getCapacityInKB(): number;
	/**
	 * 
	 * @param capacityInKB 
	 */
	public setCapacityInKB(capacityInKB: number): void;
	/**
	 * 
	 * @param label 
	 */
	public setLabel(label: string): void;
	public getVmRef(): any;
	/**
	 * 
	 * @param vmRef 
	 */
	public setVmRef(vmRef: any): void;
	/**
	 * 
	 * @param destinationPath 
	 */
	public setDestinationPath(destinationPath: string): void;
	public getDestinationPath(): string;
	public isReplicated(): boolean;
	/**
	 * 
	 * @param replicated 
	 */
	public setReplicated(replicated: boolean): void;
	public isVmHome(): boolean;
	/**
	 * 
	 * @param vmHome 
	 */
	public setVmHome(vmHome: boolean): void;
	public getFoundSeeds(): string[];
	/**
	 * 
	 * @param foundSeeds 
	 */
	public setFoundSeeds(foundSeeds: string[]): void;
	public getSourceDiskFormat(): any;
	/**
	 * 
	 * @param sourceDiskFormat 
	 */
	public setSourceDiskFormat(sourceDiskFormat: any): void;
	public getSourceStoragePolicyName(): string;
	/**
	 * 
	 * @param sourceStoragePolicyName 
	 */
	public setSourceStoragePolicyName(sourceStoragePolicyName: string): void;
	public getSourceStoragePolicyId(): string;
	/**
	 * 
	 * @param sourceStoragePolicyId 
	 */
	public setSourceStoragePolicyId(sourceStoragePolicyId: string): void;
	public isEnabledForReplication(): boolean;
	/**
	 * 
	 * @param enabledForReplication 
	 */
	public setEnabledForReplication(enabledForReplication: boolean): string;
	public isUseSeed(): boolean;
	/**
	 * 
	 * @param useSeed 
	 */
	public setUseSeed(useSeed: boolean): void;
	public getDestinationStoragePolicyId(): string;
	/**
	 * 
	 * @param destinationStoragePolicyId 
	 */
	public setDestinationStoragePolicyId(destinationStoragePolicyId: string): void;
	public getDestinationStoragePolicyName(): string;
	/**
	 * 
	 * @param destinationStoragePolicyName 
	 */
	public setDestinationStoragePolicyName(destinationStoragePolicyName: string): void;
	public getDestinationDatastoreRef(): any;
	/**
	 * 
	 * @param destinationDatastoreRef 
	 */
	public setDestinationDatastoreRef(destinationDatastoreRef: any): void;
	public getDestinationDiskFormat(): any;
	/**
	 * 
	 * @param destinationDiskFormat 
	 */
	public setDestinationDiskFormat(destinationDiskFormat: any): void;
	public isSeedsAreConfigured(): boolean;
	/**
	 * 
	 * @param seedsAreConfigured 
	 */
	public setSeedsAreConfigured(seedsAreConfigured: boolean): void;
	public isNewDisk(): boolean;
	/**
	 * 
	 * @param newDisk 
	 */
	public setNewDisk(newDisk: boolean): void;
}

/**
 * Represents consolidated Replication information.
 */
declare class VRVrReplicationDetailsData {
	// Display name of this Object.
	name: string;

	constructor();

	/**
	 * Getter for VM ref.
	 */
	public getVmRef(): any;
	/**
	 * Setter for vmRef.
	 * 
	 * @param vmRef 
	 */
	public setVmRef(vmRef: any): void;
	public getLastSyncTime(): string;
	/**
	 * 
	 * @param _lastSyncTime 
	 */
	public setLastSyncTime(_lastSyncTime: string): void;
	/**
	 * Getter for isDataSetsReplicationEnabled.
	 */
	public isDataSetsReplicationEnabled(): boolean;
	/**
	 * 
	 * @param isDataSetsReplicationEnabled 
	 */
	public setDataSetsReplicationEnabled(isDataSetsReplicationEnabled: boolean): void;
	public getDatastores(): VRRemoteDatastore[];
	/**
	 * 
	 * @param _datastores 
	 */
	public setDatastores(_datastores: VRRemoteDatastore[]): void;
	public isNetworkCompressionEnabled(): boolean;
	/**
	 * Setter for replication ref.
	 * 
	 * @param replicationRef 
	 */
	public setReplicationRef(replicationRef: any): void;
	public getReplicationRef(): any;
	/**
	 * Setter for isNetworkCompressionEnabled.
	 * 
	 * @param networkCompressionEnabled 
	 */
	public setNetworkCompressionEnabled(networkCompressionEnabled: boolean): any;
	public isEncryptionEnabled(): boolean;
	/**
	 * Setter for encryptionEnabled.
	 * 
	 * @param encryptionEnabled 
	 */
	public setEncryptionEnabled(encryptionEnabled: boolean): void;
	/**
	 * Getter for isMpitEnabled.
	 */
	public isMpitEnabled(): boolean;
	/**
	 * Setter for isMpitEnabled.
	 * 
	 * @param mpitEnabled 
	 */
	public setMpitEnabled(mpitEnabled: boolean): void;
	public getMpitInstances(): number;
	/**
	 * Setter for mpitInstances.
	 * 
	 * @param mpitInstances 
	 */
	public setMpitInstances(mpitInstances: number): void;
	public getMpitDays(): number;
	/**
	 * Setter for mpitDays.
	 * 
	 * @param mpitDays 
	 */
	public setMpitDays(mpitDays: number): void;
	public getDiskTypes(): VRDiskType[];
	/**
	 * 
	 * @param _diskTypes 
	 */
	public setDiskTypes(_diskTypes: VRDiskType[]): void;
	public getStorageProfiles(): VRStorageProfile[];
	/**
	 * 
	 * @param _storageProfiles 
	 */
	public setStorageProfiles(_storageProfiles: VRStorageProfile[]): void;
	public isAutoReplicateNewDisks(): boolean;
	/**
	 * 
	 * @param _autoReplicateNewDisks 
	 */
	public setAutoReplicateNewDisks(_autoReplicateNewDisks: boolean): void;
	/**
	 * Getter for all the VM disks configured in the replication.
	 */
	public getVmDisks(): VRVmDisk[];
	/**
	 * 
	 * @param targetHmsRef 
	 */
	public setTargetHmsRef(targetHmsRef: any): void;
	/**
	 * 
	 * @param hbrServerUuid 
	 */
	public setHbrServerUuid(hbrServerUuid: string): void;
	/**
	 * 
	 * @param hbrInternallyManaged 
	 */
	public setHbrInternallyManaged(hbrInternallyManaged: boolean): string
	/**
	 * Setter for all the VM disks configured in the replication.
	 * 
	 * @param vmDisks 
	 */
	public setVmDisks(vmDisks: VRVmDisk[]): void;
	/**
	 * Setter for all VM disks taken from the VC.
	 * 
	 * @param originalVmDisks 
	 */
	public setOriginalVmDisks(originalVmDisks: VRVmDisk[]): void;
	/**
	 * Getter for all VM disks taken from the VC.
	 */
	public getOriginalVmDisks(): VRVmDisk[];
	/**
	 * Getter for target VC ref.
	 */
	public getTargetVcRef(): any;
	/**
	 * Setter for target VC ref.
	 * 
	 * @param targetVcRef 
	 */
	public setTargetVcRef(targetVcRef: any): void;
	/**
	 * Setter for HBR server name.
	 * 
	 * @param hbrServerName 
	 */
	public setHbrServerName(hbrServerName: string): void;
	/**
	 * Setter for configuration error.
	 * 
	 * @param configurationError 
	 */
	public setConfigurationError(configurationError: any): void;
	/**
	 * Setter for last group error.
	 * 
	 * @param lastGroupError 
	 */
	public setLastGroupError(lastGroupError: any): void;
	/**
	 * Getter for Target Site Name.
	 */
	public getTargetSiteName(): string;
	/**
	 * Setter for Target Site Name.
	 * 
	 * @param targetSiteName 
	 */
	public setTargetSiteName(targetSiteName: string): void;
	/**
	 * Setter for RPO.
	 * 
	 * @param RPO 
	 */
	public setRPO(RPO: number): void;
	public getRPO(): number;
	/**
	 * Setter for Quiescing Enabled.
	 * 
	 * @param quiescingEnabled 
	 */
	public setQuiescingEnabled(quiescingEnabled: boolean): void;
	public isQuiescingEnabled(): boolean;
	/**
	 * Setter for RecoveryError.
	 * 
	 * @param recoveryError 
	 */
	public setRecoveryError(recoveryError: string): void;
	public isPerDisk(): boolean;
	/**
	 * 
	 * @param _perDisk 
	 */
	public setPerDisk(_perDisk: boolean): void;
	public getLastSyncSize(): number;
	/**
	 * 
	 * @param _lastSyncSize 
	 */
	public setLastSyncSize(_lastSyncSize: number): void;
	public getLastSyncDuration(): number;
	/**
	 * 
	 * @param _lastSyncDuration 
	 */
	public setLastSyncDuration(_lastSyncDuration: number): void;
	public getLastSyncDate(): any;
	/**
	 * 
	 * @param _lastSyncDate 
	 */
	public setLastSyncDate(_lastSyncDate: any): void;
	/**
	 * 
	 * @param rpoViolationMinutes 
	 */
	public setRpoViolationMinutes(rpoViolationMinutes: number): void;
	public getRpoViolationMinutes(): number;
}

declare interface URI {
	scheme: string;
	"scheme-specific-part": string;
	authority: string;
	"user-info": string;
	host: string;
	port: number;
	path: string;
	query: string;
	fragment: string;
}
