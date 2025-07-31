/**
 * Represents a VCD host with all its configuration parameters.
 */
declare interface VclHost {
	readonly internalId: string;
	readonly id: string;
	enabled: boolean;
	url: string;
	organization: string;
	sessionMode: VclHostSessionMode;
	useSamlAuthentication: boolean;
	samlTokenCallbackName: string;
	maxConnections: number;
	connectionTimeout: number;
	/**
	 * Gets the socket timeout on this host.
	 */
	getSocketTimeout(): number;
	/**
	 * Sets the socket timeout on this host.
	 * @param socketTimeout 
	 */
	setSocketTimeout(socketTimeout: number): void;
	/**
	 * Login using the current host configuration.
	 */
	login(): void;
	/**
	 * Logout and terminate the session.
	 */
	logout(): void;
	/**
	 * Returns all organizations on this host.
	 */
	getOrganizations(): VclOrganization[];
	/**
	 * Returns all extensions/services on this host.
	 */
	getServices(): VclService[];
	/**
	 * Sets the password to be used when using shared session with BASIC authentication.
	 * @param password 
	 */
	setPassword(password: string): void;
	/**
	 * Sets the username to be used when using shared session with BASIC authentication.
	 * @param username 
	 */
	setUsername(username: string): void;
	/**
	 * returns the username to be used when using shared session with BASIC authentication.
	 */
	getUsername(): string;
	/**
	 * Sets the action that will provide the SAML token when using shared session with SAML authentication.
	 * @param callback 
	 */
	setSamlTokenCallback(callback: Action): void;
	/**
	 * Returns an entity by its finder type and reference.
	 * @param finderType 
	 * @param reference 
	 */
	getEntityByReference(finderType: string, reference: VclReference): any;
	/**
	 * Returns an entity by its finder type and id.
	 * @param finderType 
	 * @param id 
	 */
	getEntityById(finderType: string, id: string): any;
	/**
	 * Returns an entity by its finder type and href.
	 * @param finderType 
	 * @param href 
	 */
	getEntityByHref(finderType: string, href: string): any;
	/**
	 * Returns an entity by its finder type and reference without resolving it.
	 * @param finderType 
	 * @param reference 
	 */
	wrapReference(finderType: string, reference: VclReference): any;
	/**
	 * Returns a VclQueryService instance. Use this instance to execute queries in the cloud.
	 */
	getQueryService(): VclQueryService;
	/**
	 * Sends a HTTP GET request.
	 * @param url 
	 */
	get(url: string): string;
	/**
	 * Sends a HTTP POST request.
	 * NOTE: Calling this method does not affect the state of the inventory or the inventory objects.
	 * 
	 * @param url 
	 * @param content 
	 * @param contentType 
	 * @param charset 
	 */
	post(url: string, content: string, contentType: string, charset: string): string;
	/**
	 * Sends a HTTP PUT request.
	 * NOTE: Calling this method does not affect the state of the inventory or the inventory objects.
	 * 
	 * @param url 
	 * @param content 
	 * @param contentType 
	 * @param charset 
	 */
	put(url: string, content: string, contentType: string, charset: string): string;
	/**
	 * Sends a HTTP DELETE request.
	 * NOTE: Calling this method does not affect the state of the inventory or the inventory objects.
	 * 
	 * @param url 
	 */
	delete(url: string): string;
	/**
	 * Returns the Admin version of the object.
	 */
	toAdminObject(): any;
	/**
	 * Clears the state of all resolved objects on this host.
	 */
	updateInternalState(): void;
	/**
	 * Get the VCD API Version.
	 */
	getApiVersion(): string;
	/**
	 * Validate VCD API Version.
	 * @param selectedApiVersion 
	 */
	validateApiVersion(selectedApiVersion: string): string;
	/**
	 * Fetch folder name
	 * @param parentFolderName 
	 * @param parentEntityName 
	 */
	fetchFolderName(parentFolderName: string, parentEntityName: string): string[];
	/**
	 * Fetch folder entities name
	 * @param parentType 
	 * @param parentId 
	 * @param childType 
	 */
	fetchFolderEntitiesName(parentType: string, parentId: string, childType: string): string[];
	/**
	 * Fetch folder entities name
	 * @param parentType 
	 * @param parentId 
	 * @param childType 
	 */
	fetchFolderEntityObjects(parentType: string, parentId: string, childType: string): any;
	/**
	 * execute rest queries
	 * @param org 
	 * @param href 
	 * @param contentType 
	 * @param method 
	 * @param body 
	 */
	executeRestQueries(org: VclOrganization, href: string, contentType: string, method: string, body: string): string;
	/**
	 * Convert to object for provided xml body
	 * @param xmlBody 
	 * @param org 
	 */
	toObjectForXML(xmlBody: string, org: VclOrganization): any;
	/**
	 * Convert to String for provided object
	 * @param entity 
	 */
	toXMLFromObject(entity: any): string;
	/**
	 * Get Link href for provided relation type
	 * @param entity 
	 * @param mediaType 
	 * @param relationType 
	 */
	getUrlForType(entity: any, mediaType: string, relationType: string): string[];
	/**
	 * decode href from artificial id
	 * @param value 
	 */
	decode(value: string): string;
	/**
	 * encode href for artificial id
	 * @param value 
	 */
	encode(value: string): string;
}

/**
 * Represents a VCD host for administrative tasks.
 */
declare interface VclHostAdmin {
	/**
	 * Returns all admin organizations on this host.
	 */
	getAdminOrganizations(): VclAdminOrganization[];
	/**
	 * Returns all external networks on this host.
	 */
	getExternalNetworks(): VclExternalNetwork[];
	/**
	 * Returns all provider vDCs on this host.
	 */
	getProviderVdcs(): VclProviderVdc[];
	/**
	 * Returns all roles on this host.
	 */
	getRoles(): VclRole[];
	/**
	 * Returns all rights on this host.
	 */
	getRights(): VclRight[];
	/**
	 * Adds a new admin organization to this host.
	 * @param params 
	 */
	addAdminOrg(params: VclAdminOrgParams): any;
	/**
	 * Adds a new role to this host.
	 * @param params 
	 */
	addRole(params: VclRoleParams): VclRole;
	/**
	 * Returns a VclAdminQueryService instance. Use this instance to execute queries in the cloud.
	 */
	getAdminQueryService(): VclAdminQueryService;
	/**
	 * Returns the User version of the object.
	 */
	toUserObject(): VclHost;
	/**
	 * Returns the Admin Extension version of the object.
	 */
	toAdminExtensionObject(): VclAdminExtension;
	/**
	 * Clears the state of all resolved objects on this host.
	 */
	updateInternalState(): void;
	/**
	 * Returns all children in this container.
	 */
	getContainedObjects(): any[];
}

/**
 * Manages the configuration of VCD hosts.
 */
declare class VclHostManager {
	/**
	 * Creates a host. This is the entry point to VCD.
	 */
	static createHost(): VclHost;
	/**
	 * Returns a host by its internal id.
	 * @param id 
	 */
	static getHostByInternalId(id: string): VclHost;
	/**
	 * Returns a host by its URL, username and organization.
	 * @param url 
	 * @param username 
	 * @param organization 
	 */
	static getHostByUrl(url: string, username: string, organization: string): VclHost;
	/**
	 * Returns all hosts managed by this host manager.
	 */
	static getHostList(): VclHost[];
	/**
	 * Add new host(s) to the hosts managed by this host manager.
	 * @param hosts 
	 * @param failOnAddDuplicated 
	 */
	static addOrUpdateHosts(hosts: VclHost[], failOnAddDuplicated: boolean): void;
	/**
	 * Removes host(s) from the hosts managed by this host manager.
	 * @param hosts 
	 */
	static removeHosts(hosts: VclHost[]): void;
	/**
	 * Returns the plug-in version.
	 */
	static getVersion(): string;
}

/**
 * A Catalog resource is a container for CatalogItems. An Org may contain zero or more Catalog resources.
 */
declare interface VclCatalog {
	isPublished: boolean;
	dateCreated: VclXMLGregorianCalendar;
	versionNumber: number;
	owner: VclOwner;
	description: string;
	name: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly otherAttributes: VclMap;
	readonly vCloudExtension: VclObjectList;
	readonly tasks: VclTasksInProgress;
	readonly parent: VclOrganization;
	/**
	 * Returns all catalog items of this catalog.
	 */
	getCatalogItems(): VclCatalogItem[];
	/**
	 * Add a new catalog item to this catalog.
	 * @param params 
	 */
	addCatalogItem(params: any): VclCatalogItem;
	/**
	 * Returns object's metadata.
	 */
	getMetadata(): VclMetadata;
	/**
	 * Returns the Admin version of the object.
	 */
	toAdminObject(): VclAdminCatalog;
	/**
	 * Gets the object's reference.
	 */
	getReference(): VclReference;
	/**
	 * Gets the host that this object belongs to.
	 */
	getHost(): VclHost;
	/**
	 * Reloads the object from the server
	 */
	updateInternalState(): void;
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Uploading the ovf package as a vapp template to the catalog. Make sure that the ovf file, manifest file(if any) and its corresponding vmdk
	 * files are residing in the same location.
	 * @param vAppTemplateName 
	 * @param vAppTemplateDesc 
	 * @param localOvfFileLocation 
	 * @param manifestRequired 
	 * @param vdcStorageRef 
	 */
	uploadVappTemplate(vAppTemplateName: string, vAppTemplateDesc: string, localOvfFileLocation: string, manifestRequired: boolean, vdcStorageRef: VclReference): VclVAppTemplate;
	/**
	 * ###
	 * @param name 
	 * @param description 
	 * @param sourceRef 
	 */
	copyCatalogItem(name: string, description: string, sourceRef: VclCatalogItem): VclTask;
	/**
	 * ###
	 * @param name 
	 * @param description 
	 * @param sourceRef 
	 */
	moveCatalogItem(name: string, description: string, sourceRef: VclCatalogItem): VclTask;
	/**
	 * Returns all children in this container.
	 */
	getContainedObjects(): any[];
}

/**
 * Representation of Audit Event.
 */
declare interface VclAuditEvent {
	org: VclReference;
	details: string;
	serviceNamespace: string;
	user: VclReference;
	owner: VclReference;
	timeStamp: VclXMLGregorianCalendar;
	eventType: string;
	success: boolean;
	description: string;
	name: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly otherAttributes: VclMap;
	readonly vCloudExtension: VclObjectList;
	/**
	 * Returns the object's metadata.
	 */
	getMetadata(): VclMetadata;
	/**
	 * Gets the object's reference.
	 */
	getReference(): VclReference;
	/**
	 * Reloads the object from the server
	 */
	updateInternalState(): void;
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Returns all children in this container.
	 */
	getContainedObjects(): any[];
}

/**
 * Admin representation of a catalog.
 */
declare interface VclAdminCatalog {
	catalogStorageProfiles: VclCatalogStorageProfiles;
	externalCatalogSubscriptionParams: VclExternalCatalogSubscriptionParams;
	publishExternalCatalogParams: VclPublishExternalCatalogParams;
	isPublished: boolean;
	dateCreated: VclXMLGregorianCalendar;
	versionNumber: number;
	owner: VclOwner;
	description: string;
	name: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	readonly tasks: VclTasksInProgress;
	readonly parent: VclAdminOrganization;
	/**
	 * Returns all catalog items of this admin catalog.
	 */
	getCatalogItems(): VclCatalogItem[];
	/**
	 * Adds a catalog item to this admin catalog.
	 * @param params 
	 */
	addCatalogItem(params: VclCatalogItemParams): VclCatalogItem;
	/**
	 * Updates this admin catalog.
	 */
	update(): VclAdminCatalog;
	/**
	 * Deletes this admin catalog.
	 */
	erase(): void;
	/**
	 * Publishes this admin catalog to other organizations.
	 * NOTE: Catalog publishing should be enabled for the organization that contains this catalog.
	 * @param publish 
	 */
	publishCatalog(publish: boolean): void;
	/**
	 * Returns the object's metadata.
	 */
	getMetadata(): VclMetadata;
	/**
	 * Changes the owner of this catalog.
	 * @param user 
	 */
	changeOwner(user: VclReference): void;
	/**
	 * Returns the User version of the object.
	 */
	toUserObject(): VclCatalog;
	/**
	 * Gets the object's reference.
	 */
	getReference(): VclReference;
	/**
	 * Gets the host that this object belongs to.
	 */
	getHost(): VclHost;
	/**
	 * Reloads the object from the server
	 */
	updateInternalState(): void;
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Subscribe to an external catalog
	 * @param externalCatalogSubscription 
	 */
	subscribeToExternalCatalog(externalCatalogSubscription: VclExternalCatalogSubscriptionParams): void;
	/**
	 * Publish a catalog to external organizations
	 * @param publishExternalCatalogParams 
	 */
	publishToExternalOrganizations(publishExternalCatalogParams: VclPublishExternalCatalogParams): void;
	/**
	 * Returns all children in this container.
	 */
	getContainedObjects(): any[];
}

/**
 * The CatalogItem element is a container for a reference to a catalogued entity such as a media or a template.
 */
declare interface VclCatalogItem {
	dateCreated: VclXMLGregorianCalendar;
	versionNumber: number;
	entity: VclReference;
	readonly property: VclObjectList;
	size: number;
	description: string;
	name: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly otherAttributes: VclMap;
	readonly vCloudExtension: VclObjectList;
	readonly tasks: VclTasksInProgress;
	readonly parent: VclCatalog;
	/**
	 * Updates this catalog item.
	 */
	update(): VclCatalogItem;
	/**
	 * Deletes this catalog item.
	 */
	erase(): void;
	/**
	 * Returns the object's metadata.
	 */
	getMetadata(): VclMetadata;
	/**
	 * Gets the object's reference.
	 */
	getReference(): VclReference;
	/**
	 * Gets the host that this object belongs to.
	 */
	getHost(): VclHost;
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Returns all children in this container.
	 */
	getContainedObjects(): any[];
}

/**
 * The Media element is used as the body of a request to upload virtual media, such as an ISO or floppy image. It is also used in the response.
 */
declare interface VclMedia {
	vdcStorageProfile: VclReference;
	imageType: string;
	owner: VclOwner;
	size: number;
	files: VclFilesList;
	status: number;
	description: string;
	name: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly otherAttributes: VclMap;
	readonly vCloudExtension: VclObjectList;
	isPartOfCatalogItem: boolean;
	readonly parent: VclVdc;
	readonly tasks: VclTasksInProgress;
	/**
	 * Updates this media.
	 */
	update(): VclTask;
	/**
	 * Deletes this media.
	 */
	erase(): VclTask;
	/**
	 * Uploads a file once the media is created.
	 * @param filename 
	 * @param alias 
	 */
	uploadFile(filename: string, alias: string): void;
	/**
	 * Uploads a file chunk once the media is created.
	 * @param filename 
	 * @param alias 
	 * @param startByte 
	 * @param endByte 
	 */
	uploadFileChunk(filename: string, alias: string, startByte: number, endByte: number): void;
	/**
	 * Reloads the object from the server
	 */
	updateInternalState(): void;
	/**
	 * Returns the catalog item that contains the media.
	 */
	getCatalogItem(): VclCatalogItem;
	/**
	 * Returns the object's metadata.
	 */
	getMetadata(): VclMetadata;
	/**
	 * Gets the object's reference.
	 */
	getReference(): VclReference;
	/**
	 * Gets the host that this object belongs to.
	 */
	getHost(): VclHost;
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Returns all children in this container.
	 */
	getContainedObjects(): any[];
}

/**
 * Sites detail.
 */
declare interface VclSiteObject {
	multiSiteUrl: string;
	siteAssociations: VclSiteAssociations;
	restEndpoint: string;
	baseUiEndpoint: string;
	tenantUiEndpoint: string;
	restEndpointCertificate: string;
	description: string;
	name: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly otherAttributes: VclMap;
	readonly vCloudExtension: VclObjectList;
	/**
	 * Returns all organizations for Site.
	 */
	getOrganizations(): VclOrganization[];
}

/**
 * An Organization element is a high-level abstraction that provides a container and a unit of administration for a collection of resources and users.
 */
declare interface VclOrganization {
	isEnabled: boolean;
	fullName: string;
	description: string;
	name: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly otherAttributes: VclMap;
	readonly vCloudExtension: VclObjectList;
	readonly tasks: VclTasksInProgress;
	readonly parent: VclHost;
	/**
	 * Returns all catalogs of this organization.
	 */
	getCatalogs(): VclCatalog[];
	/**
	 * Returns all VCDs of this organization.
	 */
	getVdcs(): VclVdc[];
	/**
	 * Returns the access settings for a catalog.
	 * @param ref 
	 */
	getCatalogControlAccessByReference(ref: VclReference): VclControlAccessParams;
	/**
	 * Updates the access settings for a catalog.
	 * @param ref 
	 * @param params 
	 */
	updateCatalogControlAccessByReference(ref: VclReference, params: VclControlAccessParams): VclControlAccessParams;
	/**
	 * Returns the object's metadata.
	 */
	getMetadata(): VclMetadata;
	/**
	 * Creates a custom task.
	 * @param params 
	 */
	createTask(params: VclTaskParams): VclTask;
	/**
	 * Reloads the object from the server
	 */
	updateInternalState(): void;
	/**
	 * Get the enable or disable status of Org
	 */
	getOrgStatus(): boolean;
	/**
	 * Returns the Admin version of the object.
	 */
	toAdminObject(): VclAdminOrganization;
	/**
	 * Gets the object's reference.
	 */
	getReference(): VclReference;
	/**
	 * Gets the host that this object belongs to.
	 */
	getHost(): VclHost;
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Returns all children in this container.
	 */
	getContainedObjects(): any[];
	/**
	 * Instantiates a VDC template. The caller supplies the VDC template and the name and description, which are the only two things the tenant can override in the template.
	 * @param instantiateVdcTemplateParams 
	 */
	instantiateVdcTemplate(instantiateVdcTemplateParams: VclInstantiateVdcTemplateParams): VclTask;
}

/**
 * The AdminOrg element provides an administrative view of an Organization. It includes all members of the Org element, and adds several elements that can be viewed and modified only by system administrators.
 */
declare interface VclAdminOrganization {
	catalogs: VclCatalogsList;
	vdcs: VclVdcs;
	vdcTemplates: VclVdcTemplates;
	networks: VclNetworks;
	orgAssociations: VclOrgAssociations;
	rightReferences: VclOrganizationRights;
	roleReferences: VclOrganizationRoles;
	roleTemplateReferences: VclRoleReferences;
	settings: VclOrgSettings;
	isEnabled: boolean;
	fullName: string;
	description: string;
	name: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly otherAttributes: VclMap;
	readonly vCloudExtension: VclObjectList;
	readonly tasks: VclTasksInProgress;
	readonly parent: VclHost;
	/**
	 * Returns orgAssociations for the specified admin Organization.
	 */
	getOrgAssociations(): VclOrgAssociations;
	/**
	 * Update orgAssociations
	 * @param orgAssociations 
	 */
	updateOrgAssociations(orgAssociations: VclOrgAssociations): VclTask;
	/**
	 * >Add orgAssociation to adminOrganization.
	 * @param orgAssociation 
	 */
	addOrgAssociation(orgAssociation: VclOrgAssociation): VclTask;
	/**
	 * Get orgAssociation by Name.
	 * @param orgAssociationName 
	 */
	getOrgAssociationByName(orgAssociationName: string): VclOrgAssociation;
	/**
	 * Delete orgAssociation by Name.
	 * @param orgAssociationName 
	 */
	deleteOrgAssociationByName(orgAssociationName: string): void;
	/**
	 * Returns all admin catalogs of this admin organization.
	 */
	getAdminCatalogs(): VclAdminCatalog[];
	/**
	 * Adds a new catalog to this admin organization.
	 * @param params 
	 */
	addAdminCatalog(params: VclAdminCatalogParams): VclAdminCatalog;
	/**
	 * Returns all admin VCDs of this admin organization.
	 */
	getAdminVdcs(): VclAdminVdc[];
	/**
	 * Adds a new admin vDC to this admin organization.
	 * @param params 
	 */
	addAdminVdc(params: VclCreateVdcParams): VclAdminVdc;
	/**
	 * Adds a new admin vDC to this admin organization.
	 * @param params 
	 */
	addAdminVdc15(params: VclAdminVdcParams): VclAdminVdc;
	/**
	 * Adds a new group to this admin organization.
	 * @param params 
	 */
	addGroup(params: VclGroupParams): VclGroup;
	/**
	 * Returns all groups of this admin organization.
	 */
	getGroups(): VclGroup[];
	/**
	 * Returns all roles of this admin organization.
	 */
	getRoles(): VclRole[];
	/**
	 * Adds a new user to this admin organization.
	 * @param params 
	 */
	addUser(params: VclUserParams): VclUser;
	/**
	 * Returns all users of this admin organization.
	 */
	getUsers(): VclUser[];
	/**
	 * Returns a user by its name.
	 * @param name 
	 */
	getUserByName(name: string): VclUser;
	/**
	 * Updates this admin organization.
	 */
	update(): VclAdminOrganization;
	/**
	 * Updates the settings of this admin organization.
	 * @param settings 
	 */
	updateSettings(settings: VclOrgSettings): void;
	/**
	 * Deletes this admin organization.
	 */
	erase(): void;
	/**
	 * Returns the object's metadata.
	 */
	getMetadata(): VclMetadata;
	/**
	 * Enable this admin organization.
	 */
	enable(): void;
	/**
	 * Disables this admin organization.
	 */
	disable(): void;
	/**
	 * Creates a custom event.
	 * @param event 
	 */
	createEvent(event: VclEvent): void;
	/**
	 * Resets organization LDAP SSL certificate.
	 */
	resetLdapCertificate(): void;
	/**
	 * Updates organization LDAP SSL certificate.
	 * @param params 
	 */
	updateLdapCertificate(params: VclCertificateUpdateParams): VclCertificateUploadSocket;
	/**
	 * Resets organization LDAP keystore.
	 */
	resetLdapKeyStore(): void;
	/**
	 * Updates organization LDAP keystore.
	 * @param params 
	 */
	updateLdapKeyStore(params: VclKeystoreUpdateParams): VclKeystoreUploadSocket;
	/**
	 * Resets organization LDAP keystore.
	 */
	resetLdapSspiKeytab(): void;
	/**
	 * Updates organization LDAP SSPI keytab.
	 * @param params 
	 */
	updateLdapSspiKeytab(params: VclSspiKeytabUpdateParams): VclSspiKeytabUploadSocket;
	/**
	 * Reloads the object from the server
	 */
	updateInternalState(): void;
	/**
	 * Returns the User version of the object.
	 */
	toUserObject(): VclOrganization;
	/**
	 * Gets the object's reference.
	 */
	getReference(): VclReference;
	/**
	 * Gets the host that this object belongs to.
	 */
	getHost(): VclHost;
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Returns all children in this container.
	 */
	getContainedObjects(): any[];
	/**
	 * Regenerates the certificates used to establish trust between an organization and its identity provider.
	 */
	regenerateFederationCertificate(): void;
}

/**
 * A provider vDC is a collection of all the resources available. A provider vDC is created by the vCloud service provider using tools that are specific to the host platform. In this release, a Provider vDC is created by a vSphere administrator using vSphere tools.
 */
declare interface VclProviderVdc {
	status: number;
	computeCapacity: VclRootComputeCapacity;
	storageCapacity: VclProviderVdcCapacity;
	availableNetworks: VclAvailableNetworks;
	storageProfiles: VclProviderVdcStorageProfiles;
	capabilities: VclCapabilities;
	vdcs: VclVdcs;
	isEnabled: boolean;
	networkPoolReferences: VclNetworkPoolReferences;
	name: string;
	description: string;
	id: string;
	operationKey: string;
	type: string;
	href: string;
	readonly otherAttributes: VclMap;
	readonly vCloudExtension: VclObjectList;
	readonly tasks: VclTasksInProgress;
	readonly parent: VclHostAdmin;
	/**
	 * Returns the object's metadata.
	 */
	getMetadata(): VclMetadata;
	/**
	 * Returns all admin vDCs of this provider vDC.
	 */
	getAdminVdcs(): VclAdminVdc[];
	/**
	 * Returns all external networks of this provider vDC.
	 */
	getExternalNetworks(): VclExternalNetwork[];
	/**
	 * Returns all VMW network pools of this provider vDC.
	 */
	getVMWNetworkPools(): VclVMWNetworkPool[];
	/**
	 * Returns all storage profiles of this provider vDC.
	 */
	getProviderStorageProfiles(): VclProviderVdcStorageProfile[];
	/**
	 * Returns the admin extension object for this object.
	 */
	toAdminExtensionObject(): VclVMWProviderVdc;
	/**
	 * Returns the object's reference.
	 */
	getReference(): VclReference;
	/**
	 * Gets the host that this object belongs to.
	 */
	getHost(): VclHost;
	/**
	 * Reloads the object from the server
	 */
	updateInternalState(): void;
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Returns all children in this container.
	 */
	getContainedObjects(): any[];
}

/**
 * External network representation.
 */
declare interface VclExternalNetwork {
	providerInfo: string;
	readonly networkBackingInfo: VclList;
	configuration: VclNetworkConfiguration;
	description: string;
	name: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly otherAttributes: VclMap;
	readonly vCloudExtension: VclObjectList;
	readonly tasks: VclTasksInProgress;
	readonly parent: VclHostAdmin;
	/**
	 * Resets this external network.
	 */
	reset(): VclTask;
	/**
	 * Returns the object's metadata.
	 */
	getMetadata(): VclMetadata;
	/**
	 * Returns the admin extension object for this object.
	 */
	toAdminExtensionObject(): VclVMWExternalNetwork;
	/**
	 * TBS
	 */
	getReference(): VclReference;
	/**
	 * Gets the host that this object belongs to.
	 */
	getHost(): VclHost;
	/**
	 * Reloads the object from the server
	 */
	updateInternalState(): void;
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Returns all children in this container.
	 */
	getContainedObjects(): any[];
}

/**
 * A User entity defines a user.
 */
declare interface VclUser {
	isEnabled: boolean;
	groupReferences: VclGroupsList;
	fullName: string;
	emailAddress: string;
	telephone: string;
	isLocked: boolean;
	iM: string;
	nameInSource: string;
	isAlertEnabled: boolean;
	alertEmailPrefix: string;
	alertEmail: string;
	isExternal: boolean;
	providerType: string;
	isDefaultCached: boolean;
	isGroupRole: boolean;
	storedVmQuota: number;
	deployedVmQuota: number;
	password: string;
	role: VclReference;
	description: string;
	name: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly otherAttributes: VclMap;
	readonly vCloudExtension: VclObjectList;
	readonly tasks: VclTasksInProgress;
	readonly parent: VclAdminOrganization;
	/**
	 * Updates this user.
	 */
	update(): VclUser;
	/**
	 * Deletes this user.
	 */
	erase(): void;
	/**
	 * Enables or disables this user.
	 * @param isEnable 
	 */
	enable(isEnable: boolean): VclUser;
	/**
	 * Unlocks this user.
	 */
	unlock(): void;
	/**
	 * Returns all groups that this user belongs to.
	 */
	getGroups(): VclGroup[];
	/**
	 * Returns all rights granted to this user.
	 */
	getGrantedRights(): VclRight[];
	/**
	 * Returns all rights that this user has on a given entity.
	 * @param entities 
	 */
	getEntityRights(entities: VclReference[]): VclEntityRights[];
	/**
	 * Gets the object's reference.
	 */
	getReference(): VclReference;
	/**
	 * Gets the host that this object belongs to.
	 */
	getHost(): VclHost;
	/**
	 * Reloads the object from the server
	 */
	updateInternalState(): void;
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Returns all children in this container.
	 */
	getContainedObjects(): any[];
	/**
	 * Takes ownership of specified user's vApps, media, and catalogs..
	 */
	takeOwnership(): void;
}

/**
 * The Role element contains a single RightReferences  element, which is a container for RightReference  elements.
 */
declare interface VclRole {
	rightReferences: VclRightReferences;
	description: string;
	name: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly otherAttributes: VclMap;
	readonly vCloudExtension: VclObjectList;
	readonly tasks: VclTasksInProgress;
	readonly parent: VclHostAdmin;
	/**
	 * Updates this role.
	 */
	update(): VclRole;
	/**
	 * Deletes this role.
	 */
	erase(): void;
	/**
	 * Gets the object's reference.
	 */
	getReference(): VclReference;
	/**
	 * Gets the host that this object belongs to.
	 */
	getHost(): VclHost;
	/**
	 * Reloads the object from the server.
	 */
	updateInternalState(): void;
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Returns all children in this container.
	 */
	getContainedObjects(): any[];
}

/**
 * A Group entity defines a group.
 */
declare interface VclGroup {
	nameInSource: string;
	providerType: string;
	usersList: VclUsersList;
	role: VclReference;
	description: string;
	name: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly otherAttributes: VclMap;
	readonly vCloudExtension: VclObjectList;
	readonly tasks: VclTasksInProgress;
	readonly parent: VclAdminOrganization;
	/**
	 * Returns a collection of all the users.
	 */
	getUsers(): VclUser[];
	/**
	 * Updates this group.
	 */
	update(): VclGroup;
	/**
	 * Deletes this group.
	 */
	erase(): void;
	/**
	 * Gets the object's reference.
	 */
	getReference(): VclReference;
	/**
	 * Gets the host that this object belongs to.
	 */
	getHost(): VclHost;
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Returns all children in this container.
	 */
	getContainedObjects(): any[];
}

/**
 * Right elements are read-only to users and adminsitrators, and can be retrieved with a GET operation that specifies the URL in a RightReference.
 */
declare interface VclRight {
	serviceNamespace: string;
	rightType: string;
	category: string;
	bundleKey: string;
	description: string;
	name: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly otherAttributes: VclMap;
	readonly vCloudExtension: VclObjectList;
	readonly tasks: VclTasksInProgress;
	readonly parent: VclHostAdmin;
	/**
	 * Deletes this right.
	 */
	erase(): void;
	/**
	 * Gets the object's reference.
	 */
	getReference(): VclReference;
	/**
	 * Gets the host that this object belongs to.
	 */
	getHost(): VclHost;
	/**
	 * Reloads the object from the server
	 */
	updateInternalState(): void;
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * ###
	 * @param vCloudRoleId 
	 */
	addToRole(vCloudRoleId: string): VclRole;
	/**
	 * ###
	 * @param vCloudRoleId 
	 */
	deleteFromRole(vCloudRoleId: string): VclRole;
	/**
	 * Returns all children in this container.
	 */
	getContainedObjects(): any[];
}

/**
 * Whenever the result of a request cannot be returned immediately, the server creates a task entity and returns its URL to the client. The client can use this URL in a subsequent GET request to obtain the current status of the task.
 */
declare interface VclTask {
	progress: number;
	operationName: string;
	details: string;
	serviceNamespace: string;
	user: VclReference;
	cancelRequested: boolean;
	endTime: VclXMLGregorianCalendar;
	expiryTime: VclXMLGregorianCalendar;
	operation: string;
	owner: VclReference;
	startTime: VclXMLGregorianCalendar;
	error: VclError;
	organization: VclReference;
	status: string;
	params: any;
	result: VclResult;
	description: string;
	name: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly otherAttributes: VclMap;
	readonly vCloudExtension: VclObjectList;
	/**
	 * Reloads the object from the server
	 */
	updateInternalState(): void;
	/**
	 * Creates a trigger for this task. Once created the trigger can be used to efficiently wait for the task.
	 * @param timeout 
	 */
	createEndOfTaskTrigger(timeout: number): any;
	/**
	 * Cancels the task.
	 */
	cancel(): void;
	/**
	 * Updates the task.
	 * @param params 
	 */
	update(params: VclTaskParams): VclTask;
	/**
	 * Waits for the task to complete.
	 * @param timeout 
	 * @param period 
	 */
	join(timeout: number, period: number): void;
	/**
	 * Returns true if the task is a blocking task request.
	 */
	getIsBlockingTask(): boolean;
	/**
	 * Returns the blocking task for the task.
	 */
	getBlockingTask(): VclBlockingTask;
	/**
	 * Gets the host that this object belongs to.
	 */
	getHost(): VclHost;
	/**
	 * Returns the parameters used when starting the task.
	 * @param type 
	 */
	getParams(type: any): any;
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Returns all children in this container.
	 */
	getContainedObjects(): any[];
}

/**
 * A collection of tasks currently in progress.
 */
declare interface VclTasksInProgress {
	/**
	 * Returns tasks currently in progress.
	 */
	getTasks(): VclTask[];
}

/**
 * A VApp is the result of instantiation of a VAppTemplate.
 */
declare interface VclVApp {
	inMaintenanceMode: boolean;
	autoNature: boolean;
	ovfDescriptorUploaded: boolean;
	owner: VclOwner;
	vAppParent: VclReference;
	readonly section: VclAbstractObjectSet;
	deployed: boolean;
	dateCreated: VclXMLGregorianCalendar;
	files: VclFilesList;
	status: number;
	description: string;
	name: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly otherAttributes: VclMap;
	readonly vCloudExtension: VclObjectList;
	readonly vAppSize: VclBigInteger;
	readonly vappStatus: VclVappStatus;
	readonly tasks: VclTasksInProgress;
	readonly parent: VclVdc;
	/**
	 * Powers On this vApp.
	 */
	powerOn(): VclTask;
	/**
	 * Powers Off this vApp.
	 */
	powerOff(): VclTask;
	/**
	 * Deletes this vApp.
	 */
	erase(): VclTask;
	/**
	 * Deploys this vApp.
	 * @param powerOn 
	 * @param lease 
	 * @param forceCustomization 
	 */
	deploy(powerOn: boolean, lease: number, forceCustomization: boolean): VclTask;
	/**
	 * Undeploys this vApp.
	 * @param undeployPowerActionType 
	 */
	undeploy(undeployPowerActionType: VclUndeployPowerActionType): VclTask;
	/**
	 * Reboots this vApp.
	 */
	reboot(): VclTask;
	/**
	 * Resets this vApp.
	 */
	reset(): VclTask;
	/**
	 * Suspends this vApp.
	 */
	suspend(): VclTask;
	/**
	 * Discards the suspended state of a vApp.
	 */
	discardSuspendedState(): void;
	/**
	 * TBS
	 */
	shutdown(): VclTask;
	/**
	 * Shutdowns this vApp.
	 */
	update(): VclTask;
	/**
	 * Recomposes this vApp.
	 * @param params 
	 */
	recompose(params: VclRecomposeVAppParams): VclTask;
	/**
	 * Changes the owner of this vApp.
	 * @param userRef 
	 */
	changeOwner(userRef: VclReference): void;
	/**
	 * Disables maintenance mode for this vApp.
	 */
	disableMaintenance(): void;
	/**
	 * Enables maintenance mode for this vApp.
	 */
	enableMaintenance(): void;
	/**
	 * Returns the network configurations for this vApp.
	 */
	getVappNetworkConfigurations(): VclVAppNetworkConfiguration[];
	/**
	 * Returns the networks for this vApp.
	 */
	getVAppNetworks(): VclVAppNetwork[];
	/**
	 * Resets a network of this vApp.
	 * @param networkName 
	 */
	resetVAppNetwork(networkName: string): VclTask;
	/**
	 * Returns the VIM object ref of a vApp network.
	 */
	getNetworkVimRef(): VclVimObjectRef;
	/**
	 * Synchronizes the syslog server settings with the system defaults.
	 * @param vAppNetworkName 
	 */
	syncSyslogServer(vAppNetworkName: string): VclTask;
	/**
	 * Returns the object's metadata.
	 */
	getMetadata(): VclMetadata;
	/**
	 * Returns the lease settings section of this vApp.
	 */
	getLeaseSettingsSection(): VclLeaseSettingsSection;
	/**
	 * Returns the startup section of this vApp.
	 */
	getStartUpSection(): VclStartupSection;
	/**
	 * Returns the network section of this vApp.
	 */
	getNetworkSection(): VclNetworkSection;
	/**
	 * Returns the network config section of this vApp.
	 */
	getNetworkConfigSection(): VclNetworkConfigSection;
	/**
	 * Updates the specified section of this vApp.
	 * @param section 
	 */
	updateSection(section: any): VclTask;
	/**
	 * Returns the control access settings for this vApp.
	 */
	getControlAccess(): VclControlAccessParams;
	/**
	 * Updates the control access settings for this vApp.
	 * @param params 
	 */
	updateControlAccess(params: VclControlAccessParams): VclControlAccessParams;
	/**
	 * Returns the names of all networks of this vApp.
	 */
	getNetworkNames(): string[];
	/**
	 * Returns all children vApps of this vApp.
	 */
	getChildrenVApps(): VclVApp[];
	/**
	 * Returns all children Vms of this vApp.
	 */
	getChildrenVms(): VclVM[];
	/**
	 * Reloads the object from the server
	 */
	updateInternalState(): void;
	/**
	 * Gets the object's reference.
	 */
	getReference(): VclReference;
	/**
	 * Gets the host that this object belongs to.
	 */
	getHost(): VclHost;
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Returns the ProductSections of a vApp/VM
	 */
	getProductSections(): VclProductSection[];
	/**
	 * Update the product sections of a vApp/VM
	 * @param productSections 
	 */
	updateProductSections(productSections: VclProductSection[]): VclTask;
	/**
	 * Enable a vApp for download
	 */
	enableDownload(): VclTask;
	/**
	 * Disable a vApp for download
	 */
	disableDownload(): void;
	/**
	 * Download the vApp as an ovf package. The ovf file and its vmdk contents are downloaded to the specified location.
	 * Before downloading make sure the vapp is enabled for download.
	 */
	download(): void;
	/**
	 * ###
	 */
	getSnapshotSection(): VclSnapshotSection;
	/**
	 * ###
	 * @param name 
	 * @param description 
	 * @param memory 
	 * @param quiesce 
	 */
	createSnapshot(name: string, description: string, memory: boolean, quiesce: boolean): VclTask;
	/**
	 * ###
	 */
	reverToCurrentSnapshot(): VclTask;
	/**
	 * ###
	 */
	removeAllSnapshots(): VclTask;
	/**
	 * Returns all children in this container.
	 */
	getContainedObjects(): any[];
	/**
	 * ###
	 * @param config 
	 */
	addVAppNetwork(config: VclVAppNetworkConfiguration): VclTask;
}

/**
 * A VAppTemplate element is the created when you upload an OVF package to a vDC. It provides an immutable description of a vApp.
 */
declare interface VclVAppTemplate {
	vAppScopedLocalId: string;
	readonly section: VclAbstractObjectSet;
	ovfDescriptorUploaded: boolean;
	dateCreated: VclXMLGregorianCalendar;
	defaultStorageProfile: string;
	goldMaster: boolean;
	owner: VclOwner;
	files: VclFilesList;
	status: number;
	description: string;
	name: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly otherAttributes: VclMap;
	readonly vCloudExtension: VclObjectList;
	readonly vAppTemplateSize: VclBigInteger;
	readonly vappTemplateStatus: VclVappTemplateStatus;
	isVm: boolean;
	isPartOfCatalogItem: boolean;
	readonly vmVimRef: VclVimObjectRef;
	readonly vmHostVimRef: VclVimObjectRef;
	readonly vmDatastoreVimRef: VclVimObjectRef;
	readonly vmDiskChainLength: number;
	readonly parent: VclVdc;
	readonly tasks: VclTasksInProgress;
	/**
	 * Returns all children VAppTemplate.
	 */
	getChildren(): VclVAppTemplate[];
	/**
	 * Deletes this vApp template.
	 */
	erase(): VclTask;
	/**
	 * Updates this vApp template.
	 */
	update(): VclTask;
	/**
	 * Downloads a file from this vApp template.
	 * @param alias 
	 * @param filename 
	 */
	downloadFile(alias: string, filename: string): void;
	/**
	 * Uploads a file of this vApp template.
	 * @param filename 
	 * @param alias 
	 */
	uploadFile(filename: string, alias: string): void;
	/**
	 * Uploads a file chunk of this vApp template.
	 * @param filename 
	 * @param alias 
	 * @param startByte 
	 * @param endByte 
	 */
	uploadFileChunk(filename: string, alias: string, startByte: number, endByte: number): void;
	/**
	 * Downloads the OVF file of this vApp template.
	 * @param filename 
	 */
	downloadOVFFile(filename: string): void;
	/**
	 * Download lossless vAppTemplate OVF. Lossless download mode generates ovf without loosing any of its configurations.
	 * @param filename 
	 */
	downloadLosslessOVF(filename: string): void;
	/**
	 * Uploads the OVF file of this vApp template.
	 * @param filename 
	 */
	uploadOVFFile(filename: string): string;
	/**
	 * Uploads the OVF envelope of this vApp template.
	 * @param envelope 
	 */
	uploadOVFEnvelope(envelope: VclEnvelope): void;
	/**
	 * Disables the download of this vApp template.
	 */
	disableDownload(): void;
	/**
	 * Enables the download of this vApp template.
	 */
	enableDownload(): VclTask;
	/**
	 * Returns the download URL of this vApp template's OVF.
	 */
	getOVFDownloadURL(): string;
	/**
	 * Returns the OVF envelope of this vApp template.
	 */
	getOVF(): VclEnvelope;
	/**
	 * Returns the names of all files that should be uploaded.
	 */
	getUploadFileNames(): string[];
	/**
	 * Returns the paths of all files that should be uploaded.
	 * @param ovfBasePath 
	 */
	getUploadFilePaths(ovfBasePath: string): string[];
	/**
	 * Returns the names of all files that can be downloaded.
	 */
	getDownloadFileNames(): string[];
	/**
	 * Reloads the object from the server
	 */
	updateInternalState(): void;
	/**
	 * Returns the network connection section of this vApp template.
	 */
	getNetworkConnectionSection(): VclNetworkConnectionSection;
	/**
	 * Returns the network section of this vApp template.
	 */
	getNetworkSection(): VclNetworkSection;
	/**
	 * Returns the network config section of this vApp template.
	 */
	getNetworkConfigSection(): VclNetworkConfigSection;
	/**
	 * Returns the lease settings section of this vApp template.
	 */
	getLeaseSettingsSection(): VclLeaseSettingsSection;
	/**
	 * Returns the customization section of this vApp template.
	 */
	getCustomizationSection(): VclCustomizationSection;
	/**
	 * Returns the guest customization section of this vApp template.
	 */
	getGuestCustomizationSection(): VclGuestCustomizationSection;
	/**
	 * Updates the specified section of this vApp template.
	 * @param section 
	 */
	updateSection(section: any): VclTask;
	/**
	 * Returns the list of child vAppTemplates.
	 */
	getContainedObjects(): VclVAppTemplate[];
	/**
	 * Returns a collection of shadow VM references.
	 */
	getShadowVmReferences(): VclReference[];
	/**
	 * Returns the vDC storage profile reference.
	 */
	getStorageProfileReference(): VclReference;
	/**
	 * Consolidates this vApp template.
	 */
	consolidate(): VclTask;
	/**
	 * Returns the catalog item that contains this vApp template.
	 */
	getCatalogItem(): VclCatalogItem;
	/**
	 * Returns a reference to the user that owns this vApp template.
	 */
	getOwner(): VclReference;
	/**
	 * Relocates this VApp template to a different datastore.
	 * @param datastoreRef 
	 */
	relocate(datastoreRef: VclReference): VclTask;
	/**
	 * Updates/Sets the template as goldmaster.
	 * @param value 
	 */
	updateGoldMaster(value: boolean): VclTask;
	/**
	 * Returns the Virtual Machine VIM Object reference.
	 */
	getVMVimRef(): VclVimObjectRef;
	/**
	 * Returns the Virtual Machine Host VIM Object reference.
	 */
	getVMHostVimRef(): VclVimObjectRef;
	/**
	 * Returns the Virtual Machine Datastore VIM Object reference.
	 */
	getVMDatastoreVimRef(): VclVimObjectRef;
	/**
	 * Returns the disks of a fast provisioned VM link to parent disks in a chain, and this number is the depth of this VMs disks in that chain. A chain length of 1 indicates that all the disks are flat and don't refer to parent disks.
	 */
	getVMDiskChainLength(): number;
	/**
	 * Returns the object's metadata.
	 */
	getMetadata(): VclMetadata;
	/**
	 * Gets the object's reference.
	 */
	getReference(): VclReference;
	/**
	 * Gets the host that this object belongs to.
	 */
	getHost(): VclHost;
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Returns the ProductSections of a vAppTemplate
	 */
	getProductSections(): VclProductSection[];
	/**
	 * Returns the Virtual hardware section of this vApp template VM.
	 */
	getVirtualHardwareSection(): VclVirtualHardwareSection;
	/**
	 * Returns the Virtual CPU of this vApp template VM.
	 */
	getVirtualCpu(): VclVirtualCpu;
	/**
	 * Returns the Virtual memory of  this vApp template VM.
	 */
	getVirtualMemory(): VclVirtualMemory;
	/**
	 * Returns all Virtual Network cards of this vApp template VM.
	 */
	getVirtualNetworkCards(): VclVirtualNetworkCard[];
	/**
	 * Returns all the Virtual media of this vApp template VM.
	 */
	getVirtualMedias(): VclVirtualMedia[];
	/**
	 * Returns all Virtual hard disks and hard disk controllers of this vApp template VM.
	 */
	getVirtualDisks(): VclVirtualDisk[];
}

/**
 * A vDC resource describes a deployment environment for vApps.
 */
declare interface VclVdc {
	computeProviderScope: string;
	isEnabled: boolean;
	networkProviderScope: string;
	nicQuota: number;
	vmQuota: number;
	allocationModel: string;
	storageCapacity: VclCapacityWithUsage;
	computeCapacity: VclComputeCapacity;
	resourceEntities: VclResourceEntities;
	availableNetworks: VclAvailableNetworks;
	capabilities: VclCapabilities;
	networkQuota: number;
	usedNetworkCount: number;
	vdcStorageProfiles: VclVdcStorageProfiles;
	defaultComputePolicy: VclReference;
	vCpuInMhz2: number;
	status: number;
	description: string;
	name: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly otherAttributes: VclMap;
	readonly vCloudExtension: VclObjectList;
	readonly tasks: VclTasksInProgress;
	readonly parent: VclOrganization;
	/**
	 * Creates an instance of a vApp template on this vDC.
	 * @param params 
	 */
	instantiateVAppTemplate(params: VclInstantiateVAppTemplateParams): VclVApp;
	/**
	 * Clones a vApp template on this vDC.
	 * @param params 
	 */
	cloneVAppTemplate(params: VclCloneVAppTemplateParams): VclVAppTemplate;
	/**
	 * Creates a vApp template on this vDC.
	 * @param params 
	 */
	createVAppTemplate(params: VclUploadVAppTemplateParams): VclVAppTemplate;
	/**
	 * Uploads a vApp template on this vDC
	 * @param vAppTemplateName 
	 * @param vAppTemplateDesc 
	 * @param localOvfFileLocation 
	 * @param manifestRequired 
	 * @param storageProfileRef 
	 * @param catalogRef 
	 */
	uploadVappTemplate(vAppTemplateName: string, vAppTemplateDesc: string, localOvfFileLocation: string, manifestRequired: boolean, storageProfileRef: VclReference, catalogRef: VclReference): VclVAppTemplate;
	/**
	 * Captures a vApp to a appTemplate on this vDC.
	 * @param params 
	 */
	captureVApp(params: VclCaptureVAppParams): VclVAppTemplate;
	/**
	 * Clones a vApp to on this vDC.
	 * @param params 
	 */
	cloneVApp(params: VclCloneVAppParams): VclVApp;
	/**
	 * Composes a vApp to on this vDC.
	 * @param params 
	 */
	composeVApp(params: VclComposeVAppParams): VclVApp;
	/**
	 * Uploads a media to this vDC.
	 * @param name 
	 * @param description 
	 * @param imageType 
	 * @param storageProfileRef 
	 * @param filename 
	 */
	uploadMedia(name: string, description: string, imageType: VclImageType, storageProfileRef: VclReference, filename: string): VclMedia;
	/**
	 * Clones a media to this vDC.
	 * @param params 
	 */
	cloneMedia(params: VclCloneMediaParams): VclMedia;
	/**
	 * Creates a Disk on this vDC.
	 * @param params 
	 */
	createDisk(params: VclDiskCreateParams): VclDisk;
	/**
	 * Returns the object's metadata.
	 */
	getMetadata(): VclMetadata;
	/**
	 * Returns all Org vDC networks of this vDC.
	 */
	getOrgVdcNetworks(): VclOrgVdcNetwork[];
	/**
	 * Returns all media objects of this vDC.
	 */
	getMedias(): VclMedia[];
	/**
	 * Returns all vApps of this vDC.
	 */
	getVApps(): VclVApp[];
	/**
	 * Returns all vApp templates of this vDC.
	 */
	getVAppTemplates(): VclVAppTemplate[];
	/**
	 * Returns all storage profiles of this vDC.
	 */
	getStorageProfiles(): VclVdcStorageProfile[];
	/**
	 * Returns all disks of this vDC.
	 */
	getDisks(): VclDisk[];
	/**
	 * Returns the Admin version of the object.
	 */
	toAdminObject(): VclAdminVdc;
	/**
	 * Gets the object's reference.
	 */
	getReference(): VclReference;
	/**
	 * Gets the host that this object belongs to.
	 */
	getHost(): VclHost;
	/**
	 * Reloads the object from the server
	 */
	updateInternalState(): void;
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Uploading the ovf package as a vapp. Make sure that the ovf file, and its corresponding vmdk files are residing in the same location.
	 * @param ovfParams 
	 * @param localOvfFileLocation 
	 */
	uploadVapp(ovfParams: VclInstantiateOvfParams, localOvfFileLocation: string): VclVApp;
	/**
	 * Returns all children in this container.
	 */
	getContainedObjects(): any[];
	/**
	 * Modify the name and/or description of an organization vDC.
	 */
	update(): VclTask;
	/**
	 * Delete an organization vDC.
	 */
	erase(): VclTask;
	/**
	 * Returns VM affinity/anti-affinity Rules for the specified vdc.
	 */
	getVmAffinityRules(): VclVmAffinity[];
	/**
	 * >Create a VM affinity/anti-affinity Rule for the specified vdc.
	 * @param vmAffinityRule 
	 */
	createVmAffinityRule(vmAffinityRule: VclVmAffinityRule): VclTask;
}

/**
 * A vm affinity resource describes a vm affinity/anti-affinity rule for a given vDC.
 */
declare interface VclVmAffinity {
	vmReferences: VclVms;
	scope: string;
	isEnabled: boolean;
	isMandatory: boolean;
	polarity: string;
	name: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly otherAttributes: VclMap;
	readonly vCloudExtension: VclObjectList;
	readonly parent: VclVdc;
	/**
	 * Retrieves a specific Vm Affinity/Anti-affinity rule for a given vDC.
	 */
	getVmAffinityRule(): VclVmAffinityRule;
	/**
	 * Updates a specific Vm Affinity/Anti-affinity rule for a given vDC.
	 * @param vmAffinityRule 
	 */
	update(vmAffinityRule: VclVmAffinityRule): VclTask;
	/**
	 * Deletes a specific Vm Affinity/Anti-affinity rule for a given vDC.
	 */
	erase(): VclTask;
}

/**
 * The AdminVdc resource provides an administrative view of a a vDC. It includes all members of the vDC element, and adds several elements that can be viewed and modified only by system administrators.
 */
declare interface VclAdminVdc {
	providerVdcReference: VclReference;
	networkPoolReference: VclReference;
	resourcePoolRefs: VclVimObjectRefs;
	usesFastProvisioning: boolean;
	overCommitAllowed: boolean;
	vmDiscoveryEnabled: boolean;
	resourceGuaranteedMemory: any;
	resourceGuaranteedCpu: any;
	vCpuInMhz: number;
	isThinProvision: boolean;
	vendorServices: VclVendorServices;
	universalNetworkPoolReference: VclReference;
	computeProviderScope: string;
	isEnabled: boolean;
	networkProviderScope: string;
	nicQuota: number;
	vmQuota: number;
	allocationModel: string;
	storageCapacity: VclCapacityWithUsage;
	computeCapacity: VclComputeCapacity;
	resourceEntities: VclResourceEntities;
	availableNetworks: VclAvailableNetworks;
	capabilities: VclCapabilities;
	networkQuota: number;
	usedNetworkCount: number;
	vdcStorageProfiles: VclVdcStorageProfiles;
	defaultComputePolicy: VclReference;
	vCpuInMhz2: number;
	status: number;
	description: string;
	name: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly otherAttributes: VclMap;
	readonly vCloudExtension: VclObjectList;
	readonly tasks: VclTasksInProgress;
	readonly parent: VclAdminOrganization;
	/**
	 * Returns all media objects of this admin vDC.
	 */
	getMedias(): VclMedia[];
	/**
	 * Returns all vApps of this admin vDC.
	 */
	getVApps(): VclVApp[];
	/**
	 * Returns all vApp templates of this admin vDC.
	 */
	getVAppTemplates(): VclVAppTemplate[];
	/**
	 * Returns all storage profiles of this admin vDC.
	 */
	getStorageProfiles(): VclAdminVdcStorageProfile[];
	/**
	 * Returns all gateways of this admin vDC.
	 */
	getGateways(): VclGateway[];
	/**
	 * Returns all Org vDC networks of this admin vDC.
	 */
	getOrgVdcNetworks(): VclAdminOrgVdcNetwork[];
	/**
	 * Returns all disks of this admin vDC.
	 */
	getDisks(): VclDisk[];
	/**
	 * Returns all VMW network pools of this admin vDC.
	 */
	getNetworkPool(): VclVMWNetworkPool;
	/**
	 * Returns the resource pool VIM ref.
	 */
	getResourcePoolVimRef(): VclVimObjectRef;
	/**
	 * Returns the resource pool VIM refs.
	 */
	getResourcePoolVimRefs(): VclVimObjectRef[];
	/**
	 * Deletes this admin vDC.
	 */
	erase(): VclTask;
	/**
	 * Updates this admin vDC.
	 */
	update(): VclTask;
	/**
	 * Get the enable or disable status of Vdc
	 */
	getVdcStatus(): boolean;
	/**
	 * Updates the storage profiles of this admin vDC.
	 * @param classesToAdd 
	 * @param classesToRemove 
	 */
	updateStorageProfiles(classesToAdd: VclVdcStorageProfileParams[], classesToRemove: VclReference[]): VclTask;
	/**
	 * Creates a gateway on this admin vDC.
	 * @param params 
	 */
	createGateway(params: VclGatewayParams): VclGateway;
	/**
	 * Creates an Org vDC network on this admin vDC.
	 * @param params 
	 */
	createOrgVdcNetwork(params: VclOrgVdcNetworkParams): VclAdminOrgVdcNetwork;
	/**
	 * Enable or Disable the Vdc.
	 */
	enable(): void;
	/**
	 * Disables this admin vDC.
	 */
	disable(): void;
	/**
	 * Disables the fast provisioning on this admin vDC.
	 */
	disableFastProvisioning(): VclTask;
	/**
	 * Enables the fast provisioning on this admin vDC.
	 */
	enableFastProvisioning(): VclTask;
	/**
	 * Returns the object's metadata.
	 */
	getMetadata(): VclMetadata;
	/**
	 * Returns the User version of the object.
	 */
	toUserObject(): VclVdc;
	/**
	 * Gets the object's reference.
	 */
	getReference(): VclReference;
	/**
	 * Gets the host that this object belongs to.
	 */
	getHost(): VclHost;
	/**
	 * Reloads the object from the server
	 */
	updateInternalState(): void;
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Returns all children in this container.
	 */
	getContainedObjects(): any[];
}

/**
 * Represents the CPU of a virtual machine.
 */
declare interface VclVirtualCpu {
	readonly itemResource: VclRASD;
	noOfCpus: number;
	/**
	 * ###
	 */
	getNoOfCpus(): number;
	/**
	 * ###
	 * @param value 
	 */
	setNoOfCpus(value: number): void;
	/**
	 * ###
	 */
	getCoresPerSocket(): number;
	/**
	 * ###
	 * @param coresPerSocket 
	 */
	setCoresPerSocket(coresPerSocket: number): void;
}

/**
 * Represents a hard disk and a hard disk controller of a virtual machine.
 */
declare class VclVirtualDisk {
	readonly itemResource: VclRASD;
	readonly hardDiskSize: VclBigInteger;
	readonly hardDiskBusType: string;
	constructor();
	/**
	 * TBS
	 * @param virtualDiskItem 
	 */
	constructor(virtualDiskItem: VclRASD);
	/**
	 * Returns 'true' if this is a hard disk and 'false' if this is a hard disk controller.
	 */
	isHardDisk(): boolean;
	/**
	 * Updates the size of this hard disk.
	 * @param size 
	 */
	updateHardDiskSize(size: VclBigInteger): void;
	/**
	 * Overrides the storage profile of the relevant VM.
	 * @param state 
	 */
	overrideStorageVmProfile(state: boolean): void;
	/**
	 * Updates the storage profile of this virtual disk.
	 * @param referenceType 
	 */
	updateStorageProfile(referenceType: VclReference): void;
}

/**
 * Represents a media attached to a virtual machine.
 */
declare interface VclVirtualMedia {
	readonly itemResource: VclRASD;
}

/**
 * Represents the memory of a virtual machine.
 */
declare interface VclVirtualMemory {
	readonly itemResource: VclRASD;
	memorySize: VclBigInteger;
}

/**
 * Represents a NIC of a virtual machine.
 */
declare class VclVirtualNetworkCard {
	readonly itemResource: VclRASD;
	readonly macAddress: string;
	readonly ipAddress: string;
	readonly ipAddressingMode: string;
	readonly network: string;
	readonly connected: boolean;
	readonly primaryNetworkConnection: boolean;
	constructor();
	/**
	 * TBS
	 * @param virtualNetworkCardItem 
	 */
	constructor(virtualNetworkCardItem: VclRASD);
	/**
	 * TBS
	 * @param nicId 
	 * @param isConnected 
	 * @param networkName 
	 * @param isPrimaryNetworkConnection 
	 * @param ipAddressingMode 
	 * @param ipAddress 
	 * @param adapterType 
	 */
	constructor(nicId: number, isConnected: boolean, networkName: string, isPrimaryNetworkConnection: boolean, ipAddressingMode: VclIpAddressAllocationModeType, ipAddress: string, adapterType: VclNetworkAdapterType);
	/**
	 * Resets the MAC address of this NIC.
	 */
	resetMacAddress(): void;
	/**
	 * Updates the IP address of this NIC.
	 * @param ipAddress 
	 */
	updateIpAddress(ipAddress: string): void;
	/**
	 * Updates the network of this NIC.
	 * @param networkName 
	 */
	updateNetwork(networkName: string): void;
}

/**
 * TBS
 */
declare interface VclVM {
	storageProfile: VclReference;
	vAppScopedLocalId: string;
	vdcComputePolicy: VclReference;
	vmCapabilities: VclVmCapabilities;
	needsCustomization: boolean;
	environment: VclEnvironment;
	bootOptions: VclBootOptions;
	media: VclReference;
	nestedHypervisorEnabled: boolean;
	vAppParent: VclReference;
	readonly section: VclAbstractObjectSet;
	deployed: boolean;
	dateCreated: VclXMLGregorianCalendar;
	files: VclFilesList;
	status: number;
	description: string;
	name: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly otherAttributes: VclMap;
	readonly vCloudExtension: VclObjectList;
	readonly vmSize: VclBigInteger;
	readonly vmStatus: VclVMStatus;
	readonly tasks: VclTasksInProgress;
	readonly parent: VclVApp;
	/**
	 * Returns the status description for VM
	 */
	getVmStatusDescription(): string;
	/**
	 * Powers On this virtual machine.
	 */
	powerOn(): VclTask;
	/**
	 * Powers Off this virtual machine.
	 */
	powerOff(): VclTask;
	/**
	 * Deletes this virtual machine.
	 */
	erase(): VclTask;
	/**
	 * Deploys this virtual machine.
	 * @param powerOn 
	 * @param lease 
	 * @param forceCustomization 
	 */
	deploy(powerOn: boolean, lease: number, forceCustomization: boolean): VclTask;
	/**
	 * Returns the size of VM
	 */
	getVmSize(): VclBigInteger;
	/**
	 * Undeploys this virtual machine.
	 * @param undeployPowerActionType 
	 */
	undeploy(undeployPowerActionType: VclUndeployPowerActionType): VclTask;
	/**
	 * Reboots this virtual machine.
	 */
	reboot(): VclTask;
	/**
	 * Resets this virtual machine.
	 */
	reset(): VclTask;
	/**
	 * Updates a virtual machine. Sections not included in the request body will not be updated.
	 * @param params 
	 */
	reconfigure(params: VclVmParams): VclTask;
	/**
	 * Suspends this virtual machine.
	 */
	suspend(): VclTask;
	/**
	 * Inserts media in this virtual machine.
	 * @param params 
	 */
	insertMedia(params: VclMediaInsertOrEjectParams): VclTask;
	/**
	 * Ejects media from this virtual machine.
	 * @param params 
	 */
	ejectMedia(params: VclMediaInsertOrEjectParams): VclTask;
	/**
	 * Discards the suspended state of this virtual machine.
	 */
	discardSuspendedState(): void;
	/**
	 * Shuts down this virtual machine.
	 */
	shutdown(): VclTask;
	/**
	 * Updates this virtual machine.
	 */
	update(): VclTask;
	/**
	 * Consolidates this virtual machine.
	 */
	consolidate(): VclTask;
	/**
	 * Installs VMware Tools on this virtual machine..
	 */
	installVMwareTools(): VclTask;
	/**
	 * Relocates this virtual machine to a different datastore.
	 * @param datastoreRef 
	 */
	relocate(datastoreRef: VclReference): VclTask;
	/**
	 * Attaches a disk to this virtual machine.
	 * @param diskRef 
	 */
	attachDisk(diskRef: VclReference): VclTask;
	/**
	 * Detaches a disk from this virtual machine.
	 * @param diskRef 
	 */
	detachDisk(diskRef: VclReference): VclTask;
	/**
	 * Returns all disks attached to this virtual machine.
	 */
	getAttachedDisks(): VclDisk[];
	/**
	 * Enables nested hypervisor on this virtual machine.
	 */
	enableNestedHypervisor(): VclTask;
	/**
	 * Disables nested hypervisor on this virtual machine.
	 */
	disableNestedHypervisor(): VclTask;
	/**
	 * Upgrade virtual hardware version of a VM to the highest supported virtual hardware version of provider vDC where the VM locates.
	 */
	upgradeHardware(): VclTask;
	/**
	 * Returns object's metadata.
	 */
	getMetadata(): VclMetadata;
	/**
	 * Returns the network connection section of this virtual machine.
	 */
	getNetworkConnectionSection(): VclNetworkConnectionSection;
	/**
	 * Returns the virtual hardware section of this virtual machine.
	 */
	getVirtualHardwareSection(): VclVirtualHardwareSection;
	/**
	 * Returns the operating system section of this virtual machine.
	 */
	getOperatingSystemSection(): VclOperatingSystemSection;
	/**
	 * Returns the guest customization section of this virtual machine.
	 */
	getGuestCustomizationSection(): VclGuestCustomizationSection;
	/**
	 * Returns the runtime info section of this virtual machine.
	 */
	getRuntimeInfoSection(): VclRuntimeInfoSection;
	/**
	 * Returns the platform section of this virtual machine.
	 */
	getPlatformSection(): VclPlatformSection;
	/**
	 * Returns the property section of this virtual machine.
	 */
	getPropertySection(): VclPropertySection;
	/**
	 * Updates the specified section.
	 * @param section 
	 */
	updateSection(section: any): VclTask;
	/**
	 * Reloads the object from the server
	 */
	updateInternalState(): void;
	/**
	 * Returns the pending question for this virtual machine.
	 */
	getVmQuestion(): VclVmPendingQuestion;
	/**
	 * Answers the pending question for this virtual machine.
	 * @param choiceId 
	 * @param questionId 
	 */
	answerVmQuestion(choiceId: number, questionId: string): void;
	/**
	 * Creates a snapshot of this virtual machine.
	 * @param filename 
	 */
	getScreen(filename: string): void;
	/**
	 * Returns a ticket for this virtual machine.
	 */
	acquireTicket(): VclScreenTicket;
	/**
	 * Returns the CPU of this virtual machine.
	 */
	getCpu(): VclVirtualCpu;
	/**
	 * Updates the CPU of this virtual machine.
	 * @param cpu 
	 */
	updateCpu(cpu: VclVirtualCpu): VclTask;
	/**
	 * Returns all hard disks and hard disk controllers of this virtual machine.
	 */
	getDisks(): VclVirtualDisk[];
	/**
	 * Updates the hard disks and hard disk controllers of this virtual machine.
	 * @param disks 
	 */
	updateDisks(disks: VclVirtualDisk[]): VclTask;
	/**
	 * Returns the memory of this virtual machine.
	 */
	getMemory(): VclVirtualMemory;
	/**
	 * Updates the memory of this virtual machine.
	 * @param memory 
	 */
	updateMemory(memory: VclVirtualMemory): VclTask;
	/**
	 * Returns all NICs of this virtual machine.
	 */
	getNetworkCards(): VclVirtualNetworkCard[];
	/**
	 * Updates the NICs of this virtual machine.
	 * @param nics 
	 */
	updateNetworkCards(nics: VclVirtualNetworkCard[]): VclTask;
	/**
	 * Returns all media of this virtual machine.
	 */
	getMedias(): VclVirtualMedia[];
	/**
	 * Enable/Disable the hot add options for this virtual machine.
	 * @param memoryHotAdd 
	 * @param cpuHotAdd 
	 */
	updateHotAdd(memoryHotAdd: boolean, cpuHotAdd: boolean): VclTask;
	/**
	 * Returns the virtual machine VIM Object reference.
	 */
	getVMVimRef(): VclVimObjectRef;
	/**
	 * Returns the virtual machine Host VIM Object reference.
	 */
	getVMHostVimRef(): VclVimObjectRef;
	/**
	 * Returns the virtual machine Datastore VIM Object reference.
	 */
	getVMDatastoreVimRef(): VclVimObjectRef;
	/**
	 * Checks whether the VMware tools installed or not.
	 */
	getIsVMwareToolsInstalled(): boolean;
	/**
	 * Returns the disks of a fast provisioned VM link to parent disks in a chain, and this number is the depth of this VMs disks in that chain. A chain length of 1 indicates that all the disks are flat and don't refer to parent disks.
	 */
	getVMDiskChainLength(): number;
	/**
	 * Gets the object's reference.
	 */
	getReference(): VclReference;
	/**
	 * Gets the host that this object belongs to.
	 */
	getHost(): VclHost;
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Returns the ProductSections of a virtual machine
	 */
	getProductSections(): VclProductSection[];
	/**
	 * Update the product sections of a virtual machine
	 * @param productSections 
	 */
	updateProductSections(productSections: VclProductSection[]): VclTask;
	/**
	 * Perform storage profile compliance check on a virtual machine.
	 */
	checkCompliance(): VclTask;
	/**
	 * Returns the storage profile compliance result on a virtual machine.
	 */
	getComplianceResult(): VclComplianceResult;
	/**
	 * ###
	 * @param name 
	 * @param description 
	 * @param memory 
	 * @param quiesce 
	 */
	createSnapshot(name: string, description: string, memory: boolean, quiesce: boolean): VclTask;
	/**
	 * ###
	 */
	getSnapshotSection(): VclSnapshotSection;
	/**
	 * ###
	 */
	reverToCurrentSnapshot(): VclTask;
	/**
	 * ###
	 */
	removeAllSnapshots(): VclTask;
	/**
	 * Returns all children in this container.
	 */
	getContainedObjects(): any[];
	/**
	 * Returns the Current Usage Metric of this vApp Vm.
	 */
	getCurrentUsage(): VclCurrentUsage;
	/**
	 * Returns the Historic Usage Metric of this vApp Vm.
	 */
	getHistoricUsage(): VclHistoricUsage;
	/**
	 * Returns the Current Usage Metric of this vApp Vm using CurrentUsageSpec.
	 * @param currentUsageSpec 
	 */
	getCurrentUsageUsingSpec(currentUsageSpec: VclCurrentUsageSpec): VclCurrentUsage;
	/**
	 * Returns the Historic Usage Metric of this vApp Vm using HistoricUsageSpec.
	 * @param historicUsageSpec 
	 */
	getHistoricUsageUsingSpec(historicUsageSpec: VclHistoricUsageSpec): VclHistoricUsage;
	/**
	 * Reloads this vAPP VM state from VC.
	 */
	reloadFromVC(): VclTask;
	/**
	 * Force guest customization for this vAPP VM on next power on.
	 */
	forceGuestCustomization(): void;
}

/**
 * vApp network representation.
 */
declare interface VclVAppNetwork {
	deployed: boolean;
	configuration: VclNetworkConfiguration;
	description: string;
	name: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly otherAttributes: VclMap;
	readonly vCloudExtension: VclObjectList;
	readonly tasks: VclTasksInProgress;
	readonly parent: VclVApp;
	/**
	 * Resets the vApp network.
	 */
	reset(): VclTask;
	/**
	 * Reloads the object from the server.
	 */
	updateInternalState(): void;
	/**
	 * Gets the object's reference.
	 */
	getReference(): VclReference;
	/**
	 * Gets the host that this object belongs to.
	 */
	getHost(): VclHost;
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Returns all children in this container.
	 */
	getContainedObjects(): any[];
	/**
	 * delete vapp Network
	 */
	remove(): VclTask;
	/**
	 * set dhcp service on a vapp network
	 * @param dhcpService 
	 */
	setupDhcpService(dhcpService: VclDhcpService): VclTask;
	/**
	 * set firewall service on a vapp network
	 * @param firewallService 
	 */
	setupFirewallService(firewallService: VclFirewallService): VclTask;
	/**
	 * add firewall rule on a vapp network
	 * @param FirewallRule 
	 */
	addFirewallRule(FirewallRule: VclFirewallRule): VclTask;
	/**
	 * delete all firewall rules of a vapp network
	 */
	deleteFirewallRules(): VclTask;
	/**
	 * set nat service on a vapp network
	 * @param natService 
	 */
	setupNatService(natService: VclNatService): VclTask;
	/**
	 * add firewall rule on a vapp network
	 * @param NatRule 
	 */
	addNatRule(NatRule: VclNatRule): VclTask;
	/**
	 * delete all nat rules of a vapp network
	 */
	deleteNatRules(): VclTask;
	/**
	 * set static routing service on a vapp network
	 * @param staticRoutingService 
	 */
	setupStaticRoutingService(staticRoutingService: VclStaticRoutingService): VclTask;
	/**
	 * delete all static routes of a vapp network
	 */
	deleteStaticRoutes(): VclTask;
}

/**
 * Disk representation.
 */
declare interface VclDisk {
	storageProfile: VclReference;
	busSubType: string;
	busType: string;
	iops: number;
	owner: VclOwner;
	size: number;
	files: VclFilesList;
	status: number;
	description: string;
	name: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly otherAttributes: VclMap;
	readonly vCloudExtension: VclObjectList;
	readonly tasks: VclTasksInProgress;
	readonly parent: VclVdc;
	/**
	 * Gets the object's metadata.
	 */
	getMetadata(): VclMetadata;
	/**
	 * Gets the object's reference.
	 */
	getReference(): VclReference;
	/**
	 * Gets the host that this object belongs to.
	 */
	getHost(): VclHost;
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Updates this disk's parameters.
	 * @param name 
	 * @param description 
	 * @param storageProfile 
	 */
	updateDisk(name: string, description: string, storageProfile: VclVdcStorageProfile): VclTask;
	/**
	 * Deletes this disk.
	 */
	erase(): VclTask;
	/**
	 * Changes the owner of this disk.
	 * @param value 
	 */
	changeOwner(value: VclReference): void;
	/**
	 * Returns the vm references to which this disk is attached to.
	 */
	getAttachedVms(): VclReference[];
	/**
	 * Reloads the object from the server
	 */
	updateInternalState(): void;
	/**
	 * Returns all children in this container.
	 */
	getContainedObjects(): any[];
}

/**
 * Admin extension entry point.
 */
declare interface VclAdminExtension {
	readonly href: string;
	/**
	 * Returns all VMW VdcTemplate in this VCD instance.
	 */
	getVMWVdcTemplates(): VclVMWVdcTemplate[];
	/**
	 * Returns all VIM servers in this VCD instance.
	 */
	getVimServers(): VclVimServer[];
	/**
	 * Returns all VIM datastores in this VCD instance.
	 */
	getVMWDatastores(): VclVMWDatastore[];
	/**
	 * Returns all VMW hosts in this VCD instance.
	 */
	getVMWHosts(): VclVMWHost[];
	/**
	 * Returns all VMW external networks in this VCD instance.
	 */
	getVMWExternalNetworks(): VclVMWExternalNetwork[];
	/**
	 * Returns all VMW network pools in this VCD instance.
	 */
	getVMWNetworkPools(): VclVMWNetworkPool[];
	/**
	 * Returns all VMW provider vDCs in this VCD instance.
	 */
	getVMWProviderVdcs(): VclVMWProviderVdc[];
	/**
	 * Returns all licensing reports in this VCD instance.
	 */
	getLicensingReports(): VclLicensingReport[];
	/**
	 * Returns all stranded items in this VCD instance.
	 */
	getStrandedItems(): VclStrandedItem[];
	/**
	 * Returns all extensions/services in this VCD instance.
	 */
	getServices(): VclAdminService[];
	/**
	 * Returns all blocking tasks in this VCD instance.
	 */
	getBlockingTasks(): VclBlockingTask[];
	/**
	 * Registers a VIM server.
	 * @param vimServer 
	 * @param shieldManager 
	 */
	registerVimServer(vimServer: VclVimServerParams, shieldManager: VclShieldManagerParams): VclVimServer;
	/**
	 * Creates a VMW provider vDC.
	 * @param providerVdcParams 
	 */
	createVMWProviderVdc(providerVdcParams: VclVMWProviderVdcParams): VclVMWProviderVdc;
	/**
	 * Creates a VMW external network.
	 * @param externalNetworkParams 
	 */
	createVMWExternalNetwork(externalNetworkParams: VclVMWExternalNetworkParams): VclVMWExternalNetwork;
	/**
	 * Creates a VMW network pool.
	 * @param networkPoolParams 
	 */
	createVMWNetworkPool(networkPoolParams: any): VclVMWNetworkPool;
	/**
	 * Creates an extension/service.
	 * @param params 
	 */
	createService(params: VclAdminServiceParams): VclAdminService;
	/**
	 * Clears all the rights that are not associated with a role or acl rule and which extension service is already deleted.
	 */
	clearUnusedRights(): void;
	/**
	 * Clears the unused localization bundle.
	 */
	clearUnusedLocalizationBundle(): void;
	/**
	 * Uploads the localization bundle.
	 * @param localFileLocation 
	 * @param serviceNamespace 
	 */
	uploadLocalizationBundle(localFileLocation: string, serviceNamespace: string): void;
	/**
	 * Checks user authorization for all services with enabled authorization, URL and request verb.
	 * @param params 
	 */
	checkAuthorization(params: VclAuthorizationCheckParams): boolean;
	/**
	 * Returns a VclExtensionQueryService instance. Use this instance to execute queries in the cloud.
	 */
	getExtensionQueryService(): VclExtensionQueryService;
	/**
	 * Returns the Amqp settings of the VCD system.
	 */
	getAmqpSettings(): VclAmqpSettings;
	/**
	 * Updates the VCD system's amqp settings.
	 * @param settings 
	 */
	updateAmqpSettings(settings: VclAmqpSettings): void;
	/**
	 * Test the amqp connection.
	 */
	testAmqpConnection(): boolean;
	/**
	 * Updates the VCD system's catalog settings.
	 * @param isSyncEnabled 
	 * @param refreshInterval 
	 * @param syncStartMillis 
	 * @param syncStopMillis 
	 */
	updateCatalogSettings(isSyncEnabled: boolean, refreshInterval: number, syncStartMillis: number, syncStopMillis: number): void;
	/**
	 * Returns global blocking blocking task settings.
	 */
	getBlockingTasksSettings(): VclBlockingTaskSettings;
	/**
	 * Updates the VCD system's blocking task settings.
	 * @param settings 
	 */
	updateBlockingTaskSettings(settings: VclBlockingTaskSettings): void;
	/**
	 * Returns the Branding settings of the VCD system.
	 */
	getBrandingSettings(): VclBrandingSettings;
	/**
	 * Updates the VCD system's branding settings.
	 * @param settings 
	 */
	updateBrandingSettings(settings: VclBrandingSettings): void;
	/**
	 * Returns the Email settings of the VCD system.
	 */
	getEmailSettings(): VclEmailSettings;
	/**
	 * Updates the VCD system's email settings.
	 * @param settings 
	 */
	updateEmailSettings(settings: VclEmailSettings): void;
	/**
	 * Returns the email Smtp settings of the VCD system.
	 */
	getEmailSmtpSettings(): VclSmtpSettings;
	/**
	 * Returns the General settings of the VCD system.
	 */
	getGeneralSettings(): VclGeneralSettings;
	/**
	 * Updates the VCD system's general settings.
	 * @param settings 
	 */
	updateGeneralSettings(settings: VclGeneralSettings): void;
	/**
	 * Returns the LDAP group settings of the VCD system.
	 */
	getLdapGroupSettings(): VclLdapGroupAttributes;
	/**
	 * Returns the LDAP settings of the VCD system.
	 */
	getLdapSettings(): VclLdapSettings;
	/**
	 * Updates the VCD system's LDAP settings.
	 * @param settings 
	 */
	updateLdapSettings(settings: VclLdapSettings): void;
	/**
	 * Returns the LDAP user settings of the VCD system.
	 */
	getLdapUserSettings(): VclLdapUserAttributes;
	/**
	 * Returns the License settings of the VCD system.
	 */
	getLicenseSettings(): VclLicense;
	/**
	 * Updates the VCD system's license settings.
	 * @param license 
	 */
	updateLicenseSettings(license: VclLicense): void;
	/**
	 * Returns the PasswordPolicy settings of the VCD system.
	 */
	getPasswordPolicySettings(): VclSystemPasswordPolicySettings;
	/**
	 * Updates the VCD system's PasswordPolicy settings.
	 * @param settings 
	 */
	updatePasswordPolicySettings(settings: VclSystemPasswordPolicySettings): void;
	/**
	 * Returns the VCD Admin Settings Resource as defined in the VCD API.
	 */
	getSystemSettings(): VclSystemSettings;
	/**
	 * Updates the VCD system settings.
	 * @param settings 
	 */
	updateSystemSettings(settings: VclSystemSettings): void;
	/**
	 * Returns the Kerberos settings.
	 */
	getKerberosSettings(): VclKerberosSettings;
	/**
	 * Updates the VCD system's Kerberos settings.
	 * @param settings 
	 */
	updateKerberosSettings(settings: VclKerberosSettings): void;
	/**
	 * Returns the Notifications settings of the VCD system.
	 */
	isNotificationsEnabled(): boolean;
	/**
	 * Enable/Disable the VCD system's notifications settings.
	 * @param enable 
	 */
	enableNotifications(enable: boolean): void;
	/**
	 * Returns the LicenseMetrics info of the VCD system.
	 */
	getLicenseMetricsInfo(): VclLicenseMetricsInfo;
	/**
	 * Get blocking task operations which are enabled.
	 */
	getEnabledBlockingTaskOperations(): VclBlockingTaskOperationType[];
	/**
	 * Returns the Lookup service of the VCD system.
	 */
	getLookupService(): VclLookupServiceSettings;
	/**
	 * Creates a VMW vDCTemplate.
	 * @param vdcTemplateParams 
	 */
	createVMWVdcTemplate(vdcTemplateParams: any): VclVMWVdcTemplate;
	/**
	 * Updates the Lookup service of the VCD system.
	 * @param params 
	 */
	updateLookupService(params: VclLookupServiceParams): VclTask;
	/**
	 * Resets AMQP certificate.
	 */
	resetAmqpCertificate(): void;
	/**
	 * Updates AMQP certificate.AMQP certificate and trust store are mutually exclusive. Overrides AMQP trust store, if update is successful.
	 * @param params 
	 */
	updateAmqpCertificate(params: VclCertificateUpdateParams): VclCertificateUploadSocket;
	/**
	 * Resets AMQP truststore.
	 */
	resetAmqpTruststore(): void;
	/**
	 * Updates AMQP trust store.AMQP certificate and trust store are mutually exclusive.Overrides AMQP certificate, if update is successful.
	 * @param params 
	 */
	updateAmqpTruststore(params: VclTrustStoreUpdateParams): VclTrustStoreUploadSocket;
	/**
	 * Resets vCenter trust store.
	 */
	resetVcTrustsore(): void;
	/**
	 * Updates vCenter trust store.
	 * @param params 
	 */
	updateVcTrustsore(params: VclVcTrustStoreUpdateParams): VclVcTrustStoreUploadSocket;
	/**
	 * Resets system LDAP SSL certificate.
	 */
	resetLdapCertificate(): void;
	/**
	 * Updates system LDAP SSL certificate.
	 * @param params 
	 */
	updateLdapCertificate(params: VclCertificateUpdateParams): VclCertificateUploadSocket;
	/**
	 * Resets system LDAP keystore.
	 */
	resetLdapKeyStore(): void;
	/**
	 * Updates system LDAP keystore.
	 * @param params 
	 */
	updateLdapKeyStore(params: VclKeystoreUpdateParams): VclKeystoreUploadSocket;
	/**
	 * Resets system LDAP SSPI key tab.
	 */
	resetLdapSspiKeytab(): void;
	/**
	 * Updates system LDAP SSPI key tab.
	 * @param params 
	 */
	updateLdapSspiKeytab(params: VclSspiKeytabUpdateParams): VclSspiKeytabUploadSocket;
	/**
	 * Returns the Admin version of the object.
	 */
	toAdminObject(): VclHostAdmin;
	/**
	 * Clears the state of all resolved objects on this host.
	 */
	updateInternalState(): void;
	/**
	 * ###
	 * @param serviceName 
	 */
	getAdminServiceByName(serviceName: string): VclAdminService;
	/**
	 * ###
	 * @param serviceName 
	 */
	isServiceAlreadyRegistered(serviceName: string): boolean;
	/**
	 * Returns all children in this container.
	 */
	getContainedObjects(): any[];
}

/**
 * VMW network pool representation.
 */
declare interface VclVMWNetworkPool {
	files: VclFilesList;
	status: number;
	description: string;
	name: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly otherAttributes: VclMap;
	readonly vCloudExtension: VclObjectList;
	readonly tasks: VclTasksInProgress;
	readonly parent: VclAdminExtension;
	/**
	 * Returns services associated with this network pool.
	 */
	getVendorServices(): VclVendorServices;
	/**
	 * Updates this network pool.
	 */
	update(): VclVMWNetworkPool;
	/**
	 * Deletes this network pool.
	 */
	erase(): VclTask;
	/**
	 * Gets the object's reference.
	 */
	getReference(): VclReference;
	/**
	 * Gets the host that this object belongs to.
	 */
	getHost(): VclHost;
	/**
	 * Reloads the object from the server
	 */
	updateInternalState(): void;
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Returns all children in this container.
	 */
	getContainedObjects(): any[];
}

/**
 * VMW vDCTemplate representation.
 */
declare interface VclVMWVdcTemplate {
	vdcTemplateSpecification: VclVMWVdcTemplateSpecification;
	readonly providerVdcReference: VclObjectList;
	tenantName: string;
	tenantDescription: string;
	description: string;
	name: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly otherAttributes: VclMap;
	readonly vCloudExtension: VclObjectList;
	readonly tasks: VclTasksInProgress;
	readonly parent: VclAdminExtension;
	/**
	 * Updates this VMW vDCTemplate
	 */
	update(): VclVMWNetworkPool;
	/**
	 * Deletes this VMW vDCTemplate.
	 */
	erase(): VclTask;
	/**
	 * Gets the object's reference.
	 */
	getReference(): VclReference;
	/**
	 * Gets the host that this object belongs to.
	 */
	getHost(): VclHost;
	/**
	 * Reloads the object from the server
	 */
	updateInternalState(): void;
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Returns all children in this container.
	 */
	getContainedObjects(): any[];
	/**
	 * Returns the control access settings for this vDC Template.
	 */
	getControlAccess(): VclControlAccessParams;
	/**
	 * Updates the control access settings for this vDC Template .
	 * @param params 
	 */
	updateControlAccess(params: VclControlAccessParams): VclControlAccessParams;
}

/**
 * VMW external network representation.
 */
declare interface VclVMWExternalNetwork {
	vimPortGroupRef: VclVimObjectRef;
	vimPortGroupRefs: VclVimObjectRefs;
	configuration: VclNetworkConfiguration;
	description: string;
	name: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly otherAttributes: VclMap;
	readonly vCloudExtension: VclObjectList;
	readonly tasks: VclTasksInProgress;
	readonly parent: VclAdminExtension;
	/**
	 * Deletes this external network.
	 */
	erase(): VclTask;
	/**
	 * Updates this external network.
	 */
	update(): VclVMWExternalNetwork;
	/**
	 * Resets this external network.
	 */
	reset(): VclTask;
	/**
	 * Gets the object's reference.
	 */
	getReference(): VclReference;
	/**
	 * Gets the host that this object belongs to.
	 */
	getHost(): VclHost;
	/**
	 * Returns the admin version of the object.
	 */
	toAdminObject(): VclExternalNetwork;
	/**
	 * Reloads the object from the server
	 */
	updateInternalState(): void;
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Returns all children in this container.
	 */
	getContainedObjects(): any[];
}

/**
 * VMW provider vDC representation.
 */
declare interface VclVMWProviderVdc {
	resourcePoolRefs: VclVimObjectRefs;
	nsxTManagerReference: VclReference;
	highestSupportedHardwareVersion: string;
	dataStoreRefs: VclVimObjectRefs;
	hostReferences: VclVMWHostReferences;
	readonly vimServer: VclObjectList;
	isEnabled: boolean;
	vdcs: VclVdcs;
	storageCapacity: VclProviderVdcCapacity;
	computeCapacity: VclRootComputeCapacity;
	availableNetworks: VclAvailableNetworks;
	capabilities: VclCapabilities;
	storageProfiles: VclProviderVdcStorageProfiles;
	networkPoolReferences: VclNetworkPoolReferences;
	status: number;
	description: string;
	name: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly otherAttributes: VclMap;
	readonly vCloudExtension: VclObjectList;
	readonly tasks: VclTasksInProgress;
	readonly parent: VclAdminExtension;
	/**
	 * Enables this provider vDC.
	 */
	enable(): void;
	/**
	 * Disables this provider vDC.
	 */
	disable(): void;
	/**
	 * Deletes this provider vDC.
	 */
	erase(): VclTask;
	/**
	 * Updates this provider vDC.
	 */
	update(): VclVMWProviderVdc;
	/**
	 * Disable a resource pool of this provider vDC.
	 * @param moRef 
	 */
	disableResourcePool(moRef: string): void;
	/**
	 * Enables a resource pool of this provider vDC.
	 * @param moRef 
	 */
	enableResourcePool(moRef: string): void;
	/**
	 * Returns all storage profiles of this provider vDC.
	 */
	getProviderStorageProfiles(): VclVMWProviderVdcStorageProfile[];
	/**
	 * Updates storage profiles on this provider vDC.
	 * @param classesToAdd 
	 * @param classesToRemove 
	 */
	updateStorageProfiles(classesToAdd: string[], classesToRemove: VclReference[]): VclTask;
	/**
	 * Merges provider vDC.
	 * @param refs 
	 */
	mergeProviderVdcs(refs: VclReference[]): VclTask;
	/**
	 * Migrates virtual machines on another resource pool.
	 * @param sourceRpMoRef 
	 * @param vmRefs 
	 * @param targetRpRef 
	 */
	migrateVms(sourceRpMoRef: string, vmRefs: VclReference[], targetRpRef: VclVimObjectRef): VclTask;
	/**
	 * Returns all virtual machines on a resource pool.
	 * @param rpMoRef 
	 */
	getVms(rpMoRef: string): VclReferenceResultSet;
	/**
	 * Gets the object's reference.
	 */
	getReference(): VclReference;
	/**
	 * Gets the host that this object belongs to.
	 */
	getHost(): VclHost;
	/**
	 * Returns all VIM servers visible for this provider vDC.
	 */
	getVimServers(): VclVimServer[];
	/**
	 * Returns all datastores visible for this provider vDC.
	 */
	getDatastores(): VclVMWDatastore[];
	/**
	 * Returns all resource pools visible for this provider vDC.
	 */
	getResourcePools(): VclVMWProviderVdcResourcePool[];
	/**
	 * Updates resource pools visible for this provider vDC.
	 * @param rpsToAdd 
	 * @param rpsToRemove 
	 */
	updateResourcePools(rpsToAdd: VclVimObjectRef[], rpsToRemove: VclReference[]): VclTask;
	/**
	 * Returns all VMW network pools visible for this provider vDC.
	 */
	getVMWNetworkPools(): VclVMWNetworkPool[];
	/**
	 * Returns all VMW external networks visible for this provider vDC.
	 */
	getVMWExternalNetworks(): VclVMWExternalNetwork[];
	/**
	 * Returns all VMW hosts visible for this provider vDC.
	 */
	getVMWHosts(): VclVMWHost[];
	/**
	 * Returns all admin vDCs visible for this provider vDC.
	 */
	getAdminVdcs(): VclAdminVdc[];
	/**
	 * Returns the admin object for this object.
	 */
	toAdminObject(): VclProviderVdc;
	/**
	 * Reloads the object from the server
	 */
	updateInternalState(): void;
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Returns all children in this container.
	 */
	getContainedObjects(): any[];
}

/**
 * VIM server representation.
 */
declare interface VclVimServer {
	computeProviderScope: string;
	isEnabled: boolean;
	uuid: string;
	vcVersion: string;
	rootFolder: string;
	isConnected: boolean;
	shieldManagerHost: string;
	shieldManagerUserName: string;
	vcProxy: string;
	useVsphereService: boolean;
	vsphereWebClientServerUrl: string;
	url: string;
	username: string;
	password: string;
	description: string;
	name: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly otherAttributes: VclMap;
	readonly vCloudExtension: VclObjectList;
	readonly tasks: VclTasksInProgress;
	readonly parent: VclAdminExtension;
	/**
	 * Returns references to all virtual machines on this VIM server.
	 */
	getVmRefs(): VclVmObjectRef[];
	/**
	 * Returns references to all networks on this VIM server.
	 */
	getNetworkRefs(): VclVimObjectRef[];
	/**
	 * Returns references to all resource pools on this VIM server.
	 */
	getResourcePools(): VclResourcePool[];
	/**
	 * Imports a virtual machine as a vApp on this VIM server.
	 * @param params 
	 */
	importVmAsVApp(params: VclImportVmAsVAppParams): VclVApp;
	/**
	 * Imports a virtual machine into a vApp on this VIM server.
	 * @param params 
	 */
	importVmIntoVApp(params: VclImportVmIntoExistingVAppParams): VclTask;
	/**
	 * Imports a virtual machine as a vApp template on this VIM server.
	 * @param params 
	 */
	importVmAsVAppTemplate(params: VclImportVmAsVAppTemplateParams): VclVAppTemplate;
	/**
	 * Imports media on this VIM server.
	 * @param params 
	 */
	importMedia(params: VclImportMediaParams): VclMedia;
	/**
	 * Unregisters this VIM server.
	 */
	unregister(): VclTask;
	/**
	 * Updates this VIM server.
	 */
	update(): VclTask;
	/**
	 * Refresh this VIM server.
	 */
	refresh(): VclTask;
	/**
	 * Reconnects this VIM server.
	 */
	forceReconnect(): VclTask;
	/**
	 * Returns the shield manager for this VIM server.
	 */
	getShieldManager(): VclShieldManagerParams;
	/**
	 * Updates the shield manager for this VIM server.
	 * @param params 
	 */
	updateShieldManager(params: VclShieldManagerParams): VclTask;
	/**
	 * Returns all storage profiles on this VIM server.
	 */
	getStorageProfiles(): VclVMWStorageProfile[];
	/**
	 * Refreshes storage profiles on this VIM server.
	 */
	refreshStorageProfiles(): VclTask;
	/**
	 * Returns the object's reference.
	 */
	getReference(): VclReference;
	/**
	 * Gets the host that this object belongs to.
	 */
	getHost(): VclHost;
	/**
	 * Reloads the object from the server
	 */
	updateInternalState(): void;
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Returns all children in this container.
	 */
	getContainedObjects(): any[];
}

/**
 * VMW host representation.
 */
declare interface VclVMWHost {
	busy: boolean;
	vmMoRef: string;
	systemMessages: string;
	numOfCpusPackages: number;
	numOfCpusLogical: number;
	cpuTotal: number;
	memUsed: number;
	memTotal: number;
	hostOsName: string;
	hostOsVersion: string;
	ready: boolean;
	enableHostForHostSpanning: boolean;
	cpuType: string;
	vimPropertyPageUrl: string;
	enabled: boolean;
	available: boolean;
	description: string;
	name: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly otherAttributes: VclMap;
	readonly vCloudExtension: VclObjectList;
	readonly tasks: VclTasksInProgress;
	readonly parent: VclAdminExtension;
	/**
	 * Disables this host.
	 */
	disable(): VclTask;
	/**
	 * Enables this host.
	 */
	enable(): VclTask;
	/**
	 * Prepares this host.
	 * @param username 
	 * @param password 
	 */
	prepare(username: string, password: string): VclTask;
	/**
	 * Unprepares this host.
	 */
	unprepare(): VclTask;
	/**
	 * Repairs this host.
	 */
	repair(): VclTask;
	/**
	 * Upgrades this host.
	 */
	upgrade(): VclTask;
	/**
	 * Returns the object's reference.
	 */
	getReference(): VclReference;
	/**
	 * Gets the host that this object belongs to.
	 */
	getHost(): VclHost;
	/**
	 * Reloads the object from the server.
	 */
	updateInternalState(): void;
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Returns all children in this container.
	 */
	getContainedObjects(): any[];
}

/**
 * VMW datastore representation.
 */
declare interface VclVMWDatastore {
	busy: boolean;
	vAAIForFpEnabled: boolean;
	thresholdYellowGb: number;
	thresholdRedGb: number;
	systemMessages: string;
	datastoreFsType: string;
	vcDisplayName: string;
	mountHost: string;
	mountDirectory: string;
	totalCapacityMb: any;
	totalCapacityGb: any;
	usedCapacityMb: any;
	usedCapacityGb: any;
	usedCapacityPercent: any;
	provisionedSpaceMb: any;
	provisionedSpaceGb: any;
	requestedStorageMb: any;
	requestedStorageGb: any;
	vimPropertyPageUrl: string;
	vimObjectRef: VclVimObjectRef;
	enabled: boolean;
	members: VclVimObjectRefs;
	description: string;
	name: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly otherAttributes: VclMap;
	readonly vCloudExtension: VclObjectList;
	readonly tasks: VclTasksInProgress;
	readonly parent: VclAdminExtension;
	/**
	 * Enables this datastore.
	 */
	enable(): VclVMWDatastore;
	/**
	 * Disables this datastore.
	 */
	disable(): VclVMWDatastore;
	/**
	 * Updates this datastore.
	 */
	update(): VclVMWDatastore;
	/**
	 * Deletes this datastore.
	 */
	erase(): void;
	/**
	 * Returns the VIM reference of this datastore.
	 */
	getDatastoreVimRef(): VclVimObjectRef;
	/**
	 * Returns the object's reference.
	 */
	getReference(): VclReference;
	/**
	 * Gets the host that this object belongs to.
	 */
	getHost(): VclHost;
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Reloads the object from the server.
	 */
	updateInternalState(): void;
	/**
	 * Returns all children in this container.
	 */
	getContainedObjects(): any[];
}

/**
 * Extension service representation.
 */
declare interface VclService {
	vendor: string;
	namespace: string;
	description: string;
	name: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly otherAttributes: VclMap;
	readonly vCloudExtension: VclObjectList;
	readonly tasks: VclTasksInProgress;
	readonly parent: VclHost;
	/**
	 * Returns all API definitions of this extension service.
	 */
	getApiDefinitions(): VclApiDefinition[];
	/**
	 * Returns the admin representation of the object.
	 */
	toAdminObject(): VclAdminService;
	/**
	 * Returns the object's reference.
	 */
	getReference(): VclReference;
	/**
	 * Gets the host that this object belongs to.
	 */
	getHost(): VclHost;
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Returns all children in this container.
	 */
	getContainedObjects(): any[];
}

/**
 * Admin extension service representation.
 */
declare interface VclAdminService {
	vendor: string;
	routingKey: string;
	exchange: string;
	authorizationEnabled: boolean;
	apiFilters: VclApiFilters;
	serviceLinks: VclAdminServiceLinks;
	apiDefinitions: VclAdminApiDefinitions;
	resourceClasses: VclResourceClasses;
	enabled: boolean;
	namespace: string;
	priority: number;
	description: string;
	name: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly otherAttributes: VclMap;
	readonly vCloudExtension: VclObjectList;
	readonly tasks: VclTasksInProgress;
	readonly parent: VclAdminExtension;
	/**
	 * Returns all admin API definitions of this extension service.
	 */
	getAdminApiDefinition(): VclAdminApiDefinition[];
	/**
	 * Creates a new admin API definition on this extension service.
	 * @param params 
	 */
	createAdminApiDefinition(params: VclAdminApiDefinitionParams): VclAdminApiDefinition;
	/**
	 * Returns all admin service links of this extension service.
	 */
	getAdminLinks(): VclAdminServiceLink[];
	/**
	 * Creates a new admin link on this extension service.
	 * @param params 
	 */
	createAdminLink(params: VclAdminServiceLinkParams): VclAdminServiceLink;
	/**
	 * Returns all admin API filters of this extension service.
	 */
	getAdminApiFilters(): VclAdminApiFilter[];
	/**
	 * Creates a new admin API filter on this extension service.
	 * @param params 
	 */
	createAdminApiFilter(params: VclApiFilterParams): VclAdminApiFilter;
	/**
	 * Returns all admin resource classes of this extension service.
	 */
	getAdminResourceClasses(): VclAdminResourceClass[];
	/**
	 * Registers a new admin resource class on this extension service.
	 * @param params 
	 */
	registerAdminResourceClass(params: VclResourceClassParams): VclAdminResourceClass;
	/**
	 * Creates a new right on this extension service.
	 * @param params 
	 */
	createRight(params: VclRightParams): VclRight;
	/**
	 * Deletes this extension service.
	 */
	erase(): void;
	/**
	 * Returns all rights of this extension service.
	 */
	getRights(): VclRight[];
	/**
	 * Updates this extension service with parameters.
	 * @param serviceParams 
	 */
	update(serviceParams: any): VclAdminService;
	/**
	 * Checks user authorization for service, URL and request verb.
	 * @param params 
	 */
	checkAuthorization(params: VclAuthorizationCheckParams): boolean;
	/**
	 * Returns the user representation of the object.
	 */
	toUserObject(): VclService;
	/**
	 * Returns the object's reference.
	 */
	getReference(): VclReference;
	/**
	 * Gets the host that this object belongs to.
	 */
	getHost(): VclHost;
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * ###
	 */
	disable(): VclAdminService;
	/**
	 * ###
	 * @param linkHref 
	 * @param relation 
	 * @param resourceType 
	 * @param mimeType 
	 * @param resourceId 
	 */
	createServiceLink(linkHref: string, relation: string, resourceType: string, mimeType: string, resourceId: string): VclAdminServiceLink;
	/**
	 * ###
	 * @param name 
	 * @param type 
	 * @param nid 
	 * @param urlTemplate 
	 * @param urnPattern 
	 * @param mimeType 
	 */
	registerResourceClass(name: string, type: string, nid: string, urlTemplate: string, urnPattern: string, mimeType: string): VclAdminResourceClass;
	/**
	 * Returns all children in this container.
	 */
	getContainedObjects(): any[];
}

/**
 * Admin ACL rule representation.
 */
declare interface VclAdminAclRule {
	principalAccess: VclAclAccess;
	serviceResourceAccess: VclAclAccess;
	organizationAccess: VclAclAccess;
	description: string;
	name: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly otherAttributes: VclMap;
	readonly vCloudExtension: VclObjectList;
	readonly tasks: VclTasksInProgress;
	readonly parent: VclAdminResourceClassAction;
	/**
	 * Deletes this ACL rule.
	 */
	erase(): void;
	/**
	 * Returns the object's reference.
	 */
	getReference(): VclReference;
	/**
	 * Gets the host that this object belongs to.
	 */
	getHost(): VclHost;
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Returns all children in this container.
	 */
	getContainedObjects(): any[];
}

/**
 * Admin API definition representation.
 */
declare interface VclAdminApiDefinition {
	apiVendor: string;
	entryPoint: string;
	supportedApiVersions: VclExtensionVersions;
	files: VclAdminFileDescriptors;
	namespace: string;
	description: string;
	name: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly otherAttributes: VclMap;
	readonly vCloudExtension: VclObjectList;
	readonly tasks: VclTasksInProgress;
	readonly parent: VclAdminService;
	/**
	 * Returns all admin file descriptors of this API definition.
	 */
	getAdminFileDescriptors(): VclAdminFileDescriptor[];
	/**
	 * Creates a new admin file descriptor on this API definition.
	 * @param params 
	 */
	createAdminFileDescriptor(params: any): VclAdminFileDescriptor;
	/**
	 * Deletes this admin API definition.
	 */
	erase(): void;
	/**
	 * Returns the object's reference.
	 */
	getReference(): VclReference;
	/**
	 * Gets the host that this object belongs to.
	 */
	getHost(): VclHost;
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Returns all children in this container.
	 */
	getContainedObjects(): any[];
}

/**
 * API definition representation.
 */
declare interface VclApiDefinition {
	apiVendor: string;
	entryPoint: string;
	supportedApiVersions: VclVersions;
	namespace: string;
	description: string;
	name: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly otherAttributes: VclMap;
	readonly vCloudExtension: VclObjectList;
	readonly tasks: VclTasksInProgress;
	readonly parent: VclService;
	/**
	 * Returns all file descriptors records for the API definition.
	 */
	getFileDescriptorRecords(): VclRecordResultSet;
	/**
	 * Returns the object's reference.
	 */
	getReference(): VclReference;
	/**
	 * Gets the host that this object belongs to.
	 */
	getHost(): VclHost;
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Returns all children in this container.
	 */
	getContainedObjects(): any[];
}

/**
 * Admin File descriptor representation.
 */
declare interface VclAdminFileDescriptor {
	description: string;
	file: VclReference;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly otherAttributes: VclMap;
	readonly vCloudExtension: VclObjectList;
	readonly tasks: VclTasksInProgress;
	readonly parent: VclAdminApiDefinition;
	/**
	 * Deletes this file descriptor.
	 */
	erase(): void;
	/**
	 * Returns the object's reference.
	 */
	getReference(): VclReference;
	/**
	 * Gets the host that this object belongs to.
	 */
	getHost(): VclHost;
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Returns all children in this container.
	 */
	getContainedObjects(): any[];
}

/**
 * Admin API filter representation.
 */
declare interface VclAdminApiFilter {
	urlPattern: string;
	responseContentType: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly otherAttributes: VclMap;
	readonly vCloudExtension: VclObjectList;
	readonly tasks: VclTasksInProgress;
	readonly parent: VclAdminService;
	/**
	 * Deletes this API filter.
	 */
	erase(): void;
	/**
	 * Returns the object's reference.
	 */
	getReference(): VclReference;
	/**
	 * Gets the host that this object belongs to.
	 */
	getHost(): VclHost;
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Returns all children in this container.
	 */
	getContainedObjects(): any[];
}

/**
 * Admin resource class representation.
 */
declare interface VclAdminResourceClass {
	mimeType: string;
	nid: string;
	urnPattern: string;
	serviceResources: VclServiceResources;
	resourceClassActions: VclResourceClassActions;
	urlTemplate: string;
	description: string;
	name: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly otherAttributes: VclMap;
	readonly vCloudExtension: VclObjectList;
	readonly tasks: VclTasksInProgress;
	readonly parent: VclAdminService;
	/**
	 * Returns all resource class actions for this resource class.
	 */
	getAdminResourceClassActions(): VclAdminResourceClassAction[];
	/**
	 * Registers a new admin resource class action.
	 * @param params 
	 */
	registerAdminResourceClassAction(params: VclResourceClassActionParams): VclAdminResourceClassAction;
	/**
	 * Returns all service resourcs for this resource class.
	 */
	getAdminServiceResources(): VclAdminServiceResource[];
	/**
	 * Registers a new admin service resource.
	 * @param params 
	 */
	registerAdminServiceResource(params: VclServiceResourceParams): VclAdminServiceResource;
	/**
	 * Deletes this resource class.
	 */
	erase(): void;
	/**
	 * Returns the object's reference.
	 */
	getReference(): VclReference;
	/**
	 * Gets the host that this object belongs to.
	 */
	getHost(): VclHost;
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * ###
	 * @param actionName 
	 * @param actionHttpMethod 
	 * @param actionURLPattern 
	 * @param actionOperationKey 
	 */
	registerResourceClassAction(actionName: string, actionHttpMethod: string, actionURLPattern: string, actionOperationKey: string): VclAdminResourceClassAction;
	/**
	 * Returns all children in this container.
	 */
	getContainedObjects(): any[];
}

/**
 * Admin resource class action representation.
 */
declare interface VclAdminResourceClassAction {
	urlPattern: string;
	httpMethod: string;
	aclRules: VclAclRules;
	description: string;
	name: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly otherAttributes: VclMap;
	readonly vCloudExtension: VclObjectList;
	readonly tasks: VclTasksInProgress;
	readonly parent: VclAdminResourceClass;
	/**
	 * Returns all admin ACL rules for this resource class action.
	 */
	getAdminAclRules(): VclAdminAclRule[];
	/**
	 * Registers a new ACL rule.
	 * @param params 
	 */
	registerAdminAclRule(params: VclAclRuleParams): VclAdminAclRule;
	/**
	 * Deletes this admin resource class action.
	 */
	erase(): void;
	/**
	 * Returns the object's reference.
	 */
	getReference(): VclReference;
	/**
	 * Gets the host that this object belongs to.
	 */
	getHost(): VclHost;
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Returns all children in this container.
	 */
	getContainedObjects(): any[];
}

/**
 * Admin service link representation.
 */
declare interface VclAdminServiceLink {
	rel: string;
	linkHref: string;
	mimeType: string;
	resourceId: string;
	resourceType: string;
	externalResourceId: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly otherAttributes: VclMap;
	readonly vCloudExtension: VclObjectList;
	readonly tasks: VclTasksInProgress;
	readonly parent: VclAdminService;
	/**
	 * Deletes this admin service link.
	 */
	erase(): void;
	/**
	 * Returns the object's reference.
	 */
	getReference(): VclReference;
	/**
	 * Gets the host that this object belongs to.
	 */
	getHost(): VclHost;
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Returns all children in this container.
	 */
	getContainedObjects(): any[];
}

/**
 * Admin service resource representation.
 */
declare interface VclAdminServiceResource {
	org: VclReference;
	externalObjectId: string;
	description: string;
	name: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly otherAttributes: VclMap;
	readonly vCloudExtension: VclObjectList;
	readonly tasks: VclTasksInProgress;
	readonly parent: VclAdminResourceClass;
	/**
	 * Deletes this admin service resource.
	 */
	erase(): void;
	/**
	 * Returns the object's reference.
	 */
	getReference(): VclReference;
	/**
	 * Gets the host that this object belongs to.
	 */
	getHost(): VclHost;
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Returns all children in this container.
	 */
	getContainedObjects(): any[];
}

/**
 * Metadata representation.
 */
declare interface VclMetadata {
	/**
	 * Returns all metadata typed entries.
	 * Related object types:
	 * VclMetadataEntry
	 */
	getTypedEntries(): VclObjectList;
	/**
	 * Updates a metadata typed entry.
	 * @param value 
	 */
	updateTypedEntry(value: VclMetadataEntry): VclTask;
	/**
	 * Updates the provided map of metadata typed entries for this vcloud resource.
	 * @param metadataEntries 
	 */
	updateMetadataTypedEntries(metadataEntries: VclObjectList): VclTask;
	/**
	 * Deletes a metadata typed entry.
	 * @param key 
	 */
	deleteTypedEntry(key: string): VclTask;
}

/**
 * Blocking task representation.
 */
declare interface VclBlockingTask {
	status: string;
	timeoutAction: string;
	user: VclReference;
	taskOwner: VclReference;
	createdTime: VclXMLGregorianCalendar;
	timeoutDate: VclXMLGregorianCalendar;
	organization: VclReference;
	description: string;
	name: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly otherAttributes: VclMap;
	readonly vCloudExtension: VclObjectList;
	/**
	 * This operation aborts the blocking task.
	 * @param message 
	 */
	abort(message: string): void;
	/**
	 * This operation fails the blocking task.
	 * @param message 
	 */
	fail(message: string): void;
	/**
	 * This operation resumes the blocking task.
	 * @param message 
	 */
	resume(message: string): void;
	/**
	 * Update the progress of the blocking task operation.
	 * @param message 
	 * @param timeout 
	 */
	updateProgress(message: string, timeout: number): void;
	/**
	 * Returns the task reference.
	 */
	getTask(): VclReference;
	/**
	 * Gets the host that this object belongs to.
	 */
	getHost(): VclHost;
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Returns all children in this container.
	 */
	getContainedObjects(): any[];
}

/**
 * Notification helper. Helps to parse VCD notifications.
 */
declare class VclNotificationHelper {
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Gets the notification message.
	 */
	getMessage(): string;
	/**
	 * Sets the notification message.
	 * @param message 
	 */
	setMessage(message: string): void;
	/**
	 * Gets a notification message header value by its name.
	 * @param name 
	 */
	getMessageHeader(name: string): any;
	/**
	 * Sets a notification message header value.
	 * @param name 
	 * @param value 
	 */
	setMessageHeader(name: string, value: any): void;
	/**
	 * If the notification is for blocking operation, returns the blocking request link.
	 */
	getBlockingTaskLink(): VclEntityLink;
	/**
	 * Returns the entity link.
	 */
	getEntityLink(): VclEntityLink;
	/**
	 * Returns the notification event type.
	 */
	getNotificationEventType(): VclEventType;
	/**
	 * Returns the notification entity link type.
	 */
	getEntityLinkType(): VclEntityType;
	/**
	 * Returns the organization link.
	 */
	getOrgLink(): VclEntityLink;
	/**
	 * Returns the taskOwner link.
	 */
	getTaskOwnerLink(): VclEntityLink;
	/**
	 * Returns the notification task owner link type.
	 */
	getTaskOwnerLinkType(): VclEntityType;
	/**
	 * Returns the notification task owner link type.
	 */
	getUserLink(): VclEntityLink;
	/**
	 * Check if this Notification is a Blocking task.
	 */
	isBlockingTask(): boolean;
	/**
	 * Returns the notification object.
	 */
	getNotification(): VclNotification;
}

/**
 * Shared properties. Helps to share data between workflow executions.
 */
declare class VclSharedProperties {
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns a property by its key. Returns null if the property is missing or the TTL has expired.
	 * @param key 
	 */
	getProperty(key: string): string;
	/**
	 * Puts a property in the collection of properties.
	 * @param key 
	 * @param value 
	 * @param ttl 
	 */
	putProperty(key: string, value: string, ttl: number): void;
}

/**
 * Licensing report representation.
 */
declare interface VclLicensingReport {
	readonly sample: VclObjectList;
	productSerialNumber: string;
	reportDate: VclXMLGregorianCalendar;
	signature: string;
	href: string;
	type: string;
	readonly otherAttributes: VclMap;
	readonly vCloudExtension: VclObjectList;
	readonly tasks: VclTasksInProgress;
	readonly parent: VclAdminExtension;
	/**
	 * Returns the object's reference.
	 */
	getReference(): VclReference;
	/**
	 * Gets the host that this object belongs to.
	 */
	getHost(): VclHost;
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Returns all children in this container.
	 */
	getContainedObjects(): any[];
}

/**
 * Stranded item representation.
 */
declare interface VclStrandedItem {
	entityType: string;
	deletionDate: VclXMLGregorianCalendar;
	errorMessage: string;
	strandedItemVimObjects: VclStrandedItemVimObjects;
	readonly parentEntity: VclReference;
	description: string;
	name: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly otherAttributes: VclMap;
	readonly vCloudExtension: VclObjectList;
	readonly tasks: VclTasksInProgress;
	readonly parent: VclAdminExtension;
	/**
	 * Deletes this stranded item.
	 */
	erase(): VclTask;
	/**
	 * Deletes this stranded item.
	 */
	forceErase(): VclTask;
	/**
	 * Returns the object's reference.
	 */
	getReference(): VclReference;
	/**
	 * Gets the host that this object belongs to.
	 */
	getHost(): VclHost;
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Returns all children in this container.
	 */
	getContainedObjects(): any[];
}

/**
 * vDC storage profile representation.
 */
declare interface VclVdcStorageProfile {
	storageUsedMB: number;
	iopsAllocated: number;
	units: string;
	iopsSettings: VclVdcStorageProfileIopsSettings;
	enabled: boolean;
	limit: number;
	default: boolean;
	description: string;
	name: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly otherAttributes: VclMap;
	readonly vCloudExtension: VclObjectList;
	readonly tasks: VclTasksInProgress;
	readonly parent: VclVdc;
	/**
	 * Gets the object's metadata.
	 */
	getMetadata(): VclMetadata;
	/**
	 * Reloads the object from the server.
	 */
	updateInternalState(): void;
	/**
	 * Returns the object's reference.
	 */
	getReference(): VclReference;
	/**
	 * Gets the host that this object belongs to.
	 */
	getHost(): VclHost;
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Returns the Admin version of the object.
	 */
	toAdminObject(): VclAdminVdcStorageProfile;
	/**
	 * Returns all children in this container.
	 */
	getContainedObjects(): any[];
}

/**
 * Admin vDC storage profile representation.
 */
declare interface VclAdminVdcStorageProfile {
	providerVdcStorageProfile: VclReference;
	storageUsedMB: number;
	iopsAllocated: number;
	units: string;
	iopsSettings: VclVdcStorageProfileIopsSettings;
	enabled: boolean;
	limit: number;
	default: boolean;
	description: string;
	name: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly otherAttributes: VclMap;
	readonly vCloudExtension: VclObjectList;
	readonly tasks: VclTasksInProgress;
	readonly parent: VclAdminVdc;
	/**
	 * Gets the object's metadata.
	 */
	getMetadata(): VclMetadata;
	/**
	 * Reloads the object from the server.
	 */
	updateInternalState(): void;
	/**
	 * Returns the object's reference.
	 */
	getReference(): VclReference;
	/**
	 * Gets the host that this object belongs to.
	 */
	getHost(): VclHost;
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Updates this admin vDC storage profile.
	 */
	update(): VclAdminVdcStorageProfile;
	/**
	 * Returns the User version of the object.
	 */
	toUserObject(): VclVdcStorageProfile;
	/**
	 * Returns all children in this container.
	 */
	getContainedObjects(): any[];
}

/**
 * Firewall representation.
 */
declare interface VclFirewallRuleTrinity {
	destination: VclAddressTrinityParams;
	action: string;
	application: VclApplicationTrinityParams;
	source: VclAddressTrinityParams;
	readonly obtainEquivalenceKey: string;
	direction: string;
	ruleTag: number;
	matchTranslated: boolean;
	statistics: VclFirewallRuleStatsParams;
	ruleId: number;
	ruleType: string;
	loggingEnabled: boolean;
	invalidSource: boolean;
	invalidDestination: boolean;
	invalidApplication: boolean;
	name: string;
	description: string;
	enabled: boolean;
	/**
	 * Enable or disable the firwall rule
	 * @param ruleEnableState 
	 */
	enableFirewallRule(ruleEnableState: boolean): VclFirewallRuleTrinity;
	/**
	 * Delete a firwall rule
	 */
	deleteFirewallRule(): VclFirewallRuleTrinity[];
	/**
	 * Check the state of a firwall rule
	 */
	isEnabled(): boolean;
	/**
	 * Get firewall rule type
	 */
	getFirewallRuleType(): VclFirewallPolicyType;
	/**
	 * Is TCP configured
	 */
	isTcpConfigured(): boolean;
	/**
	 * Is UDP configured
	 */
	isUdpConfigured(): boolean;
	/**
	 * Is ICMP configured
	 */
	isIcmpConfigured(): boolean;
	/**
	 * Is ALL configured
	 */
	isAllConfigured(): boolean;
	/**
	 * Get Tcp source port
	 * @param isTcpConfigured 
	 */
	getTcpSourcePort(isTcpConfigured: boolean): string;
	/**
	 * Get Tcp destination port
	 * @param isTcpConfigured 
	 */
	getTcpDestinationPort(isTcpConfigured: boolean): string;
	/**
	 * Get Udp source port
	 * @param isUdpConfigured 
	 */
	getUdpSourcePort(isUdpConfigured: boolean): string;
	/**
	 * Get Udp destination port
	 * @param isUdpConfigured 
	 */
	getUdpDestinationPort(isUdpConfigured: boolean): string;
	/**
	 * Get Rule Source Ips
	 */
	getRuleSourceIp(): string;
	/**
	 * Get Rule Destination Ips
	 */
	getRuleDestinationIp(): string;
	/**
	 * Find supported objects
	 */
	findSupportedObjects(): string[];
	/**
	 * Get available objects for provided type
	 * @param params 
	 */
	getAvailableObjectsForProvidedType(params: string): string[];
	/**
	 * get selected source objects
	 * @param selectedSourceObjectHidden 
	 */
	getSourceSelectedObjects(selectedSourceObjectHidden: string[]): string[];
	/**
	 * Find selected destination objects
	 * @param selectedDestinationObjectHidden 
	 */
	getDestinationSelectedObjects(selectedDestinationObjectHidden: string[]): string[];
	/**
	 * Create a firewall rule for advanced gateway.
	 * @param availableSelectedObj 
	 */
	updateFirewallRule(availableSelectedObj: VclFirewallRuleTrinityParams): VclFirewallRuleTrinity;
	/**
	 * Check the loogin state of a firwall rule
	 */
	isLogginEnabled(): boolean;
	/**
	 * get the Description of a firwall rule
	 */
	getDescription(): string;
	/**
	 * Update a firwall rule for legacy gateway
	 * @param params 
	 */
	updateFirewallRuleForLegacyGateway(params: VclFirewallRule): VclTask;
	/**
	 * Is gateway advanced.
	 */
	isAdvanced(): boolean;
	/**
	 * Get source port for legacy gateway
	 */
	getSourcePort(): number;
	/**
	 * Get destination port for legacy gateway
	 */
	getDestinationPort(): number;
}

/**
 * DHCP Binding representation.
 */
declare interface VclStaticBindingTrinity {
	vmId: string;
	vnicId: number;
	hostname: string;
	vmName: string;
	ipAddress: string;
	macAddress: string;
	readonly obtainEquivalenceKey: string;
	autoConfigureDNS: boolean;
	defaultGateway: string;
	domainName: string;
	primaryNameServer: string;
	secondaryNameServer: string;
	leaseTime: string;
	dnsType: string;
	subnetMask: string;
	dhcpOptions: VclDhcpOptionsParams;
	nextServer: string;
	/**
	 * Update DHCP binding.
	 * @param params 
	 */
	updateBinding(params: VclStaticBindingTrinityParams): VclStaticBindingTrinity;
	/**
	 * delete DHCP binding.
	 */
	deleteBinding(): void;
}

/**
 * DHCP IP Pool representation.
 */
declare interface VclIPPoolTrinity {
	poolId: string;
	ipRange: string;
	allowHugeRange: boolean;
	readonly obtainEquivalenceKey: string;
	autoConfigureDNS: boolean;
	defaultGateway: string;
	domainName: string;
	primaryNameServer: string;
	secondaryNameServer: string;
	leaseTime: string;
	dnsType: string;
	subnetMask: string;
	dhcpOptions: VclDhcpOptionsParams;
	nextServer: string;
	/**
	 * Update DHCP IP pool for gateway.
	 * @param params 
	 */
	updateIpPool(params: VclIPPoolTrinityParams): VclIPPoolTrinity;
	/**
	 * Update DHCP IP pool for legacy gateway.
	 * @param params 
	 * @param networkName 
	 */
	updateIpPoolForLegacyGateway(params: VclDhcpPoolService, networkName: string): VclTask;
	/**
	 * delete existing ip pool.
	 */
	deleteIpPool(): void;
	/**
	 * delete existing ip pool.
	 */
	isAdvanced(): boolean;
	/**
	 * Get Edge Interface names.
	 */
	getEdgeInterfaceNames(): string[];
	/**
	 * Get selected interface name.
	 */
	getSelectedEdgeInterfaceName(): string;
	/**
	 * Get selected interface name.
	 */
	isEnabled(): boolean;
	/**
	 * Get low IP address
	 */
	getLowIpAddress(): string;
	/**
	 * Get high Ip address.
	 */
	getHighIpAddress(): string;
	/**
	 * Get default lease time.
	 */
	getDefaultLeaseTime(): number;
	/**
	 * Get Max lease time.
	 */
	getMaxLeaseTime(): number;
}

/**
 * NAT rule representation.
 */
declare interface VclNsxNatRule {
	ruleType: string;
	action: string;
	vnic: string;
	originalAddress: string;
	translatedAddress: string;
	originalPort: string;
	translatedPort: string;
	icmpType: string;
	description: string;
	loggingEnabled: boolean;
	enabled: boolean;
	ruleId: number;
	ruleTag: number;
	readonly obtainEquivalenceKey: string;
	protocol: string;
	/**
	 * Update a NAT rule
	 * @param params 
	 */
	updateRule(params: VclNatRuleParams): void;
	/**
	 * Delete a NAT rule
	 */
	deleteRule(): void;
	/**
	 * Enable or disable the NAT rule
	 * @param enable 
	 */
	enableNatRule(enable: boolean): void;
	/**
	 * Enable or disable logging in NAT rule
	 * @param enable 
	 */
	enableLogging(enable: boolean): void;
	/**
	 * Get Edge Interface names.
	 */
	getEdgeInterfaceNames(): string[];
	/**
	 * Update Nat rule for legacy gateway
	 * @param params 
	 */
	updateNatRuleLegacy(params: VclNatRuleParams): VclGateway;
	/**
	 * Is gateway advanced.
	 */
	isAdvanced(): boolean;
	/**
	 * Get protocols.
	 */
	getProtocols(): string[];
}

/**
 * Gateway representation.
 */
declare interface VclGateway {
	gatewayBackingRef: VclGatewayBackingRef;
	configuration: VclGatewayConfiguration;
	status: number;
	description: string;
	name: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly otherAttributes: VclMap;
	readonly vCloudExtension: VclObjectList;
	readonly tasks: VclTasksInProgress;
	readonly parent: VclAdminVdc;
	/**
	 * Reloads the object from the server.
	 */
	updateInternalState(): void;
	/**
	 * Returns the object's reference.
	 */
	getReference(): VclReference;
	/**
	 * Gets the host that this object belongs to.
	 */
	getHost(): VclHost;
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Deletes this gateway.
	 */
	erase(): VclTask;
	/**
	 * Updates this gateway.
	 */
	update(): VclTask;
	/**
	 * Redeploy this gateway.
	 */
	redeploy(): VclGateway;
	/**
	 * Reapply services on this gateway.
	 */
	reapplyServices(): VclTask;
	/**
	 * Update services on this gateway.
	 */
	configureServices(): VclTask;
	/**
	 * Synchronizes syslog server settings on this gateway.
	 */
	syncSyslogServer(): VclGateway;
	/**
	 * Modify form factor of this gateway.
	 * @param formFactor 
	 */
	modifyFormFactor(formFactor: string): VclTask;
	/**
	 * Set Tenant syslog server on gateway.
	 * @param syslogIp 
	 */
	setSyslogServers(syslogIp: string): VclGateway;
	/**
	 * Create DHCP binding.
	 * @param params 
	 */
	addDhcpBinding(params: VclStaticBindingTrinityParams): VclStaticBindingTrinity;
	/**
	 * Upgrades this gateway's configuration from compact to full.
	 */
	upgradeConfig(): VclTask;
	/**
	 * Returns all children in this container.
	 */
	getContainedObjects(): any[];
	/**
	 * Convert gateway to advanced.
	 */
	convertGatewayToAdvanced(): any[];
	/**
	 * Generate a CSR Certificate for Gateway.
	 * @param commonName 
	 * @param orgUnit 
	 * @param orgName 
	 * @param locality 
	 * @param stateOrProvince 
	 * @param countryCode 
	 * @param privateKey 
	 * @param keySize 
	 * @param description 
	 */
	createCsrCert(commonName: string, orgUnit: string, orgName: string, locality: string, stateOrProvince: string, countryCode: string, privateKey: string, keySize: string, description: string): VclCsr;
	/**
	 * Set SSH settings for gateway.
	 * @param userName 
	 * @param password 
	 * @param passwordExpiry 
	 * @param loginBanner 
	 * @param enable 
	 */
	setSshSettings(userName: string, password: string, passwordExpiry: string, loginBanner: string, enable: boolean): void;
	/**
	 * Enable/Disable SSH status.
	 * @param enable 
	 */
	enableSshStatus(enable: boolean): void;
	/**
	 * Enable/Disable DHCP Service status.
	 * @param enable 
	 */
	enableDhcpService(enable: boolean): VclGateway;
	/**
	 * Create DHCP IP pool for gateway.
	 * @param params 
	 */
	createDhcpIpPool(params: VclIPPoolTrinityParams): VclIPPoolTrinity;
	/**
	 * Is gateway advanced.
	 */
	isAdvanced(): boolean;
	/**
	 * Create a CRL Certificate for Gateway.
	 * @param isUploadPem 
	 * @param pemFileLocation 
	 * @param pemContent 
	 * @param desc 
	 */
	createCrlCert(isUploadPem: boolean, pemFileLocation: string, pemContent: string, desc: string): VclCrl[];
	/**
	 * Create a CA Certificate for Gateway.
	 * @param isUploadPem 
	 * @param pemFileLocation 
	 * @param pemContent 
	 * @param desc 
	 */
	createCaCert(isUploadPem: boolean, pemFileLocation: string, pemContent: string, desc: string): VclCa[];
	/**
	 * Create a Service Certificate for Gateway.
	 * @param isUploadPem 
	 * @param pemEncodingText 
	 * @param pemEncodingPath 
	 * @param isUploadKey 
	 * @param privateKeyText 
	 * @param privateKeyPath 
	 * @param passPhrase 
	 * @param description 
	 */
	createServiceCert(isUploadPem: boolean, pemEncodingText: string, pemEncodingPath: string, isUploadKey: boolean, privateKeyText: string, privateKeyPath: string, passPhrase: string, description: string): VclServiceCertificate[];
	/**
	 * Create a firewall rule for legacy gateway.
	 * @param params 
	 */
	createFirewallRule(params: VclFirewallRule): VclTask;
	/**
	 * Create a firewall rule for advanced gateway.
	 * @param params 
	 */
	createFirewallRuleForAdvancedGateway(params: VclFirewallRuleTrinityParams): VclFirewallRuleTrinity;
	/**
	 * Get Edge Interface names.
	 */
	getEdgeInterfaceNames(): string[];
	/**
	 * Create Nat rule
	 * @param params 
	 */
	createNatRuleAdvanced(params: VclNatRuleParams): VclNatRule;
	/**
	 * Create Nat rule for legacy gateway
	 * @param params 
	 */
	createNatRuleLegacy(params: VclNatRuleParams): VclGateway;
	/**
	 * Find supported objects for advanced gateway.
	 */
	findSupportedObjects(): string[];
	/**
	 * Get available supported objects for advanced gateway.
	 * @param params 
	 */
	getAvailableObjectsForProvidedType(params: string): string[];
	/**
	 * Get the IpSet Names.
	 */
	getIpSetNames(): string[];
	/**
	 * Get the IpSet Names.
	 */
	getIpAddresses(): string[];
	/**
	 * Get the IpSet Names.
	 */
	getDomainNames(): string[];
	/**
	 * Update DHCP Relay Configuration for gateway.
	 * @param ipAddrs 
	 * @param domainNames 
	 * @param ipSetNames 
	 * @param gatewayInterface 
	 */
	updateDhcpRelayConfiguration(ipAddrs: string[], domainNames: string[], ipSetNames: string[], gatewayInterface: string): VclGateway;
	/**
	 * Enable firewall
	 * @param enabled 
	 * @param action 
	 * @param loggingEnabled 
	 */
	enableFirewall(enabled: boolean, action: string, loggingEnabled: boolean): VclGateway;
	/**
	 * return gateway Cli Status.
	 */
	getEdgeCliStatus(): boolean;
	/**
	 * add gateway Interface.
	 * @param gatewayInterface 
	 */
	addGatewayInterface(gatewayInterface: VclGatewayInterface): VclTask;
	/**
	 * Set up vpn service
	 * @param GatewayIpsecVpnService 
	 */
	setupVpnService(GatewayIpsecVpnService: VclGatewayIpsecVpnService): VclTask;
}

/**
 * Org vDC network representation.
 */
declare interface VclOrgVdcNetwork {
	providerInfo: string;
	edgeGateway: VclReference;
	serviceConfig: VclGatewayFeatures;
	isShared: boolean;
	vimPortGroupRef: VclVimObjectRefDefault;
	status: number;
	configuration: VclNetworkConfiguration;
	description: string;
	name: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly otherAttributes: VclMap;
	readonly vCloudExtension: VclObjectList;
	readonly tasks: VclTasksInProgress;
	readonly parent: VclVdc;
	/**
	 * Reloads the object from the server.
	 */
	updateInternalState(): void;
	/**
	 * Returns the list of allocated ip address.
	 */
	getAllocatedAddresses(): VclAllocatedIpAddress[];
	/**
	 * Gets the object's metadata.
	 */
	getMetadata(): VclMetadata;
	/**
	 * Returns the admin object for this object.
	 */
	toAdminObject(): VclAdminOrgVdcNetwork;
	/**
	 * Returns the object's reference.
	 */
	getReference(): VclReference;
	/**
	 * Gets the host that this object belongs to.
	 */
	getHost(): VclHost;
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Get the isShared status of Org Vdc Network
	 */
	getIsSharedNetworkStatus(): boolean;
}

/**
 * Admin Org vDC network representation.
 */
declare interface VclAdminOrgVdcNetwork {
	providerInfo: string;
	edgeGateway: VclReference;
	serviceConfig: VclGatewayFeatures;
	isShared: boolean;
	vimPortGroupRef: VclVimObjectRefDefault;
	status: number;
	configuration: VclNetworkConfiguration;
	description: string;
	name: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly otherAttributes: VclMap;
	readonly vCloudExtension: VclObjectList;
	readonly tasks: VclTasksInProgress;
	readonly parent: VclAdminVdc;
	/**
	 * Reloads the object from the server.
	 */
	updateInternalState(): void;
	/**
	 * Returns the list of allocated ip address.
	 */
	getAllocatedAddresses(): VclAllocatedIpAddress[];
	/**
	 * Gets the object's metadata.
	 */
	getMetadata(): VclMetadata;
	/**
	 * Returns the user object for this object.
	 */
	toUserObject(): VclOrgVdcNetwork;
	/**
	 * Returns the object's reference.
	 */
	getReference(): VclReference;
	/**
	 * Gets the host that this object belongs to.
	 */
	getHost(): VclHost;
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Deletes this Org vDC network.
	 */
	erase(): VclTask;
	/**
	 * Updates this Org vDC network.
	 */
	update(): VclTask;
	/**
	 * Resets an isolated Org vDC network.
	 */
	reset(): VclTask;
}

/**
 * Provider vDC storage profile representation.
 */
declare interface VclProviderVdcStorageProfile {
	iopsAllocated: number;
	iopsCapacity: number;
	units: string;
	capacityTotal: any;
	capacityUsed: any;
	enabled: boolean;
	description: string;
	name: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly otherAttributes: VclMap;
	readonly vCloudExtension: VclObjectList;
	readonly tasks: VclTasksInProgress;
	readonly parent: VclAdminVdc;
	/**
	 * Returns the object's metadata.
	 */
	getMetadata(): VclMetadata;
	/**
	 * Reloads the object from the server.
	 */
	updateInternalState(): void;
	/**
	 * Returns the admin extension object for this object.
	 */
	toAdminExtensionObject(): VclVMWProviderVdcStorageProfile;
	/**
	 * Returns the object's metadata.
	 */
	getReference(): VclReference;
	/**
	 * Gets the host that this object belongs to.
	 */
	getHost(): VclHost;
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Returns all children in this container.
	 */
	getContainedObjects(): any[];
}

/**
 * VMW provider vDC storae profile representation.
 */
declare interface VclVMWProviderVdcStorageProfile {
	vimStorageProfile: VclVimObjectRef;
	iopsAllocated: number;
	iopsCapacity: number;
	units: string;
	capacityTotal: any;
	capacityUsed: any;
	enabled: boolean;
	description: string;
	name: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly otherAttributes: VclMap;
	readonly vCloudExtension: VclObjectList;
	readonly tasks: VclTasksInProgress;
	readonly parent: VclVMWProviderVdc;
	/**
	 * Updates this VMW provider vDC storage profile.
	 */
	update(): VclVMWProviderVdcStorageProfile;
	/**
	 * Reloads the object from the server.
	 */
	updateInternalState(): void;
	/**
	 * Returns the admin object for this object.
	 */
	toAdminObject(): VclProviderVdcStorageProfile;
	/**
	 * Returns the object's reference.
	 */
	getReference(): VclReference;
	/**
	 * Gets the host that this object belongs to.
	 */
	getHost(): VclHost;
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Returns all children in this container.
	 */
	getContainedObjects(): any[];
}

/**
 * Provides performance statistics for the plug-in.
 */
declare class VclProfiler {
	/**
	 * Enables/disables instance counters. Instance counters are disabled by default.
	 * @param enable 
	 */
	static enableInstanceCounters(enable: boolean): void;
	/**
	 * Returns the class name that backs a given finder type.
	 * @param finderType 
	 */
	static getClassNameByFinderType(finderType: string): string;
	/**
	 * Returns the number of instances of a given type.
	 * @param className 
	 */
	static getInstanceCount(className: string): number;
	/**
	 * Enables/disables inventory notifications. Notifications are enabled by default.
	 * @param enable 
	 */
	static enableInventoryNotifications(enable: boolean): void;
	/**
	 * Returns the number of pending inventory notifications.
	 */
	static getPendingNotificationsCount(): number;
	/**
	 * Returns the number of pending triggers.
	 */
	static getPendingTriggersCount(): number;
	/**
	 * Returns the number of times a requested item was found in the cache.
	 * @param host 
	 */
	static getCacheHitCount(host: VclHost): number;
	/**
	 * Returns the number of times a requested element was not found in the cache.
	 * @param host 
	 */
	static getCacheMissCount(host: VclHost): number;
	/**
	 * Returns the he average get time.
	 * @param host 
	 */
	static getAverageCacheGetTime(host: VclHost): number;
	/**
	 * Returns the number of elements stored in the cache.
	 * @param host 
	 */
	static getCacheObjectCount(host: VclHost): number;
	/**
	 * Forces the garbage collector.
	 */
	static forceGC(): void;
}

/**
 * Represents the CSR certificates for gateway.
 */
declare interface VclCsr {
	/**
	 * Deletes the CSR Certificate.
	 */
	deleteCsrCert(): void;
	/**
	 * Self-Sign CSR.
	 * @param noOfDays 
	 */
	selfSign(noOfDays: number): VclSelfSignedCsr;
	/**
	 * Add signed certificate generated for CSR.
	 */
	addSignedCert(): VclServiceCertificate[];
}

/**
 * Represents the CRL certificates for gateway.
 */
declare interface VclCrl {
	/**
	 * Deletes the CRL Certificate.
	 */
	deleteCrlCert(): void;
}

/**
 * Represents the CA certificates for gateway.
 */
declare interface VclCa {
	/**
	 * Deletes the CA Certificate.
	 */
	deleteCaCert(): void;
}

/**
 * Represents the Self Signed certificates for gateway.
 */
declare interface VclSelfSignedCsr {
	/**
	 * Deletes the Self Signed Certificate.
	 */
	deleteCert(): void;
}

/**
 * Represents the Service certificates for gateway.
 */
declare interface VclServiceCertificate {
	/**
	 * Deletes the Service Certificate for gateway.
	 */
	deleteServiceCert(): void;
}

/**
 * Provides support for executing queries in the cloud.
 */
declare interface VclQueryService {
	/**
	 * Executes the given query. Result is VclAbstractRecordResultSet or VclReferenceResultSet
	 * @param query 
	 * @param formatType 
	 */
	executeQuery(query: string, formatType: VclFormatType): VclObject;
	/**
	 * TBS
	 * @param params 
	 */
	queryvAppTemplateRecords(params: VclQueryParams): VclRecordResultSet;
	/**
	 * TBS
	 * @param params 
	 */
	queryvAppTemplateIdRecords(params: VclQueryParams): VclRecordResultSet;
	/**
	 * TBS
	 * @param type 
	 */
	queryMediaReferences(type: VclQueryParams): VclReferenceResultSet;
	/**
	 * TBS
	 * @param params 
	 */
	queryMediaRecords(params: VclQueryParams): VclRecordResultSet;
	/**
	 * TBS
	 * @param params 
	 */
	queryMediaIdRecords(params: VclQueryParams): VclRecordResultSet;
	/**
	 * TBS
	 * @param type 
	 */
	queryDiskReferences(type: VclQueryParams): VclReferenceResultSet;
	/**
	 * TBS
	 * @param params 
	 */
	queryDiskRecords(params: VclQueryParams): VclRecordResultSet;
	/**
	 * TBS
	 * @param params 
	 */
	queryDiskIdRecords(params: VclQueryParams): VclRecordResultSet;
	/**
	 * TBS
	 * @param type 
	 * @param params 
	 */
	queryRecords(type: VclQueryRecordType, params: VclQueryParams): VclAbstractRecordResultSet;
	/**
	 * TBS
	 * @param type 
	 * @param params 
	 */
	queryIdRecords(type: VclQueryRecordType, params: VclQueryParams): VclAbstractRecordResultSet;
	/**
	 * TBS
	 * @param type 
	 */
	queryCatalogReferences(type: VclQueryParams): VclReferenceResultSet;
	/**
	 * TBS
	 * @param params 
	 */
	queryCatalogRecords(params: VclQueryParams): VclRecordResultSet;
	/**
	 * TBS
	 * @param params 
	 */
	queryCatalogIdRecords(params: VclQueryParams): VclRecordResultSet;
	/**
	 * TBS
	 * @param type 
	 */
	queryvAppReferences(type: VclQueryParams): VclReferenceResultSet;
	/**
	 * TBS
	 * @param params 
	 */
	queryvAppRecords(params: VclQueryParams): VclRecordResultSet;
	/**
	 * TBS
	 * @param params 
	 */
	queryvAppIdRecords(params: VclQueryParams): VclRecordResultSet;
	/**
	 * TBS
	 * @param type 
	 */
	queryVmReferences(type: VclQueryParams): VclReferenceResultSet;
	/**
	 * TBS
	 * @param params 
	 */
	queryVmRecords(params: VclQueryParams): VclRecordResultSet;
	/**
	 * TBS
	 * @param params 
	 */
	queryVmIdRecords(params: VclQueryParams): VclRecordResultSet;
	/**
	 * TBS
	 * @param type 
	 */
	queryvAppTemplateReferences(type: VclQueryParams): VclReferenceResultSet;
	/**
	 * TBS
	 * @param type 
	 * @param params 
	 */
	queryReferences(type: VclQueryReferenceType, params: VclQueryParams): VclReferenceResultSet;
}

/**
 * Provides support for executing queries in the cloud.
 */
declare interface VclAdminQueryService {
	/**
	 * Executes the given query. Result is VclAbstractRecordResultSet or VclReferenceResultSet
	 * @param query 
	 * @param formatType 
	 */
	executeQuery(query: string, formatType: VclFormatType): VclObject;
	/**
	 * TBS
	 * @param params 
	 */
	queryUserRecords(params: VclQueryParams): VclRecordResultSet;
	/**
	 * TBS
	 * @param params 
	 */
	queryRoleRecords(params: VclQueryParams): VclRecordResultSet;
	/**
	 * TBS
	 * @param type 
	 */
	queryGroupReferences(type: VclQueryParams): VclReferenceResultSet;
	/**
	 * TBS
	 * @param params 
	 */
	queryGroupRecords(params: VclQueryParams): VclRecordResultSet;
	/**
	 * TBS
	 * @param params 
	 */
	queryGroupIdRecords(params: VclQueryParams): VclRecordResultSet;
	/**
	 * TBS
	 * @param type 
	 */
	queryUserReferences(type: VclQueryParams): VclReferenceResultSet;
	/**
	 * TBS
	 * @param params 
	 */
	queryUserIdRecords(params: VclQueryParams): VclRecordResultSet;
	/**
	 * TBS
	 * @param type 
	 */
	queryStrandedUserReferences(type: VclQueryParams): VclReferenceResultSet;
	/**
	 * TBS
	 * @param params 
	 */
	queryStrandedUserRecords(params: VclQueryParams): VclRecordResultSet;
	/**
	 * TBS
	 * @param params 
	 */
	queryStrandedUserIdRecords(params: VclQueryParams): VclRecordResultSet;
	/**
	 * TBS
	 * @param type 
	 */
	queryRoleReferences(type: VclQueryParams): VclReferenceResultSet;
	/**
	 * TBS
	 * @param params 
	 */
	queryRoleIdRecords(params: VclQueryParams): VclRecordResultSet;
	/**
	 * TBS
	 * @param type 
	 */
	queryRightReferences(type: VclQueryParams): VclReferenceResultSet;
	/**
	 * TBS
	 * @param params 
	 */
	queryRightRecords(params: VclQueryParams): VclRecordResultSet;
	/**
	 * TBS
	 * @param params 
	 */
	queryRightIdRecords(params: VclQueryParams): VclRecordResultSet;
	/**
	 * TBS
	 * @param type 
	 */
	queryOrgReferences(type: VclQueryParams): VclReferenceResultSet;
	/**
	 * TBS
	 * @param params 
	 */
	queryOrgRecords(params: VclQueryParams): VclRecordResultSet;
	/**
	 * TBS
	 * @param params 
	 */
	queryOrgIdRecords(params: VclQueryParams): VclRecordResultSet;
	/**
	 * TBS
	 * @param type 
	 */
	queryOrgVdcReferences(type: VclQueryParams): VclReferenceResultSet;
	/**
	 * TBS
	 * @param params 
	 */
	queryOrgVdcRecords(params: VclQueryParams): VclRecordResultSet;
	/**
	 * TBS
	 * @param params 
	 */
	queryOrgVdcIdRecords(params: VclQueryParams): VclRecordResultSet;
}

/**
 * Provides support for executing queries in the cloud.
 */
declare interface VclExtensionQueryService {
	/**
	 * Executes the given query. Result is VclAbstractRecordResultSet or VclReferenceResultSet
	 * @param query 
	 * @param formatType 
	 */
	executeQuery(query: string, formatType: VclFormatType): VclObject;
	/**
	 * TBS
	 * @param type 
	 */
	queryHostReferences(type: VclQueryParams): VclReferenceResultSet;
	/**
	 * TBS
	 * @param params 
	 */
	queryHostRecords(params: VclQueryParams): VclRecordResultSet;
	/**
	 * TBS
	 * @param params 
	 */
	queryHostIdRecords(params: VclQueryParams): VclRecordResultSet;
	/**
	 * TBS
	 * @param type 
	 */
	queryDatastoreReferences(type: VclQueryParams): VclReferenceResultSet;
	/**
	 * TBS
	 * @param params 
	 */
	queryDatastoreRecords(params: VclQueryParams): VclRecordResultSet;
	/**
	 * TBS
	 * @param params 
	 */
	queryDatastoreIdRecords(params: VclQueryParams): VclRecordResultSet;
	/**
	 * TBS
	 * @param type 
	 */
	queryExternalNetworkReferences(type: VclQueryParams): VclReferenceResultSet;
	/**
	 * TBS
	 * @param params 
	 */
	queryExternalNetworkRecords(params: VclQueryParams): VclRecordResultSet;
	/**
	 * TBS
	 * @param params 
	 */
	queryExternalNetworkIdRecords(params: VclQueryParams): VclRecordResultSet;
	/**
	 * TBS
	 * @param type 
	 */
	queryNetworkPoolReferences(type: VclQueryParams): VclReferenceResultSet;
	/**
	 * TBS
	 * @param params 
	 */
	queryNetworkPoolRecords(params: VclQueryParams): VclRecordResultSet;
	/**
	 * TBS
	 * @param params 
	 */
	queryNetworkPoolIdRecords(params: VclQueryParams): VclRecordResultSet;
	/**
	 * TBS
	 * @param type 
	 */
	queryProviderVdcReferences(type: VclQueryParams): VclReferenceResultSet;
	/**
	 * TBS
	 * @param params 
	 */
	queryProviderVdcRecords(params: VclQueryParams): VclRecordResultSet;
	/**
	 * TBS
	 * @param params 
	 */
	queryProviderVdcIdRecords(params: VclQueryParams): VclRecordResultSet;
	/**
	 * TBS
	 * @param type 
	 */
	queryVimServerReferences(type: VclQueryParams): VclReferenceResultSet;
	/**
	 * TBS
	 * @param params 
	 */
	queryVimServerRecords(params: VclQueryParams): VclRecordResultSet;
	/**
	 * TBS
	 * @param params 
	 */
	queryVimServerIdRecords(params: VclQueryParams): VclRecordResultSet;
	/**
	 * TBS
	 * @param type 
	 */
	queryAllOrgNetworkReferences(type: VclQueryParams): VclReferenceResultSet;
	/**
	 * TBS
	 * @param params 
	 */
	queryAllOrgNetworkRecords(params: VclQueryParams): VclRecordResultSet;
	/**
	 * TBS
	 * @param params 
	 */
	queryAllOrgNetworkIdRecords(params: VclQueryParams): VclRecordResultSet;
	/**
	 * TBS
	 * @param type 
	 */
	queryAllOrgVdcReferences(type: VclQueryParams): VclReferenceResultSet;
	/**
	 * TBS
	 * @param params 
	 */
	queryAllOrgVdcRecords(params: VclQueryParams): VclRecordResultSet;
	/**
	 * TBS
	 * @param params 
	 */
	queryAllOrgVdcIdRecords(params: VclQueryParams): VclRecordResultSet;
	/**
	 * TBS
	 * @param type 
	 */
	queryAllVappReferences(type: VclQueryParams): VclReferenceResultSet;
	/**
	 * TBS
	 * @param params 
	 */
	queryAllVappRecords(params: VclQueryParams): VclRecordResultSet;
	/**
	 * TBS
	 * @param params 
	 */
	queryAllVappIdRecords(params: VclQueryParams): VclRecordResultSet;
}

/**
 * TBS
 */
declare interface VclAbstractRecordResultSet {
	/**
	 * Returns the records within the current page.
	 * @param type 
	 */
	getRecords(type: any): any[];
	/**
	 * Returns the next page of records.
	 */
	getNextPage(): VclRecordResultSet;
	/**
	 * Returns the previous page of records.
	 */
	getPreviousPage(): VclRecordResultSet;
	/**
	 * Returns the last page of records.
	 */
	getLastPage(): VclRecordResultSet;
	/**
	 * Returns the first page of records.
	 */
	getFirstPage(): VclRecordResultSet;
	/**
	 * Returns the reference results for the records.
	 */
	getReferenceResult(): VclReferenceResultSet;
	/**
	 * Returns true if the first page of records is available.
	 */
	hasFirstPage(): boolean;
	/**
	 * Returns true if the last page of records is available.
	 */
	hasLastPage(): boolean;
	/**
	 * Returns true if the next page of records is available.
	 */
	hasNextPage(): boolean;
	/**
	 * Returns true if the previous page of records is available.
	 */
	hasPreviousPage(): boolean;
	/**
	 * Returns the page number.
	 */
	getPage(): number;
	/**
	 * Returns the page size.
	 */
	getPageSize(): number;
	/**
	 * Returns the total number of items.
	 */
	getTotal(): number;
}

/**
 * TBS
 */
declare interface VclRecordResultSet {
	/**
	 * Returns the records within the current page.
	 */
	getRecords(): any[];
	/**
	 * Returns the next page of records.
	 */
	getNextPage(): VclRecordResultSet;
	/**
	 * Returns the previous page of records.
	 */
	getPreviousPage(): VclRecordResultSet;
	/**
	 * Returns the last page of records.
	 */
	getLastPage(): VclRecordResultSet;
	/**
	 * Returns the first page of records.
	 */
	getFirstPage(): VclRecordResultSet;
	/**
	 * Returns the reference results for the records.
	 */
	getReferenceResult(): VclReferenceResultSet;
	/**
	 * Returns true if the first page of records is available.
	 */
	hasFirstPage(): boolean;
	/**
	 * Returns true if the last page of records is available.
	 */
	hasLastPage(): boolean;
	/**
	 * Returns true if the next page of records is available.
	 */
	hasNextPage(): boolean;
	/**
	 * Returns true if the previous page of records is available.
	 */
	hasPreviousPage(): boolean;
	/**
	 * Returns the page number.
	 */
	getPage(): number;
	/**
	 * Returns the page size.
	 */
	getPageSize(): number;
	/**
	 * Returns the total number of items.
	 */
	getTotal(): number;
}

/**
 * TBS
 */
declare interface VclReferenceResultSet {
	/**
	 * Returns the references within the current page.
	 */
	getReferences(): VclReference[];
	/**
	 * Returns the next page of references.
	 */
	getNextPage(): VclReferenceResultSet;
	/**
	 * Returns the previous page of references.
	 */
	getPreviousPage(): VclReferenceResultSet;
	/**
	 * Returns the last page of references.
	 */
	getLastPage(): VclReferenceResultSet;
	/**
	 * Returns the first page of references.
	 */
	getFirstPage(): VclReferenceResultSet;
	/**
	 * Returns true if the first page of references is available.
	 */
	hasFirstPage(): boolean;
	/**
	 * Returns true if the last page of references is available.
	 */
	hasLastPage(): boolean;
	/**
	 * Returns true if the next page of references is available.
	 */
	hasNextPage(): boolean;
	/**
	 * Returns true if the previous page of references is available.
	 */
	hasPreviousPage(): boolean;
	/**
	 * Returns the page number.
	 */
	getPage(): number;
	/**
	 * Returns the page size.
	 */
	getPageSize(): number;
	/**
	 * Returns the total number of items.
	 */
	getTotal(): number;
}

/**
 * A generic list of objects.
 */
declare interface VclList {
	/**
	 * Finds objects of a given type within the list.
	 * @param type 
	 */
	find(type: any): any[];
	/**
	 * Returns all objects within the list.
	 */
	enumerate(): any[];
	/**
	 * Adds a new object to the list.
	 * @param object 
	 */
	add(object: any): void;
	/**
	 * Removes an object from the list.
	 * @param index 
	 */
	remove(index: number): void;
	/**
	 * Returns an index of the given element.
	 * @param element 
	 */
	indexOf(element: any): number;
	/**
	 * Clears the list.
	 */
	clear(): void;
	/**
	 * Returns the number of elements in this list.
	 */
	size(): number;
}

/**
 * TBS
 */
declare class VclIdentityProviderSourceType {
	static readonly values: string[];
	static readonly value: string;
	static readonly INTEGRATED: VclIdentityProviderSourceType;
	static readonly SAML: VclIdentityProviderSourceType;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclIdentityProviderSourceType;
}

/**
 * TBS
 */
declare class VclVMStatus {
	static readonly values: string[];
	static readonly value: string;
	static readonly FAILED_CREATION: VclVMStatus;
	static readonly UNRESOLVED: VclVMStatus;
	static readonly RESOLVED: VclVMStatus;
	static readonly SUSPENDED: VclVMStatus;
	static readonly POWERED_ON: VclVMStatus;
	static readonly WAITING_FOR_INPUT: VclVMStatus;
	static readonly UNKNOWN: VclVMStatus;
	static readonly UNRECOGNIZED: VclVMStatus;
	static readonly POWERED_OFF: VclVMStatus;
	static readonly INCONSISTENT_STATE: VclVMStatus;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclVMStatus;
}

/**
 * TBS
 */
declare class VclFirewallDirectionType {
	static readonly values: string[];
	static readonly value: string;
	static readonly IN: VclFirewallDirectionType;
	static readonly OUT: VclFirewallDirectionType;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclFirewallDirectionType;
}

/**
 * TBS
 */
declare class VclOperatingSystemFamilyType {
	static readonly values: string[];
	static readonly value: string;
	static readonly MICROSOFT_WINDOWS: VclOperatingSystemFamilyType;
	static readonly LINUX: VclOperatingSystemFamilyType;
	static readonly OTHER: VclOperatingSystemFamilyType;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclOperatingSystemFamilyType;
}

/**
 * TBS
 */
declare class VclIpAddressAllocationModeType {
	static readonly values: string[];
	static readonly value: string;
	static readonly NONE: VclIpAddressAllocationModeType;
	static readonly MANUAL: VclIpAddressAllocationModeType;
	static readonly POOL: VclIpAddressAllocationModeType;
	static readonly DHCP: VclIpAddressAllocationModeType;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclIpAddressAllocationModeType;
}

/**
 * TBS
 */
declare class VclFirewallPolicyType {
	static readonly values: string[];
	static readonly value: string;
	static readonly DROP: VclFirewallPolicyType;
	static readonly ALLOW: VclFirewallPolicyType;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclFirewallPolicyType;
}

/**
 * TBS
 */
declare class VclGatewayBackingConfigValuesType {
	static readonly values: string[];
	static readonly value: string;
	static readonly COMPACT: VclGatewayBackingConfigValuesType;
	static readonly FULL: VclGatewayBackingConfigValuesType;
	static readonly FULL4: VclGatewayBackingConfigValuesType;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclGatewayBackingConfigValuesType;
}

/**
 * TBS
 */
declare class VclVimObjectTypeEnum {
	static readonly values: string[];
	static readonly value: string;
	static readonly CLUSTER_COMPUTE_RESOURCE: VclVimObjectTypeEnum;
	static readonly RESOURCE_POOL: VclVimObjectTypeEnum;
	static readonly DATASTORE: VclVimObjectTypeEnum;
	static readonly HOST: VclVimObjectTypeEnum;
	static readonly VIRTUAL_MACHINE: VclVimObjectTypeEnum;
	static readonly VIRTUAL_APP: VclVimObjectTypeEnum;
	static readonly NETWORK: VclVimObjectTypeEnum;
	static readonly DV_PORTGROUP: VclVimObjectTypeEnum;
	static readonly DV_SWITCH: VclVimObjectTypeEnum;
	static readonly FILE: VclVimObjectTypeEnum;
	static readonly FOLDER: VclVimObjectTypeEnum;
	static readonly DATASTORE_CLUSTER: VclVimObjectTypeEnum;
	static readonly STORAGE_PROFILE: VclVimObjectTypeEnum;
	static readonly SHIELD_MANAGER: VclVimObjectTypeEnum;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclVimObjectTypeEnum;
}

/**
 * TBS
 */
declare class VclEntityType {
	static readonly values: string[];
	static readonly value: string;
	static readonly ORGANIZATION: VclEntityType;
	static readonly CATALOG: VclEntityType;
	static readonly CATALOG_ITEM: VclEntityType;
	static readonly USER: VclEntityType;
	static readonly GROUP: VclEntityType;
	static readonly ROLE: VclEntityType;
	static readonly RIGHT: VclEntityType;
	static readonly TASK: VclEntityType;
	static readonly NETWORK: VclEntityType;
	static readonly NETWORK_POOL: VclEntityType;
	static readonly VAPP: VclEntityType;
	static readonly VAPP_TEMPLATE: VclEntityType;
	static readonly VM: VclEntityType;
	static readonly MEDIA: VclEntityType;
	static readonly HOST: VclEntityType;
	static readonly VIM_SERVER: VclEntityType;
	static readonly PROVIDER_VDC: VclEntityType;
	static readonly VDC: VclEntityType;
	static readonly DATASTORE: VclEntityType;
	static readonly LICENSE_REPORT: VclEntityType;
	static readonly BLOCKING_TASK: VclEntityType;
	static readonly DISK: VclEntityType;
	static readonly GATEWAY: VclEntityType;
	static readonly STRANDED_ITEM: VclEntityType;
	static readonly VDC_STORAGE_PROFILE: VclEntityType;
	static readonly PROVIDER_VDC_STORAGE_PROFILE: VclEntityType;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclEntityType;
}

/**
 * TBS
 */
declare class VclVappTemplateStatus {
	static readonly values: string[];
	static readonly value: string;
	static readonly FAILED_CREATION: VclVappTemplateStatus;
	static readonly UNRESOLVED: VclVappTemplateStatus;
	static readonly RESOLVED: VclVappTemplateStatus;
	static readonly UNKNOWN: VclVappTemplateStatus;
	static readonly UNRECOGNIZED: VclVappTemplateStatus;
	static readonly POWERED_OFF: VclVappTemplateStatus;
	static readonly MIXED: VclVappTemplateStatus;
	static readonly DESCRIPTOR_PENDING: VclVappTemplateStatus;
	static readonly COPYING_CONTENTS: VclVappTemplateStatus;
	static readonly DISK_CONTENTS_PENDING: VclVappTemplateStatus;
	static readonly QUARANTINED: VclVappTemplateStatus;
	static readonly QUARANTINE_EXPIRED: VclVappTemplateStatus;
	static readonly REJECTED: VclVappTemplateStatus;
	static readonly TRANSFER_TIMEOUT: VclVappTemplateStatus;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclVappTemplateStatus;
}

/**
 * TBS
 */
declare class VclLdapAuthenticationMechanismType {
	static readonly values: string[];
	static readonly value: string;
	static readonly SIMPLE: VclLdapAuthenticationMechanismType;
	static readonly KERBEROS: VclLdapAuthenticationMechanismType;
	static readonly MD5DIGEST: VclLdapAuthenticationMechanismType;
	static readonly NTLM: VclLdapAuthenticationMechanismType;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclLdapAuthenticationMechanismType;
}

/**
 * TBS
 */
declare class VclAllocationModelType {
	static readonly values: string[];
	static readonly value: string;
	static readonly ALLOCATIONVAPP: VclAllocationModelType;
	static readonly ALLOCATIONPOOL: VclAllocationModelType;
	static readonly RESERVATIONPOOL: VclAllocationModelType;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclAllocationModelType;
}

/**
 * TBS
 */
declare class VclNatTypeType {
	static readonly values: string[];
	static readonly value: string;
	static readonly IPTRANSLATION: VclNatTypeType;
	static readonly PORTFORWARDING: VclNatTypeType;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclNatTypeType;
}

/**
 * TBS
 */
declare class VclNetworkAdapterType {
	static readonly values: string[];
	static readonly value: string;
	static readonly E1000: VclNetworkAdapterType;
	static readonly VLANCE: VclNetworkAdapterType;
	static readonly VMXNET: VclNetworkAdapterType;
	static readonly FLEXIBLE: VclNetworkAdapterType;
	static readonly VMXNET2: VclNetworkAdapterType;
	static readonly VMXNET3: VclNetworkAdapterType;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclNetworkAdapterType;
}

/**
 * TBS
 */
declare class VclLdapModeType {
	static readonly values: string[];
	static readonly value: string;
	static readonly NONE: VclLdapModeType;
	static readonly SYSTEM: VclLdapModeType;
	static readonly CUSTOM: VclLdapModeType;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclLdapModeType;
}

/**
 * TBS
 */
declare class VclLdapConnectorType {
	static readonly values: string[];
	static readonly value: string;
	static readonly ACTIVE_DIRECTORY: VclLdapConnectorType;
	static readonly OPEN_LDAP: VclLdapConnectorType;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclLdapConnectorType;
}

/**
 * TBS
 */
declare class VclMetadataDomain {
	static readonly values: string[];
	static readonly value: string;
	static readonly SYSTEM: VclMetadataDomain;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclMetadataDomain;
}

/**
 * TBS
 */
declare class VclBlockingTaskOperationType {
	static readonly values: string[];
	static readonly value: string;
	static readonly VDC_CREATE_VDC: VclBlockingTaskOperationType;
	static readonly VDC_DELETE_VDC: VclBlockingTaskOperationType;
	static readonly VDC_UPDATE_VDC: VclBlockingTaskOperationType;
	static readonly VDC_ENABLE_DOWNLOAD: VclBlockingTaskOperationType;
	static readonly VAPP_MIGRATE_VMS: VclBlockingTaskOperationType;
	static readonly RCL_CREATE_PROVIDER_VDC: VclBlockingTaskOperationType;
	static readonly RCL_DELETE_PROVIDER_VDC: VclBlockingTaskOperationType;
	static readonly RCL_MERGE_PROVIDER_VDC: VclBlockingTaskOperationType;
	static readonly NETWORK_CREATE_NETWORK_POOL: VclBlockingTaskOperationType;
	static readonly NETWORK_ENABLE_CROSSHOST_SERVICE: VclBlockingTaskOperationType;
	static readonly NETWORK_CREATE_PROVIDER_NETWORK: VclBlockingTaskOperationType;
	static readonly NETWORK_DELETE_NETWORK_POOL: VclBlockingTaskOperationType;
	static readonly NETWORK_UPDATE_NETWORK_POOL: VclBlockingTaskOperationType;
	static readonly NETWORK_UPDATE_VLAN_POOL: VclBlockingTaskOperationType;
	static readonly IMPORT_SINGLETON_TEMPLATE: VclBlockingTaskOperationType;
	static readonly IMPORT_SINGLETON_VAPP: VclBlockingTaskOperationType;
	static readonly IMPORT_MEDIA: VclBlockingTaskOperationType;
	static readonly IMPORT_INTO_EXISTING_VAPP: VclBlockingTaskOperationType;
	static readonly IMPORT_VC_VMS_INTO_EXISTING_VAPP: VclBlockingTaskOperationType;
	static readonly CATALOG_SYNC_ALL: VclBlockingTaskOperationType;
	static readonly CATALOG_CACHE_PUBLISHED_ITEMS: VclBlockingTaskOperationType;
	static readonly CATALOG_CACHE_PUBLISHED_ITEM: VclBlockingTaskOperationType;
	static readonly VAPP_UPDATE_VM: VclBlockingTaskOperationType;
	static readonly TEMPLATE_UPDATE_VM: VclBlockingTaskOperationType;
	static readonly VAPP_DEPLOY: VclBlockingTaskOperationType;
	static readonly VAPP_POWER_OFF: VclBlockingTaskOperationType;
	static readonly VAPP_SUSPEND: VclBlockingTaskOperationType;
	static readonly VAPP_RESET: VclBlockingTaskOperationType;
	static readonly VAPP_REBOOT_GUEST: VclBlockingTaskOperationType;
	static readonly VAPP_SHUTDOWN_GUEST: VclBlockingTaskOperationType;
	static readonly VAPP_UPGRADE_HW_VERSION: VclBlockingTaskOperationType;
	static readonly VAPP_UNDEPLOY_POWER_OFF: VclBlockingTaskOperationType;
	static readonly VAPP_UNDEPLOY_SUSPEND: VclBlockingTaskOperationType;
	static readonly VAPP_ATTACH_DISK: VclBlockingTaskOperationType;
	static readonly VAPP_DETACH_DISK: VclBlockingTaskOperationType;
	static readonly VAPP_CHECK_VM_COMPLIANCE: VclBlockingTaskOperationType;
	static readonly VAPP_CREATE_SNAPSHOT: VclBlockingTaskOperationType;
	static readonly VAPP_REVERT_TO_CURRENT_SNAPSHOT: VclBlockingTaskOperationType;
	static readonly VAPP_REMOVE_ALL_SNAPSHOTS: VclBlockingTaskOperationType;
	static readonly VDC_INSTANTIATE_VAPP: VclBlockingTaskOperationType;
	static readonly VDC_COMPOSE_VAPP: VclBlockingTaskOperationType;
	static readonly VDC_RECOMPOSE_VAPP: VclBlockingTaskOperationType;
	static readonly VDC_CAPTURE_TEMPLATE: VclBlockingTaskOperationType;
	static readonly VDC_COPY_VAPP: VclBlockingTaskOperationType;
	static readonly VDC_COPY_TEMPLATE: VclBlockingTaskOperationType;
	static readonly VDC_COPY_MEDIA: VclBlockingTaskOperationType;
	static readonly VDC_UPDATE_VAPP: VclBlockingTaskOperationType;
	static readonly LEGACY_VDC_UPDATE_TEMPLATE: VclBlockingTaskOperationType;
	static readonly LEGACY_VDC_UPDATE_MEDIA: VclBlockingTaskOperationType;
	static readonly VDC_UPDATE_TEMPLATE: VclBlockingTaskOperationType;
	static readonly VDC_UPDATE_MEDIA: VclBlockingTaskOperationType;
	static readonly VDC_DELETE_VAPP: VclBlockingTaskOperationType;
	static readonly VDC_DELETE_TEMPLATE: VclBlockingTaskOperationType;
	static readonly VDC_DELETE_MEDIA: VclBlockingTaskOperationType;
	static readonly VDC_CREATE_DISK: VclBlockingTaskOperationType;
	static readonly VDC_DELETE_DISK: VclBlockingTaskOperationType;
	static readonly VDC_UPDATE_DISK: VclBlockingTaskOperationType;
	static readonly VDC_UPDATE_STORAGE_PROFILES: VclBlockingTaskOperationType;
	static readonly VDC_UPLOAD_MEDIA: VclBlockingTaskOperationType;
	static readonly VDC_UPLOAD_OVF_CONTENTS: VclBlockingTaskOperationType;
	static readonly CATALOG_ITEM_ENABLE_DOWNLOAD: VclBlockingTaskOperationType;
	static readonly CATALOG_ITEM_SYNC: VclBlockingTaskOperationType;
	static readonly CATALOG_SYNC: VclBlockingTaskOperationType;
	static readonly CATALOG_DELETE: VclBlockingTaskOperationType;
	static readonly CATALOG_ITEM_DELETE: VclBlockingTaskOperationType;
	static readonly CATALOG_DELETE_BACKING: VclBlockingTaskOperationType;
	static readonly NETWORK_DELETE: VclBlockingTaskOperationType;
	static readonly NETWORK_UPDATE_NETWORK: VclBlockingTaskOperationType;
	static readonly RCL_ENABLE_VXLAN_FOR_PROVIDER_VDC: VclBlockingTaskOperationType;
	static readonly NETWORK_REPAIR_NETWORK_POOL: VclBlockingTaskOperationType;
	static readonly NETWORK_MERGE_NETWORK_POOLS: VclBlockingTaskOperationType;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclBlockingTaskOperationType;
}

/**
 * TBS
 */
declare class VclGatewayEnums {
	static readonly values: string[];
	static readonly value: string;
	static readonly UPLINK: VclGatewayEnums;
	static readonly INTERNAL: VclGatewayEnums;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclGatewayEnums;
}

/**
 * TBS
 */
declare class VclImageType {
	static readonly values: string[];
	static readonly value: string;
	static readonly ISO: VclImageType;
	static readonly FLOPPY: VclImageType;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclImageType;
}

/**
 * TBS
 */
declare class VclAllocatedIpAddressAllocationType {
	static readonly values: string[];
	static readonly value: string;
	static readonly VMALLOCATED: VclAllocatedIpAddressAllocationType;
	static readonly NATROUTED: VclAllocatedIpAddressAllocationType;
	static readonly VSMALLOCATED: VclAllocatedIpAddressAllocationType;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclAllocatedIpAddressAllocationType;
}

/**
 * TBS
 */
declare class VclTaskStatusType {
	static readonly values: string[];
	static readonly value: string;
	static readonly QUEUED: VclTaskStatusType;
	static readonly PRERUNNING: VclTaskStatusType;
	static readonly SUCCESS: VclTaskStatusType;
	static readonly RUNNING: VclTaskStatusType;
	static readonly ERROR: VclTaskStatusType;
	static readonly CANCELED: VclTaskStatusType;
	static readonly ABORTED: VclTaskStatusType;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclTaskStatusType;
}

/**
 * TBS
 */
declare class VclOperatingSystemType {
	static readonly values: string[];
	static readonly value: string;
	static readonly RHEL_6_64_BIT: VclOperatingSystemType;
	static readonly RHEL_6_32_BIT: VclOperatingSystemType;
	static readonly RHEL_5_64_BIT: VclOperatingSystemType;
	static readonly RHEL_5_32_BIT: VclOperatingSystemType;
	static readonly RHEL_4_64_BIT: VclOperatingSystemType;
	static readonly RHEL_4_32_BIT: VclOperatingSystemType;
	static readonly RHEL_3_64_BIT: VclOperatingSystemType;
	static readonly RHEL_3_32_BIT: VclOperatingSystemType;
	static readonly RHEL_2_1: VclOperatingSystemType;
	static readonly SUSE_11_64_BIT: VclOperatingSystemType;
	static readonly SUSE_11_32_BIT: VclOperatingSystemType;
	static readonly SUSE_10_64_BIT: VclOperatingSystemType;
	static readonly SUSE_10_32_BIT: VclOperatingSystemType;
	static readonly SUSE_8_9_64_BIT: VclOperatingSystemType;
	static readonly SUSE_8_9_32_BIT: VclOperatingSystemType;
	static readonly CENTOS_4_5_6_64_BIT: VclOperatingSystemType;
	static readonly CENTOS_4_5_6_32_BIT: VclOperatingSystemType;
	static readonly DEBIAN_6_64_BIT: VclOperatingSystemType;
	static readonly DEBIAN_6_32_BIT: VclOperatingSystemType;
	static readonly DEBIAN_5_64_BIT: VclOperatingSystemType;
	static readonly DEBIAN_5_32_BIT: VclOperatingSystemType;
	static readonly DEBIAN_4_64_BIT: VclOperatingSystemType;
	static readonly DEBIAN_4_32_BIT: VclOperatingSystemType;
	static readonly ASIANUX_4_64_BIT: VclOperatingSystemType;
	static readonly ASIANUX_4_32_BIT: VclOperatingSystemType;
	static readonly ASIANUX_3_64_BIT: VclOperatingSystemType;
	static readonly ASIANUX_3_32_BIT: VclOperatingSystemType;
	static readonly NOVELL_OPEN_ENTERPRISE_SERVER: VclOperatingSystemType;
	static readonly ORACLE_LINUX_4_5_6_64_BIT: VclOperatingSystemType;
	static readonly ORACLE_LINUX_4_5_6_32_BIT: VclOperatingSystemType;
	static readonly UBUNTU_64_BIT: VclOperatingSystemType;
	static readonly UBUNTU_32_BIT: VclOperatingSystemType;
	static readonly OTHER_2_6_X_LINUX_64_BIT: VclOperatingSystemType;
	static readonly OTHER_2_6_X_LINUX_32_BIT: VclOperatingSystemType;
	static readonly OTHER_2_4_X_LINUX_64_BIT: VclOperatingSystemType;
	static readonly OTHER_2_4_X_LINUX_32_BIT: VclOperatingSystemType;
	static readonly OTHER_LINUX_64_BIT: VclOperatingSystemType;
	static readonly OTHER_LINUX_32_BIT: VclOperatingSystemType;
	static readonly WINDOWS_SERVER_8_64_BIT: VclOperatingSystemType;
	static readonly WINDOWS_SERVER_2008_R2_64_BIT: VclOperatingSystemType;
	static readonly WINDOWS_SERVER_2008_64_BIT: VclOperatingSystemType;
	static readonly WINDOWS_SERVER_2008_32_BIT: VclOperatingSystemType;
	static readonly WINDOWS_SERVER_2003_64_BIT: VclOperatingSystemType;
	static readonly WINDOWS_SERVER_2003_32_BIT: VclOperatingSystemType;
	static readonly WINDOWS_SERVER_2003_DATACENTER_64_BIT: VclOperatingSystemType;
	static readonly WINDOWS_SERVER_2003_DATACENTER_32_BIT: VclOperatingSystemType;
	static readonly WINDOWS_SERVER_2003_STANDARD_64_BIT: VclOperatingSystemType;
	static readonly WINDOWS_SERVER_2003_STANDARD_32_BIT: VclOperatingSystemType;
	static readonly WINDOWS_SERVER_2003_WEB_32_BIT: VclOperatingSystemType;
	static readonly WINDOWS_SMALL_BUSINESS_SERVER_2003: VclOperatingSystemType;
	static readonly WINDOWS_8_64_BIT: VclOperatingSystemType;
	static readonly WINDOWS_8_32_BIT: VclOperatingSystemType;
	static readonly WINDOWS_7_64_BIT: VclOperatingSystemType;
	static readonly WINDOWS_7_32_BIT: VclOperatingSystemType;
	static readonly WINDOWS_VISTA_64_BIT: VclOperatingSystemType;
	static readonly WINDOWS_VISTA_32_BIT: VclOperatingSystemType;
	static readonly WINDOWS_XP_PROFESSIONAL_64_BIT: VclOperatingSystemType;
	static readonly WINDOWS_XP_PROFESSIONAL_32_BIT: VclOperatingSystemType;
	static readonly WINDOWS_2000: VclOperatingSystemType;
	static readonly WINDOWS_2000_SERVER: VclOperatingSystemType;
	static readonly WINDOWS_2000_PROFESSIONAL: VclOperatingSystemType;
	static readonly WINDOWS_NT: VclOperatingSystemType;
	static readonly WINDOWS_98: VclOperatingSystemType;
	static readonly WINDOWS_95: VclOperatingSystemType;
	static readonly WINDOWS_3_1: VclOperatingSystemType;
	static readonly MICROSOFT_MS_DOS: VclOperatingSystemType;
	static readonly APPLE_MAC_OS_X_10_7_64_BIT: VclOperatingSystemType;
	static readonly APPLE_MAC_OS_X_10_7_32_BIT: VclOperatingSystemType;
	static readonly APPLE_MAC_OS_X_10_6_64_BIT: VclOperatingSystemType;
	static readonly APPLE_MAC_OS_X_10_6_32_BIT: VclOperatingSystemType;
	static readonly APPLE_MAC_OS_X_10_5_64_BIT: VclOperatingSystemType;
	static readonly APPLE_MAC_OS_X_10_5_32_BIT: VclOperatingSystemType;
	static readonly FREEBSD_64_BIT: VclOperatingSystemType;
	static readonly FREEBSD_32_BIT: VclOperatingSystemType;
	static readonly IBM_OS_2: VclOperatingSystemType;
	static readonly NETWARE_6_X: VclOperatingSystemType;
	static readonly NETWARE_5_1: VclOperatingSystemType;
	static readonly ORACLE_SOLARIS_11_64_BIT: VclOperatingSystemType;
	static readonly ORACLE_SOLARIS_10_64_BIT: VclOperatingSystemType;
	static readonly ORACLE_SOLARIS_10_32_BIT: VclOperatingSystemType;
	static readonly SUN_MICROSYSTEMS_SOLARIS_9: VclOperatingSystemType;
	static readonly SUN_MICROSYSTEMS_SOLARIS_8: VclOperatingSystemType;
	static readonly SCO_OPENSERVER_6: VclOperatingSystemType;
	static readonly SCO_OPENSERVER_5: VclOperatingSystemType;
	static readonly SCO_UNIXWARE_7: VclOperatingSystemType;
	static readonly SERENITY_SYSTEMS_ECOMSTATION_2: VclOperatingSystemType;
	static readonly SERENITY_SYSTEMS_ECOMSTATION_1: VclOperatingSystemType;
	static readonly OTHER_64_BIT: VclOperatingSystemType;
	static readonly OTHER_32_BIT: VclOperatingSystemType;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclOperatingSystemType;
}

/**
 * TBS
 */
declare class VclNatPolicyType {
	static readonly values: string[];
	static readonly value: string;
	static readonly ALLOWTRAFFIC: VclNatPolicyType;
	static readonly ALLOWTRAFFICIN: VclNatPolicyType;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclNatPolicyType;
}

/**
 * TBS
 */
declare class VclBusSubType {
	static readonly values: string[];
	static readonly value: string;
	static readonly BUS_LOGIC: VclBusSubType;
	static readonly LSI_LOGIC: VclBusSubType;
	static readonly LSI_LOGIC_SAS: VclBusSubType;
	static readonly PARA_VIRTUAL: VclBusSubType;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclBusSubType;
}

/**
 * TBS
 */
declare class VclFenceModeValuesType {
	static readonly values: string[];
	static readonly value: string;
	static readonly BRIDGED: VclFenceModeValuesType;
	static readonly ISOLATED: VclFenceModeValuesType;
	static readonly NATROUTED: VclFenceModeValuesType;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclFenceModeValuesType;
}

/**
 * TBS
 */
declare class VclEventType {
	static readonly values: string[];
	static readonly value: string;
	static readonly SESSION_LOGIN: VclEventType;
	static readonly USER_IMPORT: VclEventType;
	static readonly USER_REMOVE: VclEventType;
	static readonly USER_MODIFY: VclEventType;
	static readonly USER_ENABLE: VclEventType;
	static readonly USER_DISABLE: VclEventType;
	static readonly USER_LOCKOUT: VclEventType;
	static readonly USER_UNLOCK: VclEventType;
	static readonly USER_LOCK_EXPIRED: VclEventType;
	static readonly GROUP_IMPORT: VclEventType;
	static readonly GROUP_REMOVE: VclEventType;
	static readonly GROUP_PRIVILEGE_MODIFY: VclEventType;
	static readonly ROLE_CREATE: VclEventType;
	static readonly ROLE_MODIFY: VclEventType;
	static readonly ROLE_DELETE: VclEventType;
	static readonly ORG_CREATE: VclEventType;
	static readonly ORG_MODIFY: VclEventType;
	static readonly ORG_DELETE: VclEventType;
	static readonly USER_CREATE: VclEventType;
	static readonly USER_DELETE: VclEventType;
	static readonly NETWORK_CREATE: VclEventType;
	static readonly NETWORK_MODIFY: VclEventType;
	static readonly NETWORK_DELETE: VclEventType;
	static readonly NETWORK_DEPLOY: VclEventType;
	static readonly NETWORK_UNDEPLOY: VclEventType;
	static readonly VDC_CREATE_REQUEST: VclEventType;
	static readonly VDC_CREATE: VclEventType;
	static readonly VDC_MODIFY: VclEventType;
	static readonly VDC_DELETE_REQUEST: VclEventType;
	static readonly VDC_DELETE: VclEventType;
	static readonly VDC_FAST_PROVISIONING_MODIFY: VclEventType;
	static readonly VDC_THIN_PROVISIONING_MODIFY: VclEventType;
	static readonly PROVIDERVDC_CREATE_REQUEST: VclEventType;
	static readonly PROVIDERVDC_CREATE: VclEventType;
	static readonly PROVIDERVDC_MODIFY: VclEventType;
	static readonly PROVIDERVDC_DELETE_REQUEST: VclEventType;
	static readonly PROVIDERVDC_DELETE: VclEventType;
	static readonly VAPPTEMPLATE_CREATE: VclEventType;
	static readonly VAPPTEMPLATE_IMPORT: VclEventType;
	static readonly VAPPTEMPLATE_MODIFY: VclEventType;
	static readonly VAPPTEMPLATE_DELETE: VclEventType;
	static readonly VAPPTEMPLATE_CREATE_REQUEST: VclEventType;
	static readonly VAPPTEMPLATE_IMPORT_REQUEST: VclEventType;
	static readonly VAPPTEMPLATE_MODIFY_REQUEST: VclEventType;
	static readonly VAPPTEMPLATE_DELETE_REQUEST: VclEventType;
	static readonly MEDIA_CREATE: VclEventType;
	static readonly MEDIA_IMPORT: VclEventType;
	static readonly MEDIA_MODIFY: VclEventType;
	static readonly MEDIA_DELETE: VclEventType;
	static readonly MEDIA_CREATE_REQUEST: VclEventType;
	static readonly MEDIA_IMPORT_REQUEST: VclEventType;
	static readonly MEDIA_MODIFY_REQUEST: VclEventType;
	static readonly MEDIA_DELETE_REQUEST: VclEventType;
	static readonly MEDIA_UPLOAD_TIMEOUT: VclEventType;
	static readonly MEDIA_QUARANTINE_REJECT: VclEventType;
	static readonly VAPP_CREATE: VclEventType;
	static readonly VAPP_IMPORT: VclEventType;
	static readonly VAPP_MODIFY: VclEventType;
	static readonly VAPP_DELETE: VclEventType;
	static readonly VAPP_DEPLOY: VclEventType;
	static readonly VAPP_UNDEPLOY: VclEventType;
	static readonly VAPP_RUNTIME_LEASE_EXPIRY: VclEventType;
	static readonly VAPP_LEASE_EXPIRATION_CHANGED: VclEventType;
	static readonly VAPP_CREATE_REQUEST: VclEventType;
	static readonly VAPP_IMPORT_REQUEST: VclEventType;
	static readonly VAPP_MODIFY_REQUEST: VclEventType;
	static readonly VAPP_DELETE_REQUEST: VclEventType;
	static readonly VAPP_DEPLOY_REQUEST: VclEventType;
	static readonly VAPP_UNDEPLOY_REQUEST: VclEventType;
	static readonly VM_CREATE_REQUEST: VclEventType;
	static readonly VAPP_QUARANTINE_REJECT: VclEventType;
	static readonly VAPP_UPLOAD_TIMEOUT: VclEventType;
	static readonly VM_CREATE: VclEventType;
	static readonly VM_MODIFY_REQUEST: VclEventType;
	static readonly VM_MODIFY: VclEventType;
	static readonly VM_DELETE: VclEventType;
	static readonly VM_CHANGE_STATE: VclEventType;
	static readonly VM_DEPLOY_REQUEST: VclEventType;
	static readonly VM_DEPLOY: VclEventType;
	static readonly VM_UNDEPLOY_REQUEST: VclEventType;
	static readonly VM_UNDEPLOY: VclEventType;
	static readonly VM_CONSOLIDATE_REQUEST: VclEventType;
	static readonly VM_CONSOLIDATE: VclEventType;
	static readonly VM_RELOCATE_REQUEST: VclEventType;
	static readonly VM_RELOCATE: VclEventType;
	static readonly VC_CREATE: VclEventType;
	static readonly VC_MODIFY: VclEventType;
	static readonly VC_DELETE: VclEventType;
	static readonly VC_REFRESH: VclEventType;
	static readonly CATALOG_CREATE: VclEventType;
	static readonly CATALOG_DELETE: VclEventType;
	static readonly CATALOG_MODIFY: VclEventType;
	static readonly CATALOG_PUBLISH: VclEventType;
	static readonly CATALOGITEM_CREATE: VclEventType;
	static readonly CATALOGITEM_DELETE: VclEventType;
	static readonly CATALOGITEM_MODIFY: VclEventType;
	static readonly TASK_CREATE: VclEventType;
	static readonly TASK_START: VclEventType;
	static readonly TASK_ABORT: VclEventType;
	static readonly TASK_COMPLETE: VclEventType;
	static readonly TASK_FAIL: VclEventType;
	static readonly BLOCKINGTASK_CREATE: VclEventType;
	static readonly BLOCKINGTASK_RESUME: VclEventType;
	static readonly BLOCKINGTASK_ABORT: VclEventType;
	static readonly BLOCKINGTASK_FAIL: VclEventType;
	static readonly DATASTORE_MODIFY: VclEventType;
	static readonly DATASTORE_DELETE: VclEventType;
	static readonly DISK_CREATE_REQUEST: VclEventType;
	static readonly DISK_CREATE: VclEventType;
	static readonly DISK_DELETE_REQUEST: VclEventType;
	static readonly DISK_DELETE: VclEventType;
	static readonly DISK_MODIFY: VclEventType;
	static readonly DISK_ATTACH: VclEventType;
	static readonly DISK_DETACH: VclEventType;
	static readonly PROVIDERVDCSTORAGEPROFILE_ADD: VclEventType;
	static readonly PROVIDERVDCSTORAGEPROFILE_REMOVE: VclEventType;
	static readonly PROVIDERVDCSTORAGEPROFILE_MODIFY: VclEventType;
	static readonly VDCSTORAGEPROFILE_ADD: VclEventType;
	static readonly VDCSTORAGEPROFILE_REMOVE: VclEventType;
	static readonly VDCSTORAGEPROFILE_MODIFY: VclEventType;
	static readonly GATEWAY_CREATE: VclEventType;
	static readonly GATEWAY_MODIFY: VclEventType;
	static readonly GATEWAY_DELETE: VclEventType;
	static readonly PROVIDERVDC_MERGE_REQUEST: VclEventType;
	static readonly PROVIDERVDC_MERGE: VclEventType;
	static readonly PROVIDERVDC_MERGE_WITH: VclEventType;
	static readonly VM_MIGRATE_REQUEST: VclEventType;
	static readonly VM_IP_ADDRESS_CHANGED: VclEventType;
	static readonly VM_MIGRATE: VclEventType;
	static readonly NETWORK_UPGRADE: VclEventType;
	static readonly GATEWAY_UPGRADE: VclEventType;
	static readonly LICENSING_REQUESTUSAGE: VclEventType;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclEventType;
}

/**
 * TBS
 */
declare class VclQueryVAppField {
	static readonly values: string[];
	static readonly value: string;
	static readonly ID: VclQueryVAppField;
	static readonly HREF: VclQueryVAppField;
	static readonly CREATIONDATE: VclQueryVAppField;
	static readonly ISBUSY: VclQueryVAppField;
	static readonly ISDEPLOYED: VclQueryVAppField;
	static readonly ISENABLED: VclQueryVAppField;
	static readonly ISEXPIRED: VclQueryVAppField;
	static readonly ISINMAINTENANCEMODE: VclQueryVAppField;
	static readonly ISPUBLIC: VclQueryVAppField;
	static readonly NAME: VclQueryVAppField;
	static readonly OWNERNAME: VclQueryVAppField;
	static readonly STATUS: VclQueryVAppField;
	static readonly VDC: VclQueryVAppField;
	static readonly VDCNAME: VclQueryVAppField;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclQueryVAppField;
}

/**
 * TBS
 */
declare class VclQueryAdminVdcField {
	static readonly values: string[];
	static readonly value: string;
	static readonly ID: VclQueryAdminVdcField;
	static readonly HREF: VclQueryAdminVdcField;
	static readonly CPUALLOCATIONMHZ: VclQueryAdminVdcField;
	static readonly CPULIMITMHZ: VclQueryAdminVdcField;
	static readonly CPUUSEDMHZ: VclQueryAdminVdcField;
	static readonly ISBUSY: VclQueryAdminVdcField;
	static readonly ISENABLED: VclQueryAdminVdcField;
	static readonly ISSYSTEMVDC: VclQueryAdminVdcField;
	static readonly MEMORYALLOCATIONMB: VclQueryAdminVdcField;
	static readonly MEMORYLIMITMB: VclQueryAdminVdcField;
	static readonly MEMORYUSEDMB: VclQueryAdminVdcField;
	static readonly NAME: VclQueryAdminVdcField;
	static readonly NETWORKPOOL: VclQueryAdminVdcField;
	static readonly NUMBEROFDISKS: VclQueryAdminVdcField;
	static readonly NUMBEROFMEDIA: VclQueryAdminVdcField;
	static readonly NUMBEROFSTORAGEPROFILES: VclQueryAdminVdcField;
	static readonly NUMBEROFVAPPTEMPLATES: VclQueryAdminVdcField;
	static readonly NUMBEROFVAPPS: VclQueryAdminVdcField;
	static readonly ORG: VclQueryAdminVdcField;
	static readonly ORGNAME: VclQueryAdminVdcField;
	static readonly PROVIDERVDC: VclQueryAdminVdcField;
	static readonly PROVIDERVDCNAME: VclQueryAdminVdcField;
	static readonly STATUS: VclQueryAdminVdcField;
	static readonly STORAGEALLOCATIONMB: VclQueryAdminVdcField;
	static readonly STORAGELIMITMB: VclQueryAdminVdcField;
	static readonly STORAGEUSEDMB: VclQueryAdminVdcField;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclQueryAdminVdcField;
}

/**
 * TBS
 */
declare class VclQueryCatalogItemField {
	static readonly values: string[];
	static readonly value: string;
	static readonly ID: VclQueryCatalogItemField;
	static readonly HREF: VclQueryCatalogItemField;
	static readonly CATALOG: VclQueryCatalogItemField;
	static readonly CATALOGNAME: VclQueryCatalogItemField;
	static readonly CREATIONDATE: VclQueryCatalogItemField;
	static readonly ENTITY: VclQueryCatalogItemField;
	static readonly ENTITYNAME: VclQueryCatalogItemField;
	static readonly ENTITYTYPE: VclQueryCatalogItemField;
	static readonly ISEXPIRED: VclQueryCatalogItemField;
	static readonly ISPUBLISHED: VclQueryCatalogItemField;
	static readonly ISVDCENABLED: VclQueryCatalogItemField;
	static readonly NAME: VclQueryCatalogItemField;
	static readonly OWNER: VclQueryCatalogItemField;
	static readonly OWNERNAME: VclQueryCatalogItemField;
	static readonly STATUS: VclQueryCatalogItemField;
	static readonly VDC: VclQueryCatalogItemField;
	static readonly VDCNAME: VclQueryCatalogItemField;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclQueryCatalogItemField;
}

/**
 * TBS
 */
declare class VclQueryReferenceType {
	static readonly values: string[];
	static readonly value: string;
	static readonly ORGANIZATION: VclQueryReferenceType;
	static readonly ORGVDC: VclQueryReferenceType;
	static readonly MEDIA: VclQueryReferenceType;
	static readonly VAPPTEMPLATE: VclQueryReferenceType;
	static readonly VAPP: VclQueryReferenceType;
	static readonly VM: VclQueryReferenceType;
	static readonly ORGNETWORK: VclQueryReferenceType;
	static readonly ADMINORGNETWORK: VclQueryReferenceType;
	static readonly VAPPNETWORK: VclQueryReferenceType;
	static readonly CATALOG: VclQueryReferenceType;
	static readonly ADMINORGVDC: VclQueryReferenceType;
	static readonly PROVIDERVDC: VclQueryReferenceType;
	static readonly EXTERNALNETWORK: VclQueryReferenceType;
	static readonly GROUP: VclQueryReferenceType;
	static readonly USER: VclQueryReferenceType;
	static readonly STRANDEDUSER: VclQueryReferenceType;
	static readonly ROLE: VclQueryReferenceType;
	static readonly DATASTORE: VclQueryReferenceType;
	static readonly NETWORKPOOL: VclQueryReferenceType;
	static readonly VIRTUALCENTER: VclQueryReferenceType;
	static readonly HOST: VclQueryReferenceType;
	static readonly ADMINVAPP: VclQueryReferenceType;
	static readonly RIGHT: VclQueryReferenceType;
	static readonly ADMINVM: VclQueryReferenceType;
	static readonly VAPPORGNETWORKRELATION: VclQueryReferenceType;
	static readonly ADMINUSER: VclQueryReferenceType;
	static readonly ADMINGROUP: VclQueryReferenceType;
	static readonly ADMINVAPPNETWORK: VclQueryReferenceType;
	static readonly ADMINCATALOG: VclQueryReferenceType;
	static readonly ADMINCATALOGITEM: VclQueryReferenceType;
	static readonly CATALOGITEM: VclQueryReferenceType;
	static readonly ADMINMEDIA: VclQueryReferenceType;
	static readonly ADMINVAPPTEMPLATE: VclQueryReferenceType;
	static readonly ADMINSHADOWVM: VclQueryReferenceType;
	static readonly TASK: VclQueryReferenceType;
	static readonly ADMINTASK: VclQueryReferenceType;
	static readonly BLOCKINGTASK: VclQueryReferenceType;
	static readonly DISK: VclQueryReferenceType;
	static readonly ADMINDISK: VclQueryReferenceType;
	static readonly STRANDEDITEM: VclQueryReferenceType;
	static readonly ADMINSERVICE: VclQueryReferenceType;
	static readonly SERVICE: VclQueryReferenceType;
	static readonly SERVICELINK: VclQueryReferenceType;
	static readonly ORGVDCSTORAGEPROFILE: VclQueryReferenceType;
	static readonly ADMINORGVDCSTORAGEPROFILE: VclQueryReferenceType;
	static readonly PROVIDERVDCSTORAGEPROFILE: VclQueryReferenceType;
	static readonly APIFILTER: VclQueryReferenceType;
	static readonly ADMINAPIDEFINITION: VclQueryReferenceType;
	static readonly APIDEFINITION: VclQueryReferenceType;
	static readonly ADMINFILEDESCRIPTOR: VclQueryReferenceType;
	static readonly RESOURCECLASSACTION: VclQueryReferenceType;
	static readonly ACLRULE: VclQueryReferenceType;
	static readonly RESOURCECLASS: VclQueryReferenceType;
	static readonly SERVICERESOURCE: VclQueryReferenceType;
	static readonly EDGEGATEWAY: VclQueryReferenceType;
	static readonly ORGVDCNETWORK: VclQueryReferenceType;
	static readonly VAPPORGVDCNETWORKRELATION: VclQueryReferenceType;
	static readonly RESOURCEPOOLVMLIST: VclQueryReferenceType;
	static readonly ADMINEVENT: VclQueryReferenceType;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclQueryReferenceType;
}

/**
 * TBS
 */
declare class VclQueryAdminCatalogItemField {
	static readonly values: string[];
	static readonly value: string;
	static readonly ID: VclQueryAdminCatalogItemField;
	static readonly HREF: VclQueryAdminCatalogItemField;
	static readonly CATALOG: VclQueryAdminCatalogItemField;
	static readonly CATALOGNAME: VclQueryAdminCatalogItemField;
	static readonly CREATIONDATE: VclQueryAdminCatalogItemField;
	static readonly ENTITY: VclQueryAdminCatalogItemField;
	static readonly ENTITYNAME: VclQueryAdminCatalogItemField;
	static readonly ENTITYTYPE: VclQueryAdminCatalogItemField;
	static readonly ISEXPIRED: VclQueryAdminCatalogItemField;
	static readonly ISPUBLISHED: VclQueryAdminCatalogItemField;
	static readonly ISVDCENABLED: VclQueryAdminCatalogItemField;
	static readonly NAME: VclQueryAdminCatalogItemField;
	static readonly ORG: VclQueryAdminCatalogItemField;
	static readonly OWNER: VclQueryAdminCatalogItemField;
	static readonly OWNERNAME: VclQueryAdminCatalogItemField;
	static readonly STATUS: VclQueryAdminCatalogItemField;
	static readonly VDC: VclQueryAdminCatalogItemField;
	static readonly VDCNAME: VclQueryAdminCatalogItemField;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclQueryAdminCatalogItemField;
}

/**
 * TBS
 */
declare class VclQueryVMWProviderVdcField {
	static readonly values: string[];
	static readonly value: string;
	static readonly ID: VclQueryVMWProviderVdcField;
	static readonly HREF: VclQueryVMWProviderVdcField;
	static readonly CPUALLOCATIONMHZ: VclQueryVMWProviderVdcField;
	static readonly CPULIMITMHZ: VclQueryVMWProviderVdcField;
	static readonly CPUUSEDMHZ: VclQueryVMWProviderVdcField;
	static readonly ISBUSY: VclQueryVMWProviderVdcField;
	static readonly ISDELETED: VclQueryVMWProviderVdcField;
	static readonly ISENABLED: VclQueryVMWProviderVdcField;
	static readonly MEMORYALLOCATIONMB: VclQueryVMWProviderVdcField;
	static readonly MEMORYLIMITMB: VclQueryVMWProviderVdcField;
	static readonly MEMORYUSEDMB: VclQueryVMWProviderVdcField;
	static readonly NAME: VclQueryVMWProviderVdcField;
	static readonly NUMBEROFDATASTORES: VclQueryVMWProviderVdcField;
	static readonly NUMBEROFSTORAGEPROFILES: VclQueryVMWProviderVdcField;
	static readonly NUMBEROFVDCS: VclQueryVMWProviderVdcField;
	static readonly STATUS: VclQueryVMWProviderVdcField;
	static readonly STORAGEALLOCATIONMB: VclQueryVMWProviderVdcField;
	static readonly STORAGELIMITMB: VclQueryVMWProviderVdcField;
	static readonly STORAGEUSEDMB: VclQueryVMWProviderVdcField;
	static readonly VCPURATINGMHZ: VclQueryVMWProviderVdcField;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclQueryVMWProviderVdcField;
}

/**
 * TBS
 */
declare class VclQueryVAppOrgNetworkRelationField {
	static readonly values: string[];
	static readonly value: string;
	static readonly ID: VclQueryVAppOrgNetworkRelationField;
	static readonly HREF: VclQueryVAppOrgNetworkRelationField;
	static readonly CONFIGURATIONTYPE: VclQueryVAppOrgNetworkRelationField;
	static readonly NAME: VclQueryVAppOrgNetworkRelationField;
	static readonly ORG: VclQueryVAppOrgNetworkRelationField;
	static readonly ORGNETWORK: VclQueryVAppOrgNetworkRelationField;
	static readonly ORGNETWORKNAME: VclQueryVAppOrgNetworkRelationField;
	static readonly OWNERNAME: VclQueryVAppOrgNetworkRelationField;
	static readonly STATUS: VclQueryVAppOrgNetworkRelationField;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclQueryVAppOrgNetworkRelationField;
}

/**
 * TBS
 */
declare class VclQueryGroupField {
	static readonly values: string[];
	static readonly value: string;
	static readonly ID: VclQueryGroupField;
	static readonly HREF: VclQueryGroupField;
	static readonly IDENTITYPROVIDERTYPE: VclQueryGroupField;
	static readonly ISREADONLY: VclQueryGroupField;
	static readonly NAME: VclQueryGroupField;
	static readonly ROLENAME: VclQueryGroupField;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclQueryGroupField;
}

/**
 * TBS
 */
declare class VclQueryAdminFileDescriptorField {
	static readonly values: string[];
	static readonly value: string;
	static readonly ID: VclQueryAdminFileDescriptorField;
	static readonly HREF: VclQueryAdminFileDescriptorField;
	static readonly APIDEFINITION: VclQueryAdminFileDescriptorField;
	static readonly APINAME: VclQueryAdminFileDescriptorField;
	static readonly APINAMESPACE: VclQueryAdminFileDescriptorField;
	static readonly APIVENDOR: VclQueryAdminFileDescriptorField;
	static readonly FILEMIMETYPE: VclQueryAdminFileDescriptorField;
	static readonly FILEURL: VclQueryAdminFileDescriptorField;
	static readonly NAME: VclQueryAdminFileDescriptorField;
	static readonly SERVICE: VclQueryAdminFileDescriptorField;
	static readonly SERVICENAME: VclQueryAdminFileDescriptorField;
	static readonly SERVICENAMESPACE: VclQueryAdminFileDescriptorField;
	static readonly SERVICEVENDOR: VclQueryAdminFileDescriptorField;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclQueryAdminFileDescriptorField;
}

/**
 * TBS
 */
declare class VclQueryAdminGroupField {
	static readonly values: string[];
	static readonly value: string;
	static readonly ID: VclQueryAdminGroupField;
	static readonly HREF: VclQueryAdminGroupField;
	static readonly IDENTITYPROVIDERTYPE: VclQueryAdminGroupField;
	static readonly ISREADONLY: VclQueryAdminGroupField;
	static readonly NAME: VclQueryAdminGroupField;
	static readonly ORG: VclQueryAdminGroupField;
	static readonly ROLENAME: VclQueryAdminGroupField;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclQueryAdminGroupField;
}

/**
 * TBS
 */
declare class VclQueryEdgeGatewayField {
	static readonly values: string[];
	static readonly value: string;
	static readonly ID: VclQueryEdgeGatewayField;
	static readonly HREF: VclQueryEdgeGatewayField;
	static readonly GATEWAYSTATUS: VclQueryEdgeGatewayField;
	static readonly HASTATUS: VclQueryEdgeGatewayField;
	static readonly ISBUSY: VclQueryEdgeGatewayField;
	static readonly NAME: VclQueryEdgeGatewayField;
	static readonly NUMBEROFEXTNETWORKS: VclQueryEdgeGatewayField;
	static readonly NUMBEROFORGNETWORKS: VclQueryEdgeGatewayField;
	static readonly VDC: VclQueryEdgeGatewayField;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclQueryEdgeGatewayField;
}

/**
 * TBS
 */
declare class VclQueryServiceResourceField {
	static readonly values: string[];
	static readonly value: string;
	static readonly ID: VclQueryServiceResourceField;
	static readonly HREF: VclQueryServiceResourceField;
	static readonly EXTERNALOBJECTID: VclQueryServiceResourceField;
	static readonly NAME: VclQueryServiceResourceField;
	static readonly ORG: VclQueryServiceResourceField;
	static readonly RESOURCECLASS: VclQueryServiceResourceField;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclQueryServiceResourceField;
}

/**
 * TBS
 */
declare class VclQueryExternalLocalizationField {
	static readonly values: string[];
	static readonly value: string;
	static readonly ID: VclQueryExternalLocalizationField;
	static readonly HREF: VclQueryExternalLocalizationField;
	static readonly KEY: VclQueryExternalLocalizationField;
	static readonly LOCALE: VclQueryExternalLocalizationField;
	static readonly SERVICENAMESPACE: VclQueryExternalLocalizationField;
	static readonly VALUE: VclQueryExternalLocalizationField;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclQueryExternalLocalizationField;
}

/**
 * TBS
 */
declare class VclQueryProviderVdcStorageProfileField {
	static readonly values: string[];
	static readonly value: string;
	static readonly ID: VclQueryProviderVdcStorageProfileField;
	static readonly HREF: VclQueryProviderVdcStorageProfileField;
	static readonly ISENABLED: VclQueryProviderVdcStorageProfileField;
	static readonly NAME: VclQueryProviderVdcStorageProfileField;
	static readonly NUMBEROFCONDITIONS: VclQueryProviderVdcStorageProfileField;
	static readonly PROVIDERVDC: VclQueryProviderVdcStorageProfileField;
	static readonly STORAGEPROFILEMOREF: VclQueryProviderVdcStorageProfileField;
	static readonly STORAGEPROVISIONEDMB: VclQueryProviderVdcStorageProfileField;
	static readonly STORAGEREQUESTEDMB: VclQueryProviderVdcStorageProfileField;
	static readonly STORAGETOTALMB: VclQueryProviderVdcStorageProfileField;
	static readonly STORAGEUSEDMB: VclQueryProviderVdcStorageProfileField;
	static readonly VC: VclQueryProviderVdcStorageProfileField;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclQueryProviderVdcStorageProfileField;
}

/**
 * TBS
 */
declare class VclQueryAdminVMField {
	static readonly values: string[];
	static readonly value: string;
	static readonly ID: VclQueryAdminVMField;
	static readonly HREF: VclQueryAdminVMField;
	static readonly CATALOGNAME: VclQueryAdminVMField;
	static readonly CONTAINER: VclQueryAdminVMField;
	static readonly CONTAINERNAME: VclQueryAdminVMField;
	static readonly DATASTORENAME: VclQueryAdminVMField;
	static readonly GUESTOS: VclQueryAdminVMField;
	static readonly HARDWAREVERSION: VclQueryAdminVMField;
	static readonly HOSTNAME: VclQueryAdminVMField;
	static readonly ISDELETED: VclQueryAdminVMField;
	static readonly ISDEPLOYED: VclQueryAdminVMField;
	static readonly ISPUBLISHED: VclQueryAdminVMField;
	static readonly ISVAPPTEMPLATE: VclQueryAdminVMField;
	static readonly ISVDCENABLED: VclQueryAdminVMField;
	static readonly MEMORYMB: VclQueryAdminVMField;
	static readonly MOREF: VclQueryAdminVMField;
	static readonly NAME: VclQueryAdminVMField;
	static readonly NETWORKNAME: VclQueryAdminVMField;
	static readonly NUMBEROFCPUS: VclQueryAdminVMField;
	static readonly ORG: VclQueryAdminVMField;
	static readonly STATUS: VclQueryAdminVMField;
	static readonly STORAGEPROFILENAME: VclQueryAdminVMField;
	static readonly VC: VclQueryAdminVMField;
	static readonly VDC: VclQueryAdminVMField;
	static readonly VMTOOLSVERSION: VclQueryAdminVMField;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclQueryAdminVMField;
}

/**
 * TBS
 */
declare class VclQueryVAppTemplateField {
	static readonly values: string[];
	static readonly value: string;
	static readonly ID: VclQueryVAppTemplateField;
	static readonly HREF: VclQueryVAppTemplateField;
	static readonly CATALOGNAME: VclQueryVAppTemplateField;
	static readonly CREATIONDATE: VclQueryVAppTemplateField;
	static readonly ISBUSY: VclQueryVAppTemplateField;
	static readonly ISDEPLOYED: VclQueryVAppTemplateField;
	static readonly ISENABLED: VclQueryVAppTemplateField;
	static readonly ISEXPIRED: VclQueryVAppTemplateField;
	static readonly ISGOLDMASTER: VclQueryVAppTemplateField;
	static readonly ISPUBLISHED: VclQueryVAppTemplateField;
	static readonly NAME: VclQueryVAppTemplateField;
	static readonly ORG: VclQueryVAppTemplateField;
	static readonly OWNERNAME: VclQueryVAppTemplateField;
	static readonly STATUS: VclQueryVAppTemplateField;
	static readonly STORAGEPROFILENAME: VclQueryVAppTemplateField;
	static readonly VDC: VclQueryVAppTemplateField;
	static readonly VDCNAME: VclQueryVAppTemplateField;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclQueryVAppTemplateField;
}

/**
 * TBS
 */
declare class VclQueryConditionField {
	static readonly values: string[];
	static readonly value: string;
	static readonly ID: VclQueryConditionField;
	static readonly HREF: VclQueryConditionField;
	static readonly DETAILS: VclQueryConditionField;
	static readonly OBJECT: VclQueryConditionField;
	static readonly OBJECTTYPE: VclQueryConditionField;
	static readonly OCCURENCEDATE: VclQueryConditionField;
	static readonly SEVERITY: VclQueryConditionField;
	static readonly SUMMARY: VclQueryConditionField;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclQueryConditionField;
}

/**
 * TBS
 */
declare class VclQueryRecordType {
	static readonly values: string[];
	static readonly value: string;
	static readonly ORGANIZATION: VclQueryRecordType;
	static readonly ORGVDC: VclQueryRecordType;
	static readonly MEDIA: VclQueryRecordType;
	static readonly VAPPTEMPLATE: VclQueryRecordType;
	static readonly VAPP: VclQueryRecordType;
	static readonly VM: VclQueryRecordType;
	static readonly ORGNETWORK: VclQueryRecordType;
	static readonly ADMINORGNETWORK: VclQueryRecordType;
	static readonly VAPPNETWORK: VclQueryRecordType;
	static readonly CATALOG: VclQueryRecordType;
	static readonly ADMINORGVDC: VclQueryRecordType;
	static readonly PROVIDERVDC: VclQueryRecordType;
	static readonly EXTERNALNETWORK: VclQueryRecordType;
	static readonly GROUP: VclQueryRecordType;
	static readonly USER: VclQueryRecordType;
	static readonly STRANDEDUSER: VclQueryRecordType;
	static readonly ROLE: VclQueryRecordType;
	static readonly ALLOCATEDEXTERNALADDRESS: VclQueryRecordType;
	static readonly EVENT: VclQueryRecordType;
	static readonly RESOURCEPOOL: VclQueryRecordType;
	static readonly DATASTORE: VclQueryRecordType;
	static readonly NETWORKPOOL: VclQueryRecordType;
	static readonly PORTGROUP: VclQueryRecordType;
	static readonly DVSWITCH: VclQueryRecordType;
	static readonly CELL: VclQueryRecordType;
	static readonly VIRTUALCENTER: VclQueryRecordType;
	static readonly HOST: VclQueryRecordType;
	static readonly ADMINVAPP: VclQueryRecordType;
	static readonly RIGHT: VclQueryRecordType;
	static readonly ADMINVM: VclQueryRecordType;
	static readonly ADMINALLOCATEDEXTERNALADDRESS: VclQueryRecordType;
	static readonly VAPPORGNETWORKRELATION: VclQueryRecordType;
	static readonly PROVIDERVDCRESOURCEPOOLRELATION: VclQueryRecordType;
	static readonly ORGVDCRESOURCEPOOLRELATION: VclQueryRecordType;
	static readonly DATSTOREPROVIDERVDCRELATION: VclQueryRecordType;
	static readonly DATASTOREPROVIDERVDCRELATION: VclQueryRecordType;
	static readonly ADMINUSER: VclQueryRecordType;
	static readonly ADMINGROUP: VclQueryRecordType;
	static readonly ADMINVAPPNETWORK: VclQueryRecordType;
	static readonly ADMINCATALOG: VclQueryRecordType;
	static readonly ADMINCATALOGITEM: VclQueryRecordType;
	static readonly CATALOGITEM: VclQueryRecordType;
	static readonly ADMINMEDIA: VclQueryRecordType;
	static readonly ADMINVAPPTEMPLATE: VclQueryRecordType;
	static readonly ADMINSHADOWVM: VclQueryRecordType;
	static readonly TASK: VclQueryRecordType;
	static readonly ADMINTASK: VclQueryRecordType;
	static readonly BLOCKINGTASK: VclQueryRecordType;
	static readonly DISK: VclQueryRecordType;
	static readonly VMDISKRELATION: VclQueryRecordType;
	static readonly ADMINDISK: VclQueryRecordType;
	static readonly ADMINVMDISKRELATION: VclQueryRecordType;
	static readonly CONDITION: VclQueryRecordType;
	static readonly STRANDEDITEM: VclQueryRecordType;
	static readonly ADMINSERVICE: VclQueryRecordType;
	static readonly SERVICE: VclQueryRecordType;
	static readonly SERVICELINK: VclQueryRecordType;
	static readonly ORGVDCSTORAGEPROFILE: VclQueryRecordType;
	static readonly ADMINORGVDCSTORAGEPROFILE: VclQueryRecordType;
	static readonly PROVIDERVDCSTORAGEPROFILE: VclQueryRecordType;
	static readonly APIFILTER: VclQueryRecordType;
	static readonly ADMINAPIDEFINITION: VclQueryRecordType;
	static readonly APIDEFINITION: VclQueryRecordType;
	static readonly ADMINFILEDESCRIPTOR: VclQueryRecordType;
	static readonly FILEDESCRIPTOR: VclQueryRecordType;
	static readonly RESOURCECLASSACTION: VclQueryRecordType;
	static readonly ACLRULE: VclQueryRecordType;
	static readonly RESOURCECLASS: VclQueryRecordType;
	static readonly SERVICERESOURCE: VclQueryRecordType;
	static readonly EDGEGATEWAY: VclQueryRecordType;
	static readonly ORGVDCNETWORK: VclQueryRecordType;
	static readonly VAPPORGVDCNETWORKRELATION: VclQueryRecordType;
	static readonly EXTERNALLOCALIZATION: VclQueryRecordType;
	static readonly RESOURCEPOOLVMLIST: VclQueryRecordType;
	static readonly ADMINEVENT: VclQueryRecordType;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclQueryRecordType;
}

/**
 * TBS
 */
declare class VclQueryRightField {
	static readonly values: string[];
	static readonly value: string;
	static readonly ID: VclQueryRightField;
	static readonly HREF: VclQueryRightField;
	static readonly CATEGORY: VclQueryRightField;
	static readonly NAME: VclQueryRightField;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclQueryRightField;
}

/**
 * TBS
 */
declare class VclQueryOrgVdcResourcePoolRelationField {
	static readonly values: string[];
	static readonly value: string;
	static readonly ID: VclQueryOrgVdcResourcePoolRelationField;
	static readonly HREF: VclQueryOrgVdcResourcePoolRelationField;
	static readonly RESOURCEPOOLMOREF: VclQueryOrgVdcResourcePoolRelationField;
	static readonly VC: VclQueryOrgVdcResourcePoolRelationField;
	static readonly VDC: VclQueryOrgVdcResourcePoolRelationField;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclQueryOrgVdcResourcePoolRelationField;
}

/**
 * TBS
 */
declare class VclQueryPortgroupField {
	static readonly values: string[];
	static readonly value: string;
	static readonly ID: VclQueryPortgroupField;
	static readonly HREF: VclQueryPortgroupField;
	static readonly ISVCENABLED: VclQueryPortgroupField;
	static readonly MOREF: VclQueryPortgroupField;
	static readonly NAME: VclQueryPortgroupField;
	static readonly NETWORK: VclQueryPortgroupField;
	static readonly NETWORKNAME: VclQueryPortgroupField;
	static readonly PORTGROUPTYPE: VclQueryPortgroupField;
	static readonly SCOPETYPE: VclQueryPortgroupField;
	static readonly VC: VclQueryPortgroupField;
	static readonly VCNAME: VclQueryPortgroupField;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclQueryPortgroupField;
}

/**
 * TBS
 */
declare class VclQueryAdminTaskField {
	static readonly values: string[];
	static readonly value: string;
	static readonly ID: VclQueryAdminTaskField;
	static readonly HREF: VclQueryAdminTaskField;
	static readonly CELLNAME: VclQueryAdminTaskField;
	static readonly ENDDATE: VclQueryAdminTaskField;
	static readonly HASOWNER: VclQueryAdminTaskField;
	static readonly NAME: VclQueryAdminTaskField;
	static readonly OBJECT: VclQueryAdminTaskField;
	static readonly OBJECTNAME: VclQueryAdminTaskField;
	static readonly OBJECTTYPE: VclQueryAdminTaskField;
	static readonly ORG: VclQueryAdminTaskField;
	static readonly ORGNAME: VclQueryAdminTaskField;
	static readonly OWNER: VclQueryAdminTaskField;
	static readonly OWNERNAME: VclQueryAdminTaskField;
	static readonly SERVICENAMESPACE: VclQueryAdminTaskField;
	static readonly STARTDATE: VclQueryAdminTaskField;
	static readonly STATUS: VclQueryAdminTaskField;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclQueryAdminTaskField;
}

/**
 * TBS
 */
declare class VclSortType {
	static readonly values: string[];
	static readonly value: string;
	static readonly SORT_ASC: VclSortType;
	static readonly SORT_DESC: VclSortType;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclSortType;
}

/**
 * TBS
 */
declare class VclQueryOrgVdcGatewayField {
	static readonly values: string[];
	static readonly value: string;
	static readonly ID: VclQueryOrgVdcGatewayField;
	static readonly HREF: VclQueryOrgVdcGatewayField;
	static readonly ISBUSY: VclQueryOrgVdcGatewayField;
	static readonly NAME: VclQueryOrgVdcGatewayField;
	static readonly NUMBEROFEXTNETWORKS: VclQueryOrgVdcGatewayField;
	static readonly NUMBEROFORGNETWORKS: VclQueryOrgVdcGatewayField;
	static readonly VDC: VclQueryOrgVdcGatewayField;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclQueryOrgVdcGatewayField;
}

/**
 * TBS
 */
declare class VclQueryAdminOrgNetworkField {
	static readonly values: string[];
	static readonly value: string;
	static readonly ID: VclQueryAdminOrgNetworkField;
	static readonly HREF: VclQueryAdminOrgNetworkField;
	static readonly DNS1: VclQueryAdminOrgNetworkField;
	static readonly DNS2: VclQueryAdminOrgNetworkField;
	static readonly GATEWAY: VclQueryAdminOrgNetworkField;
	static readonly IPSCOPEID: VclQueryAdminOrgNetworkField;
	static readonly ISBUSY: VclQueryAdminOrgNetworkField;
	static readonly ISIPSCOPEINHERITED: VclQueryAdminOrgNetworkField;
	static readonly NAME: VclQueryAdminOrgNetworkField;
	static readonly NETMASK: VclQueryAdminOrgNetworkField;
	static readonly NETWORKPOOL: VclQueryAdminOrgNetworkField;
	static readonly NETWORKPOOLNAME: VclQueryAdminOrgNetworkField;
	static readonly ORG: VclQueryAdminOrgNetworkField;
	static readonly ORGNAME: VclQueryAdminOrgNetworkField;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclQueryAdminOrgNetworkField;
}

/**
 * TBS
 */
declare class VclQueryAdminDiskField {
	static readonly values: string[];
	static readonly value: string;
	static readonly ID: VclQueryAdminDiskField;
	static readonly HREF: VclQueryAdminDiskField;
	static readonly BUSSUBTYPE: VclQueryAdminDiskField;
	static readonly BUSTYPE: VclQueryAdminDiskField;
	static readonly BUSTYPEDESC: VclQueryAdminDiskField;
	static readonly DATASTORE: VclQueryAdminDiskField;
	static readonly DATASTORENAME: VclQueryAdminDiskField;
	static readonly ISATTACHED: VclQueryAdminDiskField;
	static readonly NAME: VclQueryAdminDiskField;
	static readonly ORG: VclQueryAdminDiskField;
	static readonly OWNERNAME: VclQueryAdminDiskField;
	static readonly SIZEB: VclQueryAdminDiskField;
	static readonly STATUS: VclQueryAdminDiskField;
	static readonly STORAGEPROFILE: VclQueryAdminDiskField;
	static readonly STORAGEPROFILENAME: VclQueryAdminDiskField;
	static readonly TASK: VclQueryAdminDiskField;
	static readonly VC: VclQueryAdminDiskField;
	static readonly VDC: VclQueryAdminDiskField;
	static readonly VDCNAME: VclQueryAdminDiskField;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclQueryAdminDiskField;
}

/**
 * TBS
 */
declare class VclQueryOrgNetworkField {
	static readonly values: string[];
	static readonly value: string;
	static readonly ID: VclQueryOrgNetworkField;
	static readonly HREF: VclQueryOrgNetworkField;
	static readonly DNS1: VclQueryOrgNetworkField;
	static readonly DNS2: VclQueryOrgNetworkField;
	static readonly DNSSUFFIX: VclQueryOrgNetworkField;
	static readonly GATEWAY: VclQueryOrgNetworkField;
	static readonly IPSCOPEID: VclQueryOrgNetworkField;
	static readonly ISBUSY: VclQueryOrgNetworkField;
	static readonly ISIPSCOPEINHERITED: VclQueryOrgNetworkField;
	static readonly NAME: VclQueryOrgNetworkField;
	static readonly NETMASK: VclQueryOrgNetworkField;
	static readonly NETWORKPOOL: VclQueryOrgNetworkField;
	static readonly NETWORKPOOLNAME: VclQueryOrgNetworkField;
	static readonly ORG: VclQueryOrgNetworkField;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclQueryOrgNetworkField;
}

/**
 * TBS
 */
declare class VclQueryAdminVAppTemplateField {
	static readonly values: string[];
	static readonly value: string;
	static readonly ID: VclQueryAdminVAppTemplateField;
	static readonly HREF: VclQueryAdminVAppTemplateField;
	static readonly CATALOG: VclQueryAdminVAppTemplateField;
	static readonly CATALOGITEM: VclQueryAdminVAppTemplateField;
	static readonly CATALOGNAME: VclQueryAdminVAppTemplateField;
	static readonly CREATIONDATE: VclQueryAdminVAppTemplateField;
	static readonly ISBUSY: VclQueryAdminVAppTemplateField;
	static readonly ISDEPLOYED: VclQueryAdminVAppTemplateField;
	static readonly ISENABLED: VclQueryAdminVAppTemplateField;
	static readonly ISEXPIRED: VclQueryAdminVAppTemplateField;
	static readonly ISGOLDMASTER: VclQueryAdminVAppTemplateField;
	static readonly ISPUBLISHED: VclQueryAdminVAppTemplateField;
	static readonly ISVDCENABLED: VclQueryAdminVAppTemplateField;
	static readonly NAME: VclQueryAdminVAppTemplateField;
	static readonly ORG: VclQueryAdminVAppTemplateField;
	static readonly OWNER: VclQueryAdminVAppTemplateField;
	static readonly OWNERNAME: VclQueryAdminVAppTemplateField;
	static readonly STATUS: VclQueryAdminVAppTemplateField;
	static readonly STORAGEPROFILENAME: VclQueryAdminVAppTemplateField;
	static readonly VDC: VclQueryAdminVAppTemplateField;
	static readonly VDCNAME: VclQueryAdminVAppTemplateField;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclQueryAdminVAppTemplateField;
}

/**
 * TBS
 */
declare class VclQueryVAppNetworkField {
	static readonly values: string[];
	static readonly value: string;
	static readonly ID: VclQueryVAppNetworkField;
	static readonly HREF: VclQueryVAppNetworkField;
	static readonly DNS1: VclQueryVAppNetworkField;
	static readonly DNS2: VclQueryVAppNetworkField;
	static readonly DNSSUFFIX: VclQueryVAppNetworkField;
	static readonly GATEWAY: VclQueryVAppNetworkField;
	static readonly IPSCOPEID: VclQueryVAppNetworkField;
	static readonly ISBUSY: VclQueryVAppNetworkField;
	static readonly ISIPSCOPEINHERITED: VclQueryVAppNetworkField;
	static readonly NAME: VclQueryVAppNetworkField;
	static readonly NETMASK: VclQueryVAppNetworkField;
	static readonly VAPP: VclQueryVAppNetworkField;
	static readonly VAPPNAME: VclQueryVAppNetworkField;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclQueryVAppNetworkField;
}

/**
 * TBS
 */
declare class VclQueryServiceLinkField {
	static readonly values: string[];
	static readonly value: string;
	static readonly ID: VclQueryServiceLinkField;
	static readonly HREF: VclQueryServiceLinkField;
	static readonly LINKHREF: VclQueryServiceLinkField;
	static readonly MIMETYPE: VclQueryServiceLinkField;
	static readonly REL: VclQueryServiceLinkField;
	static readonly RESOURCEID: VclQueryServiceLinkField;
	static readonly RESOURCETYPE: VclQueryServiceLinkField;
	static readonly SERVICE: VclQueryServiceLinkField;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclQueryServiceLinkField;
}

/**
 * TBS
 */
declare class VclQueryVirtualCenterField {
	static readonly values: string[];
	static readonly value: string;
	static readonly ID: VclQueryVirtualCenterField;
	static readonly HREF: VclQueryVirtualCenterField;
	static readonly ISBUSY: VclQueryVirtualCenterField;
	static readonly ISENABLED: VclQueryVirtualCenterField;
	static readonly ISSUPPORTED: VclQueryVirtualCenterField;
	static readonly NAME: VclQueryVirtualCenterField;
	static readonly STATUS: VclQueryVirtualCenterField;
	static readonly URL: VclQueryVirtualCenterField;
	static readonly USERNAME: VclQueryVirtualCenterField;
	static readonly UUID: VclQueryVirtualCenterField;
	static readonly VCVERSION: VclQueryVirtualCenterField;
	static readonly VSMIP: VclQueryVirtualCenterField;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclQueryVirtualCenterField;
}

/**
 * TBS
 */
declare class VclQueryResourcePoolVMField {
	static readonly values: string[];
	static readonly value: string;
	static readonly ID: VclQueryResourcePoolVMField;
	static readonly HREF: VclQueryResourcePoolVMField;
	static readonly CONTAINERNAME: VclQueryResourcePoolVMField;
	static readonly GUESTOS: VclQueryResourcePoolVMField;
	static readonly HARDWAREVERSION: VclQueryResourcePoolVMField;
	static readonly ISBUSY: VclQueryResourcePoolVMField;
	static readonly ISDEPLOYED: VclQueryResourcePoolVMField;
	static readonly NAME: VclQueryResourcePoolVMField;
	static readonly STATUS: VclQueryResourcePoolVMField;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclQueryResourcePoolVMField;
}

/**
 * TBS
 */
declare class VclQueryAdminCatalogField {
	static readonly values: string[];
	static readonly value: string;
	static readonly ID: VclQueryAdminCatalogField;
	static readonly HREF: VclQueryAdminCatalogField;
	static readonly CREATIONDATE: VclQueryAdminCatalogField;
	static readonly ISPUBLISHED: VclQueryAdminCatalogField;
	static readonly ISSHARED: VclQueryAdminCatalogField;
	static readonly NAME: VclQueryAdminCatalogField;
	static readonly NUMBEROFMEDIA: VclQueryAdminCatalogField;
	static readonly NUMBEROFTEMPLATES: VclQueryAdminCatalogField;
	static readonly ORG: VclQueryAdminCatalogField;
	static readonly ORGNAME: VclQueryAdminCatalogField;
	static readonly OWNER: VclQueryAdminCatalogField;
	static readonly OWNERNAME: VclQueryAdminCatalogField;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclQueryAdminCatalogField;
}

/**
 * TBS
 */
declare class VclQueryApiFilterField {
	static readonly values: string[];
	static readonly value: string;
	static readonly ID: VclQueryApiFilterField;
	static readonly HREF: VclQueryApiFilterField;
	static readonly SERVICE: VclQueryApiFilterField;
	static readonly URLPATTERN: VclQueryApiFilterField;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclQueryApiFilterField;
}

/**
 * TBS
 */
declare class VclQueryProviderVdcResourcePoolRelationField {
	static readonly values: string[];
	static readonly value: string;
	static readonly ID: VclQueryProviderVdcResourcePoolRelationField;
	static readonly HREF: VclQueryProviderVdcResourcePoolRelationField;
	static readonly CPURESERVATIONALLOCATIONMHZ: VclQueryProviderVdcResourcePoolRelationField;
	static readonly CPURESERVATIONLIMITMHZ: VclQueryProviderVdcResourcePoolRelationField;
	static readonly ISENABLED: VclQueryProviderVdcResourcePoolRelationField;
	static readonly ISPRIMARY: VclQueryProviderVdcResourcePoolRelationField;
	static readonly MEMORYRESERVATIONALLOCATIONMB: VclQueryProviderVdcResourcePoolRelationField;
	static readonly MEMORYRESERVATIONLIMITMB: VclQueryProviderVdcResourcePoolRelationField;
	static readonly NAME: VclQueryProviderVdcResourcePoolRelationField;
	static readonly NUMBEROFVMS: VclQueryProviderVdcResourcePoolRelationField;
	static readonly PROVIDERVDC: VclQueryProviderVdcResourcePoolRelationField;
	static readonly RESOURCEPOOLMOREF: VclQueryProviderVdcResourcePoolRelationField;
	static readonly VC: VclQueryProviderVdcResourcePoolRelationField;
	static readonly VCNAME: VclQueryProviderVdcResourcePoolRelationField;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclQueryProviderVdcResourcePoolRelationField;
}

/**
 * TBS
 */
declare class VclQueryAdminVAppField {
	static readonly values: string[];
	static readonly value: string;
	static readonly ID: VclQueryAdminVAppField;
	static readonly HREF: VclQueryAdminVAppField;
	static readonly CPUALLOCATIONMHZ: VclQueryAdminVAppField;
	static readonly CREATIONDATE: VclQueryAdminVAppField;
	static readonly ISBUSY: VclQueryAdminVAppField;
	static readonly ISDEPLOYED: VclQueryAdminVAppField;
	static readonly ISENABLED: VclQueryAdminVAppField;
	static readonly ISEXPIRED: VclQueryAdminVAppField;
	static readonly ISINMAINTENANCEMODE: VclQueryAdminVAppField;
	static readonly ISVDCENABLED: VclQueryAdminVAppField;
	static readonly MEMORYALLOCATIONMB: VclQueryAdminVAppField;
	static readonly NAME: VclQueryAdminVAppField;
	static readonly NUMBEROFVMS: VclQueryAdminVAppField;
	static readonly ORG: VclQueryAdminVAppField;
	static readonly OWNERNAME: VclQueryAdminVAppField;
	static readonly STATUS: VclQueryAdminVAppField;
	static readonly STORAGEKB: VclQueryAdminVAppField;
	static readonly VDC: VclQueryAdminVAppField;
	static readonly VDCNAME: VclQueryAdminVAppField;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclQueryAdminVAppField;
}

/**
 * TBS
 */
declare class VclQueryAdminUserField {
	static readonly values: string[];
	static readonly value: string;
	static readonly ID: VclQueryAdminUserField;
	static readonly HREF: VclQueryAdminUserField;
	static readonly DEPLOYEDVMQUOTA: VclQueryAdminUserField;
	static readonly DEPLOYEDVMQUOTARANK: VclQueryAdminUserField;
	static readonly FULLNAME: VclQueryAdminUserField;
	static readonly IDENTITYPROVIDERTYPE: VclQueryAdminUserField;
	static readonly ISENABLED: VclQueryAdminUserField;
	static readonly ISLDAPUSER: VclQueryAdminUserField;
	static readonly NAME: VclQueryAdminUserField;
	static readonly NUMBEROFDEPLOYEDVMS: VclQueryAdminUserField;
	static readonly NUMBEROFSTOREDVMS: VclQueryAdminUserField;
	static readonly ORG: VclQueryAdminUserField;
	static readonly STOREDVMQUOTA: VclQueryAdminUserField;
	static readonly STOREDVMQUOTARANK: VclQueryAdminUserField;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclQueryAdminUserField;
}

/**
 * TBS
 */
declare class VclQueryAdminAllocatedExternalAddressField {
	static readonly values: string[];
	static readonly value: string;
	static readonly ID: VclQueryAdminAllocatedExternalAddressField;
	static readonly HREF: VclQueryAdminAllocatedExternalAddressField;
	static readonly IPADDRESS: VclQueryAdminAllocatedExternalAddressField;
	static readonly LINKEDNETWORK: VclQueryAdminAllocatedExternalAddressField;
	static readonly NETWORK: VclQueryAdminAllocatedExternalAddressField;
	static readonly ORG: VclQueryAdminAllocatedExternalAddressField;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclQueryAdminAllocatedExternalAddressField;
}

/**
 * TBS
 */
declare class VclQueryAdminServiceField {
	static readonly values: string[];
	static readonly value: string;
	static readonly ID: VclQueryAdminServiceField;
	static readonly HREF: VclQueryAdminServiceField;
	static readonly ENABLED: VclQueryAdminServiceField;
	static readonly EXCHANGE: VclQueryAdminServiceField;
	static readonly ISAUTHORIZATIONENABLED: VclQueryAdminServiceField;
	static readonly NAME: VclQueryAdminServiceField;
	static readonly NAMESPACE: VclQueryAdminServiceField;
	static readonly PRIORITY: VclQueryAdminServiceField;
	static readonly ROUTINGKEY: VclQueryAdminServiceField;
	static readonly VENDOR: VclQueryAdminServiceField;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclQueryAdminServiceField;
}

/**
 * TBS
 */
declare class VclFormatType {
	static readonly values: string[];
	static readonly value: string;
	static readonly REFERENCE_VIEW: VclFormatType;
	static readonly RECORD_VIEW: VclFormatType;
	static readonly ID_RECORD_VIEW: VclFormatType;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclFormatType;
}

/**
 * TBS
 */
declare class VclQueryMediaField {
	static readonly values: string[];
	static readonly value: string;
	static readonly ID: VclQueryMediaField;
	static readonly HREF: VclQueryMediaField;
	static readonly CATALOG: VclQueryMediaField;
	static readonly CATALOGITEM: VclQueryMediaField;
	static readonly CATALOGNAME: VclQueryMediaField;
	static readonly CREATIONDATE: VclQueryMediaField;
	static readonly ISBUSY: VclQueryMediaField;
	static readonly ISPUBLISHED: VclQueryMediaField;
	static readonly NAME: VclQueryMediaField;
	static readonly ORG: VclQueryMediaField;
	static readonly OWNER: VclQueryMediaField;
	static readonly OWNERNAME: VclQueryMediaField;
	static readonly STATUS: VclQueryMediaField;
	static readonly STORAGEB: VclQueryMediaField;
	static readonly STORAGEPROFILENAME: VclQueryMediaField;
	static readonly VDC: VclQueryMediaField;
	static readonly VDCNAME: VclQueryMediaField;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclQueryMediaField;
}

/**
 * TBS
 */
declare class VclQueryEventField {
	static readonly values: string[];
	static readonly value: string;
	static readonly ID: VclQueryEventField;
	static readonly HREF: VclQueryEventField;
	static readonly ENTITY: VclQueryEventField;
	static readonly ENTITYNAME: VclQueryEventField;
	static readonly ENTITYTYPE: VclQueryEventField;
	static readonly EVENTSTATUS: VclQueryEventField;
	static readonly EVENTTYPE: VclQueryEventField;
	static readonly ORGNAME: VclQueryEventField;
	static readonly SERVICENAMESPACE: VclQueryEventField;
	static readonly TIMESTAMP: VclQueryEventField;
	static readonly USERNAME: VclQueryEventField;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclQueryEventField;
}

/**
 * TBS
 */
declare class VclQueryResourceClassField {
	static readonly values: string[];
	static readonly value: string;
	static readonly ID: VclQueryResourceClassField;
	static readonly HREF: VclQueryResourceClassField;
	static readonly MIMETYPE: VclQueryResourceClassField;
	static readonly NAME: VclQueryResourceClassField;
	static readonly NID: VclQueryResourceClassField;
	static readonly SERVICE: VclQueryResourceClassField;
	static readonly URLTEMPLATE: VclQueryResourceClassField;
	static readonly URNPATTERN: VclQueryResourceClassField;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclQueryResourceClassField;
}

/**
 * TBS
 */
declare class VclQueryAdminApiDefinitionField {
	static readonly values: string[];
	static readonly value: string;
	static readonly ID: VclQueryAdminApiDefinitionField;
	static readonly HREF: VclQueryAdminApiDefinitionField;
	static readonly APIVENDOR: VclQueryAdminApiDefinitionField;
	static readonly ENTRYPOINT: VclQueryAdminApiDefinitionField;
	static readonly NAME: VclQueryAdminApiDefinitionField;
	static readonly NAMESPACE: VclQueryAdminApiDefinitionField;
	static readonly SERVICE: VclQueryAdminApiDefinitionField;
	static readonly SERVICENAME: VclQueryAdminApiDefinitionField;
	static readonly SERVICENAMESPACE: VclQueryAdminApiDefinitionField;
	static readonly SERVICEVENDOR: VclQueryAdminApiDefinitionField;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclQueryAdminApiDefinitionField;
}

/**
 * TBS
 */
declare class VclQueryDatastoreProviderVdcRelationField {
	static readonly values: string[];
	static readonly value: string;
	static readonly ID: VclQueryDatastoreProviderVdcRelationField;
	static readonly HREF: VclQueryDatastoreProviderVdcRelationField;
	static readonly DATASTORE: VclQueryDatastoreProviderVdcRelationField;
	static readonly DATASTORETYPE: VclQueryDatastoreProviderVdcRelationField;
	static readonly ISDELETED: VclQueryDatastoreProviderVdcRelationField;
	static readonly ISENABLED: VclQueryDatastoreProviderVdcRelationField;
	static readonly MOREF: VclQueryDatastoreProviderVdcRelationField;
	static readonly NAME: VclQueryDatastoreProviderVdcRelationField;
	static readonly PROVIDERVDC: VclQueryDatastoreProviderVdcRelationField;
	static readonly PROVISIONEDSTORAGEMB: VclQueryDatastoreProviderVdcRelationField;
	static readonly REQUESTEDSTORAGEMB: VclQueryDatastoreProviderVdcRelationField;
	static readonly STORAGEMB: VclQueryDatastoreProviderVdcRelationField;
	static readonly STORAGEUSEDMB: VclQueryDatastoreProviderVdcRelationField;
	static readonly VC: VclQueryDatastoreProviderVdcRelationField;
	static readonly VCNAME: VclQueryDatastoreProviderVdcRelationField;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclQueryDatastoreProviderVdcRelationField;
}

/**
 * TBS
 */
declare class VclQueryAdminVmDiskRelationField {
	static readonly values: string[];
	static readonly value: string;
	static readonly ID: VclQueryAdminVmDiskRelationField;
	static readonly HREF: VclQueryAdminVmDiskRelationField;
	static readonly DISK: VclQueryAdminVmDiskRelationField;
	static readonly VDC: VclQueryAdminVmDiskRelationField;
	static readonly VM: VclQueryAdminVmDiskRelationField;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclQueryAdminVmDiskRelationField;
}

/**
 * TBS
 */
declare class VclQueryAdminVAppNetworkField {
	static readonly values: string[];
	static readonly value: string;
	static readonly ID: VclQueryAdminVAppNetworkField;
	static readonly HREF: VclQueryAdminVAppNetworkField;
	static readonly DNS1: VclQueryAdminVAppNetworkField;
	static readonly DNS2: VclQueryAdminVAppNetworkField;
	static readonly DNSSUFFIX: VclQueryAdminVAppNetworkField;
	static readonly GATEWAY: VclQueryAdminVAppNetworkField;
	static readonly ISBUSY: VclQueryAdminVAppNetworkField;
	static readonly ISIPSCOPEINHERITED: VclQueryAdminVAppNetworkField;
	static readonly NAME: VclQueryAdminVAppNetworkField;
	static readonly NETMASK: VclQueryAdminVAppNetworkField;
	static readonly ORG: VclQueryAdminVAppNetworkField;
	static readonly VAPP: VclQueryAdminVAppNetworkField;
	static readonly VAPPNAME: VclQueryAdminVAppNetworkField;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclQueryAdminVAppNetworkField;
}

/**
 * TBS
 */
declare class VclQueryOrgVdcField {
	static readonly values: string[];
	static readonly value: string;
	static readonly ID: VclQueryOrgVdcField;
	static readonly HREF: VclQueryOrgVdcField;
	static readonly CPUALLOCATIONMHZ: VclQueryOrgVdcField;
	static readonly CPULIMITMHZ: VclQueryOrgVdcField;
	static readonly CPUUSEDMHZ: VclQueryOrgVdcField;
	static readonly ISBUSY: VclQueryOrgVdcField;
	static readonly ISENABLED: VclQueryOrgVdcField;
	static readonly ISSYSTEMVDC: VclQueryOrgVdcField;
	static readonly MEMORYALLOCATIONMB: VclQueryOrgVdcField;
	static readonly MEMORYLIMITMB: VclQueryOrgVdcField;
	static readonly MEMORYUSEDMB: VclQueryOrgVdcField;
	static readonly NAME: VclQueryOrgVdcField;
	static readonly NUMBEROFDATASTORES: VclQueryOrgVdcField;
	static readonly NUMBEROFDISKS: VclQueryOrgVdcField;
	static readonly NUMBEROFMEDIA: VclQueryOrgVdcField;
	static readonly NUMBEROFSTORAGEPROFILES: VclQueryOrgVdcField;
	static readonly NUMBEROFVAPPTEMPLATES: VclQueryOrgVdcField;
	static readonly NUMBEROFVAPPS: VclQueryOrgVdcField;
	static readonly ORGNAME: VclQueryOrgVdcField;
	static readonly PROVIDERVDC: VclQueryOrgVdcField;
	static readonly PROVIDERVDCNAME: VclQueryOrgVdcField;
	static readonly STATUS: VclQueryOrgVdcField;
	static readonly STORAGEALLOCATIONMB: VclQueryOrgVdcField;
	static readonly STORAGELIMITMB: VclQueryOrgVdcField;
	static readonly STORAGEUSEDMB: VclQueryOrgVdcField;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclQueryOrgVdcField;
}

/**
 * TBS
 */
declare class VclQueryServiceField {
	static readonly values: string[];
	static readonly value: string;
	static readonly ID: VclQueryServiceField;
	static readonly HREF: VclQueryServiceField;
	static readonly NAME: VclQueryServiceField;
	static readonly NAMESPACE: VclQueryServiceField;
	static readonly VENDOR: VclQueryServiceField;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclQueryServiceField;
}

/**
 * TBS
 */
declare class VclQueryVAppOrgVdcNetworkRelationField {
	static readonly values: string[];
	static readonly value: string;
	static readonly ID: VclQueryVAppOrgVdcNetworkRelationField;
	static readonly HREF: VclQueryVAppOrgVdcNetworkRelationField;
	static readonly ENTITYTYPE: VclQueryVAppOrgVdcNetworkRelationField;
	static readonly NAME: VclQueryVAppOrgVdcNetworkRelationField;
	static readonly ORG: VclQueryVAppOrgVdcNetworkRelationField;
	static readonly ORGVDCNETWORK: VclQueryVAppOrgVdcNetworkRelationField;
	static readonly ORGVDCNETWORKNAME: VclQueryVAppOrgVdcNetworkRelationField;
	static readonly OWNERNAME: VclQueryVAppOrgVdcNetworkRelationField;
	static readonly STATUS: VclQueryVAppOrgVdcNetworkRelationField;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclQueryVAppOrgVdcNetworkRelationField;
}

/**
 * TBS
 */
declare class VclQueryAdminOrgVdcStorageProfileField {
	static readonly values: string[];
	static readonly value: string;
	static readonly ID: VclQueryAdminOrgVdcStorageProfileField;
	static readonly HREF: VclQueryAdminOrgVdcStorageProfileField;
	static readonly ISDEFAULTSTORAGEPROFILE: VclQueryAdminOrgVdcStorageProfileField;
	static readonly ISENABLED: VclQueryAdminOrgVdcStorageProfileField;
	static readonly NAME: VclQueryAdminOrgVdcStorageProfileField;
	static readonly NUMBEROFCONDITIONS: VclQueryAdminOrgVdcStorageProfileField;
	static readonly ORG: VclQueryAdminOrgVdcStorageProfileField;
	static readonly STORAGELIMITMB: VclQueryAdminOrgVdcStorageProfileField;
	static readonly STORAGEPROFILEMOREF: VclQueryAdminOrgVdcStorageProfileField;
	static readonly STORAGEUSEDMB: VclQueryAdminOrgVdcStorageProfileField;
	static readonly VC: VclQueryAdminOrgVdcStorageProfileField;
	static readonly VDC: VclQueryAdminOrgVdcStorageProfileField;
	static readonly VDCNAME: VclQueryAdminOrgVdcStorageProfileField;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclQueryAdminOrgVdcStorageProfileField;
}

/**
 * TBS
 */
declare class VclQueryResourcePoolField {
	static readonly values: string[];
	static readonly value: string;
	static readonly ID: VclQueryResourcePoolField;
	static readonly HREF: VclQueryResourcePoolField;
	static readonly ISDELETED: VclQueryResourcePoolField;
	static readonly MOREF: VclQueryResourcePoolField;
	static readonly NAME: VclQueryResourcePoolField;
	static readonly VC: VclQueryResourcePoolField;
	static readonly VCNAME: VclQueryResourcePoolField;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclQueryResourcePoolField;
}

/**
 * TBS
 */
declare class VclQueryOrgVdcNetworkField {
	static readonly values: string[];
	static readonly value: string;
	static readonly ID: VclQueryOrgVdcNetworkField;
	static readonly HREF: VclQueryOrgVdcNetworkField;
	static readonly CONNECTEDTO: VclQueryOrgVdcNetworkField;
	static readonly DEFAULTGATEWAY: VclQueryOrgVdcNetworkField;
	static readonly DNS1: VclQueryOrgVdcNetworkField;
	static readonly DNS2: VclQueryOrgVdcNetworkField;
	static readonly DNSSUFFIX: VclQueryOrgVdcNetworkField;
	static readonly ISBUSY: VclQueryOrgVdcNetworkField;
	static readonly ISIPSCOPEINHERITED: VclQueryOrgVdcNetworkField;
	static readonly ISSHARED: VclQueryOrgVdcNetworkField;
	static readonly LINKTYPE: VclQueryOrgVdcNetworkField;
	static readonly NAME: VclQueryOrgVdcNetworkField;
	static readonly NETMASK: VclQueryOrgVdcNetworkField;
	static readonly VDC: VclQueryOrgVdcNetworkField;
	static readonly VDCNAME: VclQueryOrgVdcNetworkField;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclQueryOrgVdcNetworkField;
}

/**
 * TBS
 */
declare class VclQueryReferenceField {
	static readonly values: string[];
	static readonly value: string;
	static readonly ID: VclQueryReferenceField;
	static readonly HREF: VclQueryReferenceField;
	static readonly NAME: VclQueryReferenceField;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclQueryReferenceField;
}

/**
 * TBS
 */
declare class VclQueryDiskField {
	static readonly values: string[];
	static readonly value: string;
	static readonly ID: VclQueryDiskField;
	static readonly HREF: VclQueryDiskField;
	static readonly BUSSUBTYPE: VclQueryDiskField;
	static readonly BUSTYPE: VclQueryDiskField;
	static readonly BUSTYPEDESC: VclQueryDiskField;
	static readonly DATASTORE: VclQueryDiskField;
	static readonly DATASTORENAME: VclQueryDiskField;
	static readonly ISATTACHED: VclQueryDiskField;
	static readonly NAME: VclQueryDiskField;
	static readonly OWNERNAME: VclQueryDiskField;
	static readonly SIZEB: VclQueryDiskField;
	static readonly STATUS: VclQueryDiskField;
	static readonly STORAGEPROFILE: VclQueryDiskField;
	static readonly STORAGEPROFILENAME: VclQueryDiskField;
	static readonly TASK: VclQueryDiskField;
	static readonly VDC: VclQueryDiskField;
	static readonly VDCNAME: VclQueryDiskField;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclQueryDiskField;
}

/**
 * TBS
 */
declare class VclQueryVmDiskRelationField {
	static readonly values: string[];
	static readonly value: string;
	static readonly ID: VclQueryVmDiskRelationField;
	static readonly HREF: VclQueryVmDiskRelationField;
	static readonly DISK: VclQueryVmDiskRelationField;
	static readonly VDC: VclQueryVmDiskRelationField;
	static readonly VM: VclQueryVmDiskRelationField;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclQueryVmDiskRelationField;
}

/**
 * TBS
 */
declare class VclQueryNetworkPoolField {
	static readonly values: string[];
	static readonly value: string;
	static readonly ID: VclQueryNetworkPoolField;
	static readonly HREF: VclQueryNetworkPoolField;
	static readonly ISBUSY: VclQueryNetworkPoolField;
	static readonly NAME: VclQueryNetworkPoolField;
	static readonly NETWORKPOOLTYPE: VclQueryNetworkPoolField;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclQueryNetworkPoolField;
}

/**
 * TBS
 */
declare class VclQueryCellField {
	static readonly values: string[];
	static readonly value: string;
	static readonly ID: VclQueryCellField;
	static readonly HREF: VclQueryCellField;
	static readonly BUILDDATE: VclQueryCellField;
	static readonly ISACTIVE: VclQueryCellField;
	static readonly ISVMWAREVC: VclQueryCellField;
	static readonly NAME: VclQueryCellField;
	static readonly PRIMARYIP: VclQueryCellField;
	static readonly VERSION: VclQueryCellField;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclQueryCellField;
}

/**
 * TBS
 */
declare class VclQueryStrandedItemField {
	static readonly values: string[];
	static readonly value: string;
	static readonly ID: VclQueryStrandedItemField;
	static readonly HREF: VclQueryStrandedItemField;
	static readonly DELETIONDATE: VclQueryStrandedItemField;
	static readonly NAME: VclQueryStrandedItemField;
	static readonly NUMBEROFPURGEATTEMPTS: VclQueryStrandedItemField;
	static readonly PARENT: VclQueryStrandedItemField;
	static readonly PARENTNAME: VclQueryStrandedItemField;
	static readonly VIMOBJECTTYPE: VclQueryStrandedItemField;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclQueryStrandedItemField;
}

/**
 * TBS
 */
declare class VclQueryAllocatedExternalAddressField {
	static readonly values: string[];
	static readonly value: string;
	static readonly ID: VclQueryAllocatedExternalAddressField;
	static readonly HREF: VclQueryAllocatedExternalAddressField;
	static readonly IPADDRESS: VclQueryAllocatedExternalAddressField;
	static readonly LINKEDNETWORK: VclQueryAllocatedExternalAddressField;
	static readonly NETWORK: VclQueryAllocatedExternalAddressField;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclQueryAllocatedExternalAddressField;
}

/**
 * TBS
 */
declare class VclMetadataExpressionType {
	static readonly values: string[];
	static readonly value: string;
	static readonly EQUALS: VclMetadataExpressionType;
	static readonly NOT_EQUALS: VclMetadataExpressionType;
	static readonly LESSER_THAN: VclMetadataExpressionType;
	static readonly LESSER_THAN_OR_EQUAL: VclMetadataExpressionType;
	static readonly GREATER_THAN: VclMetadataExpressionType;
	static readonly GREATER_THAN_OR_EQUAL: VclMetadataExpressionType;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclMetadataExpressionType;
}

/**
 * TBS
 */
declare class VclQueryOrgField {
	static readonly values: string[];
	static readonly value: string;
	static readonly ID: VclQueryOrgField;
	static readonly HREF: VclQueryOrgField;
	static readonly CANPUBLISHCATALOGS: VclQueryOrgField;
	static readonly DEPLOYEDVMQUOTA: VclQueryOrgField;
	static readonly DISPLAYNAME: VclQueryOrgField;
	static readonly ISENABLED: VclQueryOrgField;
	static readonly ISREADONLY: VclQueryOrgField;
	static readonly NAME: VclQueryOrgField;
	static readonly NUMBEROFCATALOGS: VclQueryOrgField;
	static readonly NUMBEROFDISKS: VclQueryOrgField;
	static readonly NUMBEROFGROUPS: VclQueryOrgField;
	static readonly NUMBEROFVAPPS: VclQueryOrgField;
	static readonly NUMBEROFVDCS: VclQueryOrgField;
	static readonly STOREDVMQUOTA: VclQueryOrgField;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclQueryOrgField;
}

/**
 * TBS
 */
declare class VclExpressionType {
	static readonly values: string[];
	static readonly value: string;
	static readonly EQUALS: VclExpressionType;
	static readonly NOT_EQUALS: VclExpressionType;
	static readonly LESSER_THAN: VclExpressionType;
	static readonly LESSER_THAN_OR_EQUAL: VclExpressionType;
	static readonly GREATER_THAN: VclExpressionType;
	static readonly GREATER_THAN_OR_EQUAL: VclExpressionType;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclExpressionType;
}

/**
 * TBS
 */
declare class VclQueryAdminMediaField {
	static readonly values: string[];
	static readonly value: string;
	static readonly ID: VclQueryAdminMediaField;
	static readonly HREF: VclQueryAdminMediaField;
	static readonly CATALOG: VclQueryAdminMediaField;
	static readonly CATALOGITEM: VclQueryAdminMediaField;
	static readonly CATALOGNAME: VclQueryAdminMediaField;
	static readonly CREATIONDATE: VclQueryAdminMediaField;
	static readonly ISBUSY: VclQueryAdminMediaField;
	static readonly ISPUBLISHED: VclQueryAdminMediaField;
	static readonly ISVDCENABLED: VclQueryAdminMediaField;
	static readonly NAME: VclQueryAdminMediaField;
	static readonly ORG: VclQueryAdminMediaField;
	static readonly OWNER: VclQueryAdminMediaField;
	static readonly OWNERNAME: VclQueryAdminMediaField;
	static readonly STATUS: VclQueryAdminMediaField;
	static readonly STORAGEB: VclQueryAdminMediaField;
	static readonly STORAGEPROFILENAME: VclQueryAdminMediaField;
	static readonly VDC: VclQueryAdminMediaField;
	static readonly VDCNAME: VclQueryAdminMediaField;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclQueryAdminMediaField;
}

/**
 * TBS
 */
declare class VclQueryRoleField {
	static readonly values: string[];
	static readonly value: string;
	static readonly ID: VclQueryRoleField;
	static readonly HREF: VclQueryRoleField;
	static readonly ISREADONLY: VclQueryRoleField;
	static readonly NAME: VclQueryRoleField;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclQueryRoleField;
}

/**
 * TBS
 */
declare class VclQueryAclRuleField {
	static readonly values: string[];
	static readonly value: string;
	static readonly ID: VclQueryAclRuleField;
	static readonly HREF: VclQueryAclRuleField;
	static readonly NAME: VclQueryAclRuleField;
	static readonly ORG: VclQueryAclRuleField;
	static readonly ORGACCESS: VclQueryAclRuleField;
	static readonly PRINCIPAL: VclQueryAclRuleField;
	static readonly PRINCIPALACCESS: VclQueryAclRuleField;
	static readonly PRINCIPALTYPE: VclQueryAclRuleField;
	static readonly RESOURCECLASSACTION: VclQueryAclRuleField;
	static readonly SERVICERESOURCE: VclQueryAclRuleField;
	static readonly SERVICERESOURCEACCESS: VclQueryAclRuleField;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclQueryAclRuleField;
}

/**
 * TBS
 */
declare class VclQueryStrandedUserField {
	static readonly values: string[];
	static readonly value: string;
	static readonly ID: VclQueryStrandedUserField;
	static readonly HREF: VclQueryStrandedUserField;
	static readonly FULLNAME: VclQueryStrandedUserField;
	static readonly ISINSYNC: VclQueryStrandedUserField;
	static readonly NAME: VclQueryStrandedUserField;
	static readonly NUMBEROFDEPLOYEDVMS: VclQueryStrandedUserField;
	static readonly NUMBEROFSTOREDVMS: VclQueryStrandedUserField;
	static readonly ORG: VclQueryStrandedUserField;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclQueryStrandedUserField;
}

/**
 * TBS
 */
declare class VclQueryNetworkField {
	static readonly values: string[];
	static readonly value: string;
	static readonly ID: VclQueryNetworkField;
	static readonly HREF: VclQueryNetworkField;
	static readonly DNS1: VclQueryNetworkField;
	static readonly DNS2: VclQueryNetworkField;
	static readonly DNSSUFFIX: VclQueryNetworkField;
	static readonly GATEWAY: VclQueryNetworkField;
	static readonly IPSCOPEID: VclQueryNetworkField;
	static readonly ISBUSY: VclQueryNetworkField;
	static readonly NAME: VclQueryNetworkField;
	static readonly NETMASK: VclQueryNetworkField;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclQueryNetworkField;
}

/**
 * TBS
 */
declare class VclQueryAdminShadowVMField {
	static readonly values: string[];
	static readonly value: string;
	static readonly ID: VclQueryAdminShadowVMField;
	static readonly HREF: VclQueryAdminShadowVMField;
	static readonly DATASTORENAME: VclQueryAdminShadowVMField;
	static readonly ISBUSY: VclQueryAdminShadowVMField;
	static readonly ISDELETED: VclQueryAdminShadowVMField;
	static readonly ISPUBLISHED: VclQueryAdminShadowVMField;
	static readonly NAME: VclQueryAdminShadowVMField;
	static readonly ORG: VclQueryAdminShadowVMField;
	static readonly PRIMARYVAPPNAME: VclQueryAdminShadowVMField;
	static readonly PRIMARYVAPPTEMPLATE: VclQueryAdminShadowVMField;
	static readonly PRIMARYVM: VclQueryAdminShadowVMField;
	static readonly PRIMARYVMCATALOG: VclQueryAdminShadowVMField;
	static readonly PRIMARYVMOWNER: VclQueryAdminShadowVMField;
	static readonly PRIMARYVMNAME: VclQueryAdminShadowVMField;
	static readonly SHADOWVAPP: VclQueryAdminShadowVMField;
	static readonly STATUS: VclQueryAdminShadowVMField;
	static readonly VCNAME: VclQueryAdminShadowVMField;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclQueryAdminShadowVMField;
}

/**
 * TBS
 */
declare class VclQueryOrgVdcStorageProfileField {
	static readonly values: string[];
	static readonly value: string;
	static readonly ID: VclQueryOrgVdcStorageProfileField;
	static readonly HREF: VclQueryOrgVdcStorageProfileField;
	static readonly ISDEFAULTSTORAGEPROFILE: VclQueryOrgVdcStorageProfileField;
	static readonly ISENABLED: VclQueryOrgVdcStorageProfileField;
	static readonly ISVDCBUSY: VclQueryOrgVdcStorageProfileField;
	static readonly NAME: VclQueryOrgVdcStorageProfileField;
	static readonly NUMBEROFCONDITIONS: VclQueryOrgVdcStorageProfileField;
	static readonly STORAGELIMITMB: VclQueryOrgVdcStorageProfileField;
	static readonly STORAGEUSEDMB: VclQueryOrgVdcStorageProfileField;
	static readonly VDC: VclQueryOrgVdcStorageProfileField;
	static readonly VDCNAME: VclQueryOrgVdcStorageProfileField;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclQueryOrgVdcStorageProfileField;
}

/**
 * TBS
 */
declare class VclQueryResourceClassActionField {
	static readonly values: string[];
	static readonly value: string;
	static readonly ID: VclQueryResourceClassActionField;
	static readonly HREF: VclQueryResourceClassActionField;
	static readonly HTTPMETHOD: VclQueryResourceClassActionField;
	static readonly NAME: VclQueryResourceClassActionField;
	static readonly RESOURCECLASS: VclQueryResourceClassActionField;
	static readonly URLPATTERN: VclQueryResourceClassActionField;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclQueryResourceClassActionField;
}

/**
 * TBS
 */
declare class VclQueryVMField {
	static readonly values: string[];
	static readonly value: string;
	static readonly ID: VclQueryVMField;
	static readonly HREF: VclQueryVMField;
	static readonly CATALOGNAME: VclQueryVMField;
	static readonly CONTAINER: VclQueryVMField;
	static readonly CONTAINERNAME: VclQueryVMField;
	static readonly GUESTOS: VclQueryVMField;
	static readonly HARDWAREVERSION: VclQueryVMField;
	static readonly ISBUSY: VclQueryVMField;
	static readonly ISDELETED: VclQueryVMField;
	static readonly ISDEPLOYED: VclQueryVMField;
	static readonly ISINMAINTENANCEMODE: VclQueryVMField;
	static readonly ISPUBLISHED: VclQueryVMField;
	static readonly ISVAPPTEMPLATE: VclQueryVMField;
	static readonly MEMORYMB: VclQueryVMField;
	static readonly NAME: VclQueryVMField;
	static readonly NUMBEROFCPUS: VclQueryVMField;
	static readonly STATUS: VclQueryVMField;
	static readonly STORAGEPROFILENAME: VclQueryVMField;
	static readonly VDC: VclQueryVMField;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclQueryVMField;
}

/**
 * TBS
 */
declare class VclFilterType {
	static readonly values: string[];
	static readonly value: string;
	static readonly AND: VclFilterType;
	static readonly OR: VclFilterType;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclFilterType;
}

/**
 * TBS
 */
declare class VclQueryDvSwitchField {
	static readonly values: string[];
	static readonly value: string;
	static readonly ID: VclQueryDvSwitchField;
	static readonly HREF: VclQueryDvSwitchField;
	static readonly ISVCENABLED: VclQueryDvSwitchField;
	static readonly MOREF: VclQueryDvSwitchField;
	static readonly NAME: VclQueryDvSwitchField;
	static readonly VC: VclQueryDvSwitchField;
	static readonly VCNAME: VclQueryDvSwitchField;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclQueryDvSwitchField;
}

/**
 * TBS
 */
declare class VclQueryDatastoreField {
	static readonly values: string[];
	static readonly value: string;
	static readonly ID: VclQueryDatastoreField;
	static readonly HREF: VclQueryDatastoreField;
	static readonly DATASTORETYPE: VclQueryDatastoreField;
	static readonly ISDELETED: VclQueryDatastoreField;
	static readonly ISENABLED: VclQueryDatastoreField;
	static readonly MOREF: VclQueryDatastoreField;
	static readonly NAME: VclQueryDatastoreField;
	static readonly NUMBEROFPROVIDERVDCS: VclQueryDatastoreField;
	static readonly PROVISIONEDSTORAGEMB: VclQueryDatastoreField;
	static readonly REQUESTEDSTORAGEMB: VclQueryDatastoreField;
	static readonly STORAGEMB: VclQueryDatastoreField;
	static readonly STORAGEUSEDMB: VclQueryDatastoreField;
	static readonly VC: VclQueryDatastoreField;
	static readonly VCNAME: VclQueryDatastoreField;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclQueryDatastoreField;
}

/**
 * TBS
 */
declare class VclQueryUserField {
	static readonly values: string[];
	static readonly value: string;
	static readonly ID: VclQueryUserField;
	static readonly HREF: VclQueryUserField;
	static readonly DEPLOYEDVMQUOTA: VclQueryUserField;
	static readonly FULLNAME: VclQueryUserField;
	static readonly IDENTITYPROVIDERTYPE: VclQueryUserField;
	static readonly ISENABLED: VclQueryUserField;
	static readonly ISLDAPUSER: VclQueryUserField;
	static readonly NAME: VclQueryUserField;
	static readonly NUMBEROFDEPLOYEDVMS: VclQueryUserField;
	static readonly NUMBEROFSTOREDVMS: VclQueryUserField;
	static readonly STOREDVMQUOTA: VclQueryUserField;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclQueryUserField;
}

/**
 * TBS
 */
declare class VclQueryCatalogField {
	static readonly values: string[];
	static readonly value: string;
	static readonly ID: VclQueryCatalogField;
	static readonly HREF: VclQueryCatalogField;
	static readonly CREATIONDATE: VclQueryCatalogField;
	static readonly ISPUBLISHED: VclQueryCatalogField;
	static readonly ISSHARED: VclQueryCatalogField;
	static readonly NAME: VclQueryCatalogField;
	static readonly NUMBEROFMEDIA: VclQueryCatalogField;
	static readonly NUMBEROFVAPPTEMPLATES: VclQueryCatalogField;
	static readonly ORGNAME: VclQueryCatalogField;
	static readonly OWNER: VclQueryCatalogField;
	static readonly OWNERNAME: VclQueryCatalogField;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclQueryCatalogField;
}

/**
 * TBS
 */
declare class VclQueryAdminEventField {
	static readonly values: string[];
	static readonly value: string;
	static readonly ID: VclQueryAdminEventField;
	static readonly HREF: VclQueryAdminEventField;
	static readonly ENTITY: VclQueryAdminEventField;
	static readonly ENTITYNAME: VclQueryAdminEventField;
	static readonly ENTITYTYPE: VclQueryAdminEventField;
	static readonly EVENTID: VclQueryAdminEventField;
	static readonly EVENTSTATUS: VclQueryAdminEventField;
	static readonly EVENTTYPE: VclQueryAdminEventField;
	static readonly ORG: VclQueryAdminEventField;
	static readonly ORGNAME: VclQueryAdminEventField;
	static readonly PRODUCTVERSION: VclQueryAdminEventField;
	static readonly SERVICENAMESPACE: VclQueryAdminEventField;
	static readonly TIMESTAMP: VclQueryAdminEventField;
	static readonly USERNAME: VclQueryAdminEventField;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclQueryAdminEventField;
}

/**
 * TBS
 */
declare class VclQueryHostField {
	static readonly values: string[];
	static readonly value: string;
	static readonly ID: VclQueryHostField;
	static readonly HREF: VclQueryHostField;
	static readonly ISBUSY: VclQueryHostField;
	static readonly ISCROSSHOSTENABLED: VclQueryHostField;
	static readonly ISDELETED: VclQueryHostField;
	static readonly ISENABLED: VclQueryHostField;
	static readonly ISHUNG: VclQueryHostField;
	static readonly ISINMAINTENANCEMODE: VclQueryHostField;
	static readonly ISPENDINGUPGRADE: VclQueryHostField;
	static readonly ISPREPARED: VclQueryHostField;
	static readonly ISSUPPORTED: VclQueryHostField;
	static readonly NAME: VclQueryHostField;
	static readonly NUMBEROFVMS: VclQueryHostField;
	static readonly OSVERSION: VclQueryHostField;
	static readonly STATE: VclQueryHostField;
	static readonly VC: VclQueryHostField;
	static readonly VCNAME: VclQueryHostField;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclQueryHostField;
}

/**
 * TBS
 */
declare class VclQueryBlockingTaskField {
	static readonly values: string[];
	static readonly value: string;
	static readonly ID: VclQueryBlockingTaskField;
	static readonly HREF: VclQueryBlockingTaskField;
	static readonly CREATIONDATE: VclQueryBlockingTaskField;
	static readonly EXPIRATIONTIME: VclQueryBlockingTaskField;
	static readonly HASOWNER: VclQueryBlockingTaskField;
	static readonly JOBSTATUS: VclQueryBlockingTaskField;
	static readonly OPERATIONNAME: VclQueryBlockingTaskField;
	static readonly ORIGINATINGORG: VclQueryBlockingTaskField;
	static readonly ORIGINATINGORGNAME: VclQueryBlockingTaskField;
	static readonly OWNER: VclQueryBlockingTaskField;
	static readonly OWNERNAME: VclQueryBlockingTaskField;
	static readonly STATUS: VclQueryBlockingTaskField;
	static readonly TASK: VclQueryBlockingTaskField;
	static readonly TIMEOUTACTION: VclQueryBlockingTaskField;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclQueryBlockingTaskField;
}

/**
 * TBS
 */
declare class VclQueryApiDefinitionField {
	static readonly values: string[];
	static readonly value: string;
	static readonly ID: VclQueryApiDefinitionField;
	static readonly HREF: VclQueryApiDefinitionField;
	static readonly APIVENDOR: VclQueryApiDefinitionField;
	static readonly ENTRYPOINT: VclQueryApiDefinitionField;
	static readonly NAME: VclQueryApiDefinitionField;
	static readonly NAMESPACE: VclQueryApiDefinitionField;
	static readonly SERVICE: VclQueryApiDefinitionField;
	static readonly SERVICENAME: VclQueryApiDefinitionField;
	static readonly SERVICENAMESPACE: VclQueryApiDefinitionField;
	static readonly SERVICEVENDOR: VclQueryApiDefinitionField;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclQueryApiDefinitionField;
}

/**
 * TBS
 */
declare class VclQueryFileDescriptorField {
	static readonly values: string[];
	static readonly value: string;
	static readonly ID: VclQueryFileDescriptorField;
	static readonly HREF: VclQueryFileDescriptorField;
	static readonly APIDEFINITION: VclQueryFileDescriptorField;
	static readonly APINAME: VclQueryFileDescriptorField;
	static readonly APINAMESPACE: VclQueryFileDescriptorField;
	static readonly APIVENDOR: VclQueryFileDescriptorField;
	static readonly FILEMIMETYPE: VclQueryFileDescriptorField;
	static readonly FILEURL: VclQueryFileDescriptorField;
	static readonly NAME: VclQueryFileDescriptorField;
	static readonly SERVICE: VclQueryFileDescriptorField;
	static readonly SERVICENAME: VclQueryFileDescriptorField;
	static readonly SERVICENAMESPACE: VclQueryFileDescriptorField;
	static readonly SERVICEVENDOR: VclQueryFileDescriptorField;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclQueryFileDescriptorField;
}

/**
 * TBS
 */
declare class VclQueryTaskField {
	static readonly values: string[];
	static readonly value: string;
	static readonly ID: VclQueryTaskField;
	static readonly HREF: VclQueryTaskField;
	static readonly ENDDATE: VclQueryTaskField;
	static readonly NAME: VclQueryTaskField;
	static readonly OBJECT: VclQueryTaskField;
	static readonly OBJECTNAME: VclQueryTaskField;
	static readonly OBJECTTYPE: VclQueryTaskField;
	static readonly ORG: VclQueryTaskField;
	static readonly ORGNAME: VclQueryTaskField;
	static readonly OWNERNAME: VclQueryTaskField;
	static readonly SERVICENAMESPACE: VclQueryTaskField;
	static readonly STARTDATE: VclQueryTaskField;
	static readonly STATUS: VclQueryTaskField;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclQueryTaskField;
}

/**
 * TBS
 */
declare class VclVappStatus {
	static readonly values: string[];
	static readonly value: string;
	static readonly FAILED_CREATION: VclVappStatus;
	static readonly UNRESOLVED: VclVappStatus;
	static readonly RESOLVED: VclVappStatus;
	static readonly SUSPENDED: VclVappStatus;
	static readonly POWERED_ON: VclVappStatus;
	static readonly WAITING_FOR_INPUT: VclVappStatus;
	static readonly UNKNOWN: VclVappStatus;
	static readonly UNRECOGNIZED: VclVappStatus;
	static readonly POWERED_OFF: VclVappStatus;
	static readonly INCONSISTENT_STATE: VclVappStatus;
	static readonly MIXED: VclVappStatus;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclVappStatus;
}

/**
 * TBS
 */
declare class VclUndeployPowerActionType {
	static readonly values: string[];
	static readonly value: string;
	static readonly SUSPEND: VclUndeployPowerActionType;
	static readonly POWEROFF: VclUndeployPowerActionType;
	static readonly DEFAULT: VclUndeployPowerActionType;
	static readonly SHUTDOWN: VclUndeployPowerActionType;
	static readonly FORCE: VclUndeployPowerActionType;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclUndeployPowerActionType;
}

/**
 * TBS
 */
declare class VclNatMappingModeType {
	static readonly values: string[];
	static readonly value: string;
	static readonly AUTOMATIC: VclNatMappingModeType;
	static readonly MANUAL: VclNatMappingModeType;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclNatMappingModeType;
}

/**
 * TBS
 */
declare class VclSdkMessage {
	static readonly values: string[];
	static readonly value: string;
	static readonly REFERENCE_NOT_FOUND_MSG: VclSdkMessage;
	static readonly UPLOAD_COMPLETE_MSG: VclSdkMessage;
	static readonly FILE_UPLOAD_FAILED_MSG: VclSdkMessage;
	static readonly EXPECTED_STATUS_CODE_MSG: VclSdkMessage;
	static readonly NO_ERROR_MSG: VclSdkMessage;
	static readonly NO_PROGRESS_INFO_MSG: VclSdkMessage;
	static readonly OPERATION_NOT_SUPPORTED_VAPP_MSG: VclSdkMessage;
	static readonly VAPP_NETWORK_NOT_DEPLOYED: VclSdkMessage;
	static readonly ACTUAL_STATUS_CODE_MSG: VclSdkMessage;
	static readonly STATUS_MSG: VclSdkMessage;
	static readonly STATUS_LINE_MSG: VclSdkMessage;
	static readonly FILE_NOT_FOUND_MSG: VclSdkMessage;
	static readonly NO_RESPONSE_RECEIVED_MSG: VclSdkMessage;
	static readonly DOWNLOAD_URL_MSG: VclSdkMessage;
	static readonly PUT_URL_MSG: VclSdkMessage;
	static readonly NO_DOWNLOAD_LINK_MSG: VclSdkMessage;
	static readonly POST_URL_MSG: VclSdkMessage;
	static readonly RESPONSE_MSG: VclSdkMessage;
	static readonly UPLOAD_FAILED_MSG: VclSdkMessage;
	static readonly OPERATION_NOT_FOUND: VclSdkMessage;
	static readonly ORGS_EMPTY_INFO_MSG: VclSdkMessage;
	static readonly POST_REQUEST_BODY: VclSdkMessage;
	static readonly INVALID_NO_FILES_MSG: VclSdkMessage;
	static readonly DELETE_URL_MSG: VclSdkMessage;
	static readonly NO_FILES_TO_UPLOAD_MSG: VclSdkMessage;
	static readonly RESOURCE_NOT_FOUND_MSG: VclSdkMessage;
	static readonly NOT_HARD_DISK_MSG: VclSdkMessage;
	static readonly LOGIN_URL_MSG: VclSdkMessage;
	static readonly GET_URL_MSG: VclSdkMessage;
	static readonly GET_VCLOUD_ID_MSG: VclSdkMessage;
	static readonly HTTP_EXCEPTION__UPLOADING_MSG: VclSdkMessage;
	static readonly GET_SUPPORTED_VERSIONS_URL_MSG: VclSdkMessage;
	static readonly NO_VIRTUAL_MEMORY_MSG: VclSdkMessage;
	static readonly STATUS_CODE_MSG: VclSdkMessage;
	static readonly POST_CONTENT_TYPE: VclSdkMessage;
	static readonly NOT_VALID_SECTION_MSG: VclSdkMessage;
	static readonly UNKNOWN_REF_TYPE_MSG: VclSdkMessage;
	static readonly PUT_REQUEST_BODY: VclSdkMessage;
	static readonly VAPP_IS_A_VM_MSG: VclSdkMessage;
	static readonly UPLOAD_FILE_SIZE_MSG: VclSdkMessage;
	static readonly VERSION_ERROR_MSG: VclSdkMessage;
	static readonly OVF_DESCRIPTOR_NOT_UPLOADED_MSG: VclSdkMessage;
	static readonly OVF_DESCRIPTOR_ALREADY_UPLOADED_MSG: VclSdkMessage;
	static readonly IO_EXCEPTION_UPLOADING_MSG: VclSdkMessage;
	static readonly OVF_DESCRIPTOR_NOT_UPLOADED_NO_FILES_MSG: VclSdkMessage;
	static readonly DOWNLOAD_FAILED_MSG: VclSdkMessage;
	static readonly LOGOUT_INFO_MSG: VclSdkMessage;
	static readonly MEDIA_RESOLVED_MSG: VclSdkMessage;
	static readonly REDIRECTED_URL_MSG: VclSdkMessage;
	static readonly LOGIN_ERROR_MSG: VclSdkMessage;
	static readonly OPERATION_NOT_SUPPORTED_VM_MSG: VclSdkMessage;
	static readonly PUT_CONTENT_TYPE: VclSdkMessage;
	static readonly NO_VIRTUAL_CPU_MSG: VclSdkMessage;
	static readonly VIM_OBJECT_REF_NOT_FOUND_MSG: VclSdkMessage;
	static readonly TASK_TIMEOUT: VclSdkMessage;
	static readonly TASK_ABORTED: VclSdkMessage;
	static readonly TASK_CANCELLED: VclSdkMessage;
	static readonly TASK_ERRORED: VclSdkMessage;
	static readonly DATA_NOT_FOUND: VclSdkMessage;
	static readonly OPERATION_NOT_SUPPORTED: VclSdkMessage;
	static readonly INVALID_OBJECT: VclSdkMessage;
	static readonly NON_BLOCKING_NOTIFICATION: VclSdkMessage;
	static readonly BLOCKING_NOTIFICATION: VclSdkMessage;
	static readonly QUERY_TYPE_NOT_FOUND: VclSdkMessage;
	static readonly LINK_NOT_FOUND_MSG: VclSdkMessage;
	static readonly ENTITY_LINK_NOT_FOUND_MSG: VclSdkMessage;
	static readonly SESSION_EXTENSION_FAILED: VclSdkMessage;
	static readonly INDEPENDENT_DISK_NOT_FOUND: VclSdkMessage;
	static readonly CONTROLLER_NOT_FOUND: VclSdkMessage;
	static readonly VERSION_NOT_SUPPORTED: VclSdkMessage;
	static readonly NOT_SUPPORTED_API: VclSdkMessage;
	static readonly DUPLICATE_NAME: VclSdkMessage;
	static readonly MISSING_PROPERTY: VclSdkMessage;
	static readonly GET_SCHEMA_DEFINITION: VclSdkMessage;
	static readonly MOREF_NOT_FOUND: VclSdkMessage;
	static readonly SERVICE_NOT_FOUND: VclSdkMessage;
	static readonly REQUEST_ID: VclSdkMessage;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclSdkMessage;
}

/**
 * TBS
 */
declare class VclAccessLevelType {
	static readonly values: string[];
	static readonly value: string;
	static readonly FULLCONTROL: VclAccessLevelType;
	static readonly CHANGE: VclAccessLevelType;
	static readonly READONLY: VclAccessLevelType;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclAccessLevelType;
}

/**
 * TBS
 */
declare class VclVersion {
	static readonly values: string[];
	static readonly value: string;
	static readonly V1_5: VclVersion;
	static readonly V5_1: VclVersion;
	static readonly V5_5: VclVersion;
	static readonly V5_6: VclVersion;
	static readonly V5_7: VclVersion;
	static readonly V9_0: VclVersion;
	static readonly V20_0: VclVersion;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclVersion;
}

/**
 * TBS
 */
declare class VclLayer4ProtocolType {
	static readonly values: string[];
	static readonly value: string;
	static readonly TCP: VclLayer4ProtocolType;
	static readonly UDP: VclLayer4ProtocolType;
	static readonly TCP_UDP: VclLayer4ProtocolType;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclLayer4ProtocolType;
}

/**
 * TBS
 */
declare class VclIpsecVpnEncryptionProtocolType {
	static readonly values: string[];
	static readonly value: string;
	static readonly AES: VclIpsecVpnEncryptionProtocolType;
	static readonly AES256: VclIpsecVpnEncryptionProtocolType;
	static readonly TRIPLEDES: VclIpsecVpnEncryptionProtocolType;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclIpsecVpnEncryptionProtocolType;
}

/**
 * TBS
 */
declare class VclBusType {
	static readonly values: string[];
	static readonly value: string;
	static readonly IDE: VclBusType;
	static readonly SCSI: VclBusType;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclBusType;
}

/**
 * TBS
 */
declare class VclBlockingTaskTimeoutActionType {
	static readonly values: string[];
	static readonly value: string;
	static readonly ABORT: VclBlockingTaskTimeoutActionType;
	static readonly RESUME: VclBlockingTaskTimeoutActionType;
	static readonly FAIL: VclBlockingTaskTimeoutActionType;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclBlockingTaskTimeoutActionType;
}

/**
 * TBS
 */
declare class VclMetadataDomainVisibility {
	static readonly values: string[];
	static readonly value: string;
	static readonly PRIVATE: VclMetadataDomainVisibility;
	static readonly READONLY: VclMetadataDomainVisibility;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclMetadataDomainVisibility;
}

/**
 * A generic list of objects.
 */
declare interface VclObjectList {
	/**
	 * Returns all objects within the list.
	 */
	enumerate(): any[];
	/**
	 * Adds a new object to the list.
	 * @param object 
	 */
	add(object: any): void;
	/**
	 * Removes an object from the list.
	 * @param index 
	 */
	remove(index: number): void;
	/**
	 * Returns an index of the given element.
	 * @param element 
	 */
	indexOf(element: any): number;
	/**
	 * Clears the list.
	 */
	clear(): void;
	/**
	 * Returns the number of elements in this list.
	 */
	size(): number;
}

/**
 * TBS
 */
declare class VclGuestOSType {
	static readonly values: string[];
	static readonly value: string;
	static readonly description: string;
	static readonly WINDOWS7GUEST: VclGuestOSType;
	static readonly WINDOWS7_64GUEST: VclGuestOSType;
	static readonly WINDOWS7SERVER64GUEST: VclGuestOSType;
	static readonly WINLONGHORNGUEST: VclGuestOSType;
	static readonly WINLONGHORN64GUEST: VclGuestOSType;
	static readonly WINNETENTERPRISEGUEST: VclGuestOSType;
	static readonly WINNETENTERPRISE64GUEST: VclGuestOSType;
	static readonly WINNETDATACENTERGUEST: VclGuestOSType;
	static readonly WINNETDATACENTER64GUEST: VclGuestOSType;
	static readonly WINNETSTANDARDGUEST: VclGuestOSType;
	static readonly WINNETSTANDARD64GUEST: VclGuestOSType;
	static readonly WINNETWEBGUEST: VclGuestOSType;
	static readonly WINNETBUSINESSGUEST: VclGuestOSType;
	static readonly WINVISTAGUEST: VclGuestOSType;
	static readonly WINVISTA64GUEST: VclGuestOSType;
	static readonly WINXPPROGUEST: VclGuestOSType;
	static readonly WINXPPRO64GUEST: VclGuestOSType;
	static readonly WIN2000ADVSERVGUEST: VclGuestOSType;
	static readonly WIN2000SERVGUEST: VclGuestOSType;
	static readonly WIN2000PROGUEST: VclGuestOSType;
	static readonly WIN98GUEST: VclGuestOSType;
	static readonly WIN95GUEST: VclGuestOSType;
	static readonly WINNTGUEST: VclGuestOSType;
	static readonly WIN31GUEST: VclGuestOSType;
	static readonly RHEL6GUEST: VclGuestOSType;
	static readonly RHEL6_64GUEST: VclGuestOSType;
	static readonly RHEL5GUEST: VclGuestOSType;
	static readonly RHEL5_64GUEST: VclGuestOSType;
	static readonly RHEL4GUEST: VclGuestOSType;
	static readonly RHEL4_64GUEST: VclGuestOSType;
	static readonly RHEL3GUEST: VclGuestOSType;
	static readonly RHEL3_64GUEST: VclGuestOSType;
	static readonly RHEL2GUEST: VclGuestOSType;
	static readonly SLES11GUEST: VclGuestOSType;
	static readonly SLES11_64GUEST: VclGuestOSType;
	static readonly SLES10GUEST: VclGuestOSType;
	static readonly SLES10_64GUEST: VclGuestOSType;
	static readonly SLESGUEST: VclGuestOSType;
	static readonly SLES64GUEST: VclGuestOSType;
	static readonly OESGUEST: VclGuestOSType;
	static readonly ASIANUX3GUEST: VclGuestOSType;
	static readonly ASIANUX3_64GUEST: VclGuestOSType;
	static readonly DEBIAN5GUEST: VclGuestOSType;
	static readonly DEBIAN5_64GUEST: VclGuestOSType;
	static readonly DEBIAN4GUEST: VclGuestOSType;
	static readonly DEBIAN4_64GUEST: VclGuestOSType;
	static readonly UBUNTUGUEST: VclGuestOSType;
	static readonly UBUNTU64GUEST: VclGuestOSType;
	static readonly OTHER26XLINUXGUEST: VclGuestOSType;
	static readonly OTHER26XLINUX64GUEST: VclGuestOSType;
	static readonly OTHER24XLINUXGUEST: VclGuestOSType;
	static readonly OTHER24XLINUX64GUEST: VclGuestOSType;
	static readonly OTHERLINUXGUEST: VclGuestOSType;
	static readonly OTHERLINUX64GUEST: VclGuestOSType;
	static readonly NETWARE6GUEST: VclGuestOSType;
	static readonly NETWARE5GUEST: VclGuestOSType;
	static readonly SOLARIS10GUEST: VclGuestOSType;
	static readonly SOLARIS10_64GUEST: VclGuestOSType;
	static readonly SOLARIS9GUEST: VclGuestOSType;
	static readonly SOLARIS8GUEST: VclGuestOSType;
	static readonly FREEBSDGUEST: VclGuestOSType;
	static readonly FREEBSD64GUEST: VclGuestOSType;
	static readonly OS2GUEST: VclGuestOSType;
	static readonly OPENSERVER5GUEST: VclGuestOSType;
	static readonly UNIXWARE7GUEST: VclGuestOSType;
	static readonly DOSGUEST: VclGuestOSType;
	static readonly OTHERGUEST: VclGuestOSType;
	static readonly OTHERGUEST64: VclGuestOSType;
	static readonly CENTOSGUEST: VclGuestOSType;
	static readonly CENTOS64GUEST: VclGuestOSType;
	static readonly ORACLELINUXGUEST: VclGuestOSType;
	static readonly ORACLELINUX64GUEST: VclGuestOSType;
	static readonly DEBIAN6GUEST: VclGuestOSType;
	static readonly DEBIAN6_64GUEST: VclGuestOSType;
	static readonly ASIANUX4GUEST: VclGuestOSType;
	static readonly ASIANUX4_64GUEST: VclGuestOSType;
	static readonly SOLARIS11_64GUEST: VclGuestOSType;
	static readonly OPENSERVER6GUEST: VclGuestOSType;
	static readonly ECOMSTATIONGUEST: VclGuestOSType;
	static readonly ECOMSTATION2GUEST: VclGuestOSType;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclGuestOSType;
}

/**
 * FinderType
 */
declare class VclFinderType {
	static readonly values: string[];
	static readonly value: string;
	static readonly HOST: VclFinderType;
	static readonly ADMIN_HOST: VclFinderType;
	static readonly ADMIN_EXTENSION_HOST: VclFinderType;
	static readonly ADMIN_CATALOG: VclFinderType;
	static readonly ADMIN_ORGANIZATION: VclFinderType;
	static readonly ADMIN_VDC: VclFinderType;
	static readonly CATALOG: VclFinderType;
	static readonly CATALOG_ITEM: VclFinderType;
	static readonly GROUP: VclFinderType;
	static readonly VIM_SERVER: VclFinderType;
	static readonly VMW_HOST: VclFinderType;
	static readonly VMW_NETWORK_POOL: VclFinderType;
	static readonly VMW_EXTERNAL_NETWORK: VclFinderType;
	static readonly VMW_PROVIDER_VDC: VclFinderType;
	static readonly VMW_DATASTORE: VclFinderType;
	static readonly MEDIA: VclFinderType;
	static readonly ORGANIZATION: VclFinderType;
	static readonly EXTERNAL_NETWORK: VclFinderType;
	static readonly PROVIDER_VDC: VclFinderType;
	static readonly RIGHT: VclFinderType;
	static readonly ROLE: VclFinderType;
	static readonly TASK: VclFinderType;
	static readonly BLOCKING_TASK: VclFinderType;
	static readonly USER: VclFinderType;
	static readonly VAPP: VclFinderType;
	static readonly VM: VclFinderType;
	static readonly VAPP_TEMPLATE: VclFinderType;
	static readonly VDC: VclFinderType;
	static readonly ORG_VDC_NETWORK: VclFinderType;
	static readonly LICENSING_REPORT: VclFinderType;
	static readonly STRANDED_ITEM: VclFinderType;
	static readonly VDC_STORAGE_PROFILE: VclFinderType;
	static readonly PVDC_STORAGE_PROFILE: VclFinderType;
	static readonly DISK: VclFinderType;
	static readonly VAPP_NETWORK: VclFinderType;
	static readonly GATEWAY: VclFinderType;
	static readonly ADMIN_ORG_VDC_NETWORK: VclFinderType;
	static readonly ADMIN_SERVICE: VclFinderType;
	static readonly ADMIN_ACL_RULE: VclFinderType;
	static readonly ADMIN_API_DEFINITION: VclFinderType;
	static readonly ADMIN_FILE_DESCRIPTOR: VclFinderType;
	static readonly ADMIN_API_FILTER: VclFinderType;
	static readonly ADMIN_RESOURCE_CLASS: VclFinderType;
	static readonly ADMIN_RESOURCE_CLASS_ACTION: VclFinderType;
	static readonly ADMIN_SERVICE_LINK: VclFinderType;
	static readonly ADMIN_SERVICE_RESOURCE: VclFinderType;
	static readonly SERVICE: VclFinderType;
	static readonly API_DEFINITION: VclFinderType;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclFinderType;
}

/**
 * The VMWNetworkPoolType element defines a set of types that are used when creating network pools.
 */
declare class VclVMWNetworkPoolType {
	static readonly values: string[];
	static readonly value: string;
	static readonly VLAN_BASED: VclVMWNetworkPoolType;
	static readonly NETWORK_ISOLATION_BASED: VclVMWNetworkPoolType;
	static readonly PORTGROUP_BASED: VclVMWNetworkPoolType;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclVMWNetworkPoolType;
}

/**
 * TBS
 */
declare class VclHostSessionMode {
	static readonly values: string[];
	static readonly value: string;
	static readonly PER_USER_SESSION: VclHostSessionMode;
	static readonly SHARED_SESSION: VclHostSessionMode;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclHostSessionMode;
}

/**
 * Represents an abstract object.
 */
declare class VclAbstractObject {
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Gets the value of the abstract object.
	 * @param type 
	 */
	getValue(type: any): any[];
	/**
	 * Sets the value of the abstract object.
	 * @param value 
	 */
	setValue(value: any): void;
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
}

/**
 * A generic key/value pair map.
 */
declare interface VclMap {
	keys: any[];
	/**
	 * Puts a new key/value pair in the map.
	 * @param key 
	 * @param value 
	 */
	put(key: any, value: any): void;
	/**
	 * Returns the value for the provided key.
	 * @param key 
	 */
	get(key: any): any;
	/**
	 * Removes the value for the provided key and returns it.
	 * @param key 
	 */
	remove(key: any): any;
	/**
	 * Clears the map.
	 */
	clear(): void;
}

/**
 * A generic set of abstract objects.
 */
declare interface VclAbstractObjectSet {
	/**
	 * Finds objects of a given type within the set.
	 * @param type 
	 */
	find(type: any): any[];
	/**
	 * Adds a new object to the set.
	 * @param object 
	 */
	add(object: any): void;
	/**
	 * Removes an object from the set.
	 * @param object 
	 */
	remove(object: any): void;
	/**
	 * Clears the set.
	 */
	clear(): void;
	/**
	 * Returns the number of elements in this set.
	 */
	size(): number;
}

/**
 * Represents an abstract object.
 */
declare class VclAbstractValueObject {
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Gets the value of the abstract object.
	 * @param type 
	 */
	getValue(type: any): any[];
	/**
	 * Sets the value of the abstract object.
	 * @param value 
	 */
	setValue(value: any): void;
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
}

/**
 * A generic set of objects.
 */
declare interface VclObjectSet {
	/**
	 * Returns all objects within the set.
	 */
	enumerate(): any[];
	/**
	 * Adds a new object to the set.
	 * @param object 
	 */
	add(object: any): void;
	/**
	 * Removes an object from the set.
	 * @param index 
	 */
	remove(index: number): void;
	/**
	 * Returns an index of the given element.
	 * @param element 
	 */
	indexOf(element: any): number;
	/**
	 * Clears the set.
	 */
	clear(): void;
	/**
	 * Returns the number of elements in this set.
	 */
	size(): number;
}

/**
 * TBS
 */
declare class VclExpression {
	constructor();
	/**
	 * Creates an expression.
	 * @param key 
	 * @param value 
	 * @param type 
	 */
	constructor(key: any, value: string, type: VclExpressionType);
}

/**
 * TBS
 */
declare class VclExpressionParams {
	constructor();
	/**
	 * Creates an expression params object.
	 * @param key 
	 * @param value 
	 * @param type 
	 */
	constructor(key: any, value: string, type: VclExpressionType);
	/**
	 * Sets a key to the expression params. Use values from one of the query field enumerations based on your query type.
	 * - Example: VclQueryCatalogField, VclQueryCatalogItemField etc.
	 * @param key 
	 */
	setKey(key: any): void;
	/**
	 * Returns a key from the expression params. Return values from one of the query field enumerations based on your query type.
	 * - Example: VclQueryCatalogField, VclQueryCatalogItemField etc.
	 */
	getKey(): any;
	/**
	 * Sets a value to the expression params.
	 * @param value 
	 */
	setValue(value: string): void;
	/**
	 * Returns a value from the expression params.
	 */
	getValue(): string;
	/**
	 * Sets a type to the expression params.
	 * @param type 
	 */
	setType(type: VclExpressionType): void;
	/**
	 * Returns a type from the expression params.
	 */
	getType(): VclExpressionType;
}

/**
 * TBS
 */
declare class VclFilter {
	constructor();
	/**
	 * Creates a filter based on an expression.
	 * @param expression 
	 */
	constructor(expression: VclExpression);
	/**
	 * Creates a filter based on an array of expressions.
	 * @param expressions 
	 * @param type 
	 */
	constructor(expressions: VclExpression[], type: VclFilterType);
	/**
	 * Creates a filter based on an array of filters.
	 * @param filters 
	 * @param type 
	 */
	constructor(filters: VclFilter[], type: VclFilterType);
}

/**
 * TBS
 */
declare class VclQueryParams {
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Adds sorting fields that should be used when performing the query.
	 * @param fields 
	 * @param sortType 
	 */
	addSortFields(fields: any[], sortType: VclSortType): void;
	/**
	 * Clears the sorting fields.
	 */
	clearSortFields(): void;
	/**
	 * Adds a metadata field that should be used when performing the query.
	 * @param key 
	 * @param domain 
	 */
	addMetadataField(key: string, domain: VclMetadataDomain): void;
	/**
	 * Sets the filter to be applied when performing a query.
	 * @param filter 
	 */
	setFilter(filter: VclFilter): void;
	/**
	 * Sets the offset value to be used when performing a query.
	 * @param offset 
	 */
	setOffset(offset: number): void;
	/**
	 * Sets the page number to start from when performing a query.
	 * @param page 
	 */
	setPage(page: number): void;
	/**
	 * Sets the page size to be used when performing a query.
	 * @param pageSize 
	 */
	setPageSize(pageSize: number): void;
	/**
	 * Sets the fields to be returned when performing a query.
	 * @param fields 
	 */
	setFields(fields: any[]): void;
}

/**
 * Represents an object.
 */
declare class VclObject {
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Gets the value of the object.
	 * @param type 
	 */
	getValue(type: any): any[];
	/**
	 * Sets the value of the object.
	 * @param qname 
	 * @param value 
	 */
	setStringValue(qname: VclQName, value: string): void;
	/**
	 * Sets the value of the object.
	 * @param value 
	 */
	setValue(value: any): void;
}

/**
 * TBS
 */
declare interface VclQName {
	/**
	 * TBS
	 */
	equals(): boolean;
	/**
	 * TBS
	 */
	getLocalPart(): string;
	/**
	 * TBS
	 */
	getNamespaceURI(): string;
	/**
	 * TBS
	 */
	getPrefix(): string;
	/**
	 * TBS
	 * @param qNameAsString 
	 */
	valueOf(qNameAsString: string): VclQName;
}

/**
 * GregorianCalendar is a concrete subclass of Calendar and provides the standard calendar system used by most of the world.
 */
declare interface VclGregorianCalendar {
	/**
	 * Adds the specified (signed) amount of time to the given calendar field, based on the calendar's rules.
	 * @param field 
	 * @param amount 
	 */
	add(field: number, amount: number): string;
	/**
	 * Creates and returns a copy of this object.
	 */
	clone(): VclGregorianCalendar;
	/**
	 * Compares this GregorianCalendar to the specified Object.
	 * @param obj 
	 */
	equals(obj: any): boolean;
	/**
	 * Returns the maximum value that this calendar field could have, taking into consideration the given time value and the current values of the getFirstDayOfWeek, getMinimalDaysInFirstWeek, getGregorianChange and getTimeZone methods.
	 * @param field 
	 */
	getActualMaximum(field: number): number;
	/**
	 * Returns the minimum value that this calendar field could have, taking into consideration the given time value and the current values of the getFirstDayOfWeek, getMinimalDaysInFirstWeek, getGregorianChange and getTimeZone methods.
	 * @param field 
	 */
	getActualMinimum(field: number): number;
	/**
	 * Returns the highest minimum value for the given calendar field of this GregorianCalendar instance.
	 * @param field 
	 */
	getGreatestMinimum(field: number): number;
	/**
	 * Gets the Gregorian Calendar change date.
	 */
	getGregorianChange(): Date;
	/**
	 * Returns the lowest maximum value for the given calendar field of this GregorianCalendar instance.
	 * @param field 
	 */
	getLeastMaximum(field: number): number;
	/**
	 * Returns the maximum value for the given calendar field of this GregorianCalendar instance.
	 * @param field 
	 */
	getMaximum(field: number): number;
	/**
	 * Returns the minimum value for the given calendar field of this GregorianCalendar instance.
	 * @param field 
	 */
	getMinimum(field: number): number;
	/**
	 * Gets the time zone.
	 * @param field 
	 */
	getTimeZone(field: number): any;
	/**
	 * Determines if the given year is a leap year.
	 * @param year 
	 */
	isLeapYear(year: number): boolean;
	/**
	 * Adds or subtracts (up/down) a single unit of time on the given time field without changing larger fields.
	 * @param field 
	 * @param up 
	 */
	roll(field: number, up: boolean): void;
	/**
	 * Adds a signed amount to the specified calendar field without changing larger fields.
	 * @param field 
	 * @param amount 
	 */
	rollAmount(field: number, amount: number): void;
	/**
	 * Sets the GregorianCalendar change date.
	 * @param date 
	 */
	setGregorianChange(date: Date): void;
	/**
	 * Sets the time zone with the given time zone value.
	 * @param zone 
	 */
	setTimeZone(zone: any): void;
	/**
	 * Returns whether this Calendar represents a time after the time represented by the specified Object.
	 * @param when 
	 */
	after(when: any): boolean;
	/**
	 * Returns whether this Calendar represents a time before the time represented by the specified Object.
	 * @param when 
	 */
	before(when: any): boolean;
	/**
	 * Sets all the calendar field values and the time value (millisecond offset from the Epoch) of this Calendar undefined.
	 */
	clear(): void;
	/**
	 * Sets the given calendar field value and the time value (millisecond offset from the Epoch) of this Calendar undefined.
	 * @param field 
	 */
	clearField(field: number): void;
	/**
	 * Compares the time values (millisecond offsets from the Epoch) represented by two Calendar objects.
	 * @param anotherCalendar 
	 */
	compareTo(anotherCalendar: VclGregorianCalendar): number;
	/**
	 * Returns the value of the given calendar field.
	 * @param field 
	 */
	get(field: number): number;
	/**
	 * Returns an array of all locales for which the getInstance methods of this class can return localized instances.
	 */
	getAvailableLocales(): any[];
	/**
	 * Returns the string representation of the calendar field value in the given style and locale.
	 * @param field 
	 * @param style 
	 * @param locale 
	 */
	getDisplayName(field: number, style: number, locale: any): string;
	/**
	 * Returns a Date object representing this Calendar's time value (millisecond offset from the Epoch").
	 */
	getTime(): Date;
	/**
	 * Returns this Calendar's time value in milliseconds.
	 */
	getTimeInMillis(): number;
	/**
	 * Determines if the given calendar field has a value set, including cases that the value has been set by internal fields calculations triggered by a get method call.
	 * @param field 
	 */
	isSet(field: number): boolean;
	/**
	 * Sets this Calendar's time with the given Date.
	 * @param date 
	 */
	setTime(date: Date): void;
	/**
	 * Sets this Calendar's current time from the given long value.
	 * @param millis 
	 */
	setTimeInMillis(millis: number): void;
	/**
	 * Return a string representation of this calendar.
	 */
	toString(): string;
	/**
	 * Gets what the first day of the week is; e.g., SUNDAY in the U.S., MONDAY in France.
	 */
	getFirstDayOfWeek(): number;
	/**
	 * Sets what the first day of the week is; e.g., SUNDAY in the U.S., MONDAY in France.
	 * @param value 
	 */
	setFirstDayOfWeek(value: number): void;
	/**
	 * Tells whether date/time interpretation is to be lenient.
	 */
	isLenient(): boolean;
	/**
	 * Specifies whether or not date/time interpretation is to be lenient.
	 * @param lenient 
	 */
	setLenient(lenient: boolean): void;
	/**
	 * Gets what the minimal days required in the first week of the year are; e.g., if the first week is defined as one that contains the first day of the first month of a year, this method returns 1.
	 */
	getMinimalDaysInFirstWeek(): number;
	/**
	 * Sets what the minimal days required in the first week of the year are; For example, if the first week is defined as one that contains the first day of the first month of a year, call this method with value 1.
	 * @param value 
	 */
	setMinimalDaysInFirstWeek(value: number): void;
}

/**
 * TBS
 */
declare interface VclXMLGregorianCalendar {
	/**
	 * TBS
	 */
	getYear(): number;
	/**
	 * TBS
	 * @param value 
	 */
	setYear(value: number): void;
	/**
	 * TBS
	 */
	getMonth(): number;
	/**
	 * TBS
	 * @param value 
	 */
	setMonth(value: number): void;
	/**
	 * TBS
	 */
	getDay(): number;
	/**
	 * TBS
	 * @param value 
	 */
	setDay(value: number): void;
	/**
	 * TBS
	 */
	getHour(): number;
	/**
	 * TBS
	 * @param value 
	 */
	setHour(value: number): void;
	/**
	 * TBS
	 */
	getMinute(): number;
	/**
	 * TBS
	 * @param value 
	 */
	setMinute(value: number): void;
	/**
	 * TBS
	 */
	getSecond(): number;
	/**
	 * TBS
	 * @param value 
	 */
	setSecond(value: number): void;
	/**
	 * TBS
	 */
	getMillisecond(): number;
	/**
	 * TBS
	 * @param value 
	 */
	setMillisecond(value: number): void;
	/**
	 * TBS
	 */
	getTimezone(): number;
	/**
	 * TBS
	 * @param value 
	 */
	setTimezone(value: number): void;
	/**
	 * TBS
	 * @param value 
	 */
	addDuration(value: VclDuration): void;
	/**
	 * TBS
	 * @param timezone 
	 * @param aLocale 
	 * @param defaults 
	 */
	toGregorianCalendar(timezone: any, aLocale: any, defaults: VclXMLGregorianCalendar): VclGregorianCalendar;
}

/**
 * TBS
 */
declare class VclBigInteger {
	constructor();
	/**
	 * Default constructor.
	 * @param val 
	 */
	constructor(val: string);
}

/**
 * TBS
 */
declare interface VclDuration {
	/**
	 * TBS
	 */
	getYears(): number;
	/**
	 * TBS
	 */
	getMonths(): number;
	/**
	 * TBS
	 */
	getDays(): number;
	/**
	 * TBS
	 */
	getHours(): number;
	/**
	 * TBS
	 */
	getMinutes(): number;
	/**
	 * TBS
	 */
	getSeconds(): number;
	/**
	 * TBS
	 */
	getSign(): number;
}

/**
 * Factory used for creation of misc java native objects.
 */
declare class VclMiscObjectFactory {
	/**
	 * Creates a new instance of type BigInteger from string
	 * @param val 
	 */
	static createBigInteger(val: string): VclBigInteger;
	/**
	 * Creates a new instance of type QName
	 * @param localPart 
	 * @param namespaceURI 
	 * @param prefix 
	 */
	static createQName(localPart: string, namespaceURI: string, prefix: string): VclQName;
	/**
	 * Creates a new instance of type VclDuration
	 * @param durationInMilliSeconds 
	 */
	static createDuration(durationInMilliSeconds: number): VclDuration;
	/**
	 * Creates a new instance of type VclXMLGregorianCalendar
	 * @param year 
	 * @param month 
	 * @param day 
	 * @param hour 
	 * @param minute 
	 * @param second 
	 * @param millisecond 
	 * @param timezone 
	 */
	static createXmlGregorianCalendar(year: number, month: number, day: number, hour: number, minute: number, second: number, millisecond: number, timezone: number): VclXMLGregorianCalendar;
	/**
	 * Creates an instance of VclGregorianCalendar
	 */
	static createGregorianCalendar(): VclGregorianCalendar;
	/**
	 * Converts GregorianCalendar to XMLGregorianCalendar
	 * @param gregorianCalendar 
	 */
	static convertToXmlGregorianCalendar(gregorianCalendar: VclGregorianCalendar): VclXMLGregorianCalendar;
	/**
	 * Creates a new XMLGregorianCalendar from string
	 * @param lexicalRepresentation 
	 */
	static xmlGregorianCalendarFromString(lexicalRepresentation: string): VclXMLGregorianCalendar;
	/**
	 * Converts XMLGregorianCalendar to GregorianCalendar
	 * @param gregorianXmlCalendar 
	 */
	static convertToGregorianCalendar(gregorianXmlCalendar: VclXMLGregorianCalendar): VclGregorianCalendar;
	/**
	 * Generates a random UUID
	 */
	static generateUuid(): string;
}

/**
 * TBS
 */
declare class VclMediaTypeType {
	static readonly values: string[];
	static readonly value: string;
	static readonly ISO: VclMediaTypeType;
	static readonly FLOPPY: VclMediaTypeType;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclMediaTypeType;
}

/**
 * TBS
 */
declare class VclTimeUnitType {
	static readonly values: string[];
	static readonly value: string;
	static readonly SECOND: VclTimeUnitType;
	static readonly MINUTE: VclTimeUnitType;
	static readonly HOUR: VclTimeUnitType;
	static readonly DAY: VclTimeUnitType;
	static readonly WEEK: VclTimeUnitType;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclTimeUnitType;
}

/**
 * TBS
 */
declare class VclVirtualCpuTypeType {
	static readonly values: string[];
	static readonly value: string;
	static readonly VM_32: VclVirtualCpuTypeType;
	static readonly VM_64: VclVirtualCpuTypeType;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclVirtualCpuTypeType;
}

/**
 * TBS
 */
declare class VclResourceSharesLevelType {
	static readonly values: string[];
	static readonly value: string;
	static readonly LOW: VclResourceSharesLevelType;
	static readonly NORMAL: VclResourceSharesLevelType;
	static readonly HIGH: VclResourceSharesLevelType;
	static readonly CUSTOM: VclResourceSharesLevelType;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclResourceSharesLevelType;
}

/**
 * TBS
 */
declare class VclMediaStateType {
	static readonly values: string[];
	static readonly value: string;
	static readonly DISCONNECTED: VclMediaStateType;
	static readonly SERVER: VclMediaStateType;
	static readonly REMOTE: VclMediaStateType;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclMediaStateType;
}

/**
 * TBS
 */
declare class VclSmtpSecureModeType {
	static readonly values: string[];
	static readonly value: string;
	static readonly NONE: VclSmtpSecureModeType;
	static readonly START_TLS: VclSmtpSecureModeType;
	static readonly SSL: VclSmtpSecureModeType;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclSmtpSecureModeType;
}

/**
 * TBS
 */
declare class VclRemoteUriProbeResultStatus {
	static readonly values: string[];
	static readonly value: string;
	static readonly SUCCESS: VclRemoteUriProbeResultStatus;
	static readonly INVALID_URL_OR_PASSWORD: VclRemoteUriProbeResultStatus;
	static readonly TIMED_OUT: VclRemoteUriProbeResultStatus;
	static readonly HOST_NOT_FOUND: VclRemoteUriProbeResultStatus;
	static readonly RESOURCE_NOT_FOUND: VclRemoteUriProbeResultStatus;
	static readonly CERTIFICATE_ERROR: VclRemoteUriProbeResultStatus;
	static readonly UNKNOWN_ERROR: VclRemoteUriProbeResultStatus;
	/**
	 * TBS
	 * @param value 
	 */
	static getObject(value: string): VclRemoteUriProbeResultStatus;
}

/**
 * 
 * Parameters for creating or updating an independent disk.
 * 
 */
declare class VclDiskCreateParams {
	locality: VclReference;
	disk: VclDiskParams;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Type for a single fileDescriptor query result in records format.
 * 
 */
declare class VclQueryResultFileDescriptorRecord {
	serviceNamespace: string;
	apiDefinition: string;
	apiName: string;
	apiNamespace: string;
	apiVendor: string;
	fileMimeType: string;
	fileUrl: string;
	serviceName: string;
	serviceVendor: string;
	service: string;
	name: string;
	href: string;
	readonly otherAttributes: VclMap;
	metadata: VclMetaDataEntries;
	id: string;
	type: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents password policy settings for this organization.
 * 
 */
declare class VclOrgPasswordPolicySettings {
	accountLockoutEnabled: boolean;
	invalidLoginsBeforeLockout: number;
	accountLockoutIntervalMinutes: number;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Specifies access controls for a resource.
 * 
 */
declare class VclControlAccessParams {
	isSharedToEveryone: boolean;
	everyoneAccessLevel: string;
	accessSettings: VclAccessSettings;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Hardware parameter customization applied to a virtual machine in a vApp when instantiated.
 * 
 */
declare class VclInstantiateVmHardwareCustomizationParams {
	readonly disk: VclObjectList;
	numberOfCpus: number;
	coresPerSocket: number;
	memorySize: number;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Upload socket for keystore.
 * 
 */
declare class VclKeystoreUploadSocket {
	task: VclTaskParams;
	uploadLocation: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Type for a single adminVAppNetwork query result in records format.
 * 
 */
declare class VclQueryResultAdminVAppNetworkRecord {
	isBusy: boolean;
	org: string;
	gateway: string;
	netmask: string;
	subnetPrefixLength: number;
	dns1: string;
	dns2: string;
	dnsSuffix: string;
	isIpScopeInherited: boolean;
	vApp: string;
	vappName: string;
	name: string;
	href: string;
	readonly otherAttributes: VclMap;
	metadata: VclMetaDataEntries;
	id: string;
	type: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Contains key/value pair as property.
 * 
 */
declare class VclProperty {
	value: string;
	key: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * List of organization vDCs backed by this Provider vDC.
 * 
 */
declare class VclVdcReferences {
	readonly vdcReference: VclObjectList;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Instantiation parameters for a VM in a vApp.
 * 
 */
declare class VclInstantiateVmParams {
	hardwareCustomization: VclInstantiateVmHardwareCustomizationParams;
	vdcStorageProfile: VclReference;
	networkConnectionSection: VclNetworkConnectionSection;
	computerName: string;
	id: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Parameters for a captureVapp request.
 * 
 */
declare class VclCaptureVAppParams {
	vdcStorageProfile: VclReference;
	targetCatalogItem: VclReference;
	readonly section: VclAbstractObjectSet;
	source: VclReference;
	description: string;
	name: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents an existing VM template from a Catalog.
 * Internal use only.
 * 
 */
declare class VclSourcedVmTemplateParams {
	storageProfile: VclReference;
	localityParams: VclLocalityParams;
	vmGeneralParams: VclVmGeneralParams;
	vmCapabilities: VclVmCapabilities;
	vmTemplateInstantiationParams: VclInstantiationParams;
	source: VclReference;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * A VM affinity rule to be
 * applied to two or more VMs. A VM-VM affinity rule specifies whether
 * selected individual virtual machines should run on the same host or
 * be kept on separate hosts.
 * An affinity rule tries to keep the
 * specified virtual machines together on the same host while an
 * anti-affinity rule tries to keep the specified virtual machines
 * apart.
 * 
 */
declare class VclVmAffinityRule {
	vmReferences: VclVms;
	scope: string;
	isEnabled: boolean;
	isMandatory: boolean;
	polarity: string;
	name: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * AMQP Component config entry.
 * 
 */
declare class VclAmqpConfigEntry {
	value: string;
	key: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents an independent disk.
 * 
 */
declare class VclDiskParams {
	storageProfile: VclReference;
	busSubType: string;
	busType: string;
	iops: number;
	owner: VclOwner;
	size: number;
	files: VclFilesList;
	status: number;
	description: string;
	name: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Parameters for an insertMedia or ejectMedia request.
 * 
 */
declare class VclMediaInsertOrEjectParams {
	media: VclReference;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Container for references to ResourceEntity objects in this vDC.
 * 
 */
declare class VclResourceEntities {
	readonly resourceEntity: VclObjectList;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * The reference to a HardDiskAdapter element.
 * 
 */
declare class VclHardDiskAdapterReference {
	ref: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * OAuth Key Configuration
 * 
 */
declare class VclOAuthKeyConfiguration {
	expirationDate: VclXMLGregorianCalendar;
	keyId: string;
	algorithm: string;
	key: string;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * TBS
 */
declare class VclQueryResultAdminOrgVdcTemplateRecord {
	tenantVisibleDescription: string;
	tenantVisibleName: string;
	description: string;
	name: string;
	href: string;
	readonly otherAttributes: VclMap;
	metadata: VclMetaDataEntries;
	id: string;
	type: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Describes the administrator view of a from-the-cloud tunnel
 * 
 */
declare class VclAdminFromCloudTunnel {
	tunnelEndPoint: string;
	destinationId: string;
	trafficType: string;
	sourceId: string;
	endpointTag: string;
	description: string;
	name: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Parameters for creating an event.
 * 
 */
declare class VclEvent {
	serviceNamespace: string;
	user: VclReference;
	eventProperties: VclEventProperties;
	typeFull: string;
	owner: VclReference;
	success: boolean;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * A collection of simple metrics representing real time usage statistics
 * 
 */
declare class VclCurrentUsage {
	readonly metric: VclObjectList;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Type for a single adminFileDescriptor query result in records format.
 * 
 */
declare class VclQueryResultAdminFileDescriptorRecord {
	serviceNamespace: string;
	apiDefinition: string;
	apiName: string;
	apiNamespace: string;
	apiVendor: string;
	fileMimeType: string;
	fileUrl: string;
	serviceName: string;
	serviceVendor: string;
	service: string;
	name: string;
	href: string;
	readonly otherAttributes: VclMap;
	metadata: VclMetaDataEntries;
	id: string;
	type: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents a gateway.
 * 
 */
declare class VclGatewayParams {
	gatewayBackingRef: VclGatewayBackingRef;
	configuration: VclGatewayConfiguration;
	status: number;
	description: string;
	name: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Type for a single adminUser query result in records format.
 * 
 */
declare class VclQueryResultAdminUserRecord {
	isEnabled: boolean;
	org: string;
	fullName: string;
	identityProviderType: string;
	numberOfDeployedVMs: number;
	numberOfStoredVMs: number;
	deployedVMQuota: number;
	deployedVMQuotaRank: number;
	isLdapUser: boolean;
	storedVMQuota: number;
	storedVMQuotaRank: number;
	name: string;
	href: string;
	readonly otherAttributes: VclMap;
	metadata: VclMetaDataEntries;
	id: string;
	type: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Type for a single service query result in records format.
 * 
 */
declare class VclQueryResultServiceRecord {
	vendor: string;
	namespace: string;
	name: string;
	href: string;
	readonly otherAttributes: VclMap;
	metadata: VclMetaDataEntries;
	id: string;
	type: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents the capacity of a given resource.
 * 
 */
declare class VclCapacity {
	limit: number;
	allocated: number;
	units: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents federation settings for identity federation.
 * 
 */
declare class VclOrgFederationSettings {
	enabled: boolean;
	roleAttributeName: string;
	sAMLMetadata: string;
	certificateExpiration: VclXMLGregorianCalendar;
	samlSPEntityId: string;
	samlAttributeMapping: VclSamlAttributeMapping;
	samlSPKeyAndCertificateChain: VclSamlSPKeyAndCertificateChain;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents the results of a
 * GET /vApp/vm-{id}/complianceResult
 * request. Empty if such a request has never been made.
 * 
 */
declare class VclComplianceResult {
	complianceStatus: string;
	complianceStatusMessage: string;
	complianceCheckTime: VclXMLGregorianCalendar;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents default lease durations and policies for this
 * organization.
 * 
 */
declare class VclOrgLeaseSettings {
	deploymentLeaseSeconds: number;
	deleteOnStorageLeaseExpiration: boolean;
	storageLeaseSeconds: number;
	powerOffOnRuntimeLeaseExpiration: boolean;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents gateway IPsec VPN service.
 * 
 */
declare class VclGatewayIpsecVpnService {
	readonly endpoint: VclObjectList;
	readonly tunnel: VclObjectList;
	isEnabled: boolean;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * TBS
 */
declare class VclRouterInfo {
	externalIp: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Type for a single media query result in records format.
 * 
 */
declare class VclQueryResultMediaRecord {
	version: number;
	isBusy: boolean;
	org: string;
	creationDate: VclXMLGregorianCalendar;
	ownerName: string;
	isPublished: boolean;
	vdc: string;
	vdcName: string;
	storageProfileName: string;
	catalog: string;
	catalogItem: string;
	catalogName: string;
	storageB: number;
	lastSuccessfulSync: VclXMLGregorianCalendar;
	owner: string;
	status: string;
	name: string;
	href: string;
	readonly otherAttributes: VclMap;
	metadata: VclMetaDataEntries;
	id: string;
	type: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Internal use only.
 * 
 */
declare class VclCbmData {
	vimServer: VclReference;
	backingRef: string;
	backingRefType: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * vSphere resource pool information.
 * 
 */
declare class VclResourcePool {
	moRef: string;
	vimObjectType: string;
	dataStoreRefs: VclVimObjectRefs;
	description: string;
	name: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Parameters for updating resource pools backing a Provider vDC.
 * 
 */
declare class VclUpdateResourcePoolSetParams {
	readonly deleteItem: VclObjectList;
	readonly addItem: VclObjectList;
	description: string;
	name: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * TBS
 */
declare class VclImportVmParams {
	vdcStorageProfile: VclReference;
	computerName: string;
	vAppScopedLocalId: string;
	vdc: VclReference;
	vmName: string;
	vmMoRef: string;
	readonly importedDisk: VclObjectList;
	sourceMove: boolean;
	description: string;
	name: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Parameters for initializing an upload session for an extension
 * service localization bundle.
 * 
 */
declare class VclBundleUploadParams {
	serviceNamespace: string;
	fileSize: number;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents a list of extension service API definitions.
 * 
 */
declare class VclAdminApiDefinitions {
	readonly apiDefinition: VclObjectList;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Container for VMWVdcTemplateType
 * 
 */
declare class VclVMWVdcTemplates {
	readonly vMWVdcTemplate: VclObjectList;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Specification for a VDC template using the ReservationPool allocation model.
 * 
 */
declare class VclVMWReservationPoolVdcTemplateSpecification {
	cpuAllocationMhz: number;
	memoryAllocationMB: number;
	readonly storageProfile: VclObjectList;
	nicQuota: number;
	vmQuota: number;
	provisionedNetworkQuota: number;
	gatewayConfiguration: VclVdcTemplateSpecificationGatewayConfiguration;
	thinProvision: boolean;
	networkPoolReference: VclReference;
	fastProvisioningEnabled: boolean;
	automaticNetworkPoolReference: VclAutomaticNetworkPoolReference;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * A reference to a Provider VDC.
 * 
 */
declare class VclVMWVdcTemplateProviderVdcSpecification {
	readonly binding: VclObjectList;
	href: string;
	name: string;
	id: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Parameters to create or update a Provider vDC.
 * 
 */
declare class VclVMWProviderVdcParams {
	isEnabled: boolean;
	readonly storageProfile: VclList;
	resourcePoolRefs: VclVimObjectRefs;
	readonly vimServer: VclObjectList;
	highestSupportedHardwareVersion: string;
	hostRefs: VclHostObjectRefs;
	defaultUsername: string;
	nsxTManagerReference: VclReference;
	vxlanNetworkPool: VclReference;
	defaultPassword: string;
	description: string;
	name: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * List of VM groups in this resource pool.
 * 
 */
declare class VclVMWVmGroups {
	readonly vmGroup: VclObjectList;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Parameters for an authorization check request.
 * 
 */
declare class VclAuthorizationCheckParams {
	user: VclReference;
	httpMethod: string;
	uRL: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents a service link defined by an extension service.
 * 
 */
declare class VclAdminServiceLinkParams {
	rel: string;
	linkHref: string;
	mimeType: string;
	resourceId: string;
	resourceType: string;
	externalResourceId: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Response to an authorization check request.
 * 
 */
declare class VclAuthorizationCheckResponse {
	isAuthorized: boolean;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Specification for a VDC template using the AllocationPool allocation model.
 * 
 */
declare class VclVMWAllocationPoolVdcTemplateSpecification {
	cpuAllocationMhz: number;
	memoryAllocationMB: number;
	vCpuInMhz: number;
	cpuGuaranteedPercentage: number;
	memoryGuaranteedPercentage: number;
	readonly storageProfile: VclObjectList;
	nicQuota: number;
	vmQuota: number;
	provisionedNetworkQuota: number;
	gatewayConfiguration: VclVdcTemplateSpecificationGatewayConfiguration;
	thinProvision: boolean;
	networkPoolReference: VclReference;
	fastProvisioningEnabled: boolean;
	automaticNetworkPoolReference: VclAutomaticNetworkPoolReference;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Report of License usage metrics.
 * 
 */
declare class VclLicenseMetricsInfo {
	physicalMemoryUsed: number;
	physicalSocketCount: number;
	publishingToRemoteSites: boolean;
	subscribingToRemoteSites: boolean;
	vRAM: number;
	vCPU: number;
	runningVMs: number;
	availablePhysicalMemory: number;
	lastUpdate: VclXMLGregorianCalendar;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents system email settings.
 * 
 */
declare class VclEmailSettings {
	senderEmailAddress: string;
	emailSubjectPrefix: string;
	alertEmailToAllAdmins: boolean;
	alertEmailTo: string;
	smtpSettings: VclSmtpSettings;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents an access type in an extension service ACL rule.
 * 
 */
declare class VclAclAccess {
	access: string;
	entity: VclReference;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Container for extension service API filters.
 * 
 */
declare class VclApiFilters {
	readonly apiFilter: VclObjectList;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Container for references to VM Groups in the system that have identical names. Affinity
 * between a VM Group and a host is based on the group name, so all VM groups with identical
 * names are subject to the same set of affinity rules.
 * 
 */
declare class VclVMWNamedVmGroup {
	vMWVmGroupReferences: VclVMWVmGroupReferences;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents a single licensing data collection point.
 * 
 */
declare class VclLicensingReportSample {
	observationDate: VclXMLGregorianCalendar;
	managedServerMetrics: VclManagedServerMetrics;
	virtualMachineMetrics: VclVirtualMachineMetrics;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents a single set of virtual machine metrics.
 * 
 */
declare class VclLicensingVirtualMachine {
	cpu: number;
	memory: number;
	vimObjectRef: VclVimObjectRef;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Base extension network pool type.
 * 
 */
declare class VclVMWNetworkPoolParams {
	files: VclFilesList;
	status: number;
	description: string;
	name: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * A list of Provider vDCs to merge with the target Provider vDC.
 * 
 */
declare class VclProviderVdcMergeParams {
	readonly providerVdcReference: VclObjectList;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents the settings related to Password Policy.
 * 
 */
declare class VclSystemPasswordPolicySettings {
	accountLockoutEnabled: boolean;
	invalidLoginsBeforeLockout: number;
	accountLockoutIntervalMinutes: number;
	adminAccountLockoutEnabled: boolean;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Parameters for a register or unregister Lookup Service request.
 * If LookupServiceUrl is present and not empty, the request
 * registers a lookup service. If LookupServiceUrl is missing
 * or empty, the request unregisters a lookup service.
 * 
 */
declare class VclLookupServiceParams {
	vCDUrl: string;
	password: string;
	userName: string;
	lookupServiceUrl: string;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents an extension service ACL rule.
 * 
 */
declare class VclAclRuleParams {
	serviceResourceAccess: VclAclAccess;
	principalAccess: VclAclAccess;
	organizationAccess: VclAclAccess;
	description: string;
	name: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents credentials to a vShield Manager server.
 * 
 */
declare class VclShieldManagerParams {
	networkProviderScope: string;
	associatedVimServer: VclReference;
	controlVmResourcePoolVcPath: string;
	controlVmDatastoreName: string;
	controlVmManagementInterfaceName: string;
	url: string;
	username: string;
	password: string;
	description: string;
	name: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents the moref and the type of a vSphere object.
 * 
 */
declare class VclVimObjectRef {
	vimServerRef: VclReference;
	moRef: string;
	vimObjectType: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents the vSphere Web Client URL of a VIM object.
 * 
 */
declare class VclVSphereWebClientUrl {
	uRL: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents an extension service.
 * 
 */
declare class VclAdminServiceParams {
	enabled: boolean;
	vendor: string;
	routingKey: string;
	exchange: string;
	authorizationEnabled: boolean;
	apiFilters: VclApiFilters;
	serviceLinks: VclAdminServiceLinks;
	apiDefinitions: VclAdminApiDefinitions;
	resourceClasses: VclResourceClasses;
	namespace: string;
	priority: number;
	description: string;
	name: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Parameters to POST with a blocking task action.
 * 
 */
declare class VclBlockingTaskOperationParams {
	message: string;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents a
 * registered object extension.
 * 
 */
declare class VclObjectExtension {
	contentType: string;
	enabled: boolean;
	vendor: string;
	exchange: string;
	selectors: VclObject;
	namespace: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * List of host groups in this resource pool.
 * 
 */
declare class VclVMWHostGroups {
	readonly hostGroup: VclObjectList;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * A list of service links for extension services.
 * 
 */
declare class VclAdminServiceLinks {
	readonly serviceLink: VclObjectList;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * List of VimObjectRef elements.
 * 
 */
declare class VclVimObjectRefs {
	readonly vimObjectRef: VclObjectList;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Upload information for an extension service localization bundle.
 * 
 */
declare class VclBundleUploadSocket {
	uploadLocation: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * HostGroup identifies a list of references to hosts in this resource pool to which
 * VM-Host affinity rules can apply. VM-Host affinity rules determine placement of
 * virtual machines on hosts in a cluster.
 * 
 */
declare class VclVMWHostGroup {
	hosts: VclVMWHostReferences;
	description: string;
	name: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents the settings for the AMQP broker.
 * 
 */
declare class VclAmqpSettings {
	amqpHost: string;
	amqpPort: number;
	amqpUsername: string;
	amqpPassword: string;
	amqpExchange: string;
	amqpVHost: string;
	amqpUseSSL: boolean;
	amqpSslAcceptAll: boolean;
	amqpPrefix: string;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * A reference to a VDC template.
 * 
 */
declare class VclVMWVdcTemplateParams {
	readonly providerVdcReference: VclObjectList;
	vdcTemplateSpecification: VclAbstractValueObject;
	tenantName: string;
	tenantDescription: string;
	description: string;
	name: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Specifies default operation limits settings for all organizations in
 * the system. An operation is defined as resource-intensive if it
 * returns a Task object. The default value for all operation limits
 * settings is 0, which specifies no limit
 * 
 */
declare class VclOperationLimitsSettings {
	runningPerUser: number;
	runningPerOrg: number;
	queuedOperationsPerUser: number;
	queuedOperationsPerOrg: number;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents a list of available provider vDCs.
 * 
 */
declare class VclVMWProviderVdcReferences {
	readonly providerVdcReference: VclObjectList;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Captures a single set of metrics for a managed server for a given sample.
 * 
 */
declare class VclLicensingManagedServer {
	memoryInstalled: number;
	cpu: number;
	vimObjectRef: VclVimObjectRef;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * TBS
 */
declare class VclSerialPortConfig {
	startConnected: boolean;
	allowGuestControl: boolean;
	direction: string;
	serviceUri: string;
	yieldOnPoll: boolean;
	status: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Defines the hostname and connection details for system
 * LDAP service.
 * 
 */
declare class VclLdapSettings {
	pageSize: number;
	isSsl: boolean;
	isSslAcceptAll: boolean;
	customTruststore: string;
	pagedSearchDisabled: boolean;
	maxResults: number;
	maxUserGroups: number;
	searchBase: string;
	authenticationMechanism: string;
	groupSearchBase: string;
	isGroupSearchBaseEnabled: boolean;
	connectorType: string;
	userAttributes: VclLdapUserAttributes;
	groupAttributes: VclLdapGroupAttributes;
	useExternalKerberos: boolean;
	realm: string;
	password: string;
	userName: string;
	port: number;
	hostName: string;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents an extension service API definition.
 * 
 */
declare class VclAdminApiDefinitionParams {
	apiVendor: string;
	entryPoint: string;
	supportedApiVersions: VclExtensionVersions;
	files: VclAdminFileDescriptors;
	namespace: string;
	description: string;
	name: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents a set of resource pools that back an organization
 * vDC.
 * 
 */
declare class VclOrganizationResourcePoolSet {
	readonly resourcePoolVimObjectRef: VclObjectList;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * A value to be applied to a system configuration setting.
 * 
 */
declare class VclSystemConfigurationSettingValue {
	value: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Global Kerberos settings.
 * 
 */
declare class VclKerberosSettings {
	allowLowerCaseRealms: boolean;
	readonly realm: VclObjectList;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Internal use only.
 * 
 */
declare class VclUberAdminSettings {
	debugMode: boolean;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Catalog settings for this cloud
 * 
 */
declare class VclCatalogSettings {
	isSyncEnabled: boolean;
	syncStartDate: VclXMLGregorianCalendar;
	syncStopDate: VclXMLGregorianCalendar;
	refreshInterval: number;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents a resource class defined by an external service.
 * 
 */
declare class VclResourceClassParams {
	mimeType: string;
	urlTemplate: string;
	nid: string;
	urnPattern: string;
	serviceResources: VclServiceResources;
	resourceClassActions: VclResourceClassActions;
	description: string;
	name: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Information that allows vCloud Director to connect to a
 * vSphere lookup service.
 * 
 */
declare class VclLookupServiceSettings {
	lookupServiceUrl: string;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Parameters for preparing an ESX/ESXi host.
 * 
 */
declare class VclPrepareHostParams {
	username: string;
	password: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * TBS
 */
declare class VclManagedServerMetrics {
	readonly managedServer: VclObjectList;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents a list of available hosts.
 * 
 */
declare class VclVMWHostReferences {
	readonly hostReference: VclObjectList;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents a list of VDC templates.
 * 
 */
declare class VclVMWVdcTemplateList {
	readonly vMWVdcTemplate: VclObjectList;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * List of references to registered extension services.
 * 
 */
declare class VclExtensionServices {
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * TBS
 */
declare class VclNsxTManagers {
	readonly nsxTManager: VclObjectList;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents the settings related to blocking tasks.
 * 
 */
declare class VclBlockingTaskSettings {
	timeoutAction: string;
	blockingTaskOperations: VclTaskOperationList;
	timeoutInMilliseconds: number;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Parameters to import a virtual machine into an existing VApp.
 * 
 */
declare class VclImportVmIntoExistingVAppParams {
	vdcStorageProfile: VclReference;
	computerName: string;
	vAppScopedLocalId: string;
	vApp: VclReference;
	vmName: string;
	vmMoRef: string;
	readonly importedDisk: VclObjectList;
	sourceMove: boolean;
	vmDescription: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Parameters to import a virtual machine as a vApp template.
 * 
 */
declare class VclImportVmAsVAppTemplateParams {
	vdcStorageProfile: VclReference;
	computerName: string;
	vAppScopedLocalId: string;
	vdc: VclReference;
	goldMaster: boolean;
	catalog: VclReference;
	vmName: string;
	vmMoRef: string;
	sourceMove: boolean;
	description: string;
	name: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents enumeration of system configuration settings.
 * 
 */
declare class VclSystemConfigurationSettings {
	readonly settings: VclObjectList;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * A list of extension service resources.
 * 
 */
declare class VclServiceResources {
	readonly serviceResource: VclObjectList;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents a vSphere VM information.
 * 
 */
declare class VclVmVimInfo {
	vmVimObjectRef: VclVimObjectRef;
	datastoreVimObjectRef: VclVimObjectRef;
	readonly vmDiskDatastores: VclObjectList;
	hostVimObjectRef: VclVimObjectRef;
	virtualDisksMaxChainLength: number;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents the settings related to Notifications.
 * 
 */
declare class VclNotificationsSettings {
	enableNotifications: boolean;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * vCloud Director AMQP notification in XML format. This
 * notification format has been deprecated in favor of a JSON
 * format. It might be removed in a future release.
 * 
 */
declare class VclNotification {
	eventId: string;
	readonly entityLink: VclObjectList;
	operationSuccess: boolean;
	timestamp: VclXMLGregorianCalendar;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * References to VM Groups that have identical names.
 * 
 */
declare class VclVMWVmGroupReferences {
	readonly vmGroupReference: VclObjectList;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents a collection of StrandedItemVimObject.
 * 
 */
declare class VclStrandedItemVimObjects {
	readonly strandedItemVimObject: VclObjectList;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * A list of extension service resource class actions.
 * 
 */
declare class VclResourceClassActions {
	readonly resourceClassAction: VclObjectList;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * The SMTP server for email notifications and alerts.
 * 
 */
declare class VclSmtpSettings {
	ssl: boolean;
	useAuthentication: boolean;
	smtpServerName: string;
	smtpServerPort: number;
	smtpSecureMode: VclSmtpSecureModeType;
	sslTrustStore: string;
	password: string;
	userName: string;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents parameters to register vCloud Director
 * with Component Manager.
 * 
 */
declare class VclComponentManagerSettings {
	cMUrl: string;
	sSOAdminUsername: string;
	sSOAdminPassword: string;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * This is used by the API to update and retrieve site license specific information.
 * 
 */
declare class VclLicense {
	serialNumber: string;
	licensedVMCount: number;
	expirationDate: VclXMLGregorianCalendar;
	validSerial: boolean;
	persisted: boolean;
	publishingToRemoteSitesFeature: boolean;
	subscribingToRemoteSitesFeature: boolean;
	licenseMetricsInfo: VclLicenseMetricsInfo;
	expired: boolean;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * TBS
 */
declare class VclNsxTManager {
	url: string;
	username: string;
	password: string;
	description: string;
	name: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * VmGroup identifies a group of VMs in this resource pool to which VM-Host affinity
 * rules can be applied. VM-Host affinity rules control placement of virtual machines
 * on hosts in a resource pool.
 * 
 */
declare class VclVMWVmGroup {
	vmCount: number;
	description: string;
	name: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Kerberos realm information.
 * 
 */
declare class VclRealm {
	kdc: string;
	readonly domain: VclList;
	name: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents a vCenter storage profile.
 * 
 */
declare class VclVMWStorageProfile {
	moRef: string;
	vimObjectType: string;
	freeStorageMb: any;
	totalStorageMb: any;
	dataStoreRefs: VclVimObjectRefs;
	name: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Parameters to update a blocking task with a new timeout.
 * 
 */
declare class VclBlockingTaskUpdateProgressParams {
	timeoutValueInMilliseconds: number;
	message: string;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * TBS
 */
declare class VclSelector {
	name: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents a single system configuration setting.
 * 
 */
declare class VclSystemConfigurationSetting {
	section: string;
	name: string;
	value: string;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents a network pool backed by one or more vSphere VLANs.
 * 
 */
declare class VclVlanPoolParams {
	readonly vlanRange: VclObjectList;
	vimSwitchRef: VclVimObjectRef;
	usedNetworksCount: number;
	promiscuousMode: boolean;
	files: VclFilesList;
	status: number;
	description: string;
	name: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents a list of available external networks.
 * 
 */
declare class VclVMWExternalNetworkReferences {
	readonly externalNetworkReference: VclObjectList;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents vCenter server information.
 * 
 */
declare class VclVimServerParams {
	computeProviderScope: string;
	isEnabled: boolean;
	uuid: string;
	vcVersion: string;
	rootFolder: string;
	isConnected: boolean;
	shieldManagerHost: string;
	shieldManagerUserName: string;
	vcProxy: string;
	useVsphereService: boolean;
	vsphereWebClientServerUrl: string;
	url: string;
	username: string;
	password: string;
	description: string;
	name: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * VmHostAffinityRules in this resource pool.
 * 
 */
declare class VclVMWVmHostAffinityRules {
	readonly vmHostAffinityRule: VclObjectList;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Container for an entity and its list of rights.
 * 
 */
declare class VclEntityRights {
	rights: VclRightRefs;
	reference: VclObject;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * External network type.
 * 
 */
declare class VclVMWExternalNetworkParams {
	vimPortGroupRef: VclVimObjectRef;
	vimPortGroupRefs: VclVimObjectRefs;
	configuration: VclNetworkConfiguration;
	description: string;
	name: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * TBS
 */
declare class VclVMWVdcTemplateSpecification {
	readonly storageProfile: VclObjectList;
	nicQuota: number;
	vmQuota: number;
	provisionedNetworkQuota: number;
	gatewayConfiguration: VclVdcTemplateSpecificationGatewayConfiguration;
	thinProvision: boolean;
	networkPoolReference: VclReference;
	fastProvisioningEnabled: boolean;
	automaticNetworkPoolReference: VclAutomaticNetworkPoolReference;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents a group of vCenter storage profiles.
 * 
 */
declare class VclVMWStorageProfiles {
	readonly vMWStorageProfile: VclObjectList;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * A list of extension service resource class action ACL rules.
 * 
 */
declare class VclAclRules {
	readonly aclRule: VclObjectList;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Parameters that control virtual machine migration.
 * 
 */
declare class VclMigrateParams {
	readonly vmRef: VclObjectList;
	resourcePoolRef: VclVimObjectRef;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * TBS
 */
declare class VclVimObjectRefList {
	vimObjectRefs: VclVimObjectRefs;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Upload socket for vCenter trust store.
 * 
 */
declare class VclVcTrustStoreUploadSocket {
	task: VclTaskParams;
	uploadLocation: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents a list of available network pools.
 * 
 */
declare class VclVMWNetworkPoolReferences {
	readonly networkPoolReference: VclObjectList;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Container for references to entities.
 * 
 */
declare class VclEntityReferences {
	readonly reference: VclObjectSet;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * A list of resource classes defined by an extension service.
 * 
 */
declare class VclResourceClasses {
	readonly resourceClass: VclObjectList;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Defines how LDAP attributes are used when importing a user.
 * 
 */
declare class VclLdapUserAttributes {
	surname: string;
	givenName: string;
	objectIdentifier: string;
	fullName: string;
	telephone: string;
	objectClass: string;
	groupMembershipIdentifier: string;
	groupBackLinkIdentifier: string;
	userName: string;
	email: string;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents parameters to import media from vSphere.
 * 
 */
declare class VclImportMediaParams {
	sourcePath: string;
	vdcStorageProfile: VclReference;
	vdc: VclReference;
	datastoreMoRef: string;
	catalog: VclReference;
	sourceMove: boolean;
	description: string;
	name: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Information used to customize the vCloud Director Web console.
 * Include provider-specific text and values.
 * 
 */
declare class VclBrandingSettings {
	companyName: string;
	loginPageCustomizationTheme: any;
	theme: string;
	previewCustomTheme: any;
	finalCustomTheme: any;
	aboutCompanyUrl: string;
	supportUrl: string;
	signUpUrl: string;
	forgotUserNameOrPasswordURL: string;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Parameters to import a virtual machine as a vApp.
 * 
 */
declare class VclImportVmAsVAppParams {
	vdcStorageProfile: VclReference;
	computerName: string;
	vAppScopedLocalId: string;
	vdc: VclReference;
	vmName: string;
	vmMoRef: string;
	readonly importedDisk: VclObjectList;
	sourceMove: boolean;
	instantiationParams: VclInstantiationParams;
	vAppParent: VclReference;
	deploy: boolean;
	powerOn: boolean;
	description: string;
	name: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Container for ReferenceType elements that reference the
 * predefined RightType objects.
 * 
 */
declare class VclRightRefs {
	readonly right: VclObjectList;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * List of HostObjectRefType elements.
 * 
 */
declare class VclHostObjectRefs {
	readonly hostObjectRef: VclObjectList;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents a list of extension service API versions.
 * 
 */
declare class VclExtensionVersions {
	readonly version: VclList;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Parameters for request to add or remove Provider vDC storage
 * profiles.
 * 
 */
declare class VclUpdateProviderVdcStorageProfilesParams {
	readonly addStorageProfile: VclList;
	readonly removeStorageProfile: VclObjectList;
	description: string;
	name: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * A list of vCenter Resource Pools that are valid for adoption by VDCs.
 * 
 */
declare class VclVMWDiscoveredResourcePools {
	readonly vMWDiscoveredResourcePool: VclObjectList;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Range of integers.
 * 
 */
declare class VclNumericRange {
	end: number;
	start: number;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Defines the HTTP methods allowed on a URL pattern
 * associated with an extension service resource class.
 * 
 */
declare class VclResourceClassActionParams {
	urlPattern: string;
	httpMethod: string;
	aclRules: VclAclRules;
	description: string;
	name: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents a list of datastores.
 * 
 */
declare class VclDatastores {
	readonly datastore: VclObjectList;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * TBS
 */
declare class VclVirtualMachineMetrics {
	readonly virtualMachine: VclObjectList;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Container for EntityRights elements.
 * Each element contains a reference to an entity and a list of user rights
 * that the entity supports.
 * 
 */
declare class VclUserEntityRights {
	readonly entityRights: VclObjectList;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * A reference to a host in vCenter inventory.
 * 
 */
declare class VclHostObjectRef {
	username: string;
	password: string;
	vimServerRef: VclReference;
	moRef: string;
	vimObjectType: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * A list of extension service API definition file descriptors.
 * 
 */
declare class VclAdminFileDescriptors {
	readonly fileDescriptor: VclObjectList;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents a list of licensing reports.
 * 
 */
declare class VclLicensingReportList {
	readonly report: VclObjectList;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents backing resource pool set
 * 
 */
declare class VclVMWProviderVdcResourcePoolSet {
	readonly vMWProviderVdcResourcePool: VclObjectList;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * A reference to a virtual machine in vCenter inventory.
 * 
 */
declare class VclVmObjectRef {
	name: string;
	vimServerRef: VclReference;
	moRef: string;
	vimObjectType: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Parameters for updating right collection.
 * 
 */
declare class VclUpdateRightsParams {
	readonly addRight: VclObjectList;
	readonly removeRight: VclList;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Parameters for updating the vCenter truststore.
 * 
 */
declare class VclVcTrustStoreUpdateParams {
	password: string;
	type: string;
	fileSize: number;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * TBS
 */
declare class VclSelectors {
	readonly selector: VclObjectList;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents system-level settings.
 * 
 */
declare class VclSystemSettings {
	generalSettings: VclGeneralSettings;
	notificationsSettings: VclNotificationsSettings;
	ldapSettings: VclLdapSettings;
	amqpSettings: VclAmqpSettings;
	emailSettings: VclEmailSettings;
	license: VclLicense;
	brandingSettings: VclBrandingSettings;
	blockingTaskSettings: VclBlockingTaskSettings;
	passwordPolicySettings: VclSystemPasswordPolicySettings;
	kerberosSettings: VclKerberosSettings;
	lookupServiceSettings: VclLookupServiceSettings;
	catalogSettings: VclCatalogSettings;
	operationLimitsSettings: VclOperationLimitsSettings;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents a Resource Pool that is a candidate for adoption by a VDC.
 * 
 */
declare class VclVMWDiscoveredResourcePool {
	resourcePoolVimObjectRef: VclVimObjectRef;
	validCandidate: boolean;
	name: string;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * A Binding pairs a Name element that contains a user-specified identifier
 * in URN format with a Value element that contains a reference to an object.
 * The Name can then be used anywhere in the request where a reference to that
 * type of object is allowed. For example, when specifying multiple Provider
 * VDCs in a VMWVdcTemplate, create a Binding where the Value is a reference
 * to an external network in a candidate Provider VDC, then use the Name from
 * that binding in place of the href attribute required by the Network element
 * in the GatewayConfiguration of the VdcTemplateSpecification. When the
 * template is instantiated, the Name is replaced by the network reference
 * in the Value part of the Binding associated with the Provider VDC that
 * the system selects during instantiation.
 * 
 */
declare class VclVMWVdcTemplateBinding {
	name: string;
	value: VclReference;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents a network pool using VXLAN technology. Backed by resource pools of a PVDC that can be backed by multiple VDSes.
 * 
 */
declare class VclVxlanPoolParams {
	vimSwitchRef: VclVimObjectRef;
	usedNetworksCount: number;
	promiscuousMode: boolean;
	transportZoneRef: VclVimObjectRef;
	readonly vdsContexts: VclObjectList;
	files: VclFilesList;
	status: number;
	description: string;
	name: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Defines how a group is imported from LDAP.
 * 
 */
declare class VclLdapGroupAttributes {
	objectIdentifier: string;
	backLinkIdentifier: string;
	membership: string;
	membershipIdentifier: string;
	groupName: string;
	objectClass: string;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents a stranded item for VIM object.
 * 
 */
declare class VclStrandedItemVimObject {
	vimObjectRef: VclVimObjectRef;
	errorMessage: string;
	name: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * A VM-Host affinity rule controls the placement of VMs on hosts.
 * 
 */
declare class VclVMWVmHostAffinityRule {
	vmGroupName: string;
	hostGroupName: string;
	isEnabled: boolean;
	isMandatory: boolean;
	polarity: string;
	name: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * General system settings.
 * 
 */
declare class VclGeneralSettings {
	syslogServerSettings: VclSyslogServerSettings;
	advancedNetworkingEnabled: boolean;
	vmDiscoveryEnabled: boolean;
	syncStartDate: VclXMLGregorianCalendar;
	absoluteSessionTimeoutMinutes: number;
	activityLogDisplayDays: number;
	activityLogKeepDays: number;
	allowOverlappingExtNets: boolean;
	chargebackEventsKeepDays: number;
	chargebackTablesCleanupJobTimeInSeconds: number;
	consoleProxyExternalAddress: string;
	hostCheckDelayInSeconds: number;
	hostCheckTimeoutSeconds: number;
	installationId: number;
	ipReservationTimeoutSeconds: number;
	loginNameOnly: boolean;
	prePopDefaultName: boolean;
	quarantineEnabled: boolean;
	quarantineResponseTimeoutSeconds: number;
	restApiBaseHttpUri: string;
	restApiBaseUri: string;
	restApiBaseUriPublicCertChain: string;
	sessionTimeoutMinutes: number;
	showStackTraces: boolean;
	syncIntervalInHours: number;
	systemExternalHttpAddress: string;
	systemExternalAddress: string;
	systemExternalAddressPublicCertChain: string;
	tenantPortalExternalHttpAddress: string;
	tenantPortalExternalAddress: string;
	tenantPortalPublicCertChain: string;
	transferSessionTimeoutSeconds: number;
	verifyVcCertificates: boolean;
	vcTruststorePassword: string;
	vcTruststoreContents: any;
	vcTruststoreType: string;
	vmrcVersion: string;
	verifyVsmCertificates: boolean;
	elasticAllocationPool: boolean;
	advancedNetworkingDfwApiUrl: string;
	advancedNetworkingDfwUiUrl: string;
	advancedNetworkingGatewayApiUrl: string;
	advancedNetworkingGatewayUiUrl: string;
	maxVdcQuota: number;
	subInterfacesEnabled: boolean;
	allowFipsModeForEdgeGateways: boolean;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents a network pool backed by one or more vSphere port groups.
 * 
 */
declare class VclPortGroupPoolParams {
	vimServer: VclReference;
	usedNetworksCount: number;
	portGroupRefs: VclVimObjectRefs;
	files: VclFilesList;
	status: number;
	description: string;
	name: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents an instance of resource class defined by an
 * extension service.
 * 
 */
declare class VclServiceResourceParams {
	org: VclReference;
	externalObjectId: string;
	description: string;
	name: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents backing resource pool for provider vDC.
 * 
 */
declare class VclVMWProviderVdcResourcePool {
	enabled: boolean;
	resourcePoolVimObjectRef: VclVimObjectRef;
	resourcePoolRef: VclReference;
	primary: boolean;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents the collection of VDS Contexts.
 * 
 */
declare class VclVdsContext {
	vdsContext: VclVimObjectRef;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents the result from AMQP settings test.
 * 
 */
declare class VclAmqpSettingsTest {
	valid: boolean;
	error: VclError;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * A list of references to virtual machines in vCenter inventory.
 * 
 */
declare class VclVmObjectRefsList {
	page: number;
	readonly vmObjectRef: VclObjectList;
	numberOfPages: number;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * The collection
 * of registered object extensions.
 * 
 */
declare class VclObjectExtensions {
	readonly objectExtension: VclObjectList;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents parameters for moving tenant entities.
 * 
 */
declare class VclTenantMigrationParams {
	orgs: VclOrgs;
	sourceDatastores: VclDatastores;
	targetDatastores: VclDatastores;
	description: string;
	name: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents an extension service API filter as a UrlPattern
 * or a ResponseContentType.
 * 
 */
declare class VclApiFilterParams {
	urlPattern: string;
	responseContentType: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents an extension service API definition file.
 * 
 */
declare class VclAdminFileDescriptorParams {
	description: string;
	file: VclReference;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * A pair of VM disk's instance ID and the datastore where the disk is located.
 * 
 */
declare class VclDiskDatastore {
	instanceId: string;
	vimServerRef: VclReference;
	moRef: string;
	vimObjectType: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * List of references to vCenter servers registered to vCloud
 * Director.
 * 
 */
declare class VclVMWVimServerReferences {
	readonly vimServerReference: VclObjectList;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * TBS
 */
declare class VclAutomaticNetworkPoolReference {
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents a virtual disk of the VM.
 * 
 */
declare class VclImportedDisk {
	vdcStorageProfile: VclReference;
	instanceId: string;
	iops: number;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Collection of rights.
 * 
 */
declare class VclRights {
	readonly right: VclObjectList;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Specification for a VDC template using the AllocationVApp allocation model.
 * 
 */
declare class VclVMWAllocationVappVdcTemplateSpecification {
	cpuAllocationMhz: number;
	memoryAllocationMB: number;
	cpuGuaranteedPercentage: number;
	memoryGuaranteedPercentage: number;
	cpuLimitMhzPerVcpu: number;
	readonly storageProfile: VclObjectList;
	nicQuota: number;
	vmQuota: number;
	provisionedNetworkQuota: number;
	gatewayConfiguration: VclVdcTemplateSpecificationGatewayConfiguration;
	thinProvision: boolean;
	networkPoolReference: VclReference;
	fastProvisioningEnabled: boolean;
	automaticNetworkPoolReference: VclAutomaticNetworkPoolReference;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents a reference to a resource. Contains an href
 * attribute, a resource status attribute, and optional name and
 * type attributes.
 * 
 */
declare class VclResourceReference {
	status: number;
	href: string;
	name: string;
	id: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Configuration parameters for a catalog that is published
 * externally.
 * 
 */
declare class VclPublishExternalCatalogParams {
	isPublishedExternally: boolean;
	catalogPublishedUrl: string;
	isCacheEnabled: boolean;
	preserveIdentityInfoFlag: boolean;
	password: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Type for a single licensingReport query result in records format.
 * 
 */
declare class VclQueryResultLicensingReportRecord {
	startDate: VclXMLGregorianCalendar;
	endDate: VclXMLGregorianCalendar;
	reportId: string;
	href: string;
	readonly otherAttributes: VclMap;
	metadata: VclMetaDataEntries;
	id: string;
	type: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Type for a single adminService query result in records format.
 * 
 */
declare class VclQueryResultAdminServiceRecord {
	enabled: boolean;
	vendor: string;
	routingKey: string;
	exchange: string;
	isAuthorizationEnabled: boolean;
	namespace: string;
	name: string;
	priority: number;
	href: string;
	readonly otherAttributes: VclMap;
	metadata: VclMetaDataEntries;
	id: string;
	type: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * TBS
 */
declare class VclIdpGroups {
	readonly idpGroups: VclList;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents a network firewall service.
 * 
 */
declare class VclFirewallService {
	defaultAction: string;
	logDefaultAction: boolean;
	readonly firewallRule: VclObjectList;
	isEnabled: boolean;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Type for a single resourcePoolVmList query result in records format.
 * 
 */
declare class VclQueryResultResourcePoolVMRecord {
	isBusy: boolean;
	hardwareVersion: number;
	isDeployed: boolean;
	containerName: string;
	guestOs: string;
	status: string;
	name: string;
	href: string;
	readonly otherAttributes: VclMap;
	metadata: VclMetaDataEntries;
	id: string;
	type: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Type for a single vmGroups query result in records format.
 * 
 */
declare class VclQueryResultVmGroupsRecord {
	vmGroupId: string;
	clusterMoref: string;
	vcId: string;
	vmGroupName: string;
	href: string;
	readonly otherAttributes: VclMap;
	metadata: VclMetaDataEntries;
	id: string;
	type: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * TBS
 */
declare class VclPvdcComputePolicies {
	readonly computePolicy: VclObjectList;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Container for query result sets.
 * 
 */
declare class VclContainer {
	pageSize: number;
	page: number;
	total: number;
	name: string;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents the user view of a virtual hardware version.
 * 
 */
declare class VclVirtualHardwareVersion {
	maxCoresPerSocket: number;
	supportedOperatingSystems: VclSupportedOperatingSystemsInfo;
	maxMemorySizeMb: number;
	maxCPUs: number;
	maxNICs: number;
	supportsNestedHV: boolean;
	supportsHotPlugPCI: boolean;
	supportsHotAdd: boolean;
	readonly supportedMemorySizeGb: VclList;
	readonly supportedCoresPerSocket: VclList;
	readonly hardDiskAdapter: VclObjectList;
	name: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Parameters for updating the truststore.
 * 
 */
declare class VclTrustStoreUpdateParams {
	password: string;
	fileSize: number;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents a group in this organization.
 * 
 */
declare class VclGroupParams {
	nameInSource: string;
	providerType: string;
	usersList: VclUsersList;
	role: VclReference;
	description: string;
	name: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Extends EntityReference type by adding relation attribute.
 * 
 */
declare class VclEntityLink {
	rel: string;
	name: string;
	id: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Specifies an earlier time relative to the current time
 * 
 */
declare class VclRelativeTime {
	interval: any;
	unit: VclTimeUnitType;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Type for a single dvSwitch query result in records format.
 * 
 */
declare class VclQueryResultDvSwitchRecord {
	moref: string;
	vcName: string;
	vc: string;
	isVCEnabled: boolean;
	name: string;
	href: string;
	readonly otherAttributes: VclMap;
	metadata: VclMetaDataEntries;
	id: string;
	type: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Type for a single user query result in records format.
 * 
 */
declare class VclQueryResultUserRecord {
	isEnabled: boolean;
	fullName: string;
	isLocked: boolean;
	identityProviderType: string;
	numberOfDeployedVMs: number;
	numberOfStoredVMs: number;
	deployedVMQuota: number;
	isLdapUser: boolean;
	storedVMQuota: number;
	roleNames: string;
	name: string;
	href: string;
	readonly otherAttributes: VclMap;
	metadata: VclMetaDataEntries;
	id: string;
	type: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * TBS
 */
declare class VclQueryResultOrgVdcTemplateRecord {
	org: string;
	description: string;
	name: string;
	href: string;
	readonly otherAttributes: VclMap;
	metadata: VclMetaDataEntries;
	id: string;
	type: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Container for VdcStorageProfile elements that reference
 * storage profiles used by a catalog.
 * 
 */
declare class VclCatalogStorageProfiles {
	readonly vdcStorageProfile: VclObjectList;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents a virtual machine.
 * 
 */
declare class VclVmParams {
	storageProfile: VclReference;
	vAppScopedLocalId: string;
	vdcComputePolicy: VclReference;
	vmCapabilities: VclVmCapabilities;
	environment: VclEnvironment;
	bootOptions: VclBootOptions;
	media: VclReference;
	needsCustomization: boolean;
	nestedHypervisorEnabled: boolean;
	vAppParent: VclReference;
	deployed: boolean;
	readonly section: VclAbstractObjectSet;
	dateCreated: VclXMLGregorianCalendar;
	files: VclFilesList;
	status: number;
	description: string;
	name: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * The collection existing
 * VM
 * affinity rules available.
 * 
 */
declare class VclVmAffinityRules {
	readonly vmAffinityRule: VclObjectList;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Type for a single strandedItem query result in records format.
 * 
 */
declare class VclQueryResultStrandedItemRecord {
	vimObjectType: string;
	deletionDate: VclXMLGregorianCalendar;
	numberOfPurgeAttempts: number;
	parentName: string;
	name: string;
	parent: string;
	href: string;
	readonly otherAttributes: VclMap;
	metadata: VclMetaDataEntries;
	id: string;
	type: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * A ticket and connection information for accessing the console of a VM.
 * 
 */
declare class VclMksTicket {
	ticket: string;
	vmx: string;
	host: string;
	port: number;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents an Org vDC network in the vCloud model.
 * 
 */
declare class VclOrgVdcNetworkParams {
	isShared: boolean;
	vimPortGroupRef: VclVimObjectRefDefault;
	providerInfo: string;
	edgeGateway: VclReference;
	serviceConfig: VclGatewayFeatures;
	status: number;
	configuration: VclNetworkConfiguration;
	description: string;
	name: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * TBS
 */
declare class VclOvfToVdcNetworkEntry {
	vdcNetwork: string;
	ovfNetwork: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Specification for a VDC template using the AllocationPool allocation model.
 * 
 */
declare class VclAllocationPoolVdcTemplateSpecification {
	cpuAllocationMhz: number;
	memoryAllocationMB: number;
	vCpuInMhz: number;
	cpuGuaranteedPercentage: number;
	memoryGuaranteedPercentage: number;
	readonly storageProfile: VclObjectList;
	nicQuota: number;
	vmQuota: number;
	provisionedNetworkQuota: number;
	gatewayConfiguration: VclVdcTemplateSpecificationGatewayConfiguration;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents a list of organizations.
 * 
 */
declare class VclOrgs {
	readonly org: VclObjectList;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents default guest personalization settings for an
 * organization. These values are applied during guest
 * customization of virtual machines in this organization
 * if their GuestCustomizationSection specifies UseOrgSettings.
 * 
 */
declare class VclOrgGuestPersonalizationSettings {
	domainUsername: string;
	domainPassword: string;
	allowDomainSettings: boolean;
	accountOrganizationalUnit: string;
	domainName: string;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents the email settings for an organization.
 * 
 */
declare class VclOrgEmailSettings {
	readonly alertEmailTo: VclList;
	isDefaultSmtpServer: boolean;
	isDefaultOrgEmail: boolean;
	fromEmailAddress: string;
	defaultSubjectPrefix: string;
	isAlertEmailToAllAdmins: boolean;
	smtpServerSettings: VclSmtpServerSettings;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * A collection of time-based metrics representing historic usage statistics
 * 
 */
declare class VclHistoricUsage {
	readonly metricSeries: VclObjectList;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Type for a single portgroup query result in records format.
 * 
 */
declare class VclQueryResultPortgroupRecord {
	moref: string;
	vcName: string;
	vc: string;
	network: string;
	networkName: string;
	portgroupType: string;
	scopeType: number;
	isVCEnabled: boolean;
	name: string;
	href: string;
	readonly otherAttributes: VclMap;
	metadata: VclMetaDataEntries;
	id: string;
	type: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents a load balancer pool.
 * 
 */
declare class VclLoadBalancerPool {
	readonly servicePort: VclObjectList;
	operational: boolean;
	errorDetails: string;
	description: string;
	readonly member: VclObjectList;
	name: string;
	id: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Container for the list of typed queries available to the
 * requesting user.
 * 
 */
declare class VclQueryList {
	pageSize: number;
	page: number;
	total: number;
	name: string;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents the NAT rule for one to one mapping of VM NIC and
 * external IP addresses from a network.
 * 
 */
declare class VclNatOneToOneVmRule {
	externalIpAddress: string;
	vAppScopedVmId: string;
	vmNicId: number;
	mappingMode: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * A basic type used to specify request parameters.
 * 
 */
declare class VclParams {
	description: string;
	name: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Information for one version of the API.
 * 
 */
declare class VclVersionInfo {
	version: string;
	readonly otherAttributes: VclMap;
	loginUrl: string;
	readonly mediaTypeMapping: VclObjectList;
	deprecated: boolean;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Mapping of a content media type to a xsd complex type.
 * 
 */
declare class VclMediaMapping {
	readonly otherAttributes: VclMap;
	mediaType: string;
	complexTypeName: string;
	schemaLocation: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * List all supported versions.
 * 
 */
declare class VclSupportedVersions {
	readonly otherAttributes: VclMap;
	readonly versionInfo: VclObjectList;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * The standard error message type used in the vCloud REST API.
 * 
 */
declare class VclError {
	tenantError: VclTenantError;
	majorErrorCode: number;
	minorErrorCode: string;
	vendorSpecificErrorCode: string;
	message: string;
	stackTrace: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents parameters to a request.
 * 
 */
declare class VclRequestOperationParams {
	message: string;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents all the rights that have been granted to an organization.
 * 
 */
declare class VclOrganizationRights {
	readonly rightReference: VclObjectList;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Type for a single aclRule query result in records format.
 * 
 */
declare class VclQueryResultAclRuleRecord {
	org: string;
	resourceClassAction: string;
	serviceResource: string;
	serviceResourceAccess: string;
	orgAccess: string;
	principal: string;
	principalAccess: string;
	principalType: string;
	name: string;
	href: string;
	readonly otherAttributes: VclMap;
	metadata: VclMetaDataEntries;
	id: string;
	type: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Describes to-the-cloud tunnel
 * 
 */
declare class VclToCloudTunnel {
	destinationId: string;
	trafficType: string;
	sourceId: string;
	description: string;
	name: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents vApp creation parameters.
 * 
 */
declare class VclVAppCreationParams {
	instantiationParams: VclInstantiationParams;
	vAppParent: VclReference;
	deploy: boolean;
	powerOn: boolean;
	description: string;
	name: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * TBD
 * 
 */
declare class VclMediaSettings {
	adapterType: string;
	deviceId: string;
	mediaImage: VclReference;
	mediaType: VclMediaTypeType;
	mediaState: VclMediaStateType;
	unitNumber: number;
	busNumber: number;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Parameters for a recompose vApp request.
 * 
 */
declare class VclRecomposeVAppParams {
	readonly deleteItem: VclObjectList;
	readonly reconfigureItem: VclObjectList;
	readonly sourcedItem: VclObjectList;
	readonly createItem: VclObjectList;
	allEULAsAccepted: boolean;
	linkedClone: boolean;
	instantiationParams: VclInstantiationParams;
	vAppParent: VclReference;
	deploy: boolean;
	powerOn: boolean;
	description: string;
	name: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents a storage profile in an organization vDC.
 * 
 */
declare class VclAdminVdcStorageProfileParams {
	providerVdcStorageProfile: VclReference;
	enabled: boolean;
	limit: number;
	storageUsedMB: number;
	units: string;
	iopsAllocated: number;
	iopsSettings: VclVdcStorageProfileIopsSettings;
	default: boolean;
	description: string;
	name: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * TBS
 */
declare class VclVdcComputePolicyReferences {
	readonly vdcComputePolicyReference: VclObjectList;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Type for a single orgVdc query result in records format.
 * 
 */
declare class VclQueryResultOrgVdcRecord {
	computeProviderScope: string;
	cpuAllocationMhz: number;
	cpuLimitMhz: number;
	cpuUsedMhz: number;
	isBusy: boolean;
	isEnabled: boolean;
	isSystemVdc: boolean;
	memoryAllocationMB: number;
	memoryLimitMB: number;
	memoryUsedMB: number;
	networkPoolUniversalId: string;
	networkProviderScope: string;
	numberOfDeployedVApps: number;
	numberOfDisks: number;
	numberOfMedia: number;
	numberOfRunningVMs: number;
	numberOfStorageProfiles: number;
	numberOfVAppTemplates: number;
	numberOfVApps: number;
	numberOfVMs: number;
	orgName: string;
	providerVdc: string;
	providerVdcName: string;
	storageAllocationMB: number;
	storageLimitMB: number;
	storageUsedMB: number;
	cpuReservedMhz: number;
	memoryReservedMB: number;
	numberOfDatastores: number;
	description: string;
	status: string;
	name: string;
	href: string;
	readonly otherAttributes: VclMap;
	metadata: VclMetaDataEntries;
	id: string;
	type: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Type for a single serviceLink query result in records format.
 * 
 */
declare class VclQueryResultServiceLinkRecord {
	rel: string;
	linkHref: string;
	mimeType: string;
	resourceId: string;
	resourceType: string;
	service: string;
	href: string;
	readonly otherAttributes: VclMap;
	metadata: VclMetaDataEntries;
	id: string;
	type: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * TBS
 */
declare class VclProtocols {
	tcp: boolean;
	udp: boolean;
	icmp: boolean;
	any: boolean;
	other: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Defines connection details for the organization s SMTP server.
 * If IsDefaultSmtpServer (in OrgEmailSettings) is false, the SmtpServerSettings
 * element is taken into account.
 * 
 */
declare class VclSmtpServerSettings {
	smtpSecureMode: VclSmtpSecureModeType;
	sslTrustStore: string;
	isUseAuthentication: boolean;
	username: string;
	password: string;
	host: string;
	port: number;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Parameters for a copyCatalogItem or moveCatalogItem request.
 * 
 */
declare class VclCopyOrMoveCatalogItemParams {
	source: VclReference;
	description: string;
	name: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Parameters for an uploadVappTemplate request.
 * 
 */
declare class VclUploadVAppTemplateParams {
	vdcStorageProfile: VclReference;
	manifestRequired: boolean;
	sourceHref: string;
	transferFormat: string;
	description: string;
	name: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Type for a single adminAllocatedExternalAddress query result in records format.
 * 
 */
declare class VclQueryResultAdminAllocatedExternalAddressRecord {
	org: string;
	ipAddress: string;
	network: string;
	linkedNetwork: string;
	href: string;
	readonly otherAttributes: VclMap;
	metadata: VclMetaDataEntries;
	id: string;
	type: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Type for a single licensingManagedServer query result in records format.
 * 
 */
declare class VclQueryResultLicensingManagedServerRecord {
	managedObjectReference: string;
	memoryInstalled: number;
	memoryUsed: number;
	observationDate: VclXMLGregorianCalendar;
	parentSampleId: string;
	socketCount: number;
	virtualCenterId: string;
	name: string;
	href: string;
	readonly otherAttributes: VclMap;
	metadata: VclMetaDataEntries;
	id: string;
	type: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Contains a list of VMware virtual hardware versions supported
 * in this vDC.
 * 
 */
declare class VclSupportedHardwareVersions {
	readonly supportedHardwareVersion: VclObjectList;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Type for a single apiFilter query result in records format.
 * 
 */
declare class VclQueryResultApiFilterRecord {
	urlPattern: string;
	service: string;
	href: string;
	readonly otherAttributes: VclMap;
	metadata: VclMetaDataEntries;
	id: string;
	type: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Audit event
 * 
 */
declare class VclAuditEventParams {
	details: string;
	org: VclReference;
	serviceNamespace: string;
	user: VclReference;
	owner: VclReference;
	timeStamp: VclXMLGregorianCalendar;
	eventType: string;
	success: boolean;
	description: string;
	name: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents a list of IP ranges.
 * 
 */
declare class VclIpRanges {
	readonly ipRange: VclObjectList;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Set of range of integer values.
 * 
 */
declare class VclRanges {
	readonly range: VclObjectList;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Type for a single providerVdc query result in records format.
 * 
 */
declare class VclQueryResultVMWProviderVdcRecord {
	cpuAllocationMhz: number;
	cpuLimitMhz: number;
	cpuUsedMhz: number;
	isBusy: boolean;
	isEnabled: boolean;
	memoryAllocationMB: number;
	memoryLimitMB: number;
	memoryUsedMB: number;
	numberOfStorageProfiles: number;
	storageAllocationMB: number;
	storageLimitMB: number;
	storageUsedMB: number;
	isDeleted: boolean;
	numberOfDatastores: number;
	numberOfVdcs: number;
	vcpuRatingMhz: number;
	status: string;
	name: string;
	href: string;
	readonly otherAttributes: VclMap;
	metadata: VclMetaDataEntries;
	id: string;
	type: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents a network pool.
 * 
 */
declare class VclNetworkPool {
	files: VclFilesList;
	status: number;
	description: string;
	name: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 1.5
 */
declare class VclRuntimeInfoSection {
	vMWareTools: VclVMWareTools;
	info: VclMsg;
	readonly otherAttributes: VclMap;
	required: boolean;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Parameters to a deploy vApp request.
 * 
 */
declare class VclDeployVAppParams {
	powerOn: boolean;
	deploymentLeaseSeconds: number;
	forceCustomization: boolean;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * A sequence of metric values measured at specified times
 * 
 */
declare class VclTimeSeriesMetric {
	readonly sample: VclObjectList;
	expectedInterval: number;
	unit: string;
	name: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * Parameters for creating from-the-cloud tunnel listener
 */
declare class VclFromCloudTunnelListenerCreateParams {
	trafficType: string;
	endpointTag: string;
	port: number;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents
 * the
 * association between a registered extension and a
 * selector. At the selector level it defines a default
 * policy of association. At the instance level it defines
 * an overridden policy or a selector instance specific
 * association.
 * 
 */
declare class VclSelectorExtension {
	enabled: boolean;
	objectExtensionId: string;
	phases: VclObject;
	priority: VclBigInteger;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Container for references to roles.
 * 
 */
declare class VclRoleReferences {
	readonly roleReference: VclObjectList;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * List of event-specific properties.
 * 
 */
declare class VclEventProperties {
	readonly eventProperty: VclObjectList;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Type for a single resourceClassAction query result in records format.
 * 
 */
declare class VclQueryResultResourceClassActionRecord {
	urlPattern: string;
	httpMethod: string;
	resourceClass: string;
	name: string;
	href: string;
	readonly otherAttributes: VclMap;
	metadata: VclMetaDataEntries;
	id: string;
	type: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Parameters required by a cloneMedia request.
 * 
 */
declare class VclCloneMediaParams {
	isSourceDelete: boolean;
	vdcStorageProfile: VclReference;
	source: VclReference;
	description: string;
	name: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents features of a network.
 * 
 */
declare class VclNetworkFeatures {
	readonly networkService: VclAbstractObjectSet;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents a list of VDC templates.
 * 
 */
declare class VclVdcTemplateList {
	readonly vdcTemplate: VclObjectList;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Upload socket for SSPI keytab.
 * 
 */
declare class VclSspiKeytabUploadSocket {
	task: VclTaskParams;
	uploadLocation: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Gateway Configuration.
 * 
 */
declare class VclGatewayConfiguration {
	backwardCompatibilityMode: boolean;
	syslogServerSettings: VclSyslogServer;
	advancedNetworkingEnabled: boolean;
	gatewayBackingConfig: string;
	gatewayInterfaces: VclGatewayInterfaces;
	edgeGatewayServiceConfiguration: VclGatewayFeatures;
	haEnabled: boolean;
	useDefaultRouteForDnsRelay: boolean;
	distributedRoutingEnabled: boolean;
	fipsModeEnabled: boolean;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Parameters for a clonevApp request.
 * 
 */
declare class VclCloneVAppParams {
	isSourceDelete: boolean;
	readonly sourcedItem: VclObjectList;
	linkedClone: boolean;
	readonly sourcedVmInstantiationParams: VclObjectList;
	source: VclReference;
	instantiationParams: VclInstantiationParams;
	vAppParent: VclReference;
	deploy: boolean;
	powerOn: boolean;
	description: string;
	name: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents a list of AMQP broker configurations.
 * 
 */
declare class VclAmqpBrokers {
	readonly amqpBrokerConfiguration: VclObjectList;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents default vAppTemplate lease policies for this
 * organization.
 * 
 */
declare class VclOrgVAppTemplateLeaseSettings {
	deleteOnStorageLeaseExpiration: boolean;
	storageLeaseSeconds: number;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Map from OVF Virtual Systems via the BIOS UUID to the datastore location of
 * the replicated VM's files on the destination site.
 * 
 */
declare class VclVsToVmxMap {
	readonly entry: VclObjectList;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * TBS
 */
declare class VclPhase {
	optional: boolean;
	name: string;
	type: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Type for a single catalog query result in records format.
 * 
 */
declare class VclQueryResultCatalogRecord {
	version: number;
	numberOfMedia: number;
	numberOfVAppTemplates: number;
	orgName: string;
	creationDate: VclXMLGregorianCalendar;
	ownerName: string;
	isPublished: boolean;
	isShared: boolean;
	publishSubscriptionType: string;
	owner: string;
	description: string;
	status: string;
	name: string;
	href: string;
	readonly otherAttributes: VclMap;
	metadata: VclMetaDataEntries;
	id: string;
	type: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents a guest customization settings.
 * 
 */
declare class VclGuestCustomizationSection {
	enabled: boolean;
	href: string;
	computerName: string;
	domainName: string;
	changeSid: boolean;
	virtualMachineId: string;
	joinDomainEnabled: boolean;
	useOrgSettings: boolean;
	domainUserName: string;
	domainUserPassword: string;
	machineObjectOU: string;
	adminPasswordEnabled: boolean;
	adminPasswordAuto: boolean;
	adminPassword: string;
	adminAutoLogonEnabled: boolean;
	adminAutoLogonCount: number;
	resetPasswordRequired: boolean;
	customizationScript: string;
	type: string;
	info: VclMsg;
	readonly otherAttributes: VclMap;
	required: boolean;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Container for an organization's PEM-encoded private key and public
 * key certificate chain used during SAML-token processing.
 * 
 */
declare class VclSamlSPKeyAndCertificateChain {
	certificateChain: string;
	key: string;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents edge gateway services.
 * 
 */
declare class VclGatewayFeatures {
	readonly networkService: VclAbstractObjectSet;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Container for references to groups.
 * 
 */
declare class VclGroupsList {
	readonly groupReference: VclObjectList;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Container for references to catalog items.
 * 
 */
declare class VclCatalogItems {
	readonly catalogItem: VclObjectList;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents Gateway DHCP service.
 * 
 */
declare class VclGatewayDhcpService {
	readonly pool: VclObjectList;
	isEnabled: boolean;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Specify network settings like gateway, network mask, DNS servers, IP ranges, etc.
 * 
 */
declare class VclIpScope {
	isEnabled: boolean;
	gateway: string;
	netmask: string;
	subnetPrefixLength: number;
	ipRanges: VclIpRanges;
	dns1: string;
	dns2: string;
	dnsSuffix: string;
	isInherited: boolean;
	allocatedIpAddresses: VclIpAddresses;
	subAllocations: VclSubAllocations;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents a supported virtual hardware version.
 * 
 */
declare class VclSupportedHardwareVersion {
	href: string;
	name: string;
	default: boolean;
	type: string;
	content: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents a local or imported user.
 * 
 */
declare class VclUserParams {
	isEnabled: boolean;
	fullName: string;
	emailAddress: string;
	telephone: string;
	isLocked: boolean;
	iM: string;
	nameInSource: string;
	isAlertEnabled: boolean;
	alertEmailPrefix: string;
	alertEmail: string;
	isExternal: boolean;
	providerType: string;
	isDefaultCached: boolean;
	isGroupRole: boolean;
	storedVmQuota: number;
	deployedVmQuota: number;
	groupReferences: VclGroupsList;
	password: string;
	role: VclReference;
	description: string;
	name: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Type for a single orgVdcResourcePoolRelation query result in records format.
 * 
 */
declare class VclQueryResultOrgVdcResourcePoolRelationRecord {
	vc: string;
	vdc: string;
	resourcePoolMoref: string;
	href: string;
	readonly otherAttributes: VclMap;
	metadata: VclMetaDataEntries;
	id: string;
	type: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Describes the administrator view of a to-the-cloud tunnel
 * 
 */
declare class VclAdminToCloudTunnel {
	destinationIpAddress: string;
	destinationPort: number;
	useSsl: boolean;
	destinationId: string;
	trafficType: string;
	sourceId: string;
	description: string;
	name: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents the NAT basic rule for one to one mapping of internal
 * and external IP addresses from a network.
 * 
 */
declare class VclNatOneToOneBasicRule {
	externalIpAddress: string;
	mappingMode: string;
	internalIpAddress: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents all the roles available in an organization.
 * 
 */
declare class VclOrganizationRoles {
	readonly roleReference: VclObjectList;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents a reference to an organization.
 * 
 */
declare class VclOrganizationReference {
	href: string;
	name: string;
	id: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Maps source site external NAT IP addresses to destination site
 * external NAT IP addresses.
 * 
 */
declare class VclExternalNatIpMap {
	readonly entry: VclObjectList;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Type for a single adminDisk query result in records format.
 * 
 */
declare class VclQueryResultAdminDiskRecord {
	task: string;
	org: string;
	storageProfile: string;
	ownerName: string;
	vc: string;
	datastoreName: string;
	vdc: string;
	vdcName: string;
	attachedVmCount: number;
	busSubType: string;
	busType: string;
	busTypeDesc: string;
	datastore: string;
	isAttached: boolean;
	sizeB: number;
	storageProfileName: string;
	description: string;
	status: string;
	name: string;
	href: string;
	readonly otherAttributes: VclMap;
	metadata: VclMetaDataEntries;
	id: string;
	type: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents a file required by an extension API definition.
 * 
 */
declare class VclFileDescriptorData {
	description: string;
	file: VclReference;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * TBS
 */
declare class VclIdpRecommendedRoles {
	readonly idpRecommendedRoles: VclList;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Type for a single catalogItem query result in records format.
 * 
 */
declare class VclQueryResultCatalogItemRecord {
	entityName: string;
	creationDate: VclXMLGregorianCalendar;
	ownerName: string;
	isPublished: boolean;
	entityType: string;
	vdc: string;
	vdcName: string;
	isExpired: boolean;
	isVdcEnabled: boolean;
	catalog: string;
	catalogName: string;
	owner: string;
	entity: string;
	status: string;
	name: string;
	href: string;
	readonly otherAttributes: VclMap;
	metadata: VclMetaDataEntries;
	id: string;
	type: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * TBS
 */
declare class VclOIDCAttributeMapping {
	emailAttributeName: string;
	firstNameAttributeName: string;
	fullNameAttributeName: string;
	subjectAttributeName: string;
	lastNameAttributeName: string;
	groupsAttributeName: string;
	rolesAttributeName: string;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Container for SiteAssociationType
 * 
 */
declare class VclSiteAssociations {
	readonly siteAssociationMember: VclObjectList;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Type for a single strandedUser query result in records format.
 * 
 */
declare class VclQueryResultStrandedUserRecord {
	org: string;
	fullName: string;
	numberOfDeployedVMs: number;
	numberOfStoredVMs: number;
	isInSync: boolean;
	name: string;
	href: string;
	readonly otherAttributes: VclMap;
	metadata: VclMetaDataEntries;
	id: string;
	type: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents the owner of this entity.
 * 
 */
declare class VclOwner {
	user: VclReference;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Collection of supported hardware capabilities.
 * 
 */
declare class VclCapabilities {
	supportedHardwareVersions: VclSupportedHardwareVersions;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Parameters for a cloneVappTemplate request.
 * 
 */
declare class VclCloneVAppTemplateParams {
	isSourceDelete: boolean;
	vdcStorageProfile: VclReference;
	source: VclReference;
	description: string;
	name: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Specifies which current usage metrics to retrieve
 * 
 */
declare class VclCurrentUsageSpec {
	readonly metricPattern: VclList;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Type for a single vApp query result in records format.
 * 
 */
declare class VclQueryResultVAppRecord {
	isBusy: boolean;
	isEnabled: boolean;
	creationDate: VclXMLGregorianCalendar;
	ownerName: string;
	vdc: string;
	isInMaintenanceMode: boolean;
	isPublic: boolean;
	snapshotCreated: VclXMLGregorianCalendar;
	vdcName: string;
	isAutoNature: boolean;
	isDeployed: boolean;
	isExpired: boolean;
	snapshot: boolean;
	status: string;
	name: string;
	href: string;
	readonly otherAttributes: VclMap;
	metadata: VclMetaDataEntries;
	id: string;
	type: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents an AMQP Component Configuration list
 * 
 */
declare class VclAmqpComponentConfigurations {
	readonly amqpComponentConfiguration: VclObjectList;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Type for a single licensingReportSample query result in records format.
 * 
 */
declare class VclQueryResultLicensingReportSampleRecord {
	observationDate: VclXMLGregorianCalendar;
	allocatedVirtualMemory: number;
	parentReportId: string;
	physicalMemoryUsed: number;
	physicalSocketCount: number;
	publishingToRemoteSites: boolean;
	sampleId: string;
	subscribingToRemoteSites: boolean;
	totalPhysicalMemory: number;
	virtualProcessorCount: number;
	vmCount: number;
	href: string;
	readonly otherAttributes: VclMap;
	metadata: VclMetaDataEntries;
	id: string;
	type: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Locality parameters provide a hint that may help optimize
 * placement of a VM with respect to another VM or an independent
 * disk.
 * See <a href="http://kb.vmware.com/kb/2105352">KB 2105352</a>.
 * 
 */
declare class VclLocalityParams {
	readonly resourceEntity: VclObjectList;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents a vCloud Session.
 * 
 */
declare class VclSession {
	org: string;
	user: string;
	userId: string;
	authorizedLocations: VclAuthorizedLocations;
	locationId: string;
	roles: string;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents the moref and the type of a vSphere object.
 * 
 */
declare class VclVimObjectRefDefault {
	vimServerRef: VclReference;
	moRef: string;
	vimObjectType: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Information about vendor template attributes
 * 
 */
declare class VclVendorTemplateAttributes {
	name: string;
	value: string;
	key: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents a supported virtual hardware version.
 * 
 */
declare class VclHardwareVersion {
	href: string;
	type: string;
	content: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * A reference to a user or group managed by an identity provider configured
 * for use in this organization.
 * 
 */
declare class VclExternalSubject {
	subjectId: string;
	isUser: boolean;
	idpType: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Type for a single adminOrgVdcStorageProfile query result in records format.
 * 
 */
declare class VclQueryResultAdminOrgVdcStorageProfileRecord {
	isEnabled: boolean;
	org: string;
	storageLimitMB: number;
	storageUsedMB: number;
	iopsAllocated: number;
	numberOfConditions: number;
	storageProfileMoref: string;
	vc: string;
	vdc: string;
	vdcName: string;
	iopsLimit: number;
	isDefaultStorageProfile: boolean;
	name: string;
	href: string;
	readonly otherAttributes: VclMap;
	metadata: VclMetaDataEntries;
	id: string;
	type: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Type for a single orgVdcNetwork query result in records format.
 * 
 */
declare class VclQueryResultOrgVdcNetworkRecord {
	isBusy: boolean;
	netmask: string;
	subnetPrefixLength: number;
	vdc: string;
	dns1: string;
	dns2: string;
	dnsSuffix: string;
	isIpScopeInherited: boolean;
	vdcName: string;
	isShared: boolean;
	connectedTo: string;
	crossVdcNetworkId: string;
	crossVdcNetworkLocationId: string;
	defaultGateway: string;
	interfaceType: number;
	linkType: number;
	totalIpCount: number;
	usedIpCount: number;
	vdcGroupId: string;
	name: string;
	href: string;
	readonly otherAttributes: VclMap;
	metadata: VclMetaDataEntries;
	id: string;
	type: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * The configurations applied to a network. This is an
 * abstract base type. The concrete types include those for vApp and
 * Organization wide networks.
 * 
 */
declare class VclNetworkConfiguration {
	ipScope: VclIpScope;
	backwardCompatibilityMode: boolean;
	ipScopes: VclIpScopes;
	parentNetwork: VclReference;
	fenceMode: string;
	retainNetInfoAcrossDeployments: boolean;
	features: VclNetworkFeatures;
	syslogServerSettings: VclSyslogServerSettings;
	routerInfo: VclRouterInfo;
	advancedNetworkingEnabled: boolean;
	subInterface: boolean;
	distributedInterface: boolean;
	guestVlanAllowed: boolean;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Defines how a group is imported from LDAP.
 * 
 */
declare class VclOrgLdapGroupAttributes {
	objectIdentifier: string;
	backLinkIdentifier: string;
	membership: string;
	membershipIdentifier: string;
	groupName: string;
	objectClass: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * List of operation names.
 * 
 */
declare class VclTaskOperationList {
	readonly operation: VclList;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Type for a single event query result in records format.
 * 
 */
declare class VclQueryResultEventRecord {
	entityName: string;
	details: string;
	orgName: string;
	entityType: string;
	eventId: string;
	eventStatus: number;
	serviceNamespace: string;
	description: string;
	timeStamp: VclXMLGregorianCalendar;
	eventType: string;
	entity: string;
	userName: string;
	href: string;
	readonly otherAttributes: VclMap;
	metadata: VclMetaDataEntries;
	id: string;
	type: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents a vApp template customization settings.
 * 
 */
declare class VclCustomizationSection {
	href: string;
	goldMaster: boolean;
	customizeOnInstantiate: boolean;
	type: string;
	info: VclMsg;
	readonly otherAttributes: VclMap;
	required: boolean;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents subnet details.
 * 
 */
declare class VclIpsecVpnSubnet {
	gateway: string;
	netmask: string;
	name: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Describes cloud part of a tunneling application registered with VCD.
 * 
 */
declare class VclTunnelingApplication {
	trafficType: string;
	routingKey: string;
	exchange: string;
	description: string;
	name: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents a single site in a multi-site vCloud configuration.
 * 
 */
declare class VclSite {
	multiSiteUrl: string;
	siteAssociations: VclSiteAssociations;
	restEndpoint: string;
	baseUiEndpoint: string;
	tenantUiEndpoint: string;
	restEndpointCertificate: string;
	description: string;
	name: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents an IPSec-VPN network service.
 * 
 */
declare class VclIpsecVpnService {
	externalIpAddress: string;
	publicIpAddress: string;
	readonly ipsecVpnTunnel: VclObjectList;
	isEnabled: boolean;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Specification for a VDC template using the AllocationVApp allocation model.
 * 
 */
declare class VclAllocationVappVdcTemplateSpecification {
	cpuAllocationMhz: number;
	memoryAllocationMB: number;
	cpuGuaranteedPercentage: number;
	memoryGuaranteedPercentage: number;
	cpuLimitMhzPerVcpu: number;
	readonly storageProfile: VclObjectList;
	nicQuota: number;
	vmQuota: number;
	provisionedNetworkQuota: number;
	gatewayConfiguration: VclVdcTemplateSpecificationGatewayConfiguration;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents LDAP connection settings for an organization.
 * 
 */
declare class VclOrgLdapSettings {
	orgLdapMode: string;
	customUsersOu: string;
	customOrgLdapSettings: VclCustomOrgLdapSettings;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * A list of Gateway Interfaces.
 * 
 */
declare class VclGatewayInterfaces {
	readonly gatewayInterface: VclObjectList;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Type for a single blockingTask query result in records format.
 * 
 */
declare class VclQueryResultBlockingTaskRecord {
	task: string;
	creationDate: VclXMLGregorianCalendar;
	hasOwner: boolean;
	jobStatus: string;
	operationName: string;
	originatingOrg: string;
	originatingOrgName: string;
	ownerName: string;
	timeoutAction: string;
	owner: string;
	expirationTime: VclXMLGregorianCalendar;
	status: string;
	href: string;
	readonly otherAttributes: VclMap;
	metadata: VclMetaDataEntries;
	id: string;
	type: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * TBS
 */
declare class VclPayGoVdcSummary {
	memoryConsumptionMB: number;
	storageConsumptionMB: number;
	cpuConsumptionMhz: number;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * Parameters for creating from-the-cloud tunnel
 */
declare class VclFromCloudTunnelCreateParams {
	destinationId: string;
	trafficType: string;
	sourceId: string;
	endpointTag: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Specifies which historic usage metrics to retrieve
 * 
 */
declare class VclHistoricUsageSpec {
	absoluteStartTime: VclAbsoluteTime;
	relativeStartTime: VclRelativeTime;
	absoluteEndTime: VclAbsoluteTime;
	relativeEndTime: VclRelativeTime;
	readonly metricPattern: VclList;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Type for a single host query result in records format.
 * 
 */
declare class VclQueryResultHostRecord {
	isBusy: boolean;
	isEnabled: boolean;
	numberOfVMs: number;
	isDeleted: boolean;
	vcName: string;
	vc: string;
	isSupported: boolean;
	isInMaintenanceMode: boolean;
	isCrossHostEnabled: boolean;
	isHung: boolean;
	isPendingUpgrade: boolean;
	isPrepared: boolean;
	osVersion: string;
	name: string;
	state: number;
	href: string;
	readonly otherAttributes: VclMap;
	metadata: VclMetaDataEntries;
	id: string;
	type: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Parameters for initializing file upload session.
 * 
 */
declare class VclFileUploadParams {
	fileSize: number;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents vApp composition parameters.
 * 
 */
declare class VclComposeVAppParams {
	readonly sourcedItem: VclObjectList;
	readonly createItem: VclObjectList;
	allEULAsAccepted: boolean;
	linkedClone: boolean;
	instantiationParams: VclInstantiationParams;
	vAppParent: VclReference;
	deploy: boolean;
	powerOn: boolean;
	description: string;
	name: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents an AMQP Configuration key value pair list
 * 
 */
declare class VclAmqpConfigList {
	readonly configEntry: VclObjectList;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents service profile for a load balancing virtual server.
 * 
 */
declare class VclLBVirtualServerServiceProfile {
	isEnabled: boolean;
	persistence: VclLBPersistence;
	protocol: string;
	port: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Ticket for establishing a connection to Cloud Proxy.
 * 
 */
declare class VclHybridTicket {
	value: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Gives more details of remote peer end point.
 * 
 */
declare class VclIpsecVpnRemotePeer {
	vcdUrl: string;
	vcdOrganization: string;
	vcdUsername: string;
	name: string;
	id: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * TBS
 */
declare class VclOrgVdcRollup {
	numberOfDeployedVApps: number;
	readonly siteReference: VclObjectList;
	readonly orgReference: VclObjectList;
	readonly orgVdcReference: VclObjectList;
	numberOfOrgs: number;
	numberOfPoweredOnVms: number;
	reservationPoolVdcSummary: VclReservationPoolVdcSummary;
	allocationPoolVdcSummary: VclAllocationPoolVdcSummary;
	payGoVdcSummary: VclPayGoVdcSummary;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * Allows a user to change the size of the gateway
 */
declare class VclEdgeGatewayFormFactor {
	gatewayType: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * TBS
 */
declare class VclMetadataEntry {
	typedValue: VclAbstractValueObject;
	domain: VclMetadataDomainTag;
	value: string;
	key: string;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Type for a single resourcePool query result in records format.
 * 
 */
declare class VclQueryResultResourcePoolRecord {
	isDeleted: boolean;
	moref: string;
	vcName: string;
	vc: string;
	name: string;
	href: string;
	readonly otherAttributes: VclMap;
	metadata: VclMetaDataEntries;
	id: string;
	type: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents a list of AMQP Configuration items
 * 
 */
declare class VclAmqpConfiguration {
	amqpComponentConfigurations: VclAmqpComponentConfigurations;
	amqpBrokers: VclAmqpBrokers;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Container for references to available organization vDC networks.
 * 
 */
declare class VclAvailableNetworks {
	readonly network: VclObjectList;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents VM creation parameters.
 * 
 */
declare class VclCreateVmParams {
	powerOn: boolean;
	media: VclReference;
	createVm: VclVmParams;
	description: string;
	name: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents general settings for an organization.
 * 
 */
declare class VclOrgGeneralSettings {
	storedVmQuota: number;
	vmDiscoveryEnabled: boolean;
	deployedVMQuota: number;
	canPublishCatalogs: boolean;
	canPublishExternally: boolean;
	canSubscribe: boolean;
	useServerBootSequence: boolean;
	delayAfterPowerOnSeconds: number;
	vdcQuota: number;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents an AMQP Component Configuration.
 * 
 */
declare class VclAmqpComponentConfiguration {
	amqpConfigList: VclAmqpConfigList;
	name: string;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Type for a single adminTask query result in records format.
 * 
 */
declare class VclQueryResultAdminTaskRecord {
	progress: number;
	org: string;
	orgName: string;
	hasOwner: boolean;
	ownerName: string;
	objectName: string;
	operationFull: string;
	startDate: VclXMLGregorianCalendar;
	cellName: string;
	endDate: VclXMLGregorianCalendar;
	serviceNamespace: string;
	owner: string;
	objectType: string;
	status: string;
	object: string;
	name: string;
	href: string;
	readonly otherAttributes: VclMap;
	metadata: VclMetaDataEntries;
	id: string;
	type: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents an IPSec VPN tunnel.
 * 
 */
declare class VclGatewayIpsecVpnTunnel {
	isEnabled: boolean;
	errorDetails: string;
	peerId: string;
	ipsecVpnPeer: VclAbstractObject;
	peerIpAddress: string;
	sharedSecret: string;
	encryptionProtocol: string;
	mtu: number;
	isOperational: boolean;
	localIpAddress: string;
	localId: string;
	readonly localSubnet: VclObjectList;
	readonly peerSubnet: VclObjectList;
	sharedSecretEncrypted: boolean;
	description: string;
	name: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Information about a vendor service template. This is optional.
 * 
 */
declare class VclVendorTemplate {
	readonly vendorTemplateAttributes: VclObjectList;
	name: string;
	id: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Properties of a from-the-cloud tunnel listener. A from-the-cloud listener associates
 * a port with a particular (traffic type, endpoint tag) tuple. Ports are unique and
 * (traffic type, endpoint tag) tuples are unique.
 * 
 */
declare class VclFromCloudTunnelListener {
	trafficType: string;
	endpointTag: string;
	port: number;
	description: string;
	name: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * A basic type used to pass arguments to the instantiate VDC template operation,
 * this provides a name and optional description for a VDC instantiated from a template.
 * 
 */
declare class VclInstantiateVdcTemplateParams {
	description: string;
	source: VclReference;
	name: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents a DHCP network service.
 * 
 */
declare class VclDhcpService {
	defaultLeaseTime: number;
	maxLeaseTime: number;
	ipRange: VclIpRange;
	domainName: string;
	routerIp: string;
	subMask: string;
	primaryNameServer: string;
	secondaryNameServer: string;
	isEnabled: boolean;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Extends reference type by adding relation attribute.
 * Defines a hyper-link with a relationship, hyper-link reference,
 * and an optional MIME type.
 * 
 */
declare class VclLink {
	rel: string;
	href: string;
	name: string;
	id: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Type for a single resourceClass query result in records format.
 * 
 */
declare class VclQueryResultResourceClassRecord {
	mimeType: string;
	urlTemplate: string;
	nid: string;
	urnPattern: string;
	service: string;
	name: string;
	href: string;
	readonly otherAttributes: VclMap;
	metadata: VclMetaDataEntries;
	id: string;
	type: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents a firewall rule.
 * 
 */
declare class VclFirewallRule {
	isEnabled: boolean;
	icmpSubType: string;
	direction: string;
	matchOnTranslate: boolean;
	protocols: VclFirewallRuleProtocols;
	destinationPortRange: string;
	destinationIp: string;
	destinationVm: VclVmSelection;
	sourcePort: number;
	sourcePortRange: string;
	sourceIp: string;
	sourceVm: VclVmSelection;
	enableLogging: boolean;
	description: string;
	policy: string;
	id: string;
	port: number;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Type for a single group query result in records format.
 * 
 */
declare class VclQueryResultGroupRecord {
	isReadOnly: boolean;
	identityProviderType: string;
	roleName: string;
	description: string;
	name: string;
	href: string;
	readonly otherAttributes: VclMap;
	metadata: VclMetaDataEntries;
	id: string;
	type: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Container for OrgAssociationType
 * 
 */
declare class VclOrgAssociations {
	readonly orgAssociationMember: VclObjectList;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents the admin view of a catalog.
 * 
 */
declare class VclAdminCatalogParams {
	catalogStorageProfiles: VclCatalogStorageProfiles;
	externalCatalogSubscriptionParams: VclExternalCatalogSubscriptionParams;
	remoteUriProbeResult: VclRemoteUriProbeResult;
	publishExternalCatalogParams: VclPublishExternalCatalogParams;
	isPublished: boolean;
	dateCreated: VclXMLGregorianCalendar;
	catalogItems: VclCatalogItems;
	versionNumber: number;
	owner: VclOwner;
	description: string;
	name: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * A list of IP addresses that are sub allocated to edge gateways.
 */
declare class VclSubAllocations {
	readonly subAllocation: VclObjectList;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Provides information to access edge gateway via VC
 * 
 */
declare class VclGatewayBackingRef {
	gatewayId: string;
	vCRef: VclEntityReference;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Information about an individual operating system.
 * 
 */
declare class VclOperatingSystemInfo {
	operatingSystemId: number;
	defaultHardDiskAdapterType: VclDefaultHardDiskAdapter;
	readonly supportedHardDiskAdapter: VclObjectList;
	minimumHardDiskSizeGigabytes: number;
	minimumMemoryMegabytes: number;
	supportLevel: string;
	x64: boolean;
	maximumCpuCount: number;
	maximumCoresPerSocket: number;
	maximumSocketCount: number;
	minimumHardwareVersion: number;
	personalizationEnabled: boolean;
	personalizationAuto: boolean;
	sysprepPackagingSupported: boolean;
	supportsMemHotAdd: boolean;
	cimOsId: number;
	cimVersion: number;
	supportedForCreate: boolean;
	recommendedNIC: VclNIC;
	readonly supportedNICType: VclObjectList;
	internalName: string;
	name: string;
	supported: boolean;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Type for a single adminVAppTemplate query result in records format.
 * 
 */
declare class VclQueryResultAdminVAppTemplateRecord {
	isBusy: boolean;
	isEnabled: boolean;
	org: string;
	creationDate: VclXMLGregorianCalendar;
	ownerName: string;
	isPublished: boolean;
	vdc: string;
	vdcName: string;
	storageProfileName: string;
	isDeployed: boolean;
	isExpired: boolean;
	isVdcEnabled: boolean;
	catalog: string;
	catalogItem: string;
	catalogName: string;
	isGoldMaster: boolean;
	owner: string;
	status: string;
	name: string;
	href: string;
	readonly otherAttributes: VclMap;
	metadata: VclMetaDataEntries;
	id: string;
	type: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Type for a single vAppTemplate query result in records format.
 * 
 */
declare class VclQueryResultVAppTemplateRecord {
	version: number;
	isBusy: boolean;
	isEnabled: boolean;
	org: string;
	creationDate: VclXMLGregorianCalendar;
	ownerName: string;
	isPublished: boolean;
	vdc: string;
	vdcName: string;
	storageProfileName: string;
	isDeployed: boolean;
	isExpired: boolean;
	catalogName: string;
	isGoldMaster: boolean;
	lastSuccessfulSync: VclXMLGregorianCalendar;
	description: string;
	status: string;
	name: string;
	href: string;
	readonly otherAttributes: VclMap;
	metadata: VclMetaDataEntries;
	id: string;
	type: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * A reference to another organization
 * 
 */
declare class VclOrgAssociation {
	orgName: string;
	siteId: string;
	siteName: string;
	orgId: string;
	orgPublicKey: string;
	status: string;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents compute capacity with units.
 * 
 */
declare class VclRootComputeCapacity {
	cpu: VclProviderVdcCapacity;
	memory: VclProviderVdcCapacity;
	isElastic: boolean;
	isHA: boolean;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Type for a single adminCatalogItem query result in records format.
 * 
 */
declare class VclQueryResultAdminCatalogItemRecord {
	entityName: string;
	org: string;
	creationDate: VclXMLGregorianCalendar;
	ownerName: string;
	isPublished: boolean;
	entityType: string;
	vdc: string;
	vdcName: string;
	isExpired: boolean;
	isVdcEnabled: boolean;
	catalog: string;
	catalogName: string;
	owner: string;
	entity: string;
	status: string;
	name: string;
	href: string;
	readonly otherAttributes: VclMap;
	metadata: VclMetaDataEntries;
	id: string;
	type: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * TBS
 */
declare class VclCimUnsignedLong {
	readonly otherAttributes: VclMap;
	value: VclBigInteger;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * Descriptions of virtual disks used within the
 * package
 */
declare class VclDiskSection {
	readonly disk: VclObjectList;
	info: VclMsg;
	readonly otherAttributes: VclMap;
	required: boolean;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * Resource constraints on a
 * VirtualSystemCollection
 */
declare class VclResourceAllocationSection {
	readonly item: VclObjectList;
	info: VclMsg;
	readonly otherAttributes: VclMap;
	required: boolean;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * TBS
 */
declare class VclCimAnySimple {
	readonly otherAttributes: VclMap;
	value: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * TBS
 */
declare class VclCIMVirtualSystemSettingData {
	generation: VclCimUnsignedLong;
	readonly otherAttributes: VclMap;
	recoveryFile: VclCimString;
	snapshotDataRoot: VclCimString;
	suspendDataRoot: VclCimString;
	swapFileDataRoot: VclCimString;
	virtualSystemIdentifier: VclCimString;
	virtualSystemType: VclCimString;
	automaticRecoveryAction: VclAutomaticRecoveryAction;
	automaticShutdownAction: VclAutomaticShutdownAction;
	automaticStartupAction: VclAutomaticStartupAction;
	automaticStartupActionDelay: VclCimDateTime;
	automaticStartupActionSequenceNumber: VclCimUnsignedShort;
	configurationDataRoot: VclCimString;
	configurationID: VclCimString;
	logDataRoot: VclCimString;
	readonly notes: VclObjectList;
	caption: VclVirtualSystemCaption;
	changeableType: VclVirtualSystemChangeable;
	configurationName: VclCimString;
	elementName: VclCimString;
	instanceID: VclCimString;
	creationTime: VclCimDateTime;
	configurationFile: VclCimString;
	description: VclCimString;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * TBS
 */
declare class VclCimByte {
	readonly otherAttributes: VclMap;
	value: any;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * Enumeration of discrete deployment
 * options
 */
declare class VclDeploymentOptionSection {
	readonly configuration: VclObjectList;
	info: VclMsg;
	readonly otherAttributes: VclMap;
	required: boolean;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * TBS
 */
declare class VclDeploymentOptionSectionConfiguration {
	readonly otherAttributes: VclMap;
	description: VclMsg;
	label: VclMsg;
	default: boolean;
	id: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * TBS
 */
declare class VclNetworkSectionNetwork {
	readonly otherAttributes: VclMap;
	description: VclMsg;
	name: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * TBS
 */
declare class VclCimInt {
	readonly otherAttributes: VclMap;
	value: number;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * TBS
 */
declare class VclCIMResourceAllocationSettingData {
	readonly connection: VclObjectList;
	limit: VclCimUnsignedLong;
	generation: VclCimUnsignedLong;
	readonly otherAttributes: VclMap;
	resourceType: VclResource;
	virtualQuantity: VclCimUnsignedLong;
	reservation: VclCimUnsignedLong;
	addressOnParent: VclCimString;
	allocationUnits: VclCimString;
	automaticAllocation: VclCimBoolean;
	automaticDeallocation: VclCimBoolean;
	caption: VclResourceAllocationCaption;
	changeableType: VclResourceAllocationChangeable;
	configurationName: VclCimString;
	consumerVisibility: VclConsumerVisibility;
	elementName: VclCimString;
	readonly hostResource: VclObjectList;
	instanceID: VclCimString;
	mappingBehavior: VclMappingBehavior;
	otherResourceType: VclCimString;
	poolID: VclCimString;
	resourceSubType: VclCimString;
	virtualQuantityUnits: VclCimString;
	description: VclCimString;
	weight: VclCimUnsignedInt;
	address: VclCimString;
	parent: VclCimString;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * TBS
 */
declare class VclQualifierSArray {
	qualifier: boolean;
	readonly otherAttributes: VclMap;
	value: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * TBS
 */
declare class VclProductSectionIcon {
	readonly otherAttributes: VclMap;
	mimeType: string;
	fileRef: string;
	height: number;
	width: number;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * Content describing a virtual system
 */
declare class VclVirtualSystem {
	info: VclMsg;
	readonly otherAttributes: VclMap;
	readonly section: VclAbstractObjectSet;
	name: VclMsg;
	id: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * The base type for all objects in the vCloud model.
 * Has an optional list of links and href and type attributes.
 * 
 */
declare class VclResource {
	readonly otherAttributes: VclMap;
	value: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * TBS
 */
declare class VclResourceAllocationCaption {
	readonly otherAttributes: VclMap;
	value: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * TBS
 */
declare class VclOvfProperty {
	readonly otherAttributes: VclMap;
	readonly valueElement: VclObjectList;
	password: boolean;
	qualifiers: string;
	userConfigurable: boolean;
	valueAttrib: string;
	description: VclMsg;
	label: VclMsg;
	key: string;
	type: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * TBS
 */
declare class VclNetwork {
	readonly otherAttributes: VclMap;
	description: VclMsg;
	name: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * User defined annotation
 */
declare class VclAnnotationSection {
	annotation: VclMsg;
	info: VclMsg;
	readonly otherAttributes: VclMap;
	required: boolean;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * TBS
 */
declare class VclCimReference {
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * Type for alternative default values for properties when
 * DeploymentOptionSection is used
 */
declare class VclPropertyConfigurationValue {
	readonly otherAttributes: VclMap;
	configuration: string;
	value: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * TBS
 */
declare class VclVirtualSystemChangeable {
	readonly otherAttributes: VclMap;
	value: number;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * TBS
 */
declare class VclCimLong {
	readonly otherAttributes: VclMap;
	value: number;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * TBS
 */
declare class VclCimString {
	readonly otherAttributes: VclMap;
	value: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * Product information for a virtual
 * appliance
 */
declare class VclProductSection {
	version: VclCimString;
	vendor: VclMsg;
	product: VclMsg;
	fullVersion: VclCimString;
	productUrl: VclCimString;
	vendorUrl: VclCimString;
	appUrl: VclCimString;
	readonly icon: VclObjectList;
	readonly categoryOrProperty: VclList;
	clazz: string;
	instance: string;
	info: VclMsg;
	readonly otherAttributes: VclMap;
	required: boolean;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * TBS
 */
declare class VclCimUnsignedInt {
	readonly otherAttributes: VclMap;
	value: number;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * TBS
 */
declare class VclIcon {
	readonly otherAttributes: VclMap;
	mimeType: string;
	fileRef: string;
	height: number;
	width: number;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * Type for an external reference to a
 * resource
 */
declare class VclOvfFile {
	href: string;
	readonly otherAttributes: VclMap;
	chunkSize: number;
	compression: string;
	id: string;
	size: VclBigInteger;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * Root OVF descriptor type
 */
declare class VclEnvelope {
	readonly otherAttributes: VclMap;
	readonly section: VclAbstractObjectSet;
	lang: string;
	references: VclOvfReferences;
	readonly strings: VclObjectList;
	content: VclAbstractObject;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * Wrapper for
 * CIM_VirtualSystemSettingData_Type
 */
declare class VclVSSD {
	generation: VclCimUnsignedLong;
	readonly otherAttributes: VclMap;
	recoveryFile: VclCimString;
	snapshotDataRoot: VclCimString;
	suspendDataRoot: VclCimString;
	swapFileDataRoot: VclCimString;
	virtualSystemIdentifier: VclCimString;
	virtualSystemType: VclCimString;
	automaticRecoveryAction: VclAutomaticRecoveryAction;
	automaticShutdownAction: VclAutomaticShutdownAction;
	automaticStartupAction: VclAutomaticStartupAction;
	automaticStartupActionDelay: VclCimDateTime;
	automaticStartupActionSequenceNumber: VclCimUnsignedShort;
	configurationDataRoot: VclCimString;
	configurationID: VclCimString;
	logDataRoot: VclCimString;
	readonly notes: VclObjectList;
	caption: VclVirtualSystemCaption;
	changeableType: VclVirtualSystemChangeable;
	configurationName: VclCimString;
	elementName: VclCimString;
	instanceID: VclCimString;
	creationTime: VclCimDateTime;
	configurationFile: VclCimString;
	description: VclCimString;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * TBS
 */
declare class VclMappingBehavior {
	readonly otherAttributes: VclMap;
	value: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * Type for localizable string
 */
declare class VclMsg {
	readonly otherAttributes: VclMap;
	msgid: string;
	value: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * TBS
 */
declare class VclCimShort {
	readonly otherAttributes: VclMap;
	value: any;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * TBS
 */
declare class VclCimUnsignedShort {
	readonly otherAttributes: VclMap;
	value: number;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * TBS
 */
declare class VclProductSectionProperty {
	readonly otherAttributes: VclMap;
	readonly valueElement: VclObjectList;
	password: boolean;
	qualifiers: string;
	userConfigurable: boolean;
	valueAttrib: string;
	description: VclMsg;
	label: VclMsg;
	key: string;
	type: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * If present indicates that the virtual machine needs to be
 * initially booted to install and configure the software
 */
declare class VclInstallSection {
	initialBootStopDelay: number;
	info: VclMsg;
	readonly otherAttributes: VclMap;
	required: boolean;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * TBS
 */
declare class VclQualifierSInt64 {
	qualifier: boolean;
	readonly otherAttributes: VclMap;
	value: number;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * TBS
 */
declare class VclItem {
	readonly otherAttributes: VclMap;
	startAction: string;
	startDelay: number;
	stopAction: string;
	stopDelay: number;
	waitingForGuest: boolean;
	order: number;
	id: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * TBS
 */
declare class VclQualifierBoolean {
	qualifier: boolean;
	readonly otherAttributes: VclMap;
	value: boolean;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * A collection of Content.
 */
declare class VclVirtualSystemCollection {
	readonly content: VclAbstractObjectSet;
	info: VclMsg;
	readonly otherAttributes: VclMap;
	readonly section: VclAbstractObjectSet;
	name: VclMsg;
	id: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * TBS
 */
declare class VclRASD {
	bound: string;
	configuration: string;
	required: boolean;
	readonly connection: VclObjectList;
	limit: VclCimUnsignedLong;
	generation: VclCimUnsignedLong;
	readonly otherAttributes: VclMap;
	resourceType: VclResource;
	virtualQuantity: VclCimUnsignedLong;
	reservation: VclCimUnsignedLong;
	addressOnParent: VclCimString;
	allocationUnits: VclCimString;
	automaticAllocation: VclCimBoolean;
	automaticDeallocation: VclCimBoolean;
	caption: VclResourceAllocationCaption;
	changeableType: VclResourceAllocationChangeable;
	configurationName: VclCimString;
	consumerVisibility: VclConsumerVisibility;
	elementName: VclCimString;
	readonly hostResource: VclObjectList;
	instanceID: VclCimString;
	mappingBehavior: VclMappingBehavior;
	otherResourceType: VclCimString;
	poolID: VclCimString;
	resourceSubType: VclCimString;
	virtualQuantityUnits: VclCimString;
	description: VclCimString;
	weight: VclCimUnsignedInt;
	address: VclCimString;
	parent: VclCimString;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * Specification of the operating system installed in the
 * guest
 */
declare class VclOperatingSystemSection {
	version: string;
	description: VclMsg;
	id: number;
	info: VclMsg;
	readonly otherAttributes: VclMap;
	required: boolean;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * TBS
 */
declare class VclCimUnsignedByte {
	readonly otherAttributes: VclMap;
	value: any;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * Specifies virtual hardware requirements for a virtual
 * machine
 */
declare class VclVirtualHardwareSection {
	transport: string;
	readonly item: VclObjectList;
	system: VclVSSD;
	id: string;
	info: VclMsg;
	readonly otherAttributes: VclMap;
	required: boolean;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * Type for list of external resources
 */
declare class VclOvfReferences {
	readonly otherAttributes: VclMap;
	readonly file: VclObjectList;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * TBS
 */
declare class VclVirtualSystemCaption {
	readonly otherAttributes: VclMap;
	value: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * Descriptions of logical networks used within the
 * package
 */
declare class VclNetworkSection {
	readonly network: VclObjectList;
	info: VclMsg;
	readonly otherAttributes: VclMap;
	required: boolean;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * TBS
 */
declare class VclStringsMsg {
	readonly otherAttributes: VclMap;
	msgid: string;
	value: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * TBS
 */
declare class VclCimHexBinary {
	readonly otherAttributes: VclMap;
	value: any;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * TBS
 */
declare class VclCimChar16 {
	readonly otherAttributes: VclMap;
	value: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * Type for virtual disk descriptor
 */
declare class VclVirtualDiskDesc {
	format: string;
	readonly otherAttributes: VclMap;
	diskId: string;
	capacity: string;
	parentRef: string;
	capacityAllocationUnits: string;
	populatedSize: number;
	fileRef: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * TBS
 */
declare class VclAutomaticShutdownAction {
	readonly otherAttributes: VclMap;
	value: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * TBS
 */
declare class VclCimBoolean {
	readonly otherAttributes: VclMap;
	value: boolean;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * TBS
 */
declare class VclCimDateTime {
	date: VclXMLGregorianCalendar;
	readonly otherAttributes: VclMap;
	interval: VclDuration;
	cIMDateTime: VclObject;
	datetime: VclXMLGregorianCalendar;
	time: VclXMLGregorianCalendar;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * Type for root OVF environment
 */
declare class VclEnvironment {
	readonly otherAttributes: VclMap;
	readonly section: VclAbstractObjectSet;
	readonly entity: VclObjectList;
	id: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * Container of sections for a specific entity
 * 
 */
declare class VclEntity {
	readonly otherAttributes: VclMap;
	readonly section: VclAbstractObjectSet;
	id: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * Key/value pairs of assigned properties for an
 * entity
 */
declare class VclPropertySection {
	readonly property: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * Information about deployment platform
 */
declare class VclPlatformSection {
	locale: VclCimString;
	version: VclCimString;
	vendor: VclCimString;
	kind: VclCimString;
	timezone: number;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * TBS
 */
declare class VclOvfEnvironmentProperty {
	readonly otherAttributes: VclMap;
	value: string;
	key: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * TBS
 */
declare class VclStartupSectionItem {
	readonly otherAttributes: VclMap;
	startAction: string;
	startDelay: number;
	stopAction: string;
	stopDelay: number;
	waitingForGuest: boolean;
	order: number;
	id: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * TBS
 */
declare class VclQualifierUInt32 {
	qualifier: boolean;
	readonly otherAttributes: VclMap;
	value: number;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * TBS
 */
declare class VclAutomaticRecoveryAction {
	readonly otherAttributes: VclMap;
	value: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * TBS
 */
declare class VclCimDouble {
	readonly otherAttributes: VclMap;
	value: number;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * TBS
 */
declare class VclConfiguration {
	readonly otherAttributes: VclMap;
	description: VclMsg;
	label: VclMsg;
	default: boolean;
	id: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * Type for string resource bundle
 */
declare class VclStrings {
	readonly otherAttributes: VclMap;
	readonly msg: VclObjectList;
	fileRef: string;
	lang: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * End-User License Agreement
 */
declare class VclEulaSection {
	license: VclMsg;
	info: VclMsg;
	readonly otherAttributes: VclMap;
	required: boolean;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * TBS
 */
declare class VclQualifierString {
	qualifier: boolean;
	readonly otherAttributes: VclMap;
	value: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * TBS
 */
declare class VclConsumerVisibility {
	readonly otherAttributes: VclMap;
	value: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * TBS
 */
declare class VclCimBase64Binary {
	readonly otherAttributes: VclMap;
	value: any;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * Config element
 */
declare class VclConfig {
	required: boolean;
	value: string;
	key: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * Specifies the IP Assignment policy that is
 * supported
 */
declare class VclIpAssignmentSection {
	schemes: string;
	protocols: string;
	info: VclMsg;
	readonly otherAttributes: VclMap;
	required: boolean;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * Specifies the boot device for this VirtualSystem
 */
declare class VclBootOrderSection {
	instanceId: number;
	type: string;
	info: VclMsg;
	readonly otherAttributes: VclMap;
	required: boolean;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * TBS
 */
declare class VclLevel {
	readonly otherAttributes: VclMap;
	vendor: string;
	eax: string;
	ebx: string;
	ecx: string;
	edx: string;
	level: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * TBS
 */
declare class VclStorageGroupSection {
	readonly description: VclObjectList;
	info: VclMsg;
	readonly otherAttributes: VclMap;
	required: boolean;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * TBS
 */
declare class VclDescription {
	content: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * CPU Architecture requirements for guest
 * software
 */
declare class VclCpuCompatibilitySection {
	readonly level: VclObjectList;
	info: VclMsg;
	readonly otherAttributes: VclMap;
	required: boolean;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * ExtraConfig element
 */
declare class VclExtraConfig {
	required: boolean;
	value: string;
	key: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * TBS
 */
declare class VclStorageSection {
	type: string;
	info: VclMsg;
	readonly otherAttributes: VclMap;
	required: boolean;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * Type for CoresPerSocket element
 */
declare class VclCoresPerSocket {
	required: boolean;
	readonly otherAttributes: VclMap;
	value: number;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * Specifies the order in which entities in a
 * VirtualSystemCollection are powered on and shut down
 */
declare class VclStartupSection {
	readonly item: VclObjectList;
	info: VclMsg;
	readonly otherAttributes: VclMap;
	required: boolean;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * TBS
 */
declare class VclAutomaticStartupAction {
	readonly otherAttributes: VclMap;
	value: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * TBS
 */
declare class VclResourceAllocationChangeable {
	readonly otherAttributes: VclMap;
	value: number;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * TBS
 */
declare class VclCimFloat {
	readonly otherAttributes: VclMap;
	value: number;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Type for a single adminMedia query result in records format.
 * 
 */
declare class VclQueryResultAdminMediaRecord {
	isBusy: boolean;
	org: string;
	creationDate: VclXMLGregorianCalendar;
	ownerName: string;
	isPublished: boolean;
	vdc: string;
	vdcName: string;
	storageProfileName: string;
	isVdcEnabled: boolean;
	catalog: string;
	catalogItem: string;
	catalogName: string;
	storageB: number;
	owner: string;
	status: string;
	name: string;
	href: string;
	readonly otherAttributes: VclMap;
	metadata: VclMetaDataEntries;
	id: string;
	type: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents a vCloud Director server group that this session is authorized to access.
 * 
 */
declare class VclAuthorizedLocation {
	orgName: string;
	siteName: string;
	locationId: string;
	locationName: string;
	restApiEndpoint: string;
	uIEndpoint: string;
	useMultisiteToken: boolean;
	authContext: string;
	description: string;
	name: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Type for a single right query result in records format.
 * 
 */
declare class VclQueryResultRightRecord {
	category: string;
	name: string;
	href: string;
	readonly otherAttributes: VclMap;
	metadata: VclMetaDataEntries;
	id: string;
	type: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Type for a single fromCloudTunnel query result in records format.
 * 
 */
declare class VclQueryResultFromCloudTunnelRecord {
	org: string;
	destinationId: string;
	trafficType: string;
	destinationPort: number;
	destinationHost: string;
	href: string;
	readonly otherAttributes: VclMap;
	metadata: VclMetaDataEntries;
	id: string;
	type: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * A named collection of rights.
 * 
 */
declare class VclRoleParams {
	rightReferences: VclRightReferences;
	description: string;
	name: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Type for a single apiDefinition query result in records format.
 * 
 */
declare class VclQueryResultApiDefinitionRecord {
	serviceNamespace: string;
	apiVendor: string;
	serviceName: string;
	serviceVendor: string;
	entryPoint: string;
	namespace: string;
	service: string;
	name: string;
	href: string;
	readonly otherAttributes: VclMap;
	metadata: VclMetaDataEntries;
	id: string;
	type: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * General purpose extension element. Not related to extension
 * services.
 * 
 */
declare class VclVCloudExtension {
	readonly otherAttributes: VclMap;
	required: boolean;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Container for AuthorizedLocationType
 * 
 */
declare class VclAuthorizedLocations {
	readonly location: VclObjectList;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * TBS
 */
declare class VclPvdcComputePolicy {
	policyDescription: string;
	namedVmGroupReferences: VclVMWVmGroupReferences;
	description: string;
	name: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * A description of a virtual disk of a VM.
 * 
 */
declare class VclDiskSettings {
	adapterType: string;
	storageProfile: VclReference;
	disk: VclReference;
	unitNumber: number;
	busNumber: number;
	sizeMb: number;
	thinProvisioned: boolean;
	overrideVmDefault: boolean;
	iops: number;
	virtualQuantityUnit: string;
	virtualQuantity: number;
	diskId: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Type for a single disk query result in records format.
 * 
 */
declare class VclQueryResultDiskRecord {
	task: string;
	storageProfile: string;
	ownerName: string;
	datastoreName: string;
	vdc: string;
	vdcName: string;
	attachedVmCount: number;
	busSubType: string;
	busType: string;
	busTypeDesc: string;
	datastore: string;
	isAttached: boolean;
	sizeB: number;
	storageProfileName: string;
	description: string;
	status: string;
	name: string;
	href: string;
	readonly otherAttributes: VclMap;
	metadata: VclMetaDataEntries;
	id: string;
	type: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Parameters to be used for virtual machine relocation.
 * 
 */
declare class VclRelocateParams {
	datastore: VclReference;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Public representation of an external service.
 * 
 */
declare class VclServiceData {
	vendor: string;
	namespace: string;
	description: string;
	name: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents Static Routing network service.
 * 
 */
declare class VclStaticRoutingService {
	readonly staticRoute: VclObjectList;
	isEnabled: boolean;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents configuration for an AMQP broker
 * 
 */
declare class VclAmqpBrokerConfiguration {
	amqpHost: string;
	amqpPort: number;
	amqpUsername: string;
	amqpPassword: string;
	amqpVHost: string;
	amqpUseSSL: boolean;
	amqpSslAcceptAll: boolean;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Container for all ProductSection elements in a VAppTemplate,
 * VApp, or Vm object.
 * 
 */
declare class VclProductSectionList {
	readonly productSection: VclObjectList;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Container for references to storage profiles associated with a
 * Provider vDC.
 * 
 */
declare class VclProviderVdcStorageProfiles {
	readonly providerVdcStorageProfile: VclObjectList;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Public URL and certificate of the Cloud Proxy end point.
 * 
 */
declare class VclHybridSettings {
	cloudProxyFromCloudTunnelHostOverride: string;
	cloudProxyBaseUri: string;
	cloudProxyBaseUriPublicCertChain: string;
	cloudProxyBaseUriOverride: string;
	cloudProxyBaseUriPublicCertChainOverride: string;
	cloudProxyFromCloudTunnelHost: string;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Lists links to hybrid operations and entities in the context of an Org.
 * 
 */
declare class VclHybridOrg {
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Maps a network name specified in a Vm to the network name of a
 * vApp network defined in the VApp that contains the Vm
 * 
 */
declare class VclNetworkAssignment {
	containerNetwork: string;
	innerNetwork: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Type for a single datastore query result in records format.
 * 
 */
declare class VclQueryResultDatastoreRecord {
	isEnabled: boolean;
	storageUsedMB: number;
	isDeleted: boolean;
	moref: string;
	vcName: string;
	iopsAllocated: number;
	iopsCapacity: number;
	vc: string;
	datastoreType: string;
	numberOfProviderVdcs: number;
	provisionedStorageMB: number;
	requestedStorageMB: number;
	storageMB: number;
	name: string;
	href: string;
	readonly otherAttributes: VclMap;
	metadata: VclMetaDataEntries;
	id: string;
	type: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Base type for blocking tasks.
 * 
 */
declare class VclTaskRequestBase {
	timeoutAction: string;
	user: VclReference;
	taskOwner: VclReference;
	createdTime: VclXMLGregorianCalendar;
	timeoutDate: VclXMLGregorianCalendar;
	organization: VclReference;
	description: string;
	name: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * A set of overrides to source VM properties to apply to target VM during copying.
 * 
 */
declare class VclVmGeneralParams {
	needsCustomization: boolean;
	regenerateBiosUuid: boolean;
	description: string;
	name: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Type for a single licensingVirtualMachine query result in records format.
 * 
 */
declare class VclQueryResultLicensingVirtualMachineRecord {
	managedObjectReference: string;
	observationDate: VclXMLGregorianCalendar;
	parentSampleId: string;
	virtualCenterId: string;
	memory: number;
	virtualCpuCount: number;
	href: string;
	readonly otherAttributes: VclMap;
	metadata: VclMetaDataEntries;
	id: string;
	type: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * UTC format. For example 2012-06-18T12:00:00-05:00
 * 
 */
declare class VclMetadataDateTimeValue {
	value: VclXMLGregorianCalendar;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * UTF-8 character set. Strings longer than 1000
 * characters cannot be searched for in a query.
 * 
 */
declare class VclMetadataStringValue {
	value: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Type for a single virtualCenter query result in records format.
 * 
 */
declare class VclQueryResultVirtualCenterRecord {
	isBusy: boolean;
	isEnabled: boolean;
	isSupported: boolean;
	listenerState: string;
	uuid: string;
	vcVersion: string;
	vsmIP: string;
	url: string;
	userName: string;
	status: string;
	name: string;
	href: string;
	readonly otherAttributes: VclMap;
	metadata: VclMetaDataEntries;
	id: string;
	type: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Type for a single adminGroup query result in records format.
 * 
 */
declare class VclQueryResultAdminGroupRecord {
	org: string;
	isReadOnly: boolean;
	identityProviderType: string;
	roleName: string;
	name: string;
	href: string;
	readonly otherAttributes: VclMap;
	metadata: VclMetaDataEntries;
	id: string;
	type: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents connection details for an organization's LDAP
 * service.
 * 
 */
declare class VclCustomOrgLdapSettings {
	isSsl: boolean;
	isSslAcceptAll: boolean;
	customTruststore: string;
	searchBase: string;
	authenticationMechanism: string;
	groupSearchBase: string;
	isGroupSearchBaseEnabled: boolean;
	connectorType: string;
	userAttributes: VclOrgLdapUserAttributes;
	groupAttributes: VclOrgLdapGroupAttributes;
	useExternalKerberos: boolean;
	realm: string;
	password: string;
	userName: string;
	port: number;
	hostName: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Contains OAuth identity provider settings for an organization.
 * 
 */
declare class VclOrgOAuthSettings {
	enabled: boolean;
	issuerId: string;
	oAuthKeyConfigurations: VclOAuthKeyConfigurationsList;
	clientId: string;
	clientSecret: string;
	userAuthorizationEndpoint: string;
	accessTokenEndpoint: string;
	userInfoEndpoint: string;
	scimEndpoint: string;
	oIDCAttributeMapping: VclOIDCAttributeMapping;
	maxClockSkew: number;
	readonly scope: VclList;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * TBS
 */
declare class VclExternalNatIpEntry {
	destinationNatIp: string;
	sourceNatIp: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * A list of information for allocated IP addresses.
 */
declare class VclAllocatedIpAddresses {
	readonly ipAddress: VclObjectList;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Container for this organization's settings.
 * 
 */
declare class VclOrgSettings {
	orgGeneralSettings: VclOrgGeneralSettings;
	vAppLeaseSettings: VclOrgLeaseSettings;
	vAppTemplateLeaseSettings: VclOrgVAppTemplateLeaseSettings;
	orgLdapSettings: VclOrgLdapSettings;
	orgEmailSettings: VclOrgEmailSettings;
	orgPasswordPolicySettings: VclOrgPasswordPolicySettings;
	orgOperationLimitsSettings: VclOrgOperationLimitsSettings;
	orgFederationSettings: VclOrgFederationSettings;
	orgOAuthSettings: VclOrgOAuthSettings;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Container for the specification of this virtual machine.
 * This is an alternate to using ovf:VirtualHardwareSection + ovf:OperatingSystemSection
 * 
 */
declare class VclVmSpecSection {
	hardwareVersion: VclHardwareVersion;
	vmToolsVersion: string;
	toolsGuestOsId: string;
	virtualCpuType: VclVirtualCpuTypeType;
	timeSyncWithHost: boolean;
	modified: boolean;
	osType: string;
	numCpus: number;
	numCoresPerSocket: number;
	cpuResourceMhz: VclComputeResource;
	memoryResourceMb: VclComputeResource;
	mediaSection: VclMediaSection;
	diskSection: VclDiskSectionDefault;
	info: VclMsg;
	readonly otherAttributes: VclMap;
	required: boolean;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Parameters for a createSnapshot request.
 * 
 */
declare class VclCreateSnapshotParams {
	quiesce: boolean;
	memory: boolean;
	description: string;
	name: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Parameters for creating an organization vDC.
 * 
 */
declare class VclCreateVdcParams {
	isEnabled: boolean;
	readonly vdcStorageProfile: VclObjectList;
	nicQuota: number;
	vmQuota: number;
	providerVdcReference: VclReference;
	allocationModel: string;
	computeCapacity: VclComputeCapacity;
	networkQuota: number;
	resourceGuaranteedMemory: any;
	resourceGuaranteedCpu: any;
	vCpuInMhz: number;
	isThinProvision: boolean;
	networkPoolReference: VclReference;
	resourcePoolRefs: VclVimObjectRefs;
	usesFastProvisioning: boolean;
	overCommitAllowed: boolean;
	vmDiscoveryEnabled: boolean;
	description: string;
	name: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * TBS
 */
declare class VclRemoteUriProbeResult {
	errorMessage: string;
	sslCertificateEncoded: any;
	sslThumbprint: string;
	status: VclRemoteUriProbeResultStatus;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * TBS
 */
declare class VclQueryResultNsxTManagerRecord {
	url: string;
	name: string;
	href: string;
	readonly otherAttributes: VclMap;
	metadata: VclMetaDataEntries;
	id: string;
	type: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * TBS
 */
declare class VclSamlAttributeMapping {
	emailAttributeName: string;
	userNameAttributeName: string;
	firstNameAttributeName: string;
	surnameAttributeName: string;
	fullNameAttributeName: string;
	groupAttributeName: string;
	roleAttributeName: string;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * A signed 8-byte integer.
 * 
 */
declare class VclMetadataNumberValue {
	value: number;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Gateway Interface configuration.
 * 
 */
declare class VclGatewayInterface {
	useForDefaultRoute: boolean;
	network: VclReference;
	interfaceType: string;
	readonly subnetParticipation: VclObjectList;
	applyRateLimit: boolean;
	inRateLimit: any;
	outRateLimit: any;
	name: string;
	displayName: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Container type for query results in records format.
 * 
 */
declare class VclQueryResult {
	pageSize: VclBigInteger;
	page: VclBigInteger;
	name: string;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Type for a single orgVdcStorageProfile query result in records format.
 * 
 */
declare class VclQueryResultOrgVdcStorageProfileRecord {
	isEnabled: boolean;
	storageLimitMB: number;
	storageUsedMB: number;
	iopsAllocated: number;
	numberOfConditions: number;
	vdc: string;
	vdcName: string;
	iopsLimit: number;
	isDefaultStorageProfile: boolean;
	isVdcBusy: boolean;
	name: string;
	href: string;
	readonly otherAttributes: VclMap;
	metadata: VclMetaDataEntries;
	id: string;
	type: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents the NAT rule for port forwarding between internal
 * IP/port and external IP/port.
 * 
 */
declare class VclNatPortForwardingRule {
	externalIpAddress: string;
	externalPort: number;
	internalPort: number;
	internalIpAddress: string;
	protocol: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents a data point in a metric series
 * 
 */
declare class VclSample {
	timestamp: VclXMLGregorianCalendar;
	value: any;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * TBS
 */
declare class VclBusUnitNumber {
	unitNumber: number;
	busNumber: number;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents information about vApp and virtual machine
 * snapshots.
 * 
 */
declare class VclSnapshotSection {
	href: string;
	snapshot: VclSnapshot;
	type: string;
	info: VclMsg;
	readonly otherAttributes: VclMap;
	required: boolean;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Type for a single adminVMDiskRelation query result in records format.
 * 
 */
declare class VclQueryResultAdminVmDiskRelationRecord {
	disk: string;
	vdc: string;
	vm: string;
	href: string;
	readonly otherAttributes: VclMap;
	metadata: VclMetaDataEntries;
	id: string;
	type: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Describes from-the-cloud tunnel
 * 
 */
declare class VclFromCloudTunnel {
	destinationId: string;
	trafficType: string;
	sourceId: string;
	endpointTag: string;
	description: string;
	name: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Defines a gateway and NAT Routed organization VDC network to be created.
 * 
 */
declare class VclVdcTemplateSpecificationGatewayConfiguration {
	gateway: VclGatewayParams;
	network: VclOrgVdcNetworkParams;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Parameters for registering cloud part of the tunneling application.
 * 
 */
declare class VclTunnelingApplicationRegisterParams {
	trafficType: string;
	routingKey: string;
	exchange: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Type for a single providerVdcStorageProfile query result in records format.
 * 
 */
declare class VclQueryResultProviderVdcStorageProfileRecord {
	isEnabled: boolean;
	providerVdc: string;
	storageUsedMB: number;
	iopsAllocated: number;
	iopsCapacity: number;
	numberOfConditions: number;
	storageProfileMoref: string;
	storageProvisionedMB: number;
	storageRequestedMB: number;
	storageTotalMB: number;
	vc: string;
	name: string;
	href: string;
	readonly otherAttributes: VclMap;
	metadata: VclMetaDataEntries;
	id: string;
	type: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents operation limits settings for an organization. An
 * operation is defined as resource-intensive if it returns a Task
 * object. The default value for all operation limits settings is
 * 0, which specifies no limit.
 * 
 */
declare class VclOrgOperationLimitsSettings {
	queuedOperationsPerUser: number;
	queuedOperationsPerOrg: number;
	consolesPerVmLimit: number;
	operationsPerUser: number;
	operationsPerOrg: number;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents a list of extension service API versions.
 * 
 */
declare class VclVersions {
	readonly version: VclList;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * TBS
 */
declare class VclAllocationPoolVdcSummary {
	memoryConsumptionMB: number;
	memoryReservationMB: number;
	storageConsumptionMB: number;
	cpuReservationMhz: number;
	cpuConsumptionMhz: number;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Range of integer values.
 * 
 */
declare class VclRange {
	begin: number;
	end: number;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * IP range sub allocated to an edge gateway.
 * 
 */
declare class VclSubAllocation {
	ipRanges: VclIpRanges;
	edgeGateway: VclReference;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents DHCP pool service.
 * 
 */
declare class VclDhcpPoolService {
	isEnabled: boolean;
	network: VclReference;
	defaultLeaseTime: number;
	maxLeaseTime: number;
	lowIpAddress: string;
	highIpAddress: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents a vApp, vApp template or Vm to include in a
 * composed vApp.
 * 
 */
declare class VclSourcedCompositionItemParam {
	storageProfile: VclReference;
	localityParams: VclLocalityParams;
	sourceDelete: boolean;
	vmGeneralParams: VclVmGeneralParams;
	vAppScopedLocalId: string;
	instantiationParams: VclInstantiationParams;
	readonly networkAssignment: VclObjectList;
	vdcComputePolicy: VclReference;
	vmCapabilities: VclVmCapabilities;
	source: VclReference;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Specification for a VDC template using the ReservationPool allocation model.
 * 
 */
declare class VclReservationPoolVdcTemplateSpecification {
	cpuAllocationMhz: number;
	memoryAllocationMB: number;
	readonly storageProfile: VclObjectList;
	nicQuota: number;
	vmQuota: number;
	provisionedNetworkQuota: number;
	gatewayConfiguration: VclVdcTemplateSpecificationGatewayConfiguration;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents resource capacity in a Provider vDC.
 * 
 */
declare class VclProviderVdcCapacity {
	units: string;
	allocation: number;
	reserved: number;
	used: number;
	overhead: number;
	total: number;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents a virtual hard disk adapter type.
 * 
 */
declare class VclDefaultHardDiskAdapter {
	value: number;
	ref: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents persistence type for a load balancer service profile.
 * 
 */
declare class VclLBPersistence {
	cookieName: string;
	cookieMode: string;
	method: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * The ticket for accessing the console of a VM.
 * 
 */
declare class VclScreenTicket {
	value: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Parameters required when sharing a catalog with other
 * organizations.
 * 
 */
declare class VclPublishCatalogParams {
	isPublished: boolean;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Parameters for keystore update.
 * 
 */
declare class VclKeystoreUpdateParams {
	password: string;
	fileSize: number;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents a load balancer virtual server.
 * 
 */
declare class VclLoadBalancerVirtualServer {
	isEnabled: boolean;
	ipAddress: string;
	interface: VclReference;
	readonly serviceProfile: VclObjectList;
	logging: boolean;
	readonly loadBalancerTemplates: VclObjectList;
	description: string;
	name: string;
	pool: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Grouping of IOPs related settings associated with a particular VdcStorageProfile, i.e. a particular
 * Vdc + StorageProfile pairing.
 * 
 */
declare class VclVdcStorageProfileIopsSettings {
	enabled: boolean;
	diskIopsMax: number;
	diskIopsDefault: number;
	storageProfileIopsLimit: number;
	diskIopsPerGbMax: number;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents a NAT network service.
 * 
 */
declare class VclNatService {
	externalIp: string;
	natType: string;
	readonly natRule: VclObjectList;
	policy: string;
	isEnabled: boolean;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents a capacity and usage of a given resource.
 * 
 */
declare class VclCapacityWithUsage {
	reserved: number;
	used: number;
	overhead: number;
	limit: number;
	allocated: number;
	units: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Parameters for certificate update.
 * 
 */
declare class VclCertificateUpdateParams {
	fileSize: number;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents a supported virtual NIC type.
 * 
 */
declare class VclNIC {
	name: string;
	id: number;
	content: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Type for a single externalLocalization query result in records format.
 * 
 */
declare class VclQueryResultExternalLocalizationRecord {
	locale: string;
	serviceNamespace: string;
	value: string;
	key: string;
	href: string;
	readonly otherAttributes: VclMap;
	metadata: VclMetaDataEntries;
	id: string;
	type: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Tenant syslog server settings.
 * If logging is configured for firewall rules, the logs will be directed to these syslog servers
 * along with the provider syslog servers.
 * Only one tenant syslog server ip can be configured.
 * 
 */
declare class VclTenantSyslogServerSettings {
	readonly syslogServerIp: VclList;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents details of an IPSec-VPN tunnel.
 * 
 */
declare class VclIpsecVpnTunnel {
	isEnabled: boolean;
	errorDetails: string;
	ipsecVpnPeer: VclAbstractObject;
	peerIpAddress: string;
	peerNetworkAddress: string;
	peerNetworkMask: string;
	sharedSecret: string;
	encryptionProtocol: string;
	mtu: number;
	isOperational: boolean;
	description: string;
	name: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Allows to chose which subnets a gateway can be part of
 * 
 */
declare class VclSubnetParticipation {
	gateway: string;
	netmask: string;
	subnetPrefixLength: number;
	ipAddress: string;
	ipRanges: VclIpRanges;
	useForDefaultRoute: boolean;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * A list of the existing from-the-cloud tunnel listeners.
 * 
 */
declare class VclFromCloudTunnelListenerList {
	readonly fromCloudTunnelListener: VclObjectList;
	pageSize: number;
	page: number;
	total: number;
	name: string;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents a list of references to vDCs.
 * 
 */
declare class VclVdcs {
	readonly vdc: VclObjectList;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Type for a single edgeGateway query result in records format.
 * 
 */
declare class VclQueryResultEdgeGatewayRecord {
	isBusy: boolean;
	vdc: string;
	advancedNetworkingEnabled: boolean;
	distributedRoutingEnabled: boolean;
	availableNetCount: number;
	gatewayStatus: string;
	haStatus: string;
	numberOfExtNetworks: number;
	numberOfOrgNetworks: number;
	orgVdcName: string;
	name: string;
	href: string;
	readonly otherAttributes: VclMap;
	metadata: VclMetaDataEntries;
	id: string;
	type: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Type for a single adminVApp query result in records format.
 * 
 */
declare class VclQueryResultAdminVAppRecord {
	cpuAllocationMhz: number;
	isBusy: boolean;
	isEnabled: boolean;
	memoryAllocationMB: number;
	numberOfVMs: number;
	org: string;
	creationDate: VclXMLGregorianCalendar;
	ownerName: string;
	vdc: string;
	isInMaintenanceMode: boolean;
	vdcName: string;
	isAutoNature: boolean;
	isDeployed: boolean;
	isExpired: boolean;
	storageKB: number;
	isVdcEnabled: boolean;
	status: string;
	name: string;
	href: string;
	readonly otherAttributes: VclMap;
	metadata: VclMetaDataEntries;
	id: string;
	type: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents an operating system family.
 * 
 */
declare class VclOperatingSystemFamilyInfo {
	operatingSystemFamilyId: number;
	readonly operatingSystem: VclObjectList;
	name: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * VM's with NICs that are connected directly to a VDC network and where the NICs
 * have manually assigned IP addresses needs to be mapped to a valid IP address
 * within the IP range of the destination site's VDC network.
 * This type provides that mapping.
 * 
 */
declare class VclNicIpMap {
	readonly entry: VclObjectList;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents an OVF property value assignment.
 * 
 */
declare class VclInstantiateOvfProperty {
	instanceId: string;
	classId: string;
	value: string;
	key: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents vDC compute capacity.
 * 
 */
declare class VclComputeCapacity {
	cpu: VclCapacityWithUsage;
	memory: VclCapacityWithUsage;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents vApp template instantiation parameters.
 * 
 */
declare class VclInstantiateVAppTemplateParams {
	allEULAsAccepted: boolean;
	isSourceDelete: boolean;
	readonly sourcedItem: VclObjectList;
	linkedClone: boolean;
	readonly sourcedVmInstantiationParams: VclObjectList;
	source: VclReference;
	instantiationParams: VclInstantiationParams;
	vAppParent: VclReference;
	deploy: boolean;
	powerOn: boolean;
	description: string;
	name: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Type for a single condition query result in records format.
 * 
 */
declare class VclQueryResultConditionRecord {
	details: string;
	occurenceDate: VclXMLGregorianCalendar;
	summary: string;
	objectType: string;
	severity: string;
	object: string;
	href: string;
	readonly otherAttributes: VclMap;
	metadata: VclMetaDataEntries;
	id: string;
	type: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * Type for list of external resources
 */
declare class VclReferences {
	readonly reference: VclObjectSet;
	pageSize: number;
	page: number;
	total: number;
	name: string;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Container for VdcTemplateType
 * 
 */
declare class VclVdcTemplates {
	readonly vdcTemplate: VclObjectList;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents the status of a single site.
 * 
 */
declare class VclSiteStatus {
	siteId: string;
	siteName: string;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Checks the status of if post guest customization script should be checked. Internal Use only.
 * 
 */
declare class VclVmCheckPGC {
	checkPostGCStatus: boolean;
	vAppParent: VclReference;
	deployed: boolean;
	readonly section: VclAbstractObjectSet;
	dateCreated: VclXMLGregorianCalendar;
	files: VclFilesList;
	status: number;
	description: string;
	name: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * TBS
 */
declare class VclNicIpEntry {
	sourceNicIp: string;
	destinationNicIp: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents a service port health check list.
 * 
 */
declare class VclLBPoolHealthCheck {
	uri: string;
	healthThreshold: string;
	unhealthThreshold: string;
	interval: string;
	mode: string;
	timeout: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Type for a single datastoreProviderVdcRelation query result in records format.
 * 
 */
declare class VclQueryResultDatastoreProviderVdcRelationRecord {
	isEnabled: boolean;
	providerVdc: string;
	storageUsedMB: number;
	isDeleted: boolean;
	moref: string;
	vcName: string;
	vc: string;
	datastoreType: string;
	provisionedStorageMB: number;
	requestedStorageMB: number;
	storageMB: number;
	datastore: string;
	name: string;
	href: string;
	readonly otherAttributes: VclMap;
	metadata: VclMetaDataEntries;
	id: string;
	type: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Type for a single adminEvent query result in records format.
 * 
 */
declare class VclQueryResultAdminEventRecord {
	entityName: string;
	details: string;
	org: string;
	orgName: string;
	entityType: string;
	eventId: string;
	eventStatus: number;
	productVersion: string;
	serviceNamespace: string;
	description: string;
	timeStamp: VclXMLGregorianCalendar;
	eventType: string;
	entity: string;
	userName: string;
	href: string;
	readonly otherAttributes: VclMap;
	metadata: VclMetaDataEntries;
	id: string;
	type: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Type for a single providerVdcResourcePoolRelation query result in records format.
 * 
 */
declare class VclQueryResultProviderVdcResourcePoolRelationRecord {
	isEnabled: boolean;
	numberOfVMs: number;
	providerVdc: string;
	vcName: string;
	vc: string;
	isPrimary: boolean;
	cpuReservationAllocationMhz: number;
	cpuReservationLimitMhz: number;
	memoryReservationAllocationMB: number;
	memoryReservationLimitMB: number;
	resourcePoolMoref: string;
	name: string;
	href: string;
	readonly otherAttributes: VclMap;
	metadata: VclMetaDataEntries;
	id: string;
	type: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Syslog server settings.
 * If logging is configured for firewall rules, the logs will be directed to these syslog servers.
 * 
 */
declare class VclSyslogServerSettings {
	syslogServerIp1: string;
	syslogServerIp2: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * TBS
 */
declare class VclMultisiteSessionUserInfo {
	fullName: string;
	idpGroups: VclIdpGroups;
	memberProviderType: string;
	idpRecommendedRoles: VclIdpRecommendedRoles;
	username: string;
	email: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * A reference to a vCloud entity.
 * 
 */
declare class VclEntityReference {
	name: string;
	id: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * A list of IP addresses.
 * 
 */
declare class VclIpAddresses {
	readonly ipAddress: VclList;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents the admin view of an organization.
 * 
 */
declare class VclAdminOrgParams {
	users: VclUsersList;
	groups: VclGroupsList;
	catalogs: VclCatalogsList;
	vdcs: VclVdcs;
	vdcTemplates: VclVdcTemplates;
	networks: VclNetworks;
	orgAssociations: VclOrgAssociations;
	rightReferences: VclOrganizationRights;
	roleReferences: VclOrganizationRoles;
	roleTemplateReferences: VclRoleReferences;
	settings: VclOrgSettings;
	isEnabled: boolean;
	fullName: string;
	description: string;
	name: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Container for references to users.
 * 
 */
declare class VclUsersList {
	readonly userReference: VclObjectList;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents a service port in a load balancer pool.
 * 
 */
declare class VclLBPoolServicePort {
	isEnabled: boolean;
	healthCheckPort: string;
	readonly healthCheck: VclObjectList;
	algorithm: string;
	protocol: string;
	port: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Type for a single cell query result in records format.
 * 
 */
declare class VclQueryResultCellRecord {
	version: string;
	buildDate: VclXMLGregorianCalendar;
	isActive: number;
	isVMwareVc: number;
	primaryIp: string;
	name: string;
	href: string;
	readonly otherAttributes: VclMap;
	metadata: VclMetaDataEntries;
	id: string;
	type: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Gives more details of local peer end point.
 * 
 */
declare class VclIpsecVpnLocalPeer {
	name: string;
	id: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Type for a single vmDiskRelation query result in records format.
 * 
 */
declare class VclQueryResultVmDiskRelationRecord {
	disk: string;
	vdc: string;
	vm: string;
	href: string;
	readonly otherAttributes: VclMap;
	metadata: VclMetaDataEntries;
	id: string;
	type: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Container for references to rights.
 * 
 */
declare class VclRightReferences {
	readonly rightReference: VclObjectList;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents details of an vm+nic+iptype selection.
 * 
 */
declare class VclVmSelection {
	vAppScopedVmId: string;
	vmNicId: number;
	ipType: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Contains a list of OAuth Key configurations.
 * 
 */
declare class VclOAuthKeyConfigurationsList {
	readonly oAuthKeyConfiguration: VclObjectList;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * TBS
 */
declare class VclVMWareTools {
	version: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * TBS
 */
declare class VclMetaDataEntries {
	readonly metadataEntry: VclObjectList;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Container for references to Provider vDCs.
 * 
 */
declare class VclProviderVdcReferences {
	readonly providerVdcReference: VclObjectList;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents VM template instantiation parameters.
 * Internal use only.
 * 
 */
declare class VclInstantiateVmTemplateParams {
	allEULAsAccepted: boolean;
	powerOn: boolean;
	sourcedVmTemplateItem: VclSourcedVmTemplateParams;
	description: string;
	name: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Container for vApp networks.
 * 
 */
declare class VclNetworkConfigSection {
	href: string;
	readonly networkConfig: VclObjectList;
	type: string;
	info: VclMsg;
	readonly otherAttributes: VclMap;
	required: boolean;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Type for a single vAppOrgNetworkRelation query result in records format.
 * 
 */
declare class VclQueryResultVAppOrgNetworkRelationRecord {
	org: string;
	ownerName: string;
	configurationType: string;
	orgNetwork: string;
	orgNetworkName: string;
	status: string;
	name: string;
	href: string;
	readonly otherAttributes: VclMap;
	metadata: VclMetaDataEntries;
	id: string;
	type: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Type for a single vAppOrgVdcNetworkRelation query result in records format.
 * 
 */
declare class VclQueryResultVAppOrgVdcNetworkRelationRecord {
	org: string;
	ownerName: string;
	entityType: string;
	orgVdcNetworkName: string;
	orgVdcNetwork: string;
	status: string;
	name: string;
	href: string;
	readonly otherAttributes: VclMap;
	metadata: VclMetaDataEntries;
	id: string;
	type: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents a list of organizations.
 * 
 */
declare class VclOrgList {
	readonly org: VclObjectList;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents vApp lease settings.
 * 
 */
declare class VclLeaseSettingsSection {
	href: string;
	deploymentLeaseExpiration: VclXMLGregorianCalendar;
	storageLeaseExpiration: VclXMLGregorianCalendar;
	storageLeaseInSeconds: number;
	deploymentLeaseInSeconds: number;
	type: string;
	info: VclMsg;
	readonly otherAttributes: VclMap;
	required: boolean;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Describes an asynchronous operation to be performed by vCloud Director extension.
 * 
 */
declare class VclTaskPrototype {
	operationName: string;
	operation: string;
	owner: VclReference;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Type for a single adminShadowVM query result in records format.
 * 
 */
declare class VclQueryResultAdminShadowVMRecord {
	isBusy: boolean;
	org: string;
	isDeleted: boolean;
	vcName: string;
	datastoreName: string;
	isPublished: boolean;
	primaryVAppName: string;
	primaryVAppTemplate: string;
	primaryVM: string;
	primaryVMCatalog: string;
	primaryVMOwner: string;
	primaryVmName: string;
	shadowVApp: string;
	status: string;
	name: string;
	href: string;
	readonly otherAttributes: VclMap;
	metadata: VclMetaDataEntries;
	id: string;
	type: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Gives more details of third party peer end point.
 * 
 */
declare class VclIpsecVpnThirdPartyPeer {
	peerId: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Type for a single externalNetwork query result in records format.
 * 
 */
declare class VclQueryResultNetworkRecord {
	isBusy: boolean;
	gateway: string;
	netmask: string;
	subnetPrefixLength: number;
	dns1: string;
	dns2: string;
	dnsSuffix: string;
	ipScopeId: string;
	name: string;
	href: string;
	readonly otherAttributes: VclMap;
	metadata: VclMetaDataEntries;
	id: string;
	type: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents a list of files to be transferred (uploaded
 * or downloaded).
 * 
 */
declare class VclFilesList {
	readonly file: VclObjectList;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Upload socket for the truststore.
 * 
 */
declare class VclTrustStoreUploadSocket {
	task: VclTaskParams;
	uploadLocation: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Allows you to specify boot options for this virtual
 * machine.
 * 
 */
declare class VclBootOptions {
	bootDelay: number;
	enterBIOSSetup: boolean;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * A reference to another associated site
 * 
 */
declare class VclSiteAssociation {
	siteId: string;
	siteName: string;
	status: string;
	publicKey: string;
	restEndpoint: string;
	baseUiEndpoint: string;
	tenantUiEndpoint: string;
	restEndpointCertificate: string;
	description: string;
	name: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * xxxx
 * 
 */
declare class VclComputeResource {
	limit: number;
	configured: number;
	reservation: number;
	sharesLevel: VclResourceSharesLevelType;
	shares: number;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * Descriptions of virtual disks used within the
 * package
 */
declare class VclDiskSectionDefault {
	readonly diskSettings: VclObjectList;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Type for a single serviceResource query result in records format.
 * 
 */
declare class VclQueryResultServiceResourceRecord {
	org: string;
	externalObjectId: string;
	resourceClass: string;
	name: string;
	href: string;
	readonly otherAttributes: VclMap;
	metadata: VclMetaDataEntries;
	id: string;
	type: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Type for a single vm query result in records format.
 * 
 */
declare class VclQueryResultVMRecord {
	isBusy: boolean;
	isDeleted: boolean;
	ipAddress: string;
	ownerName: string;
	network: string;
	hardwareVersion: number;
	isPublished: boolean;
	networkName: string;
	vdc: string;
	isInMaintenanceMode: boolean;
	snapshotCreated: VclXMLGregorianCalendar;
	storageProfileName: string;
	isAutoNature: boolean;
	isDeployed: boolean;
	catalogName: string;
	autoDeleteDate: VclXMLGregorianCalendar;
	autoUndeployDate: VclXMLGregorianCalendar;
	containerName: string;
	gcStatus: string;
	guestOs: string;
	isAutoDeleteNotified: boolean;
	isAutoUndeployNotified: boolean;
	isVAppTemplate: boolean;
	memoryMB: number;
	numberOfCpus: number;
	vmToolsStatus: string;
	owner: string;
	snapshot: boolean;
	container: string;
	status: string;
	name: string;
	href: string;
	readonly otherAttributes: VclMap;
	metadata: VclMetaDataEntries;
	id: string;
	type: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Container for the network connections of this virtual machine.
 * 
 */
declare class VclNetworkConnectionSection {
	href: string;
	primaryNetworkConnectionIndex: number;
	readonly networkConnection: VclObjectList;
	type: string;
	info: VclMsg;
	readonly otherAttributes: VclMap;
	required: boolean;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * A list of registered tunneling applications.
 * 
 */
declare class VclTunnelingApplicationList {
	readonly tunnelingApplication: VclObjectList;
	pageSize: number;
	page: number;
	total: number;
	name: string;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * The tenant view of the error, when the containing error is an
 * administrator view of the error.
 * 
 */
declare class VclTenantError {
	majorErrorCode: number;
	minorErrorCode: string;
	vendorSpecificErrorCode: string;
	message: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * A reference to a VDC template.
 * 
 */
declare class VclVdcTemplate {
	vdcTemplateSpecification: VclAbstractValueObject;
	description: string;
	name: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents instantiation parameters. Deprecated in favor of
 * SourcedCompositionItemParamType
 * 
 */
declare class VclSourcedVmInstantiationParams {
	storageProfile: VclReference;
	localityParams: VclLocalityParams;
	hardwareCustomization: VclInstantiateVmHardwareCustomizationParams;
	source: VclReference;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * Type for an external reference to a
 * resource
 */
declare class VclFile {
	bytesTransferred: number;
	checksum: string;
	size: number;
	description: string;
	name: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Type for a single adminRole query result in records format.
 * 
 */
declare class VclQueryResultAdminRoleRecord {
	org: string;
	orgName: string;
	isReadOnly: boolean;
	name: string;
	href: string;
	readonly otherAttributes: VclMap;
	metadata: VclMetaDataEntries;
	id: string;
	type: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents the SNAT and DNAT rules.
 * 
 */
declare class VclGatewayNatRule {
	translatedPort: string;
	icmpSubType: string;
	interface: VclReference;
	originalIp: string;
	originalPort: string;
	translatedIp: string;
	protocol: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * A simple scalar metric value
 * 
 */
declare class VclSimpleMetric {
	unit: string;
	name: string;
	value: any;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Parameters for initiating file upload.
 * 
 */
declare class VclFileUploadSocket {
	task: VclTaskParams;
	uploadLocation: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents a mapping from an OVF source network to a vCloud target network.
 * Used to update network properties in the OVF.
 * 
 */
declare class VclNetworkMapping {
	source: string;
	target: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * The result of operations.
 * 
 */
declare class VclResult {
	resultContent: any;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents a Provider vDC storage profile.
 * 
 */
declare class VclProviderVdcStorageProfileParams {
	enabled: boolean;
	units: string;
	iopsAllocated: number;
	iopsCapacity: number;
	capacityTotal: any;
	capacityUsed: any;
	description: string;
	name: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * Parameters for creating to-the-cloud tunnel
 */
declare class VclToCloudTunnelCreateParams {
	destinationId: string;
	trafficType: string;
	sourceId: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents a question posted by a virtual machine that is
 * WAITING_FOR_INPUT (status="5").
 * 
 */
declare class VclVmPendingQuestion {
	question: string;
	questionId: string;
	readonly choices: VclObjectList;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Type for a single toCloudTunnel query result in records format.
 * 
 */
declare class VclQueryResultToCloudTunnelRecord {
	org: string;
	destinationId: string;
	trafficType: string;
	destinationPort: number;
	useSsl: boolean;
	destinationHost: string;
	href: string;
	readonly otherAttributes: VclMap;
	metadata: VclMetaDataEntries;
	id: string;
	type: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Parameters for SSPI keytab update.
 * 
 */
declare class VclSspiKeytabUpdateParams {
	sspiServiceProviderName: string;
	fileSize: number;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Container for query results in records format.
 * 
 */
declare class VclQueryResultRecords {
	readonly record: VclAbstractObjectSet;
	pageSize: number;
	page: number;
	total: number;
	name: string;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents an information for allocated IP address
 * 
 */
declare class VclAllocatedIpAddress {
	ipAddress: string;
	isDeployed: boolean;
	allocationType: string;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Contains a reference to a VappTemplate or Media object
 * and related metadata.
 * 
 */
declare class VclCatalogItemParams {
	dateCreated: VclXMLGregorianCalendar;
	versionNumber: number;
	entity: VclReference;
	readonly property: VclObjectList;
	size: number;
	description: string;
	name: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents a right.
 * 
 */
declare class VclRightParams {
	serviceNamespace: string;
	rightType: string;
	category: string;
	bundleKey: string;
	description: string;
	name: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents vApp registration parameters.
 * 
 */
declare class VclRegisterVAppParams {
	ovf: string;
	vsToVmxMap: VclVsToVmxMap;
	ovfToVdcNetworkMap: VclOvfToVdcNetworkMap;
	externalNatIpMap: VclExternalNatIpMap;
	nicIpMap: VclNicIpMap;
	description: string;
	name: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Container for references to ExternalNetwork objects.
 * 
 */
declare class VclNetworks {
	readonly network: VclObjectList;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents a network connection in the virtual machine.
 * 
 */
declare class VclNetworkConnection {
	ipAddress: string;
	network: string;
	externalIpAddress: string;
	isConnected: boolean;
	needsCustomization: boolean;
	networkConnectionIndex: number;
	mACAddress: string;
	ipAddressAllocationMode: string;
	networkAdapterType: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents the answer to a question posted by a virtual machine that is
 * WAITING_FOR_INPUT (status="5").
 * 
 */
declare class VclVmQuestionAnswerChoice {
	text: string;
	id: number;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Allows you to specify certain capabilities of this virtual
 * machine.
 * 
 */
declare class VclVmCapabilities {
	memoryHotAddEnabled: boolean;
	cpuHotAddEnabled: boolean;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Parameters for attaching or detaching an independent disk.
 * 
 */
declare class VclDiskAttachOrDetachParams {
	disk: VclReference;
	unitNumber: number;
	busNumber: number;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Type for a single networkPool query result in records format.
 * 
 */
declare class VclQueryResultNetworkPoolRecord {
	isBusy: boolean;
	universalId: string;
	networkPoolType: number;
	name: string;
	href: string;
	readonly otherAttributes: VclMap;
	metadata: VclMetaDataEntries;
	id: string;
	type: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents gateway load balancer service.
 * 
 */
declare class VclLoadBalancerService {
	readonly virtualServer: VclObjectList;
	readonly pool: VclObjectList;
	isEnabled: boolean;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Container for ovf:Section_Type elements that specify vApp
 * configuration on instantiate, compose, or recompose.
 * 
 */
declare class VclInstantiationParams {
	readonly section: VclAbstractObjectSet;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents vApp instantiation from OVF parameters
 * 
 */
declare class VclInstantiateOvfParams {
	allEULAsAccepted: boolean;
	transferFormat: string;
	removeNonStandardOvfExtensions: boolean;
	readonly networkMapping: VclObjectList;
	readonly instantiateOvfProperty: VclObjectList;
	readonly instantiateVmParams: VclObjectList;
	instantiationParams: VclInstantiationParams;
	vAppParent: VclReference;
	deploy: boolean;
	powerOn: boolean;
	description: string;
	name: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Map from OVF to Org networks.
 * 
 */
declare class VclOvfToVdcNetworkMap {
	readonly entry: VclObjectList;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * TBS
 */
declare class VclReservationPoolVdcSummary {
	memoryConsumptionMB: number;
	memoryReservationMB: number;
	storageConsumptionMB: number;
	cpuReservationMhz: number;
	cpuConsumptionMhz: number;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Controls access to the resource.
 * 
 */
declare class VclAccessSetting {
	subject: VclReference;
	externalSubject: VclExternalSubject;
	accessLevel: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Configuration parameters for a catalog that has an external
 * subscription.
 * 
 */
declare class VclExternalCatalogSubscriptionParams {
	libraryId: string;
	subscribeToExternalFeeds: boolean;
	expectedSslThumbprint: string;
	password: string;
	localCopy: boolean;
	location: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Type for a single allocatedExternalAddress query result in records format.
 * 
 */
declare class VclQueryResultAllocatedExternalAddressRecord {
	ipAddress: string;
	network: string;
	linkedNetwork: string;
	href: string;
	readonly otherAttributes: VclMap;
	metadata: VclMetaDataEntries;
	id: string;
	type: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents a NAT rule.
 * 
 */
declare class VclNatRule {
	isEnabled: boolean;
	gatewayNatRule: VclGatewayNatRule;
	oneToOneBasicRule: VclNatOneToOneBasicRule;
	oneToOneVmRule: VclNatOneToOneVmRule;
	portForwardingRule: VclNatPortForwardingRule;
	ruleType: string;
	vmRule: VclNatVmRule;
	description: string;
	id: number;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Container for references to network pools in this vDC.
 * 
 */
declare class VclNetworkPoolReferences {
	readonly networkPoolReference: VclObjectList;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Operating systems available for use on virtual machines owned
 * by this organization.
 * 
 */
declare class VclSupportedOperatingSystemsInfo {
	readonly operatingSystemFamilyInfo: VclObjectList;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Vendor services available for service insertion on networks or edge gateways.
 * 
 */
declare class VclVendorServices {
	readonly networkServices: VclObjectList;
	readonly edgeGatewayServices: VclObjectList;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Type for a single vAppNetwork query result in records format.
 * 
 */
declare class VclQueryResultVAppNetworkRecord {
	isBusy: boolean;
	gateway: string;
	netmask: string;
	subnetPrefixLength: number;
	dns1: string;
	dns2: string;
	dnsSuffix: string;
	ipScopeId: string;
	isIpScopeInherited: boolean;
	vAppName: string;
	vApp: string;
	name: string;
	href: string;
	readonly otherAttributes: VclMap;
	metadata: VclMetaDataEntries;
	id: string;
	type: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * A reference to a resource. Contains an href attribute and
 * optional name and type attributes.
 * 
 */
declare class VclReference {
	href: string;
	name: string;
	id: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * xxxx
 * 
 */
declare class VclMediaSection {
	readonly mediaSettings: VclObjectList;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Allows a user to configure syslog server settings for the gateway
 * 
 */
declare class VclSyslogServer {
	tenantSyslogServerSettings: VclTenantSyslogServerSettings;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Type for a single adminCatalog query result in records format.
 * 
 */
declare class VclQueryResultAdminCatalogRecord {
	numberOfMedia: number;
	org: string;
	orgName: string;
	creationDate: VclXMLGregorianCalendar;
	ownerName: string;
	isPublished: boolean;
	isShared: boolean;
	numberOfTemplates: number;
	owner: string;
	name: string;
	href: string;
	readonly otherAttributes: VclMap;
	metadata: VclMetaDataEntries;
	id: string;
	type: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Upload socket for certificate.
 * 
 */
declare class VclCertificateUploadSocket {
	task: VclTaskParams;
	uploadLocation: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents vApp instantiation parameters.
 * 
 */
declare class VclInstantiateVAppParams {
	isSourceDelete: boolean;
	readonly sourcedItem: VclObjectList;
	linkedClone: boolean;
	readonly sourcedVmInstantiationParams: VclObjectList;
	source: VclReference;
	instantiationParams: VclInstantiationParams;
	vAppParent: VclReference;
	deploy: boolean;
	powerOn: boolean;
	description: string;
	name: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * A list of access settings for a resource.
 * 
 */
declare class VclAccessSettings {
	readonly accessSetting: VclObjectList;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Type for a single adminVM query result in records format.
 * 
 */
declare class VclQueryResultAdminVMRecord {
	org: string;
	isDeleted: boolean;
	moref: string;
	vc: string;
	hardwareVersion: number;
	vmToolsVersion: number;
	datastoreName: string;
	isPublished: boolean;
	networkName: string;
	vdc: string;
	storageProfileName: string;
	isAutoNature: boolean;
	isDeployed: boolean;
	isVdcEnabled: boolean;
	catalogName: string;
	containerName: string;
	gcStatus: string;
	guestOs: string;
	isVAppTemplate: boolean;
	memoryMB: number;
	numberOfCpus: number;
	container: string;
	status: string;
	name: string;
	hostName: string;
	href: string;
	readonly otherAttributes: VclMap;
	metadata: VclMetaDataEntries;
	id: string;
	type: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents an IPSec VPN endpoint.
 * 
 */
declare class VclGatewayIpsecVpnEndpoint {
	network: VclReference;
	publicIp: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Information about a network service that has been inserted
 * 
 */
declare class VclNetworkServiceInsertion {
	category: string;
	readonly vendorTemplates: VclObjectList;
	categoryType: string;
	name: string;
	id: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents the admin view of an organization vDC.
 * 
 */
declare class VclAdminVdcParams {
	providerVdcReference: VclReference;
	resourceGuaranteedMemory: any;
	resourceGuaranteedCpu: any;
	vCpuInMhz: number;
	isThinProvision: boolean;
	networkPoolReference: VclReference;
	resourcePoolRefs: VclVimObjectRefs;
	usesFastProvisioning: boolean;
	overCommitAllowed: boolean;
	vmDiscoveryEnabled: boolean;
	vendorServices: VclVendorServices;
	universalNetworkPoolReference: VclReference;
	computeProviderScope: string;
	isEnabled: boolean;
	networkProviderScope: string;
	nicQuota: number;
	vmQuota: number;
	allocationModel: string;
	computeCapacity: VclComputeCapacity;
	networkQuota: number;
	storageCapacity: VclCapacityWithUsage;
	resourceEntities: VclResourceEntities;
	availableNetworks: VclAvailableNetworks;
	capabilities: VclCapabilities;
	usedNetworkCount: number;
	vdcStorageProfiles: VclVdcStorageProfiles;
	defaultComputePolicy: VclReference;
	vCpuInMhz2: number;
	status: number;
	description: string;
	name: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * A value of SYSTEM places this MetadataEntry in the SYSTEM
 * domain. Omit or leave empty to place this MetadataEntry in the
 * GENERAL domain.
 * 
 */
declare class VclMetadataDomainTag {
	visibility: string;
	value: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Type for a single vmGroupVms query result in records format.
 * 
 */
declare class VclQueryResultVmGroupVmsRecord {
	vmGroupId: string;
	clusterMoref: string;
	vcId: string;
	vmGroupName: string;
	vmName: string;
	orgVdcName: string;
	isVappTemplate: boolean;
	orgVdcId: string;
	vappId: string;
	vappName: string;
	vmMoref: string;
	href: string;
	readonly otherAttributes: VclMap;
	metadata: VclMetaDataEntries;
	id: string;
	type: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Type for a single adminOrgVdc query result in records format.
 * 
 */
declare class VclQueryResultAdminVdcRecord {
	computeProviderScope: string;
	cpuAllocationMhz: number;
	cpuLimitMhz: number;
	cpuUsedMhz: number;
	isBusy: boolean;
	isEnabled: boolean;
	isSystemVdc: boolean;
	memoryAllocationMB: number;
	memoryLimitMB: number;
	memoryUsedMB: number;
	networkPool: string;
	networkPoolUniversalId: string;
	networkProviderScope: string;
	numberOfDeployedVApps: number;
	numberOfDisks: number;
	numberOfMedia: number;
	numberOfRunningVMs: number;
	numberOfStorageProfiles: number;
	numberOfVAppTemplates: number;
	numberOfVApps: number;
	numberOfVMs: number;
	org: string;
	orgName: string;
	providerVdc: string;
	providerVdcName: string;
	storageAllocationMB: number;
	storageLimitMB: number;
	storageUsedMB: number;
	description: string;
	status: string;
	name: string;
	href: string;
	readonly otherAttributes: VclMap;
	metadata: VclMetaDataEntries;
	id: string;
	type: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * TBS
 */
declare class VclFirewallRuleProtocols {
	tcp: boolean;
	udp: boolean;
	icmp: boolean;
	any: boolean;
	other: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Type for a single orgNetwork query result in records format.
 * 
 */
declare class VclQueryResultOrgNetworkRecord {
	isBusy: boolean;
	networkPool: string;
	org: string;
	gateway: string;
	netmask: string;
	subnetPrefixLength: number;
	dns1: string;
	dns2: string;
	dnsSuffix: string;
	ipScopeId: string;
	isIpScopeInherited: boolean;
	networkPoolName: string;
	name: string;
	href: string;
	readonly otherAttributes: VclMap;
	metadata: VclMetaDataEntries;
	id: string;
	type: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Name of the storage profile that will be specified for this
 * virtual machine. The named storage profile must exist
 * in the organization vDC that contains the virtual machine. If
 * not specified, the default storage profile for the vDC is used.
 * 
 */
declare class VclDefaultStorageProfileSection {
	storageProfile: string;
	info: VclMsg;
	readonly otherAttributes: VclMap;
	required: boolean;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Container for references to catalogs.
 * 
 */
declare class VclCatalogsList {
	readonly catalogReference: VclObjectList;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Type for a single role query result in records format.
 * 
 */
declare class VclQueryResultRoleRecord {
	isReadOnly: boolean;
	description: string;
	name: string;
	href: string;
	readonly otherAttributes: VclMap;
	metadata: VclMetaDataEntries;
	id: string;
	type: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Type for a single adminEventCBM query result in records format.
 * 
 */
declare class VclQueryResultAdminEventCBMRecord {
	entityName: string;
	details: string;
	org: string;
	orgName: string;
	entityType: string;
	eventId: string;
	eventStatus: number;
	productVersion: string;
	serviceNamespace: string;
	timeStamp: VclXMLGregorianCalendar;
	eventType: string;
	entity: string;
	userName: string;
	href: string;
	readonly otherAttributes: VclMap;
	metadata: VclMetaDataEntries;
	id: string;
	type: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents the parameters to create a storage profile in an organization vDC
 * 
 */
declare class VclVdcStorageProfileParams {
	enabled: boolean;
	limit: number;
	units: string;
	iopsSettings: VclVdcStorageProfileIopsSettings;
	providerVdcStorageProfile: VclReference;
	default: boolean;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents a list of RASD items specifying a group of related
 * DMTF Resource Allocation Setting Data properties of this
 * virtual machine.
 * 
 */
declare class VclRasdItemsList {
	readonly item: VclObjectList;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents a vApp network configuration.
 * 
 */
declare class VclVAppNetworkConfiguration {
	networkName: string;
	isDeployed: boolean;
	configuration: VclNetworkConfiguration;
	description: string;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents the answer to a question posted by a virtual machine that is
 * WAITING_FOR_INPUT (status="5").
 * 
 */
declare class VclVmQuestionAnswer {
	questionId: string;
	choiceId: number;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * TBS
 */
declare class VclVsToVmxMapEntry {
	storageProfile: string;
	biosUuid: string;
	datastorePath: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Type for a single task query result in records format.
 * 
 */
declare class VclQueryResultTaskRecord {
	progress: number;
	org: string;
	orgName: string;
	ownerName: string;
	objectName: string;
	operationFull: string;
	startDate: VclXMLGregorianCalendar;
	endDate: VclXMLGregorianCalendar;
	serviceNamespace: string;
	objectType: string;
	status: string;
	object: string;
	name: string;
	href: string;
	readonly otherAttributes: VclMap;
	metadata: VclMetaDataEntries;
	id: string;
	type: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents a guest customization status. Internal Use only.
 * 
 */
declare class VclGuestCustomizationStatusSection {
	href: string;
	guestCustStatus: string;
	type: string;
	info: VclMsg;
	readonly otherAttributes: VclMap;
	required: boolean;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents a virtual machine snapshot.
 * 
 */
declare class VclSnapshot {
	poweredOn: boolean;
	created: VclXMLGregorianCalendar;
	size: number;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Hard disk controller type detailed description.
 * 
 */
declare class VclHardDiskAdapter {
	busNumberRanges: VclRanges;
	unitNumberRanges: VclRanges;
	reservedBusUnitNumber: VclBusUnitNumber;
	legacyId: number;
	maximumDiskSizeGb: number;
	name: string;
	id: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Type for a single adminApiDefinition query result in records format.
 * 
 */
declare class VclQueryResultAdminApiDefinitionRecord {
	serviceNamespace: string;
	apiVendor: string;
	serviceName: string;
	serviceVendor: string;
	entryPoint: string;
	namespace: string;
	service: string;
	name: string;
	href: string;
	readonly otherAttributes: VclMap;
	metadata: VclMetaDataEntries;
	id: string;
	type: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Parameters to an undeploy vApp request.
 * 
 */
declare class VclUndeployVAppParams {
	undeployPowerAction: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents a list of IP scopes.
 * 
 */
declare class VclIpScopes {
	readonly ipScope: VclObjectList;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents a member in a load balancer pool.
 * 
 */
declare class VclLBPoolMember {
	ipAddress: string;
	readonly servicePort: VclObjectList;
	condition: string;
	weight: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * One of: 1, 0, true, false
 * 
 */
declare class VclMetadataBooleanValue {
	value: boolean;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents a range of IP addresses, start and end inclusive.
 * 
 */
declare class VclIpRange {
	startAddress: string;
	endAddress: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Parameters for updating storage profiles in an organization vDC.
 * 
 */
declare class VclUpdateVdcStorageProfiles {
	readonly addStorageProfile: VclObjectList;
	readonly removeStorageProfile: VclObjectList;
	description: string;
	name: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * An extensibility
 * phase represents a point in a workflow where control can be passed to an
 * extension.
 * Each phase may define its own
 * message format for
 * communicating with the extension.
 * 
 */
declare class VclPhases {
	readonly phase: VclObjectList;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents a list of virtual machines.
 * 
 */
declare class VclVms {
	readonly vmReference: VclObjectList;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents the NAT rule for port forwarding between VM NIC/port
 * and external IP/port.
 * 
 */
declare class VclNatVmRule {
	externalIpAddress: string;
	externalPort: number;
	vAppScopedVmId: string;
	vmNicId: number;
	internalPort: number;
	protocol: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Type for a single adminOrgNetwork query result in records format.
 * 
 */
declare class VclQueryResultAdminOrgNetworkRecord {
	isBusy: boolean;
	networkPool: string;
	org: string;
	orgName: string;
	gateway: string;
	netmask: string;
	subnetPrefixLength: number;
	dns1: string;
	dns2: string;
	ipScopeId: string;
	isIpScopeInherited: boolean;
	networkPoolName: string;
	name: string;
	href: string;
	readonly otherAttributes: VclMap;
	metadata: VclMetaDataEntries;
	id: string;
	type: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents the admin view of a cloud.
 * 
 */
declare class VclVCloud {
	networks: VclNetworks;
	rightReferences: VclRightReferences;
	roleReferences: VclRoleReferences;
	organizationReferences: VclOrganizationReferences;
	providerVdcReferences: VclProviderVdcReferences;
	description: string;
	name: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents the admin view of this cloud.
 * 
 */
declare class VclOrganizationReferences {
	readonly organizationReference: VclObjectList;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents
 * the
 * collection of selector extensions.
 * 
 */
declare class VclSelectorExtensions {
	readonly selectorExtension: VclObjectList;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Defines how LDAP attributes are used when importing a user.
 * 
 */
declare class VclOrgLdapUserAttributes {
	surname: string;
	givenName: string;
	objectIdentifier: string;
	fullName: string;
	telephone: string;
	objectClass: string;
	groupMembershipIdentifier: string;
	groupBackLinkIdentifier: string;
	userName: string;
	email: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * TBS
 */
declare class VclStaticRoute {
	gatewayInterface: VclReference;
	network: string;
	interface: string;
	nextHopIp: string;
	name: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * TBS
 */
declare class VclEventProperty {
	name: string;
	value: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Represents an asynchronous operation in vCloud Director.
 * 
 */
declare class VclTaskParams {
	progress: number;
	details: string;
	operationName: string;
	serviceNamespace: string;
	user: VclReference;
	operation: string;
	cancelRequested: boolean;
	endTime: VclXMLGregorianCalendar;
	expiryTime: VclXMLGregorianCalendar;
	owner: VclReference;
	startTime: VclXMLGregorianCalendar;
	error: VclError;
	organization: VclReference;
	status: string;
	params: any;
	result: VclResult;
	description: string;
	name: string;
	operationKey: string;
	id: string;
	href: string;
	type: string;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Specifies an absolute time in UTC
 * 
 */
declare class VclAbsoluteTime {
	time: VclXMLGregorianCalendar;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Type for a single organization query result in records format.
 * 
 */
declare class VclQueryResultOrgRecord {
	isEnabled: boolean;
	numberOfDisks: number;
	numberOfVApps: number;
	isReadOnly: boolean;
	deployedVMQuota: number;
	storedVMQuota: number;
	canPublishCatalogs: boolean;
	numberOfCatalogs: number;
	numberOfVdcs: number;
	numberOfGroups: number;
	name: string;
	displayName: string;
	href: string;
	readonly otherAttributes: VclMap;
	metadata: VclMetaDataEntries;
	id: string;
	type: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * TBS
 */
declare class VclVdcTemplateSpecification {
	readonly storageProfile: VclObjectList;
	nicQuota: number;
	vmQuota: number;
	provisionedNetworkQuota: number;
	gatewayConfiguration: VclVdcTemplateSpecificationGatewayConfiguration;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * 
 * Container for references to storage profiles associated with a vDC.
 * 
 */
declare class VclVdcStorageProfiles {
	readonly vdcStorageProfile: VclObjectList;
	readonly vCloudExtension: VclObjectList;
	readonly otherAttributes: VclMap;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
	/**
	 * Returns the XML representation of the object.
	 */
	toXml(): string;
	/**
	 * Loads the object from its XML representation.
	 * @param xml 
	 */
	loadFromXml(xml: string): void;
}

/**
 * TBS
 */
declare class VclX509CrlParams {
	nextUpdate: string;
	issuer: string;
	x509CrlEntries: VclObjectList;
	version: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
}

/**
 * TBS
 */
declare class VclTrustObjectDataParams {
	pemEncoding: string;
	privateKey: string;
	passphrase: string;
	extendedAttributes: VclObjectList;
	isUniversal: boolean;
	universalRevision: number;
	name: string;
	type: VclObjectTypeParams;
	description: string;
	scope: VclScopeInfoParams;
	nodeId: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
}

/**
 * TBS
 */
declare class VclCertificateParams {
	subjectCn: string;
	issuerCn: string;
	pemEncoding: string;
	certificateType: string;
	x509Certificates: VclObjectList;
	description: string;
	scope: VclScopeInfoParams;
	extendedAttributes: VclObjectList;
	isUniversal: boolean;
	universalRevision: number;
	name: string;
	type: VclObjectTypeParams;
	nodeId: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
}

/**
 * TBS
 */
declare class VclCsrParams {
	subject: VclPrincipalParams;
	cn: string;
	pemEncoding: string;
	keySize: string;
	algorithm: string;
	extendedAttributes: VclObjectList;
	isUniversal: boolean;
	universalRevision: number;
	name: string;
	type: VclObjectTypeParams;
	description: string;
	scope: VclScopeInfoParams;
	nodeId: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
}

/**
 * TBS
 */
declare class VclPrincipalParams {
	attributes: VclObjectList;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
}

/**
 * TBS
 */
declare class VclX509CertificateParams {
	valid: boolean;
	ca: boolean;
	subjectCn: string;
	issuerCn: string;
	version: string;
	serialNumber: string;
	signatureAlgo: string;
	notBefore: string;
	notAfter: string;
	issuer: string;
	subject: string;
	publicKeyAlgo: string;
	publicKeyLength: string;
	rsaPublicKeyModulus: string;
	rsaPublicKeyExponent: string;
	dsaPublicKeyG: string;
	dsaPublicKeyP: string;
	dsaPublicKeyQ: string;
	dsaPublicKeyY: string;
	sha1Hash: string;
	md5Hash: string;
	isCa: boolean;
	isValid: boolean;
	signature: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
}

/**
 * TBS
 */
declare class VclObjectTypeParams {
	name: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
}

/**
 * TBS
 */
declare class VclKeyValueParams {
	value: string;
	key: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
}

/**
 * TBS
 */
declare class VclExtendedAttributeParams {
	name: string;
	value: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
}

/**
 * TBS
 */
declare class VclScopeInfoParams {
	objectTypeName: string;
	name: string;
	id: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
}

/**
 * TBS
 */
declare class VclFeatureConfigParams {
	version: number;
	enabled: boolean;
	template: string;
	readonly obtainEquivalenceKey: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
}

/**
 * TBS
 */
declare class VclIPPoolTrinityParams {
	readonly obtainEquivalenceKey: string;
	ipRange: string;
	allowHugeRange: boolean;
	autoConfigureDNS: boolean;
	defaultGateway: string;
	domainName: string;
	primaryNameServer: string;
	secondaryNameServer: string;
	leaseTime: string;
	dnsType: string;
	subnetMask: string;
	dhcpOptions: VclDhcpOptionsParams;
	nextServer: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
}

/**
 * TBS
 */
declare class VclDhcpOptionsParams {
	option66: string;
	option67: string;
	option26: string;
	option121: VclDhcpOption121Params;
	option150: VclDhcpOption150Params;
	others: VclObjectList;
	readonly obtainEquivalenceKey: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
}

/**
 * TBS
 */
declare class VclDhcpOption121Params {
	staticRoutes: VclObjectList;
	readonly obtainEquivalenceKey: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
}

/**
 * TBS
 */
declare class VclDhcpOption150Params {
	tftpServers: VclList;
	readonly obtainEquivalenceKey: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
}

/**
 * TBS
 */
declare class VclDhcpOptionOtherParams {
	code: string;
	value: string;
	readonly obtainEquivalenceKey: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
}

/**
 * TBS
 */
declare class VclDhcpOption121StaticRouteParams {
	destinationSubnet: string;
	router: string;
	readonly obtainEquivalenceKey: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
}

/**
 * TBS
 */
declare class VclStaticBindingTrinityParams {
	readonly obtainEquivalenceKey: string;
	vmId: string;
	vnicId: number;
	hostname: string;
	vmName: string;
	ipAddress: string;
	macAddress: string;
	autoConfigureDNS: boolean;
	defaultGateway: string;
	domainName: string;
	primaryNameServer: string;
	secondaryNameServer: string;
	leaseTime: string;
	dnsType: string;
	subnetMask: string;
	dhcpOptions: VclDhcpOptionsParams;
	nextServer: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
}

/**
 * TBS
 */
declare class VclFirewallRuleBaseParams {
	readonly obtainEquivalenceKey: string;
	ruleId: number;
	ruleType: string;
	loggingEnabled: boolean;
	invalidSource: boolean;
	invalidDestination: boolean;
	invalidApplication: boolean;
	direction: string;
	ruleTag: number;
	matchTranslated: boolean;
	statistics: VclFirewallRuleStatsParams;
	name: string;
	description: string;
	enabled: boolean;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
}

/**
 * TBS
 */
declare class VclServiceTrinityParams {
	readonly obtainEquivalenceKey: string;
	icmpType: string;
	sourcePort: VclList;
	protocol: string;
	port: VclList;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
}

/**
 * TBS
 */
declare class VclApplicationTrinityParams {
	applicationId: VclList;
	service: VclObjectList;
	readonly obtainEquivalenceKey: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
}

/**
 * TBS
 */
declare class VclAddressTrinityParams {
	ipAddress: VclList;
	groupingObjectId: VclList;
	vnicGroupId: VclList;
	exclude: boolean;
	readonly obtainEquivalenceKey: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
}

/**
 * TBS
 */
declare class VclFirewallRuleTrinityParams {
	application: VclApplicationTrinityParams;
	destination: VclAddressTrinityParams;
	action: string;
	source: VclAddressTrinityParams;
	readonly obtainEquivalenceKey: string;
	ruleId: number;
	ruleType: string;
	loggingEnabled: boolean;
	invalidSource: boolean;
	invalidDestination: boolean;
	invalidApplication: boolean;
	direction: string;
	ruleTag: number;
	matchTranslated: boolean;
	statistics: VclFirewallRuleStatsParams;
	name: string;
	description: string;
	enabled: boolean;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
}

/**
 * TBS
 */
declare class VclFirewallRuleStatsParams {
	connectionCount: number;
	packetCount: number;
	byteCount: number;
	timestamp: number;
	readonly obtainEquivalenceKey: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
}

/**
 * TBS
 */
declare class VclNatConfigParams {
	rules: VclNatRulesParams;
	version: number;
	enabled: boolean;
	template: string;
	readonly obtainEquivalenceKey: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
}

/**
 * TBS
 */
declare class VclNatRulesParams {
	natRulesDtos: VclObjectList;
	readonly obtainEquivalenceKey: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
}

/**
 * TBS
 */
declare class VclNatRuleParams {
	ruleType: string;
	action: string;
	vnic: string;
	originalAddress: string;
	translatedAddress: string;
	originalPort: string;
	translatedPort: string;
	icmpType: string;
	description: string;
	loggingEnabled: boolean;
	enabled: boolean;
	ruleTag: number;
	readonly obtainEquivalenceKey: string;
	protocol: string;
	constructor();
	/**
	 * Default constructor.
	 */
	constructor();
}
