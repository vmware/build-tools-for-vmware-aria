/**
 * Specifies the object Type VRA:HOST.
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
	constructor();
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
	constructor();

	/**
	 * @param genericRestClient
	 */
	setGenericRestClient(genericRestClient: VraGenericRestClient): void;

	/**
	 * Delete a vSphere Cloud Account with a given id
	 * @param cloudAccountVsphereId
	 */
	deleteVSphereCloudAccountById(cloudAccountVsphereId): void;

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
	constructor();

	/**
	 * @param genericRestClient
	 */
	setGenericRestClient(genericRestClient: VraGenericRestClient): void;

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
	constructor();

	/**
	 * An object used to track long-running operations.
	 */
	constructor();

	/**
	 * An object used to track long-running operations.
	 * @param resourcesItem
	 */
	addResourcesItem(resourcesItem: string): VraRequestTracker;
}

declare class VraGenericRestClient {
	readonly host: VraHost;

	/**
	 * Method to execute rest operation by setting Request object. Request object can hold information (http method, resource url, request payload)
	 * @param restRequest
	 */
	execute(restRequest: VraRestRequest): VraRestResponse;

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
