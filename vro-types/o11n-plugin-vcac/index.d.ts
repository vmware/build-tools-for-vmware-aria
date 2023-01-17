/**
 * vCAC Entity
 */
declare interface VCACEntity {
	hostId: string;
	keyString: string;
	entityKey: Properties;
	entitySetName: string;
	modelName: string;
	/**
	 * @param propertyName 
	 */
	getProperty(propertyName: string): any;
	getProperties(): Properties;
	/**
	 * @param host 
	 */
	getLinks(host: vCACHost): Properties;
	/**
	 * @param host 
	 * @param linkName 
	 */
	getLink(host: vCACHost, linkName: string): VCACEntity[];
	getInventoryObject(): any;
}

/**
 * Provides reading of DO model entities. All methods use implicitely a cache to store previous calls(method inputs are the key and the result is cached). A cached object expires by default in 10 min or can be explicitely evicted by calling the corresponding evictCached* with the same input parameters the read call was made. The total number of cached objects is 200.
 */
declare class vCACCachingEntityManager {
	/**
	 * @param hostId 
	 * @param modelName 
	 * @param entitySetName 
	 * @param entityId 
	 * @param headers 
	 */
	static evictCachedModelEntity(hostId: string, modelName: string, entitySetName: string, entityId: any, headers: Properties): void;
	/**
	 * Reads a vCAC model entities with option to filter them by thier propeties' values.The response is cached and if it is read again within 10 min with exactly the same input pareters it will be retrieved from a cache.
	 * @param hostId 
	 * @param modelName 
	 * @param entitySetName 
	 * @param properties 
	 * @param headers 
	 */
	static readModelEntitiesByCustomFilter(hostId: string, modelName: string, entitySetName: string, properties: any, headers: Properties): VCACEntity[];
	/**
	 * @param hostId 
	 * @param modelName 
	 * @param entitySetName 
	 * @param properties 
	 * @param headers 
	 */
	static evictCachedModelEntitiesByCustomFilter(hostId: string, modelName: string, entitySetName: string, properties: any, headers: Properties): void;
	/**
	 * Reads a vCAC model entitits by Odata system query.The response is cached and if it is read again within 10 min with exactly the same input paretersit will be retrieved from a cache.
	 * @param hostId 
	 * @param modelName 
	 * @param entitySetName 
	 * @param filter 
	 * @param orderBy 
	 * @param select 
	 * @param top 
	 * @param skip 
	 * @param headers 
	 */
	static readModelEntitiesBySystemQuery(hostId: string, modelName: string, entitySetName: string, filter: string, orderBy: string, select: string, top: number, skip: number, headers: Properties): VCACEntity[];
	/**
	 * @param hostId 
	 * @param modelName 
	 * @param entitySetName 
	 * @param filter 
	 * @param orderBy 
	 * @param select 
	 * @param top 
	 * @param skip 
	 * @param headers 
	 */
	static evictCachedModelEntitiesBySystemQuery(hostId: string, modelName: string, entitySetName: string, filter: string, orderBy: string, select: string, top: number, skip: number, headers: Properties): void;
	/**
	 * Reads a vCAC model entity. The response is cached and if it is read again within 10 min with exactly the same input pareters it will be retrieved from a cache.
	 * @param hostId 
	 * @param modelName 
	 * @param entitySetName 
	 * @param entityId 
	 * @param headers 
	 */
	static readModelEntity(hostId: string, modelName: string, entitySetName: string, entityId: any, headers: Properties): VCACEntity;
}

/**
 * Provides the core set of functions for creating, updating, and deleting DO model entities.
 */
