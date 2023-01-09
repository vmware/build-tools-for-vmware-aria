/**
 * 
 * This class represent the APIC response/return status and
 * associated error code with description.
 * For methods that are successful the APIC object that got created/modified is returned
 * in the object field.
 * 
 */
declare interface ApicResponse {
	result: string;
	errorCode: string;
	errorDesc: string;
	object: string;
}

/**
 * 
 * 
 * 
 */
declare interface ApicDomain {
	name: string;
	userName: string;
	apic: string;
	noSsl: boolean;
	virtualIpv4Address: string;
	port: number;
}

/**
 * 
 * This class provides various utility methods for APIC workflows.
 * 
 */
declare class ApicConfigHelper {
	/**
	 * 
	 * This method validate a ip address of the form a.b.c.d (a, b, c, d being digits)
	 * 
	 * @param ipAddress 
	 */
	validateIPAddress(ipAddress: string): boolean;
	/**
	 * 
	 * This methods adds an APIC host to the repository and does a login to the APIC
	 * 
	 * @param hostName 
	 * @param hostIp0 
	 * @param hostIp1 
	 * @param hostIp2 
	 * @param userName 
	 * @param pwd 
	 * @param port 
	 * @param noSsl 
	 * @param role 
	 * @param tenantName 
	 * @param findKeystore 
	 * @param authentication 
	 * @param certName 
	 * @param privKey 
	 * @param displayAllTenants 
	 * @param clientCert 
	 */
	addHost(hostName: string, hostIp0: string, hostIp1: string, hostIp2: string, userName: string, pwd: string, port: number, noSsl: boolean, role: string, tenantName: string, findKeystore: boolean, authentication: string, certName: string, privKey: string, displayAllTenants: boolean, clientCert: string): ApicResponse;
	/**
	 * 
	 * This methods gets the APIC handle give the APIC name
	 * 
	 * @param hostName 
	 */
	getApicHandle(hostName: string): ApicDomain;
	/**
	 * 
	 * This methods gets List of APIC handles for a given role, username
	 * 
	 * @param role 
	 * @param userName 
	 */
	getApicHandleByRole(role: string, userName: string): any;
	/**
	 * 
	 * This methods removes an APIC host from the repository
	 * 
	 * @param inApicName 
	 */
	removeHost(inApicName: string): boolean;
	/**
	 * 
	 * Add or remove useg attributes
	 * 
	 * @param handle 
	 * @param tenantName 
	 * @param apName 
	 * @param epgName 
	 * @param criteriaName 
	 * @param addFvVmAttrList 
	 * @param addFvMacAttrList 
	 * @param addFvIpAttrList 
	 * @param delFvVmAttrList 
	 * @param delFvMacAttrList 
	 * @param delFvIpAttrList
	 */
	addOrDelUsegAttr(handle: ApicDomain, tenantName: string, apName: string, epgName: string, criteriaName: string, addFvVmAttrList: VmCriteria[], addFvMacAttrList: MacCriteria[], addFvIpAttrList: IpCriteria[], delFvVmAttrList: any[], delFvMacAttrList: MacCriteria[], delFvIpAttrList: IpCriteria[]): ApicResponse;
	/**
	 * 
	 * This method creates a useg Tenant EPG and association to vmmDomain in APIC
	 * 
	 * @param handle 
	 * @param tenantName 
	 * @param apName 
	 * @param epgName 
	 * @param bdName 
	 * @param ctxName 
	 * @param subnet 
	 * @param domName 
	 * @param criteriaName 
	 * @param vmm 
	 * @param vpc 
	 * @param intraEpgDeny 
	 * @param fvVmAttrList 
	 * @param fvMacAttrList
	 * @param fvIpAttrList
	 * @param encapMode 
	 * @param switchingMode 
	 */
	addUsegEpg(handle: ApicDomain, tenantName: string, apName: string, epgName: string, bdName: string, ctxName: string, subnet: string, domName: string, criteriaName: string, vmm: boolean, vpc: boolean, intraEpgDeny: boolean, fvVmAttrList: VmCriteria[], fvMacAttrList: MacCriteria[], fvIpAttrList: IpCriteria[], encapMode: string, switchingMode: string): ApicResponse;
	/**
	 * 
	 * This method creates Tenant EPG and association to vmmDomain in APIC
	 * 
	 * @param handle 
	 * @param tenantName 
	 * @param apName 
	 * @param epgName 
	 * @param bdName 
	 * @param ctxName 
	 * @param subnet 
	 * @param domName 
	 * @param vmm 
	 * @param vpc 
	 * @param intraEpgDeny 
	 * @param allowUseg 
	 * @param encapMode 
	 * @param switchingMode 
	 */
	addNetwork(handle: ApicDomain, tenantName: string, apName: string, epgName: string, bdName: string, ctxName: string, subnet: string, domName: string, vmm: boolean, vpc: boolean, intraEpgDeny: boolean, allowUseg: boolean, encapMode: string, switchingMode: string): ApicResponse;
	/**
	 * 
	 * This method updates the Domain of the EPG by adding or deleting the Domain of the EPG
	 * 
	 * @param handle 
	 * @param tenantName 
	 * @param apName 
	 * @param epgName 
	 * @param domName 
	 * @param vmm 
	 * @param add 
	 * @param encapMode 
	 * @param switchingMode 
	 */
	updateNetwork(handle: ApicDomain, tenantName: string, apName: string, epgName: string, domName: string, vmm: boolean, add: boolean, encapMode: string, switchingMode: string): ApicResponse;
	/**
	 * 
	 * This method adds/deletes subnets to BD in VPC tenant
	 * 
	 * @param handle 
	 * @param tenantName 
	 * @param bdName 
	 * @param privateFlag 
	 * @param publicFlag 
	 * @param sharedFlag 
	 * @param subnetList
	 * @param add 
	 */
	updateSubnets(handle: ApicDomain, tenantName: string, bdName: string, privateFlag: boolean, publicFlag: boolean, sharedFlag: boolean, subnetList: ApicSubnet[], add: boolean): ApicResponse;
	/**
	 * 
	 * This method adds/delete BD to/from tenant
	 * 
	 * @param handle 
	 * @param tenantName 
	 * @param bdName 
	 * @param ctxName 
	 * @param arpFlooding 
	 * @param l2UnknownUnicast 
	 * @param l3UnknownMulticast 
	 * @param add 
	 */
	updateBD(handle: ApicDomain, tenantName: string, bdName: string, ctxName: string, arpFlooding: boolean, l2UnknownUnicast: string, l3UnknownMulticast: string, add: boolean): ApicResponse;
	/**
	 * 
	 * This method is used to validate build profile and filter entries
	 * 
	 * @param etype 
	 * @param proto 
	 * @param portStart 
	 * @param portEnd 
	 */
	validateFilterEntry(etype: string, proto: string, portStart: string, portEnd: string): ApicResponse;
	/**
	 * 
	 * This method validates the start and end port numbers
	 * 
	 * @param startPort 
	 * @param endPort 
	 */
	validatePort(startPort: string, endPort: string): boolean;
	/**
	 * 
	 * This method validates the port number
	 * 
	 * @param port 
	 */
	validatePort(port: string): boolean;
	/**
	 * 
	 * This method adds/delete Ctx to/from tenant
	 * 
	 * @param handle 
	 * @param tenantName 
	 * @param ctxName 
	 * @param add 
	 */
	updateCtx(handle: ApicDomain, tenantName: string, ctxName: string, add: boolean): ApicResponse;
	/**
	 * 
	 * Parse the EPG dn and get the application-profile name
	 * 
	 * @param epgDn 
	 */
	getTenantAp(epgDn: string): string;
	/**
	 * 
	 * This method adds/deletes the following based on add/delete operation
	 * 
	 * @param handle 
	 * @param tenantName 
	 * @param apName 
	 * @param epgName 
	 * @param bdName 
	 * @param ctxName 
	 * @param vpc 
	 * @param planName 
	 * @param vipPoolName 
	 * @param lbVendor 
	 * @param ldevName 
	 * @param graphName 
	 * @param sharedLb 
	 * @param protocol 
	 * @param port 
	 * @param consumerDn 
	 * @param snipIntAddress 
	 * @param snipIntNetMask 
	 * @param snipExtAddress 
	 * @param snipExtNetMask 
	 * @param snipNextHopGW 
	 * @param addOperation 
	 */
	addOrDeleteLBToNetwork(handle: ApicDomain, tenantName: string, apName: string, epgName: string, bdName: string, ctxName: string, vpc: boolean, planName: string, vipPoolName: string, lbVendor: string, ldevName: string, graphName: string, sharedLb: boolean, protocol: string, port: string, consumerDn: string, snipIntAddress: string, snipIntNetMask: string, snipExtAddress: string, snipExtNetMask: string, snipNextHopGW: string, addOperation: boolean): ApicResponse;
	/**
	 * 
	 * This method opens a connection to url, sends the postBody string to url location, and returns result.
	 * 
	 * @param handle 
	 * @param tenantName 
	 * @param apName 
	 * @param epgName 
	 * @param ctrctName 
	 * @param graphName 
	 * @param entryList
	 * @param extIntf 
	 * @param intIntf 
	 * @param consumerDn 
	 * @param addOp 
	 * @param updateOp 
	 */
	addOrDelFWReq(handle: ApicDomain, tenantName: string, apName: string, epgName: string, ctrctName: string, graphName: string, entryList: ApicSecurityRule[], extIntf: string, intIntf: string, consumerDn: string, addOp: boolean, updateOp: boolean): ApicResponse;
	/**
	 * 
	 * This method adds the firewall service to a EPG in shared and vpc plan
	 * 
	 * @param handle 
	 * @param tenantName 
	 * @param apName 
	 * @param epgName 
	 * @param vpc 
	 * @param fwVendor 
	 * @param ldevName 
	 * @param graphName 
	 * @param entryList
	 * @param fwL3extExternal 
	 * @param fwL3extInternal 
	 * @param skipFWReq 
	 * @param interEpgFw 
	 * @param provTierNum 
	 * @param consTierNum 
	 * @param extIntf 
	 * @param intIntf 
	 * @param consumerDn 
	 */
	addFWToNetworkInterEpg(handle: ApicDomain, tenantName: string, apName: string, epgName: string, vpc: boolean, fwVendor: string, ldevName: string, graphName: string, entryList: ApicSecurityRule[], fwL3extExternal: string, fwL3extInternal: string, skipFWReq: boolean, interEpgFw: boolean, provTierNum: number, consTierNum: number, extIntf: string, intIntf: string, consumerDn: string): ApicResponse;
	/**
	 * 
	 * This method adds the firewall service to a EPG in shared and vpc plan
	 * 
	 * @param handle 
	 * @param tenantName 
	 * @param apName 
	 * @param epgName 
	 * @param vpc 
	 * @param fwVendor 
	 * @param ldevName 
	 * @param graphName 
	 * @param entryList
	 * @param fwL3extExternal 
	 * @param fwL3extInternal 
	 * @param skipFWReq 
	 * @param interEpgFw 
	 * @param provTierNum 
	 * @param consTierNum 
	 * @param consumerDn 
	 */
	addFWToNetwork(handle: ApicDomain, tenantName: string, apName: string, epgName: string, vpc: boolean, fwVendor: string, ldevName: string, graphName: string, entryList: ApicSecurityRule[], fwL3extExternal: string, fwL3extInternal: string, skipFWReq: boolean, interEpgFw: boolean, provTierNum: number, consTierNum: number, consumerDn: string): ApicResponse;
	/**
	 * 
	 * This method deletes the FW from EPG in Shared and VPC Plan
	 * 
	 * @param handle 
	 * @param tenantName 
	 * @param apName 
	 * @param epgName 
	 * @param vpc 
	 * @param graphName 
	 * @param ctrctName 
	 * @param protocol 
	 * @param startPort 
	 * @param skipFWReq 
	 * @param consumerDn 
	 */
	deleteFWFromNetwork(handle: ApicDomain, tenantName: string, apName: string, epgName: string, vpc: boolean, graphName: string, ctrctName: string, protocol: string, startPort: string, skipFWReq: boolean, consumerDn: string): ApicResponse;
	/**
	 * 
	 * This method implements the Rest API to APIC
	 * 
	 * @param handle 
	 * @param apiUrl 
	 * @param method 
	 * @param postBody 
	 */
	apicRestApi(handle: ApicDomain, apiUrl: string, method: string, postBody: string): string;
	/**
	 * 
	 * This method adds/deletes the router id in a tenant
	 * 
	 * @param handle 
	 * @param rtrId 
	 * @param addOp 
	 */
	addOrDelRouterId(handle: ApicDomain, rtrId: string, addOp: boolean): ApicResponse;
	/**
	 * 
	 * This method deletes Tenant EPG and association to VMM Domain in APIC
	 * 
	 * @param handle 
	 * @param tenantName 
	 * @param apName 
	 * @param epgName 
	 */
	deleteNetwork(handle: ApicDomain, tenantName: string, apName: string, epgName: string): ApicResponse;
	/**
	 * 
	 * This method creates Tenant, BD and CTX in APIC
	 * 
	 * @param handle 
	 * @param tenantName 
	 * @param bdName 
	 * @param ctxName 
	 * @param nTiers 
	 * @param aaaDomain 
	 */
	addTenant(handle: ApicDomain, tenantName: string, bdName: string, ctxName: string, nTiers: number, aaaDomain: string): ApicResponse;
	/**
	 * 
	 * This method deletes Tenant in APIC
	 * 
	 * @param handle 
	 * @param tenantName 
	 */
	deleteTenant(handle: ApicDomain, tenantName: string): ApicResponse;
	/**
	 * 
	 * This method adds the following objects in APIC
	 * 
	 * @param handle 
	 * @param dvsName 
	 * @param vcenterIP 
	 * @param userName 
	 * @param passwd 
	 * @param datacenter 
	 * @param vlanPoolName 
	 * @param vlanStart 
	 * @param vlanEnd 
	 * @param aaaDomain 
	 * @param dvsVersion 
	 */
	addVmmDomain(handle: ApicDomain, dvsName: string, vcenterIP: string, userName: string, passwd: string, datacenter: string, vlanPoolName: string, vlanStart: number, vlanEnd: number, aaaDomain: string, dvsVersion: string): ApicResponse;
	/**
	 * 
	 * This methods deletes VlanNS and vmmDomP objects from APIC
	 * 
	 * @param handle 
	 * @param domName 
	 * @param vlanPoolName 
	 */
	deleteVmmDomain(handle: ApicDomain, domName: string, vlanPoolName: string): ApicResponse;
	/**
	 * 
	 * This method adds/deleted encap blocks in Vlan Pool
	 * 
	 * @param handle 
	 * @param vlanPoolName 
	 * @param encapList
	 */
	updateVlanPool(handle: ApicDomain, vlanPoolName: string, encapList: ApicEncapBlock[]): ApicResponse;
	/**
	 * 
	 * This method adds the following objects in APIC
	 * 
	 * @param handle 
	 * @param dvsName 
	 * @param aepName 
	 * @param vcenterIP 
	 * @param userName 
	 * @param passwd 
	 * @param datacenter 
	 * @param mcastIP 
	 * @param mcastPoolName 
	 * @param rangeStart 
	 * @param rangeEnd 
	 * @param aaaDomain 
	 * @param domType 
	 * @param vlanPoolName 
	 * @param vlanEncaps
	 * @param virtualSwitch 
	 * @param dvsVersion 
	 */
	addAvsVmmDomain(handle: ApicDomain, dvsName: string, aepName: string, vcenterIP: string, userName: string, passwd: string, datacenter: string, mcastIP: string, mcastPoolName: string, rangeStart: string, rangeEnd: string, aaaDomain: string, domType: number, vlanPoolName: string, vlanEncaps: ApicEncapBlock[], virtualSwitch: string, dvsVersion: string): ApicResponse;
	/**
	 * 
	 * This methods deletes VLAN and McastAddr Pools and vmmDomP objects from APIC
	 * 
	 * @param handle 
	 * @param domName 
	 */
	deleteAvsAveVmmDomain(handle: ApicDomain, domName: string): ApicResponse;
	/**
	 * 
	 * This method adds/deleted range blocks in Vlan/Multicast Pool
	 * 
	 * @param handle 
	 * @param poolName 
	 * @param encapList
	 * @param poolType 
	 */
	updateAvsVlanMcastPool(handle: ApicDomain, poolName: string, encapList: ApicEncapBlock[], poolType: number): ApicResponse;
	/**
	 * 
	 * This method adds the following objects in APIC
	 * 
	 * @param handle 
	 * @param polName 
	 * @param vmmName 
	 * @param polMode 
	 * @param pInterval 
	 * @param logLevel 
	 * @param adminState 
	 * @param destGrpName 
	 * @param inclAction 
	 * @param caseVal 
	 */
	createFWPol(handle: ApicDomain, polName: string, vmmName: string, polMode: string, pInterval: string, logLevel: string, adminState: string, destGrpName: string, inclAction: string, caseVal: number): ApicResponse;
	/**
	 * 
	 * This method deletes the following objects in APIC
	 * 
	 * @param handle 
	 * @param polName 
	 */
	deleteFWPol(handle: ApicDomain, polName: string): ApicResponse;
	/**
	 * 
	 * This method updates the following objects:
	 * 
	 * @param handle 
	 * @param polName 
	 * @param vmmName 
	 * @param opValue 
	 */
	updateFWPolMapping(handle: ApicDomain, polName: string, vmmName: string, opValue: boolean): ApicResponse;
	/**
	 * 
	 * This method adds security policy (contract entry) between srcEpg and dstEpg
	 * 
	 * @param handle 
	 * @param tenant 
	 * @param ap 
	 * @param srcEpg 
	 * @param dstEpg 
	 * @param entryList
	 * @param createFlg 
	 */
	addSecurityPolicySet(handle: ApicDomain, tenant: string, ap: string, srcEpg: string, dstEpg: string, entryList: ApicSecurityRule[], createFlg: boolean): ApicResponse;
	/**
	 * 
	 * This method adds security policy (contract entry) between srcEpg and dstEpg
	 * 
	 * @param handle 
	 * @param tenant 
	 * @param filterName 
	 * @param entryList
	 */
	updateSecurityFilters(handle: ApicDomain, tenant: string, filterName: string, entryList: ApicSecurityRule[]): ApicResponse;
	/**
	 * 
	 * This method add/removed Consumer contract interface.
	 * 
	 * @param handle 
	 * @param tenant 
	 * @param ap 
	 * @param consumerEpg 
	 * @param contract 
	 * @param add 
	 */
	updateSharedSvcConsumer(handle: ApicDomain, tenant: string, ap: string, consumerEpg: string, contract: ApicSecurityPolicy, add: boolean): ApicResponse;
	/**
	 * 
	 * This method updates security policy (contract entry) between L3ext and dstEpg
	 * 
	 * @param handle 
	 * @param tenant 
	 * @param ap 
	 * @param dstEpg 
	 * @param entryList
	 * @param l3out 
	 * @param vpc 
	 * @param add 
	 */
	updateL3outPolicy(handle: ApicDomain, tenant: string, ap: string, dstEpg: string, entryList: ApicSecurityRule[], l3out: ApicL3Connectivity, vpc: boolean, add: boolean): ApicResponse;
	/**
	 * 
	 * This method deletes all the security policy (contracts) between srcEpg and dstEpg.
	 * 
	 * @param handle 
	 * @param tenant 
	 * @param ap 
	 * @param srcEpg 
	 * @param dstEpg 
	 */
	deleteSecurityPolicy(handle: ApicDomain, tenant: string, ap: string, srcEpg: string, dstEpg: string): ApicResponse;
	/**
	 * 
	 * This method adds L3 external connectivity using OSPF
	 * 
	 * @param handle 
	 * @param tenant 
	 * @param bdName 
	 * @param ctxName 
	 * @param l3outPolName 
	 * @param nodeList
	 * @param pathList
	 * @param areaId 
	 */
	addL3OutOspfPolicy(handle: ApicDomain, tenant: string, bdName: string, ctxName: string, l3outPolName: string, nodeList: ApicL3ExternalNodePolicy[], pathList: ApicL3ExternalPortPolicy[], areaId: string): ApicResponse;
	/**
	 * 
	 * This method delete L3 external connectivity using OSPF
	 * 
	 * @param handle 
	 * @param tenant 
	 * @param bdName 
	 * @param ctxName 
	 * @param l3outPolName 
	 */
	deleteL3OutOspfPolicy(handle: ApicDomain, tenant: string, bdName: string, ctxName: string, l3outPolName: string): ApicResponse;
	/**
	 * 
	 * This method creates VIP Address block in tn-common.
	 * 
	 * @param handle 
	 * @param vipPoolName 
	 * @param addrStart 
	 * @param addrEnd 
	 */
	addVipPool(handle: ApicDomain, vipPoolName: string, addrStart: string, addrEnd: string): ApicResponse;
	/**
	 * 
	 * This method deletes VIP Address block in tn-common.
	 * 
	 * @param handle 
	 * @param vipPoolName 
	 * @param addrStart 
	 * @param addrEnd 
	 */
	deleteVipPool(handle: ApicDomain, vipPoolName: string, addrStart: string, addrEnd: string): ApicResponse;
	/**
	 * 
	 * This method adds vmmOrch - Prov/Plan/LB config to APIC.
	 * 
	 * @param handle 
	 * @param domName 
	 * @param planName 
	 * @param vpcEnabled 
	 * @param sharedService 
	 * @param lDevIp 
	 * @param lDevCluster 
	 * @param vipStart 
	 * @param vipEnd 
	 */
	addVmmOrchLBInfo(handle: ApicDomain, domName: string, planName: string, vpcEnabled: boolean, sharedService: boolean, lDevIp: string, lDevCluster: string, vipStart: string, vipEnd: string): ApicResponse;
	/**
	 * 
	 * This method deletes vmmOrch - Prov/Plan/LB config to APIC
	 * 
	 * @param handle 
	 * @param domName 
	 * @param planName 
	 * @param lDevIp 
	 */
	deleteVmmOrchLBInfo(handle: ApicDomain, domName: string, planName: string, lDevIp: string): ApicResponse;
	/**
	 * 
	 * This methods checks if the physical nics are attached from hypervisors to the DVS
	 * 
	 * @param handle 
	 * @param dvsName 
	 */
	validateDvs(handle: ApicDomain, dvsName: string): boolean;
	/**
	 * 
	 * This method creates Application-profile under a given tenant in APIC
	 * 
	 * @param handle 
	 * @param tenantName 
	 * @param apName 
	 */
	addAppProfile(handle: ApicDomain, tenantName: string, apName: string): ApicResponse;
	/**
	 * 
	 * This method deletes Application-Profile in APIC
	 * 
	 * @param handle 
	 * @param tenantName 
	 * @param apName 
	 */
	deleteAppProf(handle: ApicDomain, tenantName: string, apName: string): ApicResponse;
	/**
	 * 
	 * This method adds/deletes security domain associations to the Vmm Domain
	 * 
	 * @param handle 
	 * @param domName 
	 * @param aaaList[] 
	 */
	updateVmmDomain(handle: ApicDomain, domName: string, aaaList: ApicAAADomainRef[]): ApicResponse;
	/**
	 * 
	 * This method deletes a shared service provider (EPG) from a contract
	 * 
	 * @param handle 
	 * @param tenant 
	 * @param ap 
	 * @param srcEpg 
	 * @param dstEpg 
	 * @param contract 
	 */
	deleteSharedServiceProvider(handle: ApicDomain, tenant: string, ap: string, srcEpg: string, dstEpg: string, contract: ApicSecurityPolicy): ApicResponse;
	/**
	 * 
	 * This method adds a self-signed certificate to the APIC
	 * 
	 * @param handle 
	 * @param userName 
	 * @param certName 
	 * @param certData 
	 */
	addCertificate(handle: ApicDomain, userName: string, certName: string, certData: string): ApicResponse;
}

