declare module com.vmware.vcac.platform.content.evaluators {
	type Evaluator = any;
}

declare module com.vmware.vcac.platform.content.criteria {
	type Clause = any;
}

declare module com.vmware.vcac.platform.designer.data.layout {
	type DesignerLayout = any;
}

declare module com.vmware.vcac.platform.content.schema {
	type ObjectDefinitionReference = any;
	type Field = any;
}

declare module com.vmware.vcac.platform.content.literals {
	type Literal = any;
	type MonetaryValue = any;
}

declare module com.vmware.vcac.platform.content.common.dates {
	type TimeUnit = any;
}

declare module com.vmware.vcac.platform.content.schema.definitions {
	type FieldInsinuation = any;
}

declare module com.vmware.vcac.platform.content.operators {
	type CriteriaOperator = any;
}

declare module com.vmware.vcac.platform.rest.data {
	type RestErrorFields = any;
}

declare type vCACCAFEDataObject = vCACCAFEBooleanLiteral | vCACCAFEComplexLiteral | vCACCAFEDateTimeLiteral |
	vCACCAFEDecimalLiteral | vCACCAFEEntityReference | vCACCAFEEntityReferenceEx | vCACCAFEIntegerLiteral |
	vCACCAFEMoney | vCACCAFEMoneyTimeRate | vCACCAFEMultipleLiteral | vCACCAFESecureStringLiteral | vCACCAFEStringLiteral;

declare type vCACCAFEDataObjectTypeId = "string" | "integer" | "decimal" | "dateTime" | "boolean" | "entityReference" | "complex";

/**
 * Composite blueprint helper with methods to manage composite blueprints.
 */
declare class vCACCAFECompositeBlueprintHelper {
	/**
	 * Imports a composite blueprint from the given yaml file. Returns the ID of the imported blueprint
	 * @param host 
	 * @param mimeAttachment 
	 */
	static importBlueprint(host: vCACCAFEHost, mimeAttachment: any): string;
}

/**
 * Event broker service helper with methods to manage subscriptions and events.
 */
declare class vCACCAFEEBSHelper {
	/**
	 * Sets criteria to a workflow subscription.
	 * @param subscription 
	 * @param criteria 
	 */
	static setCriteriaToWorkflowSubscription(subscription: vCACCAFEWorkflowSubscription, criteria: string): void;
	/**
	 * Gets criteria from a workflow subscription as json string.
	 * @param subscription 
	 */
	static getCriteriaFromWorkflowSubscription(subscription: vCACCAFEWorkflowSubscription): string;
}

/**
 * Endpoint configuration service helper methods to create endpoint configuration objects.
 */
declare class vCACCAFEEndpointConfigurationHelper {
	/**
	 * Adds a custom property to the endpoint.
	 * @param endpoint 
	 * @param propertyName 
	 * @param propertyValue 
	 * @param isRuntime 
	 * @param isHidden 
	 * @param isEncrypted 
	 */
	static addCustomProperty(endpoint: vCACCAFEEndpoint, propertyName: string, propertyValue: string, isRuntime: boolean, isHidden: boolean, isEncrypted: boolean): vCACCAFEEndpoint;
	/**
	 * Creates an endpoint object from given parameters.
	 * @param name 
	 * @param description 
	 * @param address 
	 * @param username 
	 * @param password 
	 * @param endpointType 
	 */
	static createEndpointObject(name: string, description: string, address: string, username: string, password: string, endpointType: string): vCACCAFEEndpoint;
	/**
	 * Creates an infrastructure endpoint object from given parameters.
	 * @param name 
	 * @param description 
	 * @param address 
	 * @param username 
	 * @param password 
	 * @param integratedAuth 
	 * @param endpointType 
	 * @param vcoPriority 
	 * @param organization 
	 * @param openstackProject 
	 * @param thumbprint 
	 * @param trustAllCertificates 
	 */
	static createInfrastructureEndpointObject(name: string, description: string, address: string, username: string, password: string, integratedAuth: boolean, endpointType: string, vcoPriority: number, organization: string, openstackProject: string, thumbprint: string, trustAllCertificates: boolean): vCACCAFEEndpoint;
	/**
	 * Creates a proxy endpoint object from given parameters.
	 * @param host 
	 * @param name 
	 * @param description 
	 * @param address 
	 * @param port 
	 * @param username 
	 * @param password 
	 */
	static createProxyEndpoint(host: vCACCAFEHost, name: string, description: string, address: string, port: number, username: string, password: string): vCACCAFEEndpoint;
	/**
	 * Creates an NSX endpoint object from given parameters.
	 * @param host 
	 * @param name 
	 * @param description 
	 * @param address 
	 * @param username 
	 * @param password 
	 * @param thumbprint 
	 * @param trustAllCertificates 
	 * @param vSphereEndpoint 
	 */
	static createNsxEndpoint(host: vCACCAFEHost, name: string, description: string, address: string, username: string, password: string, thumbprint: string, trustAllCertificates: boolean, vSphereEndpoint: vCACCAFEEndpoint): vCACCAFEEndpoint;
	/**
	 * Gets an association type object for given endpoint type and association name.
	 * @param host 
	 * @param fromEndpointType 
	 * @param toEndpointType 
	 */
	static getAssociationTypeId(host: vCACCAFEHost, fromEndpointType: string, toEndpointType: string): vCACCAFEAssociationType;
	/**
	 * Associate two endpoints with a given association type id. Only object is created not saved.
	 * @param fromEndpoint 
	 * @param toEndpoint 
	 * @param associationTypeId 
	 */
	static associateEndpointObjects(fromEndpoint: vCACCAFEEndpoint, toEndpoint: vCACCAFEEndpoint, associationTypeId: string): vCACCAFEEndpoint;
}

/**
 * Entities finder that allow executing queries tenant, catalog items and so on.
 */
declare class vCACCAFEEntitiesFinder {
	/**
	 * Gets a host.
	 * @param hostId 
	 */
	static getHost(hostId: string): vCACCAFEHost;
	/**
	 * Gets am identity store from a vRA host tenant.
	 * @param host 
	 * @param identityStoreId 
	 */
	static getIdentityStoreFromHost(host: vCACCAFEHost, identityStoreId: string): vCACCAFEIdentityStore;
	/**
	 * Gets all  identity stores for a vRA host tenant.
	 * @param host 
	 */
	static getIdentityStoresFromHost(host: vCACCAFEHost): vCACCAFEIdentityStore[];
	/**
	 * Gets all identity stores for a vRA host tenant matching the query by name and/or domain.
	 * @param host 
	 * @param query 
	 */
	static findIdentityStoresFromHost(host: vCACCAFEHost, query: string): vCACCAFEIdentityStore[];
	/**
	 * Gets a business group from a host.
	 * @param host 
	 * @param businessGroupId 
	 */
	static getBusinessGroup(host: vCACCAFEHost, businessGroupId: string): vCACCAFEBusinessGroup;
	/**
	 * Invalidates an entity from the plug-in inventory cache, if needed.
	 * @param entity 
	 */
	static invalidateEntity(entity: any): void;
	/**
	 * Gets all custom resources and resource mappings for a host matching the query by name and/or description.
	 * @param host 
	 * @param query 
	 */
	static findCustomResourcesAndMappings(host: vCACCAFEHost, query: string): vCACCAFECsResourceType[];
	/**
	 * Gets all custom resources for a host matching the query by name and/or description.
	 * @param host 
	 * @param query 
	 */
	static findCustomResources(host: vCACCAFEHost, query: string): vCACCAFECsResourceType[];
	/**
	 * Gets a custom resource from a host.
	 * @param host 
	 * @param resourceTypeId 
	 */
	static getCustomResource(host: vCACCAFEHost, resourceTypeId: string): vCACCAFECsResourceType;
	/**
	 * Gets all resource mappings for a host.
	 * @param host 
	 */
	static getResourceMappings(host: vCACCAFEHost): any;
	/**
	 * Gets all resource mappings for a host matching the query by name and/or description.
	 * @param host 
	 * @param query 
	 */
	static findResourceMappings(host: vCACCAFEHost, query: string): vCACCAFECsResourceType[];
	/**
	 * Gets a resource mapping from a host.
	 * @param host 
	 * @param resourceMappingId 
	 */
	static getResourceMapping(host: vCACCAFEHost, resourceMappingId: string): vCACCAFECsResourceType;
	/**
	 * Gets all ASD service blueprints for a host.
	 * @param host 
	 */
	static getServiceBlueprints(host: vCACCAFEHost): vCACCAFEServiceBlueprint[];
	/**
	 * Gets all ASD service blueprints for a host matching the query by name and/or description.
	 * @param host 
	 * @param query 
	 */
	static findServiceBlueprints(host: vCACCAFEHost, query: string): vCACCAFEServiceBlueprint[];
	/**
	 * Gets an ASD service blueprint from a host.
	 * @param host 
	 * @param id 
	 */
	static getServiceBlueprint(host: vCACCAFEHost, id: string): vCACCAFEServiceBlueprint;
	/**
	 * Gets all admin catalog items for a host.
	 * @param host 
	 */
	static getAdminCatalogItems(host: vCACCAFEHost): vCACCAFEAdminCatalogItem[];
	/**
	 * Gets all admin catalog items for a host matching the query by name and/or description.
	 * @param host 
	 * @param query 
	 */
	static findAdminCatalogItems(host: vCACCAFEHost, query: string): vCACCAFEAdminCatalogItem[];
	/**
	 * Gets an admin catalog items from a host.
	 * @param host 
	 * @param catalogItemId 
	 */
	static getAdminCatalogItem(host: vCACCAFEHost, catalogItemId: string): vCACCAFEAdminCatalogItem;
	/**
	 * Gets all entitlements for a host.
	 * @param host 
	 */
	static getEntitlements(host: vCACCAFEHost): vCACCAFEEntitlement[];
	/**
	 * Gets all entitlements for a host matching the query by name and/or description.
	 * @param host 
	 * @param query 
	 */
	static findEntitlements(host: vCACCAFEHost, query: string): vCACCAFEEntitlement[];
	/**
	 * Gets an entitlement from a host.
	 * @param host 
	 * @param entitlementId 
	 */
	static getEntitlement(host: vCACCAFEHost, entitlementId: string): vCACCAFEEntitlement;
	/**
	 * Gets all EBS event topics for a host.
	 * @param host 
	 */
	static getEventTopics(host: vCACCAFEHost): vCACCAFEEventTopic[];
	/**
	 * Gets all EBS event topics for a host matching the query by name and/or description.
	 * @param host 
	 * @param query 
	 */
	static findEventTopics(host: vCACCAFEHost, query: string): vCACCAFEEventTopic[];
	/**
	 * Gets an EBS event topic from a host.
	 * @param host 
	 * @param eventTopicId 
	 */
	static getEventTopic(host: vCACCAFEHost, eventTopicId: string): vCACCAFEEventTopic;
	/**
	 * Invalidates all the plug-in inventory cache.
	 */
	static invalidateAll(): void;
	/**
	 * Gets a host an entity belongs to.
	 * @param entity 
	 */
	static getHostForEntity(entity: any): vCACCAFEHost;
	/**
	 * Gets all services for a host matching the query by name and/or description.
	 * @param host 
	 * @param query 
	 */
	static findServices(host: vCACCAFEHost, query: string): vCACCAFEService[];
	/**
	 * Gets all resource operations for a host.
	 * @param host 
	 */
	static getAdminCatalogResourceOperations(host: vCACCAFEHost): any[];
	/**
	 * Gets all resource operations for a host matching the query by name and/or description.
	 * @param host 
	 * @param query 
	 */
	static findAdminCatalogResourceOperations(host: vCACCAFEHost, query: string): any[];
	/**
	 * Gets all resource actions for a host.
	 * @param host 
	 */
	static getAdminCatalogResourceActions(host: vCACCAFEHost): vCACCAFEResourceAction[];
	/**
	 * Gets all resource actions for a host matching the query by name and/or description.
	 * @param host 
	 * @param query 
	 */
	static findAdminCatalogResourceActions(host: vCACCAFEHost, query: string): vCACCAFEResourceAction[];
	/**
	 * Gets a resource action from a host.
	 * @param host 
	 * @param resourceActionId 
	 */
	static getAdminCatalogResourceAction(host: vCACCAFEHost, resourceActionId: string): vCACCAFEResourceAction;
	/**
	 * Gets all resource extensions for a host.
	 * @param host 
	 */
	static getAdminCatalogResourceExtensions(host: vCACCAFEHost): vCACCAFEResourceExtension[];
	/**
	 * Gets all resource extensions for a host matching the query by name and/or description.
	 * @param host 
	 * @param query 
	 */
	static findAdminCatalogResourceExtensions(host: vCACCAFEHost, query: string): vCACCAFEResourceExtension[];
	/**
	 * Gets a resource extension from a host.
	 * @param host 
	 * @param resourceExtensionId 
	 */
	static getAdminCatalogResourceExtension(host: vCACCAFEHost, resourceExtensionId: string): vCACCAFEResourceExtension;
	/**
	 * Gets all custom resources for a host.
	 * @param host 
	 */
	static getCustomResources(host: vCACCAFEHost): any;
	/**
	 * Gets all business groups for a host.
	 * @param host 
	 */
	static getBusinessGroups(host: vCACCAFEHost): vCACCAFEBusinessGroup[];
	/**
	 * Gets all business groups for a host matching the query by name and/or description.
	 * @param host 
	 * @param query 
	 */
	static findBusinessGroups(host: vCACCAFEHost, query: string): vCACCAFEBusinessGroup[];
	/**
	 * Gets a business group from a host.
	 * @param host 
	 * @param subtenantId 
	 */
	static getSubtenant(host: vCACCAFEHost, subtenantId: string): vCACCAFESubtenant;
	/**
	 * Gets all business groups for a host.
	 * @param host 
	 */
	static getSubtenants(host: vCACCAFEHost): vCACCAFESubtenant[];
	/**
	 * Gets all business groups for a host matching the query by name and/or description.
	 * @param host 
	 * @param query 
	 */
	static findSubtenants(host: vCACCAFEHost, query: string): vCACCAFESubtenant[];
	/**
	 * Gets all custom properties from a business group.
	 * @param group 
	 */
	static getCustomProperties(group: vCACCAFEBusinessGroup): vCACCAFECustomProperty[];
	/**
	 * Gets all custom properties for a catalog resource matching the query by name.
	 * @param group 
	 * @param query 
	 */
	static findCustomProperties(group: vCACCAFEBusinessGroup, query: string): vCACCAFECustomProperty[];
	/**
	 * Gets a catalog item from a host.
	 * @param host 
	 * @param catalogItemId 
	 */
	static getCatalogItem(host: vCACCAFEHost, catalogItemId: string): vCACCAFECatalogItem;
	/**
	 * Gets a resource action request from a host.
	 * @param host 
	 * @param requestId 
	 */
	static getResourceActionRequest(host: vCACCAFEHost, requestId: string): vCACCAFEResourceActionRequest;
	/**
	 * Gets all resource action requests for a host.
	 * @param host 
	 */
	static getResourceActionRequests(host: vCACCAFEHost): vCACCAFEResourceActionRequest[];
	/**
	 * Gets all resource action requests for a host matching the query by name and/or description.
	 * @param host 
	 * @param query 
	 */
	static findResourceActionRequests(host: vCACCAFEHost, query: string): vCACCAFEResourceActionRequest[];
	/**
	 * Gets a work item from a host.
	 * @param host 
	 * @param workItemId 
	 */
	static getWorkItem(host: vCACCAFEHost, workItemId: string): vCACCAFEWorkItem;
	/**
	 * Gets all work items for a host.
	 * @param host 
	 */
	static getWorkItems(host: vCACCAFEHost): vCACCAFEWorkItem[];
	/**
	 * Gets all work items for a host matching the query by name and/or description.
	 * @param host 
	 * @param query 
	 */
	static findWorkItems(host: vCACCAFEHost, query: string): vCACCAFEWorkItem[];
	/**
	 * Gets a work item action from a work item.
	 * @param item 
	 * @param actionId 
	 */
	static getWorkItemAction(item: vCACCAFEWorkItem, actionId: string): vCACCAFEWorkItemAction;
	/**
	 * Gets all  identity stores for a tenant.
	 * @param tenant 
	 */
	static getIdentityStores(tenant: vCACCAFETenant): vCACCAFEIdentityStore[];
	/**
	 * Gets all identity stores for a tenant matching the query by name and/or domain.
	 * @param tenant 
	 * @param query 
	 */
	static findIdentityStores(tenant: vCACCAFETenant, query: string): vCACCAFEIdentityStore[];
	/**
	 * Gets all work item actions for a work item.
	 * @param item 
	 */
	static getWorkItemActions(item: vCACCAFEWorkItem): vCACCAFEWorkItemAction[];
	/**
	 * Gets all work item actions for a work item matching the query by name.
	 * @param item 
	 * @param query 
	 */
	static findWorkItemActions(item: vCACCAFEWorkItem, query: string): vCACCAFEWorkItemAction[];
	/**
	 * Gets a catalog resource from a host.
	 * @param host 
	 * @param catalogResourceId 
	 */
	static getCatalogResource(host: vCACCAFEHost, catalogResourceId: string): vCACCAFECatalogResource;
	/**
	 * Gets all catalog resources for a host.
	 * @param host 
	 */
	static getCatalogResources(host: vCACCAFEHost): vCACCAFECatalogResource[];
	/**
	 * Gets all catalog resources for a host matching the query by name and/or description.
	 * @param host 
	 * @param query 
	 */
	static findCatalogResources(host: vCACCAFEHost, query: string): vCACCAFECatalogResource[];
	/**
	 * Gets a catalog resource actions from a catalog resource.
	 * @param resource 
	 * @param resourceOperationId 
	 */
	static getCatalogResourceAction(resource: vCACCAFECatalogResource, resourceOperationId: string): vCACCAFEConsumerResourceOperation;
	/**
	 * Gets all tenants for a host matching the query by name and/or description.
	 * @param host 
	 * @param query 
	 */
	static findTenants(host: vCACCAFEHost, query: string): vCACCAFETenant[];
	/**
	 * Gets am identity store from a tenant.
	 * @param tenant 
	 * @param identityStoreId 
	 */
	static getIdentityStore(tenant: vCACCAFETenant, identityStoreId: string): vCACCAFEIdentityStore;
	/**
	 * Gets a catalog item request from a host.
	 * @param host 
	 * @param requestId 
	 */
	static getCatalogItemRequest(host: vCACCAFEHost, requestId: string): vCACCAFECatalogItemRequest;
	/**
	 * Gets all catalog item requests for a host.
	 * @param host 
	 */
	static getCatalogItemRequests(host: vCACCAFEHost): vCACCAFECatalogItemRequest[];
	/**
	 * Gets all catalog item requests for a host matching the query by name and/or description.
	 * @param host 
	 * @param query 
	 */
	static findCatalogItemRequests(host: vCACCAFEHost, query: string): vCACCAFECatalogItemRequest[];
	/**
	 * Gets all catalog item requests for a host matching the query by name and/or description.
	 * @param host 
	 * @param query 
	 */
	static findRequests(host: vCACCAFEHost, query: string): any[];
	/**
	 * Gets a machine prefix from a host.
	 * @param host 
	 * @param machinePrefixId 
	 */
	static getMachinePrefix(host: vCACCAFEHost, machinePrefixId: string): vCACCAFEMachinePrefix;
	/**
	 * Gets all machine prefixes for a host.
	 * @param host 
	 */
	static getMachinePrefixes(host: vCACCAFEHost): vCACCAFEMachinePrefix[];
	/**
	 * Gets all machine prefixes for a host matching the query by prefix.
	 * @param host 
	 * @param query 
	 */
	static findMachinePrefixes(host: vCACCAFEHost, query: string): vCACCAFEMachinePrefix[];
	/**
	 * Gets a custom property from a business group.
	 * @param group 
	 * @param customPropertyId 
	 */
	static getCustomProperty(group: vCACCAFEBusinessGroup, customPropertyId: string): vCACCAFECustomProperty;
	/**
	 * Gets all catalog items for a host.
	 * @param host 
	 */
	static getCatalogItems(host: vCACCAFEHost): vCACCAFECatalogItem[];
	/**
	 * Gets all catalog items for a host matching the query by name and/or description.
	 * @param host 
	 * @param query 
	 */
	static findCatalogItems(host: vCACCAFEHost, query: string): vCACCAFECatalogItem[];
	/**
	 * Gets all catalog resource types for a host.
	 * @param host 
	 */
	static getCatalogResourceTypes(host: vCACCAFEHost): vCACCAFEResourceType[];
	/**
	 * Gets all catalog resource types for a host matching the query by name and/or description.
	 * @param host 
	 * @param query 
	 */
	static findCatalogResourceTypes(host: vCACCAFEHost, query: string): vCACCAFEResourceType[];
	/**
	 * Gets a tenant from a host.
	 * @param host 
	 * @param tenantId 
	 */
	static getTenant(host: vCACCAFEHost, tenantId: string): vCACCAFETenant;
	/**
	 * Gets all tenants for a host.
	 * @param host 
	 */
	static getTenants(host: vCACCAFEHost): vCACCAFETenant[];
	/**
	 * Gets all system workflow subscriptions for a host.
	 * @param host 
	 */
	static getSystemWorkflowSubscriptions(host: vCACCAFEHost): vCACCAFEWorkflowSubscription[];
	/**
	 * Gets all system workflow subscriptions for a host matching the query by name and/or description.
	 * @param host 
	 * @param query 
	 */
	static findSystemWorkflowSubscriptions(host: vCACCAFEHost, query: string): vCACCAFEWorkflowSubscription[];
	/**
	 * Gets a system workflow subscription from a host.
	 * @param host 
	 * @param subscriptionId 
	 */
	static getSystemWorkflowSubscription(host: vCACCAFEHost, subscriptionId: string): vCACCAFEWorkflowSubscription;
	/**
	 * Gets all tenant workflow subscriptions for a host.
	 * @param host 
	 */
	static getTenantWorkflowSubscriptions(host: vCACCAFEHost): vCACCAFEWorkflowSubscription[];
	/**
	 * Gets all tenant workflow subscriptions for a host matching the query by name and/or description.
	 * @param host 
	 * @param query 
	 */
	static findTenantWorkflowSubscriptions(host: vCACCAFEHost, query: string): vCACCAFEWorkflowSubscription[];
	/**
	 * Gets a tenant workflow subscription from a host.
	 * @param host 
	 * @param subscriptionId 
	 */
	static getTenantWorkflowSubscription(host: vCACCAFEHost, subscriptionId: string): vCACCAFEWorkflowSubscription;
	/**
	 * Gets all composite blueprints for a host.
	 * @param host 
	 */
	static getCompositeBlueprints(host: vCACCAFEHost): vCACCAFECompositeBlueprint[];
	/**
	 * Gets all composite blueprints for a host matching the query by name and/or description.
	 * @param host 
	 * @param query 
	 */
	static findCompositeBlueprints(host: vCACCAFEHost, query: string): vCACCAFECompositeBlueprint[];
	/**
	 * Gets a composite blueprint from a host.
	 * @param host 
	 * @param blueprintId 
	 */
	static getCompositeBlueprint(host: vCACCAFEHost, blueprintId: string): vCACCAFECompositeBlueprint;
	/**
	 * Gets all reservations for a host.
	 * @param host 
	 */
	static getReservations(host: vCACCAFEHost): vCACCAFEReservation[];
	/**
	 * Gets all reservations for a host matching the query by name and/or description.
	 * @param host 
	 * @param query 
	 */
	static findReservations(host: vCACCAFEHost, query: string): vCACCAFEReservation[];
	/**
	 * Gets a reservation from a host.
	 * @param host 
	 * @param reservationId 
	 */
	static getReservation(host: vCACCAFEHost, reservationId: string): vCACCAFEReservation;
	/**
	 * Gets all reservation policies for a host.
	 * @param host 
	 */
	static getReservationPolicies(host: vCACCAFEHost): vCACCAFEReservationPolicy[];
	/**
	 * Gets all reservation policies for a host matching the query by name and/or description.
	 * @param host 
	 * @param query 
	 */
	static findReservationPolicies(host: vCACCAFEHost, query: string): vCACCAFEReservationPolicy[];
	/**
	 * Gets a reservation policy from a host.
	 * @param host 
	 * @param reservationPolicyId 
	 */
	static getReservationPolicy(host: vCACCAFEHost, reservationPolicyId: string): vCACCAFEReservationPolicy;
	/**
	 * Gets all property definitions for a host.
	 * @param host 
	 */
	static getPropertyDefinitions(host: vCACCAFEHost): vCACCAFEContextPropertyDefinition[];
	/**
	 * Gets all property definitions for a host matching the query by name and/or description.
	 * @param host 
	 * @param query 
	 */
	static findPropertyDefinitions(host: vCACCAFEHost, query: string): vCACCAFEContextPropertyDefinition[];
	/**
	 * Gets all ASD resource actions for a host.
	 * @param host 
	 */
	static getResourceActions(host: vCACCAFEHost): vCACCAFECsResourceOperation[];
	/**
	 * Gets all resource actions for a host matching the query by name and/or description.
	 * @param host 
	 * @param query 
	 */
	static findResourceActions(host: vCACCAFEHost, query: string): vCACCAFECsResourceOperation[];
	/**
	 * Gets a resource action from a host.
	 * @param host 
	 * @param id 
	 */
	static getResourceAction(host: vCACCAFEHost, id: string): vCACCAFECsResourceOperation;
	/**
	 * Gets all catalog resource actions from a catalog resource.
	 * @param resource 
	 */
	static getCatalogResourceActions(resource: vCACCAFECatalogResource): vCACCAFEConsumerResourceOperation[];
	/**
	 * Gets all catalog resource actions for a catalog resource matching the query by name and/or description.
	 * @param resource 
	 * @param query 
	 */
	static findCatalogResourceActions(resource: vCACCAFECatalogResource, query: string): vCACCAFEConsumerResourceOperation[];
	/**
	 * Gets an approval policy from a host.
	 * @param host 
	 * @param policyId 
	 */
	static getApprovalPolicy(host: vCACCAFEHost, policyId: string): vCACCAFEApprovalPolicy;
	/**
	 * Gets all approval policies for a host.
	 * @param host 
	 */
	static getApprovalPolicies(host: vCACCAFEHost): vCACCAFEApprovalPolicy[];
	/**
	 * Gets all approval policies for a host matching the query by name and/or description.
	 * @param host 
	 * @param query 
	 */
	static findApprovalPolicies(host: vCACCAFEHost, query: string): vCACCAFEApprovalPolicy[];
	/**
	 * Gets a property definition from a host.
	 * @param host 
	 * @param propertyDefinitionId 
	 */
	static getPropertyDefinition(host: vCACCAFEHost, propertyDefinitionId: string): vCACCAFEContextPropertyDefinition;
	/**
	 * Gets all property groups for a host.
	 * @param host 
	 */
	static getPropertyGroups(host: vCACCAFEHost): vCACCAFEContextPropertyGroup[];
	/**
	 * Gets all property groups for a host matching the query by name and/or description.
	 * @param host 
	 * @param query 
	 */
	static findPropertyGroups(host: vCACCAFEHost, query: string): vCACCAFEContextPropertyGroup[];
	/**
	 * Gets a property definition from a host.
	 * @param host 
	 * @param propertyGroupId 
	 */
	static getPropertyGroup(host: vCACCAFEHost, propertyGroupId: string): vCACCAFEContextPropertyGroup;
	/**
	 * Gets all endpoint types for a host.
	 * @param host 
	 */
	static getEndpointTypes(host: vCACCAFEHost): vCACCAFEEndpointType[];
	/**
	 * Gets all endpoint types for a host matching the query by name and/or description.
	 * @param host 
	 * @param query 
	 */
	static findEndpointTypes(host: vCACCAFEHost, query: string): vCACCAFEEndpointType[];
	/**
	 * Gets a endpoint type from a host.
	 * @param host 
	 * @param endpointTypeId 
	 */
	static getEndpointType(host: vCACCAFEHost, endpointTypeId: string): vCACCAFEEndpointType;
	/**
	 * Gets all endpoint association types for a host.
	 * @param host 
	 */
	static getAssociationTypes(host: vCACCAFEHost): vCACCAFEAssociationType[];
	/**
	 * Gets all endpoint association types for a host matching the query by name and/or description.
	 * @param host 
	 * @param query 
	 */
	static findAssociationTypes(host: vCACCAFEHost, query: string): vCACCAFEAssociationType[];
	/**
	 * Gets a endpoint association type from a host.
	 * @param host 
	 * @param associationTypeId 
	 */
	static getAssociationType(host: vCACCAFEHost, associationTypeId: string): vCACCAFEAssociationType;
	/**
	 * Gets all endpoints for a host.
	 * @param host 
	 */
	static getEndpoints(host: vCACCAFEHost): vCACCAFEEndpoint[];
	/**
	 * Gets all endpoints for a host matching the query by name and/or description.
	 * @param host 
	 * @param query 
	 */
	static findEndpoints(host: vCACCAFEHost, query: string): vCACCAFEEndpoint[];
	/**
	 * Gets a endpoint from a host.
	 * @param host 
	 * @param endpointId 
	 */
	static getEndpoint(host: vCACCAFEHost, endpointId: string): vCACCAFEEndpoint;
	/**
	 * Gets a service from a host.
	 * @param host 
	 * @param serviceId 
	 */
	static getService(host: vCACCAFEHost, serviceId: string): vCACCAFEService;
	/**
	 * Gets all requests for a host.
	 * @param host 
	 */
	static getRequests(host: vCACCAFEHost): any[];
	/**
	 * Invalidates an entity by type and vcoId from the plug-in inventory cache, if needed.
	 * @param type 
	 * @param id 
	 */
	static invalidate(type: string, id: string): void;
	/**
	 * Gets all services for a host.
	 * @param host 
	 */
	static getServices(host: vCACCAFEHost): vCACCAFEService[];
}

/**
 * List helper that allow adding element to to object form the scripting. It allow ading element to object`s list that does not have method add but just get list of elements.
 */
declare class vCACCAFEEntityHelper {
	/**
	 * Adds an element to a list.
	 * @param objectContainingList 
	 * @param methodName 
	 * @param objectToAdd 
	 */
	static addElementToList(objectContainingList: any, methodName: string, objectToAdd: any): void;
	/**
	 * Adds elements to a list.
	 * @param objectContainingList 
	 * @param methodName 
	 * @param objectsToAdd 
	 */
	static addElementsToList(objectContainingList: any, methodName: string, objectsToAdd: any[]): void;
	/**
	 * Removes an element from a list.
	 * @param objectContainingList 
	 * @param methodName 
	 * @param objectToRemove 
	 */
	static removeElementFromList(objectContainingList: any, methodName: string, objectToRemove: any): void;
	/**
	 * Replaces the element at the specified position in a list with the specified element
	 * @param objectContainingList 
	 * @param methodName 
	 * @param index 
	 * @param element 
	 */
	static replaceElementInList(objectContainingList: any, methodName: string, index: number, element: any): void;
	/**
	 * Removes all elements from a list.
	 * @param objectContainingList 
	 * @param methodName 
	 */
	static removeAllFromList(objectContainingList: any, methodName: string): void;
	/**
	 * Puts the given source map to the target map using the method putAll from the interafce java.util.Map.
	 * @param targetObject 
	 * @param targetMethodName 
	 * @param sourceObject 
	 * @param sourceMethodName 
	 */
	static putAllToMap(targetObject: any, targetMethodName: string, sourceObject: any, sourceMethodName: string): void;
}

/**
 * Manager that enables vCAC host creation and configuration reload.
 */
declare class vCACCAFEHostManager {
	/**
	 * Adds a new vCAC by taking the information from component registry. To use this method vCO should be registered in vCAC component registry.
	 * @param properties 
	 */
	static addHostUsingComponentRegistry(properties: any): vCACCAFEHost;
	/**
	 * Adds a new vCAC host.
	 * @param properties 
	 */
	static addHost(properties: any): vCACCAFEHost;
	/**
	 * Reloads the configuration of all brokers.
	 */
	static reloadConfiguration(): void;
	/**
	 * Checks if the default host is already added.
	 */
	static defaultHostExists(): boolean;
	/**
	 * Updates an existing vCAC host.
	 * @param host 
	 * @param properties 
	 */
	static updateHost(host: vCACCAFEHost, properties: any): vCACCAFEHost;
	/**
	 * Removes an existing vCAC host.
	 * @param host 
	 */
	static removeHost(host: vCACCAFEHost): void;
	/**
	 * Creates a temporary vCAC host.
	 * @param properties 
	 */
	static createHost(properties: any): vCACCAFEHost;
	/**
	 * Checks if the default host is already added and returns it. If the host does not exists a new one will be created. If fail on mismatch is set to false a host with token's tenant will be returned.
	 * @param tenantId 
	 * @param failOnTenantMismatch 
	 */
	static getDefaultHostForTenant(tenantId: string, failOnTenantMismatch: boolean): vCACCAFEHost;
}

/**
 * Request helper with methods to manage catalog requests.
 */
declare class vCACCAFERequestsHelper {
	/**
	 * Gets the request data for a resource action as string representing the JSON.
	 * @param request 
	 */
	static getResourceActionRequestData(request: vCACCAFECatalogResourceRequest): string;
	/**
	 * Sets the resource action request data.
	 * @param request 
	 * @param json 
	 */
	static setResourceActionRequestData(request: vCACCAFECatalogResourceRequest, json: string): vCACCAFECatalogResourceRequest;
	/**
	 * Submits a resource action request using request template. If the request is null the default one will be used.
	 * @param action 
	 * @param resourceRequest 
	 */
	static requestResourceActionWithRequest(action: vCACCAFEConsumerResourceOperation, resourceRequest: vCACCAFECatalogResourceRequest): vCACCAFEResourceActionRequest;
	/**
	 * Submits a catalog item request.
	 * @param action 
	 * @param form 
	 */
	static requestResourceAction(action: vCACCAFEConsumerResourceOperation, form: vCACCAFEForm): vCACCAFEResourceActionRequest;
	/**
	 * Submits a resource action request on behalf of some user.
	 * @param action 
	 * @param form 
	 * @param onBehalfOf 
	 */
	static requestResourceActionOnBehalfOf(action: vCACCAFEConsumerResourceOperation, form: vCACCAFEForm, onBehalfOf: string): vCACCAFEResourceActionRequest;
	/**
	 * Gets the request form input parameters.
	 * @param form 
	 */
	static getFormKeys(form: vCACCAFEForm): string[];
	/**
	 * Sets the request form input parameters from the provided properties.
	 * @param form 
	 * @param entries 
	 */
	static setFormValues(form: vCACCAFEForm, entries: any): vCACCAFEForm;
	/**
	 * Completes a work item.
	 * @param item 
	 * @param action 
	 * @param map 
	 */
	static completeWorkItem(item: vCACCAFEWorkItem, action: vCACCAFEWorkItemAction, map: vCACCAFELiteralMap): void;
	/**
	 * Cancels a work item.
	 * @param item 
	 */
	static cancelWorkItem(item: vCACCAFEWorkItem): void;
	/**
	 * Creates a literal map from the provided properties.
	 * @param entries 
	 */
	static createLiteralMap(entries: any): vCACCAFELiteralMap;
	/**
	 * Sets the work item literal map input parameters from the provided properties.
	 * @param map 
	 * @param entries 
	 */
	static setLiteralMapValues(map: vCACCAFELiteralMap, entries: any): vCACCAFELiteralMap;
	/**
	 * Creates a trigger for monitoring catalog item requests.
	 * @param request 
	 * @param timeout 
	 */
	static createTriggerForCatalogItemRequest(request: vCACCAFECatalogItemRequest, timeout: number): any;
	/**
	 * Creates a trigger for monitoring resource action requests.
	 * @param request 
	 * @param timeout 
	 */
	static createTriggerForResourceActionRequest(request: vCACCAFEResourceActionRequest, timeout: number): any;
	/**
	 * Creates a trigger for monitoring work items.
	 * @param item 
	 * @param timeout 
	 */
	static createTriggerForWorkItem(item: vCACCAFEWorkItem, timeout: number): any;
	/**
	 * Retrieves the result of a trigger.
	 * @param trigger 
	 */
	static extractTriggerResult(trigger: any): string;
	/**
	 * Gets the request form for a catalog item and the first subtenant available.
	 * @param item 
	 */
	static getRequestFormForCatalogItem(item: vCACCAFECatalogItem): vCACCAFEForm;
	/**
	 * Gets the provisioning request for a catalog item.
	 * @param item 
	 */
	static getProvisioningRequestForCatalogItem(item: vCACCAFECatalogItem): vCACCAFECatalogItemProvisioningRequest;
	/**
	 * Gets the provisioning request data as string representing the JSON.
	 * @param request 
	 */
	static getProvisioningRequestData(request: vCACCAFECatalogItemProvisioningRequest): string;
	/**
	 * Sets the provisioning request data.
	 * @param request 
	 * @param json 
	 */
	static setProvisioningRequestData(request: vCACCAFECatalogItemProvisioningRequest, json: string): vCACCAFECatalogItemProvisioningRequest;
	/**
	 * Gets the request form for a catalog item and a specific subtenant.
	 * @param item 
	 * @param subtenant 
	 */
	static getRequestFormForCatalogItemBySubtenant(item: vCACCAFECatalogItem, subtenant: string): vCACCAFEForm;
	/**
	 * Submits a catalog item request.
	 * @param item 
	 * @param form 
	 */
	static requestCatalogItem(item: vCACCAFECatalogItem, form: vCACCAFEForm): vCACCAFECatalogItemRequest;
	/**
	 * Submits a catalog item request using provisioning request. If the provisioning request is null the default one will be used.
	 * @param item 
	 * @param provisioningRequest 
	 */
	static requestCatalogItemWithProvisioningRequest(item: vCACCAFECatalogItem, provisioningRequest: vCACCAFECatalogItemProvisioningRequest): vCACCAFECatalogItemRequest;
	/**
	 * Submits a catalog item request with a specific subtenant.
	 * @param item 
	 * @param form 
	 * @param subtenant 
	 */
	static requestCatalogItemBySubtenant(item: vCACCAFECatalogItem, form: vCACCAFEForm, subtenant: string): vCACCAFECatalogItemRequest;
	/**
	 * Submits a catalog item request on behalf of some user.
	 * @param item 
	 * @param form 
	 * @param onBehalfOf 
	 */
	static requestCatalogItemOnBehalfOf(item: vCACCAFECatalogItem, form: vCACCAFEForm, onBehalfOf: string): vCACCAFECatalogItemRequest;
	/**
	 * Submits a catalog item request with a specific subtenant on behalf of some user.
	 * @param item 
	 * @param form 
	 * @param subtenant 
	 * @param onBehalfOf 
	 */
	static requestCatalogItemBySubtenantOnBehalfOf(item: vCACCAFECatalogItem, form: vCACCAFEForm, subtenant: string, onBehalfOf: string): vCACCAFECatalogItemRequest;
	/**
	 * Gets the request form of a resource action.
	 * @param action 
	 */
	static getRequestFormForResourceAction(action: vCACCAFEConsumerResourceOperation): vCACCAFEForm;
	/**
	 * Gets the template resource request for a resource action
	 * @param action 
	 */
	static getRequestForResourceAction(action: vCACCAFEConsumerResourceOperation): vCACCAFECatalogResourceRequest;
	/**
	 * Gets the available subtenants for a catalog item.
	 * @param item 
	 */
	static getCatalogItemAvailableSubtenants(item: vCACCAFECatalogItem): string[];
}

/**
 * Enumeration of the default vCAC CAFE services end points available.
 */
declare class vCACCAFEServicesEnum {
	static readonly ADVANCED_DESIGNER_SERVICE: string;
	static readonly APPROVAL_SERVICE: string;
	static readonly AUTHENTICATION_SERVICE: string;
	static readonly AUTHORIZATION_SERVICE: string;
	static readonly CATALOG_SERVICE: string;
	static readonly CATALOG_PROVIDER_SERVICE: string;
	static readonly EVENT_LOG_SERVICE: string;
	static readonly NOTIFICATION_SERVICE: string;
	static readonly WORK_ITEM_SERVICE: string;
	static readonly EVENT_BROKER_SERVICE: string;
	static readonly COMPOSITION_SERVICE: string;
	static readonly RESERVATION_SERVICE: string;
	static readonly PROPERTY_SERVICE: string;
	static readonly ENDPOINT_CONFIGURATION_SERVICE: string;
	/**
	 * Lists all services end points.
	 */
	static getAll(): string[];
}

/**
 * Subtenant helper with methods to manage business groups. The scripting methods change only the state of the business group. In order to save the business group SubtenantService should be used.
 */
declare class vCACCAFESubtenantHelper {
	/**
	 * Returns the machine prefix id of the business group.
	 * @param group 
	 */
	static getMachinePrefixId(group: vCACCAFESubtenant): string;
	/**
	 * Returns the support members of the business group.
	 * @param group 
	 */
	static getSupport(group: vCACCAFESubtenant): string[];
	/**
	 * Returns the users of the business group.
	 * @param group 
	 */
	static getUsers(group: vCACCAFESubtenant): string[];
	/**
	 * Sets the machine prefix id of the business group.
	 * @param group 
	 * @param machinePrefixId 
	 */
	static setMachinePrefixId(group: vCACCAFESubtenant, machinePrefixId: string): void;
	/**
	 * Sets the users to the business group.
	 * @param group 
	 * @param users 
	 */
	static setUsers(group: vCACCAFESubtenant, users: string[]): void;
	/**
	 * Updates a custom property from the business group.
	 * @param group 
	 * @param oldName 
	 * @param newName 
	 * @param value 
	 * @param isEncrypted 
	 * @param isRuntime 
	 */
	static updateCustomProperty(group: vCACCAFESubtenant, oldName: string, newName: string, value: string, isEncrypted: boolean, isRuntime: boolean): vCACCAFESubtenant;
	/**
	 * Sets the managers to the business group.
	 * @param group 
	 * @param users 
	 */
	static setManagers(group: vCACCAFESubtenant, users: string[]): void;
	/**
	 * Sets the support members to the business group.
	 * @param group 
	 * @param users 
	 */
	static setSupportMembers(group: vCACCAFESubtenant, users: string[]): void;
	/**
	 * Sets the manager emails of the business group.
	 * @param group 
	 * @param managerEmails 
	 */
	static setManagerEmails(group: vCACCAFESubtenant, managerEmails: string): void;
	/**
	 * Sets the active directory container of the business group.
	 * @param group 
	 * @param adContainer 
	 */
	static setADContainer(group: vCACCAFESubtenant, adContainer: string): void;
	/**
	 * Adds a custom property to the business group.
	 * @param group 
	 * @param name 
	 * @param value 
	 * @param isEncrypted 
	 * @param isRuntime 
	 */
	static addCustomProperty(group: vCACCAFESubtenant, name: string, value: string, isEncrypted: boolean, isRuntime: boolean): vCACCAFESubtenant;
	/**
	 * Adds the managers to the business group.
	 * @param group 
	 * @param users 
	 */
	static addManagers(group: vCACCAFESubtenant, users: string[]): void;
	/**
	 * Adds the support members to the business group.
	 * @param group 
	 * @param users 
	 */
	static addSupportMembers(group: vCACCAFESubtenant, users: string[]): void;
	/**
	 * Adds the users to the business group.
	 * @param group 
	 * @param users 
	 */
	static addUsers(group: vCACCAFESubtenant, users: string[]): void;
	/**
	 * Returns the manager emails of the business group.
	 * @param group 
	 */
	static getManagerEmails(group: vCACCAFESubtenant): string;
	/**
	 * Returns the active directory container of the business group.
	 * @param group 
	 */
	static getADContainer(group: vCACCAFESubtenant): string;
	/**
	 * Returns the custom property names of the business group.
	 * @param group 
	 * @param propertyName 
	 */
	static getCustomPropertyValue(group: vCACCAFESubtenant, propertyName: string): string;
	/**
	 * Returns the custom property value.
	 * @param group 
	 */
	static getCustomPropertyNames(group: vCACCAFESubtenant): string[];
	/**
	 * Returns if the custom property is encrypted.
	 * @param group 
	 * @param propertyName 
	 */
	static isCustomPropertyEncrypted(group: vCACCAFESubtenant, propertyName: string): boolean;
	/**
	 * Returns the custom property 'Prompt User' value.
	 * @param group 
	 * @param propertyName 
	 */
	static isCustomPropertyPromptUser(group: vCACCAFESubtenant, propertyName: string): boolean;
	/**
	 * Removes a custom property from the business group.
	 * @param group 
	 * @param name 
	 */
	static removeCustomProperty(group: vCACCAFESubtenant, name: string): vCACCAFESubtenant;
	/**
	 * Returns the managers of the business group.
	 * @param group 
	 */
	static getManagers(group: vCACCAFESubtenant): string[];
}

declare class vCACCAFEAboutInfo {
	constructor();
	/**
	 * @param buildNumber 
	 * @param buildDate 
	 * @param productVersion 
	 * @param productBuildNumber 
	 */
	constructor(buildNumber: string, buildDate: string, productVersion: string, productBuildNumber: string);
	getProductVersion(): string;
	getBuildNumber(): string;
	getProductBuildNumber(): string;
	getApiVersion(): string;
	getBuildDate(): string;
}

declare class vCACCAFEAclEntry {
	constructor();
	constructor();
	/**
	 * @param value 
	 */
	setRoleId(value: string): void;
	/**
	 * @param value 
	 */
	setResourceRefId(value: string): void;
	getRoleId(): string;
	getResourceRefId(): string;
	getPrincipalId(): string;
	/**
	 * @param value 
	 */
	setPrincipalId(value: string): void;
}

declare class vCACCAFEActiveSubscription {
	constructor();
	constructor();
	getSessionId(): string;
	/**
	 * @param obj 
	 */
	equals(obj: any): boolean;
	toString(): string;
	hashCode(): number;
	getId(): string;
	/**
	 * @param value 
	 */
	setSessionId(value: string): void;
	/**
	 * @param value 
	 */
	setEndpoint(value: string): void;
	getEndpoint(): string;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	/**
	 * @param value 
	 */
	setUserId(value: string): void;
	getUserId(): string;
}

declare class vCACCAFEAddressSpace {
	constructor();
	constructor();
	getProviderEndpointId(): string;
	/**
	 * @param value 
	 */
	setProviderEndpointId(value: string): void;
	getExternalId(): string;
	/**
	 * @param value 
	 */
	setExternalId(value: string): void;
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	/**
	 * @param value 
	 */
	setExtensionData(value: vCACCAFELiteralMap): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	getExtensionData(): vCACCAFELiteralMap;
}

declare interface vCACCAFEAdminCatalogItem {
	version: number;
	organization: vCACCAFECatalogOrganizationReference;
	forms: vCACCAFECatalogItemForms;
	name: string;
	id: string;
	description: string;
	status: vCACCAFEPublishStatus;
	dateCreated: any;
	requestable: boolean;
	quota: number;
	serviceRef: vCACCAFELabelledReference;
	isNoteworthy: boolean;
	outputResourceTypeRef: vCACCAFELabelledReference;
	providerBinding: vCACCAFEProviderBinding;
	vcoId: any;
	lastUpdatedDate: any;
	statusName: string;
	callbacks: vCACCAFECatalogItemCallbackSupport;
	iconId: string;
	catalogItemTypeRef: vCACCAFELabelledReference;
	getVersion(): number;
	/**
	 * @param value 
	 */
	setOrganization(value: vCACCAFECatalogOrganizationReference): void;
	getOrganization(): vCACCAFECatalogOrganizationReference;
	getForms(): vCACCAFECatalogItemForms;
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	getStatus(): vCACCAFEPublishStatus;
	/**
	 * @param value 
	 */
	setVersion(value: number): void;
	getDateCreated(): any;
	/**
	 * @param value 
	 */
	setDateCreated(value: any): void;
	/**
	 * @param value 
	 */
	setQuota(value: number): void;
	isRequestable(): boolean;
	/**
	 * @param value 
	 */
	setRequestable(value: boolean): void;
	getQuota(): number;
	getServiceRef(): vCACCAFELabelledReference;
	/**
	 * @param value 
	 */
	setServiceRef(value: vCACCAFELabelledReference): void;
	/**
	 * @param value 
	 */
	setIsNoteworthy(value: boolean): void;
	isIsNoteworthy(): boolean;
	/**
	 * @param value 
	 */
	setCatalogItemTypeRef(value: vCACCAFELabelledReference): void;
	getOutputResourceTypeRef(): vCACCAFELabelledReference;
	/**
	 * @param value 
	 */
	setOutputResourceTypeRef(value: vCACCAFELabelledReference): void;
	/**
	 * @param value 
	 */
	setForms(value: vCACCAFECatalogItemForms): void;
	getProviderBinding(): vCACCAFEProviderBinding;
	/**
	 * @param value 
	 */
	setProviderBinding(value: vCACCAFEProviderBinding): void;
	getVcoId(): any;
	/**
	 * @param undefined 
	 */
	setVcoId(id: any): void;
	getLastUpdatedDate(): any;
	/**
	 * @param value 
	 */
	setLastUpdatedDate(value: any): void;
	getStatusName(): string;
	/**
	 * @param value 
	 */
	setStatusName(value: string): void;
	getCallbacks(): vCACCAFECatalogItemCallbackSupport;
	/**
	 * @param value 
	 */
	setCallbacks(value: vCACCAFECatalogItemCallbackSupport): void;
	getIconId(): string;
	/**
	 * @param value 
	 */
	setIconId(value: string): void;
	getCatalogItemTypeRef(): vCACCAFELabelledReference;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	/**
	 * @param value 
	 */
	setStatus(value: vCACCAFEPublishStatus): void;
}

declare interface vCACCAFEAdvancedDesignerClient {
	getAdvancedDesignerEndpointManagementService(): vCACCAFEAdvancedDesignerEndpointManagementService;
	getAdvancedDesignerGenericFormService(): vCACCAFEAdvancedDesignerGenericFormService;
	getAdvancedDesignerInteractionWorkItemService(): vCACCAFEAdvancedDesignerInteractionWorkItemService;
	getAdvancedDesignerInventoryTypeService(): vCACCAFEAdvancedDesignerInventoryTypeService;
	getAdvancedDesignerConfigurationService(): vCACCAFEAdvancedDesignerConfigurationService;
	getAdvancedDesignerSchemaService(): vCACCAFEAdvancedDesignerSchemaService;
	getAdvancedDesignerScriptActionService(): vCACCAFEAdvancedDesignerScriptActionService;
	getAdvancedDesignerValueDefinitionService(): vCACCAFEAdvancedDesignerValueDefinitionService;
	getAdvancedDesignerVcoImportService(): vCACCAFEAdvancedDesignerVcoImportService;
	getAdvancedDesignerWorkflowService(): vCACCAFEAdvancedDesignerWorkflowService;
	getAdvancedDesignerCsResourceTypeService(): vCACCAFEAdvancedDesignerCsResourceTypeService;
	getAdvancedDesignerCsResourceOperationService(): vCACCAFEAdvancedDesignerCsResourceOperationService;
	getAdvancedDesignerServiceBlueprintService(): vCACCAFEAdvancedDesignerServiceBlueprintService;
	/**
	 * Does a POST request expanding the given template variables in order.
	 * @param resourceUrl 
	 * @param object 
	 * @param urlVariables 
	 */
	postWithVariables(resourceUrl: string, object: any, urlVariables: string[]): vCACCAFEServiceResponse;
	/**
	 * Does a PUT request expanding the given template variables in order.
	 * @param resourceUrl 
	 * @param object 
	 * @param urlVariables 
	 */
	putWithVariables(resourceUrl: string, object: any, urlVariables: string[]): vCACCAFEServiceResponse;
	/**
	 * Does a DELETE request expanding the given template variables in order.
	 * @param resourceUrl 
	 * @param urlVariables 
	 */
	deleteWithVariables(resourceUrl: string, urlVariables: string[]): void;
	/**
	 * Gets the REST client URL.
	 */
	getUrl(): string;
	/**
	 * Indicates whether the current connection is already closed or not.
	 */
	isClosed(): boolean;
	/**
	 * Does a GET request for a file and allows specifying 'Accept' header.
	 * @param resourceUrl 
	 * @param acceptHeaderValue 
	 */
	getFileWithAcceptHeader(resourceUrl: string, acceptHeaderValue: string): any;
	/**
	 * Does a POST request with a file.
	 * @param resourceUrl 
	 * @param object 
	 */
	postFile(resourceUrl: string, object: any): vCACCAFEServiceResponse;
	/**
	 * Does a GET request expanding the given template variables in order.
	 * @param resourceUrl 
	 * @param urlVariables 
	 */
	getWithVariables(resourceUrl: string, urlVariables: string[]): any;
	/**
	 * Does a GET request.
	 * @param resourceUrl 
	 */
	get(resourceUrl: string): any;
	/**
	 * Does a PUT request.
	 * @param resourceUrl 
	 * @param object 
	 */
	put(resourceUrl: string, object: any): vCACCAFEServiceResponse;
	/**
	 * Does a DELETE request.
	 * @param resourceUrl 
	 */
	delete(resourceUrl: string): void;
	/**
	 * Invalidates the current connection.
	 */
	close(): void;
	/**
	 * Does a GET request for a file.
	 * @param resourceUrl 
	 */
	getFile(resourceUrl: string): any;
	/**
	 * Does a POST request.
	 * @param resourceUrl 
	 * @param object 
	 */
	post(resourceUrl: string, object: any): vCACCAFEServiceResponse;
}

declare interface vCACCAFEAdvancedDesignerConfigurationService {
	/**
	 * This method invokes the SDK method ConfigurationService.createOrUpdateConfiguration(String tenantId, Configuration configuration).
	 * @param tenantId 
	 * @param configuration 
	 */
	createOrUpdateConfiguration(tenantId: string, configuration: vCACCAFEConfiguration): void;
	/**
	 * This method invokes the SDK method ConfigurationService.testConfiguration(String tenantId, Configuration configuration).
	 * @param tenantId 
	 * @param configuration 
	 */
	testConfiguration(tenantId: string, configuration: vCACCAFEConfiguration): vCACCAFEConfigurationStatus;
	/**
	 * This method invokes the SDK method ConfigurationService.testDefaultConfiguration(String tenantId).
	 * @param tenantId 
	 */
	testDefaultConfiguration(tenantId: string): vCACCAFEConfigurationStatus;
	/**
	 * This method invokes the SDK method ConfigurationService.useDefaultConfiguration(String tenantId).
	 * @param tenantId 
	 */
	useDefaultConfiguration(tenantId: string): void;
	/**
	 * This method invokes the SDK method ConfigurationService.getConfiguration(String tenantId, String configurationId).
	 * @param tenantId 
	 * @param configurationId 
	 */
	getConfiguration(tenantId: string, configurationId: string): vCACCAFEConfiguration;
	/**
	 * This method invokes the SDK method ConfigurationService.getConfigurations(String tenantId).
	 * @param tenantId 
	 */
	getConfigurations(tenantId: string): vCACCAFEConfiguration[];
	/**
	 * This method invokes the SDK method ConfigurationService.removeConfiguration(String tenantId, String configurationId).
	 * @param tenantId 
	 * @param configurationId 
	 */
	removeConfiguration(tenantId: string, configurationId: string): void;
}

declare interface vCACCAFEAdvancedDesignerCsResourceOperationService {
	/**
	 * This method invokes the SDK method CsResourceOperationService.getResourceOperations(Pageable pageable).
	 * @param pageable 
	 */
	getResourceOperations(pageable: any): vCACCAFECsResourceOperation[];
	/**
	 * This method invokes the SDK method CsResourceOperationService.getResourceOperation(String id).
	 * @param id 
	 */
	getResourceOperation(id: string): vCACCAFECsResourceOperation;
	/**
	 * This method invokes the SDK method CsResourceOperationService.updateResourceOperationStatus(String resourceOperationId, DesignerPublishStatus status).
	 * @param resourceOperationId 
	 * @param status 
	 */
	updateResourceOperationStatus(resourceOperationId: string, status: vCACCAFEDesignerPublishStatus): void;
	/**
	 * This method invokes the SDK method CsResourceOperationService.cloneResourceOperation(String resourceOperationId).
	 * @param resourceOperationId 
	 */
	cloneResourceOperation(resourceOperationId: string): any;
	/**
	 * This method invokes the SDK method CsResourceOperationService.createResourceOperation(CsResourceOperation resourceOperation).
	 * @param resourceOperation 
	 */
	createResourceOperation(resourceOperation: vCACCAFECsResourceOperation): any;
	/**
	 * This method invokes the SDK method CsResourceOperationService.deleteResourceOperation(String resourceOperationId).
	 * @param resourceOperationId 
	 */
	deleteResourceOperation(resourceOperationId: string): void;
	/**
	 * This method invokes the SDK method CsResourceOperationService.getResourceOperation(URI uri).
	 * @param uri 
	 */
	getResourceOperationByUri(uri: any): vCACCAFECsResourceOperation;
	/**
	 * This method invokes the SDK method CsResourceOperationService.getResourceOperations(Pageable page).
	 * @param page 
	 */
	getResourceOperationsPaged(page: any): vCACCAFEPagedResources;
	/**
	 * This method invokes the SDK method CsResourceOperationService.updateResourceOperation(CsResourceOperation resourceOperation).
	 * @param resourceOperation 
	 */
	updateResourceOperation(resourceOperation: vCACCAFECsResourceOperation): void;
}

declare interface vCACCAFEAdvancedDesignerCsResourceTypeService {
	/**
	 * This method invokes the SDK method CsResourceTypeService.deleteResourceType(String tenantId, String resourceTypeId).
	 * @param tenantId 
	 * @param resourceTypeId 
	 */
	deleteResourceType(tenantId: string, resourceTypeId: string): void;
	/**
	 * This method invokes the SDK method CsResourceTypeService.getResourceCount(String tenantId, String resourceTypeId).
	 * @param tenantId 
	 * @param resourceTypeId 
	 */
	getResourceCount(tenantId: string, resourceTypeId: string): number;
	/**
	 * This method invokes the SDK method CsResourceTypeService.generateCsResourceTypeDetailsForm(String tenantId, InventoryType inventoryType).
	 * @param tenantId 
	 * @param inventoryType 
	 */
	generateCsResourceTypeDetailsForm(tenantId: string, inventoryType: vCACCAFEInventoryType): vCACCAFEFormScenario;
	/**
	 * This method invokes the SDK method CsResourceTypeService.createResourceType(String tenantId, CsResourceType resourceType).
	 * @param tenantId 
	 * @param resourceType 
	 */
	createResourceType(tenantId: string, resourceType: vCACCAFECsResourceType): any;
	/**
	 * This method invokes the SDK method CsResourceTypeService.getResourceType(URI uri).
	 * @param uri 
	 */
	getResourceTypeByUri(uri: any): vCACCAFECsResourceType;
	/**
	 * This method invokes the SDK method CsResourceTypeService.getResourceMappings(String tenantId, Pageable page).
	 * @param tenantId 
	 * @param page 
	 */
	getResourceMappings(tenantId: string, page: any): vCACCAFEPagedResources;
	/**
	 * This method invokes the SDK method CsResourceTypeService.getResourceTypes(String tenantId, Pageable page).
	 * @param tenantId 
	 * @param page 
	 */
	getResourceTypes(tenantId: string, page: any): vCACCAFEPagedResources;
	/**
	 * This method invokes the SDK method CsResourceTypeService.getResourceType(String tenantId, String resourceTypeId).
	 * @param tenantId 
	 * @param resourceTypeId 
	 */
	getResourceType(tenantId: string, resourceTypeId: string): vCACCAFECsResourceType;
	/**
	 * This method invokes the SDK method CsResourceTypeService.getSchema(String resourceTypeId).
	 * @param resourceTypeId 
	 */
	getSchema(resourceTypeId: string): vCACCAFESchema;
}

declare interface vCACCAFEAdvancedDesignerEndpointManagementService {
	/**
	 * This method invokes the SDK method EndpointManagementService.create(CsEndpointData endpointData).
	 * @param endpointData 
	 */
	create(endpointData: vCACCAFECsEndpointData): any;
	/**
	 * This method invokes the SDK method EndpointManagementService.getEndpoints(Pageable pageable).
	 * @param pageable 
	 */
	getEndpoints(pageable: any): vCACCAFEPagedResources;
}

declare interface vCACCAFEAdvancedDesignerGenericFormService {
	/**
	 * This method invokes the SDK method GenericFormService.deleteForm(String resourceOperationId).
	 * @param resourceOperationId 
	 */
	deleteForm(resourceOperationId: string): void;
	/**
	 * This method invokes the SDK method GenericFormService.getForm(String tenantId, String genericFormId).
	 * @param tenantId 
	 * @param genericFormId 
	 */
	getForm(tenantId: string, genericFormId: string): vCACCAFEGenericForm;
	/**
	 * This method invokes the SDK method GenericFormService.getForm(URI uri).
	 * @param uri 
	 */
	getFormByUri(uri: any): vCACCAFEGenericForm;
	/**
	 * This method invokes the SDK method GenericFormService.getForms(Pageable page).
	 * @param page 
	 */
	getForms(page: any): vCACCAFEPagedResources;
	/**
	 * This method invokes the SDK method GenericFormService.createForm(GenericForm genericForm).
	 * @param genericForm 
	 */
	createForm(genericForm: vCACCAFEGenericForm): any;
	/**
	 * This method invokes the SDK method GenericFormService.updateForm(GenericForm genericForm).
	 * @param genericForm 
	 */
	updateForm(genericForm: vCACCAFEGenericForm): void;
}

declare interface vCACCAFEAdvancedDesignerInteractionWorkItemService {
	/**
	 * This method invokes the SDK method InteractionWorkItemService.createWorkItem(InteractionWorkItem interactionWorkItem).
	 * @param interactionWorkItem 
	 */
	createWorkItem(interactionWorkItem: vCACCAFEInteractionWorkItem): void;
}

declare interface vCACCAFEAdvancedDesignerInventoryTypeService {
	/**
	 * This method invokes the SDK method InventoryTypeService.getInventoryTypes(String endpointType, String typeName, boolean includeSimpleTypes).
	 * @param endpointType 
	 * @param typeName 
	 * @param includeSimpleTypes 
	 */
	getInventoryTypesByEndpointTypeTypeNameIncludeSimpleTypes(endpointType: string, typeName: string, includeSimpleTypes: boolean): vCACCAFEResources;
	/**
	 * This method invokes the SDK method InventoryTypeService.getInventoryTypes().
	 */
	getInventoryTypes(): vCACCAFEResources;
}

declare interface vCACCAFEAdvancedDesignerSchemaService {
	/**
	 * This method invokes the SDK method SchemaService.getSchemaTypeValues(String classId, ElementValuesRequest elementValuesRequest).
	 * @param classId 
	 * @param elementValuesRequest 
	 */
	getSchemaTypeValues(classId: string, elementValuesRequest: any): any;
}

declare interface vCACCAFEAdvancedDesignerScriptActionService {
	/**
	 * This method invokes the SDK method ScriptActionService.getScriptActionByFqn(String scriptActionFqn).
	 * @param scriptActionFqn 
	 */
	getScriptActionByFqn(scriptActionFqn: string): vCACCAFEScriptAction;
	/**
	 * This method invokes the SDK method ScriptActionService.getScriptActionCategories().
	 */
	getScriptActionCategories(): vCACCAFEScriptActionCategory[];
	/**
	 * This method invokes the SDK method ScriptActionService.getScriptActionsForCategory(String categoryName).
	 * @param categoryName 
	 */
	getScriptActionsForCategory(categoryName: string): vCACCAFEScriptActionSummary[];
	/**
	 * This method invokes the SDK method ScriptActionService.findScriptActions(String name, String description, String categoryName).
	 * @param name 
	 * @param description 
	 * @param categoryName 
	 */
	findScriptActions(name: string, description: string, categoryName: string): vCACCAFEScriptActionSummary[];
}

declare interface vCACCAFEAdvancedDesignerServiceBlueprintService {
	/**
	 * This method invokes the SDK method ServiceBlueprintService.updateServiceBlueprintStatus(String tenantId, String serviceBlueprintId, DesignerPublishStatus status).
	 * @param tenantId 
	 * @param serviceBlueprintId 
	 * @param status 
	 */
	updateServiceBlueprintStatus(tenantId: string, serviceBlueprintId: string, status: vCACCAFEDesignerPublishStatus): void;
	/**
	 * This method invokes the SDK method ServiceBlueprintService.cloneServiceBlueprint(String tenantId, String serviceBlueprintId).
	 * @param tenantId 
	 * @param serviceBlueprintId 
	 */
	cloneServiceBlueprint(tenantId: string, serviceBlueprintId: string): any;
	/**
	 * This method invokes the SDK method ServiceBlueprintService.createServiceBlueprint(String tenantId, ServiceBlueprint serviceBlueprint).
	 * @param tenantId 
	 * @param serviceBlueprint 
	 */
	createServiceBlueprint(tenantId: string, serviceBlueprint: vCACCAFEServiceBlueprint): any;
	/**
	 * This method invokes the SDK method ServiceBlueprintService.deleteServiceBlueprint(String tenantId, String serviceBlueprintId).
	 * @param tenantId 
	 * @param serviceBlueprintId 
	 */
	deleteServiceBlueprint(tenantId: string, serviceBlueprintId: string): void;
	/**
	 * This method invokes the SDK method ServiceBlueprintService.getServiceBlueprint(URI uri).
	 * @param uri 
	 */
	getServiceBlueprintByUri(uri: any): vCACCAFEServiceBlueprint;
	/**
	 * This method invokes the SDK method ServiceBlueprintService.updateServiceBlueprint(String tenantId, ServiceBlueprint serviceBlueprint).
	 * @param tenantId 
	 * @param serviceBlueprint 
	 */
	updateServiceBlueprint(tenantId: string, serviceBlueprint: vCACCAFEServiceBlueprint): void;
	/**
	 * This method invokes the SDK method ServiceBlueprintService.getServiceBlueprints(String tenantId, Pageable page).
	 * @param tenantId 
	 * @param page 
	 */
	getServiceBlueprints(tenantId: string, page: any): vCACCAFEPagedResources;
	/**
	 * This method invokes the SDK method ServiceBlueprintService.getServiceBlueprint(String tenantId, String serviceBlueprintId).
	 * @param tenantId 
	 * @param serviceBlueprintId 
	 */
	getServiceBlueprint(tenantId: string, serviceBlueprintId: string): vCACCAFEServiceBlueprint;
	/**
	 * This method invokes the SDK method ServiceBlueprintService.getSchema(String blueprintId).
	 * @param blueprintId 
	 */
	getSchema(blueprintId: string): vCACCAFESchema;
}

declare interface vCACCAFEAdvancedDesignerValueDefinitionService {
	/**
	 * This method invokes the SDK method ValueDefinitionService.getValuesByKey(String valueDefinitionKey, LiteralMap context).
	 * @param valueDefinitionKey 
	 * @param context 
	 */
	getValuesByKey(valueDefinitionKey: string, context: vCACCAFELiteralMap): any[];
	/**
	 * This method invokes the SDK method ValueDefinitionService.updateValueDefinition(ValueDefinition valueDefinition).
	 * @param valueDefinition 
	 */
	updateValueDefinition(valueDefinition: any): void;
	/**
	 * This method invokes the SDK method ValueDefinitionService.createValueDefinition(ValueDefinition valueDefinition).
	 * @param valueDefinition 
	 */
	createValueDefinition(valueDefinition: any): any;
	/**
	 * This method invokes the SDK method ValueDefinitionService.deleteValueDefinition(String valueDefinitionId).
	 * @param valueDefinitionId 
	 */
	deleteValueDefinition(valueDefinitionId: string): void;
	/**
	 * This method invokes the SDK method ValueDefinitionService.getValueByKey(String valueDefinitionKey, LiteralMap context).
	 * @param valueDefinitionKey 
	 * @param context 
	 */
	getValueByKey(valueDefinitionKey: string, context: vCACCAFELiteralMap): any;
	/**
	 * This method invokes the SDK method ValueDefinitionService.getValueDefinition(String valueDefinitionId).
	 * @param valueDefinitionId 
	 */
	getValueDefinition(valueDefinitionId: string): any;
	/**
	 * This method invokes the SDK method ValueDefinitionService.getValueDefinitions(Pageable page).
	 * @param page 
	 */
	getValueDefinitions(page: any): vCACCAFEPagedResources;
	/**
	 * This method invokes the SDK method ValueDefinitionService.getValueDesinition(URI uri).
	 * @param uri 
	 */
	getValueDesinitionByUri(uri: any): any;
}

declare interface vCACCAFEAdvancedDesignerVcoImportService {
	/**
	 * This method invokes the SDK method VcoImportService.importResource(Resource resource).
	 * @param resource 
	 */
	importResource(resource: any): string;
}

declare interface vCACCAFEAdvancedDesignerWorkflowService {
	/**
	 * This method invokes the SDK method WorkflowService.generateFormByType(String workflowId, CsFormType formType, String excludedField).
	 * @param workflowId 
	 * @param formType 
	 * @param excludedField 
	 */
	generateFormByTypeExcludedField(workflowId: string, formType: vCACCAFECsFormType, excludedField: string): vCACCAFEFormScenario;
	/**
	 * This method invokes the SDK method WorkflowService.generateResourceOperationByWorkflowIdAndInputParameter(String workflowId, CsParameter inputParameter).
	 * @param workflowId 
	 * @param inputParameter 
	 */
	generateResourceOperationByWorkflowIdAndInputParameter(workflowId: string, inputParameter: vCACCAFECsParameter): vCACCAFECsResourceOperation;
	/**
	 * This method invokes the SDK method WorkflowService.generateServiceBlueprintByWorkflowId(String workflowId).
	 * @param workflowId 
	 */
	generateServiceBlueprintByWorkflowId(workflowId: string): vCACCAFEServiceBlueprint;
	/**
	 * This method invokes the SDK method WorkflowService.getWorkflowById(String workflowId).
	 * @param workflowId 
	 */
	getWorkflowById(workflowId: string): vCACCAFEWorkflow;
	/**
	 * This method invokes the SDK method WorkflowService.getWorkflowFields(String workflowId, String usageClass).
	 * @param workflowId 
	 * @param usageClass 
	 */
	getWorkflowFields(workflowId: string, usageClass: string): any;
	/**
	 * This method invokes the SDK method WorkflowService.getWorkflowOutputs(String workflowId, String usageClass).
	 * @param workflowId 
	 * @param usageClass 
	 */
	getWorkflowOutputs(workflowId: string, usageClass: string): any;
	/**
	 * This method invokes the SDK method WorkflowService.generateFormByType(String workflowId, CsFormType formType).
	 * @param workflowId 
	 * @param formType 
	 */
	generateFormByType(workflowId: string, formType: vCACCAFECsFormType): vCACCAFEFormScenario;
}

declare class vCACCAFEAgentInfo {
	constructor();
	constructor();
	getAppdVersion(): string;
	/**
	 * @param value 
	 */
	setAppdVersion(value: string): void;
	getAppdAgentIdentifier(): string;
	/**
	 * @param value 
	 */
	setAppdAgentIdentifier(value: string): void;
}

declare class vCACCAFEAgentScriptExecutionRequest {
	constructor();
	constructor();
	getProperties(): vCACCAFEScriptProperty[];
	getId(): string;
	getScript(): string;
	isRebootAfter(): boolean;
	/**
	 * @param value 
	 */
	setScript(value: string): void;
	getScriptType(): vCACCAFEScriptType;
	/**
	 * @param value 
	 */
	setScriptType(value: vCACCAFEScriptType): void;
	getRoutingKey(): string;
	/**
	 * @param value 
	 */
	setRoutingKey(value: string): void;
	/**
	 * @param value 
	 */
	setRebootAfter(value: boolean): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
}

declare class vCACCAFEAgentScriptExecutionResult {
	constructor();
	constructor();
	getOutputProperties(): vCACCAFEScriptProperty[];
	getId(): string;
	getStatus(): vCACCAFEScriptExecutionStatus;
	getLogMessage(): string;
	/**
	 * @param value 
	 */
	setLogMessage(value: string): void;
	isOverwriteLog(): boolean;
	/**
	 * @param value 
	 */
	setOverwriteLog(value: boolean): void;
	getLogDescription(): string;
	/**
	 * @param value 
	 */
	setLogDescription(value: string): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	/**
	 * @param value 
	 */
	setStatus(value: vCACCAFEScriptExecutionStatus): void;
}

declare class vCACCAFEAlert {
	constructor();
	constructor();
	getAlertPercentLevel(): number;
	/**
	 * @param value 
	 */
	setAlertPercentLevel(value: number): void;
	getReferenceResourceId(): string;
	/**
	 * @param value 
	 */
	setReferenceResourceId(value: string): void;
}

declare class vCACCAFEAlertPolicy {
	constructor();
	constructor();
	isEnabled(): boolean;
	getAlerts(): vCACCAFEAlert[];
	getFrequencyReminder(): number;
	/**
	 * @param value 
	 */
	setFrequencyReminder(value: number): void;
	isEmailBgMgr(): boolean;
	/**
	 * @param value 
	 */
	setEmailBgMgr(value: boolean): void;
	getRecipients(): string[];
	/**
	 * @param value 
	 */
	setEnabled(value: boolean): void;
}

declare class vCACCAFEAlertType {
	constructor();
	constructor();
	getVersion(): number;
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	/**
	 * @param value 
	 */
	setVersion(value: number): void;
	getReferenceResourceId(): string;
	/**
	 * @param value 
	 */
	setReferenceResourceId(value: string): void;
	getLastUpdated(): any;
	/**
	 * @param value 
	 */
	setLastUpdated(value: any): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	/**
	 * @param value 
	 */
	setCreatedDate(value: any): void;
	getCreatedDate(): any;
}

declare class vCACCAFEAllocationRequestCompletion {
	constructor();
	constructor();
	getAllocationResult(): vCACCAFELiteralMap;
	/**
	 * @param value 
	 */
	setAllocationResult(value: vCACCAFELiteralMap): void;
	getRequestCallbackAdditionalUrlPath(): string;
	/**
	 * @param value 
	 */
	setRequestCallbackAdditionalUrlPath(value: string): void;
	getServiceCallbackId(): string;
	/**
	 * @param value 
	 */
	setServiceCallbackId(value: string): void;
	getErrMsg(): string;
	/**
	 * @param value 
	 */
	setErrMsg(value: string): void;
	/**
	 * @param value 
	 */
	setRequestId(value: string): void;
	getRequestId(): string;
}

declare class vCACCAFEAndClause {
	constructor();
	/**
	 * @param subClauses 
	 */
	constructor(subClauses: com.vmware.vcac.platform.content.criteria.Clause[]);
	getSubClauses(): any[];
	getConstantValue(): vCACCAFEBooleanLiteral;
	isConstant(): boolean;
	/**
	 * @param bVal 
	 */
	fromBoolean(bVal: boolean): any;
	/**
	 * @param sVal 
	 */
	fromString(sVal: string): any;
	/**
	 * @param lVal 
	 */
	fromInt(lVal: number): any;
	/**
	 * @param dVal 
	 */
	fromDecimal(dVal: number): any;
}

declare class vCACCAFEApiVersion {
	constructor();
	constructor();
	getCurrentVersion(): string;
	/**
	 * @param currentVersion 
	 */
	setCurrentVersion(currentVersion: string): void;
	getSupportedVersions(): string[];
	/**
	 * @param supportedVersions 
	 */
	setSupportedVersions(supportedVersions: string[]): void;
}

declare class vCACCAFEApplicationProfile {
	constructor();
	constructor();
	/**
	 * @param value 
	 */
	setMethod(value: string): void;
	getMethod(): string;
	getExpiry(): number;
	isInsertXForwardedFor(): boolean;
	/**
	 * @param value 
	 */
	setInsertXForwardedFor(value: boolean): void;
	isSslPassthrough(): boolean;
	/**
	 * @param value 
	 */
	setSslPassthrough(value: boolean): void;
	getTemplate(): string;
	/**
	 * @param value 
	 */
	setTemplate(value: string): void;
	isServerSslEnabled(): boolean;
	/**
	 * @param value 
	 */
	setServerSslEnabled(value: boolean): void;
	getCookieMode(): string;
	/**
	 * @param value 
	 */
	setCookieMode(value: string): void;
	getCookieName(): string;
	/**
	 * @param value 
	 */
	setCookieName(value: string): void;
	/**
	 * @param value 
	 */
	setExpiry(value: number): void;
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	/**
	 * @param value 
	 */
	setExtensionData(value: vCACCAFELiteralMap): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	getExtensionData(): vCACCAFELiteralMap;
	getExternalId(): string;
	/**
	 * @param value 
	 */
	setExternalId(value: string): void;
}

declare class vCACCAFEApprovableItem {
	constructor();
	constructor();
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	getResourceType(): string;
	getTypeFilter(): string;
	/**
	 * @param value 
	 */
	setTypeFilter(value: string): void;
	getPolicyTypeId(): string;
	/**
	 * @param value 
	 */
	setPolicyTypeId(value: string): void;
	/**
	 * @param value 
	 */
	setResourceType(value: string): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
}

declare class vCACCAFEApproval {
	constructor();
	constructor();
	getLastModified(): any;
	getVersion(): number;
	getId(): string;
	getState(): vCACCAFEEvaluationState;
	/**
	 * @param value 
	 */
	setLastModified(value: any): void;
	getPolicy(): vCACCAFEApprovalPolicy;
	/**
	 * @param value 
	 */
	setStartTime(value: any): void;
	getStartTime(): any;
	/**
	 * @param value 
	 */
	setVersion(value: number): void;
	/**
	 * @param value 
	 */
	setState(value: vCACCAFEEvaluationState): void;
	getTenantId(): string;
	getDecisions(): vCACCAFEPhaseDecision[];
	/**
	 * @param value 
	 */
	setEvaluationSpec(value: vCACCAFEApprovalPolicyEvaluationSpec): void;
	getEvaluationSpec(): vCACCAFEApprovalPolicyEvaluationSpec;
	getCompletionTime(): any;
	/**
	 * @param value 
	 */
	setCompletionTime(value: any): void;
	getPhaseNumber(): number;
	/**
	 * @param value 
	 */
	setPhaseNumber(value: number): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	/**
	 * @param value 
	 */
	setPolicy(value: vCACCAFEApprovalPolicy): void;
	/**
	 * @param value 
	 */
	setCreatedDate(value: any): void;
	/**
	 * @param value 
	 */
	setTenantId(value: string): void;
	getCreatedDate(): any;
}

declare interface vCACCAFEApprovalApprovalInfoService {
	/**
	 * This method invokes the SDK method ApprovalInfoServiceImpl.getApprovalInfo(String approvalId).
	 * @param approvalId 
	 */
	getApprovalInfo(approvalId: string): any;
	/**
	 * This method invokes the SDK method ApprovalInfoServiceImpl.getApprovalRequestInfo(String approvalRequestId).
	 * @param approvalRequestId 
	 */
	getApprovalRequestInfo(approvalRequestId: string): any;
}

declare interface vCACCAFEApprovalApprovalPolicyService {
	/**
	 * This method invokes the SDK method ApprovalPolicyServiceImpl.createPolicy(ApprovalPolicy policySpec).
	 * @param policySpec 
	 */
	createPolicy(policySpec: vCACCAFEApprovalPolicy): vCACCAFEApprovalPolicy;
	/**
	 * This method invokes the SDK method ApprovalPolicyServiceImpl.update(ApprovalPolicy policySpec).
	 * @param policySpec 
	 */
	update(policySpec: vCACCAFEApprovalPolicy): vCACCAFEApprovalPolicy;
	/**
	 * This method invokes the SDK method ApprovalPolicyServiceImpl.delete(ApprovalPolicy policy).
	 * @param policy 
	 */
	delete(policy: vCACCAFEApprovalPolicy): void;
	/**
	 * This method invokes the SDK method ApprovalPolicyServiceImpl.retrieveApprovalPolicies(Pageable pageable).
	 * @param pageable 
	 */
	retrieveApprovalPolicies(pageable: any): vCACCAFEPagedResources;
	/**
	 * This method invokes the SDK method ApprovalPolicyServiceImpl.retrieveApprovalPolicy(String policyId).
	 * @param policyId 
	 */
	retrieveApprovalPolicy(policyId: string): vCACCAFEApprovalPolicy;
}

declare interface vCACCAFEApprovalApprovalPolicyTypeService {
	/**
	 * This method invokes the SDK method ApprovalPolicyTypeServiceImpl.getApprovalPolicyType(String approvalPolicyTypeId).
	 * @param approvalPolicyTypeId 
	 */
	getApprovalPolicyType(approvalPolicyTypeId: string): vCACCAFEApprovalPolicyType;
	/**
	 * This method invokes the SDK method ApprovalPolicyTypeServiceImpl.deleteApprovalPolicyType(String approvalPolicyTypeId).
	 * @param approvalPolicyTypeId 
	 */
	deleteApprovalPolicyType(approvalPolicyTypeId: string): void;
	/**
	 * This method invokes the SDK method ApprovalPolicyTypeServiceImpl.createApprovalPolicyType(ApprovalPolicyType approvalPolicyType).
	 * @param approvalPolicyType 
	 */
	createApprovalPolicyType(approvalPolicyType: vCACCAFEApprovalPolicyType): vCACCAFEApprovalPolicyType;
	/**
	 * This method invokes the SDK method ApprovalPolicyTypeServiceImpl.getApprovalPolicyType(URI uri).
	 * @param uri 
	 */
	getApprovalPolicyTypeByUri(uri: any): vCACCAFEApprovalPolicyType;
	/**
	 * This method invokes the SDK method ApprovalPolicyTypeServiceImpl.getApprovalPolicyTypes(Pageable pageable).
	 * @param pageable 
	 */
	getApprovalPolicyTypes(pageable: any): vCACCAFEPagedResources;
	/**
	 * This method invokes the SDK method ApprovalPolicyTypeServiceImpl.updateApprovalPolicyType(ApprovalPolicyType approvalPolicyType).
	 * @param approvalPolicyType 
	 */
	updateApprovalPolicyType(approvalPolicyType: vCACCAFEApprovalPolicyType): void;
}

declare interface vCACCAFEApprovalClient {
	getApprovalPolicyEvaluationService(): vCACCAFEApprovalPolicyEvaluationService;
	getApprovalApprovalInfoService(): vCACCAFEApprovalApprovalInfoService;
	getApprovalApprovalPolicyTypeService(): vCACCAFEApprovalApprovalPolicyTypeService;
	getApprovalApprovalPolicyService(): vCACCAFEApprovalApprovalPolicyService;
	/**
	 * Does a POST request expanding the given template variables in order.
	 * @param resourceUrl 
	 * @param object 
	 * @param urlVariables 
	 */
	postWithVariables(resourceUrl: string, object: any, urlVariables: string[]): vCACCAFEServiceResponse;
	/**
	 * Does a PUT request expanding the given template variables in order.
	 * @param resourceUrl 
	 * @param object 
	 * @param urlVariables 
	 */
	putWithVariables(resourceUrl: string, object: any, urlVariables: string[]): vCACCAFEServiceResponse;
	/**
	 * Does a DELETE request expanding the given template variables in order.
	 * @param resourceUrl 
	 * @param urlVariables 
	 */
	deleteWithVariables(resourceUrl: string, urlVariables: string[]): void;
	/**
	 * Gets the REST client URL.
	 */
	getUrl(): string;
	/**
	 * Indicates whether the current connection is already closed or not.
	 */
	isClosed(): boolean;
	/**
	 * Does a GET request for a file and allows specifying 'Accept' header.
	 * @param resourceUrl 
	 * @param acceptHeaderValue 
	 */
	getFileWithAcceptHeader(resourceUrl: string, acceptHeaderValue: string): any;
	/**
	 * Does a POST request with a file.
	 * @param resourceUrl 
	 * @param object 
	 */
	postFile(resourceUrl: string, object: any): vCACCAFEServiceResponse;
	/**
	 * Does a GET request expanding the given template variables in order.
	 * @param resourceUrl 
	 * @param urlVariables 
	 */
	getWithVariables(resourceUrl: string, urlVariables: string[]): any;
	/**
	 * Does a GET request.
	 * @param resourceUrl 
	 */
	get(resourceUrl: string): any;
	/**
	 * Does a PUT request.
	 * @param resourceUrl 
	 * @param object 
	 */
	put(resourceUrl: string, object: any): vCACCAFEServiceResponse;
	/**
	 * Does a DELETE request.
	 * @param resourceUrl 
	 */
	delete(resourceUrl: string): void;
	/**
	 * Invalidates the current connection.
	 */
	close(): void;
	/**
	 * Does a GET request for a file.
	 * @param resourceUrl 
	 */
	getFile(resourceUrl: string): any;
	/**
	 * Does a POST request.
	 * @param resourceUrl 
	 * @param object 
	 */
	post(resourceUrl: string, object: any): vCACCAFEServiceResponse;
}

declare class vCACCAFEApprovalDescriptiveReference {
	constructor();
	constructor();
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	/**
	 * @param value 
	 */
	setId(value: string): void;
}

declare class vCACCAFEApprovalForms {
	constructor();
	constructor();
	/**
	 * @param value 
	 */
	setApprovalForm(value: any): void;
	getApprovalForm(): any;
	/**
	 * @param value 
	 */
	setDetailsForm(value: any): void;
	getDetailsForm(): any;
}

declare class vCACCAFEApprovalInfo {
	constructor();
	constructor();
	getCurrentLevel(): vCACCAFEApprovalLevelStatusInfo;
	/**
	 * @param value 
	 */
	setCurrentLevel(value: vCACCAFEApprovalLevelStatusInfo): void;
	getPastLevel(): vCACCAFEApprovalLevelStatusInfo[];
	getFutureLevel(): vCACCAFEApprovalLevelStatusInfo[];
	/**
	 * @param value 
	 */
	setPhase(value: vCACCAFEApprovalDescriptiveReference): void;
	getId(): string;
	getState(): vCACCAFEEvaluationState;
	getPolicy(): vCACCAFEApprovalDescriptiveReference;
	/**
	 * @param value 
	 */
	setStartTime(value: any): void;
	getStartTime(): any;
	/**
	 * @param value 
	 */
	setState(value: vCACCAFEEvaluationState): void;
	getPolicyType(): vCACCAFEApprovalDescriptiveReference;
	/**
	 * @param value 
	 */
	setPolicyType(value: vCACCAFEApprovalDescriptiveReference): void;
	getSpec(): vCACCAFEApprovalPolicyEvaluationSpec;
	/**
	 * @param value 
	 */
	setSpec(value: vCACCAFEApprovalPolicyEvaluationSpec): void;
	getCompletionTime(): any;
	/**
	 * @param value 
	 */
	setCompletionTime(value: any): void;
	isHasSourceDetailsForm(): boolean;
	/**
	 * @param value 
	 */
	setHasSourceDetailsForm(value: boolean): void;
	getPhase(): vCACCAFEApprovalDescriptiveReference;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	/**
	 * @param value 
	 */
	setPolicy(value: vCACCAFEApprovalDescriptiveReference): void;
}

declare class vCACCAFEApprovalLevel {
	constructor();
	constructor();
	isExternal(): boolean;
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	getApprovalMode(): vCACCAFEApprovalMode;
	/**
	 * @param value 
	 */
	setApprovalMode(value: vCACCAFEApprovalMode): void;
	getEditSchema(): vCACCAFEFieldList;
	/**
	 * @param value 
	 */
	setEditSchema(value: vCACCAFEFieldList): void;
	getLevelNumber(): number;
	/**
	 * @param value 
	 */
	setLevelNumber(value: number): void;
	/**
	 * @param value 
	 */
	setExternal(value: boolean): void;
	getApprovers(): vCACCAFEApprovalPrincipal[];
	/**
	 * @param value 
	 */
	setId(value: string): void;
	/**
	 * @param value 
	 */
	setCriteria(value: any): void;
	getCriteria(): any;
}

declare class vCACCAFEApprovalLevelDecision {
	constructor();
	constructor();
	getId(): string;
	getState(): vCACCAFEEvaluationState;
	/**
	 * @param value 
	 */
	setStartTime(value: any): void;
	getStartTime(): any;
	/**
	 * @param value 
	 */
	setState(value: vCACCAFEEvaluationState): void;
	/**
	 * @param value 
	 */
	setLevel(value: vCACCAFEApprovalLevel): void;
	getLevel(): vCACCAFEApprovalLevel;
	getCompletionTime(): any;
	/**
	 * @param value 
	 */
	setCompletionTime(value: any): void;
	getApprovalMode(): vCACCAFEApprovalMode;
	/**
	 * @param value 
	 */
	setApprovalMode(value: vCACCAFEApprovalMode): void;
	getPhaseNumber(): number;
	/**
	 * @param value 
	 */
	setPhaseNumber(value: number): void;
	getApprovalRequests(): vCACCAFEApprovalRequest[];
	/**
	 * @param value 
	 */
	setId(value: string): void;
}

declare class vCACCAFEApprovalLevelStateChangeEvent {
	constructor();
	constructor();
	/**
	 * @param value 
	 */
	setLevel(value: vCACCAFEApprovalLevel): void;
	getLevel(): vCACCAFEApprovalLevel;
	/**
	 * @param value 
	 */
	setFromState(value: vCACCAFEEvaluationState): void;
	getToState(): vCACCAFEEvaluationState;
	/**
	 * @param value 
	 */
	setToState(value: vCACCAFEEvaluationState): void;
	getFromState(): vCACCAFEEvaluationState;
	getEventType(): vCACCAFEEvaluationEventType;
	/**
	 * @param value 
	 */
	setEventType(value: vCACCAFEEvaluationEventType): void;
	getApprovalId(): string;
	/**
	 * @param value 
	 */
	setApprovalId(value: string): void;
}

declare class vCACCAFEApprovalLevelStatusInfo {
	constructor();
	constructor();
	getRequest(): vCACCAFEApprovalRequestStatusInfo[];
	getId(): string;
	getState(): vCACCAFEEvaluationState;
	/**
	 * @param value 
	 */
	setStartTime(value: any): void;
	getStartTime(): any;
	/**
	 * @param value 
	 */
	setState(value: vCACCAFEEvaluationState): void;
	/**
	 * @param value 
	 */
	setLevel(value: vCACCAFEApprovalDescriptiveReference): void;
	getLevel(): vCACCAFEApprovalDescriptiveReference;
	/**
	 * @param value 
	 */
	setMode(value: vCACCAFEApprovalMode): void;
	getMode(): vCACCAFEApprovalMode;
	getCompletionTime(): any;
	/**
	 * @param value 
	 */
	setCompletionTime(value: any): void;
	getCurrentApprover(): vCACCAFEApprovalPrincipal[];
	/**
	 * @param value 
	 */
	setId(value: string): void;
}

declare interface vCACCAFEApprovalMode {
	value(): string;
	values(): vCACCAFEApprovalMode[];
	/**
	 * @param name 
	 */
	valueOf(name: string): vCACCAFEApprovalMode;
	/**
	 * @param v 
	 */
	fromValue(v: string): vCACCAFEApprovalMode;
}

declare class vCACCAFEApprovalPhaseType {
	constructor();
	constructor();
	getForms(): vCACCAFEApprovalForms;
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	getPhaseOrder(): number;
	/**
	 * @param value 
	 */
	setPhaseOrder(value: number): void;
	isAllowUpdates(): boolean;
	/**
	 * @param value 
	 */
	setAllowUpdates(value: boolean): void;
	/**
	 * @param value 
	 */
	setForms(value: vCACCAFEApprovalForms): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
}

declare class vCACCAFEApprovalPolicy {
	lastModified: any;
	version: number;
	phases: vCACCAFEPhase[];
	name: string;
	id: string;
	state: vCACCAFEApprovalPolicyState;
	description: string;
	policyType: vCACCAFEApprovalPolicyType;
	stateName: string;
	approvableItemId: string;
	typeFilter: string;
	approvableItemName: string;
	approvableItemServiceTypeId: string;
	createdBy: string;
	lastModifiedBy: string;
	vcoId: any;
	createdDate: any;
	constructor();
	constructor();
	getLastModified(): any;
	getVersion(): number;
	getPhases(): vCACCAFEPhase[];
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	getState(): vCACCAFEApprovalPolicyState;
	/**
	 * @param value 
	 */
	setLastModified(value: any): void;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	/**
	 * @param value 
	 */
	setVersion(value: number): void;
	/**
	 * @param value 
	 */
	setState(value: vCACCAFEApprovalPolicyState): void;
	getPolicyType(): vCACCAFEApprovalPolicyType;
	/**
	 * @param value 
	 */
	setPolicyType(value: vCACCAFEApprovalPolicyType): void;
	getStateName(): string;
	/**
	 * @param value 
	 */
	setStateName(value: string): void;
	getApprovableItemId(): string;
	/**
	 * @param value 
	 */
	setApprovableItemId(value: string): void;
	getTypeFilter(): string;
	/**
	 * @param value 
	 */
	setTypeFilter(value: string): void;
	getApprovableItemName(): string;
	/**
	 * @param value 
	 */
	setApprovableItemName(value: string): void;
	getApprovableItemServiceTypeId(): string;
	/**
	 * @param value 
	 */
	setApprovableItemServiceTypeId(value: string): void;
	getCreatedBy(): string;
	/**
	 * @param value 
	 */
	setCreatedBy(value: string): void;
	getLastModifiedBy(): string;
	/**
	 * @param value 
	 */
	setLastModifiedBy(value: string): void;
	getVcoId(): any;
	/**
	 * @param undefined 
	 */
	setVcoId(id: any): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	/**
	 * @param value 
	 */
	setCreatedDate(value: any): void;
	getCreatedDate(): any;
}

declare interface vCACCAFEApprovalPolicyEvaluationService {
	/**
	 * This method invokes the SDK method PolicyEvaluationServiceImpl.evaluatePolicy(ApprovalPolicyEvaluationSpec evalSpec).
	 * @param evalSpec 
	 */
	evaluatePolicy(evalSpec: any): any;
	/**
	 * This method invokes the SDK method PolicyEvaluationServiceImpl.retrieveApproval(String id).
	 * @param id 
	 */
	retrieveApproval(id: string): any;
}

declare class vCACCAFEApprovalPolicyEvaluationSpec {
	constructor();
	constructor();
	getContext(): any;
	/**
	 * @param value 
	 */
	setContext(value: any): void;
	getPolicyId(): string;
	/**
	 * @param value 
	 */
	setPolicyId(value: string): void;
	getPhaseId(): string;
	/**
	 * @param value 
	 */
	setPhaseId(value: string): void;
	getRequestingServiceId(): string;
	/**
	 * @param value 
	 */
	setRequestingServiceId(value: string): void;
	getRequestClassId(): string;
	/**
	 * @param value 
	 */
	setRequestClassId(value: string): void;
	getRequestInstanceId(): string;
	/**
	 * @param value 
	 */
	setRequestInstanceId(value: string): void;
	getRequestRef(): string;
	/**
	 * @param value 
	 */
	setRequestRef(value: string): void;
	getRequestedItemName(): string;
	/**
	 * @param value 
	 */
	setRequestedItemName(value: string): void;
	getRequestedItemDescription(): string;
	/**
	 * @param value 
	 */
	setRequestedItemDescription(value: string): void;
	getRequestedFor(): string;
	getSubTenantId(): string;
	/**
	 * @param value 
	 */
	setSubTenantId(value: string): void;
	getRequestDescription(): string;
	/**
	 * @param value 
	 */
	setRequestDescription(value: string): void;
	getRequestReasons(): string;
	/**
	 * @param value 
	 */
	setRequestReasons(value: string): void;
	getCosts(): vCACCAFEApprovalRequestCosts;
	/**
	 * @param value 
	 */
	setCosts(value: vCACCAFEApprovalRequestCosts): void;
	getRequestedBy(): string;
	/**
	 * @param value 
	 */
	setRequestedBy(value: string): void;
	/**
	 * @param value 
	 */
	setRequestedFor(value: string): void;
}

declare interface vCACCAFEApprovalPolicyState {
	value(): string;
	values(): vCACCAFEApprovalPolicyState[];
	/**
	 * @param name 
	 */
	valueOf(name: string): vCACCAFEApprovalPolicyState;
	/**
	 * @param v 
	 */
	fromValue(v: string): vCACCAFEApprovalPolicyState;
}

declare class vCACCAFEApprovalPolicyType {
	constructor();
	constructor();
	toString(): string;
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	getPhaseTypes(): vCACCAFEApprovalPhaseType[];
	getClassId(): string;
	/**
	 * @param value 
	 */
	setClassId(value: string): void;
	getTypeFilter(): string;
	/**
	 * @param value 
	 */
	setTypeFilter(value: string): void;
	/**
	 * @param value 
	 */
	setServiceTypeId(value: string): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	getServiceTypeId(): string;
}

declare class vCACCAFEApprovalPolicyUsage {
	constructor();
	constructor();
	getObjectId(): string;
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	getClassId(): string;
	/**
	 * @param value 
	 */
	setClassId(value: string): void;
	/**
	 * @param value 
	 */
	setObjectId(value: string): void;
	getSubtenantName(): string;
	/**
	 * @param value 
	 */
	setSubtenantName(value: string): void;
	/**
	 * @param value 
	 */
	setSubtenantId(value: string): void;
	getSubtenantId(): string;
}

declare class vCACCAFEApprovalPrincipal {
	constructor();
	constructor();
	/**
	 * @param value 
	 */
	setDisplayName(value: string): void;
	getValue(): string;
	/**
	 * @param value 
	 */
	setValue(value: string): void;
	getType(): vCACCAFEApprovalPrincipalType;
	getDisplayName(): string;
	/**
	 * @param value 
	 */
	setType(value: vCACCAFEApprovalPrincipalType): void;
}

declare interface vCACCAFEApprovalPrincipalType {
	value(): string;
	values(): vCACCAFEApprovalPrincipalType[];
	/**
	 * @param name 
	 */
	valueOf(name: string): vCACCAFEApprovalPrincipalType;
	/**
	 * @param v 
	 */
	fromValue(v: string): vCACCAFEApprovalPrincipalType;
}

declare class vCACCAFEApprovalRequest {
	constructor();
	constructor();
	/**
	 * @param value 
	 */
	setCreationTime(value: any): void;
	getCreationTime(): any;
	getVersion(): number;
	getAction(): vCACCAFEUserAction;
	getId(): string;
	getState(): vCACCAFEApprovalRequestState;
	/**
	 * @param value 
	 */
	setVersion(value: number): void;
	/**
	 * @param value 
	 */
	setState(value: vCACCAFEApprovalRequestState): void;
	getTenantId(): string;
	/**
	 * @param value 
	 */
	setAction(value: vCACCAFEUserAction): void;
	getCompletedBy(): string;
	/**
	 * @param value 
	 */
	setCompletedBy(value: string): void;
	getCompletionTime(): any;
	/**
	 * @param value 
	 */
	setCompletionTime(value: any): void;
	getApprovers(): vCACCAFEApprovalPrincipal[];
	getAssignTime(): any;
	/**
	 * @param value 
	 */
	setAssignTime(value: any): void;
	getWorkItemId(): string;
	getWorkItemNumber(): number;
	/**
	 * @param value 
	 */
	setWorkItemNumber(value: number): void;
	getWiqServiceId(): string;
	/**
	 * @param value 
	 */
	setWiqServiceId(value: string): void;
	getBusinessJustification(): string;
	/**
	 * @param value 
	 */
	setBusinessJustification(value: string): void;
	/**
	 * @param value 
	 */
	setWorkItemId(value: string): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	/**
	 * @param value 
	 */
	setTenantId(value: string): void;
}

declare class vCACCAFEApprovalRequestCosts {
	constructor();
	constructor();
	getLeaseForDisplay(): vCACCAFETimeSpan;
	getLease(): vCACCAFETimeSpan;
	/**
	 * @param value 
	 */
	setLease(value: vCACCAFETimeSpan): void;
	/**
	 * @param value 
	 */
	setLeaseForDisplay(value: vCACCAFETimeSpan): void;
	getLeaseRate(): vCACCAFEMoneyTimeRate;
	/**
	 * @param value 
	 */
	setLeaseRate(value: vCACCAFEMoneyTimeRate): void;
	getTotalLeaseCost(): any;
	/**
	 * @param value 
	 */
	setTotalLeaseCost(value: any): void;
	isQuoteProvided(): boolean;
	/**
	 * @param value 
	 */
	setQuoteProvided(value: boolean): void;
}

declare class vCACCAFEApprovalRequestInfo {
	constructor();
	constructor();
	getId(): string;
	getState(): vCACCAFEApprovalRequestState;
	/**
	 * @param value 
	 */
	setState(value: vCACCAFEApprovalRequestState): void;
	/**
	 * @param value 
	 */
	setLevel(value: vCACCAFEApprovalDescriptiveReference): void;
	getLevel(): vCACCAFEApprovalDescriptiveReference;
	getTenantId(): string;
	getWorkItem(): vCACCAFEApprovalWorkItemInfo;
	getApproval(): vCACCAFEApprovalStatusInfo;
	/**
	 * @param value 
	 */
	setApproval(value: vCACCAFEApprovalStatusInfo): void;
	getApprover(): vCACCAFEApprovalPrincipal[];
	/**
	 * @param value 
	 */
	setWorkItem(value: vCACCAFEApprovalWorkItemInfo): void;
	getBusinessJustification(): string;
	/**
	 * @param value 
	 */
	setBusinessJustification(value: string): void;
	getCompletionAction(): vCACCAFEUserAction;
	/**
	 * @param value 
	 */
	setCompletionAction(value: vCACCAFEUserAction): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	/**
	 * @param value 
	 */
	setTenantId(value: string): void;
}

declare interface vCACCAFEApprovalRequestState {
	value(): string;
	values(): vCACCAFEApprovalRequestState[];
	/**
	 * @param name 
	 */
	valueOf(name: string): vCACCAFEApprovalRequestState;
	/**
	 * @param v 
	 */
	fromValue(v: string): vCACCAFEApprovalRequestState;
}

declare class vCACCAFEApprovalRequestStatusInfo {
	constructor();
	constructor();
	getId(): string;
	getState(): vCACCAFEApprovalRequestState;
	/**
	 * @param value 
	 */
	setState(value: vCACCAFEApprovalRequestState): void;
	getCompletedBy(): string;
	/**
	 * @param value 
	 */
	setCompletedBy(value: string): void;
	getAssignDate(): any;
	/**
	 * @param value 
	 */
	setAssignDate(value: any): void;
	getCompletionDate(): any;
	/**
	 * @param value 
	 */
	setCompletionDate(value: any): void;
	getApprovers(): vCACCAFEApprovalPrincipal[];
	getWorkItemId(): string;
	getWorkItemNumber(): number;
	/**
	 * @param value 
	 */
	setWorkItemNumber(value: number): void;
	getBusinessJustification(): string;
	/**
	 * @param value 
	 */
	setBusinessJustification(value: string): void;
	getCompletionAction(): vCACCAFEUserAction;
	/**
	 * @param value 
	 */
	setCompletionAction(value: vCACCAFEUserAction): void;
	/**
	 * @param value 
	 */
	setWorkItemId(value: string): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
}

declare class vCACCAFEApprovalStateChangeEvent {
	constructor();
	constructor();
	getNewState(): vCACCAFEEvaluationState;
	/**
	 * @param value 
	 */
	setNewState(value: vCACCAFEEvaluationState): void;
	getEventType(): vCACCAFEEvaluationEventType;
	/**
	 * @param value 
	 */
	setEventType(value: vCACCAFEEvaluationEventType): void;
	getApprovalId(): string;
	/**
	 * @param value 
	 */
	setApprovalId(value: string): void;
}

declare interface vCACCAFEApprovalStatus {
	value(): string;
	values(): vCACCAFEApprovalStatus[];
	/**
	 * @param name 
	 */
	valueOf(name: string): vCACCAFEApprovalStatus;
	/**
	 * @param v 
	 */
	fromValue(v: string): vCACCAFEApprovalStatus;
}

declare class vCACCAFEApprovalStatusInfo {
	constructor();
	constructor();
	/**
	 * @param value 
	 */
	setPhase(value: vCACCAFEApprovalDescriptiveReference): void;
	getId(): string;
	getState(): vCACCAFEEvaluationState;
	getPolicy(): vCACCAFEApprovalDescriptiveReference;
	/**
	 * @param value 
	 */
	setStartTime(value: any): void;
	getStartTime(): any;
	/**
	 * @param value 
	 */
	setState(value: vCACCAFEEvaluationState): void;
	getPolicyType(): vCACCAFEApprovalDescriptiveReference;
	/**
	 * @param value 
	 */
	setPolicyType(value: vCACCAFEApprovalDescriptiveReference): void;
	getSpec(): vCACCAFEApprovalPolicyEvaluationSpec;
	/**
	 * @param value 
	 */
	setSpec(value: vCACCAFEApprovalPolicyEvaluationSpec): void;
	getCompletionTime(): any;
	/**
	 * @param value 
	 */
	setCompletionTime(value: any): void;
	isHasSourceDetailsForm(): boolean;
	/**
	 * @param value 
	 */
	setHasSourceDetailsForm(value: boolean): void;
	getPhase(): vCACCAFEApprovalDescriptiveReference;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	/**
	 * @param value 
	 */
	setPolicy(value: vCACCAFEApprovalDescriptiveReference): void;
}

declare class vCACCAFEApprovalUpdateRequest {
	constructor();
	constructor();
	getData(): vCACCAFELiteralMap;
	getUpdatedAt(): any;
	/**
	 * @param value 
	 */
	setUpdatedAt(value: any): void;
	getUpdatedBy(): string;
	/**
	 * @param value 
	 */
	setUpdatedBy(value: string): void;
	/**
	 * @param value 
	 */
	setData(value: vCACCAFELiteralMap): void;
}

declare class vCACCAFEApprovalWorkItemInfo {
	constructor();
	constructor();
	getId(): string;
	getNumber(): number;
	getStatus(): string;
	getAssignedDate(): any;
	/**
	 * @param value 
	 */
	setAssignedDate(value: any): void;
	getAssignedTo(): vCACCAFEApprovalPrincipal[];
	getCompletedDate(): any;
	/**
	 * @param value 
	 */
	setCompletedDate(value: any): void;
	getCompletedBy(): string;
	/**
	 * @param value 
	 */
	setCompletedBy(value: string): void;
	/**
	 * @param value 
	 */
	setNumber(value: number): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	/**
	 * @param value 
	 */
	setStatus(value: string): void;
	/**
	 * @param value 
	 */
	setCreatedDate(value: any): void;
	getCreatedDate(): any;
}

declare class vCACCAFEAsset {
	constructor();
	constructor();
	getId(): string;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	/**
	 * @param value 
	 */
	setAssetInfo(value: vCACCAFEAssetInfo): void;
	getAssetInfo(): vCACCAFEAssetInfo;
}

declare class vCACCAFEAssetInfo {
	constructor();
	constructor();
	getScopeId(): string;
	/**
	 * @param value 
	 */
	setScopeId(value: string): void;
	getProduct(): vCACCAFEProduct;
	/**
	 * @param value 
	 */
	setProduct(value: vCACCAFEProduct): void;
	getInstanceId(): string;
	/**
	 * @param value 
	 */
	setInstanceId(value: string): void;
}

declare class vCACCAFEAssetMapper {
	constructor();
	constructor();
}

declare class vCACCAFEAssociation {
	constructor();
	constructor();
	getVersion(): number;
	getId(): string;
	/**
	 * @param value 
	 */
	setVersion(value: number): void;
	/**
	 * @param value 
	 */
	setCustomProperties(value: string): void;
	getCustomProperties(): string;
	getAssociationTypeInfoId(): string;
	getFromEndpointId(): string;
	getToEndpointId(): string;
	getLastUpdated(): any;
	/**
	 * @param value 
	 */
	setLastUpdated(value: any): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	/**
	 * @param value 
	 */
	setCreatedDate(value: any): void;
	/**
	 * @param value 
	 */
	setAssociationTypeInfoId(value: string): void;
	/**
	 * @param value 
	 */
	setFromEndpointId(value: string): void;
	/**
	 * @param value 
	 */
	setToEndpointId(value: string): void;
	getCreatedDate(): any;
}

declare class vCACCAFEAssociationType {
	version: number;
	cardinality: number;
	name: string;
	id: string;
	description: string;
	vcoId: any;
	lastUpdated: any;
	fromType: string;
	toType: string;
	createdDate: any;
	constructor();
	constructor();
	getVersion(): number;
	/**
	 * @param value 
	 */
	setCardinality(value: number): void;
	getCardinality(): number;
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	/**
	 * @param value 
	 */
	setVersion(value: number): void;
	getVcoId(): any;
	/**
	 * @param undefined 
	 */
	setVcoId(id: any): void;
	/**
	 * @param value 
	 */
	setFromType(value: string): void;
	/**
	 * @param value 
	 */
	setToType(value: string): void;
	getLastUpdated(): any;
	/**
	 * @param value 
	 */
	setLastUpdated(value: any): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	/**
	 * @param value 
	 */
	setCreatedDate(value: any): void;
	getFromType(): string;
	getToType(): string;
	getCreatedDate(): any;
}

declare class vCACCAFEAttribute {
	constructor();
	constructor();
	getValue(): string;
	getKey(): string;
	getId(): string;
	/**
	 * @param value 
	 */
	setValue(value: string): void;
	/**
	 * @param value 
	 */
	setKey(value: string): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
}

declare interface vCACCAFEAuthType {
	value(): string;
	values(): vCACCAFEAuthType[];
	/**
	 * @param name 
	 */
	valueOf(name: string): vCACCAFEAuthType;
	/**
	 * @param v 
	 */
	fromValue(v: string): vCACCAFEAuthType;
}

declare interface vCACCAFEAuthenticationClassId {
	value(): string;
	values(): vCACCAFEAuthenticationClassId[];
	/**
	 * @param name 
	 */
	valueOf(name: string): vCACCAFEAuthenticationClassId;
	/**
	 * @param v 
	 */
	fromValue(v: string): vCACCAFEAuthenticationClassId;
}

declare interface vCACCAFEAuthenticationClient {
	getAuthenticationRelyingPartyService(): vCACCAFEAuthenticationRelyingPartyService;
	getAuthenticationGroupService(): vCACCAFEAuthenticationGroupService;
	getAuthenticationPrincipalService(): vCACCAFEAuthenticationPrincipalService;
	getAuthenticationTenantService(): vCACCAFEAuthenticationTenantService;
	getAuthenticationIdentityStoreClientService(): vCACCAFEAuthenticationIdentityStoreClientService;
	getAuthenticationSubtenantService(): vCACCAFEAuthenticationSubtenantService;
	/**
	 * Does a POST request expanding the given template variables in order.
	 * @param resourceUrl 
	 * @param object 
	 * @param urlVariables 
	 */
	postWithVariables(resourceUrl: string, object: any, urlVariables: string[]): vCACCAFEServiceResponse;
	/**
	 * Does a PUT request expanding the given template variables in order.
	 * @param resourceUrl 
	 * @param object 
	 * @param urlVariables 
	 */
	putWithVariables(resourceUrl: string, object: any, urlVariables: string[]): vCACCAFEServiceResponse;
	/**
	 * Does a DELETE request expanding the given template variables in order.
	 * @param resourceUrl 
	 * @param urlVariables 
	 */
	deleteWithVariables(resourceUrl: string, urlVariables: string[]): void;
	/**
	 * Gets the REST client URL.
	 */
	getUrl(): string;
	/**
	 * Indicates whether the current connection is already closed or not.
	 */
	isClosed(): boolean;
	/**
	 * Does a GET request for a file and allows specifying 'Accept' header.
	 * @param resourceUrl 
	 * @param acceptHeaderValue 
	 */
	getFileWithAcceptHeader(resourceUrl: string, acceptHeaderValue: string): any;
	/**
	 * Does a POST request with a file.
	 * @param resourceUrl 
	 * @param object 
	 */
	postFile(resourceUrl: string, object: any): vCACCAFEServiceResponse;
	/**
	 * Does a GET request expanding the given template variables in order.
	 * @param resourceUrl 
	 * @param urlVariables 
	 */
	getWithVariables(resourceUrl: string, urlVariables: string[]): any;
	/**
	 * Does a GET request.
	 * @param resourceUrl 
	 */
	get(resourceUrl: string): any;
	/**
	 * Does a PUT request.
	 * @param resourceUrl 
	 * @param object 
	 */
	put(resourceUrl: string, object: any): vCACCAFEServiceResponse;
	/**
	 * Does a DELETE request.
	 * @param resourceUrl 
	 */
	delete(resourceUrl: string): void;
	/**
	 * Invalidates the current connection.
	 */
	close(): void;
	/**
	 * Does a GET request for a file.
	 * @param resourceUrl 
	 */
	getFile(resourceUrl: string): any;
	/**
	 * Does a POST request.
	 * @param resourceUrl 
	 * @param object 
	 */
	post(resourceUrl: string, object: any): vCACCAFEServiceResponse;
}

declare interface vCACCAFEAuthenticationGroupService {
	/**
	 * This method invokes the SDK method GroupService.getGroups(String tenantName, String criteria).
	 * @param tenantName 
	 * @param criteria 
	 */
	getGroupsByCriteria(tenantName: string, criteria: string): vCACCAFEGroup[];
	/**
	 * This method invokes the SDK method GroupService.getGroups(String tenantName, String criteria, String domain).
	 * @param tenantName 
	 * @param criteria 
	 * @param domain 
	 */
	getGroupsByCriteriaAndDomain(tenantName: string, criteria: string, domain: string): vCACCAFEGroup[];
	/**
	 * This method invokes the SDK method GroupService.getGroups(String tenantName, String criteria, String domain, String parentGroup, Type groupType).
	 * @param tenantName 
	 * @param criteria 
	 * @param domain 
	 * @param parentGroup 
	 * @param groupType 
	 */
	getGroupsByCriteriaAndDomainAndParentGroupAndGroupType(tenantName: string, criteria: string, domain: string, parentGroup: string, groupType: vCACCAFEType): vCACCAFEGroup[];
	/**
	 * This method invokes the SDK method GroupService.getUsersInGroup(String tenantName, Group group, String criteria, Pageable pageable).
	 * @param tenantName 
	 * @param group 
	 * @param criteria 
	 * @param pageable 
	 */
	getUsersInGroup(tenantName: string, group: vCACCAFEGroup, criteria: string, pageable: any): vCACCAFEUser[];
	/**
	 * This method invokes the SDK method GroupService.replaceGroupsAndUsersInCustomGroup(String tenantName, Group parentGroup, List<Group> groups, List<User> users).
	 * @param tenantName 
	 * @param parentGroup 
	 * @param groups 
	 * @param users 
	 */
	replaceGroupsAndUsersInCustomGroup(tenantName: string, parentGroup: vCACCAFEGroup, groups: vCACCAFEGroup[], users: vCACCAFEUser[]): void;
	/**
	 * This method invokes the SDK method GroupService.saveCustomGroup(String tenantName, Group group).
	 * @param tenantName 
	 * @param group 
	 */
	saveCustomGroup(tenantName: string, group: vCACCAFEGroup): any;
	/**
	 * This method invokes the SDK method GroupService.updateCustomGroup(String tenantName, Group customGroup).
	 * @param tenantName 
	 * @param customGroup 
	 */
	updateCustomGroup(tenantName: string, customGroup: vCACCAFEGroup): void;
	/**
	 * This method invokes the SDK method GroupService.assignGroupsAndUsersToCustomGroup(String tenantName, Group parentGroup, List<Group> groups, List<User> users).
	 * @param tenantName 
	 * @param parentGroup 
	 * @param groups 
	 * @param users 
	 */
	assignGroupsAndUsersToCustomGroup(tenantName: string, parentGroup: vCACCAFEGroup, groups: vCACCAFEGroup[], users: vCACCAFEUser[]): any;
	/**
	 * This method invokes the SDK method GroupService.assignUserToGroup(String tenantName, User user, Group parentGroup).
	 * @param tenantName 
	 * @param user 
	 * @param parentGroup 
	 */
	assignUserToGroup(tenantName: string, user: vCACCAFEUser, parentGroup: vCACCAFEGroup): any;
	/**
	 * This method invokes the SDK method GroupService.deleteCustomGroup(String tenantName, Group customGroup).
	 * @param tenantName 
	 * @param customGroup 
	 */
	deleteCustomGroup(tenantName: string, customGroup: vCACCAFEGroup): void;
	/**
	 * This method invokes the SDK method GroupService.deleteSsoGroupFromGroup(String tenantName, Group customGroup, Group ssoGroup).
	 * @param tenantName 
	 * @param customGroup 
	 * @param ssoGroup 
	 */
	deleteSsoGroupFromGroup(tenantName: string, customGroup: vCACCAFEGroup, ssoGroup: vCACCAFEGroup): void;
	/**
	 * This method invokes the SDK method GroupService.deleteUserFromGroup(String tenantName, Group customGroup, User user).
	 * @param tenantName 
	 * @param customGroup 
	 * @param user 
	 */
	deleteUserFromGroup(tenantName: string, customGroup: vCACCAFEGroup, user: vCACCAFEUser): void;
	/**
	 * This method invokes the SDK method GroupService.getGroupParents(String tenantName, Group group, Pageable pageable).
	 * @param tenantName 
	 * @param group 
	 * @param pageable 
	 */
	getGroupParents(tenantName: string, group: vCACCAFEGroup, pageable: any): vCACCAFEGroup[];
	/**
	 * This method invokes the SDK method GroupService.getGroup(String tenantName, PrincipalId principalId).
	 * @param tenantName 
	 * @param principalId 
	 */
	getGroupByPrincipalId(tenantName: string, principalId: vCACCAFEPrincipalId): vCACCAFEGroup;
	/**
	 * This method invokes the SDK method GroupService.getGroupsByType(String tenantName, Type groupType, Pageable pageable).
	 * @param tenantName 
	 * @param groupType 
	 * @param pageable 
	 */
	getGroupsByType(tenantName: string, groupType: vCACCAFEType, pageable: any): vCACCAFEGroup[];
	/**
	 * This method invokes the SDK method GroupService.getGroups(GroupSearchCriteria groupSearch).
	 * @param groupSearch 
	 */
	getGroupsByGroupSearchCriteria(groupSearch: vCACCAFEGroupSearchCriteria): vCACCAFEGroup[];
	/**
	 * This method invokes the SDK method GroupService.getGroups(String tenantName, String criteria, String domain, String parentGroup).
	 * @param tenantName 
	 * @param criteria 
	 * @param domain 
	 * @param parentGroup 
	 */
	getGroupsByCriteriaAndDomainAndParentGroup(tenantName: string, criteria: string, domain: string, parentGroup: string): vCACCAFEGroup[];
	/**
	 * This method invokes the SDK method GroupService.getGroup(String tenantName, String principalId).
	 * @param tenantName 
	 * @param principalId 
	 */
	getGroup(tenantName: string, principalId: string): vCACCAFEGroup;
	/**
	 * This method invokes the SDK method GroupService.getGroups(String tenantName).
	 * @param tenantName 
	 */
	getGroups(tenantName: string): vCACCAFEGroup[];
}

declare interface vCACCAFEAuthenticationIdentityStoreClientService {
	/**
	 * This method invokes the SDK method IdentityStoreClientServiceImpl.createIdentityStore(String tenantId, IdentityStore identityStore).
	 * @param tenantId 
	 * @param identityStore 
	 */
	createIdentityStore(tenantId: string, identityStore: vCACCAFEIdentityStore): any;
	/**
	 * This method invokes the SDK method IdentityStoreClientServiceImpl.deleteIdentityStore(String tenantId, String identityStore).
	 * @param tenantId 
	 * @param identityStore 
	 */
	deleteIdentityStore(tenantId: string, identityStore: string): void;
	/**
	 * This method invokes the SDK method IdentityStoreClientServiceImpl.getIdentityStoreStatus(String tenantId, String identityStore).
	 * @param tenantId 
	 * @param identityStore 
	 */
	getIdentityStoreStatus(tenantId: string, identityStore: string): vCACCAFEIdentityStoreStatus;
	/**
	 * This method invokes the SDK method IdentityStoreClientServiceImpl.getIdentityStore(URI uri).
	 * @param uri 
	 */
	getIdentityStoreByUri(uri: any): vCACCAFEIdentityStore;
	/**
	 * This method invokes the SDK method IdentityStoreClientServiceImpl.updateOrCreateIdentityStore(String tenantId, IdentityStore identityStore).
	 * @param tenantId 
	 * @param identityStore 
	 */
	updateOrCreateIdentityStore(tenantId: string, identityStore: vCACCAFEIdentityStore): void;
	/**
	 * This method invokes the SDK method IdentityStoreClientServiceImpl.getIdentityStores(String tenantId, Pageable pageable).
	 * @param tenantId 
	 * @param pageable 
	 */
	getIdentityStores(tenantId: string, pageable: any): vCACCAFEIdentityStore[];
	/**
	 * This method invokes the SDK method IdentityStoreClientServiceImpl.getIdentityStore(String tenantId, String identityStoreId).
	 * @param tenantId 
	 * @param identityStoreId 
	 */
	getIdentityStore(tenantId: string, identityStoreId: string): vCACCAFEIdentityStore;
}

declare interface vCACCAFEAuthenticationPrincipalService {
	/**
	 * This method invokes the SDK method PrincipalService.deleteLocalUser(String tenant, PrincipalId user).
	 * @param tenant 
	 * @param user 
	 */
	deleteLocalUser(tenant: string, user: vCACCAFEPrincipalId): void;
	/**
	 * This method invokes the SDK method PrincipalService.getLocalUsers(String tenantName).
	 * @param tenantName 
	 */
	getLocalUsers(tenantName: string): vCACCAFEUser[];
	/**
	 * This method invokes the SDK method PrincipalService.getPrincipal(String tenantName, PrincipalId principalId).
	 * @param tenantName 
	 * @param principalId 
	 */
	getPrincipalByPrincipalId(tenantName: string, principalId: vCACCAFEPrincipalId): vCACCAFEUser;
	/**
	 * This method invokes the SDK method PrincipalService.getUserParentGroupsByType(String tenantName, PrincipalId principalId, Type groupType, Pageable pageable).
	 * @param tenantName 
	 * @param principalId 
	 * @param groupType 
	 * @param pageable 
	 */
	getUserParentGroupsByType(tenantName: string, principalId: vCACCAFEPrincipalId, groupType: vCACCAFEType, pageable: any): vCACCAFEGroup[];
	/**
	 * This method invokes the SDK method PrincipalService.getUserParentGroups(String tenantName, PrincipalId principalId, Pageable pageable).
	 * @param tenantName 
	 * @param principalId 
	 * @param pageable 
	 */
	getUserParentGroups(tenantName: string, principalId: vCACCAFEPrincipalId, pageable: any): vCACCAFEGroup[];
	/**
	 * This method invokes the SDK method PrincipalService.getUserParentGroups(String tenantName, User user, Pageable pageable).
	 * @param tenantName 
	 * @param user 
	 * @param pageable 
	 */
	getUserParentGroupsByUser(tenantName: string, user: vCACCAFEUser, pageable: any): vCACCAFEGroup[];
	/**
	 * This method invokes the SDK method PrincipalService.loadPrincipals(String tenantId, List<PrincipalId> principalIds).
	 * @param tenantId 
	 * @param principalIds 
	 */
	loadPrincipals(tenantId: string, principalIds: vCACCAFEPrincipalId[]): vCACCAFEPrincipalValidationWrapper[];
	/**
	 * This method invokes the SDK method PrincipalService.updateLocalUser(User user).
	 * @param user 
	 */
	updateLocalUser(user: vCACCAFEUser): void;
	/**
	 * This method invokes the SDK method PrincipalService.createLocalUser(User user).
	 * @param user 
	 */
	createLocalUser(user: vCACCAFEUser): any;
	/**
	 * This method invokes the SDK method PrincipalService.getPrincipals(PrincipalSearchCriteria searchCriteria, Pageable pageable).
	 * @param searchCriteria 
	 * @param pageable 
	 */
	getPrincipalsByPrincipalSearchCriteriaPageable(searchCriteria: vCACCAFEPrincipalSearchCriteria, pageable: any): vCACCAFEUser[];
	/**
	 * This method invokes the SDK method PrincipalService.getPrincipals(String tenantName, String criteria).
	 * @param tenantName 
	 * @param criteria 
	 */
	getPrincipalsByCriteria(tenantName: string, criteria: string): vCACCAFEUser[];
	/**
	 * This method invokes the SDK method PrincipalService.getPrincipals(String tenantName, String criteria, String domain).
	 * @param tenantName 
	 * @param criteria 
	 * @param domain 
	 */
	getPrincipalsByCriteriaAndDomain(tenantName: string, criteria: string, domain: string): vCACCAFEUser[];
	/**
	 * This method invokes the SDK method PrincipalService.getPrincipals(String tenantName, String criteria, String domain, String parentGroup).
	 * @param tenantName 
	 * @param criteria 
	 * @param domain 
	 * @param parentGroup 
	 */
	getPrincipalsByCriteriaAndDomainAndParentGroup(tenantName: string, criteria: string, domain: string, parentGroup: string): vCACCAFEUser[];
	/**
	 * This method invokes the SDK method PrincipalService.getPrincipals(String tenantName, String criteria, String domain, String parentGroup, Pageable pageable).
	 * @param tenantName 
	 * @param criteria 
	 * @param domain 
	 * @param parentGroup 
	 * @param pageable 
	 */
	getPrincipalsByCriteriaAndDomainAndParentGroupPageable(tenantName: string, criteria: string, domain: string, parentGroup: string, pageable: any): vCACCAFEUser[];
	/**
	 * This method invokes the SDK method PrincipalService.getPrincipals(String tenantName, String criteria, String domain, String parentGroup, Pageable pageable, boolean expandGroups).
	 * @param tenantName 
	 * @param criteria 
	 * @param domain 
	 * @param parentGroup 
	 * @param pageable 
	 * @param expandGroups 
	 */
	getPrincipalsByCriteriaAndDomainAndParentGroupPageableExpandGroups(tenantName: string, criteria: string, domain: string, parentGroup: string, pageable: any, expandGroups: boolean): vCACCAFEUser[];
	/**
	 * This method invokes the SDK method PrincipalService.getPrincipals(String tenantName, String criteria, String domain, String parentGroup, Pageable pageable, boolean expandGroups, boolean localUsersOnly).
	 * @param tenantName 
	 * @param criteria 
	 * @param domain 
	 * @param parentGroup 
	 * @param pageable 
	 * @param expandGroups 
	 * @param localUsersOnly 
	 */
	getPrincipalsByTenantNameCriteriaDomainParentGroupPageableExpandGroupsLocalUsersOnly(tenantName: string, criteria: string, domain: string, parentGroup: string, pageable: any, expandGroups: boolean, localUsersOnly: boolean): vCACCAFEUser[];
	/**
	 * This method invokes the SDK method PrincipalService.getPrincipals(PrincipalSearchCriteria searchCriteria).
	 * @param searchCriteria 
	 */
	getPrincipalsByPrincipalSearchCriteria(searchCriteria: vCACCAFEPrincipalSearchCriteria): vCACCAFEUser[];
	/**
	 * This method invokes the SDK method PrincipalService.getPrincipals(String tenantName).
	 * @param tenantName 
	 */
	getPrincipals(tenantName: string): vCACCAFEUser[];
	/**
	 * This method invokes the SDK method PrincipalService.getPrincipal(String tenantName, String principalId).
	 * @param tenantName 
	 * @param principalId 
	 */
	getPrincipal(tenantName: string, principalId: string): vCACCAFEUser;
}

declare interface vCACCAFEAuthenticationRelyingPartyService {
	/**
	 * This method invokes the SDK method RelyingPartyService.register(String tenant, RelyingParty relyingParty).
	 * @param tenant 
	 * @param relyingParty 
	 */
	register(tenant: string, relyingParty: vCACCAFERelyingParty): void;
}

declare interface vCACCAFEAuthenticationSubtenantService {
	/**
	 * This method invokes the SDK method SubtenantService.addPrincipals(String tenantId, String subtenantId, String roleRef, List<PrincipalId> principals).
	 * @param tenantId 
	 * @param subtenantId 
	 * @param roleRef 
	 * @param principals 
	 */
	addPrincipals(tenantId: string, subtenantId: string, roleRef: string, principals: vCACCAFEPrincipalId[]): any;
	/**
	 * This method invokes the SDK method SubtenantService.findSubtenantsByPrincipal(String tenantId, PrincipalId principalId, Pageable pageable).
	 * @param tenantId 
	 * @param principalId 
	 * @param pageable 
	 */
	findSubtenantsByPrincipal(tenantId: string, principalId: vCACCAFEPrincipalId, pageable: any): vCACCAFESubtenant[];
	/**
	 * This method invokes the SDK method SubtenantService.getSubtenantsByPrincipalAndRole(String tenantId, PrincipalId principalId, String roleRef, boolean expandGroups, Pageable pageable).
	 * @param tenantId 
	 * @param principalId 
	 * @param roleRef 
	 * @param expandGroups 
	 * @param pageable 
	 */
	getSubtenantsByPrincipalAndRole(tenantId: string, principalId: vCACCAFEPrincipalId, roleRef: string, expandGroups: boolean, pageable: any): vCACCAFESubtenant[];
	/**
	 * This method invokes the SDK method SubtenantService.getSubtenantsByUserAndRole(String tenantId, PrincipalId principalId, String roleRef, Pageable pageable).
	 * @param tenantId 
	 * @param principalId 
	 * @param roleRef 
	 * @param pageable 
	 */
	getSubtenantsByUserAndRole(tenantId: string, principalId: vCACCAFEPrincipalId, roleRef: string, pageable: any): vCACCAFESubtenant[];
	/**
	 * This method invokes the SDK method SubtenantService.getSubtenantsRolesPrincipals(String tenantId, List<String> subtenants, String roleRef, boolean resolvePrincipals).
	 * @param tenantId 
	 * @param subtenants 
	 * @param roleRef 
	 * @param resolvePrincipals 
	 */
	getSubtenantsRolesPrincipals(tenantId: string, subtenants: string[], roleRef: string, resolvePrincipals: boolean): vCACCAFESubtenantPrincipalsData[];
	/**
	 * This method invokes the SDK method SubtenantService.deleteSubtenantRolePrincipal(String tenantId, String subtenantId, String roleRef, String principalId).
	 * @param tenantId 
	 * @param subtenantId 
	 * @param roleRef 
	 * @param principalId 
	 */
	deleteSubtenantRolePrincipal(tenantId: string, subtenantId: string, roleRef: string, principalId: string): void;
	/**
	 * This method invokes the SDK method SubtenantService.getPrincipalsBySubtenantAndRole(String tenantId, String subtenantId, String criteria, String role, Pageable pageable).
	 * @param tenantId 
	 * @param subtenantId 
	 * @param criteria 
	 * @param role 
	 * @param pageable 
	 */
	getPrincipalsBySubtenantAndRole(tenantId: string, subtenantId: string, criteria: string, role: string, pageable: any): vCACCAFEPrincipalData[];
	/**
	 * This method invokes the SDK method SubtenantService.getPrincipalsBySubtenant(String tenantId, String subtenantId, String roleRef, Pageable pageable).
	 * @param tenantId 
	 * @param subtenantId 
	 * @param roleRef 
	 * @param pageable 
	 */
	getPrincipalsBySubtenant(tenantId: string, subtenantId: string, roleRef: string, pageable: any): vCACCAFEPrincipalData[];
	/**
	 * This method invokes the SDK method SubtenantService.getPrincipalsBySubtenantsAndRole(String tenantId, List<String> subtenants, String criteria, String role, Pageable pageable).
	 * @param tenantId 
	 * @param subtenants 
	 * @param criteria 
	 * @param role 
	 * @param pageable 
	 */
	getPrincipalsBySubtenantsAndRole(tenantId: string, subtenants: string[], criteria: string, role: string, pageable: any): vCACCAFEPrincipalData[];
	/**
	 * This method invokes the SDK method SubtenantService.getSubtenantRolePrincipals(String tenantId, String subtenantId, String roleRef, Pageable pageable).
	 * @param tenantId 
	 * @param subtenantId 
	 * @param roleRef 
	 * @param pageable 
	 */
	getSubtenantRolePrincipals(tenantId: string, subtenantId: string, roleRef: string, pageable: any): vCACCAFEPrincipalData[];
	/**
	 * This method invokes the SDK method SubtenantService.getSubtenantRolesByPrincipal(String tenantId, String subtenantId, PrincipalId principalId, Pageable pageable).
	 * @param tenantId 
	 * @param subtenantId 
	 * @param principalId 
	 * @param pageable 
	 */
	getSubtenantRolesByPrincipal(tenantId: string, subtenantId: string, principalId: vCACCAFEPrincipalId, pageable: any): vCACCAFESubtenantRole[];
	/**
	 * This method invokes the SDK method SubtenantService.getSubtenant(URI uri).
	 * @param uri 
	 */
	getSubtenantByUri(uri: any): vCACCAFESubtenant;
	/**
	 * This method invokes the SDK method SubtenantService.getSubtenant(String tenantId, String subtenantId).
	 * @param tenantId 
	 * @param subtenantId 
	 */
	getSubtenant(tenantId: string, subtenantId: string): vCACCAFESubtenant;
	/**
	 * This method invokes the SDK method SubtenantService.getSubtenants(String tenantId, Pageable pageable).
	 * @param tenantId 
	 * @param pageable 
	 */
	getSubtenants(tenantId: string, pageable: any): vCACCAFESubtenant[];
	/**
	 * This method invokes the SDK method SubtenantService.createSubtenant(String tenantId, Subtenant subtenant).
	 * @param tenantId 
	 * @param subtenant 
	 */
	createSubtenant(tenantId: string, subtenant: vCACCAFESubtenant): vCACCAFESubtenant;
	/**
	 * This method invokes the SDK method SubtenantService.getSubtenantRoles(String tenantId, String subtenantId, Pageable pageable).
	 * @param tenantId 
	 * @param subtenantId 
	 * @param pageable 
	 */
	getSubtenantRoles(tenantId: string, subtenantId: string, pageable: any): vCACCAFESubtenantRole[];
	/**
	 * This method invokes the SDK method SubtenantService.deleteSubtenantRole(String tenantId, String subtenantId, String roleRef).
	 * @param tenantId 
	 * @param subtenantId 
	 * @param roleRef 
	 */
	deleteSubtenantRole(tenantId: string, subtenantId: string, roleRef: string): void;
	/**
	 * This method invokes the SDK method SubtenantService.updateSubtenant(String tenantId, Subtenant subtenant).
	 * @param tenantId 
	 * @param subtenant 
	 */
	updateSubtenant(tenantId: string, subtenant: vCACCAFESubtenant): void;
	/**
	 * This method invokes the SDK method SubtenantService.addRoles(String tenantId, String subtenantId, List<SubtenantRole> subtenantRoles).
	 * @param tenantId 
	 * @param subtenantId 
	 * @param subtenantRoles 
	 */
	addRoles(tenantId: string, subtenantId: string, subtenantRoles: vCACCAFESubtenantRole[]): any;
	/**
	 * This method invokes the SDK method SubtenantService.deleteSubtenant(String tenantId, String subtenantId).
	 * @param tenantId 
	 * @param subtenantId 
	 */
	deleteSubtenant(tenantId: string, subtenantId: string): void;
}

declare interface vCACCAFEAuthenticationTenantService {
	/**
	 * This method invokes the SDK method TenantService.deleteTenant(String tenant).
	 * @param tenant 
	 */
	deleteTenant(tenant: string): void;
	/**
	 * This method invokes the SDK method TenantService.getTenant(URI uri).
	 * @param uri 
	 */
	getTenantByUri(uri: any): vCACCAFETenant;
	/**
	 * This method invokes the SDK method TenantService.updateOrCreateTenant(Tenant tenant).
	 * @param tenant 
	 */
	updateOrCreateTenant(tenant: vCACCAFETenant): void;
	/**
	 * This method invokes the SDK method TenantService.getTenant(String tenant).
	 * @param tenant 
	 */
	getTenant(tenant: string): vCACCAFETenant;
	/**
	 * This method invokes the SDK method TenantService.getTenants(Pageable pageable).
	 * @param pageable 
	 */
	getTenants(pageable: any): vCACCAFETenant[];
}

declare class vCACCAFEAuthoritiesContext {
	constructor();
	constructor();
	getAuthorities(): string[];
	getScopeAuthorities(): vCACCAFEScopeAuthoritiesEntry[];
}

declare class vCACCAFEAuthoritiesDisplayContext {
	constructor();
	constructor();
	getAssignableRoleIds(): string[];
	getAssignedRoleIds(): string[];
	getGroupAssignedRoleIds(): string[];
	getDisplayAuthorities(): vCACCAFEDisplayAuthoritiesEntry[];
}

declare class vCACCAFEAuthoritiesExtendedContext {
	constructor();
	constructor();
	getPermissions(): vCACCAFEPermission[];
	getScopeAuthoritiesExtended(): vCACCAFEScopeAuthoritiesExtendedEntry[];
	getRoles(): any[];
}

declare interface vCACCAFEAuthorizationClient {
	getAuthorizationPrincipalService(): vCACCAFEAuthorizationPrincipalService;
	getAuthorizationRoleExtensionService(): vCACCAFEAuthorizationRoleExtensionService;
	getAuthorizationRoleService(): vCACCAFEAuthorizationRoleService;
	getAuthorizationScopeService(): vCACCAFEAuthorizationScopeService;
	getAuthorizationScopeTypeService(): vCACCAFEAuthorizationScopeTypeService;
	getAuthorizationPermissionService(): vCACCAFEAuthorizationPermissionService;
	getAuthorizationResourceService(): vCACCAFEAuthorizationResourceService;
	getAuthorizationResourceTypeService(): vCACCAFEAuthorizationResourceTypeService;
	/**
	 * Does a POST request expanding the given template variables in order.
	 * @param resourceUrl 
	 * @param object 
	 * @param urlVariables 
	 */
	postWithVariables(resourceUrl: string, object: any, urlVariables: string[]): vCACCAFEServiceResponse;
	/**
	 * Does a PUT request expanding the given template variables in order.
	 * @param resourceUrl 
	 * @param object 
	 * @param urlVariables 
	 */
	putWithVariables(resourceUrl: string, object: any, urlVariables: string[]): vCACCAFEServiceResponse;
	/**
	 * Does a DELETE request expanding the given template variables in order.
	 * @param resourceUrl 
	 * @param urlVariables 
	 */
	deleteWithVariables(resourceUrl: string, urlVariables: string[]): void;
	/**
	 * Gets the REST client URL.
	 */
	getUrl(): string;
	/**
	 * Indicates whether the current connection is already closed or not.
	 */
	isClosed(): boolean;
	/**
	 * Does a GET request for a file and allows specifying 'Accept' header.
	 * @param resourceUrl 
	 * @param acceptHeaderValue 
	 */
	getFileWithAcceptHeader(resourceUrl: string, acceptHeaderValue: string): any;
	/**
	 * Does a POST request with a file.
	 * @param resourceUrl 
	 * @param object 
	 */
	postFile(resourceUrl: string, object: any): vCACCAFEServiceResponse;
	/**
	 * Does a GET request expanding the given template variables in order.
	 * @param resourceUrl 
	 * @param urlVariables 
	 */
	getWithVariables(resourceUrl: string, urlVariables: string[]): any;
	/**
	 * Does a GET request.
	 * @param resourceUrl 
	 */
	get(resourceUrl: string): any;
	/**
	 * Does a PUT request.
	 * @param resourceUrl 
	 * @param object 
	 */
	put(resourceUrl: string, object: any): vCACCAFEServiceResponse;
	/**
	 * Does a DELETE request.
	 * @param resourceUrl 
	 */
	delete(resourceUrl: string): void;
	/**
	 * Invalidates the current connection.
	 */
	close(): void;
	/**
	 * Does a GET request for a file.
	 * @param resourceUrl 
	 */
	getFile(resourceUrl: string): any;
	/**
	 * Does a POST request.
	 * @param resourceUrl 
	 * @param object 
	 */
	post(resourceUrl: string, object: any): vCACCAFEServiceResponse;
}

declare interface vCACCAFEAuthorizationPermissionService {
	/**
	 * This method invokes the SDK method PermissionService.removeAdminPermissionFromPermission(String permissionId, String adminPermissionId).
	 * @param permissionId 
	 * @param adminPermissionId 
	 */
	removeAdminPermissionFromPermission(permissionId: string, adminPermissionId: string): void;
	/**
	 * This method invokes the SDK method PermissionService.getPermission(URI uri).
	 * @param uri 
	 */
	getPermissionByUri(uri: any): vCACCAFEPermission;
	/**
	 * This method invokes the SDK method PermissionService.createOrUpdatePermission(Permission permission).
	 * @param permission 
	 */
	createOrUpdatePermission(permission: vCACCAFEPermission): void;
	/**
	 * This method invokes the SDK method PermissionService.getPermissions(Pageable page).
	 * @param page 
	 */
	getPermissions(page: any): vCACCAFEPagedResources;
	/**
	 * This method invokes the SDK method PermissionService.getPermission(String permissionId).
	 * @param permissionId 
	 */
	getPermission(permissionId: string): vCACCAFEPermission;
	/**
	 * This method invokes the SDK method PermissionService.addAdminPermission(String permissionId, String adminPermissionId).
	 * @param permissionId 
	 * @param adminPermissionId 
	 */
	addAdminPermission(permissionId: string, adminPermissionId: string): void;
	/**
	 * This method invokes the SDK method PermissionService.getAdminPermissions(String permissionId).
	 * @param permissionId 
	 */
	getAdminPermissions(permissionId: string): vCACCAFEPermission[];
}

declare interface vCACCAFEAuthorizationPrincipalService {
	/**
	 * This method invokes the SDK method PrincipalService.createOrUpdatePrincipalExtension(String tenant, PrincipalExtension principal).
	 * @param tenant 
	 * @param principal 
	 */
	createOrUpdatePrincipalExtension(tenant: string, principal: vCACCAFEPrincipalExtension): void;
	/**
	 * This method invokes the SDK method PrincipalService.getPrincipalExtensionsWithRoles(String tenant, Pageable page).
	 * @param tenant 
	 * @param page 
	 */
	getPrincipalExtensionsWithRoles(tenant: string, page: any): vCACCAFEPagedResources;
	/**
	 * This method invokes the SDK method PrincipalService.removeScopeRole(String tenant, String principalId, String scopeId, String scopeRoleId).
	 * @param tenant 
	 * @param principalId 
	 * @param scopeId 
	 * @param scopeRoleId 
	 */
	removeScopeRole(tenant: string, principalId: string, scopeId: string, scopeRoleId: string): void;
	/**
	 * This method invokes the SDK method PrincipalService.setScopeRoles(String tenant, String principalId, String scopeId, Set<String> scopeRoles).
	 * @param tenant 
	 * @param principalId 
	 * @param scopeId 
	 * @param scopeRoles 
	 */
	setScopeRoles(tenant: string, principalId: string, scopeId: string, scopeRoles: string[]): void;
	/**
	 * This method invokes the SDK method PrincipalService.addScopeRole(String tenant, String principalId, String scopeId, String scopeRoleId).
	 * @param tenant 
	 * @param principalId 
	 * @param scopeId 
	 * @param scopeRoleId 
	 */
	addScopeRole(tenant: string, principalId: string, scopeId: string, scopeRoleId: string): void;
	/**
	 * This method invokes the SDK method PrincipalService.addScopeRoles(String tenant, String principalId, String scopeId, Set<String> scopeRoles).
	 * @param tenant 
	 * @param principalId 
	 * @param scopeId 
	 * @param scopeRoles 
	 */
	addScopeRoles(tenant: string, principalId: string, scopeId: string, scopeRoles: string[]): void;
	/**
	 * This method invokes the SDK method PrincipalService.deletePrincipalExtension(String tenant, String principalId).
	 * @param tenant 
	 * @param principalId 
	 */
	deletePrincipalExtension(tenant: string, principalId: string): void;
	/**
	 * This method invokes the SDK method PrincipalService.deletePrincipalExtensions(String tenant, Pageable page).
	 * @param tenant 
	 * @param page 
	 */
	deletePrincipalExtensions(tenant: string, page: any): void;
	/**
	 * This method invokes the SDK method PrincipalService.getPrincipalExtension(String tenant, String principalId).
	 * @param tenant 
	 * @param principalId 
	 */
	getPrincipalExtension(tenant: string, principalId: string): vCACCAFEPrincipalExtension;
	/**
	 * This method invokes the SDK method PrincipalService.getPrincipalExtension(URI uri).
	 * @param uri 
	 */
	getPrincipalExtensionByUri(uri: any): vCACCAFEPrincipalExtension;
	/**
	 * This method invokes the SDK method PrincipalService.getPrincipalExtensions(String tenant, Pageable page).
	 * @param tenant 
	 * @param page 
	 */
	getPrincipalExtensions(tenant: string, page: any): vCACCAFEPagedResources;
	/**
	 * This method invokes the SDK method PrincipalService.getPrincipalExtensionsWithRole(String tenant, String role, Pageable page).
	 * @param tenant 
	 * @param role 
	 * @param page 
	 */
	getPrincipalExtensionsWithRole(tenant: string, role: string, page: any): vCACCAFEPagedResources;
	/**
	 * This method invokes the SDK method PrincipalService.getPermissions(String tenant, String principalId, RoleType type, String scopeType, String scopeId, Pageable page).
	 * @param tenant 
	 * @param principalId 
	 * @param type 
	 * @param scopeType 
	 * @param scopeId 
	 * @param page 
	 */
	getPermissions(tenant: string, principalId: string, type: vCACCAFERoleType, scopeType: string, scopeId: string, page: any): vCACCAFEPagedResources;
	/**
	 * This method invokes the SDK method PrincipalService.getScope(String tenant, String principalId, String scopeId).
	 * @param tenant 
	 * @param principalId 
	 * @param scopeId 
	 */
	getScope(tenant: string, principalId: string, scopeId: string): vCACCAFEScope;
	/**
	 * This method invokes the SDK method PrincipalService.getScopes(String tenant, String role, Pageable page).
	 * @param tenant 
	 * @param role 
	 * @param page 
	 */
	getScopes(tenant: string, role: string, page: any): vCACCAFEPagedResources;
	/**
	 * This method invokes the SDK method PrincipalService.addRole(String tenant, String principalId, String roleId).
	 * @param tenant 
	 * @param principalId 
	 * @param roleId 
	 */
	addRole(tenant: string, principalId: string, roleId: string): void;
	/**
	 * This method invokes the SDK method PrincipalService.removeRole(String tenant, String principalId, String roleId).
	 * @param tenant 
	 * @param principalId 
	 * @param roleId 
	 */
	removeRole(tenant: string, principalId: string, roleId: string): void;
	/**
	 * This method invokes the SDK method PrincipalService.getRoles(String tenant, String principalId, RoleType type, String scopeType, String scopeId, Pageable page).
	 * @param tenant 
	 * @param principalId 
	 * @param type 
	 * @param scopeType 
	 * @param scopeId 
	 * @param page 
	 */
	getRoles(tenant: string, principalId: string, type: vCACCAFERoleType, scopeType: string, scopeId: string, page: any): vCACCAFEPagedResources;
	/**
	 * This method invokes the SDK method PrincipalService.setRoles(String tenant, String principalId, Set<String> roles).
	 * @param tenant 
	 * @param principalId 
	 * @param roles 
	 */
	setRoles(tenant: string, principalId: string, roles: string[]): void;
}

declare interface vCACCAFEAuthorizationResourceService {
	/**
	 * This method invokes the SDK method ResourceService.getResource(String tenant, String resourceId).
	 * @param tenant 
	 * @param resourceId 
	 */
	getResource(tenant: string, resourceId: string): vCACCAFEResourceRef;
	/**
	 * This method invokes the SDK method ResourceService.getResources(String tenant, Pageable page).
	 * @param tenant 
	 * @param page 
	 */
	getResources(tenant: string, page: any): vCACCAFEPagedResources;
	/**
	 * This method invokes the SDK method ResourceService.getScopes(String tenant, String resourceId, Pageable page).
	 * @param tenant 
	 * @param resourceId 
	 * @param page 
	 */
	getScopes(tenant: string, resourceId: string, page: any): vCACCAFEPagedResources;
	/**
	 * This method invokes the SDK method ResourceService.createOrUpdateResource(String tenant, ResourceRef resource).
	 * @param tenant 
	 * @param resource 
	 */
	createOrUpdateResource(tenant: string, resource: vCACCAFEResourceRef): void;
	/**
	 * This method invokes the SDK method ResourceService.deleteResource(String tenant, String resourceId).
	 * @param tenant 
	 * @param resourceId 
	 */
	deleteResource(tenant: string, resourceId: string): void;
	/**
	 * This method invokes the SDK method ResourceService.deleteResources(String tenant, Pageable page).
	 * @param tenant 
	 * @param page 
	 */
	deleteResources(tenant: string, page: any): void;
	/**
	 * This method invokes the SDK method ResourceService.getResource(URI uri).
	 * @param uri 
	 */
	getResourceByUri(uri: any): vCACCAFEResourceRef;
}

declare interface vCACCAFEAuthorizationResourceTypeService {
	/**
	 * This method invokes the SDK method ResourceTypeService.createOrUpdateResourceType(ResourceTypeRef type).
	 * @param type 
	 */
	createOrUpdateResourceType(type: vCACCAFEResourceTypeRef): void;
	/**
	 * This method invokes the SDK method ResourceTypeService.getResourceType(URI uri).
	 * @param uri 
	 */
	getResourceTypeByUri(uri: any): vCACCAFEResourceTypeRef;
	/**
	 * This method invokes the SDK method ResourceTypeService.getResourceTypes(Pageable page).
	 * @param page 
	 */
	getResourceTypes(page: any): vCACCAFEPagedResources;
	/**
	 * This method invokes the SDK method ResourceTypeService.getResourceType(String typeId).
	 * @param typeId 
	 */
	getResourceType(typeId: string): vCACCAFEResourceTypeRef;
}

declare interface vCACCAFEAuthorizationRoleExtensionService {
	/**
	 * This method invokes the SDK method RoleExtensionService.getRoleExtension(String roleExtensionId).
	 * @param roleExtensionId 
	 */
	getRoleExtension(roleExtensionId: string): vCACCAFERoleExtension;
	/**
	 * This method invokes the SDK method RoleExtensionService.getRoleExtension(URI uri).
	 * @param uri 
	 */
	getRoleExtensionByUri(uri: any): vCACCAFERoleExtension;
	/**
	 * This method invokes the SDK method RoleExtensionService.createOrUpdateRoleExtension(RoleExtension roleExtension).
	 * @param roleExtension 
	 */
	createOrUpdateRoleExtension(roleExtension: vCACCAFERoleExtension): void;
	/**
	 * This method invokes the SDK method RoleExtensionService.getRoleExtensions(Pageable page).
	 * @param page 
	 */
	getRoleExtensions(page: any): vCACCAFEPagedResources;
	/**
	 * This method invokes the SDK method RoleExtensionService.removePermission(String roleExtensionId, String permissionId).
	 * @param roleExtensionId 
	 * @param permissionId 
	 */
	removePermission(roleExtensionId: string, permissionId: string): void;
	/**
	 * This method invokes the SDK method RoleExtensionService.addPermission(String roleExtensionId, String permissionId).
	 * @param roleExtensionId 
	 * @param permissionId 
	 */
	addPermission(roleExtensionId: string, permissionId: string): void;
	/**
	 * This method invokes the SDK method RoleExtensionService.getPermissions(String roleExtensionId).
	 * @param roleExtensionId 
	 */
	getPermissions(roleExtensionId: string): vCACCAFEPermission[];
}

declare interface vCACCAFEAuthorizationRoleService {
	/**
	 * This method invokes the SDK method RoleService.getAssignedPermissions(String roleId).
	 * @param roleId 
	 */
	getAssignedPermissions(roleId: string): vCACCAFEPermission[];
	/**
	 * This method invokes the SDK method RoleService.getRoleExtensions(String roleId).
	 * @param roleId 
	 */
	getRoleExtensions(roleId: string): vCACCAFERoleExtension[];
	/**
	 * This method invokes the SDK method RoleService.getRole(URI uri).
	 * @param uri 
	 */
	getRoleByUri(uri: any): any;
	/**
	 * This method invokes the SDK method RoleService.getRolesAssignable(Pageable page).
	 * @param page 
	 */
	getRolesAssignable(page: any): vCACCAFEPagedResources;
	/**
	 * This method invokes the SDK method RoleService.removePermission(String roleId, String permissionId).
	 * @param roleId 
	 * @param permissionId 
	 */
	removePermission(roleId: string, permissionId: string): void;
	/**
	 * This method invokes the SDK method RoleService.addPermission(String roleId, String permissionId).
	 * @param roleId 
	 * @param permissionId 
	 */
	addPermission(roleId: string, permissionId: string): void;
	/**
	 * This method invokes the SDK method RoleService.createOrUpdateRole(Role role).
	 * @param role 
	 */
	createOrUpdateRole(role: any): void;
	/**
	 * This method invokes the SDK method RoleService.getPermissions(String roleId).
	 * @param roleId 
	 */
	getPermissions(roleId: string): vCACCAFEPermission[];
	/**
	 * This method invokes the SDK method RoleService.getRole(String roleId).
	 * @param roleId 
	 */
	getRole(roleId: string): any;
	/**
	 * This method invokes the SDK method RoleService.getRoles(Pageable page).
	 * @param page 
	 */
	getRoles(page: any): vCACCAFEPagedResources;
	/**
	 * This method invokes the SDK method RoleService.getAdminPermissions(String roleId).
	 * @param roleId 
	 */
	getAdminPermissions(roleId: string): vCACCAFEPermission[];
}

declare interface vCACCAFEAuthorizationScopeService {
	/**
	 * This method invokes the SDK method ScopeService.getResources(String tenant, String scopeId, Pageable page).
	 * @param tenant 
	 * @param scopeId 
	 * @param page 
	 */
	getResources(tenant: string, scopeId: string, page: any): vCACCAFEPagedResources;
	/**
	 * This method invokes the SDK method ScopeService.getPrincipals(String tenant, String scopeId, String roleId, String permissionId, Pageable page).
	 * @param tenant 
	 * @param scopeId 
	 * @param roleId 
	 * @param permissionId 
	 * @param page 
	 */
	getPrincipals(tenant: string, scopeId: string, roleId: string, permissionId: string, page: any): vCACCAFEPagedResources;
	/**
	 * This method invokes the SDK method ScopeService.getScope(String tenant, String scopeId).
	 * @param tenant 
	 * @param scopeId 
	 */
	getScope(tenant: string, scopeId: string): vCACCAFEScope;
	/**
	 * This method invokes the SDK method ScopeService.getScopes(String tenant, Pageable page).
	 * @param tenant 
	 * @param page 
	 */
	getScopes(tenant: string, page: any): vCACCAFEPagedResources;
	/**
	 * This method invokes the SDK method ScopeService.addResources(String tenant, String scopeId, List<ResourceRef> resources).
	 * @param tenant 
	 * @param scopeId 
	 * @param resources 
	 */
	addResources(tenant: string, scopeId: string, resources: vCACCAFEResourceRef[]): void;
	/**
	 * This method invokes the SDK method ScopeService.removeResource(String tenant, String scopeId, String resourceId).
	 * @param tenant 
	 * @param scopeId 
	 * @param resourceId 
	 */
	removeResource(tenant: string, scopeId: string, resourceId: string): void;
	/**
	 * This method invokes the SDK method ScopeService.setResources(String tenant, String scopeId, List<ResourceRef> resources).
	 * @param tenant 
	 * @param scopeId 
	 * @param resources 
	 */
	setResources(tenant: string, scopeId: string, resources: vCACCAFEResourceRef[]): void;
	/**
	 * This method invokes the SDK method ScopeService.getPrincipals(String tenant, List<String> scopes, String roleId, String permissionId).
	 * @param tenant 
	 * @param scopes 
	 * @param roleId 
	 * @param permissionId 
	 */
	getPrincipalsByScopes(tenant: string, scopes: string[], roleId: string, permissionId: string): vCACCAFEScopePrincipalsRef[];
	/**
	 * This method invokes the SDK method ScopeService.getScope(URI uri).
	 * @param uri 
	 */
	getScopeByUri(uri: any): vCACCAFEScope;
	/**
	 * This method invokes the SDK method ScopeService.createOrUpdateScope(String tenant, Scope scope).
	 * @param tenant 
	 * @param scope 
	 */
	createOrUpdateScope(tenant: string, scope: vCACCAFEScope): void;
	/**
	 * This method invokes the SDK method ScopeService.deleteScope(String tenant, String scopeId).
	 * @param tenant 
	 * @param scopeId 
	 */
	deleteScope(tenant: string, scopeId: string): void;
	/**
	 * This method invokes the SDK method ScopeService.deleteScopes(String tenant, Pageable page).
	 * @param tenant 
	 * @param page 
	 */
	deleteScopes(tenant: string, page: any): void;
	/**
	 * This method invokes the SDK method ScopeService.removeResources(String tenant, String scopeId, Pageable page).
	 * @param tenant 
	 * @param scopeId 
	 * @param page 
	 */
	removeResources(tenant: string, scopeId: string, page: any): void;
}

declare interface vCACCAFEAuthorizationScopeTypeService {
	/**
	 * This method invokes the SDK method ScopeTypeService.addAdminPermission(String scopeTypeId, String adminPermissionId).
	 * @param scopeTypeId 
	 * @param adminPermissionId 
	 */
	addAdminPermission(scopeTypeId: string, adminPermissionId: string): void;
	/**
	 * This method invokes the SDK method ScopeTypeService.createOrUpdateScopeType(ScopeType type).
	 * @param type 
	 */
	createOrUpdateScopeType(type: vCACCAFEScopeType): void;
	/**
	 * This method invokes the SDK method ScopeTypeService.getAdminPermissions(String scopeTypeId).
	 * @param scopeTypeId 
	 */
	getAdminPermissions(scopeTypeId: string): vCACCAFEPermission[];
	/**
	 * This method invokes the SDK method ScopeTypeService.getScopeType(String scopeTypeId).
	 * @param scopeTypeId 
	 */
	getScopeType(scopeTypeId: string): vCACCAFEScopeType;
	/**
	 * This method invokes the SDK method ScopeTypeService.getScopeType(URI uri).
	 * @param uri 
	 */
	getScopeTypeByUri(uri: any): vCACCAFEScopeType;
	/**
	 * This method invokes the SDK method ScopeTypeService.getScopeTypesAssignable(Pageable page).
	 * @param page 
	 */
	getScopeTypesAssignable(page: any): vCACCAFEPagedResources;
	/**
	 * This method invokes the SDK method ScopeTypeService.getScopeTypes(Pageable page).
	 * @param page 
	 */
	getScopeTypes(page: any): vCACCAFEPagedResources;
	/**
	 * This method invokes the SDK method ScopeTypeService.removeAdminPermission(String scopeTypeId, String adminPermissionId).
	 * @param scopeTypeId 
	 * @param adminPermissionId 
	 */
	removeAdminPermission(scopeTypeId: string, adminPermissionId: string): void;
}

declare class vCACCAFEBaseInboundNotification {
	constructor();
	constructor();
	getBody(): string;
	getAction(): string;
	getUserToken(): string;
	/**
	 * @param value 
	 */
	setUserToken(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setSubject(value: string): void;
	/**
	 * @param value 
	 */
	setAction(value: string): void;
	/**
	 * @param value 
	 */
	setNotificationId(value: string): void;
	getFromUser(): string;
	/**
	 * @param value 
	 */
	setFromUser(value: string): void;
	getMessageReceivedAt(): any;
	/**
	 * @param value 
	 */
	setMessageReceivedAt(value: any): void;
	getNotificationId(): string;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	/**
	 * @param value 
	 */
	setUserId(value: string): void;
	getUserId(): string;
	/**
	 * @param value 
	 */
	setBody(value: string): void;
	getSubject(): string;
}

declare class vCACCAFEBlueprintForm {
	constructor();
	/**
	 * @param name 
	 * @param layout 
	 */
	constructor(name: string, layout: com.vmware.vcac.platform.designer.data.layout.DesignerLayout);
	getName(): string;
	getFields(): any;
	/**
	 * @param name 
	 */
	setName(name: string): void;
	getDesignerLayout(): any;
}

declare class vCACCAFEBlueprintNode {
	constructor();
	constructor();
	isMandatory(): boolean;
	getChildren(): vCACCAFEBlueprintNode[];
	getComponentTypeName(): string;
	/**
	 * @param value 
	 */
	setComponentTypeName(value: string): void;
	getFieldPath(): string;
	/**
	 * @param value 
	 */
	setFieldPath(value: string): void;
	/**
	 * @param value 
	 */
	setMandatory(value: boolean): void;
	getComponentTypeId(): string;
	/**
	 * @param value 
	 */
	setComponentTypeId(value: string): void;
	getComponentTypeTenantId(): string;
	/**
	 * @param value 
	 */
	setComponentTypeTenantId(value: string): void;
	getIconURL(): string;
	/**
	 * @param value 
	 */
	setIconURL(value: string): void;
	/**
	 * @param value 
	 */
	setLabel(value: string): void;
	getClassId(): string;
	/**
	 * @param value 
	 */
	setClassId(value: string): void;
	getTypeFilter(): string;
	/**
	 * @param value 
	 */
	setTypeFilter(value: string): void;
	getLabel(): string;
}

declare class vCACCAFEBlueprintPriceRequest {
	constructor();
	constructor();
	getRequestedFor(): string;
	getSubTenantId(): string;
	/**
	 * @param value 
	 */
	setSubTenantId(value: string): void;
	getRequestData(): vCACCAFELiteralMap;
	getTargetResourceId(): string;
	/**
	 * @param value 
	 */
	setTargetResourceId(value: string): void;
	/**
	 * @param value 
	 */
	setRequestedFor(value: string): void;
	/**
	 * @param value 
	 */
	setRequestData(value: vCACCAFELiteralMap): void;
}

declare class vCACCAFEBlueprintRequest {
	constructor();
	constructor();
	/**
	 * @param value 
	 */
	setOrganization(value: vCACCAFEOrganization): void;
	getOrganization(): vCACCAFEOrganization;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	getBlueprint(): vCACCAFECompositeBlueprint;
	getRequestedFor(): string;
	getRequestedBy(): string;
	getDeploymentSpec(): vCACCAFECompositeBlueprintSpecification;
	/**
	 * @param value 
	 */
	setDeploymentSpec(value: vCACCAFECompositeBlueprintSpecification): void;
	getInputData(): vCACCAFELiteralMap;
	/**
	 * @param value 
	 */
	setInputData(value: vCACCAFELiteralMap): void;
	getCallbackServiceId(): string;
	/**
	 * @param value 
	 */
	setCallbackServiceId(value: string): void;
	getComponentRequests(): vCACCAFEComponentRequest[];
	getReasons(): string;
	getRequestNumber(): number;
	/**
	 * @param value 
	 */
	setRequestNumber(value: number): void;
	/**
	 * @param value 
	 */
	setBlueprint(value: vCACCAFECompositeBlueprint): void;
	/**
	 * @param value 
	 */
	setReasons(value: string): void;
	/**
	 * @param value 
	 */
	setRequestedBy(value: string): void;
	/**
	 * @param value 
	 */
	setRequestedFor(value: string): void;
	getParentId(): string;
	/**
	 * @param value 
	 */
	setParentId(value: string): void;
	getVersion(): number;
	getId(): string;
	getStatus(): vCACCAFERequestStatus;
	/**
	 * @param value 
	 */
	setVersion(value: number): void;
	getStatusName(): string;
	/**
	 * @param value 
	 */
	setStatusName(value: string): void;
	getDateStarted(): any;
	/**
	 * @param value 
	 */
	setDateStarted(value: any): void;
	getDateCompleted(): any;
	/**
	 * @param value 
	 */
	setDateCompleted(value: any): void;
	getCompletionDetails(): string;
	/**
	 * @param value 
	 */
	setCompletionDetails(value: string): void;
	getApprovalStatus(): vCACCAFEApprovalStatus;
	/**
	 * @param value 
	 */
	setApprovalStatus(value: vCACCAFEApprovalStatus): void;
	getPreApprovalId(): string;
	/**
	 * @param value 
	 */
	setPreApprovalId(value: string): void;
	getPostApprovalId(): string;
	/**
	 * @param value 
	 */
	setPostApprovalId(value: string): void;
	getRetriesRemaining(): number;
	/**
	 * @param value 
	 */
	setRetriesRemaining(value: number): void;
	getDateSubmitted(): any;
	/**
	 * @param value 
	 */
	setDateSubmitted(value: any): void;
	getLastUpdated(): any;
	/**
	 * @param value 
	 */
	setLastUpdated(value: any): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	/**
	 * @param value 
	 */
	setStatus(value: vCACCAFERequestStatus): void;
	/**
	 * @param value 
	 */
	setCreatedDate(value: any): void;
	getCreatedDate(): any;
}

declare class vCACCAFEBlueprintRequestInfo {
	constructor();
	constructor();
	getId(): string;
	getDeploymentName(): string;
	/**
	 * @param value 
	 */
	setDeploymentName(value: string): void;
	getDeploymentId(): string;
	/**
	 * @param value 
	 */
	setDeploymentId(value: string): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
}

declare class vCACCAFEBlueprintRequestStatus {
	constructor();
	constructor();
	getId(): string;
	getStatus(): vCACCAFERequestStatus;
	/**
	 * @param value 
	 */
	setBlueprintName(value: string): void;
	getBlueprintName(): string;
	getComponentRequestStatuses(): vCACCAFEPage;
	/**
	 * @param value 
	 */
	setComponentRequestStatuses(value: vCACCAFEPage): void;
	getPlacementRequestStatus(): vCACCAFEPlacementRequestStatus;
	/**
	 * @param value 
	 */
	setPlacementRequestStatus(value: vCACCAFEPlacementRequestStatus): void;
	/**
	 * @param value 
	 */
	setBlueprintId(value: string): void;
	getBlueprintId(): string;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	/**
	 * @param value 
	 */
	setStatus(value: vCACCAFERequestStatus): void;
	getDetails(): string;
	/**
	 * @param value 
	 */
	setDetails(value: string): void;
}

declare interface vCACCAFEBooleanLiteral {
	/**
	 * @param value 
	 */
	fromBoolean(value: boolean): vCACCAFEBooleanLiteral;
	isList(): boolean;
	/**
	 * @param other 
	 */
	compareTo(other: any): number;
	getValue(): boolean;
	not(): vCACCAFEBooleanLiteral;
	getTypeId(): vCACCAFEDataTypeId;
	/**
	 * @param obj 
	 */
	safeEquals(obj: any): boolean;
	/**
	 * @param sVal 
	 */
	fromString(sVal: string): any;
	/**
	 * @param lVal 
	 */
	fromInt(lVal: number): any;
	/**
	 * @param dVal 
	 */
	fromDecimal(dVal: number): any;
}

declare class vCACCAFEBusinessGroup {
	name: string;
	id: any;
	description: string;
	allocatedMemory: number;
	href: string;
	allocatedQuota: number;
	allocatedStorage: number;
	tenantId: string;
	totalMachines: number;
	totalMemory: number;
	totalQuota: number;
	totalStorage: number;
	activeDirectoryContainer: string;
	administratorEmail: string;
	administrators: string[];
	machinePrefixId: any;
	support: string[];
	users: string[];
	customProperties: vCACCAFECustomProperty[];
	vcoId: any;
	constructor();
	constructor();
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): any;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	getAllocatedMemory(): number;
	getHref(): string;
	getAllocatedQuota(): number;
	getAllocatedStorage(): number;
	getTenantId(): string;
	getTotalMachines(): number;
	getTotalMemory(): number;
	getTotalQuota(): number;
	getTotalStorage(): number;
	getActiveDirectoryContainer(): string;
	getAdministratorEmail(): string;
	getAdministrators(): string[];
	getMachinePrefixId(): any;
	getSupport(): string[];
	getUsers(): string[];
	/**
	 * @param value 
	 */
	setActiveDirectoryContainer(value: string): void;
	/**
	 * @param value 
	 */
	setAdministratorEmail(value: string): void;
	/**
	 * @param value 
	 */
	setAdministrators(value: string[]): void;
	/**
	 * @param value 
	 */
	setMachinePrefixId(value: any): void;
	/**
	 * @param value 
	 */
	setCustomProperties(value: vCACCAFECustomProperty[]): void;
	/**
	 * @param value 
	 */
	setSupport(value: string[]): void;
	/**
	 * @param value 
	 */
	setUsers(value: string[]): void;
	getCustomProperties(): vCACCAFECustomProperty[];
}

declare interface vCACCAFEBusinessGroupsService {
	/**
	 * @param id 
	 */
	get(id: any): vCACCAFEBusinessGroup;
	/**
	 * @param group 
	 */
	update(group: vCACCAFEBusinessGroup): void;
	/**
	 * @param id 
	 */
	delete(id: any): void;
	/**
	 * @param group 
	 */
	create(group: vCACCAFEBusinessGroup): any;
	getAll(): vCACCAFEBusinessGroup[];
}

declare interface vCACCAFECallbackEvaluator {
	getConstantValue(): any;
	isConstant(): boolean;
	/**
	 * @param bVal 
	 */
	fromBoolean(bVal: boolean): any;
	/**
	 * @param sVal 
	 */
	fromString(sVal: string): any;
	/**
	 * @param lVal 
	 */
	fromInt(lVal: number): any;
	/**
	 * @param dVal 
	 */
	fromDecimal(dVal: number): any;
}

declare interface vCACCAFECallbackOperation {
}

declare interface vCACCAFECatalogAdminCatalogItemService {
	/**
	 * This method invokes the SDK method AdminCatalogItemService.getCatalogItemsNotAssociatedWithAnyService(Pageable pageable).
	 * @param pageable 
	 */
	getCatalogItemsNotAssociatedWithAnyService(pageable: any): vCACCAFEPagedResources;
	/**
	 * This method invokes the SDK method AdminCatalogItemService.getEntitlementsForCatalogItem(String catalogItemId, String serviceId, Pageable pageable).
	 * @param catalogItemId 
	 * @param serviceId 
	 * @param pageable 
	 */
	getEntitlementsForCatalogItem(catalogItemId: string, serviceId: string, pageable: any): vCACCAFEPagedResources;
	/**
	 * This method invokes the SDK method AdminCatalogItemService.getEntitlementsForcatalogItem(String catalogItemId, Pageable pageable).
	 * @param catalogItemId 
	 * @param pageable 
	 */
	getEntitlementsForcatalogItem(catalogItemId: string, pageable: any): vCACCAFEPagedResources;
	/**
	 * This method invokes the SDK method AdminCatalogItemService.getCatalogItem(String catalogItemId).
	 * @param catalogItemId 
	 */
	getCatalogItem(catalogItemId: string): vCACCAFECatalogItem;
	/**
	 * This method invokes the SDK method AdminCatalogItemService.getCatalogItems(Pageable pageable).
	 * @param pageable 
	 */
	getCatalogItems(pageable: any): vCACCAFEPagedResources;
	/**
	 * This method invokes the SDK method AdminCatalogItemService.updateCatalogItem(CatalogItem catalogItem).
	 * @param catalogItem 
	 */
	updateCatalogItem(catalogItem: vCACCAFECatalogItem): void;
	/**
	 * This method invokes the SDK method AdminCatalogItemService.updateCatalogItemForUpgrade(CatalogItem catalogItem).
	 * @param catalogItem 
	 */
	updateCatalogItemForUpgrade(catalogItem: vCACCAFECatalogItem): void;
}

declare interface vCACCAFECatalogAdminCatalogItemTypeService {
	/**
	 * This method invokes the SDK method AdminCatalogItemTypeService.getCatalogItemTypes(Pageable pageable).
	 * @param pageable 
	 */
	getCatalogItemTypes(pageable: any): vCACCAFECatalogItemType[];
}

declare interface vCACCAFECatalogAdminResourceOperationService {
	/**
	 * This method invokes the SDK method AdminResourceOperationService.findEntitlementsForOperation(String operationId, Pageable pagingInfo).
	 * @param operationId 
	 * @param pagingInfo 
	 */
	findEntitlementsForOperation(operationId: string, pagingInfo: any): vCACCAFEEntitlement[];
	/**
	 * This method invokes the SDK method AdminResourceOperationService.findResourceOperations(Pageable pagingInfo).
	 * @param pagingInfo 
	 */
	findResourceOperations(pagingInfo: any): any[];
	/**
	 * This method invokes the SDK method AdminResourceOperationService.getResourceOperation(String operationId).
	 * @param operationId 
	 */
	getResourceOperation(operationId: string): any;
}

declare interface vCACCAFECatalogAdminResourceTypeService {
	/**
	 * This method invokes the SDK method AdminResourceTypeService.getResourceTypes(Pageable pageInfo).
	 * @param pageInfo 
	 */
	getResourceTypes(pageInfo: any): vCACCAFEPagedResources;
}

declare interface vCACCAFECatalogApprovalRequestCallbackService {
	/**
	 * Replaces the specified approval policy by the provided one.
	 * @param policyId 
	 * @param replacementPolicy 
	 */
	replaceApprovalPolicy(policyId: string, replacementPolicy: vCACCAFEApprovalPolicy): void;
	/**
	 * Gets all the approval policy usages using a specified approval policy.
	 * @param page 
	 * @param policyId 
	 */
	getApprovalPolicyUsage(page: any, policyId: string): vCACCAFEApprovalPolicyUsage[];
}

declare class vCACCAFECatalogChildResource {
	constructor();
	constructor();
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	getStatus(): vCACCAFEResourceStatus;
	getIconId(): string;
	/**
	 * @param value 
	 */
	setIconId(value: string): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	/**
	 * @param value 
	 */
	setStatus(value: vCACCAFEResourceStatus): void;
}

declare interface vCACCAFECatalogClient {
	getCatalogServiceAdminService(): vCACCAFECatalogServiceAdminService;
	getCatalogAdminResourceOperationService(): vCACCAFECatalogAdminResourceOperationService;
	getCatalogAdminCatalogItemService(): vCACCAFECatalogAdminCatalogItemService;
	getCatalogEntitlementService(): vCACCAFECatalogEntitlementService;
	getCatalogConsumerEntitledCatalogItemService(): vCACCAFECatalogConsumerEntitledCatalogItemService;
	getCatalogAdminResourceTypeService(): vCACCAFECatalogAdminResourceTypeService;
	getCatalogConsumerRequestService(): vCACCAFECatalogConsumerRequestService;
	getCatalogConsumerResourceService(): vCACCAFECatalogConsumerResourceService;
	getCatalogAdminCatalogItemTypeService(): vCACCAFECatalogAdminCatalogItemTypeService;
	getCatalogApprovalRequestCallbackService(): vCACCAFECatalogApprovalRequestCallbackService;
	getCatalogConsumerCalendarEventService(): vCACCAFECatalogConsumerCalendarEventService;
	getCatalogConsumerCatalogItemService(): vCACCAFECatalogConsumerCatalogItemService;
	getCatalogConsumerResourceTypeService(): vCACCAFECatalogConsumerResourceTypeService;
	getCatalogConsumerServiceService(): vCACCAFECatalogConsumerServiceService;
	getCatalogIconService(): vCACCAFECatalogIconService;
	/**
	 * Does a POST request expanding the given template variables in order.
	 * @param resourceUrl 
	 * @param object 
	 * @param urlVariables 
	 */
	postWithVariables(resourceUrl: string, object: any, urlVariables: string[]): vCACCAFEServiceResponse;
	/**
	 * Does a PUT request expanding the given template variables in order.
	 * @param resourceUrl 
	 * @param object 
	 * @param urlVariables 
	 */
	putWithVariables(resourceUrl: string, object: any, urlVariables: string[]): vCACCAFEServiceResponse;
	/**
	 * Does a DELETE request expanding the given template variables in order.
	 * @param resourceUrl 
	 * @param urlVariables 
	 */
	deleteWithVariables(resourceUrl: string, urlVariables: string[]): void;
	/**
	 * Gets the REST client URL.
	 */
	getUrl(): string;
	/**
	 * Indicates whether the current connection is already closed or not.
	 */
	isClosed(): boolean;
	/**
	 * Does a GET request for a file and allows specifying 'Accept' header.
	 * @param resourceUrl 
	 * @param acceptHeaderValue 
	 */
	getFileWithAcceptHeader(resourceUrl: string, acceptHeaderValue: string): any;
	/**
	 * Does a POST request with a file.
	 * @param resourceUrl 
	 * @param object 
	 */
	postFile(resourceUrl: string, object: any): vCACCAFEServiceResponse;
	/**
	 * Does a GET request expanding the given template variables in order.
	 * @param resourceUrl 
	 * @param urlVariables 
	 */
	getWithVariables(resourceUrl: string, urlVariables: string[]): any;
	/**
	 * Does a GET request.
	 * @param resourceUrl 
	 */
	get(resourceUrl: string): any;
	/**
	 * Does a PUT request.
	 * @param resourceUrl 
	 * @param object 
	 */
	put(resourceUrl: string, object: any): vCACCAFEServiceResponse;
	/**
	 * Does a DELETE request.
	 * @param resourceUrl 
	 */
	delete(resourceUrl: string): void;
	/**
	 * Invalidates the current connection.
	 */
	close(): void;
	/**
	 * Does a GET request for a file.
	 * @param resourceUrl 
	 */
	getFile(resourceUrl: string): any;
	/**
	 * Does a POST request.
	 * @param resourceUrl 
	 * @param object 
	 */
	post(resourceUrl: string, object: any): vCACCAFEServiceResponse;
}

declare interface vCACCAFECatalogConsumerCalendarEventService {
	/**
	 * This method invokes the SDK method ConsumerCalendarEventService.getCalendarEvents(Pageable pageable, String start, String end).
	 * @param pageable 
	 * @param start 
	 * @param end 
	 */
	getCalendarEvents(pageable: any, start: string, end: string): vCACCAFEConsumerCalendarEvent[];
}

declare interface vCACCAFECatalogConsumerCatalogItemService {
	/**
	 * This method is deprecated, use the method vCACCAFECatalogConsumerEntitledCatalogItemService.getEntitledCatalogItemsByServiceOnBehalfOf(Pageable pageable, String serviceId, String onBehalfOf).
	 * @param pageable 
	 * @param serviceId 
	 * @param onBehalfOf 
	 */
	getCatalogItemsByServiceOnBehalfOf(pageable: any, serviceId: string, onBehalfOf: string): vCACCAFECatalogItem[];
	/**
	 * This method invokes the SDK method ConsumerCatalogItemService.getCatalogItem(UUID catalogItemId).
	 * @param catalogItemId 
	 */
	getCatalogItemByUuid(catalogItemId: any): vCACCAFECatalogItem;
	/**
	 * This method is deprecated, use the method vCACCAFECatalogConsumerEntitledCatalogItemService.getEntitledCatalogItemsByService(Pageable pageable, String serviceId).
	 * @param pageable 
	 * @param serviceId 
	 */
	getCatalogItemsByService(pageable: any, serviceId: string): vCACCAFECatalogItem[];
	/**
	 * This method invokes the SDK method ConsumerCatalogItemService.getCatalogItems(Pageable pageable).
	 * @param pageable 
	 */
	getCatalogItemsPaged(pageable: any): vCACCAFEPagedResources;
	/**
	 * This method invokes the SDK method ConsumerCatalogItemService.getDetailsForm(UUID catalogItemId, String contextSubtenantId).
	 * @param catalogItemId 
	 * @param contextSubtenantId 
	 */
	getDetailsFormByUuid(catalogItemId: any, contextSubtenantId: string): vCACCAFEForm;
	/**
	 * This method invokes the SDK method ConsumerCatalogItemService.getRequestFormDetailsFieldValues(UUID catalogItemId, String contextSubtenantId, String elementId, ElementValuesPagingInfo pageInfo, ElementValuesRequest elementValuesRequest).
	 * @param catalogItemId 
	 * @param contextSubtenantId 
	 * @param elementId 
	 * @param pageInfo 
	 * @param elementValuesRequest 
	 */
	getRequestFormDetailsFieldValuesByUuid(catalogItemId: any, contextSubtenantId: string, elementId: string, pageInfo: vCACCAFEElementValuesPagingInfo, elementValuesRequest: any): any;
	/**
	 * This method invokes the SDK method ConsumerCatalogItemService.getRequestFormElementMetadata(UUID catalogItemId, String contextSubtenantId, ElementMetadataRequest element).
	 * @param catalogItemId 
	 * @param contextSubtenantId 
	 * @param element 
	 */
	getRequestFormElementMetadataByUuid(catalogItemId: any, contextSubtenantId: string, element: any): any;
	/**
	 * This method invokes the SDK method ConsumerCatalogItemService.getRequestFormFieldValues(UUID catalogItemId, String contextSubtenantId, String elementId, ElementValuesPagingInfo pageInfo, ElementValuesRequest elementValuesRequest).
	 * @param catalogItemId 
	 * @param contextSubtenantId 
	 * @param elementId 
	 * @param pageInfo 
	 * @param elementValuesRequest 
	 */
	getRequestFormFieldValuesByUuid(catalogItemId: any, contextSubtenantId: string, elementId: string, pageInfo: vCACCAFEElementValuesPagingInfo, elementValuesRequest: any): any;
	/**
	 * This method invokes the SDK method ConsumerCatalogItemService.getRequestFormForCatalogItem(UUID catalogItemId, String contextSubtenantId).
	 * @param catalogItemId 
	 * @param contextSubtenantId 
	 */
	getRequestFormForCatalogItemByUuid(catalogItemId: any, contextSubtenantId: string): vCACCAFEForm;
	/**
	 * This method invokes the SDK method ConsumerCatalogItemService.getRequestFormDetailsElementMetadata(UUID catalogItemId, String contextSubtenantId, ElementMetadataRequest element).
	 * @param catalogItemId 
	 * @param contextSubtenantId 
	 * @param element 
	 */
	getRequestFormDetailsElementMetadataByUuid(catalogItemId: any, contextSubtenantId: string, element: any): any;
	/**
	 * This method invokes the SDK method ConsumerCatalogItemService.getCatalogItems(Pageable pageable).
	 * @param pageable 
	 */
	getCatalogItems(pageable: any): vCACCAFECatalogItem[];
	/**
	 * This method invokes the SDK method ConsumerCatalogItemService.getRequestFormForCatalogItem(String catalogItemId, String contextSubtenantId).
	 * @param catalogItemId 
	 * @param contextSubtenantId 
	 */
	getRequestFormForCatalogItem(catalogItemId: string, contextSubtenantId: string): vCACCAFEForm;
}

declare interface vCACCAFECatalogConsumerEntitledCatalogItemService {
	/**
	 * This method invokes the SDK method ConsumerEntitledCatalogItemService.getEntitledCatalogItems(Pageable pageable).
	 * @param pageable 
	 */
	getEntitledCatalogItems(pageable: any): vCACCAFEPagedResources;
	/**
	 * This method invokes the SDK method ConsumerEntitledCatalogItemService.getEntitledCatalogItem(UUID catalogItemId).
	 * @param catalogItemId 
	 */
	getEntitledCatalogItemByUuid(catalogItemId: any): vCACCAFEConsumerEntitledCatalogItem;
	/**
	 * This method invokes the SDK method ConsumerEntitledCatalogItemService.getTemplateCatalogItemProvisioningRequest(String catItemId, String businessGroupId, String requestedFor).
	 * @param catItemId 
	 * @param businessGroupId 
	 * @param requestedFor 
	 */
	getTemplateCatalogItemProvisioningRequestByBusinessGroup(catItemId: string, businessGroupId: string, requestedFor: string): vCACCAFECatalogItemProvisioningRequest;
	/**
	 * This method invokes the SDK method ConsumerEntitledCatalogItemService.submitCatalogItemProvisionRequest(CatalogItemProvisioningRequest provisioningRequest).
	 * @param provisioningRequest 
	 */
	submitCatalogItemProvisionRequest(provisioningRequest: vCACCAFECatalogItemProvisioningRequest): any;
	/**
	 * This method invokes the SDK method ConsumerEntitledCatalogItemService.getEntitledCatalogItemViews(Pageable pageable).
	 * @param pageable 
	 */
	getEntitledCatalogItemViews(pageable: any): vCACCAFEPagedResources;
	/**
	 * This method invokes the SDK method ConsumerEntitledCatalogItemService.getEntitledCatalogItemViews(Pageable pageable, String serviceId).
	 * @param pageable 
	 * @param serviceId 
	 */
	getEntitledCatalogItemViewsByService(pageable: any, serviceId: string): vCACCAFEPagedResources;
	/**
	 * This method invokes the SDK method ConsumerEntitledCatalogItemService.getEntitledCatalogItemViews(Pageable pageable, String serviceId, String onBehalfOf).
	 * @param pageable 
	 * @param serviceId 
	 * @param onBehalfOf 
	 */
	getEntitledCatalogItemViewsByServiceOnBehalfOf(pageable: any, serviceId: string, onBehalfOf: string): vCACCAFEPagedResources;
	/**
	 * This method invokes the SDK method ConsumerEntitledCatalogItemService.getEntitledCatalogItemViews(Pageable pageable, String serviceId, String onBehalfOf, String subtenantId).
	 * @param pageable 
	 * @param serviceId 
	 * @param onBehalfOf 
	 * @param subtenantId 
	 */
	getEntitledCatalogItemViewsByServiceOnBehalfOfBySubtenant(pageable: any, serviceId: string, onBehalfOf: string, subtenantId: string): vCACCAFEPagedResources;
	/**
	 * This method invokes the SDK method ConsumerEntitledCatalogItemService.getEntitledCatalogItems(Pageable pageable, String serviceId).
	 * @param pageable 
	 * @param serviceId 
	 */
	getEntitledCatalogItemsByService(pageable: any, serviceId: string): vCACCAFEPagedResources;
	/**
	 * This method invokes the SDK method ConsumerEntitledCatalogItemService.getEntitledCatalogItems(Pageable pageable, String serviceId, String onBehalfOf).
	 * @param pageable 
	 * @param serviceId 
	 * @param onBehalfOf 
	 */
	getEntitledCatalogItemsByServiceOnBehalfOf(pageable: any, serviceId: string, onBehalfOf: string): vCACCAFEPagedResources;
	/**
	 * This method invokes the SDK method ConsumerEntitledCatalogItemService.getTemplateCatalogItemProvisioningRequest(String catItemId).
	 * @param catItemId 
	 */
	getTemplateCatalogItemProvisioningRequest(catItemId: string): vCACCAFECatalogItemProvisioningRequest;
	/**
	 * This method invokes the SDK method ConsumerEntitledCatalogItemService.getEntitledCatalogItemView(String id).
	 * @param id 
	 */
	getEntitledCatalogItemView(id: string): vCACCAFEConsumerEntitledCatalogItemView;
}

declare interface vCACCAFECatalogConsumerRequestService {
	/**
	 * This method invokes the SDK method ConsumerRequestService.getRequest(String requestId).
	 * @param requestId 
	 */
	getRequest(requestId: string): any;
	/**
	 * This method invokes the SDK method ConsumerRequestService.createRequest(Request request).
	 * @param request 
	 */
	createRequest(request: any): any;
	/**
	 * This method invokes the SDK method ConsumerRequestService.getRequests(Pageable pageable).
	 * @param pageable 
	 */
	getRequests(pageable: any): any;
	/**
	 * This method invokes the SDK method ConsumerRequestService.deleteRequest(String requestId).
	 * @param requestId 
	 */
	deleteRequest(requestId: string): void;
	/**
	 * This method invokes the SDK method ConsumerRequestService.getDetailsFormElementMetadata(String requestID, ElementMetadataRequest request).
	 * @param requestID 
	 * @param request 
	 */
	getDetailsFormElementMetadata(requestID: string, request: any): any;
	/**
	 * This method invokes the SDK method ConsumerRequestService.getDetailsFormFieldValues(String requestId, String elementId, ElementValuesRequest elementValuesRequest).
	 * @param requestId 
	 * @param elementId 
	 * @param elementValuesRequest 
	 */
	getDetailsFormFieldValues(requestId: string, elementId: string, elementValuesRequest: any): any;
	/**
	 * This method invokes the SDK method ConsumerRequestService.getDetailsForm(String requestId).
	 * @param requestId 
	 */
	getDetailsForm(requestId: string): vCACCAFEForm;
	/**
	 * This method invokes the SDK method ConsumerRequestService.getRequest(URI requestUri).
	 * @param requestUri 
	 */
	getRequestByUri(requestUri: any): any;
	/**
	 * This method invokes the SDK method ConsumerRequestService.getResourceViewsProvisionedByRequest(String requestId, Pageable pageable).
	 * @param requestId 
	 * @param pageable 
	 */
	getResourceViewsProvisionedByRequest(requestId: string, pageable: any): any;
	/**
	 * This method invokes the SDK method ConsumerRequestService.getResourceViewsProvisionedByRequest(URI requestUri, Pageable pageable).
	 * @param requestUri 
	 * @param pageable 
	 */
	getResourceViewsProvisionedByRequestByUri(requestUri: any, pageable: any): any;
	/**
	 * This method invokes the SDK method ConsumerRequestService.getResourcesProvisionedByRequest(String requestId, Pageable pageable).
	 * @param requestId 
	 * @param pageable 
	 */
	getResourcesProvisionedByRequest(requestId: string, pageable: any): any;
	/**
	 * This method invokes the SDK method ConsumerRequestService.updateRequest(Request request).
	 * @param request 
	 */
	updateRequest(request: any): void;
	/**
	 * This method invokes the SDK method ConsumerRequestService.getResourcesProvisionedByRequest(URI requestUri, Pageable pageable).
	 * @param requestUri 
	 * @param pageable 
	 */
	getResourcesProvisionedByRequestByUri(requestUri: any, pageable: any): any;
	/**
	 * This method invokes the SDK method ConsumerRequestService.getSubmissionFormElementMetadata(String requestID, ElementMetadataRequest request).
	 * @param requestID 
	 * @param request 
	 */
	getSubmissionFormElementMetadata(requestID: string, request: any): any;
	/**
	 * This method invokes the SDK method ConsumerRequestService.getSubmissionFormFieldValues(String requestId, String elementId, ElementValuesRequest elementValuesRequest).
	 * @param requestId 
	 * @param elementId 
	 * @param elementValuesRequest 
	 */
	getSubmissionFormFieldValues(requestId: string, elementId: string, elementValuesRequest: any): any;
	/**
	 * This method invokes the SDK method ConsumerRequestService.getSubmissionForm(String requestId).
	 * @param requestId 
	 */
	getSubmissionForm(requestId: string): vCACCAFEForm;
}

declare interface vCACCAFECatalogConsumerResourceService {
	/**
	 * This method invokes the SDK method ConsumerResourceService.getRequestFormFieldValues(String resourceId, String resourceActionId, String elementId, ElementValuesRequest elementValuesRequest).
	 * @param resourceId 
	 * @param resourceActionId 
	 * @param elementId 
	 * @param elementValuesRequest 
	 */
	getRequestFormFieldValues(resourceId: string, resourceActionId: string, elementId: string, elementValuesRequest: any): any;
	/**
	 * This method invokes the SDK method ConsumerResourceService.getResourcesByResourceType(String resourceTypeId, boolean withExtendedData, boolean withOperations, Pageable pageable).
	 * @param resourceTypeId 
	 * @param withExtendedData 
	 * @param withOperations 
	 * @param pageable 
	 */
	getResourcesByResourceTypeWithData(resourceTypeId: string, withExtendedData: boolean, withOperations: boolean, pageable: any): vCACCAFECatalogResource[];
	/**
	 * This method invokes the SDK method ConsumerResourceService.getResourcesList(boolean withExtendedData, boolean withOperations, Pageable pageable).
	 * @param withExtendedData 
	 * @param withOperations 
	 * @param pageable 
	 */
	getResourcesListWithData(withExtendedData: boolean, withOperations: boolean, pageable: any): vCACCAFECatalogResource[];
	/**
	 * This method invokes the SDK method ConsumerResourceService.getResourceView(String id).
	 * @param id 
	 */
	getResourceView(id: string): vCACCAFECatalogResourceView;
	/**
	 * This method invokes the SDK method ConsumerResourceService.getAvailableOperations(String resourceId).
	 * @param resourceId 
	 */
	getAvailableOperationsWithoutPage(resourceId: string): vCACCAFEConsumerResourceOperation[];
	/**
	 * This method invokes the SDK method ConsumerResourceService.getRequestFormElementMetadata(String resourceId, String resourceActionId, ElementMetadataRequest elementStateRequest).
	 * @param resourceId 
	 * @param resourceActionId 
	 * @param elementStateRequest 
	 */
	getRequestFormElementMetadata(resourceId: string, resourceActionId: string, elementStateRequest: any): any;
	/**
	 * This method invokes the SDK method ConsumerResourceService.getResourceViews(boolean managedOnly, boolean withExtendedData, boolean withOperations, Pageable pageable).
	 * @param managedOnly 
	 * @param withExtendedData 
	 * @param withOperations 
	 * @param pageable 
	 */
	getResourceViews(managedOnly: boolean, withExtendedData: boolean, withOperations: boolean, pageable: any): vCACCAFECatalogResourceView[];
	/**
	 * This method invokes the SDK method ConsumerResourceService.getIconForResource(String id).
	 * @param id 
	 */
	getIconForResource(id: string): any;
	/**
	 * This method invokes the SDK method ConsumerResourceService.getResourcesByResourceType(String resourceTypeId, Pageable pageable).
	 * @param resourceTypeId 
	 * @param pageable 
	 */
	getResourcesByResourceType(resourceTypeId: string, pageable: any): vCACCAFECatalogResource[];
	/**
	 * This method invokes the SDK method ConsumerResourceService.getResource(String resourceId).
	 * @param resourceId 
	 */
	getResource(resourceId: string): vCACCAFECatalogResource;
	/**
	 * This method invokes the SDK method ConsumerResourceService.getResourcesList(Pageable pageable).
	 * @param pageable 
	 */
	getResourcesList(pageable: any): vCACCAFECatalogResource[];
	/**
	 * This method invokes the SDK method ConsumerResourceService.getAvailableOperations(String resourceTypeId, Pageable pageable).
	 * @param resourceTypeId 
	 * @param pageable 
	 */
	getAvailableOperations(resourceTypeId: string, pageable: any): vCACCAFEConsumerResourceOperation[];
	/**
	 * This method invokes the SDK method ConsumerResourceService.getRequestForm(String resourceId, String resourceActionId).
	 * @param resourceId 
	 * @param resourceActionId 
	 */
	getRequestForm(resourceId: string, resourceActionId: string): vCACCAFEForm;
	/**
	 * This method invokes the SDK method ConsumerResourceService.getTemplateResourceRequest(String resourceId, String resourceActionId).
	 * @param resourceId 
	 * @param resourceActionId 
	 */
	getTemplateResourceRequest(resourceId: string, resourceActionId: string): vCACCAFECatalogResourceRequest;
	/**
	 * This method invokes the SDK method ConsumerResourceService.submitResourceRequest(CatalogResourceRequest request).
	 * @param request 
	 */
	submitResourceRequest(request: vCACCAFECatalogResourceRequest): any;
}

declare interface vCACCAFECatalogConsumerResourceTypeService {
	/**
	 * This method invokes the SDK method ConsumerResourceTypeService.getResourceTypesList(Pageable pageable).
	 * @param pageable 
	 */
	getResourceTypesList(pageable: any): vCACCAFEResourceType[];
	/**
	 * This method invokes the SDK method ConsumerResourceTypeService.getResourceType(String id).
	 * @param id 
	 */
	getResourceType(id: string): vCACCAFEResourceType;
}

declare interface vCACCAFECatalogConsumerServiceService {
	/**
	 * This method invokes the SDK method ConsumerServiceService.getServiceById(UUID serviceId).
	 * @param serviceId 
	 */
	getServiceByIdByUuid(serviceId: any): vCACCAFEService;
	/**
	 * This method invokes the SDK method ConsumerServiceService.getServiceIcon(UUID serviceId).
	 * @param serviceId 
	 */
	getServiceIconByUuid(serviceId: any): any[];
	/**
	 * This method invokes the SDK method ConsumerServiceService.getServices(Pageable pageable).
	 * @param pageable 
	 */
	getServices(pageable: any): vCACCAFEService[];
}

declare interface vCACCAFECatalogDefaultIcons {
	value(): string;
	values(): vCACCAFECatalogDefaultIcons[];
	/**
	 * @param name 
	 */
	valueOf(name: string): vCACCAFECatalogDefaultIcons;
	/**
	 * @param v 
	 */
	fromValue(v: string): vCACCAFECatalogDefaultIcons;
}

declare interface vCACCAFECatalogEntitlementService {
	/**
	 * This method invokes the SDK method EntitlementService.getForUser(String tenantId, Pageable pageable).
	 * @param tenantId 
	 * @param pageable 
	 */
	getForUser(tenantId: string, pageable: any): vCACCAFEEntitlement[];
	/**
	 * This method invokes the SDK method EntitlementService.setOrder(String subtenantId, MultipleLiteral entitlementIds).
	 * @param subtenantId 
	 * @param entitlementIds 
	 */
	setOrder(subtenantId: string, entitlementIds: vCACCAFEMultipleLiteral): void;
	/**
	 * This method invokes the SDK method EntitlementService.get(String tenantId, Pageable pageable).
	 * @param tenantId 
	 * @param pageable 
	 */
	get(tenantId: string, pageable: any): vCACCAFEEntitlement[];
	/**
	 * This method invokes the SDK method EntitlementService.update(Entitlement entitlement).
	 * @param entitlement 
	 */
	update(entitlement: vCACCAFEEntitlement): void;
	/**
	 * This method invokes the SDK method EntitlementService.create(Entitlement entitlement).
	 * @param entitlement 
	 */
	create(entitlement: vCACCAFEEntitlement): any;
	/**
	 * This method invokes the SDK method EntitlementService.get(UUID entitlementId).
	 * @param entitlementId 
	 */
	getByUuid(entitlementId: any): vCACCAFEEntitlement;
}

declare interface vCACCAFECatalogIconService {
	/**
	 * This method invokes the SDK method IconService.createOrUpdateIcon(Icon icon).
	 * @param icon 
	 */
	createOrUpdateIcon(icon: vCACCAFEIcon): any;
	/**
	 * This method invokes the SDK method IconService.downloadIcon(String iconId).
	 * @param iconId 
	 */
	downloadIcon(iconId: string): any[];
}

declare class vCACCAFECatalogItem {
	version: number;
	organization: vCACCAFECatalogOrganizationReference;
	forms: vCACCAFECatalogItemForms;
	name: string;
	id: string;
	description: string;
	status: vCACCAFEPublishStatus;
	dateCreated: any;
	requestable: boolean;
	quota: number;
	serviceRef: vCACCAFELabelledReference;
	isNoteworthy: boolean;
	outputResourceTypeRef: vCACCAFELabelledReference;
	providerBinding: vCACCAFEProviderBinding;
	vcoId: any;
	lastUpdatedDate: any;
	statusName: string;
	callbacks: vCACCAFECatalogItemCallbackSupport;
	iconId: string;
	catalogItemTypeRef: vCACCAFELabelledReference;
	constructor();
	constructor();
	getVersion(): number;
	/**
	 * @param value 
	 */
	setOrganization(value: vCACCAFECatalogOrganizationReference): void;
	getOrganization(): vCACCAFECatalogOrganizationReference;
	getForms(): vCACCAFECatalogItemForms;
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	getStatus(): vCACCAFEPublishStatus;
	/**
	 * @param value 
	 */
	setVersion(value: number): void;
	getDateCreated(): any;
	/**
	 * @param value 
	 */
	setDateCreated(value: any): void;
	/**
	 * @param value 
	 */
	setQuota(value: number): void;
	isRequestable(): boolean;
	/**
	 * @param value 
	 */
	setRequestable(value: boolean): void;
	getQuota(): number;
	getServiceRef(): vCACCAFELabelledReference;
	/**
	 * @param value 
	 */
	setServiceRef(value: vCACCAFELabelledReference): void;
	/**
	 * @param value 
	 */
	setIsNoteworthy(value: boolean): void;
	isIsNoteworthy(): boolean;
	/**
	 * @param value 
	 */
	setCatalogItemTypeRef(value: vCACCAFELabelledReference): void;
	getOutputResourceTypeRef(): vCACCAFELabelledReference;
	/**
	 * @param value 
	 */
	setOutputResourceTypeRef(value: vCACCAFELabelledReference): void;
	/**
	 * @param value 
	 */
	setForms(value: vCACCAFECatalogItemForms): void;
	getProviderBinding(): vCACCAFEProviderBinding;
	/**
	 * @param value 
	 */
	setProviderBinding(value: vCACCAFEProviderBinding): void;
	getVcoId(): any;
	/**
	 * @param undefined 
	 */
	setVcoId(id: any): void;
	getLastUpdatedDate(): any;
	/**
	 * @param value 
	 */
	setLastUpdatedDate(value: any): void;
	getStatusName(): string;
	/**
	 * @param value 
	 */
	setStatusName(value: string): void;
	getCallbacks(): vCACCAFECatalogItemCallbackSupport;
	/**
	 * @param value 
	 */
	setCallbacks(value: vCACCAFECatalogItemCallbackSupport): void;
	getIconId(): string;
	/**
	 * @param value 
	 */
	setIconId(value: string): void;
	getCatalogItemTypeRef(): vCACCAFELabelledReference;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	/**
	 * @param value 
	 */
	setStatus(value: vCACCAFEPublishStatus): void;
}

declare class vCACCAFECatalogItemCallbackSupport {
	constructor();
	constructor();
	isItemInformation(): boolean;
	/**
	 * @param value 
	 */
	setItemInformation(value: boolean): void;
	isItemInitialize(): boolean;
	/**
	 * @param value 
	 */
	setItemInitialize(value: boolean): void;
	isRollback(): boolean;
	/**
	 * @param value 
	 */
	setRollback(value: boolean): void;
	isValidate(): boolean;
	/**
	 * @param value 
	 */
	setValidate(value: boolean): void;
}

declare class vCACCAFECatalogItemForms {
	constructor();
	constructor();
	/**
	 * @param value 
	 */
	setItemDetails(value: any): void;
	getRequestPostApproval(): any;
	/**
	 * @param value 
	 */
	setRequestPostApproval(value: any): void;
	getRequestPreApproval(): any;
	/**
	 * @param value 
	 */
	setRequestPreApproval(value: any): void;
	getRequestSubmission(): any;
	/**
	 * @param value 
	 */
	setRequestSubmission(value: any): void;
	getRequestFormScale(): vCACCAFEFormScale;
	/**
	 * @param value 
	 */
	setRequestFormScale(value: vCACCAFEFormScale): void;
	getItemDetails(): any;
	isCatalogRequestInfoHidden(): boolean;
	/**
	 * @param value 
	 */
	setCatalogRequestInfoHidden(value: boolean): void;
	getRequestDetails(): any;
	/**
	 * @param value 
	 */
	setRequestDetails(value: any): void;
}

declare class vCACCAFECatalogItemProvisioningRequest {
	constructor();
	constructor();
	/**
	 * @param catalogItemId 
	 */
	constructor(catalogItemId: string);
	getData(): any;
	/**
	 * @param obj 
	 */
	equals(obj: any): boolean;
	toString(): string;
	hashCode(): number;
	getType(): string;
	/**
	 * @param description 
	 */
	setDescription(description: string): void;
	getDescription(): string;
	/**
	 * @param businessGroupId 
	 */
	setBusinessGroupId(businessGroupId: string): void;
	getBusinessGroupId(): string;
	/**
	 * @param catalogItemId 
	 */
	setCatalogItemId(catalogItemId: string): void;
	getCatalogItemId(): string;
	getRequestedFor(): string;
	getReasons(): string;
	/**
	 * @param reasons 
	 */
	setReasons(reasons: string): void;
	/**
	 * @param requestedFor 
	 */
	setRequestedFor(requestedFor: string): void;
	/**
	 * @param data 
	 */
	setData(data: any): void;
}

declare class vCACCAFECatalogItemRequest {
	version: number;
	organization: vCACCAFECatalogOrganizationReference;
	name: string;
	id: string;
	state: vCACCAFERequestState;
	description: string;
	dateCreated: any;
	dateApproved: any;
	quote: vCACCAFERequestQuote;
	requestCompletion: vCACCAFERequestCompletion;
	requestorEntitlementId: string;
	executionStatus: vCACCAFERequestExecutionStatus;
	waitingStatus: vCACCAFERequestWaitingStatus;
	stateName: string;
	requestedItemName: string;
	requestedItemDescription: string;
	requestedFor: string;
	vcoId: any;
	requestedBy: string;
	dateCompleted: any;
	reasons: string;
	requestNumber: number;
	requestData: vCACCAFELiteralMap;
	approvalStatus: vCACCAFERequestApprovalStatus;
	preApprovalId: string;
	postApprovalId: string;
	retriesRemaining: number;
	iconId: string;
	dateSubmitted: any;
	lastUpdated: any;
	phase: vCACCAFERequestPhase;
	catalogItemProviderBinding: vCACCAFEProviderBinding;
	catalogItemRef: vCACCAFELabelledReference;
	constructor();
	constructor();
	getCatalogItemProviderBinding(): vCACCAFEProviderBinding;
	/**
	 * @param value 
	 */
	setCatalogItemProviderBinding(value: vCACCAFEProviderBinding): void;
	getCatalogItemRef(): vCACCAFELabelledReference;
	/**
	 * @param value 
	 */
	setCatalogItemRef(value: vCACCAFELabelledReference): void;
	getVersion(): number;
	/**
	 * @param value 
	 */
	setOrganization(value: vCACCAFECatalogOrganizationReference): void;
	getOrganization(): vCACCAFECatalogOrganizationReference;
	/**
	 * @param phase 
	 */
	setPhase(phase: vCACCAFERequestPhase): void;
	getName(): string;
	getId(): string;
	getState(): vCACCAFERequestState;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	/**
	 * @param value 
	 */
	setState(value: vCACCAFERequestState): void;
	getDateCreated(): any;
	/**
	 * @param value 
	 */
	setDateCreated(value: any): void;
	getDateApproved(): any;
	/**
	 * @param value 
	 */
	setDateApproved(value: any): void;
	getQuote(): vCACCAFERequestQuote;
	/**
	 * @param quote 
	 */
	setQuote(quote: vCACCAFERequestQuote): void;
	getRequestCompletion(): vCACCAFERequestCompletion;
	/**
	 * @param value 
	 */
	setRequestCompletion(value: vCACCAFERequestCompletion): void;
	getRequestorEntitlementId(): string;
	/**
	 * @param value 
	 */
	setRequestorEntitlementId(value: string): void;
	getExecutionStatus(): vCACCAFERequestExecutionStatus;
	/**
	 * @param executionStatus 
	 */
	setExecutionStatus(executionStatus: vCACCAFERequestExecutionStatus): void;
	getWaitingStatus(): vCACCAFERequestWaitingStatus;
	/**
	 * @param waitingStatus 
	 */
	setWaitingStatus(waitingStatus: vCACCAFERequestWaitingStatus): void;
	getStateName(): string;
	/**
	 * @param value 
	 */
	setStateName(value: string): void;
	getRequestedItemName(): string;
	/**
	 * @param requestedItemName 
	 */
	setRequestedItemName(requestedItemName: string): void;
	getRequestedItemDescription(): string;
	/**
	 * @param requestedItemDescription 
	 */
	setRequestedItemDescription(requestedItemDescription: string): void;
	getRequestedFor(): string;
	getVcoId(): any;
	/**
	 * @param undefined 
	 */
	setVcoId(id: any): void;
	getRequestedBy(): string;
	getDateCompleted(): any;
	/**
	 * @param value 
	 */
	setDateCompleted(value: any): void;
	getReasons(): string;
	getRequestNumber(): number;
	getRequestData(): vCACCAFELiteralMap;
	getApprovalStatus(): vCACCAFERequestApprovalStatus;
	/**
	 * @param approvalStatus 
	 */
	setApprovalStatus(approvalStatus: vCACCAFERequestApprovalStatus): void;
	getPreApprovalId(): string;
	/**
	 * @param preApprovalId 
	 */
	setPreApprovalId(preApprovalId: string): void;
	getPostApprovalId(): string;
	/**
	 * @param postApprovalId 
	 */
	setPostApprovalId(postApprovalId: string): void;
	getRetriesRemaining(): number;
	getIconId(): string;
	/**
	 * @param iconId 
	 */
	setIconId(iconId: string): void;
	getDateSubmitted(): any;
	/**
	 * @param value 
	 */
	setDateSubmitted(value: any): void;
	/**
	 * @param value 
	 */
	setReasons(value: string): void;
	/**
	 * @param value 
	 */
	setRequestedBy(value: string): void;
	/**
	 * @param value 
	 */
	setRequestedFor(value: string): void;
	/**
	 * @param value 
	 */
	setRequestData(value: vCACCAFELiteralMap): void;
	getLastUpdated(): any;
	/**
	 * @param value 
	 */
	setLastUpdated(value: any): void;
	getPhase(): vCACCAFERequestPhase;
	/**
	 * @param value 
	 */
	setId(value: string): void;
}

declare class vCACCAFECatalogItemType {
	constructor();
	constructor();
	getForms(): vCACCAFECatalogItemForms;
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	getProviderTypeId(): string;
	/**
	 * @param value 
	 */
	setProviderTypeId(value: string): void;
	getRequestSchema(): vCACCAFESchemaInfo;
	/**
	 * @param value 
	 */
	setRequestSchema(value: vCACCAFESchemaInfo): void;
	isRequestable(): boolean;
	/**
	 * @param value 
	 */
	setRequestable(value: boolean): void;
	getItemSchema(): vCACCAFESchemaInfo;
	/**
	 * @param value 
	 */
	setItemSchema(value: vCACCAFESchemaInfo): void;
	isCreateCustomApprovalTypes(): boolean;
	/**
	 * @param value 
	 */
	setCreateCustomApprovalTypes(value: boolean): void;
	/**
	 * @param value 
	 */
	setForms(value: vCACCAFECatalogItemForms): void;
	getProviderId(): string;
	/**
	 * @param value 
	 */
	setProviderId(value: string): void;
	getOutputResourceTypeId(): string;
	/**
	 * @param value 
	 */
	setOutputResourceTypeId(value: string): void;
	getCallbacks(): vCACCAFECatalogItemCallbackSupport;
	/**
	 * @param value 
	 */
	setCallbacks(value: vCACCAFECatalogItemCallbackSupport): void;
	getIconId(): string;
	/**
	 * @param value 
	 */
	setIconId(value: string): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
}

declare class vCACCAFECatalogOrganizationReference {
	constructor();
	constructor();
	toString(): string;
	getTenantRef(): string;
	/**
	 * @param value 
	 */
	setTenantRef(value: string): void;
	getTenantLabel(): string;
	/**
	 * @param value 
	 */
	setTenantLabel(value: string): void;
	getSubtenantLabel(): string;
	/**
	 * @param value 
	 */
	setSubtenantLabel(value: string): void;
	/**
	 * @param value 
	 */
	setSubtenantRef(value: string): void;
	getSubtenantRef(): string;
}

declare class vCACCAFECatalogPrincipal {
	constructor();
	constructor();
	toString(): string;
	getValue(): string;
	/**
	 * @param value 
	 */
	setValue(value: string): void;
	getType(): vCACCAFEPrincipalType;
	getRef(): string;
	/**
	 * @param value 
	 */
	setType(value: vCACCAFEPrincipalType): void;
	/**
	 * @param value 
	 */
	setRef(value: string): void;
	getTenantName(): string;
	/**
	 * @param value 
	 */
	setTenantName(value: string): void;
}

declare interface vCACCAFECatalogProviderCatalogItemService {
	/**
	 * This method invokes the SDK method CatalogItemService.createOrUpdateCatalogItem(String providerId, ProviderCatalogItem catalogItem).
	 * @param providerId 
	 * @param catalogItem 
	 */
	createOrUpdateCatalogItem(providerId: string, catalogItem: vCACCAFEProviderCatalogItem): void;
	/**
	 * This method invokes the SDK method CatalogItemService.deleteCatalogItem(String id, String providerId).
	 * @param id 
	 * @param providerId 
	 */
	deleteCatalogItem(id: string, providerId: string): void;
	/**
	 * This method invokes the SDK method CatalogItemService.getCatalogItem(String id, String providerId).
	 * @param id 
	 * @param providerId 
	 */
	getCatalogItem(id: string, providerId: string): vCACCAFEProviderCatalogItem;
}

declare interface vCACCAFECatalogProviderCatalogItemTypeService {
	/**
	 * This method invokes the SDK method CatalogItemTypeService.createOrUpdateCatalogItemType(CatalogItemType catalogItemType).
	 * @param catalogItemType 
	 */
	createOrUpdateCatalogItemType(catalogItemType: vCACCAFECatalogItemType): void;
	/**
	 * This method invokes the SDK method CatalogItemTypeService.deleteCatalogItemType(String catalogItemTypeId).
	 * @param catalogItemTypeId 
	 */
	deleteCatalogItemType(catalogItemTypeId: string): void;
	/**
	 * This method invokes the SDK method CatalogItemTypeService.getCatalogItemType(String id).
	 * @param id 
	 */
	getCatalogItemType(id: string): vCACCAFECatalogItemType;
}

declare interface vCACCAFECatalogProviderClient {
	getCatalogProviderCatalogItemService(): vCACCAFECatalogProviderCatalogItemService;
	getCatalogProviderCatalogItemTypeService(): vCACCAFECatalogProviderCatalogItemTypeService;
	getCatalogProviderIconService(): vCACCAFECatalogProviderIconService;
	getCatalogProviderResourceActionService(): vCACCAFECatalogProviderResourceActionService;
	getCatalogProviderResourceTypeService(): vCACCAFECatalogProviderResourceTypeService;
	getCatalogProviderServiceService(): vCACCAFECatalogProviderServiceService;
	getCatalogProviderRequestService(): vCACCAFECatalogProviderRequestService;
	getCatalogProviderResourceService(): vCACCAFECatalogProviderResourceService;
	/**
	 * Does a POST request expanding the given template variables in order.
	 * @param resourceUrl 
	 * @param object 
	 * @param urlVariables 
	 */
	postWithVariables(resourceUrl: string, object: any, urlVariables: string[]): vCACCAFEServiceResponse;
	/**
	 * Does a PUT request expanding the given template variables in order.
	 * @param resourceUrl 
	 * @param object 
	 * @param urlVariables 
	 */
	putWithVariables(resourceUrl: string, object: any, urlVariables: string[]): vCACCAFEServiceResponse;
	/**
	 * Does a DELETE request expanding the given template variables in order.
	 * @param resourceUrl 
	 * @param urlVariables 
	 */
	deleteWithVariables(resourceUrl: string, urlVariables: string[]): void;
	/**
	 * Gets the REST client URL.
	 */
	getUrl(): string;
	/**
	 * Indicates whether the current connection is already closed or not.
	 */
	isClosed(): boolean;
	/**
	 * Does a GET request for a file and allows specifying 'Accept' header.
	 * @param resourceUrl 
	 * @param acceptHeaderValue 
	 */
	getFileWithAcceptHeader(resourceUrl: string, acceptHeaderValue: string): any;
	/**
	 * Does a POST request with a file.
	 * @param resourceUrl 
	 * @param object 
	 */
	postFile(resourceUrl: string, object: any): vCACCAFEServiceResponse;
	/**
	 * Does a GET request expanding the given template variables in order.
	 * @param resourceUrl 
	 * @param urlVariables 
	 */
	getWithVariables(resourceUrl: string, urlVariables: string[]): any;
	/**
	 * Does a GET request.
	 * @param resourceUrl 
	 */
	get(resourceUrl: string): any;
	/**
	 * Does a PUT request.
	 * @param resourceUrl 
	 * @param object 
	 */
	put(resourceUrl: string, object: any): vCACCAFEServiceResponse;
	/**
	 * Does a DELETE request.
	 * @param resourceUrl 
	 */
	delete(resourceUrl: string): void;
	/**
	 * Invalidates the current connection.
	 */
	close(): void;
	/**
	 * Does a GET request for a file.
	 * @param resourceUrl 
	 */
	getFile(resourceUrl: string): any;
	/**
	 * Does a POST request.
	 * @param resourceUrl 
	 * @param object 
	 */
	post(resourceUrl: string, object: any): vCACCAFEServiceResponse;
}

declare interface vCACCAFECatalogProviderIconService {
	/**
	 * This method invokes the SDK method IconService.getIcon(String id).
	 * @param id 
	 */
	getIcon(id: string): vCACCAFEIcon;
	/**
	 * This method invokes the SDK method IconService.createOrUpdateIcon(String id, String contentType, String tenant, String subTenant, byte[] content).
	 * @param id 
	 * @param contentType 
	 * @param tenant 
	 * @param subTenant 
	 * @param content 
	 */
	createOrUpdateIcon(id: string, contentType: string, tenant: string, subTenant: string, content: any[]): void;
}

declare interface vCACCAFECatalogProviderRequestService {
	/**
	 * This method invokes the SDK method RequestService.completeRequest(String localId, String providerId, RequestCompletion requestCompletion).
	 * @param localId 
	 * @param providerId 
	 * @param requestCompletion 
	 */
	completeRequest(localId: string, providerId: string, requestCompletion: vCACCAFERequestCompletion): void;
}

declare interface vCACCAFECatalogProviderResourceActionService {
	/**
	 * This method invokes the SDK method ResourceActionService.deleteResourceAction(String actionId).
	 * @param actionId 
	 */
	deleteResourceAction(actionId: string): void;
	/**
	 * This method invokes the SDK method ResourceActionService.createOrUpdateResourceAction(ProviderResourceAction action).
	 * @param action 
	 */
	createOrUpdateResourceAction(action: vCACCAFEProviderResourceAction): string;
	/**
	 * This method invokes the SDK method ResourceActionService.findResourceActions(Pageable pagingInfo).
	 * @param pagingInfo 
	 */
	findResourceActions(pagingInfo: any): vCACCAFEProviderResourceAction[];
	/**
	 * This method invokes the SDK method ResourceActionService.getResourceAction(String actionId).
	 * @param actionId 
	 */
	getResourceAction(actionId: string): vCACCAFEProviderResourceAction;
}

declare interface vCACCAFECatalogProviderResourceService {
	/**
	 * This method invokes the SDK method ResourceService.findByCafeResourceId(String cafeResourceId).
	 * @param cafeResourceId 
	 */
	findByCafeResourceId(cafeResourceId: string): vCACCAFEProviderResource;
	/**
	 * This method invokes the SDK method ResourceService.findResourcesByRequestBindingId(String providerId, String requestBindingId, Pageable pageable).
	 * @param providerId 
	 * @param requestBindingId 
	 * @param pageable 
	 */
	findResourcesByRequestBindingId(providerId: string, requestBindingId: string, pageable: any): vCACCAFEPagedResources;
	/**
	 * This method invokes the SDK method ResourceService.getResource(String id, String providerId).
	 * @param id 
	 * @param providerId 
	 */
	getResource(id: string, providerId: string): vCACCAFEProviderResource;
	/**
	 * This method invokes the SDK method ResourceService.createOrUpdateResource(String providerId, ProviderResource resource).
	 * @param providerId 
	 * @param resource 
	 */
	createOrUpdateResource(providerId: string, resource: vCACCAFEProviderResource): void;
	/**
	 * This method invokes the SDK method ResourceService.deleteResource(String id, String providerId).
	 * @param id 
	 * @param providerId 
	 */
	deleteResource(id: string, providerId: string): void;
}

declare interface vCACCAFECatalogProviderResourceTypeService {
	/**
	 * This method invokes the SDK method ResourceTypeService.deleteResourceType(String resourceTypeId).
	 * @param resourceTypeId 
	 */
	deleteResourceType(resourceTypeId: string): void;
	/**
	 * This method invokes the SDK method ResourceTypeService.findApplicableResourceOperations(String resourceTypeId, Pageable pageable).
	 * @param resourceTypeId 
	 * @param pageable 
	 */
	findApplicableResourceOperations(resourceTypeId: string, pageable: any): any[];
	/**
	 * This method invokes the SDK method ResourceTypeService.getResourceCount(String resourceTypeId).
	 * @param resourceTypeId 
	 */
	getResourceCount(resourceTypeId: string): number;
	/**
	 * This method invokes the SDK method ResourceTypeService.getResourceTypeLifecycleAction(String resourceTypeId, String lifecycleActionId).
	 * @param resourceTypeId 
	 * @param lifecycleActionId 
	 */
	getResourceTypeLifecycleAction(resourceTypeId: string, lifecycleActionId: string): vCACCAFEResourceAction;
	/**
	 * This method invokes the SDK method ResourceTypeService.createOrUpdateResourceType(ResourceType resourceType).
	 * @param resourceType 
	 */
	createOrUpdateResourceType(resourceType: vCACCAFEResourceType): void;
	/**
	 * This method invokes the SDK method ResourceTypeService.getResourceType(String resourceTypeId).
	 * @param resourceTypeId 
	 */
	getResourceType(resourceTypeId: string): vCACCAFEResourceType;
}

declare interface vCACCAFECatalogProviderServiceService {
	/**
	 * This method invokes the SDK method ServiceService.createOrUpdateService(Service service).
	 * @param service 
	 */
	createOrUpdateService(service: vCACCAFEService): void;
	/**
	 * This method invokes the SDK method ServiceService.getService(UUID serviceId).
	 * @param serviceId 
	 */
	getServiceByUuid(serviceId: any): vCACCAFEService;
	/**
	 * This method invokes the SDK method ServiceService.createService(Service service).
	 * @param service 
	 */
	createService(service: vCACCAFEService): any;
	/**
	 * This method invokes the SDK method ServiceService.getServices(Pageable pageable).
	 * @param pageable 
	 */
	getServices(pageable: any): vCACCAFEService[];
}

declare class vCACCAFECatalogResource {
	organization: vCACCAFECatalogOrganizationReference;
	forms: vCACCAFEResourceForms;
	name: string;
	id: string;
	description: string;
	status: vCACCAFEResourceStatus;
	costToDate: any;
	dateCreated: any;
	hasChildren: boolean;
	owners: vCACCAFECatalogPrincipal[];
	totalCost: any;
	hasLease: boolean;
	hasCosts: boolean;
	expenseMonthToDate: vCACCAFECostToDate;
	parentResourceRef: vCACCAFELabelledReference;
	destroyDate: any;
	pendingRequests: vCACCAFEResourceActionRequest[];
	leaseForDisplay: vCACCAFETimeSpan;
	lease: vCACCAFEResourceLease;
	resourceTypeRef: vCACCAFELabelledReference;
	operations: vCACCAFEConsumerResourceOperation[];
	catalogItem: vCACCAFELabelledReference;
	costs: vCACCAFEResourceCosts;
	providerBinding: vCACCAFEProviderBinding;
	vcoId: any;
	resourceData: vCACCAFELiteralMap;
	iconId: string;
	requestId: string;
	requestState: vCACCAFERequestState;
	lastUpdated: any;
	constructor();
	constructor();
	/**
	 * @param value 
	 */
	setOrganization(value: vCACCAFECatalogOrganizationReference): void;
	getOrganization(): vCACCAFECatalogOrganizationReference;
	getForms(): vCACCAFEResourceForms;
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	getStatus(): vCACCAFEResourceStatus;
	getCostToDate(): any;
	/**
	 * @param value 
	 */
	setCostToDate(value: any): void;
	getDateCreated(): any;
	/**
	 * @param value 
	 */
	setDateCreated(value: any): void;
	isHasChildren(): boolean;
	/**
	 * @param value 
	 */
	setHasChildren(value: boolean): void;
	getOwners(): vCACCAFECatalogPrincipal[];
	getTotalCost(): any;
	/**
	 * @param value 
	 */
	setTotalCost(value: any): void;
	isHasLease(): boolean;
	/**
	 * @param value 
	 */
	setHasLease(value: boolean): void;
	isHasCosts(): boolean;
	/**
	 * @param value 
	 */
	setHasCosts(value: boolean): void;
	getExpenseMonthToDate(): vCACCAFECostToDate;
	/**
	 * @param value 
	 */
	setExpenseMonthToDate(value: vCACCAFECostToDate): void;
	getParentResourceRef(): vCACCAFELabelledReference;
	/**
	 * @param value 
	 */
	setParentResourceRef(value: vCACCAFELabelledReference): void;
	getDestroyDate(): any;
	/**
	 * @param value 
	 */
	setDestroyDate(value: any): void;
	getPendingRequests(): vCACCAFEResourceActionRequest[];
	/**
	 * @param value 
	 */
	setCatalogItem(value: vCACCAFELabelledReference): void;
	getLeaseForDisplay(): vCACCAFETimeSpan;
	getLease(): vCACCAFEResourceLease;
	/**
	 * @param value 
	 */
	setLease(value: vCACCAFEResourceLease): void;
	/**
	 * @param value 
	 */
	setLeaseForDisplay(value: vCACCAFETimeSpan): void;
	getResourceTypeRef(): vCACCAFELabelledReference;
	/**
	 * @param value 
	 */
	setResourceTypeRef(value: vCACCAFELabelledReference): void;
	getOperations(): vCACCAFEConsumerResourceOperation[];
	getCatalogItem(): vCACCAFELabelledReference;
	getCosts(): vCACCAFEResourceCosts;
	/**
	 * @param value 
	 */
	setCosts(value: vCACCAFEResourceCosts): void;
	/**
	 * @param value 
	 */
	setForms(value: vCACCAFEResourceForms): void;
	getProviderBinding(): vCACCAFEProviderBinding;
	/**
	 * @param value 
	 */
	setProviderBinding(value: vCACCAFEProviderBinding): void;
	getVcoId(): any;
	/**
	 * @param undefined 
	 */
	setVcoId(id: any): void;
	getResourceData(): vCACCAFELiteralMap;
	/**
	 * @param value 
	 */
	setResourceData(value: vCACCAFELiteralMap): void;
	/**
	 * @param value 
	 */
	setRequestId(value: string): void;
	getIconId(): string;
	/**
	 * @param value 
	 */
	setIconId(value: string): void;
	getRequestId(): string;
	getRequestState(): vCACCAFERequestState;
	/**
	 * @param value 
	 */
	setRequestState(value: vCACCAFERequestState): void;
	getLastUpdated(): any;
	/**
	 * @param value 
	 */
	setLastUpdated(value: any): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	/**
	 * @param value 
	 */
	setStatus(value: vCACCAFEResourceStatus): void;
}

declare class vCACCAFECatalogResourceRequest {
	constructor();
	constructor();
	getData(): any;
	/**
	 * @param obj 
	 */
	equals(obj: any): boolean;
	toString(): string;
	hashCode(): number;
	getType(): string;
	/**
	 * @param description 
	 */
	setDescription(description: string): void;
	getDescription(): string;
	getResourceId(): string;
	/**
	 * @param resourceId 
	 */
	setResourceId(resourceId: string): void;
	getActionId(): string;
	/**
	 * @param actionId 
	 */
	setActionId(actionId: string): void;
	/**
	 * @param data 
	 */
	setData(data: any): void;
}

declare interface vCACCAFECatalogResourceView {
	getData(): any;
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	getStatus(): string;
	getTenantId(): string;
	/**
	 * @param value 
	 */
	setBusinessGroupId(value: string): void;
	getBusinessGroupId(): string;
	/**
	 * @param value 
	 */
	setCatalogItemId(value: string): void;
	getCatalogItemLabel(): string;
	/**
	 * @param value 
	 */
	setCatalogItemLabel(value: string): void;
	getCostToDate(): any;
	/**
	 * @param value 
	 */
	setCostToDate(value: any): void;
	getDateCreated(): any;
	/**
	 * @param value 
	 */
	setDateCreated(value: any): void;
	isHasChildren(): boolean;
	/**
	 * @param value 
	 */
	setHasChildren(value: boolean): void;
	getOwners(): string[];
	getParentResourceId(): string;
	/**
	 * @param value 
	 */
	setParentResourceId(value: string): void;
	getTotalCost(): any;
	/**
	 * @param value 
	 */
	setTotalCost(value: any): void;
	getCatalogItemId(): string;
	getLease(): vCACCAFEResourceLease;
	/**
	 * @param value 
	 */
	setLease(value: vCACCAFEResourceLease): void;
	getResourceType(): string;
	getCosts(): vCACCAFEResourceCosts;
	/**
	 * @param value 
	 */
	setCosts(value: vCACCAFEResourceCosts): void;
	getResourceId(): string;
	/**
	 * @param value 
	 */
	setResourceId(value: string): void;
	/**
	 * @param value 
	 */
	setResourceType(value: string): void;
	/**
	 * @param value 
	 */
	setRequestId(value: string): void;
	getIconId(): string;
	/**
	 * @param value 
	 */
	setIconId(value: string): void;
	getRequestId(): string;
	getRequestState(): vCACCAFERequestState;
	/**
	 * @param value 
	 */
	setRequestState(value: vCACCAFERequestState): void;
	/**
	 * @param value 
	 */
	setData(value: any): void;
	getLastUpdated(): any;
	/**
	 * @param value 
	 */
	setLastUpdated(value: any): void;
	/**
	 * @param value 
	 */
	setStatus(value: string): void;
	/**
	 * @param value 
	 */
	setTenantId(value: string): void;
}

declare interface vCACCAFECatalogServiceAdminService {
	/**
	 * This method invokes the SDK method ServiceAdminService.getCatalogItemsForService(String serviceId, Pageable pageable).
	 * @param serviceId 
	 * @param pageable 
	 */
	getCatalogItemsForService(serviceId: string, pageable: any): vCACCAFEPagedResources;
	/**
	 * This method invokes the SDK method ServiceAdminService.updateService(Service service).
	 * @param service 
	 */
	updateService(service: vCACCAFEService): void;
	/**
	 * This method invokes the SDK method ServiceAdminService.createService(Service service).
	 * @param service 
	 */
	createService(service: vCACCAFEService): vCACCAFEService;
	/**
	 * This method invokes the SDK method ServiceAdminService.getService(String serviceId).
	 * @param serviceId 
	 */
	getService(serviceId: string): vCACCAFEService;
	/**
	 * This method invokes the SDK method ServiceAdminService.getServices(Pageable pageable).
	 * @param pageable 
	 */
	getServices(pageable: any): vCACCAFEPagedResources;
}

declare class vCACCAFECertificateInfo {
	constructor();
	constructor();
	getIssuerName(): string;
	/**
	 * @param issuerName 
	 */
	setIssuerName(issuerName: string): void;
	getPrincipalName(): string;
	/**
	 * @param principalName 
	 */
	setPrincipalName(principalName: string): void;
	getNotValidBefore(): any;
	/**
	 * @param notValidBefore 
	 */
	setNotValidBefore(notValidBefore: any): void;
	getNotValidAfter(): any;
	/**
	 * @param notValidAfter 
	 */
	setNotValidAfter(notValidAfter: any): void;
	getThumbprint(): string;
	/**
	 * @param thumbprint 
	 */
	setThumbprint(thumbprint: string): void;
	/**
	 * @param identityCertificateExists 
	 */
	setIdentityCertificateExists(identityCertificateExists: boolean): void;
}

declare class vCACCAFEChangeWindow {
	constructor();
	constructor();
	getDayOfWeek(): vCACCAFEDay;
	/**
	 * @param value 
	 */
	setDayOfWeek(value: vCACCAFEDay): void;
	getHours(): vCACCAFETimeRange;
	/**
	 * @param value 
	 */
	setHours(value: vCACCAFETimeRange): void;
}

declare class vCACCAFECheckActionIdValidator {
	constructor();
	constructor();
}

declare interface vCACCAFEClusterBehavior {
	value(): string;
	values(): vCACCAFEClusterBehavior[];
	/**
	 * @param name 
	 */
	valueOf(name: string): vCACCAFEClusterBehavior;
	/**
	 * @param v 
	 */
	fromValue(v: string): vCACCAFEClusterBehavior;
}

declare class vCACCAFECoalesceEvaluator {
	constructor();
	/**
	 * @param arguments 
	 */
	constructor(arguments: com.vmware.vcac.platform.content.evaluators.Evaluator[]);
	/**
	 * @param arguments 
	 */
	constructor(arguments: com.vmware.vcac.platform.content.evaluators.Evaluator[]);
	getConstantValue(): any;
	isConstant(): boolean;
	/**
	 * @param bVal 
	 */
	fromBoolean(bVal: boolean): any;
	/**
	 * @param sVal 
	 */
	fromString(sVal: string): any;
	/**
	 * @param lVal 
	 */
	fromInt(lVal: number): any;
	/**
	 * @param dVal 
	 */
	fromDecimal(dVal: number): any;
}

declare class vCACCAFEColumn {
	constructor();
	constructor();
	isFilterable(): boolean;
	/**
	 * @param filterable 
	 */
	setFilterable(filterable: boolean): void;
	isSortable(): boolean;
	/**
	 * @param sortable 
	 */
	setSortable(sortable: boolean): void;
	/**
	 * @param isMultiValued 
	 */
	setMultiValued(isMultiValued: boolean): void;
	getId(): string;
	getState(): vCACCAFEElementState;
	/**
	 * @param description 
	 */
	setDescription(description: string): void;
	getDescription(): string;
	/**
	 * @param state 
	 */
	setState(state: vCACCAFEElementState): void;
	/**
	 * @param label 
	 */
	setLabel(label: string): void;
	getDataType(): any;
	getDisplayAdvice(): any;
	/**
	 * @param displayAdvice 
	 */
	setDisplayAdvice(displayAdvice: any): void;
	getPermissibleValues(): any;
	/**
	 * @param permissibleValues 
	 */
	setPermissibleValues(permissibleValues: any): void;
	getOrderIndex(): number;
	/**
	 * @param orderIndex 
	 */
	setOrderIndex(orderIndex: number): void;
	/**
	 * @param dataType 
	 */
	setDataType(dataType: any): void;
	isMultiValued(): boolean;
	getLabel(): string;
	/**
	 * @param id 
	 */
	setId(id: string): void;
}

declare class vCACCAFEComplexDataType {
	constructor();
	/**
	 * @param componentTypeId 
	 * @param componentId 
	 * @param classId 
	 * @param typeFilter 
	 * @param label 
	 * @param schema 
	 */
	constructor(componentTypeId: string, componentId: string, classId: string, typeFilter: string, label: string, schema: vCACCAFESchema);
	/**
	 * @param componentTypeId 
	 * @param componentId 
	 * @param classId 
	 * @param typeFilter 
	 * @param label 
	 */
	constructor(componentTypeId: string, componentId: string, classId: string, typeFilter: string, label: string);
	/**
	 * @param classId 
	 */
	constructor(classId: string);
	getSchema(): vCACCAFESchema;
	getTypeId(): vCACCAFEDataTypeId;
	getComponentTypeId(): string;
	/**
	 * @param label 
	 */
	setLabel(label: string): void;
	getClassId(): string;
	getTypeFilter(): string;
	getComponentId(): string;
	getLabel(): string;
	getOperatorCategories(): any[];
}

declare class vCACCAFEComplexEvaluator {
	constructor();
	/**
	 * @param classId 
	 * @param evaluatorMap 
	 */
	constructor(classId: string, evaluatorMap: java.util.Map);
	/**
	 * @param componentTypeId 
	 * @param componentId 
	 * @param classId 
	 * @param typeFilter 
	 * @param evaluatorMap 
	 */
	constructor(componentTypeId: string, componentId: string, classId: string, typeFilter: string, evaluatorMap: java.util.Map);
	getConstantValue(): any;
	getEvaluatorMap(): any;
	isConstant(): boolean;
	getComponentTypeId(): string;
	getClassId(): string;
	getTypeFilter(): string;
	getComponentId(): string;
	/**
	 * @param bVal 
	 */
	fromBoolean(bVal: boolean): any;
	/**
	 * @param sVal 
	 */
	fromString(sVal: string): any;
	/**
	 * @param lVal 
	 */
	fromInt(lVal: number): any;
	/**
	 * @param dVal 
	 */
	fromDecimal(dVal: number): any;
}

declare class vCACCAFEComplexLiteral {
	constructor();
	/**
	 * @param definition 
	 * @param values 
	 */
	constructor(definition: com.vmware.vcac.platform.content.schema.ObjectDefinitionReference, values: java.util.Map);
	/**
	 * @param componentTypeId 
	 * @param componentId 
	 * @param classId 
	 * @param typeFilter 
	 * @param values 
	 */
	constructor(componentTypeId: string, componentId: string, classId: string, typeFilter: string, values: java.util.Map);
	/**
	 * @param componentTypeId 
	 * @param componentId 
	 * @param classId 
	 * @param typeFilter 
	 */
	constructor(componentTypeId: string, componentId: string, classId: string, typeFilter: string);
	/**
	 * @param componentTypeId 
	 * @param componentId 
	 * @param classId 
	 * @param typeFilter 
	 * @param values 
	 */
	constructor(componentTypeId: string, componentId: string, classId: string, typeFilter: string, values: vCACCAFELiteralMap);
	/**
	 * @param definition 
	 * @param values 
	 */
	constructor(definition: com.vmware.vcac.platform.content.schema.ObjectDefinitionReference, values: vCACCAFELiteralMap);
	/**
	 * @param definition 
	 */
	constructor(definition: com.vmware.vcac.platform.content.schema.ObjectDefinitionReference);
	hasComponentId(): boolean;
	hasComponentTypeId(): boolean;
	hasTypeFilter(): boolean;
	isList(): boolean;
	/**
	 * @param o 
	 */
	compareTo(o: any): number;
	getValue(): vCACCAFELiteralMap;
	getComponentTypeId(): string;
	getClassId(): string;
	getTypeFilter(): string;
	getComponentId(): string;
	getTypeId(): vCACCAFEDataTypeId;
	/**
	 * @param bVal 
	 */
	fromBoolean(bVal: boolean): any;
	/**
	 * @param obj 
	 */
	safeEquals(obj: any): boolean;
	/**
	 * @param sVal 
	 */
	fromString(sVal: string): any;
	/**
	 * @param lVal 
	 */
	fromInt(lVal: number): any;
	/**
	 * @param dVal 
	 */
	fromDecimal(dVal: number): any;
}

declare class vCACCAFEComponentDeclaration {
	constructor();
	constructor();
	getData(): any;
	getType(): string;
	/**
	 * @param value 
	 */
	setType(value: string): void;
	/**
	 * @param name 
	 * @param fieldValue 
	 */
	addComponentFieldValue(name: string, fieldValue: vCACCAFEComponentFieldValue): void;
	/**
	 * @param name 
	 */
	removeComponentFieldValue(name: string): void;
	/**
	 * @param name 
	 */
	getComponentFieldValue(name: string): vCACCAFEComponentFieldValue;
	getDependson(): string[];
	/**
	 * @param propertyGroups 
	 */
	setPropertyGroups(propertyGroups: string[]): void;
	getComponentProfiles(): vCACCAFECompositeBlueprintComponentProfile[];
	/**
	 * @param componentProfiles 
	 */
	setComponentProfiles(componentProfiles: vCACCAFECompositeBlueprintComponentProfile[]): void;
	getPropertyGroups(): string[];
	/**
	 * @param value 
	 */
	setData(value: any): void;
}

declare class vCACCAFEComponentFieldValue {
	constructor();
	/**
	 * @param defaultValue 
	 */
	constructor(defaultValue: com.vmware.vcac.platform.content.literals.Literal);
	getData(): any;
	/**
	 * @param obj 
	 */
	equals(obj: any): boolean;
	getFacets(): any;
	/**
	 * @param value 
	 */
	setFacets(value: any): void;
	/**
	 * @param facetName 
	 * @param evaluator 
	 */
	addFacetValue(facetName: string, evaluator: any): void;
	getPermittedValues(): any;
	/**
	 * @param value 
	 */
	setPermittedValues(value: any): void;
	/**
	 * @param lVal 
	 */
	fromDefaultValueInt(lVal: number): vCACCAFEComponentFieldValue;
	/**
	 * @param lVal 
	 */
	fromDefaultValueLong(lVal: number): vCACCAFEComponentFieldValue;
	/**
	 * @param sVal 
	 */
	fromDefaultValueString(sVal: string): vCACCAFEComponentFieldValue;
	/**
	 * @param bVal 
	 */
	fromDefaultValueBoolean(bVal: boolean): vCACCAFEComponentFieldValue;
	/**
	 * @param dVal 
	 */
	fromDefaultValueDecimal(dVal: number): vCACCAFEComponentFieldValue;
	/**
	 * @param data 
	 */
	setData(data: any): void;
}

declare class vCACCAFEComponentInstancePlacementRequestStatus {
	constructor();
	constructor();
	getClusterIndex(): number;
	/**
	 * @param value 
	 */
	setClusterIndex(value: number): void;
	getRecommendations(): vCACCAFEComponentPlacementReservationTarget[];
	getResourceStatus(): vCACCAFEComponentInstanceResourcePlacementRequestStatus;
	/**
	 * @param value 
	 */
	setResourceStatus(value: vCACCAFEComponentInstanceResourcePlacementRequestStatus): void;
}

declare class vCACCAFEComponentInstanceResourcePlacementRequestStatus {
	constructor();
	constructor();
	getResourceName(): string;
	/**
	 * @param value 
	 */
	setResourceName(value: string): void;
	getActualCluster(): string;
	/**
	 * @param value 
	 */
	setActualCluster(value: string): void;
	getActualEndpoint(): string;
	/**
	 * @param value 
	 */
	setActualEndpoint(value: string): void;
	getActualReservation(): vCACCAFEComponentPlacementTarget;
	/**
	 * @param value 
	 */
	setActualReservation(value: vCACCAFEComponentPlacementTarget): void;
	getActualDatastore(): vCACCAFEComponentPlacementTarget;
	/**
	 * @param value 
	 */
	setActualDatastore(value: vCACCAFEComponentPlacementTarget): void;
}

declare class vCACCAFEComponentLifecycleTask {
	constructor();
	constructor();
	getVersion(): number;
	getId(): string;
	getScript(): vCACCAFEScript;
	/**
	 * @param value 
	 */
	setVersion(value: number): void;
	/**
	 * @param value 
	 */
	setScript(value: vCACCAFEScript): void;
	getTaskDescriptorRef(): vCACCAFELabelledReference;
	/**
	 * @param value 
	 */
	setTaskDescriptorRef(value: vCACCAFELabelledReference): void;
	getLastUpdated(): any;
	/**
	 * @param value 
	 */
	setLastUpdated(value: any): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	/**
	 * @param value 
	 */
	setCreatedDate(value: any): void;
	getCreatedDate(): any;
}

declare class vCACCAFEComponentPlacementCandidateReservation {
	constructor();
	constructor();
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	getDatastores(): string[];
	/**
	 * @param value 
	 */
	setEndpoint(value: string): void;
	getCluster(): string;
	/**
	 * @param value 
	 */
	setCluster(value: string): void;
	isUnknownCluster(): boolean;
	/**
	 * @param value 
	 */
	setUnknownCluster(value: boolean): void;
	getUnknownDatastores(): string[];
	getEndpoint(): string;
	/**
	 * @param value 
	 */
	setId(value: string): void;
}

declare class vCACCAFEComponentPlacementRequestStatus {
	constructor();
	constructor();
	getCandidates(): vCACCAFEComponentPlacementCandidateReservation[];
	getInstanceStatuses(): vCACCAFEComponentInstancePlacementRequestStatus[];
}

declare class vCACCAFEComponentPlacementReservationTarget {
	constructor();
	constructor();
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	getDatastores(): vCACCAFEComponentPlacementTarget[];
	/**
	 * @param value 
	 */
	setId(value: string): void;
}

declare class vCACCAFEComponentPlacementTarget {
	constructor();
	constructor();
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setId(value: string): void;
}

declare class vCACCAFEComponentPriceInfo {
	constructor();
	constructor();
	getCurrencyCode(): string;
	getCount(): number;
	/**
	 * @param value 
	 */
	setCount(value: number): void;
	getComponentId(): string;
	/**
	 * @param value 
	 */
	setComponentId(value: string): void;
	getSetupFee(): vCACCAFEPriceInfo;
	/**
	 * @param value 
	 */
	setSetupFee(value: vCACCAFEPriceInfo): void;
	getTotalLeasePriceInfo(): vCACCAFEPriceInfo;
	/**
	 * @param value 
	 */
	setTotalLeasePriceInfo(value: vCACCAFEPriceInfo): void;
	getAverageDailyPriceInfo(): vCACCAFEPriceInfo;
	/**
	 * @param value 
	 */
	setAverageDailyPriceInfo(value: vCACCAFEPriceInfo): void;
	/**
	 * @param value 
	 */
	setCurrencyCode(value: string): void;
	getFieldMap(): any;
	/**
	 * @param value 
	 */
	setFieldMap(value: any): void;
}

declare class vCACCAFEComponentRequest {
	constructor();
	constructor();
	getParent(): vCACCAFEBlueprintRequest;
	/**
	 * @param value 
	 */
	setParent(value: vCACCAFEBlueprintRequest): void;
	getProviderBinding(): vCACCAFEProviderAndBinding;
	/**
	 * @param value 
	 */
	setProviderBinding(value: vCACCAFEProviderAndBinding): void;
	getWaitForRequests(): string[];
	getNumNonCompletedDependentRequests(): number;
	/**
	 * @param value 
	 */
	setNumNonCompletedDependentRequests(value: number): void;
	getComponentId(): string;
	/**
	 * @param value 
	 */
	setComponentId(value: string): void;
	getVersion(): number;
	getId(): string;
	getStatus(): vCACCAFERequestStatus;
	/**
	 * @param value 
	 */
	setVersion(value: number): void;
	getStatusName(): string;
	/**
	 * @param value 
	 */
	setStatusName(value: string): void;
	getDateStarted(): any;
	/**
	 * @param value 
	 */
	setDateStarted(value: any): void;
	getDateCompleted(): any;
	/**
	 * @param value 
	 */
	setDateCompleted(value: any): void;
	getCompletionDetails(): string;
	/**
	 * @param value 
	 */
	setCompletionDetails(value: string): void;
	getApprovalStatus(): vCACCAFEApprovalStatus;
	/**
	 * @param value 
	 */
	setApprovalStatus(value: vCACCAFEApprovalStatus): void;
	getPreApprovalId(): string;
	/**
	 * @param value 
	 */
	setPreApprovalId(value: string): void;
	getPostApprovalId(): string;
	/**
	 * @param value 
	 */
	setPostApprovalId(value: string): void;
	getRetriesRemaining(): number;
	/**
	 * @param value 
	 */
	setRetriesRemaining(value: number): void;
	getDateSubmitted(): any;
	/**
	 * @param value 
	 */
	setDateSubmitted(value: any): void;
	getLastUpdated(): any;
	/**
	 * @param value 
	 */
	setLastUpdated(value: any): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	/**
	 * @param value 
	 */
	setStatus(value: vCACCAFERequestStatus): void;
	/**
	 * @param value 
	 */
	setCreatedDate(value: any): void;
	getCreatedDate(): any;
}

declare class vCACCAFEComponentRequestStatus {
	constructor();
	constructor();
	getId(): string;
	getStatus(): vCACCAFERequestStatus;
	getRequestType(): string;
	/**
	 * @param value 
	 */
	setRequestType(value: string): void;
	/**
	 * @param value 
	 */
	setLabel(value: string): void;
	getProviderId(): string;
	/**
	 * @param value 
	 */
	setProviderId(value: string): void;
	getComponentId(): string;
	/**
	 * @param value 
	 */
	setComponentId(value: string): void;
	getDateStarted(): any;
	/**
	 * @param value 
	 */
	setDateStarted(value: any): void;
	getDateCompleted(): any;
	/**
	 * @param value 
	 */
	setDateCompleted(value: any): void;
	getClusterIndex(): number;
	/**
	 * @param value 
	 */
	setClusterIndex(value: number): void;
	getComponentTypeRef(): vCACCAFELabelledReference;
	/**
	 * @param value 
	 */
	setComponentTypeRef(value: vCACCAFELabelledReference): void;
	getComponentRequestId(): string;
	/**
	 * @param value 
	 */
	setComponentRequestId(value: string): void;
	getIconId(): string;
	/**
	 * @param value 
	 */
	setIconId(value: string): void;
	getDateSubmitted(): any;
	/**
	 * @param value 
	 */
	setDateSubmitted(value: any): void;
	getWaitsFor(): any;
	/**
	 * @param value 
	 */
	setWaitsFor(value: any): void;
	getRequestBindingId(): string;
	/**
	 * @param value 
	 */
	setRequestBindingId(value: string): void;
	isRequestDetails(): boolean;
	getComponentPlacementRequestStatus(): vCACCAFEComponentPlacementRequestStatus;
	/**
	 * @param value 
	 */
	setComponentPlacementRequestStatus(value: vCACCAFEComponentPlacementRequestStatus): void;
	/**
	 * @param value 
	 */
	setServiceTypeId(value: string): void;
	/**
	 * @param value 
	 */
	setRequestDetails(value: boolean): void;
	getLabel(): string;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	/**
	 * @param value 
	 */
	setStatus(value: vCACCAFERequestStatus): void;
	getDetails(): string;
	/**
	 * @param value 
	 */
	setDetails(value: string): void;
	getServiceTypeId(): string;
}

declare class vCACCAFEComponentRequestStatusDetails {
	constructor();
	constructor();
	getStatus(): vCACCAFERequestStatus;
	/**
	 * @param value 
	 */
	setRequestId(value: string): void;
	getDateStarted(): any;
	/**
	 * @param value 
	 */
	setDateStarted(value: any): void;
	getDateCompleted(): any;
	/**
	 * @param value 
	 */
	setDateCompleted(value: any): void;
	getRequestId(): string;
	/**
	 * @param value 
	 */
	setStatus(value: vCACCAFERequestStatus): void;
	getDetails(): vCACCAFETaskStatusDetail[];
}

declare class vCACCAFEComponentResourceData {
	constructor();
	constructor();
	getResourceProviderId(): string;
	/**
	 * @param value 
	 */
	setResourceProviderId(value: string): void;
	getResourceProviderBindingId(): string;
	/**
	 * @param value 
	 */
	setResourceProviderBindingId(value: string): void;
	getResourceTypeId(): string;
	getComponentData(): vCACCAFELiteralMap;
	/**
	 * @param value 
	 */
	setComponentData(value: vCACCAFELiteralMap): void;
	/**
	 * @param value 
	 */
	setResourceTypeId(value: string): void;
	getComponentId(): string;
	/**
	 * @param value 
	 */
	setComponentId(value: string): void;
	getClusterIndex(): number;
	/**
	 * @param value 
	 */
	setClusterIndex(value: number): void;
	getCafeResourceId(): string;
	/**
	 * @param value 
	 */
	setCafeResourceId(value: string): void;
}

declare class vCACCAFEComponentResourceRequest {
	constructor();
	constructor();
	getResourceProviderId(): string;
	/**
	 * @param value 
	 */
	setResourceProviderId(value: string): void;
	getResourceProviderBindingId(): string;
	/**
	 * @param value 
	 */
	setResourceProviderBindingId(value: string): void;
	getComponentId(): string;
	/**
	 * @param value 
	 */
	setComponentId(value: string): void;
	getClusterIndex(): number;
	/**
	 * @param value 
	 */
	setClusterIndex(value: number): void;
}

declare class vCACCAFEComponentResourceRootDeployment {
	constructor();
	constructor();
	getProviderBindingId(): string;
	/**
	 * @param value 
	 */
	setProviderBindingId(value: string): void;
	getDepId(): string;
	/**
	 * @param value 
	 */
	setDepId(value: string): void;
	getDepName(): string;
	/**
	 * @param value 
	 */
	setDepName(value: string): void;
	getProviderId(): string;
	/**
	 * @param value 
	 */
	setProviderId(value: string): void;
}

declare class vCACCAFEComponentType {
	constructor();
	constructor();
	getVersion(): number;
	getForms(): any;
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	/**
	 * @param value 
	 */
	setVersion(value: number): void;
	getTenantId(): string;
	getClassId(): string;
	/**
	 * @param value 
	 */
	setClassId(value: string): void;
	/**
	 * @param value 
	 */
	setForms(value: any): void;
	/**
	 * @param value 
	 */
	setCategoryId(value: string): void;
	getCategoryId(): string;
	/**
	 * @param value 
	 */
	setSupportLenientDestroy(value: boolean): void;
	getCatalogItemTypeId(): string;
	/**
	 * @param value 
	 */
	setCatalogItemTypeId(value: string): void;
	getOutputResourceTypeId(): string;
	/**
	 * @param value 
	 */
	setOutputResourceTypeId(value: string): void;
	getContainerFieldId(): string;
	/**
	 * @param value 
	 */
	setContainerFieldId(value: string): void;
	getCallbacks(): vCACCAFEComponentTypeCallbackSupport;
	/**
	 * @param value 
	 */
	setCallbacks(value: vCACCAFEComponentTypeCallbackSupport): void;
	getIdForEntitlements(): string;
	/**
	 * @param value 
	 */
	setIdForEntitlements(value: string): void;
	getRequiredPermissionIds(): string[];
	isSupportLenientDestroy(): boolean;
	getAllocationRelatedResourceTypeIds(): string[];
	getIconId(): string;
	/**
	 * @param value 
	 */
	setIconId(value: string): void;
	/**
	 * @param value 
	 */
	setServiceTypeId(value: string): void;
	getLastUpdated(): any;
	/**
	 * @param value 
	 */
	setLastUpdated(value: any): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	getSchema(): vCACCAFESchema;
	/**
	 * @param value 
	 */
	setSchema(value: vCACCAFESchema): void;
	getServiceTypeId(): string;
	/**
	 * @param value 
	 */
	setCreatedDate(value: any): void;
	/**
	 * @param value 
	 */
	setTenantId(value: string): void;
	getCreatedDate(): any;
	getServiceId(): string;
	/**
	 * @param value 
	 */
	setServiceId(value: string): void;
}

declare class vCACCAFEComponentTypeCallbackSupport {
	constructor();
	constructor();
	/**
	 * @param value 
	 */
	setClusterBehavior(value: vCACCAFEClusterBehavior): void;
	isDestroy(): boolean;
	/**
	 * @param value 
	 */
	setDestroy(value: boolean): void;
	isListOffers(): boolean;
	/**
	 * @param value 
	 */
	setListOffers(value: boolean): void;
	isRollback(): boolean;
	/**
	 * @param value 
	 */
	setRollback(value: boolean): void;
	isValidate(): boolean;
	/**
	 * @param value 
	 */
	setValidate(value: boolean): void;
	/**
	 * @param value 
	 */
	setAllocate(value: boolean): void;
	getClusterBehavior(): vCACCAFEClusterBehavior;
	isAllocate(): boolean;
	isRequestDetails(): boolean;
	/**
	 * @param value 
	 */
	setRequestDetails(value: boolean): void;
}

declare class vCACCAFEComponentTypeCategory {
	constructor();
	constructor();
	getOrder(): number;
	/**
	 * @param value 
	 */
	setOrder(value: number): void;
	getName(): string;
	getProperties(): string;
	/**
	 * @param value 
	 */
	setProperties(value: string): void;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	getType(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	/**
	 * @param value 
	 */
	setType(value: string): void;
	getDescription(): string;
	getIconId(): string;
	/**
	 * @param value 
	 */
	setIconId(value: string): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
}

declare class vCACCAFEComponentTypeExtension {
	constructor();
	constructor();
	getObjectId(): string;
	getComponentType(): vCACCAFEComponentType;
	getName(): string;
	/**
	 * @param name 
	 */
	setName(name: string): void;
	/**
	 * @param description 
	 */
	setDescription(description: string): void;
	getDescription(): string;
	getTenantId(): string;
	/**
	 * @param objectId 
	 */
	setObjectId(objectId: string): void;
	getFieldIds(): string[];
	/**
	 * @param fieldIds 
	 */
	setFieldIds(fieldIds: string[]): void;
	/**
	 * @param componentType 
	 */
	setComponentType(componentType: vCACCAFEComponentType): void;
	/**
	 * @param tenantId 
	 */
	setTenantId(tenantId: string): void;
}

declare class vCACCAFEComponentTypeExtensionValue {
	constructor();
	constructor();
	isEnabled(): boolean;
	getName(): string;
	/**
	 * @param name 
	 */
	setName(name: string): void;
	/**
	 * @param description 
	 */
	setDescription(description: string): void;
	getDescription(): string;
	getTenantId(): string;
	/**
	 * @param valuesMap 
	 */
	setValuesMap(valuesMap: vCACCAFELiteralMap): void;
	getComponentTypeExtension(): vCACCAFEComponentTypeExtension;
	/**
	 * @param componentTypeExtension 
	 */
	setComponentTypeExtension(componentTypeExtension: vCACCAFEComponentTypeExtension): void;
	/**
	 * @param label 
	 */
	setLabel(label: string): void;
	getValuesMap(): vCACCAFELiteralMap;
	getLabel(): string;
	/**
	 * @param isEnabled 
	 */
	setEnabled(isEnabled: boolean): void;
	/**
	 * @param tenantId 
	 */
	setTenantId(tenantId: string): void;
}

declare interface vCACCAFEComponentTypeFormScenario {
	value(): string;
	values(): vCACCAFEComponentTypeFormScenario[];
	/**
	 * @param name 
	 */
	valueOf(name: string): vCACCAFEComponentTypeFormScenario;
	/**
	 * @param v 
	 */
	fromValue(v: string): vCACCAFEComponentTypeFormScenario;
}

declare class vCACCAFECompositeBlueprint {
	version: number;
	layout: any;
	name: string;
	properties: any;
	id: string;
	description: string;
	tenantId: string;
	publishStatus: vCACCAFECompositionPublishStatus;
	publishStatusName: string;
	usingCustomForm: boolean;
	snapshotVersion: any;
	vcoId: any;
	catalogItemTypeId: string;
	propertyGroups: string[];
	components: any;
	externalId: string;
	constructor();
	constructor();
	getVersion(): number;
	getLayout(): any;
	getName(): string;
	getProperties(): any;
	/**
	 * @param properties 
	 */
	setProperties(properties: any): void;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	/**
	 * @param version 
	 */
	setVersion(version: number): void;
	getTenantId(): string;
	getPublishStatus(): vCACCAFECompositionPublishStatus;
	/**
	 * @param value 
	 */
	setPublishStatus(value: vCACCAFECompositionPublishStatus): void;
	getPublishStatusName(): string;
	/**
	 * @param value 
	 */
	setPublishStatusName(value: string): void;
	isUsingCustomForm(): boolean;
	/**
	 * @param usingCustomForm 
	 */
	setUsingCustomForm(usingCustomForm: boolean): void;
	getSnapshotVersion(): any;
	/**
	 * @param value 
	 */
	setSnapshotVersion(value: any): void;
	getVcoId(): any;
	/**
	 * @param undefined 
	 */
	setVcoId(id: any): void;
	/**
	 * @param propertyGroups 
	 */
	setPropertyGroups(propertyGroups: string[]): void;
	getCatalogItemTypeId(): string;
	/**
	 * @param catalogItemTypeId 
	 */
	setCatalogItemTypeId(catalogItemTypeId: string): void;
	getPropertyGroups(): string[];
	getComponents(): any;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	/**
	 * @param value 
	 */
	setComponents(value: any): void;
	/**
	 * @param layout 
	 */
	setLayout(layout: any): void;
	getExternalId(): string;
	/**
	 * @param value 
	 */
	setExternalId(value: string): void;
	/**
	 * @param value 
	 */
	setTenantId(value: string): void;
}

declare class vCACCAFECompositeBlueprintComponentProfile {
	constructor();
	constructor();
	/**
	 * @param id 
	 * @param defaultValueId 
	 * @param permissibleValueNames 
	 */
	constructor(id: string, defaultValueId: string, permissibleValueNames: string[]);
	/**
	 * @param obj 
	 */
	equals(obj: any): boolean;
	hashCode(): number;
	getId(): string;
	getDefaultValueName(): string;
	/**
	 * @param defaultValueName 
	 */
	setDefaultValueName(defaultValueName: string): void;
	getPermissibleValueNames(): string[];
	/**
	 * @param permissibleValueNames 
	 */
	setPermissibleValueNames(permissibleValueNames: string[]): void;
	/**
	 * @param id 
	 */
	setId(id: string): void;
}

declare class vCACCAFECompositeBlueprintInfo {
	constructor();
	constructor();
	getVersion(): number;
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	/**
	 * @param value 
	 */
	setVersion(value: number): void;
	getPublishStatus(): vCACCAFECompositionPublishStatus;
	/**
	 * @param value 
	 */
	setPublishStatus(value: vCACCAFECompositionPublishStatus): void;
	getPublishStatusName(): string;
	/**
	 * @param value 
	 */
	setPublishStatusName(value: string): void;
	getLastUpdated(): any;
	/**
	 * @param value 
	 */
	setLastUpdated(value: any): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	/**
	 * @param value 
	 */
	setCreatedDate(value: any): void;
	getCreatedDate(): any;
}

declare class vCACCAFECompositeBlueprintSpecification {
	constructor();
	constructor();
	getComponents(): any;
	/**
	 * @param value 
	 */
	setComponents(value: any): void;
}

declare interface vCACCAFECompositionBlueprintNodeService {
	/**
	 * This method invokes the SDK method BlueprintNodeService.getTypeSchemaByCafeRequestId(String cafeRequestId, String fieldPath).
	 * @param cafeRequestId 
	 * @param fieldPath 
	 */
	getTypeSchemaByCafeRequestId(cafeRequestId: string, fieldPath: string): vCACCAFESchema;
	/**
	 * This method invokes the SDK method BlueprintNodeService.getDefaultFormValueByCafeRequestId(String cafeRequestId, String fieldPath, String formType, String formElementId, ElementMetadataRequest request).
	 * @param cafeRequestId 
	 * @param fieldPath 
	 * @param formType 
	 * @param formElementId 
	 * @param request 
	 */
	getDefaultFormValueByCafeRequestId(cafeRequestId: string, fieldPath: string, formType: string, formElementId: string, request: any): any;
	/**
	 * This method invokes the SDK method BlueprintNodeService.getBlueprintHierarchyByCafeRequestId(String blueprintId).
	 * @param blueprintId 
	 */
	getBlueprintHierarchyByCafeRequestId(blueprintId: string): vCACCAFEBlueprintNode;
	/**
	 * This method invokes the SDK method BlueprintNodeService.getBlueprintHierarchy(String blueprintId).
	 * @param blueprintId 
	 */
	getBlueprintHierarchy(blueprintId: string): vCACCAFEBlueprintNode;
	/**
	 * This method invokes the SDK method BlueprintNodeService.getBlueprintSchemaByCafeRequestId(String cafeRequestId).
	 * @param cafeRequestId 
	 */
	getBlueprintSchemaByCafeRequestId(cafeRequestId: string): vCACCAFESchema;
	/**
	 * This method invokes the SDK method BlueprintNodeService.getBlueprintSchemaMetadataByCafeRequestId(String cafeRequestId, ElementMetadataRequest elementMetadataRequest).
	 * @param cafeRequestId 
	 * @param elementMetadataRequest 
	 */
	getBlueprintSchemaMetadataByCafeRequestId(cafeRequestId: string, elementMetadataRequest: any): any;
	/**
	 * This method invokes the SDK method BlueprintNodeService.getBlueprintTypeSchemaByCafeRequestId(String cafeRequestId).
	 * @param cafeRequestId 
	 */
	getBlueprintTypeSchemaByCafeRequestId(cafeRequestId: string): vCACCAFESchema;
	/**
	 * This method invokes the SDK method BlueprintNodeService.getComponentFormByCafeRequestId(String cafeRequestId, String fieldPath, String formType).
	 * @param cafeRequestId 
	 * @param fieldPath 
	 * @param formType 
	 */
	getComponentFormByCafeRequestId(cafeRequestId: string, fieldPath: string, formType: string): vCACCAFEForm;
	/**
	 * This method invokes the SDK method BlueprintNodeService.getComponentSchemaByCafeRequestId(String cafeRequestId, String fieldPath).
	 * @param cafeRequestId 
	 * @param fieldPath 
	 */
	getComponentSchemaByCafeRequestId(cafeRequestId: string, fieldPath: string): vCACCAFESchema;
	/**
	 * This method invokes the SDK method BlueprintNodeService.getComponentSchemaMetadataByCafeRequestId(String cafeRequestId, String fieldPath, ElementMetadataRequest elementMetadataRequest).
	 * @param cafeRequestId 
	 * @param fieldPath 
	 * @param elementMetadataRequest 
	 */
	getComponentSchemaMetadataByCafeRequestId(cafeRequestId: string, fieldPath: string, elementMetadataRequest: any): any;
	/**
	 * This method invokes the SDK method BlueprintNodeService.getDefaultFormMetadataByCafeRequestId(String cafeRequestId, String fieldPath, String formType, ElementMetadataRequest request).
	 * @param cafeRequestId 
	 * @param fieldPath 
	 * @param formType 
	 * @param request 
	 */
	getDefaultFormMetadataByCafeRequestId(cafeRequestId: string, fieldPath: string, formType: string, request: any): any;
}

declare interface vCACCAFECompositionClient {
	getCompositionComponentTypeService(): vCACCAFECompositionComponentTypeService;
	getCompositionDataAndSchemaClientService(): vCACCAFECompositionDataAndSchemaClientService;
	getCompositionDeploymentResourceService(): vCACCAFECompositionDeploymentResourceService;
	getCompositionProviderComponentTypeService(): vCACCAFECompositionProviderComponentTypeService;
	getCompositionRequestStatusService(): vCACCAFECompositionRequestStatusService;
	getCompositionComponentProviderRequestService(): vCACCAFECompositionComponentProviderRequestService;
	getCompositionComponentResourceService(): vCACCAFECompositionComponentResourceService;
	getCompositionBlueprintNodeService(): vCACCAFECompositionBlueprintNodeService;
	getCompositionCompositeBlueprintService(): vCACCAFECompositionCompositeBlueprintService;
	/**
	 * Does a POST request expanding the given template variables in order.
	 * @param resourceUrl 
	 * @param object 
	 * @param urlVariables 
	 */
	postWithVariables(resourceUrl: string, object: any, urlVariables: string[]): vCACCAFEServiceResponse;
	/**
	 * Does a PUT request expanding the given template variables in order.
	 * @param resourceUrl 
	 * @param object 
	 * @param urlVariables 
	 */
	putWithVariables(resourceUrl: string, object: any, urlVariables: string[]): vCACCAFEServiceResponse;
	/**
	 * Does a DELETE request expanding the given template variables in order.
	 * @param resourceUrl 
	 * @param urlVariables 
	 */
	deleteWithVariables(resourceUrl: string, urlVariables: string[]): void;
	/**
	 * Gets the REST client URL.
	 */
	getUrl(): string;
	/**
	 * Indicates whether the current connection is already closed or not.
	 */
	isClosed(): boolean;
	/**
	 * Does a GET request for a file and allows specifying 'Accept' header.
	 * @param resourceUrl 
	 * @param acceptHeaderValue 
	 */
	getFileWithAcceptHeader(resourceUrl: string, acceptHeaderValue: string): any;
	/**
	 * Does a POST request with a file.
	 * @param resourceUrl 
	 * @param object 
	 */
	postFile(resourceUrl: string, object: any): vCACCAFEServiceResponse;
	/**
	 * Does a GET request expanding the given template variables in order.
	 * @param resourceUrl 
	 * @param urlVariables 
	 */
	getWithVariables(resourceUrl: string, urlVariables: string[]): any;
	/**
	 * Does a GET request.
	 * @param resourceUrl 
	 */
	get(resourceUrl: string): any;
	/**
	 * Does a PUT request.
	 * @param resourceUrl 
	 * @param object 
	 */
	put(resourceUrl: string, object: any): vCACCAFEServiceResponse;
	/**
	 * Does a DELETE request.
	 * @param resourceUrl 
	 */
	delete(resourceUrl: string): void;
	/**
	 * Invalidates the current connection.
	 */
	close(): void;
	/**
	 * Does a GET request for a file.
	 * @param resourceUrl 
	 */
	getFile(resourceUrl: string): any;
	/**
	 * Does a POST request.
	 * @param resourceUrl 
	 * @param object 
	 */
	post(resourceUrl: string, object: any): vCACCAFEServiceResponse;
}

declare interface vCACCAFECompositionComponentProviderRequestService {
	/**
	 * This method invokes the SDK method ComponentProviderRequestService.completeAllocateRequest(String localId, String providerId, AllocateRequestCompletion requestCompletion, AllocateComponentRequest request).
	 * @param localId 
	 * @param providerId 
	 * @param requestCompletion 
	 * @param request 
	 */
	completeAllocateRequest(localId: string, providerId: string, requestCompletion: any, request: any): void;
}

declare interface vCACCAFECompositionComponentResourceService {
	/**
	 * This method invokes the SDK method ComponentResourceService.getEffectiveSchema(String providerId, String bindingId).
	 * @param providerId 
	 * @param bindingId 
	 */
	getEffectiveSchema(providerId: string, bindingId: string): vCACCAFESchema;
}

declare interface vCACCAFECompositionComponentTypeService {
	/**
	 * This method invokes the SDK method ComponentTypeService.getComponentType(String id).
	 * @param id 
	 */
	getComponentType(id: string): vCACCAFEComponentType;
	/**
	 * This method invokes the SDK method ComponentTypeService.getComponentTypes(Pageable pageInfo).
	 * @param pageInfo 
	 */
	getComponentTypes(pageInfo: any): vCACCAFEPagedResources;
	/**
	 * This method invokes the SDK method ComponentTypeService.getComponentType(URI uri).
	 * @param uri 
	 */
	getComponentTypeByUri(uri: any): vCACCAFEComponentType;
}

declare interface vCACCAFECompositionCompositeBlueprintService {
	/**
	 * This method invokes the SDK method CompositeBlueprintService.updateBlueprintComponentProperties(String id, String componentId, Map<String, ComponentFieldValue> propertyDiff).
	 * @param id 
	 * @param componentId 
	 * @param propertyDiff 
	 */
	updateBlueprintComponentProperties(id: string, componentId: string, propertyDiff: any): vCACCAFECompositeBlueprint;
	/**
	 * This method invokes the SDK method CompositeBlueprintService.updateBlueprint(CompositeBlueprint compositeBlueprint).
	 * @param compositeBlueprint 
	 */
	updateBlueprint(compositeBlueprint: vCACCAFECompositeBlueprint): void;
	/**
	 * This method invokes the SDK method CompositeBlueprintService.updateBlueprintProperties(String id, Map<String, ComponentFieldValue> propertyDiff).
	 * @param id 
	 * @param propertyDiff 
	 */
	updateBlueprintProperties(id: string, propertyDiff: any): vCACCAFECompositeBlueprint;
	/**
	 * This method invokes the SDK method CompositeBlueprintService.updateStatus(String blueprintId, CompositionPublishStatus publisStatus).
	 * @param blueprintId 
	 * @param publisStatus 
	 */
	updateStatus(blueprintId: string, publisStatus: vCACCAFECompositionPublishStatus): void;
	/**
	 * This method invokes the SDK method CompositeBlueprintService.createBlueprint(CompositeBlueprint compositeBlueprint).
	 * @param compositeBlueprint 
	 */
	createBlueprint(compositeBlueprint: vCACCAFECompositeBlueprint): any;
	/**
	 * This method invokes the SDK method CompositeBlueprintService.createOrUpdateBlueprint(CompositeBlueprint compositeBlueprint).
	 * @param compositeBlueprint 
	 */
	createOrUpdateBlueprint(compositeBlueprint: vCACCAFECompositeBlueprint): vCACCAFECompositeBlueprint;
	/**
	 * This method invokes the SDK method CompositeBlueprintService.deleteBlueprint(String id).
	 * @param id 
	 */
	deleteBlueprint(id: string): void;
	/**
	 * This method invokes the SDK method CompositeBlueprintService.getBlueprint(URI uri).
	 * @param uri 
	 */
	getBlueprintByUri(uri: any): vCACCAFECompositeBlueprint;
	/**
	 * This method invokes the SDK method CompositeBlueprintService.getBlueprints(Pageable pageInfo).
	 * @param pageInfo 
	 */
	getBlueprints(pageInfo: any): vCACCAFEPagedResources;
	/**
	 * This method invokes the SDK method CompositeBlueprintService.getBlueprintsWithContent(Pageable pageInfo).
	 * @param pageInfo 
	 */
	getBlueprintsWithContent(pageInfo: any): vCACCAFEPagedResources;
	/**
	 * This method invokes the SDK method CompositeBlueprintService.getBlueprint(String id).
	 * @param id 
	 */
	getBlueprint(id: string): vCACCAFECompositeBlueprint;
}

declare interface vCACCAFECompositionDataAndSchemaClientService {
	/**
	 * This method invokes the SDK method DataAndSchemaClientService.getClassOrTypeSchema(String classId, String instanceId).
	 * @param classId 
	 * @param instanceId 
	 */
	getClassOrTypeSchema(classId: string, instanceId: string): vCACCAFESchema;
	/**
	 * This method invokes the SDK method DataAndSchemaClientService.getInstanceSchema(String classId, String instanceId).
	 * @param classId 
	 * @param instanceId 
	 */
	getInstanceSchema(classId: string, instanceId: string): vCACCAFESchema;
}

declare interface vCACCAFECompositionDeploymentResourceService {
	/**
	 * This method invokes the SDK method DeploymentResourceService.getDeployment(String deploymentId, boolean includeComponentResourceData).
	 * @param deploymentId 
	 * @param includeComponentResourceData 
	 */
	getDeploymentWithComponentResourceData(deploymentId: string, includeComponentResourceData: boolean): vCACCAFEDeploymentResource;
	/**
	 * This method invokes the SDK method DeploymentResourceService.register(String tenantId, DeploymentResourceRequest request, Boolean isUpgrade).
	 * @param tenantId 
	 * @param request 
	 * @param isUpgrade 
	 */
	registerByTenantIdRequestIsUpgrade(tenantId: string, request: vCACCAFEDeploymentResourceRequest, isUpgrade: boolean): vCACCAFEDeploymentResource;
	/**
	 * This method invokes the SDK method DeploymentResourceService.register(String tenantId, DeploymentResourceRequest request, Boolean isUpgrade, Boolean isDryRun).
	 * @param tenantId 
	 * @param request 
	 * @param isUpgrade 
	 * @param isDryRun 
	 */
	registerByTenantIdRequestIsUpgradeIsDryRun(tenantId: string, request: vCACCAFEDeploymentResourceRequest, isUpgrade: boolean, isDryRun: boolean): vCACCAFEDeploymentResource;
	/**
	 * This method invokes the SDK method DeploymentResourceService.registerInPlace(String tenantId, DeploymentResourceUpdateRequest request).
	 * @param tenantId 
	 * @param request 
	 */
	registerInPlace(tenantId: string, request: vCACCAFEDeploymentResourceUpdateRequest): vCACCAFEDeploymentResource;
	/**
	 * This method invokes the SDK method DeploymentResourceService.getDeployment(String deploymentId).
	 * @param deploymentId 
	 */
	getDeployment(deploymentId: string): vCACCAFEDeploymentResource;
	/**
	 * This method invokes the SDK method DeploymentResourceService.register(String tenantId, DeploymentResourceRequest request).
	 * @param tenantId 
	 * @param request 
	 */
	register(tenantId: string, request: vCACCAFEDeploymentResourceRequest): vCACCAFEDeploymentResource;
}

declare interface vCACCAFECompositionProviderComponentTypeService {
	/**
	 * This method invokes the SDK method ProviderComponentTypeService.deleteComponentType(String id).
	 * @param id 
	 */
	deleteComponentType(id: string): void;
	/**
	 * This method invokes the SDK method ProviderComponentTypeService.deleteComponentType(String id, String tenantId).
	 * @param id 
	 * @param tenantId 
	 */
	deleteComponentTypeByIdAndTenant(id: string, tenantId: string): void;
	/**
	 * This method invokes the SDK method ProviderComponentTypeService.createComponentType(ComponentType componentType).
	 * @param componentType 
	 */
	createComponentType(componentType: vCACCAFEComponentType): any;
	/**
	 * This method invokes the SDK method ProviderComponentTypeService.createOrUpdateComponentType(ComponentType componentType).
	 * @param componentType 
	 */
	createOrUpdateComponentType(componentType: vCACCAFEComponentType): void;
	/**
	 * This method invokes the SDK method ProviderComponentTypeService.getComponentType(String id).
	 * @param id 
	 */
	getComponentType(id: string): vCACCAFEComponentType;
	/**
	 * This method invokes the SDK method ProviderComponentTypeService.getComponentTypes(Pageable pageInfo).
	 * @param pageInfo 
	 */
	getComponentTypes(pageInfo: any): vCACCAFEPagedResources;
	/**
	 * This method invokes the SDK method ProviderComponentTypeService.getComponentType(URI uri).
	 * @param uri 
	 */
	getComponentTypeByUri(uri: any): vCACCAFEComponentType;
}

declare interface vCACCAFECompositionPublishStatus {
	value(): string;
	values(): vCACCAFECompositionPublishStatus[];
	/**
	 * @param name 
	 */
	valueOf(name: string): vCACCAFECompositionPublishStatus;
	/**
	 * @param v 
	 */
	fromValue(v: string): vCACCAFECompositionPublishStatus;
}

declare interface vCACCAFECompositionRequestStatusService {
	/**
	 * This method invokes the SDK method RequestStatusService.getRequestStatus(String externalRequestId).
	 * @param externalRequestId 
	 */
	getRequestStatus(externalRequestId: string): vCACCAFEBlueprintRequestStatus;
}

declare class vCACCAFEConditionalEvaluator {
	constructor();
	/**
	 * @param conditionalValues 
	 */
	constructor(conditionalValues: vCACCAFEConditionalEvaluatorCase[]);
	/**
	 * @param conditionalValues 
	 * @param defaultValue 
	 */
	constructor(conditionalValues: vCACCAFEConditionalEvaluatorCase[], defaultValue: com.vmware.vcac.platform.content.evaluators.Evaluator);
	getConditionValuePairs(): vCACCAFEConditionalEvaluatorCase[];
	getConstantValue(): any;
	getDefaultValue(): any;
	isConstant(): boolean;
	/**
	 * @param bVal 
	 */
	fromBoolean(bVal: boolean): any;
	/**
	 * @param sVal 
	 */
	fromString(sVal: string): any;
	/**
	 * @param lVal 
	 */
	fromInt(lVal: number): any;
	/**
	 * @param dVal 
	 */
	fromDecimal(dVal: number): any;
}

declare class vCACCAFEConditionalEvaluatorCase {
	constructor();
	/**
	 * @param condition 
	 * @param value 
	 */
	constructor(condition: com.vmware.vcac.platform.content.criteria.Clause, value: com.vmware.vcac.platform.content.evaluators.Evaluator);
	getCondition(): any;
	getValue(): any;
}

declare class vCACCAFEConfiguration {
	constructor();
	constructor();
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	getHost(): string;
	getPort(): number;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	getTenantId(): string;
	isSsoMode(): boolean;
	/**
	 * @param value 
	 */
	setSsoMode(value: boolean): void;
	isEmbedded(): boolean;
	/**
	 * @param value 
	 */
	setEmbedded(value: boolean): void;
	isSystemDefault(): boolean;
	/**
	 * @param value 
	 */
	setSystemDefault(value: boolean): void;
	getUsername(): string;
	getPassword(): string;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	/**
	 * @param value 
	 */
	setHost(value: string): void;
	/**
	 * @param value 
	 */
	setPort(value: number): void;
	/**
	 * @param value 
	 */
	setPassword(value: string): void;
	/**
	 * @param value 
	 */
	setUsername(value: string): void;
	/**
	 * @param value 
	 */
	setTenantId(value: string): void;
}

declare class vCACCAFEConfigurationCertificateInfo {
	constructor();
	constructor();
	getConfigurationStatus(): vCACCAFEConfigurationStatus;
	/**
	 * @param value 
	 */
	setConfigurationStatus(value: vCACCAFEConfigurationStatus): void;
	getCertificateInfo(): vCACCAFECertificateInfo;
	/**
	 * @param value 
	 */
	setCertificateInfo(value: vCACCAFECertificateInfo): void;
}

declare interface vCACCAFEConfigurationStatus {
	value(): string;
	values(): vCACCAFEConfigurationStatus[];
	/**
	 * @param name 
	 */
	valueOf(name: string): vCACCAFEConfigurationStatus;
	/**
	 * @param v 
	 */
	fromValue(v: string): vCACCAFEConfigurationStatus;
}

declare class vCACCAFEConnectivityStatus {
	constructor();
	constructor();
	getMessage(): string;
	getProvider(): vCACCAFENotificationProvider;
	/**
	 * @param value 
	 */
	setProvider(value: vCACCAFENotificationProvider): void;
	isTestPassed(): boolean;
	/**
	 * @param value 
	 */
	setTestPassed(value: boolean): void;
	/**
	 * @param value 
	 */
	setMessage(value: string): void;
}

declare interface vCACCAFEConnectivityStatusEnum {
	value(): string;
	values(): vCACCAFEConnectivityStatusEnum[];
	/**
	 * @param name 
	 */
	valueOf(name: string): vCACCAFEConnectivityStatusEnum;
	/**
	 * @param v 
	 */
	fromValue(v: string): vCACCAFEConnectivityStatusEnum;
}

declare class vCACCAFEConstantValue {
	constructor();
	/**
	 * @param value 
	 */
	constructor(value: com.vmware.vcac.platform.content.literals.Literal);
	/**
	 * @param value 
	 */
	from(value: any): vCACCAFEConstantValue;
	/**
	 * @param value 
	 */
	fromSecure(value: string): vCACCAFEConstantValue;
	getConstantValue(): any;
	getValue(): any;
	isConstant(): boolean;
	/**
	 * @param bVal 
	 */
	fromBoolean(bVal: boolean): any;
	/**
	 * @param sVal 
	 */
	fromString(sVal: string): any;
	/**
	 * @param lVal 
	 */
	fromInt(lVal: number): any;
	/**
	 * @param dVal 
	 */
	fromDecimal(dVal: number): any;
}

declare class vCACCAFEConsumerCalendarEvent {
	constructor();
	constructor();
	getEnd(): any;
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	/**
	 * @param value 
	 */
	setCalendarId(value: number): void;
	getUiPlaceId(): string;
	/**
	 * @param value 
	 */
	setUiPlaceId(value: string): void;
	getCalendarId(): number;
	/**
	 * @param value 
	 */
	setCatalogItemId(value: string): void;
	getCatalogItemId(): string;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	getStart(): any;
	/**
	 * @param value 
	 */
	setStart(value: any): void;
	/**
	 * @param value 
	 */
	setEnd(value: any): void;
}

declare class vCACCAFEConsumerEntitledCatalogItem {
	constructor();
	constructor();
	/**
	 * @param value 
	 */
	setCatalogItem(value: vCACCAFECatalogItem): void;
	getCatalogItem(): vCACCAFECatalogItem;
	getEntitledOrganizations(): vCACCAFECatalogOrganizationReference[];
}

declare class vCACCAFEConsumerEntitledCatalogItemView {
	constructor();
	constructor();
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	/**
	 * @param value 
	 */
	setCatalogItemId(value: string): void;
	getDateCreated(): any;
	/**
	 * @param value 
	 */
	setDateCreated(value: any): void;
	getCatalogItemId(): string;
	getServiceRef(): vCACCAFELabelledReference;
	/**
	 * @param value 
	 */
	setServiceRef(value: vCACCAFELabelledReference): void;
	/**
	 * @param value 
	 */
	setIsNoteworthy(value: boolean): void;
	isIsNoteworthy(): boolean;
	/**
	 * @param value 
	 */
	setCatalogItemTypeRef(value: vCACCAFELabelledReference): void;
	getOutputResourceTypeRef(): vCACCAFELabelledReference;
	/**
	 * @param value 
	 */
	setOutputResourceTypeRef(value: vCACCAFELabelledReference): void;
	getEntitledOrganizations(): vCACCAFECatalogOrganizationReference[];
	getLastUpdatedDate(): any;
	/**
	 * @param value 
	 */
	setLastUpdatedDate(value: any): void;
	getIconId(): string;
	/**
	 * @param value 
	 */
	setIconId(value: string): void;
	getCatalogItemTypeRef(): vCACCAFELabelledReference;
}

declare class vCACCAFEConsumerResourceOperation {
	name: string;
	id: string;
	type: vCACCAFEResourceOperationType;
	description: string;
	extensionId: string;
	providerTypeId: string;
	hasForm: boolean;
	formScale: vCACCAFEFormScale;
	vcoId: any;
	bindingId: string;
	iconId: string;
	constructor();
	constructor();
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	getType(): vCACCAFEResourceOperationType;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	/**
	 * @param value 
	 */
	setType(value: vCACCAFEResourceOperationType): void;
	getDescription(): string;
	getExtensionId(): string;
	getProviderTypeId(): string;
	/**
	 * @param value 
	 */
	setProviderTypeId(value: string): void;
	isHasForm(): boolean;
	/**
	 * @param value 
	 */
	setHasForm(value: boolean): void;
	getFormScale(): vCACCAFEFormScale;
	/**
	 * @param value 
	 */
	setFormScale(value: vCACCAFEFormScale): void;
	getVcoId(): any;
	/**
	 * @param undefined 
	 */
	setVcoId(id: any): void;
	getBindingId(): string;
	/**
	 * @param value 
	 */
	setBindingId(value: string): void;
	getIconId(): string;
	/**
	 * @param value 
	 */
	setIconId(value: string): void;
	/**
	 * @param value 
	 */
	setExtensionId(value: string): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
}

declare interface vCACCAFEContainsOperator {
	getId(): string;
	/**
	 * @param left 
	 * @param right 
	 */
	evaluate(left: any, right: any): vCACCAFEBooleanLiteral;
	getDataTypeConstraint(): any;
	getCategory(): any;
	getMultiplicityConstraint(): any;
}

declare class vCACCAFEContent {
	constructor();
	constructor();
	getVersion(): number;
	/**
	 * @param value 
	 */
	setMimeType(value: string): void;
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	/**
	 * @param value 
	 */
	setVersion(value: number): void;
	getDependencies(): string[];
	getTenantId(): string;
	getMimeType(): string;
	getContentId(): string;
	/**
	 * @param value 
	 */
	setContentId(value: string): void;
	getContentTypeId(): string;
	/**
	 * @param value 
	 */
	setContentTypeId(value: string): void;
	getLastUpdated(): any;
	/**
	 * @param value 
	 */
	setLastUpdated(value: any): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	/**
	 * @param value 
	 */
	setSubtenantId(value: string): void;
	getSubtenantId(): string;
	/**
	 * @param value 
	 */
	setCreatedDate(value: any): void;
	/**
	 * @param value 
	 */
	setTenantId(value: string): void;
	getCreatedDate(): any;
}

declare class vCACCAFEContentData {
	constructor();
	constructor();
	getData(): any[];
	/**
	 * @param value 
	 */
	setMimeType(value: string): void;
	getMimeType(): string;
	/**
	 * @param value 
	 */
	setData(value: any[]): void;
}

declare class vCACCAFEContentDependency {
	constructor();
	constructor();
	getEntityReference(): vCACCAFEEntityReference;
	/**
	 * @param value 
	 */
	setEntityReference(value: vCACCAFEEntityReference): void;
	/**
	 * @param value 
	 */
	setIsOptional(value: boolean): void;
	isIsOptional(): boolean;
}

declare interface vCACCAFEContentManagementClient {
	/**
	 * Does a PUT request to get content file.
	 * @param resourceUrl 
	 * @param filterInfo 
	 */
	getFile(resourceUrl: string, filterInfo: any): any;
	/**
	 * Does a POST request expanding the given template variables in order.
	 * @param resourceUrl 
	 * @param object 
	 * @param urlVariables 
	 */
	postWithVariables(resourceUrl: string, object: any, urlVariables: string[]): vCACCAFEServiceResponse;
	/**
	 * Does a PUT request expanding the given template variables in order.
	 * @param resourceUrl 
	 * @param object 
	 * @param urlVariables 
	 */
	putWithVariables(resourceUrl: string, object: any, urlVariables: string[]): vCACCAFEServiceResponse;
	/**
	 * Does a DELETE request expanding the given template variables in order.
	 * @param resourceUrl 
	 * @param urlVariables 
	 */
	deleteWithVariables(resourceUrl: string, urlVariables: string[]): void;
	/**
	 * Gets the REST client URL.
	 */
	getUrl(): string;
	/**
	 * Indicates whether the current connection is already closed or not.
	 */
	isClosed(): boolean;
	/**
	 * Does a GET request for a file and allows specifying 'Accept' header.
	 * @param resourceUrl 
	 * @param acceptHeaderValue 
	 */
	getFileWithAcceptHeader(resourceUrl: string, acceptHeaderValue: string): any;
	/**
	 * Does a POST request with a file.
	 * @param resourceUrl 
	 * @param object 
	 */
	postFile(resourceUrl: string, object: any): vCACCAFEServiceResponse;
	/**
	 * Does a GET request expanding the given template variables in order.
	 * @param resourceUrl 
	 * @param urlVariables 
	 */
	getWithVariables(resourceUrl: string, urlVariables: string[]): any;
	/**
	 * Does a GET request.
	 * @param resourceUrl 
	 */
	get(resourceUrl: string): any;
	/**
	 * Does a PUT request.
	 * @param resourceUrl 
	 * @param object 
	 */
	put(resourceUrl: string, object: any): vCACCAFEServiceResponse;
	/**
	 * Does a DELETE request.
	 * @param resourceUrl 
	 */
	delete(resourceUrl: string): void;
	/**
	 * Invalidates the current connection.
	 */
	close(): void;
	/**
	 * Does a POST request.
	 * @param resourceUrl 
	 * @param object 
	 */
	post(resourceUrl: string, object: any): vCACCAFEServiceResponse;
}

declare class vCACCAFEContentType {
	constructor();
	constructor();
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	getClassId(): string;
	/**
	 * @param value 
	 */
	setClassId(value: string): void;
	/**
	 * @param value 
	 */
	setServiceTypeId(value: string): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	getServiceTypeId(): string;
}

declare class vCACCAFEContextPermissibleValueList {
	constructor();
	constructor();
	isCustomAllowed(): boolean;
	/**
	 * @param customAllowed 
	 */
	setCustomAllowed(customAllowed: boolean): void;
}

declare class vCACCAFEContextProperty {
	constructor();
	constructor();
	/**
	 * @param defaultValue 
	 */
	constructor(defaultValue: com.vmware.vcac.platform.content.evaluators.Evaluator);
	/**
	 * @param defaultLiteral 
	 */
	constructor(defaultLiteral: com.vmware.vcac.platform.content.literals.Literal);
	getVisible(): vCACCAFEBooleanLiteral;
	/**
	 * @param visible 
	 */
	setVisible(visible: vCACCAFEBooleanLiteral): void;
	getValue(): vCACCAFEStringLiteral;
	/**
	 * @param value 
	 */
	setValue(value: vCACCAFESecureStringLiteral): void;
	getFacets(): any;
	/**
	 * @param facets 
	 */
	setFacets(facets: any): void;
	/**
	 * @param facetName 
	 */
	getFacetValue(facetName: string): any;
	getEncrypted(): vCACCAFEBooleanLiteral;
	/**
	 * @param encrypted 
	 */
	setEncrypted(encrypted: vCACCAFEBooleanLiteral): void;
}

declare class vCACCAFEContextPropertyDefinition {
	version: number;
	id: string;
	description: string;
	tenantId: string;
	dataType: any;
	isMultiValued: boolean;
	displayAdvice: any;
	permissibleValues: any;
	facets: any;
	orderIndex: number;
	vcoId: any;
	lastUpdated: any;
	label: string;
	createdDate: any;
	constructor();
	constructor();
	getVersion(): number;
	getId(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	/**
	 * @param value 
	 */
	setVersion(value: number): void;
	getTenantId(): string;
	/**
	 * @param value 
	 */
	setLabel(value: string): void;
	getDataType(): any;
	isIsMultiValued(): boolean;
	/**
	 * @param value 
	 */
	setIsMultiValued(value: boolean): void;
	getDisplayAdvice(): any;
	/**
	 * @param value 
	 */
	setDisplayAdvice(value: any): void;
	getPermissibleValues(): any;
	/**
	 * @param permissibleValues 
	 */
	setPermissibleValues(permissibleValues: any): void;
	getFacets(): any;
	/**
	 * @param facets 
	 */
	setFacets(facets: any): void;
	getOrderIndex(): number;
	/**
	 * @param orderIndex 
	 */
	setOrderIndex(orderIndex: number): void;
	/**
	 * @param value 
	 */
	setDataType(value: any): void;
	getVcoId(): any;
	/**
	 * @param undefined 
	 */
	setVcoId(id: any): void;
	getLastUpdated(): any;
	/**
	 * @param value 
	 */
	setLastUpdated(value: any): void;
	getLabel(): string;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	/**
	 * @param value 
	 */
	setCreatedDate(value: any): void;
	/**
	 * @param value 
	 */
	setTenantId(value: string): void;
	getCreatedDate(): any;
}

declare class vCACCAFEContextPropertyGroup {
	name: string;
	properties: any;
	id: string;
	description: string;
	tenantId: string;
	vcoId: any;
	lastUpdated: any;
	label: string;
	constructor();
	constructor();
	/**
	 * @param propertyName 
	 */
	getProperty(propertyName: string): vCACCAFEContextProperty;
	getName(): string;
	getProperties(): any;
	/**
	 * @param properties 
	 */
	setProperties(properties: any): void;
	/**
	 * @param name 
	 */
	setName(name: string): void;
	getId(): string;
	/**
	 * @param description 
	 */
	setDescription(description: string): void;
	getDescription(): string;
	getTenantId(): string;
	/**
	 * @param label 
	 */
	setLabel(label: string): void;
	getVcoId(): any;
	/**
	 * @param undefined 
	 */
	setVcoId(id: any): void;
	getLastUpdated(): any;
	/**
	 * @param value 
	 */
	setLastUpdated(value: any): void;
	getLabel(): string;
	/**
	 * @param id 
	 */
	setId(id: string): void;
	/**
	 * @param tenantId 
	 */
	setTenantId(tenantId: string): void;
}

declare class vCACCAFECostToDate {
	constructor();
	constructor();
	getCurrencyCode(): string;
	getAmount(): number;
	/**
	 * @param value 
	 */
	setAmount(value: number): void;
	getAsOnDate(): any;
	/**
	 * @param value 
	 */
	setAsOnDate(value: any): void;
	/**
	 * @param value 
	 */
	setCurrencyCode(value: string): void;
}

declare class vCACCAFECostUnit {
	constructor();
	constructor();
	getId(): string;
	/**
	 * @param value 
	 */
	setId(value: string): void;
}

declare class vCACCAFECostUnitLimit {
	constructor();
	constructor();
	/**
	 * @param value 
	 */
	setUnit(value: vCACCAFECostUnit): void;
	getValue(): number;
	/**
	 * @param value 
	 */
	setValue(value: number): void;
	getEnforcementType(): string;
	/**
	 * @param value 
	 */
	setEnforcementType(value: string): void;
	getUnit(): vCACCAFECostUnit;
}

declare class vCACCAFECsEndpointConfiguration {
	constructor();
	constructor();
	/**
	 * @param value 
	 */
	setPluginName(value: string): void;
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	getTenantId(): string;
	getPluginName(): string;
	getPluginDisplayName(): string;
	/**
	 * @param value 
	 */
	setPluginDisplayName(value: string): void;
	/**
	 * @param value 
	 */
	setRootObjectUri(value: string): void;
	getRootObjectClass(): string;
	/**
	 * @param value 
	 */
	setRootObjectClass(value: string): void;
	getRootObjectId(): string;
	/**
	 * @param value 
	 */
	setRootObjectId(value: string): void;
	getLastUpdatedDate(): any;
	/**
	 * @param value 
	 */
	setLastUpdatedDate(value: any): void;
	getLastUpdatedBy(): string;
	/**
	 * @param value 
	 */
	setLastUpdatedBy(value: string): void;
	isDeletable(): boolean;
	/**
	 * @param value 
	 */
	setDeletable(value: boolean): void;
	isMultiEndpointAllowed(): boolean;
	/**
	 * @param value 
	 */
	setMultiEndpointAllowed(value: boolean): void;
	getProviderId(): string;
	/**
	 * @param value 
	 */
	setProviderId(value: string): void;
	getRootObjectUri(): string;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	isEditable(): boolean;
	/**
	 * @param value 
	 */
	setEditable(value: boolean): void;
	/**
	 * @param value 
	 */
	setTenantId(value: string): void;
}

declare class vCACCAFECsEndpointData {
	constructor();
	constructor();
	/**
	 * @param value 
	 */
	setValues(value: vCACCAFELiteralMap): void;
	/**
	 * @param value 
	 */
	setNamespace(value: string): void;
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	getValues(): vCACCAFELiteralMap;
	getNamespace(): string;
}

declare class vCACCAFECsEndpointMetadata {
	constructor();
	constructor();
	/**
	 * @param value 
	 */
	setDisplayName(value: string): void;
	isConfigured(): boolean;
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getDisplayName(): string;
	/**
	 * @param value 
	 */
	setConfigured(value: boolean): void;
	isMultiEndpoint(): boolean;
	/**
	 * @param value 
	 */
	setMultiEndpoint(value: boolean): void;
}

declare interface vCACCAFECsFormType {
	value(): string;
	values(): vCACCAFECsFormType[];
	/**
	 * @param name 
	 */
	valueOf(name: string): vCACCAFECsFormType;
	/**
	 * @param v 
	 */
	fromValue(v: string): vCACCAFECsFormType;
}

declare interface vCACCAFECsFormTypeClass {
	value(): string;
	values(): vCACCAFECsFormTypeClass[];
	/**
	 * @param name 
	 */
	valueOf(name: string): vCACCAFECsFormTypeClass;
	/**
	 * @param v 
	 */
	fromValue(v: string): vCACCAFECsFormTypeClass;
}

declare interface vCACCAFECsFormTypeUsageClass {
	value(): string;
	values(): vCACCAFECsFormTypeUsageClass[];
	/**
	 * @param name 
	 */
	valueOf(name: string): vCACCAFECsFormTypeUsageClass;
	/**
	 * @param v 
	 */
	fromValue(v: string): vCACCAFECsFormTypeUsageClass;
}

declare class vCACCAFECsParameter {
	constructor();
	constructor();
	toString(): string;
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	getResourceType(): vCACCAFECsResourceType;
	/**
	 * @param value 
	 */
	setResourceType(value: vCACCAFECsResourceType): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
}

declare class vCACCAFECsResourceOperation {
	version: vCACCAFEVersion;
	forms: vCACCAFEFormScenario[];
	name: string;
	id: string;
	description: string;
	status: vCACCAFEDesignerPublishStatus;
	tenant: string;
	vcoId: any;
	statusName: string;
	inputParameter: vCACCAFECsParameter;
	outputParameter: vCACCAFECsParameter;
	disposal: boolean;
	targetCriteria: any;
	tenantedUuid: vCACCAFETenantedUuid;
	workflowId: string;
	catalogRequestInfoHidden: boolean;
	constructor();
	constructor();
	getVersion(): vCACCAFEVersion;
	getForms(): vCACCAFEFormScenario[];
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	getStatus(): vCACCAFEDesignerPublishStatus;
	/**
	 * @param value 
	 */
	setVersion(value: vCACCAFEVersion): void;
	/**
	 * @param value 
	 */
	setWorkflowId(value: string): void;
	getTenant(): string;
	getVcoId(): any;
	/**
	 * @param undefined 
	 */
	setVcoId(id: any): void;
	getStatusName(): string;
	/**
	 * @param value 
	 */
	setStatusName(value: string): void;
	getInputParameter(): vCACCAFECsParameter;
	/**
	 * @param value 
	 */
	setInputParameter(value: vCACCAFECsParameter): void;
	getOutputParameter(): vCACCAFECsParameter;
	/**
	 * @param value 
	 */
	setOutputParameter(value: vCACCAFECsParameter): void;
	isDisposal(): boolean;
	/**
	 * @param value 
	 */
	setDisposal(value: boolean): void;
	getTargetCriteria(): any;
	/**
	 * @param value 
	 */
	setTargetCriteria(value: any): void;
	isBuiltIn(): boolean;
	/**
	 * @param value 
	 */
	setBuiltIn(value: boolean): void;
	getTenantedUuid(): vCACCAFETenantedUuid;
	/**
	 * @param tenantedUuid 
	 */
	setTenantedUuid(tenantedUuid: vCACCAFETenantedUuid): void;
	getWorkflowId(): string;
	isCatalogRequestInfoHidden(): boolean;
	/**
	 * @param value 
	 */
	setCatalogRequestInfoHidden(value: boolean): void;
	/**
	 * @param value 
	 */
	setTenant(value: string): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	/**
	 * @param value 
	 */
	setStatus(value: vCACCAFEDesignerPublishStatus): void;
}

declare class vCACCAFECsResourceType {
	version: vCACCAFEVersion;
	name: string;
	id: string;
	description: string;
	tenantId: string;
	vcoId: any;
	externalTypeName: string;
	mappingWorkflowId: string;
	mappingWorkflow: vCACCAFEWorkflow;
	mappingScriptActionFqn: string;
	mappingScriptAction: vCACCAFEScriptAction;
	targetCriteria: any;
	listLayout: vCACCAFELayout;
	categoryId: string;
	reservationTypeId: string;
	tenantedUuid: vCACCAFETenantedUuid;
	vcoType: string;
	providerId: string;
	externalTypeId: string;
	detailsForm: vCACCAFEFormScenario;
	constructor();
	constructor();
	getVersion(): vCACCAFEVersion;
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	/**
	 * @param value 
	 */
	setVersion(value: vCACCAFEVersion): void;
	getTenantId(): string;
	getVcoId(): any;
	/**
	 * @param undefined 
	 */
	setVcoId(id: any): void;
	/**
	 * @param value 
	 */
	setExternalTypeId(value: string): void;
	getExternalTypeName(): string;
	/**
	 * @param value 
	 */
	setExternalTypeName(value: string): void;
	getMappingWorkflowId(): string;
	/**
	 * @param value 
	 */
	setMappingWorkflowId(value: string): void;
	getMappingWorkflow(): vCACCAFEWorkflow;
	/**
	 * @param value 
	 */
	setMappingWorkflow(value: vCACCAFEWorkflow): void;
	getMappingScriptActionFqn(): string;
	/**
	 * @param value 
	 */
	setMappingScriptActionFqn(value: string): void;
	getMappingScriptAction(): vCACCAFEScriptAction;
	/**
	 * @param value 
	 */
	setMappingScriptAction(value: vCACCAFEScriptAction): void;
	getTargetCriteria(): any;
	/**
	 * @param value 
	 */
	setTargetCriteria(value: any): void;
	getListLayout(): vCACCAFELayout;
	/**
	 * @param value 
	 */
	setListLayout(value: vCACCAFELayout): void;
	isBuiltIn(): boolean;
	/**
	 * @param value 
	 */
	setBuiltIn(value: boolean): void;
	/**
	 * @param categoryId 
	 */
	setCategoryId(categoryId: string): void;
	getCategoryId(): string;
	getReservationTypeId(): string;
	/**
	 * @param reservationTypeId 
	 */
	setReservationTypeId(reservationTypeId: string): void;
	getTenantedUuid(): vCACCAFETenantedUuid;
	/**
	 * @param tenantedUuid 
	 */
	setTenantedUuid(tenantedUuid: vCACCAFETenantedUuid): void;
	getVcoType(): string;
	/**
	 * @param value 
	 */
	setVcoType(value: string): void;
	getProviderId(): string;
	/**
	 * @param value 
	 */
	setProviderId(value: string): void;
	/**
	 * @param value 
	 */
	setDetailsForm(value: vCACCAFEFormScenario): void;
	getExternalTypeId(): string;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	getDetailsForm(): vCACCAFEFormScenario;
	/**
	 * @param value 
	 */
	setTenantId(value: string): void;
}

declare class vCACCAFECustomGroup {
	constructor();
	constructor();
	getGroupName(): string;
	/**
	 * @param value 
	 */
	setGroupName(value: string): void;
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	getDn(): string;
	/**
	 * @param value 
	 */
	setDn(value: string): void;
	getTenantName(): string;
	/**
	 * @param value 
	 */
	setTenantName(value: string): void;
}

declare class vCACCAFECustomGroupChild {
	constructor();
	constructor();
	getId(): string;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	/**
	 * @param value 
	 */
	setMemberType(value: vCACCAFEMemberType): void;
	getParentDn(): string;
	/**
	 * @param value 
	 */
	setParentDn(value: string): void;
	getParentId(): string;
	/**
	 * @param value 
	 */
	setParentId(value: string): void;
	getDn(): string;
	/**
	 * @param value 
	 */
	setDn(value: string): void;
	getMemberType(): vCACCAFEMemberType;
}

declare class vCACCAFECustomProperty {
	name: string;
	value: string;
	id: number;
	vcoId: any;
	isRuntime: boolean;
	uniqueId: any;
	isEncrypted: boolean;
	constructor();
	constructor();
	getName(): string;
	getValue(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): number;
	/**
	 * @param value 
	 */
	setValue(value: string): void;
	getIsRuntime(): boolean;
	getUniqueId(): any;
	/**
	 * @param value 
	 */
	setIsEncrypted(value: boolean): void;
	getIsEncrypted(): boolean;
	/**
	 * @param value 
	 */
	setIsRuntime(value: boolean): void;
	/**
	 * @param value 
	 */
	setUniqueId(value: any): void;
	toComplexLiteral(): any;
	/**
	 * @param value 
	 */
	setId(value: number): void;
}

declare class vCACCAFECustomPropertyDefinitionMetadata {
	constructor();
	/**
	 * @param value 
	 */
	constructor(value: com.vmware.vcac.platform.content.literals.Literal);
	/**
	 * @param value 
	 */
	constructor(value: com.vmware.vcac.platform.content.evaluators.Evaluator);
	/**
	 * @param value 
	 */
	cloneWithValue(value: any): any;
	/**
	 * @param constraint 
	 */
	mergeConstraint(constraint: any): vCACCAFECustomPropertyDefinitionMetadata;
	getField(): any;
	/**
	 * @param field 
	 */
	setField(field: any): void;
	clone(): any;
	getValue(): any;
}

declare class vCACCAFECustomValidationConstraint {
	constructor();
	/**
	 * @param hasCustomValidation 
	 */
	constructor(hasCustomValidation: boolean);
	/**
	 * @param value 
	 */
	constructor(value: com.vmware.vcac.platform.content.evaluators.Evaluator);
	/**
	 * @param value 
	 */
	cloneWithValue(value: any): vCACCAFECustomValidationConstraint;
	/**
	 * @param hasCustomValidation 
	 */
	fromBoolean(hasCustomValidation: boolean): vCACCAFECustomValidationConstraint;
	/**
	 * @param constraint 
	 */
	mergeConstraint(constraint: any): any;
	clone(): any;
	getValue(): any;
}

declare interface vCACCAFEDataTypeId {
	values(): vCACCAFEDataTypeId[];
	/**
	 * @param name 
	 */
	valueOf(name: string): vCACCAFEDataTypeId;
	getId(): vCACCAFEDataObjectTypeId;
	getMultiplicitySupport(): any;
	getDefaultSingleValueDisplayAdvice(): any;
	getDefaultMultiValueDisplayAdvice(): any;
	getOperatorCategories(): any[];
	getAssociatedLiteralType(): any;
	/**
	 * @param id 
	 */
	fromId(id: string): vCACCAFEDataTypeId;
	/**
	 * @param literal 
	 */
	isValidDataType(literal: any): boolean;
	/**
	 * @param value 
	 */
	forValue(value: string): vCACCAFEDataTypeId;
}

declare class vCACCAFEDataUpdateEvent {
	constructor();
	constructor();
	getData(): vCACCAFELiteralMap;
	getApprovalId(): string;
	getUpdatedAt(): any;
	/**
	 * @param value 
	 */
	setUpdatedAt(value: any): void;
	getUpdatedBy(): string;
	/**
	 * @param value 
	 */
	setUpdatedBy(value: string): void;
	/**
	 * @param value 
	 */
	setApprovalId(value: string): void;
	/**
	 * @param value 
	 */
	setData(value: vCACCAFELiteralMap): void;
}

declare class vCACCAFEDateTimeLiteral {
	constructor();
	/**
	 * @param value 
	 */
	constructor(value: java.util.Date);
	isList(): boolean;
	/**
	 * @param thisValue 
	 * @param otherValue 
	 */
	compareDateTimes(thisValue: any, otherValue: any): number;
	/**
	 * @param other 
	 */
	compareTo(other: any): number;
	getValue(): any;
	getTypeId(): vCACCAFEDataTypeId;
	/**
	 * @param bVal 
	 */
	fromBoolean(bVal: boolean): any;
	/**
	 * @param obj 
	 */
	safeEquals(obj: any): boolean;
	/**
	 * @param sVal 
	 */
	fromString(sVal: string): any;
	/**
	 * @param lVal 
	 */
	fromInt(lVal: number): any;
	/**
	 * @param dVal 
	 */
	fromDecimal(dVal: number): any;
}

declare interface vCACCAFEDay {
	value(): string;
	values(): vCACCAFEDay[];
	/**
	 * @param name 
	 */
	valueOf(name: string): vCACCAFEDay;
	/**
	 * @param v 
	 */
	fromValue(v: string): vCACCAFEDay;
}

declare class vCACCAFEDeallocationRequestCompletion {
	constructor();
	constructor();
	getRequestCallbackAdditionalUrlPath(): string;
	/**
	 * @param value 
	 */
	setRequestCallbackAdditionalUrlPath(value: string): void;
	getServiceCallbackId(): string;
	/**
	 * @param value 
	 */
	setServiceCallbackId(value: string): void;
	getErrMsg(): string;
	/**
	 * @param value 
	 */
	setErrMsg(value: string): void;
	/**
	 * @param value 
	 */
	setRequestId(value: string): void;
	getRequestId(): string;
}

declare class vCACCAFEDecimalLiteral {
	constructor();
	/**
	 * @param value 
	 */
	constructor(value: number);
	getValue(): any;
	getTypeId(): vCACCAFEDataTypeId;
	isList(): boolean;
	/**
	 * @param other 
	 */
	compareTo(other: any): number;
	/**
	 * @param bVal 
	 */
	fromBoolean(bVal: boolean): any;
	/**
	 * @param obj 
	 */
	safeEquals(obj: any): boolean;
	/**
	 * @param sVal 
	 */
	fromString(sVal: string): any;
	/**
	 * @param lVal 
	 */
	fromInt(lVal: number): any;
	/**
	 * @param dVal 
	 */
	fromDecimal(dVal: number): any;
}

declare class vCACCAFEDefaultValueBehavior {
	constructor();
	/**
	 * @param value 
	 */
	constructor(value: com.vmware.vcac.platform.content.evaluators.Evaluator);
	/**
	 * @param value 
	 */
	constructor(value: com.vmware.vcac.platform.content.literals.Literal);
	/**
	 * @param value 
	 */
	cloneWithValue(value: any): any;
	/**
	 * @param constraint 
	 */
	mergeConstraint(constraint: any): vCACCAFEDefaultValueBehavior;
	clone(): any;
	getValue(): any;
}

declare interface vCACCAFEDeliveryStatus {
	value(): string;
	values(): vCACCAFEDeliveryStatus[];
	/**
	 * @param name 
	 */
	valueOf(name: string): vCACCAFEDeliveryStatus;
	/**
	 * @param v 
	 */
	fromValue(v: string): vCACCAFEDeliveryStatus;
}

declare class vCACCAFEDeploymentDetails {
	constructor();
	constructor();
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	getStatus(): vCACCAFEDeploymentStatus;
	getTenantId(): string;
	getSubTenantId(): string;
	/**
	 * @param value 
	 */
	setSubTenantId(value: string): void;
	/**
	 * @param value 
	 */
	setBlueprintId(value: string): void;
	getCafeResourceId(): string;
	/**
	 * @param value 
	 */
	setCafeResourceId(value: string): void;
	getBlueprintId(): string;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	/**
	 * @param value 
	 */
	setStatus(value: vCACCAFEDeploymentStatus): void;
	/**
	 * @param value 
	 */
	setTenantId(value: string): void;
}

declare class vCACCAFEDeploymentResource {
	constructor();
	constructor();
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	getComponentResourceData(): vCACCAFEComponentResourceData[];
	/**
	 * @param value 
	 */
	setBlueprintId(value: string): void;
	getCafeResourceId(): string;
	/**
	 * @param value 
	 */
	setCafeResourceId(value: string): void;
	getBlueprintId(): string;
	/**
	 * @param value 
	 */
	setId(value: string): void;
}

declare class vCACCAFEDeploymentResourceRequest {
	constructor();
	constructor();
	getDeploymentName(): string;
	/**
	 * @param value 
	 */
	setDeploymentName(value: string): void;
	getLeaseDays(): number;
	/**
	 * @param value 
	 */
	setLeaseDays(value: number): void;
	getArchiveDays(): number;
	/**
	 * @param value 
	 */
	setArchiveDays(value: number): void;
	getComponentResources(): vCACCAFEComponentResourceRequest[];
	/**
	 * @param value 
	 */
	setBlueprintId(value: string): void;
	getBlueprintId(): string;
	/**
	 * @param value 
	 */
	setSubtenantId(value: string): void;
	getSubtenantId(): string;
	getPrincipalIds(): string[];
}

declare class vCACCAFEDeploymentResourceUpdateRequest {
	constructor();
	constructor();
	getResourceProviderId(): string;
	/**
	 * @param value 
	 */
	setResourceProviderId(value: string): void;
	getResourceProviderBindingId(): string;
	/**
	 * @param value 
	 */
	setResourceProviderBindingId(value: string): void;
	getComponentResources(): vCACCAFEComponentResourceRequest[];
	/**
	 * @param value 
	 */
	setBlueprintId(value: string): void;
	getBlueprintId(): string;
}

declare interface vCACCAFEDeploymentStatus {
	value(): string;
	values(): vCACCAFEDeploymentStatus[];
	/**
	 * @param name 
	 */
	valueOf(name: string): vCACCAFEDeploymentStatus;
	/**
	 * @param v 
	 */
	fromValue(v: string): vCACCAFEDeploymentStatus;
}

declare interface vCACCAFEDesignerPublishStatus {
	value(): string;
	values(): vCACCAFEDesignerPublishStatus[];
	/**
	 * @param name 
	 */
	valueOf(name: string): vCACCAFEDesignerPublishStatus;
	/**
	 * @param v 
	 */
	fromValue(v: string): vCACCAFEDesignerPublishStatus;
}

declare class vCACCAFEDisplayAuthoritiesEntry {
	constructor();
	constructor();
	getPermissions(): vCACCAFEPermission[];
	/**
	 * @param value 
	 */
	setRole(value: any): void;
	getRole(): any;
}

declare class vCACCAFEDisplayTextBehavior {
	constructor();
	constructor();
	/**
	 * @param value 
	 */
	constructor(value: com.vmware.vcac.platform.content.evaluators.Evaluator);
	/**
	 * @param value 
	 */
	constructor(value: string);
	/**
	 * @param value 
	 */
	cloneWithValue(value: any): any;
	/**
	 * @param constraint 
	 */
	mergeConstraint(constraint: any): vCACCAFEDisplayTextBehavior;
	clone(): any;
	getValue(): any;
}

declare class vCACCAFEDlf {
	constructor();
	constructor();
	getId(): string;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	/**
	 * @param value 
	 */
	setDlfProperties(value: any): void;
	getDlfEncoded(): string;
	/**
	 * @param value 
	 */
	setDlfEncoded(value: string): void;
	getDlfProperties(): any;
}

declare class vCACCAFEDnsInfo {
	constructor();
	constructor();
	getDnsSuffix(): string;
	/**
	 * @param value 
	 */
	setDnsSuffix(value: string): void;
	getPrimaryDNS(): string;
	/**
	 * @param value 
	 */
	setPrimaryDNS(value: string): void;
	getSecondaryDNS(): string;
	/**
	 * @param value 
	 */
	setSecondaryDNS(value: string): void;
	getDnsSearchSuffixes(): string;
	/**
	 * @param value 
	 */
	setDnsSearchSuffixes(value: string): void;
	getPreferredWINS(): string;
	/**
	 * @param value 
	 */
	setPreferredWINS(value: string): void;
	getAlternateWINS(): string;
	/**
	 * @param value 
	 */
	setAlternateWINS(value: string): void;
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	/**
	 * @param value 
	 */
	setExtensionData(value: vCACCAFELiteralMap): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	getExtensionData(): vCACCAFELiteralMap;
}

declare class vCACCAFEDocument {
	constructor();
	constructor();
	getName(): string;
	getProperties(): any;
	/**
	 * @param value 
	 */
	setProperties(value: any): void;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	getPropertyGroups(): string[];
	getComponents(): any;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	/**
	 * @param value 
	 */
	setComponents(value: any): void;
}

declare class vCACCAFEDynamicDataRequest {
	constructor();
	/**
	 * @param ids 
	 * @param tenantId 
	 * @param ignoreUnknownFields 
	 */
	constructor(ids: string[], tenantId: string, ignoreUnknownFields: boolean);
	/**
	 * @param ids 
	 */
	constructor(ids: string[]);
	/**
	 * @param ids 
	 * @param ignoreUnknownFields 
	 */
	constructor(ids: string[], ignoreUnknownFields: boolean);
	/**
	 * @param ids 
	 * @param tenantId 
	 */
	constructor(ids: string[], tenantId: string);
	getIgnoreUnknownFields(): boolean;
	/**
	 * @param ignoreUnknownFields 
	 */
	setIgnoreUnknownFields(ignoreUnknownFields: boolean): void;
	getIds(): string[];
	getTenantId(): string;
	/**
	 * @param tenantId 
	 */
	setTenantId(tenantId: string): void;
}

declare class vCACCAFEDynamicDataResponse {
	constructor();
	/**
	 * @param values 
	 */
	constructor(values: vCACCAFELiteralMap);
	/**
	 * @param values 
	 */
	constructor(values: java.util.Map);
	getValues(): any;
}

declare class vCACCAFEDynamicLayoutReference {
	constructor();
	/**
	 * @param dependencies 
	 */
	constructor(dependencies: string[]);
	getDependencies(): string[];
}

declare class vCACCAFEDynamicPermissibleValueList {
	constructor();
	/**
	 * @param dependencies 
	 * @param context 
	 */
	constructor(dependencies: string[], context: vCACCAFEDynamicValueContext);
	/**
	 * @param dependencies 
	 */
	constructor(dependencies: string[]);
	/**
	 * @param dependencies 
	 */
	setDependencies(dependencies: string[]): void;
	getContext(): vCACCAFEDynamicValueContext;
	getDependencies(): string[];
	isCustomAllowed(): boolean;
	/**
	 * @param customAllowed 
	 */
	setCustomAllowed(customAllowed: boolean): void;
}

declare class vCACCAFEDynamicValueContext {
	constructor();
	/**
	 * @param providerEntityId 
	 * @param parameterMappings 
	 */
	constructor(providerEntityId: string, parameterMappings: vCACCAFEParameterMappingCollection);
	getProviderEntityId(): string;
	getParameterMappings(): vCACCAFEParameterMappingCollection;
	getDependencies(): string[];
}

declare class vCACCAFEEditableBehavior {
	constructor();
	/**
	 * @param isEnabled 
	 */
	constructor(isEnabled: boolean);
	/**
	 * @param editableBehavior 
	 */
	isEnabled(editableBehavior: any): boolean;
	/**
	 * @param value 
	 */
	cloneWithValue(value: any): any;
	/**
	 * @param constraint 
	 */
	mergeConstraint(constraint: any): any;
	clone(): any;
	getValue(): any;
}

declare class vCACCAFEElementMetadataResponse_ElementUpdateEntry {
	constructor();
	/**
	 * @param id 
	 * @param permissibleValues 
	 * @param facetValues 
	 */
	constructor(id: string, permissibleValues: vCACCAFEPermissibleValue[], facetValues: vCACCAFEFacetValueMap);
	/**
	 * @param id 
	 */
	constructor(id: string);
	/**
	 * @param id 
	 * @param permissibleValues 
	 * @param facetValues 
	 * @param detailLayout 
	 * @param extensionRendererContext 
	 * @param errors 
	 */
	constructor(id: string, permissibleValues: vCACCAFEPermissibleValue[], facetValues: vCACCAFEFacetValueMap, detailLayout: vCACCAFELayout, extensionRendererContext: vCACCAFELiteralMap, errors: string[]);
	getExtensionRendererContext(): vCACCAFELiteralMap;
	/**
	 * @param extensionRendererContext 
	 */
	setExtensionRendererContext(extensionRendererContext: vCACCAFELiteralMap): void;
	getDetailLayout(): vCACCAFELayout;
	/**
	 * @param detailLayout 
	 */
	setDetailLayout(detailLayout: vCACCAFELayout): void;
	getId(): string;
	getErrors(): string[];
	getPermissibleValues(): vCACCAFEPermissibleValue[];
	/**
	 * @param values 
	 */
	setPermissibleValues(values: vCACCAFEPermissibleValue[]): void;
	/**
	 * @param id 
	 */
	setId(id: string): void;
	getFacetValues(): vCACCAFEFacetValueMap;
	/**
	 * @param facets 
	 */
	setFacetValues(facets: vCACCAFEFacetValueMap): void;
	/**
	 * @param facetValues 
	 */
	setFacetValuesAsMap(facetValues: any): void;
	/**
	 * @param errors 
	 */
	setErrors(errors: string[]): void;
}

declare class vCACCAFEElementState {
	constructor();
	/**
	 * @param facets 
	 * @param dependencies 
	 */
	constructor(facets: java.util.Map, dependencies: string[]);
	getFacetsAsCollection(): any;
	/**
	 * @param type 
	 */
	hasFacet(type: any): boolean;
	/**
	 * @param type 
	 */
	getFacet(type: any): any;
	clone(): vCACCAFEElementState;
	isEmpty(): boolean;
	/**
	 * @param other 
	 */
	merge(other: vCACCAFEElementState): vCACCAFEElementState;
	getDependencies(): string[];
	getFacets(): any[];
	/**
	 * @param type 
	 */
	getFacetValue(type: any): any;
}

declare class vCACCAFEElementValuesPagingInfo {
	constructor();
	/**
	 * @param offset 
	 * @param count 
	 */
	constructor(offset: number, count: number);
	getCount(): number;
	getOffset(): number;
}

declare interface vCACCAFEEmailFormat {
	value(): string;
	values(): vCACCAFEEmailFormat[];
	/**
	 * @param name 
	 */
	valueOf(name: string): vCACCAFEEmailFormat;
	/**
	 * @param v 
	 */
	fromValue(v: string): vCACCAFEEmailFormat;
}

declare class vCACCAFEEmailNotificationProvider {
	constructor();
	constructor();
	getProtocol(): vCACCAFEEmailProtocol;
	getPort(): number;
	isSelfSignedCertificateAccepted(): boolean;
	/**
	 * @param value 
	 */
	setSelfSignedCertificateAccepted(value: boolean): void;
	getServerName(): string;
	/**
	 * @param value 
	 */
	setServerName(value: string): void;
	isSslEnabled(): boolean;
	/**
	 * @param value 
	 */
	setSslEnabled(value: boolean): void;
	isAuthenticationRequired(): boolean;
	/**
	 * @param value 
	 */
	setAuthenticationRequired(value: boolean): void;
	isDeleteMessagesAfterRead(): boolean;
	/**
	 * @param value 
	 */
	setDeleteMessagesAfterRead(value: boolean): void;
	getFolder(): string;
	/**
	 * @param value 
	 */
	setFolder(value: string): void;
	getUsername(): string;
	getPassword(): string;
	/**
	 * @param value 
	 */
	setProtocol(value: vCACCAFEEmailProtocol): void;
	/**
	 * @param value 
	 */
	setPort(value: number): void;
	/**
	 * @param value 
	 */
	setPassword(value: string): void;
	/**
	 * @param value 
	 */
	setUsername(value: string): void;
	getEmailAddress(): string;
	/**
	 * @param value 
	 */
	setEmailAddress(value: string): void;
	getVersion(): number;
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	getType(): vCACCAFENotificationProviderType;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	/**
	 * @param value 
	 */
	setType(value: vCACCAFENotificationProviderType): void;
	getDescription(): string;
	/**
	 * @param value 
	 */
	setVersion(value: number): void;
	getProviderDirection(): vCACCAFEProviderDirection;
	/**
	 * @param value 
	 */
	setProviderDirection(value: vCACCAFEProviderDirection): void;
	getProviderMode(): vCACCAFEProviderMode;
	/**
	 * @param value 
	 */
	setProviderMode(value: vCACCAFEProviderMode): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
}

declare interface vCACCAFEEmailProtocol {
	value(): string;
	values(): vCACCAFEEmailProtocol[];
	/**
	 * @param name 
	 */
	valueOf(name: string): vCACCAFEEmailProtocol;
	/**
	 * @param v 
	 */
	fromValue(v: string): vCACCAFEEmailProtocol;
}

declare class vCACCAFEEmbeddedDataContext {
	constructor();
	constructor();
	/**
	 * @param data 
	 */
	constructor(data: vCACCAFELiteralMap);
	getData(): vCACCAFELiteralMap;
	/**
	 * @param data 
	 */
	setData(data: vCACCAFELiteralMap): void;
}

declare class vCACCAFEEmbeddedFieldInsinuation {
	constructor();
	constructor();
	/**
	 * @param definition 
	 */
	constructor(definition: com.vmware.vcac.platform.content.schema.Field);
	getDefinition(): any;
	/**
	 * @param handler 
	 */
	handle(handler: any): any;
	/**
	 * @param other 
	 */
	equalsForAttributes(other: vCACCAFEEmbeddedFieldInsinuation): boolean;
	/**
	 * @param definition 
	 */
	setDefinition(definition: any): void;
}

declare class vCACCAFEEmbeddedLicenseEntryUtil {
	constructor();
	constructor();
}

declare class vCACCAFEEncryptedBehavior {
	constructor();
	/**
	 * @param value 
	 */
	constructor(value: com.vmware.vcac.platform.content.evaluators.Evaluator);
	/**
	 * @param value 
	 */
	cloneWithValue(value: any): any;
	/**
	 * @param constraint 
	 */
	mergeConstraint(constraint: any): any;
	clone(): any;
	getValue(): any;
}

declare class vCACCAFEEndPoint {
	constructor();
	constructor();
	/**
	 * @param value 
	 */
	setUrl(value: any): void;
	getUrl(): any;
	getId(): string;
	getLastUpdated(): any;
	/**
	 * @param value 
	 */
	setLastUpdated(value: any): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	getServiceInfoId(): string;
	/**
	 * @param value 
	 */
	setServiceInfoId(value: string): void;
	getEndPointAttributes(): vCACCAFEAttribute[];
	getSslTrusts(): string[];
	getEndPointType(): vCACCAFEEndPointType;
	/**
	 * @param value 
	 */
	setEndPointType(value: vCACCAFEEndPointType): void;
	/**
	 * @param value 
	 */
	setCreatedDate(value: any): void;
	getCreatedDate(): any;
}

declare class vCACCAFEEndPointAttribute {
	constructor();
	constructor();
	getValue(): string;
	getKey(): string;
	getId(): string;
	/**
	 * @param value 
	 */
	setValue(value: string): void;
	/**
	 * @param value 
	 */
	setKey(value: string): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
}

declare class vCACCAFEEndPointFormData {
	constructor();
	constructor();
	getData(): vCACCAFELiteralMap;
	/**
	 * @param value 
	 */
	setEndpointTypeSpecId(value: string): void;
	getEndpointTypeSpecId(): string;
	/**
	 * @param value 
	 */
	setData(value: vCACCAFELiteralMap): void;
}

declare interface vCACCAFEEndPointProtocol {
	value(): string;
	values(): vCACCAFEEndPointProtocol[];
	/**
	 * @param name 
	 */
	valueOf(name: string): vCACCAFEEndPointProtocol;
	/**
	 * @param v 
	 */
	fromValue(v: string): vCACCAFEEndPointProtocol;
}

declare class vCACCAFEEndPointType {
	constructor();
	constructor();
	getProtocol(): vCACCAFEEndPointProtocol;
	/**
	 * @param value 
	 */
	setProtocol(value: vCACCAFEEndPointProtocol): void;
	/**
	 * @param value 
	 */
	setTypeId(value: string): void;
	getTypeId(): string;
}

declare class vCACCAFEEndPointTypeExtension {
	constructor();
	constructor();
	getVersion(): number;
	getId(): string;
	/**
	 * @param value 
	 */
	setVersion(value: number): void;
	getTenantId(): string;
	getFormReference(): any;
	/**
	 * @param value 
	 */
	setFormReference(value: any): void;
	getProviderServiceTypeId(): string;
	/**
	 * @param value 
	 */
	setProviderServiceTypeId(value: string): void;
	getEndpointTypeId(): string;
	/**
	 * @param value 
	 */
	setEndpointTypeId(value: string): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	getOwnerId(): string;
	/**
	 * @param value 
	 */
	setOwnerId(value: string): void;
	getSchema(): vCACCAFESchema;
	/**
	 * @param value 
	 */
	setSchema(value: vCACCAFESchema): void;
	/**
	 * @param value 
	 */
	setTenantId(value: string): void;
}

declare class vCACCAFEEndPointTypeSpec {
	constructor();
	constructor();
	getVersion(): number;
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	getProtocol(): vCACCAFEEndPointProtocol;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	/**
	 * @param value 
	 */
	setVersion(value: number): void;
	getTenantId(): string;
	getAuthType(): vCACCAFEAuthType;
	/**
	 * @param value 
	 */
	setAuthType(value: vCACCAFEAuthType): void;
	getEndpointTypeExtensions(): vCACCAFEEndPointTypeExtension[];
	/**
	 * @param value 
	 */
	setId(value: string): void;
	getOwnerId(): string;
	/**
	 * @param value 
	 */
	setOwnerId(value: string): void;
	/**
	 * @param value 
	 */
	setProtocol(value: vCACCAFEEndPointProtocol): void;
	/**
	 * @param value 
	 */
	setCategory(value: string): void;
	getCategory(): string;
	/**
	 * @param value 
	 */
	setTypeId(value: string): void;
	/**
	 * @param value 
	 */
	setTenantId(value: string): void;
	getTypeId(): string;
}

declare class vCACCAFEEndpoint {
	version: number;
	uri: string;
	name: string;
	id: string;
	description: string;
	tenantId: string;
	vcoId: any;
	typeDisplayName: string;
	lastUpdated: any;
	extensionData: vCACCAFELiteralMap;
	typeId: string;
	associations: vCACCAFEAssociation[];
	createdDate: any;
	constructor();
	constructor();
	getVersion(): number;
	getUri(): string;
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	/**
	 * @param value 
	 */
	setVersion(value: number): void;
	getTenantId(): string;
	getVcoId(): any;
	/**
	 * @param undefined 
	 */
	setVcoId(id: any): void;
	/**
	 * @param value 
	 */
	setUri(value: string): void;
	getTypeDisplayName(): string;
	/**
	 * @param value 
	 */
	setTypeDisplayName(value: string): void;
	/**
	 * @param value 
	 */
	setExtensionData(value: vCACCAFELiteralMap): void;
	getLastUpdated(): any;
	/**
	 * @param value 
	 */
	setLastUpdated(value: any): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	getExtensionData(): vCACCAFELiteralMap;
	/**
	 * @param value 
	 */
	setTypeId(value: string): void;
	/**
	 * @param value 
	 */
	setCreatedDate(value: any): void;
	/**
	 * @param value 
	 */
	setTenantId(value: string): void;
	getTypeId(): string;
	getAssociations(): vCACCAFEAssociation[];
	getCreatedDate(): any;
}

declare interface vCACCAFEEndpointConfigurationAssociationTypeService {
	/**
	 * This method invokes the SDK method AssociationTypeService.getAllAssociationTypes(Pageable pageable).
	 * @param pageable 
	 */
	getAllAssociationTypes(pageable: any): vCACCAFEPagedResources;
	/**
	 * This method invokes the SDK method AssociationTypeService.getAssociationTypes(String associationType, Pageable pageable).
	 * @param associationType 
	 * @param pageable 
	 */
	getAssociationTypes(associationType: string, pageable: any): vCACCAFEPagedResources;
	/**
	 * This method invokes the SDK method AssociationTypeService.getAssociationType(String associationTypeId).
	 * @param associationTypeId 
	 */
	getAssociationType(associationTypeId: string): vCACCAFEAssociationType;
	/**
	 * This method invokes the SDK method AssociationTypeService.createAssociationType(AssociationType associationType).
	 * @param associationType 
	 */
	createAssociationType(associationType: vCACCAFEAssociationType): string;
	/**
	 * This method invokes the SDK method AssociationTypeService.deleteAssociationType(String associationTypeId).
	 * @param associationTypeId 
	 */
	deleteAssociationType(associationTypeId: string): void;
	/**
	 * This method invokes the SDK method AssociationTypeService.updateAssociationType(AssociationType associationType).
	 * @param associationType 
	 */
	updateAssociationType(associationType: vCACCAFEAssociationType): void;
	/**
	 * This method invokes the SDK method AssociationTypeService.getAssociationTypesForEndpointType(String associationType, Pageable pageable).
	 * @param associationType 
	 * @param pageable 
	 */
	getAssociationTypesForEndpointType(associationType: string, pageable: any): vCACCAFEPagedResources;
}

declare interface vCACCAFEEndpointConfigurationClient {
	getEndpointConfigurationEndpointTypeCategoryService(): vCACCAFEEndpointConfigurationEndpointTypeCategoryService;
	getEndpointConfigurationEndpointTypeService(): vCACCAFEEndpointConfigurationEndpointTypeService;
	getEndpointConfigurationAssociationTypeService(): vCACCAFEEndpointConfigurationAssociationTypeService;
	getEndpointConfigurationEndpointService(): vCACCAFEEndpointConfigurationEndpointService;
	/**
	 * Does a POST request expanding the given template variables in order.
	 * @param resourceUrl 
	 * @param object 
	 * @param urlVariables 
	 */
	postWithVariables(resourceUrl: string, object: any, urlVariables: string[]): vCACCAFEServiceResponse;
	/**
	 * Does a PUT request expanding the given template variables in order.
	 * @param resourceUrl 
	 * @param object 
	 * @param urlVariables 
	 */
	putWithVariables(resourceUrl: string, object: any, urlVariables: string[]): vCACCAFEServiceResponse;
	/**
	 * Does a DELETE request expanding the given template variables in order.
	 * @param resourceUrl 
	 * @param urlVariables 
	 */
	deleteWithVariables(resourceUrl: string, urlVariables: string[]): void;
	/**
	 * Gets the REST client URL.
	 */
	getUrl(): string;
	/**
	 * Indicates whether the current connection is already closed or not.
	 */
	isClosed(): boolean;
	/**
	 * Does a GET request for a file and allows specifying 'Accept' header.
	 * @param resourceUrl 
	 * @param acceptHeaderValue 
	 */
	getFileWithAcceptHeader(resourceUrl: string, acceptHeaderValue: string): any;
	/**
	 * Does a POST request with a file.
	 * @param resourceUrl 
	 * @param object 
	 */
	postFile(resourceUrl: string, object: any): vCACCAFEServiceResponse;
	/**
	 * Does a GET request expanding the given template variables in order.
	 * @param resourceUrl 
	 * @param urlVariables 
	 */
	getWithVariables(resourceUrl: string, urlVariables: string[]): any;
	/**
	 * Does a GET request.
	 * @param resourceUrl 
	 */
	get(resourceUrl: string): any;
	/**
	 * Does a PUT request.
	 * @param resourceUrl 
	 * @param object 
	 */
	put(resourceUrl: string, object: any): vCACCAFEServiceResponse;
	/**
	 * Does a DELETE request.
	 * @param resourceUrl 
	 */
	delete(resourceUrl: string): void;
	/**
	 * Invalidates the current connection.
	 */
	close(): void;
	/**
	 * Does a GET request for a file.
	 * @param resourceUrl 
	 */
	getFile(resourceUrl: string): any;
	/**
	 * Does a POST request.
	 * @param resourceUrl 
	 * @param object 
	 */
	post(resourceUrl: string, object: any): vCACCAFEServiceResponse;
}

declare interface vCACCAFEEndpointConfigurationEndpointService {
	/**
	 * This method invokes the SDK method EndpointService.getEndpointsForCategory(String endpointType, Pageable pageable).
	 * @param endpointType 
	 * @param pageable 
	 */
	getEndpointsForCategory(endpointType: string, pageable: any): vCACCAFEPagedResources;
	/**
	 * This method invokes the SDK method EndpointService.getEndpointsForType(String endpointType, Pageable pageable).
	 * @param endpointType 
	 * @param pageable 
	 */
	getEndpointsForType(endpointType: string, pageable: any): vCACCAFEPagedResources;
	/**
	 * This method invokes the SDK method EndpointService.getEndpointsForType(String endpointType, boolean secureFields, Pageable pageable).
	 * @param endpointType 
	 * @param secureFields 
	 * @param pageable 
	 */
	getConfiguredEndpointsForType(endpointType: string, secureFields: boolean, pageable: any): vCACCAFEPagedResources;
	/**
	 * This method invokes the SDK method EndpointService.updateEndpoint(Endpoint endpoint).
	 * @param endpoint 
	 */
	updateEndpoint(endpoint: vCACCAFEEndpoint): void;
	/**
	 * This method invokes the SDK method EndpointService.createEndpoint(Endpoint endpoint).
	 * @param endpoint 
	 */
	createEndpoint(endpoint: vCACCAFEEndpoint): string;
	/**
	 * This method invokes the SDK method EndpointService.deleteEndpoint(String endpointId).
	 * @param endpointId 
	 */
	deleteEndpoint(endpointId: string): void;
	/**
	 * This method invokes the SDK method EndpointService.getAllEndpoints(Pageable pageable).
	 * @param pageable 
	 */
	getAllEndpoints(pageable: any): vCACCAFEPagedResources;
	/**
	 * This method invokes the SDK method EndpointService.getEndpointsForCategory(String endpointType, boolean secureFields, Pageable pageable).
	 * @param endpointType 
	 * @param secureFields 
	 * @param pageable 
	 */
	getConfiguredEndpointsForCategory(endpointType: string, secureFields: boolean, pageable: any): vCACCAFEPagedResources;
	/**
	 * This method invokes the SDK method EndpointService.getAllEndpoints(boolean secureFields, Pageable pageable).
	 * @param secureFields 
	 * @param pageable 
	 */
	getAllConfiguredEndpoints(secureFields: boolean, pageable: any): vCACCAFEPagedResources;
	/**
	 * This method invokes the SDK method EndpointService.getEndpoint(String endpointId, boolean secureFields).
	 * @param endpointId 
	 * @param secureFields 
	 */
	getEndpoint(endpointId: string, secureFields: boolean): vCACCAFEEndpoint;
}

declare interface vCACCAFEEndpointConfigurationEndpointTypeCategoryService {
	/**
	 * This method invokes the SDK method EndpointTypeCategoryService.deleteEndpointTypeCategory(String endpointTypeCategoryId).
	 * @param endpointTypeCategoryId 
	 */
	deleteEndpointTypeCategory(endpointTypeCategoryId: string): void;
	/**
	 * This method invokes the SDK method EndpointTypeCategoryService.getAllEndpointTypeCategories(Pageable pageable).
	 * @param pageable 
	 */
	getAllEndpointTypeCategories(pageable: any): vCACCAFEPagedResources;
	/**
	 * This method invokes the SDK method EndpointTypeCategoryService.getEndpointTypeCategory(String endpointTypeCategoryId).
	 * @param endpointTypeCategoryId 
	 */
	getEndpointTypeCategory(endpointTypeCategoryId: string): vCACCAFEEndpointTypeCategory;
	/**
	 * This method invokes the SDK method EndpointTypeCategoryService.updateEndpointTypeCategory(EndpointTypeCategory endpointTypeCategory).
	 * @param endpointTypeCategory 
	 */
	updateEndpointTypeCategory(endpointTypeCategory: vCACCAFEEndpointTypeCategory): void;
	/**
	 * This method invokes the SDK method EndpointTypeCategoryService.createEndpointTypeCategory(EndpointTypeCategory endpointTypeCategory).
	 * @param endpointTypeCategory 
	 */
	createEndpointTypeCategory(endpointTypeCategory: vCACCAFEEndpointTypeCategory): string;
}

declare interface vCACCAFEEndpointConfigurationEndpointTypeService {
	/**
	 * This method invokes the SDK method EndpointTypeService.createEndpointType(EndpointType endpointType).
	 * @param endpointType 
	 */
	createEndpointType(endpointType: vCACCAFEEndpointType): string;
	/**
	 * This method invokes the SDK method EndpointTypeService.deleteEndpointType(String endpointTypeId).
	 * @param endpointTypeId 
	 */
	deleteEndpointType(endpointTypeId: string): void;
	/**
	 * This method invokes the SDK method EndpointTypeService.updateEndpointType(EndpointType endpointType).
	 * @param endpointType 
	 */
	updateEndpointType(endpointType: vCACCAFEEndpointType): void;
	/**
	 * This method invokes the SDK method EndpointTypeService.getAllEndpointTypes(Pageable pageable).
	 * @param pageable 
	 */
	getAllEndpointTypes(pageable: any): vCACCAFEPagedResources;
	/**
	 * This method invokes the SDK method EndpointTypeService.getEndpointType(String endpointTypeId).
	 * @param endpointTypeId 
	 */
	getEndpointType(endpointTypeId: string): vCACCAFEEndpointType;
}

declare class vCACCAFEEndpointCreateRequest {
	constructor();
	constructor();
	getTenantId(): string;
	getAssociatedEndpoints(): vCACCAFEEndpoint[];
	getProviderServiceId(): string;
	/**
	 * @param value 
	 */
	setProviderServiceId(value: string): void;
	/**
	 * @param value 
	 */
	setEndpoint(value: vCACCAFEEndpoint): void;
	getEndpoint(): vCACCAFEEndpoint;
	/**
	 * @param value 
	 */
	setTenantId(value: string): void;
}

declare class vCACCAFEEndpointDeleteRequest {
	constructor();
	constructor();
	getTenantId(): string;
	getAssociatedEndpoints(): vCACCAFEEndpoint[];
	getProviderServiceId(): string;
	/**
	 * @param value 
	 */
	setProviderServiceId(value: string): void;
	/**
	 * @param value 
	 */
	setEndpoint(value: vCACCAFEEndpoint): void;
	getEndpoint(): vCACCAFEEndpoint;
	/**
	 * @param value 
	 */
	setTenantId(value: string): void;
}

declare class vCACCAFEEndpointMenuItem {
	constructor();
	constructor();
	getIcon(): string;
	/**
	 * @param value 
	 */
	setDisplayName(value: string): void;
	getLocation(): string;
	getId(): number;
	getDisplayName(): string;
	getExtensionId(): string;
	/**
	 * @param value 
	 */
	setIcon(value: string): void;
	/**
	 * @param value 
	 */
	setMenuId(value: string): void;
	getMenuId(): string;
	/**
	 * @param value 
	 */
	setExtensionId(value: string): void;
	/**
	 * @param value 
	 */
	setId(value: number): void;
	/**
	 * @param value 
	 */
	setLocation(value: string): void;
}

declare class vCACCAFEEndpointRequest {
	constructor();
	constructor();
	getTenantId(): string;
	getAssociatedEndpoints(): vCACCAFEEndpoint[];
	getProviderServiceId(): string;
	/**
	 * @param value 
	 */
	setProviderServiceId(value: string): void;
	/**
	 * @param value 
	 */
	setEndpoint(value: vCACCAFEEndpoint): void;
	getEndpoint(): vCACCAFEEndpoint;
	/**
	 * @param value 
	 */
	setTenantId(value: string): void;
}

declare class vCACCAFEEndpointType {
	version: number;
	id: string;
	displayName: string;
	description: string;
	tenantId: string;
	vcoId: any;
	categoryDisplayName: string;
	tenantable: boolean;
	serviceProviderId: string;
	capabilities: string;
	menuItems: vCACCAFEEndpointMenuItem[];
	lastUpdated: any;
	schema: vCACCAFESchema;
	category: string;
	createdDate: any;
	constructor();
	constructor();
	getVersion(): number;
	/**
	 * @param value 
	 */
	setDisplayName(value: string): void;
	getId(): string;
	getDisplayName(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	/**
	 * @param value 
	 */
	setVersion(value: number): void;
	getTenantId(): string;
	getVcoId(): any;
	/**
	 * @param undefined 
	 */
	setVcoId(id: any): void;
	getCategoryDisplayName(): string;
	/**
	 * @param value 
	 */
	setCategoryDisplayName(value: string): void;
	isTenantable(): boolean;
	/**
	 * @param value 
	 */
	setTenantable(value: boolean): void;
	getServiceProviderId(): string;
	/**
	 * @param value 
	 */
	setServiceProviderId(value: string): void;
	getCapabilities(): string;
	/**
	 * @param value 
	 */
	setCapabilities(value: string): void;
	getMenuItems(): vCACCAFEEndpointMenuItem[];
	getLastUpdated(): any;
	/**
	 * @param value 
	 */
	setLastUpdated(value: any): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	getSchema(): vCACCAFESchema;
	/**
	 * @param value 
	 */
	setSchema(value: vCACCAFESchema): void;
	/**
	 * @param value 
	 */
	setCategory(value: string): void;
	getCategory(): string;
	/**
	 * @param value 
	 */
	setCreatedDate(value: any): void;
	/**
	 * @param value 
	 */
	setTenantId(value: string): void;
	getCreatedDate(): any;
}

declare class vCACCAFEEndpointTypeCategory {
	constructor();
	constructor();
	getVersion(): number;
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setVersion(value: number): void;
	getLastUpdated(): any;
	/**
	 * @param value 
	 */
	setLastUpdated(value: any): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	/**
	 * @param value 
	 */
	setCreatedDate(value: any): void;
	getCreatedDate(): any;
}

declare class vCACCAFEEndpointTypeRequest {
	constructor();
	constructor();
	/**
	 * @param value 
	 */
	setEndpointType(value: vCACCAFEEndpointType): void;
	getEndpointType(): vCACCAFEEndpointType;
}

declare class vCACCAFEEndpointUpdateRequest {
	constructor();
	constructor();
	getExistingEndpoint(): vCACCAFEEndpoint;
	/**
	 * @param value 
	 */
	setExistingEndpoint(value: vCACCAFEEndpoint): void;
	getExistingAssociatedEndpoints(): vCACCAFEEndpoint[];
	getTenantId(): string;
	getAssociatedEndpoints(): vCACCAFEEndpoint[];
	getProviderServiceId(): string;
	/**
	 * @param value 
	 */
	setProviderServiceId(value: string): void;
	/**
	 * @param value 
	 */
	setEndpoint(value: vCACCAFEEndpoint): void;
	getEndpoint(): vCACCAFEEndpoint;
	/**
	 * @param value 
	 */
	setTenantId(value: string): void;
}

declare class vCACCAFEEndpointValidateRequest {
	constructor();
	constructor();
	getTenantId(): string;
	getAssociatedEndpoints(): vCACCAFEEndpoint[];
	getProviderServiceId(): string;
	/**
	 * @param value 
	 */
	setProviderServiceId(value: string): void;
	/**
	 * @param value 
	 */
	setEndpoint(value: vCACCAFEEndpoint): void;
	getEndpoint(): vCACCAFEEndpoint;
	/**
	 * @param value 
	 */
	setTenantId(value: string): void;
}

declare class vCACCAFEEndpointValidationResult {
	constructor();
	constructor();
	getMessage(): string;
	getStatus(): vCACCAFEValidationStatusCode;
	getCertificateInfo(): vCACCAFECertificateInfo;
	/**
	 * @param value 
	 */
	setCertificateInfo(value: vCACCAFECertificateInfo): void;
	/**
	 * @param value 
	 */
	setStatus(value: vCACCAFEValidationStatusCode): void;
	/**
	 * @param value 
	 */
	setMessage(value: string): void;
}

declare interface vCACCAFEEndsWithOperator {
	getId(): string;
	/**
	 * @param left 
	 * @param right 
	 */
	evaluate(left: any, right: any): vCACCAFEBooleanLiteral;
	getDataTypeConstraint(): any;
	getCategory(): any;
	getMultiplicityConstraint(): any;
}

declare class vCACCAFEEntitledCatalogItem {
	constructor();
	constructor();
	toString(): string;
	isHidden(): boolean;
	isActive(): boolean;
	getApprovalPolicyId(): string;
	/**
	 * @param value 
	 */
	setApprovalPolicyId(value: string): void;
	getCatalogItemRef(): vCACCAFELabelledReference;
	isCatalogItemRequestable(): boolean;
	/**
	 * @param value 
	 */
	setCatalogItemRequestable(value: boolean): void;
	/**
	 * @param value 
	 */
	setCatalogItemRef(value: vCACCAFELabelledReference): void;
	/**
	 * @param value 
	 */
	setActive(value: boolean): void;
	/**
	 * @param value 
	 */
	setHidden(value: boolean): void;
}

declare class vCACCAFEEntitledResourceOperation {
	constructor();
	constructor();
	toString(): string;
	isActive(): boolean;
	getResourceOperationRef(): vCACCAFELabelledReference;
	/**
	 * @param value 
	 */
	setResourceOperationRef(value: vCACCAFELabelledReference): void;
	getResourceOperationType(): vCACCAFEResourceOperationType;
	/**
	 * @param value 
	 */
	setResourceOperationType(value: vCACCAFEResourceOperationType): void;
	getApprovalPolicyId(): string;
	/**
	 * @param value 
	 */
	setApprovalPolicyId(value: string): void;
	getTargetResourceTypeRef(): vCACCAFELabelledReference;
	/**
	 * @param value 
	 */
	setTargetResourceTypeRef(value: vCACCAFELabelledReference): void;
	/**
	 * @param value 
	 */
	setActive(value: boolean): void;
	getExternalId(): string;
	/**
	 * @param value 
	 */
	setExternalId(value: string): void;
}

declare class vCACCAFEEntitledService {
	constructor();
	constructor();
	toString(): string;
	isActive(): boolean;
	getApprovalPolicyId(): string;
	/**
	 * @param value 
	 */
	setApprovalPolicyId(value: string): void;
	getServiceRef(): vCACCAFELabelledReference;
	/**
	 * @param value 
	 */
	setServiceRef(value: vCACCAFELabelledReference): void;
	/**
	 * @param value 
	 */
	setActive(value: boolean): void;
}

declare class vCACCAFEEntitlement {
	version: number;
	organization: vCACCAFECatalogOrganizationReference;
	expiryDate: any;
	name: string;
	principals: vCACCAFECatalogPrincipal[];
	id: string;
	description: string;
	status: vCACCAFEEntitlementStatus;
	entitledResourceOperations: vCACCAFEEntitledResourceOperation[];
	entitledServices: vCACCAFEEntitledService[];
	priorityOrder: number;
	localScopeForActions: boolean;
	allUsers: boolean;
	entitledCatalogItems: vCACCAFEEntitledCatalogItem[];
	vcoId: any;
	lastUpdatedDate: any;
	lastUpdatedBy: string;
	statusName: string;
	constructor();
	constructor();
	getVersion(): number;
	/**
	 * @param value 
	 */
	setOrganization(value: vCACCAFECatalogOrganizationReference): void;
	getOrganization(): vCACCAFECatalogOrganizationReference;
	getExpiryDate(): any;
	/**
	 * @param value 
	 */
	setExpiryDate(value: any): void;
	getName(): string;
	getPrincipals(): vCACCAFECatalogPrincipal[];
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	getStatus(): vCACCAFEEntitlementStatus;
	/**
	 * @param value 
	 */
	setVersion(value: number): void;
	getEntitledResourceOperations(): vCACCAFEEntitledResourceOperation[];
	getEntitledServices(): vCACCAFEEntitledService[];
	getPriorityOrder(): number;
	/**
	 * @param value 
	 */
	setPriorityOrder(value: number): void;
	isLocalScopeForActions(): boolean;
	/**
	 * @param value 
	 */
	setLocalScopeForActions(value: boolean): void;
	isAllUsers(): boolean;
	/**
	 * @param value 
	 */
	setAllUsers(value: boolean): void;
	getEntitledCatalogItems(): vCACCAFEEntitledCatalogItem[];
	getVcoId(): any;
	/**
	 * @param undefined 
	 */
	setVcoId(id: any): void;
	getLastUpdatedDate(): any;
	/**
	 * @param value 
	 */
	setLastUpdatedDate(value: any): void;
	getLastUpdatedBy(): string;
	/**
	 * @param value 
	 */
	setLastUpdatedBy(value: string): void;
	getStatusName(): string;
	/**
	 * @param value 
	 */
	setStatusName(value: string): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	/**
	 * @param value 
	 */
	setStatus(value: vCACCAFEEntitlementStatus): void;
}

declare interface vCACCAFEEntitlementStatus {
	value(): string;
	values(): vCACCAFEEntitlementStatus[];
	/**
	 * @param name 
	 */
	valueOf(name: string): vCACCAFEEntitlementStatus;
	/**
	 * @param v 
	 */
	fromValue(v: string): vCACCAFEEntitlementStatus;
}

declare class vCACCAFEEntityCallback {
	constructor();
	constructor();
	/**
	 * @param callbackOperation 
	 * @param entity 
	 */
	constructor(callbackOperation: vCACCAFECallbackOperation, entity: any);
	/**
	 * @param entity 
	 */
	setEntity(entity: any): void;
	getEntity(): any;
	getCallbackOperation(): vCACCAFECallbackOperation;
	/**
	 * @param callbackOperation 
	 */
	setCallbackOperation(callbackOperation: vCACCAFECallbackOperation): void;
}

declare class vCACCAFEEntityReference {
	constructor();
	/**
	 * @param componentId 
	 * @param classId 
	 * @param id 
	 * @param label 
	 */
	constructor(componentId: string, classId: string, id: string, label: string);
	/**
	 * @param classId 
	 * @param id 
	 */
	constructor(classId: string, id: string);
	hasComponentId(): boolean;
	isList(): boolean;
	/**
	 * @param o 
	 */
	compareTo(o: any): number;
	getValue(): vCACCAFEEntityReference;
	getId(): string;
	/**
	 * @param label 
	 */
	setLabel(label: string): void;
	getClassId(): string;
	getComponentId(): string;
	getLabel(): string;
	hasLabel(): boolean;
	getTypeId(): vCACCAFEDataTypeId;
	/**
	 * @param bVal 
	 */
	fromBoolean(bVal: boolean): any;
	/**
	 * @param obj 
	 */
	safeEquals(obj: any): boolean;
	/**
	 * @param sVal 
	 */
	fromString(sVal: string): any;
	/**
	 * @param lVal 
	 */
	fromInt(lVal: number): any;
	/**
	 * @param dVal 
	 */
	fromDecimal(dVal: number): any;
}

declare class vCACCAFEEntityReferenceDataType {
	constructor();
	/**
	 * @param componentTypeId 
	 * @param componentId 
	 * @param classId 
	 * @param typeFilter 
	 * @param label 
	 */
	constructor(componentTypeId: string, componentId: string, classId: string, typeFilter: string, label: string);
	/**
	 * @param classId 
	 */
	constructor(classId: string);
	getTypeId(): vCACCAFEDataTypeId;
	getComponentTypeId(): string;
	/**
	 * @param label 
	 */
	setLabel(label: string): void;
	getClassId(): string;
	getTypeFilter(): string;
	getComponentId(): string;
	getLabel(): string;
	getOperatorCategories(): any[];
}

declare class vCACCAFEEntityReferenceEx {
	constructor();
	/**
	 * @param classId 
	 * @param id 
	 */
	constructor(classId: string, id: string);
	getHref(): string;
	/**
	 * @param href 
	 */
	setHref(href: string): void;
	hasComponentId(): boolean;
	isList(): boolean;
	/**
	 * @param o 
	 */
	compareTo(o: any): number;
	getValue(): vCACCAFEEntityReference;
	getId(): string;
	/**
	 * @param label 
	 */
	setLabel(label: string): void;
	getClassId(): string;
	getComponentId(): string;
	getLabel(): string;
	hasLabel(): boolean;
	getTypeId(): vCACCAFEDataTypeId;
	/**
	 * @param bVal 
	 */
	fromBoolean(bVal: boolean): any;
	/**
	 * @param obj 
	 */
	safeEquals(obj: any): boolean;
	/**
	 * @param sVal 
	 */
	fromString(sVal: string): any;
	/**
	 * @param lVal 
	 */
	fromInt(lVal: number): any;
	/**
	 * @param dVal 
	 */
	fromDecimal(dVal: number): any;
}

declare class vCACCAFEEnumRef {
	constructor();
	constructor();
	getEventTopics(): vCACCAFEExtensibilityEventTopics;
	/**
	 * @param value 
	 */
	setEventTopics(value: vCACCAFEExtensibilityEventTopics): void;
}

declare interface vCACCAFEEqualsOperator {
	getId(): string;
	/**
	 * @param leftValue 
	 * @param rightValue 
	 */
	evaluate(leftValue: any, rightValue: any): vCACCAFEBooleanLiteral;
	getDataTypeConstraint(): any;
	getCategory(): any;
	getMultiplicityConstraint(): any;
}

declare interface vCACCAFEEvaluationEventType {
	value(): string;
	values(): vCACCAFEEvaluationEventType[];
	/**
	 * @param name 
	 */
	valueOf(name: string): vCACCAFEEvaluationEventType;
	/**
	 * @param v 
	 */
	fromValue(v: string): vCACCAFEEvaluationEventType;
}

declare interface vCACCAFEEvaluationState {
	value(): string;
	values(): vCACCAFEEvaluationState[];
	/**
	 * @param name 
	 */
	valueOf(name: string): vCACCAFEEvaluationState;
	/**
	 * @param v 
	 */
	fromValue(v: string): vCACCAFEEvaluationState;
}

declare class vCACCAFEEvent {
	constructor();
	constructor();
	toString(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	getEventType(): vCACCAFEEventType;
	getTenantId(): string;
	/**
	 * @param value 
	 */
	setEventType(value: vCACCAFEEventType): void;
	/**
	 * @param value 
	 */
	setTargetType(value: string): void;
	getTargetId(): string;
	/**
	 * @param value 
	 */
	setTargetId(value: string): void;
	getEventTypeName(): string;
	/**
	 * @param value 
	 */
	setEventTypeName(value: string): void;
	/**
	 * @param value 
	 */
	setUserName(value: string): void;
	getUserName(): string;
	getTargetType(): string;
	/**
	 * @param value 
	 */
	setTenantId(value: string): void;
	getData(): vCACCAFELiteralMap;
	getHeaders(): vCACCAFELiteralMap;
	getId(): string;
	/**
	 * @param headers 
	 */
	setHeaders(headers: vCACCAFELiteralMap): void;
	getTimeStamp(): any;
	getEventTopicId(): string;
	getCorrelationId(): string;
	getSourceType(): string;
	getSourceIdentity(): string;
	getTraceId(): string;
	getCorrelationType(): string;
	/**
	 * @param value 
	 */
	setCorrelationId(value: string): void;
	/**
	 * @param value 
	 */
	setSourceType(value: string): void;
	/**
	 * @param value 
	 */
	setSourceIdentity(value: string): void;
	/**
	 * @param value 
	 */
	setTimeStamp(value: any): void;
	/**
	 * @param value 
	 */
	setEventTopicId(value: string): void;
	/**
	 * @param value 
	 */
	setData(value: vCACCAFELiteralMap): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
}

declare interface vCACCAFEEventBrokerClient {
	getEventBrokerEventTopicService(): vCACCAFEEventBrokerEventTopicService;
	/**
	 * Does a POST request expanding the given template variables in order.
	 * @param resourceUrl 
	 * @param object 
	 * @param urlVariables 
	 */
	postWithVariables(resourceUrl: string, object: any, urlVariables: string[]): vCACCAFEServiceResponse;
	/**
	 * Does a PUT request expanding the given template variables in order.
	 * @param resourceUrl 
	 * @param object 
	 * @param urlVariables 
	 */
	putWithVariables(resourceUrl: string, object: any, urlVariables: string[]): vCACCAFEServiceResponse;
	/**
	 * Does a DELETE request expanding the given template variables in order.
	 * @param resourceUrl 
	 * @param urlVariables 
	 */
	deleteWithVariables(resourceUrl: string, urlVariables: string[]): void;
	/**
	 * Gets the REST client URL.
	 */
	getUrl(): string;
	/**
	 * Indicates whether the current connection is already closed or not.
	 */
	isClosed(): boolean;
	/**
	 * Does a GET request for a file and allows specifying 'Accept' header.
	 * @param resourceUrl 
	 * @param acceptHeaderValue 
	 */
	getFileWithAcceptHeader(resourceUrl: string, acceptHeaderValue: string): any;
	/**
	 * Does a POST request with a file.
	 * @param resourceUrl 
	 * @param object 
	 */
	postFile(resourceUrl: string, object: any): vCACCAFEServiceResponse;
	/**
	 * Does a GET request expanding the given template variables in order.
	 * @param resourceUrl 
	 * @param urlVariables 
	 */
	getWithVariables(resourceUrl: string, urlVariables: string[]): any;
	/**
	 * Does a GET request.
	 * @param resourceUrl 
	 */
	get(resourceUrl: string): any;
	/**
	 * Does a PUT request.
	 * @param resourceUrl 
	 * @param object 
	 */
	put(resourceUrl: string, object: any): vCACCAFEServiceResponse;
	/**
	 * Does a DELETE request.
	 * @param resourceUrl 
	 */
	delete(resourceUrl: string): void;
	/**
	 * Invalidates the current connection.
	 */
	close(): void;
	/**
	 * Does a GET request for a file.
	 * @param resourceUrl 
	 */
	getFile(resourceUrl: string): any;
	/**
	 * Does a POST request.
	 * @param resourceUrl 
	 * @param object 
	 */
	post(resourceUrl: string, object: any): vCACCAFEServiceResponse;
}

declare interface vCACCAFEEventBrokerEventTopicService {
	/**
	 * This method invokes the SDK method EventTopicService.getEventTopic(String eventTopicId).
	 * @param eventTopicId 
	 */
	getEventTopic(eventTopicId: string): vCACCAFEEventTopic;
	/**
	 * This method invokes the SDK method EventTopicService.getAllEventTopics(Pageable pageable).
	 * @param pageable 
	 */
	getAllEventTopics(pageable: any): vCACCAFEPagedResources;
}

declare interface vCACCAFEEventClient {
	getEventSystemWorkflowSubscriptionService(): vCACCAFEEventSystemWorkflowSubscriptionService;
	getEventTenantWorkflowSubscriptionService(): vCACCAFEEventTenantWorkflowSubscriptionService;
	/**
	 * Does a POST request expanding the given template variables in order.
	 * @param resourceUrl 
	 * @param object 
	 * @param urlVariables 
	 */
	postWithVariables(resourceUrl: string, object: any, urlVariables: string[]): vCACCAFEServiceResponse;
	/**
	 * Does a PUT request expanding the given template variables in order.
	 * @param resourceUrl 
	 * @param object 
	 * @param urlVariables 
	 */
	putWithVariables(resourceUrl: string, object: any, urlVariables: string[]): vCACCAFEServiceResponse;
	/**
	 * Does a DELETE request expanding the given template variables in order.
	 * @param resourceUrl 
	 * @param urlVariables 
	 */
	deleteWithVariables(resourceUrl: string, urlVariables: string[]): void;
	/**
	 * Gets the REST client URL.
	 */
	getUrl(): string;
	/**
	 * Indicates whether the current connection is already closed or not.
	 */
	isClosed(): boolean;
	/**
	 * Does a GET request for a file and allows specifying 'Accept' header.
	 * @param resourceUrl 
	 * @param acceptHeaderValue 
	 */
	getFileWithAcceptHeader(resourceUrl: string, acceptHeaderValue: string): any;
	/**
	 * Does a POST request with a file.
	 * @param resourceUrl 
	 * @param object 
	 */
	postFile(resourceUrl: string, object: any): vCACCAFEServiceResponse;
	/**
	 * Does a GET request expanding the given template variables in order.
	 * @param resourceUrl 
	 * @param urlVariables 
	 */
	getWithVariables(resourceUrl: string, urlVariables: string[]): any;
	/**
	 * Does a GET request.
	 * @param resourceUrl 
	 */
	get(resourceUrl: string): any;
	/**
	 * Does a PUT request.
	 * @param resourceUrl 
	 * @param object 
	 */
	put(resourceUrl: string, object: any): vCACCAFEServiceResponse;
	/**
	 * Does a DELETE request.
	 * @param resourceUrl 
	 */
	delete(resourceUrl: string): void;
	/**
	 * Invalidates the current connection.
	 */
	close(): void;
	/**
	 * Does a GET request for a file.
	 * @param resourceUrl 
	 */
	getFile(resourceUrl: string): any;
	/**
	 * Does a POST request.
	 * @param resourceUrl 
	 * @param object 
	 */
	post(resourceUrl: string, object: any): vCACCAFEServiceResponse;
}

declare interface vCACCAFEEventLogClient {
	getEventLogEventLogService(): vCACCAFEEventLogEventLogService;
	/**
	 * Does a POST request expanding the given template variables in order.
	 * @param resourceUrl 
	 * @param object 
	 * @param urlVariables 
	 */
	postWithVariables(resourceUrl: string, object: any, urlVariables: string[]): vCACCAFEServiceResponse;
	/**
	 * Does a PUT request expanding the given template variables in order.
	 * @param resourceUrl 
	 * @param object 
	 * @param urlVariables 
	 */
	putWithVariables(resourceUrl: string, object: any, urlVariables: string[]): vCACCAFEServiceResponse;
	/**
	 * Does a DELETE request expanding the given template variables in order.
	 * @param resourceUrl 
	 * @param urlVariables 
	 */
	deleteWithVariables(resourceUrl: string, urlVariables: string[]): void;
	/**
	 * Gets the REST client URL.
	 */
	getUrl(): string;
	/**
	 * Indicates whether the current connection is already closed or not.
	 */
	isClosed(): boolean;
	/**
	 * Does a GET request for a file and allows specifying 'Accept' header.
	 * @param resourceUrl 
	 * @param acceptHeaderValue 
	 */
	getFileWithAcceptHeader(resourceUrl: string, acceptHeaderValue: string): any;
	/**
	 * Does a POST request with a file.
	 * @param resourceUrl 
	 * @param object 
	 */
	postFile(resourceUrl: string, object: any): vCACCAFEServiceResponse;
	/**
	 * Does a GET request expanding the given template variables in order.
	 * @param resourceUrl 
	 * @param urlVariables 
	 */
	getWithVariables(resourceUrl: string, urlVariables: string[]): any;
	/**
	 * Does a GET request.
	 * @param resourceUrl 
	 */
	get(resourceUrl: string): any;
	/**
	 * Does a PUT request.
	 * @param resourceUrl 
	 * @param object 
	 */
	put(resourceUrl: string, object: any): vCACCAFEServiceResponse;
	/**
	 * Does a DELETE request.
	 * @param resourceUrl 
	 */
	delete(resourceUrl: string): void;
	/**
	 * Invalidates the current connection.
	 */
	close(): void;
	/**
	 * Does a GET request for a file.
	 * @param resourceUrl 
	 */
	getFile(resourceUrl: string): any;
	/**
	 * Does a POST request.
	 * @param resourceUrl 
	 * @param object 
	 */
	post(resourceUrl: string, object: any): vCACCAFEServiceResponse;
}

declare class vCACCAFEEventLogConfiguration {
	constructor();
	constructor();
	/**
	 * @param retentionPeriod 
	 * @param blacklistedTargettypes 
	 */
	constructor(retentionPeriod: number, blacklistedTargettypes: string[]);
	getRetentionPeriod(): number;
	/**
	 * @param retentionPeriod 
	 */
	setRetentionPeriod(retentionPeriod: number): void;
	getBlacklistedTargettypes(): string[];
	/**
	 * @param blacklistedTargettypes 
	 */
	setBlacklistedTargettypes(blacklistedTargettypes: string[]): void;
	getAvailableTargettypes(): string[];
}

declare interface vCACCAFEEventLogEventLogService {
	/**
	 * This method invokes the SDK method EventLogService.postEvent(Event event).
	 * @param event 
	 */
	postEvent(event: vCACCAFEEvent): any;
	/**
	 * This method invokes the SDK method EventLogService.publishEvent(Event event).
	 * @param event 
	 */
	publishEvent(event: vCACCAFEEvent): string;
	/**
	 * This method invokes the SDK method EventLogService.getAllEvents(Pageable pageable).
	 * @param pageable 
	 */
	getAllEvents(pageable: any): vCACCAFEPagedResources;
	/**
	 * This method invokes the SDK method EventLogService.getAllEventsByEventType(Pageable pageable, EventType eventType).
	 * @param pageable 
	 * @param eventType 
	 */
	getAllEventsByEventType(pageable: any, eventType: vCACCAFEEventType): vCACCAFEPagedResources;
	/**
	 * This method invokes the SDK method EventLogService.getEvent(String eventId).
	 * @param eventId 
	 */
	getEvent(eventId: string): vCACCAFEEvent;
	/**
	 * This method invokes the SDK method EventLogService.getEvent(URI uri).
	 * @param uri 
	 */
	getEventByUri(uri: any): vCACCAFEEvent;
}

declare interface vCACCAFEEventSystemWorkflowSubscriptionService {
	/**
	 * This method invokes the SDK method SystemWorkflowSubscriptionService.getSystemSubscriptions(Pageable pageable).
	 * @param pageable 
	 */
	getSystemSubscriptions(pageable: any): vCACCAFEPagedResources;
	/**
	 * This method invokes the SDK method SystemWorkflowSubscriptionService.getSystemSubscription(String subscriptionId).
	 * @param subscriptionId 
	 */
	getSystemSubscription(subscriptionId: string): vCACCAFEWorkflowSubscription;
	/**
	 * This method invokes the SDK method SystemWorkflowSubscriptionService.createSystemSubscription(WorkflowSubscription subscription).
	 * @param subscription 
	 */
	createSystemSubscription(subscription: vCACCAFEWorkflowSubscription): string;
	/**
	 * This method invokes the SDK method SystemWorkflowSubscriptionService.deleteSystemSubscription(String subscriptionId).
	 * @param subscriptionId 
	 */
	deleteSystemSubscription(subscriptionId: string): void;
	/**
	 * This method invokes the SDK method SystemWorkflowSubscriptionService.registerSystemSubscription(WorkflowSubscription subscription).
	 * @param subscription 
	 */
	registerSystemSubscription(subscription: vCACCAFEWorkflowSubscription): vCACCAFEWorkflowSubscription;
	/**
	 * This method invokes the SDK method SystemWorkflowSubscriptionService.updateSystemSubscription(WorkflowSubscription subscription).
	 * @param subscription 
	 */
	updateSystemSubscription(subscription: vCACCAFEWorkflowSubscription): void;
}

declare interface vCACCAFEEventTenantWorkflowSubscriptionService {
	/**
	 * This method invokes the SDK method TenantWorkflowSubscriptionService.cloneTenantSubscription(String tenantId, String tenantSubscriptionId).
	 * @param tenantId 
	 * @param tenantSubscriptionId 
	 */
	cloneTenantSubscription(tenantId: string, tenantSubscriptionId: string): string;
	/**
	 * This method invokes the SDK method TenantWorkflowSubscriptionService.createTenantSubscription(String tenantId, WorkflowSubscription subscription).
	 * @param tenantId 
	 * @param subscription 
	 */
	createTenantSubscription(tenantId: string, subscription: vCACCAFEWorkflowSubscription): string;
	/**
	 * This method invokes the SDK method TenantWorkflowSubscriptionService.deleteTenantSubscription(String tenantId, String subscriptionId).
	 * @param tenantId 
	 * @param subscriptionId 
	 */
	deleteTenantSubscription(tenantId: string, subscriptionId: string): void;
	/**
	 * This method invokes the SDK method TenantWorkflowSubscriptionService.registerTenantSubscription(String tenantId, WorkflowSubscription subscription).
	 * @param tenantId 
	 * @param subscription 
	 */
	registerTenantSubscription(tenantId: string, subscription: vCACCAFEWorkflowSubscription): vCACCAFEWorkflowSubscription;
	/**
	 * This method invokes the SDK method TenantWorkflowSubscriptionService.updateTenantSubscriptionStatus(String tenantId, String tenantSubscriptionId, DesignerPublishStatus status).
	 * @param tenantId 
	 * @param tenantSubscriptionId 
	 * @param status 
	 */
	updateTenantSubscriptionStatus(tenantId: string, tenantSubscriptionId: string, status: vCACCAFEDesignerPublishStatus): void;
	/**
	 * This method invokes the SDK method TenantWorkflowSubscriptionService.updateTenantSubscription(String tenantId, WorkflowSubscription subscription).
	 * @param tenantId 
	 * @param subscription 
	 */
	updateTenantSubscription(tenantId: string, subscription: vCACCAFEWorkflowSubscription): void;
	/**
	 * This method invokes the SDK method TenantWorkflowSubscriptionService.getTenantSubscriptions(String tenantId, Pageable pageable).
	 * @param tenantId 
	 * @param pageable 
	 */
	getTenantSubscriptions(tenantId: string, pageable: any): vCACCAFEPagedResources;
	/**
	 * This method invokes the SDK method TenantWorkflowSubscriptionService.getTenantSubscription(String tenantId, String subscriptionId).
	 * @param tenantId 
	 * @param subscriptionId 
	 */
	getTenantSubscription(tenantId: string, subscriptionId: string): vCACCAFEWorkflowSubscription;
}

declare class vCACCAFEEventTopic {
	name: string;
	id: string;
	type: vCACCAFEEventTopicType;
	description: string;
	schemaReference: any;
	replySchemaReference: any;
	blockable: boolean;
	persistable: boolean;
	replyable: boolean;
	vcoId: any;
	serviceTypeId: string;
	constructor();
	constructor();
	/**
	 * @param obj 
	 */
	equals(obj: any): boolean;
	toString(): string;
	hashCode(): number;
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	getType(): vCACCAFEEventTopicType;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	/**
	 * @param value 
	 */
	setType(value: vCACCAFEEventTopicType): void;
	getDescription(): string;
	getSchemaReference(): any;
	/**
	 * @param value 
	 */
	setSchemaReference(value: any): void;
	getReplySchemaReference(): any;
	/**
	 * @param value 
	 */
	setReplySchemaReference(value: any): void;
	isBlockable(): boolean;
	/**
	 * @param value 
	 */
	setBlockable(value: boolean): void;
	isPersistable(): boolean;
	/**
	 * @param value 
	 */
	setPersistable(value: boolean): void;
	isReplyable(): boolean;
	/**
	 * @param value 
	 */
	setReplyable(value: boolean): void;
	getVcoId(): any;
	/**
	 * @param undefined 
	 */
	setVcoId(id: any): void;
	/**
	 * @param value 
	 */
	setServiceTypeId(value: string): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	getServiceTypeId(): string;
}

declare class vCACCAFEEventTopicPermissions {
	constructor();
	constructor();
	/**
	 * @param canPublish 
	 * @param canSubscribe 
	 */
	constructor(canPublish: boolean, canSubscribe: boolean);
	getCanPublish(): boolean;
	/**
	 * @param value 
	 */
	setCanPublish(value: boolean): void;
	getCanSubscribe(): boolean;
	/**
	 * @param value 
	 */
	setCanSubscribe(value: boolean): void;
}

declare interface vCACCAFEEventTopicType {
	value(): string;
	values(): vCACCAFEEventTopicType[];
	/**
	 * @param name 
	 */
	valueOf(name: string): vCACCAFEEventTopicType;
	/**
	 * @param v 
	 */
	fromValue(v: string): vCACCAFEEventTopicType;
}

declare interface vCACCAFEEventType {
	value(): string;
	values(): vCACCAFEEventType[];
	/**
	 * @param name 
	 */
	valueOf(name: string): vCACCAFEEventType;
	/**
	 * @param v 
	 */
	fromValue(v: string): vCACCAFEEventType;
}

declare class vCACCAFEExportData {
	constructor();
	constructor();
	getEntityName(): string;
	getEntityVersion(): string;
	/**
	 * @param value 
	 */
	setEntityVersion(value: string): void;
	getEntityType(): string;
	/**
	 * @param value 
	 */
	setEntityType(value: string): void;
	getEntityHash(): string;
	/**
	 * @param value 
	 */
	setEntityName(value: string): void;
	/**
	 * @param value 
	 */
	setEntityHash(value: string): void;
}

declare class vCACCAFEExportInfo {
	constructor();
	constructor();
	getData(): vCACCAFEExportData[];
	getVcacVersion(): string;
	/**
	 * @param value 
	 */
	setVcacVersion(value: string): void;
	getExportTime(): any;
	/**
	 * @param value 
	 */
	setExportTime(value: any): void;
}

declare class vCACCAFEExpression {
	constructor();
	/**
	 * @param operator 
	 * @param left 
	 * @param right 
	 */
	constructor(operator: com.vmware.vcac.platform.content.operators.CriteriaOperator, left: com.vmware.vcac.platform.content.evaluators.Evaluator, right: com.vmware.vcac.platform.content.evaluators.Evaluator);
	getLeftOperand(): any;
	getRightOperand(): any;
	getOperator(): any;
	getConstantValue(): vCACCAFEBooleanLiteral;
	isConstant(): boolean;
	/**
	 * @param bVal 
	 */
	fromBoolean(bVal: boolean): any;
	/**
	 * @param sVal 
	 */
	fromString(sVal: string): any;
	/**
	 * @param lVal 
	 */
	fromInt(lVal: number): any;
	/**
	 * @param dVal 
	 */
	fromDecimal(dVal: number): any;
}

declare interface vCACCAFEExtensibilityEventTopics {
	value(): string;
	values(): vCACCAFEExtensibilityEventTopics[];
	/**
	 * @param name 
	 */
	valueOf(name: string): vCACCAFEExtensibilityEventTopics;
	/**
	 * @param v 
	 */
	fromValue(v: string): vCACCAFEExtensibilityEventTopics;
}

declare class vCACCAFEExtensionForm {
	constructor();
	constructor();
	getExtensionId(): string;
	getExtensionPointId(): string;
	/**
	 * @param extensionPointId 
	 */
	setExtensionPointId(extensionPointId: string): void;
	/**
	 * @param extensionId 
	 */
	setExtensionId(extensionId: string): void;
	/**
	 * @param extensionId 
	 */
	newExtensionId(extensionId: string): vCACCAFEExtensionForm;
	/**
	 * @param extensionPointId 
	 */
	newExtensionPoint(extensionPointId: string): vCACCAFEExtensionForm;
}

declare class vCACCAFEExtensionIdContainer {
	constructor();
	constructor();
	getExtensionId(): string;
	/**
	 * @param value 
	 */
	setExtensionId(value: string): void;
}

declare class vCACCAFEExtensionInfo {
	constructor();
	constructor();
	getPlugin(): vCACCAFEPluginInfo;
	getPermissions(): string[];
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	/**
	 * @param value 
	 */
	setLabel(value: string): void;
	getExtensionId(): string;
	getIconUrl(): string;
	/**
	 * @param value 
	 */
	setIconUrl(value: string): void;
	getJson(): string;
	/**
	 * @param value 
	 */
	setJson(value: string): void;
	getExtensionPointId(): string;
	/**
	 * @param value 
	 */
	setExtensionPointId(value: string): void;
	/**
	 * @param value 
	 */
	setExtensionId(value: string): void;
	getLabel(): string;
	/**
	 * @param value 
	 */
	setPlugin(value: vCACCAFEPluginInfo): void;
}

declare class vCACCAFEExtensionRendererContext {
	constructor();
	/**
	 * @param extensionId 
	 * @param parameterMappings 
	 */
	constructor(extensionId: string, parameterMappings: vCACCAFEParameterMappingCollection);
	/**
	 * @param extensionId 
	 */
	constructor(extensionId: string);
	getParameterMappings(): vCACCAFEParameterMappingCollection;
	getExtensionId(): string;
}

declare class vCACCAFEExternalDataContext {
	constructor();
	constructor();
	/**
	 * @param classId 
	 * @param instanceId 
	 */
	constructor(classId: string, instanceId: string);
	getClassId(): string;
	/**
	 * @param classId 
	 */
	setClassId(classId: string): void;
	getInstanceId(): string;
	/**
	 * @param instanceId 
	 */
	setInstanceId(instanceId: string): void;
}

declare class vCACCAFEExternalFormReference {
	constructor();
	constructor();
	/**
	 * @param formId 
	 */
	constructor(formId: string);
	getFormId(): string;
	/**
	 * @param formId 
	 */
	setFormId(formId: string): void;
}

declare class vCACCAFEExternalProviderResource {
	constructor();
	constructor();
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	getResourceTypeId(): string;
	/**
	 * @param value 
	 */
	setResourceTypeId(value: string): void;
	getResourceData(): vCACCAFELiteralMap;
	/**
	 * @param value 
	 */
	setResourceData(value: vCACCAFELiteralMap): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
}

declare class vCACCAFEExternalResourceType {
	constructor();
	constructor();
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setId(value: string): void;
}

declare class vCACCAFEExternalSchemaReference {
	constructor();
	constructor();
	/**
	 * @param schemaId 
	 */
	constructor(schemaId: string);
	/**
	 * @param schemaId 
	 */
	setSchemaId(schemaId: string): void;
	getSchemaId(): string;
}

declare class vCACCAFEExternalValue {
	constructor();
	/**
	 * @param dynamicValueProviderInfo 
	 */
	constructor(dynamicValueProviderInfo: vCACCAFEDynamicValueContext);
	getDynamicValueProviderInfo(): vCACCAFEDynamicValueContext;
	getConstantValue(): any;
	isConstant(): boolean;
	/**
	 * @param bVal 
	 */
	fromBoolean(bVal: boolean): any;
	/**
	 * @param sVal 
	 */
	fromString(sVal: string): any;
	/**
	 * @param lVal 
	 */
	fromInt(lVal: number): any;
	/**
	 * @param dVal 
	 */
	fromDecimal(dVal: number): any;
}

declare interface vCACCAFEFabricGroup {
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): any;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	getHref(): string;
	getAdministrators(): string[];
	/**
	 * @param value 
	 */
	setAdministrators(value: string[]): void;
	/**
	 * @param value 
	 */
	setHref(value: string): void;
	/**
	 * @param value 
	 */
	setId(value: any): void;
}

declare interface vCACCAFEFabricGroupService {
	/**
	 * @param id 
	 */
	delete(id: any): void;
	/**
	 * @param group 
	 */
	create(group: vCACCAFEFabricGroup): any;
}

declare class vCACCAFEFacetValueMap {
	constructor();
	constructor();
	/**
	 * @param valuesMap 
	 */
	constructor(valuesMap: java.util.Map);
	asMap(): any;
	/**
	 * @param key 
	 */
	remove(key: any): any;
	/**
	 * @param key 
	 */
	get(key: any): any;
	/**
	 * @param key 
	 * @param value 
	 */
	put(key: any, value: any): any;
	values(): any[];
	clear(): void;
	isEmpty(): boolean;
	size(): number;
	entrySet(): any[];
	/**
	 * @param m 
	 */
	putAll(m: any): void;
	keySet(): any[];
	/**
	 * @param value 
	 */
	containsValue(value: any): boolean;
	/**
	 * @param key 
	 */
	containsKey(key: any): boolean;
	/**
	 * @param facets 
	 */
	setFacets(facets: any[]): void;
}

declare class vCACCAFEFeature {
	constructor();
	constructor();
	getId(): string;
	/**
	 * @param value 
	 */
	setId(value: string): void;
}

declare class vCACCAFEFieldList {
	constructor();
	/**
	 * @param fields 
	 */
	constructor(fields: com.vmware.vcac.platform.content.schema.definitions.FieldInsinuation[]);
	constructor();
	/**
	 * @param fields 
	 */
	setFields(fields: any[]): void;
	isEmpty(): boolean;
	size(): number;
	getFields(): any[];
}

declare class vCACCAFEFieldReference {
	constructor();
	/**
	 * @param path 
	 */
	constructor(path: string);
	getConstantValue(): any;
	getPath(): string;
	isConstant(): boolean;
	/**
	 * @param bVal 
	 */
	fromBoolean(bVal: boolean): any;
	/**
	 * @param sVal 
	 */
	fromString(sVal: string): any;
	/**
	 * @param lVal 
	 */
	fromInt(lVal: number): any;
	/**
	 * @param dVal 
	 */
	fromDecimal(dVal: number): any;
}

declare class vCACCAFEFieldReferenceInsinuation {
	constructor();
	/**
	 * @param path 
	 */
	constructor(path: string);
	constructor();
	/**
	 * @param path 
	 */
	setPath(path: string): void;
	getPath(): string;
	/**
	 * @param handler 
	 */
	handle(handler: any): any;
}

declare class vCACCAFEFilterData {
	constructor();
	constructor();
	getId(): string;
	getEntityType(): string;
	/**
	 * @param value 
	 */
	setEntityType(value: string): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
}

declare class vCACCAFEFilterInfo {
	constructor();
	constructor();
	getData(): vCACCAFEFilterData[];
	getTenantId(): string;
	isJsonAccepted(): boolean;
	/**
	 * @param value 
	 */
	setJsonAccepted(value: boolean): void;
	/**
	 * @param value 
	 */
	setTenantId(value: string): void;
}

declare class vCACCAFEFilterParam {
	constructor();
	/**
	 * @param operation 
	 * @param property 
	 * @param value 
	 */
	constructor(operation: string, property: string, value: any);
	toString(): string;
	/**
	 * @param date 
	 */
	static date(date: any): string;
	/**
	 * @param property 
	 */
	static toupper(property: string): vCACCAFEFilterParam;
	/**
	 * @param value 
	 */
	static guid(value: any): string;
	/**
	 * @param date 
	 */
	static dateNoOffset(date: any): string;
	static toParamValue(): string;
	/**
	 * @param value 
	 */
	static string(value: string): string;
	/**
	 * @param params 
	 */
	static group(params: vCACCAFEFilterParam[]): vCACCAFEFilterParam;
	/**
	 * @param property 
	 * @param value 
	 */
	static startsWith(property: string, value: any): vCACCAFEFilterParam;
	/**
	 * @param property 
	 * @param value 
	 */
	static endsWith(property: string, value: any): vCACCAFEFilterParam;
	/**
	 * @param property 
	 */
	static isNull(property: string): vCACCAFEFilterParam;
	/**
	 * @param conditions 
	 */
	static and(conditions: any[]): vCACCAFEFilterParam;
	/**
	 * @param conditions 
	 */
	static or(conditions: any[]): vCACCAFEFilterParam;
	/**
	 * @param property 
	 * @param value 
	 */
	static equal(property: string, value: any): vCACCAFEFilterParam;
	/**
	 * @param param 
	 */
	static not(param: vCACCAFEFilterParam): vCACCAFEFilterParam;
	/**
	 * @param property 
	 */
	static tolower(property: string): vCACCAFEFilterParam;
	/**
	 * @param property 
	 * @param value 
	 */
	static substringOf(property: string, value: string): vCACCAFEFilterParam;
	/**
	 * @param property 
	 * @param value 
	 */
	static greaterThan(property: string, value: any): vCACCAFEFilterParam;
	/**
	 * @param property 
	 * @param value 
	 */
	static lessThan(property: string, value: any): vCACCAFEFilterParam;
	/**
	 * @param property 
	 * @param value 
	 */
	static notEqual(property: string, value: any): vCACCAFEFilterParam;
	/**
	 * @param property 
	 */
	static isNotNull(property: string): vCACCAFEFilterParam;
	/**
	 * @param property 
	 * @param value 
	 */
	static greaterThanOrEqual(property: string, value: any): vCACCAFEFilterParam;
	/**
	 * @param property 
	 * @param value 
	 */
	static lessThanOrEqual(property: string, value: any): vCACCAFEFilterParam;
}

declare class vCACCAFEFixedValueConstraint {
	constructor();
	/**
	 * @param value 
	 */
	constructor(value: com.vmware.vcac.platform.content.literals.Literal);
	/**
	 * @param value 
	 */
	constructor(value: com.vmware.vcac.platform.content.evaluators.Evaluator);
	/**
	 * @param value 
	 */
	cloneWithValue(value: any): vCACCAFEFixedValueConstraint;
	/**
	 * @param constraint 
	 */
	mergeConstraint(constraint: any): vCACCAFEFixedValueConstraint;
	clone(): any;
	getValue(): any;
}

declare class vCACCAFEForm {
	constructor();
	/**
	 * @param layout 
	 * @param values 
	 * @param prefixes 
	 */
	constructor(layout: vCACCAFELayout, values: java.util.Map, prefixes: string[]);
	/**
	 * @param layout 
	 * @param values 
	 */
	constructor(layout: vCACCAFELayout, values: java.util.Map);
	getLayout(): vCACCAFELayout;
	getFields(): any;
	/**
	 * @param fieldId 
	 */
	getField(fieldId: string): any;
	getValues(): vCACCAFELiteralMap;
	getFieldPrefixes(): string[];
	/**
	 * @param fieldPrefixes 
	 */
	setFieldPrefixes(fieldPrefixes: string[]): void;
}

declare interface vCACCAFEFormScale {
	value(): string;
	values(): vCACCAFEFormScale[];
	/**
	 * @param name 
	 */
	valueOf(name: string): vCACCAFEFormScale;
	/**
	 * @param v 
	 */
	fromValue(v: string): vCACCAFEFormScale;
}

declare class vCACCAFEFormScenario {
	constructor();
	constructor();
	getForm(): vCACCAFEBlueprintForm;
	toString(): string;
	getId(): string;
	/**
	 * @param value 
	 */
	setForm(value: vCACCAFEBlueprintForm): void;
	getScenario(): vCACCAFELayoutScenarioImpl;
	/**
	 * @param value 
	 */
	setScenario(value: vCACCAFELayoutScenarioImpl): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
}

declare class vCACCAFEFormsNotificationContent {
	constructor();
	constructor();
	getData(): vCACCAFELiteralMap;
	getBody(): vCACCAFELayout;
	/**
	 * @param value 
	 */
	setSubject(value: vCACCAFELayoutFlow): void;
	getDataPrefixes(): string[];
	/**
	 * @param value 
	 */
	setData(value: vCACCAFELiteralMap): void;
	/**
	 * @param value 
	 */
	setBody(value: vCACCAFELayout): void;
	getSubject(): vCACCAFELayoutFlow;
}

declare class vCACCAFEFormsScope {
	constructor();
	constructor();
	/**
	 * @param value 
	 */
	setLevel(value: string): void;
	getLevel(): string;
}

declare class vCACCAFEGenericForm {
	constructor();
	constructor();
	getLayout(): any;
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	getTenant(): string;
	/**
	 * @param value 
	 */
	setTenant(value: string): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	/**
	 * @param value 
	 */
	setLayout(value: any): void;
}

declare interface vCACCAFEGreaterThanOperator {
	getId(): string;
	/**
	 * @param left 
	 * @param right 
	 */
	evaluate(left: any, right: any): vCACCAFEBooleanLiteral;
	getDataTypeConstraint(): any;
	getCategory(): any;
	getMultiplicityConstraint(): any;
}

declare interface vCACCAFEGreaterThanOrEqualsOperator {
	getId(): string;
	/**
	 * @param left 
	 * @param right 
	 */
	evaluate(left: any, right: any): vCACCAFEBooleanLiteral;
	getDataTypeConstraint(): any;
	getCategory(): any;
	getMultiplicityConstraint(): any;
}

declare class vCACCAFEGroup {
	constructor();
	constructor();
	/**
	 * @param value 
	 */
	setDomain(value: string): void;
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	getDomain(): string;
	/**
	 * @param value 
	 */
	setGroupType(value: vCACCAFEType): void;
	getNetbios(): string;
	/**
	 * @param value 
	 */
	setNetbios(value: string): void;
	getFqdn(): string;
	/**
	 * @param value 
	 */
	setFqdn(value: string): void;
	getPrincipalId(): vCACCAFEPrincipalId;
	getEmailAddress(): string;
	/**
	 * @param value 
	 */
	setEmailAddress(value: string): void;
	/**
	 * @param value 
	 */
	setPrincipalId(value: vCACCAFEPrincipalId): void;
	getGroupType(): vCACCAFEType;
}

declare class vCACCAFEGroupSearchCriteria {
	constructor();
	/**
	 * @param tenantName 
	 */
	constructor(tenantName: string);
	/**
	 * @param domain 
	 */
	setDomain(domain: string): void;
	getDomain(): string;
	getPermissionFilter(): vCACCAFEPermissionFilter;
	/**
	 * @param permissionFilter 
	 */
	setPermissionFilter(permissionFilter: vCACCAFEPermissionFilter): void;
	/**
	 * @param groupType 
	 */
	setGroupType(groupType: vCACCAFEType): void;
	getTenantName(): string;
	getParentGroup(): vCACCAFEGroup;
	/**
	 * @param parentGroup 
	 */
	setParentGroup(parentGroup: vCACCAFEGroup): void;
	getGroupType(): vCACCAFEType;
	/**
	 * @param criteria 
	 */
	setCriteria(criteria: string): void;
	getCriteria(): string;
}

/**
 * vCloud Automation Center host
 */
declare interface vCACCAFEHost {
	readonly url: string;
	readonly connectionTimeout: number;
	readonly sessionMode: string;
	readonly operationTimeout: number;
	readonly name: string;
	readonly id: string;
	readonly displayName: string;
	readonly tenant: string;
	readonly pageSize: number;
	readonly username: string;
	/**
	 * Validates this vCAC host connection.
	 */
	validate(): void;
	/**
	 * Creates a REST client for the Catalog Provider service.
	 */
	createCatalogProviderClient(): vCACCAFECatalogProviderClient;
	/**
	 * Creates a REST client for the Envent Log service.
	 */
	createEventLogClient(): vCACCAFEEventLogClient;
	/**
	 * Creates a REST client for the Notification service.
	 */
	createNotificationClient(): vCACCAFENotificationClient;
	/**
	 * Creates a temporary vCAC host with the current vCAC host settings and the given username and password.
	 * @param username 
	 * @param password 
	 */
	getHostWithCredentials(username: string, password: string): vCACCAFEHost;
	/**
	 * Creates a temporary vCAC host with the current vCAC host settings but for another tenant and the given username and password.
	 * @param tenant 
	 * @param username 
	 * @param password 
	 */
	getHostForTenant(tenant: string, username: string, password: string): vCACCAFEHost;
	/**
	 * Creates a temporary vCAC host with the current vCAC host settings and the current vCO user credentials.
	 */
	getHostForCurrentUser(): vCACCAFEHost;
	/**
	 * Creates a REST client for the Content management service.
	 */
	createContentManagementClient(): vCACCAFERestClient;
	/**
	 * Creates a REST client for the Authorization service.
	 */
	createAuthorizationClient(): vCACCAFEAuthorizationClient;
	/**
	 * Creates a generic REST client for this vCAC host service endpoint.
	 * @param endpoint 
	 */
	createRestClient(endpoint: string): vCACCAFERestClient;
	/**
	 * Removes this vCAC host.
	 */
	remove(): void;
	/**
	 * Updates this vCAC host properties.
	 * @param properties 
	 */
	update(properties: any): vCACCAFEHost;
	/**
	 * Creates a REST client for the Property service.
	 */
	createPropertyClient(): vCACCAFEPropertyClient;
	/**
	 * Create a REST client for the endpoint configuration service
	 */
	createEndpointConfigurationClient(): vCACCAFEEndpointConfigurationClient;
	/**
	 * Creates a REST client for the Advanced Service Designer service.
	 */
	createAdvancedDesignerClient(): vCACCAFEAdvancedDesignerClient;
	/**
	 * Creates a REST client for the Event Broker service.
	 */
	createEventBrokerClient(): vCACCAFEEventBrokerClient;
	/**
	 * Creates a REST client for the Advanced Service Designer service.
	 */
	createEventClient(): vCACCAFEEventClient;
	/**
	 * Creates a REST client for the Composition service.
	 */
	createCompositionClient(): vCACCAFECompositionClient;
	/**
	 * Creates a REST client for the Reservation service.
	 */
	createReservationClient(): vCACCAFEReservationClient;
	/**
	 * Creates a REST client for the Authentication service.
	 */
	createAuthenticationClient(): vCACCAFEAuthenticationClient;
	/**
	 * Creates a REST client for the Infrastructure services.
	 */
	createInfrastructureClient(): vCACCAFEInfrastructureClient;
	/**
	 * Creates a REST client for the Catalog service.
	 */
	createCatalogClient(): vCACCAFECatalogClient;
	/**
	 * Creates a REST client for the Work item service.
	 */
	createWorkItemClient(): vCACCAFEWorkItemClient;
	/**
	 * Creates a REST client for the Approval service.
	 */
	createApprovalClient(): vCACCAFEApprovalClient;
}

declare class vCACCAFEIaasPropertyNameBehavior {
	constructor();
	/**
	 * @param value 
	 */
	constructor(value: com.vmware.vcac.platform.content.evaluators.Evaluator);
	/**
	 * @param value 
	 */
	constructor(value: com.vmware.vcac.platform.content.literals.Literal);
	/**
	 * @param value 
	 */
	cloneWithValue(value: any): any;
	/**
	 * @param constraint 
	 */
	mergeConstraint(constraint: any): vCACCAFEIaasPropertyNameBehavior;
	clone(): any;
	getValue(): any;
}

declare class vCACCAFEIcon {
	constructor();
	constructor();
	getContentType(): string;
	/**
	 * @param value 
	 */
	setContentType(value: string): void;
	/**
	 * @param value 
	 */
	setOrganization(value: vCACCAFECatalogOrganizationReference): void;
	getOrganization(): vCACCAFECatalogOrganizationReference;
	getImage(): string;
	getId(): string;
	getFileName(): string;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	/**
	 * @param value 
	 */
	setImage(value: string): void;
	/**
	 * @param value 
	 */
	setFileName(value: string): void;
}

declare class vCACCAFEIdentityStore {
	url: string;
	name: string;
	type: vCACCAFEIdentityStoreType;
	description: string;
	domain: string;
	vcoId: any;
	alias: string;
	groupBaseSearchDn: string;
	userBaseSearchDn: string;
	userNameDn: string;
	domainAdminUsername: string;
	domainAdminPassword: string;
	subdomains: string[];
	groupBaseSearchDns: string[];
	userBaseSearchDns: string[];
	useGlobalCatalog: boolean;
	groupObjectQuery: string;
	bindUserObjectQuery: string;
	userObjectQuery: string;
	customDirectorySearchAttribute: string;
	membershipAttribute: string;
	objectUuidAttribute: string;
	distinguishedNameAttribute: string;
	constructor();
	constructor();
	/**
	 * @param value 
	 */
	setUrl(value: string): void;
	getUrl(): string;
	/**
	 * @param value 
	 */
	setDomain(value: string): void;
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getType(): vCACCAFEIdentityStoreType;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	/**
	 * @param value 
	 */
	setType(value: vCACCAFEIdentityStoreType): void;
	getDescription(): string;
	getDomain(): string;
	getVcoId(): any;
	/**
	 * @param undefined 
	 */
	setVcoId(id: any): void;
	/**
	 * @param value 
	 */
	setAlias(value: string): void;
	getAlias(): string;
	getPassword(): string;
	/**
	 * @param value 
	 */
	setPassword(value: string): void;
	getGroupBaseSearchDn(): string;
	/**
	 * @param value 
	 */
	setGroupBaseSearchDn(value: string): void;
	getUserBaseSearchDn(): string;
	/**
	 * @param value 
	 */
	setUserBaseSearchDn(value: string): void;
	getUserNameDn(): string;
	/**
	 * @param value 
	 */
	setUserNameDn(value: string): void;
	getDomainAdminUsername(): string;
	/**
	 * @param value 
	 */
	setDomainAdminUsername(value: string): void;
	getDomainAdminPassword(): string;
	/**
	 * @param value 
	 */
	setDomainAdminPassword(value: string): void;
	getSubdomains(): string[];
	getGroupBaseSearchDns(): string[];
	getUserBaseSearchDns(): string[];
	isUseGlobalCatalog(): boolean;
	/**
	 * @param value 
	 */
	setUseGlobalCatalog(value: boolean): void;
	getGroupObjectQuery(): string;
	/**
	 * @param value 
	 */
	setGroupObjectQuery(value: string): void;
	getBindUserObjectQuery(): string;
	/**
	 * @param value 
	 */
	setBindUserObjectQuery(value: string): void;
	getUserObjectQuery(): string;
	/**
	 * @param value 
	 */
	setUserObjectQuery(value: string): void;
	getCustomDirectorySearchAttribute(): string;
	/**
	 * @param value 
	 */
	setCustomDirectorySearchAttribute(value: string): void;
	getMembershipAttribute(): string;
	/**
	 * @param value 
	 */
	setMembershipAttribute(value: string): void;
	getObjectUuidAttribute(): string;
	/**
	 * @param value 
	 */
	setObjectUuidAttribute(value: string): void;
	getDistinguishedNameAttribute(): string;
	/**
	 * @param value 
	 */
	setDistinguishedNameAttribute(value: string): void;
}

declare class vCACCAFEIdentityStoreConnectivityStatus {
	constructor();
	constructor();
	getMessage(): string;
	getStatus(): vCACCAFEConnectivityStatusEnum;
	/**
	 * @param value 
	 */
	setStatus(value: vCACCAFEConnectivityStatusEnum): void;
	/**
	 * @param value 
	 */
	setMessage(value: string): void;
}

declare class vCACCAFEIdentityStoreMigrationResult {
	constructor();
	constructor();
	getWarnings(): string[];
	getIdentityStore(): vCACCAFEIdentityStore;
	/**
	 * @param value 
	 */
	setIdentityStore(value: vCACCAFEIdentityStore): void;
}

declare class vCACCAFEIdentityStorePrincipalsMigrationSpec {
	constructor();
	constructor();
	getIdentityStore(): vCACCAFEIdentityStore;
	getPrincipalIds(): vCACCAFEPrincipalId[];
	/**
	 * @param value 
	 */
	setIdentityStore(value: vCACCAFEIdentityStore): void;
}

declare class vCACCAFEIdentityStoreStatus {
	constructor();
	constructor();
	getSyncStatus(): vCACCAFEIdentityStoreSyncStatus;
	/**
	 * @param value 
	 */
	setSyncStatus(value: vCACCAFEIdentityStoreSyncStatus): void;
}

declare class vCACCAFEIdentityStoreSyncStatus {
	constructor();
	constructor();
	getMessage(): string;
	getStatus(): vCACCAFESyncStatusEnum;
	/**
	 * @param value 
	 */
	setStatus(value: vCACCAFESyncStatusEnum): void;
	/**
	 * @param value 
	 */
	setMessage(value: string): void;
}

declare interface vCACCAFEIdentityStoreType {
	value(): string;
	values(): vCACCAFEIdentityStoreType[];
	/**
	 * @param name 
	 */
	valueOf(name: string): vCACCAFEIdentityStoreType;
	/**
	 * @param v 
	 */
	fromValue(v: string): vCACCAFEIdentityStoreType;
}

declare class vCACCAFEIdpConfiguration {
	constructor();
	constructor();
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	getExternalIdpContent(): string;
	/**
	 * @param value 
	 */
	setExternalIdpContent(value: string): void;
	getExternalIdpURI(): string;
	/**
	 * @param value 
	 */
	setExternalIdpURI(value: string): void;
}

declare class vCACCAFEImportData {
	constructor();
	constructor();
	getEntityName(): string;
	getMessage(): string;
	getEntityType(): string;
	/**
	 * @param value 
	 */
	setEntityType(value: string): void;
	/**
	 * @param value 
	 */
	setLogLevel(value: vCACCAFELogLevel): void;
	getLogLevel(): vCACCAFELogLevel;
	getEntityId(): string;
	/**
	 * @param value 
	 */
	setEntityId(value: string): void;
	/**
	 * @param value 
	 */
	setEntityName(value: string): void;
	getMessageKey(): string;
	/**
	 * @param value 
	 */
	setMessageKey(value: string): void;
	/**
	 * @param value 
	 */
	setMessage(value: string): void;
}

declare class vCACCAFEImportInfo {
	constructor();
	constructor();
	getData(): vCACCAFEImportData[];
	/**
	 * @param value 
	 */
	setImportStatus(value: vCACCAFEImportStatus): void;
	getImportStatus(): vCACCAFEImportStatus;
}

declare class vCACCAFEImportModificator {
	constructor();
	constructor();
	getPrefix(): string;
	isPrefixOnlyConflicting(): boolean;
	/**
	 * @param value 
	 */
	setPrefixOnlyConflicting(value: boolean): void;
	/**
	 * @param value 
	 */
	setPrefix(value: string): void;
}

declare class vCACCAFEImportRequest {
	constructor();
	constructor();
	getData(): any[];
	/**
	 * @param value 
	 */
	setMimeType(value: string): void;
	getClassId(): string;
	/**
	 * @param value 
	 */
	setClassId(value: string): void;
	isInDryRunMode(): boolean;
	/**
	 * @param value 
	 */
	setInDryRunMode(value: boolean): void;
	getResolutionMode(): vCACCAFEResolutionMode;
	/**
	 * @param value 
	 */
	setResolutionMode(value: vCACCAFEResolutionMode): void;
	getMimeType(): string;
	/**
	 * @param value 
	 */
	setData(value: any[]): void;
}

declare interface vCACCAFEImportStatus {
	value(): string;
	values(): vCACCAFEImportStatus[];
	/**
	 * @param name 
	 */
	valueOf(name: string): vCACCAFEImportStatus;
	/**
	 * @param v 
	 */
	fromValue(v: string): vCACCAFEImportStatus;
}

declare class vCACCAFEImportValidationData {
	constructor();
	constructor();
	getEntityName(): string;
	getMessage(): string;
	getEntityType(): string;
	/**
	 * @param value 
	 */
	setEntityType(value: string): void;
	/**
	 * @param value 
	 */
	setLogLevel(value: vCACCAFELogLevel): void;
	getLogLevel(): vCACCAFELogLevel;
	getEntityId(): string;
	/**
	 * @param value 
	 */
	setEntityId(value: string): void;
	/**
	 * @param value 
	 */
	setEntityName(value: string): void;
	getMessageKey(): string;
	/**
	 * @param value 
	 */
	setMessageKey(value: string): void;
	/**
	 * @param value 
	 */
	setMessage(value: string): void;
}

declare class vCACCAFEImportValidationInfo {
	constructor();
	constructor();
	getStatus(): vCACCAFEImportValidationStatus;
	/**
	 * @param value 
	 */
	setStatus(value: vCACCAFEImportValidationStatus): void;
	getData(): vCACCAFEImportData[];
	/**
	 * @param value 
	 */
	setImportStatus(value: vCACCAFEImportStatus): void;
	getImportStatus(): vCACCAFEImportStatus;
}

declare interface vCACCAFEImportValidationStatus {
	value(): string;
	values(): vCACCAFEImportValidationStatus[];
	/**
	 * @param name 
	 */
	valueOf(name: string): vCACCAFEImportValidationStatus;
	/**
	 * @param v 
	 */
	fromValue(v: string): vCACCAFEImportValidationStatus;
}

declare class vCACCAFEInboundNotification {
	constructor();
	constructor();
	getBody(): string;
	getAction(): string;
	getUserToken(): string;
	/**
	 * @param value 
	 */
	setUserToken(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setSubject(value: string): void;
	/**
	 * @param value 
	 */
	setAction(value: string): void;
	/**
	 * @param value 
	 */
	setNotificationId(value: string): void;
	getFromUser(): string;
	/**
	 * @param value 
	 */
	setFromUser(value: string): void;
	getMessageReceivedAt(): any;
	/**
	 * @param value 
	 */
	setMessageReceivedAt(value: any): void;
	getNotificationId(): string;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	/**
	 * @param value 
	 */
	setUserId(value: string): void;
	getUserId(): string;
	/**
	 * @param value 
	 */
	setBody(value: string): void;
	getSubject(): string;
}

declare class vCACCAFEIncrementBehavior {
	constructor();
	/**
	 * @param value 
	 */
	constructor(value: com.vmware.vcac.platform.content.evaluators.Evaluator);
	/**
	 * @param value 
	 */
	constructor(value: com.vmware.vcac.platform.content.literals.Literal);
	/**
	 * @param value 
	 */
	cloneWithValue(value: any): any;
	/**
	 * @param constraint 
	 */
	mergeConstraint(constraint: any): vCACCAFEIncrementBehavior;
	clone(): any;
	getValue(): any;
}

declare interface vCACCAFEInfrastructureClient {
	getFabricGroupService(): vCACCAFEFabricGroupService;
	getInfrastructureBusinessGroupsService(): vCACCAFEBusinessGroupsService;
	getInfrastructureMachinePrefixesService(): vCACCAFEMachinePrefixesService;
	/**
	 * Gets the REST client URL.
	 */
	getUrl(): string;
}

declare class vCACCAFEIntegerLiteral {
	constructor();
	/**
	 * @param value 
	 */
	constructor(value: number);
	getValue(): number;
	getTypeId(): vCACCAFEDataTypeId;
	isList(): boolean;
	/**
	 * @param other 
	 */
	compareTo(other: any): number;
	/**
	 * @param bVal 
	 */
	fromBoolean(bVal: boolean): any;
	/**
	 * @param obj 
	 */
	safeEquals(obj: any): boolean;
	/**
	 * @param sVal 
	 */
	fromString(sVal: string): any;
	/**
	 * @param lVal 
	 */
	fromInt(lVal: number): any;
	/**
	 * @param dVal 
	 */
	fromDecimal(dVal: number): any;
}

declare class vCACCAFEInteractionWorkItem {
	constructor();
	constructor();
	/**
	 * @param value 
	 */
	setWorkflowId(value: string): void;
	/**
	 * @param value 
	 */
	setSubtenantRef(value: string): void;
	getSubtenantRef(): string;
	getWorkflowExecutionId(): string;
	/**
	 * @param value 
	 */
	setWorkflowExecutionId(value: string): void;
	getRequestedBy(): string;
	getAssignees(): string[];
	getWorkflowId(): string;
	/**
	 * @param value 
	 */
	setRequestedBy(value: string): void;
}

declare class vCACCAFEInternalConstraint {
	constructor();
	/**
	 * @param isInternal 
	 */
	constructor(isInternal: boolean);
	/**
	 * @param value 
	 */
	constructor(value: com.vmware.vcac.platform.content.evaluators.Evaluator);
	/**
	 * @param value 
	 */
	cloneWithValue(value: any): vCACCAFEInternalConstraint;
	/**
	 * @param isInternal 
	 */
	fromBoolean(isInternal: boolean): vCACCAFEInternalConstraint;
	/**
	 * @param constraint 
	 */
	mergeConstraint(constraint: any): any;
	clone(): any;
	getValue(): any;
}

declare class vCACCAFEInventoryType {
	constructor();
	constructor();
	/**
	 * @param value 
	 */
	setPluginName(value: string): void;
	getName(): string;
	getProperties(): vCACCAFEInventoryTypeProperty[];
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	getPluginName(): string;
	getPluginType(): string;
	/**
	 * @param value 
	 */
	setPluginType(value: string): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
}

declare class vCACCAFEInventoryTypeProperty {
	constructor();
	constructor();
	/**
	 * @param value 
	 */
	setDisplayName(value: string): void;
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	getDisplayName(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	/**
	 * @param value 
	 */
	setId(value: string): void;
}

declare class vCACCAFEIpAllocationRequest {
	constructor();
	constructor();
	getRangeIds(): string[];
	getAllocationStartAddress(): string;
	/**
	 * @param value 
	 */
	setAllocationStartAddress(value: string): void;
	getAllocationSize(): number;
	/**
	 * @param value 
	 */
	setAllocationSize(value: number): void;
	isPrimary(): boolean;
	/**
	 * @param value 
	 */
	setPrimary(value: boolean): void;
	getBlueprintRequestId(): string;
	/**
	 * @param value 
	 */
	setBlueprintRequestId(value: string): void;
	getNicIndex(): number;
	/**
	 * @param value 
	 */
	setNicIndex(value: number): void;
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	/**
	 * @param value 
	 */
	setExtensionData(value: vCACCAFELiteralMap): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	getExtensionData(): vCACCAFELiteralMap;
}

declare class vCACCAFEIpAllocationResult {
	constructor();
	constructor();
	/**
	 * @param value 
	 */
	setRangeId(value: string): void;
	getRangeId(): string;
	getAllocatedAddresses(): string[];
	getIpAllocationRequestId(): string;
	/**
	 * @param value 
	 */
	setIpAllocationRequestId(value: string): void;
	getSubnetPrefixLength(): number;
	/**
	 * @param value 
	 */
	setSubnetPrefixLength(value: number): void;
	getIpVersion(): vCACCAFEIpVersionEnum;
	getDnsInfo(): vCACCAFEDnsInfo;
	/**
	 * @param value 
	 */
	setDnsInfo(value: vCACCAFEDnsInfo): void;
	/**
	 * @param value 
	 */
	setIpVersion(value: vCACCAFEIpVersionEnum): void;
	getGateway(): string;
	/**
	 * @param value 
	 */
	setGateway(value: string): void;
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	/**
	 * @param value 
	 */
	setExtensionData(value: vCACCAFELiteralMap): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	getExtensionData(): vCACCAFELiteralMap;
}

declare class vCACCAFEIpRange {
	constructor();
	constructor();
	getEnd(): string;
	getAddressSpaceId(): string;
	/**
	 * @param value 
	 */
	setAddressSpaceId(value: string): void;
	getProviderEndpointId(): string;
	/**
	 * @param value 
	 */
	setProviderEndpointId(value: string): void;
	getStart(): string;
	getProviderEndpointURI(): string;
	/**
	 * @param value 
	 */
	setProviderEndpointURI(value: string): void;
	getSubnetPrefixLength(): number;
	/**
	 * @param value 
	 */
	setSubnetPrefixLength(value: number): void;
	getIpVersion(): vCACCAFEIpVersionEnum;
	getExternalId(): string;
	/**
	 * @param value 
	 */
	setExternalId(value: string): void;
	getDnsInfo(): vCACCAFEDnsInfo;
	/**
	 * @param value 
	 */
	setStart(value: string): void;
	/**
	 * @param value 
	 */
	setEnd(value: string): void;
	/**
	 * @param value 
	 */
	setDnsInfo(value: vCACCAFEDnsInfo): void;
	/**
	 * @param value 
	 */
	setIpVersion(value: vCACCAFEIpVersionEnum): void;
	getGateway(): string;
	/**
	 * @param value 
	 */
	setGateway(value: string): void;
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	/**
	 * @param value 
	 */
	setExtensionData(value: vCACCAFELiteralMap): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	getExtensionData(): vCACCAFELiteralMap;
}

declare class vCACCAFEIpRangeCreateResult {
	constructor();
	constructor();
	getIpRangeCreateRequestId(): string;
	/**
	 * @param value 
	 */
	setIpRange(value: vCACCAFEIpRange): void;
	getIpRange(): vCACCAFEIpRange;
	/**
	 * @param value 
	 */
	setIpRangeCreateRequestId(value: string): void;
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	/**
	 * @param value 
	 */
	setExtensionData(value: vCACCAFELiteralMap): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	getExtensionData(): vCACCAFELiteralMap;
}

declare class vCACCAFEIpRangeDeleteRequest {
	constructor();
	constructor();
	getIpRangeId(): string;
	getExternalIpRangeId(): string;
	/**
	 * @param value 
	 */
	setExternalIpRangeId(value: string): void;
	/**
	 * @param value 
	 */
	setIpRangeId(value: string): void;
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	/**
	 * @param value 
	 */
	setExtensionData(value: vCACCAFELiteralMap): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	getExtensionData(): vCACCAFELiteralMap;
}

declare class vCACCAFEIpRangeDeleteResult {
	constructor();
	constructor();
	getIpRangeDeleteRequestId(): string;
	/**
	 * @param value 
	 */
	setIpRangeDeleteRequestId(value: string): void;
	getStatus(): vCACCAFERequestStateEnum;
	getErrorMessage(): string;
	getErrorCode(): string;
	/**
	 * @param value 
	 */
	setErrorCode(value: string): void;
	/**
	 * @param value 
	 */
	setErrorMessage(value: string): void;
	/**
	 * @param value 
	 */
	setStatus(value: vCACCAFERequestStateEnum): void;
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	/**
	 * @param value 
	 */
	setExtensionData(value: vCACCAFELiteralMap): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	getExtensionData(): vCACCAFELiteralMap;
}

declare class vCACCAFEIpRangeOperation {
	constructor();
	constructor();
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	/**
	 * @param value 
	 */
	setExtensionData(value: vCACCAFELiteralMap): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	getExtensionData(): vCACCAFELiteralMap;
}

declare class vCACCAFEIpRangeOperationRequest {
	constructor();
	constructor();
	getNetworkProfileId(): string;
	/**
	 * @param value 
	 */
	setNetworkProfileId(value: string): void;
	getIpRangeOperations(): vCACCAFEIpRangeOperation[];
	getResourceInfo(): vCACCAFEResourceInfo;
	/**
	 * @param value 
	 */
	setResourceInfo(value: vCACCAFEResourceInfo): void;
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	/**
	 * @param value 
	 */
	setExtensionData(value: vCACCAFELiteralMap): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	getExtensionData(): vCACCAFELiteralMap;
}

declare class vCACCAFEIpRangeOperationResult {
	constructor();
	constructor();
	getNetworkProfileId(): string;
	/**
	 * @param value 
	 */
	setNetworkProfileId(value: string): void;
	getIpRangeOperations(): vCACCAFEIpRangeOperation[];
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	/**
	 * @param value 
	 */
	setExtensionData(value: vCACCAFELiteralMap): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	getExtensionData(): vCACCAFELiteralMap;
}

declare class vCACCAFEIpRangesCreateRequest {
	constructor();
	constructor();
	getNetworkProfileId(): string;
	/**
	 * @param value 
	 */
	setNetworkProfileId(value: string): void;
	getBlueprintRequestId(): string;
	/**
	 * @param value 
	 */
	setBlueprintRequestId(value: string): void;
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	/**
	 * @param value 
	 */
	setExtensionData(value: vCACCAFELiteralMap): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	getExtensionData(): vCACCAFELiteralMap;
}

declare class vCACCAFEIpReleaseRequest {
	constructor();
	constructor();
	getAddress(): string;
	/**
	 * @param value 
	 */
	setRangeId(value: string): void;
	getNicIndex(): number;
	getRangeId(): string;
	/**
	 * @param value 
	 */
	setNicIndex(value: number): void;
	/**
	 * @param value 
	 */
	setAddress(value: string): void;
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	/**
	 * @param value 
	 */
	setExtensionData(value: vCACCAFELiteralMap): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	getExtensionData(): vCACCAFELiteralMap;
}

declare class vCACCAFEIpReleaseResult {
	constructor();
	constructor();
	getStatus(): vCACCAFERequestStateEnum;
	getErrorMessage(): string;
	getErrorCode(): string;
	getIpReleaseRequestId(): string;
	/**
	 * @param value 
	 */
	setIpReleaseRequestId(value: string): void;
	/**
	 * @param value 
	 */
	setErrorCode(value: string): void;
	/**
	 * @param value 
	 */
	setErrorMessage(value: string): void;
	/**
	 * @param value 
	 */
	setStatus(value: vCACCAFERequestStateEnum): void;
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	/**
	 * @param value 
	 */
	setExtensionData(value: vCACCAFELiteralMap): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	getExtensionData(): vCACCAFELiteralMap;
}

declare interface vCACCAFEIpVersionEnum {
	value(): string;
	values(): vCACCAFEIpVersionEnum[];
	/**
	 * @param name 
	 */
	valueOf(name: string): vCACCAFEIpVersionEnum;
	/**
	 * @param v 
	 */
	fromValue(v: string): vCACCAFEIpVersionEnum;
}

declare class vCACCAFEIpamNetworkProfile {
	constructor();
	constructor();
	getId(): string;
	getAddressSpaceId(): string;
	/**
	 * @param value 
	 */
	setAddressSpaceId(value: string): void;
	getExternalRangeIds(): string[];
	getNetworkProfileType(): vCACCAFEIpamNetworkProfileTypeEnum;
	/**
	 * @param value 
	 */
	setNetworkProfileType(value: vCACCAFEIpamNetworkProfileTypeEnum): void;
	getProviderEndpointId(): string;
	/**
	 * @param value 
	 */
	setProviderEndpointId(value: string): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
}

declare interface vCACCAFEIpamNetworkProfileTypeEnum {
	value(): string;
	values(): vCACCAFEIpamNetworkProfileTypeEnum[];
	/**
	 * @param name 
	 */
	valueOf(name: string): vCACCAFEIpamNetworkProfileTypeEnum;
	/**
	 * @param v 
	 */
	fromValue(v: string): vCACCAFEIpamNetworkProfileTypeEnum;
}

declare class vCACCAFEIpamRequest {
	constructor();
	constructor();
	getVersion(): number;
	getId(): string;
	getRequestType(): vCACCAFERequestTypeEnum;
	/**
	 * @param value 
	 */
	setVersion(value: number): void;
	getTenantId(): string;
	/**
	 * @param value 
	 */
	setRequestType(value: vCACCAFERequestTypeEnum): void;
	getLastUpdated(): any;
	/**
	 * @param value 
	 */
	setLastUpdated(value: any): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	getProviderCorrelationRequestId(): string;
	/**
	 * @param value 
	 */
	setProviderCorrelationRequestId(value: string): void;
	getOriginalCallbackServiceId(): string;
	/**
	 * @param value 
	 */
	setOriginalCallbackServiceId(value: string): void;
	getRequestInput(): any;
	/**
	 * @param value 
	 */
	setCreatedDate(value: any): void;
	/**
	 * @param value 
	 */
	setTenantId(value: string): void;
	/**
	 * @param value 
	 */
	setRequestInput(value: any): void;
	getCreatedDate(): any;
}

declare class vCACCAFEIpamRequestStatus {
	constructor();
	constructor();
	getProviderEndpointId(): string;
	/**
	 * @param value 
	 */
	setProviderEndpointId(value: string): void;
	/**
	 * @param value 
	 */
	setRequestPayload(value: any): void;
	getRequestState(): vCACCAFERequestStateEnum;
	/**
	 * @param value 
	 */
	setRequestState(value: vCACCAFERequestStateEnum): void;
	getRequestDetails(): string;
	/**
	 * @param value 
	 */
	setRequestDetails(value: string): void;
	getRequestPayload(): any;
	getVersion(): number;
	getId(): string;
	getRequestType(): vCACCAFERequestTypeEnum;
	/**
	 * @param value 
	 */
	setVersion(value: number): void;
	getTenantId(): string;
	/**
	 * @param value 
	 */
	setRequestType(value: vCACCAFERequestTypeEnum): void;
	getLastUpdated(): any;
	/**
	 * @param value 
	 */
	setLastUpdated(value: any): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	getProviderCorrelationRequestId(): string;
	/**
	 * @param value 
	 */
	setProviderCorrelationRequestId(value: string): void;
	getOriginalCallbackServiceId(): string;
	/**
	 * @param value 
	 */
	setOriginalCallbackServiceId(value: string): void;
	getRequestInput(): any;
	/**
	 * @param value 
	 */
	setCreatedDate(value: any): void;
	/**
	 * @param value 
	 */
	setTenantId(value: string): void;
	/**
	 * @param value 
	 */
	setRequestInput(value: any): void;
	getCreatedDate(): any;
}

declare class vCACCAFEIpamRoutedNetworkProfile {
	constructor();
	constructor();
	/**
	 * @param value 
	 */
	setSubnetMask(value: string): void;
	getExternalNetworkProfileId(): string;
	/**
	 * @param value 
	 */
	setExternalNetworkProfileId(value: string): void;
	getSubnetMask(): string;
	getId(): string;
	getAddressSpaceId(): string;
	/**
	 * @param value 
	 */
	setAddressSpaceId(value: string): void;
	getExternalRangeIds(): string[];
	getNetworkProfileType(): vCACCAFEIpamNetworkProfileTypeEnum;
	/**
	 * @param value 
	 */
	setNetworkProfileType(value: vCACCAFEIpamNetworkProfileTypeEnum): void;
	getProviderEndpointId(): string;
	/**
	 * @param value 
	 */
	setProviderEndpointId(value: string): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
}

declare class vCACCAFEIpamnatNetworkProfile {
	constructor();
	constructor();
	getId(): string;
	getAddressSpaceId(): string;
	/**
	 * @param value 
	 */
	setAddressSpaceId(value: string): void;
	getExternalRangeIds(): string[];
	getNetworkProfileType(): vCACCAFEIpamNetworkProfileTypeEnum;
	/**
	 * @param value 
	 */
	setNetworkProfileType(value: vCACCAFEIpamNetworkProfileTypeEnum): void;
	getProviderEndpointId(): string;
	/**
	 * @param value 
	 */
	setProviderEndpointId(value: string): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
}

declare interface vCACCAFEIsDefinedOperator {
	getId(): string;
	/**
	 * @param context 
	 * @param leftOperand 
	 * @param rightOperand 
	 */
	evaluate(context: any, leftOperand: any, rightOperand: any): any;
	getDataTypeConstraint(): any;
	getCategory(): any;
	getMultiplicityConstraint(): any;
}

declare class vCACCAFEKeyAnyValue {
	constructor();
	constructor();
	getValue(): string;
	getKey(): string;
	/**
	 * @param value 
	 */
	setValue(value: string): void;
	/**
	 * @param value 
	 */
	setKey(value: string): void;
}

declare class vCACCAFEKeyValuePair {
	constructor();
	/**
	 * @param key 
	 * @param value 
	 */
	constructor(key: java.io.Serializable, value: java.io.Serializable);
	/**
	 * @param key 
	 */
	constructor(key: java.io.Serializable);
	constructor();
	getValue(): any;
	getKey(): any;
	/**
	 * @param value 
	 */
	setValue(value: any): void;
}

declare class vCACCAFELabelledReference {
	constructor();
	/**
	 * @param id 
	 * @param label 
	 */
	constructor(id: string, label: string);
	/**
	 * @param id 
	 */
	constructor(id: string);
	constructor();
	getId(): string;
	/**
	 * @param label 
	 */
	setLabel(label: string): void;
	getLabel(): string;
	/**
	 * @param id 
	 */
	setId(id: string): void;
}

declare class vCACCAFELayout {
	constructor();
	constructor();
	getFields(): any;
	/**
	 * @param fieldId 
	 */
	getField(fieldId: string): any;
	getPages(): vCACCAFELayoutPage[];
	/**
	 * @param pages 
	 */
	setPages(pages: vCACCAFELayoutPage[]): void;
	/**
	 * @param other 
	 */
	equalsForAttributes(other: vCACCAFELayout): boolean;
}

declare class vCACCAFELayoutExtension {
	constructor();
	constructor();
	/**
	 * @param fieldPrefix 
	 */
	setFieldPrefix(fieldPrefix: string): void;
	getFieldPrefix(): string;
	getFields(): any;
	getId(): string;
	getState(): vCACCAFEElementState;
	/**
	 * @param state 
	 */
	setState(state: vCACCAFEElementState): void;
	getExtensionId(): string;
	getExtensionPointId(): string;
	/**
	 * @param extensionPointId 
	 */
	setExtensionPointId(extensionPointId: string): void;
	/**
	 * @param extensionId 
	 */
	setExtensionId(extensionId: string): void;
	/**
	 * @param id 
	 */
	setId(id: string): void;
	getSize(): number;
	/**
	 * @param size 
	 */
	setSize(size: number): void;
	/**
	 * @param other 
	 */
	equalsForAttributes(other: any): boolean;
}

declare class vCACCAFELayoutField {
	constructor();
	constructor();
	getExtensionRendererContext(): vCACCAFEExtensionRendererContext;
	/**
	 * @param extensionRendererContext 
	 */
	setExtensionRendererContext(extensionRendererContext: vCACCAFEExtensionRendererContext): void;
	getLabelDisplaySize(): number;
	/**
	 * @param size 
	 */
	setLabelDisplaySize(size: number): void;
	/**
	 * @param isMultiValued 
	 */
	setMultiValued(isMultiValued: boolean): void;
	getColumns(): vCACCAFELayoutField[];
	/**
	 * @param columns 
	 */
	setColumns(columns: vCACCAFELayoutField[]): void;
	getDetailLayout(): any;
	/**
	 * @param detailLayout 
	 */
	setDetailLayout(detailLayout: any): void;
	getFields(): any;
	getId(): string;
	getState(): vCACCAFEElementState;
	/**
	 * @param description 
	 */
	setDescription(description: string): void;
	getDescription(): string;
	/**
	 * @param state 
	 */
	setState(state: vCACCAFEElementState): void;
	/**
	 * @param label 
	 */
	setLabel(label: string): void;
	getDataType(): any;
	getDisplayAdvice(): any;
	/**
	 * @param displayAdvice 
	 */
	setDisplayAdvice(displayAdvice: any): void;
	getPermissibleValues(): any;
	/**
	 * @param permissibleValues 
	 */
	setPermissibleValues(permissibleValues: any): void;
	getOrderIndex(): number;
	/**
	 * @param orderIndex 
	 */
	setOrderIndex(orderIndex: number): void;
	/**
	 * @param dataType 
	 */
	setDataType(dataType: any): void;
	isMultiValued(): boolean;
	getLabel(): string;
	/**
	 * @param id 
	 */
	setId(id: string): void;
	getSize(): number;
	/**
	 * @param size 
	 */
	setSize(size: number): void;
	/**
	 * @param other 
	 */
	equalsForAttributes(other: any): boolean;
}

declare class vCACCAFELayoutFlow {
	constructor();
	constructor();
	getParts(): any[];
	/**
	 * @param parts 
	 */
	setParts(parts: any[]): void;
	getFields(): any;
	getId(): string;
	getState(): vCACCAFEElementState;
	/**
	 * @param state 
	 */
	setState(state: vCACCAFEElementState): void;
	/**
	 * @param id 
	 */
	setId(id: string): void;
	getSize(): number;
	/**
	 * @param size 
	 */
	setSize(size: number): void;
	/**
	 * @param other 
	 */
	equalsForAttributes(other: any): boolean;
}

declare class vCACCAFELayoutPage {
	constructor();
	constructor();
	/**
	 * @param sections 
	 */
	setSections(sections: vCACCAFELayoutSection[]): void;
	getSections(): vCACCAFELayoutSection[];
	getFields(): any;
	getId(): string;
	getState(): vCACCAFEElementState;
	/**
	 * @param state 
	 */
	setState(state: vCACCAFEElementState): void;
	/**
	 * @param label 
	 */
	setLabel(label: string): void;
	/**
	 * @param other 
	 */
	equalsForAttributes(other: vCACCAFELayoutPage): boolean;
	getLabel(): string;
	/**
	 * @param id 
	 */
	setId(id: string): void;
}

declare class vCACCAFELayoutPlaceholderCell {
	constructor();
	constructor();
	getFields(): any;
	getSize(): number;
	/**
	 * @param size 
	 */
	setSize(size: number): void;
	/**
	 * @param other 
	 */
	equalsForAttributes(other: any): boolean;
}

declare class vCACCAFELayoutRow {
	constructor();
	constructor();
	/**
	 * @param items 
	 */
	setItems(items: any[]): void;
	getFields(): any;
	/**
	 * @param other 
	 */
	equalsForAttributes(other: vCACCAFELayoutRow): boolean;
	getItems(): any[];
}

declare class vCACCAFELayoutScenarioImpl {
	constructor();
	constructor();
	getId(): string;
	getUsageClass(): string;
	/**
	 * @param value 
	 */
	setUsageClass(value: string): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
}

declare class vCACCAFELayoutSection {
	constructor();
	constructor();
	getFields(): any;
	getId(): string;
	getState(): vCACCAFEElementState;
	/**
	 * @param state 
	 */
	setState(state: vCACCAFEElementState): void;
	/**
	 * @param label 
	 */
	setLabel(label: string): void;
	getRows(): vCACCAFELayoutRow[];
	/**
	 * @param other 
	 */
	equalsForAttributes(other: vCACCAFELayoutSection): boolean;
	/**
	 * @param rows 
	 */
	setRows(rows: vCACCAFELayoutRow[]): void;
	getLabel(): string;
	/**
	 * @param id 
	 */
	setId(id: string): void;
}

declare class vCACCAFELayoutText {
	constructor();
	constructor();
	getValue(): string;
	getFields(): any;
	getId(): string;
	getState(): vCACCAFEElementState;
	/**
	 * @param value 
	 */
	setValue(value: string): void;
	/**
	 * @param state 
	 */
	setState(state: vCACCAFEElementState): void;
	/**
	 * @param id 
	 */
	setId(id: string): void;
	getSize(): number;
	/**
	 * @param size 
	 */
	setSize(size: number): void;
	/**
	 * @param other 
	 */
	equalsForAttributes(other: any): boolean;
}

declare interface vCACCAFELessThanOperator {
	getId(): string;
	/**
	 * @param left 
	 * @param right 
	 */
	evaluate(left: any, right: any): vCACCAFEBooleanLiteral;
	getDataTypeConstraint(): any;
	getCategory(): any;
	getMultiplicityConstraint(): any;
}

declare interface vCACCAFELessThanOrEqualsOperator {
	getId(): string;
	/**
	 * @param left 
	 * @param right 
	 */
	evaluate(left: any, right: any): vCACCAFEBooleanLiteral;
	getDataTypeConstraint(): any;
	getCategory(): any;
	getMultiplicityConstraint(): any;
}

declare class vCACCAFELicense {
	constructor();
	constructor();
	getId(): string;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	getLicenseInfo(): vCACCAFELicenseInfo;
	/**
	 * @param value 
	 */
	setLicenseInfo(value: vCACCAFELicenseInfo): void;
	getAssetId(): string;
	/**
	 * @param value 
	 */
	setAssetId(value: string): void;
}

declare class vCACCAFELicenseInfo {
	constructor();
	constructor();
	getExpiration(): any;
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getRestrictions(): vCACCAFELicenseProductRestriction[];
	/**
	 * @param value 
	 */
	setExpiration(value: any): void;
}

declare class vCACCAFELicenseProductCapability {
	constructor();
	constructor();
	getVersion(): string;
	/**
	 * @param value 
	 */
	setVersion(value: string): void;
	getFeatures(): vCACCAFEFeature[];
	getKeyValues(): vCACCAFEKeyAnyValue[];
}

declare class vCACCAFELicenseProductRestriction {
	constructor();
	constructor();
	getProduct(): vCACCAFEProduct;
	/**
	 * @param value 
	 */
	setProduct(value: vCACCAFEProduct): void;
	getLicenseProductCapabilities(): vCACCAFELicenseProductCapability[];
	getCostUnitLimits(): vCACCAFECostUnitLimit[];
}

declare class vCACCAFELink {
	constructor();
	constructor();
	/**
	 * @param href 
	 * @param rel 
	 */
	constructor(href: string, rel: string);
	/**
	 * @param href 
	 */
	constructor(href: string);
	getHref(): string;
	getRel(): string;
}

declare class vCACCAFELinkResolveRequest {
	constructor();
	constructor();
	getLinks(): vCACCAFEPath[];
}

declare class vCACCAFELiteralMap {
	constructor();
	/**
	 * @param capacity 
	 */
	constructor(capacity: number);
	constructor();
	/**
	 * @param source 
	 */
	mergeFrom(source: vCACCAFELiteralMap): void;
	/**
	 * @param key 
	 */
	containsKeyIgnorecase(key: any): boolean;
	emptyLiteralMap(): vCACCAFELiteralMap;
	asMap(): any;
	/**
	 * @param key 
	 */
	remove(key: any): any;
	/**
	 * @param key 
	 */
	get(key: any): any;
	/**
	 * @param key 
	 * @param value 
	 */
	put(key: string, value: any): any;
	values(): any[];
	clear(): void;
	isEmpty(): boolean;
	size(): number;
	/**
	 * @param m 
	 */
	putAll(m: any): void;
	keySet(): string[];
	/**
	 * @param value 
	 */
	containsValue(value: any): boolean;
	/**
	 * @param key 
	 */
	containsKey(key: any): boolean;
}

declare class vCACCAFELoadBalancer {
	constructor();
	constructor();
	/**
	 * @param value 
	 */
	setLoggingLevel(value: string): void;
	getLoggingLevel(): string;
	getPools(): vCACCAFELoadBalancerPool[];
	getMonitors(): vCACCAFELoadBalancerMonitor[];
	getVirtualIps(): vCACCAFELoadBalancerVirtualIp[];
	getVipNetwork(): string;
	/**
	 * @param value 
	 */
	setVipNetwork(value: string): void;
	getVipNicIndex(): number;
	/**
	 * @param value 
	 */
	setVipNicIndex(value: number): void;
	getTierNetwork(): string;
	/**
	 * @param value 
	 */
	setTierNetwork(value: string): void;
	getTierNicIndex(): number;
	/**
	 * @param value 
	 */
	setTierNicIndex(value: number): void;
	getLoadBalancerTypeId(): string;
	/**
	 * @param value 
	 */
	setLoadBalancerTypeId(value: string): void;
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	/**
	 * @param value 
	 */
	setExtensionData(value: vCACCAFELiteralMap): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	getExtensionData(): vCACCAFELiteralMap;
	getExternalId(): string;
	/**
	 * @param value 
	 */
	setExternalId(value: string): void;
}

declare class vCACCAFELoadBalancerMember {
	constructor();
	constructor();
	getAddress(): string;
	getId(): string;
	getPort(): number;
	/**
	 * @param value 
	 */
	setExtensionData(value: vCACCAFELiteralMap): void;
	/**
	 * @param value 
	 */
	setAddress(value: string): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	/**
	 * @param value 
	 */
	setPort(value: number): void;
	getExtensionData(): vCACCAFELiteralMap;
}

declare class vCACCAFELoadBalancerMonitor {
	constructor();
	constructor();
	/**
	 * @param value 
	 */
	setMethod(value: string): void;
	getMethod(): string;
	getId(): string;
	getInterval(): number;
	/**
	 * @param value 
	 */
	setInterval(value: number): void;
	getMaxRetries(): number;
	/**
	 * @param value 
	 */
	setMaxRetries(value: number): void;
	getMonitorPath(): string;
	/**
	 * @param value 
	 */
	setMonitorPath(value: string): void;
	getSend(): string;
	/**
	 * @param value 
	 */
	setSend(value: string): void;
	getReceive(): string;
	/**
	 * @param value 
	 */
	setReceive(value: string): void;
	isNewEntity(): boolean;
	/**
	 * @param value 
	 */
	setNewEntity(value: boolean): void;
	/**
	 * @param value 
	 */
	setExtensionData(value: vCACCAFELiteralMap): void;
	/**
	 * @param value 
	 */
	setTimeout(value: number): void;
	getTimeout(): number;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	getExtensionData(): vCACCAFELiteralMap;
}

declare class vCACCAFELoadBalancerPool {
	constructor();
	constructor();
	getAlgorithm(): string;
	getMembers(): vCACCAFELoadBalancerMember[];
	getProtocol(): string;
	getPort(): number;
	getConnectionLimit(): number;
	/**
	 * @param value 
	 */
	setConnectionLimit(value: number): void;
	getConnectionRate(): number;
	/**
	 * @param value 
	 */
	setConnectionRate(value: number): void;
	getHealthCheckProtocol(): string;
	/**
	 * @param value 
	 */
	setHealthCheckProtocol(value: string): void;
	getMemberPort(): number;
	/**
	 * @param value 
	 */
	setMemberPort(value: number): void;
	getMonitorPort(): number;
	/**
	 * @param value 
	 */
	setMonitorPort(value: number): void;
	getMemberMaxConnections(): number;
	/**
	 * @param value 
	 */
	setMemberMaxConnections(value: number): void;
	getMemberMinConnections(): number;
	/**
	 * @param value 
	 */
	setMemberMinConnections(value: number): void;
	isAcceleration(): boolean;
	/**
	 * @param value 
	 */
	setAcceleration(value: boolean): void;
	isTransparent(): boolean;
	/**
	 * @param value 
	 */
	setTransparent(value: boolean): void;
	getAlgorithmParameter(): string;
	/**
	 * @param value 
	 */
	setAlgorithmParameter(value: string): void;
	getPersistenceMethod(): string;
	/**
	 * @param value 
	 */
	setPersistenceMethod(value: string): void;
	getPersistenceCookieName(): string;
	/**
	 * @param value 
	 */
	setPersistenceCookieName(value: string): void;
	getPersistenceCookieMode(): string;
	/**
	 * @param value 
	 */
	setPersistenceCookieMode(value: string): void;
	getPersistenceExpiration(): number;
	/**
	 * @param value 
	 */
	setPersistenceExpiration(value: number): void;
	getMonitorIds(): string[];
	getUniqueMonitorId(): string;
	/**
	 * @param value 
	 */
	setUniqueMonitorId(value: string): void;
	getServicePortId(): string;
	/**
	 * @param value 
	 */
	setServicePortId(value: string): void;
	/**
	 * @param value 
	 */
	setAlgorithm(value: string): void;
	/**
	 * @param value 
	 */
	setProtocol(value: string): void;
	/**
	 * @param value 
	 */
	setPort(value: number): void;
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	/**
	 * @param value 
	 */
	setExtensionData(value: vCACCAFELiteralMap): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	getExtensionData(): vCACCAFELiteralMap;
	getExternalId(): string;
	/**
	 * @param value 
	 */
	setExternalId(value: string): void;
}

declare class vCACCAFELoadBalancerType {
	constructor();
	constructor();
	getVersion(): number;
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	/**
	 * @param value 
	 */
	setVersion(value: number): void;
	getAllocationRelatedResourceTypes(): string[];
	getFormReference(): any;
	/**
	 * @param value 
	 */
	setFormReference(value: any): void;
	/**
	 * @param value 
	 */
	setServiceTypeId(value: string): void;
	getLastUpdated(): any;
	/**
	 * @param value 
	 */
	setLastUpdated(value: any): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	getSchema(): vCACCAFENetworkObjectSchema[];
	getServiceTypeId(): string;
	/**
	 * @param value 
	 */
	setCreatedDate(value: any): void;
	getCreatedDate(): any;
}

declare class vCACCAFELoadBalancerVirtualIp {
	constructor();
	constructor();
	getAddress(): string;
	getPoolId(): string;
	/**
	 * @param value 
	 */
	setPoolId(value: string): void;
	/**
	 * @param value 
	 */
	setAddress(value: string): void;
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	/**
	 * @param value 
	 */
	setExtensionData(value: vCACCAFELiteralMap): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	getExtensionData(): vCACCAFELiteralMap;
	getExternalId(): string;
	/**
	 * @param value 
	 */
	setExternalId(value: string): void;
}

declare class vCACCAFELocalizableString {
	constructor();
	/**
	 * @param messageKey 
	 */
	constructor(messageKey: string);
	/**
	 * @param messagesMap 
	 */
	constructor(messagesMap: java.util.Map);
	getMessageKey(): string;
	/**
	 * @param messageKey 
	 */
	setMessageKey(messageKey: string): void;
}

declare interface vCACCAFELogLevel {
	value(): string;
	values(): vCACCAFELogLevel[];
	/**
	 * @param name 
	 */
	valueOf(name: string): vCACCAFELogLevel;
	/**
	 * @param v 
	 */
	fromValue(v: string): vCACCAFELogLevel;
}

declare class vCACCAFELoggerEntity {
	constructor();
	constructor();
	/**
	 * @param loggingLevel 
	 */
	setLoggingLevel(loggingLevel: vCACCAFELoggerLevel): void;
	getLoggingLevel(): vCACCAFELoggerLevel;
	/**
	 * @param category 
	 */
	setCategory(category: string): void;
	getCategory(): string;
}

declare interface vCACCAFELoggerLevel {
}

declare class vCACCAFEMachinePrefix {
	name: string;
	id: any;
	prefix: string;
	href: string;
	numberOfDigits: number;
	vcoId: any;
	nextNumber: number;
	constructor();
	constructor();
	getId(): any;
	getPrefix(): string;
	getHref(): string;
	getNumberOfDigits(): number;
	getNextNumber(): number;
}

declare interface vCACCAFEMachinePrefixesService {
	/**
	 * @param id 
	 */
	get(id: any): vCACCAFEMachinePrefix;
	getAll(): vCACCAFEMachinePrefix[];
}

declare class vCACCAFEMandatoryConstraint {
	constructor();
	/**
	 * @param isMandatory 
	 */
	constructor(isMandatory: boolean);
	/**
	 * @param value 
	 */
	constructor(value: com.vmware.vcac.platform.content.evaluators.Evaluator);
	/**
	 * @param value 
	 */
	cloneWithValue(value: any): vCACCAFEMandatoryConstraint;
	/**
	 * @param isMandatory 
	 */
	fromBoolean(isMandatory: boolean): vCACCAFEMandatoryConstraint;
	/**
	 * @param constraint 
	 */
	mergeConstraint(constraint: any): any;
	clone(): any;
	getValue(): any;
}

declare class vCACCAFEMaxCardinalityConstraint {
	constructor();
	/**
	 * @param value 
	 */
	constructor(value: number);
	/**
	 * @param value 
	 */
	constructor(value: com.vmware.vcac.platform.content.evaluators.Evaluator);
	/**
	 * @param value 
	 */
	cloneWithValue(value: any): any;
	/**
	 * @param constraint 
	 */
	mergeConstraint(constraint: any): vCACCAFEMaxCardinalityConstraint;
	clone(): any;
	getValue(): any;
}

declare class vCACCAFEMaxLengthConstraint {
	constructor();
	/**
	 * @param value 
	 */
	constructor(value: com.vmware.vcac.platform.content.evaluators.Evaluator);
	/**
	 * @param length 
	 */
	constructor(length: number);
	/**
	 * @param value 
	 */
	cloneWithValue(value: any): vCACCAFEMaxLengthConstraint;
	/**
	 * @param constraint 
	 */
	mergeConstraint(constraint: any): vCACCAFEMaxLengthConstraint;
	clone(): any;
	getValue(): any;
}

declare class vCACCAFEMaxValueConstraint {
	constructor();
	/**
	 * @param value 
	 */
	constructor(value: com.vmware.vcac.platform.content.literals.Literal);
	/**
	 * @param value 
	 */
	constructor(value: com.vmware.vcac.platform.content.evaluators.Evaluator);
	/**
	 * @param value 
	 */
	cloneWithValue(value: any): vCACCAFEMaxValueConstraint;
	/**
	 * @param constraint 
	 */
	mergeConstraint(constraint: any): vCACCAFEMaxValueConstraint;
	clone(): any;
	getValue(): any;
}

declare class vCACCAFEMaximumValueEvaluator {
	constructor();
	/**
	 * @param arguments 
	 */
	constructor(arguments: com.vmware.vcac.platform.content.evaluators.Evaluator[]);
	/**
	 * @param arguments 
	 */
	constructor(arguments: com.vmware.vcac.platform.content.evaluators.Evaluator[]);
	getConstantValue(): any;
	isConstant(): boolean;
	/**
	 * @param bVal 
	 */
	fromBoolean(bVal: boolean): any;
	/**
	 * @param sVal 
	 */
	fromString(sVal: string): any;
	/**
	 * @param lVal 
	 */
	fromInt(lVal: number): any;
	/**
	 * @param dVal 
	 */
	fromDecimal(dVal: number): any;
}

declare interface vCACCAFEMemberType {
	value(): string;
	values(): vCACCAFEMemberType[];
	/**
	 * @param name 
	 */
	valueOf(name: string): vCACCAFEMemberType;
	/**
	 * @param v 
	 */
	fromValue(v: string): vCACCAFEMemberType;
}

declare class vCACCAFEMembershipContext {
	constructor();
	constructor();
	getPrincipals(): vCACCAFEPrincipalId[];
	getSubtenants(): vCACCAFESimpleSubtenant[];
	getTenant(): string;
	/**
	 * @param tenant 
	 */
	setTenant(tenant: string): void;
	getCurrentUserPrincipal(): vCACCAFEPrincipalId;
	/**
	 * @param currentUserPrincipal 
	 */
	setCurrentUserPrincipal(currentUserPrincipal: vCACCAFEPrincipalId): void;
	/**
	 * @param principals 
	 */
	setPrincipals(principals: vCACCAFEPrincipalId[]): void;
	/**
	 * @param subtenants 
	 */
	setSubtenants(subtenants: vCACCAFESimpleSubtenant[]): void;
}

declare class vCACCAFEMembershipRequest {
	constructor();
	constructor();
	getPrincipals(): vCACCAFEPrincipalId[];
	/**
	 * @param principals 
	 */
	setPrincipals(principals: vCACCAFEPrincipalId[]): void;
	getCurrentUserId(): vCACCAFEPrincipalId;
	/**
	 * @param currentUserId 
	 */
	setCurrentUserId(currentUserId: vCACCAFEPrincipalId): void;
}

declare class vCACCAFEMetaData {
	constructor();
	constructor();
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	getDependencies(): string[];
	/**
	 * @param value 
	 */
	setLocator(value: string): void;
	getLocator(): string;
}

declare class vCACCAFEMetaDataInfo {
	constructor();
	constructor();
	getData(): vCACCAFEMetaData[];
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getExportTime(): any;
	/**
	 * @param value 
	 */
	setExportTime(value: any): void;
	getProductVersion(): string;
	/**
	 * @param value 
	 */
	setProductVersion(value: string): void;
}

declare class vCACCAFEMinCardinalityConstraint {
	constructor();
	/**
	 * @param value 
	 */
	constructor(value: number);
	/**
	 * @param value 
	 */
	constructor(value: com.vmware.vcac.platform.content.evaluators.Evaluator);
	/**
	 * @param value 
	 */
	cloneWithValue(value: any): any;
	/**
	 * @param constraint 
	 */
	mergeConstraint(constraint: any): vCACCAFEMinCardinalityConstraint;
	clone(): any;
	getValue(): any;
}

declare class vCACCAFEMinLengthConstraint {
	constructor();
	/**
	 * @param value 
	 */
	constructor(value: com.vmware.vcac.platform.content.evaluators.Evaluator);
	/**
	 * @param length 
	 */
	constructor(length: number);
	/**
	 * @param value 
	 */
	cloneWithValue(value: any): vCACCAFEMinLengthConstraint;
	/**
	 * @param constraint 
	 */
	mergeConstraint(constraint: any): vCACCAFEMinLengthConstraint;
	clone(): any;
	getValue(): any;
}

declare class vCACCAFEMinValueConstraint {
	constructor();
	/**
	 * @param value 
	 */
	constructor(value: com.vmware.vcac.platform.content.literals.Literal);
	/**
	 * @param value 
	 */
	constructor(value: com.vmware.vcac.platform.content.evaluators.Evaluator);
	/**
	 * @param value 
	 */
	cloneWithValue(value: any): vCACCAFEMinValueConstraint;
	/**
	 * @param constraint 
	 */
	mergeConstraint(constraint: any): vCACCAFEMinValueConstraint;
	clone(): any;
	getValue(): any;
}

declare class vCACCAFEMinimumValueEvaluator {
	constructor();
	/**
	 * @param arguments 
	 */
	constructor(arguments: com.vmware.vcac.platform.content.evaluators.Evaluator[]);
	/**
	 * @param arguments 
	 */
	constructor(arguments: com.vmware.vcac.platform.content.evaluators.Evaluator[]);
	getConstantValue(): any;
	isConstant(): boolean;
	/**
	 * @param bVal 
	 */
	fromBoolean(bVal: boolean): any;
	/**
	 * @param sVal 
	 */
	fromString(sVal: string): any;
	/**
	 * @param lVal 
	 */
	fromInt(lVal: number): any;
	/**
	 * @param dVal 
	 */
	fromDecimal(dVal: number): any;
}

declare class vCACCAFEMoney {
	constructor();
	/**
	 * @param amount 
	 * @param currencyCode 
	 */
	constructor(amount: number, currencyCode: string);
	/**
	 * @param amount 
	 */
	constructor(amount: number);
	getValue(): vCACCAFEMoney;
	isInfinite(): boolean;
	/**
	 * @param multiplier 
	 */
	multiply(multiplier: any): vCACCAFEMoney;
	getAmount(): number;
	getTypeId(): vCACCAFEDataTypeId;
	getCurrencyCode(): string;
	isList(): boolean;
	/**
	 * @param other 
	 */
	compareTo(other: any): number;
	/**
	 * @param bVal 
	 */
	fromBoolean(bVal: boolean): any;
	/**
	 * @param obj 
	 */
	safeEquals(obj: any): boolean;
	/**
	 * @param sVal 
	 */
	fromString(sVal: string): any;
	/**
	 * @param lVal 
	 */
	fromInt(lVal: number): any;
	/**
	 * @param dVal 
	 */
	fromDecimal(dVal: number): any;
}

declare class vCACCAFEMoneyTimeRate {
	constructor();
	/**
	 * @param cost 
	 * @param basis 
	 */
	constructor(cost: com.vmware.vcac.platform.content.literals.MonetaryValue, basis: vCACCAFETimeSpan);
	getValue(): vCACCAFEMoneyTimeRate;
	isInfinite(): boolean;
	getBasis(): vCACCAFETimeSpan;
	/**
	 * @param period 
	 */
	calculateTotalForPeriodToNearestMillisecond(period: vCACCAFETimeSpan): any;
	/**
	 * @param period 
	 */
	calculateTotalForPeriodToNearestCycle(period: vCACCAFETimeSpan): any;
	getTypeId(): vCACCAFEDataTypeId;
	isList(): boolean;
	/**
	 * @param other 
	 */
	compareTo(other: any): number;
	getCost(): any;
	/**
	 * @param bVal 
	 */
	fromBoolean(bVal: boolean): any;
	/**
	 * @param obj 
	 */
	safeEquals(obj: any): boolean;
	/**
	 * @param sVal 
	 */
	fromString(sVal: string): any;
	/**
	 * @param lVal 
	 */
	fromInt(lVal: number): any;
	/**
	 * @param dVal 
	 */
	fromDecimal(dVal: number): any;
}

declare class vCACCAFEMultipleEvaluator {
	constructor();
	/**
	 * @param values 
	 */
	constructor(values: com.vmware.vcac.platform.content.evaluators.Evaluator[]);
	/**
	 * @param elementTypeId 
	 * @param evaluatorList 
	 */
	constructor(elementTypeId: vCACCAFEDataTypeId, evaluatorList: com.vmware.vcac.platform.content.evaluators.Evaluator[]);
	/**
	 * @param evaluatorList 
	 */
	constructor(evaluatorList: com.vmware.vcac.platform.content.evaluators.Evaluator[]);
	getElementTypeId(): vCACCAFEDataTypeId;
	getConstantValue(): any;
	isConstant(): boolean;
	getItems(): any[];
	/**
	 * @param bVal 
	 */
	fromBoolean(bVal: boolean): any;
	/**
	 * @param sVal 
	 */
	fromString(sVal: string): any;
	/**
	 * @param lVal 
	 */
	fromInt(lVal: number): any;
	/**
	 * @param dVal 
	 */
	fromDecimal(dVal: number): any;
}

declare class vCACCAFEMultipleLiteral {
	constructor();
	/**
	 * @param dataTypeId 
	 */
	constructor(dataTypeId: vCACCAFEDataTypeId);
	/**
	 * @param value 
	 * @param dataTypeId 
	 */
	constructor(value: com.vmware.vcac.platform.content.literals.Literal[], dataTypeId: vCACCAFEDataTypeId);
	isList(): boolean;
	/**
	 * @param o 
	 */
	compareTo(o: any): number;
	isEmpty(): boolean;
	getValue(): any[];
	getTypeId(): vCACCAFEDataTypeId;
	/**
	 * @param bVal 
	 */
	fromBoolean(bVal: boolean): any;
	/**
	 * @param obj 
	 */
	safeEquals(obj: any): boolean;
	/**
	 * @param sVal 
	 */
	fromString(sVal: string): any;
	/**
	 * @param lVal 
	 */
	fromInt(lVal: number): any;
	/**
	 * @param dVal 
	 */
	fromDecimal(dVal: number): any;
}

declare class vCACCAFENatRule {
	constructor();
	constructor();
	isEnabled(): boolean;
	getOrder(): number;
	getAction(): string;
	getComponentName(): string;
	/**
	 * @param value 
	 */
	setOrder(value: number): void;
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	getType(): string;
	getProtocol(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	/**
	 * @param value 
	 */
	setType(value: string): void;
	getDescription(): string;
	/**
	 * @param value 
	 */
	setAction(value: string): void;
	getOriginalAddress(): string;
	/**
	 * @param value 
	 */
	setOriginalAddress(value: string): void;
	getTranslatedAddress(): string;
	/**
	 * @param value 
	 */
	setTranslatedAddress(value: string): void;
	getOriginalPort(): string;
	/**
	 * @param value 
	 */
	setOriginalPort(value: string): void;
	getTranslatedPort(): string;
	/**
	 * @param value 
	 */
	setTranslatedPort(value: string): void;
	getInterfaceIndex(): number;
	/**
	 * @param value 
	 */
	setInterfaceIndex(value: number): void;
	getPeerInterfaceIndex(): number;
	/**
	 * @param value 
	 */
	setPeerInterfaceIndex(value: number): void;
	/**
	 * @param value 
	 */
	setComponentName(value: string): void;
	isUserDefined(): boolean;
	/**
	 * @param value 
	 */
	setUserDefined(value: boolean): void;
	getNetworkInterfaceId(): string;
	getEdgeId(): string;
	/**
	 * @param value 
	 */
	setTargetType(value: string): void;
	/**
	 * @param value 
	 */
	setNetworkInterfaceId(value: string): void;
	/**
	 * @param value 
	 */
	setEdgeId(value: string): void;
	getComponentId(): string;
	/**
	 * @param value 
	 */
	setComponentId(value: string): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	/**
	 * @param value 
	 */
	setTag(value: string): void;
	/**
	 * @param value 
	 */
	setEnabled(value: boolean): void;
	getTag(): string;
	/**
	 * @param value 
	 */
	setProtocol(value: string): void;
	getMachineId(): string;
	/**
	 * @param value 
	 */
	setMachineId(value: string): void;
	getTargetType(): string;
	getExternalId(): string;
	/**
	 * @param value 
	 */
	setExternalId(value: string): void;
}

declare class vCACCAFENetwork {
	constructor();
	constructor();
	getMachineIdCollection(): string[];
	getNetworkBackings(): vCACCAFENetworkBacking[];
	getNatRules(): vCACCAFENatRule[];
	getNetworkTypeId(): string;
	/**
	 * @param value 
	 */
	setNetworkTypeId(value: string): void;
	getNetworkProfileType(): vCACCAFENetworkProfileTypeEnum;
	/**
	 * @param value 
	 */
	setNetworkProfileType(value: vCACCAFENetworkProfileTypeEnum): void;
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	/**
	 * @param value 
	 */
	setExtensionData(value: vCACCAFELiteralMap): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	getExtensionData(): vCACCAFELiteralMap;
	getExternalId(): string;
	/**
	 * @param value 
	 */
	setExternalId(value: string): void;
}

declare class vCACCAFENetworkBacking {
	constructor();
	constructor();
	getId(): string;
	getComputeResourceNetworkId(): string;
	/**
	 * @param value 
	 */
	setComputeResourceNetworkId(value: string): void;
	getComputeResourceName(): string;
	/**
	 * @param value 
	 */
	setComputeResourceName(value: string): void;
	getComputeResourceId(): string;
	/**
	 * @param value 
	 */
	setComputeResourceId(value: string): void;
	/**
	 * @param value 
	 */
	setExtensionData(value: vCACCAFELiteralMap): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	getExtensionData(): vCACCAFELiteralMap;
	getExternalId(): string;
	/**
	 * @param value 
	 */
	setExternalId(value: string): void;
}

declare class vCACCAFENetworkObject {
	constructor();
	constructor();
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	/**
	 * @param value 
	 */
	setExtensionData(value: vCACCAFELiteralMap): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	getExtensionData(): vCACCAFELiteralMap;
	getExternalId(): string;
	/**
	 * @param value 
	 */
	setExternalId(value: string): void;
}

declare class vCACCAFENetworkObjectSchema {
	constructor();
	constructor();
	getVersion(): number;
	getId(): string;
	/**
	 * @param value 
	 */
	setVersion(value: number): void;
	getSchemaClassId(): string;
	/**
	 * @param value 
	 */
	setSchemaClassId(value: string): void;
	getLastUpdated(): any;
	/**
	 * @param value 
	 */
	setLastUpdated(value: any): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	getSchema(): vCACCAFESchema;
	/**
	 * @param value 
	 */
	setSchema(value: vCACCAFESchema): void;
	/**
	 * @param value 
	 */
	setCreatedDate(value: any): void;
	getCreatedDate(): any;
}

declare class vCACCAFENetworkObjectType {
	constructor();
	constructor();
	getVersion(): number;
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	/**
	 * @param value 
	 */
	setVersion(value: number): void;
	getAllocationRelatedResourceTypes(): string[];
	getFormReference(): any;
	/**
	 * @param value 
	 */
	setFormReference(value: any): void;
	/**
	 * @param value 
	 */
	setServiceTypeId(value: string): void;
	getLastUpdated(): any;
	/**
	 * @param value 
	 */
	setLastUpdated(value: any): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	getSchema(): vCACCAFENetworkObjectSchema[];
	getServiceTypeId(): string;
	/**
	 * @param value 
	 */
	setCreatedDate(value: any): void;
	getCreatedDate(): any;
}

declare interface vCACCAFENetworkOperationType {
	value(): string;
	values(): vCACCAFENetworkOperationType[];
	/**
	 * @param name 
	 */
	valueOf(name: string): vCACCAFENetworkOperationType;
	/**
	 * @param v 
	 */
	fromValue(v: string): vCACCAFENetworkOperationType;
}

declare interface vCACCAFENetworkProfileTypeEnum {
	value(): string;
	values(): vCACCAFENetworkProfileTypeEnum[];
	/**
	 * @param name 
	 */
	valueOf(name: string): vCACCAFENetworkProfileTypeEnum;
	/**
	 * @param v 
	 */
	fromValue(v: string): vCACCAFENetworkProfileTypeEnum;
}

declare class vCACCAFENetworkProviderRequest {
	constructor();
	constructor();
	getBlueprintRequestId(): string;
	/**
	 * @param value 
	 */
	setBlueprintRequestId(value: string): void;
	getTenantId(): string;
	getComponentTypeId(): string;
	/**
	 * @param value 
	 */
	setComponentTypeId(value: string): void;
	getNetworkObject(): vCACCAFENetworkObject;
	/**
	 * @param value 
	 */
	setNetworkObject(value: vCACCAFENetworkObject): void;
	getRootCafeRequestId(): string;
	/**
	 * @param value 
	 */
	setRootCafeRequestId(value: string): void;
	getOperationType(): vCACCAFENetworkOperationType;
	/**
	 * @param value 
	 */
	setOperationType(value: vCACCAFENetworkOperationType): void;
	getResourceId(): string;
	/**
	 * @param value 
	 */
	setResourceId(value: string): void;
	/**
	 * @param value 
	 */
	setBlueprintName(value: string): void;
	getBlueprintName(): string;
	getComponentData(): vCACCAFELiteralMap;
	/**
	 * @param value 
	 */
	setComponentData(value: vCACCAFELiteralMap): void;
	getPreviousComponentData(): vCACCAFELiteralMap;
	/**
	 * @param value 
	 */
	setPreviousComponentData(value: vCACCAFELiteralMap): void;
	getRequestInputData(): vCACCAFELiteralMap;
	/**
	 * @param value 
	 */
	setRequestInputData(value: vCACCAFELiteralMap): void;
	getCallbackAdditionalUrlPath(): string;
	/**
	 * @param value 
	 */
	setCallbackAdditionalUrlPath(value: string): void;
	getRootCafeDeploymentId(): string;
	/**
	 * @param value 
	 */
	setRootCafeDeploymentId(value: string): void;
	getComponentId(): string;
	/**
	 * @param value 
	 */
	setComponentId(value: string): void;
	/**
	 * @param value 
	 */
	setRequestId(value: string): void;
	/**
	 * @param value 
	 */
	setBlueprintId(value: string): void;
	getBlueprintId(): string;
	getCallbackServiceId(): string;
	/**
	 * @param value 
	 */
	setCallbackServiceId(value: string): void;
	getRequestId(): string;
	/**
	 * @param value 
	 */
	setSubtenantId(value: string): void;
	getOriginalCallbackServiceId(): string;
	/**
	 * @param value 
	 */
	setOriginalCallbackServiceId(value: string): void;
	getSubtenantId(): string;
	/**
	 * @param value 
	 */
	setTenantId(value: string): void;
}

declare class vCACCAFENetworkRequestCompletion {
	constructor();
	constructor();
	getTenantId(): string;
	getNetworkObject(): vCACCAFENetworkObject;
	/**
	 * @param value 
	 */
	setNetworkObject(value: vCACCAFENetworkObject): void;
	getRootCafeRequestId(): string;
	/**
	 * @param value 
	 */
	setRootCafeRequestId(value: string): void;
	isRequestSuccessful(): boolean;
	/**
	 * @param value 
	 */
	setRequestSuccessful(value: boolean): void;
	getOperationType(): vCACCAFENetworkOperationType;
	/**
	 * @param value 
	 */
	setOperationType(value: vCACCAFENetworkOperationType): void;
	getResourceId(): string;
	/**
	 * @param value 
	 */
	setResourceId(value: string): void;
	getCompletionDetails(): string;
	/**
	 * @param value 
	 */
	setCompletionDetails(value: string): void;
	/**
	 * @param value 
	 */
	setSubtenantId(value: string): void;
	getOriginalCallbackServiceId(): string;
	/**
	 * @param value 
	 */
	setOriginalCallbackServiceId(value: string): void;
	getSubtenantId(): string;
	/**
	 * @param value 
	 */
	setTenantId(value: string): void;
}

declare class vCACCAFENetworkType {
	constructor();
	constructor();
	getVersion(): number;
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	/**
	 * @param value 
	 */
	setVersion(value: number): void;
	getAllocationRelatedResourceTypes(): string[];
	getFormReference(): any;
	/**
	 * @param value 
	 */
	setFormReference(value: any): void;
	/**
	 * @param value 
	 */
	setServiceTypeId(value: string): void;
	getLastUpdated(): any;
	/**
	 * @param value 
	 */
	setLastUpdated(value: any): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	getSchema(): vCACCAFENetworkObjectSchema[];
	getServiceTypeId(): string;
	/**
	 * @param value 
	 */
	setCreatedDate(value: any): void;
	getCreatedDate(): any;
}

declare class vCACCAFENode {
	constructor();
	constructor();
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	getOsFamily(): vCACCAFEOsFamily;
	getTenantId(): string;
	/**
	 * @param value 
	 */
	setOsFamily(value: vCACCAFEOsFamily): void;
	getSubTenantId(): string;
	/**
	 * @param value 
	 */
	setSubTenantId(value: string): void;
	/**
	 * @param value 
	 */
	setLastAgentPingDate(value: any): void;
	getRegistrationState(): vCACCAFERegistrationState;
	/**
	 * @param value 
	 */
	setRegistrationState(value: vCACCAFERegistrationState): void;
	getMachineResourceId(): string;
	/**
	 * @param value 
	 */
	setMachineResourceId(value: string): void;
	getAgentId(): string;
	/**
	 * @param value 
	 */
	setAgentId(value: string): void;
	getAgentVersion(): string;
	/**
	 * @param value 
	 */
	setAgentVersion(value: string): void;
	getLastAgentPingDate(): any;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	/**
	 * @param value 
	 */
	setTenantId(value: string): void;
}

declare class vCACCAFENotClause {
	constructor();
	/**
	 * @param subClause 
	 */
	constructor(subClause: com.vmware.vcac.platform.content.criteria.Clause);
	getSubClause(): any;
	getConstantValue(): vCACCAFEBooleanLiteral;
	isConstant(): boolean;
	/**
	 * @param bVal 
	 */
	fromBoolean(bVal: boolean): any;
	/**
	 * @param sVal 
	 */
	fromString(sVal: string): any;
	/**
	 * @param lVal 
	 */
	fromInt(lVal: number): any;
	/**
	 * @param dVal 
	 */
	fromDecimal(dVal: number): any;
}

declare interface vCACCAFENotEqualsOperator {
	getId(): string;
	/**
	 * @param left 
	 * @param right 
	 */
	evaluate(left: any, right: any): vCACCAFEBooleanLiteral;
	getDataTypeConstraint(): any;
	getCategory(): any;
	getMultiplicityConstraint(): any;
}

declare class vCACCAFENotification {
	constructor();
	constructor();
	/**
	 * @param value 
	 */
	setContent(value: any): void;
	/**
	 * @param value 
	 */
	setPriority(value: vCACCAFENotificationPriority): void;
	getPriority(): vCACCAFENotificationPriority;
	getId(): string;
	getContent(): any;
	getRecipients(): vCACCAFENotificationPrincipal[];
	getAttachments(): vCACCAFENotificationAttachment[];
	getNotificationScenarioId(): string;
	/**
	 * @param value 
	 */
	setNotificationScenarioId(value: string): void;
	getEntityId(): string;
	/**
	 * @param value 
	 */
	setEntityId(value: string): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	getServiceId(): string;
	/**
	 * @param value 
	 */
	setServiceId(value: string): void;
}

declare class vCACCAFENotificationAction {
	constructor();
	constructor();
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setId(value: string): void;
}

declare class vCACCAFENotificationAttachment {
	constructor();
	constructor();
	/**
	 * @param value 
	 */
	setMimeType(value: string): void;
	getValue(): string;
	/**
	 * @param value 
	 */
	setValue(value: string): void;
	getFileName(): string;
	getMimeType(): string;
	getContentId(): string;
	/**
	 * @param value 
	 */
	setContentId(value: string): void;
	/**
	 * @param value 
	 */
	setFileName(value: string): void;
}

declare interface vCACCAFENotificationClient {
	getNotificationUserNotificationPreferenceService(): vCACCAFENotificationUserNotificationPreferenceService;
	getNotificationNotificationScenarioConfigurationService(): vCACCAFENotificationNotificationScenarioConfigurationService;
	getNotificationNotificationScenarioRegistrationService(): vCACCAFENotificationNotificationScenarioRegistrationService;
	getNotificationNotificationService(): vCACCAFENotificationNotificationService;
	getNotificationNotificationProviderService(): vCACCAFENotificationNotificationProviderService;
	/**
	 * Does a POST request expanding the given template variables in order.
	 * @param resourceUrl 
	 * @param object 
	 * @param urlVariables 
	 */
	postWithVariables(resourceUrl: string, object: any, urlVariables: string[]): vCACCAFEServiceResponse;
	/**
	 * Does a PUT request expanding the given template variables in order.
	 * @param resourceUrl 
	 * @param object 
	 * @param urlVariables 
	 */
	putWithVariables(resourceUrl: string, object: any, urlVariables: string[]): vCACCAFEServiceResponse;
	/**
	 * Does a DELETE request expanding the given template variables in order.
	 * @param resourceUrl 
	 * @param urlVariables 
	 */
	deleteWithVariables(resourceUrl: string, urlVariables: string[]): void;
	/**
	 * Gets the REST client URL.
	 */
	getUrl(): string;
	/**
	 * Indicates whether the current connection is already closed or not.
	 */
	isClosed(): boolean;
	/**
	 * Does a GET request for a file and allows specifying 'Accept' header.
	 * @param resourceUrl 
	 * @param acceptHeaderValue 
	 */
	getFileWithAcceptHeader(resourceUrl: string, acceptHeaderValue: string): any;
	/**
	 * Does a POST request with a file.
	 * @param resourceUrl 
	 * @param object 
	 */
	postFile(resourceUrl: string, object: any): vCACCAFEServiceResponse;
	/**
	 * Does a GET request expanding the given template variables in order.
	 * @param resourceUrl 
	 * @param urlVariables 
	 */
	getWithVariables(resourceUrl: string, urlVariables: string[]): any;
	/**
	 * Does a GET request.
	 * @param resourceUrl 
	 */
	get(resourceUrl: string): any;
	/**
	 * Does a PUT request.
	 * @param resourceUrl 
	 * @param object 
	 */
	put(resourceUrl: string, object: any): vCACCAFEServiceResponse;
	/**
	 * Does a DELETE request.
	 * @param resourceUrl 
	 */
	delete(resourceUrl: string): void;
	/**
	 * Invalidates the current connection.
	 */
	close(): void;
	/**
	 * Does a GET request for a file.
	 * @param resourceUrl 
	 */
	getFile(resourceUrl: string): any;
	/**
	 * Does a POST request.
	 * @param resourceUrl 
	 * @param object 
	 */
	post(resourceUrl: string, object: any): vCACCAFEServiceResponse;
}

declare class vCACCAFENotificationDeliveryMechanism {
	constructor();
	constructor();
	isEnabled(): boolean;
	getNotificationProvider(): vCACCAFENotificationProvider;
	/**
	 * @param value 
	 */
	setNotificationProvider(value: vCACCAFENotificationProvider): void;
	/**
	 * @param value 
	 */
	setEnabled(value: boolean): void;
}

declare class vCACCAFENotificationDeliveryStatus {
	constructor();
	constructor();
	/**
	 * @param value 
	 */
	setNotificationId(value: string): void;
	getDeliveryStatus(): vCACCAFEDeliveryStatus;
	/**
	 * @param value 
	 */
	setDeliveryStatus(value: vCACCAFEDeliveryStatus): void;
	getRecipientDeliveryStatuses(): vCACCAFERecipientDeliveryStatus[];
	getNotificationId(): string;
}

declare interface vCACCAFENotificationNotificationProviderService {
	/**
	 * This method invokes the SDK method NotificationProviderServiceImpl.deleteGlobalNotificationProvider(String providerId).
	 * @param providerId 
	 */
	deleteGlobalNotificationProvider(providerId: string): void;
	/**
	 * This method invokes the SDK method NotificationProviderServiceImpl.deleteTenantNotificationProvider(String tenantId, String providerId).
	 * @param tenantId 
	 * @param providerId 
	 */
	deleteTenantNotificationProvider(tenantId: string, providerId: string): void;
	/**
	 * This method invokes the SDK method NotificationProviderServiceImpl.retrieveCreateableGlobalNotificationProviderTypeInfo(Pageable pageable).
	 * @param pageable 
	 */
	retrieveCreateableGlobalNotificationProviderTypeInfo(pageable: any): vCACCAFEPagedResources;
	/**
	 * This method invokes the SDK method NotificationProviderServiceImpl.retrieveCreateableTenantNotificationProviderTypeInfo(String tenantId, Pageable pageable).
	 * @param tenantId 
	 * @param pageable 
	 */
	retrieveCreateableTenantNotificationProviderTypeInfo(tenantId: string, pageable: any): vCACCAFEPagedResources;
	/**
	 * This method invokes the SDK method NotificationProviderServiceImpl.retrieveGlobalNotificationProvider(String providerId).
	 * @param providerId 
	 */
	retrieveGlobalNotificationProvider(providerId: string): vCACCAFENotificationProvider;
	/**
	 * This method invokes the SDK method NotificationProviderServiceImpl.retrieveGlobalNotificationProviders(Pageable pageable).
	 * @param pageable 
	 */
	retrieveGlobalNotificationProviders(pageable: any): vCACCAFEPagedResources;
	/**
	 * This method invokes the SDK method NotificationProviderServiceImpl.retrieveTenantNotificationProvider(String tenantId, String id).
	 * @param tenantId 
	 * @param id 
	 */
	retrieveTenantNotificationProvider(tenantId: string, id: string): vCACCAFENotificationProvider;
	/**
	 * This method invokes the SDK method NotificationProviderServiceImpl.retrieveTenantNotificationProviders(String tenantId, Pageable pageable).
	 * @param tenantId 
	 * @param pageable 
	 */
	retrieveTenantNotificationProviders(tenantId: string, pageable: any): vCACCAFEPagedResources;
	/**
	 * This method invokes the SDK method NotificationProviderServiceImpl.testConnectionParameters(NotificationProvider notificationProvider).
	 * @param notificationProvider 
	 */
	testConnectionParameters(notificationProvider: vCACCAFENotificationProvider): vCACCAFEConnectivityStatus;
	/**
	 * This method invokes the SDK method NotificationProviderServiceImpl.createTenantNotificationProvider(String tenantId, NotificationProvider notificationProvider).
	 * @param tenantId 
	 * @param notificationProvider 
	 */
	createTenantNotificationProvider(tenantId: string, notificationProvider: vCACCAFENotificationProvider): vCACCAFENotificationProvider;
	/**
	 * This method invokes the SDK method NotificationProviderServiceImpl.createGlobalNotificationProvider(NotificationProvider notificationProvider).
	 * @param notificationProvider 
	 */
	createGlobalNotificationProvider(notificationProvider: vCACCAFENotificationProvider): vCACCAFENotificationProvider;
	/**
	 * This method invokes the SDK method NotificationProviderServiceImpl.updateGlobalNotificationProvider(NotificationProvider notificationProvider).
	 * @param notificationProvider 
	 */
	updateGlobalNotificationProvider(notificationProvider: vCACCAFENotificationProvider): vCACCAFENotificationProvider;
	/**
	 * This method invokes the SDK method NotificationProviderServiceImpl.updateTenantNotificationProvider(String tenantId, NotificationProvider notificationProvider).
	 * @param tenantId 
	 * @param notificationProvider 
	 */
	updateTenantNotificationProvider(tenantId: string, notificationProvider: vCACCAFENotificationProvider): vCACCAFENotificationProvider;
}

declare interface vCACCAFENotificationNotificationScenarioConfigurationService {
	/**
	 * This method invokes the SDK method NotificationScenarioConfigurationServiceImpl.getNotificationScenarioConfiguration(String tenantId).
	 * @param tenantId 
	 */
	getNotificationScenarioConfiguration(tenantId: string): vCACCAFENotificationScenarioConfiguration;
	/**
	 * This method invokes the SDK method NotificationScenarioConfigurationServiceImpl.getNotificationScenarioConfiguration(String tenantId, Pageable pageable).
	 * @param tenantId 
	 * @param pageable 
	 */
	getNotificationScenarioConfigurations(tenantId: string, pageable: any): vCACCAFEPagedResources;
	/**
	 * This method invokes the SDK method NotificationScenarioConfigurationServiceImpl.updateNotificationScenarioConfiguration(NotificationScenarioConfiguration notificationScenarioConfiguration).
	 * @param notificationScenarioConfiguration 
	 */
	updateNotificationScenarioConfiguration(notificationScenarioConfiguration: vCACCAFENotificationScenarioConfiguration): vCACCAFENotificationScenarioConfiguration;
}

declare interface vCACCAFENotificationNotificationScenarioRegistrationService {
	/**
	 * This method invokes the SDK method NotificationScenarioRegistrationServiceImpl.registerNotificationScenario(NotificationScenario notificationScenario).
	 * @param notificationScenario 
	 */
	registerNotificationScenario(notificationScenario: vCACCAFENotificationScenario): vCACCAFENotificationScenario;
	/**
	 * This method invokes the SDK method NotificationScenarioRegistrationServiceImpl.retrieveNotificationScenario(String id).
	 * @param id 
	 */
	retrieveNotificationScenario(id: string): vCACCAFENotificationScenario;
	/**
	 * This method invokes the SDK method NotificationScenarioRegistrationServiceImpl.deleteNotificationScenario(String id).
	 * @param id 
	 */
	deleteNotificationScenario(id: string): void;
	/**
	 * This method invokes the SDK method NotificationScenarioRegistrationServiceImpl.updateNotificationScenario(NotificationScenario notificationScenario).
	 * @param notificationScenario 
	 */
	updateNotificationScenario(notificationScenario: vCACCAFENotificationScenario): vCACCAFENotificationScenario;
}

declare interface vCACCAFENotificationNotificationService {
	/**
	 * This method invokes the SDK method NotificationServiceImpl.retrieve(String tenantName, String notificationId).
	 * @param tenantName 
	 * @param notificationId 
	 */
	retrieve(tenantName: string, notificationId: string): vCACCAFENotification;
	/**
	 * This method invokes the SDK method NotificationServiceImpl.retrieveInboundNotifications(String notificationId, Pageable pageable).
	 * @param notificationId 
	 * @param pageable 
	 */
	retrieveInboundNotifications(notificationId: string, pageable: any): vCACCAFEPagedResources;
	/**
	 * This method invokes the SDK method NotificationServiceImpl.retrieveNotificationDeliveryStatus(String notificationId).
	 * @param notificationId 
	 */
	retrieveNotificationDeliveryStatus(notificationId: string): vCACCAFENotificationDeliveryStatus;
	/**
	 * This method invokes the SDK method NotificationServiceImpl.send(String tenantName, Notification notification).
	 * @param tenantName 
	 * @param notification 
	 */
	send(tenantName: string, notification: vCACCAFENotification): vCACCAFENotification;
}

declare class vCACCAFENotificationPrincipal {
	constructor();
	constructor();
	getId(): string;
	getType(): vCACCAFENotificationPrincipalType;
	/**
	 * @param value 
	 */
	setType(value: vCACCAFENotificationPrincipalType): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
}

declare interface vCACCAFENotificationPrincipalType {
	value(): string;
	values(): vCACCAFENotificationPrincipalType[];
	/**
	 * @param name 
	 */
	valueOf(name: string): vCACCAFENotificationPrincipalType;
	/**
	 * @param v 
	 */
	fromValue(v: string): vCACCAFENotificationPrincipalType;
}

declare interface vCACCAFENotificationPriority {
	value(): string;
	values(): vCACCAFENotificationPriority[];
	/**
	 * @param name 
	 */
	valueOf(name: string): vCACCAFENotificationPriority;
	/**
	 * @param v 
	 */
	fromValue(v: string): vCACCAFENotificationPriority;
}

declare class vCACCAFENotificationProvider {
	constructor();
	constructor();
	getVersion(): number;
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	getType(): vCACCAFENotificationProviderType;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	/**
	 * @param value 
	 */
	setType(value: vCACCAFENotificationProviderType): void;
	getDescription(): string;
	/**
	 * @param value 
	 */
	setVersion(value: number): void;
	getProviderDirection(): vCACCAFEProviderDirection;
	/**
	 * @param value 
	 */
	setProviderDirection(value: vCACCAFEProviderDirection): void;
	getProviderMode(): vCACCAFEProviderMode;
	/**
	 * @param value 
	 */
	setProviderMode(value: vCACCAFEProviderMode): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
}

declare class vCACCAFENotificationProviderCreationInfo {
	constructor();
	constructor();
	getDirection(): vCACCAFEProviderDirection;
	/**
	 * @param value 
	 */
	setDirection(value: vCACCAFEProviderDirection): void;
	getProviderType(): vCACCAFENotificationProviderType;
	/**
	 * @param value 
	 */
	setProviderType(value: vCACCAFENotificationProviderType): void;
	/**
	 * @param value 
	 */
	setCreateable(value: boolean): void;
	isCreateable(): boolean;
}

declare interface vCACCAFENotificationProviderType {
	value(): string;
	values(): vCACCAFENotificationProviderType[];
	/**
	 * @param name 
	 */
	valueOf(name: string): vCACCAFENotificationProviderType;
	/**
	 * @param v 
	 */
	fromValue(v: string): vCACCAFENotificationProviderType;
}

declare class vCACCAFENotificationScenario {
	constructor();
	constructor();
	getName(): string;
	getActions(): vCACCAFENotificationAction[];
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	getServiceName(): string;
	/**
	 * @param value 
	 */
	setServiceName(value: string): void;
	/**
	 * @param value 
	 */
	setServiceTypeId(value: string): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	getServiceTypeId(): string;
}

declare class vCACCAFENotificationScenarioActionIdMapping {
	constructor();
	constructor();
	getId(): string;
	/**
	 * @param value 
	 */
	setScenarioId(value: string): void;
	getUuid(): string;
	/**
	 * @param value 
	 */
	setUuid(value: string): void;
	getActionId(): string;
	getScenarioId(): string;
	/**
	 * @param value 
	 */
	setActionId(value: string): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
}

declare class vCACCAFENotificationScenarioConfiguration {
	constructor();
	constructor();
	getVersion(): number;
	getSettings(): vCACCAFENotificationScenarioSetting[];
	/**
	 * @param value 
	 */
	setVersion(value: number): void;
	getTenantId(): string;
	/**
	 * @param value 
	 */
	setTenantId(value: string): void;
}

declare class vCACCAFENotificationScenarioSetting {
	constructor();
	constructor();
	isEnabled(): boolean;
	getNotificationScenario(): vCACCAFENotificationScenario;
	/**
	 * @param value 
	 */
	setNotificationScenario(value: vCACCAFENotificationScenario): void;
	/**
	 * @param value 
	 */
	setEnabled(value: boolean): void;
}

declare class vCACCAFENotificationStatus {
	constructor();
	constructor();
	/**
	 * @param value 
	 */
	setComment(value: string): void;
	getAction(): string;
	getComment(): string;
	/**
	 * @param value 
	 */
	setAction(value: string): void;
	/**
	 * @param value 
	 */
	setNotificationId(value: string): void;
	getEntityId(): string;
	/**
	 * @param value 
	 */
	setEntityId(value: string): void;
	getNotificationId(): string;
	/**
	 * @param value 
	 */
	setUserId(value: string): void;
	getUserId(): string;
}

declare interface vCACCAFENotificationUserNotificationPreferenceService {
	/**
	 * This method invokes the SDK method UserNotificationPreferenceServiceImpl.getNotificationPreference().
	 */
	getNotificationPreference(): vCACCAFEUserNotificationPreference;
	/**
	 * This method invokes the SDK method UserNotificationPreferenceServiceImpl.retrieveDefaultUserNotificationPreference(Pageable pageable).
	 * @param pageable 
	 */
	retrieveDefaultUserNotificationPreference(pageable: any): vCACCAFEPagedResources;
	/**
	 * This method invokes the SDK method UserNotificationPreferenceServiceImpl.retrieveUserNotificationPreference(Pageable pageable).
	 * @param pageable 
	 */
	retrieveUserNotificationPreference(pageable: any): vCACCAFEPagedResources;
	/**
	 * This method invokes the SDK method UserNotificationPreferenceServiceImpl.updateUserNotificationPreference(UserNotificationPreference userNotificationPreference).
	 * @param userNotificationPreference 
	 */
	updateUserNotificationPreference(userNotificationPreference: vCACCAFEUserNotificationPreference): vCACCAFEUserNotificationPreference;
}

declare class vCACCAFENullDisplayTextBehavior {
	constructor();
	constructor();
	/**
	 * @param value 
	 */
	constructor(value: com.vmware.vcac.platform.content.evaluators.Evaluator);
	/**
	 * @param value 
	 */
	constructor(value: string);
	/**
	 * @param value 
	 */
	cloneWithValue(value: any): any;
	/**
	 * @param constraint 
	 */
	mergeConstraint(constraint: any): vCACCAFENullDisplayTextBehavior;
	clone(): any;
	getValue(): any;
}

declare class vCACCAFENullableAdapter {
	constructor();
	constructor();
}

declare class vCACCAFEOdataQuery {
	constructor();
	/**
	 * @param value 
	 */
	static encode(value: string): string;
	static query(): vCACCAFEOdataQuery;
	getSort(): vCACCAFEOdataSort;
	/**
	 * @param filters 
	 */
	addFilter(filters: vCACCAFEFilterParam[]): vCACCAFEOdataQuery;
	/**
	 * @param top 
	 */
	setTop(top: number): vCACCAFEOdataQuery;
	/**
	 * @param skip 
	 */
	setSkip(skip: number): vCACCAFEOdataQuery;
	/**
	 * @param properties 
	 */
	addDescOrderBy(properties: string[]): vCACCAFEOdataQuery;
	getTop(): number;
	getSkip(): number;
	isSkipParamDefined(): boolean;
	/**
	 * @param properties 
	 */
	addAscOrderBy(properties: string[]): vCACCAFEOdataQuery;
	isTopParamDefined(): boolean;
	/**
	 * @param oDataOrder 
	 */
	addOrderBy(oDataOrder: any): vCACCAFEOdataQuery;
	toString(): string;
	/**
	 * @param name 
	 * @param value 
	 */
	setParam(name: string, value: number): void;
	/**
	 * @param name 
	 */
	toQueryString(name: string): string;
	getParamNames(): string[];
}

declare class vCACCAFEOdataSort {
	constructor();
	/**
	 * @param orders 
	 */
	constructor(orders: org.springframework.data.domain.Sort$Order[]);
	/**
	 * @param orders 
	 */
	constructor(orders: org.springframework.data.domain.Sort$Order[]);
	toString(): string;
	/**
	 * @param orders 
	 */
	with(orders: any[]): vCACCAFEOdataSort;
	toParamValue(): string;
	/**
	 * @param sort 
	 */
	getParamValue(sort: any): string;
}

declare class vCACCAFEOperationDescriptor {
	constructor();
	constructor();
	getVersion(): number;
	/**
	 * @param value 
	 */
	setDisplayName(value: string): void;
	getId(): string;
	getDisplayName(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	/**
	 * @param value 
	 */
	setVersion(value: number): void;
	getDefaultTaskMappings(): vCACCAFEOperationTaskMapping[];
	getLastUpdated(): any;
	/**
	 * @param value 
	 */
	setLastUpdated(value: any): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	/**
	 * @param value 
	 */
	setCreatedDate(value: any): void;
	getCreatedDate(): any;
}

declare class vCACCAFEOperationError {
	constructor();
	constructor();
	getErrorMessage(): string;
	getErrorCode(): number;
	/**
	 * @param value 
	 */
	setErrorCode(value: number): void;
	/**
	 * @param value 
	 */
	setErrorMessage(value: string): void;
}

declare class vCACCAFEOperationInfo {
	constructor();
	constructor();
	getOperationType(): vCACCAFEOperationType;
	/**
	 * @param value 
	 */
	setOperationType(value: vCACCAFEOperationType): void;
	getOperationStatus(): vCACCAFEOperationStatus;
	/**
	 * @param value 
	 */
	setOperationStatus(value: vCACCAFEOperationStatus): void;
	getOperationResults(): vCACCAFEOperationResult[];
}

declare class vCACCAFEOperationResult {
	constructor();
	constructor();
	getMessages(): string[];
	getContentId(): string;
	/**
	 * @param value 
	 */
	setContentId(value: string): void;
	getContentName(): string;
	/**
	 * @param value 
	 */
	setContentName(value: string): void;
	getContentTypeId(): string;
	/**
	 * @param value 
	 */
	setContentTypeId(value: string): void;
	getOperationStatus(): vCACCAFEOperationStatus;
	/**
	 * @param value 
	 */
	setOperationStatus(value: vCACCAFEOperationStatus): void;
	getOperationErrors(): vCACCAFEOperationError[];
}

declare interface vCACCAFEOperationStatus {
	value(): string;
	values(): vCACCAFEOperationStatus[];
	/**
	 * @param name 
	 */
	valueOf(name: string): vCACCAFEOperationStatus;
	/**
	 * @param v 
	 */
	fromValue(v: string): vCACCAFEOperationStatus;
}

declare class vCACCAFEOperationTaskMapping {
	constructor();
	constructor();
	getOrder(): number;
	/**
	 * @param value 
	 */
	setOrder(value: number): void;
	getTaskDescriptorRef(): vCACCAFELabelledReference;
	/**
	 * @param value 
	 */
	setTaskDescriptorRef(value: vCACCAFELabelledReference): void;
}

declare interface vCACCAFEOperationType {
	value(): string;
	values(): vCACCAFEOperationType[];
	/**
	 * @param name 
	 */
	valueOf(name: string): vCACCAFEOperationType;
	/**
	 * @param v 
	 */
	fromValue(v: string): vCACCAFEOperationType;
}

declare class vCACCAFEOrClause {
	constructor();
	/**
	 * @param subClauses 
	 */
	constructor(subClauses: com.vmware.vcac.platform.content.criteria.Clause[]);
	getSubClauses(): any[];
	getConstantValue(): vCACCAFEBooleanLiteral;
	isConstant(): boolean;
	/**
	 * @param bVal 
	 */
	fromBoolean(bVal: boolean): any;
	/**
	 * @param sVal 
	 */
	fromString(sVal: string): any;
	/**
	 * @param lVal 
	 */
	fromInt(lVal: number): any;
	/**
	 * @param dVal 
	 */
	fromDecimal(dVal: number): any;
}

declare class vCACCAFEOrganization {
	constructor();
	constructor();
}

declare interface vCACCAFEOsFamily {
	value(): string;
	values(): vCACCAFEOsFamily[];
	/**
	 * @param name 
	 */
	valueOf(name: string): vCACCAFEOsFamily;
	/**
	 * @param v 
	 */
	fromValue(v: string): vCACCAFEOsFamily;
}

declare class vCACCAFEOsFamilyInfo {
	constructor();
	constructor();
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getOsFamily(): vCACCAFEOsFamily;
	/**
	 * @param value 
	 */
	setOsFamily(value: vCACCAFEOsFamily): void;
}

declare class vCACCAFEPackage {
	constructor();
	constructor();
	getContents(): string[];
	getVersion(): number;
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	/**
	 * @param value 
	 */
	setVersion(value: number): void;
	getTenantId(): string;
	getLastUpdated(): any;
	/**
	 * @param value 
	 */
	setLastUpdated(value: any): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	/**
	 * @param value 
	 */
	setSubtenantId(value: string): void;
	getSubtenantId(): string;
	/**
	 * @param value 
	 */
	setCreatedDate(value: any): void;
	/**
	 * @param value 
	 */
	setTenantId(value: string): void;
	getCreatedDate(): any;
}

declare class vCACCAFEPage {
	constructor();
	constructor();
	getContent(): vCACCAFEComponentRequestStatus[];
}

declare class vCACCAFEPageMetadata {
	constructor();
	/**
	 * @param size 
	 * @param number 
	 * @param totalElements 
	 * @param totalPages 
	 * @param offset 
	 */
	constructor(size: number, number: number, totalElements: number, totalPages: number, offset: number);
	getSize(): number;
	getOffset(): number;
	getNumber(): number;
	getTotalElements(): number;
	getTotalPages(): number;
}

declare class vCACCAFEPageOdataRequest {
	constructor();
	/**
	 * @param oDataQuery 
	 */
	constructor(oDataQuery: vCACCAFEOdataQuery);
	/**
	 * @param page 
	 * @param size 
	 */
	constructor(page: number, size: number);
	/**
	 * @param page 
	 * @param size 
	 * @param oDataQuery 
	 */
	constructor(page: number, size: number, oDataQuery: vCACCAFEOdataQuery);
	next(): any;
	first(): any;
	getOffset(): number;
	hasPrevious(): boolean;
	getQuerySpec(): vCACCAFEOdataQuery;
	/**
	 * @param oDataQuery 
	 */
	page(oDataQuery: vCACCAFEOdataQuery): vCACCAFEPageOdataRequest;
	getPageSize(): number;
	getPageNumber(): number;
	getSort(): any;
	/**
	 * @param urib 
	 */
	appendUrlQueryParams(urib: any): void;
	isPageParams(): boolean;
	previousOrFirst(): any;
}

declare class vCACCAFEPagedResources {
	constructor();
	/**
	 * @param content 
	 * @param metadata 
	 * @param links 
	 */
	constructor(content: java.util.Collection, metadata: vCACCAFEPageMetadata, links: java.lang.Iterable);
	/**
	 * @param content 
	 * @param metadata 
	 * @param links 
	 */
	constructor(content: java.util.Collection, metadata: vCACCAFEPageMetadata, links: vCACCAFELink[]);
	getMetadata(): vCACCAFEPageMetadata;
	getContent(): any;
}

declare class vCACCAFEParam {
	constructor();
	constructor();
	getValue(): string;
	getKey(): string;
	/**
	 * @param value 
	 */
	setValue(value: string): void;
	/**
	 * @param value 
	 */
	setKey(value: string): void;
}

declare class vCACCAFEParameterMapping {
	constructor();
	constructor();
	/**
	 * @param key 
	 * @param value 
	 */
	constructor(key: string, value: com.vmware.vcac.platform.content.evaluators.Evaluator);
	getValue(): any;
	getKey(): string;
	/**
	 * @param value 
	 */
	setValue(value: any): void;
	/**
	 * @param key 
	 */
	setKey(key: string): void;
}

declare class vCACCAFEParameterMappingCollection {
	constructor();
	/**
	 * @param inner 
	 */
	constructor(inner: java.util.Map);
	/**
	 * @param collection 
	 */
	constructor(collection: vCACCAFEParameterMapping[]);
	constructor();
	getMappings(): vCACCAFEParameterMapping[];
	/**
	 * @param mappings 
	 */
	setMappings(mappings: vCACCAFEParameterMapping[]): void;
}

declare class vCACCAFEPath {
	constructor();
	constructor();
	getParams(): vCACCAFEParam[];
	getPlace(): string;
	/**
	 * @param value 
	 */
	setPlace(value: string): void;
}

declare class vCACCAFEPermissibleValue {
	constructor();
	/**
	 * @param underlyingValue 
	 * @param label 
	 */
	constructor(underlyingValue: com.vmware.vcac.platform.content.literals.Literal, label: string);
	/**
	 * @param underlyingValue 
	 */
	constructor(underlyingValue: com.vmware.vcac.platform.content.literals.Literal);
	constructor();
	/**
	 * @param label 
	 */
	setLabel(label: string): void;
	getLabel(): string;
	getUnderlyingValue(): any;
}

declare class vCACCAFEPermission {
	constructor();
	constructor();
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	getStatus(): string;
	getPrereqAdminPermissions(): vCACCAFEPermission[];
	/**
	 * @param value 
	 */
	setId(value: string): void;
	/**
	 * @param value 
	 */
	setStatus(value: string): void;
}

declare interface vCACCAFEPermissionFilter {
	values(): vCACCAFEPermissionFilter[];
	/**
	 * @param name 
	 */
	valueOf(name: string): vCACCAFEPermissionFilter;
}

declare class vCACCAFEPhase {
	constructor();
	constructor();
	toString(): string;
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	getPhasetype(): vCACCAFEApprovalPhaseType;
	/**
	 * @param value 
	 */
	setPhasetype(value: vCACCAFEApprovalPhaseType): void;
	getLevels(): vCACCAFEApprovalLevel[];
	/**
	 * @param value 
	 */
	setId(value: string): void;
}

declare class vCACCAFEPhaseDecision {
	constructor();
	constructor();
	/**
	 * @param value 
	 */
	setPhase(value: vCACCAFEPhase): void;
	getId(): string;
	getState(): vCACCAFEEvaluationState;
	/**
	 * @param value 
	 */
	setStartTime(value: any): void;
	getStartTime(): any;
	/**
	 * @param value 
	 */
	setState(value: vCACCAFEEvaluationState): void;
	getDecisions(): vCACCAFEApprovalLevelDecision[];
	getCompletionTime(): any;
	/**
	 * @param value 
	 */
	setCompletionTime(value: any): void;
	getPhaseNumber(): number;
	/**
	 * @param value 
	 */
	setPhaseNumber(value: number): void;
	getPhase(): vCACCAFEPhase;
	/**
	 * @param value 
	 */
	setId(value: string): void;
}

declare class vCACCAFEPlacementRequestStatus {
	constructor();
	constructor();
	getRequestingServiceId(): string;
	/**
	 * @param value 
	 */
	setRequestingServiceId(value: string): void;
	getPlanId(): string;
	/**
	 * @param value 
	 */
	setPlanId(value: string): void;
	getBlueprintExternalId(): string;
	/**
	 * @param value 
	 */
	setBlueprintExternalId(value: string): void;
	getPlacementState(): string;
	/**
	 * @param value 
	 */
	setPlacementState(value: string): void;
	getErrorDetails(): string;
	/**
	 * @param value 
	 */
	setErrorDetails(value: string): void;
	/**
	 * @param value 
	 */
	setRequestId(value: string): void;
	getRequestBindingId(): string;
	/**
	 * @param value 
	 */
	setRequestBindingId(value: string): void;
	getRequestId(): string;
}

declare class vCACCAFEPlugin {
	constructor();
	constructor();
	getId(): string;
	getPluginUrl(): string;
	/**
	 * @param value 
	 */
	setPluginUrl(value: string): void;
	/**
	 * @param value 
	 */
	setMetadata(value: string): void;
	getMetadata(): string;
	/**
	 * @param value 
	 */
	setId(value: string): void;
}

declare class vCACCAFEPluginInfo {
	constructor();
	constructor();
	/**
	 * @param value 
	 */
	setPluginId(value: string): void;
	getPublisher(): string;
	/**
	 * @param value 
	 */
	setPublisher(value: string): void;
	getPluginId(): string;
}

declare class vCACCAFEPolicyDefinition {
	constructor();
	constructor();
	getProperties(): vCACCAFELiteralMap;
	/**
	 * @param value 
	 */
	setProperties(value: vCACCAFELiteralMap): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	getTenantId(): string;
	/**
	 * @param value 
	 */
	setHrid(value: string): void;
	getHrid(): string;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	/**
	 * @param value 
	 */
	setTenantId(value: string): void;
}

declare class vCACCAFEPreRegisterNodeResponse {
	constructor();
	constructor();
	/**
	 * @param value 
	 */
	setNode(value: vCACCAFENode): void;
	getNode(): vCACCAFENode;
	getMachineCustomProperties(): vCACCAFESwAttribute[];
	getPropertyFileEntries(): vCACCAFESwAttribute[];
}

declare class vCACCAFEPriceInfo {
	constructor();
	constructor();
	/**
	 * @param value 
	 */
	setMin(value: any): void;
	/**
	 * @param value 
	 */
	setMax(value: any): void;
	getDisplayString(): string;
	getMax(): any;
	/**
	 * @param value 
	 */
	setDisplayString(value: string): void;
	getMin(): any;
}

declare interface vCACCAFEPrimitiveDataType {
	/**
	 * @param typeId 
	 */
	fromTypeId(typeId: vCACCAFEDataTypeId): vCACCAFEPrimitiveDataType;
	/**
	 * @param typeId 
	 */
	tryFromTypeId(typeId: vCACCAFEDataTypeId): vCACCAFEPrimitiveDataType;
	getOperatorCategories(): any[];
	getTypeId(): vCACCAFEDataTypeId;
}

declare class vCACCAFEPrincipalData {
	constructor();
	constructor();
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getPrincipalId(): vCACCAFEPrincipalId;
	/**
	 * @param value 
	 */
	setPrincipalId(value: vCACCAFEPrincipalId): void;
	getPrincipalType(): vCACCAFEPrincipalType;
	/**
	 * @param value 
	 */
	setPrincipalType(value: vCACCAFEPrincipalType): void;
}

declare class vCACCAFEPrincipalExtension {
	constructor();
	constructor();
	/**
	 * @param value 
	 */
	setDisplayName(value: string): void;
	getId(): string;
	getDisplayName(): string;
	getScopes(): vCACCAFEScope[];
	getTenant(): string;
	getPrincipalRef(): vCACCAFEPrincipalRef;
	/**
	 * @param value 
	 */
	setPrincipalRef(value: vCACCAFEPrincipalRef): void;
	getSystemRoles(): vCACCAFESystemRole[];
	getTenantRoles(): vCACCAFETenantRole[];
	/**
	 * @param value 
	 */
	setTenant(value: string): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
}

declare class vCACCAFEPrincipalId {
	constructor();
	constructor();
	/**
	 * @param name 
	 * @param domain 
	 */
	constructor(name: string, domain: string);
	/**
	 * @param domain 
	 */
	setDomain(domain: string): void;
	getName(): string;
	/**
	 * @param name 
	 */
	setName(name: string): void;
	getDomain(): string;
}

declare class vCACCAFEPrincipalRef {
	constructor();
	constructor();
	/**
	 * @param value 
	 */
	setDomain(value: string): void;
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getDomain(): string;
}

declare class vCACCAFEPrincipalSearchCriteria {
	constructor();
	/**
	 * @param tenantName 
	 */
	constructor(tenantName: string);
	/**
	 * @param domain 
	 */
	setDomain(domain: string): void;
	getDomain(): string;
	getPermissionFilter(): vCACCAFEPermissionFilter;
	/**
	 * @param permissionFilter 
	 */
	setPermissionFilter(permissionFilter: vCACCAFEPermissionFilter): void;
	/**
	 * @param localUserOnly 
	 */
	setLocalUsersOnly(localUserOnly: boolean): void;
	isExpandGroups(): boolean;
	/**
	 * @param expandGroups 
	 */
	setExpandGroups(expandGroups: boolean): void;
	isLocalUsersOnly(): boolean;
	getTenantName(): string;
	getParentGroup(): vCACCAFEGroup;
	/**
	 * @param parentGroup 
	 */
	setParentGroup(parentGroup: vCACCAFEGroup): void;
	/**
	 * @param criteria 
	 */
	setCriteria(criteria: string): void;
	getCriteria(): string;
}

declare interface vCACCAFEPrincipalType {
}

declare class vCACCAFEPrincipalValidationWrapper {
	constructor();
	constructor();
	getInputPrincipalId(): vCACCAFEPrincipalId;
	/**
	 * @param value 
	 */
	setInputPrincipalId(value: vCACCAFEPrincipalId): void;
	getPrincipalData(): vCACCAFEPrincipalData;
	/**
	 * @param value 
	 */
	setPrincipalData(value: vCACCAFEPrincipalData): void;
}

declare class vCACCAFEProduct {
	constructor();
	constructor();
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setFamily(value: vCACCAFEProductFamily): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	getFamily(): vCACCAFEProductFamily;
	getEditionKey(): string;
	/**
	 * @param value 
	 */
	setEditionKey(value: string): void;
	getSuiteName(): string;
	/**
	 * @param value 
	 */
	setSuiteName(value: string): void;
}

declare class vCACCAFEProductFamily {
	constructor();
	constructor();
	getVersion(): string;
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	/**
	 * @param value 
	 */
	setVersion(value: string): void;
}

declare interface vCACCAFEPropertyClient {
	getPropertyContextPropertyDefinitionService(): vCACCAFEPropertyContextPropertyDefinitionService;
	getPropertyContextPropertyGroupService(): vCACCAFEPropertyContextPropertyGroupService;
	/**
	 * Does a POST request expanding the given template variables in order.
	 * @param resourceUrl 
	 * @param object 
	 * @param urlVariables 
	 */
	postWithVariables(resourceUrl: string, object: any, urlVariables: string[]): vCACCAFEServiceResponse;
	/**
	 * Does a PUT request expanding the given template variables in order.
	 * @param resourceUrl 
	 * @param object 
	 * @param urlVariables 
	 */
	putWithVariables(resourceUrl: string, object: any, urlVariables: string[]): vCACCAFEServiceResponse;
	/**
	 * Does a DELETE request expanding the given template variables in order.
	 * @param resourceUrl 
	 * @param urlVariables 
	 */
	deleteWithVariables(resourceUrl: string, urlVariables: string[]): void;
	/**
	 * Gets the REST client URL.
	 */
	getUrl(): string;
	/**
	 * Indicates whether the current connection is already closed or not.
	 */
	isClosed(): boolean;
	/**
	 * Does a GET request for a file and allows specifying 'Accept' header.
	 * @param resourceUrl 
	 * @param acceptHeaderValue 
	 */
	getFileWithAcceptHeader(resourceUrl: string, acceptHeaderValue: string): any;
	/**
	 * Does a POST request with a file.
	 * @param resourceUrl 
	 * @param object 
	 */
	postFile(resourceUrl: string, object: any): vCACCAFEServiceResponse;
	/**
	 * Does a GET request expanding the given template variables in order.
	 * @param resourceUrl 
	 * @param urlVariables 
	 */
	getWithVariables(resourceUrl: string, urlVariables: string[]): any;
	/**
	 * Does a GET request.
	 * @param resourceUrl 
	 */
	get(resourceUrl: string): any;
	/**
	 * Does a PUT request.
	 * @param resourceUrl 
	 * @param object 
	 */
	put(resourceUrl: string, object: any): vCACCAFEServiceResponse;
	/**
	 * Does a DELETE request.
	 * @param resourceUrl 
	 */
	delete(resourceUrl: string): void;
	/**
	 * Invalidates the current connection.
	 */
	close(): void;
	/**
	 * Does a GET request for a file.
	 * @param resourceUrl 
	 */
	getFile(resourceUrl: string): any;
	/**
	 * Does a POST request.
	 * @param resourceUrl 
	 * @param object 
	 */
	post(resourceUrl: string, object: any): vCACCAFEServiceResponse;
}

declare interface vCACCAFEPropertyContextPropertyDefinitionService {
	/**
	 * This method invokes the SDK method ContextPropertyDefinitionService.savePropertyDefinition(String id, ContextPropertyDefinition definition).
	 * @param id 
	 * @param definition 
	 */
	savePropertyDefinition(id: string, definition: vCACCAFEContextPropertyDefinition): void;
	/**
	 * This method invokes the SDK method ContextPropertyDefinitionService.createPropertyDefinition(ContextPropertyDefinition definition).
	 * @param definition 
	 */
	createPropertyDefinition(definition: vCACCAFEContextPropertyDefinition): any;
	/**
	 * This method invokes the SDK method ContextPropertyDefinitionService.deletePropertyDefinition(String id).
	 * @param id 
	 */
	deletePropertyDefinition(id: string): void;
	/**
	 * This method invokes the SDK method ContextPropertyDefinitionService.deletePropertyDefinition(String id, String tenantId).
	 * @param id 
	 * @param tenantId 
	 */
	deletePropertyDefinitionByIdAndTenantId(id: string, tenantId: string): void;
	/**
	 * This method invokes the SDK method ContextPropertyDefinitionService.getMultiplePropertyDefinitions(List<String> ids, String tenantId).
	 * @param ids 
	 * @param tenantId 
	 */
	getMultiplePropertyDefinitions(ids: string[], tenantId: string): vCACCAFEContextPropertyDefinition[];
	/**
	 * This method invokes the SDK method ContextPropertyDefinitionService.getValues(String id, ElementValuesRequest request).
	 * @param id 
	 * @param request 
	 */
	getValues(id: string, request: any): any;
	/**
	 * This method invokes the SDK method ContextPropertyDefinitionService.getPropertyDefinitions(Pageable pageable).
	 * @param pageable 
	 */
	getPropertyDefinitions(pageable: any): vCACCAFEPagedResources;
	/**
	 * This method invokes the SDK method ContextPropertyDefinitionService.getPropertyDefinition(String id).
	 * @param id 
	 */
	getPropertyDefinition(id: string): vCACCAFEContextPropertyDefinition;
}

declare interface vCACCAFEPropertyContextPropertyGroupService {
	/**
	 * This method invokes the SDK method ContextPropertyGroupService.deletePropertyGroup(String id, String tenantId).
	 * @param id 
	 * @param tenantId 
	 */
	deletePropertyGroupByIdAndTenantId(id: string, tenantId: string): void;
	/**
	 * This method invokes the SDK method ContextPropertyGroupService.createPropertyGroup(ContextPropertyGroup group).
	 * @param group 
	 */
	createPropertyGroup(group: vCACCAFEContextPropertyGroup): any;
	/**
	 * This method invokes the SDK method ContextPropertyGroupService.deletePropertyGroup(String id).
	 * @param id 
	 */
	deletePropertyGroup(id: string): void;
	/**
	 * This method invokes the SDK method ContextPropertyGroupService.getMultiplePropertyGroups(List<String> ids, String tenantId).
	 * @param ids 
	 * @param tenantId 
	 */
	getMultiplePropertyGroups(ids: string[], tenantId: string): vCACCAFEContextPropertyGroup[];
	/**
	 * This method invokes the SDK method ContextPropertyGroupService.savePropertyGroup(String id, ContextPropertyGroup group).
	 * @param id 
	 * @param group 
	 */
	savePropertyGroup(id: string, group: vCACCAFEContextPropertyGroup): void;
	/**
	 * This method invokes the SDK method ContextPropertyGroupService.getPropertyGroups(Pageable pageable).
	 * @param pageable 
	 */
	getPropertyGroups(pageable: any): vCACCAFEPagedResources;
	/**
	 * This method invokes the SDK method ContextPropertyGroupService.getPropertyGroup(String id).
	 * @param id 
	 */
	getPropertyGroup(id: string): vCACCAFEContextPropertyGroup;
}

declare interface vCACCAFEPropertyType {
	value(): string;
	values(): vCACCAFEPropertyType[];
	/**
	 * @param name 
	 */
	valueOf(name: string): vCACCAFEPropertyType;
	/**
	 * @param v 
	 */
	fromValue(v: string): vCACCAFEPropertyType;
}

declare class vCACCAFEProvider {
	constructor();
	constructor();
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	getProviderTypeId(): string;
	/**
	 * @param value 
	 */
	setProviderTypeId(value: string): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
}

declare interface vCACCAFEProviderAction {
	value(): string;
	values(): vCACCAFEProviderAction[];
	/**
	 * @param name 
	 */
	valueOf(name: string): vCACCAFEProviderAction;
	/**
	 * @param v 
	 */
	fromValue(v: string): vCACCAFEProviderAction;
}

declare class vCACCAFEProviderAndBinding {
	constructor();
	constructor();
	getProviderId(): string;
	/**
	 * @param value 
	 */
	setProviderId(value: string): void;
	getBindingId(): string;
	/**
	 * @param value 
	 */
	setBindingId(value: string): void;
}

declare class vCACCAFEProviderBinding {
	constructor();
	constructor();
	toString(): string;
	getProviderRef(): vCACCAFELabelledReference;
	/**
	 * @param value 
	 */
	setProviderRef(value: vCACCAFELabelledReference): void;
	getBindingId(): string;
	/**
	 * @param value 
	 */
	setBindingId(value: string): void;
}

declare interface vCACCAFEProviderCardinalityConstraint {
	value(): string;
	values(): vCACCAFEProviderCardinalityConstraint[];
	/**
	 * @param name 
	 */
	valueOf(name: string): vCACCAFEProviderCardinalityConstraint;
	/**
	 * @param v 
	 */
	fromValue(v: string): vCACCAFEProviderCardinalityConstraint;
}

declare class vCACCAFEProviderCatalogItem {
	constructor();
	constructor();
	/**
	 * @param value 
	 */
	setOrganization(value: vCACCAFECatalogOrganizationReference): void;
	getOrganization(): vCACCAFECatalogOrganizationReference;
	getForms(): vCACCAFECatalogItemForms;
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	/**
	 * @param value 
	 */
	setQuota(value: number): void;
	isRequestable(): boolean;
	/**
	 * @param value 
	 */
	setRequestable(value: boolean): void;
	getQuota(): number;
	/**
	 * @param value 
	 */
	setForms(value: vCACCAFECatalogItemForms): void;
	getCatalogItemTypeId(): string;
	/**
	 * @param value 
	 */
	setCatalogItemTypeId(value: string): void;
	getOutputResourceTypeId(): string;
	/**
	 * @param value 
	 */
	setOutputResourceTypeId(value: string): void;
	getIconId(): string;
	/**
	 * @param value 
	 */
	setIconId(value: string): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
}

declare class vCACCAFEProviderContent {
	constructor();
	constructor();
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	getDependencies(): vCACCAFEContentDependency[];
	getTenantId(): string;
	getSubTenantId(): string;
	/**
	 * @param value 
	 */
	setSubTenantId(value: string): void;
	getContentId(): string;
	/**
	 * @param value 
	 */
	setContentId(value: string): void;
	getContentTypeId(): string;
	/**
	 * @param value 
	 */
	setContentTypeId(value: string): void;
	getContentData(): vCACCAFEContentData;
	/**
	 * @param value 
	 */
	setContentData(value: vCACCAFEContentData): void;
	getStorageType(): vCACCAFEStorageType;
	/**
	 * @param value 
	 */
	setStorageType(value: vCACCAFEStorageType): void;
	/**
	 * @param value 
	 */
	setTenantId(value: string): void;
	getServiceId(): string;
	/**
	 * @param value 
	 */
	setServiceId(value: string): void;
}

declare interface vCACCAFEProviderDirection {
	value(): string;
	values(): vCACCAFEProviderDirection[];
	/**
	 * @param name 
	 */
	valueOf(name: string): vCACCAFEProviderDirection;
	/**
	 * @param v 
	 */
	fromValue(v: string): vCACCAFEProviderDirection;
}

declare class vCACCAFEProviderMode {
	constructor();
	constructor();
	/**
	 * @param value 
	 */
	setRole(value: vCACCAFEProviderRole): void;
	getOverriddenGlobalProviderId(): string;
	/**
	 * @param value 
	 */
	setOverriddenGlobalProviderId(value: string): void;
	getRole(): vCACCAFEProviderRole;
}

declare interface vCACCAFEProviderMultiplicity {
	value(): string;
	values(): vCACCAFEProviderMultiplicity[];
	/**
	 * @param name 
	 */
	valueOf(name: string): vCACCAFEProviderMultiplicity;
	/**
	 * @param v 
	 */
	fromValue(v: string): vCACCAFEProviderMultiplicity;
}

declare class vCACCAFEProviderPreferences {
	constructor();
	constructor();
	getId(): string;
	getPreferences(): vCACCAFELiteralMap;
	/**
	 * @param value 
	 */
	setPreferences(value: vCACCAFELiteralMap): void;
	/**
	 * @param value 
	 */
	setServiceTypeId(value: string): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	getServiceTypeId(): string;
}

declare class vCACCAFEProviderRequest {
	constructor();
	constructor();
	/**
	 * @param value 
	 */
	setOrganization(value: vCACCAFECatalogOrganizationReference): void;
	getOrganization(): vCACCAFECatalogOrganizationReference;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	getRequestType(): vCACCAFERequestType;
	/**
	 * @param value 
	 */
	setRequestType(value: vCACCAFERequestType): void;
	getRequestedObjectId(): string;
	/**
	 * @param value 
	 */
	setRequestedObjectId(value: string): void;
	getTargetResourceTypeId(): string;
	/**
	 * @param value 
	 */
	setTargetResourceTypeId(value: string): void;
	getTargetResourceProviderId(): string;
	/**
	 * @param value 
	 */
	setTargetResourceProviderId(value: string): void;
	getTargetCafeResourceId(): string;
	/**
	 * @param value 
	 */
	setTargetCafeResourceId(value: string): void;
	getRootCafeResourceId(): string;
	/**
	 * @param value 
	 */
	setRootCafeResourceId(value: string): void;
	getCatalogRequestId(): string;
	/**
	 * @param value 
	 */
	setCatalogRequestId(value: string): void;
	getRequestedFor(): string;
	getRequestedBy(): string;
	getCatalogItemTypeId(): string;
	/**
	 * @param value 
	 */
	setCatalogItemTypeId(value: string): void;
	getCallbackServiceId(): string;
	/**
	 * @param value 
	 */
	setCallbackServiceId(value: string): void;
	getReasons(): string;
	getRequestNumber(): number;
	/**
	 * @param value 
	 */
	setRequestNumber(value: number): void;
	getRequestData(): vCACCAFELiteralMap;
	getTargetResourceId(): string;
	/**
	 * @param value 
	 */
	setTargetResourceId(value: string): void;
	getRequestBindingId(): string;
	/**
	 * @param value 
	 */
	setRequestBindingId(value: string): void;
	/**
	 * @param value 
	 */
	setReasons(value: string): void;
	/**
	 * @param value 
	 */
	setRequestedBy(value: string): void;
	/**
	 * @param value 
	 */
	setRequestedFor(value: string): void;
	/**
	 * @param value 
	 */
	setRequestData(value: vCACCAFELiteralMap): void;
	isSolution(): boolean;
	/**
	 * @param value 
	 */
	setSolution(value: boolean): void;
}

declare class vCACCAFEProviderRequestExtension {
	constructor();
	constructor();
	getExtData(): vCACCAFELiteralMap;
	/**
	 * @param value 
	 */
	setExtData(value: vCACCAFELiteralMap): void;
}

declare class vCACCAFEProviderRequestInitialization {
	constructor();
	constructor();
	getTotalQuote(): vCACCAFERequestQuote;
	/**
	 * @param value 
	 */
	setTotalQuote(value: vCACCAFERequestQuote): void;
	getRequestBindingId(): string;
	/**
	 * @param value 
	 */
	setRequestBindingId(value: string): void;
}

declare class vCACCAFEProviderRequestValidation {
	constructor();
	constructor();
	getUpdatedData(): vCACCAFELiteralMap;
	/**
	 * @param value 
	 */
	setUpdatedData(value: vCACCAFELiteralMap): void;
	getQuote(): vCACCAFERequestQuote;
	/**
	 * @param value 
	 */
	setQuote(value: vCACCAFERequestQuote): void;
}

declare class vCACCAFEProviderResource {
	constructor();
	constructor();
	getData(): vCACCAFELiteralMap;
	/**
	 * @param value 
	 */
	setOrganization(value: vCACCAFECatalogOrganizationReference): void;
	getOrganization(): vCACCAFECatalogOrganizationReference;
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	/**
	 * @param value 
	 */
	setCatalogItemId(value: string): void;
	getOwners(): vCACCAFECatalogPrincipal[];
	getParentResourceId(): string;
	/**
	 * @param value 
	 */
	setParentResourceId(value: string): void;
	getCatalogItemId(): string;
	getCatalogItemRequestId(): string;
	/**
	 * @param value 
	 */
	setCatalogItemRequestId(value: string): void;
	getLeaseState(): vCACCAFEProviderResourceLeaseState;
	/**
	 * @param value 
	 */
	setLeaseState(value: vCACCAFEProviderResourceLeaseState): void;
	getParentCafeResourceId(): string;
	/**
	 * @param value 
	 */
	setParentCafeResourceId(value: string): void;
	getCafeRequestId(): string;
	/**
	 * @param value 
	 */
	setCafeRequestId(value: string): void;
	getLease(): vCACCAFEResourceLease;
	/**
	 * @param value 
	 */
	setLease(value: vCACCAFEResourceLease): void;
	getCosts(): vCACCAFEResourceCosts;
	/**
	 * @param value 
	 */
	setCosts(value: vCACCAFEResourceCosts): void;
	getArchiveDays(): number;
	/**
	 * @param value 
	 */
	setArchiveDays(value: number): void;
	getResourceTypeId(): string;
	/**
	 * @param value 
	 */
	setResourceTypeId(value: string): void;
	getCafeResourceId(): string;
	/**
	 * @param value 
	 */
	setCafeResourceId(value: string): void;
	getIconId(): string;
	/**
	 * @param value 
	 */
	setIconId(value: string): void;
	/**
	 * @param value 
	 */
	setData(value: vCACCAFELiteralMap): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
}

declare class vCACCAFEProviderResourceAction {
	constructor();
	constructor();
	/**
	 * @param value 
	 */
	setOrganization(value: vCACCAFECatalogOrganizationReference): void;
	getOrganization(): vCACCAFECatalogOrganizationReference;
	getForms(): vCACCAFEResourceActionForms;
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	getProviderTypeId(): string;
	/**
	 * @param value 
	 */
	setProviderTypeId(value: string): void;
	getRequestSchema(): vCACCAFESchemaInfo;
	/**
	 * @param value 
	 */
	setRequestSchema(value: vCACCAFESchemaInfo): void;
	getAppearsAfterOperationId(): string;
	/**
	 * @param value 
	 */
	setAppearsAfterOperationId(value: string): void;
	getLifecycleAction(): string;
	/**
	 * @param value 
	 */
	setLifecycleAction(value: string): void;
	isEntitleable(): boolean;
	/**
	 * @param value 
	 */
	setEntitleable(value: boolean): void;
	isDeleted(): boolean;
	/**
	 * @param value 
	 */
	setDeleted(value: boolean): void;
	getTargetResourceTypeId(): string;
	/**
	 * @param value 
	 */
	setTargetResourceTypeId(value: string): void;
	/**
	 * @param value 
	 */
	setForms(value: vCACCAFEResourceActionForms): void;
	getTargetCriteria(): any;
	/**
	 * @param value 
	 */
	setTargetCriteria(value: any): void;
	getCallbacks(): vCACCAFEResourceActionCallbackSupport;
	/**
	 * @param value 
	 */
	setCallbacks(value: vCACCAFEResourceActionCallbackSupport): void;
	getIconId(): string;
	/**
	 * @param value 
	 */
	setIconId(value: string): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
}

declare interface vCACCAFEProviderResourceLeaseState {
	value(): string;
	values(): vCACCAFEProviderResourceLeaseState[];
	/**
	 * @param name 
	 */
	valueOf(name: string): vCACCAFEProviderResourceLeaseState;
	/**
	 * @param v 
	 */
	fromValue(v: string): vCACCAFEProviderResourceLeaseState;
}

declare interface vCACCAFEProviderRole {
	value(): string;
	values(): vCACCAFEProviderRole[];
	/**
	 * @param name 
	 */
	valueOf(name: string): vCACCAFEProviderRole;
	/**
	 * @param v 
	 */
	fromValue(v: string): vCACCAFEProviderRole;
}

declare class vCACCAFEProviderRollbackRequest {
	constructor();
	constructor();
	getResourceIds(): string[];
}

declare class vCACCAFEProviderType {
	constructor();
	constructor();
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setId(value: string): void;
}

declare interface vCACCAFEProvisionSourceType {
	value(): string;
	values(): vCACCAFEProvisionSourceType[];
	/**
	 * @param name 
	 */
	valueOf(name: string): vCACCAFEProvisionSourceType;
	/**
	 * @param v 
	 */
	fromValue(v: string): vCACCAFEProvisionSourceType;
}

declare interface vCACCAFEPublishStatus {
	value(): string;
	values(): vCACCAFEPublishStatus[];
	/**
	 * @param name 
	 */
	valueOf(name: string): vCACCAFEPublishStatus;
	/**
	 * @param v 
	 */
	fromValue(v: string): vCACCAFEPublishStatus;
}

declare class vCACCAFEReadOnlyConstraint {
	constructor();
	/**
	 * @param isReadOnly 
	 */
	constructor(isReadOnly: boolean);
	/**
	 * @param value 
	 */
	constructor(value: com.vmware.vcac.platform.content.evaluators.Evaluator);
	/**
	 * @param value 
	 */
	cloneWithValue(value: any): vCACCAFEReadOnlyConstraint;
	/**
	 * @param isReadOnly 
	 */
	fromBoolean(isReadOnly: boolean): vCACCAFEReadOnlyConstraint;
	/**
	 * @param constraint 
	 */
	mergeConstraint(constraint: any): any;
	clone(): any;
	getValue(): any;
}

declare class vCACCAFERecipientDeliveryStatus {
	constructor();
	constructor();
	getMessage(): string;
	getId(): string;
	getStatus(): vCACCAFEDeliveryStatus;
	/**
	 * @param value 
	 */
	setRecipientId(value: string): void;
	getRecipientId(): string;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	/**
	 * @param value 
	 */
	setStatus(value: vCACCAFEDeliveryStatus): void;
	/**
	 * @param value 
	 */
	setMessage(value: string): void;
}

declare class vCACCAFERefreshOnChangeBehavior {
	constructor();
	/**
	 * @param isEnabled 
	 */
	constructor(isEnabled: boolean);
	/**
	 * @param roc 
	 */
	isEnabled(roc: any): boolean;
	/**
	 * @param value 
	 */
	cloneWithValue(value: any): vCACCAFERefreshOnChangeBehavior;
	/**
	 * @param constraint 
	 */
	mergeConstraint(constraint: any): any;
	clone(): any;
	getValue(): any;
}

declare class vCACCAFERegexpConstraint {
	constructor();
	/**
	 * @param value 
	 */
	constructor(value: com.vmware.vcac.platform.content.evaluators.Evaluator);
	/**
	 * @param value 
	 */
	constructor(value: string);
	/**
	 * @param value 
	 * @param errorMessage 
	 */
	constructor(value: string, errorMessage: string);
	/**
	 * @param value 
	 * @param errorMessage 
	 */
	constructor(value: com.vmware.vcac.platform.content.evaluators.Evaluator, errorMessage: string);
	/**
	 * @param value 
	 */
	cloneWithValue(value: any): vCACCAFERegexpConstraint;
	/**
	 * @param constraint 
	 */
	mergeConstraint(constraint: any): any;
	getErrorMessage(): string;
	clone(): any;
	getValue(): any;
}

declare class vCACCAFERegisterNodeRequest {
	constructor();
	constructor();
	/**
	 * @param value 
	 */
	setQueueName(value: string): void;
	getQueueName(): string;
	getPassPhrase(): string;
	/**
	 * @param value 
	 */
	setPassPhrase(value: string): void;
}

declare class vCACCAFERegisterNodeResponse {
	constructor();
	constructor();
	getCertificateChain(): string[];
	getCertificate(): any[];
	getSuiteToken(): string;
	/**
	 * @param value 
	 */
	setSuiteToken(value: string): void;
	getPrivateKey(): any[];
}

declare interface vCACCAFERegistrationState {
	value(): string;
	values(): vCACCAFERegistrationState[];
	/**
	 * @param name 
	 */
	valueOf(name: string): vCACCAFERegistrationState;
	/**
	 * @param v 
	 */
	fromValue(v: string): vCACCAFERegistrationState;
}

declare class vCACCAFERelyingParty {
	constructor();
	constructor();
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getSigningCertificate(): string;
	/**
	 * @param value 
	 */
	setSigningCertificate(value: string): void;
	getServiceId(): string;
	/**
	 * @param value 
	 */
	setServiceId(value: string): void;
}

declare class vCACCAFEReplyEvent {
	constructor();
	constructor();
	toString(): string;
	/**
	 * @param value 
	 */
	setError(value: boolean): void;
	isError(): boolean;
	getOriginEventId(): string;
	/**
	 * @param value 
	 */
	setOriginEventId(value: string): void;
	getData(): vCACCAFELiteralMap;
	getHeaders(): vCACCAFELiteralMap;
	getId(): string;
	/**
	 * @param headers 
	 */
	setHeaders(headers: vCACCAFELiteralMap): void;
	getTimeStamp(): any;
	getEventTopicId(): string;
	getCorrelationId(): string;
	getSourceType(): string;
	getSourceIdentity(): string;
	getTraceId(): string;
	getCorrelationType(): string;
	/**
	 * @param value 
	 */
	setCorrelationId(value: string): void;
	/**
	 * @param value 
	 */
	setSourceType(value: string): void;
	/**
	 * @param value 
	 */
	setSourceIdentity(value: string): void;
	/**
	 * @param value 
	 */
	setTimeStamp(value: any): void;
	/**
	 * @param value 
	 */
	setEventTopicId(value: string): void;
	/**
	 * @param value 
	 */
	setData(value: vCACCAFELiteralMap): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
}

declare interface vCACCAFERequestApprovalStatus {
	value(): string;
	values(): vCACCAFERequestApprovalStatus[];
	/**
	 * @param name 
	 */
	valueOf(name: string): vCACCAFERequestApprovalStatus;
	/**
	 * @param v 
	 */
	fromValue(v: string): vCACCAFERequestApprovalStatus;
}

declare class vCACCAFERequestCompletion {
	constructor();
	constructor();
	toString(): string;
	getRequestCompletionState(): vCACCAFERequestCompletionState;
	/**
	 * @param value 
	 */
	setRequestCompletionState(value: vCACCAFERequestCompletionState): void;
	getResourceBindingIds(): string[];
	getCompletionDetails(): string;
	/**
	 * @param value 
	 */
	setCompletionDetails(value: string): void;
}

declare interface vCACCAFERequestCompletionState {
	value(): string;
	values(): vCACCAFERequestCompletionState[];
	/**
	 * @param name 
	 */
	valueOf(name: string): vCACCAFERequestCompletionState;
	/**
	 * @param v 
	 */
	fromValue(v: string): vCACCAFERequestCompletionState;
}

declare class vCACCAFERequestComponent {
	constructor();
	constructor();
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	getRequestType(): vCACCAFERequestType;
	/**
	 * @param value 
	 */
	setRequestType(value: vCACCAFERequestType): void;
	getQuote(): vCACCAFERequestQuote;
	/**
	 * @param value 
	 */
	setQuote(value: vCACCAFERequestQuote): void;
	getRequestableItemBindingId(): string;
	/**
	 * @param value 
	 */
	setRequestableItemBindingId(value: string): void;
	getClassId(): string;
	/**
	 * @param value 
	 */
	setClassId(value: string): void;
	getTypeFilter(): string;
	/**
	 * @param value 
	 */
	setTypeFilter(value: string): void;
	getProviderId(): string;
	/**
	 * @param value 
	 */
	setProviderId(value: string): void;
	getComponentId(): string;
	/**
	 * @param value 
	 */
	setComponentId(value: string): void;
	getCafeResourceId(): string;
	/**
	 * @param value 
	 */
	setCafeResourceId(value: string): void;
	/**
	 * @param value 
	 */
	setServiceTypeId(value: string): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	getParentId(): string;
	/**
	 * @param value 
	 */
	setParentId(value: string): void;
	getServiceTypeId(): string;
}

declare class vCACCAFERequestEvent {
	constructor();
	constructor();
	getDate(): any;
	/**
	 * @param value 
	 */
	setDate(value: any): void;
	getRequest(): any;
	getId(): string;
	getState(): vCACCAFERequestState;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	/**
	 * @param value 
	 */
	setState(value: vCACCAFERequestState): void;
	getSeverity(): vCACCAFERequestEventSeverity;
	/**
	 * @param value 
	 */
	setRequest(value: any): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	getDetails(): vCACCAFERequestEventDetail[];
	/**
	 * @param value 
	 */
	setSeverity(value: vCACCAFERequestEventSeverity): void;
}

declare class vCACCAFERequestEventDetail {
	constructor();
	constructor();
	getSource(): string;
	getSeverity(): vCACCAFERequestEventSeverity;
	getEventCode(): string;
	/**
	 * @param value 
	 */
	setEventCode(value: string): void;
	/**
	 * @param value 
	 */
	setSystemMessage(value: string): void;
	getUserMessage(): string;
	/**
	 * @param value 
	 */
	setUserMessage(value: string): void;
	/**
	 * @param value 
	 */
	setSource(value: string): void;
	getSystemMessage(): string;
	/**
	 * @param value 
	 */
	setSeverity(value: vCACCAFERequestEventSeverity): void;
}

declare interface vCACCAFERequestEventSeverity {
	value(): string;
	values(): vCACCAFERequestEventSeverity[];
	/**
	 * @param name 
	 */
	valueOf(name: string): vCACCAFERequestEventSeverity;
	/**
	 * @param v 
	 */
	fromValue(v: string): vCACCAFERequestEventSeverity;
}

declare interface vCACCAFERequestExecutionStatus {
	value(): string;
	values(): vCACCAFERequestExecutionStatus[];
	/**
	 * @param name 
	 */
	valueOf(name: string): vCACCAFERequestExecutionStatus;
	/**
	 * @param v 
	 */
	fromValue(v: string): vCACCAFERequestExecutionStatus;
}

declare interface vCACCAFERequestPhase {
	value(): string;
	values(): vCACCAFERequestPhase[];
	/**
	 * @param name 
	 */
	valueOf(name: string): vCACCAFERequestPhase;
	/**
	 * @param v 
	 */
	fromValue(v: string): vCACCAFERequestPhase;
}

declare class vCACCAFERequestQuote {
	constructor();
	constructor();
	toString(): string;
	getLeasePeriod(): vCACCAFETimeSpan;
	/**
	 * @param value 
	 */
	setLeasePeriod(value: vCACCAFETimeSpan): void;
	getLeaseRate(): vCACCAFEMoneyTimeRate;
	/**
	 * @param value 
	 */
	setLeaseRate(value: vCACCAFEMoneyTimeRate): void;
	getTotalLeaseCost(): any;
	/**
	 * @param value 
	 */
	setTotalLeaseCost(value: any): void;
}

declare interface vCACCAFERequestState {
	value(): string;
	values(): vCACCAFERequestState[];
	/**
	 * @param name 
	 */
	valueOf(name: string): vCACCAFERequestState;
	/**
	 * @param v 
	 */
	fromValue(v: string): vCACCAFERequestState;
}

declare interface vCACCAFERequestStateEnum {
	value(): string;
	values(): vCACCAFERequestStateEnum[];
	/**
	 * @param name 
	 */
	valueOf(name: string): vCACCAFERequestStateEnum;
	/**
	 * @param v 
	 */
	fromValue(v: string): vCACCAFERequestStateEnum;
}

declare interface vCACCAFERequestStatus {
	value(): string;
	values(): vCACCAFERequestStatus[];
	/**
	 * @param name 
	 */
	valueOf(name: string): vCACCAFERequestStatus;
	/**
	 * @param v 
	 */
	fromValue(v: string): vCACCAFERequestStatus;
}

declare interface vCACCAFERequestType {
	value(): string;
	values(): vCACCAFERequestType[];
	/**
	 * @param name 
	 */
	valueOf(name: string): vCACCAFERequestType;
	/**
	 * @param v 
	 */
	fromValue(v: string): vCACCAFERequestType;
}

declare interface vCACCAFERequestTypeEnum {
	value(): string;
	values(): vCACCAFERequestTypeEnum[];
	/**
	 * @param name 
	 */
	valueOf(name: string): vCACCAFERequestTypeEnum;
	/**
	 * @param v 
	 */
	fromValue(v: string): vCACCAFERequestTypeEnum;
}

declare interface vCACCAFERequestWaitingStatus {
	value(): string;
	values(): vCACCAFERequestWaitingStatus[];
	/**
	 * @param name 
	 */
	valueOf(name: string): vCACCAFERequestWaitingStatus;
	/**
	 * @param v 
	 */
	fromValue(v: string): vCACCAFERequestWaitingStatus;
}

declare class vCACCAFERequestedItem {
	constructor();
	constructor();
	getContext(): any;
	/**
	 * @param value 
	 */
	setContext(value: any): void;
	getPolicyId(): string;
	/**
	 * @param value 
	 */
	setPolicyId(value: string): void;
	getPhaseId(): string;
	/**
	 * @param value 
	 */
	setPhaseId(value: string): void;
	getRequestClassId(): string;
	/**
	 * @param value 
	 */
	setRequestClassId(value: string): void;
	getRequestInstanceId(): string;
	/**
	 * @param value 
	 */
	setRequestInstanceId(value: string): void;
	getRequestedItemName(): string;
	/**
	 * @param value 
	 */
	setRequestedItemName(value: string): void;
	getRequestedItemDescription(): string;
	/**
	 * @param value 
	 */
	setRequestedItemDescription(value: string): void;
	getCosts(): vCACCAFEApprovalRequestCosts;
	/**
	 * @param value 
	 */
	setCosts(value: vCACCAFEApprovalRequestCosts): void;
}

declare class vCACCAFERequestedItemApproval {
	constructor();
	constructor();
	getLastModified(): any;
	getVersion(): number;
	getContext(): any;
	getId(): string;
	getState(): vCACCAFEEvaluationState;
	/**
	 * @param value 
	 */
	setLastModified(value: any): void;
	getPolicy(): vCACCAFEApprovalPolicy;
	/**
	 * @param value 
	 */
	setStartTime(value: any): void;
	getStartTime(): any;
	/**
	 * @param value 
	 */
	setVersion(value: number): void;
	/**
	 * @param value 
	 */
	setState(value: vCACCAFEEvaluationState): void;
	/**
	 * @param value 
	 */
	setContext(value: any): void;
	getPhaseDecision(): vCACCAFEPhaseDecision;
	/**
	 * @param value 
	 */
	setPhaseDecision(value: vCACCAFEPhaseDecision): void;
	getCompletionTime(): any;
	/**
	 * @param value 
	 */
	setCompletionTime(value: any): void;
	getPhaseId(): string;
	/**
	 * @param value 
	 */
	setPhaseId(value: string): void;
	getRequestClassId(): string;
	/**
	 * @param value 
	 */
	setRequestClassId(value: string): void;
	getRequestInstanceId(): string;
	/**
	 * @param value 
	 */
	setRequestInstanceId(value: string): void;
	getRequestedItemName(): string;
	/**
	 * @param value 
	 */
	setRequestedItemName(value: string): void;
	getRequestedItemDescription(): string;
	/**
	 * @param value 
	 */
	setRequestedItemDescription(value: string): void;
	getCosts(): vCACCAFEApprovalRequestCosts;
	/**
	 * @param value 
	 */
	setCosts(value: vCACCAFEApprovalRequestCosts): void;
	getPhaseNumber(): number;
	/**
	 * @param value 
	 */
	setPhaseNumber(value: number): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	/**
	 * @param value 
	 */
	setPolicy(value: vCACCAFEApprovalPolicy): void;
	/**
	 * @param value 
	 */
	setCreatedDate(value: any): void;
	getCreatedDate(): any;
}

declare class vCACCAFERequestedItemComponentMetadata {
	constructor();
	constructor();
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	getRequestType(): vCACCAFERequestType;
	/**
	 * @param value 
	 */
	setRequestType(value: vCACCAFERequestType): void;
	getQuote(): vCACCAFERequestQuote;
	/**
	 * @param value 
	 */
	setQuote(value: vCACCAFERequestQuote): void;
	getRequestableItemBindingId(): string;
	/**
	 * @param value 
	 */
	setRequestableItemBindingId(value: string): void;
	getClassId(): string;
	/**
	 * @param value 
	 */
	setClassId(value: string): void;
	getTypeFilter(): string;
	/**
	 * @param value 
	 */
	setTypeFilter(value: string): void;
	getProviderId(): string;
	/**
	 * @param value 
	 */
	setProviderId(value: string): void;
	getCafeResourceId(): string;
	/**
	 * @param value 
	 */
	setCafeResourceId(value: string): void;
	/**
	 * @param value 
	 */
	setServiceTypeId(value: string): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	getParentId(): string;
	/**
	 * @param value 
	 */
	setParentId(value: string): void;
	getServiceTypeId(): string;
}

declare class vCACCAFERequestedItemDataRequest {
	constructor();
	constructor();
	getDynamicDataRequest(): vCACCAFEDynamicDataRequest;
	/**
	 * @param value 
	 */
	setDynamicDataRequest(value: vCACCAFEDynamicDataRequest): void;
	getRequestData(): vCACCAFELiteralMap;
	/**
	 * @param value 
	 */
	setRequestData(value: vCACCAFELiteralMap): void;
}

declare class vCACCAFERequestedItemMetadata {
	constructor();
	constructor();
	getTotalQuote(): vCACCAFERequestQuote;
	/**
	 * @param value 
	 */
	setTotalQuote(value: vCACCAFERequestQuote): void;
	getLifecycleActionOnAllChildren(): string;
	/**
	 * @param value 
	 */
	setLifecycleActionOnAllChildren(value: string): void;
	getComponents(): vCACCAFERequestedItemComponentMetadata[];
}

declare class vCACCAFEReservation {
	version: number;
	enabled: boolean;
	name: string;
	priority: number;
	id: string;
	tenantId: string;
	alertPolicy: vCACCAFEAlertPolicy;
	reservationPolicyId: string;
	subTenantId: string;
	vcoId: any;
	reservationTypeId: string;
	lastUpdated: any;
	extensionData: vCACCAFELiteralMap;
	createdDate: any;
	constructor();
	constructor();
	getVersion(): number;
	isEnabled(): boolean;
	getName(): string;
	/**
	 * @param value 
	 */
	setPriority(value: number): void;
	getPriority(): number;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setVersion(value: number): void;
	getTenantId(): string;
	getAlertPolicy(): vCACCAFEAlertPolicy;
	/**
	 * @param value 
	 */
	setAlertPolicy(value: vCACCAFEAlertPolicy): void;
	getReservationPolicyId(): string;
	/**
	 * @param value 
	 */
	setReservationPolicyId(value: string): void;
	getSubTenantId(): string;
	/**
	 * @param value 
	 */
	setSubTenantId(value: string): void;
	getVcoId(): any;
	/**
	 * @param undefined 
	 */
	setVcoId(id: any): void;
	getReservationTypeId(): string;
	/**
	 * @param value 
	 */
	setReservationTypeId(value: string): void;
	/**
	 * @param value 
	 */
	setExtensionData(value: vCACCAFELiteralMap): void;
	getLastUpdated(): any;
	/**
	 * @param value 
	 */
	setLastUpdated(value: any): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	/**
	 * @param value 
	 */
	setEnabled(value: boolean): void;
	getExtensionData(): vCACCAFELiteralMap;
	/**
	 * @param value 
	 */
	setCreatedDate(value: any): void;
	/**
	 * @param value 
	 */
	setTenantId(value: string): void;
	getCreatedDate(): any;
}

declare interface vCACCAFEReservationClient {
	getReservationReservationPolicyTypeService(): vCACCAFEReservationReservationPolicyTypeService;
	getReservationReservationTypeCategoryService(): vCACCAFEReservationReservationTypeCategoryService;
	getReservationReservationTypeService(): vCACCAFEReservationReservationTypeService;
	getReservationReservationPolicyService(): vCACCAFEReservationReservationPolicyService;
	getReservationReservationService(): vCACCAFEReservationReservationService;
	/**
	 * Does a POST request expanding the given template variables in order.
	 * @param resourceUrl 
	 * @param object 
	 * @param urlVariables 
	 */
	postWithVariables(resourceUrl: string, object: any, urlVariables: string[]): vCACCAFEServiceResponse;
	/**
	 * Does a PUT request expanding the given template variables in order.
	 * @param resourceUrl 
	 * @param object 
	 * @param urlVariables 
	 */
	putWithVariables(resourceUrl: string, object: any, urlVariables: string[]): vCACCAFEServiceResponse;
	/**
	 * Does a DELETE request expanding the given template variables in order.
	 * @param resourceUrl 
	 * @param urlVariables 
	 */
	deleteWithVariables(resourceUrl: string, urlVariables: string[]): void;
	/**
	 * Gets the REST client URL.
	 */
	getUrl(): string;
	/**
	 * Indicates whether the current connection is already closed or not.
	 */
	isClosed(): boolean;
	/**
	 * Does a GET request for a file and allows specifying 'Accept' header.
	 * @param resourceUrl 
	 * @param acceptHeaderValue 
	 */
	getFileWithAcceptHeader(resourceUrl: string, acceptHeaderValue: string): any;
	/**
	 * Does a POST request with a file.
	 * @param resourceUrl 
	 * @param object 
	 */
	postFile(resourceUrl: string, object: any): vCACCAFEServiceResponse;
	/**
	 * Does a GET request expanding the given template variables in order.
	 * @param resourceUrl 
	 * @param urlVariables 
	 */
	getWithVariables(resourceUrl: string, urlVariables: string[]): any;
	/**
	 * Does a GET request.
	 * @param resourceUrl 
	 */
	get(resourceUrl: string): any;
	/**
	 * Does a PUT request.
	 * @param resourceUrl 
	 * @param object 
	 */
	put(resourceUrl: string, object: any): vCACCAFEServiceResponse;
	/**
	 * Does a DELETE request.
	 * @param resourceUrl 
	 */
	delete(resourceUrl: string): void;
	/**
	 * Invalidates the current connection.
	 */
	close(): void;
	/**
	 * Does a GET request for a file.
	 * @param resourceUrl 
	 */
	getFile(resourceUrl: string): any;
	/**
	 * Does a POST request.
	 * @param resourceUrl 
	 * @param object 
	 */
	post(resourceUrl: string, object: any): vCACCAFEServiceResponse;
}

declare class vCACCAFEReservationInfo {
	constructor();
	constructor();
	isEnabled(): boolean;
	getName(): string;
	/**
	 * @param value 
	 */
	setPriority(value: number): void;
	getPriority(): number;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	getTenantId(): string;
	getSubTenantRef(): vCACCAFELabelledReference;
	/**
	 * @param value 
	 */
	setSubTenantRef(value: vCACCAFELabelledReference): void;
	getAlertPolicy(): vCACCAFEAlertPolicy;
	/**
	 * @param value 
	 */
	setAlertPolicy(value: vCACCAFEAlertPolicy): void;
	getReservationPolicyRef(): vCACCAFELabelledReference;
	/**
	 * @param value 
	 */
	setReservationPolicyRef(value: vCACCAFELabelledReference): void;
	getReservationTypeRef(): vCACCAFELabelledReference;
	/**
	 * @param value 
	 */
	setReservationTypeRef(value: vCACCAFELabelledReference): void;
	/**
	 * @param value 
	 */
	setExtensionData(value: vCACCAFELiteralMap): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	/**
	 * @param value 
	 */
	setEnabled(value: boolean): void;
	getExtensionData(): vCACCAFELiteralMap;
	/**
	 * @param value 
	 */
	setTenantId(value: string): void;
}

declare class vCACCAFEReservationPolicy {
	version: number;
	name: string;
	id: string;
	description: string;
	reservationPolicyTypeId: string;
	vcoId: any;
	lastUpdated: any;
	createdDate: any;
	constructor();
	constructor();
	getVersion(): number;
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	/**
	 * @param value 
	 */
	setVersion(value: number): void;
	getReservationPolicyTypeId(): string;
	/**
	 * @param value 
	 */
	setReservationPolicyTypeId(value: string): void;
	getVcoId(): any;
	/**
	 * @param undefined 
	 */
	setVcoId(id: any): void;
	getLastUpdated(): any;
	/**
	 * @param value 
	 */
	setLastUpdated(value: any): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	/**
	 * @param value 
	 */
	setCreatedDate(value: any): void;
	getCreatedDate(): any;
}

declare class vCACCAFEReservationPolicyType {
	constructor();
	constructor();
	getVersion(): number;
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	/**
	 * @param value 
	 */
	setVersion(value: number): void;
	getTenantId(): string;
	/**
	 * @param value 
	 */
	setServiceTypeId(value: string): void;
	getLastUpdated(): any;
	/**
	 * @param value 
	 */
	setLastUpdated(value: any): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	getServiceTypeId(): string;
	/**
	 * @param value 
	 */
	setCreatedDate(value: any): void;
	/**
	 * @param value 
	 */
	setTenantId(value: string): void;
	getCreatedDate(): any;
}

declare interface vCACCAFEReservationReservationPolicyService {
	/**
	 * This method invokes the SDK method ReservationPolicyService.createReservationPolicy(ReservationPolicy reservationPolicy).
	 * @param reservationPolicy 
	 */
	createReservationPolicy(reservationPolicy: vCACCAFEReservationPolicy): string;
	/**
	 * This method invokes the SDK method ReservationPolicyService.updateReservationPolicy(ReservationPolicy reservationPolicy).
	 * @param reservationPolicy 
	 */
	updateReservationPolicy(reservationPolicy: vCACCAFEReservationPolicy): void;
	/**
	 * This method invokes the SDK method ReservationPolicyService.deleteReservationPolicy(String reservationPolicyId).
	 * @param reservationPolicyId 
	 */
	deleteReservationPolicy(reservationPolicyId: string): void;
	/**
	 * This method invokes the SDK method ReservationPolicyService.getAllReservationPolicies(Pageable pageable).
	 * @param pageable 
	 */
	getAllReservationPolicies(pageable: any): vCACCAFEPagedResources;
	/**
	 * This method invokes the SDK method ReservationPolicyService.getReservationPolicy(String reservationPolicyId).
	 * @param reservationPolicyId 
	 */
	getReservationPolicy(reservationPolicyId: string): vCACCAFEReservationPolicy;
}

declare interface vCACCAFEReservationReservationPolicyTypeService {
	/**
	 * This method invokes the SDK method ReservationPolicyTypeService.createReservationPolicyType(ReservationPolicyType reservationPolicyType).
	 * @param reservationPolicyType 
	 */
	createReservationPolicyType(reservationPolicyType: vCACCAFEReservationPolicyType): string;
	/**
	 * This method invokes the SDK method ReservationPolicyTypeService.deleteReservationPolicyType(String reservationPolicyTypeId).
	 * @param reservationPolicyTypeId 
	 */
	deleteReservationPolicyType(reservationPolicyTypeId: string): void;
	/**
	 * This method invokes the SDK method ReservationPolicyTypeService.getAllReservationPolicyTypes(Pageable pageable).
	 * @param pageable 
	 */
	getAllReservationPolicyTypes(pageable: any): vCACCAFEPagedResources;
	/**
	 * This method invokes the SDK method ReservationPolicyTypeService.getReservationPolicyType(String reservationPolicyTypeId).
	 * @param reservationPolicyTypeId 
	 */
	getReservationPolicyType(reservationPolicyTypeId: string): vCACCAFEReservationPolicyType;
	/**
	 * This method invokes the SDK method ReservationPolicyTypeService.updateReservationPolicyType(ReservationPolicyType reservationPolicyType).
	 * @param reservationPolicyType 
	 */
	updateReservationPolicyType(reservationPolicyType: vCACCAFEReservationPolicyType): void;
}

declare interface vCACCAFEReservationReservationService {
	/**
	 * This method invokes the SDK method ReservationService.getAllTenants(Pageable pageable).
	 * @param pageable 
	 */
	getAllTenants(pageable: any): vCACCAFEPagedResources;
	/**
	 * This method invokes the SDK method ReservationService.updateReservation(Reservation reservation).
	 * @param reservation 
	 */
	updateReservation(reservation: vCACCAFEReservation): void;
	/**
	 * This method invokes the SDK method ReservationService.createReservation(Reservation reservation).
	 * @param reservation 
	 */
	createReservation(reservation: vCACCAFEReservation): string;
	/**
	 * This method invokes the SDK method ReservationService.deleteReservation(String reservationId).
	 * @param reservationId 
	 */
	deleteReservation(reservationId: string): void;
	/**
	 * This method invokes the SDK method ReservationService.getAllReservationInfos(Pageable pageable).
	 * @param pageable 
	 */
	getAllReservationInfos(pageable: any): vCACCAFEPagedResources;
	/**
	 * This method invokes the SDK method ReservationService.getAllSubtenants(String tenantId, Pageable pageable).
	 * @param tenantId 
	 * @param pageable 
	 */
	getAllSubtenants(tenantId: string, pageable: any): vCACCAFEPagedResources;
	/**
	 * This method invokes the SDK method ReservationService.getSubtenant(String subtenantId, String tenantId).
	 * @param subtenantId 
	 * @param tenantId 
	 */
	getSubtenant(subtenantId: string, tenantId: string): vCACCAFESubtenant;
	/**
	 * This method invokes the SDK method ReservationService.getAllReservations(Pageable pageable).
	 * @param pageable 
	 */
	getAllReservations(pageable: any): vCACCAFEPagedResources;
	/**
	 * This method invokes the SDK method ReservationService.getReservation(String reservationId).
	 * @param reservationId 
	 */
	getReservation(reservationId: string): vCACCAFEReservation;
}

declare interface vCACCAFEReservationReservationTypeCategoryService {
	/**
	 * This method invokes the SDK method ReservationTypeCategoryService.getAllReservationTypeCategories(Pageable pageable).
	 * @param pageable 
	 */
	getAllReservationTypeCategories(pageable: any): vCACCAFEPagedResources;
	/**
	 * This method invokes the SDK method ReservationTypeCategoryService.getReservationTypeCategoryById(String reservationTypeCategoryId).
	 * @param reservationTypeCategoryId 
	 */
	getReservationTypeCategoryById(reservationTypeCategoryId: string): vCACCAFEReservationTypeCategory;
	/**
	 * This method invokes the SDK method ReservationTypeCategoryService.createOrUpdateReservationTypeCategory(ReservationTypeCategory reservationTypeCategory).
	 * @param reservationTypeCategory 
	 */
	createOrUpdateReservationTypeCategory(reservationTypeCategory: vCACCAFEReservationTypeCategory): void;
	/**
	 * This method invokes the SDK method ReservationTypeCategoryService.deleteReservationTypeCategoryById(String reservationTypeCategoryId).
	 * @param reservationTypeCategoryId 
	 */
	deleteReservationTypeCategoryById(reservationTypeCategoryId: string): void;
}

declare interface vCACCAFEReservationReservationTypeService {
	/**
	 * This method invokes the SDK method ReservationTypeService.createOrUpdateReservationType(ReservationType reservationType).
	 * @param reservationType 
	 */
	createOrUpdateReservationType(reservationType: vCACCAFEReservationType): void;
	/**
	 * This method invokes the SDK method ReservationTypeService.deleteReservationTypeById(String reservationTypeId).
	 * @param reservationTypeId 
	 */
	deleteReservationTypeById(reservationTypeId: string): void;
	/**
	 * This method invokes the SDK method ReservationTypeService.getAllReservationTypes(Pageable pageable).
	 * @param pageable 
	 */
	getAllReservationTypes(pageable: any): vCACCAFEPagedResources;
	/**
	 * This method invokes the SDK method ReservationTypeService.getReservationTypeById(String reservationTypeId).
	 * @param reservationTypeId 
	 */
	getReservationTypeById(reservationTypeId: string): vCACCAFEReservationType;
}

declare class vCACCAFEReservationType {
	constructor();
	constructor();
	getVersion(): number;
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	/**
	 * @param value 
	 */
	setVersion(value: number): void;
	getTenantId(): string;
	getAlertTypes(): vCACCAFEAlertType[];
	getSchemaClassId(): string;
	/**
	 * @param value 
	 */
	setSchemaClassId(value: string): void;
	getFormReference(): any;
	/**
	 * @param value 
	 */
	setFormReference(value: any): void;
	/**
	 * @param value 
	 */
	setServiceTypeId(value: string): void;
	getLastUpdated(): any;
	/**
	 * @param value 
	 */
	setLastUpdated(value: any): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	/**
	 * @param value 
	 */
	setCategory(value: string): void;
	getCategory(): string;
	getServiceTypeId(): string;
	/**
	 * @param value 
	 */
	setCreatedDate(value: any): void;
	/**
	 * @param value 
	 */
	setTenantId(value: string): void;
	getCreatedDate(): any;
}

declare class vCACCAFEReservationTypeCategory {
	constructor();
	constructor();
	getVersion(): number;
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	/**
	 * @param value 
	 */
	setVersion(value: number): void;
	/**
	 * @param value 
	 */
	setParentCategoryId(value: string): void;
	getParentCategoryId(): string;
	getListView(): vCACCAFETableView;
	/**
	 * @param value 
	 */
	setListView(value: vCACCAFETableView): void;
	/**
	 * @param value 
	 */
	setServiceTypeId(value: string): void;
	getLastUpdated(): any;
	/**
	 * @param value 
	 */
	setLastUpdated(value: any): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	getServiceTypeId(): string;
	/**
	 * @param value 
	 */
	setCreatedDate(value: any): void;
	getCreatedDate(): any;
}

declare interface vCACCAFEReservedComponentField {
	value(): string;
	values(): vCACCAFEReservedComponentField[];
	/**
	 * @param name 
	 */
	valueOf(name: string): vCACCAFEReservedComponentField;
	/**
	 * @param v 
	 */
	fromValue(v: string): vCACCAFEReservedComponentField;
}

declare class vCACCAFEResetValueBehavior {
	constructor();
	/**
	 * @param value 
	 */
	constructor(value: com.vmware.vcac.platform.content.evaluators.Evaluator);
	/**
	 * @param value 
	 */
	constructor(value: com.vmware.vcac.platform.content.literals.Literal);
	/**
	 * @param value 
	 */
	cloneWithValue(value: any): any;
	/**
	 * @param constraint 
	 */
	mergeConstraint(constraint: any): vCACCAFEResetValueBehavior;
	clone(): any;
	getValue(): any;
}

declare interface vCACCAFEResolutionMode {
	value(): string;
	values(): vCACCAFEResolutionMode[];
	/**
	 * @param name 
	 */
	valueOf(name: string): vCACCAFEResolutionMode;
	/**
	 * @param v 
	 */
	fromValue(v: string): vCACCAFEResolutionMode;
}

declare class vCACCAFEResolvedLinks {
	constructor();
	constructor();
	getHrefs(): string[];
}

declare class vCACCAFEResource {
	constructor();
	/**
	 * @param content 
	 * @param links 
	 */
	constructor(content: any, links: java.lang.Iterable);
	/**
	 * @param content 
	 * @param links 
	 */
	constructor(content: any, links: vCACCAFELink[]);
	/**
	 * @param content 
	 */
	setContent(content: any): void;
	getContent(): any;
}

declare class vCACCAFEResourceAction {
	name: string;
	id: string;
	description: string;
	status: vCACCAFEPublishStatus;
	targetResourceTypeRef: vCACCAFELabelledReference;
	vcoId: any;
	targetCriteria: any;
	iconId: string;
	externalId: string;
	organization: vCACCAFECatalogOrganizationReference;
	forms: vCACCAFEResourceActionForms;
	requestSchema: vCACCAFESchemaInfo;
	lifecycleAction: string;
	providerTypeRef: vCACCAFELabelledReference;
	bindingId: string;
	callbacks: vCACCAFEResourceActionCallbackSupport;
	constructor();
	constructor();
	/**
	 * @param value 
	 */
	setOrganization(value: vCACCAFECatalogOrganizationReference): void;
	getOrganization(): vCACCAFECatalogOrganizationReference;
	getForms(): vCACCAFEResourceActionForms;
	getRequestSchema(): vCACCAFESchemaInfo;
	/**
	 * @param value 
	 */
	setRequestSchema(value: vCACCAFESchemaInfo): void;
	getLifecycleAction(): string;
	/**
	 * @param value 
	 */
	setLifecycleAction(value: string): void;
	getProviderTypeRef(): vCACCAFELabelledReference;
	/**
	 * @param value 
	 */
	setProviderTypeRef(value: vCACCAFELabelledReference): void;
	/**
	 * @param value 
	 */
	setForms(value: vCACCAFEResourceActionForms): void;
	getBindingId(): string;
	/**
	 * @param value 
	 */
	setBindingId(value: string): void;
	getCallbacks(): vCACCAFEResourceActionCallbackSupport;
	/**
	 * @param value 
	 */
	setCallbacks(value: vCACCAFEResourceActionCallbackSupport): void;
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	getStatus(): vCACCAFEPublishStatus;
	getTargetResourceTypeRef(): vCACCAFELabelledReference;
	/**
	 * @param value 
	 */
	setTargetResourceTypeRef(value: vCACCAFELabelledReference): void;
	getVcoId(): any;
	/**
	 * @param undefined 
	 */
	setVcoId(id: any): void;
	getTargetCriteria(): any;
	/**
	 * @param value 
	 */
	setTargetCriteria(value: any): void;
	getIconId(): string;
	/**
	 * @param value 
	 */
	setIconId(value: string): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	/**
	 * @param value 
	 */
	setStatus(value: vCACCAFEPublishStatus): void;
	getExternalId(): string;
	/**
	 * @param value 
	 */
	setExternalId(value: string): void;
}

declare class vCACCAFEResourceActionCallbackSupport {
	constructor();
	constructor();
	isItemInformation(): boolean;
	/**
	 * @param value 
	 */
	setItemInformation(value: boolean): void;
	isItemInitialize(): boolean;
	/**
	 * @param value 
	 */
	setItemInitialize(value: boolean): void;
	isRollback(): boolean;
	/**
	 * @param value 
	 */
	setRollback(value: boolean): void;
	isValidate(): boolean;
	/**
	 * @param value 
	 */
	setValidate(value: boolean): void;
}

declare class vCACCAFEResourceActionForms {
	constructor();
	constructor();
	getRequestPostApproval(): any;
	/**
	 * @param value 
	 */
	setRequestPostApproval(value: any): void;
	getRequestPreApproval(): any;
	/**
	 * @param value 
	 */
	setRequestPreApproval(value: any): void;
	getRequestSubmission(): any;
	/**
	 * @param value 
	 */
	setRequestSubmission(value: any): void;
	getRequestFormScale(): vCACCAFEFormScale;
	/**
	 * @param value 
	 */
	setRequestFormScale(value: vCACCAFEFormScale): void;
	isCatalogRequestInfoHidden(): boolean;
	/**
	 * @param value 
	 */
	setCatalogRequestInfoHidden(value: boolean): void;
	getRequestDetails(): any;
	/**
	 * @param value 
	 */
	setRequestDetails(value: any): void;
}

declare class vCACCAFEResourceActionRequest {
	version: number;
	organization: vCACCAFECatalogOrganizationReference;
	name: string;
	id: string;
	state: vCACCAFERequestState;
	description: string;
	dateCreated: any;
	dateApproved: any;
	quote: vCACCAFERequestQuote;
	requestCompletion: vCACCAFERequestCompletion;
	requestorEntitlementId: string;
	executionStatus: vCACCAFERequestExecutionStatus;
	waitingStatus: vCACCAFERequestWaitingStatus;
	stateName: string;
	requestedItemName: string;
	requestedItemDescription: string;
	requestedFor: string;
	vcoId: any;
	requestedBy: string;
	dateCompleted: any;
	reasons: string;
	requestNumber: number;
	requestData: vCACCAFELiteralMap;
	approvalStatus: vCACCAFERequestApprovalStatus;
	preApprovalId: string;
	postApprovalId: string;
	retriesRemaining: number;
	iconId: string;
	dateSubmitted: any;
	lastUpdated: any;
	phase: vCACCAFERequestPhase;
	resourceRef: vCACCAFELabelledReference;
	resourceActionRef: vCACCAFELabelledReference;
	constructor();
	constructor();
	getResourceRef(): vCACCAFELabelledReference;
	getResourceActionRef(): vCACCAFELabelledReference;
	/**
	 * @param value 
	 */
	setResourceRef(value: vCACCAFELabelledReference): void;
	/**
	 * @param value 
	 */
	setResourceActionRef(value: vCACCAFELabelledReference): void;
	getVersion(): number;
	/**
	 * @param value 
	 */
	setOrganization(value: vCACCAFECatalogOrganizationReference): void;
	getOrganization(): vCACCAFECatalogOrganizationReference;
	/**
	 * @param phase 
	 */
	setPhase(phase: vCACCAFERequestPhase): void;
	getName(): string;
	getId(): string;
	getState(): vCACCAFERequestState;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	/**
	 * @param value 
	 */
	setState(value: vCACCAFERequestState): void;
	getDateCreated(): any;
	/**
	 * @param value 
	 */
	setDateCreated(value: any): void;
	getDateApproved(): any;
	/**
	 * @param value 
	 */
	setDateApproved(value: any): void;
	getQuote(): vCACCAFERequestQuote;
	/**
	 * @param quote 
	 */
	setQuote(quote: vCACCAFERequestQuote): void;
	getRequestCompletion(): vCACCAFERequestCompletion;
	/**
	 * @param value 
	 */
	setRequestCompletion(value: vCACCAFERequestCompletion): void;
	getRequestorEntitlementId(): string;
	/**
	 * @param value 
	 */
	setRequestorEntitlementId(value: string): void;
	getExecutionStatus(): vCACCAFERequestExecutionStatus;
	/**
	 * @param executionStatus 
	 */
	setExecutionStatus(executionStatus: vCACCAFERequestExecutionStatus): void;
	getWaitingStatus(): vCACCAFERequestWaitingStatus;
	/**
	 * @param waitingStatus 
	 */
	setWaitingStatus(waitingStatus: vCACCAFERequestWaitingStatus): void;
	getStateName(): string;
	/**
	 * @param value 
	 */
	setStateName(value: string): void;
	getRequestedItemName(): string;
	/**
	 * @param requestedItemName 
	 */
	setRequestedItemName(requestedItemName: string): void;
	getRequestedItemDescription(): string;
	/**
	 * @param requestedItemDescription 
	 */
	setRequestedItemDescription(requestedItemDescription: string): void;
	getRequestedFor(): string;
	getVcoId(): any;
	/**
	 * @param undefined 
	 */
	setVcoId(id: any): void;
	getRequestedBy(): string;
	getDateCompleted(): any;
	/**
	 * @param value 
	 */
	setDateCompleted(value: any): void;
	getReasons(): string;
	getRequestNumber(): number;
	getRequestData(): vCACCAFELiteralMap;
	getApprovalStatus(): vCACCAFERequestApprovalStatus;
	/**
	 * @param approvalStatus 
	 */
	setApprovalStatus(approvalStatus: vCACCAFERequestApprovalStatus): void;
	getPreApprovalId(): string;
	/**
	 * @param preApprovalId 
	 */
	setPreApprovalId(preApprovalId: string): void;
	getPostApprovalId(): string;
	/**
	 * @param postApprovalId 
	 */
	setPostApprovalId(postApprovalId: string): void;
	getRetriesRemaining(): number;
	getIconId(): string;
	/**
	 * @param iconId 
	 */
	setIconId(iconId: string): void;
	getDateSubmitted(): any;
	/**
	 * @param value 
	 */
	setDateSubmitted(value: any): void;
	/**
	 * @param value 
	 */
	setReasons(value: string): void;
	/**
	 * @param value 
	 */
	setRequestedBy(value: string): void;
	/**
	 * @param value 
	 */
	setRequestedFor(value: string): void;
	/**
	 * @param value 
	 */
	setRequestData(value: vCACCAFELiteralMap): void;
	getLastUpdated(): any;
	/**
	 * @param value 
	 */
	setLastUpdated(value: any): void;
	getPhase(): vCACCAFERequestPhase;
	/**
	 * @param value 
	 */
	setId(value: string): void;
}

declare class vCACCAFEResourceCallbackSupport {
	constructor();
	constructor();
	isList(): boolean;
	/**
	 * @param value 
	 */
	setList(value: boolean): void;
	isDetails(): boolean;
	/**
	 * @param value 
	 */
	setDetails(value: boolean): void;
}

declare class vCACCAFEResourceCostFeatures {
	constructor();
	constructor();
	isLease(): boolean;
	isLeaseCost(): boolean;
	/**
	 * @param value 
	 */
	setLeaseCost(value: boolean): void;
	/**
	 * @param value 
	 */
	setLease(value: boolean): void;
}

declare class vCACCAFEResourceCosts {
	constructor();
	constructor();
	toString(): string;
	getLeaseRate(): vCACCAFEMoneyTimeRate;
	/**
	 * @param value 
	 */
	setLeaseRate(value: vCACCAFEMoneyTimeRate): void;
}

declare class vCACCAFEResourceDetails {
	constructor();
	constructor();
	getResourceName(): string;
	getTenantId(): string;
	getProviderBindingId(): string;
	/**
	 * @param value 
	 */
	setProviderBindingId(value: string): void;
	getComponentTypeId(): string;
	/**
	 * @param value 
	 */
	setComponentTypeId(value: string): void;
	getSubTenantId(): string;
	/**
	 * @param value 
	 */
	setSubTenantId(value: string): void;
	getResourceId(): string;
	getDeploymentName(): string;
	/**
	 * @param value 
	 */
	setDeploymentName(value: string): void;
	/**
	 * @param value 
	 */
	setResourceId(value: string): void;
	/**
	 * @param value 
	 */
	setResourceName(value: string): void;
	getDeploymentId(): string;
	/**
	 * @param value 
	 */
	setDeploymentId(value: string): void;
	getBlueprintSnapshotId(): string;
	/**
	 * @param value 
	 */
	setBlueprintSnapshotId(value: string): void;
	getBlueprintComponentId(): string;
	/**
	 * @param value 
	 */
	setBlueprintComponentId(value: string): void;
	/**
	 * @param value 
	 */
	setBlueprintName(value: string): void;
	getBlueprintName(): string;
	getProviderId(): string;
	/**
	 * @param value 
	 */
	setProviderId(value: string): void;
	/**
	 * @param value 
	 */
	setBlueprintId(value: string): void;
	getCafeResourceId(): string;
	/**
	 * @param value 
	 */
	setCafeResourceId(value: string): void;
	getBlueprintId(): string;
	/**
	 * @param value 
	 */
	setTenantId(value: string): void;
}

declare class vCACCAFEResourceExtension {
	name: string;
	id: string;
	description: string;
	status: vCACCAFEPublishStatus;
	targetResourceTypeRef: vCACCAFELabelledReference;
	vcoId: any;
	targetCriteria: any;
	iconId: string;
	externalId: string;
	extensionId: string;
	constructor();
	constructor();
	getExtensionId(): string;
	/**
	 * @param value 
	 */
	setExtensionId(value: string): void;
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	getStatus(): vCACCAFEPublishStatus;
	getTargetResourceTypeRef(): vCACCAFELabelledReference;
	/**
	 * @param value 
	 */
	setTargetResourceTypeRef(value: vCACCAFELabelledReference): void;
	getVcoId(): any;
	/**
	 * @param undefined 
	 */
	setVcoId(id: any): void;
	getTargetCriteria(): any;
	/**
	 * @param value 
	 */
	setTargetCriteria(value: any): void;
	getIconId(): string;
	/**
	 * @param value 
	 */
	setIconId(value: string): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	/**
	 * @param value 
	 */
	setStatus(value: vCACCAFEPublishStatus): void;
	getExternalId(): string;
	/**
	 * @param value 
	 */
	setExternalId(value: string): void;
}

declare class vCACCAFEResourceForms {
	constructor();
	constructor();
	toString(): string;
	isCatalogResourceInfoHidden(): boolean;
	/**
	 * @param value 
	 */
	setCatalogResourceInfoHidden(value: boolean): void;
	getRequestFormScale(): vCACCAFEFormScale;
	/**
	 * @param value 
	 */
	setRequestFormScale(value: vCACCAFEFormScale): void;
	isCatalogRequestInfoHidden(): boolean;
	/**
	 * @param value 
	 */
	setCatalogRequestInfoHidden(value: boolean): void;
	getDetails(): any;
	/**
	 * @param value 
	 */
	setDetails(value: any): void;
}

declare class vCACCAFEResourceInfo {
	constructor();
	constructor();
	getType(): string;
	/**
	 * @param value 
	 */
	setType(value: string): void;
	getTenantId(): string;
	getMachineId(): string;
	/**
	 * @param value 
	 */
	setMachineId(value: string): void;
	/**
	 * @param value 
	 */
	setTenantId(value: string): void;
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	/**
	 * @param value 
	 */
	setExtensionData(value: vCACCAFELiteralMap): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	getExtensionData(): vCACCAFELiteralMap;
}

declare class vCACCAFEResourceLease {
	constructor();
	constructor();
	getEnd(): any;
	toString(): string;
	getStart(): any;
	/**
	 * @param value 
	 */
	setStart(value: any): void;
	/**
	 * @param value 
	 */
	setEnd(value: any): void;
}

declare class vCACCAFEResourceOperationComparator {
	constructor();
	constructor();
}

declare interface vCACCAFEResourceOperationType {
	value(): string;
	values(): vCACCAFEResourceOperationType[];
	/**
	 * @param name 
	 */
	valueOf(name: string): vCACCAFEResourceOperationType;
	/**
	 * @param v 
	 */
	fromValue(v: string): vCACCAFEResourceOperationType;
}

declare class vCACCAFEResourceOperationUpdateRequest {
	constructor();
	constructor();
	getPublishStatus(): vCACCAFEPublishStatus;
	/**
	 * @param value 
	 */
	setPublishStatus(value: vCACCAFEPublishStatus): void;
	getIconId(): string;
	/**
	 * @param value 
	 */
	setIconId(value: string): void;
}

declare class vCACCAFEResourcePriceUpdates {
	constructor();
	constructor();
	getTimestamp(): number;
	/**
	 * @param value 
	 */
	setTimestamp(value: number): void;
}

declare class vCACCAFEResourceRef {
	constructor();
	constructor();
	getId(): string;
	/**
	 * @param value 
	 */
	setLabel(value: string): void;
	getResourceTypeRef(): vCACCAFEResourceTypeRef;
	/**
	 * @param value 
	 */
	setResourceTypeRef(value: vCACCAFEResourceTypeRef): void;
	getTenant(): string;
	/**
	 * @param value 
	 */
	setTenant(value: string): void;
	getLabel(): string;
	/**
	 * @param value 
	 */
	setId(value: string): void;
}

declare interface vCACCAFEResourceStatus {
	value(): string;
	values(): vCACCAFEResourceStatus[];
	/**
	 * @param name 
	 */
	valueOf(name: string): vCACCAFEResourceStatus;
	/**
	 * @param v 
	 */
	fromValue(v: string): vCACCAFEResourceStatus;
}

declare class vCACCAFEResourceType {
	constructor();
	constructor();
	isPrimary(): boolean;
	/**
	 * @param value 
	 */
	setPrimary(value: boolean): void;
	/**
	 * @param value 
	 */
	setOrganization(value: vCACCAFECatalogOrganizationReference): void;
	getOrganization(): vCACCAFECatalogOrganizationReference;
	getForms(): vCACCAFEResourceForms;
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	getStatus(): vCACCAFEStatus;
	getCostFeatures(): vCACCAFEResourceCostFeatures;
	/**
	 * @param value 
	 */
	setCostFeatures(value: vCACCAFEResourceCostFeatures): void;
	getParentTypeId(): string;
	/**
	 * @param value 
	 */
	setParentTypeId(value: string): void;
	getProviderTypeId(): string;
	/**
	 * @param value 
	 */
	setProviderTypeId(value: string): void;
	isListDescendantTypesSeparately(): boolean;
	/**
	 * @param value 
	 */
	setListDescendantTypesSeparately(value: boolean): void;
	isShowChildrenOutsideParent(): boolean;
	/**
	 * @param value 
	 */
	setShowChildrenOutsideParent(value: boolean): void;
	isDeleteIfNoChildren(): boolean;
	/**
	 * @param value 
	 */
	setDeleteIfNoChildren(value: boolean): void;
	/**
	 * @param value 
	 */
	setForms(value: vCACCAFEResourceForms): void;
	getPluralizedName(): string;
	/**
	 * @param value 
	 */
	setPluralizedName(value: string): void;
	getListView(): vCACCAFETableView;
	/**
	 * @param value 
	 */
	setListView(value: vCACCAFETableView): void;
	getCallbacks(): vCACCAFEResourceCallbackSupport;
	/**
	 * @param value 
	 */
	setCallbacks(value: vCACCAFEResourceCallbackSupport): void;
	getIconId(): string;
	/**
	 * @param value 
	 */
	setIconId(value: string): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	/**
	 * @param value 
	 */
	setStatus(value: vCACCAFEStatus): void;
	getSchema(): vCACCAFESchemaInfo;
	/**
	 * @param value 
	 */
	setSchema(value: vCACCAFESchemaInfo): void;
}

declare class vCACCAFEResourceTypeNode {
	constructor();
	constructor();
	getChildren(): vCACCAFEResourceTypeNode[];
	getResourceType(): vCACCAFEResourceType;
	/**
	 * @param value 
	 */
	setResourceType(value: vCACCAFEResourceType): void;
}

declare class vCACCAFEResourceTypeRef {
	constructor();
	constructor();
	getId(): string;
	/**
	 * @param value 
	 */
	setLabel(value: string): void;
	getLabel(): string;
	/**
	 * @param value 
	 */
	setId(value: string): void;
}

declare class vCACCAFEResources {
	constructor();
	/**
	 * @param content 
	 * @param links 
	 */
	constructor(content: java.lang.Iterable, links: java.lang.Iterable);
	/**
	 * @param content 
	 * @param links 
	 */
	constructor(content: java.lang.Iterable, links: vCACCAFELink[]);
	getContent(): any;
}

/**
 * Rest client to communicate with vCAC CAFE services.
 */
declare interface vCACCAFERestClient {
	/**
	 * Does a POST request expanding the given template variables in order.
	 * @param resourceUrl 
	 * @param object 
	 * @param urlVariables 
	 */
	postWithVariables(resourceUrl: string, object: any, urlVariables: string[]): vCACCAFEServiceResponse;
	/**
	 * Does a PUT request expanding the given template variables in order.
	 * @param resourceUrl 
	 * @param object 
	 * @param urlVariables 
	 */
	putWithVariables(resourceUrl: string, object: any, urlVariables: string[]): vCACCAFEServiceResponse;
	/**
	 * Does a DELETE request expanding the given template variables in order.
	 * @param resourceUrl 
	 * @param urlVariables 
	 */
	deleteWithVariables(resourceUrl: string, urlVariables: string[]): void;
	/**
	 * Gets the REST client URL.
	 */
	getUrl(): string;
	/**
	 * Indicates whether the current connection is already closed or not.
	 */
	isClosed(): boolean;
	/**
	 * Does a GET request for a file and allows specifying 'Accept' header.
	 * @param resourceUrl 
	 * @param acceptHeaderValue 
	 */
	getFileWithAcceptHeader(resourceUrl: string, acceptHeaderValue: string): any;
	/**
	 * Does a POST request with a file.
	 * @param resourceUrl 
	 * @param object 
	 */
	postFile(resourceUrl: string, object: any): vCACCAFEServiceResponse;
	/**
	 * Does a GET request expanding the given template variables in order.
	 * @param resourceUrl 
	 * @param urlVariables 
	 */
	getWithVariables(resourceUrl: string, urlVariables: string[]): any;
	/**
	 * Does a GET request.
	 * @param resourceUrl 
	 */
	get(resourceUrl: string): any;
	/**
	 * Does a PUT request.
	 * @param resourceUrl 
	 * @param object 
	 */
	put(resourceUrl: string, object: any): vCACCAFEServiceResponse;
	/**
	 * Does a DELETE request.
	 * @param resourceUrl 
	 */
	delete(resourceUrl: string): void;
	/**
	 * Invalidates the current connection.
	 */
	close(): void;
	/**
	 * Does a GET request for a file.
	 * @param resourceUrl 
	 */
	getFile(resourceUrl: string): any;
	/**
	 * Does a POST request.
	 * @param resourceUrl 
	 * @param object 
	 */
	post(resourceUrl: string, object: any): vCACCAFEServiceResponse;
}

declare class vCACCAFERestError {
	constructor();
	/**
	 * @param error 
	 */
	constructor(error: com.vmware.vcac.platform.rest.data.RestErrorFields);
	/**
	 * @param code 
	 * @param source 
	 * @param message 
	 * @param systemMessage 
	 * @param moreInfoUrl 
	 */
	constructor(code: number, source: string, message: string, systemMessage: string, moreInfoUrl: string);
	/**
	 * @param code 
	 * @param message 
	 * @param systemMessage 
	 * @param moreInfoUrl 
	 */
	constructor(code: number, message: string, systemMessage: string, moreInfoUrl: string);
	getCode(): number;
	getMessage(): string;
	getSource(): string;
	getSystemMessage(): string;
	getMoreInfoUrl(): string;
}

declare class vCACCAFERestErrors {
	constructor();
	/**
	 * @param errors 
	 */
	constructor(errors: vCACCAFERestError[]);
	/**
	 * @param restError 
	 */
	constructor(restError: vCACCAFERestError);
	constructor();
	getErrors(): vCACCAFERestError[];
}

declare class vCACCAFERoleExtension {
	constructor();
	constructor();
	getAssignedPermissions(): vCACCAFEPermission[];
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	getStatus(): string;
	/**
	 * @param value 
	 */
	setRole(value: any): void;
	getRole(): any;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	/**
	 * @param value 
	 */
	setStatus(value: string): void;
}

declare interface vCACCAFERoleType {
	value(): string;
	values(): vCACCAFERoleType[];
	/**
	 * @param name 
	 */
	valueOf(name: string): vCACCAFERoleType;
	/**
	 * @param v 
	 */
	fromValue(v: string): vCACCAFERoleType;
}

declare class vCACCAFERoutineRunCorrelation {
	constructor();
	constructor();
	/**
	 * @param correlationType 
	 * @param correlationId 
	 */
	constructor(correlationType: string, correlationId: string);
	/**
	 * @param correlationType 
	 */
	setCorrelationType(correlationType: string): void;
	getCorrelationId(): string;
	getCorrelationType(): string;
	/**
	 * @param correlationId 
	 */
	setCorrelationId(correlationId: string): void;
}

declare class vCACCAFERoutineRunInfo {
	constructor();
	constructor();
	/**
	 * @param id 
	 * @param runStatus 
	 * @param routineId 
	 * @param executionId 
	 * @param traceId 
	 * @param correlationType 
	 * @param correlationId 
	 * @param tenant 
	 */
	constructor(id: java.util.UUID, runStatus: string, routineId: string, executionId: string, traceId: string, correlationType: string, correlationId: string, tenant: string);
	/**
	 * @param id 
	 * @param routineRunStatus 
	 * @param routineId 
	 * @param executionId 
	 * @param traceId 
	 * @param correlation 
	 * @param tenant 
	 */
	constructor(id: java.util.UUID, routineRunStatus: vCACCAFERoutineRunStatus, routineId: string, executionId: string, traceId: string, correlation: vCACCAFERoutineRunCorrelation, tenant: string);
	getExecutionId(): string;
	/**
	 * @param executionId 
	 */
	setExecutionId(executionId: string): void;
	getRoutineId(): string;
	/**
	 * @param routineId 
	 */
	setRoutineId(routineId: string): void;
	getRoutineRunStatus(): vCACCAFERoutineRunStatus;
	/**
	 * @param routineRunStatus 
	 */
	setRoutineRunStatus(routineRunStatus: vCACCAFERoutineRunStatus): void;
	/**
	 * @param traceId 
	 */
	setTraceId(traceId: string): void;
	getCorrelation(): vCACCAFERoutineRunCorrelation;
	/**
	 * @param correlation 
	 */
	setCorrelation(correlation: vCACCAFERoutineRunCorrelation): void;
	getId(): any;
	getTenantId(): string;
	getTraceId(): string;
	/**
	 * @param id 
	 */
	setId(id: any): void;
	/**
	 * @param tenantId 
	 */
	setTenantId(tenantId: string): void;
}

declare class vCACCAFERoutineRunRequest {
	constructor();
	constructor();
	getRequestHeader(): vCACCAFELiteralMap;
	/**
	 * @param requestHeader 
	 */
	setRequestHeader(requestHeader: vCACCAFELiteralMap): void;
	getCorrelation(): vCACCAFERoutineRunCorrelation;
	/**
	 * @param correlation 
	 */
	setCorrelation(correlation: vCACCAFERoutineRunCorrelation): void;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	getRequestedBy(): string;
	getCallbackServiceId(): string;
	/**
	 * @param callbackServiceId 
	 */
	setCallbackServiceId(callbackServiceId: string): void;
	getRequestData(): vCACCAFELiteralMap;
	/**
	 * @param value 
	 */
	setRequestedBy(value: string): void;
	/**
	 * @param requestData 
	 */
	setRequestData(requestData: vCACCAFELiteralMap): void;
}

declare class vCACCAFERoutineRunResult {
	constructor();
	constructor();
	getResultHeader(): vCACCAFELiteralMap;
	/**
	 * @param resultHeader 
	 */
	setResultHeader(resultHeader: vCACCAFELiteralMap): void;
	getResultData(): vCACCAFELiteralMap;
	/**
	 * @param resultData 
	 */
	setResultData(resultData: vCACCAFELiteralMap): void;
	getRunDetails(): string;
	/**
	 * @param routineDetails 
	 */
	setRunDetails(routineDetails: string): void;
	getRunStatus(): vCACCAFERoutineRunStatus;
	/**
	 * @param runStatus 
	 */
	setRunStatus(runStatus: vCACCAFERoutineRunStatus): void;
	getCreateDate(): any;
	/**
	 * @param createDate 
	 */
	setCreateDate(createDate: any): void;
	getCompleteDate(): any;
	/**
	 * @param completeDate 
	 */
	setCompleteDate(completeDate: any): void;
	isCompleted(): boolean;
	getCorrelation(): vCACCAFERoutineRunCorrelation;
	/**
	 * @param correlation 
	 */
	setCorrelation(correlation: vCACCAFERoutineRunCorrelation): void;
	getRequestedBy(): string;
	/**
	 * @param requestId 
	 */
	setRequestId(requestId: string): void;
	getRequestId(): string;
	/**
	 * @param requestedBy 
	 */
	setRequestedBy(requestedBy: string): void;
}

declare interface vCACCAFERoutineRunStatus {
	value(): string;
	values(): vCACCAFERoutineRunStatus[];
	/**
	 * @param name 
	 */
	valueOf(name: string): vCACCAFERoutineRunStatus;
	/**
	 * @param v 
	 */
	fromValue(v: string): vCACCAFERoutineRunStatus;
}

declare class vCACCAFESchema {
	constructor();
	/**
	 * @param fields 
	 */
	constructor(fields: com.vmware.vcac.platform.content.schema.Field[]);
	getFields(): any[];
	/**
	 * @param fieldId 
	 */
	getField(fieldId: string): any;
	getFieldIds(): string[];
	/**
	 * @param other 
	 */
	equalsForAttributes(other: vCACCAFESchema): boolean;
	/**
	 * @param fieldId 
	 */
	getFieldById(fieldId: string): any;
}

declare class vCACCAFESchemaInfo {
	constructor();
	constructor();
	getClassId(): string;
	/**
	 * @param value 
	 */
	setClassId(value: string): void;
	getTypeFilter(): string;
	/**
	 * @param value 
	 */
	setTypeFilter(value: string): void;
}

declare class vCACCAFEScope {
	constructor();
	constructor();
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	getStatus(): string;
	/**
	 * @param value 
	 */
	setScopeType(value: vCACCAFEScopeType): void;
	getPrincipalScopeRole(): vCACCAFEScopeRole[];
	getTenant(): string;
	/**
	 * @param value 
	 */
	setTenant(value: string): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	/**
	 * @param value 
	 */
	setStatus(value: string): void;
	getScopeType(): vCACCAFEScopeType;
}

declare class vCACCAFEScopeAuthoritiesEntry {
	constructor();
	constructor();
	/**
	 * @param value 
	 */
	setScope(value: string): void;
	getScope(): string;
	getAuthorities(): string[];
}

declare class vCACCAFEScopeAuthoritiesExtendedEntry {
	constructor();
	constructor();
	/**
	 * @param value 
	 */
	setScope(value: vCACCAFEScope): void;
	getPermissions(): vCACCAFEPermission[];
	getScope(): vCACCAFEScope;
	getRoles(): any[];
}

declare class vCACCAFEScopePrincipalsRef {
	constructor();
	constructor();
	getScopeId(): string;
	getPrincipals(): vCACCAFEPrincipalRef[];
	/**
	 * @param value 
	 */
	setScopeId(value: string): void;
}

declare class vCACCAFEScopeRole {
	constructor();
	constructor();
	/**
	 * @param value 
	 */
	setScopeType(value: vCACCAFEScopeType): void;
	getScopeType(): vCACCAFEScopeType;
	getAssignedPermissions(): vCACCAFEPermission[];
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	getStatus(): string;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	/**
	 * @param value 
	 */
	setStatus(value: string): void;
}

declare class vCACCAFEScopeRoleAssignment {
	constructor();
	constructor();
	getId(): string;
	/**
	 * @param value 
	 */
	setRole(value: vCACCAFEScopeRole): void;
	getPrincipal(): vCACCAFEPrincipalExtension;
	getRole(): vCACCAFEScopeRole;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	/**
	 * @param value 
	 */
	setPrincipal(value: vCACCAFEPrincipalExtension): void;
}

declare class vCACCAFEScopeType {
	constructor();
	constructor();
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	getStatus(): string;
	getPrereqAdminPermissions(): vCACCAFEPermission[];
	/**
	 * @param value 
	 */
	setId(value: string): void;
	/**
	 * @param value 
	 */
	setStatus(value: string): void;
}

declare class vCACCAFEScript {
	constructor();
	constructor();
	getScriptTypeId(): string;
	/**
	 * @param value 
	 */
	setScriptTypeId(value: string): void;
	getScriptContent(): string;
	/**
	 * @param value 
	 */
	setScriptContent(value: string): void;
	isIsRebootAfter(): boolean;
	/**
	 * @param value 
	 */
	setIsRebootAfter(value: boolean): void;
}

declare class vCACCAFEScriptAction {
	constructor();
	constructor();
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	getInputParameters(): vCACCAFEScriptActionParameter[];
	getResultType(): string;
	/**
	 * @param value 
	 */
	setResultType(value: string): void;
	getResultContentType(): any;
	/**
	 * @param value 
	 */
	setResultContentType(value: any): void;
	getFqn(): string;
	/**
	 * @param value 
	 */
	setFqn(value: string): void;
}

declare class vCACCAFEScriptActionCategory {
	constructor();
	constructor();
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
}

declare class vCACCAFEScriptActionParameter {
	constructor();
	constructor();
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getType(): string;
	/**
	 * @param value 
	 */
	setType(value: string): void;
}

declare class vCACCAFEScriptActionSummary {
	constructor();
	constructor();
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	getFqn(): string;
	/**
	 * @param value 
	 */
	setFqn(value: string): void;
}

declare class vCACCAFEScriptActionSupport {
	constructor();
	constructor();
	isSupported(): boolean;
	/**
	 * @param value 
	 */
	setSupported(value: boolean): void;
}

declare class vCACCAFEScriptActionValueDefinition {
	constructor();
	constructor();
	getScriptActionFqn(): string;
	/**
	 * @param value 
	 */
	setScriptActionFqn(value: string): void;
	/**
	 * @param value 
	 */
	setScope(value: vCACCAFEFormsScope): void;
	getName(): string;
	getKey(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	getScope(): vCACCAFEFormsScope;
	getDataType(): any;
	/**
	 * @param value 
	 */
	setKey(value: string): void;
	/**
	 * @param value 
	 */
	setDataType(value: any): void;
	/**
	 * @param value 
	 */
	setMulti(value: boolean): void;
	isMulti(): boolean;
	/**
	 * @param value 
	 */
	setId(value: string): void;
}

declare interface vCACCAFEScriptExecutionStatus {
	value(): string;
	values(): vCACCAFEScriptExecutionStatus[];
	/**
	 * @param name 
	 */
	valueOf(name: string): vCACCAFEScriptExecutionStatus;
	/**
	 * @param v 
	 */
	fromValue(v: string): vCACCAFEScriptExecutionStatus;
}

declare class vCACCAFEScriptProperty {
	constructor();
	constructor();
	getName(): string;
	getValue(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	/**
	 * @param value 
	 */
	setValue(value: string): void;
	getType(): vCACCAFEPropertyType;
	/**
	 * @param value 
	 */
	setType(value: vCACCAFEPropertyType): void;
	/**
	 * @param value 
	 */
	setSecured(value: boolean): void;
	isSecured(): boolean;
}

declare class vCACCAFEScriptType {
	constructor();
	constructor();
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getSupportedOsDetails(): vCACCAFEScriptTypeOsDetails[];
}

declare class vCACCAFEScriptTypeOsDetails {
	constructor();
	constructor();
	getOsFamily(): vCACCAFEOsFamilyInfo;
	/**
	 * @param value 
	 */
	setOsFamily(value: vCACCAFEOsFamilyInfo): void;
	/**
	 * @param value 
	 */
	setExecCommand(value: string): void;
	getExecScript(): string;
	/**
	 * @param value 
	 */
	setExecScript(value: string): void;
	getFileExtension(): string;
	getExecScriptExtension(): string;
	/**
	 * @param value 
	 */
	setExecScriptExtension(value: string): void;
	/**
	 * @param value 
	 */
	setFileExtension(value: string): void;
	isSupportsOutputProps(): boolean;
	/**
	 * @param value 
	 */
	setSupportsOutputProps(value: boolean): void;
	getExecCommand(): string;
}

declare class vCACCAFESecureStringLiteral {
	constructor();
	/**
	 * @param value 
	 */
	constructor(value: string);
	getTypeId(): vCACCAFEDataTypeId;
	isList(): boolean;
	/**
	 * @param other 
	 */
	compareTo(other: any): number;
	getValue(): string;
	/**
	 * @param bVal 
	 */
	fromBoolean(bVal: boolean): any;
	/**
	 * @param obj 
	 */
	safeEquals(obj: any): boolean;
	/**
	 * @param sVal 
	 */
	fromString(sVal: string): any;
	/**
	 * @param lVal 
	 */
	fromInt(lVal: number): any;
	/**
	 * @param dVal 
	 */
	fromDecimal(dVal: number): any;
}

declare class vCACCAFESecurityGroup {
	constructor();
	constructor();
	isInternal(): boolean;
	getMachineIdCollection(): string[];
	getIpAddressCollection(): string[];
	getSecurityGroupTypeId(): string;
	/**
	 * @param value 
	 */
	setSecurityGroupTypeId(value: string): void;
	/**
	 * @param value 
	 */
	setInternal(value: boolean): void;
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	/**
	 * @param value 
	 */
	setExtensionData(value: vCACCAFELiteralMap): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	getExtensionData(): vCACCAFELiteralMap;
	getExternalId(): string;
	/**
	 * @param value 
	 */
	setExternalId(value: string): void;
}

declare class vCACCAFESecurityGroupType {
	constructor();
	constructor();
	getVersion(): number;
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	/**
	 * @param value 
	 */
	setVersion(value: number): void;
	getAllocationRelatedResourceTypes(): string[];
	getFormReference(): any;
	/**
	 * @param value 
	 */
	setFormReference(value: any): void;
	/**
	 * @param value 
	 */
	setServiceTypeId(value: string): void;
	getLastUpdated(): any;
	/**
	 * @param value 
	 */
	setLastUpdated(value: any): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	getSchema(): vCACCAFENetworkObjectSchema[];
	getServiceTypeId(): string;
	/**
	 * @param value 
	 */
	setCreatedDate(value: any): void;
	getCreatedDate(): any;
}

declare class vCACCAFESecurityPolicy {
	constructor();
	constructor();
	isInternal(): boolean;
	/**
	 * @param value 
	 */
	setInternal(value: boolean): void;
	getSecurityPolicyTypeId(): string;
	/**
	 * @param value 
	 */
	setSecurityPolicyTypeId(value: string): void;
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	/**
	 * @param value 
	 */
	setExtensionData(value: vCACCAFELiteralMap): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	getExtensionData(): vCACCAFELiteralMap;
	getExternalId(): string;
	/**
	 * @param value 
	 */
	setExternalId(value: string): void;
}

declare class vCACCAFESecurityPolicyType {
	constructor();
	constructor();
	getVersion(): number;
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	/**
	 * @param value 
	 */
	setVersion(value: number): void;
	getAllocationRelatedResourceTypes(): string[];
	getFormReference(): any;
	/**
	 * @param value 
	 */
	setFormReference(value: any): void;
	/**
	 * @param value 
	 */
	setServiceTypeId(value: string): void;
	getLastUpdated(): any;
	/**
	 * @param value 
	 */
	setLastUpdated(value: any): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	getSchema(): vCACCAFENetworkObjectSchema[];
	getServiceTypeId(): string;
	/**
	 * @param value 
	 */
	setCreatedDate(value: any): void;
	getCreatedDate(): any;
}

declare class vCACCAFESecurityTag {
	constructor();
	constructor();
	getSecurityTagTypeId(): string;
	/**
	 * @param value 
	 */
	setSecurityTagTypeId(value: string): void;
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	/**
	 * @param value 
	 */
	setExtensionData(value: vCACCAFELiteralMap): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	getExtensionData(): vCACCAFELiteralMap;
	getExternalId(): string;
	/**
	 * @param value 
	 */
	setExternalId(value: string): void;
}

declare class vCACCAFESecurityTagType {
	constructor();
	constructor();
	getVersion(): number;
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	/**
	 * @param value 
	 */
	setVersion(value: number): void;
	getAllocationRelatedResourceTypes(): string[];
	getFormReference(): any;
	/**
	 * @param value 
	 */
	setFormReference(value: any): void;
	/**
	 * @param value 
	 */
	setServiceTypeId(value: string): void;
	getLastUpdated(): any;
	/**
	 * @param value 
	 */
	setLastUpdated(value: any): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	getSchema(): vCACCAFENetworkObjectSchema[];
	getServiceTypeId(): string;
	/**
	 * @param value 
	 */
	setCreatedDate(value: any): void;
	getCreatedDate(): any;
}

declare class vCACCAFESerialKeyInfo {
	constructor();
	constructor();
	getSerialKeys(): string[];
}

declare class vCACCAFESerialKeyLicenseInfo {
	constructor();
	constructor();
	getSerialKeys(): string[];
	getExpiration(): any;
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getRestrictions(): vCACCAFELicenseProductRestriction[];
	/**
	 * @param value 
	 */
	setExpiration(value: any): void;
}

declare class vCACCAFEService {
	hours: vCACCAFETimeRange;
	version: number;
	organization: vCACCAFECatalogOrganizationReference;
	name: string;
	id: string;
	owner: vCACCAFECatalogPrincipal;
	description: string;
	status: vCACCAFEStatus;
	changeWindow: vCACCAFEChangeWindow;
	newDuration: vCACCAFETimeSpan;
	supportTeam: vCACCAFECatalogPrincipal;
	vcoId: any;
	lastUpdatedDate: any;
	lastUpdatedBy: string;
	statusName: string;
	iconId: string;
	constructor();
	constructor();
	getHours(): vCACCAFETimeRange;
	/**
	 * @param value 
	 */
	setHours(value: vCACCAFETimeRange): void;
	getVersion(): number;
	/**
	 * @param value 
	 */
	setOrganization(value: vCACCAFECatalogOrganizationReference): void;
	getOrganization(): vCACCAFECatalogOrganizationReference;
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	getOwner(): vCACCAFECatalogPrincipal;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	getStatus(): vCACCAFEStatus;
	/**
	 * @param value 
	 */
	setVersion(value: number): void;
	getChangeWindow(): vCACCAFEChangeWindow;
	/**
	 * @param value 
	 */
	setChangeWindow(value: vCACCAFEChangeWindow): void;
	getNewDuration(): vCACCAFETimeSpan;
	/**
	 * @param value 
	 */
	setNewDuration(value: vCACCAFETimeSpan): void;
	getSupportTeam(): vCACCAFECatalogPrincipal;
	/**
	 * @param value 
	 */
	setSupportTeam(value: vCACCAFECatalogPrincipal): void;
	getVcoId(): any;
	/**
	 * @param undefined 
	 */
	setVcoId(id: any): void;
	getLastUpdatedDate(): any;
	/**
	 * @param value 
	 */
	setLastUpdatedDate(value: any): void;
	getLastUpdatedBy(): string;
	/**
	 * @param value 
	 */
	setLastUpdatedBy(value: string): void;
	getStatusName(): string;
	/**
	 * @param value 
	 */
	setStatusName(value: string): void;
	getIconId(): string;
	/**
	 * @param value 
	 */
	setIconId(value: string): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	/**
	 * @param value 
	 */
	setStatus(value: vCACCAFEStatus): void;
	/**
	 * @param value 
	 */
	setOwner(value: vCACCAFECatalogPrincipal): void;
}

declare class vCACCAFEServiceAttribute {
	constructor();
	constructor();
	getValue(): string;
	getKey(): string;
	getId(): string;
	/**
	 * @param value 
	 */
	setValue(value: string): void;
	/**
	 * @param value 
	 */
	setKey(value: string): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
}

declare class vCACCAFEServiceBlueprint {
	version: vCACCAFEVersion;
	forms: vCACCAFEFormScenario[];
	name: string;
	id: string;
	description: string;
	status: vCACCAFEDesignerPublishStatus;
	tenant: string;
	vcoId: any;
	accessmode: string;
	componentConfiguration: vCACCAFEXaaSComponentConfiguration;
	statusName: string;
	outputParameter: vCACCAFECsParameter;
	tenantedUuid: vCACCAFETenantedUuid;
	workflowId: string;
	catalogRequestInfoHidden: boolean;
	constructor();
	constructor();
	getVersion(): vCACCAFEVersion;
	getForms(): vCACCAFEFormScenario[];
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	getStatus(): vCACCAFEDesignerPublishStatus;
	/**
	 * @param value 
	 */
	setVersion(value: vCACCAFEVersion): void;
	/**
	 * @param value 
	 */
	setWorkflowId(value: string): void;
	getTenant(): string;
	getVcoId(): any;
	/**
	 * @param undefined 
	 */
	setVcoId(id: any): void;
	getAccessmode(): string;
	/**
	 * @param accessmode 
	 */
	setAccessmode(accessmode: string): void;
	isComponent(): boolean;
	/**
	 * @param component 
	 */
	setComponent(component: boolean): void;
	getComponentConfiguration(): vCACCAFEXaaSComponentConfiguration;
	/**
	 * @param componentConfiguration 
	 */
	setComponentConfiguration(componentConfiguration: vCACCAFEXaaSComponentConfiguration): void;
	getStatusName(): string;
	/**
	 * @param value 
	 */
	setStatusName(value: string): void;
	getOutputParameter(): vCACCAFECsParameter;
	/**
	 * @param value 
	 */
	setOutputParameter(value: vCACCAFECsParameter): void;
	isBuiltIn(): boolean;
	/**
	 * @param value 
	 */
	setBuiltIn(value: boolean): void;
	getTenantedUuid(): vCACCAFETenantedUuid;
	/**
	 * @param tenantedUuid 
	 */
	setTenantedUuid(tenantedUuid: vCACCAFETenantedUuid): void;
	getWorkflowId(): string;
	isCatalogRequestInfoHidden(): boolean;
	/**
	 * @param value 
	 */
	setCatalogRequestInfoHidden(value: boolean): void;
	/**
	 * @param value 
	 */
	setTenant(value: string): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	/**
	 * @param value 
	 */
	setStatus(value: vCACCAFEDesignerPublishStatus): void;
}

declare class vCACCAFEServiceIdentityStatus {
	constructor();
	constructor();
	getServiceInitializationStatus(): string;
	getErrorMessage(): string;
	/**
	 * @param errorMessage 
	 */
	setErrorMessage(errorMessage: string): void;
	getServiceName(): string;
	/**
	 * @param serviceName 
	 */
	setServiceName(serviceName: string): void;
	getSolutionUser(): string;
	/**
	 * @param solutionUser 
	 */
	setSolutionUser(solutionUser: string): void;
	getStartedTime(): any;
	/**
	 * @param startedTime 
	 */
	setStartedTime(startedTime: any): void;
	/**
	 * @param serviceInitalizationStatus 
	 */
	setServiceInitializationStatus(serviceInitalizationStatus: string): void;
	getIdentityCertificateInfo(): vCACCAFECertificateInfo;
	/**
	 * @param identityCertificateInfo 
	 */
	setIdentityCertificateInfo(identityCertificateInfo: vCACCAFECertificateInfo): void;
	/**
	 * @param initialized 
	 */
	setInitialized(initialized: boolean): void;
}

declare class vCACCAFEServiceInfo {
	constructor();
	constructor();
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	getLastUpdated(): any;
	/**
	 * @param value 
	 */
	setLastUpdated(value: any): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	getOwnerId(): string;
	/**
	 * @param value 
	 */
	setOwnerId(value: string): void;
	getServiceVersion(): string;
	/**
	 * @param value 
	 */
	setServiceVersion(value: string): void;
	getServiceAttributes(): vCACCAFEAttribute[];
	getEndPoints(): vCACCAFEEndPoint[];
	getServiceType(): vCACCAFEServiceType;
	/**
	 * @param value 
	 */
	setServiceType(value: vCACCAFEServiceType): void;
	getNameMsgKey(): string;
	/**
	 * @param value 
	 */
	setNameMsgKey(value: string): void;
	/**
	 * @param value 
	 */
	setCreatedDate(value: any): void;
	getCreatedDate(): any;
}

declare class vCACCAFEServiceRegistration {
	constructor();
	constructor();
	getServiceInfo(): vCACCAFEServiceInfo;
	/**
	 * @param value 
	 */
	setServiceInfo(value: vCACCAFEServiceInfo): void;
	getSolutionInfo(): vCACCAFESolutionInfo;
	/**
	 * @param value 
	 */
	setSolutionInfo(value: vCACCAFESolutionInfo): void;
}

declare class vCACCAFEServiceRegistryStatus {
	constructor();
	constructor();
	getServiceRegistrationId(): string;
	/**
	 * @param serviceRegistrationId 
	 */
	setServiceRegistrationId(serviceRegistrationId: string): void;
	/**
	 * @param sslCertificateInfo 
	 */
	setSslCertificateInfo(sslCertificateInfo: vCACCAFECertificateInfo): void;
	getDefaultServiceEndpointType(): string;
	/**
	 * @param defaultServiceEndpointType 
	 */
	setDefaultServiceEndpointType(defaultServiceEndpointType: string): void;
	getSslCertificateInfo(): vCACCAFECertificateInfo;
	getServiceInitializationStatus(): string;
	getErrorMessage(): string;
	/**
	 * @param errorMessage 
	 */
	setErrorMessage(errorMessage: string): void;
	getServiceName(): string;
	/**
	 * @param serviceName 
	 */
	setServiceName(serviceName: string): void;
	getSolutionUser(): string;
	/**
	 * @param solutionUser 
	 */
	setSolutionUser(solutionUser: string): void;
	getStartedTime(): any;
	/**
	 * @param startedTime 
	 */
	setStartedTime(startedTime: any): void;
	/**
	 * @param serviceInitalizationStatus 
	 */
	setServiceInitializationStatus(serviceInitalizationStatus: string): void;
	getIdentityCertificateInfo(): vCACCAFECertificateInfo;
	/**
	 * @param identityCertificateInfo 
	 */
	setIdentityCertificateInfo(identityCertificateInfo: vCACCAFECertificateInfo): void;
	/**
	 * @param initialized 
	 */
	setInitialized(initialized: boolean): void;
}

/**
 * Object representing the response of a REST call to vCAC
 */
declare interface vCACCAFEServiceResponse {
	/**
	 * Gets all the headers names.
	 */
	getHeaders(): string[];
	/**
	 * Gets the raw response body.
	 */
	getBody(): any;
	/**
	 * Sets the property value for the given property name.
	 * @param name 
	 * @param value 
	 */
	setProperty(name: string, value: any): void;
	/**
	 * Gets the property value for the given property name.
	 * @param name 
	 */
	getProperty(name: string): any;
	/**
	 * Returns the headers and the body JSON object as a string.
	 */
	toString(): string;
	/**
	 * Gets all the properties.
	 */
	getProperties(): any;
	/**
	 * Returns the response status.
	 */
	getStatus(): string;
	/**
	 * Gets the headers value(s) for the given headers name.
	 * @param name 
	 */
	getHeadersProperty(name: string): string[];
	/**
	 * Gets the response body as a JSON object.
	 */
	getBodyAsJson(): any;
	/**
	 * Gets the response body as a string.
	 */
	getBodyAsString(): string;
}

declare class vCACCAFEServiceStatus {
	constructor();
	constructor();
	getStatusEndPointUrl(): string;
	/**
	 * @param value 
	 */
	setStatusEndPointUrl(value: string): void;
	getServiceStatus(): vCACCAFEServiceRegistryStatus;
	/**
	 * @param value 
	 */
	setServiceStatus(value: vCACCAFEServiceRegistryStatus): void;
	getSslTrust(): string;
	/**
	 * @param value 
	 */
	setSslTrust(value: string): void;
	getServiceName(): string;
	/**
	 * @param value 
	 */
	setServiceName(value: string): void;
	/**
	 * @param value 
	 */
	setServiceTypeId(value: string): void;
	isNotAvailable(): boolean;
	/**
	 * @param value 
	 */
	setNotAvailable(value: boolean): void;
	getLastUpdated(): any;
	/**
	 * @param value 
	 */
	setLastUpdated(value: any): void;
	getServiceTypeId(): string;
	getServiceId(): string;
	/**
	 * @param value 
	 */
	setServiceId(value: string): void;
}

declare class vCACCAFEServiceType {
	constructor();
	constructor();
	/**
	 * @param value 
	 */
	setProductId(value: string): void;
	getServiceName(): string;
	/**
	 * @param value 
	 */
	setServiceName(value: string): void;
	/**
	 * @param value 
	 */
	setServiceTypeId(value: string): void;
	getServiceTypeId(): string;
	getProductId(): string;
	/**
	 * @param value 
	 */
	setTypeId(value: string): void;
	getTypeId(): string;
}

declare class vCACCAFESimpleSubtenant {
	constructor();
	constructor();
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	getSubtenantRoles(): vCACCAFESimpleSubtenantRole[];
	/**
	 * @param value 
	 */
	setId(value: string): void;
	/**
	 * @param subtenantRoles 
	 */
	setSubtenantRoles(subtenantRoles: vCACCAFESimpleSubtenantRole[]): void;
}

declare class vCACCAFESimpleSubtenantRole {
	constructor();
	constructor();
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setId(value: string): void;
}

declare class vCACCAFESkipExportBehavior {
	constructor();
	/**
	 * @param isEnabled 
	 */
	constructor(isEnabled: boolean);
	/**
	 * @param value 
	 */
	constructor(value: com.vmware.vcac.platform.content.evaluators.Evaluator);
	/**
	 * @param skipExportBehavior 
	 */
	isEnabled(skipExportBehavior: any): boolean;
	/**
	 * @param value 
	 */
	cloneWithValue(value: any): any;
	/**
	 * @param isInternal 
	 */
	fromBoolean(isInternal: boolean): vCACCAFESkipExportBehavior;
	/**
	 * @param constraint 
	 */
	mergeConstraint(constraint: any): any;
	clone(): any;
	getValue(): any;
}

declare interface vCACCAFESoftwareClassId {
	value(): string;
	values(): vCACCAFESoftwareClassId[];
	/**
	 * @param name 
	 */
	valueOf(name: string): vCACCAFESoftwareClassId;
	/**
	 * @param v 
	 */
	fromValue(v: string): vCACCAFESoftwareClassId;
}

declare class vCACCAFESoftwareClassIdContants {
	constructor();
	constructor();
}

declare class vCACCAFESoftwareComponentType {
	constructor();
	constructor();
	getLifecycleTasks(): vCACCAFEComponentLifecycleTask[];
	getDerivesFromRef(): vCACCAFELabelledReference;
	/**
	 * @param value 
	 */
	setDerivesFromRef(value: vCACCAFELabelledReference): void;
	getCatalogResourceTypeId(): string;
	/**
	 * @param value 
	 */
	setCatalogResourceTypeId(value: string): void;
	getContainerFieldId(): string;
	/**
	 * @param value 
	 */
	setContainerFieldId(value: string): void;
	getSchema(): vCACCAFESchema;
	/**
	 * @param value 
	 */
	setSchema(value: vCACCAFESchema): void;
	getVersion(): number;
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	/**
	 * @param value 
	 */
	setVersion(value: number): void;
	getTenantId(): string;
	getPublishStatus(): vCACCAFESoftwarePublishStatus;
	/**
	 * @param value 
	 */
	setPublishStatus(value: vCACCAFESoftwarePublishStatus): void;
	getPublishStatusName(): string;
	/**
	 * @param value 
	 */
	setPublishStatusName(value: string): void;
	getLastUpdated(): any;
	/**
	 * @param value 
	 */
	setLastUpdated(value: any): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	/**
	 * @param value 
	 */
	setCreatedDate(value: any): void;
	/**
	 * @param value 
	 */
	setTenantId(value: string): void;
	getCreatedDate(): any;
}

declare class vCACCAFESoftwareComponentTypeInfo {
	constructor();
	constructor();
	getVersion(): number;
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	/**
	 * @param value 
	 */
	setVersion(value: number): void;
	getTenantId(): string;
	getPublishStatus(): vCACCAFESoftwarePublishStatus;
	/**
	 * @param value 
	 */
	setPublishStatus(value: vCACCAFESoftwarePublishStatus): void;
	getPublishStatusName(): string;
	/**
	 * @param value 
	 */
	setPublishStatusName(value: string): void;
	getLastUpdated(): any;
	/**
	 * @param value 
	 */
	setLastUpdated(value: any): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	/**
	 * @param value 
	 */
	setCreatedDate(value: any): void;
	/**
	 * @param value 
	 */
	setTenantId(value: string): void;
	getCreatedDate(): any;
}

declare interface vCACCAFESoftwarePublishStatus {
	value(): string;
	values(): vCACCAFESoftwarePublishStatus[];
	/**
	 * @param name 
	 */
	valueOf(name: string): vCACCAFESoftwarePublishStatus;
	/**
	 * @param v 
	 */
	fromValue(v: string): vCACCAFESoftwarePublishStatus;
}

declare interface vCACCAFESoftwareRequestState {
	value(): string;
	values(): vCACCAFESoftwareRequestState[];
	/**
	 * @param name 
	 */
	valueOf(name: string): vCACCAFESoftwareRequestState;
	/**
	 * @param v 
	 */
	fromValue(v: string): vCACCAFESoftwareRequestState;
}

declare class vCACCAFESoftwareResourceRequest {
	constructor();
	constructor();
	getVersion(): number;
	getId(): string;
	getState(): vCACCAFESoftwareRequestState;
	/**
	 * @param value 
	 */
	setVersion(value: number): void;
	/**
	 * @param value 
	 */
	setState(value: vCACCAFESoftwareRequestState): void;
	getTenantId(): string;
	getRootCafeRequestId(): string;
	/**
	 * @param value 
	 */
	setRootCafeRequestId(value: string): void;
	getStateName(): string;
	/**
	 * @param value 
	 */
	setStateName(value: string): void;
	getSubTenantId(): string;
	/**
	 * @param value 
	 */
	setSubTenantId(value: string): void;
	getResourceId(): string;
	/**
	 * @param value 
	 */
	setResourceId(value: string): void;
	getRequestedBy(): string;
	getSoftwareComponentTypeRef(): vCACCAFELabelledReference;
	/**
	 * @param value 
	 */
	setSoftwareComponentTypeRef(value: vCACCAFELabelledReference): void;
	getResourceData(): vCACCAFELiteralMap;
	/**
	 * @param value 
	 */
	setResourceData(value: vCACCAFELiteralMap): void;
	getPreviousResourceData(): vCACCAFELiteralMap;
	/**
	 * @param value 
	 */
	setPreviousResourceData(value: vCACCAFELiteralMap): void;
	getExternalServiceId(): string;
	/**
	 * @param value 
	 */
	setExternalServiceId(value: string): void;
	getOperationDescriptorRef(): vCACCAFELabelledReference;
	/**
	 * @param value 
	 */
	setOperationDescriptorRef(value: vCACCAFELabelledReference): void;
	getStatusMessage(): string;
	/**
	 * @param value 
	 */
	setStatusMessage(value: string): void;
	getTaskRequests(): vCACCAFETaskRequest[];
	getDateCompleted(): any;
	/**
	 * @param value 
	 */
	setDateCompleted(value: any): void;
	getRequestData(): vCACCAFELiteralMap;
	getDateSubmitted(): any;
	/**
	 * @param value 
	 */
	setDateSubmitted(value: any): void;
	/**
	 * @param value 
	 */
	setRequestedBy(value: string): void;
	/**
	 * @param value 
	 */
	setRequestData(value: vCACCAFELiteralMap): void;
	getLastUpdated(): any;
	/**
	 * @param value 
	 */
	setLastUpdated(value: any): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	/**
	 * @param value 
	 */
	setCreatedDate(value: any): void;
	/**
	 * @param value 
	 */
	setTenantId(value: string): void;
	getCreatedDate(): any;
}

declare class vCACCAFESolutionInfo {
	constructor();
	constructor();
	getCertificate(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	getSolutionName(): string;
	/**
	 * @param value 
	 */
	setSolutionName(value: string): void;
	/**
	 * @param value 
	 */
	setCertificate(value: string): void;
}

declare interface vCACCAFEStartsWithOperator {
	getId(): string;
	/**
	 * @param left 
	 * @param right 
	 */
	evaluate(left: any, right: any): vCACCAFEBooleanLiteral;
	getDataTypeConstraint(): any;
	getCategory(): any;
	getMultiplicityConstraint(): any;
}

declare class vCACCAFEStaticForm {
	constructor();
	constructor();
	/**
	 * @param layout 
	 */
	constructor(layout: vCACCAFELayout);
	getLayout(): vCACCAFELayout;
	/**
	 * @param other 
	 */
	equalsForAttributes(other: vCACCAFEStaticForm): boolean;
	/**
	 * @param layout 
	 */
	setLayout(layout: vCACCAFELayout): void;
}

declare class vCACCAFEStaticLayout {
	constructor();
	constructor();
	/**
	 * @param layout 
	 */
	constructor(layout: vCACCAFELayout);
	getLayout(): vCACCAFELayout;
	/**
	 * @param other 
	 */
	equalsForAttributes(other: vCACCAFEStaticLayout): boolean;
	/**
	 * @param layout 
	 */
	setLayout(layout: vCACCAFELayout): void;
}

declare class vCACCAFEStaticNotificationContent {
	constructor();
	constructor();
	getBody(): string;
	/**
	 * @param value 
	 */
	setSubject(value: string): void;
	/**
	 * @param value 
	 */
	setBodyFormat(value: vCACCAFEEmailFormat): void;
	getBodyFormat(): vCACCAFEEmailFormat;
	/**
	 * @param value 
	 */
	setBody(value: string): void;
	getSubject(): string;
}

declare class vCACCAFEStaticPermissibleValueList {
	constructor();
	/**
	 * @param values 
	 */
	constructor(values: vCACCAFEPermissibleValue[]);
	iterator(): any;
	getValues(): vCACCAFEPermissibleValue[];
	isCustomAllowed(): boolean;
	/**
	 * @param customAllowed 
	 */
	setCustomAllowed(customAllowed: boolean): void;
}

declare class vCACCAFEStaticSchema {
	constructor();
	constructor();
	/**
	 * @param schema 
	 */
	constructor(schema: vCACCAFESchema);
	getSchema(): vCACCAFESchema;
	/**
	 * @param schema 
	 */
	setSchema(schema: vCACCAFESchema): void;
}

declare interface vCACCAFEStatus {
	value(): string;
	values(): vCACCAFEStatus[];
	/**
	 * @param name 
	 */
	valueOf(name: string): vCACCAFEStatus;
	/**
	 * @param v 
	 */
	fromValue(v: string): vCACCAFEStatus;
}

declare interface vCACCAFEStorageType {
	value(): string;
	values(): vCACCAFEStorageType[];
	/**
	 * @param name 
	 */
	valueOf(name: string): vCACCAFEStorageType;
	/**
	 * @param v 
	 */
	fromValue(v: string): vCACCAFEStorageType;
}

declare class vCACCAFEStringLiteral {
	constructor();
	/**
	 * @param value 
	 */
	constructor(value: string);
	getTypeId(): vCACCAFEDataTypeId;
	isList(): boolean;
	/**
	 * @param other 
	 */
	compareTo(other: any): number;
	getValue(): string;
	/**
	 * @param bVal 
	 */
	fromBoolean(bVal: boolean): any;
	/**
	 * @param obj 
	 */
	safeEquals(obj: any): boolean;
	/**
	 * @param sVal 
	 */
	fromString(sVal: string): any;
	/**
	 * @param lVal 
	 */
	fromInt(lVal: number): any;
	/**
	 * @param dVal 
	 */
	fromDecimal(dVal: number): any;
}

declare class vCACCAFESubscription {
	constructor();
	constructor();
	getId(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	/**
	 * @param value 
	 */
	setReply(value: boolean): void;
	isReply(): boolean;
	getSubscriberId(): string;
	/**
	 * @param value 
	 */
	setSubscriberId(value: string): void;
	isBlocking(): boolean;
	/**
	 * @param value 
	 */
	setBlocking(value: boolean): void;
	getMessageTTL(): number;
	/**
	 * @param value 
	 */
	setMessageTTL(value: number): void;
	getRequeueDelay(): number;
	/**
	 * @param requeueDelay 
	 */
	setRequeueDelay(requeueDelay: number): void;
	getEventTopicId(): string;
	/**
	 * @param value 
	 */
	setEventTopicId(value: string): void;
	/**
	 * @param value 
	 */
	setEndpoint(value: string): void;
	getEndpoints(): vCACCAFESubscriptionEndpoint[];
	getEndpoint(): string;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	/**
	 * @param value 
	 */
	setCriteria(value: string): void;
	getCriteria(): string;
}

declare class vCACCAFESubscriptionEndpoint {
	constructor();
	constructor();
	toString(): string;
	hashCode(): number;
	getProtocol(): vCACCAFESubscriptionEndpointProtocol;
	getEndpoint(): string;
}

declare interface vCACCAFESubscriptionEndpointProtocol {
	value(): string;
	values(): vCACCAFESubscriptionEndpointProtocol[];
	/**
	 * @param name 
	 */
	valueOf(name: string): vCACCAFESubscriptionEndpointProtocol;
	/**
	 * @param v 
	 */
	fromValue(v: string): vCACCAFESubscriptionEndpointProtocol;
}

declare class vCACCAFESubscriptionSpec {
	constructor();
	constructor();
	getId(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	getSubscriberId(): string;
	/**
	 * @param value 
	 */
	setSubscriberId(value: string): void;
	isBlocking(): boolean;
	/**
	 * @param value 
	 */
	setBlocking(value: boolean): void;
	getMessageTTL(): number;
	/**
	 * @param value 
	 */
	setMessageTTL(value: number): void;
	getRequeueDelay(): number;
	/**
	 * @param requeueDelay 
	 */
	setRequeueDelay(requeueDelay: number): void;
	getEventTopicId(): string;
	/**
	 * @param value 
	 */
	setEventTopicId(value: string): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	/**
	 * @param value 
	 */
	setCriteria(value: string): void;
	getCriteria(): string;
}

declare interface vCACCAFESubsetOperator {
	getId(): string;
	/**
	 * @param left 
	 * @param right 
	 */
	evaluate(left: any, right: any): vCACCAFEBooleanLiteral;
	getMultiplicityConstraint(): any;
	getDataTypeConstraint(): any;
	getCategory(): any;
}

declare class vCACCAFESubtenant {
	name: string;
	id: string;
	description: string;
	tenant: string;
	vcoId: any;
	subtenantRoles: vCACCAFESubtenantRole[];
	extensionData: vCACCAFELiteralMap;
	constructor();
	constructor();
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	getTenant(): string;
	getVcoId(): any;
	/**
	 * @param undefined 
	 */
	setVcoId(id: any): void;
	/**
	 * @param value 
	 */
	setTenant(value: string): void;
	/**
	 * @param value 
	 */
	setExtensionData(value: vCACCAFELiteralMap): void;
	getSubtenantRoles(): vCACCAFESubtenantRole[];
	/**
	 * @param value 
	 */
	setId(value: string): void;
	getExtensionData(): vCACCAFELiteralMap;
}

declare class vCACCAFESubtenantPrincipalsData {
	constructor();
	constructor();
	getPrincipals(): vCACCAFEPrincipalData[];
	/**
	 * @param value 
	 */
	setSubtenantId(value: string): void;
	getSubtenantId(): string;
}

declare class vCACCAFESubtenantRole {
	constructor();
	constructor();
	toString(): string;
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	getScopeRoleRef(): string;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	getPrincipalId(): vCACCAFEPrincipalId[];
	/**
	 * @param value 
	 */
	setScopeRoleRef(value: string): void;
}

declare class vCACCAFESuiteTokenRequest {
	constructor();
	constructor();
	/**
	 * @param value 
	 */
	setValidity(value: number): void;
	getPrincipal(): string;
	getTenant(): string;
	/**
	 * @param value 
	 */
	setTenant(value: string): void;
	/**
	 * @param value 
	 */
	setPrincipal(value: string): void;
	getValidity(): number;
	getSigningCert(): string;
	/**
	 * @param value 
	 */
	setSigningCert(value: string): void;
	getRequestLifetime(): number;
	/**
	 * @param value 
	 */
	setRequestLifetime(value: number): void;
}

declare interface vCACCAFESupersetOperator {
	getId(): string;
	/**
	 * @param left 
	 * @param right 
	 */
	evaluate(left: any, right: any): vCACCAFEBooleanLiteral;
	getMultiplicityConstraint(): any;
	getDataTypeConstraint(): any;
	getCategory(): any;
}

declare class vCACCAFESwAttribute {
	constructor();
	constructor();
	getValue(): string;
	getKey(): string;
	/**
	 * @param value 
	 */
	setValue(value: string): void;
	/**
	 * @param value 
	 */
	setKey(value: string): void;
}

declare interface vCACCAFESyncStatusEnum {
	value(): string;
	values(): vCACCAFESyncStatusEnum[];
	/**
	 * @param name 
	 */
	valueOf(name: string): vCACCAFESyncStatusEnum;
	/**
	 * @param v 
	 */
	fromValue(v: string): vCACCAFESyncStatusEnum;
}

declare class vCACCAFESystemRole {
	constructor();
	constructor();
	getAssignedPermissions(): vCACCAFEPermission[];
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	getStatus(): string;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	/**
	 * @param value 
	 */
	setStatus(value: string): void;
}

declare class vCACCAFETableView {
	constructor();
	constructor();
	/**
	 * @param columns 
	 * @param defaultSequence 
	 */
	constructor(columns: vCACCAFEColumn[], defaultSequence: string[]);
	/**
	 * @param columns 
	 * @param defaultSequence 
	 */
	constructor(columns: vCACCAFEColumn[], defaultSequence: string[]);
	/**
	 * @param columns 
	 */
	constructor(columns: vCACCAFEColumn[]);
	/**
	 * @param columns 
	 */
	constructor(columns: vCACCAFEColumn[]);
	getDefaultSequence(): string[];
	getColumns(): vCACCAFEColumn[];
}

declare class vCACCAFETagInstanceList {
	constructor();
	constructor();
	getId(): string;
	getGlobalTags(): vCACCAFELiteralMap;
	/**
	 * @param value 
	 */
	setGlobalTags(value: vCACCAFELiteralMap): void;
	getUserTags(): vCACCAFELiteralMap;
	/**
	 * @param value 
	 */
	setUserTags(value: vCACCAFELiteralMap): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
}

declare class vCACCAFETaskDescriptor {
	constructor();
	constructor();
	getVersion(): number;
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	/**
	 * @param value 
	 */
	setVersion(value: number): void;
	getLastUpdated(): any;
	/**
	 * @param value 
	 */
	setLastUpdated(value: any): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	/**
	 * @param value 
	 */
	setCreatedDate(value: any): void;
	getCreatedDate(): any;
}

declare class vCACCAFETaskRequest {
	constructor();
	constructor();
	getVersion(): number;
	getId(): string;
	getState(): vCACCAFESoftwareRequestState;
	getScript(): vCACCAFEScript;
	/**
	 * @param value 
	 */
	setVersion(value: number): void;
	/**
	 * @param value 
	 */
	setState(value: vCACCAFESoftwareRequestState): void;
	getLog(): string;
	getStateName(): string;
	/**
	 * @param value 
	 */
	setStateName(value: string): void;
	/**
	 * @param value 
	 */
	setScript(value: vCACCAFEScript): void;
	getTaskDescriptorRef(): vCACCAFELabelledReference;
	/**
	 * @param value 
	 */
	setTaskDescriptorRef(value: vCACCAFELabelledReference): void;
	getNodeRef(): vCACCAFELabelledReference;
	/**
	 * @param value 
	 */
	setNodeRef(value: vCACCAFELabelledReference): void;
	getDateStarted(): any;
	/**
	 * @param value 
	 */
	setDateStarted(value: any): void;
	getDateCompleted(): any;
	/**
	 * @param value 
	 */
	setDateCompleted(value: any): void;
	getLogDescription(): string;
	/**
	 * @param value 
	 */
	setLogDescription(value: string): void;
	getLastUpdated(): any;
	/**
	 * @param value 
	 */
	setLastUpdated(value: any): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	/**
	 * @param value 
	 */
	setLog(value: string): void;
	/**
	 * @param value 
	 */
	setCreatedDate(value: any): void;
	getCreatedDate(): any;
}

declare class vCACCAFETaskStatusDetail {
	constructor();
	constructor();
	getStatus(): vCACCAFERequestStatus;
	getLog(): string;
	/**
	 * @param value 
	 */
	setLabel(value: string): void;
	getDateStarted(): any;
	/**
	 * @param value 
	 */
	setDateStarted(value: any): void;
	getDateCompleted(): any;
	/**
	 * @param value 
	 */
	setDateCompleted(value: any): void;
	getLogDescription(): string;
	/**
	 * @param value 
	 */
	setLogDescription(value: string): void;
	getLabel(): string;
	/**
	 * @param value 
	 */
	setStatus(value: vCACCAFERequestStatus): void;
	/**
	 * @param value 
	 */
	setLog(value: string): void;
}

declare class vCACCAFETenant {
	name: string;
	id: string;
	description: string;
	vcoId: any;
	defaultTenant: boolean;
	urlName: string;
	contactEmail: string;
	constructor();
	constructor();
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	getVcoId(): any;
	/**
	 * @param undefined 
	 */
	setVcoId(id: any): void;
	getPassword(): string;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	/**
	 * @param value 
	 */
	setPassword(value: string): void;
	isDefaultTenant(): boolean;
	/**
	 * @param value 
	 */
	setDefaultTenant(value: boolean): void;
	getUrlName(): string;
	/**
	 * @param value 
	 */
	setUrlName(value: string): void;
	getContactEmail(): string;
	/**
	 * @param value 
	 */
	setContactEmail(value: string): void;
}

declare class vCACCAFETenantNotificationProviders {
	constructor();
	constructor();
	getTenantId(): string;
	getEmailNotificationProvider(): vCACCAFEEmailNotificationProvider[];
	/**
	 * @param value 
	 */
	setTenantId(value: string): void;
}

declare class vCACCAFETenantRole {
	constructor();
	constructor();
	getAssignedPermissions(): vCACCAFEPermission[];
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	getStatus(): string;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	/**
	 * @param value 
	 */
	setStatus(value: string): void;
}

declare class vCACCAFETenantValidator {
	constructor();
	constructor();
}

declare class vCACCAFETenantedUuid {
	constructor();
	constructor();
	/**
	 * @param id 
	 * @param tenant 
	 */
	constructor(id: string, tenant: string);
	/**
	 * @param obj 
	 */
	equals(obj: any): boolean;
	toString(): string;
	hashCode(): number;
	/**
	 * @param encodedString 
	 */
	decode(encodedString: string): vCACCAFETenantedUuid;
	encode(): string;
	/**
	 * @param id 
	 * @param tenant 
	 */
	create(id: string, tenant: string): vCACCAFETenantedUuid;
	getId(): string;
	getTenant(): string;
	/**
	 * @param id 
	 */
	createTenantLessId(id: string): vCACCAFETenantedUuid;
	/**
	 * @param value 
	 */
	setTenant(value: string): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
}

declare class vCACCAFETimeRange {
	constructor();
	constructor();
	/**
	 * @param value 
	 */
	setStartTime(value: any): void;
	getStartTime(): any;
	/**
	 * @param value 
	 */
	setEndTime(value: any): void;
	getEndTime(): any;
}

declare class vCACCAFETimeSpan {
	constructor();
	/**
	 * @param unit 
	 * @param amount 
	 */
	constructor(unit: com.vmware.vcac.platform.content.common.dates.TimeUnit, amount: number);
	/**
	 * @param unit 
	 * @param amount 
	 */
	constructor(unit: com.vmware.vcac.platform.content.common.dates.TimeUnit, amount: number);
	/**
	 * @param targetUnit 
	 */
	convert(targetUnit: any): vCACCAFETimeSpan;
	getNormalizedValue(): vCACCAFETimeSpan;
	/**
	 * @param maxComponents 
	 * @param minUnit 
	 */
	toComponents(maxComponents: number, minUnit: any): vCACCAFETimeSpan[];
	/**
	 * @param start 
	 * @param end 
	 * @param unit 
	 */
	fromPeriod(start: any, end: any, unit: any): vCACCAFETimeSpan;
	isList(): boolean;
	/**
	 * @param other 
	 */
	compareTo(other: any): number;
	getValue(): vCACCAFETimeSpan;
	/**
	 * @param divisor 
	 */
	divide(divisor: number): vCACCAFETimeSpan;
	/**
	 * @param targetUnit 
	 */
	round(targetUnit: any): vCACCAFETimeSpan;
	/**
	 * @param multiplier 
	 */
	multiply(multiplier: number): vCACCAFETimeSpan;
	getAmount(): number;
	getUnit(): any;
	getTypeId(): vCACCAFEDataTypeId;
	/**
	 * @param bVal 
	 */
	fromBoolean(bVal: boolean): any;
	/**
	 * @param obj 
	 */
	safeEquals(obj: any): boolean;
	/**
	 * @param sVal 
	 */
	fromString(sVal: string): any;
	/**
	 * @param lVal 
	 */
	fromInt(lVal: number): any;
	/**
	 * @param dVal 
	 */
	fromDecimal(dVal: number): any;
}

declare interface vCACCAFEType {
	value(): string;
	values(): vCACCAFEType[];
	/**
	 * @param name 
	 */
	valueOf(name: string): vCACCAFEType;
	/**
	 * @param v 
	 */
	fromValue(v: string): vCACCAFEType;
}

declare class vCACCAFEUniqueConstraint {
	constructor();
	/**
	 * @param isUnique 
	 */
	constructor(isUnique: boolean);
	/**
	 * @param value 
	 */
	constructor(value: com.vmware.vcac.platform.content.evaluators.Evaluator);
	/**
	 * @param value 
	 */
	cloneWithValue(value: any): any;
	/**
	 * @param isUnique 
	 */
	fromBoolean(isUnique: boolean): vCACCAFEUniqueConstraint;
	/**
	 * @param constraint 
	 */
	mergeConstraint(constraint: any): any;
	clone(): any;
	getValue(): any;
}

declare class vCACCAFEUnsolicitedInboundNotification {
	constructor();
	constructor();
	getBody(): string;
	getAction(): string;
	getUserToken(): string;
	/**
	 * @param value 
	 */
	setUserToken(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setSubject(value: string): void;
	/**
	 * @param value 
	 */
	setAction(value: string): void;
	/**
	 * @param value 
	 */
	setNotificationId(value: string): void;
	getFromUser(): string;
	/**
	 * @param value 
	 */
	setFromUser(value: string): void;
	getMessageReceivedAt(): any;
	/**
	 * @param value 
	 */
	setMessageReceivedAt(value: any): void;
	getNotificationId(): string;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	/**
	 * @param value 
	 */
	setUserId(value: string): void;
	getUserId(): string;
	/**
	 * @param value 
	 */
	setBody(value: string): void;
	getSubject(): string;
}

declare interface vCACCAFEUnspecifiedEvaluator {
	getConstantValue(): any;
	isConstant(): boolean;
	/**
	 * @param bVal 
	 */
	fromBoolean(bVal: boolean): any;
	/**
	 * @param sVal 
	 */
	fromString(sVal: string): any;
	/**
	 * @param lVal 
	 */
	fromInt(lVal: number): any;
	/**
	 * @param dVal 
	 */
	fromDecimal(dVal: number): any;
}

declare class vCACCAFEUser {
	constructor();
	constructor();
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	isLocked(): boolean;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	getManager(): vCACCAFEPrincipalId;
	isDisabled(): boolean;
	getPassword(): string;
	/**
	 * @param value 
	 */
	setPassword(value: string): void;
	getPrincipalId(): vCACCAFEPrincipalId;
	getEmailAddress(): string;
	/**
	 * @param value 
	 */
	setEmailAddress(value: string): void;
	/**
	 * @param value 
	 */
	setPrincipalId(value: vCACCAFEPrincipalId): void;
	getTenantName(): string;
	/**
	 * @param value 
	 */
	setTenantName(value: string): void;
	/**
	 * @param value 
	 */
	setManager(value: vCACCAFEPrincipalId): void;
	/**
	 * @param value 
	 */
	setLocked(value: boolean): void;
	/**
	 * @param value 
	 */
	setDisabled(value: boolean): void;
	getFirstName(): string;
	/**
	 * @param value 
	 */
	setFirstName(value: string): void;
	getLastName(): string;
	/**
	 * @param value 
	 */
	setLastName(value: string): void;
}

declare interface vCACCAFEUserAction {
	value(): string;
	values(): vCACCAFEUserAction[];
	/**
	 * @param name 
	 */
	valueOf(name: string): vCACCAFEUserAction;
	/**
	 * @param v 
	 */
	fromValue(v: string): vCACCAFEUserAction;
}

declare class vCACCAFEUserActionEvent {
	constructor();
	constructor();
	getAction(): vCACCAFEUserAction;
	/**
	 * @param value 
	 */
	setLevel(value: vCACCAFEApprovalLevel): void;
	getLevel(): vCACCAFEApprovalLevel;
	/**
	 * @param value 
	 */
	setAction(value: vCACCAFEUserAction): void;
	/**
	 * @param value 
	 */
	setJustification(value: string): void;
	getJustification(): string;
	getEventType(): vCACCAFEEvaluationEventType;
	/**
	 * @param value 
	 */
	setEventType(value: vCACCAFEEvaluationEventType): void;
	getApprovalId(): string;
	/**
	 * @param value 
	 */
	setApprovalId(value: string): void;
}

declare class vCACCAFEUserCredentials {
	constructor();
	constructor();
	getTenant(): string;
	/**
	 * @param value 
	 */
	setTenant(value: string): void;
	getUsername(): string;
	getPassword(): string;
	/**
	 * @param value 
	 */
	setPassword(value: string): void;
	/**
	 * @param value 
	 */
	setUsername(value: string): void;
}

declare class vCACCAFEUserGroupWrapper {
	constructor();
	constructor();
	getUsers(): vCACCAFEUser[];
	getGroups(): vCACCAFEGroup[];
	getParentGroup(): vCACCAFEGroup;
	/**
	 * @param value 
	 */
	setParentGroup(value: vCACCAFEGroup): void;
}

declare class vCACCAFEUserNotificationPreference {
	constructor();
	constructor();
	/**
	 * @param value 
	 */
	setLocale(value: string): void;
	getLocale(): string;
	getVersion(): number;
	/**
	 * @param value 
	 */
	setVersion(value: number): void;
	getNotificationDeliveryMechanisms(): vCACCAFENotificationDeliveryMechanism[];
}

declare class vCACCAFEUserSecurityContext {
	constructor();
	constructor();
	getSamlToken(): string;
	getAuthoritiesContext(): vCACCAFEAuthoritiesContext;
	/**
	 * @param value 
	 */
	setAuthoritiesContext(value: vCACCAFEAuthoritiesContext): void;
	getMembershipContext(): vCACCAFEMembershipContext;
	/**
	 * @param value 
	 */
	setMembershipContext(value: vCACCAFEMembershipContext): void;
	/**
	 * @param value 
	 */
	setSamlToken(value: string): void;
	isSolution(): boolean;
	/**
	 * @param value 
	 */
	setSolution(value: boolean): void;
	isSuite(): boolean;
	/**
	 * @param value 
	 */
	setSuite(value: boolean): void;
}

declare interface vCACCAFEUserSortKeys {
	value(): string;
	values(): vCACCAFEUserSortKeys[];
	/**
	 * @param name 
	 */
	valueOf(name: string): vCACCAFEUserSortKeys;
	/**
	 * @param v 
	 */
	fromValue(v: string): vCACCAFEUserSortKeys;
}

declare class vCACCAFEUserTokenResource {
	constructor();
	constructor();
	getId(): string;
	getTenant(): string;
	/**
	 * @param value 
	 */
	setTenant(value: string): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	getExpires(): any;
	/**
	 * @param value 
	 */
	setExpires(value: any): void;
}

declare interface vCACCAFEValidationStatusCode {
	value(): string;
	values(): vCACCAFEValidationStatusCode[];
	/**
	 * @param name 
	 */
	valueOf(name: string): vCACCAFEValidationStatusCode;
	/**
	 * @param v 
	 */
	fromValue(v: string): vCACCAFEValidationStatusCode;
}

declare class vCACCAFEVersion {
	constructor();
	constructor();
	getVersion(): string;
	getMinor(): number;
	getMajor(): number;
	/**
	 * @param value 
	 */
	setVersion(value: string): void;
	getRevision(): number;
	/**
	 * @param value 
	 */
	setRevision(value: number): void;
	getMicro(): number;
	/**
	 * @param value 
	 */
	setMicro(value: number): void;
	/**
	 * @param value 
	 */
	setMajor(value: number): void;
	/**
	 * @param value 
	 */
	setMinor(value: number): void;
}

declare class vCACCAFEVirtualServer {
	constructor();
	constructor();
	isEnabled(): boolean;
	getProtocol(): string;
	getPort(): number;
	getPoolId(): string;
	/**
	 * @param value 
	 */
	setPoolId(value: string): void;
	getIpAddress(): string;
	/**
	 * @param value 
	 */
	setIpAddress(value: string): void;
	getConnectionLimit(): number;
	/**
	 * @param value 
	 */
	setConnectionLimit(value: number): void;
	getConnectionRate(): number;
	/**
	 * @param value 
	 */
	setConnectionRate(value: number): void;
	getApplicationProfileId(): string;
	/**
	 * @param value 
	 */
	setApplicationProfileId(value: string): void;
	isEnableServiceInsertion(): boolean;
	/**
	 * @param value 
	 */
	setEnableServiceInsertion(value: boolean): void;
	isAccelerationEnabled(): boolean;
	/**
	 * @param value 
	 */
	setAccelerationEnabled(value: boolean): void;
	getApplicationRulesIds(): string[];
	/**
	 * @param value 
	 */
	setEnabled(value: boolean): void;
	/**
	 * @param value 
	 */
	setProtocol(value: string): void;
	/**
	 * @param value 
	 */
	setPort(value: number): void;
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	/**
	 * @param value 
	 */
	setExtensionData(value: vCACCAFELiteralMap): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	getExtensionData(): vCACCAFELiteralMap;
	getExternalId(): string;
	/**
	 * @param value 
	 */
	setExternalId(value: string): void;
}

declare class vCACCAFEVisibilityConstraint {
	constructor();
	/**
	 * @param isVisible 
	 */
	constructor(isVisible: boolean);
	/**
	 * @param value 
	 */
	constructor(value: com.vmware.vcac.platform.content.evaluators.Evaluator);
	/**
	 * @param value 
	 */
	cloneWithValue(value: any): vCACCAFEVisibilityConstraint;
	/**
	 * @param isVisible 
	 */
	fromBoolean(isVisible: boolean): vCACCAFEVisibilityConstraint;
	/**
	 * @param constraint 
	 */
	mergeConstraint(constraint: any): any;
	clone(): any;
	getValue(): any;
}

declare interface vCACCAFEWithinOperator {
	getId(): string;
	/**
	 * @param left 
	 * @param right 
	 */
	evaluate(left: any, right: any): vCACCAFEBooleanLiteral;
	getDataTypeConstraint(): any;
	getCategory(): any;
	getMultiplicityConstraint(): any;
}

declare class vCACCAFEWizardState {
	constructor();
	constructor();
	getId(): string;
	getTenantId(): string;
	getGoalId(): string;
	/**
	 * @param value 
	 */
	setGoalId(value: string): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	/**
	 * @param value 
	 */
	setUserId(value: string): void;
	getUserId(): string;
	/**
	 * @param value 
	 */
	setTenantId(value: string): void;
}

declare class vCACCAFEWorkItem {
	version: number;
	name: string;
	id: string;
	status: string;
	tenantId: string;
	callbackEntityId: string;
	assignedOrCompletedDate: any;
	formUrl: string;
	workItemRequest: vCACCAFEWorkItemRequest;
	assignedDate: any;
	completedDate: any;
	completedBy: string;
	subTenantId: string;
	workItemNumber: number;
	vcoId: any;
	availableActions: vCACCAFEWorkItemAction[];
	assignees: vCACCAFEWorkItemPrincipal[];
	workItemType: vCACCAFEWorkItemType;
	createdDate: any;
	serviceId: string;
	constructor();
	constructor();
	getVersion(): number;
	getName(): string;
	getId(): string;
	getStatus(): string;
	/**
	 * @param value 
	 */
	setVersion(value: number): void;
	getTenantId(): string;
	getCallbackEntityId(): string;
	/**
	 * @param value 
	 */
	setCallbackEntityId(value: string): void;
	/**
	 * @param value 
	 */
	setWorkItemType(value: vCACCAFEWorkItemType): void;
	getAssignedOrCompletedDate(): any;
	/**
	 * @param value 
	 */
	setAssignedOrCompletedDate(value: any): void;
	getFormUrl(): string;
	/**
	 * @param value 
	 */
	setFormUrl(value: string): void;
	getWorkItemRequest(): vCACCAFEWorkItemRequest;
	/**
	 * @param value 
	 */
	setWorkItemRequest(value: vCACCAFEWorkItemRequest): void;
	getAssignedDate(): any;
	/**
	 * @param value 
	 */
	setAssignedDate(value: any): void;
	getCompletedDate(): any;
	/**
	 * @param value 
	 */
	setCompletedDate(value: any): void;
	getCompletedBy(): string;
	/**
	 * @param value 
	 */
	setCompletedBy(value: string): void;
	getSubTenantId(): string;
	/**
	 * @param value 
	 */
	setSubTenantId(value: string): void;
	getWorkItemNumber(): number;
	/**
	 * @param value 
	 */
	setWorkItemNumber(value: number): void;
	getVcoId(): any;
	/**
	 * @param undefined 
	 */
	setVcoId(id: any): void;
	getAvailableActions(): vCACCAFEWorkItemAction[];
	getAssignees(): vCACCAFEWorkItemPrincipal[];
	/**
	 * @param value 
	 */
	setId(value: string): void;
	/**
	 * @param value 
	 */
	setStatus(value: string): void;
	getWorkItemType(): vCACCAFEWorkItemType;
	/**
	 * @param value 
	 */
	setCreatedDate(value: any): void;
	/**
	 * @param value 
	 */
	setTenantId(value: string): void;
	getCreatedDate(): any;
	getServiceId(): string;
	/**
	 * @param value 
	 */
	setServiceId(value: string): void;
}

declare interface vCACCAFEWorkItemAccess {
	value(): string;
	values(): vCACCAFEWorkItemAccess[];
	/**
	 * @param name 
	 */
	valueOf(name: string): vCACCAFEWorkItemAccess;
	/**
	 * @param v 
	 */
	fromValue(v: string): vCACCAFEWorkItemAccess;
}

declare class vCACCAFEWorkItemAction {
	icon: vCACCAFEWorkItemActionIcon;
	name: string;
	id: string;
	stateName: string;
	vcoId: any;
	stateNameId: string;
	constructor();
	constructor();
	getIcon(): vCACCAFEWorkItemActionIcon;
	toString(): string;
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	getStateName(): string;
	/**
	 * @param value 
	 */
	setStateName(value: string): void;
	getVcoId(): any;
	/**
	 * @param undefined 
	 */
	setVcoId(id: any): void;
	/**
	 * @param value 
	 */
	setIcon(value: vCACCAFEWorkItemActionIcon): void;
	getStateNameId(): string;
	/**
	 * @param value 
	 */
	setStateNameId(value: string): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
}

declare class vCACCAFEWorkItemActionComplete {
	constructor();
	constructor();
	getWorkItemActionId(): string;
	getFormData(): vCACCAFELiteralMap;
	getWorkItemId(): string;
	/**
	 * @param value 
	 */
	setWorkItemId(value: string): void;
	/**
	 * @param value 
	 */
	setWorkItemActionId(value: string): void;
	/**
	 * @param value 
	 */
	setFormData(value: vCACCAFELiteralMap): void;
}

declare class vCACCAFEWorkItemActionIcon {
	constructor();
	constructor();
	getContentType(): string;
	/**
	 * @param value 
	 */
	setContentType(value: string): void;
	getImage(): any[];
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	/**
	 * @param value 
	 */
	setImage(value: any[]): void;
}

declare class vCACCAFEWorkItemAssignee {
	constructor();
	constructor();
	getDelegates(): vCACCAFEWorkItemPrincipal[];
}

declare class vCACCAFEWorkItemCallbackAction {
	constructor();
	constructor();
	getCompleteActionId(): string;
	getCompletedDate(): any;
	/**
	 * @param value 
	 */
	setCompletedDate(value: any): void;
	getFormData(): vCACCAFELiteralMap;
	getWorkItemId(): string;
	getTargetId(): string;
	/**
	 * @param value 
	 */
	setTargetId(value: string): void;
	/**
	 * @param value 
	 */
	setCompleteActionId(value: string): void;
	getWorkItemTypeId(): string;
	/**
	 * @param value 
	 */
	setWorkItemTypeId(value: string): void;
	getAssignee(): string;
	/**
	 * @param value 
	 */
	setAssignee(value: string): void;
	/**
	 * @param value 
	 */
	setWorkItemId(value: string): void;
	/**
	 * @param value 
	 */
	setFormData(value: vCACCAFELiteralMap): void;
}

declare interface vCACCAFEWorkItemClient {
	getWorkItemWorkItemTypeService(): vCACCAFEWorkItemWorkItemTypeService;
	getWorkItemWorkItemActionIconService(): vCACCAFEWorkItemWorkItemActionIconService;
	getWorkItemWorkItemAssigneeService(): vCACCAFEWorkItemWorkItemAssigneeService;
	getWorkItemWorkItemService(): vCACCAFEWorkItemWorkItemService;
	/**
	 * Does a POST request expanding the given template variables in order.
	 * @param resourceUrl 
	 * @param object 
	 * @param urlVariables 
	 */
	postWithVariables(resourceUrl: string, object: any, urlVariables: string[]): vCACCAFEServiceResponse;
	/**
	 * Does a PUT request expanding the given template variables in order.
	 * @param resourceUrl 
	 * @param object 
	 * @param urlVariables 
	 */
	putWithVariables(resourceUrl: string, object: any, urlVariables: string[]): vCACCAFEServiceResponse;
	/**
	 * Does a DELETE request expanding the given template variables in order.
	 * @param resourceUrl 
	 * @param urlVariables 
	 */
	deleteWithVariables(resourceUrl: string, urlVariables: string[]): void;
	/**
	 * Gets the REST client URL.
	 */
	getUrl(): string;
	/**
	 * Indicates whether the current connection is already closed or not.
	 */
	isClosed(): boolean;
	/**
	 * Does a GET request for a file and allows specifying 'Accept' header.
	 * @param resourceUrl 
	 * @param acceptHeaderValue 
	 */
	getFileWithAcceptHeader(resourceUrl: string, acceptHeaderValue: string): any;
	/**
	 * Does a POST request with a file.
	 * @param resourceUrl 
	 * @param object 
	 */
	postFile(resourceUrl: string, object: any): vCACCAFEServiceResponse;
	/**
	 * Does a GET request expanding the given template variables in order.
	 * @param resourceUrl 
	 * @param urlVariables 
	 */
	getWithVariables(resourceUrl: string, urlVariables: string[]): any;
	/**
	 * Does a GET request.
	 * @param resourceUrl 
	 */
	get(resourceUrl: string): any;
	/**
	 * Does a PUT request.
	 * @param resourceUrl 
	 * @param object 
	 */
	put(resourceUrl: string, object: any): vCACCAFEServiceResponse;
	/**
	 * Does a DELETE request.
	 * @param resourceUrl 
	 */
	delete(resourceUrl: string): void;
	/**
	 * Invalidates the current connection.
	 */
	close(): void;
	/**
	 * Does a GET request for a file.
	 * @param resourceUrl 
	 */
	getFile(resourceUrl: string): any;
	/**
	 * Does a POST request.
	 * @param resourceUrl 
	 * @param object 
	 */
	post(resourceUrl: string, object: any): vCACCAFEServiceResponse;
}

declare class vCACCAFEWorkItemCompletionInfo {
	constructor();
	constructor();
	getAction(): vCACCAFEUserAction;
	/**
	 * @param value 
	 */
	setAction(value: vCACCAFEUserAction): void;
	getCompletedBy(): string;
	/**
	 * @param value 
	 */
	setCompletedBy(value: string): void;
	getApprovalRequestId(): string;
	/**
	 * @param value 
	 */
	setApprovalRequestId(value: string): void;
	getCompletionDate(): any;
	/**
	 * @param value 
	 */
	setCompletionDate(value: any): void;
	getFormData(): vCACCAFELiteralMap;
	/**
	 * @param value 
	 */
	setFormData(value: vCACCAFELiteralMap): void;
}

declare interface vCACCAFEWorkItemError {
	value(): string;
	values(): vCACCAFEWorkItemError[];
	/**
	 * @param name 
	 */
	valueOf(name: string): vCACCAFEWorkItemError;
	/**
	 * @param v 
	 */
	fromValue(v: string): vCACCAFEWorkItemError;
}

declare class vCACCAFEWorkItemForms {
	constructor();
	constructor();
	getWorkItemDetails(): any;
	/**
	 * @param value 
	 */
	setWorkItemDetails(value: any): void;
	getWorkItemSubmission(): any;
	/**
	 * @param value 
	 */
	setWorkItemSubmission(value: any): void;
	getWorkItemNotification(): any;
	/**
	 * @param value 
	 */
	setWorkItemNotification(value: any): void;
}

declare class vCACCAFEWorkItemPrincipal {
	constructor();
	constructor();
	/**
	 * @param value 
	 */
	setDisplayName(value: string): void;
	toString(): string;
	getDisplayName(): string;
	getPrincipalId(): string;
	/**
	 * @param value 
	 */
	setPrincipalId(value: string): void;
	getPrincipalType(): vCACCAFEWorkItemPrincipalType;
	/**
	 * @param value 
	 */
	setPrincipalType(value: vCACCAFEWorkItemPrincipalType): void;
}

declare interface vCACCAFEWorkItemPrincipalType {
	value(): string;
	values(): vCACCAFEWorkItemPrincipalType[];
	/**
	 * @param name 
	 */
	valueOf(name: string): vCACCAFEWorkItemPrincipalType;
	/**
	 * @param v 
	 */
	fromValue(v: string): vCACCAFEWorkItemPrincipalType;
}

declare class vCACCAFEWorkItemRequest {
	constructor();
	constructor();
	toString(): string;
	getItemId(): string;
	/**
	 * @param value 
	 */
	setItemId(value: string): void;
	getItemName(): string;
	/**
	 * @param value 
	 */
	setItemName(value: string): void;
	getItemDescription(): string;
	/**
	 * @param value 
	 */
	setItemDescription(value: string): void;
	getItemRequestor(): string;
	/**
	 * @param value 
	 */
	setItemRequestor(value: string): void;
	getItemCost(): number;
	/**
	 * @param value 
	 */
	setItemCost(value: number): void;
	getItemData(): vCACCAFELiteralMap;
	/**
	 * @param value 
	 */
	setItemData(value: vCACCAFELiteralMap): void;
}

declare interface vCACCAFEWorkItemState {
	value(): string;
	values(): vCACCAFEWorkItemState[];
	/**
	 * @param name 
	 */
	valueOf(name: string): vCACCAFEWorkItemState;
	/**
	 * @param v 
	 */
	fromValue(v: string): vCACCAFEWorkItemState;
}

declare class vCACCAFEWorkItemType {
	constructor();
	constructor();
	getVersion(): number;
	getForms(): vCACCAFEWorkItemForms;
	toString(): string;
	getName(): string;
	getActions(): vCACCAFEWorkItemAction[];
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	/**
	 * @param value 
	 */
	setVersion(value: number): void;
	/**
	 * @param value 
	 */
	setForms(value: vCACCAFEWorkItemForms): void;
	getPluralizedName(): string;
	/**
	 * @param value 
	 */
	setPluralizedName(value: string): void;
	isCompleteByEmail(): boolean;
	/**
	 * @param value 
	 */
	setCompleteByEmail(value: boolean): void;
	getCommentsField(): string;
	/**
	 * @param value 
	 */
	setCommentsField(value: string): void;
	getListView(): vCACCAFETableView;
	/**
	 * @param value 
	 */
	setListView(value: vCACCAFETableView): void;
	/**
	 * @param value 
	 */
	setServiceTypeId(value: string): void;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	getServiceTypeId(): string;
}

declare interface vCACCAFEWorkItemWorkItemActionIconService {
	/**
	 * This method invokes the SDK method WorkItemActionIconService.createWorkItemActionIcon(WorkItemActionIcon workItemActionIcon).
	 * @param workItemActionIcon 
	 */
	createWorkItemActionIcon(workItemActionIcon: vCACCAFEWorkItemActionIcon): vCACCAFEWorkItemActionIcon;
	/**
	 * This method invokes the SDK method WorkItemActionIconService.deleteWorkItemActionIcon(String workItemActionIconId).
	 * @param workItemActionIconId 
	 */
	deleteWorkItemActionIcon(workItemActionIconId: string): void;
	/**
	 * This method invokes the SDK method WorkItemActionIconService.getAllWorkItemActionIcons(Pageable pageable).
	 * @param pageable 
	 */
	getAllWorkItemActionIcons(pageable: any): vCACCAFEPagedResources;
	/**
	 * This method invokes the SDK method WorkItemActionIconService.getWorkItemActionIconImage(String workItemActionIconId).
	 * @param workItemActionIconId 
	 */
	getWorkItemActionIconImage(workItemActionIconId: string): any[];
	/**
	 * This method invokes the SDK method WorkItemActionIconService.getWorkItemActionIcon(String workItemActionIconId).
	 * @param workItemActionIconId 
	 */
	getWorkItemActionIcon(workItemActionIconId: string): vCACCAFEWorkItemActionIcon;
	/**
	 * This method invokes the SDK method WorkItemActionIconService.getWorkItemActionIcon(URI uri).
	 * @param uri 
	 */
	getWorkItemActionIconByUri(uri: any): vCACCAFEWorkItemActionIcon;
	/**
	 * This method invokes the SDK method WorkItemActionIconService.updateWorkItemActionIcon(WorkItemActionIcon workItemActionIcon).
	 * @param workItemActionIcon 
	 */
	updateWorkItemActionIcon(workItemActionIcon: vCACCAFEWorkItemActionIcon): void;
}

declare interface vCACCAFEWorkItemWorkItemAssigneeService {
	/**
	 * This method invokes the SDK method WorkItemAssigneeService.deleteDelegatesForAssignee(String principalId).
	 * @param principalId 
	 */
	deleteDelegatesForAssignee(principalId: string): void;
	/**
	 * This method invokes the SDK method WorkItemAssigneeService.deleteDelegatesForCurrentAssignee().
	 */
	deleteDelegatesForCurrentAssignee(): void;
	/**
	 * This method invokes the SDK method WorkItemAssigneeService.getDelegatesForAssignee(String principalId).
	 * @param principalId 
	 */
	getDelegatesForAssignee(principalId: string): vCACCAFEWorkItemAssignee;
	/**
	 * This method invokes the SDK method WorkItemAssigneeService.getDelegatesForCurrentAssignee().
	 */
	getDelegatesForCurrentAssignee(): vCACCAFEWorkItemAssignee;
	/**
	 * This method invokes the SDK method WorkItemAssigneeService.createOrupdateDelegatesForCurrentAssignee(WorkItemAssignee assignee).
	 * @param assignee 
	 */
	createOrupdateDelegatesForCurrentAssignee(assignee: vCACCAFEWorkItemAssignee): vCACCAFEWorkItemAssignee;
	/**
	 * This method invokes the SDK method WorkItemAssigneeService.createOrupdateDelegatesForAssignee(WorkItemAssignee assignee, String principalId).
	 * @param assignee 
	 * @param principalId 
	 */
	createOrupdateDelegatesForAssignee(assignee: vCACCAFEWorkItemAssignee, principalId: string): vCACCAFEWorkItemAssignee;
}

declare interface vCACCAFEWorkItemWorkItemService {
	/**
	 * This method invokes the SDK method WorkItemService.createOrUpdateWorkItems(Collection<WorkItem> workItems).
	 * @param workItems 
	 */
	createOrUpdateWorkItems(workItems: vCACCAFEWorkItem[]): vCACCAFEWorkItem[];
	/**
	 * This method invokes the SDK method WorkItemService.getWorkItem(URI uri).
	 * @param uri 
	 */
	getWorkItemByUri(uri: any): vCACCAFEWorkItem;
	/**
	 * This method invokes the SDK method WorkItemService.updateWorkItem(WorkItem workItem).
	 * @param workItem 
	 */
	updateWorkItem(workItem: vCACCAFEWorkItem): void;
	/**
	 * This method invokes the SDK method WorkItemService.getAllWorkItems(Pageable pageable).
	 * @param pageable 
	 */
	getAllWorkItems(pageable: any): vCACCAFEPagedResources;
	/**
	 * This method invokes the SDK method WorkItemService.getWorkItem(String workItemId).
	 * @param workItemId 
	 */
	getWorkItem(workItemId: string): vCACCAFEWorkItem;
	/**
	 * This method invokes the SDK method WorkItemService.completeWorkItem(WorkItemActionComplete workItemActionComplete).
	 * @param workItemActionComplete 
	 */
	completeWorkItem(workItemActionComplete: vCACCAFEWorkItemActionComplete): void;
	/**
	 * This method invokes the SDK method WorkItemService.cancelWorkItem(WorkItemActionComplete workItemActionComplete).
	 * @param workItemActionComplete 
	 */
	cancelWorkItem(workItemActionComplete: vCACCAFEWorkItemActionComplete): void;
	/**
	 * This method invokes the SDK method WorkItemService.createWorkItem(WorkItem workItem).
	 * @param workItem 
	 */
	createWorkItem(workItem: vCACCAFEWorkItem): vCACCAFEWorkItem;
}

declare interface vCACCAFEWorkItemWorkItemTypeService {
	/**
	 * This method invokes the SDK method WorkItemTypeService.createWorkItemType(WorkItemType workItemType).
	 * @param workItemType 
	 */
	createWorkItemType(workItemType: vCACCAFEWorkItemType): vCACCAFEWorkItemType;
	/**
	 * This method invokes the SDK method WorkItemTypeService.deleteWorkItemType(String workItemTypeId).
	 * @param workItemTypeId 
	 */
	deleteWorkItemType(workItemTypeId: string): void;
	/**
	 * This method invokes the SDK method WorkItemTypeService.getAllWorkItemTypes(Pageable pageable).
	 * @param pageable 
	 */
	getAllWorkItemTypes(pageable: any): vCACCAFEPagedResources;
	/**
	 * This method invokes the SDK method WorkItemTypeService.getWorkItemType(String workItemTypeId).
	 * @param workItemTypeId 
	 */
	getWorkItemType(workItemTypeId: string): vCACCAFEWorkItemType;
	/**
	 * This method invokes the SDK method WorkItemTypeService.getWorkItemType(URI uri).
	 * @param uri 
	 */
	getWorkItemTypeByUri(uri: any): vCACCAFEWorkItemType;
	/**
	 * This method invokes the SDK method WorkItemTypeService.updateWorkItemType(WorkItemType workItemType).
	 * @param workItemType 
	 */
	updateWorkItemType(workItemType: vCACCAFEWorkItemType): void;
}

declare class vCACCAFEWorkflow {
	constructor();
	constructor();
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	getOutputParameters(): vCACCAFEWorkflowParameter[];
	getInputParameters(): vCACCAFEWorkflowParameter[];
	/**
	 * @param value 
	 */
	setId(value: string): void;
}

declare class vCACCAFEWorkflowCategory {
	constructor();
	constructor();
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	getWorkflows(): vCACCAFEWorkflow[];
	getCategories(): vCACCAFEWorkflowCategory[];
	/**
	 * @param value 
	 */
	setId(value: string): void;
}

declare class vCACCAFEWorkflowParameter {
	constructor();
	constructor();
	getName(): string;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getType(): string;
	/**
	 * @param value 
	 */
	setType(value: string): void;
}

declare class vCACCAFEWorkflowSubscription {
	version: vCACCAFEVersion;
	name: string;
	priority: number;
	id: string;
	description: string;
	status: vCACCAFEDesignerPublishStatus;
	tenantId: string;
	reply: boolean;
	blocking: boolean;
	eventTopicId: string;
	vcoId: any;
	statusName: string;
	workflowId: string;
	timeout: number;
	criteria: any;
	constructor();
	constructor();
	getVersion(): vCACCAFEVersion;
	/**
	 * @param obj 
	 */
	equals(obj: any): boolean;
	toString(): string;
	hashCode(): number;
	getName(): string;
	/**
	 * @param priority 
	 */
	setPriority(priority: number): void;
	getPriority(): number;
	/**
	 * @param value 
	 */
	setName(value: string): void;
	getId(): string;
	/**
	 * @param value 
	 */
	setDescription(value: string): void;
	getDescription(): string;
	getStatus(): vCACCAFEDesignerPublishStatus;
	/**
	 * @param value 
	 */
	setVersion(value: vCACCAFEVersion): void;
	getTenantId(): string;
	/**
	 * @param value 
	 */
	setWorkflowId(value: string): void;
	/**
	 * @param value 
	 */
	setReply(value: boolean): void;
	isReply(): boolean;
	isBlocking(): boolean;
	/**
	 * @param value 
	 */
	setBlocking(value: boolean): void;
	getEventTopicId(): string;
	/**
	 * @param value 
	 */
	setEventTopicId(value: string): void;
	getVcoId(): any;
	/**
	 * @param undefined 
	 */
	setVcoId(id: any): void;
	getStatusName(): string;
	/**
	 * @param value 
	 */
	setStatusName(value: string): void;
	getWorkflowId(): string;
	/**
	 * @param timeout 
	 */
	setTimeout(timeout: number): void;
	getTimeout(): number;
	/**
	 * @param value 
	 */
	setId(value: string): void;
	/**
	 * @param value 
	 */
	setStatus(value: vCACCAFEDesignerPublishStatus): void;
	/**
	 * @param value 
	 */
	setTenantId(value: string): void;
	/**
	 * @param criteria 
	 */
	setCriteria(criteria: any): void;
	getCriteria(): any;
}

declare class vCACCAFEWrapperClause {
	constructor();
	/**
	 * @param evaluator 
	 */
	constructor(evaluator: com.vmware.vcac.platform.content.evaluators.Evaluator);
	getEvaluator(): any;
	getConstantValue(): vCACCAFEBooleanLiteral;
	isConstant(): boolean;
	/**
	 * @param bVal 
	 */
	fromBoolean(bVal: boolean): any;
	/**
	 * @param sVal 
	 */
	fromString(sVal: string): any;
	/**
	 * @param lVal 
	 */
	fromInt(lVal: number): any;
	/**
	 * @param dVal 
	 */
	fromDecimal(dVal: number): any;
}

declare class vCACCAFEXaaSComponentConfiguration {
	constructor();
	constructor();
	getOperations(): any[];
	/**
	 * @param value 
	 */
	setCategoryId(value: string): void;
	getCategoryId(): string;
	isScalable(): boolean;
	/**
	 * @param value 
	 */
	setScalable(value: boolean): void;
}
