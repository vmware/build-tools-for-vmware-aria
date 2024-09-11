/**
 * VMware Aria Automation Host provides access to connection host properties & validation access.
 */
declare class VraHost {
	readonly displayName: string;
	readonly name: string;
	readonly cloudHost: string;
	readonly id: string;
	readonly vraHost: string;
	readonly sessionMode: string;
	readonly connectionType: string;
	readonly user: string;
	/**
	 * Automation Host No Argument Constructor.
	 */
	constructor();

	createInfrastructureClient(): VraInfrastructureClient;

	/**
	 * Validates the Host Connection.
	 */
	validate(): boolean;

	/**
	 * Destroys the connection object
	 */
	destroy(): void;

	/**
	 * Creates a generic REST client for Automation Host
	 */
	createRestClient(): VraGenericRestClient;
}

declare class VraInfrastructureClient {
	/**
	 * @param genericRestClient
	 */
	constructor(genericRestClient: VraGenericRestClient);

	/**
	 * Create Data Collector service to invoke APIs.
	 */
	createDataCollectorService(): VraDataCollectorService;

	/**
	 * Create a Request service to invoke APIs.
	 */
	createRequestService(): VraRequestService;

	/**
	 * Create Cloud Zone service to invoke APIs.
	 */
	createCloudZoneService(): VraCloudZoneService;

	/**
	 * Create a Cloud Account service to invoke APIs.
	 */
	createCloudAccountService(): VraCloudAccountService;
}

declare class VraCloudAccountService {
	constructor();

	/**
	 * @param genericRestClient
	 */
	setGenericRestClient(genericRestClient: VraGenericRestClient): void;

	/**
	 * Delete a vSphere Cloud Account with a given id
	 * @param cloudAccountVsphereId
	 */
	deleteVSphereCloudAccountById(cloudAccountVsphereId: string): void;

	/**
	 * Delete a vSphere Cloud Account with a given cloudAccountVsphere object
	 * @param cloudAccountVsphere
	 */
	deleteVSphereCloudAccount(
		cloudAccountVsphere: VraCloudAccountVsphere
	): void;

	/**
	 * Create a vSphere cloud account
	 * @param body
	 */
	createVSphereCloudAccount(
		body: VraCloudAccountVsphereSpecification
	): VraCloudAccountVsphere;

	/**
	 * Create a vSphere cloud account in an asynchronous manner
	 * @param body
	 */
	createVSphereCloudAccountAsync(
		body: VraCloudAccountVsphereSpecification
	): VraRequestTracker;

	/**
	 * Update vSphere cloud account in an asynchronous manner
	 * @param cloudAccountVsphere
	 * @param body
	 */
	updateVSphereCloudAccountAsync(
		cloudAccountVsphere: VraCloudAccountVsphere,
		body: VraUpdateCloudAccountVsphereSpecification
	): VraRequestTracker;

	/**
	 * Update vSphere cloud account
	 * @param cloudAccountVsphere
	 * @param body
	 */
	updateVSphereCloudAccount(
		cloudAccountVsphere: VraCloudAccountVsphere,
		body: VraUpdateCloudAccountVsphereSpecification
	): VraCloudAccountVsphere;
}

declare class VraUpdateCloudAccountVsphereSpecification {
	hostName: string;
	acceptSelfSignedCertificate: boolean;
	createDefaultZones: boolean;
	password: string;
	associatedCloudAccountIds: Object[];
	regions: Object[];
	dcid: string;
	name: string;
	description: string;
	username: string;
	tags: Object[];
	constructor();

	/**
	 * @param associatedCloudAccountIdsItem
	 */
	addAssociatedCloudAccountIdsItem(
		associatedCloudAccountIdsItem: string
	): VraUpdateCloudAccountVsphereSpecification;

	/**
	 * @param tagsItem
	 */
	addTagsItem(tagsItem: VraTag): VraUpdateCloudAccountVsphereSpecification;

	/**
	 * @param regionsItem
	 */
	addRegionsItem(
		regionsItem: VraRegionSpecification
	): VraUpdateCloudAccountVsphereSpecification;
}

/**
 * Specification for a vSphere cloud account.<br><br>A cloud account identifies a cloud account type and an account-specific deployment region or data center where the associated cloud account resources are hosted.
 */
declare class VraCloudAccountVsphereSpecification {
	hostName: string;
	acceptSelfSignedCertificate: boolean;
	createDefaultZones: boolean;
	password: string;
	associatedCloudAccountIds: Object[];
	regions: Object[];
	dcid: string;
	name: string;
	description: string;
	username: string;
	tags: Object[];
	constructor();

	/**
	 * @param regionsItem
	 */
	addRegionsItem(
		regionsItem: VraRegionSpecification
	): VraCloudAccountVsphereSpecification;

	/**
	 * @param associatedCloudAccountIdsItem
	 */
	addAssociatedCloudAccountIdsItem(
		associatedCloudAccountIdsItem: string
	): VraCloudAccountVsphereSpecification;

	/**
	 * @param tagsItem
	 */
	addTagsItem(tagsItem: VraTag): VraCloudAccountVsphereSpecification;
}

declare class VraRegionSpecification {
	externalRegionId: string;
	name: string;
	constructor();
}

declare class VraCloudAccountVsphere {
	owner: string;
	hostName: string;
	linksExtension: string;
	enabledRegionIdsExtension: string;
	description: string;
	orgId: string;
	tags: Object[];
	createdAt: string;
	enabledRegions: Object[];
	dcid: string;
	readonly internalIdString: string;
	name: string;
	host: VraHost;
	tagsExtension: string;
	id: string;
	updatedAt: string;
	username: string;
	customPropertiesExtension: string;
	constructor();

	/**
	 * @param key
	 * @param linksItem
	 */
	putLinksItem(key: string, linksItem: VraHref): VraCloudAccountVsphere;

	/**
	 * @param key
	 * @param customPropertiesItem
	 */
	putCustomPropertiesItem(
		key: string,
		customPropertiesItem: string
	): VraCloudAccountVsphere;
}

declare class VraCloudZoneService {
	constructor();

	/**
	 * @param genericRestClient
	 */
	setGenericRestClient(genericRestClient: VraGenericRestClient): void;

	/**
	 * This method deletes Zone based on given zoneId
	 * @param zoneId
	 */
	deleteZoneById(zoneId: string): void;

	/**
	 * This method deletes Zone based on given zone.
	 * @param zone
	 */
	deleteZone(zone: VraZone): void;

	/**
	 * This method updates the Zone based on zoneId & ZoneSpecification.
	 * @param id
	 * @param body
	 */
	updateZone(id: string, body: VraZoneSpecification): VraZone;

	/**
	 * This method creates Zone based on ZoneSpecification details.
	 * @param body
	 */
	createZone(body: VraZoneSpecification): VraZone;
}

declare class VraZone {
	owner: string;
	linksExtension: string;
	externalRegionId: string;
	cloudAccountId: string;
	description: string;
	orgId: string;
	tags: Object[];
	createdAt: string;
	folder: string;
	tagsToMatch: Object[];
	internalIdString: string;
	name: string;
	host: VraHost;
	placementPolicy: string;
	tagsExtension: string;
	tagsToMatchExtension: string;
	id: string;
	updatedAt: string;
	customPropertiesExtension: string;
	constructor();

	/**
	 * @param tagsToMatchItem
	 */
	addTagsToMatchItem(tagsToMatchItem: VraTag): VraZone;

	/**
	 * @param key
	 * @param linksItem
	 */
	putLinksItem(key: string, linksItem: VraHref): VraZone;

	/**
	 * @param tagsItem
	 */
	addTagsItem(tagsItem: VraTag): VraZone;

	/**
	 * @param key
	 * @param customPropertiesItem
	 */
	putCustomPropertiesItem(key: string, customPropertiesItem: string): VraZone;
}

declare class VraHref {
	hrefs: Object[];
	href: string;
	constructor();

	/**
	 * @param hrefsItem
	 */
	addHrefsItem(hrefsItem: string): VraHref;
}

/**
 * Specification for a zone.
 */
declare class VraZoneSpecification {
	folder: string;
	computeIds: Object[];
	tagsToMatch: Object[];
	regionId: string;
	name: string;
	placementPolicy: string;
	description: string;
	tags: Object[];
	constructor();

	/**
	 * @param tagsToMatchItem
	 */
	addTagsToMatchItem(tagsToMatchItem: VraTag): VraZoneSpecification;

	/**
	 * @param key
	 * @param customPropertiesItem
	 */
	putCustomPropertiesItem(
		key: string,
		customPropertiesItem: string
	): VraZoneSpecification;

