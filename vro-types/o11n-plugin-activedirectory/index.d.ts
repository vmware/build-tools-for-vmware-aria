/**
 * Represents the Active Directory base.
 */
declare class ActiveDirectory {
	/**
	 * Search the active directory for a certain type of object.
	 * 
	 * @param type May be one of this: ComputerAD, User, UserGroup, OrganizationalUnit or Group.
	 * @param query The query. You don't have to place * before and after your query.
	 * @param adServer If not provided default Active Directory server will be used.
	 */
	static search<T>(type: string, query: string, adServer: AD_Host): T[];
	/**
	 * Search the active directory for a certain type of object. Object will be returned only if the there is exact match.
	 *
	 * @param type May be one of this: ComputerAD, User, UserGroup, OrganizationalUnit or Group.
	 * @param objectName Object name to search for.
	 * @param limit Maximum number of returned results.
	 * @param adServer If not provided default Active Directory server will be searched.
	 */
	static searchExactMatch<T>(type: string, objectName: string, limit: number, adServer: AD_Host): T[];
	/**
	 * Returns a computer when his exact name is given. If no computer found, returns null
	 *
	 * @param computerName The exact name of the computer.
	 * @param adServer If not provided default Active Directory server will be used.
	 */
	static getComputerAD(computerName: string, adServer: AD_Host): AD_Computer;
	/**
	 * Return the AD_Computer representing the Domain Conroller
	 * @param adServer If not provided default Active Directory server will be used.
	 */
	static getDC(adServer: AD_Host): AD_Computer;
	/**
	 * Processes a search operation with the provided information. It is expected that at most one entry will be 
	 * returned from the search, and that no additional content from the successful search result 
	 * (e.g., diagnostic message or response controls) are needed.
	 *
	 * @param baseDN
	 * @param searchScope
	 * @param timeLimit
	 * @param filter
	 * @param attributes
	 * @param host
	 */

	static searchForEntry<T>(baseDN: string, searchScope: LdapSearchScope, timeLimit: number, filter: string, attributes: string[], host: AD_Host): T;
	/**
	 * Search recursively the whole domain tree of Active Directory for a certain type of objects.
	 * 
	 * @param type 
	 * @param query 
	 * @param adServer 
	 */
	static searchRecursively<T>(type: string, query: string, adServer: AD_Host): T[];
	/**
	 * Gets computers recursively for the whole domain tree.
	 * 
	 * @param computerName 
	 * @param adServer 
	 */
	static getComputerADRecursively(computerName: string, adServer: AD_Host): AD_Computer[];
	/**
	 * Retrieves all AD_Computer instances for the domain and all sub-domain Domain Controllers.
	 * 
	 * @param param0 
	 */
	static getAllDomainControllerComputers(param0: AD_Host): AD_Computer[];
	/** Retrieves the entry with the specified DN. */
	static getEntry<T>(baseDN: string, attributes: string[], host: AD_Host): T;
	/**
	 * Allows a client to change the leftmost (least significant) component of the name of an entry in the directory, or to move a subtree of entries to a new location in the directory. For example, to rename "cn=SomeUser, ou=People, dc=demo, dc=org" to "cn=AnotherUser, ou=People, dc=demo, dc=org" you must provide from as "cn=SomeUser, ou=People, dc=demo, dc=org" and to as "cn=AnotherUser". To move the entry under different tree node new_parent "cn=NewName, ou=NewParent, dc=demo, dc=org" must be provided.
	 * 
	 * @param from 
	 * @param to 
	 * @param new_parent 
	 * @param param3 
	 */
	static rename(from: string, to: string, new_parent: string, param3: AD_Host): void;
}

/**
 * Computer on the AD
 */
declare interface AD_Computer {
	// The unique Id of the element
	readonly id: string;
	// The node name
	readonly name: string;
	// The remote host name
	readonly hostname: string;
	// Activate or deactivate a computer
	enabled: boolean;
	// Return object GUID formatted as dashed string
	readonly gUID: string;
	// Return the DN of the item
	readonly distinguishedName: string;
	// Returns all attributes
	readonly allAttributes: any[];

	/**
	 * Get an AD attribute for an array of values
	 * @param attibName
	 */
	getArrayAttribute(attibName: string): string[];

	/**
	 * Get an AD attribute value as byte array.
	 * @param attibName
	 */
	getAttributeValueBytes(attibName: string): any[];

	/**
	 * Removes an attribute as specified by the attribName parameter
	 * @param attribName 
	 */
	removeAttribute(attribName: string): void;