declare class vCACEntityManager {
	/**
	 * Deletes a vCAC model entity
	 * @param hostId 
	 * @param modelName 
	 * @param entitySetName 
	 * @param entityKey 
	 * @param headers 
	 */
	static deleteModelEntityBySerializedKey(hostId: string, modelName: string, entitySetName: string, entityKey: string, headers: Properties): void;
	/**
	 * Reads a vCAC model entity by filter using expand paramer.
	 * @param hostId 
	 * @param modelName 
	 * @param entitySetName 
	 * @param filter 
	 * @param expand 
	 * @param orderBy 
	 * @param select 
	 * @param top 
	 * @param skip 
	 * @param headers 
	 */
	static readModelEntitiesBySystemExpandQuery(hostId: string, modelName: string, entitySetName: string, filter: string, expand: string, orderBy: string, select: string, top: number, skip: number, headers: Properties): VCACEntity[];
	/**
	 * Updates a vCAC model entity
	 * @param hostId 
	 * @param modelName 
	 * @param entitySetName 
	 * @param entityId 
	 * @param parameters 
	 * @param links 
	 * @param headers 
	 */
	static updateModelEntityBySerializedKey(hostId: string, modelName: string, entitySetName: string, entityId: string, parameters: any, links: any, headers: Properties): VCACEntity;
	/**
	 * Updates a vCAC model entity
	 * @param hostId 
	 * @param modelName 
	 * @param entitySetName 
	 * @param entityId 
	 * @param parameters 
	 * @param links 
	 * @param headers 
	 */
	static updateModelEntity(hostId: string, modelName: string, entitySetName: string, entityId: any, parameters: any, links: any, headers: Properties): VCACEntity;
	/**
	 * Calculates the hash of a Mime Attachment
	 * @param input 
	 */
	static calculateMD5Hash(input: any): any;
	/**
	 * @param hostId 
	 * @param modelName 
	 * @param entitySetName 
	 * @param properties 
	 * @param headers 
	 */
	static readModelEntitiesByCustomFilter(hostId: string, modelName: string, entitySetName: string, properties: any, headers: Properties): VCACEntity[];
	/**
	 * @param hostId 
	 * @param modelName 
	 * @param entitySetName 
	 * @param filter 
	 * @param orderBy 
	 * @param select 
	 * @param top 
	 * @param skip 
	 * @param headers 
	 */
	static readModelEntitiesBySystemQuery(hostId: string, modelName: string, entitySetName: string, filter: string, orderBy: string, select: string, top: number, skip: number, headers: Properties): VCACEntity[];
	/**
	 * Creates and opens OData batch session to the Model Manager.
	 * @param vcacHost 
	 * @param modelName 
	 * @param headers 
	 */
	static createEntityBatchSession(vcacHost: vCACHost, modelName: string, headers: Properties): vCACEntityBatchSession;
	/**
	 * Creates a vCAC model entity
	 * @param hostId 
	 * @param modelName 
	 * @param entitySetName 
	 * @param parameters 
	 * @param links 
	 * @param headers 
	 */
	static createModelEntity(hostId: string, modelName: string, entitySetName: string, parameters: any, links: any, headers: Properties): VCACEntity;
	/**
	 * Deletes related entity links between two entities by navigation property.
	 * @param hostId 
	 * @param modelName 
	 * @param entitySetName 
	 * @param sourceEntity 
	 * @param targetNavProperty 
	 * @param serializedKeyString 
	 * @param headers 
	 */
	static deleteLink(hostId: string, modelName: string, entitySetName: string, sourceEntity: VCACEntity, targetNavProperty: string, serializedKeyString: string, headers: Properties): void;
	/**
	 * Reads a vCAC model entity
	 * @param hostId 
	 * @param modelName 
	 * @param entitySetName 
	 * @param entityId 
	 * @param headers 
	 */
	static readModelEntity(hostId: string, modelName: string, entitySetName: string, entityId: any, headers: Properties): VCACEntity;
}

/**
 * Manager that enables vCAC host creation and configuration reload.
 */
declare class vCACVcacHostManager {
	/**
	 * Creates a new vCAC Host.
	 * @param props 
	 */
	static addVcacHost(props: any): vCACHost;
	/**
	 * Updates a new vCAC Host.
	 * @param host 
	 * @param props 
	 */
	static updateVcacHost(host: vCACHost, props: any): vCACHost;
	/**
	 * Removes a new vCAC Host.
	 * @param host 
	 */
	static removeVcacHost(host: vCACHost): void;
	/**
	 * Retrieves the Data Model Manager host by looking up in the vCAC component registry.If vCO is not configured with Component registry authentication throws an error.
	 */
	static retrieveIaasRepositoryFromComponentRegistry(): string;
}

