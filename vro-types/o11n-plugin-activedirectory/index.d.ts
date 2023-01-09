/**
 * Create new non persisted ldap connections.
 */
declare class LdapClientFactory {
}

/**
 * Connection to ldap server.
 */
declare interface LdapClient {
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
 * Represents the Active Directory base.
 */
declare class ActiveDirectory {
	/**
	 * Search the active directory for a certain type of object
	 * @param type
	 * @param query
	 * @param adServer
	 */
	static search(type: string, query: string, adServer: AD_Host): any[];
	/**
	 * Search the active directory for a certain type of object. Object will be returned only
	 * if the there is
	 * exact match.
	 *
	 * @param type
	 * @param objectName
	 * @param limit
	 * @param adServer
	 */
	static searchExactMatch(type: string, objectName: string, limit: number, adServer: AD_Host): any[];
	/**
	 * Returns a computer when his exact name is given. If no computer found, returns null
	 *
	 * @param computerName
	 * @param adServer
	 */
	static getComputerAD(computerName: string, adServer: AD_Host): AD_Computer;
	/**
	 * Return the AD_Computer representing the Domain Conroller
	 * @param adServer
	 */
	static getDC(adServer: AD_Host): AD_Computer;
	 /**
	 * Move Ad Object to a diferent OU or rename
	 * @param from // Objcect Distinguish Name
	 * @param to   // Object new Name
	 * @param new_parent // Object New Ou Parent
	 * @param adServer // AdServer
	 */
	static rename(from:string, to:string, new_parent:string, adServer: AD_Host): void;

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

	static searchForEntry(baseDN: string, searchScope: LdapSearchScope, timeLimit: number, filter: string, attributes: string, host: AD_Host): any;
}

/**
 * Represents Active directory server connection.
 */
declare interface AD_Host {
	readonly name: string;
	readonly Url: string;
	readonly hostConfiguration: AD_ServerConfiguration;
}

/**
 * Active Directory Plug-in options
 */
declare interface AD_PluginOptions {
	readonly defaultAdServerId: string;
	readonly searchSizeLimit: number;
	readonly searchSizeLimitPerServer: number;
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
 * Computer on the AD
 */
declare interface AD_Computer {
	id: string;
	readonly name: string;
	readonly hostname: string;
  	readonly distinguishedName: string;
}

/**
 * Group
 */
declare interface AD_Group {
	id: string;
}

/**
 * Represents an Organizational Unit
 */
declare interface AD_OrganizationalUnit {
	id: string;
  	distinguishedName: string;
	/**
	 * Get a computer by name
	 * @param computerName
	 */
	searchComputer(computerName: string): AD_Computer;
}

/**
 * Unknown type of object
 */
declare interface AD_Unknown {
	id: string;
}

/**
 * User
 */
declare interface AD_User {
	id: string;
}

/**
 * UserGroup
 */
declare interface AD_UserGroup {
	id: string;
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
