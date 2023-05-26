/**
 * Specifies the deployment type for the deployment operations.
 */
declare interface AzureDeploymentMode {
	readonly Incremental: AzureDeploymentMode;
	readonly Complete: AzureDeploymentMode;
}

/**
 * The usage unit.
 */
declare interface AzureUsageUnit {
	readonly Count: AzureUsageUnit;
}

/**
 * The operation status.
 */
declare interface AzureComputeOperationStatus {
	readonly Succeeded: AzureComputeOperationStatus;
	readonly Preempted: AzureComputeOperationStatus;
	readonly Failed: AzureComputeOperationStatus;
	readonly InProgress: AzureComputeOperationStatus;
}

/**
 * The state of the storage account at the time the operation was called.
 */
declare interface AzureProvisioningState {
	readonly Succeeded: AzureProvisioningState;
	readonly Creating: AzureProvisioningState;
	readonly ResolvingDNS: AzureProvisioningState;
}

/**
 * Specifies the reason that a storage account name could not be used.
 */
declare interface AzureReason {
	readonly AccountNameInvalid: AzureReason;
	readonly AlreadyExists: AzureReason;
}

/**
 * The status of the storage account.
 */
declare interface AzureAccountStatus {
	readonly Available: AzureAccountStatus;
	readonly Unavailable: AzureAccountStatus;
}

/**
 * The account type of the storage account.
 */
declare interface AzureAccountType {
	readonly StandardLRS: AzureAccountType;
	readonly StandardRAGRS: AzureAccountType;
	readonly StandardZRS: AzureAccountType;
	readonly StandardGRS: AzureAccountType;
	readonly PremiumLRS: AzureAccountType;
}

/**
 * The key names.
 */
declare interface AzureKeyName {
	readonly Key2: AzureKeyName;
	readonly Key1: AzureKeyName;
}

/**
 * Web Hosting Plan SKU options.
 */
declare interface AzureSkuOptions {
	readonly Basic: AzureSkuOptions;
	readonly Standard: AzureSkuOptions;
	readonly Premium: AzureSkuOptions;
	readonly Shared: AzureSkuOptions;
	readonly Free: AzureSkuOptions;
}

/**
 * The runtime availability of a website.
 */
declare interface AzureWebSiteRuntimeAvailabilityState {
	readonly Degraded: AzureWebSiteRuntimeAvailabilityState;
	readonly NotAvailable: AzureWebSiteRuntimeAvailabilityState;
	readonly Normal: AzureWebSiteRuntimeAvailabilityState;
}

/**
 * The managed pipeline mode of a website.
 */
declare interface AzureManagedPipelineMode {
	readonly Classic: AzureManagedPipelineMode;
	readonly Integrated: AzureManagedPipelineMode;
}

declare interface AzureWorkerSizeOptions {
	readonly Small: AzureWorkerSizeOptions;
	readonly Medium: AzureWorkerSizeOptions;
	readonly Large: AzureWorkerSizeOptions;
}

/**
 * Backup status
 */
declare interface AzureBackupItemStatus {
	readonly Succeeded: AzureBackupItemStatus;
	readonly PartiallySucceeded: AzureBackupItemStatus;
	readonly Failed: AzureBackupItemStatus;
	readonly Skipped: AzureBackupItemStatus;
	readonly TimedOut: AzureBackupItemStatus;
	readonly InProgress: AzureBackupItemStatus;
	readonly Created: AzureBackupItemStatus;
}

/**
 * Usage of a website's quota.
 */
declare interface AzureWebSiteUsageState {
	readonly Exceeded: AzureWebSiteUsageState;
	readonly Normal: AzureWebSiteUsageState;
}

/**
 * The state of the website.
 */
declare interface AzureWebSiteState {
	readonly Stopped: AzureWebSiteState;
	readonly Running: AzureWebSiteState;
}

/**
 * A web site's SSL state.
 */
declare interface AzureWebSiteSslState {
	readonly IpBasedEnabled: AzureWebSiteSslState;
	readonly SniEnabled: AzureWebSiteSslState;
	readonly Disabled: AzureWebSiteSslState;
}

/**
 * The type of host
 */
declare interface AzureHostType {
	readonly Repository: AzureHostType;
	readonly Standard: AzureHostType;
}

/**
 * Defines the unit for the backup frequency
 */
declare interface AzureFrequencyUnit {
	readonly Hour: AzureFrequencyUnit;
	readonly Day: AzureFrequencyUnit;
}

/**
 * Type of Database
 */
declare interface AzureDatabaseServerType {
	readonly SQLAzure: AzureDatabaseServerType;
	readonly MySql: AzureDatabaseServerType;
	readonly Custom: AzureDatabaseServerType;
	readonly SQLServer: AzureDatabaseServerType;
}

/**
 * The availability of a web space.
 */
declare interface AzureWebSpaceAvailabilityState {
	readonly Normal: AzureWebSpaceAvailabilityState;
	readonly Limited: AzureWebSpaceAvailabilityState;
}

/**
 * The remote debugging version.
 */
declare interface AzureRemoteDebuggingVersion {
	readonly VS2013: AzureRemoteDebuggingVersion;
	readonly VS2012: AzureRemoteDebuggingVersion;
}

/**
 * The available DNS record types.
 */
declare interface AzureRecordType {
	readonly A: AzureRecordType;
	readonly TXT: AzureRecordType;
	readonly SOA: AzureRecordType;
	readonly NS: AzureRecordType;
	readonly SRV: AzureRecordType;
	readonly CNAME: AzureRecordType;
	readonly MX: AzureRecordType;
	readonly AAAA: AzureRecordType;
	readonly PTR: AzureRecordType;
}

declare class AzureConnectionManager {
	/**
	 * @param connection 
	 */
	static delete(connection: AzureConnection): void;
	/**
	 * Creates a new Connection for the provided parameters.
	 * @param props 
	 * @param resource 
	 */
	static saveFromResource(props: any, resource: ResourceElement): string;
	/**
	 * Creates a new Connection for the provided parameters.
	 * @param props 
	 * @param keystoreLocation 
	 */
	static save(props: any, keystoreLocation: string): string;
	/**
	 * @param id 
	 */
	static getConnections(id: string): AzureConnection;
	/**
	 * Creates a new Connection for the provided parameters.
	 * @param props 
	 * @param privateKey 
	 * @param certificate 
	 */
	static saveFromPem(props: any, privateKey: string, certificate: string): string;
}

