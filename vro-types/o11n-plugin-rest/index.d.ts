
/**
 * Provides CRUD operations for RootElement objects.
 */
declare class RESTHostManager {
	/**
	 * Creates a RESTHost object.
	 * @param name 
	 */
	static createHost(name: string): RESTHost;
	/**
	 * Creates a RESTHost object with option to use new Http Context per request. With that setup the Rest Host supports parallel requests.
	 * @param name 
	 */
	static createHostSupportingParallelRequests(name: string): RESTHost;
	/**
	 * Adds a RESTHost object to the plug-in's inventory.
	 * @param host 
	 */
	static addHost(host: RESTHost): RESTHost;
	/**
	 * Updates the specified RESTHost in the plug-in's inventory.
	 * @param host 
	 */
	static updateHost(host: RESTHost): RESTHost;
	/**
	 * Removes a RESTHost object from the plug-in's inventory.
	 * @param id 
	 */
	static removeHost(id: string): RESTHost;
	/**
	 * Retrieves a RESTHost object by name.
	 * @param id 
	 */
	static getHost(id: string): RESTHost;
	/**
	 * Retrieves an array of available host names.
	 */
	static getHosts(): string[];
	/**
	 * Reloads the plug-in configuration.
	 */
	static reloadConfiguration(): void;
	/**
	 * Generates a workflow from a REST operation.
	 * @param operation 
	 * @param workflowName 
	 * @param category 
	 * @param defaultContentType 
	 */
	static createWorkflow(operation: RESTOperation, workflowName: string, category: WorkflowCategory, defaultContentType: string): Workflow;
	/**
	 * Generates a workflow from a REST operation.
	 * @param operation 
	 * @param workflowName 
	 * @param category 
	 * @param namespace 
	 * @param elementName 
	 * @param defaultContentType 
	 */
	static createWorkflowWithXsdInput(operation: RESTOperation, workflowName: string, category: WorkflowCategory, namespace: string, elementName: string, defaultContentType: string): Workflow;
	/**
	 * Creates a new transient (not persisted) host.
	 * @param restHostPrototype 
	 */
	static createTransientHostFrom(restHostPrototype: RESTHost): RESTHost;
	/**
	 * Creates a new transient RESTOperation. If Tthe base URL of operation's host is changed, all subsequent requests to the operation will be executed against the new base URL (new host).
	 * @param restOperationPrototype 
	 */
	static createTransientOperationFrom(restOperationPrototype: RESTOperation): RESTOperation;
	/**
	 * Get the Swagger service URL from Swagger spec and the preferred protocol
	 * @param swaggerSpec 
	 * @param host 
	 * @param basePath 
	 * @param preferredCommunicationProtocol 
	 */
	static getSwaggerServiceUrl(swaggerSpec: string, host: string, basePath: string, preferredCommunicationProtocol: string): string;
	/**
	 * Adds a new operation from a URL.
	 * @param hostName 
	 * @param swaggerSpecUrl 
	 * @param auths 
	 * @param preferredCommunicationProtocol 
	 * @param params 
	 */
	static createRESTHostFromSwaggerSpecUrl(hostName: string, swaggerSpecUrl: string, auths: AuthorizationValue[], preferredCommunicationProtocol: string, params: any): RESTHost;
	/**
	 * Adds a new operation from string definition
	 * @param hostName 
	 * @param swaggerSpec 
	 * @param host 
	 * @param basePath 
	 * @param preferredCommunicationProtocol 
	 * @param params 
	 */
	static createRESTHostFromSwaggerSpecString(hostName: string, swaggerSpec: string, host: string, basePath: string, preferredCommunicationProtocol: string, params: any): RESTHost;
}

/**
 * Provides authentication types listing and creation.
 */
declare class RESTAuthenticationManager {
	/**
	 * Returns all available authentication types.
	 */
	static getRESTAuthentications(): string[];
	/**
	 * Returns the supported session modes.
	 */
	static getSessionModes(): string[];
	/**
	 * Creates an authentication instance.
	 * @param type 
	 * @param params 
	 */
	static createAuthentication(type: string, params: string[]): RESTAuthentication;
}

/**
 * Various utility functions.
 */
