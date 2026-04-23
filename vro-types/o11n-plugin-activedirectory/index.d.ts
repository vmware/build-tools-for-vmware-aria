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

/**
 * Represents single Active Directory service end point configuration.
 */
declare class AD_ServerConfiguration {
	// Id
	id: string;
	// Name
	name: string;
	// Active Directory host
	host: string;
	// Active Directory port
	port: number;
	// Root
	ldapBase: string;
	// Use SSL
	useSSL: boolean;
	// Default domain
	defaultDomain: string;
	// Use a shared session (deprecated)
	useSharedSession: boolean;
	// User name for shared session
	sharedUserName: string;
	// Password for shared session
	sharedUserPassword: string;
	// Follow refferals
	followReferrals: boolean;
	// Alternative host adresses
	alternativeHosts: string[];
	// Strategy for load balancing between configured alternative hosts.
	loadBalancingMode: LdapLoadBalancingMode;
	// Bind Type. Supported bind types are 'Simple' and 'Digest'.
	bindType: LdapBindType;

	constructor();
}

/**
 * Unknown type of object
 */
declare interface AD_Unknown {
	// the unique Id of the element
	readonly id: string;
	// Return object GUID formatted as dashed string
	readonly gUID: string;
	// Return the DN of the item
	readonly distinguishedName: string;
	// Returns all attributes
	readonly allAttributes: any[];

	/**
	 * Get an AD attribute for an array of values..
	 * 
	 * @param attribName 
	 */
	getArrayAttribute(attribName: string): string[];
	/**
	 * Get an AD attribute value as byte array.
	 * 
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
	 * Destroy this element from the AD. Take care, this action PERMANENTLY DESTROY the element.
	 * 
	 * @param param0 
	 */
	destroy(param0: boolean): void;
	/**
	 * Allows a client to change the leftmost (least significant) component of the name of an entry in the directory. Тo rename the entry you must provide it with the attribute as prefix - e.g. "cn=newName".
	 * 
	 * @param name 
	 */
	rename(name: string): void;
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
 * User
 */
declare interface AD_User {
	// the unique Id of the element
	readonly id: string;
	// Return groups that contains the user
	readonly memberOf: AD_UserGroup[];
	// Return the SAM Account Name
	readonly accountName: string;
	// The SID represented as a character string with following format 'S-1-IdentifierAuthority-SubAuthority1-SubAuthority2-...-SubAuthorityn'
	readonly sID: string;
	// The user principal name
	readonly userPrincipalName: string;
	// Activate or deactivate a user
	readonly enabled: boolean;
	// Return object GUID formatted as dashed string
	readonly gUID: string;
	// Return the DN of the item
	readonly distinguishedName: string;
	// Returns all attributes
	readonly allAttributes: any[];

	/**
	 * Sets the user account to change or not change the password at next logon.
	 * @param param0
	 */
	setChangePasswordAtNextLogon(param0: boolean): void;
	/**
	 * Sets the passed in String value as a password of this user.
	 * 
	 * @param password 
	 */
	setPassword(password: string): void;
	/**
	 * Get an AD attribute for an array of values.
	 * 
	 * @param attribName 
	 */
	getArrayAttribute(attribName: string): string[];
	/**
	 * Get an AD attribute value as byte array.
	 * 
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
	 * Destroy this element from the AD. Take care, this action PERMANENTLY DESTROY the element.
	 * 
	 * @param param0 
	 */
	destroy(param0: boolean): void;
	/**
	 * Allows a client to change the leftmost (least significant) component of the name of an entry in the directory. Тo rename the entry you must provide it with the attribute as prefix - e.g. "cn=newName".
	 * 
	 * @param name 
	 */
	rename(name: string): void;
	/**
	 * Change the value of an existing attribute.
	 * 
	 * @param attribName 
	 * @param newValue 
	 */
	setAttribute(attribName: string, newValue: any): void;
	/**
	 * Get an AD attribute.
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
 * UserGroup
 */
declare interface AD_UserGroup {
	// the unique Id of the element
	readonly id: string;
	// Gets the users that are members of the current group
	readonly userMembers: AD_User[];
	// Gets the groups that are members of the current group
	readonly groupMembers: AD_UserGroup[];
	// Get the group of which current group is member
	readonly memberOf: AD_UserGroup[];
	// Gets the computers that are members of the current group
	readonly computerMembers: AD_Computer[];
	// The SID represented as a character string with following format 'S-1-IdentifierAuthority-SubAuthority1-SubAuthority2-...-SubAuthorityn'
	readonly sID: string;
	// Return object GUID formatted as dashed string
	readonly gUID: string;
	// Return the DN of the item
	readonly distinguishedName: string;
	// Returns all attributes
	readonly allAttributes: any[];

