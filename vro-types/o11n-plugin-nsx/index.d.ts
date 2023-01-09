declare interface NSXAddressGroup {
	readonly primaryAddress: string;
	readonly subnetMask: string;
	readonly secondaryAddresses: NSXSecondaryAddresses;
	readonly id: string;
	readonly prefixLength: string;
	readonly displayName: string;
}

declare interface NSXAddressGroups {
	readonly addressGroups: NSXAddressGroup[];
}

declare interface NSXAppliance {
	readonly resourcePoolId: string;
	readonly datastoreId: string;
	readonly hostId: string;
}

declare interface NSXAppliances {
	readonly appliances: NSXAppliance[];
}

declare interface NSXApplicationProfile {
	readonly id: any;
	readonly persistence: NSXPersistence;
	readonly insertForwardedFor: boolean;
	readonly clientSSLPassThrough: boolean;
	readonly name: string;
	readonly type: string;
	readonly protocol: string;
	readonly port: string;
	readonly objectId: string;
}

declare interface NSXApplicationProfileInv {
	readonly id: any;
	readonly displayName: string;
}

declare interface NSXApplicationRule {
	readonly id: any;
	readonly applicationRuleId: string;
	readonly name: string;
	readonly script: string;
	readonly description: string;
	readonly objectId: string;
}

declare interface NSXApplicationRuleInv {
	readonly id: any;
	readonly displayName: string;
}

declare interface NSXBasicDomainEntity {
	readonly objectTypeName: string;
	readonly revision: string;
	readonly name: string;
	readonly description: string;
	readonly objectId: string;
}

declare interface NSXCluster {
	readonly id: any;
	readonly objectTypeName: string;
	readonly revision: string;
	readonly name: string;
	readonly description: string;
	readonly objectId: string;
	readonly scopeId: string;
}

/**
 * Represents the Endpoint scriptable object
 */
declare interface NSXConnection {
	readonly version: string;
	readonly role: string;
	readonly retryCount: number;
	readonly connTimeout: number;
	readonly relatedNsxManagers: string[];
	readonly name: string;
	readonly id: string;
	readonly displayName: string;
	readonly url: string;
	readonly username: string;
}

declare interface NSXDatacenterInv {
	readonly id: any;
	readonly displayName: string;
}

declare interface NSXDefaultRoute {
	readonly id: any;
	readonly gatewayIpAddress: string;
	readonly vNicIndex: string;
}

declare interface NSXDhcpPool {
	readonly id: any;
	readonly poolId: string;
	readonly autoConfigureDNS: boolean;
	readonly ipRange: string;
	readonly leaseTime: string;
	readonly defaultGateway: string;
	readonly dnsType: string;
	readonly domainName: string;
	readonly primaryNameServer: string;
	readonly secondaryNameServer: string;
}

declare interface NSXDhcpPools {
	readonly dhcpPools: NSXDhcpPool[];
}

declare interface NSXEdge {
	readonly id: any;
	readonly objectTypeName: string;
	readonly revision: string;
	readonly name: string;
	readonly description: string;
	readonly objectId: string;
	readonly lbAsJson: string;
	readonly nics: NSXNics;
	readonly edgeType: string;
	readonly datacenterMoid: string;
	readonly datacenterName: string;
	readonly apiVersion: string;
	readonly nicsAsJson: string;
	readonly vmId: string;
	readonly vmName: string;
	readonly vmHostName: string;
	readonly vcUniqueId: string;
	readonly tenantId: string;
	readonly state: string;
}

declare interface NSXEdgeInv {
	readonly id: any;
	readonly displayName: string;
}

declare interface NSXEdgePage {
	readonly id: any;
	readonly totalCount: number;
	readonly startIndex: number;
	readonly pageSize: number;
	readonly displayName: string;
}

declare interface NSXFirewallRule {
	readonly id: any;
	readonly ruleName: string;
	readonly destination: string[];
	readonly source: string[];
	readonly action: string;
}

declare interface NSXFirewallRules {
	readonly firewallRules: NSXFirewallRule[];
}

declare interface NSXHealthcheck {
	readonly id: any;
	readonly uri: string;
	readonly interval: string;
	readonly healthThreshold: string;
	readonly unhealthThreshold: string;
	readonly displayName: string;
	readonly timeout: string;
	readonly mode: string;
}

declare interface NSXIPSet {
	readonly id: any;
	readonly objectTypeName: string;
	readonly revision: string;
	readonly name: string;
	readonly description: string;
	readonly objectId: string;
	readonly value: string;
	readonly universal: boolean;
}