declare class RESTUtils {
	/**
	 * @param xmlString 
	 */
	static xml2json(xmlString: string): string;
	/**
	 * @param date 
	 */
	static xmlDate(date: Date): string;
	/**
	 * @param number 
	 */
	static xmlInt(number: number): string;
}

/**
 * Authorization value util
 */
declare interface AuthorizationValue {
	/**
	 * @param keyName 
	 */
	setKeyName(keyName: string): void;
	/**
	 * @param type 
	 */
	setType(type: string): void;
	/**
	 * @param value 
	 */
	setValue(value: string): void;
	getKeyName(): string;
	getValue(): string;
	getType(): string;
	/**
	 * @param keyName 
	 */
	keyName(keyName: string): AuthorizationValue;
	/**
	 * @param value 
	 */
	value(value: string): AuthorizationValue;
	/**
	 * @param type 
	 */
	type(type: string): AuthorizationValue;
}

/**
 * REST plug-in host object
 */
declare class RESTHost {
	readonly id: string;
	name: string;
	url: string;
	authentication: RESTAuthentication;
	proxyAuthentication: RESTAuthentication;
	connectionTimeout: number;
	operationTimeout: number;
	hostVerification: boolean;
	proxyHost: string;
	proxyPort: number;
	privateKeyId: string;

  /**
   * constructor
   * @param name
   */
  constructor(name: string);
	/**
	 * Creates a copy of the REST host.
	 */
	clone(): RESTHost;
	/**
	 * Creates a new host from this REST host.
	 */
	newHostFromThis(): RESTHost;
	/**
	 * Adds a new operation to the host.
	 * @param operation 
	 */
	addOperation(operation: RESTOperation): RESTOperation;
	/**
	 * Updates a REST operation.
	 * @param operation 
	 */
	updateOperation(operation: RESTOperation): RESTOperation;
	/**
	 * Retrieves an operation by id.
	 * @param id 
	 */
	getOperation(id: string): RESTOperation;
	/**
	 * Removes a REST operation from this host.
	 * @param id 
	 */
	removeOperation(id: string): RESTOperation;
	/**
	 * Retrieves all operation names.
	 */
	getOperations(): string[];
	/**
	 * Adds an XSD schema from a given URL.
	 * @param url 
	 */
	addSchemaFromUrl(url: string): string[];
	/**
	 * Adds an XSD schema provided as a string.
	 * @param xml 
	 */
	addSchemaFromXmlString(xml: string): string[];
	/**
	 * Removes all associated XSD schemas from this host.
	 */
	removeAllSchemas(): void;
	/**
	 * Retrieves all XML elements for a given namespace, from XSD files for this host.
	 * @param namespace 
	 */
	getSchemaElements(namespace: string): any[];
	/**
	 * Retrieves all namespaces from XSD files for this host.
	 */
	getSchemaNamespaces(): string[];
	/**
	 * Creates a RESTRequest using the host data.
	 * @param method 
	 * @param url 
	 * @param content 
	 */
	createRequest(method: string, url: string, content: any): RESTRequest;
	/**
	 * Set automatic URL redirection - if set to true the generated HTTP request will automatically follow the URL redirects. Return true if the value is propagated.
	 * @param autoRedirectEnabled 
	 */
	setAutoUrlRedirect(autoRedirectEnabled: boolean): void;
	/**
	 * Check if the automatically redirectiong is enabled.
	 */
	getAutoUrlRedirectEnabled(): boolean;
	/**
	 * Executes a request with provided credentials.
	 * Supported only for hosts created with RESTHostManager.createTransientHostFrom() with HTTP Basic Authentication.
	 * @param method 
	 * @param url 
	 * @param content 
	 * @param user 
	 * @param pass 
	 */
	executeRequestWithCredentials(method: string, url: string, content: any, user: string, pass: string): RESTResponse;
}

/**
 * Contains server authentication properties.
 */
declare interface RESTAuthentication {
	type: string;
	rawAuthProperties: string[];
	/**
	 * Retrieves an authentication property by specified index.
	 * @param index 
	 */
	getRawAuthProperty(index: number): string;
}

/**
 * Contains server authentication properties.
 */