	/**
	 * Adds elements to the group.
	 * 
	 * @elements - All elements to remove from the group
	 */
	addElements(elements: any[]): void;
	/**
	 * Removes elements from the group.
	 * 
	 * @elements - All elements to remove from the group
	 */
	removeElements(elements: any[]): void;
	/**
	 * Get an AD attribute for an array of values.
	 * 
	 * @param attribName 
	 */
	getArrayAttribute(attribName: string): string[];
	/**
	 * Get an AD attribute value as byte array.
	 * 
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
	 * Destroy this element from the AD. Take care, this action PERMANENTLY DESTROY the element.
	 * 
	 * @param param0 
	 */
	destroy(param0: boolean): void;
	/**
	 * Allows a client to change the leftmost (least significant) component of the name of an entry in the directory. Тo rename the entry you must provide it with the attribute as prefix - e.g. "cn=newName".
	 * 
	 * @param name 
	 */
	rename(name: string): void;
	/**
	 * Change the value of an existing attribute.
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
	 * @param attribName 
	 * @param newValue 
	 */
	addAttribute(attribName: string, newValue: any): void;
}

/**
 * Manage Active Directory plug-in configuration
 */
declare class ConfigurationManager {
	private constructor();

	/**
	 * Validate Active Directory configuration.
	 * 
	 * @param param0
	 */
	static validateConfiguration(param0: AD_ServerConfiguration): void;
	/**
	 * Save ActiveDirectory plugin options.
	 * 
	 * @param param0 
	 */
	static savePluginOptions(param0: any): void;
	/**
	 * Save Active Directory server configuration. If provided configuration id is not set new configuration will be created otherwise existing configuration will be updated.
	 * 
	 * @param param0
	 */
	static saveConfiguration(param0: AD_ServerConfiguration): string;
	/**
	 * Delete Active Directory server configuration with the specified id.
	 * 
	 * @param param0
	 */
	static deleteConfiguration(param0: string): void;
	/**
	 * Get global ActiveDirectory plugin options.
	 */
	static getPluginOptions(): AD_PluginOptions;
	/**
	 * Return Active Directory server configuration with specified id. If id is not provided default Active Directory server will be returned.
	 * 
	 * @param param0 
	 */
	static getConfigurationById(param0: string): AD_ServerConfiguration;
	/**
	 * Delete Active Directory server configuration. Since version 2.0.0 of the plug-in multiple Active Directory servers can be configured. This method will delete default Active Directory server configuration.
	 */
	static reset(): void;
	/**
	 * Return available Active Directory server configurations.
	 */
	static findAll(): any[];
	/**
	 * Configure Active Directory server. Since version 2.0.0 of Active Directory plug-in multiple Active Directory servers can be configured.This method will update default Active Directory configuration if exists otherwise new configuration will be created.Deprecated use saveConfiguration(ServerConfiguration).
	 * 
	 * @param param0 
	 */
	static updateConfiguration(param0: AD_ServerConfiguration): void;
}

/**
 * This class provides a data structure for holding information about an LDAP attribute, which
 * includes an attribute name (which may include a set of attribute options) and zero or more values.
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

	/**
	 * Retrieves the set of values for this attribute as byte arrays.
	 */
	getValueByteArrays(): any[];
	/**
	 * Retrieves the value for this attribute as a Date.
	 */
	getValueAsDate(): any;
	/**
	 * Retrieves the value for this attribute as a byte arrays.
	 */
	getValueByteArrays(): any[];
	/**
	 * Indicates whether this attribute contains the specified value.
	 * 
	 * @param param0 
	 */
	hasValueAsByte(param0: any[]): boolean;
	/**
	 * Retrieves the value for this attribute as a long.
	 */
	getValueAsLong(): number;
	/**
	 * Retrieves the set of values for this attribute as strings.
	 */
	getValues(): string[];
	/**
	 * Retrieves the name for this attribute (i.e., the attribute description), which may include zero or more attribute options.
	 */
	getName(): string;
	/**
	 * Retrieves the value for this attribute as a string.
	 */
	getValue(): string;
	/**
	 * Retrieves the number of values for this attribute.
	 */
	size(): number;
	/**
	 * Indicates whether this attribute contains the specified value.
	 * 
	 * @param param0 
	 */
	hasValue(param0: string): boolean;
	/**
	 * Retrieves the base name for this attribute, which is the name or OID of the attribute type, without any attribute options.
	 */
	getBaseName(): string;
	/**
	 * Retrieves the set of options for this attribute.
	 */
	getOptions(): any[];
}

/**
 * LdapBindType
 */
declare class LdapBindType {
	private constructor();