declare interface NSXIPSetInv {
	readonly id: any;
	readonly displayName: string;
}

declare interface NSXLoadBalancer {
	readonly id: any;
	readonly enableLogging: boolean;
	readonly enableServiceInsertion: boolean;
	readonly enableAcceleration: boolean;
	readonly logLevel: string;
	readonly virtualServer: NSXLoadBalancerVirtualServer;
	readonly pool: NSXLoadBalancerPool;
	readonly enabled: boolean;
}

declare interface NSXLoadBalancerInv {
	readonly id: any;
	readonly enableLogging: boolean;
	readonly enableServiceInsertion: boolean;
	readonly enableAcceleration: boolean;
	readonly logLevel: string;
	readonly displayName: string;
	readonly enabled: boolean;
}

declare interface NSXLoadBalancerOutputParameter {
	readonly id: any;
	readonly poolId: string;
	readonly virtualServerId: string;
	readonly protocol: string;
	readonly port: number;
}

declare interface NSXLoadBalancerOutputParameters {
	readonly loadBalancerOutputParameters: NSXLoadBalancerOutputParameter[];
}

declare interface NSXLoadBalancerPool {
	readonly id: any;
	readonly loadBalancerPoolMembers: NSXLoadBalancerPoolMembers;
	readonly loadBalancerPoolServicesProfiles: NSXLoadBalancerPoolServicesProfiles;
	readonly name: string;
}

declare interface NSXLoadBalancerPoolMember {
	readonly id: any;
	readonly ipAddress: string;
	readonly weight: number;
}

declare interface NSXLoadBalancerPoolMembers {
}

declare interface NSXLoadBalancerPoolServicesProfile {
	readonly id: any;
	readonly algorithm: string;
	readonly unHealthThreshold: number;
	readonly interval: number;
	readonly healthcheckPort: number;
	readonly protocol: string;
	readonly port: number;
	readonly threshold: number;
	readonly url: string;
	readonly timeout: number;
	readonly mode: string;
}

declare interface NSXLoadBalancerPoolServicesProfiles {
	readonly loadBalancerPoolServicesProfiles: NSXLoadBalancerPoolServicesProfile[];
}

declare interface NSXLoadBalancerVirtualServer {
	readonly id: any;
	readonly ipAddress: string;
	readonly vnicIndex: number;
	readonly name: string;
}

declare interface NSXMonitor {
	readonly id: any;
	readonly interval: string;
	readonly monitorId: string;
	readonly maxRetries: string;
	readonly send: string;
	readonly receive: string;
	readonly name: string;
	readonly method: string;
	readonly type: string;
	readonly extension: string;
	readonly description: string;
	readonly url: string;
	readonly objectId: string;
	readonly timeout: string;
	readonly expected: string;
}

declare interface NSXMonitorInv {
	readonly id: any;
	readonly displayName: string;
}

declare interface NSXNatRule {
	readonly originalAddress: string;
	readonly originalPort: string;
	readonly ruleId: string;
	readonly ruleTag: string;
	readonly ruleType: string;
	readonly translatedAddress: string;
	readonly translatedPort: string;
	readonly vnicIndex: string;
	readonly loggingEnabled: boolean;
	readonly icmpSubType: string;
	readonly dnatSourceAddress: string;
	readonly dnatSourcePort: string;
	readonly snatDestinationAddress: string;
	readonly snatDestinationPort: string;
	readonly protocol: string;
	readonly description: string;
	readonly enabled: boolean;
	readonly action: string;
}

declare interface NSXNic {
	readonly id: any;
	readonly index: string;
	readonly portgroupId: string;
	readonly name: string;
	readonly type: string;
	readonly connected: boolean;
}

declare interface NSXNics {
	readonly nics: NSXNic[];
}

declare interface NSXNicsInv {
	readonly id: any;
	readonly displayName: string;
}

declare interface NSXPersistence {
	readonly id: any;
	readonly cookieName: string;
	readonly cookieMode: string;
	readonly expiresIn: string;
	readonly method: string;
}

declare interface NSXPool {
	readonly id: any;
	readonly algorithm: string;
	readonly servicePorts: NSXServicePort[];
	readonly transparent: boolean;
	readonly listMonitors: NSXMonitor[];
	readonly poolId: string;
	readonly name: string;
	readonly members: NSXPoolMember[];
	readonly description: string;
	readonly algorithmParameters: string;
	readonly objectId: string;
}

declare interface NSXPoolInv {
	readonly id: any;
	readonly displayName: string;
}