	/**
	 * @param computeIdsItem
	 */
	addComputeIdsItem(computeIdsItem: string): VraZoneSpecification;

	/**
	 * @param tagsItem
	 */
	addTagsItem(tagsItem: VraTag): VraZoneSpecification;
}

declare class VraTag {
	readonly displayName: string;
	readonly internalIdString: string;
	host: VraHost;
	value: string;
	key: string;
	constructor();
}

declare class VraDataCollectorService {
	constructor();

	/**
	 * @param genericRestClient
	 */
	setGenericRestClient(genericRestClient: VraGenericRestClient): void;
}

declare class VraRequestService {
	constructor();

	/**
	 * @param genericRestClient
	 */
	setGenericRestClient(genericRestClient: VraGenericRestClient): void;

	/**
	 * Awaits successfully completed RequestTracker Response.
	 * @param requestTracker
	 */
	awaitRequestTrackerResponse(
		requestTracker: VraRequestTracker
	): VraRequestTracker;

	/**
	 * Delete a request tracker object for a given request id.
	 * @param requestTrackerId
	 */
	deleteRequestTracker(requestTrackerId: string): void;
}

declare class VraRequestTracker {
	deploymentId: string;
	readonly internalIdString: string;
	name: string;
	host: VraHost;
	resources: Object[];
	progress: number;
	id: string;
	requestTrackerStatus: string;
	message: string;
	selfLink: string;

	/**
	 * An object used to track long-running operations.
	 */
	constructor();

	/**
	 * @param resourcesItem
	 */
	addResourcesItem(resourcesItem: string): VraRequestTracker;
}

/**
 * A generic VMware Aria Automation Rest client for executing REST operations
 */
declare class VraGenericRestClient {
	host: VraHost;

	/**
	 * Automation GenericRestClient No Argument Constructor.
	 */
	constructor();

	/**
	 * Get Method to execute rest operation by setting Request object. Request object can hold information (http method, resource url, request payload)
	 * @param request
	 */
	get(request: VraRestRequest): VraRestResponse;

	/**
	 * Method to execute rest operation by setting Request object. Request object can hold information (http method, resource url, request payload)
	 * @param restRequest
	 */
	execute(restRequest: VraRestRequest): VraRestResponse;

	/**
	 * Put Method to execute rest operation by setting Request object. Request object can hold information (http method, resource url, request payload)
	 * @param request
	 */
	put(request: VraRestRequest): VraRestResponse;

	/**
	 * Delete Method (Http Delete) to execute rest operation by setting Request object. Request object can hold information (http method, resource url, request payload)
	 * @param request
	 */
	delete(request: VraRestRequest): VraRestResponse;

	/**
	 * Post Method to execute rest operation by setting Request object. Request object can hold information (http method, resource url, request payload)
	 * @param request
	 */
	post(request: VraRestRequest): VraRestResponse;

	/**
	 * Method to create HTTP rest Request. It holds parameter (HTTP Method (GET/PUT/POST/DELETE/PATCH), Resource Path URI, Request Payload (Stringified JSON)).
	 * @param method
	 * @param path
	 * @param requestPayload
	 */
	createRequest(
		method: string,
		path: string,
		requestPayload: string
	): VraRestRequest;

	/**
	 * Patch Method (Http Patch) to execute rest operation by setting Request object. Request object can hold information (http method, resource url, request payload)
	 * @param request
	 */
	patch(request: VraRestRequest): VraRestResponse;
}

declare class VraRestRequest {
	readonly path: string;
	readonly method: string;
	readonly payload: string;

	/**
	 * Sets Headers to the Http Request Object.
	 * @param key
	 * @param value
	 */
	setHeader(key: string, value: string): void;

	/**
	 * Gets Http Header value for the key from the Http Request.
	 * @param header
	 */
	getHeader(header: string): string;
}

declare class VraRestResponse {
	readonly contentAsString: string;
	readonly allHeaders: { [key: string]: string };
	readonly contentLength: number;
	readonly statusMessage: string;
	readonly statusCode: number;

	/**
	 * Retrieves the server's response header values per header with specific name.
	 * @param headerName
	 */
	getHeaderValues(headerName: string): string;
}

declare class VraBlockDevice {
	owner: string;
	cloudAccountIdsExtension: string;
	linksExtension: string;
	externalZoneId: string;
	externalRegionId: string;
	externalId: string;
	description: string;
	orgId: string;
	tags: Object[];
	capacityInGB: number;
	cloudAccountIds: Object[];
	createdAt: string;
	provisioningStatus: string;
	deploymentId: string;
	readonly internalIdString: string;
	name: string;
	host: VraHost;
	tagsExtension: string;
	blockDeviceStatus: string;
	id: string;
	persistent: boolean;
	projectId: string;
	updatedAt: string;
	customPropertiesExtension: string;
	constructor();

	/**
	 * @param key
	 * @param customPropertiesItem
	 */
	putCustomPropertiesItem(
		key: string,
		customPropertiesItem: string
	): VraBlockDevice;

	/**
	 * @param tagsItem
	 */
	addTagsItem(tagsItem: VraTag): VraBlockDevice;

	/**
	 * @param cloudAccountIdsItem
	 */
	addCloudAccountIdsItem(cloudAccountIdsItem: string): VraBlockDevice;

	/**
	 * @param key
	 * @param linksItem
	 */
	putLinksItem(key: string, linksItem: VraHref): VraBlockDevice;
}

/**
 * State object representing a query result of block device.
 */
declare class VraBlockDeviceResult {
	readonly numberOfElements: number;
	readonly content: Object[];
	readonly totalElements: number;
	constructor();
}

/**
 * BlockDeviceSpecification
 */
declare class VraBlockDeviceSpecification {
	capacityInGB: number;
	sourceReference: string;
	encrypted: boolean;
	deploymentId: string;
	name: string;
	description: string;
	diskContentBase64: string;
	persistent: boolean;
	constraints: Object[];
	projectId: string;
	tags: Object[];
	constructor();

	/**
	 * @param constraintsItem
	 */
	addConstraintsItem(
		constraintsItem: VraConstraint
	): VraBlockDeviceSpecification;

	/**
	 * @param tagsItem
	 */
	addTagsItem(tagsItem: VraTag): VraBlockDeviceSpecification;

	/**
	 * @param key
	 * @param customPropertiesItem
	 */
	putCustomPropertiesItem(
		key: string,
		customPropertiesItem: string
	): VraBlockDeviceSpecification;
}

/**
 * A constraint that is conveyed to the policy engine.
 */
declare class VraConstraint {
	expression: string;
	mandatory: boolean;
	constructor();
}

/**
 * Specification for a second day change security groups operation for a vsphere machine
 */
declare class VraChangeSecurityGroupSpecification {
	owner: string;
	createdAt: string;
	networkInterfaceSpecifications: Object[];
	name: string;
	description: string;
	id: string;
	orgId: string;
	updatedAt: string;
	constructor();

	/**
	 * @param networkInterfaceSpecificationsItem
	 */
	addNetworkInterfaceSpecificationsItem(
		networkInterfaceSpecificationsItem: VraNetworkInterfaceSpecification
	): VraChangeSecurityGroupSpecification;

	/**
	 * @param key
	 * @param linksItem
	 */
	putLinksItem(
		key: string,
		linksItem: VraHref
	): VraChangeSecurityGroupSpecification;
}

declare class VraNetworkInterfaceSpecification {
	addresses: Object[];
	macAddress: string;
	securityGroupIds: Object[];
	fabricNetworkId: string;
	name: string;
	description: string;
	networkId: string;
	deviceIndex: number;
	constructor();

	/**
	 * @param key
	 * @param customPropertiesItem
	 */
	putCustomPropertiesItem(
		key: string,
		customPropertiesItem: string
	): VraNetworkInterfaceSpecification;

	/**
	 * @param securityGroupIdsItem
	 */
	addSecurityGroupIdsItem(
		securityGroupIdsItem: string
	): VraNetworkInterfaceSpecification;

	/**
	 * @param addressesItem
	 */
	addAddressesItem(addressesItem: string): VraNetworkInterfaceSpecification;
}

declare class VraCloudAccount {
	owner: string;
	linksExtension: string;
	cloudAccountType: string;
	description: string;
	enabledRegionIdsExtension: string;
	orgId: string;
	tags: Object[];
	createdAt: string;
	enabledRegions: Object[];
	readonly internalIdString: string;
	name: string;
	host: VraHost;
	tagsExtention: string;
	id: string;
	updatedAt: string;
	customPropertiesExtension: string;
	constructor();

