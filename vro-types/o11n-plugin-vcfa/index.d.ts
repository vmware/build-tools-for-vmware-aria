declare class VCFACatalogResourceService {
	readonly allSupervisorResources_internal: any[];

	/**
	 * @param host 
	 */
	constructor(host: VCFAHost);

	/**
	 * @param id 
	 */
	public getSupervisorResource_internal(id: string): VCFASupervisorResource;
	/**
	 * @param id 
	 */
	public getSupervisorResource(id: string): VCFASupervisorResource;
	/**
	 * @param namespaceName 
	 */
	public getSupervisorNamespaceResources_internal(namespaceName: string): VCFASupervisorResource[];
}

declare class VCFACciService {
	readonly allSupervisorNamespaces_internal: any[];
	readonly allSupervisorNamespaces: any[];

	/**
	 * @param host 
	 */
	constructor(host: VCFAHost);

	/**
	 * @param projectName 
	 */
	public getSupervisorNamespacesForProject(projectName: string): VCFASupervisorNamespace[];
	/**
	 * @param projectName 
	 * @param jsonSpec 
	 */
	public createSupervisorNamespace(projectName: string, jsonSpec: string): VCFASupervisorNamespace;
	/**
	 * @param projectName 
	 * @param name 
	 */
	public getSupervisorNamespace(projectName: string, name: string): VCFASupervisorNamespace;
	/**
	 * @param projectName 
	 */
	public getSupervisorNamespacesForProject_internal(projectName: string): VCFASupervisorNamespace[];
	/**
	 * @param projectName 
	 * @param name 
	 */
	public getSupervisorNamespace_internal(projectName: string, name: string): VCFASupervisorNamespace;
}

/**
 * VMware Cloud Foundation Automation Entity finder to search for an entity.
 */
declare class VCFAEntitiesFinder {
}

/**
 * A generic VMware Cloud Foundation Automation Rest client for executing REST operations.
 */
declare class VCFAGenericRestClient {
	host: VCFAHost;

	constructor();

	/**
	 * Get Method to execute rest operation by setting Request object. Request object can hold information (http method, resource url, request payload).
	 * @param request 
	 */
	public get(request: VCFARestRequest): VCFARestResponse;
	/**
	 * Method to execute rest operation by setting Request object. Request object can hold information (http method, resource url, request payload).
	 * @param restRequest 
	 */
	public execute(restRequest: VCFARestRequest): VCFARestResponse;
	/**
	 * Put Method to execute rest operation by setting Request object. Request object can hold information (http method, resource url, request payload).
	 * @param request 
	 */
	public put(request: VCFARestRequest): VCFARestResponse;
	/**
	 * Delete Method (Http Delete) to execute rest operation by setting Request object. Request object can hold information (http method, resource url, request payload).
	 * @param request 
	 */
	public delete(request: VCFARestRequest): VCFARestResponse;
	/**
	 * Post Method to execute rest operation by setting Request object. Request object can hold information (http method, resource url, request payload).
	 * @param request 
	 */
	public post(request: VCFARestRequest): VCFARestResponse;
	/**
	 * Method to create HTTP rest Request. It holds parameter (HTTP Method (GET/PUT/POST/DELETE/PATCH), Resource Path URI, Request Payload (Stringified JSON)).
	 * 
	 * @param method 
	 * @param path 
	 * @param requestPayload 
	 */
	public createRequest(method: string, path: string, requestPayload: string): VCFARestRequest;
	/**
	 * Patch Method (Http Patch) to execute rest operation by setting Request object. Request object can hold information (http method, resource url, request payload).
	 * @param request 
	 */
	public patch(request: VCFARestRequest): VCFARestResponse;
}

/**
 * VMware Cloud Foundation Automation Host provides access to connection host properties & validation access.
 */
declare class VCFAHost {
	readonly apiToken: string;
	readonly projectService: VCFAProjectService;
	/** DisplayName for the Connection Host. */
	readonly displayName: string;
	/** Host Connection Name */
	readonly name: string;
	/** Host URL for the Automation Host Connection. */
	readonly host: string;
	readonly cciService: VCFACciService;
	/** Host Id. */
	readonly id: string;
	/** Session Mode of the Automation Host Connection (Shared Session or Per User Session). */
	readonly sessionMode: string;
	readonly catalogResourceService: VCFACatalogResourceService;
	readonly tenant: string;
	readonly k8sApiVersion: string;

