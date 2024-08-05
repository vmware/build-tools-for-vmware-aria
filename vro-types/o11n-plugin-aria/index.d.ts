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
	enabledRegionIdsExtension: Object[];
	orgId: string;
	tags: Object[];
	createdAt: string;
	enabledRegions: string;
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
	putDiskAttachmentPropertiesItem(key: string, diskAttachmentPropertiesItem: string): VraDiskAttachmentSpecification
}

declare class VraDiskService {
	constructor();

	/**
     * @param genericRestClient
     */
	setGenericRestClient(genericRestClient: VraGenericRestClient): void

	/**
	 * Create a BlockDevice in Synchronous manner
     * @param blockDeviceSpecification
     */
	createBlockDevice(blockDeviceSpecification: VraBlockDeviceSpecification): VraBlockDevice

	/**
     * Resize a BlockDevice in an asynchronous manner.
     * @param diskId
	 * @param capacityInGB
     */
	resizeBlockDevice(diskId: string, capacityInGB: number): VraRequestTracker

	/**
     * Delete a BlockDevice in an asynchronous manner.
     * @param diskId
	 * @param purge
	 * @param forceDelete
     */
	deleteBlockDevice(diskId: string, purge: boolean, forceDelete: boolean): VraRequestTracker

	/**
     * Create a BlockDevice in an asynchronous manner
	 * @param blockDeviceSpecification
     */
	createBlockDeviceAsync(blockDeviceSpecification: VraBlockDeviceSpecification): VraRequestTracker

	/**
	 * Create a BlockDevice Snapshot in an asynchronous manner.
	 * @param diskId
	 * @param diskSnapshotSpecification
	 */
	createBlockDeviceSnapshot(diskId: string, diskSnapshotSpecification: VraDiskSnapshotSpecification): VraRequestTracker

	/**
     * Revert a BlockDevice Snapshot in an asynchronous manner.
     * @param diskId
     * @param snapshotId
     */
	revertBlockDeviceSnapshot(diskId: string, snapshotId: string): VraRequestTracker

	/**
     * Promote a BlockDevice in an asynchronous manner. Second day promote operation on disk. Applicable for vSphere Block Devices only
     * @param diskId
     */
	promoteBlockDevice(diskId: string): VraRequestTracker

	/**
     * Delete a BlockDevice Snapshot in an asynchronous manner.
     * @param diskId
	 * @param snapshotId
     */
	deleteBlockDeviceSnapshot(diskId: string, snapshotId: string): VraRequestTracker
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
	putSnapshotPropertiesItem(key: string, customPropertiesItem: string): VraDiskSnapshotSpecification

	/**
     * @param tagsItem
     */
	addTagsItem(tagsItem: VraTag): VraDiskSnapshotSpecification
}