	/** Use Simple bind */
	public static readonly Simple: LdapBindType;
	/** Use Digest. */
	public static readonly Digest: LdapBindType;

	/**
	 * Returns Bind Type as string.
	 */
	getValue(): string;
	/**
	 * Returns supported bind types.
	 */
	public static getValuesAsString(): string[];
	/**
	 * Create BindType object from it's string representation.
	 * 
	 * @param value 
	 */
	public static fromString(value: string): LdapBindType;
}


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

	/** Retrieves the user-friendly name that has been assigned to this connection. */
	getConnectionName(): string;
	/** Retrieves the disconnect message for this connection, which may provide additional information about the reason for the disconnect, if available. */
	getDisconnectMessage(): string;
	/** Retrieves the time that this connection was last used to send or receive an LDAP message. The value will represent the number of milliseconds since January 1, 1970 UTC (the same format used by System.currentTimeMillis. */
	getLastCommunicationTime(): number;
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
	/**
	 * Applies the provided modification to the specified entry.
	 * 
	 * @param dn 
	 * @param newRDN 
	 * @param deleteOldRDN 
	 * @param newSuperiorDN 
	 */
	modifyDN(dn: string, newRDN: string, deleteOldRDN: boolean, newSuperiorDN: string): LdapResult;
	/** Retrieves the time that this connection was established in the number of milliseconds since January 1, 1970 UTC (the same format used by System.currentTimeMillis. */
	getConnectTime(): number;
	/**
	 * Applies the provided modification to the specified entry.
	 * 
	 * @param dn 
	 * @param modificaitons 
	 */
	modify(dn: string, modificaitons: any[]): any;
	/** Attempts to re-establish a connection to the server and re-authenticate if appropriate. */
	reconnect(): void;
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
 * This class implements the processing necessary to perform an LDAPv3 search operation, which can
 * be used to retrieve entries that match a given set of criteria.
 */
declare class LdapDeleteRequest {
	private constructor();

	/** Retrieves the DN of the entry to delete. */
	getDN(): string;
	/**
	 * Specifies the DN of the entry to delete.
	 * 
	 * @param dn 
	 */
	setDN(dn: string): void;
	/**
	 * Creates a new instance of this LDAP request that may be modified without impacting this request. The provided controls will be used for the new request instead of duplicating the controls from this request.
	 * 
	 * @param controls
	 */
	duplicate(controls: any[]): LdapDeleteRequest;
	/**
	 * Factory method for creating new LdapDeleteRequest instances from provided info.
	 * 
	 * @param param0 
	 * @param param1 
	 */
	public static createRequest(param0: string, param1: any[]): LdapDeleteRequest;
	/** Retrieves the type of operation that is represented by this request. */
	getOperationType(): any;
	/**
	 * Specifies whether to automatically follow any referrals encountered while processing this request. This may be used to override the default behavior defined in the connection options for the connection used to process the request.
	 * 
	 * @param followReferrals 
	 */
	setFollowReferrals(followReferrals: boolean): void;
	/**
	 * Indicates whether to automatically follow any referrals encountered while processing this request. If a value has been set for this request, then it will be returned. Otherwise, the default from the connection options for the provided connection will be used.
	 * 
	 * @param client 
	 */
	followReferrals(client: LdapClient): boolean;
	/** Retrieves the set of controls for this request. The caller must not alter this set of controls. */
	getControls(): any[];
	/**
	 * Retrieves the maximum length of time in milliseconds that processing on this operation should be allowed to block while waiting for a response from the server.
	 * 
	 * @param client 
	 */
	getResponseTimeoutMillis(client: LdapClient): number;
	/**
	 * Specifies the maximum length of time in milliseconds that processing on this operation should be allowed to block while waiting for a response from the server. A value of zero indicates that no timeout should be enforced. A value that is less than zero indicates that the default response timeout for the underlying connection should be used.
	 * 
	 * @param responseTimeout 
	 */
	setResponseTimeoutMillis(responseTimeout: number): void;
	/** Retrieves the message ID for the last LDAP message sent using this request. */
	getLastMessageID(): number;
	/**
	 * Replaces the control with the same OID as the provided control with the provided control. If no control with the same OID exists in the request, then the control will be added to the request. If the request has multiple controls with the same OID as the new control, then only the first will be replaced.
	 * 
	 * @param control 
	 */
	replaceControl(control: any): any;
	/** Removes all controls from this request. */
	clearControls(): void;
	/**
	 * Replaces the control with the specified OID with the provided control. If no control with the given OID exists in the request, then a new control will be added. If this request has multiple controls with the specified OID, then only the first will be replaced.
	 * 
	 * @param oid 
	 * @param control 
	 */
	replaceControlByOid(oid: string, control: any): void;
	/**
	 * Indicates whether this request contains at least one control with the specified OID.
	 * 
	 * @param oid 
	 */
	hasControlByOid(oid: string): boolean;
	/**
	 * Removes the control with the specified OID from the set of controls for this request. If this request has multiple controls with the same OID, then only the first will be removed.
	 * 
	 * @param oid 
	 */
	removeControlByOid(oid: string): any;
	/**
	 * Adds the provided control to the set of controls for this request.
	 * 
	 * @param control 
	 */
	addControl(control: any): void;
	/** Indicates whether this request contains at least one control. */
	hasControl(): boolean;