/**
 * Represent OData batch session. All entity modifications within a single session will be executed in a single OData request.
 */
declare interface vCACEntityBatchSession {
	/**
	 * @param dataEntity 
	 */
	deleteEntity(dataEntity: VCACEntity): void;
	/**
	 * @param entitySetName 
	 * @param entityId 
	 * @param parameters 
	 * @param links 
	 */
	updateEntity(entitySetName: string, entityId: any, parameters: any, links: any): void;
	/**
	 * @param entitySetName 
	 * @param entityKey 
	 */
	deleteEntityByKey(entitySetName: string, entityKey: string): void;
	/**
	 * @param entitySetName 
	 * @param parameters 
	 * @param links 
	 */
	createEntity(entitySetName: string, parameters: any, links: any): void;
	/**
	 * @param entitySetName 
	 * @param entityKey 
	 * @param parameters 
	 * @param links 
	 */
	updateEntityByKey(entitySetName: string, entityKey: string, parameters: any, links: any): void;
	/**
	 * @param dataEntities 
	 */
	deleteEntities(dataEntities: VCACEntity[]): void;
}

/**
 * vCAC Host Machine
 */
declare interface vCACHostMachine {
	hostId: string;
	physicalLocationId: string;
	hostName: string;
	hostComments: string;
	hostOSType: string;
	hostManufacturer: string;
	hostModel: string;
	hostUniqueId: string;
	isCluster: boolean;
	hostProcessors: number;
	hostProcessorSpeed: number;
	hostProcessorType: string;
	recCreationTime: any;
	recDeleteTime: any;
	recUpdateTime: any;
	flags: number;
	text1: string;
	text2: string;
	hostStateID: any;
	hostTotalMemoryMB: number;
	hostTotalStorageGB: number;
	hostUsedMemoryMB: number;
	hostUsedStorageGB: number;
	isVRMManaged: boolean;
	hostDNSName: string;
	machineType: any;
	readonly displayName: string;
	getEntity(): VCACEntity;
}

/**
 * vCAC Machine Prefix
 */
declare interface vCACMachinePrefix {
	hostnamePrefixId: string;
	machinePrefix: string;
	nextMachineNo: number;
	machineNumberLength: number;
	readonly displayName: string;
	getEntity(): VCACEntity;
}

/**
 * vCAC Management Endpoint
 */
declare interface vCACManagementEndpoint {
	managementEndpointID: string;
	managementUri: string;
	managementEndpointName: string;
	managementEndpointDescription: string;
	interfaceType: string;
	externalReferenceId: string;
	readonly displayName: string;
	readonly domain: string;
	readonly credentialName: string;
	readonly credentialDescription: string;
	readonly credentialId: string;
	readonly password: string;
	readonly userName: string;
	/**
	 * @param sdkHost 
	 */
	isSame(sdkHost: string): boolean;
	getEntity(): VCACEntity;
}

/**
 * vCAC ProvisioningGroup
 */
declare interface vCACProvisioningGroup {
	groupID: string;
	isTestGroup: boolean;
	groupName: string;
	groupDescription: string;
	administratorEmail: string;
	adContainer: string;
	groupType: any;
	displayName: string;
	getVirtualMachineTemplates(): any[];
	/**
	 * @param virtualMachineTemplates 
	 */
	setVirtualMachineTemplates(virtualMachineTemplates: any[]): void;
	getEntity(): VCACEntity;
}

/**
 * vCAC Reservation
 */
declare interface vCACReservation {
	hostReservationID: string;
	hostReservationName: string;
	enabled: boolean;
	reservationMemorySizeMB: number;
	reservationStorageSizeGB: number;
	maxVMsPowerOn: number;
	maxVMsCreate: number;
	flags: number;
	text1: string;
	text2: string;
	reservationPriority: number;
	storageAllocationPolicyID: any;
	currentStorageAllocationIndex: number;
	currentNetworkAllocationIndex: number;
	id: string;
	isFileLevelCloningEnabled: boolean;
	machineType: any;
	readonly displayName: string;
	getEntity(): VCACEntity;
}