declare interface NSXPoolMember {
	readonly id: any;
	readonly ipAddress: string;
	readonly weight: string;
	readonly servicePortList: NSXServicePort[];
	readonly groupingObjectId: string;
	readonly monitorPort: string;
	readonly maxConn: string;
	readonly minConn: string;
	readonly memberId: string;
	readonly name: string;
	readonly port: string;
	readonly displayName: string;
	readonly objectId: string;
}

declare interface NSXResource {
	readonly id: any;
	readonly objectTypeName: string;
	readonly revision: string;
	readonly name: string;
	readonly description: string;
	readonly objectId: string;
}

declare interface NSXSecondaryAddresses {
	readonly ipAddresses: string[];
}

declare interface NSXSecurityGroup {
	readonly id: any;
	readonly objectTypeName: string;
	readonly revision: string;
	readonly name: string;
	readonly description: string;
	readonly objectId: string;
	readonly scopeId: string;
}

declare interface NSXSecurityGroupInv {
	readonly id: any;
	readonly displayName: string;
}

declare interface NSXSecurityGroupPage {
	readonly id: any;
	readonly totalCount: number;
	readonly startIndex: number;
	readonly pageSize: number;
	readonly displayName: string;
}

declare interface NSXSecurityPolicy {
	readonly id: any;
	readonly objectTypeName: string;
	readonly revision: string;
	readonly name: string;
	readonly objectId: string;
	readonly description: string;
}

declare interface NSXSecurityPolicyInv {
	readonly id: any;
	readonly displayName: string;
}

declare interface NSXSecurityTag {
	readonly id: any;
	readonly objectTypeName: string;
	readonly revision: string;
	readonly name: string;
	readonly objectId: string;
	readonly isUniversal: boolean;
	readonly description: string;
}

declare interface NSXSecurityTagsInv {
	readonly id: any;
	readonly displayName: string;
}

declare interface NSXServicePort {
	readonly id: any;
	readonly algorithm: string;
	readonly healthchecks: NSXHealthcheck[];
	readonly healthcheckPort: string;
	readonly protocol: string;
	readonly port: string;
	readonly displayName: string;
}

declare interface NSXStaticRoute {
	readonly id: any;
	readonly mtu: number;
	readonly vNicIndex: string;
	readonly network: string;
	readonly nextHop: string;
}

declare interface NSXStaticRoutes {
	readonly staticRoutes: NSXStaticRoute[];
}

declare interface NSXVCNSDomainObject {
	readonly id: any;
	readonly objectTypeName: string;
	readonly revision: string;
	readonly name: string;
	readonly description: string;
	readonly objectId: string;
}

declare interface NSXVdnScope {
	readonly id: any;
	readonly objectTypeName: string;
	readonly revision: string;
	readonly name: string;
	readonly description: string;
	readonly objectId: string;
	readonly clustersAsJson: string;
}

declare interface NSXVdnScopeInv {
	readonly id: any;
	readonly displayName: string;
}

declare interface NSXVirtualServer {
	readonly id: any;
	readonly enableServiceInsertion: boolean;
	readonly poolId: string;
	readonly ipAddress: string;
	readonly virtualServerId: string;
	readonly serviceProfileList: NSXApplicationProfile[];
	readonly applicationRules: NSXApplicationRule[];
	readonly accelerationEnabled: boolean;
	readonly connectionLimit: string;
	readonly connectionRateLimit: string;
	readonly applicationProfile: NSXApplicationProfile;
	readonly defaultPool: NSXPool;
	readonly name: string;
	readonly protocol: string;
	readonly port: string;
	readonly description: string;
	readonly enabled: boolean;
	readonly objectId: string;
}

declare interface NSXVirtualServerInv {
	readonly id: any;
	readonly displayName: string;
}

declare interface NSXVirtualWire {
	readonly id: any;
	readonly objectTypeName: string;
	readonly revision: string;
	readonly name: string;
	readonly description: string;
	readonly objectId: string;
	readonly vdnScopeId: string;
	readonly portGroupBackingValues: string[];
	readonly tenantId: string;
}

declare interface NSXVirtualWireCreateSpec {
	readonly tenantId: string;
	readonly name: string;
	readonly description: string;
}

declare interface NSXVirtualWireInv {
	readonly id: any;
	readonly displayName: string;
}

declare interface NSXVirtualWirePage {
	readonly id: any;
	readonly totalCount: number;
	readonly startIndex: number;
	readonly pageSize: number;
	readonly displayName: string;
}

/**
 * This class exposes scriptable APIs for create, update, delete operations on individual entities of NSX Edge Load Balancer
 */