	/**
	 * Destroy this element from the AD. Take care, this action PERMANENTLY DESTROY the element
	 * @param param0 
	 */
	destroy(param0: boolean): void;

	/**
	 * Allows a client to change the leftmost (least significant) component of the name of an entry in the directory. Тo rename the entry you must provide it with the attribute as prefix - e.g. "cn=newName".
	 * @param name 
	 */
	rename(name: string): void;

	/**
	 * Change the value of an existing attribute
	 * 
	 * @param attribName 
	 * @param newValue - Note: newValue is `Object` in the API, but it should be just a string
	 */
	setAttribute(attribName: string, newValue: any): void;

	/**
	 * Get an AD attribute
	 * @param attibName
	 */
	getAttribute(attibName: string): string;

	/**
	 * Adds an attribute
	 *
	 * @param attribName 
	 * @param newValue - Note: newValue is `Object` in the API, but it should be just a string
	 */
	addAttribute(attribName: string, newValue: any): void;
}

/**
 * Group
 */
declare interface AD_Group {
	// the unique Id of the element
	readonly id: string;
	// List of all user groups (read-only)
	readonly userGroups: any[];
	// List of all Group (read-only)
	readonly containers: any[];
	// List of all OU (read-only)
	readonly organizationalUnits: any[];
	// List of all computers (read-only)
	readonly computers: any[];
	// List of all users (read-only)
	readonly users: any[];
	// Return object GUID formatted as dashed string
	readonly gUID: string;
	// Return the DN of the item
	readonly distinguishedName: string;
	// Returns all attributes
	readonly allAttributes: any[];

	/**
	 * Creates a new user, sets its password and adds it to this container.
	 * 
	 * @param accountName 
	 * @param password 
	 * @param domainName 
	 * @param displayName 
	 */
	createUserWithPassword(accountName: string, password: string, domainName: string, displayName: string): void;
	/**
	 * Creates a new user, sets its password and adds it to this container.
	 * 
	 * @param accountName 
	 * @param password 
	 * @param domainName 
	 * @param displayName 
	 * @param firstName 
	 * @param lastName 
	 */
	createUserWithDetails(accountName: string, password: string, domainName: string, displayName: string, firstName: string, lastName: string): void;
	/**
	 * Creates a new user group and adds it to this container.
	 * 
	 * @param groupName 
	 */
	createUserGroup(groupName: string): void;
	/**
	 * Creates a new organizational unit and adds it to this container.
	 * 
	 * @param ouName 
	 */
	createOrganizationalUnit(ouName: string): void;
	/**
	 * Create a new computer and add it to this container.
	 * 
	 * @param computerName 
	 * @param domainName 
	 * @param computerNamePreWin2K 
	 */
	createComputer(computerName: string, domainName: string, computerNamePreWin2K: string): void;
	/**
	 * Create a new computer with password and add it to this container.
	 * 
	 * @param computerName 
	 * @param domainName 
	 * @param password 
	 * @param computerNamePreWin2K 
	 */
	createComputerWithPassword(computerName: string, domainName: string, password: string, computerNamePreWin2K: string): void;
	/**
	 * Creates a new user and adds it to this container.
	 * 
	 * @param accountName 
	 * @param domainName 
	 * @param displayName 
	 */
	createUser(accountName: string, domainName: string, displayName: string): void;
	/**
	 * Get an AD attribute for an array of values.
	 * 
	 * @param attribName 
	 */
	getArrayAttribute(attribName: string): string[];
	/**
	 * Get an AD attribute value as byte array.
	 * @param attribName 
	 */
	getAttributeValueBytes(attribName: string): any[];
	/**
	 * Removes an attribute as specified by the attribName parameter.
	 * 
	 * @param attribName 
	 */
	removeAttribute(attribName: string): void;
	/**
	 * Change the value of an existing attribute.
	 * 
	 * @param attribName 
	 * @param newValue 
	 */
	setAttribute(attribName: string, newValue: any): void;
	/**
	 * Get an AD attribute.
	 * 
	 * @param attribName 
	 */
	getAttribute(attribName: string): string;
	/**
	 * Adds an attribute.
	 * 
	 * @param attribName 
	 * @param newValue 
	 */
	addAttribute(attribName: string, newValue: any): void;
}

/**
 * Represents Active directory server connection.
 */
declare interface AD_Host {
	// Active directory configuration name
	readonly name: string;
	// Active Directory host connection URL. Actual URL used for connection with Active Directory server. It might differ from configured values when current object represents sub-domain entity.
	readonly Url: string;
	// Active Directory host configuration settings for current AD_Host connection.
	readonly hostConfiguration: AD_ServerConfiguration;