	/**
	 * @param enabledRegionsItem
	 */
	addEnabledRegionsItem(enabledRegionsItem: VraRegion): VraCloudAccount;

	/**
	 * @param key
	 * @param cloudAccountPropertiesItem
	 */
	putCloudAccountPropertiesItem(
		key: string,
		cloudAccountProperties: string
	): VraCloudAccount;

	/**
	 * @param key
	 * @param linksItem
	 */
	putLinksItem(key: string, linksItem: VraHref): VraCloudAccount;

	/**
	 * @param tagsItem
	 */
	addTagsItem(tagsItem: VraTag): VraCloudAccount;

	/**
	 * @param key
	 * @param customPropertiesItem
	 */
	putCustomPropertiesItem(
		key: string,
		customPropertiesItem: string
	): VraCloudAccount;
}

declare class VraCloudAccountNsxT {
	owner: string;
	hostName: string;
	managerMode: boolean;
	description: string;
	orgId: string;
	tags: Object[];
	createdAt: string;
	dcid: string;
	readonly internalIdString: string;
	name: string;
	host: VraHost;
	id: string;
	isGlobalManager: boolean;
	updatedAt: string;
	username: string;
	constructor();

	/**
	 * @param key
	 * @param linksItem
	 */
	putLinksItem(key: string, linksItem: VraHref): VraCloudAccountNsxT;

	/**
	 * @param key
	 * @param customPropertiesItem
	 */
	putCustomPropertiesItem(
		key: string,
		customPropertiesItem: string
	): VraCloudAccountNsxT;

	/**
	 * @param tagsItem
	 */
	addTagsItem(tagsItem: VraTag): VraCloudAccountNsxT;
}

declare class VraCloudAccountNsxTResult {
	readonly numberOfElements: number;
	readonly content: Object[];
	readonly totalElements: number;
	constructor();
}

/**
 * State object representing an Nsx-V cloud account.<br><br>A cloud account identifies a cloud account type and an account-specific deployment region or data center where the associated cloud account resources are hosted.<br>**HATEOAS** links:<br>**self** - CloudAccountNsxV - Self link to this cloud account
 */
declare class VraCloudAccountNsxV {
	owner: string;
	hostName: string;
	description: string;
	orgId: string;
	tags: Object[];
	createdAt: string;
	dcid: string;
	readonly internalIdString: string;
	name: string;
	host: VraHost;
	id: string;
	updatedAt: string;
	username: string;
	constructor();

	/**
	 * @param tagsItem
	 */
	addTagsItem(tagsItem: VraTag): VraCloudAccountNsxV;

	/**
	 * @param key
	 * @param customPropertiesItem
	 */
	putCustomPropertiesItem(
		key: string,
		customPropertiesItem: string
	): VraCloudAccountNsxV;

	/**
	 * @param key
	 * @param linksItem
	 */
	putLinksItem(key: string, linksItem: VraHref): VraCloudAccountNsxV;
}

/**
 * State object representing a query result of Nsx-V cloud accounts.
 */
declare class VraCloudAccountNsxVResult {
	readonly numberOfElements: number;
	readonly content: Object[];
	readonly totalElements: number;
	constructor();
}

/**
 * State object representing cloud account region.<br><br>**externalRegions** - array[RegionSpecification] - Set of regions that can be enabled for this cloud account.<br>**externalRegionIds** - array[String] - Set of ids of regions that can be enabled for this cloud account.<br>
 */
declare class VraCloudAccountRegions {
	externalRegions: Object[];
	constructor();

	/**
	 * @param externalRegionsItem
	 */
	addExternalRegionsItem(
		externalRegionsItem: VraRegionSpecification
	): VraCloudAccountRegions;
}

declare class VraRegion {
	owner: string;
	createdAt: string;
	externalRegionId: string;
	readonly internalIdString: string;
	cloudAccountId: string;
	name: string;
	host: VraHost;
	id: string;
	orgId: string;
	updatedAt: string;
	constructor();

	/**
	 * @param key
	 * @param linksItem
	 */
	putLinksItem(key: string, linksItem: VraHref): VraRegion;
}

/**
 * State object representing a query result of cloud accounts.
 */
declare class VraCloudAccountResult {
	readonly numberOfElements: number;
	readonly content: VraCloudAccount[];
	readonly totalElements: number;
	constructor();
}

/**
 * Specification for a region enumeration of vshpere cloud account.
 */
declare class VraCloudAccountVsphereRegionEnumerationSpecification {
	acceptSelfSignedCertificate: boolean;
	hostName: string;
	password: string;
	dcid: string;
	cloudAccountId: string;
	username: string;
	constructor();
}

declare class VraCloudAccountVsphereResult {
	readonly numberOfElements: number;
	readonly content: Object[];
	readonly totalElements: number;
	constructor();
}

declare class VraDataCollector {
	hostName: string;
	dcid: string;
	readonly internalIdString: string;
	ipAddress: string;
	host: VraHost;
	name: string;
	status: string;
	constructor();
}

/**
 * State object representing a query result of data collectors.
 */
declare class VraDataCollectorResult {
	readonly numberOfElements: number;
	readonly content: Object[];
	readonly totalElements: number;
	constructor();
}

declare class VraDiskAttachmentSpecification {
	blockDeviceId: string;
	scsiController: string;
	name: string;
	description: string;
	unitNumber: string;
	constructor();

	/**
	 * @param key
	 * @param diskAttachmentPropertiesItem
	 */
	putDiskAttachmentPropertiesItem(
		key: string,
		diskAttachmentPropertiesItem: string
	): VraDiskAttachmentSpecification;
}

declare class VraDiskService {
	constructor();

	/**
	 * @param genericRestClient
	 */
	setGenericRestClient(genericRestClient: VraGenericRestClient): void;

	/**
	 * Create a BlockDevice in Synchronous manner
	 * @param blockDeviceSpecification
	 */
	createBlockDevice(
		blockDeviceSpecification: VraBlockDeviceSpecification
	): VraBlockDevice;

	/**
	 * Resize a BlockDevice in an asynchronous manner.
	 * @param diskId
	 * @param capacityInGB
	 */
	resizeBlockDevice(diskId: string, capacityInGB: number): VraRequestTracker;

	/**
	 * Delete a BlockDevice in an asynchronous manner.
	 * @param diskId
	 * @param purge
	 * @param forceDelete
	 */
	deleteBlockDevice(
		diskId: string,
		purge: boolean,
		forceDelete: boolean
	): VraRequestTracker;

	/**
	 * Create a BlockDevice in an asynchronous manner
	 * @param blockDeviceSpecification
	 */
	createBlockDeviceAsync(
		blockDeviceSpecification: VraBlockDeviceSpecification
	): VraRequestTracker;

	/**
	 * Create a BlockDevice Snapshot in an asynchronous manner.
	 * @param diskId
	 * @param diskSnapshotSpecification
	 */
	createBlockDeviceSnapshot(
		diskId: string,
		diskSnapshotSpecification: VraDiskSnapshotSpecification
	): VraRequestTracker;

	/**
	 * Revert a BlockDevice Snapshot in an asynchronous manner.
	 * @param diskId
	 * @param snapshotId
	 */
	revertBlockDeviceSnapshot(
		diskId: string,
		snapshotId: string
	): VraRequestTracker;

	/**
	 * Promote a BlockDevice in an asynchronous manner. Second day promote operation on disk. Applicable for vSphere Block Devices only
	 * @param diskId
	 */
	promoteBlockDevice(diskId: string): VraRequestTracker;

	/**
	 * Delete a BlockDevice Snapshot in an asynchronous manner.
	 * @param diskId
	 * @param snapshotId
	 */
	deleteBlockDeviceSnapshot(
		diskId: string,
		snapshotId: string
	): VraRequestTracker;
}

/**
 * Specification for Disk Snapshot creation request.
 */
declare class VraDiskSnapshotSpecification {
	name: string;
	description: string;
	tags: Object[];
	constructor();

	/**
	 * @param key
	 * @param customPropertiesItem
	 */
	putSnapshotPropertiesItem(
		key: string,
		customPropertiesItem: string
	): VraDiskSnapshotSpecification;

	/**
	 * @param tagsItem
	 */
	addTagsItem(tagsItem: VraTag): VraDiskSnapshotSpecification;
}

/**
 * VMware Aria Automation Entity finder to search for an entity
 */
declare class VraEntitiesFinder {
	/**
	 * Get all tags.
	 * @param vRAHost
	 * @param filter
	 */
	getTags(vRAHost: VraHost, filter: string): VraTag[];