declare class NSXAdvancedEdgeLBManager {
	/**
	 * Get the load balancer application profile object
	 * @param connection 
	 * @param edge 
	 * @param appProfile 
	 */
	static getLBApplicationProfile(connection: NSXConnection, edge: NSXEdge, appProfile: NSXApplicationProfile): NSXApplicationProfile;
	/**
	 * Method to get load balancer application profile from edge ID and application profile ID
	 * @param connection 
	 * @param edgeId 
	 * @param appProfileId 
	 */
	static getLBApplicationProfileFromEdgeIdAppProfileId(connection: NSXConnection, edgeId: string, appProfileId: string): NSXApplicationProfile;
	/**
	 * Method to get all the load balancer application profiles for an edge
	 * @param connection 
	 * @param edgeId 
	 */
	static getLBApplicationProfiles(connection: NSXConnection, edgeId: string): NSXApplicationProfile[];
	/**
	 * Method to get all load balancer application rules for an edge
	 * @param connection 
	 * @param edgeId 
	 */
	static getLBApplicationRules(connection: NSXConnection, edgeId: string): NSXApplicationRule[];
	/**
	 * Method to get all the load balancer monitors of an edge
	 * @param connection 
	 * @param edgeId 
	 */
	static getLBMonitors(connection: NSXConnection, edgeId: string): NSXMonitor[];
	/**
	 * Method to create a load balancer application profile
	 * @param connection 
	 * @param edge 
	 * @param appProfile 
	 */
	static createLBApplicationProfile(connection: NSXConnection, edge: NSXEdge, appProfile: NSXApplicationProfile): NSXApplicationProfile;
	/**
	 * Method to modify load balancer application profile
	 * @param connection 
	 * @param edge 
	 * @param appProfile 
	 */
	static modifyLBApplicationProfile(connection: NSXConnection, edge: NSXEdge, appProfile: NSXApplicationProfile): NSXApplicationProfile;
	/**
	 * Method to delete a load balancer application profile
	 * @param connection 
	 * @param edge 
	 * @param appProfile 
	 */
	static deleteLBApplicationProfile(connection: NSXConnection, edge: NSXEdge, appProfile: NSXApplicationProfile): void;
	/**
	 * Method to get a load balancer monitor object
	 * @param connection 
	 * @param edge 
	 * @param monitor 
	 */
	static getLBMonitor(connection: NSXConnection, edge: NSXEdge, monitor: NSXMonitor): NSXMonitor;
	/**
	 * Method to get load balancer monitor object from edge ID and monitor ID
	 * @param connection 
	 * @param edgeId 
	 * @param monitorId 
	 */
	static getLBMonitorFromEdgeIdMonitorId(connection: NSXConnection, edgeId: string, monitorId: string): NSXMonitor;
	/**
	 * Method to create a load balancer service monitor
	 * @param connection 
	 * @param edge 
	 * @param monitor 
	 */
	static createLBMonitor(connection: NSXConnection, edge: NSXEdge, monitor: NSXMonitor): NSXMonitor;
	/**
	 * Method to modify an existing load balancer service monitor
	 * @param connection 
	 * @param edge 
	 * @param monitor 
	 */
	static modifyLBMonitor(connection: NSXConnection, edge: NSXEdge, monitor: NSXMonitor): NSXMonitor;
	/**
	 * Method to delete a load balancer monitor
	 * @param connection 
	 * @param edge 
	 * @param monitor 
	 */
	static deleteLBMonitor(connection: NSXConnection, edge: NSXEdge, monitor: NSXMonitor): void;
	/**
	 * Method to create a load balancer application rule
	 * @param connection 
	 * @param edge 
	 * @param appRule 
	 */
	static createLBAppRule(connection: NSXConnection, edge: NSXEdge, appRule: NSXApplicationRule): NSXApplicationRule;
	/**
	 * Method to modify a load balancer application rule
	 * @param connection 
	 * @param edge 
	 * @param appRule 
	 */
	static modifyLBAppRule(connection: NSXConnection, edge: NSXEdge, appRule: NSXApplicationRule): NSXApplicationRule;
	/**
	 * Method to delete a load balancer application rule
	 * @param connection 
	 * @param edge 
	 * @param appRule 
	 */
	static deleteLBApplicationRule(connection: NSXConnection, edge: NSXEdge, appRule: NSXApplicationRule): void;
	/**
	 * Method to get a load balancer application rule
	 * @param connection 
	 * @param edge 
	 * @param rule 
	 */
	static getLBApplicationRule(connection: NSXConnection, edge: NSXEdge, rule: NSXApplicationRule): NSXApplicationRule;
	/**
	 * Method to get a load balancer application rule from edge ID and application rule ID
	 * @param connection 
	 * @param edgeId 
	 * @param ruleId 
	 */
	static getLBApplicationRuleFromEdgeIdAppRuleId(connection: NSXConnection, edgeId: string, ruleId: string): NSXApplicationRule;
	/**
	 * Method to get load balancer Pool
	 * @param connection 
	 * @param edge 
	 * @param pool 
	 */
	static getLBPool(connection: NSXConnection, edge: NSXEdge, pool: NSXPool): NSXPool;
	/**
	 * Method to get load balancer pool from edge ID and pool ID
	 * @param connection 
	 * @param edgeId 
	 * @param poolId 
	 */
	static getLBPoolFromEdgeIdPoolId(connection: NSXConnection, edgeId: string, poolId: string): NSXPool;
	/**
	 * Method to create a Load balancer pool
	 * @param connection 
	 * @param edge 
	 * @param pool 
	 */
	static createLBPool(connection: NSXConnection, edge: NSXEdge, pool: NSXPool): NSXPool;
	/**
	 * Method to modify an existing load balancer pool
	 * @param connection 
	 * @param edge 
	 * @param pool 
	 */
	static updateLBPool(connection: NSXConnection, edge: NSXEdge, pool: NSXPool): NSXPool;
	/**
	 * Method to delete an existing load balancer pool
	 * @param connection 
	 * @param edge 
	 * @param pool 
	 */
	static deleteLBPool(connection: NSXConnection, edge: NSXEdge, pool: NSXPool): void;
	/**
	 * Method to get a load balancer virtual server
	 * @param connection 
	 * @param edge 
	 * @param virtualServer 
	 */
	static getLoadBalancerVirtualServer(connection: NSXConnection, edge: NSXEdge, virtualServer: NSXVirtualServer): NSXVirtualServer;
	/**
	 * Method to get a load balancer virtual server from edge ID and virtual server ID
	 * @param connection 
	 * @param edgeId 
	 * @param virtualServerId 
	 */
	static getLBVirtualServerFromEdgeIdVirtualServerId(connection: NSXConnection, edgeId: string, virtualServerId: string): NSXVirtualServer;
	/**
	 * Method to create a load balancer virtual server
	 * @param connection 
	 * @param edge 
	 * @param virtualServer 
	 */
	static createLoadBalancerVirtualServer(connection: NSXConnection, edge: NSXEdge, virtualServer: NSXVirtualServer): NSXVirtualServer;
	/**
	 * Method to modify a load balancer virtual server
	 * @param connection 
	 * @param edge 
	 * @param virtualServer 
	 */
	static updateLoadBalancerVirtualServer(connection: NSXConnection, edge: NSXEdge, virtualServer: NSXVirtualServer): NSXVirtualServer;
	/**
	 * Method to delete a load balancer virtual server
	 * @param connection 
	 * @param edge 
	 * @param virtualServer 
	 */
	static deleteLoadBalancerVirtualServer(connection: NSXConnection, edge: NSXEdge, virtualServer: NSXVirtualServer): void;
	/**
	 * Method to configure the global settings of an NSX Edge load balancer
	 * @param connection 
	 * @param edge 
	 * @param loadBalancer 
	 */
	static configureGlobalSettingsOfLoadBalancer(connection: NSXConnection, edge: NSXEdge, loadBalancer: NSXLoadBalancer): void;
}