	/**
	 * Retrieve LdapClient based on current host configuration settings.
	 */
	getLdapClient(): LdapClient;
}

/**
 * Manage Active Directory hosts
 */
declare class AD_HostManager {
	/**
	 * Return Active Directory hosts by it's configuration id.
	 * @param param0
	 */
	static findHost(param0: string): AD_Host;

	/**
	 * Return all Active Directory hosts.
	 */
	static findAllHosts(): any[];
}

/**
 * Represents an Organizational Unit
 */
declare interface AD_OrganizationalUnit {
	// the unique Id of the element
	readonly id: string;
	// List of all user groups (read-only)
	readonly userGroups: AD_UserGroup[];
	// List of all Group (read-only)
	readonly containers: AD_Group[];
	// List of all OU (read-only)
	readonly organizationalUnits: AD_OrganizationalUnit[];
	// List of all computers (read-only)
	readonly computers: AD_Computer[];
	// List of all users (read-only)
	readonly users: AD_User[];
	// Return object GUID formatted as dashed string
	readonly gUID: string;
	// Return the DN of the item
	readonly distinguishedName: string;
	// Returns all attributes
	readonly allAttributes: any[];

	/**
	 * Get a computer by name.
	 * 
	 * @param computerName
	 */
	searchComputer(computerName: string): AD_Computer;
	/**
	 * Creates a new user, sets its password and adds it to this container.
	 *
	 * @param accountName
	 * @param password 
	 * @param domainName 
	 * @param displayName 
	 */
	createUserWithPassword(accountName: string, password: string, domainName: string, displayName: string): void;
	/**
	 * Creates a new user, sets its password and adds it to this container.
	 * 
	 * @param accountName 
	 * @param password 
	 * @param domainName 
	 * @param displayName 
	 * @param firstName 
	 * @param lastName 
	 */
	createUserWithDetails(accountName: string, password: string, domainName: string, displayName: string, firstName: string, lastName: string): void;
	/**
	 * Creates a new user group and adds it to this container.
	 * 
	 * @param groupName 
	 */
	createUserGroup(groupName: string): void;
	/**
	 * Creates a new organizational unit and adds it to this container.
	 * 
	 * @param ouName 
	 */
	createOrganizationalUnit(ouName: string): void;
	/**
	 * Create a new computer and add it to this container.
	 * 
	 * @param domainName 
	 * @param computerName 
	 * @param computerNamePreWin2K 
	 */
	createComputer(computerName: string, domainName: string, computerNamePreWin2K: string): void;
	/**
	 * Create a new computer with password and add it to this container.
	 * 
	 * @param computerNamePreWin2K 
	 * @param computerName
	 * @param password 
	 * @param domainName 
	 */
	createComputerWithPassword(computerName: string, domainName: string, password: string, computerNamePreWin2K: string): void;
	/**
	 * Creates a new user and adds it to this container.
	 * 
	 * @param domainName 
	 * @param displayName 
	 * @param accountName 
	 */
	createUser(accountName: string, domainName: string, displayName: string): void;
	/**
	 * Get an AD attribute for an array of values.
	 * 
	 * @param attibName 
	 */
	getArrayAttribute(attibName: string): string[];
	/**
	 * Get an AD attribute value as byte array.
	 * 
	 * @param attibName
	 */
	getAttributeValueBytes(attibName: string): any[];
	/**
	 * Removes an attribute as specified by the attribName parameter.
	 * 
	 * @param attribName 
	 */
	removeAttribute(attribName: string): void;
	/**
	 * Destroy this element from the AD. Take care, this action PERMANENTLY DESTROY the element.
	 * 
	 * @param param0 
	 */
	destroy(param0: boolean): void;
	/**
	 * Change the value of an existing attribute
	 * 
	 * @param attribName 
	 * @param newValue - Note: newValue is `Object` in the API, but it should be just a string
	 */
	setAttribute(attribName: string, newValue: any): void;
	/**
	 * Get an AD attribute.
	 * 
	 * @param attibName
	 */
	getAttribute(attibName: string): string;

	/**
	 * Adds an attribute.
	 *
	 * @param attribName 
	 * @param newValue - Note: newValue is `Object` in the API, but it should be just a string
	 */
	addAttribute(attribName: string, newValue: any): void;
}

/**
 * Active Directory Plug-in options
 */
declare interface AD_PluginOptions {
	// Default Active Directory end point id.
	readonly defaultAdServerId: string;
	// Maximum number of items that will be returned by a search.
	readonly searchSizeLimit: number;
	// Maximum number of items that will be returned by a search from single Active Directory server.
	readonly searchSizeLimitPerServer: number;
}