/**
 * 
 * This class provides various utility functionalities for APIC actions.
 * 
 */
declare class ApicActionUtils {
	/**
	 * 
	 * Connects to a APIC and returns a ApicDomain object.
	 * 
	 * @param host 
	 * @param userName 
	 * @param password 
	 * @param noSsl 
	 * @param port 
	 * @param refreshable 
	 */
	static connectApic(host: string, userName: string, password: any, noSsl: boolean, port: number, refreshable: boolean): ApicDomain;
	/**
	 * 
	 * Disconnects connection with APIC.
	 * 
	 * @param apicDomain 
	 */
	static disconnectApic(apicDomain: ApicDomain): void;
	/**
	 * 
	 * Get ApicConfigHelper Utility.
	 * 
	 */
	static getConfigHelper(): ApicConfigHelper;
}

/**
 * 
 * This class represents APIC Address Instance.
 * 
 */
declare interface ApicAddressInstance {
	multiApicDn: string;
	name: string;
	dn: string;
	"address-type": string;
	status: string;
}

/**
 * 
 * This class represents APIC Unicast Address Block.
 * 
 */
declare interface ApicUnicastAddressBlock {
	multiApicDn: string;
	name: string;
	dn: string;
	"address-start": string;
	"address-end": string;
	status: string;
}