	constructor();

	/**
	 * Destroys the connection object.
	 */
	public destroy(): void;
	/**
	 * Validates the Host Connection.
	 */
	public validate(): boolean;
	/**
	 * Creates a generic REST client for Automation Host.
	 */
	public createRestClient(): VCFAGenericRestClient;
}

/**
 * HostManager provides all the CRUD operations for VMware Cloud Foundation Automation Plugin along with Generic Rest Client support.
 */
declare class VCFAHostManager {
	/** Default Host Connection. */
	static readonly defaultHostData: VCFAHost;

	constructor();

	/**
	 * Save Automation Host object.
	 * @param props 
	 */
	public static save(props: any): string;

	/**
	 * Update Automation Host object..
	 * 
	 * @param props 
	 * @param host 
	 */
	public static update(props: any, host: VCFAHost): void;

	/**
	 * Creates a dynamic Automation Host.
	 * @param properties 
	 */
	public static createHost(properties: any): VCFAHost;
	/**
	 * Creates a temporary host with the current user host and credentials.
	 */
	public static createHostForCurrentUser(): VCFAHost;
	/**
	 * Delete Automation Host.
	 * @param host 
	 */
	public static delete(host: VCFAHost): void;
	/**
	 * Validate Automation Host.
	 * @param host 
	 */
	public static validate(host: VCFAHost): boolean;
	/**
	 * Get Automation Host by Sid.
	 * @param sid 
	 */
	public static getHostBySid(sid: string): VCFAHost;
}

declare class VCFAPrincipal {
	type: string;
	email: string;

	constructor();
}

declare class VCFAProject {
	sharedResources: boolean;
	readonly internalIdString: string;
	host: VCFAHost;
	name: string;
	description: string;
	id: string;
	administrators: VCFAPrincipal[];

	constructor();
}

declare class VCFAProjectService {
	readonly projects: any[];
	readonly projects_internal: any[];

	/**
	 * @param host 
	 */
	constructor(host: VCFAHost);

	/**
	 * @param id 
	 */
	getProject_internal(id: string): VCFAProject;
	/**
	 * @param id 
	 */
	getProject(id: string): VCFAProject;
}

/**
 * Represents a request object for VMware Cloud Foundation Automation API.
 */
declare class VCFARestRequest {
	path: string;
	method: string;
	payload: string;

	/**
	 * Gets Http Header value for the key from the Http Request.
	 * 
	 * @param header 
	 */
	getHeader(header: string): string;
	/**
	 * Sets Headers to the Http Request Object.
	 * 
	 * @param key 
	 * @param value 
	 */
	setHeader(key: string, value: string): void;
}

/**
 * Utility class used for VMware Cloud Foundation Automation API server response.
 */
declare class VCFARestResponse {
	/** The Server's response body as string. */
	readonly contentAsString: string;
	/** Retrieves the server's response headers as a Properties object. */
	readonly allHeaders: Properties;
	/** The Server's response content length. */
	readonly contentLength: number;
	/** The Server's response status message. */
	readonly statusMessage: string;
	/** The Server's response status code. */
	readonly statusCode: number;

	constructor();

	/**
	 * Retrieves the server's response header values per header with specific name.
	 * 
	 * @param headerName 
	 */
	getHeaderValues(headerName: string): string[];
}

declare class VCFASupervisorNamespace {
	readonly internalIdString: string;
	host: VCFAHost;
	readonly name: string;
	readonly projectName: string;
	readonly projectId: string;
	readonly k8sId: string;

	constructor();
}

declare class VCFASupervisorResource {
	apiVersion: string;
	kind: string;
	readonly internalIdString: string;
	host: VCFAHost;
	readonly name: string;
	readonly namespace: string;
	id: string;
	readonly projectName: string;
	readonly projectId: string;

	constructor();
}