	/**
	 * Retrieves the control with the specified OID from this request. If this request has multiple controls with the specified OID, then the first will be returned.
	 * 
	 * @param oid 
	 */
	getControl(oid: string): void;
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
 * This class provides a data structure for holding information about an LDAP distinguished name
 * (DN). A DN consists of a comma-delimited list of zero or more RDN components.
 *
 */
declare class LdapDN {
	private constructor();

	/**
	 * Creates a new DN from the provided string representation.
	 * @param dnString
	 */
	public static fromString(dnString: string): LdapRDN;
	/**
	 * Creates a new DN with the provided set of RDNs.
	 * @param rdns
	 */
	public static fromRdns(rdns: LdapRDN[]): LdapRDN;
	/**
	 * Creates a new DN below the provided parent DN with the given RDN.
	 * @param rdn
	 * @param parentDN
	 */
	public static fromParentDn(rdn: LdapRDN, parentDN: LdapDN): LdapRDN;
	/**
	 * Retrieves the leftmost (i.e., furthest from the naming context) RDN component for this
	 * DN.
	 */
	getRDN(): LdapRDN;
	/**
	 * Retrieves the string representation of the leftmost (i.e., furthest from the naming
	 * context) RDN component for this DN.
	 */
	getRDNString(): string;
	/**
	 * Retrieves the set of RDNs that comprise this DN.
	 */
	getRDNs(): LdapRDN[];
	/**
	 * Retrieves the set of string representations of the RDNs that comprise this DN.
	 */
	getRDNStrings(): string[];
	/**
	 * Indicates whether this DN represents the null DN, which does not have any RDN
	 * components.
	 */
	isNullDN(): boolean;
	/**
	 * Retrieves the DN that is the parent for this DN. Note that neither the null DN nor DNs
	 * consisting of a single RDN component will be considered to have parent DNs.
	 */
	getParent(): LdapDN;
	/**
	 * Retrieves the string representation of the DN that is the parent for this DN. Note that
	 * neither the null DN nor DNs consisting of a single RDN component will be considered to have
	 * parent DNs.
	 */
	getParentString(): string;
	/**
	 * Indicates whether this DN is an ancestor of the provided DN. It will be considered an
	 * ancestor of the provided DN if the array of RDN components for the provided DN ends with the
	 * elements that comprise the array of RDN components for this DN (i.e., if the provided DN is
	 * subordinate to, or optionally equal to, this DN). The null DN will be considered an ancestor for
	 * all other DNs (with the exception of the null DN if allowEquals is false).
	 * 
	 * @param param0 
	 * @param param1 
	 */
	isAncestorOf(param0: LdapDN, param1: boolean): boolean;
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