/**
 * 
 * This class represents a Vlan Encap Block.
 * 
 */
declare interface ApicEncapBlock {
	multiApicDn: string;
	name: string;
	dn: string;
	from: string;
	to: string;
	role: string;
	status: string;
}

/**
 * 
 * This class represents a IP criteria
 * 
 */
declare interface IpCriteria {
	name: string;
	dn: string;
	ip: string;
	status: string;
	descr: string;
}

/**
 * 
 * This class represents a Mac criteria
 * 
 */
declare interface MacCriteria {
	name: string;
	dn: string;
	mac: string;
	status: string;
	descr: string;
}

/**
 * 
 * This class represents a VM criteria.
 * 
 */
declare interface VmCriteria {
	labelName: string;
	name: string;
	dn: string;
	type: string;
	value: string;
	status: string;
	operator: string;
	descr: string;
	controller: string;
	vmName: string;
	domain: string;
}

/**
 * 
 * This class represents a Security Filter Rule entry.
 * 
 */
declare class ApicSecurityRule {
	multiApicDn: string;
	name: string;
	dn: string;
	dstFromPort: string;
	dstToPort: string;
	protocol: string;
	etherType: string;
	status: string;
	/**
	 * 
	 * Method returns value of any attribute of ApicSecurityRule.
	 * 
	 * @param attrName 
	 */
	getAttr(attrName: string): any;
	/**
	 * 
	 * Method sets value to any attribute of ApicSecurityRule.
	 * 
	 * @param attrName 
	 * @param value 
	 */
	setAttr(attrName: string, value: any): void;
}