////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Connection to ldap server.
 */
declare class LdapClient {
	/**
	 * Creates a new LDAP connection that is established to the specified server and is authenticated as the specified user (via LDAP simple authentication).
	 * 
	 * @param param0 
	 * @param param1 
	 * @param param2 
	 * @param param3 
	 * @param param4 
	 */
	constructor(param0: any, param1: string, param2: number, param3: string, param4: string);

	/**
	 * Applies the provided modification to the specified entry.
	 * 
	 * @param dn 
	 * @param newRDN 
	 * @param deleteOldRDN 
	 * @param newSuperiorDN 
	 */
	modifyDN(dn: string, newRDN: string, deleteOldRDN: boolean, newSuperiorDN: string): LdapResult;
	/**
	 * Processes a search operation with the provided information. It is expected that at most one entry will be returned from the search, and that no additional content from the successful search result (e.g., diagnostic message or response controls) are needed.
	 * 
	 * @param dn 
	 * @param searchScope 
	 * @param timeLimit 
	 * @param filter 
	 * @param attributes 
	 */
	searchForEntry(dn: string, searchScope: LdapSearchScope, timeLimit: number, filter: string, attributes: string[]): LdapEntry;
	/** Retrieves the time that this connection was established in the number of milliseconds since January 1, 1970 UTC (the same format used by System.currentTimeMillis. */
	getConnectTime(): number;
	/** Retrieves the user-friendly name that has been assigned to this connection. */
	getConnectionName(): string;
	/**
	 * Deletes the entry with the specified DN.
	 * 
	 * @param param0 
	 */
	deleteByDeleteRequest(param0: LdapDeleteRequest): LdapResult;
	/**
	 * Processes a search operation with the provided information. The search result entries and references will be collected internally and included in the SearchResult object that is returned.
	 * 
	 * @param searchRequest 
	 */
	searchBySearchRequest(searchRequest: LdapSearchRequest): LdapSearchResult;
	/** Indicates whether this connection is currently established. */
	isConnected(): boolean;
	/**
	 * Deletes the entry with the specified DN.
	 * 
	 * @param dn 
	 */
	delete(dn: string): LdapResult;
	/**
	 * Establishes an unauthenticated connection to the directory server using the provided information.
	 * 
	 * @param host 
	 * @param port 
	 * @param timeout 
	 */
	connect(host: string, port: number, timeout: number): void;
	/** If this method is invoked while any operations are in progress on this connection, then the directory server may or may not abort processing for those operations, depending on the type of operation and how far along the server has already gotten while processing that operation. It is recommended that all active operations be abandoned, canceled, or allowed to complete before attempting to close an active connection. */
	close(): void;
	/**
	 * Processes an add operation with the provided information.
	 * 
	 * @param entry 
	 */
	addEntry(entry: LdapEntry): LdapResult;
	/**
	 * Processes a search operation with the provided information. The search result entries and references will be collected internally and included in the SearchResult object that is returned.
	 * 
	 * @param baseDN 
	 * @param searchScope 
	 * @param derefPolicy 
	 * @param sizeLimit 
	 * @param timeLimit 
	 * @param filter 
	 * @param attributes 
	 */
	search(baseDN: string, searchScope: LdapSearchScope, derefPolicy: LdapDereferencePolicy, sizeLimit: number, timeLimit: number, filter: string, attributes: string[]): LdapSearchResult;
	/**
	 * The LDAP protocol specification forbids clients from attempting to perform a bind on a connection in which one or more other operations are already in progress. If a bind is attempted while any operations are in progress, then the directory server may or may not abort processing for those operations.
	 * 
	 * @param bindDN 
	 * @param password 
	 */
	bind(bindDN: string, password: string): LdapResult;
	/**
	 * Retrieves the entry with the specified DN. The requested entry, or null if the target entry does not exist or no entry was returned (e.g., if the authenticated user does not have permission to read the target entry).
	 * 
	 * @param dn 
	 * @param attributes 
	 */
	getEntry(dn: string, attributes: string[]): LdapEntry;
	/**
	 * Processes an add operation with the provided information.
	 * @param dn 
	 * @param attributes 
	 */
	addAttribute(dn: string, attributes: any[]): LdapResult;
	/**
	 * Specifies the user-friendly name that should be used for this connection. This name may be used in debugging to help identify the purpose of this connection.
	 * 
	 * @param connectionName 
	 */
	setConnectionName(connectionName: string): void;
	/**
	 * Applies the provided modification to the specified entry.
	 * 
	 * @param dn 
	 * @param modificaitons 
	 */
	modify(dn: string, modificaitons: any[]): any;
	/** Attempts to re-establish a connection to the server and re-authenticate if appropriate. */
	reconnect(): void;
	/** Retrieves the disconnect message for this connection, which may provide additional information about the reason for the disconnect, if available. */
	getDisconnectMessage(): string;
	/** Retrieves the time that this connection was last used to send or receive an LDAP message. The value will represent the number of milliseconds since January 1, 1970 UTC (the same format used by System.currentTimeMillis. */
	getLastCommunicationTime(): number;
}