declare class HTTPBasicAuthentication {
	type: string;
	rawAuthProperties: string[];
	/**
	 * Retrieves an authentication property by specified index.
	 * @param index 
	 */
	getRawAuthProperty(index: number): string;
	/**
	 * get Basic Auth Header Value
	 * @param username 
	 * @param password 
	 */
	static getBasicAuthHeaderValue(username: string, password: string): string;
}

/**
 * Utility class created from REST operation (or host), used to make the actual HTTP request.
 */
declare interface RESTRequest {
	fullUrl: string;
	contentType: string;
	/**
	 * Runs the HTTP request.
	 */
	execute(): RESTResponse;
	/**
	 * Runs the HTTP request with provided Basic HTTP Authentication credentials. This operation is supported only for requests executed against RESTHost that supports Basic HTTP Authentication.
	 * @param user 
	 * @param pass 
	 */
	executeWithCredentials(user: string, pass: string): RESTResponse;
	/**
	 * Sets an additional HTTP header to this request.
	 * @param header 
	 * @param value 
	 */
	setHeader(header: string, value: string): void;
	/**
	 * Get HTTP method of the request.
	 */
	getMethod(): string;
}

/**
 * Utility class used for HTTP server response.
 */
declare interface RESTResponse {
	contentAsString: string;
	statusCode: number;
	contentLength: number;
	/**
	 * Retrieves the server's response header values per header with specified name
	 * @param headerName 
	 */
	getHeaderValues(headerName: string): string[];
	/**
	 * Retrieves the server's response headers as a Properties object.
	 */
	getAllHeaders(): Properties;
}

/**
 * REST plug-in operation object
 */
declare class RESTOperation {
	readonly id: string;
	name: void;
	host: void;
	inParametersCount: void;
	method: void;
	urlTemplate: void;
	defaultContentType: void;
	constructor(urlTemplate: string);
	/**
	 * Creates a copy of the REST operation.
	 */
	clone(): RESTOperation;
	/**
	 * Creates a new operation from this REST operation.
	 */
	newOperationFromThis(): RESTOperation;
	/**
	 * Creates a RESTRequest by using the information contained in this operation and its parent host.
	 * @param params 
	 * @param content 
	 */
	createRequest(params: string[], content: any): RESTRequest;
	/**
	 * Retrieves the names of URL template input parameters.
	 */
	getInParameters(): string[];
	/**
	 * Retrieves the names of HTTP
	 * request headers.
	 */
	getHeaderParameters(): string[];
	/**
	 * The path parameters count.
	 */
	getPathParamsCount(): number;
	/**
	 * Get the default content type
	 */
	getDefaultContentType(): string;
	/**
	 * Get the supported request body content type
	 */
	getSupportedContentTypes(): any;
	/**
	 * Add a supported content type
	 * @param supportedContentType 
	 */
	addSupportedContentType(supportedContentType: string): void;
	/**
	 * Get supportet Accept header media types
	 */
	getSupportedAcceptHeaderMediaTypes(): string[];
	/**
	 * Add preferred media type for the Accept header
	 * @param mediaType 
	 */
	addPreferredAcceptHeaderMediaType(mediaType: string): void;
	/**
	 * Add supported media type for the Accept header]
	 * @param mediaType 
	 */
	addSupportedAcceptHeaderMediaType(mediaType: string): void;
	/**
	 * Is a mandatory path parameter.
	 * @param pathParamName 
	 */
	isPathParamMandatory(pathParamName: string): boolean;
	/**
	 * Add a mandatory path parameter
	 * @param mandatoryPathParamName 
	 */
	addMandatoryPathParameter(mandatoryPathParamName: string): void;
	/**
	 * Check if query param is mandatory]
	 * @param queryParamName 
	 */
	isQueryParamMandatory(queryParamName: string): boolean;
	/**
	 * Add a mandatory query parameter
	 * @param mandatoryQueryParamName 
	 */
	addMandatoryQueryParameter(mandatoryQueryParamName: string): void;
	/**
	 * Is a mandatory header parameter.
	 * @param headerParamName 
	 */
	isHeaderParamMandatory(headerParamName: string): boolean;
	/**
	 * Add a mandatory header parameter
	 * @param mandatoryHeaderParamName 
	 */
	addMandatoryHeaderParameter(mandatoryHeaderParamName: string): void;
}