	/** Retrieves the DN for this entry. */
	getDN(): string;
	/**
	 * Specifies the DN for this entry.
	 * 
	 * @param param0 
	 */
	setDN(param0: string): void;
	/** Retrieves the parsed DN for this entry. */
	getParsedDN(): LdapDN;
	/** Retrieves the parent DN for this entry as a string. */
	getParentDNString(): string;
	/**
	 * Indicates whether this entry contains an attribute with the given name and value.
	 * 
	 * @param param0 
	 * @param param1 
	 */
	hasAttributeValue(param0: string, param1: string): boolean;
	/**
	 * Indicates whether this entry contains the specified object class.
	 * 
	 * @param param0 
	 */
	hasObjectClass(param0: string): boolean;
	/**
	 * Retrieves the value for the specified attribute, if available. If the attribute has more than one value, then the first value will be returned.
	 * 
	 * @param param0 
	 */
	getAttributeValueAsBoolean(param0: string): boolean;
	/**
	 * Retrieves the value for the specified attribute, if available. If the attribute has more than one value, then the first value will be returned.
	 * 
	 * @param param0 
	 */
	getAttributeValueAsDate(param0: string): any;
	/**
	 * Retrieves the value for the specified attribute, if available. If the attribute has more than one value, then the first value will be returned.
	 * 
	 * @param param0 
	 */
	getAttributeValueAsInteger(param0: string): number;
	/**
	 * Retrieves the value for the specified attribute, if available. If the attribute has more than one value, then the first value will be returned.
	 * 
	 * @param param0 
	 */
	getAttributeValueAsLong(param0: string): number;
	/** Retrieves the values of the "objectClass" attribute from the entry, if available. */
	getObjectClassValues(): string[];
	/**
	 * Retrieves an LDIF-formatted string representation of this entry. No wrapping will be performed, and no extra blank lines will be added.
	 * 
	 * @param param0 
	 */
	toLDIFString(param0: number): string;
	/** Retrieves an LDIF representation of this entry, with each attribute value on a separate line. Long lines will not be wrapped. */
	toLDIF(): string[];
	/**
	 * Indicates whether this entry contains the specified attribute.
	 * 
	 * @param param0 
	 */
	hasAttributeWithName(param0: string): boolean;
	/**
	 * Indicates whether this entry contains an attribute with the given name and value.
	 * 
	 * @param param0 
	 * @param param1 
	 */
	hasAttributeValueBytes(param0: string, param1: any[]): boolean;
	/**
	 * Retrieves the value for the specified attribute, if available. If the attribute has more than one value, then the first value will be returned.
	 * 
	 * @param param0 
	 */
	getAttributeValueAsBytes(param0: string): any[];
	/**
	 * Adds the specified attribute value to this entry, if it is not already present.
	 * 
	 * @param param0 
	 * @param param1 
	 */
	addAttributeValue(param0: string, param1: string): boolean;
	/**
	 * Adds the specified attribute value to this entry, if it is not already present.
	 * 
	 * @param param0 
	 * @param param1 
	 */
	addAttributeValueAsByte(param0: string, param1: any[]): boolean;
	/**
	 * Adds the provided attribute to this entry. If this entry already contains an attribute with the same name, then their values will be merged.
	 * 
	 * @param param0 
	 * @param param1 
	 */
	addAttributeValues(param0: string, param1: any[]): boolean;
	/**
	 * Adds the provided attribute to this entry, replacing any existing set of values for the associated attribute.
	 * 
	 * @param param0 
	 * @param param1 
	 */
	setAttributeValue(param0: string, param1: string): void;
	/**
	 * Adds the provided attribute to this entry, replacing any existing set of values for the associated attribute.
	 * 
	 * @param param0 
	 * @param param1 
	 */
	setAttributeValueAsBytes(param0: string, param1: any[]): void;
	/**
	 * Adds the provided attribute to this entry, replacing any existing set of values for the associated attribute.
	 * 
	 * @param param0 
	 * @param param1 
	 */
	setAttributeValues(param0: string, param1: any[]): void;
	/**
	 * Retrieves the set of values for the specified attribute, if available
	 * 
	 * @param param0 
	 */
	getAttributeValueByteArrays(param0: string): any[];
	/**
	 * Removes the specified attribute value from this entry if it is present. If it is the last value for the attribute, then the entire attribute will be removed. If the specified value is not present, then no change will be made.
	 * 
	 * @param param0 
	 * @param param1 
	 */
	removeAttributeValueAsBytes(param0: string, param1: any[]): boolean;
	/**
	 * Retrieves the set of values for the specified attribute, if available
	 * 
	 * @param param0 
	 */
	getAttributeValues(param0: string): string[];
	/**
	 * Removes the specified attribute value from this entry if it is present. If it is the last value for the attribute, then the entire attribute will be removed. If the specified value is not present, then no change will be made.
	 * 
	 * @param param0 
	 * @param param1 
	 */
	removeAttributeValue(param0: string, param1: string): boolean;
	/**
	 * Indicates whether this entry contains the specified attribute.
	 * 
	 * @param param0 
	 */
	hasAttribute(param0: LdapAttribute): boolean;
	/**
	 * Retrieves the value for the specified attribute, if available. If the attribute has more than one value, then the first value will be returned.
	 * 
	 * @param param0 
	 */
	getAttributeValue(param0: string): string;
	/**
	 * Removes the specified attribute from this entry.
	 * 
	 * @param param0 
	 */
	removeAttribute(param0: string): boolean;
	/** Retrieves the set of attributes contained in this entry */
	getAttributes(): any[];
	/**
	 * Retrieves the attribute with the specified name.
	 * 
	 * @param param0 
	 */
	getAttribute(param0: string): LdapAttribute;
	/**
	 * Adds the specified attribute to this entry, if it is not already present.
	 * 
	 * @param param0 
	 */
	addAttribute(param0: LdapAttribute): boolean;
}

/**
 * This class provides a data structure that represents an LDAP search filter. It provides methods
 * for as parsing a filter from a string. See RFC 4515 for more information about representing search
 * filters as strings.
 *
 */
declare class LdapFilter {
	private constructor();