/**
 * 
 * This class represents a Security Filter group.
 * 
 */
declare interface ApicSecurityFilter {
	multiApicDn: string;
	name: string;
	dn: string;
}

/**
 * 
 * This class represents a L3 external connectivity Node Policy.
 * 
 */
declare interface ApicL3ExternalNodePolicy {
	nodeDn: string;
	routerId: string;
}

/**
 * 
 * This class represents a L3 external connectivity Port Policy.
 * 
 */
declare interface ApicL3ExternalPortPolicy {
	portDn: string;
	portType: string;
	address: string;
}

/**
 * 
 * This class represents an End-Point Group's End Point in the APIC.
 * 
 */
declare interface ApicCEp {
	name: string;
	ip: string;
	lcC: string;
	encap: string;
}

/**
 * 
 * This class represents a Security Policy in APIC.
 * 
 */
declare interface ApicSecurityPolicy {
	multiApicDn: string;
	name: string;
	dn: string;
}

/**
 * 
 * This class represents a Tenant's Application Profile in APIC.
 * 
 */
declare interface ApicAp {
	multiApicDn: string;
	name: string;
	dn: string;
}

/**
 * 
 * This class represents a Tenant's Network/EPG in APIC.
 * 
 */
declare interface ApicEPG {
	multiApicDn: string;
	name: string;
	dn: string;
}