/**
 * Exposes operations that can be performed on NSX Connections
 */
declare class NSXConnectionManager {
	/**
	 * Synchronises the NSX Endpoint properties. After an endpoint is created, certain properties of NSX may change. E.g. version, role etc. This method refreshes these properties by querying NSX and updating the properties. The properties that are synced are version, role and related endpoints.
	 * @param connection 
	 */
	static syncNsxConnection(connection: NSXConnection): NSXConnection;
	/**
	 * Deletes all endpoints
	 */
	static clearAllConnections(): void;
	/**
	 * Creates a new NSX Endpoint
	 * @param name 
	 * @param username 
	 * @param password 
	 * @param url 
	 * @param retryCount 
	 * @param connTimeout 
	 */
	static createNSXConnnection(name: string, username: string, password: string, url: string, retryCount: number, connTimeout: number): NSXConnection;
	/**
	 * Finds a connection from the Connection Repository, given its id
	 * @param id 
	 */
	static findConnectionById(id: string): NSXConnection;
	/**
	 * Finds a connection from the Connection Repository, given its name
	 * @param name 
	 */
	static findConnectionByName(name: string): NSXConnection;
}

declare class NSXConverterUtil {
	/**
	 * @param obj 
	 * @param format 
	 */
	static serialize(obj: any, format: string): string;
}