	/**
	 * Get CloudZone for a given zone id.
	 * @param vRAHost
	 * @param zoneId
	 */
	getCloudZone(vRAHost: VraHost, zoneId: string): VraZone;

	/**
	 * Get all FabricNetwork for a given Host.
	 * @param vRAHost
	 * @param filter
	 */
	getFabricNetworks(vRAHost: VraHost, filter: string): VraFabricNetwork[];

	/**
	 * Get all CloudZones for the given Host.
	 * @param vRAHost
	 * @param filter
	 */
	getCloudZones(vRAHost: VraHost, filter: string): VraZone[];

	/**
	 * Get all FlavorProfile objects for a given Host.
	 * @param vRAHost
	 */
	getFlavorProfiles(vRAHost: VraHost): VraFlavorProfile[];

	/**
	 * Get StorageProfile by ID.
	 * @param vRAHost
	 * @param id
	 */
	getStorageProfile(vRAHost: VraHost, id: string): VraStorageProfile;

	/**
	 * Get vSphere Cloud Account By Id.
	 * @param vRAHost
	 * @param vsphereCloudAccountId
	 */
	getVsphereCloudAccount(
		vRAHost: VraHost,
		vsphereCloudAccountId: string
	): VraCloudAccountVsphere;

	/**
	 * Retrieves a project by id for the given Host.
	 * @param vRAHost
	 * @param id
	 */
	getProject(vRAHost: VraHost, id: string): VraProject;

	/**
	 * Get FabricNetwork by ID.
	 * @param vRAHost
	 * @param id
	 * @param select
	 */
	getFabricNetwork(
		vRAHost: VraHost,
		id: string,
		select: string
	): VraFabricNetwork;

	/**
	 * Get project resource metadata by a given project id.
	 * @param vRAHost
	 * @param id
	 */
	getProjectResourceMetadata(
		vRAHost: VraHost,
		id: string
	): VraProjectResourceMetadata;

	/**
	 * Get all Data Collectors/Cloud Proxies.
	 * @param vRAHost
	 * @param isDisabledDataCollector
	 */
	getDataCollectors(
		vRAHost: VraHost,
		isDisabledDataCollector: boolean
	): VraDataCollector[];

	/**
	 * Get all image mapping keys for a given Host.
	 * @param vRAHost
	 */
	getAllImageMapping(vRAHost: VraHost): string[];

	/**
	 * Get NSX-V Cloud Account by Id.
	 * @param vRAHost
	 * @param id
	 */
	getNsxVCloudAccount(vRAHost: VraHost, id: string): VraCloudAccountNsxV;

	/**
	 * Get ImageProfile by ID.
	 * @param vRAHost
	 * @param id
	 */
	getImageProfile(vRAHost: VraHost, id: string): VraImageProfile;

	/**
	 * Get all image reference keys for a given Host and Image.
	 * @param vRAHost
	 * @param image
	 */
	getAllImageReferencesByImage(vRAHost: VraHost, image: string): string[];

	/**
	 * Get all RequestTracker objects.
	 * @param vRAHost
	 */
	getRequestTrackers(vRAHost: VraHost): VraRequestTracker[];

	/**
	 * Get all NSX-V Cloud Account.
	 * @param vRAHost
	 */
	getNsxVCloudAccounts(vRAHost: VraHost): VraCloudAccountNsxV[];

	/**
	 * Get vSphere Cloud Account Regions/DataCenter.
	 * @param vRAHost
	 * @param cloudAccountVsphereSpecification
	 */
	getVsphereCloudAccountRegions(
		vRAHost: VraHost,
		cloudAccountVsphereSpecification: VraCloudAccountVsphereSpecification
	): VraCloudAccountRegions;

	/**
	 * Get all BlockDevice/Disk snapshots for a given BlockDevice/Disk Id.
	 * @param vRAHost
	 * @param blockDeviceId
	 */
	getBlockDeviceSnapshots(
		vRAHost: VraHost,
		blockDeviceId: string
	): VraDiskSnapshot[];

	/**
	 * Get all ImageProfile objects for a given Host.
	 * @param vRAHost
	 */
	getImageProfiles(vRAHost: VraHost): VraImageProfile[];

	/**
	 * Get Block Device/Disk Snapshot details for given Block Device Id.
	 * @param vRAHost
	 * @param blockDeviceId
	 * @param snapshotId
	 */
	getBlockDeviceSnapshot(
		vRAHost: VraHost,
		blockDeviceId: string,
		snapshotId: string
	): VraDiskSnapshot;

	/**
	 * Get Machine Snapshot details for given Machine ID and Snapshot ID.
	 * @param vRAHost
	 * @param machineId
	 * @param snapshotId
	 */
	getMachineSnapshot(
		vRAHost: VraHost,
		machineId: string,
		snapshotId: string
	): VraSnapshot;

	/**
	 * Get NetworkProfile by ID.
	 * @param vRAHost
	 * @param id
	 */
	getNetworkProfile(vRAHost: VraHost, id: string): VraNetworkProfile;

	/**
	 * Get all Machine objects for a given Host.
	 * @param vRAHost
	 * @param machineId
	 */
	getMachineDisksByMachineId(
		vRAHost: VraHost,
		machineId: string
	): VraBlockDevice[];

	/**
	 * Get Available BlockDevices for a given Host.
	 * @param vRAHost
	 * @param filter
	 */
	getAvailableBlockDevices(
		vRAHost: VraHost,
		filter: string
	): VraBlockDevice[];

	/**
	 * Get all Networks.
	 * @param vRAHost
	 */
	getNetworks(vRAHost: VraHost): VraNetwork[];

	/**
	 * Get Machine Disk by ID for a specific machine.
	 * @param vRAHost
	 * @param machineId
	 * @param diskId
	 */
	getMachineDisk(
		vRAHost: VraHost,
		machineId: string,
		diskId: string
	): VraBlockDevice;

	/**
	 * Get tags by key.
	 * @param vRAHost
	 * @param key
	 * @param filter
	 */
	getTagsByKey(vRAHost: VraHost, key: string, filter: string): VraTag[];

	/**
	 * Get all vSphere Cloud Accounts.
	 * @param vRAHost
	 */
	getVsphereCloudAccounts(vRAHost: VraHost): VraCloudAccountVsphere[];

	/**
	 * Get all flavor mapping keys for a given Host.
	 * @param vRAHost
	 */
	getAllFlavorMapping(vRAHost: VraHost): string[];

	/**
	 * Get Machine Network Interface details for given Machine ID and Network Interface ID.
	 * @param vRAHost
	 * @param machineId
	 * @param networkInterfaceId
	 */
	getMachineNetworkInterface(
		vRAHost: VraHost,
		machineId: string,
		networkInterfaceId: string
	): VraNetworkInterface;

	/**
	 * Get machine by ID.
	 * @param vRAHost
	 * @param Id
	 * @param select
	 */
	getMachine(vRAHost: VraHost, id: string, select: string): VraMachine;

	/**
	 * Get all Snapshots for a given Machine
	 * @param vRAHost
	 * @param machineId
	 */
	getMachineSnapshots(vRAHost: VraHost, machineId: string): VraSnapshot[];

	/**
	 * Get Cloud Account - Vsphere, NSXT, NXTV, AWS, Azure, GCP etc by ID.
	 * @param vRAHost
	 * @param id
	 * @param select
	 */
	getCloudAccount(
		vRAHost: VraHost,
		id: string,
		select: string
	): VraCloudAccount;

	/**
	 * Get all Machines for a given Host.
	 * @param vRAHost
	 * @param filter
	 */
	getMachines(vRAHost: VraHost, filter: string): VraMachine[];

	/**
	 * Get RequestTracker object for a given request id.
	 * @param vRAHost
	 * @param requestTrackerId
	 */
	getRequestTracker(
		vRAHost: VraHost,
		requestTrackerId: string
	): VraRequestTracker;

	/**
	 * Get all NSX-T Cloud Account.
	 * @param vRAHost
	 */
	getNsxTCloudAccounts(vRAHost: VraHost): VraCloudAccountNsxT[];

	/**
	 * Get all projects with specified paging parameters for the given Host.
	 * @param vRAHost
	 * @param filter
	 */
	getProjects(vRAHost: VraHost, filter: string): VraProject[];

	/**
	 * Get all Network Interfaces for a given Machine.
	 * @param vRAHost
	 * @param machineId
	 */
	getMachineNetworkInterfaces(
		vRAHost: VraHost,
		machineId: string
	): VraNetworkInterface[];