/**
 * 
 * This class represents a Tenant in APIC.
 * 
 */
declare interface ApicTenant {
	name: string;
	dn: string;
	multiApicDn: string;
}

/**
 * 
 * This class represents APIC AAA Domain.
 * 
 */
declare interface ApicAAADomain {
	name: string;
	dn: string;
	multiApicDn: string;
	status: string;
}

/**
 * 
 * This class represents APIC AAA Domain Reference.
 * 
 */
declare interface ApicAAADomainRef {
	name: string;
	dn: string;
	multiApicDn: string;
	status: string;
}

/**
 * 
 * This class represents a Service Graph Template
 * 
 */
declare interface ApicVnsAbsGraph {
	name: string;
	dn: string;
	multiApicDn: string;
}

/**
 * 
 * This class represents APIC L4-L7 Logical LB Device.
 * 
 */
declare interface ApicLogicalLBDevice {
	name: string;
	dn: string;
	multiApicDn: string;
}

/**
 * 
 * This class represents a Subnet entry
 * 
 */
declare interface ApicSubnet {
	name: string;
	dn: string;
	multiApicDn: string;
}

/**
 * 
 * This class represents a Bridge Domain in APIC.
 * 
 */
declare interface ApicBridgeDomain {
	name: string;
	dn: string;
	multiApicDn: string;
}

/**
 * 
 * This class represents a L3 Context (VRF) in APIC.
 * 
 */
declare interface ApicL3Context {
	name: string;
	dn: string;
	multiApicDn: string;
}

/**
 * 
 * This class represents a L3 outside connectivity policy
 * 
 */
declare interface ApicL3Connectivity {
	name: string;
	dn: string;
	multiApicDn: string;
}

/**
 * 
 * This class represents an APIC Physical Domain
 * 
 */
declare interface ApicPhysicalDomain {
	name: string;
	dn: string;
	multiApicDn: string;
}

/**
 * 
 * This class represents an APIC VMM Domain
 * 
 */
declare interface ApicVmmDomain {
	name: string;
	dn: string;
	multiApicDn: string;
}

/**
 * 
 * This class represents a VMM controller on APIC.
 * 
 */
declare interface ApicVmmController {
	name: string;
	dn: string;
	multiApicDn: string;
	rootContName: string;
}

/**
 * 
 * This class represents a Logical Switch on APIC.
 * 
 */
declare interface ApicDVS {
	name: string;
	dn: string;
	multiApicDn: string;
}