/**
 * Exposes scriptable Apis that can be performed on Datacenter registered with NSX
 */
declare class NSXDatacenterManager {
	/**
	 * Method to retrieve list of all datacenters registered with specified nsx
	 * @param connection 
	 */
	static getDatacenters(connection: NSXConnection): NSXResource[];
}

/**
 * This class exposes scriptable APIs for managing and configuring services on an NSX edge.
 */
declare class NSXEdgeManager {
	/**
	 * This method returns the edge version.
	 * @param connection 
	 * @param edgeId 
	 */
	static getVersionofEdge(connection: NSXConnection, edgeId: string): string;
	/**
	 * This method returns the edge vNIC configuration for the specified NIC index.
	 * @param connection 
	 * @param edgeId 
	 * @param nicIndex 
	 */
	static getEdgeVnic(connection: NSXConnection, edgeId: string, nicIndex: string): NSXNic;
	/**
	 * This method disconnects a given edge interface.
	 * @param connection 
	 * @param edgeId 
	 * @param index 
	 */
	static disconnectEdgeInterface(connection: NSXConnection, edgeId: string, index: string): void;
	/**
	 * This method deletes an NSX edge.
	 * @param connection 
	 * @param edgeId 
	 */
	static deleteEdge(connection: NSXConnection, edgeId: string): void;
	/**
	 * This method creates an NSX edge.
	 * @param connection 
	 * @param edge 
	 */
	static createEdge(connection: NSXConnection, edge: NSXEdge): NSXEdge;
	/**
	 * This method adds secondary IP addresses to an edge vNIC.
	 * @param connection 
	 * @param edgeId 
	 * @param vNicIndex 
	 * @param ipAddresses 
	 */
	static addSecondaryIpsToVnic(connection: NSXConnection, edgeId: string, vNicIndex: number, ipAddresses: string[]): void;
	/**
	 * This method removes secondary IP addresses from an edge vNIC.
	 * @param connection 
	 * @param edgeId 
	 * @param vNicIndex 
	 * @param ipAddresses 
	 */
	static removeSecondaryIpsFromVnic(connection: NSXConnection, edgeId: string, vNicIndex: number, ipAddresses: string[]): void;
	/**
	 * This method adds a DHCP pool to an NSX edge
	 * @param connection 
	 * @param edgeId 
	 * @param dhcpPools 
	 */
	static addDhcpIpPool(connection: NSXConnection, edgeId: string, dhcpPools: NSXDhcpPools): void;
	/**
	 * This method adds firewall rules between egde vNICs. In this configuration, the source and/or destination of the firewall rule is typically a vNIC index.
	 * @param connection 
	 * @param edgeId 
	 * @param firewallRules 
	 */
	static addFwRulesBetweenInterfaces(connection: NSXConnection, edgeId: string, firewallRules: NSXFirewallRules): void;
	/**
	 * This method returns an NSX edge.
	 * @param connection 
	 * @param edgeId 
	 */
	static getEdge(connection: NSXConnection, edgeId: string): NSXEdge;
	/**
	 * This method returns the list of edge vNIC configuration.
	 * @param connection 
	 * @param edgeId 
	 */
	static getEdgeVNics(connection: NSXConnection, edgeId: string): NSXNics;
	/**
	 * This method connects an NSX edge interface to a logical switch or a portgroup.
	 * @param connection 
	 * @param edgeId 
	 * @param intf 
	 */
	static connectEdgeInterface(connection: NSXConnection, edgeId: string, intf: NSXNic): NSXNic;
	/**
	 * This method sets the default route on NSX edge
	 * @param connection 
	 * @param edgeId 
	 * @param defaultRoute 
	 */
	static setDefaultRoute(connection: NSXConnection, edgeId: string, defaultRoute: NSXDefaultRoute): void;
	/**
	 * This method adds static routes on the NSX edge
	 * @param connection 
	 * @param edgeId 
	 * @param staticRoutes 
	 */
	static addStaticRoutes(connection: NSXConnection, edgeId: string, staticRoutes: NSXStaticRoutes): void;
	/**
	 * This method removes the static routes from the NSX edge.
	 * @param connection 
	 * @param edgeId 
	 * @param staticRoutes 
	 */
	static deleteStaticRoutes(connection: NSXConnection, edgeId: string, staticRoutes: NSXStaticRoutes): void;
}