	/**
	 * Get NSX-T Cloud Account by Id.
	 * @param vRAHost
	 * @param Id
	 */
	getNsxTCloudAccount(vRAHost: VraHost, id: string): VraCloudAccountNsxT;

	/**
	 * Get CloudZone Region by given Zone.
	 * @param vRAHost
	 * @param zone
	 */
	getCloudZoneRegionByZone(vRAHost: VraHost, zone: VraZone): VraRegion;

	/**
	 * Get all NetworkProfile objects for a given Host.
	 * @param vRAHost
	 */
	getNetworkProfiles(VraHost: VraHost): VraNetworkProfile[];

	/**
	 * Get CloudZone Region by given region Id.
	 * @param vRAHost
	 * @param regionId
	 */
	getCloudZoneRegion(vRAHost: VraHost, regionId: string): VraRegion;

	/**
	 * Get Tag Object by String Key and value.
	 * @param vRAHost
	 * @param key
	 * @param value
	 */
	getTag(vRAHost: VraHost, key: string, value: string): VraTag;

	/**
	 * Get all StorageProfile objects for a given Host.
	 * @param vRAHost
	 */
	getStorageProfiles(vRAHost: VraHost): VraStorageProfile[];

	/**
	 * Get all Cloud zone Region.
	 * @param vRAHost
	 */
	getCloudZoneRegions(vRAHost: VraHost): VraRegion[];

	/**
	 * Get Data Collector/Cloud Proxy by Id.
	 * @param vRAHost
	 * @param dataCollectorId
	 */
	getDataCollector(
		vRAHost: VraHost,
		dataCollectorId: string
	): VraDataCollector;

	/**
	 * Get all flavor references for a given Host and flavor.
	 * @param vRAHost
	 * @param flavor
	 */
	getAllFlavorReferencesByFlavor(vRAHost: VraHost, flavor: string): string[];

	/**
	 * Get BlockDevice by ID.
	 * @param vRAHost
	 * @param Id
	 */
	getBlockDevice(vRAHost: VraHost, id: string): VraBlockDevice;

	/**
	 * Get all Cloud Accounts - Vsphere, NSXT, NXTV, AWS, Azure, GCP etc.
	 * @param vRAHost
	 * @param filter
	 */
	getCloudAccounts(vRAHost: VraHost, filter: string): VraCloudAccount[];

	/**
	 * Get all BlockDevice for a given Host.
	 * @param vRAHost
	 * @param filter
	 */
	getBlockDevices(vRAHost: VraHost, filter: string): VraBlockDevice[];

	/**
	 * Get FlavorProfile by ID.
	 * @param vRAHost
	 * @param id
	 */
	getFlavorProfile(vRAHost: VraHost, id: string): VraFlavorProfile;

	/**
	 * Get Network for a given Network Id.
	 * @param vRAHost
	 * @param networkId
	 */
	getNetwork(vRAHost: VraHost, networkId: string): VraNetwork;
}

declare class VraFabricNetwork {
	owner: string;
	cloudAccountIdsExtension: string;
	linksExtension: string;
	externalRegionId: string;
	externalId: string;
	description: string;
	orgId: string;
	tags: Object[];
	cloudAccountIds: Object[];
	ipv6Cidr: string;
	createdAt: string;
	isDefault: boolean;
	readonly internalIdString: string;
	name: string;
	host: VraHost;
	tagsExtension: string;
	isPublic: boolean;
	cidr: string;
	id: string;
	updatedAt: string;
	customPropertiesExtension: string;

	constructor();

	/**
	 * Get all Fabric Network Tags by Key.
	 * @param key
	 * @param customPropertiesItem
	 */
	putCustomPropertiesItem(
		key: string,
		customPropertiesItem: string
	): VraFabricNetwork;

	/**
	 * @param tagsItem
	 */
	addTagsItem(tagsItem: VraTag): VraFabricNetwork;

	/**
	 * @param cloudAccountIdsItem
	 */
	addCloudAccountIdsItem(cloudAccountIdsItem: string): VraFabricNetwork;

	/**
	 * @param key
	 * @param linksItem
	 */
	putLinksItem(key: string, linksItem: VraHref): VraFabricNetwork;
}

declare class VraFlavorProfile {
	owner: string;
	flavorMappings: VraFlavorMapping;
	linksExtension: string;
	externalRegionId: string;
	flavorMappingsExtension: string;
	readonly displayName: string;
	cloudAccountId: string;
	description: string;
	orgId: string;
	createdAt: string;
	readonly internalIdString: string;
	name: string;
	host: VraHost;
	id: string;
	updatedAt: string;

	constructor();

	/**
	 * @param key
	 * @param linksItem
	 */

	putLinksItem(key: string, linksItem: VraHref): VraFlavorProfile;
}

/**
 * Describes a flavor mapping between a global fabric flavor key and fabric flavor.<br>**HATEOAS** links:<br>**region** - Region - Region for the mapping.
 */
declare class VraFlavorMapping {
	externalRegionId: string;

	constructor();

	/**
	 * @param key
	 * @param mappingItem
	 */
	putMappingItem(key: string, mappingItem: VraFabricFlavor): VraFlavorMapping;

	/**
	 * @param key
	 * @param linksItem
	 */
	putLinksItem(key: string, linksItem: VraHref): VraFlavorMapping;
}

declare class VraFabricFlavor {
	bootDiskSizeInMB: number;
	memoryInMB: number;
	name: string;
	storageType: string;
	dataDiskMaxCount: number;
	id: string;
	dataDiskSizeInMB: number;
	networkType: string;
	cpuCount: number;

	constructor();
}

/**
 * Represents a structure that holds details of storage profile linked to a cloud zone / region.**HATEOAS** links:<br>**region** - Region - Region for the profile.<br>**self** - StorageProfile - Self link to this storage profile.<br>**datastore** - FabricVsphereDatastore - Datastore of this storage profile.<br>**storage-policy** - FabricVsphereStoragePolicy - vSphere storage policy for this profile.<br> **storage-account** - FabricAzureStorageAccount - Azure storage account for this profile.<br>
 */
declare class VraStorageProfile {
	owner: string;
	linksExtension: string;
	supportsEncryption: boolean;
	externalRegionId: string;
	cloudAccountId: string;
	description: string;
	orgId: string;
	tags: Object[];
	createdAt: string;
	diskPropertiesExtension: string;
	readonly internalIdString: string;
	name: string;
	host: VraHost;
	tagsExtension: string;
	defaultItem: boolean;
	id: string;
	updatedAt: string;

	constructor();

	/**
	 * @param tagsItem
	 */
	addTagsItem(tagsItem: VraTag): VraStorageProfile;

	/**
	 * Indicates if a storage profile is default profile or not.
	 */
	isDefaultItem(): boolean;

	/**
	 * Indicates whether this storage profile supports encryption or not.
	 */
	isSupportsEncryption(): boolean;

	/**
	 * @param key
	 * @param linksItem
	 */
	putLinksItem(key: string, linksItem: VraHref): VraStorageProfile;

	/**
	 * @param key
	 * @param diskPropertiesItem
	 */
	putDiskPropertiesItem(
		key: string,
		diskPropertiesItem: string
	): VraStorageProfile;
}

declare class VraProject {
	owner: string;
	viewersExtension: string;
	linksExtension: string;
	constraintsExtension: string;
	zonesExtension: string;
	operationTimeout: number;
	description: string;
	zones: Object[];
	orgId: string;
	memberExtension: string;
	createdAt: string;
	viewers: Object[];
	machineNamingTemplate: string;
	sharedResources: boolean;
	members: Object[];
	readonly internalIdString: string;
	name: string;
	host: VraHost;
	placementPolicy: string;
	id: string;
	administratorsExtension: string;
	administrators: Object[];
	updatedAt: string;
	customPropertiesExtension: string;

	constructor();

	/**
	 * @param key
	 * @param customPropertiesItem
	 */
	putCustomPropertiesItem(
		key: string,
		customPropertiesItem: string
	): VraProject;

	/**
	 * @param membersItem
	 */
	addMembersItem(membersItem: VraUser): VraProject;

	/**
	 * @param viewersItem
	 */
	addViewersItem(viewersItem: VraUser): VraProject;

	/**
	 * @param zonesItem
	 */
	addZonesItem(zonesItem: VraZoneAssignment): VraProject;

	/**
	 * @param administratorsItem
	 */
	addAdministratorsItem(administratorsItem: VraUser): VraProject;

	/**
	 * @param key
	 * @param linksItem
	 */
	putLinksItem(key: string, linksItem: VraHref): VraProject;