/**
 * Create new non persisted ldap connections.
 */
declare class LdapClientFactory {
	/**
	 * Creates a new LDAP connection that is established to the specified server and is authenticated as the specified user (via LDAP simple authentication)..
	 * 
	 * @param host 
	 * @param port 
	 * @param bindDN 
	 * @param password 
	 * @param useSSL 
	 */
	public static newLdapClient(host: string, port: number, bindDN: string, password: string, useSSL: boolean): LdapClient;
}




/**
 * This class provides a data structure for holding information about an LDAP
 * entry. An entry contains a distinguished name (DN) and a set of attributes.
 * An entry can be created from these components, and it can also be created
 * from its LDIF representation as described in RFC 2849.
 *
 */
declare class LdapEntry {
	constructor();
	/**
	 * Creates a new entry with the provided DN and set of attributes.
	 * @param dn
	 * @param values
	 */
	constructor(dn: string, values: LdapAttribute[]);
	/**
	 * Creates a new entry from the provided LDIF representation.
	 * @param entryLines
	 */
	constructor(entryLines: string[]);
}

/**
 * This class provides a data structure for holding information about an LDAP attribute, which
 * includes an attribute name (which may include a set of attribute options) and zero or more values.
 *
 */
declare class LdapAttribute {
	constructor();
	/**
	 * Creates a new LDAP attribute with the specified name and no values.
	 * @param name
	 */
	constructor(name: string);
	/**
	 * Creates a new LDAP attribute with the specified name and no values.
	 * @param name
	 * @param values
	 */
	constructor(name: string, values: string[]);
}

declare class ModificationTypeWrapper {
	constructor();
}

declare class byte {
	constructor();
}

declare class LdapModification {
	constructor();
	/**
	 * Creates a new LDAP modification with the provided information.
	 * @param modificationType
	 * @param attributeName
	 */
	constructor(modificationType: ModificationTypeWrapper, attributeName: string);
	/**
	 * Creates a new LDAP modification with the provided information.
	 * @param modificationType
	 * @param attributeName
	 * @param attributeValue
	 */
	constructor(modificationType: ModificationTypeWrapper, attributeName: string, attributeValue: string);
	/**
	 * Creates a new LDAP modification with the provided information.
	 * @param modificationType
	 * @param attributeName
	 * @param attributeValues
	 */
	constructor(modificationType: ModificationTypeWrapper, attributeName: string, attributeValues: string[]);
	/**
	 * Retrieves the name of the attribute to target with this modification.
	 */
	getAttributeName(): string;
	/**
	 * the set of values for this modification as an array of strings.
	 */
	getValues(): string[];
	/**
	 * Retrieves the set of values for this modification as an array of byte arrays
	 *
	 */
	getValueByteArrays(): any;
}

declare class LdapModificationType {
	static readonly ADD: LdapModificationType;
	static readonly DELETE: LdapModificationType;
	static readonly REPLACE: LdapModificationType;
	static readonly INCREMENT: LdapModificationType;
}

/**
 * This class provides a data structure for holding the elements that are common to most types of
 * LDAP responses.
 *
 */
declare interface LdapResult {
}

/**
 * This class implements the processing necessary to perform an LDAPv3 search operation, which can
 * be used to retrieve entries that match a given set of criteria.
 *
 */
declare interface LdapSearchRequest {
	/**
	 * @param baseDN
	 * @param filter
	 * @param scope
	 * @param attributes
	 * @param derefPolicy
	 * @param sizeLimit
	 * @param timeLimit
	 * @param typesOnly
	 */
	create(baseDN: string, filter: string, scope: LdapSearchScope, attributes: string[], derefPolicy: LdapDereferencePolicy, sizeLimit: number, timeLimit: number, typesOnly: boolean): LdapSearchRequest;
}

/**
 * This class implements the processing necessary to perform an LDAPv3 search operation, which can
 * be used to retrieve entries that match a given set of criteria.
 *
 */