	/**
	 * Creates a new search filter from the provided string representation.
	 * @param filterString
	 */
	public static create(filterString: string): LdapFilter;
	/**
	 * Encodes the provided value into a form suitable for use as the assertion value in the
	 * string representation of a search filter. Parentheses, asterisks, backslashes, null characters,
	 * and any non-ASCII characters will be escaped using a backslash before the hexadecimal
	 * representation of each byte in the character to escape.
	 *
	 * @param value
	 */
	public static encodeValue(value: string): string;
	/**
	 * Encodes the provided value into a form suitable for use as the assertion value in the
	 * string representation of a search filter. Parentheses, asterisks, backslashes, null characters,
	 * and any non-ASCII characters will be escaped using a backslash before the hexadecimal
	 * representation of each byte in the character to escape.
	 *
	 * @param value
	 */
	public static encodeValueBytes(value: any): string;
}

declare class LdapLoadBalancingMode {
	private constructor();

	// Connect to a single server
	public static readonly SingleServer: LdapLoadBalancingMode;
	// Use a round-robin algorithm to select the server to which the connection should be established. Any number of servers may be included in this server set, and each request will attempt to retrieve a connection to the next server in the list.
	public static readonly RoundRobin: LdapLoadBalancingMode;
	// Establish connections to servers in the order they are provided. If the first server is unavailable, then it will attempt to connect to the second, then to the third.
	public static readonly Failover: LdapLoadBalancingMode;

	/**
	 * Returns load balancing mode as string.
	 */
	getValue(): string;
	/**
	 * Returns supported load balancing modes .
	 */
	public static getValuesAsString(): any;
	/**
	 * Create LdapLoadBalancingMode object from it's string representation.
	 * @param value
	 */
	public static fromString(value: string): LdapLoadBalancingMode;
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
	private constructor();

	public static readonly ADD: LdapModificationType;
	public static readonly DELETE: LdapModificationType;
	public static readonly REPLACE: LdapModificationType;
	public static readonly INCREMENT: LdapModificationType;
}

/**
 * This class provides a data structure for holding information about an LDAP relative
 * distinguished name (RDN). An RDN consists of one or more attribute name-value pairs.
 *
 */
declare class LdapRDN {
	private constructor();

	/**
	 * Creates a new RDN from the provided string representation.
	 * @param rdnString
	 */
	public static fromString(rdnString: string): LdapRDN;
	/**
	 * Creates a new single-valued RDN with the provided information.
	 * @param attributeName
	 * @param attributeValue
	 */
	public static fromNameValue(attributeName: string, attributeValue: string): LdapRDN;
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
 * This class provides a data structure for holding the elements that are common to most types of
 * LDAP responses.
 *
 */
declare interface LdapResult {
	/** Retrieves the diagnostic message from the response, if available. */
	getDiagnosticMessage(): string;
	/** Retrieves a string representation of this LDAP result, consisting of the result code, diagnostic message (if present), matched DN (if present), and referral URLs (if present). */
	getResultString(): string;
	/** Retrieves the result code from the response. */
	getResultCode(): any;
	/** Retrieves the matched DN from the response, if available. */
	getMatchedDN(): string;
	/** Retrieves the set of referral URLs from the response, if available. */
	getReferralURLs(): string[];
}

/**
 * This class implements the processing necessary to perform an LDAPv3 search operation, which can
 * be used to retrieve entries that match a given set of criteria.
 *
 */

declare class LdapSearchRequest {
	private constructor();