	/**
	 * @param key
	 * @param constraintsItem
	 */
	putConstraintsItem(
		key: string,
		constraintsItem: VraConstraint[]
	): VraProject;
}

/**
 * A representation of a user.
 */
declare class VraUser {
	type: string;
	email: string;

	constructor();
}

declare class VraZoneAssignment {
	allocatedCpu: number;
	storageLimitGB: number;
	allocatedStorageGB: number;
	allocatedInstancesCount: number;
	cpuLimit: number;
	memoryLimitMB: number;
	allocatedMemoryMB: number;
	zoneId: string;
	maxNumberInstances: number;
	priority: number;

	constructor();
}

/**
 * Represents the resource metadata associated with a project
 */
declare class VraProjectResourceMetadata {
	readonly internalIdString: string;
	host: VraHost;
	tags: Object[];

	constructor();

	/**
	 * @param tagsItem
	 */
	addTagsItem(tagsItem: VraTag): VraProjectResourceMetadata;
}

/**
 * Represents a structure that holds a list of image mappings defined for the particular region.<br>**HATEOAS** links:<br>**region** - Region - Region for the profile.<br>**self** - ImageProfile - Self link to this image profile
 */
declare class VraImageProfile {
	owner: string;
	linksExtension: string;
	externalRegionId: string;
	imageMappings: VraImageMapping;
	readonly displayName: string;
	cloudAccountId: string;
	description: string;
	orgId: string;
	imageMappingsExtension: string;
	createdAt: string;
	readonly internalIdString: string;
	name: string;
	host: VraHost;
	id: string;
	updatedAt: string;

	constructor();

	/**
	 * @param key
	 * @param linksItem
	 */
	putLinksItem(key: string, linksItem: VraHref): VraImageProfile;
}

/**
 * Describes an image mapping between image key and fabric image.<br>**HATEOAS** links:<br>**region** - Region - Region for the mapping.
 */
declare class VraImageMapping {
	externalRegionId: string;

	constructor();

	/**
	 * @param key
	 * @param linksItem
	 */
	putLinksItem(key: string, linksItem: VraHref): VraImageMapping;

	/**
	 * @param key
	 * @param mappingItem
	 */
	putMappingItem(
		key: string,
		mappingItem: VraImageMappingDescription
	): VraImageMapping;
}

declare class VraImageMappingDescription {
	owner: string;
	cloudConfig: string;
	osFamily: string;
	externalRegionId: string;
	description: string;
	externalId: string;
	isPrivate: boolean;
	constraints: Object[];
	orgId: string;
	cloudAccountIds: Object[];
	createdAt: string;
	name: string;
	id: string;
	updatedAt: string;

	constructor();

	/**
	 * @param constraintsItem
	 */
	addConstraintsItem(
		constraintsItem: VraConstraint
	): VraImageMappingDescription;

	/**
	 * @param key
	 * @param linksItem
	 */
	putLinksItem(key: string, linksItem: VraHref): VraImageMappingDescription;

	/**
	 * @param key
	 * @param customPropertiesItem
	 */
	putCustomPropertiesItem(
		key: string,
		customPropertiesItem: string
	): VraImageMappingDescription;

	/**
	 * @param cloudAccountIdsItem
	 */
	addCloudAccountIdsItem(
		cloudAccountIdsItem: string
	): VraImageMappingDescription;
}

/**
 * Represents a disk snapshot
 */
declare class VraDiskSnapshot {
	owner: string;
	createdAt: string;
	linksExtension: string;
	readonly internalIdString: string;
	name: string;
	snapshotPropertiesExtension: string;
	host: VraHost;
	id: string;
	orgId: string;
	updatedAt: string;
	desc: string;
	tags: Object[];

	constructor();

	/**
	 * @param key
	 * @param linksItem
	 */
	putLinksItem(key: string, linksItem: VraHref): VraDiskSnapshot;

	/**
	 * @param tagsItem
	 */
	addTagsItem(tagsItem: VraTag): VraDiskSnapshot;

	/**
	 * @param key
	 * @param snapshotPropertiesItem
	 */
	putSnapshotPropertiesItem(
		key: string,
		snapshotPropertiesItem: string
	): VraDiskSnapshot;
}

declare class VraSnapshot {
	owner: string;
	createdAt: string;
	linksExtension: string;
	isCurrent: boolean;
	readonly displayName: string;
	readonly internalIdString: string;
	name: string;
	host: VraHost;
	description: string;
	id: string;
	orgId: string;
	updatedAt: string;

	constructor();

	/**
	 * @param key
	 * @param linksItem
	 */
	putLinksItem(key: string, linksItem: VraHref): VraSnapshot;
}

declare class VraNetworkProfile {
	owner: string;
	linksExtension: string;
	externalRegionId: string;
	cloudAccountId: string;
	description: string;
	isolationNetworkDomainCIDR: string;
	orgId: string;
	tags: Object[];
	createdAt: string;
	readonly internalIdString: string;
	name: string;
	host: VraHost;
	tagsExtension: string;
	id: string;
	isolatedNetworkCIDRPrefix: number;
	updatedAt: string;
	customPropertiesExtension: string;

	constructor();

	/**
	 * @param tagsItem
	 */
	addTagsItem(tagsItem: VraTag): VraNetworkProfile;

	/**
	 * @param key
	 * @param customPropertiesItem
	 */
	putCustomPropertiesItem(
		key: string,
		customPropertiesItem: string
	): VraNetworkProfile;

	/**
	 * @param key
	 * @param linksItem
	 */
	putLinksItem(key: string, linksItem: VraHref): VraNetworkProfile;
}

/**
 * The network object is an opaque reference to a logical network that network interfaces are attached to.<br> Based on settings specified by your cloud administrator, it may be a reference to an existing network, or be backed by an on-demand network created for isolation, or a security group that will be attached to machines as part of provisioning.<br> Networks are a limited resource, when it is not needed it should be deleted.<br>**HATEOAS** links:<br>**self** - Network - Self link to this network
 */
declare class VraNetwork {
	owner: string;
	cloudAccountIdsExtension: string;
	linksExtension: string;
	externalZoneId: string;
	externalRegionId: string;
	externalId: string;
	description: string;
	orgId: string;
	tags: Object[];
	cloudAccountIds: Object[];
	createdAt: string;
	provisioningStatus: string;
	deploymentId: string;
	readonly internalIdString: string;
	name: string;
	host: VraHost;
	tagsExtension: string;
	cidr: string;
	id: string;
	projectId: string;
	updatedAt: string;
	customPropertiesExtension: string;

	constructor();

	/**
	 * @param key
	 * @param customPropertiesItem
	 */
	putCustomPropertiesItem(
		key: string,
		customPropertiesItem: string
	): VraNetwork;

	/**
	 * @param key
	 * @param linksItem
	 */
	putLinksItem(key: string, linksItem: VraHref): VraNetwork;

	/**
	 * @param cloudAccountIdsItem
	 */
	addCloudAccountIdsItem(cloudAccountIdsItem: string): VraNetwork;

	/**
	 * @param tagsItem
	 */
	addTagsItem(tagsItem: VraTag): VraNetwork;
}

declare class VraNetworkInterface {
	owner: string;
	addresses: Object[];
	externalRegionId: string;
	externalId: string;
	description: string;
	deviceIndex: number;
	orgId: string;
	tags: Object[];
	cloudAccountIds: Object[];
	createdAt: string;
	securityGroupIds: Object[];
	readonly internalIdString: string;
	name: string;
	host: VraHost;
	id: string;
	updatedAt: string;

	constructor();

	/**
	 * @param tagsItem
	 */
	addTagsItem(tagsItem: VraTag): VraNetworkInterface;

	/**
	 * @param addressesItem
	 */
	addAddressesItem(addressesItem: string): VraNetworkInterface;

	/**
	 * @param cloudAccountIdsItem
	 */
	addCloudAccountIdsItem(cloudAccountIdsItem: string): VraNetworkInterface;

	/**
	 * @param key
	 * @param linksItem
	 */
	putLinksItem(key: string, linksItem: VraHref): VraNetworkInterface;

	/**
	 * @param securityGroupIdsItem
	 */
	addSecurityGroupIdsItem(securityGroupIdsItem: string): VraNetworkInterface;

	/**
	 * @param key
	 * @param customPropertiesItem
	 */
	putCustomPropertiesItem(
		key: string,
		customPropertiesItem: string
	): VraNetworkInterface;
}