declare class AzureOperationResponse {
	requestId: string;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * The response body contains the status of the specified asynchronous
 * operation, indicating whether it has succeeded, is inprogress, or has failed.
 * Note that this status is distinct from the HTTP status code returned for the
 * Get Operation Status operation itself. If the asynchronous operation
 * succeeded, the response body includes the HTTP status code for the successful
 * request. If the asynchronous operation failed, the response body includes the
 * HTTP status code for the failed request, and also includes error information
 * regarding the failure.
 */
declare class AzureOperationStatusResponse {
	requestId: string;
	id: string;
	httpStatusCode: number;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * Response wrapper for the {@link GenericClient}
 */
declare class AzureAzureResponse {
	readonly payload: any;
	readonly reasonPhrase: string;
	readonly protocolVersion: string;
	readonly statusCode: number;
	constructor()
	/**
	 * @param statusCode 
	 * @param reasonPhrase 
	 * @param protocolVersion 
	 * @param payload 
	 */
	constructor(statusCode: number, reasonPhrase: string, protocolVersion: string, payload: any)
	payloadAsString(): string;
}

/**
 * A generic Azure client for executing REST operations
 */
declare interface AzureGenericClient {
	/**
	 * @param url 
	 */
	delete(url: string): AzureAzureResponse;
	/**
	 * @param url 
	 */
	get(url: string): AzureAzureResponse;
	/**
	 * @param url 
	 */
	patch(url: string): AzureAzureResponse;
	/**
	 * @param url 
	 * @param payload 
	 */
	post(url: string, payload: any): AzureAzureResponse;
	/**
	 * @param url 
	 * @param payload 
	 */
	put(url: string, payload: any): AzureAzureResponse;
}

/**
 * Resource identity.
 */
declare class AzureResourceIdentity {
	resourceProviderNamespace: string;
	resourceProviderApiVersion: string;
	resourceName: string;
	parentResourcePath: string;
	resourceType: string;
	constructor()
	constructor()
}

/**
 * List of resource groups.
 */
declare class AzureResourceGroupListResult {
	resourceGroups: any[];
	requestId: string;
	nextLink: string;
	statusCode: number;
	constructor()
	/**
	 * Initializes a new instance of the ResourceGroupListResult class with
	 * required arguments.
	 * @param nextLink 
	 */
	constructor(nextLink: string)
	/**
	 * Initializes a new instance of the ResourceGroupListResult class.
	 */
	constructor()
}

/**
 * Resource group information.
 */
declare class AzureResourceGroupExtended {
	readonly resourceGroup: AzureResourceGroupExtended;
	readonly applicationGateways: AzureApplicationGateway[];
	readonly displayName: string;
	readonly virtualMachines: AzureVirtualMachine[];
	readonly databaseServers: AzureServer[];
	readonly dnsZones: AzureZone[];
	readonly internalIdString: string;
	readonly connection: AzureConnection;
	readonly virtualNetworkGateways: AzureVirtualNetworkGateway[];
	id: string;
	readonly locationObject: AzureLocation;
	readonly storageAccounts: AzureStorageAccount[];
	readonly webSites: AzureWebSite[];
	readonly loadBalancers: AzureLoadBalancer[];
	provisioningState: string;
	readonly availabilitySets: AzureAvailabilitySet[];
	tags: Properties;
	readonly networkInterfaces: AzureNetworkInterface[];
	readonly publicIpAddresses: AzurePublicIpAddress[];
	name: string;
	location: string;
	readonly networkSecurityGroups: AzureNetworkSecurityGroup[];
	readonly routeTables: AzureRouteTable[];
	properties: string;
	readonly virtualNetworks: AzureVirtualNetwork[];
	constructor()
	/**
	 * Initializes a new instance of the ResourceGroupExtended class with
	 * required arguments.
	 * @param location 
	 */
	constructor(location: string)
	/**
	 * Initializes a new instance of the ResourceGroupExtended class.
	 */
	constructor()
	/**
	 * @param webSiteName 
	 */
	getWebSite(webSiteName: string): AzureWebSite;
	/**
	 * @param dnsZone 
	 */
	getDnsZone(dnsZone: string): AzureZone;
	/**
	 * @param ipAddressName 
	 */
	getPublicIpAddressByName(ipAddressName: string): AzurePublicIpAddress;
	/**
	 * @param appGateway 
	 */
	getApplicationGateway(appGateway: string): AzureApplicationGateway;
	/**
	 * @param storageAccountName 
	 */
	getStorageAccountByName(storageAccountName: string): AzureStorageAccount;
	delete(): void;
	/**
	 * @param loadBalancerName 
	 */
	getLoadBalancer(loadBalancerName: string): AzureLoadBalancer;
	/**
	 * @param networkSecurityGroupName 
	 */
	getNetworkSecurityGroupByGroupName(networkSecurityGroupName: string): AzureNetworkSecurityGroup;
	/**
	 * @param networkInterfaceName 
	 */
	getNetworkInterfaceByName(networkInterfaceName: string): AzureNetworkInterface;
	/**
	 * @param serverName 
	 */
	getDatabaseServer(serverName: string): AzureServer;
	/**
	 * @param virtualNetworkName 
	 */
	getVirtualNetworkByName(virtualNetworkName: string): AzureVirtualNetwork;
	/**
	 * @param virtualMachineName 
	 */
	getVirtualMachineByName(virtualMachineName: string): AzureVirtualMachine;
	beginDeleting(): void;
	/**
	 * @param routeTableName 
	 */
	getRouteTable(routeTableName: string): AzureRouteTable;
	/**
	 * @param availabilitySetName 
	 */
	getAvailabilitySetByName(availabilitySetName: string): AzureAvailabilitySet;
	/**
	 * @param virtualNetworkGatewayName 
	 */
	getVirtualNetworkGateway(virtualNetworkGatewayName: string): AzureVirtualNetworkGateway;
}

/**
 * Deployment list operation parameters.
 */
declare class AzureDeploymentListParameters {
	top: number;
	provisioningState: string;
	constructor()
	constructor()
}

/**
 * Resource type managed by the resource provider.
 */
declare class AzureProviderResourceType {
	apiVersions: any[];
	name: string;
	locations: any[];
	properties: Properties;
	constructor()
	/**
	 * Initializes a new instance of the ProviderResourceType class.
	 */
	constructor()
}

/**
 * Resource information.
 */
declare class AzureGenericResourceExtended {
	name: string;
	location: string;
	id: string;
	provisioningState: string;
	type: string;
	properties: string;
	tags: Properties;
	constructor()
	/**
	 * Initializes a new instance of the GenericResourceExtended class with
	 * required arguments.
	 * @param location 
	 */
	constructor(location: string)
	/**
	 * Initializes a new instance of the GenericResourceExtended class.
	 */
	constructor()
}

/**
 * Resource information.
 */
declare class AzureGenericResource {
	location: string;
	provisioningState: string;
	properties: string;
	tags: Properties;
	constructor()
	/**
	 * Initializes a new instance of the GenericResource class with required
	 * arguments.
	 * @param location 
	 */
	constructor(location: string)
	/**
	 * Initializes a new instance of the GenericResource class.
	 */
	constructor()
}

/**
 * Resource information.
 */
declare class AzureResourceCreateOrUpdateResult {
	resource: AzureGenericResourceExtended;
	requestId: string;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * Tag count.
 */
declare class AzureTagCount {
	type: string;
	value: string;
	constructor()
	constructor()
}

/**
 * Resource group information.
 */
declare class AzureResourceGroupListParameters {
	top: number;
	tagValue: string;
	tagName: string;
	constructor()
	constructor()
}

/**
 * List of subscription tags.
 */
declare class AzureTagsListResult {
	requestId: string;
	tags: any[];
	nextLink: string;
	statusCode: number;
	constructor()
	/**
	 * Initializes a new instance of the TagsListResult class with required
	 * arguments.
	 * @param nextLink 
	 */
	constructor(nextLink: string)
	/**
	 * Initializes a new instance of the TagsListResult class.
	 */
	constructor()
}

/**
 * Deployment dependency information.
 */
declare class AzureBasicDependency {
	resourceName: string;
	id: string;
	resourceType: string;
	constructor()
	constructor()
}

/**
 * Resource group information.
 */
declare class AzureResourceGroupExistsResult {
	requestId: string;
	exists: boolean;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * Provider Operations metadata
 */
declare class AzureProviderOperationsMetadata {
	resourceTypes: any[];
	operations: any[];
	displayName: string;
	name: string;
	id: string;
	type: string;
	constructor()
	/**
	 * Initializes a new instance of the ProviderOperationsMetadata class.
	 */
	constructor()
}

/**
 * Deployment information.
 */
declare class AzureDeploymentExtended {
	readonly resourceGroup: AzureResourceGroupExtended;
	readonly internalIdString: string;
	name: string;
	readonly connection: AzureConnection;
	id: string;
	readonly relatedObjects: string;
	properties: AzureDeploymentPropertiesExtended;
	constructor()
	/**
	 * Initializes a new instance of the DeploymentExtended class with required
	 * arguments.
	 * @param name 
	 */
	constructor(name: string)
	/**
	 * Initializes a new instance of the DeploymentExtended class.
	 */
	constructor()
}

/**
 * Tag information.
 */
declare class AzureTagValue {
	count: AzureTagCount;
	id: string;
	value: string;
	constructor()
	constructor()
}

/**
 * Resource group information.
 */
declare class AzureResourceGroupCreateOrUpdateResult {
	resourceGroup: AzureResourceGroupExtended;
	requestId: string;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * Provider operations metadata
 */
declare class AzureProviderOperationsMetadataGetResult {
	provider: AzureProviderOperationsMetadata;
	requestId: string;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * Provider operations metadata list
 */
declare class AzureProviderOperationsMetadataListResult {
	requestId: string;
	providers: any[];
	statusCode: number;
	constructor()
	/**
	 * Initializes a new instance of the ProviderOperationsMetadataListResult
	 * class.
	 */
	constructor()
}

/**
 * Resource provider operation information.
 */
declare class AzureResourceProviderOperationDefinition {
	name: string;
	resourceProviderOperationDisplayProperties: AzureResourceProviderOperationDisplayProperties;
	constructor()
	constructor()
}

/**
 * Tag details.
 */
declare class AzureTagDetails {
	values: any[];
	count: AzureTagCount;
	name: string;
	id: string;
	constructor()
	/**
	 * Initializes a new instance of the TagDetails class.
	 */
	constructor()
}

/**
 * List of resource groups.
 */
declare class AzureResourceListResult {
	requestId: string;
	resources: any[];
	nextLink: string;
	statusCode: number;
	constructor()
	/**
	 * Initializes a new instance of the ResourceListResult class with required
	 * arguments.
	 * @param nextLink 
	 */
	constructor(nextLink: string)
	/**
	 * Initializes a new instance of the ResourceListResult class.
	 */
	constructor()
}

/**
 * Tag information.
 */
declare class AzureTagCreateResult {
	requestId: string;
	tag: AzureTagDetails;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * List of deployments.
 */
declare class AzureDeploymentListResult {
	deployments: any[];
	requestId: string;
	nextLink: string;
	statusCode: number;
	constructor()
	/**
	 * Initializes a new instance of the DeploymentListResult class.
	 */
	constructor()
}

/**
 * Tag information.
 */
declare class AzureTagCreateValueResult {
	requestId: string;
	value: AzureTagValue;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * Deployment operation parameters.
 */
declare class AzureDeployment {
	properties: AzureDeploymentProperties;
	constructor()
	constructor()
}

/**
 * Management lock get operation scope filter parameters.
 */
declare class AzureManagementLockGetQueryParameter {
	atScope: string;
	constructor()
	constructor()
}

/**
 * Resource group information.
 */
declare class AzureResourceGroupPatchResult {
	resourceGroup: AzureResourceGroupExtended;
	requestId: string;
	statusCode: number;
	constructor()
	constructor()
}

declare class AzureResourceManagementErrorWithDetails {
	code: string;
	details: any[];
	message: string;
	target: string;
	constructor()
	/**
	 * Initializes a new instance of the ResourceManagementErrorWithDetails
	 * class with required arguments.
	 * @param code 
	 * @param message 
	 */
	constructor(code: string, message: string)
	/**
	 * Initializes a new instance of the ResourceManagementErrorWithDetails
	 * class.
	 */
	constructor()
}

/**
 * Resource group information.
 */
declare class AzureResourceExistsResult {
	requestId: string;
	exists: boolean;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * Resource provider information.
 */
declare class AzureProviderGetResult {
	provider: AzureProvider;
	requestId: string;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * Target resource.
 */
declare class AzureTargetResource {
	resourceName: string;
	id: string;
	resourceType: string;
	constructor()
	constructor()
}

/**
 * Resource provider registration information.
 */
declare class AzureProviderRegistionResult {
	provider: AzureProvider;
	requestId: string;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * Resource group information.
 */
declare class AzureResourceGroup {
	location: string;
	provisioningState: string;
	properties: string;
	tags: Properties;
	constructor()
	/**
	 * Initializes a new instance of the ResourceGroup class with required
	 * arguments.
	 * @param location 
	 */
	constructor(location: string)
	/**
	 * Initializes a new instance of the ResourceGroup class.
	 */
	constructor()
}

declare class AzureResourceManagementError {
	code: string;
	message: string;
	target: string;
	constructor()
	/**
	 * Initializes a new instance of the ResourceManagementError class with
	 * required arguments.
	 * @param code 
	 * @param message 
	 */
	constructor(code: string, message: string)
	/**
	 * Initializes a new instance of the ResourceManagementError class.
	 */
	constructor()
}

/**
 * Deployment operation.
 */
declare class AzureDeploymentOperationsGetResult {
	requestId: string;
	operation: AzureDeploymentOperation;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * Deployment properties with additional details.
 */
declare class AzureDeploymentPropertiesExtended {
	outputs: string;
	mode: AzureDeploymentMode;
	template: string;
	parametersLink: AzureParametersLink;
	templateLink: AzureTemplateLink;
	correlationId: string;
	provisioningState: string;
	parameters: string;
	providers: any[];
	timestamp: Date;
	dependencies: any[];
	constructor()
	/**
	 * Initializes a new instance of the DeploymentPropertiesExtended class.
	 */
	constructor()
}

/**
 * The management lock properties.
 */
declare class AzureManagementLockProperties {
	notes: string;
	level: string;
	constructor()
	constructor()
}

/**
 * Deployment operation list parameters.
 */
declare class AzureDeploymentOperationsListParameters {
	top: number;
	constructor()
	constructor()
}

/**
 * A standard service response for long running operations.
 */
declare class AzureLongRunningOperationResponse {
	retryAfter: number;
	requestId: string;
	error: AzureResourceManagementError;
	operationStatusLink: string;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * List of resource provider operations.
 */
declare class AzureResourceProviderOperationDetailListResult {
	requestId: string;
	resourceProviderOperationDetails: any[];
	statusCode: number;
	constructor()
	/**
	 * Initializes a new instance of the
	 * ResourceProviderOperationDetailListResult class.
	 */
	constructor()
}

/**
 * Deployment dependency information.
 */
declare class AzureDependency {
	dependsOn: any[];
	resourceName: string;
	id: string;
	resourceType: string;
	constructor()
	/**
	 * Initializes a new instance of the Dependency class.
	 */
	constructor()
}

/**
 * Resource Type
 */
declare class AzureResourceType {
	operations: any[];
	displayName: string;
	name: string;
	constructor()
	/**
	 * Initializes a new instance of the ResourceType class.
	 */
	constructor()
}

/**
 * Management lock information.
 */
declare class AzureManagementLockReturnResult {
	managementLock: AzureManagementLockObject;
	requestId: string;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * List of previewed features.
 */
declare class AzureFeatureOperationsListResult {
	features: any[];
	requestId: string;
	nextLink: string;
	statusCode: number;
	constructor()
	/**
	 * Initializes a new instance of the FeatureOperationsListResult class.
	 */
	constructor()
}

/**
 * Resource group information.
 */
declare class AzureResourceGroupGetResult {
	resourceGroup: AzureResourceGroupExtended;
	requestId: string;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * Previewed feature information.
 */
declare class AzureFeatureResponse {
	requestId: string;
	name: string;
	id: string;
	type: string;
	properties: AzureFeatureProperties;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * Resource provider operation's display properties.
 */
declare class AzureResourceProviderOperationDisplayProperties {
	provider: string;
	resource: string;
	publisher: string;
	description: string;
	operation: string;
	constructor()
	constructor()
}

/**
 * Deployment operation information.
 */
declare class AzureDeploymentOperation {
	operationId: string;
	id: string;
	properties: AzureDeploymentOperationProperties;
	constructor()
	constructor()
}

/**
 * List of resource providers.
 */
declare class AzureProviderListResult {
	requestId: string;
	providers: any[];
	nextLink: string;
	statusCode: number;
	constructor()
	/**
	 * Initializes a new instance of the ProviderListResult class.
	 */
	constructor()
}

/**
 * Resource provider registration information.
 */
declare class AzureProviderUnregistionResult {
	provider: AzureProvider;
	requestId: string;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * Previewed feature information.
 */
declare class AzureFeatureProperties {
	state: string;
	constructor()
	constructor()
}

/**
 * Deployment information.
 */
declare class AzureDeploymentExistsResult {
	requestId: string;
	exists: boolean;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * Template deployment operation create result.
 */
declare class AzureDeploymentOperationsCreateResult {
	requestId: string;
	deployment: AzureDeploymentExtended;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * Resource provider information.
 */
declare class AzureProvider {
	resourceTypes: any[];
	requestId: string;
	namespace: string;
	id: string;
	registrationState: string;
	statusCode: number;
	constructor()
	/**
	 * Initializes a new instance of the Provider class.
	 */
	constructor()
}

/**
 * Operation
 */
declare class AzureOperation {
	displayName: string;
	origin: string;
	name: string;
	description: string;
	properties: any;
	constructor()
	constructor()
}

/**
 * Template deployment information.
 */
declare class AzureDeploymentGetResult {
	requestId: string;
	deployment: AzureDeploymentExtended;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * Deployment properties.
 */
declare class AzureDeploymentProperties {
	mode: AzureDeploymentMode;
	template: string;
	parametersLink: AzureParametersLink;
	templateLink: AzureTemplateLink;
	parameters: string;
	constructor()
	constructor()
}

/**
 * List of deployment operations.
 */
declare class AzureDeploymentOperationsListResult {
	operations: any[];
	requestId: string;
	nextLink: string;
	statusCode: number;
	constructor()
	/**
	 * Initializes a new instance of the DeploymentOperationsListResult class.
	 */
	constructor()
}

/**
 * Entity representing the reference to the deployment paramaters.
 */
declare class AzureParametersLink {
	contentVersion: string;
	uri: string;
	constructor()
	/**
	 * Initializes a new instance of the ParametersLink class with required
	 * arguments.
	 * @param uri 
	 */
	constructor(uri: string)
	/**
	 * Initializes a new instance of the ParametersLink class.
	 */
	constructor()
}

/**
 * Parameters of move resources.
 */
declare class AzureResourcesMoveInfo {
	targetResourceGroup: string;
	resources: any[];
	constructor()
	/**
	 * Initializes a new instance of the ResourcesMoveInfo class.
	 */
	constructor()
}

/**
 * Information from validate template deployment response.
 */
declare class AzureDeploymentValidateResponse {
	readonly valid: boolean;
	requestId: string;
	error: AzureResourceManagementErrorWithDetails;
	properties: AzureDeploymentPropertiesExtended;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * Deployment operation properties.
 */
declare class AzureDeploymentOperationProperties {
	targetResource: AzureTargetResource;
	provisioningState: string;
	statusMessage: string;
	statusCode: string;
	timestamp: Date;
	constructor()
	constructor()
}

/**
 * Management lock information.
 */
declare class AzureManagementLockObject {
	name: string;
	id: string;
	type: string;
	properties: AzureManagementLockProperties;
	constructor()
	constructor()
}

/**
 * Entity representing the reference to the template.
 */
declare class AzureTemplateLink {
	contentVersion: string;
	uri: string;
	constructor()
	/**
	 * Initializes a new instance of the TemplateLink class with required
	 * arguments.
	 * @param uri 
	 */
	constructor(uri: string)
	/**
	 * Initializes a new instance of the TemplateLink class.
	 */
	constructor()
}

/**
 * Resource information.
 */
declare class AzureResourceGetResult {
	resource: AzureGenericResourceExtended;
	requestId: string;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * Resource group information.
 */
declare class AzureResourceListParameters {
	resourceGroupName: string;
	top: number;
	tagValue: string;
	tagName: string;
	resourceType: string;
	constructor()
	constructor()
}

/**
 * Deployment list operation parameters.
 */
declare class AzureProviderListParameters {
	top: number;
	constructor()
	constructor()
}

/**
 * List of management locks.
 */
declare class AzureManagementLockListResult {
	requestId: string;
	lock: any[];
	nextLink: string;
	statusCode: number;
	constructor()
	/**
	 * Initializes a new instance of the ManagementLockListResult class.
	 */
	constructor()
}

declare interface AzureResourceManagementClient {
	readonly resourceProviderOperationDetailsOperations: AzureResourceProviderOperationDetailsOperations;
	readonly providerOperationsMetadataOperations: AzureProviderOperationsMetadataOperations;
	readonly apiVersion: string;
	readonly resourceGroupsOperations: AzureResourceGroupOperations;
	readonly deploymentOperationsOperations: AzureDeploymentOperationOperations;
	readonly deploymentsOperations: AzureDeploymentOperations;
	longRunningOperationRetryTimeout: number;
	readonly resourcesOperations: AzureResourceOperations;
	longRunningOperationInitialTimeout: number;
	readonly baseUri: string;
	readonly tagsOperations: AzureTagOperations;
	readonly providersOperations: AzureProviderOperations;
	/**
	 * The Get Operation Status operation returns the status of the specified
	 * operation. After calling an asynchronous operation, you can call Get
	 * Operation Status to determine whether the operation has succeeded,
	 * failed, or is still in progress.
	 * @param arg0 
	 */
	getLongRunningOperationStatus(arg0: string): AzureLongRunningOperationResponse;
	/**
	 * Closes this stream and releases any system resources associated
	 * with it. If the stream is already closed then invoking this
	 * method has no effect.
	 * 
	 * <p> As noted in {@link AutoCloseable#close()}, cases where the
	 * close may fail require careful attention. It is strongly advised
	 * to relinquish the underlying resources and to internally
	 * <em>mark</em> the {@code Closeable} as closed, prior to throwing
	 * the {@code IOException}.
	 */
	close(): void;
}

/**
 * Operations for managing deployment operations.
 */
declare interface AzureDeploymentOperationOperations {
	/**
	 * Gets a next list of deployments operations.
	 * @param arg0 
	 */
	listNext(arg0: string): AzureDeploymentOperationsListResult;
	/**
	 * Get a list of deployments operations.
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 */
	get(arg0: string, arg1: string, arg2: string): AzureDeploymentOperationsGetResult;
	/**
	 * Gets a list of deployments operations.
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 */
	list(arg0: string, arg1: string, arg2: AzureDeploymentOperationsListParameters): AzureDeploymentOperationsListResult;
}

/**
 * Operations for managing deployments.
 */
declare interface AzureDeploymentOperations {
	/**
	 * Checks whether deployment exists.
	 * @param arg0 
	 * @param arg1 
	 */
	checkExistence(arg0: string, arg1: string): AzureDeploymentExistsResult;
	/**
	 * Get a list of deployments.
	 * @param arg0 
	 */
	listNext(arg0: string): AzureDeploymentListResult;
	/**
	 * Get a list of deployments.
	 * @param arg0 
	 * @param arg1 
	 */
	list(arg0: string, arg1: AzureDeploymentListParameters): AzureDeploymentListResult;
	/**
	 * Validate a deployment template.
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 */
	validate(arg0: string, arg1: string, arg2: AzureDeployment): AzureDeploymentValidateResponse;
	/**
	 * Delete deployment and all of its resources.
	 * @param arg0 
	 * @param arg1 
	 */
	delete(arg0: string, arg1: string): AzureOperationResponse;
	/**
	 * Cancel a currently running template deployment.
	 * @param arg0 
	 * @param arg1 
	 */
	cancel(arg0: string, arg1: string): AzureOperationResponse;
	/**
	 * Begin deleting deployment.To determine whether the operation has finished
	 * processing the request, call GetLongRunningOperationStatus.
	 * @param arg0 
	 * @param arg1 
	 */
	beginDeleting(arg0: string, arg1: string): AzureLongRunningOperationResponse;
	/**
	 * Create a named template deployment using a template.
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 */
	createOrUpdate(arg0: string, arg1: string, arg2: AzureDeployment): AzureDeploymentOperationsCreateResult;
	/**
	 * Get a deployment.
	 * @param arg0 
	 * @param arg1 
	 */
	get(arg0: string, arg1: string): AzureDeploymentGetResult;
}

/**
 * Operations for managing locks.
 */
declare interface AzureManagementLockOperations {
	/**
	 * Get a list of management locks at resource level or below.
	 * @param arg0 
	 */
	listNext(arg0: string): AzureManagementLockListResult;
	/**
	 * Deletes the management lock of a subscription.
	 * @param arg0 
	 */
	deleteAtSubscriptionLevel(arg0: string): AzureOperationResponse;
	/**
	 * Gets all the management locks of a subscription.
	 * @param arg0 
	 */
	listAtSubscriptionLevel(arg0: AzureManagementLockGetQueryParameter): AzureManagementLockListResult;
	/**
	 * Gets the management lock of a scope.
	 * @param arg0 
	 */
	get(arg0: string): AzureManagementLockReturnResult;
	/**
	 * Create or update a management lock at the resource group level.
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 */
	createOrUpdateAtResourceGroupLevel(arg0: string, arg1: string, arg2: AzureManagementLockProperties): AzureManagementLockReturnResult;
	/**
	 * Gets all the management locks of a resource group.
	 * @param arg0 
	 * @param arg1 
	 */
	listAtResourceGroupLevel(arg0: string, arg1: AzureManagementLockGetQueryParameter): AzureManagementLockListResult;
	/**
	 * Deletes the management lock of a resource or any level below resource.
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 */
	deleteAtResourceLevel(arg0: string, arg1: AzureResourceIdentity, arg2: string): AzureOperationResponse;
	/**
	 * Create or update a management lock at the subscription level.
	 * @param arg0 
	 * @param arg1 
	 */
	createOrUpdateAtSubscriptionLevel(arg0: string, arg1: AzureManagementLockProperties): AzureManagementLockReturnResult;
	/**
	 * Gets all the management locks of a resource or any level below resource.
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 */
	listAtResourceLevel(arg0: string, arg1: AzureResourceIdentity, arg2: AzureManagementLockGetQueryParameter): AzureManagementLockListResult;
	/**
	 * Deletes the management lock of a resource group.
	 * @param arg0 
	 * @param arg1 
	 */
	deleteAtResourceGroupLevel(arg0: string, arg1: string): AzureOperationResponse;
	/**
	 * Create or update a management lock at the resource level or any level
	 * below resource.
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 * @param arg3 
	 */
	createOrUpdateAtResourceLevel(arg0: string, arg1: AzureResourceIdentity, arg2: string, arg3: AzureManagementLockProperties): AzureManagementLockReturnResult;
}

/**
 * Operations for managing providers.
 */
declare interface AzureProviderOperations {
	/**
	 * Registers provider to be used with a subscription.
	 * @param arg0 
	 */
	register(arg0: string): AzureProviderRegistionResult;
	/**
	 * Unregisters provider from a subscription.
	 * @param arg0 
	 */
	unregister(arg0: string): AzureProviderUnregistionResult;
	/**
	 * Get a list of deployments.
	 * @param arg0 
	 */
	listNext(arg0: string): AzureProviderListResult;
	/**
	 * Gets a list of resource providers.
	 * @param arg0 
	 */
	list(arg0: AzureProviderListParameters): AzureProviderListResult;
	/**
	 * Gets a resource provider.
	 * @param arg0 
	 */
	get(arg0: string): AzureProviderGetResult;
}

/**
 * Operations for getting provider operations metadata.
 */
declare interface AzureProviderOperationsMetadataOperations {
	/**
	 * Gets provider operations metadata list
	 */
	list(): AzureProviderOperationsMetadataListResult;
	/**
	 * Gets provider operations metadata
	 * @param arg0 
	 */
	get(arg0: string): AzureProviderOperationsMetadataGetResult;
}

/**
 * Operations for managing resource groups.
 */
declare interface AzureResourceGroupOperations {
	/**
	 * Delete resource group and all of its resources.
	 * @param arg0 
	 */
	delete(arg0: string): AzureOperationResponse;
	/**
	 * Resource groups can be updated through a simple PATCH operation to a
	 * group address. The format of the request is the same as that for
	 * creating a resource groups, though if a field is unspecified current
	 * value will be carried over.
	 * @param arg0 
	 * @param arg1 
	 */
	patch(arg0: string, arg1: AzureResourceGroup): AzureResourceGroupPatchResult;
	/**
	 * Get a list of deployments.
	 * @param arg0 
	 */
	listNext(arg0: string): AzureResourceGroupListResult;
	/**
	 * Checks whether resource group exists.
	 * @param arg0 
	 */
	checkExistence(arg0: string): AzureResourceGroupExistsResult;
	/**
	 * Create a resource group.
	 * @param arg0 
	 * @param arg1 
	 */
	createOrUpdate(arg0: string, arg1: AzureResourceGroup): AzureResourceGroupCreateOrUpdateResult;
	/**
	 * Get a resource group.
	 * @param arg0 
	 */
	get(arg0: string): AzureResourceGroupGetResult;
	/**
	 * Begin deleting resource group.To determine whether the operation has
	 * finished processing the request, call GetLongRunningOperationStatus.
	 * @param arg0 
	 */
	beginDeleting(arg0: string): AzureLongRunningOperationResponse;
	/**
	 * Gets a collection of resource groups.
	 * @param arg0 
	 */
	list(arg0: AzureResourceGroupListParameters): AzureResourceGroupListResult;
}

/**
 * Operations for managing resources.
 */
declare interface AzureResourceOperations {
	/**
	 * Returns a resource belonging to a resource group.
	 * @param arg0 
	 * @param arg1 
	 */
	get(arg0: string, arg1: AzureResourceIdentity): AzureResourceGetResult;
	/**
	 * Move resources within or across subscriptions.
	 * @param arg0 
	 * @param arg1 
	 */
	moveResources(arg0: string, arg1: AzureResourcesMoveInfo): AzureOperationResponse;
	/**
	 * Get all of the resources under a subscription.
	 * @param arg0 
	 */
	list(arg0: AzureResourceListParameters): AzureResourceListResult;
	/**
	 * Get a list of deployments.
	 * @param arg0 
	 */
	listNext(arg0: string): AzureResourceListResult;
	/**
	 * Create a resource.
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 */
	createOrUpdate(arg0: string, arg1: AzureResourceIdentity, arg2: AzureGenericResource): AzureResourceCreateOrUpdateResult;
	/**
	 * Delete resource and all of its resources.
	 * @param arg0 
	 * @param arg1 
	 */
	delete(arg0: string, arg1: AzureResourceIdentity): AzureOperationResponse;
	/**
	 * Begin moving resources.To determine whether the operation has finished
	 * processing the request, call GetLongRunningOperationStatus.
	 * @param arg0 
	 * @param arg1 
	 */
	beginMoving(arg0: string, arg1: AzureResourcesMoveInfo): AzureLongRunningOperationResponse;
	/**
	 * Checks whether resource exists.
	 * @param arg0 
	 * @param arg1 
	 */
	checkExistence(arg0: string, arg1: AzureResourceIdentity): AzureResourceExistsResult;
}

/**
 * Operations for managing Resource provider operations.
 */
declare interface AzureResourceProviderOperationDetailsOperations {
	/**
	 * Gets a list of resource providers.
	 * @param arg0 
	 */
	list(arg0: AzureResourceIdentity): AzureResourceProviderOperationDetailListResult;
}

/**
 * Operations for managing tags.
 */
declare interface AzureTagOperations {
	/**
	 * Delete a subscription resource tag.
	 * @param arg0 
	 */
	delete(arg0: string): AzureOperationResponse;
	/**
	 * Create a subscription resource tag value.
	 * @param arg0 
	 * @param arg1 
	 */
	createOrUpdateValue(arg0: string, arg1: string): AzureTagCreateValueResult;
	/**
	 * Get a list of tags under a subscription.
	 * @param arg0 
	 */
	listNext(arg0: string): AzureTagsListResult;
	/**
	 * Get a list of subscription resource tags.
	 */
	list(): AzureTagsListResult;
	/**
	 * Create a subscription resource tag.
	 * @param arg0 
	 */
	createOrUpdate(arg0: string): AzureTagCreateResult;
	/**
	 * Delete a subscription resource tag value.
	 * @param arg0 
	 * @param arg1 
	 */
	deleteValue(arg0: string, arg1: string): AzureOperationResponse;
}

/**
 * The source image reference.
 */
declare class AzureSourceImageReference {
	referenceUri: string;
	constructor()
	constructor()
}

/**
 * Gets or sets additional XML formatted information that can be included in the
 * Unattend.xml file, which is used by Windows Setup. Contents are defined by
 * setting name, component name, and the pass in which the content is a applied.
 */
declare class AzureAdditionalUnattendContent {
	passName: string;
	componentName: string;
	content: string;
	settingName: string;
	constructor()
	constructor()
}

/**
 * The compute long running operation response.
 */
declare class AzureComputeOperationResponse {
	azureAsyncOperation: string;
	requestId: string;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * Contains the os disk image information.
 */
declare class AzureOSDiskImage {
	operatingSystem: string;
	constructor()
	/**
	 * Initializes a new instance of the OSDiskImage class with required
	 * arguments.
	 * @param operatingSystem 
	 */
	constructor(operatingSystem: string)
	/**
	 * Initializes a new instance of the OSDiskImage class.
	 */
	constructor()
}

/**
 * Describes Boot Diagnostics.
 */
declare class AzureBootDiagnostics {
	storageUri: string;
	enabled: boolean;
	constructor()
	constructor()
}

/**
 * Describes a Virtual Machine Extension.
 */
declare class AzureVirtualMachineExtension {
	settings: string;
	autoUpgradeMinorVersion: boolean;
	provisioningState: string;
	type: string;
	typeHandlerVersion: string;
	protectedSettings: string;
	tags: Properties;
	instanceView: AzureVirtualMachineExtensionInstanceView;
	extensionType: string;
	name: string;
	publisher: string;
	location: string;
	id: string;
	constructor()
	/**
	 * Initializes a new instance of the VirtualMachineExtension class with
	 * required arguments.
	 * @param location 
	 */
	constructor(location: string)
	/**
	 * Initializes a new instance of the VirtualMachineExtension class.
	 */
	constructor()
}

/**
 * Describes a network interface reference.
 */
declare class AzureNetworkInterfaceReference {
	referenceUri: string;
	primary: boolean;
	constructor()
	constructor()
}

/**
 * Plan for the resource.
 */
declare class AzurePlan {
	product: string;
	promotionCode: string;
	name: string;
	publisher: string;
	constructor()
	constructor()
}

/**
 * The get virtual machine extension image operation response.
 */
declare class AzureVirtualMachineExtensionImageGetResponse {
	virtualMachineExtensionImage: AzureVirtualMachineExtensionImage;
	requestId: string;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * The compute long running operation response.
 */
declare class AzureVirtualMachineExtensionCreateOrUpdateResponse {
	azureAsyncOperation: string;
	requestId: string;
	virtualMachineExtension: AzureVirtualMachineExtension;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * Contains the parameters required to list virtual machine images with details.
 */
declare class AzureVirtualMachineImageListDetailsParameters {
	offer: string;
	skus: string;
	publisherName: string;
	location: string;
	constructor()
	constructor()
}

/**
 * Virtual machine image resource information.
 */
declare class AzureVirtualMachineImageResource {
	name: string;
	location: string;
	id: string;
	constructor()
	/**
	 * Initializes a new instance of the VirtualMachineImageResource class with
	 * required arguments.
	 * @param id 
	 * @param name 
	 * @param location 
	 */
	constructor(id: string, name: string, location: string)
	/**
	 * Initializes a new instance of the VirtualMachineImageResource class.
	 */
	constructor()
}

/**
 * Specifies the parameters to be passed to List APIs.
 */
declare class AzureListParameters {
	constructor()
	constructor()
}

/**
 * Describes a disk.
 */
declare class AzureDisk {
	virtualHardDisk: AzureVirtualHardDisk;
	name: string;
	sourceImage: AzureVirtualHardDisk;
	caching: string;
	createOption: string;
	diskSizeGB: number;
	constructor()
	/**
	 * Initializes a new instance of the Disk class with required arguments.
	 * @param name 
	 * @param virtualHardDisk 
	 * @param createOption 
	 */
	constructor(name: string, virtualHardDisk: AzureVirtualHardDisk, createOption: string)
	/**
	 * Initializes a new instance of the Disk class.
	 */
	constructor()
}

/**
 * The compute long running operation response.
 */
declare class AzureDeleteOperationResponse {
	trackingOperationId: string;
	azureAsyncOperation: string;
	requestId: string;
	startTime: Date;
	endTime: Date;
	error: AzureApiError;
	statusCode: number;
	constructor()
	/**
	 * Initializes a new instance of the DeleteOperationResponse class.
	 */
	constructor()
}

/**
 * Describes a Virtual Machine Extension Image.
 */
declare class AzureVirtualMachineExtensionImage {
	vMScaleSetEnabled: boolean;
	name: string;
	handlerSchema: string;
	location: string;
	id: string;
	computeRole: string;
	supportsMultipleExtensions: boolean;
	operatingSystem: string;
	constructor()
	constructor()
}

/**
 * The List Virtual Machine operation response.
 */
declare class AzureVirtualMachineListResponse {
	requestId: string;
	virtualMachines: any[];
	nextLink: string;
	statusCode: number;
	constructor()
	/**
	 * Initializes a new instance of the VirtualMachineListResponse class.
	 */
	constructor()
}

/**
 * Describes an OS profile.
 */
declare class AzureOSProfile {
	adminUsername: string;
	computerName: string;
	linuxConfiguration: AzureLinuxConfiguration;
	customData: string;
	windowsConfiguration: AzureWindowsConfiguration;
	secrets: any[];
	adminPassword: string;
	constructor()
	/**
	 * Initializes a new instance of the OSProfile class.
	 */
	constructor()
}

/**
 * The GetVM operation response.
 */
declare class AzureVirtualMachineGetResponse {
	virtualMachine: AzureVirtualMachine;
	requestId: string;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * Contains the parameters required to list virtual machine image versions.
 */
declare class AzureVirtualMachineImageListParameters {
	offer: string;
	skus: string;
	publisherName: string;
	filterExpression: string;
	location: string;
	constructor()
	constructor()
}

/**
 * The List Virtual Machine operation response.
 */
declare class AzureVirtualMachineSizeListResponse {
	virtualMachineSizes: any[];
	requestId: string;
	statusCode: number;
	constructor()
	/**
	 * Initializes a new instance of the VirtualMachineSizeListResponse class.
	 */
	constructor()
}

/**
 * Describes a Virtual Machine Image.
 */
declare class AzureVirtualMachineImage {
	readonly resourceGroup: AzureResourceGroupExtended;
	readonly product: string;
	oSDiskImage: AzureOSDiskImage;
	readonly displayName: string;
	readonly version: string;
	readonly operatingSystem: string;
	readonly offer: string;
	readonly internalIdString: string;
	purchasePlan: AzurePurchasePlan;
	name: string;
	readonly publisher: string;
	readonly location: string;
	readonly connection: AzureConnection;
	id: string;
	readonly sku: string;
	dataDiskImages: any[];
	constructor()
	/**
	 * Initializes a new instance of the VirtualMachineImage class with required
	 * arguments.
	 * @param id 
	 * @param name 
	 * @param location 
	 */
	constructor(id: string, name: string, location: string)
	/**
	 * Initializes a new instance of the VirtualMachineImage class.
	 */
	constructor()
}

/**
 * The instance view of a virtual machine extension.
 */
declare class AzureVirtualMachineExtensionInstanceView {
	subStatuses: any[];
	extensionType: string;
	name: string;
	statuses: any[];
	typeHandlerVersion: string;
	constructor()
	/**
	 * Initializes a new instance of the VirtualMachineExtensionInstanceView
	 * class.
	 */
	constructor()
}

/**
 * Create or update Availability Set parameters.
 */
declare class AzureAvailabilitySet {
	readonly resourceGroup: AzureResourceGroupExtended;
	platformFaultDomainCount: number;
	readonly displayName: string;
	platformUpdateDomainCount: number;
	type: string;
	virtualMachinesReferences: any[];
	tags: Properties;
	readonly internalIdString: string;
	name: string;
	statuses: any[];
	location: string;
	readonly connection: AzureConnection;
	id: string;
	constructor()
	/**
	 * Initializes a new instance of the AvailabilitySet class with required
	 * arguments.
	 * @param location 
	 */
	constructor(location: string)
	/**
	 * Initializes a new instance of the AvailabilitySet class.
	 */
	constructor()
	delete(): void;
}

/**
 * Describes a diagnostics profile.
 */
declare class AzureDiagnosticsProfile {
	bootDiagnostics: AzureBootDiagnostics;
	constructor()
	constructor()
}

/**
 * Describes the properties of a VM size.
 */
declare class AzureVirtualMachineSize {
	resourceDiskSizeInMB: number;
	oSDiskSizeInMB: number;
	maxDataDiskCount: number;
	memoryInMB: number;
	name: string;
	numberOfCores: number;
	constructor()
	constructor()
}

/**
 * Contains the parameters required to list publishers.
 */
declare class AzureVirtualMachineImageListPublishersParameters {
	location: string;
	constructor()
	/**
	 * Initializes a new instance of the
	 * VirtualMachineImageListPublishersParameters class with required
	 * arguments.
	 * @param location 
	 */
	constructor(location: string)
	/**
	 * Initializes a new instance of the
	 * VirtualMachineImageListPublishersParameters class.
	 */
	constructor()
}

/**
 * Describes the uri of a disk.
 */
declare class AzureVirtualHardDisk {
	uri: string;
	constructor()
	constructor()
}

/**
 * The instance view of a virtual machine extension handler.
 */
declare class AzureVirtualMachineExtensionHandlerInstanceView {
	type: string;
	typeHandlerVersion: string;
	status: AzureInstanceViewStatus;
	constructor()
	constructor()
}

/**
 * The Create Virtual Machine operation response.
 */
declare class AzureVirtualMachineCreateOrUpdateResponse {
	virtualMachine: AzureVirtualMachine;
	azureAsyncOperation: string;
	requestId: string;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * Describes a Virtual Machine.
 */
declare class AzureVirtualMachine {
	readonly resourceGroup: AzureResourceGroupExtended;
	readonly imageSku: string;
	readonly virtualMachineMemoryMb: string;
	hardwareProfile: AzureHardwareProfile;
	readonly displayName: string;
	type: string;
	instanceView: AzureVirtualMachineInstanceView;
	readonly powerState: string;
	readonly internalIdString: string;
	availabilitySetReference: AzureAvailabilitySetReference;
	readonly imageOffer: string;
	diagnosticsProfile: AzureDiagnosticsProfile;
	networkProfile: AzureNetworkProfile;
	readonly connection: AzureConnection;
	id: string;
	plan: AzurePlan;
	readonly virtualMachineMaxDiskCount: string;
	readonly imageVersion: string;
	readonly virtualMachineNumberCores: string;
	readonly operatingSystemType: string;
	readonly resourceGroupName: string;
	readonly osDiskUri: string;
	readonly imagePublisher: string;
	provisioningState: string;
	tags: Properties;
	oSProfile: AzureOSProfile;
	extensions: any[];
	readonly virtualMachineSize: string;
	storageProfile: AzureStorageProfile;
	name: string;
	location: string;
	readonly subscriptionId: string;
	readonly virtualMachineResourceDiskSizeMb: string;
	constructor()
	/**
	 * Initializes a new instance of the VirtualMachine class with required
	 * arguments.
	 * @param location 
	 */
	constructor(location: string)
	/**
	 * Initializes a new instance of the VirtualMachine class.
	 */
	constructor()
	delete(): void;
	beginRestarting(): void;
	beginPoweringOff(): void;
	beginDeleting(): void;
	start(): void;
	deallocate(): void;
	/**
	 * @param destinationContainerName 
	 * @param overwrite 
	 * @param virtualHardDiskNamePrefix 
	 */
	capture(destinationContainerName: string, overwrite: boolean, virtualHardDiskNamePrefix: string): void;
	powerOff(): void;
	beginDeallocating(): void;
	beginStarting(): void;
	/**
	 * @param destinationContainerName 
	 * @param overwrite 
	 * @param virtualHardDiskNamePrefix 
	 */
	beginCapture(destinationContainerName: string, overwrite: boolean, virtualHardDiskNamePrefix: string): void;
	restart(): void;
}

/**
 * Describes Compute Resource Usage.
 */
declare class AzureUsage {
	unit: AzureUsageUnit;
	limit: number;
	name: AzureUsageName;
	currentValue: number;
	constructor()
	constructor()
}

/**
 * Contains the parameteres required to get a virtual machine extension image.
 */
declare class AzureVirtualMachineExtensionImageGetParameters {
	publisherName: string;
	filterExpression: string;
	location: string;
	type: string;
	version: string;
	constructor()
	constructor()
}

/**
 * Describes a data disk.
 */
declare class AzureDataDisk {
	lun: number;
	virtualHardDisk: AzureVirtualHardDisk;
	name: string;
	sourceImage: AzureVirtualHardDisk;
	caching: string;
	createOption: string;
	diskSizeGB: number;
	constructor()
	constructor()
}

/**
 * Describes Windows Remote Management configuration of the VM
 */
declare class AzureWinRMConfiguration {
	listeners: any[];
	constructor()
	/**
	 * Initializes a new instance of the WinRMConfiguration class.
	 */
	constructor()
}

/**
 * Describes a network profile.
 */
declare class AzureNetworkProfile {
	networkInterfaces: any[];
	constructor()
	/**
	 * Initializes a new instance of the NetworkProfile class.
	 */
	constructor()
}

/**
 * Describes a storage profile.
 */
declare class AzureStorageProfile {
	dataDisks: any[];
	imageReference: AzureImageReference;
	oSDisk: AzureOSDisk;
	constructor()
	/**
	 * Initializes a new instance of the StorageProfile class.
	 */
	constructor()
}

/**
 * GET Availability Set operation response.
 */
declare class AzureAvailabilitySetGetResponse {
	availabilitySet: AzureAvailabilitySet;
	requestId: string;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * Instance view status.
 */
declare class AzureInstanceViewStatus {
	code: string;
	level: string;
	displayStatus: string;
	time: Date;
	message: string;
	constructor()
	constructor()
}

/**
 * Contains the parameters required to list skus.
 */
declare class AzureVirtualMachineImageListSkusParameters {
	offer: string;
	publisherName: string;
	location: string;
	constructor()
	/**
	 * Initializes a new instance of the VirtualMachineImageListSkusParameters
	 * class with required arguments.
	 * @param offer 
	 * @param publisherName 
	 * @param location 
	 */
	constructor(offer: string, publisherName: string, location: string)
	/**
	 * Initializes a new instance of the VirtualMachineImageListSkusParameters
	 * class.
	 */
	constructor()
}

/**
 * Contains the data disk images information.
 */
declare class AzureDataDiskImage {
	lun: number;
	constructor()
	/**
	 * Initializes a new instance of the DataDiskImage class with required
	 * arguments.
	 * @param lun 
	 */
	constructor(lun: number)
	/**
	 * Initializes a new instance of the DataDiskImage class.
	 */
	constructor()
}

/**
 * Describes Windows Configuration of the OS Profile.
 */
declare class AzureWindowsConfiguration {
	provisionVMAgent: boolean;
	winRMConfiguration: AzureWinRMConfiguration;
	timeZone: string;
	additionalUnattendContents: any[];
	enableAutomaticUpdates: boolean;
	constructor()
	/**
	 * Initializes a new instance of the WindowsConfiguration class.
	 */
	constructor()
}

/**
 * Contains a Source Key Vault relative URL.
 */
declare class AzureSourceVaultReference {
	referenceUri: string;
	constructor()
	constructor()
}

/**
 * Capture Virtual Machine parameters.
 */
declare class AzureVirtualMachineCaptureParameters {
	virtualHardDiskNamePrefix: string;
	destinationContainerName: string;
	overwrite: boolean;
	constructor()
	/**
	 * Initializes a new instance of the VirtualMachineCaptureParameters class.
	 */
	constructor()
	/**
	 * Initializes a new instance of the VirtualMachineCaptureParameters class
	 * with required arguments.
	 * @param virtualHardDiskNamePrefix 
	 * @param destinationContainerName 
	 * @param overwrite 
	 */
	constructor(virtualHardDiskNamePrefix: string, destinationContainerName: string, overwrite: boolean)
}

/**
 * The Usage Names.
 */
declare class AzureUsageName {
	value: string;
	localizedValue: string;
	constructor()
	constructor()
}

/**
 * Describes a set of certificates which are all in the same Key Vault.
 */
declare class AzureVaultSecretGroup {
	vaultCertificates: any[];
	sourceVault: AzureSourceVaultReference;
	constructor()
	/**
	 * Initializes a new instance of the VaultSecretGroup class.
	 */
	constructor()
}

/**
 * The instance view of the VM Agent running on the virtual machine.
 */
declare class AzureVirtualMachineAgentInstanceView {
	vMAgentVersion: string;
	statuses: any[];
	extensionHandlers: any[];
	constructor()
	/**
	 * Initializes a new instance of the VirtualMachineAgentInstanceView class.
	 */
	constructor()
}

/**
 * The Compute service response for long-running operations.
 */
declare class AzureComputeLongRunningOperationResponse {
	output: string;
	trackingOperationId: string;
	requestId: string;
	startTime: Date;
	endTime: Date;
	error: AzureApiError;
	statusCode: number;
	status: AzureComputeOperationStatus;
	constructor()
	constructor()
}

/**
 * Contains the parameters required to get a virtual machine image.
 */
declare class AzureVirtualMachineImageGetParameters {
	offer: string;
	skus: string;
	publisherName: string;
	location: string;
	version: string;
	constructor()
	constructor()
}

/**
 * Contains information about SSH certificate public key and the path on the
 * Linux VM where the public key is placed.
 */
declare class AzureSshPublicKey {
	path: string;
	keyData: string;
	constructor()
	constructor()
}

/**
 * Describes Windows Configuration of the OS Profile.
 */
declare class AzureLinuxConfiguration {
	disablePasswordAuthentication: boolean;
	sshConfiguration: AzureSshConfiguration;
	constructor()
	constructor()
}

/**
 * Used for establishing the purchase context of any 3rd Party artifact through
 * MarketPlace.
 */
declare class AzurePurchasePlan {
	product: string;
	name: string;
	publisher: string;
	constructor()
	/**
	 * Initializes a new instance of the PurchasePlan class with required
	 * arguments.
	 * @param publisher 
	 * @param name 
	 * @param product 
	 */
	constructor(publisher: string, name: string, product: string)
	/**
	 * Initializes a new instance of the PurchasePlan class.
	 */
	constructor()
}

/**
 * The instance view of the disk.
 */
declare class AzureDiskInstanceView {
	name: string;
	statuses: any[];
	constructor()
	/**
	 * Initializes a new instance of the DiskInstanceView class.
	 */
	constructor()
}

/**
 * Api error.
 */
declare class AzureApiError {
	code: string;
	innerError: AzureInnerError;
	details: any[];
	message: string;
	target: string;
	constructor()
	/**
	 * Initializes a new instance of the ApiError class.
	 */
	constructor()
}

/**
 * Contains the parameteres required to list virtual machine extension image
 * versions.
 */
declare class AzureVirtualMachineExtensionImageListVersionsParameters {
	publisherName: string;
	filterExpression: string;
	location: string;
	type: string;
	constructor()
	/**
	 * Initializes a new instance of the
	 * VirtualMachineExtensionImageListVersionsParameters class with required
	 * arguments.
	 * @param type 
	 * @param location 
	 * @param publisherName 
	 */
	constructor(type: string, location: string, publisherName: string)
	/**
	 * Initializes a new instance of the
	 * VirtualMachineExtensionImageListVersionsParameters class.
	 */
	constructor()
}

/**
 * The API entity reference.
 */
declare class AzureApiEntityReference {
	referenceUri: string;
	constructor()
	constructor()
}

/**
 * SSH configuration for Linux based VMs running on Azure
 */
declare class AzureSshConfiguration {
	publicKeys: any[];
	constructor()
	/**
	 * Initializes a new instance of the SshConfiguration class.
	 */
	constructor()
}

/**
 * Describes the base OS profile.
 */
declare class AzureOSProfileBase {
	adminUsername: string;
	linuxConfiguration: AzureLinuxConfiguration;
	customData: string;
	windowsConfiguration: AzureWindowsConfiguration;
	secrets: any[];
	adminPassword: string;
	constructor()
	/**
	 * Initializes a new instance of the OSProfileBase class.
	 */
	constructor()
}

/**
 * The get vm image operation response.
 */
declare class AzureVirtualMachineImageGetResponse {
	requestId: string;
	virtualMachineImage: AzureVirtualMachineImage;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * Inner error details.
 */
declare class AzureInnerError {
	exceptionType: string;
	errorDetail: string;
	constructor()
	constructor()
}

/**
 * The Get VM-Extension operation response.
 */
declare class AzureVirtualMachineExtensionGetResponse {
	requestId: string;
	virtualMachineExtension: AzureVirtualMachineExtension;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * The image reference.
 */
declare class AzureImageReference {
	offer: string;
	publisher: string;
	sku: string;
	version: string;
	constructor()
	constructor()
}

/**
 * Describes a hardware profile.
 */
declare class AzureHardwareProfile {
	virtualMachineSize: string;
	constructor()
	constructor()
}

/**
 * Api error base.
 */
declare class AzureApiErrorBase {
	code: string;
	message: string;
	target: string;
	constructor()
	constructor()
}

/**
 * Contains the parameteres required to list virtual machine extension image
 * types.
 */
declare class AzureVirtualMachineExtensionImageListTypesParameters {
	publisherName: string;
	location: string;
	constructor()
	/**
	 * Initializes a new instance of the
	 * VirtualMachineExtensionImageListTypesParameters class with required
	 * arguments.
	 * @param location 
	 * @param publisherName 
	 */
	constructor(location: string, publisherName: string)
	/**
	 * Initializes a new instance of the
	 * VirtualMachineExtensionImageListTypesParameters class.
	 */
	constructor()
}

/**
 * A list of virtual machine image resource information.
 */
declare class AzureVirtualMachineImageResourceList {
	requestId: string;
	resources: any[];
	statusCode: number;
	constructor()
	/**
	 * Initializes a new instance of the VirtualMachineImageResourceList class.
	 */
	constructor()
}

/**
 * Describes an Operating System disk.
 */
declare class AzureOSDisk {
	operatingSystemType: string;
	virtualHardDisk: AzureVirtualHardDisk;
	name: string;
	sourceImage: AzureVirtualHardDisk;
	caching: string;
	createOption: string;
	diskSizeGB: number;
	constructor()
	/**
	 * Initializes a new instance of the OSDisk class with required arguments.
	 * @param name 
	 * @param virtualHardDisk 
	 * @param createOption 
	 */
	constructor(name: string, virtualHardDisk: AzureVirtualHardDisk, createOption: string)
	/**
	 * Initializes a new instance of the OSDisk class.
	 */
	constructor()
}

/**
 * The List Usages operation response.
 */
declare class AzureListUsagesResponse {
	requestId: string;
	usages: any[];
	statusCode: number;
	constructor()
	/**
	 * Initializes a new instance of the ListUsagesResponse class.
	 */
	constructor()
}

/**
 * Describes an availability set reference.
 */
declare class AzureAvailabilitySetReference {
	referenceUri: string;
	constructor()
	constructor()
}

/**
 * Describes a single certificate reference in a Key Vault, and where the
 * certificate should reside on the VM.
 */
declare class AzureVaultCertificate {
	certificateUrl: string;
	certificateStore: string;
	constructor()
	constructor()
}

/**
 * The instance view of a virtual machine boot diagnostics.
 */
declare class AzureBootDiagnosticsInstanceView {
	consoleScreenshotBlobUri: string;
	serialConsoleLogBlobUri: string;
	constructor()
	constructor()
}

/**
 * Describes Protocol and thumbprint of Windows Remote Management listener
 */
declare class AzureWinRMListener {
	protocol: string;
	certificateUrl: string;
	constructor()
	constructor()
}

/**
 * Describes a virtual machine reference.
 */
declare class AzureVirtualMachineReference {
	referenceUri: string;
	constructor()
	constructor()
}

/**
 * The instance view of a virtual machine.
 */
declare class AzureVirtualMachineInstanceView {
	remoteDesktopThumbprint: string;
	extensions: any[];
	disks: any[];
	vMAgent: AzureVirtualMachineAgentInstanceView;
	statuses: any[];
	bootDiagnostics: AzureBootDiagnosticsInstanceView;
	platformUpdateDomain: number;
	platformFaultDomain: number;
	constructor()
	/**
	 * Initializes a new instance of the VirtualMachineInstanceView class.
	 */
	constructor()
}

/**
 * Contains the parameters required to list offers.
 */
declare class AzureVirtualMachineImageListOffersParameters {
	publisherName: string;
	location: string;
	constructor()
	/**
	 * Initializes a new instance of the VirtualMachineImageListOffersParameters
	 * class with required arguments.
	 * @param publisherName 
	 * @param location 
	 */
	constructor(publisherName: string, location: string)
	/**
	 * Initializes a new instance of the VirtualMachineImageListOffersParameters
	 * class.
	 */
	constructor()
}

/**
 * The List Availability Set operation response.
 */
declare class AzureAvailabilitySetListResponse {
	requestId: string;
	availabilitySets: any[];
	statusCode: number;
	constructor()
	/**
	 * Initializes a new instance of the AvailabilitySetListResponse class.
	 */
	constructor()
}

/**
 * The Create Availability Set operation response.
 */
declare class AzureAvailabilitySetCreateOrUpdateResponse {
	availabilitySet: AzureAvailabilitySet;
	requestId: string;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * The Compute Management Client.
 */
declare interface AzureComputeManagementClient {
	readonly virtualMachineExtensionsOperations: AzureVirtualMachineExtensionOperations;
	readonly virtualMachineExtensionImagesOperations: AzureVirtualMachineExtensionImageOperations;
	readonly virtualMachineImagesOperations: AzureVirtualMachineImageOperations;
	readonly apiVersion: string;
	readonly availabilitySetsOperations: AzureAvailabilitySetOperations;
	readonly virtualMachinesOperations: AzureVirtualMachineOperations;
	longRunningOperationRetryTimeout: number;
	longRunningOperationInitialTimeout: number;
	readonly virtualMachineSizesOperations: AzureVirtualMachineSizeOperations;
	readonly baseUri: string;
	readonly usageOperations: AzureComputeUsageOperations;
	/**
	 * The Get Operation Status operation returns the status of the specified
	 * operation. After calling an asynchronous operation, you can call
	 * GetLongRunningOperationStatus to determine whether the operation has
	 * succeeded, failed, or is still in progress.
	 * @param arg0 
	 */
	getLongRunningOperationStatus(arg0: string): AzureComputeLongRunningOperationResponse;
	/**
	 * Closes this stream and releases any system resources associated
	 * with it. If the stream is already closed then invoking this
	 * method has no effect.
	 * 
	 * <p> As noted in {@link AutoCloseable#close()}, cases where the
	 * close may fail require careful attention. It is strongly advised
	 * to relinquish the underlying resources and to internally
	 * <em>mark</em> the {@code Closeable} as closed, prior to throwing
	 * the {@code IOException}.
	 */
	close(): void;
	/**
	 * The Get Delete Operation Status operation returns the status of the
	 * specified operation. After calling an asynchronous operation, you can
	 * call GetDeleteOperationStatus to determine whether the operation has
	 * succeeded, failed, or is still in progress.
	 * @param arg0 
	 */
	getDeleteOperationStatus(arg0: string): AzureDeleteOperationResponse;
}

/**
 * Operations for managing the virtual machine images in compute management.
 */
declare interface AzureVirtualMachineImageOperations {
	/**
	 * Gets a list of virtual machine image offers.
	 * @param arg0 
	 */
	listOffers(arg0: AzureVirtualMachineImageListOffersParameters): AzureVirtualMachineImageResourceList;
	/**
	 * Gets a list of virtual machine image skus.
	 * @param arg0 
	 */
	listSkus(arg0: AzureVirtualMachineImageListSkusParameters): AzureVirtualMachineImageResourceList;
	/**
	 * Gets a virtual machine image.
	 * @param arg0 
	 */
	get(arg0: AzureVirtualMachineImageGetParameters): AzureVirtualMachineImageGetResponse;
	/**
	 * Gets a list of virtual machine images.
	 * @param arg0 
	 */
	list(arg0: AzureVirtualMachineImageListParameters): AzureVirtualMachineImageResourceList;
	/**
	 * Gets a list of virtual machine image publishers.
	 * @param arg0 
	 */
	listPublishers(arg0: AzureVirtualMachineImageListPublishersParameters): AzureVirtualMachineImageResourceList;
}

/**
 * Operations for managing the availability sets in compute management.
 */
declare interface AzureAvailabilitySetOperations {
	/**
	 * The operation to create or update the availability set.
	 * @param arg0 
	 * @param arg1 
	 */
	createOrUpdate(arg0: string, arg1: AzureAvailabilitySet): AzureAvailabilitySetCreateOrUpdateResponse;
	/**
	 * The operation to get the availability set.
	 * @param arg0 
	 * @param arg1 
	 */
	get(arg0: string, arg1: string): AzureAvailabilitySetGetResponse;
	/**
	 * The operation to delete the availability set.
	 * @param arg0 
	 * @param arg1 
	 */
	delete(arg0: string, arg1: string): AzureOperationResponse;
	/**
	 * The operation to list the availability sets.
	 * @param arg0 
	 */
	list(arg0: string): AzureAvailabilitySetListResponse;
	/**
	 * Lists virtual-machine-sizes available to be used for an availability set.
	 * @param arg0 
	 * @param arg1 
	 */
	listAvailableSizes(arg0: string, arg1: string): AzureVirtualMachineSizeListResponse;
}

/**
 * Operations for listing usage.
 */
declare interface AzureComputeUsageOperations {
	/**
	 * Lists compute usages for a subscription.
	 * @param arg0 
	 */
	list(arg0: string): AzureListUsagesResponse;
}

/**
 * Operations for managing the virtual machine extension images in compute
 * management.
 */
declare interface AzureVirtualMachineExtensionImageOperations {
	/**
	 * Gets a list of virtual machine extension image types.
	 * @param arg0 
	 */
	listTypes(arg0: AzureVirtualMachineExtensionImageListTypesParameters): AzureVirtualMachineImageResourceList;
	/**
	 * Gets a virtual machine extension image.
	 * @param arg0 
	 */
	get(arg0: AzureVirtualMachineExtensionImageGetParameters): AzureVirtualMachineExtensionImageGetResponse;
	/**
	 * Gets a list of virtual machine extension image versions.
	 * @param arg0 
	 */
	listVersions(arg0: AzureVirtualMachineExtensionImageListVersionsParameters): AzureVirtualMachineImageResourceList;
}

/**
 * Operations for managing the virtual machine extensions in compute management.
 */
declare interface AzureVirtualMachineExtensionOperations {
	/**
	 * The operation to get the extension.
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 */
	get(arg0: string, arg1: string, arg2: string): AzureVirtualMachineExtensionGetResponse;
	/**
	 * The operation to get an extension along with its instance view.
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 */
	getWithInstanceView(arg0: string, arg1: string, arg2: string): AzureVirtualMachineExtensionGetResponse;
	/**
	 * The operation to create or update the extension.
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 */
	beginCreatingOrUpdating(arg0: string, arg1: string, arg2: AzureVirtualMachineExtension): AzureVirtualMachineExtensionCreateOrUpdateResponse;
	/**
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 */
	delete(arg0: string, arg1: string, arg2: string): AzureDeleteOperationResponse;
	/**
	 * The operation to create or update the extension.
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 */
	createOrUpdate(arg0: string, arg1: string, arg2: AzureVirtualMachineExtension): AzureComputeLongRunningOperationResponse;
	/**
	 * The operation to delete the extension.
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 */
	beginDeleting(arg0: string, arg1: string, arg2: string): AzureDeleteOperationResponse;
}

/**
 * Operations for managing the virtual machines in compute management.
 */
declare interface AzureVirtualMachineOperations {
	/**
	 * The operation to power off (stop) a virtual machine.
	 * @param arg0 
	 * @param arg1 
	 */
	powerOff(arg0: string, arg1: string): AzureComputeLongRunningOperationResponse;
	/**
	 * Captures the VM by copying VirtualHardDisks of the VM and outputs a
	 * template that can be used to create similar VMs.
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 */
	beginCapturing(arg0: string, arg1: string, arg2: AzureVirtualMachineCaptureParameters): AzureComputeOperationResponse;
	/**
	 * The operation to delete a virtual machine.
	 * @param arg0 
	 * @param arg1 
	 */
	beginDeleting(arg0: string, arg1: string): AzureDeleteOperationResponse;
	/**
	 * The operation to create or update a virtual machine.
	 * @param arg0 
	 * @param arg1 
	 */
	beginCreatingOrUpdating(arg0: string, arg1: AzureVirtualMachine): AzureVirtualMachineCreateOrUpdateResponse;
	/**
	 * The operation to restart a virtual machine.
	 * @param arg0 
	 * @param arg1 
	 */
	restart(arg0: string, arg1: string): AzureComputeLongRunningOperationResponse;
	/**
	 * Lists virtual-machine-sizes available to be used for a virtual machine.
	 * @param arg0 
	 * @param arg1 
	 */
	listAvailableSizes(arg0: string, arg1: string): AzureVirtualMachineSizeListResponse;
	/**
	 * The operation to restart a virtual machine.
	 * @param arg0 
	 * @param arg1 
	 */
	beginRestarting(arg0: string, arg1: string): AzureComputeOperationResponse;
	/**
	 * The operation to get a virtual machine along with its instance view.
	 * @param arg0 
	 * @param arg1 
	 */
	getWithInstanceView(arg0: string, arg1: string): AzureVirtualMachineGetResponse;
	/**
	 * The operation to list virtual machines under a resource group.
	 * @param arg0 
	 */
	list(arg0: string): AzureVirtualMachineListResponse;
	/**
	 * Captures the VM by copying VirtualHardDisks of the VM and outputs a
	 * template that can be used to create similar VMs.
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 */
	capture(arg0: string, arg1: string, arg2: AzureVirtualMachineCaptureParameters): AzureComputeLongRunningOperationResponse;
	/**
	 * Gets the next page of Virtual Machines. NextLink is obtained by making a
	 * ListAll() callwhich fetches the first page of Virtual Machines and a
	 * link to fetch the next page.
	 * @param arg0 
	 */
	listNext(arg0: string): AzureVirtualMachineListResponse;
	/**
	 * Shuts down the Virtual Machine and releases the compute resources. You
	 * are not billed for the compute resources that this Virtual Machine uses.
	 * @param arg0 
	 * @param arg1 
	 */
	deallocate(arg0: string, arg1: string): AzureComputeLongRunningOperationResponse;
	/**
	 * The operation to start a virtual machine.
	 * @param arg0 
	 * @param arg1 
	 */
	beginStarting(arg0: string, arg1: string): AzureComputeOperationResponse;
	/**
	 * The operation to create or update a virtual machine.
	 * @param arg0 
	 * @param arg1 
	 */
	createOrUpdate(arg0: string, arg1: AzureVirtualMachine): AzureComputeLongRunningOperationResponse;
	/**
	 * Gets the list of Virtual Machines in the subscription. Use nextLink
	 * property in the response to get the next page of Virtual Machines. Do
	 * this till nextLink is not null to fetch all the Virtual Machines.
	 * @param arg0 
	 */
	listAll(arg0: AzureListParameters): AzureVirtualMachineListResponse;
	/**
	 * Shuts down the Virtual Machine and releases the compute resources. You
	 * are not billed for the compute resources that this Virtual Machine uses.
	 * @param arg0 
	 * @param arg1 
	 */
	beginDeallocating(arg0: string, arg1: string): AzureComputeOperationResponse;
	/**
	 * The operation to start a virtual machine.
	 * @param arg0 
	 * @param arg1 
	 */
	start(arg0: string, arg1: string): AzureComputeLongRunningOperationResponse;
	/**
	 * The operation to delete a virtual machine.
	 * @param arg0 
	 * @param arg1 
	 */
	delete(arg0: string, arg1: string): AzureDeleteOperationResponse;
	/**
	 * Sets the state of the VM as Generalized.
	 * @param arg0 
	 * @param arg1 
	 */
	generalize(arg0: string, arg1: string): AzureOperationResponse;
	/**
	 * The operation to get a virtual machine.
	 * @param arg0 
	 * @param arg1 
	 */
	get(arg0: string, arg1: string): AzureVirtualMachineGetResponse;
	/**
	 * The operation to power off (stop) a virtual machine.
	 * @param arg0 
	 * @param arg1 
	 */
	beginPoweringOff(arg0: string, arg1: string): AzureComputeOperationResponse;
}

/**
 * Operations for listing virtual machine sizes available in a region.
 */
declare interface AzureVirtualMachineSizeOperations {
	/**
	 * Lists virtual-machine-sizes available in a location for a subscription.
	 * @param arg0 
	 */
	list(arg0: string): AzureVirtualMachineSizeListResponse;
}

declare class AzureVirtualMachineImagePublisher {
	readonly name: string;
	readonly location: string;
	readonly id: string;
	constructor()
	/**
	 * @param virtualMachineImageResource 
	 */
	constructor(virtualMachineImageResource: AzureVirtualMachineImageResource)
}

declare class AzureVirtualMachineImageOffer {
	readonly name: string;
	readonly location: string;
	readonly id: string;
	constructor()
	/**
	 * @param virtualMachineImageResource 
	 */
	constructor(virtualMachineImageResource: AzureVirtualMachineImageResource)
}

declare class AzureVirtualMachineImageSku {
	readonly name: string;
	readonly location: string;
	readonly id: string;
	constructor()
	/**
	 * @param virtualMachineImageResource 
	 */
	constructor(virtualMachineImageResource: AzureVirtualMachineImageResource)
}

/**
 * The access keys for the storage account.
 */
declare class AzureStorageAccountKeys {
	key1: string;
	key2: string;
	constructor()
	constructor()
}

/**
 * The RegenerateKey operation response.
 */
declare class AzureStorageAccountRegenerateKeyResponse {
	storageAccountKeys: AzureStorageAccountKeys;
	requestId: string;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * The list storage accounts operation response.
 */
declare class AzureStorageAccountListResponse {
	storageAccounts: any[];
	requestId: string;
	statusCode: number;
	constructor()
	/**
	 * Initializes a new instance of the StorageAccountListResponse class.
	 */
	constructor()
}

/**
 * The parameters to provide for the account.
 */
declare class AzureStorageAccountCreateParameters {
	accountType: AzureAccountType;
	location: string;
	tags: Properties;
	constructor()
	/**
	 * Initializes a new instance of the StorageAccountCreateParameters class
	 * with required arguments.
	 * @param accountType 
	 * @param location 
	 */
	constructor(accountType: AzureAccountType, location: string)
	/**
	 * Initializes a new instance of the StorageAccountCreateParameters class.
	 */
	constructor()
}

/**
 * The CheckNameAvailability operation response.
 */
declare class AzureCheckNameAvailabilityResponse {
	reason: AzureReason;
	requestId: string;
	nameAvailable: boolean;
	message: string;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * The URIs that are used to perform a retrieval of a public blob, queue or
 * table object.
 */
declare class AzureEndpoints {
	blob: string;
	file: string;
	queue: string;
	table: string;
	constructor()
	constructor()
}

/**
 * The Update storage account operation response.
 */
declare class AzureStorageAccountUpdateResponse {
	requestId: string;
	storageAccount: AzureStorageAccount;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * The custom domain assigned to this storage account. This can be set via
 * Update.
 */
declare class AzureCustomDomain {
	useSubDomain: boolean;
	name: string;
	constructor()
	/**
	 * Initializes a new instance of the CustomDomain class with required
	 * arguments.
	 * @param name 
	 */
	constructor(name: string)
	/**
	 * Initializes a new instance of the CustomDomain class.
	 */
	constructor()
}

/**
 * The parameters to update on the account.
 */
declare class AzureStorageAccountUpdateParameters {
	accountType: AzureAccountType;
	customDomain: AzureCustomDomain;
	tags: Properties;
	constructor()
	/**
	 * Initializes a new instance of the StorageAccountUpdateParameters class.
	 */
	constructor()
}

/**
 * The storage account.
 */
declare class AzureStorageAccount {
	readonly resourceGroup: AzureResourceGroupExtended;
	creationTime: Date;
	readonly displayName: string;
	secondaryLocation: string;
	lastGeoFailoverTime: Date;
	accountType: AzureAccountType;
	primaryEndpoints: AzureEndpoints;
	secondaryEndpoints: AzureEndpoints;
	provisioningState: AzureProvisioningState;
	type: string;
	statusOfSecondary: AzureAccountStatus;
	customDomain: AzureCustomDomain;
	tags: Properties;
	primaryLocation: string;
	readonly internalIdString: string;
	name: string;
	location: string;
	readonly connection: AzureConnection;
	statusOfPrimary: AzureAccountStatus;
	id: string;
	constructor()
	/**
	 * Initializes a new instance of the StorageAccount class with required
	 * arguments.
	 * @param location 
	 */
	constructor(location: string)
	/**
	 * Initializes a new instance of the StorageAccount class.
	 */
	constructor()
	delete(): void;
	/**
	 * @param containerName 
	 * @param vhdName 
	 */
	deleteBlob(containerName: string, vhdName: string): void;
}

/**
 * The List Usages operation response.
 */
declare class AzureUsageListResponse {
	requestId: string;
	statusCode: number;
	constructor()
	/**
	 * Initializes a new instance of the UsageListResponse class.
	 */
	constructor()
}

/**
 * The ListKeys operation response.
 */
declare class AzureStorageAccountListKeysResponse {
	storageAccountKeys: AzureStorageAccountKeys;
	requestId: string;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * The Create storage account operation response.
 */
declare class AzureStorageAccountCreateResponse {
	retryAfter: number;
	requestId: string;
	operationStatusLink: string;
	storageAccount: AzureStorageAccount;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * The Get storage account operation response.
 */
declare class AzureStorageAccountGetPropertiesResponse {
	requestId: string;
	storageAccount: AzureStorageAccount;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * The Storage Management Client.
 */
declare interface AzureStorageManagementClient {
	readonly storageAccountsOperations: AzureStorageAccountOperations;
	readonly apiVersion: string;
	longRunningOperationRetryTimeout: number;
	longRunningOperationInitialTimeout: number;
	readonly baseUri: string;
	readonly usageOperations: AzureStorageUsageOperations;
	/**
	 * The Get Create Operation Status operation returns the status of the
	 * specified create operation. After calling the asynchronous Begin Create
	 * operation, you can call Get Create Operation Status to determine whether
	 * the operation has succeeded, failed, or is still in progress.
	 * @param arg0 
	 */
	getCreateOperationStatus(arg0: string): AzureStorageAccountCreateResponse;
	/**
	 * Closes this stream and releases any system resources associated
	 * with it. If the stream is already closed then invoking this
	 * method has no effect.
	 * 
	 * <p> As noted in {@link AutoCloseable#close()}, cases where the
	 * close may fail require careful attention. It is strongly advised
	 * to relinquish the underlying resources and to internally
	 * <em>mark</em> the {@code Closeable} as closed, prior to throwing
	 * the {@code IOException}.
	 */
	close(): void;
}

/**
 * Operations for managing storage accounts.
 */
declare interface AzureStorageAccountOperations {
	/**
	 * Updates the account type or tags for a storage account. It can also be
	 * used to add a custom domain (note that custom domains cannot be added
	 * via the Create operation). Only one custom domain is supported per
	 * storage account. In order to replace a custom domain, the old value must
	 * be cleared before a new value may be set. To clear a custom domain,
	 * simply update the custom domain with empty string. Then call update
	 * again with the new cutsom domain name. The update API can only be used
	 * to update one of tags, accountType, or customDomain per call. To update
	 * multiple of these properties, call the API multiple times with one
	 * change per call. This call does not change the storage keys for the
	 * account. If you want to change storage account keys, use the
	 * RegenerateKey operation. The location and name of the storage account
	 * cannot be changed after creation.
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 */
	update(arg0: string, arg1: string, arg2: AzureStorageAccountUpdateParameters): AzureStorageAccountUpdateResponse;
	/**
	 * Asynchronously creates a new storage account with the specified
	 * parameters. Existing accounts cannot be updated with this API and should
	 * instead use the Update Storage Account API. If an account is already
	 * created and subsequent PUT request is issued with exact same set of
	 * properties, then HTTP 200 would be returned.
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 */
	beginCreate(arg0: string, arg1: string, arg2: AzureStorageAccountCreateParameters): AzureStorageAccountCreateResponse;
	/**
	 * Regenerates the access keys for the specified storage account.
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 */
	regenerateKey(arg0: string, arg1: string, arg2: string): AzureStorageAccountRegenerateKeyResponse;
	/**
	 * Lists the access keys for the specified storage account.
	 * @param arg0 
	 * @param arg1 
	 */
	listKeys(arg0: string, arg1: string): AzureStorageAccountListKeysResponse;
	/**
	 * Lists all the storage accounts available under the given resource group.
	 * Note that storage keys are not returned; use the ListKeys operation for
	 * this.
	 * @param arg0 
	 */
	listByResourceGroup(arg0: string): AzureStorageAccountListResponse;
	/**
	 * Returns the properties for the specified storage account including but
	 * not limited to name, account type, location, and account status. The
	 * ListKeys operation should be used to retrieve storage keys.
	 * @param arg0 
	 * @param arg1 
	 */
	getProperties(arg0: string, arg1: string): AzureStorageAccountGetPropertiesResponse;
	/**
	 * Checks that account name is valid and is not in use.
	 * @param arg0 
	 */
	checkNameAvailability(arg0: string): AzureCheckNameAvailabilityResponse;
	/**
	 * Lists all the storage accounts available under the subscription. Note
	 * that storage keys are not returned; use the ListKeys operation for this.
	 */
	list(): AzureStorageAccountListResponse;
	/**
	 * Deletes a storage account in Microsoft Azure.
	 * @param arg0 
	 * @param arg1 
	 */
	delete(arg0: string, arg1: string): AzureOperationResponse;
	/**
	 * Asynchronously creates a new storage account with the specified
	 * parameters. Existing accounts cannot be updated with this API and should
	 * instead use the Update Storage Account API. If an account is already
	 * created and subsequent create request is issued with exact same set of
	 * properties, the request succeeds.The max number of storage accounts that
	 * can be created per subscription is limited to 100.
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 */
	create(arg0: string, arg1: string, arg2: AzureStorageAccountCreateParameters): AzureStorageAccountCreateResponse;
}

/**
 * Operations for listing usage.
 */
declare interface AzureStorageUsageOperations {
	/**
	 * Gets the current usage count and the limit for the resources under the
	 * subscription.
	 */
	list(): AzureUsageListResponse;
}

/**
 * Response for GetLocalNetworkgateway Api Service call.
 */
declare class AzureLocalNetworkGatewayGetResponse {
	requestId: string;
	localNetworkGateway: AzureLocalNetworkGateway;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * Response for PutNetworkInterface Api Service call
 */
declare class AzureNetworkInterfacePutResponse {
	azureAsyncOperation: string;
	retryAfter: number;
	requestId: string;
	networkInterface: AzureNetworkInterface;
	error: AzureError;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * If the resource provide needs to return an error to any operation, it should
 * return the appropriate HTTP error code and a message body as can be seen
 * below.The message should be localized per the Accept-Language header
 * specified in the original request such thatit could be directly be exposed
 * to users
 */
declare class AzureRetriableOperationResponse {
	retryAfter: number;
	requestId: string;
	error: AzureError;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * IpConfiguration for Virtual network gateway
 */
declare class AzureVirtualNetworkGatewayIpConfiguration {
	subnet: AzureResourceId;
	publicIpAddress: AzureResourceId;
	name: string;
	etag: string;
	id: string;
	provisioningState: string;
	privateIpAddress: string;
	privateIpAllocationMethod: string;
	constructor()
	constructor()
}

/**
 * If the resource provide needs to return an error to any operation, it should
 * return the appropriate HTTP error code and a message body as can be seen
 * below.The message should be localized per the Accept-Language header
 * specified in the original request such thatit could be directly be exposed
 * to users
 */
declare class AzureResourceProviderErrorResponse {
	requestId: string;
	error: AzureError;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * Backend Address Pool of application gateway
 */
declare class AzureApplicationGatewayBackendAddressPool {
	name: string;
	backendIPConfigurations: any[];
	etag: string;
	provisioningState: string;
	id: string;
	backendAddresses: any[];
	constructor()
	/**
	 * Initializes a new instance of the ApplicationGatewayBackendAddressPool
	 * class.
	 */
	constructor()
}

/**
 * The response body contains the status of the specified asynchronous
 * operation, indicating whether it has succeeded, is inprogress, or has
 * failed. Note that this status is distinct from the HTTP status code returned
 * for the Get Operation Status operation itself. If the asynchronous operation
 * succeeded, the response body includes the HTTP status code for the
 * successful request. If the asynchronous operation failed, the response body
 * includes the HTTP status code for the failed request and error information
 * regarding the failure.
 */
declare class AzureAzureAsyncOperationResponse {
	retryAfter: number;
	requestId: string;
	error: AzureError;
	status: string;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * Response for PUT NetworkSecurityGroups Api Service call
 */
declare class AzureNetworkSecurityGroupPutResponse {
	azureAsyncOperation: string;
	retryAfter: number;
	requestId: string;
	error: AzureError;
	networkSecurityGroup: AzureNetworkSecurityGroup;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * Route resource
 */
declare class AzureRoute {
	readonly resourceGroup: AzureResourceGroupExtended;
	readonly displayName: string;
	readonly addressPrefix: string;
	nextHopType: string;
	readonly internalIdString: string;
	name: string;
	readonly connection: AzureConnection;
	etag: string;
	nextHopIpAddress: string;
	id: string;
	provisioningState: string;
	constructor()
	/**
	 * Initializes a new instance of the Route class with required arguments.
	 * @param nextHopType 
	 */
	constructor(nextHopType: string)
	/**
	 * Initializes a new instance of the Route class.
	 */
	constructor()
	delete(): void;
}

/**
 * Response for ListVirtualNetworks Api Service call
 */
declare class AzureVirtualNetworkListResponse {
	requestId: string;
	nextLink: string;
	statusCode: number;
	virtualNetworks: any[];
	constructor()
	/**
	 * Initializes a new instance of the VirtualNetworkListResponse class.
	 */
	constructor()
}

/**
 * Response for ListLocalNetworkGateways Api service call
 */
declare class AzureLocalNetworkGatewayListResponse {
	localNetworkGateways: any[];
	requestId: string;
	nextLink: string;
	statusCode: number;
	constructor()
	/**
	 * Initializes a new instance of the LocalNetworkGatewayListResponse class.
	 */
	constructor()
}

/**
 * Response for PutVirtualNetworkGatewayConnectionResetSharedKey Api Service call
 */
declare class AzureConnectionResetSharedKeyPutResponse {
	azureAsyncOperation: string;
	retryAfter: number;
	requestId: string;
	connectionResetSharedKey: AzureConnectionResetSharedKey;
	error: AzureError;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * Inbound NAT rule of the loadbalancer
 */
declare class AzureInboundNatRule {
	protocol: string;
	enableFloatingIP: boolean;
	backendPort: number;
	frontendIPConfiguration: AzureResourceId;
	backendIPConfiguration: AzureResourceId;
	idleTimeoutInMinutes: number;
	name: string;
	etag: string;
	frontendPort: number;
	id: string;
	provisioningState: string;
	constructor()
	/**
	 * Initializes a new instance of the InboundNatRule class with required
	 * arguments.
	 * @param protocol 
	 * @param frontendPort 
	 * @param enableFloatingIP 
	 */
	constructor(protocol: string, frontendPort: number, enableFloatingIP: boolean)
	/**
	 * Initializes a new instance of the InboundNatRule class.
	 */
	constructor()
}

/**
 * Request routing rule of application gateway
 */
declare class AzureApplicationGatewayRequestRoutingRule {
	httpListener: AzureResourceId;
	ruleType: string;
	name: string;
	etag: string;
	backendAddressPool: AzureResourceId;
	id: string;
	provisioningState: string;
	backendHttpSettings: AzureResourceId;
	constructor()
	constructor()
}

/**
 * Response for ListNetworkInterface Api service call
 */
declare class AzureNetworkInterfaceListResponse {
	networkInterfaces: any[];
	requestId: string;
	nextLink: string;
	statusCode: number;
	constructor()
	/**
	 * Initializes a new instance of the NetworkInterfaceListResponse class.
	 */
	constructor()
}

/**
 * Response for GetNetworkSecurityGroup Api service call
 */
declare class AzureNetworkSecurityGroupGetResponse {
	requestId: string;
	networkSecurityGroup: AzureNetworkSecurityGroup;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * A common class for general resource information
 */
declare class AzureLocalNetworkGateway {
	localNetworkAddressSpace: AzureAddressSpace;
	resourceGuid: string;
	gatewayIpAddress: string;
	name: string;
	location: string;
	etag: string;
	id: string;
	provisioningState: string;
	type: string;
	tags: Properties;
	constructor()
	/**
	 * Initializes a new instance of the LocalNetworkGateway class with required
	 * arguments.
	 * @param location 
	 */
	constructor(location: string)
	/**
	 * Initializes a new instance of the LocalNetworkGateway class.
	 */
	constructor()
}

/**
 * Response for PutLocalNetworkGateway Api Service call
 */
declare class AzureLocalNetworkGatewayPutResponse {
	azureAsyncOperation: string;
	retryAfter: number;
	requestId: string;
	error: AzureError;
	localNetworkGateway: AzureLocalNetworkGateway;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * Network security rule
 */
declare class AzureSecurityRule {
	access: string;
	description: string;
	provisioningState: string;
	priority: number;
	destinationAddressPrefix: string;
	protocol: string;
	sourcePortRange: string;
	sourceAddressPrefix: string;
	destinationPortRange: string;
	name: string;
	etag: string;
	id: string;
	direction: string;
	constructor()
	constructor()
}

/**
 * Contains FQDN of the DNS record associated with the public IP address
 */
declare class AzurePublicIpAddressDnsSettings {
	reverseFqdn: string;
	fqdn: string;
	domainNameLabel: string;
	constructor()
	constructor()
}

/**
 * PublicIPAddress resource
 */
declare class AzurePublicIpAddress {
	readonly resourceGroup: AzureResourceGroupExtended;
	readonly displayName: string;
	resourceGuid: string;
	ipAddress: string;
	idleTimeoutInMinutes: number;
	provisioningState: string;
	type: string;
	tags: Properties;
	dnsSettings: AzurePublicIpAddressDnsSettings;
	ipConfiguration: AzureResourceId;
	readonly internalIdString: string;
	name: string;
	location: string;
	readonly connection: AzureConnection;
	etag: string;
	publicIpAllocationMethod: string;
	id: string;
	constructor()
	/**
	 * Initializes a new instance of the PublicIpAddress class with required
	 * arguments.
	 * @param publicIpAllocationMethod 
	 * @param location 
	 */
	constructor(publicIpAllocationMethod: string, location: string)
	/**
	 * Initializes a new instance of the PublicIpAddress class.
	 */
	constructor()
	delete(): void;
}

/**
 * Response for PutVirtualNetworkGateway Api Service call
 */
declare class AzureVirtualNetworkGatewayPutResponse {
	azureAsyncOperation: string;
	retryAfter: number;
	requestId: string;
	virtualNetworkGateway: AzureVirtualNetworkGateway;
	error: AzureError;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * A common class for general resource information
 */
declare class AzureVirtualNetworkGateway {
	ipConfigurations: any[];
	enableBgp: boolean;
	gatewayType: string;
	resourceGuid: string;
	name: string;
	vpnType: string;
	location: string;
	etag: string;
	id: string;
	provisioningState: string;
	type: string;
	tags: Properties;
	constructor()
	/**
	 * Initializes a new instance of the VirtualNetworkGateway class with
	 * required arguments.
	 * @param location 
	 */
	constructor(location: string)
	/**
	 * Initializes a new instance of the VirtualNetworkGateway class.
	 */
	constructor()
}

/**
 * Response for PUT Routes Api Service call
 */
declare class AzureRoutePutResponse {
	azureAsyncOperation: string;
	route: AzureRoute;
	retryAfter: number;
	requestId: string;
	error: AzureError;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * Response for PutVirtualNetworks API service calls.
 */
declare class AzureVirtualNetworkPutResponse {
	azureAsyncOperation: string;
	retryAfter: number;
	requestId: string;
	error: AzureError;
	virtualNetwork: AzureVirtualNetwork;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * Response for PutSubnet Api service call
 */
declare class AzureSubnetPutResponse {
	subnet: AzureSubnet;
	azureAsyncOperation: string;
	retryAfter: number;
	requestId: string;
	error: AzureError;
	statusCode: number;
	constructor()
	constructor()
}

declare class AzureError {
	code: string;
	innerError: string;
	details: any[];
	message: string;
	target: string;
	constructor()
	/**
	 * Initializes a new instance of the Error class.
	 */
	constructor()
}

/**
 * SSL certificates of application gateway
 */
declare class AzureApplicationGatewaySslCertificate {
	password: string;
	data: string;
	name: string;
	etag: string;
	id: string;
	provisioningState: string;
	publicCertData: string;
	constructor()
	constructor()
}

/**
 * Response for GetVirtualNetworks API service calls.
 */
declare class AzureVirtualNetworkGetResponse {
	requestId: string;
	virtualNetwork: AzureVirtualNetwork;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * Response for ListSecurityRule Api service callRetrieves all security rules
 * that belongs to a network security group
 */
declare class AzureSecurityRuleListResponse {
	requestId: string;
	securityRules: any[];
	nextLink: string;
	statusCode: number;
	constructor()
	/**
	 * Initializes a new instance of the SecurityRuleListResponse class.
	 */
	constructor()
}

declare class AzureResourceProperties {
	provisioningState: string;
	constructor()
	constructor()
}

/**
 * DHCPOptions contains an array of DNS servers available to VMs deployed in the
 * virtual networkStandard DHCP option for a subnet overrides VNET DHCP options.
 */
declare class AzureDhcpOptions {
	dnsServers: any[];
	constructor()
	/**
	 * Initializes a new instance of the DhcpOptions class.
	 */
	constructor()
}

/**
 * Subnet in a VirtualNework resource
 */
declare class AzureSubnet {
	ipConfigurations: any[];
	readonly resourceGroup: AzureResourceGroupExtended;
	routeTable: AzureResourceId;
	readonly displayName: string;
	readonly addressPrefix: string;
	readonly internalIdString: string;
	name: string;
	readonly connection: AzureConnection;
	etag: string;
	id: string;
	provisioningState: string;
	networkSecurityGroup: AzureResourceId;
	constructor()
	/**
	 * Initializes a new instance of the Subnet class with required arguments.
	 * @param addressPrefix 
	 */
	constructor(addressPrefix: string)
	/**
	 * Initializes a new instance of the Subnet class.
	 */
	constructor()
	delete(): void;
}

declare class AzureErrorDetails {
	code: string;
	message: string;
	target: string;
	constructor()
	constructor()
}

/**
 * Response for ListLoadBalancers Api service call
 */
declare class AzureLoadBalancerListResponse {
	requestId: string;
	loadBalancers: any[];
	nextLink: string;
	statusCode: number;
	constructor()
	/**
	 * Initializes a new instance of the LoadBalancerListResponse class.
	 */
	constructor()
}

/**
 * Response for ListSubnets Api service callRetrieves all subnet that belongs to
 * a virtual network
 */
declare class AzureSubnetListResponse {
	requestId: string;
	subnets: any[];
	nextLink: string;
	statusCode: number;
	constructor()
	/**
	 * Initializes a new instance of the SubnetListResponse class.
	 */
	constructor()
}

/**
 * Response for CheckDnsNameAvailability Api Service call
 */
declare class AzureDnsNameAvailabilityResponse {
	dnsNameAvailability: boolean;
	requestId: string;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * Backend Address of application gateway
 */
declare class AzureApplicationGatewayBackendAddress {
	fqdn: string;
	ipAddress: string;
	constructor()
	constructor()
}

/**
 * A common class for general resource information
 */
declare class AzureTopLevelResource {
	name: string;
	etag: string;
	location: string;
	id: string;
	type: string;
	tags: Properties;
	constructor()
	/**
	 * Initializes a new instance of the TopLevelResource class with required
	 * arguments.
	 * @param location 
	 */
	constructor(location: string)
	/**
	 * Initializes a new instance of the TopLevelResource class.
	 */
	constructor()
}

/**
 * Response for GetSecurityRule Api service call
 */
declare class AzureSecurityRuleGetResponse {
	requestId: string;
	securityRule: AzureSecurityRule;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * Frontend IP address of the load balancer
 */
declare class AzureFrontendIpConfiguration {
	inboundNatPools: any[];
	subnet: AzureResourceId;
	inboundNatRules: any[];
	loadBalancingRules: any[];
	publicIpAddress: AzureResourceId;
	name: string;
	etag: string;
	id: string;
	provisioningState: string;
	privateIpAddress: string;
	privateIpAllocationMethod: string;
	outboundNatRules: any[];
	constructor()
	/**
	 * Initializes a new instance of the FrontendIpConfiguration class.
	 */
	constructor()
}

/**
 * Response of a GET ApplicationGateway operation
 */
declare class AzureApplicationGatewayGetResponse {
	requestId: string;
	applicationGateway: AzureApplicationGateway;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * ApplicationGateways resource
 */
declare class AzureApplicationGateway {
	sslCertificates: any[];
	resourceGuid: string;
	backendHttpSettingsCollection: any[];
	provisioningState: string;
	type: string;
	requestRoutingRules: any[];
	frontendPorts: any[];
	tags: Properties;
	gatewayIPConfigurations: any[];
	name: string;
	location: string;
	etag: string;
	operationalState: string;
	id: string;
	frontendIPConfigurations: any[];
	backendAddressPools: any[];
	sku: AzureApplicationGatewaySku;
	httpListeners: any[];
	constructor()
	/**
	 * Initializes a new instance of the ApplicationGateway class.
	 */
	constructor()
	/**
	 * Initializes a new instance of the ApplicationGateway class with required
	 * arguments.
	 * @param location 
	 */
	constructor(location: string)
}

/**
 * Frontend IP configuration of application gateway
 */
declare class AzureApplicationGatewayFrontendIPConfiguration {
	subnet: AzureResourceId;
	privateIPAllocationMethod: string;
	privateIPAddress: string;
	name: string;
	etag: string;
	id: string;
	provisioningState: string;
	publicIPAddress: AzureResourceId;
	constructor()
	constructor()
}

/**
 * Response for ListPublicIpAddresses Api service call
 */
declare class AzurePublicIpAddressListResponse {
	publicIpAddresses: any[];
	requestId: string;
	nextLink: string;
	statusCode: number;
	constructor()
	/**
	 * Initializes a new instance of the PublicIpAddressListResponse class.
	 */
	constructor()
}

/**
 * Response for GetPublicIpAddress Api Service call
 */
declare class AzurePublicIpAddressGetResponse {
	publicIpAddress: AzurePublicIpAddress;
	requestId: string;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * Virtual Network resource
 */
declare class AzureVirtualNetwork {
	readonly resourceGroup: AzureResourceGroupExtended;
	readonly displayName: string;
	resourceGuid: string;
	addressSpace: AzureAddressSpace;
	readonly virtualNetworkSubnets: AzureSubnet[];
	dhcpOptions: AzureDhcpOptions;
	provisioningState: string;
	type: string;
	tags: Properties;
	readonly internalIdString: string;
	name: string;
	location: string;
	readonly connection: AzureConnection;
	etag: string;
	subnets: any[];
	id: string;
	constructor()
	/**
	 * Initializes a new instance of the VirtualNetwork class with required
	 * arguments.
	 * @param location 
	 */
	constructor(location: string)
	/**
	 * Initializes a new instance of the VirtualNetwork class.
	 */
	constructor()
	delete(): void;
	/**
	 * @param subnetName 
	 */
	getSubnet(subnetName: string): AzureSubnet;
}

/**
 * Response for ListVirtualNetworkGateways Api service call
 */
declare class AzureVirtualNetworkGatewayListResponse {
	requestId: string;
	virtualNetworkGateways: any[];
	nextLink: string;
	statusCode: number;
	constructor()
	/**
	 * Initializes a new instance of the VirtualNetworkGatewayListResponse class.
	 */
	constructor()
}

/**
 * IP configuration of application gateway
 */
declare class AzureApplicationGatewayIPConfiguration {
	subnet: AzureResourceId;
	name: string;
	etag: string;
	provisioningState: string;
	id: string;
	constructor()
	constructor()
}

/**
 * IPConfiguration in a NetworkInterface
 */
declare class AzureNetworkInterfaceIpConfiguration {
	subnet: AzureResourceId;
	loadBalancerInboundNatRules: any[];
	publicIpAddress: AzureResourceId;
	name: string;
	etag: string;
	id: string;
	provisioningState: string;
	loadBalancerBackendAddressPools: any[];
	privateIpAddress: string;
	privateIpAllocationMethod: string;
	constructor()
	/**
	 * Initializes a new instance of the NetworkInterfaceIpConfiguration class.
	 */
	constructor()
}

/**
 * Http listener of application gateway
 */
declare class AzureApplicationGatewayHttpListener {
	protocol: string;
	sslCertificate: AzureResourceId;
	frontendIPConfiguration: AzureResourceId;
	name: string;
	etag: string;
	frontendPort: AzureResourceId;
	id: string;
	provisioningState: string;
	constructor()
	constructor()
}

/**
 * AddressSpace contains an array of IP address ranges that can be used by
 * subnets
 */
declare class AzureAddressSpace {
	addressPrefixes: any[];
	constructor()
	/**
	 * Initializes a new instance of the AddressSpace class.
	 */
	constructor()
}

/**
 * A common class for general resource information
 */
declare class AzureChildResource {
	name: string;
	etag: string;
	id: string;
	constructor()
	constructor()
}

/**
 * Dns Settings of a network interface
 */
declare class AzureNetworkInterfaceDnsSettings {
	appliedDnsServers: any[];
	internalDnsNameLabel: string;
	internalFqdn: string;
	dnsServers: any[];
	constructor()
	/**
	 * Initializes a new instance of the NetworkInterfaceDnsSettings class.
	 */
	constructor()
}

/**
 * The List Usages operation response.
 */
declare class AzureUsagesListResponse {
	requestId: string;
	statusCode: number;
	constructor()
	/**
	 * Initializes a new instance of the UsagesListResponse class.
	 */
	constructor()
}

/**
 * Response for ListRouteTable Api Service call
 */
declare class AzureRouteTableListResponse {
	requestId: string;
	routeTables: any[];
	nextLink: string;
	statusCode: number;
	constructor()
	/**
	 * Initializes a new instance of the RouteTableListResponse class.
	 */
	constructor()
}

/**
 * Response for CreateOrUpdateVirtualNetworkGatewayConnection Api Service call
 */
declare class AzureVirtualNetworkGatewayConnectionPutResponse {
	azureAsyncOperation: string;
	retryAfter: number;
	requestId: string;
	virtualNetworkGatewayConnection: AzureVirtualNetworkGatewayConnection;
	error: AzureError;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * A common class for general resource information
 */
declare class AzureVirtualNetworkGatewayConnection {
	sharedKey: string;
	resourceGuid: string;
	routingWeight: number;
	provisioningState: string;
	type: string;
	connectionType: string;
	tags: Properties;
	localNetworkGateway2: AzureLocalNetworkGateway;
	name: string;
	virtualNetworkGateway1: AzureVirtualNetworkGateway;
	virtualNetworkGateway2: AzureVirtualNetworkGateway;
	location: string;
	etag: string;
	id: string;
	constructor()
	/**
	 * Initializes a new instance of the VirtualNetworkGatewayConnection class.
	 */
	constructor()
	/**
	 * Initializes a new instance of the VirtualNetworkGatewayConnection class
	 * with required arguments.
	 * @param location 
	 */
	constructor(location: string)
}

/**
 * Response for ListVirtualNetworkGatewayConnections Api service call
 */
declare class AzureVirtualNetworkGatewayConnectionListResponse {
	virtualNetworkGatewayConnections: any[];
	requestId: string;
	nextLink: string;
	statusCode: number;
	constructor()
	/**
	 * Initializes a new instance of the
	 * VirtualNetworkGatewayConnectionListResponse class.
	 */
	constructor()
}

/**
 * Response for ListNetworkSecurityGroups Api Service call
 */
declare class AzureNetworkSecurityGroupListResponse {
	requestId: string;
	networkSecurityGroups: any[];
	nextLink: string;
	statusCode: number;
	constructor()
	/**
	 * Initializes a new instance of the NetworkSecurityGroupListResponse class.
	 */
	constructor()
}

/**
 * Backend address pool settings of application gateway
 */
declare class AzureApplicationGatewayBackendHttpSettings {
	protocol: string;
	port: number;
	name: string;
	etag: string;
	cookieBasedAffinity: string;
	id: string;
	provisioningState: string;
	constructor()
	constructor()
}

/**
 * Response for PutVirtualNetworkGatewayConnectionSharedKey Api Service call
 */
declare class AzureConnectionSharedKeyPutResponse {
	azureAsyncOperation: string;
	retryAfter: number;
	requestId: string;
	connectionSharedKey: AzureConnectionSharedKey;
	error: AzureError;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * Response for GetVitualNetworkgateway Api Service call.
 */
declare class AzureVirtualNetworkGatewayGetResponse {
	requestId: string;
	virtualNetworkGateway: AzureVirtualNetworkGateway;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * Outbound NAT pool of the loadbalancer
 */
declare class AzureOutboundNatRule {
	allocatedOutboundPorts: number;
	name: string;
	etag: string;
	backendAddressPool: AzureResourceId;
	id: string;
	provisioningState: string;
	frontendIpConfigurations: any[];
	constructor()
	/**
	 * Initializes a new instance of the OutboundNatRule class with required
	 * arguments.
	 * @param allocatedOutboundPorts 
	 * @param backendAddressPool 
	 */
	constructor(allocatedOutboundPorts: number, backendAddressPool: AzureResourceId)
	/**
	 * Initializes a new instance of the OutboundNatRule class.
	 */
	constructor()
}

/**
 * Response of a GET Load Balancer operation
 */
declare class AzureLoadBalancerGetResponse {
	loadBalancer: AzureLoadBalancer;
	requestId: string;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * Id of the resource
 */
declare class AzureResourceId {
	id: string;
	constructor()
	constructor()
}

/**
 * Response for ListLoadBalancers Api service call
 */
declare class AzureApplicationGatewayListResponse {
	applicationGateways: any[];
	requestId: string;
	nextLink: string;
	statusCode: number;
	constructor()
	/**
	 * Initializes a new instance of the ApplicationGatewayListResponse class.
	 */
	constructor()
}

/**
 * Response of Put ApplicationGateway operation
 */
declare class AzureApplicationGatewayPutResponse {
	azureAsyncOperation: string;
	retryAfter: number;
	requestId: string;
	applicationGateway: AzureApplicationGateway;
	error: AzureError;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * Rules of the load balancer
 */
declare class AzureLoadBalancingRule {
	enableFloatingIP: boolean;
	frontendIPConfiguration: AzureResourceId;
	idleTimeoutInMinutes: number;
	frontendPort: number;
	backendAddressPool: AzureResourceId;
	provisioningState: string;
	probe: AzureResourceId;
	protocol: string;
	loadDistribution: string;
	backendPort: number;
	name: string;
	etag: string;
	id: string;
	constructor()
	constructor()
}

/**
 * Inbound NAT pool of the loadbalancer
 */
declare class AzureInboundNatPool {
	protocol: string;
	backendPort: number;
	frontendPortRangeStart: number;
	frontendIPConfiguration: AzureResourceId;
	name: string;
	etag: string;
	id: string;
	provisioningState: string;
	frontendPortRangeEnd: number;
	constructor()
	/**
	 * Initializes a new instance of the InboundNatPool class.
	 */
	constructor()
	/**
	 * Initializes a new instance of the InboundNatPool class with required
	 * arguments.
	 * @param protocol 
	 * @param frontendPortRangeStart 
	 * @param frontendPortRangeEnd 
	 */
	constructor(protocol: string, frontendPortRangeStart: number, frontendPortRangeEnd: number)
}

/**
 * Response for GetSubnet Api service call
 */
declare class AzureSubnetGetResponse {
	subnet: AzureSubnet;
	requestId: string;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * Response for GetRoute Api service call
 */
declare class AzureRouteGetResponse {
	route: AzureRoute;
	requestId: string;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * Dns Settings of a resource
 */
declare class AzureDnsSettings {
	appliedDnsServers: any[];
	dnsServers: any[];
	constructor()
	/**
	 * Initializes a new instance of the DnsSettings class.
	 */
	constructor()
}

/**
 * Response for PUT SecurityRule Api service call
 */
declare class AzureSecurityRulePutResponse {
	azureAsyncOperation: string;
	retryAfter: number;
	requestId: string;
	securityRule: AzureSecurityRule;
	error: AzureError;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * NetworkSecurityGroup resource
 */
declare class AzureNetworkSecurityGroup {
	readonly resourceGroup: AzureResourceGroupExtended;
	defaultSecurityRules: any[];
	resourceGuid: string;
	securityRules: any[];
	provisioningState: string;
	type: string;
	tags: Properties;
	networkInterfaces: any[];
	readonly internalIdString: string;
	name: string;
	readonly networkSecurityGroupSecurityRules: AzureSecurityRule[];
	location: string;
	readonly connection: AzureConnection;
	readonly etag: string;
	subnets: any[];
	id: string;
	constructor()
	/**
	 * Initializes a new instance of the NetworkSecurityGroup class with
	 * required arguments.
	 * @param location 
	 */
	constructor(location: string)
	/**
	 * Initializes a new instance of the NetworkSecurityGroup class.
	 */
	constructor()
	/**
	 * @param securityRuleName 
	 */
	getSecurityRule(securityRuleName: string): AzureSecurityRule;
	delete(): void;
}

/**
 * Load balancer Probe
 */
declare class AzureProbe {
	loadBalancingRules: any[];
	protocol: string;
	port: number;
	name: string;
	intervalInSeconds: number;
	etag: string;
	numberOfProbes: number;
	id: string;
	provisioningState: string;
	requestPath: string;
	constructor()
	/**
	 * Initializes a new instance of the Probe class.
	 */
	constructor()
	/**
	 * Initializes a new instance of the Probe class with required arguments.
	 * @param protocol 
	 * @param port 
	 */
	constructor(protocol: string, port: number)
}

/**
 * Response for ListRoute Api Service call
 */
declare class AzureRouteListResponse {
	routes: any[];
	requestId: string;
	nextLink: string;
	statusCode: number;
	constructor()
	/**
	 * Initializes a new instance of the RouteListResponse class.
	 */
	constructor()
}

/**
 * If the resource provide needs to return an error to any operation, it should
 * return the appropriate HTTP error code and a message body as can be seen
 * below.The message should be localized per the Accept-Language header
 * specified in the original request such thatit could be directly be exposed
 * to users
 */
declare class AzureUpdateOperationResponse {
	azureAsyncOperation: string;
	retryAfter: number;
	requestId: string;
	error: AzureError;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * Frontend Port of application gateway
 */
declare class AzureApplicationGatewayFrontendPort {
	port: number;
	name: string;
	etag: string;
	provisioningState: string;
	id: string;
	constructor()
	constructor()
}

declare class AzureConnectionResetSharedKey {
	keyLength: number;
	constructor()
	constructor()
}

/**
 * A NetworkInterface in a resource group
 */
declare class AzureNetworkInterface {
	virtualMachine: AzureResourceId;
	readonly resourceGroup: AzureResourceGroupExtended;
	readonly displayName: string;
	resourceGuid: string;
	provisioningState: string;
	type: string;
	tags: Properties;
	ipConfigurations: any[];
	macAddress: string;
	dnsSettings: AzureNetworkInterfaceDnsSettings;
	readonly internalIdString: string;
	name: string;
	location: string;
	readonly connection: AzureConnection;
	etag: string;
	id: string;
	enableIPForwarding: boolean;
	networkSecurityGroup: AzureResourceId;
	primary: boolean;
	constructor()
	/**
	 * Initializes a new instance of the NetworkInterface class with required
	 * arguments.
	 * @param location 
	 */
	constructor(location: string)
	/**
	 * Initializes a new instance of the NetworkInterface class.
	 */
	constructor()
	delete(): void;
}

/**
 * Pool of backend IP addresseses
 */
declare class AzureBackendAddressPool {
	outboundNatRule: AzureResourceId;
	loadBalancingRules: any[];
	backendIpConfigurations: any[];
	name: string;
	etag: string;
	id: string;
	provisioningState: string;
	constructor()
	/**
	 * Initializes a new instance of the BackendAddressPool class.
	 */
	constructor()
}

/**
 * Response for CheckConnectionSharedKey Api Service call
 */
declare class AzureConnectionSharedKeyResponse {
	requestId: string;
	value: string;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * SKU of application gateway
 */
declare class AzureApplicationGatewaySku {
	tier: string;
	name: string;
	capacity: number;
	constructor()
	constructor()
}

/**
 * LoadBalancer resource
 */
declare class AzureLoadBalancer {
	inboundNatPools: any[];
	inboundNatRules: any[];
	loadBalancingRules: any[];
	resourceGuid: string;
	provisioningState: string;
	frontendIpConfigurations: any[];
	type: string;
	outboundNatRules: any[];
	tags: Properties;
	name: string;
	location: string;
	probes: any[];
	etag: string;
	id: string;
	backendAddressPools: any[];
	constructor()
	/**
	 * Initializes a new instance of the LoadBalancer class with required
	 * arguments.
	 * @param location 
	 */
	constructor(location: string)
	/**
	 * Initializes a new instance of the LoadBalancer class.
	 */
	constructor()
}

/**
 * Response for PutPublicIpAddress Api Service call
 */
declare class AzurePublicIpAddressPutResponse {
	azureAsyncOperation: string;
	publicIpAddress: AzurePublicIpAddress;
	retryAfter: number;
	requestId: string;
	error: AzureError;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * RouteTable resource
 */
declare class AzureRouteTable {
	readonly resourceGroup: AzureResourceGroupExtended;
	readonly displayName: string;
	provisioningState: string;
	type: string;
	tags: Properties;
	routes: any[];
	readonly internalIdString: string;
	name: string;
	location: string;
	readonly connection: AzureConnection;
	etag: string;
	subnets: any[];
	id: string;
	constructor()
	/**
	 * Initializes a new instance of the RouteTable class with required
	 * arguments.
	 * @param location 
	 */
	constructor(location: string)
	/**
	 * Initializes a new instance of the RouteTable class.
	 */
	constructor()
	delete(): void;
	/**
	 * @param routeName 
	 */
	getRoute(routeName: string): AzureRoute;
}

/**
 * Response for GetRouteTable Api service call
 */
declare class AzureRouteTableGetResponse {
	requestId: string;
	routeTable: AzureRouteTable;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * Response for PUT RouteTables Api Service call
 */
declare class AzureRouteTablePutResponse {
	azureAsyncOperation: string;
	retryAfter: number;
	requestId: string;
	routeTable: AzureRouteTable;
	error: AzureError;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * Response for GetConnectionSharedKey Api Service call
 */
declare class AzureConnectionSharedKey {
	value: string;
	constructor()
	constructor()
}

/**
 * Response for GetNetworkInterface Api service call
 */
declare class AzureNetworkInterfaceGetResponse {
	requestId: string;
	networkInterface: AzureNetworkInterface;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * Response of a PUT Load Balancer operation
 */
declare class AzureLoadBalancerPutResponse {
	azureAsyncOperation: string;
	retryAfter: number;
	loadBalancer: AzureLoadBalancer;
	requestId: string;
	error: AzureError;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * Response for GetVitualNetworkGatewayConnection Api Service call.
 */
declare class AzureVirtualNetworkGatewayConnectionGetResponse {
	requestId: string;
	virtualNetworkGatewayConnection: AzureVirtualNetworkGatewayConnection;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * The Windows Azure Network management API provides a RESTful set of web
 * services that interact with Windows Azure Networks service to manage your
 * network resrources. The API has entities that capture the relationship
 * between an end user and the Windows Azure Networks service.
 */
declare interface AzureNetworkResourceProviderClient {
	readonly subnetsOperations: AzureSubnetOperations;
	readonly applicationGatewaysOperations: AzureApplicationGatewayOperations;
	readonly routeTablesOperations: AzureRouteTableOperations;
	longRunningOperationRetryTimeout: number;
	longRunningOperationInitialTimeout: number;
	readonly networkSecurityGroupsOperations: AzureNetworkSecurityGroupOperations;
	readonly virtualNetworkGatewayConnectionsOperations: AzureVirtualNetworkGatewayConnectionOperations;
	readonly virtualNetworksOperations: AzureVirtualNetworkOperations;
	readonly routesOperations: AzureRouteOperations;
	readonly virtualNetworkGatewaysOperations: AzureVirtualNetworkGatewayOperations;
	readonly publicIpAddressesOperations: AzurePublicIpAddressOperations;
	readonly apiVersion: string;
	readonly networkInterfacesOperations: AzureNetworkInterfaceOperations;
	readonly localNetworkGatewaysOperations: AzureLocalNetworkGatewayOperations;
	readonly baseUri: string;
	readonly securityRulesOperations: AzureSecurityRuleOperations;
	readonly loadBalancersOperations: AzureLoadBalancerOperations;
	readonly usagesOperations: AzureNetworkUsageOperations;
	/**
	 * The Get Operation Status operation returns the status of the specified
	 * operation. After calling an asynchronous operation, you can call Get
	 * Operation Status to determine whether the operation has succeeded,
	 * failed, or is still in progress.
	 * @param arg0 
	 */
	getLongRunningOperationStatus(arg0: string): AzureAzureAsyncOperationResponse;
	/**
	 * Closes this stream and releases any system resources associated
	 * with it. If the stream is already closed then invoking this
	 * method has no effect.
	 * 
	 * <p> As noted in {@link AutoCloseable#close()}, cases where the
	 * close may fail require careful attention. It is strongly advised
	 * to relinquish the underlying resources and to internally
	 * <em>mark</em> the {@code Closeable} as closed, prior to throwing
	 * the {@code IOException}.
	 */
	close(): void;
	/**
	 * Checks whether a domain name in the cloudapp.net zone is available for
	 * use.
	 * @param arg0 
	 * @param arg1 
	 */
	checkDnsNameAvailability(arg0: string, arg1: string): AzureDnsNameAvailabilityResponse;
}

/**
 * The Network Resource Provider API includes operations managing the
 * application gateways for your subscription.
 */
declare interface AzureApplicationGatewayOperations {
	/**
	 * The Get applicationgateway operation retreives information about the
	 * specified applicationgateway.
	 * @param arg0 
	 * @param arg1 
	 */
	get(arg0: string, arg1: string): AzureApplicationGatewayGetResponse;
	/**
	 * The Start ApplicationGateway operation starts application gatewayin the
	 * specified resource group through Network resource provider.
	 * @param arg0 
	 * @param arg1 
	 */
	start(arg0: string, arg1: string): AzureAzureAsyncOperationResponse;
	/**
	 * The Put ApplicationGateway operation creates/updates a ApplicationGateway
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 */
	beginCreateOrUpdating(arg0: string, arg1: string, arg2: AzureApplicationGateway): AzureApplicationGatewayPutResponse;
	/**
	 * The Start ApplicationGateway operation starts application gatewayin the
	 * specified resource group through Network resource provider.
	 * @param arg0 
	 * @param arg1 
	 */
	beginStart(arg0: string, arg1: string): AzureVirtualNetworkGatewayPutResponse;
	/**
	 * The delete applicationgateway operation deletes the specified
	 * applicationgateway.
	 * @param arg0 
	 * @param arg1 
	 */
	beginDeleting(arg0: string, arg1: string): AzureUpdateOperationResponse;
	/**
	 * @param arg0 
	 * @param arg1 
	 */
	delete(arg0: string, arg1: string): AzureOperationResponse;
	/**
	 * The STOP ApplicationGateway operation stops application gatewayin the
	 * specified resource group through Network resource provider.
	 * @param arg0 
	 * @param arg1 
	 */
	beginStop(arg0: string, arg1: string): AzureVirtualNetworkGatewayPutResponse;
	/**
	 * The List applicationgateway opertion retrieves all the
	 * applicationgateways in a subscription.
	 */
	listAll(): AzureApplicationGatewayListResponse;
	/**
	 * The List ApplicationGateway opertion retrieves all the
	 * applicationgateways in a resource group.
	 * @param arg0 
	 */
	list(arg0: string): AzureApplicationGatewayListResponse;
	/**
	 * The STOP ApplicationGateway operation stops application gatewayin the
	 * specified resource group through Network resource provider.
	 * @param arg0 
	 * @param arg1 
	 */
	stop(arg0: string, arg1: string): AzureAzureAsyncOperationResponse;
	/**
	 * The Put ApplicationGateway operation creates/updates a ApplicationGateway
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 */
	createOrUpdate(arg0: string, arg1: string, arg2: AzureApplicationGateway): AzureAzureAsyncOperationResponse;
}

/**
 * The Network Resource Provider API includes operations for managing the load
 * balancers for your subscription.
 */
declare interface AzureLoadBalancerOperations {
	/**
	 * The List loadBalancer opertion retrieves all the loadbalancers in a
	 * resource group.
	 * @param arg0 
	 */
	list(arg0: string): AzureLoadBalancerListResponse;
	/**
	 * The Get ntework interface operation retreives information about the
	 * specified network interface.
	 * @param arg0 
	 * @param arg1 
	 */
	get(arg0: string, arg1: string): AzureLoadBalancerGetResponse;
	/**
	 * The Put LoadBalancer operation creates/updates a LoadBalancer
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 */
	createOrUpdate(arg0: string, arg1: string, arg2: AzureLoadBalancer): AzureAzureAsyncOperationResponse;
	/**
	 * The delete loadbalancer operation deletes the specified loadbalancer.
	 * @param arg0 
	 * @param arg1 
	 */
	beginDeleting(arg0: string, arg1: string): AzureUpdateOperationResponse;
	/**
	 * The List loadBalancer opertion retrieves all the loadbalancers in a
	 * subscription.
	 */
	listAll(): AzureLoadBalancerListResponse;
	/**
	 * @param arg0 
	 * @param arg1 
	 */
	delete(arg0: string, arg1: string): AzureOperationResponse;
	/**
	 * The Put LoadBalancer operation creates/updates a LoadBalancer
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 */
	beginCreateOrUpdating(arg0: string, arg1: string, arg2: AzureLoadBalancer): AzureLoadBalancerPutResponse;
}

/**
 * The Network Resource Provider API includes operations for managing the
 * Virtual network Gateway for your subscription.
 */
declare interface AzureLocalNetworkGatewayOperations {
	/**
	 * The List LocalNetworkGateways opertion retrieves all the local network
	 * gateways stored.
	 * @param arg0 
	 */
	list(arg0: string): AzureLocalNetworkGatewayListResponse;
	/**
	 * The Delete LocalNetworkGateway operation deletes the specifed local
	 * network Gateway through Network resource provider.
	 * @param arg0 
	 * @param arg1 
	 */
	beginDeleting(arg0: string, arg1: string): AzureUpdateOperationResponse;
	/**
	 * The Delete LocalNetworkGateway operation deletes the specifed local
	 * network Gateway through Network resource provider.
	 * @param arg0 
	 * @param arg1 
	 */
	delete(arg0: string, arg1: string): AzureOperationResponse;
	/**
	 * The Put LocalNetworkGateway operation creates/updates a local network
	 * gateway in the specified resource group through Network resource
	 * provider.
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 */
	beginCreateOrUpdating(arg0: string, arg1: string, arg2: AzureLocalNetworkGateway): AzureLocalNetworkGatewayPutResponse;
	/**
	 * The Get LocalNetworkGateway operation retrieves information about the
	 * specified local network gateway through Network resource provider.
	 * @param arg0 
	 * @param arg1 
	 */
	get(arg0: string, arg1: string): AzureLocalNetworkGatewayGetResponse;
	/**
	 * The Put LocalNetworkGateway operation creates/updates a local network
	 * gateway in the specified resource group through Network resource
	 * provider.
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 */
	createOrUpdate(arg0: string, arg1: string, arg2: AzureLocalNetworkGateway): AzureAzureAsyncOperationResponse;
}

/**
 * The Network Resource Provider API includes operations for managing the
 * subnets for your subscription.
 */
declare interface AzureNetworkInterfaceOperations {
	/**
	 * The List networkInterfaces opertion retrieves all the networkInterfaces
	 * in a subscription.
	 */
	listAll(): AzureNetworkInterfaceListResponse;
	/**
	 * The Get ntework interface operation retreives information about the
	 * specified network interface in a virtual machine scale set.
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 * @param arg3 
	 */
	getVirtualMachineScaleSetNetworkInterface(arg0: string, arg1: string, arg2: string, arg3: string): AzureNetworkInterfaceGetResponse;
	/**
	 * The Put NetworkInterface operation creates/updates a networkInterface
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 */
	createOrUpdate(arg0: string, arg1: string, arg2: AzureNetworkInterface): AzureAzureAsyncOperationResponse;
	/**
	 * The delete netwokInterface operation deletes the specified
	 * netwokInterface.
	 * @param arg0 
	 * @param arg1 
	 */
	beginDeleting(arg0: string, arg1: string): AzureUpdateOperationResponse;
	/**
	 * The Get ntework interface operation retreives information about the
	 * specified network interface.
	 * @param arg0 
	 * @param arg1 
	 */
	get(arg0: string, arg1: string): AzureNetworkInterfaceGetResponse;
	/**
	 * The List networkInterfaces opertion retrieves all the networkInterfaces
	 * in a resource group.
	 * @param arg0 
	 */
	list(arg0: string): AzureNetworkInterfaceListResponse;
	/**
	 * @param arg0 
	 * @param arg1 
	 */
	delete(arg0: string, arg1: string): AzureOperationResponse;
	/**
	 * The list network interface operation retrieves information about all
	 * network interfaces in a virtual machine scale set.
	 * @param arg0 
	 * @param arg1 
	 */
	listVirtualMachineScaleSetNetworkInterfaces(arg0: string, arg1: string): AzureNetworkInterfaceListResponse;
	/**
	 * The Put NetworkInterface operation creates/updates a networkInterface
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 */
	beginCreateOrUpdating(arg0: string, arg1: string, arg2: AzureNetworkInterface): AzureNetworkInterfacePutResponse;
}

/**
 * The Network Resource Provider API includes operations for managing the
 * NetworkSecurityGroups for your subscription.
 */
declare interface AzureNetworkSecurityGroupOperations {
	/**
	 * The list NetworkSecurityGroups returns all network security groups in a
	 * resource group
	 * @param arg0 
	 */
	list(arg0: string): AzureNetworkSecurityGroupListResponse;
	/**
	 * The Delete NetworkSecurityGroup operation deletes the specifed network
	 * security group
	 * @param arg0 
	 * @param arg1 
	 */
	beginDeleting(arg0: string, arg1: string): AzureUpdateOperationResponse;
	/**
	 * The Delete NetworkSecurityGroup operation deletes the specifed network
	 * security group
	 * @param arg0 
	 * @param arg1 
	 */
	delete(arg0: string, arg1: string): AzureOperationResponse;
	/**
	 * The list NetworkSecurityGroups returns all network security groups in a
	 * subscription
	 */
	listAll(): AzureNetworkSecurityGroupListResponse;
	/**
	 * The Get NetworkSecurityGroups operation retrieves information about the
	 * specified network security group.
	 * @param arg0 
	 * @param arg1 
	 */
	get(arg0: string, arg1: string): AzureNetworkSecurityGroupGetResponse;
	/**
	 * The Put NetworkSecurityGroup operation creates/updates a network security
	 * groupin the specified resource group.
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 */
	beginCreateOrUpdating(arg0: string, arg1: string, arg2: AzureNetworkSecurityGroup): AzureNetworkSecurityGroupPutResponse;
	/**
	 * The Put NetworkSecurityGroup operation creates/updates a network security
	 * groupin the specified resource group.
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 */
	createOrUpdate(arg0: string, arg1: string, arg2: AzureNetworkSecurityGroup): AzureAzureAsyncOperationResponse;
}

/**
 * The Network Resource Provider API includes operations for managing the
 * PublicIPAddress for your subscription.
 */
declare interface AzurePublicIpAddressOperations {
	/**
	 * The Put PublicIPAddress operation creates/updates a stable/dynamic
	 * PublicIP address
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 */
	createOrUpdate(arg0: string, arg1: string, arg2: AzurePublicIpAddress): AzureAzureAsyncOperationResponse;
	/**
	 * The List publicIpAddress opertion retrieves all the publicIpAddresses in
	 * a resource group.
	 * @param arg0 
	 */
	list(arg0: string): AzurePublicIpAddressListResponse;
	/**
	 * The delete publicIpAddress operation deletes the specified
	 * publicIpAddress.
	 * @param arg0 
	 * @param arg1 
	 */
	beginDeleting(arg0: string, arg1: string): AzureUpdateOperationResponse;
	/**
	 * The Get Role operation retrieves information about the specified virtual
	 * machine.
	 * @param arg0 
	 * @param arg1 
	 */
	delete(arg0: string, arg1: string): AzureOperationResponse;
	/**
	 * The List publicIpAddress opertion retrieves all the publicIpAddresses in
	 * a subscription.
	 */
	listAll(): AzurePublicIpAddressListResponse;
	/**
	 * The Get publicIpAddress operation retreives information about the
	 * specified pubicIpAddress
	 * @param arg0 
	 * @param arg1 
	 */
	get(arg0: string, arg1: string): AzurePublicIpAddressGetResponse;
	/**
	 * The Put PublicIPAddress operation creates/updates a stable/dynamic
	 * PublicIP address
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 */
	beginCreateOrUpdating(arg0: string, arg1: string, arg2: AzurePublicIpAddress): AzurePublicIpAddressPutResponse;
}

/**
 * The Network Resource Provider API includes operations for managing the Routes
 * for your subscription.
 */
declare interface AzureRouteOperations {
	/**
	 * The Put route operation creates/updates a route in the specified route
	 * table
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 * @param arg3 
	 */
	createOrUpdate(arg0: string, arg1: string, arg2: string, arg3: AzureRoute): AzureAzureAsyncOperationResponse;
	/**
	 * The delete route operation deletes the specified route from a route table.
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 */
	delete(arg0: string, arg1: string, arg2: string): AzureOperationResponse;
	/**
	 * The Get route operation retreives information about the specified route
	 * from the route table.
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 */
	get(arg0: string, arg1: string, arg2: string): AzureRouteGetResponse;
	/**
	 * The List network security rule opertion retrieves all the routes in a
	 * route table.
	 * @param arg0 
	 * @param arg1 
	 */
	list(arg0: string, arg1: string): AzureRouteListResponse;
	/**
	 * The delete route operation deletes the specified route from a route table.
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 */
	beginDeleting(arg0: string, arg1: string, arg2: string): AzureUpdateOperationResponse;
	/**
	 * The Put route operation creates/updates a route in the specified route
	 * table
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 * @param arg3 
	 */
	beginCreateOrUpdating(arg0: string, arg1: string, arg2: string, arg3: AzureRoute): AzureRoutePutResponse;
}

/**
 * The Network Resource Provider API includes operations for managing the
 * RouteTables for your subscription.
 */
declare interface AzureRouteTableOperations {
	/**
	 * The Put RouteTable operation creates/updates a route tablein the
	 * specified resource group.
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 */
	beginCreateOrUpdating(arg0: string, arg1: string, arg2: AzureRouteTable): AzureRouteTablePutResponse;
	/**
	 * The list RouteTables returns all route tables in a subscription
	 */
	listAll(): AzureRouteTableListResponse;
	/**
	 * The Delete RouteTable operation deletes the specifed Route Table
	 * @param arg0 
	 * @param arg1 
	 */
	beginDeleting(arg0: string, arg1: string): AzureUpdateOperationResponse;
	/**
	 * The Delete RouteTable operation deletes the specifed Route Table
	 * @param arg0 
	 * @param arg1 
	 */
	delete(arg0: string, arg1: string): AzureOperationResponse;
	/**
	 * The Get RouteTables operation retrieves information about the specified
	 * route table.
	 * @param arg0 
	 * @param arg1 
	 */
	get(arg0: string, arg1: string): AzureRouteTableGetResponse;
	/**
	 * The Put RouteTable operation creates/updates a route tablein the
	 * specified resource group.
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 */
	createOrUpdate(arg0: string, arg1: string, arg2: AzureRouteTable): AzureAzureAsyncOperationResponse;
	/**
	 * The list RouteTables returns all route tables in a resource group
	 * @param arg0 
	 */
	list(arg0: string): AzureRouteTableListResponse;
}

/**
 * The Network Resource Provider API includes operations for managing the
 * SecurityRules for your subscription.
 */
declare interface AzureSecurityRuleOperations {
	/**
	 * The delete network security rule operation deletes the specified network
	 * security rule.
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 */
	delete(arg0: string, arg1: string, arg2: string): AzureOperationResponse;
	/**
	 * The List network security rule opertion retrieves all the security rules
	 * in a network security group.
	 * @param arg0 
	 * @param arg1 
	 */
	list(arg0: string, arg1: string): AzureSecurityRuleListResponse;
	/**
	 * The delete network security rule operation deletes the specified network
	 * security rule.
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 */
	beginDeleting(arg0: string, arg1: string, arg2: string): AzureUpdateOperationResponse;
	/**
	 * The Put network security rule operation creates/updates a security rule
	 * in the specified network security group
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 * @param arg3 
	 */
	createOrUpdate(arg0: string, arg1: string, arg2: string, arg3: AzureSecurityRule): AzureAzureAsyncOperationResponse;
	/**
	 * The Put network security rule operation creates/updates a security rule
	 * in the specified network security group
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 * @param arg3 
	 */
	beginCreateOrUpdating(arg0: string, arg1: string, arg2: string, arg3: AzureSecurityRule): AzureSecurityRulePutResponse;
	/**
	 * The Get NetworkSecurityRule operation retreives information about the
	 * specified network security rule.
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 */
	get(arg0: string, arg1: string, arg2: string): AzureSecurityRuleGetResponse;
}

/**
 * The Network Resource Provider API includes operations for managing the
 * subnets for your subscription.
 */
declare interface AzureSubnetOperations {
	/**
	 * The delete subnet operation deletes the specified subnet.
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 */
	delete(arg0: string, arg1: string, arg2: string): AzureOperationResponse;
	/**
	 * The List subnets opertion retrieves all the subnets in a virtual network.
	 * @param arg0 
	 * @param arg1 
	 */
	list(arg0: string, arg1: string): AzureSubnetListResponse;
	/**
	 * The Put Subnet operation creates/updates a subnet in thespecified virtual
	 * network
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 * @param arg3 
	 */
	beginCreateOrUpdating(arg0: string, arg1: string, arg2: string, arg3: AzureSubnet): AzureSubnetPutResponse;
	/**
	 * The delete subnet operation deletes the specified subnet.
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 */
	beginDeleting(arg0: string, arg1: string, arg2: string): AzureUpdateOperationResponse;
	/**
	 * The Put Subnet operation creates/updates a subnet in thespecified virtual
	 * network
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 * @param arg3 
	 */
	createOrUpdate(arg0: string, arg1: string, arg2: string, arg3: AzureSubnet): AzureAzureAsyncOperationResponse;
	/**
	 * The Get subnet operation retreives information about the specified subnet.
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 */
	get(arg0: string, arg1: string, arg2: string): AzureSubnetGetResponse;
}

/**
 * Operations for listing usage.
 */
declare interface AzureNetworkUsageOperations {
	/**
	 * Lists compute usages for a subscription.
	 * @param arg0 
	 */
	list(arg0: string): AzureUsagesListResponse;
}

/**
 * The Network Resource Provider API includes operations for managing the
 * Virtual network Gateway for your subscription.
 */
declare interface AzureVirtualNetworkGatewayConnectionOperations {
	/**
	 * The Put VirtualNetworkGatewayConnection operation creates/updates a
	 * virtual network gateway connection in the specified resource group
	 * through Network resource provider.
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 */
	beginCreateOrUpdating(arg0: string, arg1: string, arg2: AzureVirtualNetworkGatewayConnection): AzureVirtualNetworkGatewayConnectionPutResponse;
	/**
	 * The Put VirtualNetworkGatewayConnectionSharedKey operation sets the
	 * virtual network gateway connection shared key for passed virtual network
	 * gateway connection in the specified resource group through Network
	 * resource provider.
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 */
	beginSetSharedKey(arg0: string, arg1: string, arg2: AzureConnectionSharedKey): AzureConnectionSharedKeyPutResponse;
	/**
	 * The Delete VirtualNetworkGatewayConnection operation deletes the specifed
	 * virtual network Gateway connection through Network resource provider.
	 * @param arg0 
	 * @param arg1 
	 */
	beginDeleting(arg0: string, arg1: string): AzureUpdateOperationResponse;
	/**
	 * The Delete VirtualNetworkGatewayConnection operation deletes the specifed
	 * virtual network Gateway connection through Network resource provider.
	 * @param arg0 
	 * @param arg1 
	 */
	delete(arg0: string, arg1: string): AzureOperationResponse;
	/**
	 * The Get VirtualNetworkGatewayConnection operation retrieves information
	 * about the specified virtual network gateway connection through Network
	 * resource provider.
	 * @param arg0 
	 * @param arg1 
	 */
	get(arg0: string, arg1: string): AzureVirtualNetworkGatewayConnectionGetResponse;
	/**
	 * The CreateOrUpdate Virtual network Gateway connection creates a new or
	 * updates an existing virtualnetwork gateway connection through Network
	 * resource provider.
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 */
	createOrUpdate(arg0: string, arg1: string, arg2: AzureVirtualNetworkGatewayConnection): AzureAzureAsyncOperationResponse;
	/**
	 * The Put VirtualNetworkGatewayConnectionSharedKey operation sets the
	 * virtual network gateway connection shared key for passed virtual network
	 * gateway connection in the specified resource group through Network
	 * resource provider.
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 */
	setSharedKey(arg0: string, arg1: string, arg2: AzureConnectionSharedKey): AzureAzureAsyncOperationResponse;
	/**
	 * The VirtualNetworkGatewayConnectionResetSharedKey operation resets the
	 * virtual network gateway connection shared key for passed virtual network
	 * gateway connection in the specified resource group through Network
	 * resource provider.
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 */
	beginResetSharedKey(arg0: string, arg1: string, arg2: AzureConnectionResetSharedKey): AzureConnectionResetSharedKeyPutResponse;
	/**
	 * The Get VirtualNetworkGatewayConnectionSharedKey operation retrieves
	 * information about the specified virtual network gateway connection
	 * shared key through Network resource provider.
	 * @param arg0 
	 * @param arg1 
	 */
	getSharedKey(arg0: string, arg1: string): AzureConnectionSharedKeyResponse;
	/**
	 * The List VirtualNetworkGatewayConnections operation retrieves all the
	 * virtual network gateways connections created.
	 * @param arg0 
	 */
	list(arg0: string): AzureVirtualNetworkGatewayConnectionListResponse;
	/**
	 * The Reset VirtualNetworkGatewayConnectionSharedKey operation resets the
	 * virtual network gateway connection shared key for passed virtual network
	 * gateway connection in the specified resource group through Network
	 * resource provider.
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 */
	resetSharedKey(arg0: string, arg1: string, arg2: AzureConnectionResetSharedKey): AzureAzureAsyncOperationResponse;
}

/**
 * The Network Resource Provider API includes operations for managing the
 * Virtual network Gateway for your subscription.
 */
declare interface AzureVirtualNetworkGatewayOperations {
	/**
	 * The Reset VirtualNetworkGateway operation resets the primary of the
	 * virtual network gatewayin the specified resource group through Network
	 * resource provider.
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 */
	beginReset(arg0: string, arg1: string, arg2: AzureVirtualNetworkGateway): AzureVirtualNetworkGatewayPutResponse;
	/**
	 * The Put VirtualNetworkGateway operation creates/updates a virtual network
	 * gateway in the specified resource group through Network resource
	 * provider.
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 */
	beginCreateOrUpdating(arg0: string, arg1: string, arg2: AzureVirtualNetworkGateway): AzureVirtualNetworkGatewayPutResponse;
	/**
	 * The List VirtualNetworkGateways opertion retrieves all the virtual
	 * network gateways stored.
	 * @param arg0 
	 */
	list(arg0: string): AzureVirtualNetworkGatewayListResponse;
	/**
	 * The Delete VirtualNetworkGateway operation deletes the specifed virtual
	 * network Gateway through Network resource provider.
	 * @param arg0 
	 * @param arg1 
	 */
	beginDeleting(arg0: string, arg1: string): AzureUpdateOperationResponse;
	/**
	 * The Reset VirtualNetworkGateway operation resets the primary of the
	 * virtual network gateway in the specified resource group through Network
	 * resource provider.
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 */
	reset(arg0: string, arg1: string, arg2: AzureVirtualNetworkGateway): AzureAzureAsyncOperationResponse;
	/**
	 * The Delete VirtualNetworkGateway operation deletes the specifed virtual
	 * network Gateway through Network resource provider.
	 * @param arg0 
	 * @param arg1 
	 */
	delete(arg0: string, arg1: string): AzureOperationResponse;
	/**
	 * The Get VirtualNetworkGateway operation retrieves information about the
	 * specified virtual network gateway through Network resource provider.
	 * @param arg0 
	 * @param arg1 
	 */
	get(arg0: string, arg1: string): AzureVirtualNetworkGatewayGetResponse;
	/**
	 * The Put VirtualNetworkGateway operation creates/updates a virtual network
	 * gateway in the specified resource group through Network resource
	 * provider.
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 */
	createOrUpdate(arg0: string, arg1: string, arg2: AzureVirtualNetworkGateway): AzureAzureAsyncOperationResponse;
}

/**
 * The Network Resource Provider API includes operations for managing the
 * Virtual Networks for your subscription.
 */
declare interface AzureVirtualNetworkOperations {
	/**
	 * The list VirtualNetwork returns all Virtual Networks in a subscription
	 */
	listAll(): AzureVirtualNetworkListResponse;
	/**
	 * The Put VirtualNetwork operation creates/updates a virtual network in the
	 * specified resource group.
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 */
	beginCreateOrUpdating(arg0: string, arg1: string, arg2: AzureVirtualNetwork): AzureVirtualNetworkPutResponse;
	/**
	 * The Delete VirtualNetwork operation deletes the specifed virtual network
	 * @param arg0 
	 * @param arg1 
	 */
	beginDeleting(arg0: string, arg1: string): AzureUpdateOperationResponse;
	/**
	 * The Get VirtualNetwork operation retrieves information about the
	 * specified virtual network.
	 * @param arg0 
	 * @param arg1 
	 */
	get(arg0: string, arg1: string): AzureVirtualNetworkGetResponse;
	/**
	 * The Delete VirtualNetwork operation deletes the specifed virtual network
	 * @param arg0 
	 * @param arg1 
	 */
	delete(arg0: string, arg1: string): AzureOperationResponse;
	/**
	 * The Put VirtualNetwork operation creates/updates a virtual networkin the
	 * specified resource group.
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 */
	createOrUpdate(arg0: string, arg1: string, arg2: AzureVirtualNetwork): AzureAzureAsyncOperationResponse;
	/**
	 * The list VirtualNetwork returns all Virtual Networks in a resource group
	 * @param arg0 
	 */
	list(arg0: string): AzureVirtualNetworkListResponse;
}

/**
 * Represents an Azure SQL Database Transparent Data Encryption Scan.
 */
declare class AzureTransparentDataEncryptionActivity {
	name: string;
	location: string;
	id: string;
	type: string;
	properties: AzureTransparentDataEncryptionActivityProperties;
	tags: Properties;
	constructor()
	/**
	 * Initializes a new instance of the TransparentDataEncryptionActivity class
	 * with required arguments.
	 * @param location 
	 */
	constructor(location: string)
	/**
	 * Initializes a new instance of the TransparentDataEncryptionActivity class.
	 */
	constructor()
}

/**
 * Represents an Azure SQL Database Elastic Pool (Elastic Pool).
 */
declare class AzureElasticPool {
	name: string;
	location: string;
	id: string;
	type: string;
	properties: AzureElasticPoolProperties;
	tags: Properties;
	constructor()
	/**
	 * Initializes a new instance of the ElasticPool class with required
	 * arguments.
	 * @param location 
	 */
	constructor(location: string)
	/**
	 * Initializes a new instance of the ElasticPool class.
	 */
	constructor()
}

/**
 * Represents the properties of an Azure SQL Database Elastic Pool.
 */
declare class AzureElasticPoolProperties {
	databaseDtuMin: number;
	databaseDtuMax: number;
	dtu: number;
	storageMB: number;
	edition: string;
	state: string;
	creationDate: Date;
	constructor()
	constructor()
}

/**
 * Represents the response to a List data masking rules request.
 */
declare class AzureDataMaskingRuleListResponse {
	requestId: string;
	dataMaskingRules: any[];
	statusCode: number;
	constructor()
	/**
	 * Initializes a new instance of the DataMaskingRuleListResponse class.
	 */
	constructor()
}

/**
 * Represents an Azure SQL Database restore point.
 */
declare class AzureRestorePoint {
	name: string;
	location: string;
	id: string;
	type: string;
	properties: AzureRestorePointProperties;
	tags: Properties;
	constructor()
	/**
	 * Initializes a new instance of the RestorePoint class with required
	 * arguments.
	 * @param location 
	 */
	constructor(location: string)
	/**
	 * Initializes a new instance of the RestorePoint class.
	 */
	constructor()
}

/**
 * Represents the response to a List Azure Sql Elastic Pool Database Activity
 * request.
 */
declare class AzureElasticPoolDatabaseActivityListResponse {
	requestId: string;
	elasticPoolDatabaseActivities: any[];
	statusCode: number;
	constructor()
	/**
	 * Initializes a new instance of the ElasticPoolDatabaseActivityListResponse
	 * class.
	 */
	constructor()
}

/**
 * Represents the response to a data masking rule get request.
 */
declare class AzureDataMaskingRuleGetResponse {
	dataMaskingRule: AzureDataMaskingRule;
	requestId: string;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * Create or update Sql Azure Database Transparent Data Encryption parameters.
 */
declare class AzureTransparentDataEncryptionCreateOrUpdateParameters {
	properties: AzureTransparentDataEncryptionCreateOrUpdateProperties;
	constructor()
	/**
	 * Initializes a new instance of the
	 * TransparentDataEncryptionCreateOrUpdateParameters class with required
	 * arguments.
	 * @param properties 
	 */
	constructor(properties: AzureTransparentDataEncryptionCreateOrUpdateProperties)
	/**
	 * Initializes a new instance of the
	 * TransparentDataEncryptionCreateOrUpdateParameters class.
	 */
	constructor()
}

/**
 * Create or update server parameters properties.
 */
declare class AzureServerCreateOrUpdateProperties {
	administratorLogin: string;
	administratorLoginPassword: string;
	version: string;
	constructor()
	constructor()
}

/**
 * Represents an Azure SQL Database Service Objective.
 */
declare class AzureServiceObjective {
	name: string;
	location: string;
	id: string;
	type: string;
	properties: AzureServiceObjectiveProperties;
	tags: Properties;
	constructor()
	/**
	 * Initializes a new instance of the ServiceObjective class with required
	 * arguments.
	 * @param location 
	 */
	constructor(location: string)
	/**
	 * Initializes a new instance of the ServiceObjective class.
	 */
	constructor()
}

/**
 * Represents the response to a Get for a Azure Sql Database Transparent Data
 * Encryption request.
 */
declare class AzureTransparentDataEncryptionGetResponse {
	transparentDataEncryption: AzureTransparentDataEncryption;
	requestId: string;
	statusCode: number;
	constructor()
	constructor()
}

declare class AzureCreateUpdateBase {
	location: string;
	tags: Properties;
	constructor()
	/**
	 * Initializes a new instance of the CreateUpdateBase class with required
	 * arguments.
	 * @param location 
	 * @param tags 
	 */
	constructor(location: string, tags: Properties)
	/**
	 * Initializes a new instance of the CreateUpdateBase class.
	 */
	constructor()
}

/**
 * Represents the activity on an Azure SQL Database Elastic Pool.
 */
declare class AzureElasticPoolDatabaseActivity {
	name: string;
	location: string;
	id: string;
	type: string;
	properties: AzureElasticPoolDatabaseActivityProperties;
	tags: Properties;
	constructor()
	/**
	 * Initializes a new instance of the ElasticPoolDatabaseActivity class with
	 * required arguments.
	 * @param location 
	 */
	constructor(location: string)
	/**
	 * Initializes a new instance of the ElasticPoolDatabaseActivity class.
	 */
	constructor()
}

/**
 * Create or update Azure Sql Database Transparent Data Encryption parameters
 * properties.
 */
declare class AzureTransparentDataEncryptionCreateOrUpdateProperties {
	state: string;
	constructor()
	constructor()
}

/**
 * Represents an Azure SQL Server Active Directory Administrator.
 */
declare class AzureServerAdministrator {
	name: string;
	location: string;
	id: string;
	type: string;
	properties: AzureServerAdministratorProperties;
	tags: Properties;
	constructor()
	/**
	 * Initializes a new instance of the ServerAdministrator class with required
	 * arguments.
	 * @param location 
	 */
	constructor(location: string)
	/**
	 * Initializes a new instance of the ServerAdministrator class.
	 */
	constructor()
}

/**
 * Represents the properties of a Azure SQL Recommended Elastic Pool being
 * upgraded.
 */
declare class AzureUpgradeRecommendedElasticPoolProperties {
	databaseDtuMin: number;
	databaseDtuMax: number;
	dtu: number;
	name: string;
	databaseCollection: any[];
	storageMb: number;
	edition: string;
	includeAllDatabases: boolean;
	constructor()
	/**
	 * Initializes a new instance of the UpgradeRecommendedElasticPoolProperties
	 * class.
	 */
	constructor()
}

/**
 * Create or update Azure SQL Database Server audting policy parameters.
 */
declare class AzureServerAuditingPolicyCreateOrUpdateParameters {
	properties: AzureServerAuditingPolicyProperties;
	constructor()
	/**
	 * Initializes a new instance of the
	 * ServerAuditingPolicyCreateOrUpdateParameters class with required
	 * arguments.
	 * @param properties 
	 */
	constructor(properties: AzureServerAuditingPolicyProperties)
	/**
	 * Initializes a new instance of the
	 * ServerAuditingPolicyCreateOrUpdateParameters class.
	 */
	constructor()
}

/**
 * Represents the properties of an Azure SQL Server Administrator.
 */
declare class AzureServerAdministratorProperties {
	tenantId: string;
	administratorType: string;
	login: string;
	sid: string;
	constructor()
	/**
	 * Initializes a new instance of the ServerAdministratorProperties class
	 * with required arguments.
	 * @param tenantId 
	 */
	constructor(tenantId: string)
	/**
	 * Initializes a new instance of the ServerAdministratorProperties class.
	 */
	constructor()
}

/**
 * Create or update Azure Sql Database Elastic Pool parameters properties.
 */
declare class AzureElasticPoolCreateOrUpdateProperties {
	databaseDtuMin: number;
	databaseDtuMax: number;
	dtu: number;
	edition: string;
	storageMB: number;
	constructor()
	constructor()
}

/**
 * Represents the properties of an Azure SQL auditing policy.
 */
declare class AzureBaseAuditingPolicyProperties {
	retentionDays: string;
	auditLogsTableName: string;
	eventTypesToAudit: string;
	storageAccountName: string;
	storageAccountKey: string;
	storageTableEndpoint: string;
	fullAuditLogsTableName: string;
	auditingState: string;
	storageAccountSubscriptionId: string;
	storageAccountResourceGroupName: string;
	storageAccountSecondaryKey: string;
	constructor()
	constructor()
}

/**
 * Represents an Azure SQL Database Elastic Pool metric name.
 */
declare class AzureName {
	value: string;
	localizedValue: string;
	constructor()
	constructor()
}

/**
 * Represents the properties of an Azure SQL Database Elastic Pool Database
 * Activity.
 */
declare class AzureElasticPoolDatabaseActivityProperties {
	databaseName: string;
	currentElasticPoolName: string;
	errorMessage: string;
	errorSeverity: number;
	errorCode: number;
	serverName: string;
	percentComplete: number;
	requestedElasticPoolName: string;
	operationId: string;
	startTime: Date;
	state: string;
	endTime: Date;
	currentServiceObjectiveName: string;
	requestedServiceObjectiveName: string;
	operation: string;
	constructor()
	constructor()
}

/**
 * Represents the response to a get database auditing policy request.
 */
declare class AzureDatabaseAuditingPolicyGetResponse {
	auditingPolicy: AzureDatabaseAuditingPolicy;
	requestId: string;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * Represents the activity on an Azure SQL Elastic Pool.
 */
declare class AzureElasticPoolActivity {
	name: string;
	location: string;
	id: string;
	type: string;
	properties: AzureElasticPoolActivityProperties;
	tags: Properties;
	constructor()
	/**
	 * Initializes a new instance of the ElasticPoolActivity class with required
	 * arguments.
	 * @param location 
	 */
	constructor(location: string)
	/**
	 * Initializes a new instance of the ElasticPoolActivity class.
	 */
	constructor()
}

/**
 * Represents an Azure SQL Database Server.
 */
declare class AzureServer {
	readonly databases: AzureDatabase[];
	readonly resourceGroup: AzureResourceGroupExtended;
	readonly displayName: string;
	readonly internalIdString: string;
	name: string;
	readonly location: string;
	readonly connection: AzureConnection;
	id: string;
	readonly type: string;
	readonly version: string;
	properties: AzureServerProperties;
	tags: Properties;
	constructor()
	/**
	 * Initializes a new instance of the Server class with required arguments.
	 * @param location 
	 */
	constructor(location: string)
	/**
	 * Initializes a new instance of the Server class.
	 */
	constructor()
	delete(): void;
	/**
	 * @param databaseName 
	 */
	getDatabase(databaseName: string): AzureDatabase;
}

/**
 * Represents the properties of a Service Tier Advisor.
 */
declare class AzureServiceTierAdvisorProperties {
	currentServiceLevelObjective: string;
	overallRecommendationServiceLevelObjective: string;
	databaseSizeBasedRecommendationServiceLevelObjective: string;
	avgDtu: number;
	confidence: number;
	overallRecommendationServiceLevelObjectiveId: string;
	minDtu: number;
	usageBasedRecommendationServiceLevelObjectiveId: string;
	disasterPlanBasedRecommendationServiceLevelObjectiveId: string;
	maxDtu: number;
	serviceLevelObjectiveUsageMetrics: any[];
	observationPeriodEnd: Date;
	activeTimeRatio: number;
	databaseSizeBasedRecommendationServiceLevelObjectiveId: string;
	currentServiceLevelObjectiveId: string;
	disasterPlanBasedRecommendationServiceLevelObjective: string;
	observationPeriodStart: Date;
	usageBasedRecommendationServiceLevelObjective: string;
	maxSizeInGB: number;
	constructor()
	/**
	 * Initializes a new instance of the ServiceTierAdvisorProperties class.
	 */
	constructor()
}

/**
 * Represents the properties of an Azure SQL Database table.
 */
declare class AzureTableProperties {
	tableType: string;
	columns: any[];
	recommendedIndexes: any[];
	constructor()
	/**
	 * Initializes a new instance of the TableProperties class.
	 */
	constructor()
}

/**
 * Represents the properties of an Azure SQL Database table column.
 */
declare class AzureColumnProperties {
	columnType: string;
	constructor()
	constructor()
}

/**
 * Represents an Azure SQL Database Elastic Pool metric name.
 */
declare class AzureMetricName {
	value: string;
	localizedValue: string;
	constructor()
	constructor()
}

/**
 * Represents the properties of an Azure SQL Database.
 */
declare class AzureDatabaseProperties {
	elasticPoolName: string;
	serviceTierAdvisors: any[];
	edition: string;
	upgradeHint: AzureUpgradeHint;
	creationDate: Date;
	currentServiceObjectiveId: string;
	serviceObjective: string;
	maxSizeBytes: number;
	schemas: any[];
	defaultSecondaryLocation: string;
	collation: string;
	databaseId: string;
	earliestRestoreDate: Date;
	requestedServiceObjectiveName: string;
	requestedServiceObjectiveId: string;
	status: string;
	constructor()
	/**
	 * Initializes a new instance of the DatabaseProperties class.
	 */
	constructor()
}

declare class AzureErrorResponse {
	code: string;
	message: string;
	target: string;
	constructor()
	/**
	 * Initializes a new instance of the ErrorResponse class with required
	 * arguments.
	 * @param code 
	 * @param message 
	 */
	constructor(code: string, message: string)
	/**
	 * Initializes a new instance of the ErrorResponse class.
	 */
	constructor()
}

/**
 * Represents the properties of an Azure SQL Database security policy.
 */
declare class AzureDatabaseSecurityPolicyProperties {
	readonly eventTypeSecurityExceptionsEnabled: boolean;
	readonly eventTypeDataChangesEnabled: boolean;
	jdbcConnectionString: string;
	proxyDnsName: string;
	useServerDefault: boolean;
	readonly eventTypeDataAccessEnabled: boolean;
	storageAccountName: string;
	storageAccountSubscriptionId: string;
	storageAccountResourceGroupName: string;
	readonly auditingEnabled: boolean;
	odbcConnectionString: string;
	phpConnectionString: string;
	readonly eventTypeSchemaChangeEnabled: boolean;
	retentionDays: number;
	readonly eventTypeGrantRevokePermissionsEnabled: boolean;
	secondaryStorageAccountKey: string;
	readonly blockDirectAccessEnabled: boolean;
	storageTableEndpoint: string;
	storageAccountKey: string;
	adoNetConnectionString: string;
	constructor()
	constructor()
}

/**
 * Represents an Azure SQL Database Server Firewall Rule.
 */
declare class AzureFirewallRule {
	name: string;
	location: string;
	id: string;
	type: string;
	properties: AzureFirewallRuleProperties;
	tags: Properties;
	constructor()
	/**
	 * Initializes a new instance of the FirewallRule class with required
	 * arguments.
	 * @param location 
	 */
	constructor(location: string)
	/**
	 * Initializes a new instance of the FirewallRule class.
	 */
	constructor()
}

/**
 * Represents the response to a Get Azure Sql Database Service Objectives
 * request.
 */
declare class AzureServiceObjectiveListResponse {
	requestId: string;
	serviceObjectives: any[];
	statusCode: number;
	constructor()
	/**
	 * Initializes a new instance of the ServiceObjectiveListResponse class.
	 */
	constructor()
}

/**
 * Represents the response to a Get Azure Sql Database Server request.
 */
declare class AzureServerGetResponse {
	server: AzureServer;
	requestId: string;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * Response for long running Azure Sql Database operations.
 */
declare class AzureDatabaseCreateOrUpdateResponse {
	database: AzureDatabase;
	retryAfter: number;
	requestId: string;
	operationStatusLink: string;
	error: AzureErrorResponse;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * Create or update Firewall Rule parameters properties.
 */
declare class AzureFirewallRuleCreateOrUpdateProperties {
	endIpAddress: string;
	startIpAddress: string;
	constructor()
	constructor()
}

/**
 * Represents the properties of an Azure SQL Database Server Firewall Rule.
 */
declare class AzureFirewallRuleProperties {
	endIpAddress: string;
	startIpAddress: string;
	constructor()
	constructor()
}

/**
 * Represents the response to a List Azure Sql Database request.
 */
declare class AzureDatabaseListResponse {
	databases: any[];
	requestId: string;
	statusCode: number;
	constructor()
	/**
	 * Initializes a new instance of the DatabaseListResponse class.
	 */
	constructor()
}

/**
 * Represents an Azure SQL Database security Policy
 */
declare class AzureDatabaseSecurityPolicy {
	name: string;
	location: string;
	id: string;
	type: string;
	properties: AzureDatabaseSecurityPolicyProperties;
	tags: Properties;
	constructor()
	/**
	 * Initializes a new instance of the DatabaseSecurityPolicy class.
	 */
	constructor()
}

/**
 * Represents an Azure SQL Database recommended index.
 */
declare class AzureRecommendedIndex {
	name: string;
	location: string;
	id: string;
	type: string;
	properties: AzureRecommendedIndexProperties;
	tags: Properties;
	constructor()
	/**
	 * Initializes a new instance of the RecommendedIndex class with required
	 * arguments.
	 * @param location 
	 */
	constructor(location: string)
	/**
	 * Initializes a new instance of the RecommendedIndex class.
	 */
	constructor()
}

/**
 * Create or update database secure connection policy parameters.
 */
declare class AzureDatabaseSecureConnectionPolicyCreateOrUpdateParameters {
	properties: AzureDatabaseSecureConnectionPolicyCreateOrUpdateProperties;
	constructor()
	/**
	 * Initializes a new instance of the
	 * DatabaseSecureConnectionPolicyCreateOrUpdateParameters class with
	 * required arguments.
	 * @param properties 
	 */
	constructor(properties: AzureDatabaseSecureConnectionPolicyCreateOrUpdateProperties)
	/**
	 * Initializes a new instance of the
	 * DatabaseSecureConnectionPolicyCreateOrUpdateParameters class.
	 */
	constructor()
}

/**
 * Represents the maximum size limits for an Azure SQL Database.
 */
declare class AzureMaxSizeCapability {
	unit: string;
	limit: number;
	status: string;
	constructor()
	constructor()
}

/**
 * Update Sql Azure Database recommended index state.
 */
declare class AzureRecommendedIndexUpdateParameters {
	properties: AzureRecommendedIndexUpdateProperties;
	constructor()
	/**
	 * Initializes a new instance of the RecommendedIndexUpdateParameters class
	 * with required arguments.
	 * @param properties 
	 */
	constructor(properties: AzureRecommendedIndexUpdateProperties)
	/**
	 * Initializes a new instance of the RecommendedIndexUpdateParameters class.
	 */
	constructor()
}

/**
 * Represents the properties of an Azure SQL auditing policy.
 */
declare class AzureDatabaseAuditingPolicyProperties {
	retentionDays: string;
	useServerDefault: string;
	auditLogsTableName: string;
	eventTypesToAudit: string;
	storageAccountName: string;
	storageAccountKey: string;
	storageTableEndpoint: string;
	fullAuditLogsTableName: string;
	auditingState: string;
	storageAccountSubscriptionId: string;
	storageAccountResourceGroupName: string;
	storageAccountSecondaryKey: string;
	constructor()
	constructor()
}

/**
 * Represents an Azure SQL Database schema.
 */
declare class AzureSchema {
	name: string;
	location: string;
	id: string;
	type: string;
	properties: AzureSchemaProperties;
	tags: Properties;
	constructor()
	/**
	 * Initializes a new instance of the Schema class with required arguments.
	 * @param location 
	 */
	constructor(location: string)
	/**
	 * Initializes a new instance of the Schema class.
	 */
	constructor()
}

/**
 * Represents the response to a List Azure Sql Recommended Elastic Pool metrics
 * request.
 */
declare class AzureRecommendedElasticPoolListMetricsResponse {
	requestId: string;
	recommendedElasticPoolsMetrics: any[];
	statusCode: number;
	constructor()
	/**
	 * Initializes a new instance of the
	 * RecommendedElasticPoolListMetricsResponse class.
	 */
	constructor()
}

/**
 * Response for long running Azure Sql Database replication failover operations.
 */
declare class AzureReplicationLinkFailoverResponse {
	retryAfter: number;
	requestId: string;
	operationStatusLink: string;
	error: AzureErrorResponse;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * Create or update Azure SQL Server Active Directory Administrator parameters.
 */
declare class AzureServerAdministratorCreateOrUpdateParameters {
	properties: AzureServerAdministratorCreateOrUpdateProperties;
	constructor()
	/**
	 * Initializes a new instance of the
	 * ServerAdministratorCreateOrUpdateParameters class with required
	 * arguments.
	 * @param properties 
	 */
	constructor(properties: AzureServerAdministratorCreateOrUpdateProperties)
	/**
	 * Initializes a new instance of the
	 * ServerAdministratorCreateOrUpdateParameters class.
	 */
	constructor()
}

/**
 * Update Azure SQL Database security policy parameters.
 */
declare class AzureDatabaseSecurityPolicyUpdateParameters {
	properties: AzureDatabaseSecurityPolicyProperties;
	constructor()
	/**
	 * Initializes a new instance of the DatabaseSecurityPolicyUpdateParameters
	 * class with required arguments.
	 * @param properties 
	 */
	constructor(properties: AzureDatabaseSecurityPolicyProperties)
	/**
	 * Initializes a new instance of the DatabaseSecurityPolicyUpdateParameters
	 * class.
	 */
	constructor()
}

/**
 * Represents the response to a Get Azure Sql Recommended Resource pool request.
 */
declare class AzureRecommendedElasticPoolGetResponse {
	requestId: string;
	recommendedElasticPool: AzureRecommendedElasticPool;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * Response for long running Azure SQL Server Active Directory administrator
 * delete operations.
 */
declare class AzureServerAdministratorDeleteResponse {
	retryAfter: number;
	requestId: string;
	operationStatusLink: string;
	error: AzureErrorResponse;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * Create or update server parameters.
 */
declare class AzureServerCreateOrUpdateParameters {
	location: string;
	properties: AzureServerCreateOrUpdateProperties;
	tags: Properties;
	constructor()
	/**
	 * Initializes a new instance of the ServerCreateOrUpdateParameters class
	 * with required arguments.
	 * @param properties 
	 * @param location 
	 */
	constructor(properties: AzureServerCreateOrUpdateProperties, location: string)
	/**
	 * Initializes a new instance of the ServerCreateOrUpdateParameters class.
	 */
	constructor()
}

/**
 * Represents the response to a List Azure SQL Active Directory Administrators
 * request.
 */
declare class AzureServerAdministratorListResponse {
	requestId: string;
	administrators: any[];
	statusCode: number;
	constructor()
	/**
	 * Initializes a new instance of the ServerAdministratorListResponse class.
	 */
	constructor()
}

/**
 * Response for long running Azure SQL Server Active Directory Administrator
 * operations.
 */
declare class AzureServerAdministratorCreateOrUpdateResponse {
	retryAfter: number;
	requestId: string;
	serverAdministrator: AzureServerAdministrator;
	operationStatusLink: string;
	error: AzureErrorResponse;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * Represents Azure SQL Database metrics.
 */
declare class AzureDatabaseMetric {
	unit: string;
	displayName: string;
	limit: number;
	nextResetTime: Date;
	resourceName: string;
	currentValue: number;
	constructor()
	constructor()
}

/**
 * Create or update Sql Azure Database Elastic Pool parameters.
 */
declare class AzureElasticPoolCreateOrUpdateParameters {
	location: string;
	properties: AzureElasticPoolCreateOrUpdateProperties;
	tags: Properties;
	constructor()
	/**
	 * Initializes a new instance of the ElasticPoolCreateOrUpdateParameters
	 * class with required arguments.
	 * @param properties 
	 * @param location 
	 */
	constructor(properties: AzureElasticPoolCreateOrUpdateProperties, location: string)
	/**
	 * Initializes a new instance of the ElasticPoolCreateOrUpdateParameters
	 * class.
	 */
	constructor()
}

/**
 * Represents an Azure SQL Database server auditing policy.
 */
declare class AzureServerAuditingPolicy {
	name: string;
	location: string;
	id: string;
	type: string;
	properties: AzureServerAuditingPolicyProperties;
	tags: Properties;
	constructor()
	/**
	 * Initializes a new instance of the ServerAuditingPolicy class with
	 * required arguments.
	 * @param location 
	 */
	constructor(location: string)
	/**
	 * Initializes a new instance of the ServerAuditingPolicy class.
	 */
	constructor()
}

/**
 * Represents the properties of an Azure SQL Database Transparent Data
 * Encryption.
 */
declare class AzureTransparentDataEncryptionProperties {
	state: string;
	constructor()
	constructor()
}

/**
 * Create or update Azure Server Active Directory Administrator parameters
 * properties.
 */
declare class AzureServerAdministratorCreateOrUpdateProperties {
	tenantId: string;
	administratorType: string;
	login: string;
	sid: string;
	constructor()
	constructor()
}

/**
 * Represents the response to a get Azure SQL Database security policy request
 */
declare class AzureDatabaseSecurityPolicyGetResponse {
	databaseSecurityPolicy: AzureDatabaseSecurityPolicy;
	requestId: string;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * Represents a Slo Usage Metric.
 */
declare class AzureSloUsageMetric {
	serviceLevelObjectiveId: string;
	serviceLevelObjective: string;
	inRangeTimeRatio: number;
	name: string;
	location: string;
	id: string;
	type: string;
	tags: Properties;
	constructor()
	/**
	 * Initializes a new instance of the SloUsageMetric class with required
	 * arguments.
	 * @param location 
	 */
	constructor(location: string)
	/**
	 * Initializes a new instance of the SloUsageMetric class.
	 */
	constructor()
}

/**
 * Represents the response to a Get Azure Sql Database request.
 */
declare class AzureDatabaseGetResponse {
	database: AzureDatabase;
	requestId: string;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * Represents the Azure SQL Server edition capabilities.
 */
declare class AzureEditionCapability {
	supportedServiceObjectives: any[];
	name: string;
	status: string;
	constructor()
	/**
	 * Initializes a new instance of the EditionCapability class.
	 */
	constructor()
}

/**
 * Represents an Azure SQL Database data masking rule.
 */
declare class AzureDataMaskingRule {
	name: string;
	location: string;
	id: string;
	type: string;
	properties: AzureDataMaskingRuleProperties;
	tags: Properties;
	constructor()
	/**
	 * Initializes a new instance of the DataMaskingRule class with required
	 * arguments.
	 * @param location 
	 */
	constructor(location: string)
	/**
	 * Initializes a new instance of the DataMaskingRule class.
	 */
	constructor()
}

/**
 * Represents Azure SQL Database Elastic Pool metrics.
 */
declare class AzureElasticPoolMetrics {
	requestId: string;
	metrics: any[];
	statusCode: number;
	constructor()
	/**
	 * Initializes a new instance of the ElasticPoolMetrics class.
	 */
	constructor()
}

/**
 * Represents an Azure SQL Recommended Elastic Pool.
 */
declare class AzureRecommendedElasticPool {
	name: string;
	location: string;
	id: string;
	type: string;
	properties: AzureRecommendedElasticPoolProperties;
	tags: Properties;
	constructor()
	/**
	 * Initializes a new instance of the RecommendedElasticPool class with
	 * required arguments.
	 * @param location 
	 */
	constructor(location: string)
	/**
	 * Initializes a new instance of the RecommendedElasticPool class.
	 */
	constructor()
}

/**
 * Update Azure Sql Database recommended index properties.
 */
declare class AzureRecommendedIndexUpdateProperties {
	state: string;
	constructor()
	constructor()
}

/**
 * Represents the properties of an Azure SQL Database restore point.
 */
declare class AzureRestorePointProperties {
	restorePointCreationDate: Date;
	earliestRestoreDate: Date;
	restorePointType: string;
	constructor()
	constructor()
}

/**
 * Represents the properties of an Azure SQL Database secure connection policy.
 */
declare class AzureDatabaseSecureConnectionPolicyProperties {
	securityEnabledAccess: string;
	proxyPort: string;
	proxyDnsName: string;
	constructor()
	constructor()
}

/**
 * Create or update database secure connection policy parameters properties.
 */
declare class AzureDatabaseSecureConnectionPolicyCreateOrUpdateProperties {
	securityEnabledAccess: string;
	constructor()
	constructor()
}

/**
 * Represents a Upgrade Hint.
 */
declare class AzureUpgradeHint {
	targetServiceLevelObjectiveId: string;
	name: string;
	targetServiceLevelObjective: string;
	location: string;
	id: string;
	type: string;
	tags: Properties;
	constructor()
	/**
	 * Initializes a new instance of the UpgradeHint class with required
	 * arguments.
	 * @param location 
	 */
	constructor(location: string)
	/**
	 * Initializes a new instance of the UpgradeHint class.
	 */
	constructor()
}

/**
 * Represents the properties of an Azure SQL Database Elastic Pool.
 */
declare class AzureElasticPoolActivityProperties {
	elasticPoolName: string;
	requestedDatabaseDtuMin: number;
	errorMessage: string;
	errorSeverity: number;
	errorCode: number;
	serverName: string;
	percentComplete: number;
	requestedDtu: number;
	requestedElasticPoolName: string;
	requestedStorageLimitInGB: number;
	requestedDatabaseDtuMax: number;
	operationId: string;
	startTime: Date;
	state: string;
	endTime: Date;
	operation: string;
	constructor()
	constructor()
}

/**
 * Represents the response to a list service tier advisor request.
 */
declare class AzureServiceTierAdvisorListResponse {
	serviceTierAdvisors: any[];
	requestId: string;
	statusCode: number;
	constructor()
	/**
	 * Initializes a new instance of the ServiceTierAdvisorListResponse class.
	 */
	constructor()
}

/**
 * Represents the response to a List Azure Sql Elastic Pool request.
 */
declare class AzureElasticPoolListResponse {
	requestId: string;
	elasticPools: any[];
	statusCode: number;
	constructor()
	/**
	 * Initializes a new instance of the ElasticPoolListResponse class.
	 */
	constructor()
}

/**
 * Represents the response to a Get database secure connection request.
 */
declare class AzureDatabaseSecureConnectionPolicyGetResponse {
	secureConnectionPolicy: AzureDatabaseSecureConnectionPolicy;
	requestId: string;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * Represents Azure SQL recommended elastic pool metric.
 */
declare class AzureRecommendedElasticPoolMetric {
	dateTime: Date;
	sizeGB: number;
	dtu: number;
	constructor()
	constructor()
}

/**
 * Create or update Sql Azure Database parameters.
 */
declare class AzureDatabaseCreateOrUpdateParameters {
	location: string;
	properties: AzureDatabaseCreateOrUpdateProperties;
	tags: Properties;
	constructor()
	/**
	 * Initializes a new instance of the DatabaseCreateOrUpdateParameters class
	 * with required arguments.
	 * @param properties 
	 * @param location 
	 */
	constructor(properties: AzureDatabaseCreateOrUpdateProperties, location: string)
	/**
	 * Initializes a new instance of the DatabaseCreateOrUpdateParameters class.
	 */
	constructor()
}

/**
 * Represents an Azure SQL Database secure connection policy.
 */
declare class AzureDatabaseSecureConnectionPolicy {
	name: string;
	location: string;
	id: string;
	type: string;
	properties: AzureDatabaseSecureConnectionPolicyProperties;
	tags: Properties;
	constructor()
	/**
	 * Initializes a new instance of the DatabaseSecureConnectionPolicy class
	 * with required arguments.
	 * @param location 
	 */
	constructor(location: string)
	/**
	 * Initializes a new instance of the DatabaseSecureConnectionPolicy class.
	 */
	constructor()
}

/**
 * Represents an Azure SQL Database Elastic Pool metric availability.
 */
declare class AzureMetricAvailability {
	timeGrain: string;
	retention: string;
	constructor()
	constructor()
}

/**
 * Represents the Azure SQL Server capabilities.
 */
declare class AzureServerVersionCapability {
	supportedEditions: any[];
	name: string;
	status: string;
	constructor()
	/**
	 * Initializes a new instance of the ServerVersionCapability class.
	 */
	constructor()
}

/**
 * Represents the response to a List Azure Sql Recommended Elastic Pool request.
 */
declare class AzureRecommendedElasticPoolListResponse {
	requestId: string;
	recommendedElasticPools: any[];
	statusCode: number;
	constructor()
	/**
	 * Initializes a new instance of the RecommendedElasticPoolListResponse
	 * class.
	 */
	constructor()
}

/**
 * Response for long running Azure Sql Database Elastic Pool operation.
 */
declare class AzureElasticPoolCreateOrUpdateResponse {
	elasticPool: AzureElasticPool;
	retryAfter: number;
	requestId: string;
	operationStatusLink: string;
	error: AzureErrorResponse;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * Represents the response to a List Azure Sql Elastic Pool Activity request.
 */
declare class AzureElasticPoolActivityListResponse {
	elasticPoolActivities: any[];
	requestId: string;
	statusCode: number;
	constructor()
	/**
	 * Initializes a new instance of the ElasticPoolActivityListResponse class.
	 */
	constructor()
}

/**
 * Represents an Azure SQL Database.
 */
declare class AzureDatabase {
	readonly resourceGroup: AzureResourceGroupExtended;
	readonly displayName: string;
	readonly internalIdString: string;
	name: string;
	readonly location: string;
	readonly connection: AzureConnection;
	id: string;
	readonly type: string;
	properties: AzureDatabaseProperties;
	tags: Properties;
	readonly status: string;
	constructor()
	/**
	 * Initializes a new instance of the Database class with required arguments.
	 * @param location 
	 */
	constructor(location: string)
	/**
	 * Initializes a new instance of the Database class.
	 */
	constructor()
	delete(): void;
}

/**
 * Represents the properties of an Azure SQL Database schema.
 */
declare class AzureSchemaProperties {
	tables: any[];
	constructor()
	/**
	 * Initializes a new instance of the SchemaProperties class.
	 */
	constructor()
}

/**
 * Represents the Service Objectives capabilities.
 */
declare class AzureServiceObjectiveCapability {
	name: string;
	id: string;
	supportedMaxSizes: any[];
	status: string;
	constructor()
	/**
	 * Initializes a new instance of the ServiceObjectiveCapability class.
	 */
	constructor()
}

/**
 * Represents an Azure SQL Database Transparent Data Encryption .
 */
declare class AzureTransparentDataEncryption {
	name: string;
	location: string;
	id: string;
	type: string;
	properties: AzureTransparentDataEncryptionProperties;
	tags: Properties;
	constructor()
	/**
	 * Initializes a new instance of the TransparentDataEncryption class with
	 * required arguments.
	 * @param location 
	 */
	constructor(location: string)
	/**
	 * Initializes a new instance of the TransparentDataEncryption class.
	 */
	constructor()
}

/**
 * Create or update Firewall Rule parameters.
 */
declare class AzureFirewallRuleCreateOrUpdateParameters {
	properties: AzureFirewallRuleCreateOrUpdateProperties;
	constructor()
	/**
	 * Initializes a new instance of the FirewallRuleCreateOrUpdateParameters
	 * class with required arguments.
	 * @param properties 
	 */
	constructor(properties: AzureFirewallRuleCreateOrUpdateProperties)
	/**
	 * Initializes a new instance of the FirewallRuleCreateOrUpdateParameters
	 * class.
	 */
	constructor()
}

/**
 * Represents the response to a List Azure Sql Database metrics request.
 */
declare class AzureDatabaseMetricListResponse {
	requestId: string;
	metrics: any[];
	statusCode: number;
	constructor()
	/**
	 * Initializes a new instance of the DatabaseMetricListResponse class.
	 */
	constructor()
}

/**
 * Represents the response to a Get Azure Sql Database Replication Link request.
 */
declare class AzureReplicationLinkGetResponse {
	requestId: string;
	replicationLink: AzureReplicationLink;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * Represents an Azure SQL Database Replication Link.
 */
declare class AzureReplicationLink {
	name: string;
	location: string;
	id: string;
	type: string;
	properties: AzureReplicationLinkProperties;
	tags: Properties;
	constructor()
	/**
	 * Initializes a new instance of the ReplicationLink class with required
	 * arguments.
	 * @param location 
	 */
	constructor(location: string)
	/**
	 * Initializes a new instance of the ReplicationLink class.
	 */
	constructor()
}

/**
 * Represents Azure SQL Database metrics.
 */
declare class AzureServerMetric {
	unit: string;
	displayName: string;
	limit: number;
	nextResetTime: Date;
	resourceName: string;
	currentValue: number;
	constructor()
	constructor()
}

/**
 * Represents the response to a Get Azure Sql Database Server request.
 */
declare class AzureServerListResponse {
	servers: any[];
	requestId: string;
	statusCode: number;
	constructor()
	/**
	 * Initializes a new instance of the ServerListResponse class.
	 */
	constructor()
}

/**
 * Represents the response to a data masking policy get request.
 */
declare class AzureDataMaskingPolicyGetResponse {
	requestId: string;
	dataMaskingPolicy: AzureDataMaskingPolicy;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * Represents the response to a get database auditing policy request.
 */
declare class AzureServerAuditingPolicyGetResponse {
	auditingPolicy: AzureServerAuditingPolicy;
	requestId: string;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * Represents the properties of an Azure SQL Database Service Objective.
 */
declare class AzureServiceObjectiveProperties {
	readonly default: boolean;
	readonly system: boolean;
	serviceObjectiveName: string;
	description: string;
	enabled: boolean;
	constructor()
	constructor()
}

/**
 * Represents the properties of an Azure SQL Database.
 */
declare class AzureServerProperties {
	administratorLogin: string;
	fullyQualifiedDomainName: string;
	administratorLoginPassword: string;
	version: string;
	constructor()
	constructor()
}

/**
 * Represents the response to a List Azure Sql Database restore points request.
 */
declare class AzureRestorePointListResponse {
	requestId: string;
	restorePoints: any[];
	statusCode: number;
	constructor()
	/**
	 * Initializes a new instance of the RestorePointListResponse class.
	 */
	constructor()
}

/**
 * Represents an Azure SQL Database data masking policy.
 */
declare class AzureDataMaskingPolicy {
	name: string;
	location: string;
	id: string;
	type: string;
	properties: AzureDataMaskingPolicyProperties;
	tags: Properties;
	constructor()
	/**
	 * Initializes a new instance of the DataMaskingPolicy class with required
	 * arguments.
	 * @param location 
	 */
	constructor(location: string)
	/**
	 * Initializes a new instance of the DataMaskingPolicy class.
	 */
	constructor()
}

/**
 * Create or update database audting policy parameters.
 */
declare class AzureDatabaseAuditingPolicyCreateOrUpdateParameters {
	properties: AzureDatabaseAuditingPolicyProperties;
	constructor()
	/**
	 * Initializes a new instance of the
	 * DatabaseAuditingPolicyCreateOrUpdateParameters class with required
	 * arguments.
	 * @param properties 
	 */
	constructor(properties: AzureDatabaseAuditingPolicyProperties)
	/**
	 * Initializes a new instance of the
	 * DatabaseAuditingPolicyCreateOrUpdateParameters class.
	 */
	constructor()
}

/**
 * Represents the properties of an Azure SQL auditing policy.
 */
declare class AzureServerAuditingPolicyProperties {
	retentionDays: string;
	auditLogsTableName: string;
	eventTypesToAudit: string;
	storageAccountName: string;
	storageAccountKey: string;
	storageTableEndpoint: string;
	fullAuditLogsTableName: string;
	auditingState: string;
	storageAccountSubscriptionId: string;
	storageAccountResourceGroupName: string;
	storageAccountSecondaryKey: string;
	constructor()
	constructor()
}

/**
 * Represents the response to a List Firewall Rules request.
 */
declare class AzureFirewallRuleListResponse {
	requestId: string;
	firewallRules: any[];
	statusCode: number;
	constructor()
	/**
	 * Initializes a new instance of the FirewallRuleListResponse class.
	 */
	constructor()
}

/**
 * Represents the properties of an Azure SQL Database data masking rule.
 */
declare class AzureDataMaskingRuleProperties {
	replacementString: string;
	suffixSize: string;
	numberFrom: string;
	numberTo: string;
	id: string;
	schemaName: string;
	maskingFunction: string;
	prefixSize: string;
	tableName: string;
	columnName: string;
	constructor()
	/**
	 * Initializes a new instance of the DataMaskingRuleProperties class with
	 * required arguments.
	 * @param id 
	 * @param maskingFunction 
	 */
	constructor(id: string, maskingFunction: string)
	/**
	 * Initializes a new instance of the DataMaskingRuleProperties class.
	 */
	constructor()
}

/**
 * Represents the response to a Get request for Upgrade status of an Azure SQL
 * Database Server.
 */
declare class AzureServerUpgradeGetResponse {
	requestId: string;
	scheduleUpgradeAfterTime: Date;
	status: string;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * Represents the properties of an Azure SQL Database Transparent Data
 * Encryption Scan.
 */
declare class AzureTransparentDataEncryptionActivityProperties {
	percentComplete: number;
	status: string;
	constructor()
	constructor()
}

/**
 * Represents an Azure SQL Database table column.
 */
declare class AzureColumn {
	name: string;
	location: string;
	id: string;
	type: string;
	properties: AzureColumnProperties;
	tags: Properties;
	constructor()
	/**
	 * Initializes a new instance of the Column class with required arguments.
	 * @param location 
	 */
	constructor(location: string)
	/**
	 * Initializes a new instance of the Column class.
	 */
	constructor()
}

/**
 * Create or update data masking Rule parameters.
 */
declare class AzureDataMaskingPolicyCreateOrUpdateParameters {
	properties: AzureDataMaskingPolicyProperties;
	constructor()
	/**
	 * Initializes a new instance of the
	 * DataMaskingPolicyCreateOrUpdateParameters class with required arguments.
	 * @param properties 
	 */
	constructor(properties: AzureDataMaskingPolicyProperties)
	/**
	 * Initializes a new instance of the
	 * DataMaskingPolicyCreateOrUpdateParameters class.
	 */
	constructor()
}

/**
 * Represents Azure SQL Database Elastic Pool metric definitions.
 */
declare class AzureElasticPoolMetricDefinitions {
	requestId: string;
	metricDefinitions: any[];
	statusCode: number;
	constructor()
	/**
	 * Initializes a new instance of the ElasticPoolMetricDefinitions class.
	 */
	constructor()
}

/**
 * Represents the properties of an Azure SQL Database data masking policy.
 */
declare class AzureDataMaskingPolicyProperties {
	dataMaskingState: string;
	exemptPrincipals: string;
	constructor()
	/**
	 * Initializes a new instance of the DataMaskingPolicyProperties class with
	 * required arguments.
	 * @param dataMaskingState 
	 * @param exemptPrincipals 
	 */
	constructor(dataMaskingState: string, exemptPrincipals: string)
	/**
	 * Initializes a new instance of the DataMaskingPolicyProperties class.
	 */
	constructor()
}

/**
 * Represents the properties of an Azure SQL Database recommended index.
 */
declare class AzureRecommendedIndexProperties {
	reportedImpact: any[];
	schema: string;
	estimatedImpact: any[];
	indexType: string;
	columns: any[];
	created: Date;
	includedColumns: any[];
	indexScript: string;
	action: string;
	lastModified: Date;
	state: string;
	table: string;
	constructor()
	/**
	 * Initializes a new instance of the RecommendedIndexProperties class.
	 */
	constructor()
}

/**
 * Represents the response to a Get Azure SQL Server Active Directory
 * Administrators request.
 */
declare class AzureServerAdministratorGetResponse {
	administrator: AzureServerAdministrator;
	requestId: string;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * Represents the response to a Get Azure Sql Elastic Pool request.
 */
declare class AzureElasticPoolGetResponse {
	elasticPool: AzureElasticPool;
	requestId: string;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * Represents the response to a get recommended index request.
 */
declare class AzureRecommendedIndexUpdateResponse {
	recommendedIndex: AzureRecommendedIndex;
	retryAfter: number;
	requestId: string;
	operationStatusLink: string;
	error: AzureErrorResponse;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * Represents the response to a List Azure Sql Database Transparent Data
 * Encryption Activity request.
 */
declare class AzureTransparentDataEncryptionActivityListResponse {
	transparentDataEncryptionActivities: any[];
	requestId: string;
	statusCode: number;
	constructor()
	/**
	 * Initializes a new instance of the
	 * TransparentDataEncryptionActivityListResponse class.
	 */
	constructor()
}

/**
 * Create or update data masking rule parameters.
 */
declare class AzureDataMaskingRuleCreateOrUpdateParameters {
	properties: AzureDataMaskingRuleProperties;
	constructor()
	/**
	 * Initializes a new instance of the DataMaskingRuleCreateOrUpdateParameters
	 * class with required arguments.
	 * @param properties 
	 */
	constructor(properties: AzureDataMaskingRuleProperties)
	/**
	 * Initializes a new instance of the DataMaskingRuleCreateOrUpdateParameters
	 * class.
	 */
	constructor()
}

/**
 * Represents a Service Tier Advisor.
 */
declare class AzureServiceTierAdvisor {
	name: string;
	location: string;
	id: string;
	type: string;
	properties: AzureServiceTierAdvisorProperties;
	tags: Properties;
	constructor()
	/**
	 * Initializes a new instance of the ServiceTierAdvisor class with required
	 * arguments.
	 * @param location 
	 */
	constructor(location: string)
	/**
	 * Initializes a new instance of the ServiceTierAdvisor class.
	 */
	constructor()
}

/**
 * Represents an Azure SQL Database Elastic Pool metric definition.
 */
declare class AzureMetricDefinition {
	primaryAggregationType: string;
	unit: string;
	name: AzureMetricName;
	metricAvailabilities: any[];
	constructor()
	/**
	 * Initializes a new instance of the MetricDefinition class.
	 */
	constructor()
}

/**
 * Represents the response to a List Azure Sql Database Replication Link request.
 */
declare class AzureReplicationLinkListResponse {
	replicationLinks: any[];
	requestId: string;
	statusCode: number;
	constructor()
	/**
	 * Initializes a new instance of the ReplicationLinkListResponse class.
	 */
	constructor()
}

/**
 * Represents the properties of an Azure SQL Database Replication Link.
 */
declare class AzureReplicationLinkProperties {
	replicationState: string;
	role: string;
	partnerRole: string;
	partnerServer: string;
	partnerDatabase: string;
	startTime: Date;
	percentComplete: string;
	partnerLocation: string;
	constructor()
	constructor()
}

/**
 * Represents an Azure SQL Database auditing policy.
 */
declare class AzureDatabaseAuditingPolicy {
	name: string;
	location: string;
	id: string;
	type: string;
	properties: AzureDatabaseAuditingPolicyProperties;
	tags: Properties;
	constructor()
	/**
	 * Initializes a new instance of the DatabaseAuditingPolicy class with
	 * required arguments.
	 * @param location 
	 */
	constructor(location: string)
	/**
	 * Initializes a new instance of the DatabaseAuditingPolicy class.
	 */
	constructor()
}

/**
 * Start Azure SQL Database Server Upgrade properties.
 */
declare class AzureServerUpgradeProperties {
	scheduleUpgradeAfterUtcDateTime: Date;
	databaseCollection: any[];
	elasticPoolCollection: any[];
	version: string;
	constructor()
	/**
	 * Initializes a new instance of the ServerUpgradeProperties class.
	 */
	constructor()
	/**
	 * Initializes a new instance of the ServerUpgradeProperties class with
	 * required arguments.
	 * @param version 
	 */
	constructor(version: string)
}

/**
 * Create or update Azure Sql Database parameters properties.
 */
declare class AzureDatabaseCreateOrUpdateProperties {
	elasticPoolName: string;
	createMode: string;
	maxSizeBytes: number;
	sourceDatabaseId: string;
	edition: string;
	collation: string;
	requestedServiceObjectiveName: string;
	requestedServiceObjectiveId: string;
	constructor()
	constructor()
}

/**
 * Represents impact of an operation, both in absolute and relative terms.
 */
declare class AzureOperationImpact {
	unit: string;
	changeValueRelative: number;
	name: string;
	changeValueAbsolute: number;
	constructor()
	constructor()
}

/**
 * Represents an Azure SQL Database Elastic Pool metric name.
 */
declare class AzureValue {
	average: number;
	total: number;
	count: number;
	maximum: number;
	minimum: number;
	timestamp: Date;
	constructor()
	constructor()
}

/**
 * Represents the response to a List Azure Sql Database Server metrics request.
 */
declare class AzureServerMetricListResponse {
	requestId: string;
	metrics: any[];
	statusCode: number;
	constructor()
	/**
	 * Initializes a new instance of the ServerMetricListResponse class.
	 */
	constructor()
}

declare class AzureResourceBase {
	location: string;
	id: string;
	type: string;
	tags: Properties;
	constructor()
	/**
	 * Initializes a new instance of the ResourceBase class.
	 */
	constructor()
}

/**
 * Represents the response to a List Firewall Rules request.
 */
declare class AzureFirewallRuleGetResponse {
	requestId: string;
	firewallRule: AzureFirewallRule;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * Represents the properties of a recommended Azure SQL Database being upgraded.
 */
declare class AzureRecommendedDatabaseProperties {
	targetEdition: string;
	name: string;
	targetServiceLevelObjective: string;
	constructor()
	constructor()
}

/**
 * Represents an Azure SQL Database Elastic Pool metric.
 */
declare class AzureMetric {
	timeGrain: string;
	unit: string;
	values: any[];
	name: AzureName;
	startTime: Date;
	endTime: Date;
	constructor()
	/**
	 * Initializes a new instance of the Metric class.
	 */
	constructor()
}

/**
 * Represents the response to a get service tier advisor request.
 */
declare class AzureServiceTierAdvisorGetResponse {
	serviceTierAdvisor: AzureServiceTierAdvisor;
	requestId: string;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * Represents the response to a get recommended index request.
 */
declare class AzureRecommendedIndexGetResponse {
	recommendedIndex: AzureRecommendedIndex;
	requestId: string;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * Represents the properties of an Azure SQL Recommended Elastic Pool.
 */
declare class AzureRecommendedElasticPoolProperties {
	observationPeriodEnd: Date;
	databases: any[];
	databaseEdition: string;
	databaseDtuMin: number;
	databaseDtuMax: number;
	dtu: number;
	maxObservedDtu: number;
	observationPeriodStart: Date;
	storageMB: number;
	maxObservedStorageMB: number;
	metrics: any[];
	constructor()
	/**
	 * Initializes a new instance of the RecommendedElasticPoolProperties class.
	 */
	constructor()
}

/**
 * Represents the response to a Get Azure Sql Database Service Objective request.
 */
declare class AzureServiceObjectiveGetResponse {
	serviceObjective: AzureServiceObjective;
	requestId: string;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * Start Azure SQL Database Server Upgrade parameters.
 */
declare class AzureServerUpgradeStartParameters {
	properties: AzureServerUpgradeProperties;
	constructor()
	/**
	 * Initializes a new instance of the ServerUpgradeStartParameters class with
	 * required arguments.
	 * @param properties 
	 */
	constructor(properties: AzureServerUpgradeProperties)
	/**
	 * Initializes a new instance of the ServerUpgradeStartParameters class.
	 */
	constructor()
}

/**
 * Represents the Azure SQL capabilities for a region.
 */
declare class AzureLocationCapability {
	supportedServerVersions: any[];
	name: string;
	status: string;
	constructor()
	/**
	 * Initializes a new instance of the LocationCapability class.
	 */
	constructor()
}

/**
 * Represents an Azure SQL Database table.
 */
declare class AzureTable {
	name: string;
	location: string;
	id: string;
	type: string;
	properties: AzureTableProperties;
	tags: Properties;
	constructor()
	/**
	 * Initializes a new instance of the Table class with required arguments.
	 * @param location 
	 */
	constructor(location: string)
	/**
	 * Initializes a new instance of the Table class.
	 */
	constructor()
}

/**
 * The Windows Azure SQL Database management API provides a RESTful set of web
 * services that interact with Windows Azure SQL Database services to manage
 * your databases. The API enables users to create, retrieve, update, and
 * delete databases and servers.
 */
declare interface AzureSqlManagementClient {
	readonly apiVersion: string;
	readonly databaseBackupOperations: AzureDatabaseBackupOperations;
	longRunningOperationRetryTimeout: number;
	longRunningOperationInitialTimeout: number;
	readonly baseUri: string;
	readonly serversOperations: AzureServerOperations;
	readonly databaseActivationOperations: AzureDatabaseActivationOperations;
	readonly databasesOperations: AzureDatabaseOperations;
	/**
	 * Closes this stream and releases any system resources associated
	 * with it. If the stream is already closed then invoking this
	 * method has no effect.
	 * 
	 * <p> As noted in {@link AutoCloseable#close()}, cases where the
	 * close may fail require careful attention. It is strongly advised
	 * to relinquish the underlying resources and to internally
	 * <em>mark</em> the {@code Closeable} as closed, prior to throwing
	 * the {@code IOException}.
	 */
	close(): void;
}

/**
 * Represents all the operations for operating on Azure SQL Database Servers.
 * Contains operations to: Create, Retrieve, Update, and Delete servers.
 */
declare interface AzureServerOperations {
	/**
	 * Returns information about Azure SQL Database Server usage.
	 * @param arg0 
	 * @param arg1 
	 */
	listUsages(arg0: string, arg1: string): AzureServerMetricListResponse;
	/**
	 * Creates a new Azure SQL Database server.
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 */
	createOrUpdate(arg0: string, arg1: string, arg2: AzureServerCreateOrUpdateParameters): AzureServerGetResponse;
	/**
	 * Returns information about an Azure SQL Database Server.
	 * @param arg0 
	 * @param arg1 
	 */
	delete(arg0: string, arg1: string): AzureOperationResponse;
	/**
	 * Returns information about an Azure SQL Database Server.
	 * @param arg0 
	 * @param arg1 
	 */
	get(arg0: string, arg1: string): AzureServerGetResponse;
	/**
	 * Returns information about an Azure SQL Database Server.
	 * @param arg0 
	 */
	list(arg0: string): AzureServerListResponse;
}

/**
 * Represents all the operations for operating on Azure SQL Databases.  Contains
 * operations to: Create, Retrieve, Update, and Delete databases, and also
 * includes the ability to get the event logs for a database.
 */
declare interface AzureDatabaseOperations {
	/**
	 * Returns information about Azure SQL Databases.
	 * @param arg0 
	 * @param arg1 
	 */
	list(arg0: string, arg1: string): AzureDatabaseListResponse;
	/**
	 * Creates a new Azure SQL Database or updates an existing Azure SQL
	 * Database.
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 * @param arg3 
	 */
	createOrUpdate(arg0: string, arg1: string, arg2: string, arg3: AzureDatabaseCreateOrUpdateParameters): AzureDatabaseCreateOrUpdateResponse;
	/**
	 * Deletes the Azure SQL Database with the given name.
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 */
	delete(arg0: string, arg1: string, arg2: string): AzureOperationResponse;
	/**
	 * Begins creating a new Azure SQL Database or updating an existing Azure
	 * SQL Database. To determine the status of the operation call
	 * GetDatabaseOperationStatus.
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 * @param arg3 
	 */
	beginCreateOrUpdate(arg0: string, arg1: string, arg2: string, arg3: AzureDatabaseCreateOrUpdateParameters): AzureDatabaseCreateOrUpdateResponse;
	/**
	 * Gets the status of an Azure Sql Database create or update operation.
	 * @param arg0 
	 */
	getDatabaseOperationStatus(arg0: string): AzureDatabaseCreateOrUpdateResponse;
	/**
	 * Returns information about an Azure SQL Database.
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 * @param arg3 
	 */
	getExpanded(arg0: string, arg1: string, arg2: string, arg3: string): AzureDatabaseGetResponse;
	/**
	 * Returns information about Azure SQL Databases.
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 */
	listExpanded(arg0: string, arg1: string, arg2: string): AzureDatabaseListResponse;
	/**
	 * Returns information about an Azure SQL Database.
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 */
	get(arg0: string, arg1: string, arg2: string): AzureDatabaseGetResponse;
	/**
	 * Returns information about Azure SQL Database usages.
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 */
	listUsages(arg0: string, arg1: string, arg2: string): AzureDatabaseMetricListResponse;
	/**
	 * Returns information about an Azure SQL Database.
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 */
	getById(arg0: string, arg1: string, arg2: string): AzureDatabaseListResponse;
}

/**
 * Represents all the operations for operating on Azure SQL Database restore
 * points. Contains operations to: List restore points.
 */
declare interface AzureDatabaseBackupOperations {
	/**
	 * Returns a list of Azure SQL Database restore points.
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 */
	listRestorePoints(arg0: string, arg1: string, arg2: string): AzureRestorePointListResponse;
}

/**
 * Represents all the operations for operating pertaining to activation on Azure
 * SQL Data Warehouse databases. Contains operations to: Pause and Resume
 * databases
 */
declare interface AzureDatabaseActivationOperations {
	/**
	 * Start an Azure SQL Data Warehouse database resume operation.
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 */
	resume(arg0: string, arg1: string, arg2: string): AzureDatabaseCreateOrUpdateResponse;
	/**
	 * Start an Azure SQL Data Warehouse database pause operation.
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 */
	pause(arg0: string, arg1: string, arg2: string): AzureDatabaseCreateOrUpdateResponse;
	/**
	 * Start an Azure SQL Data Warehouse database pause operation.To determine
	 * the status of the operation call GetDatabaseActivationOperationStatus.
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 */
	beginPause(arg0: string, arg1: string, arg2: string): AzureDatabaseCreateOrUpdateResponse;
	/**
	 * Start an Azure SQL Data Warehouse database resume operation. To determine
	 * the status of the operation call GetDatabaseActivationOperationStatus.
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 */
	beginResume(arg0: string, arg1: string, arg2: string): AzureDatabaseCreateOrUpdateResponse;
	/**
	 * Gets the status of an Azure SQL Data Warehouse Database pause or resume
	 * operation.
	 * @param arg0 
	 */
	getDatabaseActivationOperationStatus(arg0: string): AzureDatabaseCreateOrUpdateResponse;
}

/**
 * The Get Web Site Publish Profile operation response.
 */
declare class AzureWebSiteGetPublishProfileResponse {
	publishProfiles: any[];
	requestId: string;
	statusCode: number;
	constructor()
	/**
	 * Initializes a new instance of the WebSiteGetPublishProfileResponse class.
	 */
	constructor()
}

/**
 * Describes a website.
 */
declare class AzureWebSite {
	readonly resourceGroup: AzureResourceGroupExtended;
	readonly displayName: string;
	readonly internalIdString: string;
	name: string;
	readonly location: string;
	readonly connection: AzureConnection;
	id: string;
	readonly type: string;
	properties: AzureWebSiteProperties;
	tags: Properties;
	constructor()
	/**
	 * Initializes a new instance of the WebSite class with required arguments.
	 * @param location 
	 */
	constructor(location: string)
	/**
	 * Initializes a new instance of the WebSite class.
	 */
	constructor()
	delete(): void;
}

/**
 * The Get Web Site Repository operation response.
 */
declare class AzureWebSiteGetRepositoryResponse {
	requestId: string;
	uri: string;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * The Delete Web Site Repository operation response.
 */
declare class AzureWebSiteDeleteRepositoryResponse {
	requestId: string;
	uri: string;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * Publishing Credentials  for the website.
 */
declare class AzurePublishingCredentials {
	publishingPassword: string;
	publishingUserName: string;
	constructor()
	/**
	 * Initializes a new instance of the PublishingCredentials class with
	 * required arguments.
	 * @param publishingPassword 
	 * @param publishingUserName 
	 */
	constructor(publishingPassword: string, publishingUserName: string)
	/**
	 * Initializes a new instance of the PublishingCredentials class.
	 */
	constructor()
}

/**
 * List of metadata for the website.
 */
declare class AzureWebSiteMetadataResult {
	resource: AzureWebSiteMetadataEnvelope;
	requestId: string;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * Connection string for database and other external resources.
 */
declare class AzureConnectionStringInfo {
	connectionString: string;
	name: string;
	type: AzureDatabaseServerType;
	constructor()
	constructor()
}

/**
 * List of app settings for the website.
 */
declare class AzureSlotConfigNamesEnvelope {
	name: string;
	location: string;
	id: string;
	type: string;
	properties: AzureSlotConfigNames;
	tags: Properties;
	constructor()
	/**
	 * Initializes a new instance of the SlotConfigNamesEnvelope class with
	 * required arguments.
	 * @param location 
	 */
	constructor(location: string)
	/**
	 * Initializes a new instance of the SlotConfigNamesEnvelope class.
	 */
	constructor()
}

/**
 * Parameters supplied to the Get Historical Usage Metrics Web hosting plan
 * operation.
 */
declare class AzureWebHostingPlanGetHistoricalUsageMetricsParameters {
	metricNames: any[];
	timeGrain: string;
	includeInstanceBreakdown: boolean;
	startTime: Date;
	endTime: Date;
	constructor()
	/**
	 * Initializes a new instance of the
	 * WebHostingPlanGetHistoricalUsageMetricsParameters class.
	 */
	constructor()
}

/**
 * The website operation response.
 */
declare class AzureWebSiteAsyncOperationResponse {
	retryAfter: string;
	requestId: string;
	location: string;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * The Update Web Site connection strings operation parameters.
 */
declare class AzureWebSiteUpdateConnectionStringsParameters {
	name: string;
	location: string;
	id: string;
	type: string;
	properties: any[];
	tags: Properties;
	constructor()
	/**
	 * Initializes a new instance of the
	 * WebSiteUpdateConnectionStringsParameters class with required arguments.
	 * @param properties 
	 * @param location 
	 */
	constructor(properties: AzureConnectionStringInfo[], location: string)
	/**
	 * Initializes a new instance of the
	 * WebSiteUpdateConnectionStringsParameters class.
	 */
	constructor()
}

/**
 * The backup record created based on the backup request.
 */
declare class AzureWebSiteBackupResponse {
	requestId: string;
	backupItem: AzureBackupItemEnvelope;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * The Get Web Site Configuration operation response.
 */
declare class AzureWebSiteConfiguration {
	numberOfWorkers: number;
	pythonVersion: string;
	remoteDebuggingVersion: AzureRemoteDebuggingVersion;
	metadata: Properties;
	detailedErrorLoggingEnabled: boolean;
	defaultDocuments: any[];
	use32BitWorkerProcess: boolean;
	publishingPassword: string;
	logsDirectorySizeLimit: number;
	remoteDebuggingEnabled: boolean;
	requestTracingEnabled: boolean;
	webSocketsEnabled: boolean;
	netFrameworkVersion: string;
	limits: AzureSiteLimits;
	phpVersion: string;
	connectionStrings: any[];
	httpLoggingEnabled: boolean;
	managedPipelineMode: AzureManagedPipelineMode;
	autoSwapSlotName: string;
	handlerMappings: any[];
	appSettings: Properties;
	documentRoot: string;
	requestTracingExpirationTime: Date;
	scmType: string;
	publishingUserName: string;
	constructor()
	/**
	 * Initializes a new instance of the WebSiteConfiguration class.
	 */
	constructor()
}

/**
 * Historical metric snapshot data sample.
 */
declare class AzureHistoricalUsageMetricSample {
	total: string;
	instanceName: string;
	count: number;
	maximum: string;
	timeCreated: Date;
	minimum: string;
	constructor()
	constructor()
}

/**
 * List of backups for the website.
 */
declare class AzureWebSiteRestoreDiscover {
	name: string;
	location: string;
	id: string;
	type: string;
	properties: AzureWebSiteRestoreDiscoverProperties;
	tags: Properties;
	constructor()
	/**
	 * Initializes a new instance of the WebSiteRestoreDiscover class with
	 * required arguments.
	 * @param location 
	 */
	constructor(location: string)
	/**
	 * Initializes a new instance of the WebSiteRestoreDiscover class.
	 */
	constructor()
}

/**
 * Defines a backup schedule for a website.
 */
declare class AzureBackupSchedule {
	retentionPeriodInDays: number;
	frequencyUnit: AzureFrequencyUnit;
	frequencyInterval: number;
	startTime: Date;
	lastExecutionTime: Date;
	keepAtLeastOneBackup: boolean;
	constructor()
	constructor()
}

/**
 * Historical metric snapshot data for the web site.
 */
declare class AzureHistoricalUsageMetricData {
	primaryAggregationType: string;
	unit: string;
	timeGrain: string;
	displayName: string;
	values: any[];
	name: string;
	startTime: Date;
	endTime: Date;
	constructor()
	/**
	 * Initializes a new instance of the HistoricalUsageMetricData class.
	 */
	constructor()
}

/**
 * Clone website parameters.
 */
declare class AzureWebSiteCloneParameters {
	webSiteClone: AzureWebSiteCloneBase;
	constructor()
	/**
	 * Initializes a new instance of the WebSiteCloneParameters class with
	 * required arguments.
	 * @param webSiteClone 
	 */
	constructor(webSiteClone: AzureWebSiteCloneBase)
	/**
	 * Initializes a new instance of the WebSiteCloneParameters class.
	 */
	constructor()
}

/**
 * The List Web Sites operation response.
 */
declare class AzureWebSiteListResponse {
	requestId: string;
	webSites: any[];
	statusCode: number;
	constructor()
	/**
	 * Initializes a new instance of the WebSiteListResponse class.
	 */
	constructor()
}

/**
 * The Create Web Hosting Plan operation response.
 */
declare class AzureWebHostingPlanCreateOrUpdateResponse {
	requestId: string;
	webHostingPlan: AzureWebHostingPlan;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * The Get Historical Usage Metrics Web hosting plan operation response.
 */
declare class AzureWebHostingPlanGetHistoricalUsageMetricsResponse {
	usageMetrics: any[];
	requestId: string;
	statusCode: number;
	constructor()
	/**
	 * Initializes a new instance of the
	 * WebHostingPlanGetHistoricalUsageMetricsResponse class.
	 */
	constructor()
}

/**
 * Parameters supplied to the Create or Update operation for the resource group.
 */
declare class AzureResourceGroupCreateOrUpdateParameters {
	location: string;
	constructor()
	/**
	 * Initializes a new instance of the ResourceGroupCreateOrUpdateParameters
	 * class with required arguments.
	 * @param location 
	 */
	constructor(location: string)
	/**
	 * Initializes a new instance of the ResourceGroupCreateOrUpdateParameters
	 * class.
	 */
	constructor()
}

/**
 * List of backups for the website.
 */
declare class AzureWebSiteGetBackupsResponse {
	requestId: string;
	backupList: AzureBackupItemsEnvelope;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * The Create or Update resource group operation response.
 */
declare class AzureResourceGroupCreateOrUpdateResponse {
	requestId: string;
	name: string;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * Specifies a custom executable program for handling requests for specific
 * file name extensions.
 */
declare class AzureHandlerMapping {
	extension: string;
	scriptProcessor: string;
	arguments: string;
	constructor()
	constructor()
}

/**
 * Cloning information for target site
 */
declare class AzureCloningInfo {
	correlationId: string;
	source: AzureSourceWebSite;
	cloneCustomHostNames: boolean;
	overwrite: boolean;
	hostingEnvironment: string;
	constructor()
	constructor()
}

/**
 * The get source control operation response.
 */
declare class AzureSourceControlGetResponse {
	requestId: string;
	sourceControl: AzureSourceControl;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * List of app settings for the website.
 */
declare class AzureWebSiteAppSettingsEnvelope {
	name: string;
	location: string;
	id: string;
	type: string;
	properties: any[];
	tags: Properties;
	constructor()
	/**
	 * Initializes a new instance of the WebSiteAppSettingsEnvelope class with
	 * required arguments.
	 * @param location 
	 */
	constructor(location: string)
	/**
	 * Initializes a new instance of the WebSiteAppSettingsEnvelope class.
	 */
	constructor()
}

/**
 * A specific backup.
 */
declare class AzureBackupRequest {
	backupSchedule: AzureBackupSchedule;
	databases: any[];
	name: string;
	storageAccountUrl: string;
	enabled: boolean;
	constructor()
	/**
	 * Initializes a new instance of the BackupRequest class.
	 */
	constructor()
}

/**
 * Configuration for the website.
 */
declare class AzureWebSiteConfigurationEnvelope {
	name: string;
	location: string;
	id: string;
	type: string;
	properties: AzureWebSiteConfiguration;
	tags: Properties;
	constructor()
	/**
	 * Initializes a new instance of the WebSiteConfigurationEnvelope class with
	 * required arguments.
	 * @param location 
	 */
	constructor(location: string)
	/**
	 * Initializes a new instance of the WebSiteConfigurationEnvelope class.
	 */
	constructor()
}

/**
 * The slot specific config names.
 */
declare class AzureSlotConfigNames {
	connectionStringNames: any[];
	appSettingNames: any[];
	constructor()
	/**
	 * Initializes a new instance of the SlotConfigNames class.
	 */
	constructor()
}

/**
 * Link site to source control request parameters.
 */
declare class AzureSiteSourceControlUpdateParameters {
	properties: AzureSiteSourceControlProperties;
	constructor()
	/**
	 * Initializes a new instance of the SiteSourceControlUpdateParameters class
	 * with required arguments.
	 * @param properties 
	 */
	constructor(properties: AzureSiteSourceControlProperties)
	/**
	 * Initializes a new instance of the SiteSourceControlUpdateParameters class.
	 */
	constructor()
}

/**
 * List of slot specific settings.
 */
declare class AzureSlotConfigNamesResult {
	resource: AzureSlotConfigNamesEnvelope;
	requestId: string;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * Publishing credentials for the website.
 */
declare class AzureWebSitePublishingCredentialsResult {
	resource: AzureWebSitePublishingCredentialsEnvelope;
	requestId: string;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * The Get Web Site Historical Usage Metrics operation response.
 */
declare class AzureWebSiteGetHistoricalUsageMetricsResponse {
	usageMetrics: any[];
	requestId: string;
	statusCode: number;
	constructor()
	/**
	 * Initializes a new instance of the
	 * WebSiteGetHistoricalUsageMetricsResponse class.
	 */
	constructor()
}

/**
 * TODO
 */
declare class AzureBackupScheduleRequestEnvelope {
	name: string;
	location: string;
	id: string;
	type: string;
	properties: AzureBackupScheduleRequestResponse;
	tags: Properties;
	constructor()
	/**
	 * Initializes a new instance of the BackupScheduleRequestEnvelope class
	 * with required arguments.
	 * @param location 
	 */
	constructor(location: string)
	/**
	 * Initializes a new instance of the BackupScheduleRequestEnvelope class.
	 */
	constructor()
}

/**
 * Describes a website.
 */
declare class AzureWebSiteCloneBase {
	name: string;
	location: string;
	id: string;
	type: string;
	properties: AzureWebSiteCloneBaseProperties;
	tags: Properties;
	constructor()
	/**
	 * Initializes a new instance of the WebSiteCloneBase class with required
	 * arguments.
	 * @param location 
	 */
	constructor(location: string)
	/**
	 * Initializes a new instance of the WebSiteCloneBase class.
	 */
	constructor()
}

/**
 * Describes a website.
 */
declare class AzureWebSiteBase {
	name: string;
	location: string;
	id: string;
	type: string;
	properties: AzureWebSiteBaseProperties;
	tags: Properties;
	constructor()
	/**
	 * Initializes a new instance of the WebSiteBase class with required
	 * arguments.
	 * @param location 
	 */
	constructor(location: string)
	/**
	 * Initializes a new instance of the WebSiteBase class.
	 */
	constructor()
}

/**
 * Source site for cloning
 */
declare class AzureSourceWebSite {
	resourceGroupName: string;
	name: string;
	location: string;
	slot: string;
	subscriptionId: string;
	constructor()
	constructor()
}

/**
 * A specific backup.
 */
declare class AzureBackupRequestEnvelope {
	request: AzureBackupRequest;
	name: string;
	location: string;
	id: string;
	type: string;
	tags: Properties;
	constructor()
	/**
	 * Initializes a new instance of the BackupRequestEnvelope class with
	 * required arguments.
	 * @param location 
	 */
	constructor(location: string)
	/**
	 * Initializes a new instance of the BackupRequestEnvelope class.
	 */
	constructor()
}

/**
 * Publishing credentials for the website.
 */
declare class AzureWebSitePublishingCredentialsEnvelope {
	name: string;
	location: string;
	id: string;
	type: string;
	properties: AzurePublishingCredentials;
	tags: Properties;
	constructor()
	/**
	 * Initializes a new instance of the WebSitePublishingCredentialsEnvelope
	 * class with required arguments.
	 * @param location 
	 */
	constructor(location: string)
	/**
	 * Initializes a new instance of the WebSitePublishingCredentialsEnvelope
	 * class.
	 */
	constructor()
}

/**
 * List of metadata for the website.
 */
declare class AzureWebSiteMetadataEnvelope {
	name: string;
	location: string;
	id: string;
	type: string;
	properties: any[];
	tags: Properties;
	constructor()
	/**
	 * Initializes a new instance of the WebSiteMetadataEnvelope class with
	 * required arguments.
	 * @param location 
	 */
	constructor(location: string)
	/**
	 * Initializes a new instance of the WebSiteMetadataEnvelope class.
	 */
	constructor()
}

/**
 * The Get Web Site Configuration operation parameters.
 */
declare class AzureWebSiteGetConfigurationParameters {
	propertiesToInclude: any[];
	constructor()
	/**
	 * Initializes a new instance of the WebSiteGetConfigurationParameters class.
	 */
	constructor()
}

/**
 * The List Web Sites operation parameters.
 */
declare class AzureWebSiteListParameters {
	propertiesToInclude: any[];
	constructor()
	/**
	 * Initializes a new instance of the WebSiteListParameters class.
	 */
	constructor()
}

/**
 * TODO
 */
declare class AzureBackupScheduleRequestResponse {
	backupSchedule: AzureBackupSchedule;
	databases: any[];
	name: string;
	storageAccountUrl: string;
	enabled: boolean;
	constructor()
	/**
	 * Initializes a new instance of the BackupScheduleRequestResponse class.
	 */
	constructor()
}

/**
 * A specific backup.
 */
declare class AzureDatabaseBackupSetting {
	connectionString: string;
	databaseType: string;
	connectionStringName: string;
	name: string;
	constructor()
	constructor()
}

/**
 * Describes the site's source control.
 */
declare class AzureSiteSourceControl {
	name: string;
	location: string;
	id: string;
	type: string;
	properties: AzureSiteSourceControlProperties;
	tags: Properties;
	constructor()
	/**
	 * Initializes a new instance of the SiteSourceControl class with required
	 * arguments.
	 * @param location 
	 */
	constructor(location: string)
	/**
	 * Initializes a new instance of the SiteSourceControl class.
	 */
	constructor()
}

/**
 * A specific backup.
 */
declare class AzureBackupItem {
	blobName: string;
	databases: any[];
	sizeInBytes: number;
	log: string;
	scheduled: boolean;
	created: Date;
	name: string;
	storageAccountUrl: string;
	correlationId: string;
	finishedTimeStamp: Date;
	status: AzureBackupItemStatus;
	lastRestoreTimeStamp: Date;
	constructor()
	/**
	 * Initializes a new instance of the BackupItem class.
	 */
	constructor()
}

/**
 * List of connection strings for the website.
 */
declare class AzureWebSiteConnectionStringsResult {
	resource: AzureWebSiteConnectionStringsEnvelope;
	requestId: string;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * The Update slot configs parameters.
 */
declare class AzureSlotConfigNamesUpdateParameters {
	name: string;
	location: string;
	id: string;
	type: string;
	properties: AzureSlotConfigNames;
	tags: Properties;
	constructor()
	/**
	 * Initializes a new instance of the SlotConfigNamesUpdateParameters class
	 * with required arguments.
	 * @param properties 
	 * @param location 
	 */
	constructor(properties: AzureSlotConfigNames, location: string)
	/**
	 * Initializes a new instance of the SlotConfigNamesUpdateParameters class.
	 */
	constructor()
}

/**
 * The Get Web Hosting Plan operation response.
 */
declare class AzureWebHostingPlanGetResponse {
	requestId: string;
	webHostingPlan: AzureWebHostingPlan;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * List of app settings for the website.
 */
declare class AzureWebSiteAppSettingsResult {
	resource: AzureWebSiteAppSettingsEnvelope;
	requestId: string;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * Represents the properties of the site's source control.
 */
declare class AzureSiteSourceControlProperties {
	repoUrl: string;
	readonly manualIntegration: boolean;
	readonly mercurial: boolean;
	deploymentRollbackEnabled: boolean;
	branch: string;
	constructor()
	/**
	 * Initializes a new instance of the SiteSourceControlProperties class with
	 * required arguments.
	 * @param repoUrl 
	 */
	constructor(repoUrl: string)
	/**
	 * Initializes a new instance of the SiteSourceControlProperties class.
	 */
	constructor()
}

/**
 * Config for the website.
 */
declare class AzureWebSiteGetConfigurationResult {
	resource: AzureWebSiteConfigurationEnvelope;
	requestId: string;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * Parameters supplied to the clone Web Site operation.
 */
declare class AzureWebSiteCloneBaseProperties {
	cloningInfo: AzureCloningInfo;
	serverFarm: string;
	constructor()
	constructor()
}

/**
 * The Update Web Site Configuration parameters.
 */
declare class AzureWebSiteUpdateConfigurationDetails {
	pythonVersion: string;
	numberOfWorkers: number;
	remoteDebuggingVersion: AzureRemoteDebuggingVersion;
	connectionStrings: any[];
	metadata: Properties;
	defaultDocuments: any[];
	detailedErrorLoggingEnabled: boolean;
	use32BitWorkerProcess: boolean;
	httpLoggingEnabled: boolean;
	autoSwapSlotName: string;
	logsDirectorySizeLimit: number;
	managedPipelineMode: AzureManagedPipelineMode;
	documentRoot: string;
	requestTracingExpirationTime: Date;
	appSettings: Properties;
	scmType: string;
	remoteDebuggingEnabled: boolean;
	requestTracingEnabled: boolean;
	webSocketsEnabled: boolean;
	alwaysOn: boolean;
	netFrameworkVersion: string;
	limits: AzureSiteLimits;
	phpVersion: string;
	constructor()
	/**
	 * Initializes a new instance of the WebSiteUpdateConfigurationDetails class.
	 */
	constructor()
}

/**
 * Contains attributes that hold publish profile values.
 */
declare class AzurePublishProfile {
	profileName: string;
	userPassword: string;
	sqlServerConnectionString: string;
	publishMethod: string;
	controlPanelUri: string;
	userName: string;
	ftpPassiveMode: boolean;
	destinationAppUri: string;
	mSDeploySite: string;
	publishUrl: string;
	hostingProviderForumUri: string;
	mySqlConnectionString: string;
	constructor()
	/**
	 * Initializes a new instance of the PublishProfile class.
	 */
	constructor()
}

/**
 * Represents the properties of a website.
 */
declare class AzureWebSiteProperties {
	enabledHostNames: any[];
	repositorySiteName: string;
	trafficManagerHostNames: any[];
	lastModifiedTimeUtc: Date;
	hostNames: any[];
	hostNameSslStates: any[];
	provisioningState: string;
	uri: string;
	enabled: boolean;
	serverFarmId: string;
	webSpace: string;
	availabilityState: AzureWebSpaceAvailabilityState;
	usageState: AzureWebSiteUsageState;
	adminEnabled: boolean;
	siteConfig: AzureWebSiteConfiguration;
	runtimeAvailabilityState: AzureWebSiteRuntimeAvailabilityState;
	state: AzureWebSiteState;
	sku: AzureSkuOptions;
	serverFarm: string;
	properties: AzureSiteProperties;
	constructor()
	/**
	 * Initializes a new instance of the WebSiteProperties class.
	 */
	constructor()
}

/**
 * Represents the properties of a Web Hosting Plan.
 */
declare class AzureWebHostingPlanProperties {
	numberOfWorkers: number;
	adminSiteName: string;
	workerSize: AzureWorkerSizeOptions;
	sku: AzureSkuOptions;
	constructor()
	constructor()
}

/**
 * The information gathered about a backup storaged in a storage account.
 */
declare class AzureWebSiteRestoreDiscoverResponse {
	envelope: AzureWebSiteRestoreDiscover;
	requestId: string;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * Restore request to either a new or an existing site.
 */
declare class AzureRestoreRequest {
	blobName: string;
	databases: any[];
	ignoreConflictingHostNames: boolean;
	adjustConnectionStrings: boolean;
	storageAccountUrl: string;
	overwrite: boolean;
	constructor()
	/**
	 * Initializes a new instance of the RestoreRequest class.
	 */
	constructor()
}

/**
 * The Create Web Space operation response.
 */
declare class AzureWebSiteCreateResponse {
	webSite: AzureWebSite;
	requestId: string;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * Parameters supplied to the Create Web Site operation.
 */
declare class AzureWebSiteBaseProperties {
	serverFarm: string;
	constructor()
	/**
	 * Initializes a new instance of the WebSiteBaseProperties class with
	 * required arguments.
	 * @param serverFarm 
	 */
	constructor(serverFarm: string)
	/**
	 * Initializes a new instance of the WebSiteBaseProperties class.
	 */
	constructor()
}

/**
 * The Get Web Site Details operation response.
 */
declare class AzureWebSiteGetResponse {
	webSite: AzureWebSite;
	requestId: string;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * The Get Web Site Usage Metrics operation response.
 */
declare class AzureWebSiteGetUsageMetricsResponse {
	requestId: string;
	statusCode: number;
	constructor()
	/**
	 * Initializes a new instance of the WebSiteGetUsageMetricsResponse class.
	 */
	constructor()
}

/**
 * The Update Web Site key value pair operation parameters.
 */
declare class AzureWebSiteNameValueParameters {
	name: string;
	location: string;
	id: string;
	type: string;
	properties: any[];
	tags: Properties;
	constructor()
	/**
	 * Initializes a new instance of the WebSiteNameValueParameters class with
	 * required arguments.
	 * @param properties 
	 * @param location 
	 */
	constructor(properties: AzureNameValuePair[], location: string)
	/**
	 * Initializes a new instance of the WebSiteNameValueParameters class.
	 */
	constructor()
}

/**
 * Restore operation information.
 */
declare class AzureWebSiteRestoreResponse {
	requestId: string;
	operationId: string;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * Create or update Web Hosting Plan parameters.
 */
declare class AzureWebHostingPlanCreateOrUpdateParameters {
	webHostingPlan: AzureWebHostingPlan;
	constructor()
	/**
	 * Initializes a new instance of the WebHostingPlanCreateOrUpdateParameters
	 * class with required arguments.
	 * @param webHostingPlan 
	 */
	constructor(webHostingPlan: AzureWebHostingPlan)
	/**
	 * Initializes a new instance of the WebHostingPlanCreateOrUpdateParameters
	 * class.
	 */
	constructor()
}

/**
 * List of backups for the website.
 */
declare class AzureWebSiteConnectionStringsEnvelope {
	name: string;
	location: string;
	id: string;
	type: string;
	properties: any[];
	tags: Properties;
	constructor()
	/**
	 * Initializes a new instance of the WebSiteConnectionStringsEnvelope class
	 * with required arguments.
	 * @param location 
	 */
	constructor(location: string)
	/**
	 * Initializes a new instance of the WebSiteConnectionStringsEnvelope class.
	 */
	constructor()
}

/**
 * The update site configuration parameters.
 */
declare class AzureWebSiteUpdateConfigurationParameters {
	name: string;
	location: string;
	id: string;
	type: string;
	properties: AzureWebSiteUpdateConfigurationDetails;
	tags: Properties;
	constructor()
	/**
	 * Initializes a new instance of the WebSiteUpdateConfigurationParameters
	 * class with required arguments.
	 * @param properties 
	 * @param location 
	 */
	constructor(properties: AzureWebSiteUpdateConfigurationDetails, location: string)
	/**
	 * Initializes a new instance of the WebSiteUpdateConfigurationParameters
	 * class.
	 */
	constructor()
}

/**
 * Describes a source control.
 */
declare class AzureSourceControl {
	name: string;
	location: string;
	id: string;
	type: string;
	properties: AzureSourceControlProperties;
	tags: Properties;
	constructor()
	/**
	 * Initializes a new instance of the SourceControl class with required
	 * arguments.
	 * @param location 
	 */
	constructor(location: string)
	/**
	 * Initializes a new instance of the SourceControl class.
	 */
	constructor()
}

/**
 * The Get Web Site Historical Usage Metrics parameters.
 */
declare class AzureWebSiteGetHistoricalUsageMetricsParameters {
	metricNames: any[];
	timeGrain: string;
	includeInstanceBreakdown: boolean;
	startTime: Date;
	endTime: Date;
	slotView: boolean;
	constructor()
	/**
	 * Initializes a new instance of the
	 * WebSiteGetHistoricalUsageMetricsParameters class.
	 */
	constructor()
}

/**
 * The list source controls operation response.
 */
declare class AzureSourceControlListResponse {
	requestId: string;
	sourceControls: any[];
	statusCode: number;
	constructor()
	/**
	 * Initializes a new instance of the SourceControlListResponse class.
	 */
	constructor()
}

/**
 * The information gathered about a backup storaged in a storage account.
 */
declare class AzureWebSiteRestoreDiscoverProperties {
	blobName: string;
	databases: any[];
	ignoreConflictingHostNames: boolean;
	adjustConnectionStrings: boolean;
	storageAccountUrl: string;
	overwrite: boolean;
	constructor()
	/**
	 * Initializes a new instance of the WebSiteRestoreDiscoverProperties class.
	 */
	constructor()
}

/**
 * Historical metric snapshot for the web site.
 */
declare class AzureHistoricalUsageMetric {
	code: string;
	data: AzureHistoricalUsageMetricData;
	message: string;
	constructor()
	constructor()
}

/**
 * The Delete Web Site operation parameters.
 */
declare class AzureWebSiteDeleteParameters {
	deleteEmptyServerFarm: boolean;
	deleteAllSlots: boolean;
	deleteMetrics: boolean;
	constructor()
	/**
	 * Initializes a new instance of the WebSiteDeleteParameters class with
	 * required arguments.
	 * @param deleteEmptyServerFarm 
	 * @param deleteMetrics 
	 * @param deleteAllSlots 
	 */
	constructor(deleteEmptyServerFarm: boolean, deleteMetrics: boolean, deleteAllSlots: boolean)
	/**
	 * Initializes a new instance of the WebSiteDeleteParameters class.
	 */
	constructor()
}

/**
 * Name value pair.
 */
declare class AzureNameValuePair {
	name: string;
	value: string;
	constructor()
	constructor()
}

/**
 * The link site to source control operation response.
 */
declare class AzureSiteSourceControlUpdateResponse {
	siteSourceControl: AzureSiteSourceControl;
	requestId: string;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * The List Web Hosting Plans operation response.
 */
declare class AzureWebHostingPlanListResponse {
	requestId: string;
	webHostingPlans: any[];
	statusCode: number;
	constructor()
	/**
	 * Initializes a new instance of the WebHostingPlanListResponse class.
	 */
	constructor()
}

/**
 * Scheduled backup definition.
 */
declare class AzureWebSiteGetBackupConfigurationResponse {
	backupSchedule: AzureBackupScheduleRequestEnvelope;
	requestId: string;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * Create or update website parameters.
 */
declare class AzureWebSiteCreateOrUpdateParameters {
	webSite: AzureWebSiteBase;
	constructor()
	/**
	 * Initializes a new instance of the WebSiteCreateOrUpdateParameters class
	 * with required arguments.
	 * @param webSite 
	 */
	constructor(webSite: AzureWebSiteBase)
	/**
	 * Initializes a new instance of the WebSiteCreateOrUpdateParameters class.
	 */
	constructor()
}

/**
 * List of backups for the website.
 */
declare class AzureBackupItemsEnvelope {
	name: string;
	location: string;
	id: string;
	type: string;
	properties: any[];
	tags: Properties;
	constructor()
	/**
	 * Initializes a new instance of the BackupItemsEnvelope class with required
	 * arguments.
	 * @param location 
	 */
	constructor(location: string)
	/**
	 * Initializes a new instance of the BackupItemsEnvelope class.
	 */
	constructor()
}

/**
 * The update source control operation response.
 */
declare class AzureSourceControlUpdateResponse {
	requestId: string;
	sourceControl: AzureSourceControl;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * Describes a Web Hosting Plan.
 */
declare class AzureWebHostingPlan {
	name: string;
	location: string;
	id: string;
	type: string;
	properties: AzureWebHostingPlanProperties;
	tags: Properties;
	constructor()
	/**
	 * Initializes a new instance of the WebHostingPlan class with required
	 * arguments.
	 * @param location 
	 */
	constructor(location: string)
	/**
	 * Initializes a new instance of the WebHostingPlan class.
	 */
	constructor()
}

/**
 * Per site limits
 */
declare class AzureSiteLimits {
	maxMemoryInMb: number;
	maxPercentageCpu: number;
	maxDiskSizeInMb: number;
	constructor()
	constructor()
}

/**
 * The Get Web Site Configuration operation response.
 */
declare class AzureWebSiteGetConfigurationResponse {
	pythonVersion: string;
	numberOfWorkers: number;
	remoteDebuggingVersion: AzureRemoteDebuggingVersion;
	connectionStrings: any[];
	metadata: Properties;
	defaultDocuments: any[];
	detailedErrorLoggingEnabled: boolean;
	use32BitWorkerProcess: boolean;
	publishingPassword: string;
	httpLoggingEnabled: boolean;
	autoSwapSlotName: string;
	logsDirectorySizeLimit: number;
	managedPipelineMode: AzureManagedPipelineMode;
	documentRoot: string;
	appSettings: Properties;
	requestTracingExpirationTime: Date;
	scmType: string;
	remoteDebuggingEnabled: boolean;
	requestTracingEnabled: boolean;
	webSocketsEnabled: boolean;
	publishingUserName: string;
	netFrameworkVersion: string;
	phpVersion: string;
	constructor()
	/**
	 * Initializes a new instance of the WebSiteGetConfigurationResponse class.
	 */
	constructor()
}

/**
 * Update source control parameters.
 */
declare class AzureSourceControlUpdateParameters {
	properties: AzureSourceControlProperties;
	constructor()
	/**
	 * Initializes a new instance of the SourceControlUpdateParameters class
	 * with required arguments.
	 * @param properties 
	 */
	constructor(properties: AzureSourceControlProperties)
	/**
	 * Initializes a new instance of the SourceControlUpdateParameters class.
	 */
	constructor()
}

/**
 * The Get Web Site operation parameters.
 */
declare class AzureWebSiteGetParameters {
	propertiesToInclude: any[];
	constructor()
	/**
	 * Initializes a new instance of the WebSiteGetParameters class.
	 */
	constructor()
}

/**
 * Represents the properties of a source control.
 */
declare class AzureSourceControlProperties {
	tokenSecret: string;
	token: string;
	constructor()
	constructor()
}

/**
 * List of backups for the website.
 */
declare class AzureBackupItemEnvelope {
	name: string;
	location: string;
	id: string;
	type: string;
	properties: AzureBackupItem;
	tags: Properties;
	constructor()
	/**
	 * Initializes a new instance of the BackupItemEnvelope class with required
	 * arguments.
	 * @param location 
	 */
	constructor(location: string)
	/**
	 * Initializes a new instance of the BackupItemEnvelope class.
	 */
	constructor()
}

/**
 * Restore request to either a new or an existing site.
 */
declare class AzureRestoreRequestEnvelope {
	request: AzureRestoreRequest;
	name: string;
	location: string;
	id: string;
	type: string;
	tags: Properties;
	constructor()
	/**
	 * Initializes a new instance of the RestoreRequestEnvelope class with
	 * required arguments.
	 * @param location 
	 */
	constructor(location: string)
	/**
	 * Initializes a new instance of the RestoreRequestEnvelope class.
	 */
	constructor()
}

/**
 * SSL states bound to a website.
 */
declare class AzureWebSiteHostNameSslState {
	sslState: AzureWebSiteSslState;
	toUpdateIpBasedSsl: boolean;
	ipBasedSslResult: string;
	toUpdate: boolean;
	hostType: AzureHostType;
	thumbprint: string;
	name: string;
	virtualIP: string;
	constructor()
	constructor()
}

declare class AzureSiteProperties {
	metadata: Properties;
	appSettings: Properties;
	properties: Properties;
	constructor()
	/**
	 * Initializes a new instance of the SiteProperties class.
	 */
	constructor()
}

/**
 * The Windows Azure Web Sites management API provides a RESTful set of web
 * services that interact with Windows Azure Web Sites service to manage your
 * web sites. The API has entities that capture the relationship between an end
 * user and the Windows Azure Web Sites service.  (see
 * http://msdn.microsoft.com/en-us/library/windowsazure/dn166981.aspx for more
 * information)
 */
declare interface AzureWebSiteManagementClient {
	readonly webHostingPlansOperations: AzureWebHostingOperations;
	readonly apiVersion: string;
	longRunningOperationRetryTimeout: number;
	longRunningOperationInitialTimeout: number;
	readonly baseUri: string;
	readonly sourceControlsOperations: AzureSourceControlOperations;
	readonly webSitesOperations: AzureWebsiteOperations;
	/**
	 * Closes this stream and releases any system resources associated
	 * with it. If the stream is already closed then invoking this
	 * method has no effect.
	 * 
	 * <p> As noted in {@link AutoCloseable#close()}, cases where the
	 * close may fail require careful attention. It is strongly advised
	 * to relinquish the underlying resources and to internally
	 * <em>mark</em> the {@code Closeable} as closed, prior to throwing
	 * the {@code IOException}.
	 */
	close(): void;
}

/**
 * Operations for managing the web sites in a web space.
 */
declare interface AzureWebsiteOperations {
	/**
	 * Retrieve the publish settings information for a web site.  (see
	 * http://msdn.microsoft.com/en-us/library/windowsazure/dn166996.aspx for
	 * more information)
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 */
	getPublishProfile(arg0: string, arg1: string, arg2: string): AzureWebSiteGetPublishProfileResponse;
	/**
	 * Backups a site on-demand.
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 * @param arg3 
	 */
	backup(arg0: string, arg1: string, arg2: string, arg3: AzureBackupRequestEnvelope): AzureWebSiteBackupResponse;
	/**
	 * You can retrieve historical usage metrics for a site by issuing an HTTP
	 * GET request.  (see
	 * http://msdn.microsoft.com/en-us/library/windowsazure/dn166964.aspx for
	 * more information)
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 * @param arg3 
	 */
	getHistoricalUsageMetrics(arg0: string, arg1: string, arg2: string, arg3: AzureWebSiteGetHistoricalUsageMetricsParameters): AzureWebSiteGetHistoricalUsageMetricsResponse;
	/**
	 * You can generate a new random password for publishing a site by issuing
	 * an HTTP POST request.  Tip: If you want to verify that the publish
	 * password has changed, call HTTP GET on /publishxml before calling
	 * /newpassword. In the publish XML, note the hash value in the userPWD
	 * attribute. After calling /newpassword, call /publishxml again. You can
	 * then compare the new value of userPWD in the Publish XML with the one
	 * you noted earlier.  (see
	 * http://msdn.microsoft.com/en-us/library/windowsazure/dn236428.aspx for
	 * more information)
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 */
	generatePassword(arg0: string, arg1: string, arg2: string): AzureOperationResponse;
	/**
	 * Get a web site's current usage metrics. The metrics returned include CPU
	 * Time, Data In, Data Out, Local bytes read, Local bytes written, Network
	 * bytes read, Network bytes written, WP stop requests, Memory Usage, CPU
	 * Time - Minute Limit, and File System Storage.  (see
	 * http://msdn.microsoft.com/en-us/library/windowsazure/dn166991.aspx for
	 * more information)
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 */
	getUsageMetrics(arg0: string, arg1: string, arg2: string): AzureWebSiteGetUsageMetricsResponse;
	/**
	 * Returns list of all backups which are tracked by the system.
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 */
	listBackups(arg0: string, arg1: string, arg2: string): AzureWebSiteGetBackupsResponse;
	/**
	 * You can retrieve details for a web site by issuing an HTTP GET request.
	 * (see http://msdn.microsoft.com/en-us/library/windowsazure/dn167007.aspx
	 * for more information)
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 * @param arg3 
	 */
	get(arg0: string, arg1: string, arg2: string, arg3: AzureWebSiteGetParameters): AzureWebSiteGetResponse;
	/**
	 * List the Web Sites in a resource group.
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 */
	list(arg0: string, arg1: string, arg2: AzureWebSiteListParameters): AzureWebSiteListResponse;
	/**
	 * You can retrieve the config settings for a web site by issuing an HTTP
	 * GET request, or update them by using HTTP PUT with a request body that
	 * contains the settings to be updated.  (see
	 * http://msdn.microsoft.com/en-us/library/windowsazure/dn166985.aspx for
	 * more information)
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 * @param arg3 
	 */
	getConfiguration(arg0: string, arg1: string, arg2: string, arg3: AzureWebSiteGetConfigurationParameters): AzureWebSiteGetConfigurationResult;
	/**
	 * A web site repository is essentially a GIT repository that you can use to
	 * manage your web site content. By using GIT source control tools, you can
	 * push or pull version controlled changes to your site. You can create a
	 * repository for your web site by issuing an HTTP POST request, or
	 * retrieve information about the repository by using HTTP GET.  (see
	 * http://msdn.microsoft.com/en-us/library/windowsazure/dn166967.aspx for
	 * more information)
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 */
	deleteRepository(arg0: string, arg1: string, arg2: string): AzureWebSiteDeleteRepositoryResponse;
	/**
	 * You can retrieve the metadata for a web site by issuing an HTTP GET
	 * request, or update them by using HTTP PUT with a request body that
	 * contains the settings to be updated.  (see
	 * http://msdn.microsoft.com/en-us/library/windowsazure/dn166985.aspx for
	 * more information)
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 * @param arg3 
	 */
	updateMetadata(arg0: string, arg1: string, arg2: string, arg3: AzureWebSiteNameValueParameters): AzureWebSiteMetadataResult;
	/**
	 * Unlink source control from website
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 * @param arg3 
	 */
	deleteSiteSourceControl(arg0: string, arg1: string, arg2: string, arg3: string): AzureOperationResponse;
	/**
	 * A web site repository is essentially a GIT repository that you can use to
	 * manage your web site content. By using GIT source control tools, you can
	 * push or pull version controlled changes to your site. You can create a
	 * repository for your web site by issuing an HTTP POST request, or
	 * retrieve information about the repository by using HTTP GET.  (see
	 * http://msdn.microsoft.com/en-us/library/windowsazure/dn166967.aspx for
	 * more information)
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 */
	createRepository(arg0: string, arg1: string, arg2: string): AzureOperationResponse;
	/**
	 * You can retrieve the config settings for a web site by issuing an HTTP
	 * GET request, or update them by using HTTP PUT with a request body that
	 * contains the settings to be updated.  (see
	 * http://msdn.microsoft.com/en-us/library/windowsazure/dn166985.aspx for
	 * more information)
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 * @param arg3 
	 */
	updateConfiguration(arg0: string, arg1: string, arg2: string, arg3: AzureWebSiteUpdateConfigurationParameters): AzureOperationResponse;
	/**
	 * Updates a backup schedule for a site.
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 * @param arg3 
	 */
	updateBackupConfiguration(arg0: string, arg1: string, arg2: string, arg3: AzureBackupRequestEnvelope): AzureOperationResponse;
	/**
	 * Get publishing credentials for the web site.
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 */
	getPublishingCredentials(arg0: string, arg1: string, arg2: string): AzureWebSitePublishingCredentialsResult;
	/**
	 * You can create a web site by using a POST request that includes the name
	 * of the web site and other information in the request body.  (see
	 * http://msdn.microsoft.com/en-us/library/windowsazure/dn166986.aspx for
	 * more information)
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 * @param arg3 
	 */
	createOrUpdate(arg0: string, arg1: string, arg2: string, arg3: AzureWebSiteCreateOrUpdateParameters): AzureWebSiteCreateResponse;
	/**
	 * Link source control to website (do not forget to setup the token, and if
	 * needed token secret, for the specific source control type used).
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 * @param arg3 
	 */
	updateSiteSourceControl(arg0: string, arg1: string, arg2: string, arg3: AzureSiteSourceControlUpdateParameters): AzureSiteSourceControlUpdateResponse;
	/**
	 * You can retrieve the application settings for a web site by issuing an
	 * HTTP GET request, or update them by using HTTP PUT with a request body
	 * that contains the settings to be updated.  (see
	 * http://msdn.microsoft.com/en-us/library/windowsazure/dn166985.aspx for
	 * more information)
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 * @param arg3 
	 */
	updateAppSettings(arg0: string, arg1: string, arg2: string, arg3: AzureWebSiteNameValueParameters): AzureWebSiteAppSettingsResult;
	/**
	 * Restart the web site.
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 */
	getMetadata(arg0: string, arg1: string, arg2: string): AzureWebSiteMetadataResult;
	/**
	 * Deletes the web site.
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 * @param arg3 
	 */
	delete(arg0: string, arg1: string, arg2: string, arg3: AzureWebSiteDeleteParameters): AzureOperationResponse;
	/**
	 * Update list of app settings and connection strings which to be slot
	 * specific. E.g. settings in staging slots remain in staging after swap
	 * with production.
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 */
	updateSlotConfigNames(arg0: string, arg1: string, arg2: AzureSlotConfigNamesUpdateParameters): AzureOperationResponse;
	/**
	 * A web site repository is essentially a GIT repository that you can use to
	 * manage your web site content. By using GIT source control tools, you can
	 * push or pull version controlled changes to your site. You can create a
	 * repository for your web site by issuing an HTTP POST request, or
	 * retrieve information about the repository by using HTTP GET.  (see
	 * http://msdn.microsoft.com/en-us/library/windowsazure/dn166967.aspx for
	 * more information)
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 */
	getRepository(arg0: string, arg1: string, arg2: string): AzureWebSiteGetRepositoryResponse;
	/**
	 * Restart the web site.
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 */
	getAppSettings(arg0: string, arg1: string, arg2: string): AzureWebSiteAppSettingsResult;
	/**
	 * Restart the web site.
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 */
	stop(arg0: string, arg1: string, arg2: string): AzureOperationResponse;
	/**
	 * Restart the web site.
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 */
	getConnectionStrings(arg0: string, arg1: string, arg2: string): AzureWebSiteConnectionStringsResult;
	/**
	 * You can retrieve details for a web site by issuing an HTTP GET request.
	 * (see http://msdn.microsoft.com/en-us/library/windowsazure/dn167007.aspx
	 * for more information)
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 * @param arg3 
	 */
	getOperation(arg0: string, arg1: string, arg2: string, arg3: string): AzureWebSiteAsyncOperationResponse;
	/**
	 * Scans a backup in a storage account and returns database information etc.
	 * Should be called before calling Restore to discover what parameters are
	 * needed for the restore operation. KNOWN BUG: This has to be called
	 * against an exisingsite, otherwise will hit an error about non-existing
	 * resource.
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 * @param arg3 
	 */
	discover(arg0: string, arg1: string, arg2: string, arg3: AzureRestoreRequestEnvelope): AzureWebSiteRestoreDiscoverResponse;
	/**
	 * Update list of app settings and connection strings which to be slot
	 * specific. E.g. settings in staging slots remain in staging after swap
	 * with production.
	 * @param arg0 
	 * @param arg1 
	 */
	getSlotConfigNames(arg0: string, arg1: string): AzureSlotConfigNamesResult;
	/**
	 * You can clone a web site by using a PUT request that includes the name of
	 * the web site and other information in the request body.  (see
	 * http://msdn.microsoft.com/en-us/library/windowsazure/dn166986.aspx for
	 * more information)
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 * @param arg3 
	 */
	cloneMethod(arg0: string, arg1: string, arg2: string, arg3: AzureWebSiteCloneParameters): AzureWebSiteAsyncOperationResponse;
	/**
	 * Gets a schedule configuration for site backups.
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 */
	getBackupConfiguration(arg0: string, arg1: string, arg2: string): AzureWebSiteGetBackupConfigurationResponse;
	/**
	 * Restart the web site.
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 */
	start(arg0: string, arg1: string, arg2: string): AzureOperationResponse;
	/**
	 * You can retrieve the connection strings for a web site by issuing an HTTP
	 * GET request, or update them by using HTTP PUT with a request body that
	 * contains the settings to be updated.  (see
	 * http://msdn.microsoft.com/en-us/library/windowsazure/dn166985.aspx for
	 * more information)
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 * @param arg3 
	 */
	updateConnectionStrings(arg0: string, arg1: string, arg2: string, arg3: AzureWebSiteUpdateConnectionStringsParameters): AzureWebSiteConnectionStringsResult;
	/**
	 * Restores a site to either a new site or existing site (Overwrite flag has
	 * to be set to true for that).
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 * @param arg3 
	 */
	restore(arg0: string, arg1: string, arg2: string, arg3: AzureRestoreRequestEnvelope): AzureWebSiteRestoreResponse;
	/**
	 * Restart the web site.
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 */
	restart(arg0: string, arg1: string, arg2: string): AzureOperationResponse;
}

/**
 * Operations for managing the Web Hosting Plans in a resource group. Web
 * hosting plans (WHPs) represent a set of features and capacity that you can
 * share across your web sites. Web hosting plans support the 4 Azure Web Sites
 * pricing tiers (Free, Shared, Basic, and Standard) where each tier has its
 * own capabilities and capacity. Sites in the same subscription, resource
 * group, and geographic location can share a web hosting plan. All the sites
 * sharing a web hosting plan can leverage all the capabilities and features
 * defined by the web hosting plan tier. All web sites associated with a given
 * web hosting plan run on the resources defined by the web hosting plan.  (see
 * http://azure.microsoft.com/en-us/documentation/articles/azure-web-sites-web-hosting-plans-in-depth-overview/
 * for more information)
 */
declare interface AzureWebHostingOperations {
	/**
	 * You can retrieve historical usage metrics for a site by issuing an HTTP
	 * GET request.  (see
	 * http://msdn.microsoft.com/en-us/library/windowsazure/dn166964.aspx for
	 * more information)
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 */
	getHistoricalUsageMetrics(arg0: string, arg1: string, arg2: AzureWebHostingPlanGetHistoricalUsageMetricsParameters): AzureWebHostingPlanGetHistoricalUsageMetricsResponse;
	/**
	 * Gets all Web Hosting Plans in a current subscription and Resource Group.
	 * (see http://msdn.microsoft.com/en-us/library/windowsazure/dn194277.aspx
	 * for more information)
	 * @param arg0 
	 */
	list(arg0: string): AzureWebHostingPlanListResponse;
	/**
	 * Deletes a Web Hosting Plan  (see
	 * http://azure.microsoft.com/en-us/documentation/articles/azure-web-sites-web-hosting-plans-in-depth-overview/
	 * for more information)
	 * @param arg0 
	 * @param arg1 
	 */
	delete(arg0: string, arg1: string): AzureOperationResponse;
	/**
	 * Gets details of an existing Web Hosting Plan  (see
	 * http://azure.microsoft.com/en-us/documentation/articles/azure-web-sites-web-hosting-plans-in-depth-overview/
	 * for more information)
	 * @param arg0 
	 * @param arg1 
	 */
	get(arg0: string, arg1: string): AzureWebHostingPlanGetResponse;
	/**
	 * Creates a new Web Hosting Plan or updates an existing one.  (see
	 * http://azure.microsoft.com/en-us/documentation/articles/azure-web-sites-web-hosting-plans-in-depth-overview/
	 * for more information)
	 * @param arg0 
	 * @param arg1 
	 */
	createOrUpdate(arg0: string, arg1: AzureWebHostingPlanCreateOrUpdateParameters): AzureWebHostingPlanCreateOrUpdateResponse;
}

/**
 * User source controls operations
 */
declare interface AzureSourceControlOperations {
	/**
	 * List source controls for user.
	 */
	list(): AzureSourceControlListResponse;
	/**
	 * Get user source control
	 * @param arg0 
	 */
	get(arg0: string): AzureSourceControlGetResponse;
	/**
	 * Update source control for user.
	 * @param arg0 
	 * @param arg1 
	 */
	update(arg0: string, arg1: AzureSourceControlUpdateParameters): AzureSourceControlUpdateResponse;
}

/**
 * A CNAME record.
 */
declare class AzureCnameRecord {
	cname: string;
	constructor()
	/**
	 * Initializes a new instance of the CnameRecord class with required
	 * arguments.
	 * @param cname 
	 */
	constructor(cname: string)
	/**
	 * Initializes a new instance of the CnameRecord class.
	 */
	constructor()
}

/**
 * Represents the properties of the records in the RecordSet.
 */
declare class AzureRecordSetProperties {
	aRecords: any[];
	cnameRecord: AzureCnameRecord;
	txtRecords: any[];
	nsRecords: any[];
	mxRecords: any[];
	aaaaRecords: any[];
	srvRecords: any[];
	ttl: number;
	ptrRecords: any[];
	soaRecord: AzureSoaRecord;
	constructor()
	/**
	 * Initializes a new instance of the RecordSetProperties class with required
	 * arguments.
	 * @param ttl 
	 */
	constructor(ttl: number)
	/**
	 * Initializes a new instance of the RecordSetProperties class.
	 */
	constructor()
}

/**
 * Parameters supplied to delete a record set.
 */
declare class AzureRecordSetDeleteParameters {
	ifMatch: string;
	constructor()
	constructor()
}

/**
 * The response to a Zone Get operation.
 */
declare class AzureZoneGetResponse {
	zone: AzureZone;
	requestId: string;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * An AAAA record.
 */
declare class AzureAaaaRecord {
	ipv6Address: string;
	constructor()
	/**
	 * Initializes a new instance of the AaaaRecord class with required
	 * arguments.
	 * @param ipv6Address 
	 */
	constructor(ipv6Address: string)
	/**
	 * Initializes a new instance of the AaaaRecord class.
	 */
	constructor()
}

/**
 * Parameters supplied to delete a zone.
 */
declare class AzureZoneDeleteParameters {
	ifMatch: string;
	constructor()
	constructor()
}

/**
 * The response to a Zone List or ListAll operation.
 */
declare class AzureZoneListResponse {
	requestId: string;
	zones: any[];
	nextLink: string;
	statusCode: number;
	constructor()
	/**
	 * Initializes a new instance of the ZoneListResponse class.
	 */
	constructor()
	/**
	 * Initializes a new instance of the ZoneListResponse class with required
	 * arguments.
	 * @param zones 
	 */
	constructor(zones: AzureZone[])
}

/**
 * An A record.
 */
declare class AzureARecord {
	ipv4Address: string;
	constructor()
	/**
	 * Initializes a new instance of the ARecord class with required arguments.
	 * @param ipv4Address 
	 */
	constructor(ipv4Address: string)
	/**
	 * Initializes a new instance of the ARecord class.
	 */
	constructor()
}

/**
 * Common parameters supplied to delete operations.
 */
declare class AzureDnsResourceDeleteParametersBase {
	ifMatch: string;
	constructor()
	constructor()
}

/**
 * Describes a DNS RecordSet (a set of DNS records with the same name and type).
 */
declare class AzureRecordSet {
	name: string;
	location: string;
	eTag: string;
	id: string;
	type: string;
	properties: AzureRecordSetProperties;
	tags: Properties;
	constructor()
	/**
	 * Initializes a new instance of the RecordSet class with required arguments.
	 * @param location 
	 */
	constructor(location: string)
	/**
	 * Initializes a new instance of the RecordSet class.
	 */
	constructor()
}

/**
 * An SOA record.
 */
declare class AzureSoaRecord {
	serialNumber: number;
	expireTime: number;
	minimumTtl: number;
	refreshTime: number;
	host: string;
	retryTime: number;
	email: string;
	constructor()
	constructor()
}

/**
 * A PTR record.
 */
declare class AzurePtrRecord {
	ptrdname: string;
	constructor()
	/**
	 * Initializes a new instance of the PtrRecord class with required arguments.
	 * @param ptrdname 
	 */
	constructor(ptrdname: string)
	/**
	 * Initializes a new instance of the PtrRecord class.
	 */
	constructor()
}

/**
 * The response to a Zone CreateOrUpdate operation.
 */
declare class AzureZoneCreateOrUpdateResponse {
	zone: AzureZone;
	requestId: string;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * Parameters supplied to create or update a RecordSet.
 */
declare class AzureRecordSetCreateOrUpdateParameters {
	recordSet: AzureRecordSet;
	ifNoneMatch: string;
	constructor()
	/**
	 * Initializes a new instance of the RecordSetCreateOrUpdateParameters class.
	 */
	constructor()
	/**
	 * Initializes a new instance of the RecordSetCreateOrUpdateParameters class
	 * with required arguments.
	 * @param recordSet 
	 */
	constructor(recordSet: AzureRecordSet)
}

/**
 * An MX record.
 */
declare class AzureMxRecord {
	preference: number;
	exchange: string;
	constructor()
	/**
	 * Initializes a new instance of the MxRecord class.
	 */
	constructor()
	/**
	 * Initializes a new instance of the MxRecord class with required arguments.
	 * @param preference 
	 * @param exchange 
	 */
	constructor(preference: number, exchange: string)
}

/**
 * Parameters supplied to create a zone.
 */
declare class AzureZoneCreateOrUpdateParameters {
	zone: AzureZone;
	ifNoneMatch: string;
	constructor()
	/**
	 * Initializes a new instance of the ZoneCreateOrUpdateParameters class.
	 */
	constructor()
	/**
	 * Initializes a new instance of the ZoneCreateOrUpdateParameters class with
	 * required arguments.
	 * @param zone 
	 */
	constructor(zone: AzureZone)
}

/**
 * An SRV record.
 */
declare class AzureSrvRecord {
	port: number;
	weight: number;
	priority: number;
	target: string;
	constructor()
	constructor()
}

/**
 * The response to a RecordSet Get operation.
 */
declare class AzureRecordSetGetResponse {
	requestId: string;
	recordSet: AzureRecordSet;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * The response to a RecordSet List operation.
 */
declare class AzureRecordSetListResponse {
	requestId: string;
	nextLink: string;
	recordSets: any[];
	statusCode: number;
	constructor()
	/**
	 * Initializes a new instance of the RecordSetListResponse class.
	 */
	constructor()
	/**
	 * Initializes a new instance of the RecordSetListResponse class with
	 * required arguments.
	 * @param recordSets 
	 */
	constructor(recordSets: AzureRecordSet[])
}

declare class AzureRecordSetListParameters {
	filter: string;
	top: string;
	constructor()
	constructor()
}

/**
 * A TXT record.
 */
declare class AzureTxtRecord {
	value: string;
	constructor()
	/**
	 * Initializes a new instance of the TxtRecord class with required arguments.
	 * @param value 
	 */
	constructor(value: string)
	/**
	 * Initializes a new instance of the TxtRecord class.
	 */
	constructor()
}

/**
 * The response to a RecordSet CreateOrUpdate operation.
 */
declare class AzureRecordSetCreateOrUpdateResponse {
	requestId: string;
	recordSet: AzureRecordSet;
	statusCode: number;
	constructor()
	constructor()
}

/**
 * Represents the properties of the zone.
 */
declare class AzureZoneProperties {
	numberOfRecordSets: number;
	maxNumberOfRecordSets: number;
	constructor()
	constructor()
}

/**
 * Zone list operation parameters.
 */
declare class AzureZoneListParameters {
	filter: string;
	top: string;
	constructor()
	constructor()
}

/**
 * An NS record.
 */
declare class AzureNsRecord {
	nsdname: string;
	constructor()
	/**
	 * Initializes a new instance of the NsRecord class with required arguments.
	 * @param nsdname 
	 */
	constructor(nsdname: string)
	/**
	 * Initializes a new instance of the NsRecord class.
	 */
	constructor()
}

/**
 * Describes a DNS zone.
 */
declare class AzureZone {
	name: string;
	location: string;
	id: string;
	type: string;
	properties: AzureZoneProperties;
	tags: Properties;
	constructor()
	/**
	 * Initializes a new instance of the Zone class with required arguments.
	 * @param location 
	 */
	constructor(location: string)
	/**
	 * Initializes a new instance of the Zone class.
	 */
	constructor()
}

/**
 * Client for managing DNS zones and record.
 */
declare interface AzureDnsManagementClient {
	readonly recordSetsOperations: AzureRecordSetOperations;
	readonly apiVersion: string;
	longRunningOperationRetryTimeout: number;
	longRunningOperationInitialTimeout: number;
	readonly baseUri: string;
	readonly zonesOperations: AzureZoneOperations;
	/**
	 * Closes this stream and releases any system resources associated
	 * with it. If the stream is already closed then invoking this
	 * method has no effect.
	 * 
	 * <p> As noted in {@link AutoCloseable#close()}, cases where the
	 * close may fail require careful attention. It is strongly advised
	 * to relinquish the underlying resources and to internally
	 * <em>mark</em> the {@code Closeable} as closed, prior to throwing
	 * the {@code IOException}.
	 */
	close(): void;
}

/**
 * Operations for managing the RecordSets in a DNS zone.
 */
declare interface AzureRecordSetOperations {
	/**
	 * Lists all RecordSets in a DNS zone.
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 */
	listAll(arg0: string, arg1: string, arg2: AzureRecordSetListParameters): AzureRecordSetListResponse;
	/**
	 * Lists RecordSets in a DNS zone. Depending on the previous call, it will
	 * list all types or by type.
	 * @param arg0 
	 */
	listNext(arg0: string): AzureRecordSetListResponse;
	/**
	 * Gets a RecordSet.
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 * @param arg3 
	 */
	get(arg0: string, arg1: string, arg2: string, arg3: AzureRecordType): AzureRecordSetGetResponse;
	/**
	 * Removes a RecordSet from a DNS zone.
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 * @param arg3 
	 * @param arg4 
	 */
	delete(arg0: string, arg1: string, arg2: string, arg3: AzureRecordType, arg4: AzureRecordSetDeleteParameters): AzureOperationResponse;
	/**
	 * Creates a RecordSet within a DNS zone.
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 * @param arg3 
	 * @param arg4 
	 */
	createOrUpdate(arg0: string, arg1: string, arg2: string, arg3: AzureRecordType, arg4: AzureRecordSetCreateOrUpdateParameters): AzureRecordSetCreateOrUpdateResponse;
	/**
	 * Lists the RecordSets of a specified type in a DNS zone.
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 * @param arg3 
	 */
	list(arg0: string, arg1: string, arg2: AzureRecordType, arg3: AzureRecordSetListParameters): AzureRecordSetListResponse;
}

/**
 * Operations for managing DNS zones.
 */
declare interface AzureZoneOperations {
	/**
	 * Lists the DNS zones within a resource group.
	 * @param arg0 
	 */
	listNext(arg0: string): AzureZoneListResponse;
	/**
	 * Lists the DNS zones within a resource group.
	 * @param arg0 
	 * @param arg1 
	 */
	list(arg0: string, arg1: AzureZoneListParameters): AzureZoneListResponse;
	/**
	 * Creates a DNS zone within a resource group.
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 */
	createOrUpdate(arg0: string, arg1: string, arg2: AzureZoneCreateOrUpdateParameters): AzureZoneCreateOrUpdateResponse;
	/**
	 * Gets a DNS zone.
	 * @param arg0 
	 * @param arg1 
	 */
	get(arg0: string, arg1: string): AzureZoneGetResponse;
	/**
	 * Removes a DNS zone from a resource group.
	 * @param arg0 
	 * @param arg1 
	 * @param arg2 
	 */
	delete(arg0: string, arg1: string, arg2: AzureZoneDeleteParameters): AzureOperationResponse;
}

declare interface AzureConnection {
	readonly resourceGroups: AzureResourceGroupExtended[];
	readonly genericClient: AzureGenericClient;
	readonly clientId: string;
	readonly proxyUsername: string;
	readonly displayName: string;
	readonly storageAccounts: AzureStorageAccount[];
	readonly databaseClient: AzureSqlManagementClient;
	readonly websiteClient: AzureWebSiteManagementClient;
	readonly storageClient: AzureStorageManagementClient;
	readonly ownerId: string;
	readonly proxyHost: string;
	readonly resourceClient: AzureResourceManagementClient;
	readonly proxyPort: number;
	readonly dnsManagementClient: AzureDnsManagementClient;
	readonly computeClient: AzureComputeManagementClient;
	readonly networkClient: AzureNetworkResourceProviderClient;
	readonly loginUrl: string;
	readonly name: string;
	readonly tenantId: string;
	readonly locations: any[];
	readonly subscriptionId: string;
	readonly managementUri: string;
	readonly virtualNetworks: AzureVirtualNetwork[];
	/**
	 * @param locationName 
	 */
	getLocationByName(locationName: string): AzureLocation;
	/**
	 * Destroys the connection
	 */
	destroy(): void;
	/**
	 * @param resourceGroupName 
	 */
	getResourceGroupByName(resourceGroupName: string): AzureResourceGroupExtended;
	validate(): void;
	afterPropertiesSet(): void;
	/**
	 * @param props 
	 */
	update(props: any): void;
	acquireNewToken(): void;
}

declare class AzureLocation {
	readonly virtualMachineSizes: AzureVirtualMachineSize[];
	readonly displayName: string;
	readonly name: string;
	readonly virtualMachineImagePublishers: AzureVirtualMachineImagePublisher[];
	constructor()
	/**
	 * @param name 
	 * @param displayName 
	 */
	constructor(name: string, displayName: string)
	/**
	 * Gets a virtual machine image publisher for that location by name.
	 * @param publisherName 
	 */
	getVirtualMachineImagePublisherByName(publisherName: string): AzureVirtualMachineImagePublisher;
	/**
	 * Gets a virtual machine size for that location by name.
	 * @param sizeName 
	 */
	getVirtualMachineSizeByName(sizeName: string): AzureVirtualMachineSize;
}