/**
 * vCAC Host
 */
declare interface vCACHost {
	readonly url: string;
	readonly name: string;
	readonly id: string;
	readonly displayName: string;
	readonly proxyHost: string;
	readonly proxyUrl: string;
	readonly acceptAllCertificates: boolean;
	readonly authenticationType: string;
	readonly proxyPort: number;
	readonly username: string;
	/**
	 * Removes this vCAC' host.
	 */
	remove(): void;
	/**
	 * Updates this vCAC' host properties.
	 * @param props 
	 */
	update(props: any): vCACHost;
	/**
	 * Validate connection for this vCAC' host
	 */
	validate(): void;
	/**
	 * Gets any of the host's authentication properties
	 * @param index 
	 */
	getAuthProperty(index: number): string;
	/**
	 * Receives a vm status asynchronously.
	 * @param virtualMachines 
	 * @param successStates 
	 * @param failStates 
	 * @param timeoutSeconds 
	 */
	receiveAsync(virtualMachines: VCACEntity[], successStates: string[], failStates: string[], timeoutSeconds: number): any;
	/**
	 * Extract the provisioning status from the trigger
	 * @param trigger 
	 */
	extractMessage(trigger: any): string;
	/**
	 * Finds and obtains all child provisioning groups of the current host
	 */
	findAllChildProvisioningGroups(): vCACProvisioningGroup[];
	/**
	 * Finds and obtains all endpoints of the current host
	 */
	findAllChildManagementEndpoints(): vCACManagementEndpoint[];
}

/**
 * vCAC VirtualMachine
 */
declare interface vCACVirtualMachine {
	virtualMachineID: string;
	virtualMachineName: string;
	expires: any;
	initiatorType: string;
	notes: string;
	guestOS: string;
	vmUniqueID: string;
	platformDetails: string;
	vmCreationDate: any;
	vmDeleteDate: any;
	lastLoggedDate: any;
	lastLoggedUser: string;
	lastPowerOffDate: any;
	lastPowerOnDate: any;
	ownerExists: boolean;
	usageIndex: number;
	usageIndexIgnoreBy: string;
	isDeleted: boolean;
	isMissing: boolean;
	isRogue: boolean;
	isRunning: boolean;
	recCreationTime: any;
	recDeleteTime: any;
	recUpdateTime: any;
	flags: number;
	text1: string;
	text2: string;
	vmCPUs: number;
	vmTotalMemoryMB: number;
	vmTotalStorageGB: number;
	guestOSFamily: string;
	virtualMachineState: string;
	currentTask: string;
	isTemplate: boolean;
	vmDNSName: string;
	vmUsedStorageGB: any;
	fileLevelCloneImageName: string;
	vmInitialUsedSpace: any;
	vmEstimatedUsedSpace: any;
	expireDays: number;
	storagePath: string;
	connectToVdi: boolean;
	blueprintType: any;
	machineType: any;
	isManaged: boolean;
	isComponent: boolean;
	externalReferenceId: string;
	virtualMachineTemplateID: string;
	readonly displayName: string;
	/**
	 * @param user 
	 * @param identityUser 
	 * @param templateId 
	 * @param hostReservationId 
	 * @param hostStorageReservationId 
	 * @param headers 
	 */
	register(user: string, identityUser: string, templateId: string, hostReservationId: string, hostStorageReservationId: string, headers: Properties): void;
	/**
	 * @param arguments 
	 * @param headers 
	 */
	registerVm(arguments: Properties, headers: Properties): void;
	/**
	 * Collect a list of the VM's available post-provisioning actions
	 */
	collectPostProvisiningActions(): string[];
	/**
	 * Invoke a DO Post-Provisioning Action
	 * @param actionName 
	 * @param properties 
	 * @param headers 
	 */
	postProvisionAction(actionName: string, properties: any, headers: Properties): void;
	getEntity(): VCACEntity;
}

declare interface vCACVirtualMachineProperty {
	readonly id: number;
	readonly propertyName: string;
	readonly propertyValue: string;
	readonly isHidden: boolean;
	readonly isRuntime: boolean;
	readonly isEncrypted: boolean;
}