/**
 * Represents a cloud agnostic machine.<br>**HATEOAS** links:<br>**operations** - array[String] - Supported operations for the machine.<br>**network-interfaces** - array[NetworkInterface] - Network interfaces for the machine.<br>**disks** - array[MachineDisk] - disks for the machine.<br>**deployment** - Deployment - Deployment that this machine is part of.<br>**cloud-accounts** - array[CloudAccount] - Cloud accounts where this machine is provisioned.<br>**self** - Machine - Self link to this machine
 */
declare class VraMachine {
	externalZoneId: string;
	externalRegionId: string;
	description: string;
	orgId: string;
	cloudAccountIds: Object[];
	createdAt: string;
	hostname: string;
	provisioningStatus: string;
	deploymentId: string;
	readonly internalIdString: string;
	host: VraHost;
	id: string;
	updatedAt: string;
	owner: string;
	cloudAccountIdsExtension: string;
	linksExtension: string;
	address: string;
	externalId: string;
	tags: Object[];
	saltConfiguration: VraSaltConfiguration;
	bootConfig: VraMachineBootConfig;
	name: string;
	machinePowerState: string;
	tagsExtension: string;
	projectId: string;
	customPropertiesExtension: string;

	constructor();

	/**
	 * @param key
	 * @param linksItem
	 */
	putLinksItem(key: string, linksItem: VraHref): VraMachine;

	/**
	 * @param cloudAccountIdsItem
	 */
	addCloudAccountIdsItem(cloudAccountIdsItem: string): VraMachine;

	/**
	 * @param tagsItem
	 */
	addTagsItem(tagsItem: VraTag): VraMachine;

	/**
	 * @param key
	 * @param customPropertiesItem
	 */
	putCustomPropertiesItem(
		key: string,
		customPropertiesItem: string
	): VraMachine;
}

declare class VraSaltConfiguration {
	installerFileName: string;
	masterId: string;
	saltEnvironment: string;
	pillarEnvironment: string;
	stateFiles: Object[];
	minionId: string;

	constructor();

	/**
	 * @param key
	 * @param additionalAuthParamsItem
	 */
	putAdditionalAuthParamsItem(
		key: string,
		additionalAuthParamsItem: string
	): VraSaltConfiguration;

	/**
	 * @param stateFilesItem
	 */
	addStateFilesItem(stateFilesItem: string): VraSaltConfiguration;

	/**
	 * @param key
	 * @param additionalMinionParamsItem
	 */
	putAdditionalMinionParamsItem(
		key: string,
		additionalMinionParamsItem: string
	): VraSaltConfiguration;

	/**
	 * @param key
	 * @param variablesItem
	 */
	putVariablesItem(key: string, variablesItem: string): VraSaltConfiguration;
}

/**
 * Machine boot config that will be passed to the instance that can be used to perform common automated configuration tasks and even run scripts after the instance starts.
 */
declare class VraMachineBootConfig {
	content: string;

	constructor();
}

declare class VraMachineBootConfigSettings {
	phoneHomeShouldWait: boolean;
	phoneHomeFailOnTimeout: boolean;
	phoneHomeTimeoutSeconds: number;

	constructor();
}

declare class VraMachineResult {
	readonly numberOfElements: number;
	readonly content: Object[];
	readonly totalElements: number;

	constructor();
}

declare class VraMachineService {
	constructor();

	/**
	 * @param genericRestClient
	 */
	setGenericRestClient(genericRestClient: VraGenericRestClient): void;

	/**
	 * Create a Machine in Synchronous manner
	 * @param machineSpecification
	 */
	createMachine(machineSpecification: VraMachineSpecification): VraMachine;

	/**
	 * Shutdown a Machine
	 * @param id
	 */
	shutDown(id: string): VraRequestTracker;

	/**
	 * Delete a Machine in an asynchronous manner
	 * @param machine
	 * @param forceDelete
	 */
	deleteMachine(machine: VraMachine, forceDelete: boolean): VraRequestTracker;

	/**
	 * Resets a Machine
	 * @param id
	 */
	reset(id: string): VraRequestTracker;

	/**
	 * Resize a Machine.
	 * @param id
	 * @param name
	 * @param cpuCount
	 * @param memoryInMB
	 * @param coreCount
	 * @param rebootMachine
	 */
	resize(
		id: string,
		name: string,
		cpuCount: string,
		memoryInMB: string,
		coreCount: string,
		rebootMachine: boolean
	): VraRequestTracker;

	/**
	 * Reboot a Machine
	 * @param id
	 */
	reboot(id: string): VraRequestTracker;

	/**
	 * Power-on a Machine
	 * @param id
	 */
	powerOn(id: string): VraRequestTracker;

	/**
	 * Revert Machine Snapshot.
	 * @param machineId
	 * @param snapshotId
	 */
	revertMachineSnapshot(
		machineId: string,
		snapshotId: string
	): VraRequestTracker;

	/**
	 * Power-off a Machine
	 * @param id
	 */
	powerOff(id: string): VraRequestTracker;

	/**
	 * Create a Machine in an asynchronous manner
	 * @param machineSpecification
	 */
	createMachineAsync(
		machineSpecification: VraMachineSpecification
	): VraRequestTracker;

	/**
	 * Create Machine Snapshot.
	 * @param machineId
	 * @param snapshotSpecification
	 */
	createMachineSnapshot(
		machineId: string,
		snapshotSpecification: VraSnapshotSpecification
	): VraRequestTracker;

	/**
	 * Update a Machine
	 * @param machine
	 * @param updateMachineSpecification
	 */
	updateMachine(
		machine: VraMachine,
		updateMachineSpecification: VraUpdateMachineSpecification
	): VraMachine;

	/**
	 * Detach Disk from machine
	 * @param machineId
	 * @param diskId
	 */
	detachMachineDisk(machineId: string, diskId: string): VraRequestTracker;

	/**
	 * Attach machine disk to a machine.
	 * @param attachMachineDisk
	 * @param machineId
	 */
	attachMachineDisk(
		attachmentSpecification: VraDiskAttachmentSpecification,
		machineId: string
	): VraRequestTracker;

	/**
	 * Delete Machine Snapshot.
	 * @param machineId
	 * @param snapshotId
	 */
	deleteMachineSnapshot(
		machineId: string,
		snapshotId: string
	): VraRequestTracker;
}

declare class VraUpdateMachineSpecification {
	description: string;
	tags: Object[];

	constructor();

	/**
	 * @param tagsItem
	 */
	addTagsItem(tagsItem: VraTag): VraUpdateMachineSpecification;

	/**
	 * @param key
	 * @param customPropertiesItem
	 */
	putCustomPropertiesItem(
		key: string,
		customPropertiesItem: string
	): VraUpdateMachineSpecification;
}

declare class VraSnapshotSpecification {
	owner: string;
	createdAt: string;
	snapshotMemory: boolean;
	name: string;
	description: string;
	id: string;
	orgId: string;
	updatedAt: string;

	constructor();

	/**
	 * @param key
	 * @param customPropertiesItem
	 */
	putCustomPropertiesItem(
		key: string,
		customPropertiesItem: string
	): VraSnapshotSpecification;

	/**
	 * @param key
	 * @param linksItem
	 */
	putLinksItem(key: string, linksItem: VraHref): VraSnapshotSpecification;
}

declare class VraMachineSpecification {
	image: string;
	disks: Object[];
	imageDiskConstraints: Object[];
	description: string;
	constraints: Object[];
	machineCount: number;
	tags: Object[];
	flavor: string;
	saltConfiguration: VraSaltConfiguration;
	bootConfigSettings: VraMachineBootConfigSettings;
	deploymentId: string;
	bootConfig: VraMachineBootConfig;
	nics: Object[];
	name: string;
	flavorRef: string;
	remoteAccess: VraRemoteAccessSpecification;
	imageRef: string;
	projectId: string;

	constructor();

	/**
	 * @param nicsItem
	 */
	addNicsItem(
		nicsItem: VraNetworkInterfaceSpecification
	): VraMachineSpecification;

	/**
	 * @param imageDiskConstraintsItem
	 */
	addImageDiskConstraintsItem(
		imageDiskConstraintsItem: VraConstraint
	): VraMachineSpecification;

	/**
	 * @param constraintsItem
	 */
	addConstraintsItem(constraintsItem: VraConstraint): VraMachineSpecification;

	/**
	 * @param tagsItem
	 */
	addTagsItem(tagsItem: VraTag): VraMachineSpecification;

	/**
	 * @param disksItem
	 */
	addDisksItem(
		disksItem: VraDiskAttachmentSpecification
	): VraMachineSpecification;

	/**
	 * @param key
	 * @param customPropertiesItem
	 */
	putCustomPropertiesItem(
		key: string,
		customPropertiesItem: string
	): VraMachineSpecification;
}