declare interface LdapDeleteRequest {
}

/**
 * This class provides a data structure for holding information about the result of processing a
 * search request.
 *
 */
declare interface LdapSearchResult {
}

/**
 * This class provides a data structure for holding information about an LDAP distinguished name
 * (DN). A DN consists of a comma-delimited list of zero or more RDN components.
 *
 */
declare interface LdapDN {
	/**
	 * Creates a new DN from the provided string representation.
	 * @param dnString
	 */
	fromString(dnString: string): LdapRDN;
	/**
	 * Creates a new DN with the provided set of RDNs.
	 * @param rdns
	 */
	fromRdns(rdns: LdapRDN[]): LdapRDN;
	/**
	 * Creates a new DN below the provided parent DN with the given RDN.
	 * @param rdn
	 * @param parentDN
	 */
	fromParentDn(rdn: LdapRDN, parentDN: LdapDN): LdapRDN;
	/**
	 * Retrieves the leftmost (i.e., furthest from the naming context) RDN component for this
	 * DN.
	 *
	 */
	getRDN(): LdapRDN;
	/**
	 * Retrieves the string representation of the leftmost (i.e., furthest from the naming
	 * context) RDN component for this DN.
	 *
	 */
	getRDNString(): string;
	/**
	 * Retrieves the set of RDNs that comprise this DN.
	 */
	getRDNs(): LdapRDN[];
	/**
	 * Retrieves the set of string representations of the RDNs that comprise this DN.
	 *
	 */
	getRDNStrings(): any[];
	/**
	 * Indicates whether this DN represents the null DN, which does not have any RDN
	 * components.
	 *
	 */
	isNullDN(): boolean;
	/**
	 * Retrieves the DN that is the parent for this DN. Note that neither the null DN nor DNs
	 * consisting of a single RDN component will be considered to have parent DNs.
	 *
	 */
	getParent(): LdapDN;
	/**
	 * Retrieves the string representation of the DN that is the parent for this DN. Note that
	 * neither the null DN nor DNs consisting of a single RDN component will be considered to have
	 * parent DNs.
	 *
	 */
	getParentString(): string;
	/**
	 * Indicates whether this DN is an ancestor of the provided DN. It will be considered an
	 * ancestor of the provided DN if the array of RDN components for the provided DN ends with the
	 * elements that comprise the array of RDN components for this DN (i.e., if the provided DN is
	 * subordinate to, or optionally equal to, this DN). The null DN will be considered an ancestor for
	 * all other DNs (with the exception of the null DN if allowEquals is false).
	 *
	 */
	isAncestorOf(): boolean;
	/**
	 * Indicates whether the provided object is equal to this DN. In order for the provided
	 * object to be considered equal, it must be a non-null DN with the same set of RDN components.
	 *
	 * @param dn
	 */
	equals(dn: LdapDN): boolean;
	/**
	 * Retrieves a string representation of this DN.
	 */
	toString(): string;
	/**
	 * Retrieves a normalized string representation of this DN.
	 */
	toNormalizedString(): string;
}

/**
 * This class provides a data structure for holding information about an LDAP relative
 * distinguished name (RDN). An RDN consists of one or more attribute name-value pairs.
 *
 */
declare interface LdapRDN {
	/**
	 * Creates a new RDN from the provided string representation.
	 * @param rdnString
	 */
	fromString(rdnString: string): LdapRDN;
	/**
	 * Creates a new single-valued RDN with the provided information.
	 * @param attributeName
	 * @param attributeValue
	 */
	fromNameValue(attributeName: string, attributeValue: string): LdapRDN;
	/**
	 * Retrieves the set of attribute names for this RDN.
	 */
	getAttributeNames(): string[];
	/**
	 * Retrieves the set of attribute values for this RDN.
	 */
	getAttributeValues(): string[];
	/**
	 * Retrieves a string representation of this RDN.
	 */
	toString(): string;
	/**
	 * Retrieves a normalized string representation of this RDN.
	 */
	toNormalizedString(): string;
	/**
	 * Indicates whether the provided object is equal to this RDN. The given object will only
	 * be considered equal to this RDN if it is also an RDN with the same set of names and values.
	 *
	 * @param rdn
	 */
	equals(rdn: LdapRDN): boolean;
}

/**
 * TThis class provides an implementation of the subtree delete request control as defined in
 * draft-armijo-ldap-treedelete. This can be used to delete an entry and all subordinate entries in a
 * single operation."
 *
 */
declare interface LdapSubtreeDeleteRequestControl {
}