/**
 * This class exposes scriptable APIs for configuring IP sets in NSX
 */
declare class NSXIPSetManager {
	/**
	 * This method creates an IP Set in NSX. It can be used to create both, universal and non-universal IP Sets.
	 * @param connection 
	 * @param ipSet 
	 */
	static createIpSet(connection: NSXConnection, ipSet: NSXIPSet): NSXIPSet;
	/**
	 * This method retrieves an IP Set from its identifier
	 * @param connection 
	 * @param ipSetId 
	 */
	static getIpSet(connection: NSXConnection, ipSetId: string): NSXIPSet;
	/**
	 * This method deletes a given IP Set from NSX
	 * @param connection 
	 * @param ipSet 
	 * @param force 
	 */
	static deleteIpSet(connection: NSXConnection, ipSet: NSXIPSet, force: boolean): void;
	/**
	 * This method updates a given IP Set
	 * @param connection 
	 * @param existingIpSet 
	 * @param modifiedIpSet 
	 */
	static updateIpSet(connection: NSXConnection, existingIpSet: NSXIPSet, modifiedIpSet: NSXIPSet): NSXIPSet;
}

/**
 * Exposes scriptable APIs for configuring NAT service on an NSX edge
 */
declare class NSXNatManager {
	/**
	 * This method configures NAT service on an NSX edge. It can be used to add new NAT rules to existing NAT configuration or to modify or delete existing NAT rules from the configuration. For new rules, ruleId field is not specified. For rules to be deleted, set value of action field to 'delete'.
	 * @param connection 
	 * @param edge 
	 * @param natRules 
	 */
	static applyNatConfiguration(connection: NSXConnection, edge: NSXEdge, natRules: NSXNatRule[]): NSXNatRule[];
	/**
	 * This method retrieves the NAT rules configured on the NSX edge.
	 * @param connection 
	 * @param edge 
	 */
	static getNatRules(connection: NSXConnection, edge: NSXEdge): NSXNatRule[];
	/**
	 * This method deletes specified NAT rules from the NSX edge. Rule ids of the NAT rules to be deleted are provided as input parameters.
	 * @param connection 
	 * @param edgeId 
	 * @param ruleIds 
	 */
	static deleteNatRules(connection: NSXConnection, edgeId: string, ruleIds: number[]): void;
	/**
	 * This method adds NAT rules to a given NSX edge.
	 * @param connection 
	 * @param edgeId 
	 * @param natRules 
	 */
	static addNatRules(connection: NSXConnection, edgeId: string, natRules: NSXNatRule[]): NSXNatRule[];
}

/**
 * This class exposes scriptable APIs for managing NSX security groups.
 */
declare class NSXSecurityGroupsManager {
	/**
	 * This method retrieves a list of global security groups from a given starting index.
	 * @param connection 
	 * @param startIndex 
	 */
	static getGlobalSecurityGroups(connection: NSXConnection, startIndex: number): NSXSecurityGroup[];
	/**
	 * This method creates a security group and adds VMs and IP addresses as members of the group
	 * @param connection 
	 * @param vmMorefs 
	 * @param ips 
	 * @param securitygroup 
	 */
	static createSecurityGroup(connection: NSXConnection, vmMorefs: string[], ips: string[], securitygroup: NSXSecurityGroup): NSXSecurityGroup;
	/**
	 * This method updates the security group membership. It adds the specified VMs and IP addresses to the group.
	 * @param connection 
	 * @param securityGroupId 
	 * @param vmMorefs 
	 * @param ips 
	 */
	static updateSecurityGroup(connection: NSXConnection, securityGroupId: string, vmMorefs: string[], ips: string[]): void;
	/**
	 * This method updates the security group membership. It adds the specified VMs and IP sets to multiple security groups.
	 * @param connection 
	 * @param securityGroups 
	 * @param vmMorefs 
	 * @param ipSets 
	 */
	static updateSecurityGroups(connection: NSXConnection, securityGroups: NSXSecurityGroup[], vmMorefs: string[], ipSets: NSXIPSet[]): void;
	/**
	 * This method updates the security group membership. It removes the specified VMs and IP sets from multiple security groups.
	 * @param connection 
	 * @param securityGroups 
	 * @param vmMorefs 
	 * @param ipSets 
	 */
	static removeMembersFromSecurityGroups(connection: NSXConnection, securityGroups: NSXSecurityGroup[], vmMorefs: string[], ipSets: NSXIPSet[]): void;
	/**
	 * This method retrieves the list of members of the given security group
	 * @param connection 
	 * @param group 
	 */
	static getSecurityGroupMembers(connection: NSXConnection, group: NSXSecurityGroup): NSXBasicDomainEntity[];
	/**
	 * This method retrieves a list of security groups based on the scope (e.g. globalroot-0)
	 * @param connection 
	 * @param scopeId 
	 */
	static getSecurityGroupsByScope(connection: NSXConnection, scopeId: string): NSXSecurityGroup[];
}