	/** Indicates whether the server should return only attribute names in matching entries, rather than both names and values. */
	typesOnly(): boolean;
	/** Retrieves the maximum number of entries that should be returned by the server when processing this search request. */
	getSizeLimit(): number;
	/** Retrieves the maximum length of time in seconds that the server should spend processing this search request. */
	getTimeLimitSeconds(): number;
	/**
	 * Specifies the maximum number of entries that should be returned by the server when processing this search request. A value of zero indicates that there should be no limit.
	 * 
	 * @param sizeLimit 
	 */
	setSizeLimit(sizeLimit: number): void;
	/**
	 * Specifies the dereference policy that should be used by the server for any aliases encountered during search processing.
	 * 
	 * @param param0 
	 */
	setDerefPolicy(param0: LdapDereferencePolicy): void;
	/**
	 * Specifies whether the server should return only attribute names in matching entries, rather than both names and values.
	 * 
	 * @param typesOnly 
	 */
	setTypesOnly(typesOnly: boolean): void;
	/** Retrieves the set of requested attributes to include in matching entries. */
	getAttributeList(): any[];
	/**
	 * Specifies the maximum length of time in seconds that the server should spend processing this search request. A value of zero indicates that there should be no limit.
	 * 
	 * @param timeLimit 
	 */
	setTimeLimitSeconds(timeLimit: number): void;
	/** Retrieves the base DN for this search request */
	getBaseDN(): string;
	/**
	 * Specifies the base DN for this search request.
	 * 
	 * @param baseDN 
	 */
	setBaseDN(baseDN: string): void;
	/**
	 * Creates a new instance of this LDAP request that may be modified without impacting this request. The provided controls will be used for the new request instead of duplicating the controls from this request.
	 * 
	 * @param controls 
	 */
	duplicate(controls: any[]): LdapSearchRequest;
	/**
	 * Specifies the filter that should be used to identify matching entries.
	 * 
	 * @param filter 
	 */
	setFilter(filter: string): void;
	/**
	 * Factory method for creating new LdapSearchRequest instances from provided info.
	 * 
	 * @param param0 
	 * @param param1 
	 * @param param2 
	 * @param param3 
	 * @param param4 
	 * @param param5 
	 * @param param6 
	 * @param param7 
	 */
	public static createRequest(param0: string, param1: string, param2: LdapSearchScope, param3: string[], param4: LdapDereferencePolicy, param5: number, param6: number, param7: boolean): LdapSearchRequest;
	/**
	 * Specifies the scope for this search request.
	 * 
	 * @param param0 
	 */
	setScope(param0: LdapSearchScope): void;
	/**
	 * Specifies the set of requested attributes to include in matching entries.
	 * 
	 * @param attributes 
	 */
	setAttributes(attributes: any[]): void;
	/** Retrieves the scope for this search request. */
	getScope(): LdapSearchScope;
	/**
	 * Specifies whether to automatically follow any referrals encountered while processing this request. This may be used to override the default behavior defined in the connection options for the connection used to process the request.
	 * 
	 * @param followReferrals 
	 */
	setFollowReferrals(followReferrals: boolean): void;
	/**
	 * Indicates whether to automatically follow any referrals encountered while processing this request. If a value has been set for this request, then it will be returned. Otherwise, the default from the connection options for the provided connection will be used.
	 * 
	 * @param client 
	 */
	followReferrals(client: LdapClient): boolean;
	/** Indicates whether this request contains at least one control. */
	hasControl(): boolean;
	/** Retrieves the set of controls for this request. The caller must not alter this set of controls. */
	getControls(): any[];
	/**
	 * Retrieves the maximum length of time in milliseconds that processing on this operation should be allowed to block while waiting for a response from the server.
	 * 
	 * @param client 
	 */
	getResponseTimeoutMillis(client: LdapClient): number;
	/**
	 * Specifies the maximum length of time in milliseconds that processing on this operation should be allowed to block while waiting for a response from the server. A value of zero indicates that no timeout should be enforced. A value that is less than zero indicates that the default response timeout for the underlying connection should be used.
	 * 
	 * @param responseTimeout 
	 */
	setResponseTimeoutMillis(responseTimeout: number): void;
	/** Retrieves the message ID for the last LDAP message sent using this request. */
	getLastMessageID(): number;
	/**
	 * Replaces the control with the same OID as the provided control with the provided control. If no control with the same OID exists in the request, then the control will be added to the request. If the request has multiple controls with the same OID as the new control, then only the first will be replaced.
	 * 
	 * @param control 
	 */
	replaceControl(control: any): any;
	/** Removes all controls from this request. */
	clearControls(): void;
	/**
	 * Replaces the control with the specified OID with the provided control. If no control with the given OID exists in the request, then a new control will be added. If this request has multiple controls with the specified OID, then only the first will be replaced.
	 * 
	 * @param oid 
	 * @param control 
	 */
	replaceControlByOid(oid: string, control: any): void;
	/**
	 * Indicates whether this request contains at least one control with the specified OID.
	 * 
	 * @param oid 
	 */
	hasControlByOid(oid: string): boolean;
	/**
	 * Removes the control with the specified OID from the set of controls for this request. If this request has multiple controls with the same OID, then only the first will be removed.
	 * 
	 * @param oid 
	 */
	removeControlByOid(oid: string): any;
	/**
	 * Adds the provided control to the set of controls for this request.
	 * 
	 * @param control 
	 */
	addControl(control: any): void;
	/** Retrieves the type of operation that is represented by this request. */
	getOperationType(): any;
	/**
	 * Retrieves the control with the specified OID from this request. If this request has multiple controls with the specified OID, then the first will be returned.
	 * 
	 * @param oid 
	 */
	getControl(oid: string): void;
}

/**
 * This class provides a data structure for holding information about the result of processing a
 * search request.
 *
 */
declare interface LdapSearchResult {
	/** Retrieves the diagnostic message from the response, if available. */
	getDiagnosticMessage(): string;
	/** Retrieves the search result entry with the specified DN from the set of entries returned. This will only be available if a SearchResultListener was not used during the search. */
	getSearchEntries(): any[];
	/** Retrieves the result code from the response. */
	getResultCode(): any;
	/** Retrieves the matched DN from the response, if available. */
	getMatchedDN(): string;
	/** Retrieves the number of search references returned for the search operation. This may be zero even if search references were received if the connection used when processing the search was configured to automatically follow referrals. */
	getReferenceCount(): number;
	/**
	 * Retrieves the search result entry with the specified DN from the set of entries returned. This will only be available if a SearchResultListener was not used during the search.
	 * 
	 * @param dn 
	 */
	getSearchEntry(dn: string): LdapEntry;
	/** Retrieves a list containing the search references returned from the search operation. May be empty even if search references were received if the connection used when processing the search was configured to automatically follow referrals. */
	getSearchReferences(): any[];
	/** Retrieves the number of matching entries returned for the search operation. */
	getEntryCount(): number;
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
	/** Retrieves the set of referral URLs for this search result reference. */
	getReferralURLs(): string[];
}

/**
 * Ldap search scope
 */
declare class LdapSearchScope {
	public static readonly BASE: LdapSearchScope;
	public static readonly ONE: LdapSearchScope;
	public static readonly SUB: LdapSearchScope;
	public static readonly SUBORDINATE_SUBTREE: LdapSearchScope;
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
	constructor(pageSize: number, cookie: any[], isCritical: boolean);