/**
 * This class provides an implementation of the simple paged results control as defined in RFC
 * 2696.
 * It allows the client to iterate through a potentially large set of search results in subsets of a
 * specified
 * number of entries (i.e., "pages")."
 *
 */
declare class LdapSimplePagedResultsControl {
	constructor();
	/**
	 * Creates a new paged results control with the specified page size and the provided
	 * cookie. This version of the constructor should be used to continue iterating through an existing
	 * set of results, but potentially using a different page size.
	 *
	 * @param pageSize
	 * @param cookie
	 * @param isCritical
	 */
	constructor(pageSize: number, cookie: byte[], isCritical: boolean);
}

/**
 * This class provides a data structure for representing an LDAP search result reference. A search
 * result reference consists of a set of referral URLs and may also include zero or more controls. It
 * describes an alternate location in which additional results for the search may be found. If there are
 * multiple referral URLs, then they should all be considered equivalent ways to access the information
 * (e.g., referrals referencing different servers that may be contacted).
 *
 */
declare interface LdapSearchResultReference {
}



/**
 * Manage Active Directory plug-in configuration
 */
declare class ConfigurationManager {
	/**
	 * Save Active Directory server configuration.
	 * @param param0
	 */
	static saveConfiguration(param0: AD_ServerConfiguration): string;

	/**
	 * Delete Active Directory server configuration with the specified id.
	 * @param param0
	 */
	static deleteConfiguration(param0: string): void;

	/**
	 * Validate Active Directory configuration.
	 * @param param0
	 */
	static validateConfiguration(param0: AD_ServerConfiguration): void;
}







/**
 * Represents single Active Directory service end point configuration.
 */
declare class AD_ServerConfiguration {
	id: string;
	name: string;
	host: string;
	port: number;
	ldapBase: string;
	useSSL: boolean;
	defaultDomain: string;
	useSharedSession: boolean;
	sharedUserName: string;
	sharedUserPassword: string;
	followReferrals: boolean;
	alternativeHosts: string[];
	loadBalancingMode: LdapLoadBalancingMode;

	constructor();
}

/**
 * Unknown type of object
 */
declare interface AD_Unknown {
	readonly id: string;
	readonly distinguishedName: string;
	readonly allAttributes: object[];
	readonly gUID: string;

	/**
	 * Removes an attribute as specified by the attribName parameter.
	 */
	removeAttribute(attribName: string): void;

	/**
	 * Destroy this element from the AD. Take care, this action PERMANENTLY DESTROY the element.
	 */
	destroy(param0: boolean): void;

	/**
	 * Allows a client to change the leftmost (least significant) component of the name of an entry in the directory. Тo rename the entry you must provide it with the attribute as prefix - e.g. "cn=newName".
	 */
	rename(name: string): void;

	/**
	 * Change the value of an existing attribute.
	 */
	setAttribute(attribName: string, newValue: object): void;

	/**
	 * Get an AD attribute.
	 */
	getAttribute(attribName: string): string;

	/**
	 * Adds an attribute.
	 */
	addAttribute(attribName: string, newValue: object): void;

	/**
	 * Get an AD attribute value as byte array.
	 */
	getAttributeValueBytes(attribName: string): any[];

	/**
	 * Get an AD attribute for an array of values.
	 */
	getArrayAttribute(attribName: string): string[];
}

/**
 * User
 */
declare interface AD_User {
	readonly id: string;
	readonly enabled: boolean;
	readonly userPrincipalName: string;
	readonly sID: string;
	readonly accountName: string;
	readonly memberOf: AD_Group[];
	readonly distinguishedName: string;
	readonly allAttributes: any;
	readonly gUID: string;

	/**
	 * Sets the passed in String value as a password of this user.
	 */
	setPassword(password: string): void;

	/**
	 * Sets the user account to change or not change the password at next logon.
	 * @param param0 - Set this value to true if you want to enforce the user to change his password at next logon
	 */
	setChangePasswordAtNextLogon(param0: boolean): void;

	/**
	 * Removes an attribute as specified by the attribName parameter.
	 */
	removeAttribute(attribName: string): void;

	/**
	 * Destroy this element from the AD. Take care, this action PERMANENTLY DESTROY the element.
	 */
	destroy(param0: boolean): void;

	/**
	 * Allows a client to change the leftmost (least significant) component of the name of an entry in the directory. Тo rename the entry you must provide it with the attribute as prefix - e.g. "cn=newName".
	 */
	rename(name: string): void;

	/**
	 * Change the value of an existing attribute.
	 */
	setAttribute(attribName: string, newValue: object): void;