/**
 * This class represents a set of scriptable APIs to manage and configure security policies and also for creation of app isolation policy
 */
declare class NSXSecurityPolicyManager {
	/**
	 * Method to create app isolation security policy based on NSX endpoint ID
	 * @param connection 
	 * @param endPointId 
	 */
	static createAppIsolationPolicy(connection: NSXConnection, endPointId: string): NSXSecurityPolicy;
	/**
	 * Method to enable support for overlapping subnets for an NSX endpoint
	 * @param connection 
	 */
	static enableSupportForOverlappingSubnets(connection: NSXConnection): void;
	/**
	 * Method for applying an individual security policy on an individual security group
	 * @param connection 
	 * @param securityGroupId 
	 * @param policyId 
	 */
	static applyPolicyOnSecurityGroup(connection: NSXConnection, securityGroupId: string, policyId: string): void;
	/**
	 * Method for applying multiple security policies on an individual security group
	 * @param connection 
	 * @param securityGroupId 
	 * @param policyIds 
	 */
	static applyPoliciesOnSecurityGroup(connection: NSXConnection, securityGroupId: string, policyIds: string[]): void;
}

/**
 * Exposes Security tag operations that can be performed on VMs
 */
declare class NSXSecurityTagManager {
	/**
	 * Method to apply the specified Security tags on VM
	 * @param connection 
	 * @param vmMoref 
	 * @param tagIds 
	 */
	static applySecurityTagOnVMs(connection: NSXConnection, vmMoref: string, tagIds: string[]): void;
	/**
	 * Method to remove the Security tags on VM
	 * @param connection 
	 * @param securityTags 
	 * @param vmMoref 
	 */
	static detachSecurityTagsOnVm(connection: NSXConnection, securityTags: NSXSecurityTag[], vmMoref: string): void;
	/**
	 * Method to retrieve the list of VMs tagged with the specified security tag
	 * @param connection 
	 * @param tag 
	 */
	static getTaggedVms(connection: NSXConnection, tag: NSXSecurityTag): NSXBasicDomainEntity[];
	/**
	 * Method to retrieve the list of Security tags in the specified NSX
	 * @param connection 
	 */
	static getSecurityTags(connection: NSXConnection): NSXSecurityTag[];
	/**
	 * Method to retrieve the Security tag with specified tag id
	 * @param connection 
	 * @param tagId 
	 */
	static getSecurityTag(connection: NSXConnection, tagId: string): NSXSecurityTag;
}

/**
 * This class exposes the scriptable APIs for accessing vdn scopes or transport zones
 */
declare class NSXVdnScopeManager {
	/**
	 * Method to get a specific vdn scope or transport zone based on the scope ID
	 * @param connection 
	 * @param scopeId 
	 */
	static getVdnScope(connection: NSXConnection, scopeId: string): NSXVdnScope;
	/**
	 * Method to get all vdn scopes or transport zones. Includes both local and universal transport zones
	 * @param connection 
	 */
	static getVdnScopes(connection: NSXConnection): NSXVdnScope[];
}

/**
 * This class exposes scriptable APIs to manage and configure virtual wires or logical switches
 */
declare class NSXVirtualWireManager {
	/**
	 * Method to create a virtual wire or logical switch on a given transport zone ID
	 * @param connection 
	 * @param scopeId 
	 * @param spec 
	 */
	static createVirtualWire(connection: NSXConnection, scopeId: string, spec: NSXVirtualWireCreateSpec): NSXVirtualWire;
	/**
	 * Method to get a Logical switch or Virtual wire from Logical switch or Virtual wire ID
	 * @param connection 
	 * @param virtualWireId 
	 */
	static getVirtualWire(connection: NSXConnection, virtualWireId: string): NSXVirtualWire;
	/**
	 * Method to delete a Logical switch or Virtual wire given the Logical switch or Virtual wire ID
	 * @param connection 
	 * @param virtualWireId 
	 */
	static deleteVirtualWire(connection: NSXConnection, virtualWireId: string): void;
}