/**
 * Represents a specification for machine's remote access settings.
 */
declare class VraRemoteAccessSpecification {
	password: string;
	sshKey: string;
	keyPair: string;
	authentication: string;
	username: string;

	constructor();
}

declare class VraMachinesFolder {
	name: string;

	constructor();
}

/**
 * State object representing a query result of network profiles.
 */
declare class VraNetworkProfileResult {
	readonly numberOfElements: number;
	readonly content: Object[];
	readonly totalElements: number;

	constructor();
}

declare class VraNetworkProfileService {
	/**
	 * @param genericRestClient
	 */
	setGenericRestClient(genericRestClient: VraGenericRestClient): void;
}

declare class VraNetworkService {
	/**
	 * @param genericRestClient
	 */
	setGenericRestClient(genericRestClient: VraGenericRestClient): void;

	/**
	 * Delete a network in an asynchronous manner.
	 * @param networkId
	 * @param forceDelete
	 */
	deleteNetwork(networkId: string, forceDelete: boolean): VraRequestTracker;

	/**
	 * Create a network Provision a new network based on the passed in constraints. The network should be destroyed after the machine is destroyed to free up resources.
	 * @param networkSpecification
	 */
	createNetwork(
		networkSpecification: VraNetworkSpecification
	): VraRequestTracker;
}

/**
 * Specification for a cloud network
 */
declare class VraNetworkSpecification {
	deploymentId: string;
	outboundAccess: boolean;
	name: string;
	description: string;
	createGateway: boolean;
	constraints: Object[];
	projectId: string;
	tags: Object[];

	constructor();

	/**
	 * @param constraintsItem
	 */
	addConstraintsItem(constraintsItem: VraConstraint): VraNetworkSpecification;

	/**
	 * @param tagsItem
	 */
	addTagsItem(tagsItem: VraTag): VraNetworkSpecification;

	/**
	 * @param key
	 * @param customPropertiesItem
	 */
	putCustomPropertiesItem(
		key: string,
		customPropertiesItem: string
	): VraNetworkSpecification;
}

declare class VraProjectResourceMetadataSpecification {
	tags: Object[];

	constructor();

	/**
	 * @param tagsItem
	 */
	addTagsItem(tagsItem: VraTag): VraProjectResourceMetadataSpecification;
}

declare class VraProjectResult {
	readonly numberOfElements: number;
	readonly content: Object[];
	readonly totalElements: number;

	constructor();
}

declare class VraProjectService {
	constructor();

	/**
	 * @param genericRestClient
	 */
	setGenericRestClient(genericRestClient: VraGenericRestClient): void;

	/**
	 * Update project resource metadata by a given project id.
	 * @param id
	 * @param projectResourceMetadataSpecification
	 */
	updateProjectResourceMetadata(
		id: string,
		projectResourceMetadataSpecification: VraProjectResourceMetadataSpecification
	): VraProject;

	/**
	 * Updates a project by id.
	 * @param id
	 * @param projectSpecification
	 */
	updateProject(
		id: string,
		projectSpecification: VraProjectSpecification
	): VraProject;

	/**
	 * Deletes a project with a given Project object.
	 * @param project
	 */
	deleteProject(project: VraProject): void;

	/**
	 * Creates a project.
	 * @param request
	 */
	createProject(request: VraProjectSpecification): VraProject;

	/**
	 * Deletes a project with a given id.
	 * @param id
	 */
	deleteProjectById(id: string): void;
}

/**
 * Represents a specification for a project.
 */
declare class VraProjectSpecification {
	viewers: Object[];
	machineNamingTemplate: string;
	sharedResources: boolean;
	operationTimeout: number;
	members: Object[];
	zoneAssignmentConfigurations: Object[];
	name: string;
	placementPolicy: string;
	description: string;
	administrators: Object[];

	constructor();

	/**
	 * @param key
	 * @param constraintsItem
	 */
	putConstraintsItem(
		key: string,
		constraintsItem: VraConstraint[]
	): VraProjectSpecification;

	/**
	 * @param zoneAssignmentConfigurationsItem
	 */
	addZoneAssignmentConfigurationsItem(
		zoneAssignmentConfigurationsItem: VraZoneAssignmentSpecification
	): VraProjectSpecification;

	/**
	 * @param membersItem
	 */
	addMembersItem(membersItem: VraUser): VraProjectSpecification;

	/**
	 * @param viewersItem
	 */
	addViewersItem(viewersItem: VraUser): VraProjectSpecification;

	/**
	 * @param administratorsItem
	 */
	addAdministratorsItem(administratorsItem: VraUser): VraProjectSpecification;

	/**
	 * @param key
	 * @param customPropertiesItem
	 */
	putCustomPropertiesItem(
		key: string,
		customPropertiesItem: string
	): VraProjectSpecification;
}

/**
 * State object representing a query result of regions.
 */
declare class VraRegionResult {
	readonly numberOfElements: number;
	readonly content: Object[];
	readonly totalElements: number;

	constructor();
}

declare class VraRequestTrackerResult {
	readonly numberOfElements: number;
	readonly internalIdString: string;
	host: VraHost;
	readonly content: Object[];
	readonly totalElements: number;

	constructor();
}

/**
 * State object representing a query result of storage profiles.
 */
declare class VraStorageProfileResult {
	readonly numberOfElements: number;
	readonly content: Object[];
	readonly totalElements: number;

	constructor();
}

declare class VraStorageProfileService {
	constructor();

	/**
	 * @param genericRestClient
	 */
	setGenericRestClient(genericRestClient: VraGenericRestClient): void;
}

/**
 * State object representing a query result of tags.
 */
declare class VraTagResult {
	readonly numberOfElements: number;
	readonly content: Object[];
	readonly totalElements: number;

	constructor();
}

declare class VraTagsService {
	constructor();

	/**
	 * @param genericRestClient
	 */
	setGenericRestClient(genericRestClient: VraGenericRestClient): void;
}

declare class VraZoneAssignmentSpecification {
	storageLimitGB: number;
	cpuLimit: number;
	memoryLimitMB: number;
	zoneId: string;
	maxNumberInstances: number;
	priority: number;

	constructor();
}

declare class VraZoneResult {
	readonly numberOfElements: number;
	readonly content: Object[];
	readonly totalElements: number;

	constructor();
}

/**
 * State object representing a query result of fabric networks.
 */
declare class VraFabricNetworkResult {
	readonly numberOfElements: number;
	readonly content: Object[];
	readonly totalElements: number;

	constructor();
}

/**
 * State object representing a query result of flavor profiles.
 */
declare class VraFlavorProfileResult {
	readonly numberOfElements: number;
	readonly content: Object[];
	readonly totalElements: number;

	constructor();
}

declare class VraFlavorProfileService {
	constructor();

	/**
	 * @param genericRestClient
	 */
	setGenericRestClient(genericRestClient: VraGenericRestClient): void;
}

/**
 * HostManager provides all the CRUD operations for VMware Aria Automation Plugin along with Generic Rest Client support.
 */
declare class VraHostManager {
	readonly defaultHostData;

	/**
	 * Save Automation Host object.
	 * @param props
	 */
	save(props: Object): string;

	/**
	 * Update Automation Host object.
	 * @param props
	 * @param host
	 */
	update(props: Object, host: VraHost): string;

	/**
	 * Creates a dynamic Automation Host.
	 * @param properties
	 */
	createHost(properties: Object): VraHost;

	/**
	 * Creates a temporary host with the current user host and credentials.
	 * @param id
	 */
	createHostForCurrentUser(): VraHost;

	/**
	 * Get All Automation Host by ConnectionType.
	 * @param connectionType
	 */
	findHostsByType(connectionType: string): VraHost[];

	/**
	 * Delete Automation Host.
	 * @param host
	 */
	delete(host: VraHost): void;

	/**
	 * Validate Automation Host.
	 * @param host
	 */
	validate(host: VraHost): boolean;

	/**
	 * Get Automation Host by Sid.
	 * @param sid
	 */
	getHostBySid(sid: string): VraHost;
}

/**
 * State object representing a query result of image profiles.
 */
declare class VraImageProfileResult {
	readonly numberOfElements: number;
	readonly content: Object[];
	readonly totalElements: number;

	constructor();
}

declare class VraImageProfileService {
	constructor();

	/**
	 * @param genericRestClient
	 */
	setGenericRestClient(genericRestClient: VraGenericRestClient): void;
}

declare class VraInfrastructure {
	readonly displayName;
	name: string;

	constructor();
}