	/** Indicates whether there are more results to return as part of this search */
	moreResultsToReturn(): boolean;
	/** Retrieves the cookie for this control, which may be used in a subsequent request to resume reading entries from the next page of results. The value should have a length of zero when used to retrieve the first page of results for a given search, and also in the response from the server when there are no more entries to send. It should be non-empty for all other conditions. */
	getCookieBytes(): any[];
	/**
	 * Extracts a simple paged results response control from the provided result.
	 * 
	 * @param param0 
	 */
	get(param0: LdapSearchResult): LdapSimplePagedResultsControl;
	/** Retrieves the size for this paged results control. For a request control, it may be used to specify the number of entries that should be included in the next page of results. For a response control, it may be used to specify the estimated number of entries in the complete result set. */
	getSize(): number;
	/** Retrieves the cookie for this control, which may be used in a subsequent request to resume reading entries from the next page of results. The value should have a length of zero when used to retrieve the first page of results for a given search, and also in the response from the server when there are no more entries to send. It should be non-empty for all other conditions. */
	getCookie(): any;
	/** Retrieves the user-friendly name for this control, if available. If no user-friendly name has been defined, then the OID will be returned. */
	getControlName(): string;
	/** Indicates whether this control should be considered critical. */
	isCritical(): boolean;
	/** @ScriptingFunction("Retrieves the user-friendly name for this control, if available. If no user-friendly name has been defined, then the OID will be returned.") */
	getOID(): string;
}

/**
 * TThis class provides an implementation of the subtree delete request control as defined in
 * draft-armijo-ldap-treedelete. This can be used to delete an entry and all subordinate entries in a
 * single operation."
 *
 */
declare interface LdapSubtreeDeleteRequestControl {
	/** Retrieves the user-friendly name for this control, if available. If no user-friendly name has been defined, then the OID will be returned. */
	getControlName(): string;
	/** Indicates whether this control should be considered critical. */
	isCritical(): boolean;
	/** @ScriptingFunction("Retrieves the user-friendly name for this control, if available. If no user-friendly name has been defined, then the OID will be returned.") */
	getOID(): string;
}

declare class ModificationTypeWrapper {
	constructor();
}

declare class byte {
	constructor();
}