	/**
	 * Get an AD attribute.
	 */
	getAttribute(attribName: string): string;

	/**
	 * Adds an attribute.
	 */
	addAttribute(attribName: string, newValue: object): void;

	/**
	 * Get an AD attribute value as byte array.
	 */
	getAttributeValueBytes(attribName: string): object[];

	/**
	 * Get an AD attribute for an array of values.
	 */
	getArrayAttribute(attribName: string): string[];
}

/**
 * UserGroup
 */
declare interface AD_UserGroup {
	readonly id: string;
	readonly sID: string;
	readonly computerMembers: AD_Computer[];
	readonly userMembers: AD_User[];
	readonly memberOf: AD_UserGroup[];
	readonly groupMembers: AD_UserGroup[];
	readonly distinguishedName: string;
	readonly allAttributes: object[];
	readonly gUID: object;

	/**
	 * Removes elements from the group.
	 * @elements - All elements to remove from the group
	 */
	removeElements(elements: object[]): void;

	/**
	 * Adds elements to the group.
	 * @elements - All elements to remove from the group
	 */
	addElements(elements: object[]): void;

	/**
	 * Removes an attribute as specified by the attribName parameter.
	 */
	removeAttribute(attribName: string): void;

	/**
	 * Destroy this element from the AD. Take care, this action PERMANENTLY DESTROY the element.
	 */
	destroy(param0: boolean): void;

	/**
	 * Allows a client to change the leftmost (least significant) component of the name of an entry in the directory. Тo rename the entry you must provide it with the attribute as prefix - e.g. "cn=newName".
	 */
	rename(name: string): void;

	/**
	 * Change the value of an existing attribute.
	 */
	setAttribute(attribName: string, newValue: object): void;

	/**
	 * Get an AD attribute.
	 */
	getAttribute(attribName: string): string;

	/**
	 * Adds an attribute.
	 */
	addAttribute(attribName: string, newValue: object): void;

	/**
	 * Get an AD attribute value as byte array.
	 */
	getAttributeValueBytes(attribName: string): object[];

	/**
	 * Get an AD attribute for an array of values.
	 */
	getArrayAttribute(attribName: string): string[];
}

/**
 * Ldap search scope
 */
declare class LdapSearchScope {
	static readonly BASE: LdapSearchScope;
	static readonly ONE: LdapSearchScope;
	static readonly SUB: LdapSearchScope;
	static readonly SUBORDINATE_SUBTREE: LdapSearchScope;
}

/**
 * This class defines a data type for dereference policy values. Clients should
 * generally use one of the {@code NEVER}, {@code SEARCHING}, {@code FINDING},
 * {@code ALWAYS} values.
 *
 */
declare class LdapDereferencePolicy {
	static readonly NEVER: LdapDereferencePolicy;
	static readonly SEARCHING: LdapDereferencePolicy;
	static readonly FINDING: LdapDereferencePolicy;
	static readonly ALWAYS: LdapDereferencePolicy;
}

/**
 * This class provides a data structure that represents an LDAP search filter. It provides methods
 * for as parsing a filter from a string. See RFC 4515 for more information about representing search
 * filters as strings.
 *
 */
declare interface LdapFilter {
	/**
	 * Creates a new search filter from the provided string representation.
	 * @param filterString
	 */
	create(filterString: string): LdapFilter;
	/**
	 * Encodes the provided value into a form suitable for use as the assertion value in the
	 * string representation of a search filter. Parentheses, asterisks, backslashes, null characters,
	 * and any non-ASCII characters will be escaped using a backslash before the hexadecimal
	 * representation of each byte in the character to escape.
	 *
	 * @param value
	 */
	encodeValue(value: string): string;
	/**
	 * Encodes the provided value into a form suitable for use as the assertion value in the
	 * string representation of a search filter. Parentheses, asterisks, backslashes, null characters,
	 * and any non-ASCII characters will be escaped using a backslash before the hexadecimal
	 * representation of each byte in the character to escape.
	 *
	 * @param value
	 */
	encodeValueBytes(value: any): string;
}

declare interface LdapLoadBalancingMode {
	readonly SingleServer: LdapLoadBalancingMode;
	readonly RoundRobin: LdapLoadBalancingMode;
	readonly Failover: LdapLoadBalancingMode;
	/**
	 * Returns load balancing mode as string.
	 */
	getValue(): string;
	/**
	 * Returns supported load balancing modes .
	 */
	getValuesAsString(): any;
	/**
	 * Create LdapLoadBalancingMode object from it's string representation.
	 * @param value
	 */
	fromString(value: string): LdapLoadBalancingMode;
}
