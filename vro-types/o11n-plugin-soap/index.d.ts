/**
 * Provides CRUD operations for SOAP hosts.
 */
declare class SOAPHostManager {
	/**
	 * Adds a SOAP host to the plug-in's inventory.
	 * @param host 
	 */
	static addHost(host: SOAPHost): SOAPHost;
	/**
	 * Updates the specified SOAP host in the plug-in's inventory.
	 * @param host 
	 */
	static updateHost(host: SOAPHost): SOAPHost;
	/**
	 * Removes a SOAP host from the plug-in's inventory.
	 * @param id 
	 */
	static removeHost(id: string): SOAPHost;
	/**
	 * Returns the list of SOAP host ids from the plug-in's inventory.
	 */
	static getHosts(): string[];
	/**
	 * Returns the SOAP host with the specified name from the plug-in's inventory.
	 * @param id 
	 */
	static getHost(id: string): SOAPHost;
	/**
	 * Reloads the plug-in configuration.
	 */
	static reloadConfiguration(): void;
}

/**
 * Represents an external system as its Web Service interface.
 */
declare interface SOAPHost {
	readonly id: string;
	preferredEndpointURL: string;
	name: string;
	wsdlUri: string;
	wsdlFileContent: string;
	wsdlLocal: boolean;
	connectionTimeout: number;
	requestTimeout: number;
	authentication: SOAPAuthentication;
	proxyHost: string;
	keyId: string;
	proxyPort: number;
	/**
	 * Gets a SOAP operation by name.
	 * @param operationName 
	 */
	getOperation(operationName: string): SOAPOperation;
	/**
	 * Gets all the SOAP operation names.
	 */
	getOperations(): string[];
	/**
	 * Creates a new workflow for a SOAP operation.
	 * @param operationName 
	 * @param workflowName 
	 * @param category 
	 * @param options 
	 */
	createWorkflow(operationName: string, workflowName: string, category: WorkflowCategory, options: Properties): Workflow;
}

/**
 * Represents a SOAP operation template generated from WSDL definition.
 */
declare interface SOAPOperation {
	readonly name: string;
	/**
	 * Gets the parent SOAP host of the SOAP operation.
	 */
	getHost(): SOAPHost;
	/**
	 * Gets a flat representation of the SOAP operation's input headers.
	 */
	getInHeaders(): string[];
	/**
	 * Gets a flat representation of the SOAP operation's input parameters.
	 */
	getInParameters(): string[];
	/**
	 * Gets a flat representation of the SOAP operation's output parameters.
	 */
	getOutParameters(): string[];
	/**
	 * Creates a new SOAP request for the SOAP operation.
	 */
	createSOAPRequest(): SOAPRequest;
	/**
	 * Invokes the SOAP operation.
	 * @param request 
	 */
	invoke(request: SOAPRequest): SOAPResponse;
	/**
	 * Invokes the SOAP operation with message interceptor.
	 * @param request 
	 * @param interceptor 
	 */
	invokeWithInterceptor(request: SOAPRequest, interceptor: SOAPInterceptor): SOAPResponse;
}

/**
 * Represents a SOAP request to pass when a SOAP operation is invoked.
 */
declare interface SOAPRequest {
	/**
	 * Sets an input parameter in the request.
	 * @param name 
	 * @param value 
	 */
	setInParameter(name: string, value: any): void;
	/**
	 * Adds an attribute to the input parameter in the request.
	 * @param parameterName 
	 * @param attributeName 
	 * @param attributeValue 
	 */
	addInParameterAttribute(parameterName: string, attributeName: string, attributeValue: any): void;
	/**
	 * Adds an raw XML header to the request.
	 * @param rawHeader 
	 */
	addRawHeader(rawHeader: string): void;
	/**
	 * Sets an input header in the request.
	 * @param headerName 
	 * @param headerValue 
	 */
	setInHeader(headerName: string, headerValue: any): void;
	/**
	 * Adds an attribute to the input header.
	 * @param headerName 
	 * @param attributeName 
	 * @param attributeValue 
	 */
	addInHeaderAttribute(headerName: string, attributeName: string, attributeValue: any): void;
	/**
	 * Adds a single HTTP header to the SOAP request. Any already existing HTTP header that has the same key will be overwritten.
	 * @param key 
	 * @param value 
	 */
	setHttpHeader(key: string, value: string): void;
	/**
	 * Adds HTTP headers to the SOAP request. All already existing HTTP headers that have the same keys will be overwritten.
	 * @param httpHeaders 
	 */
	setHttpHeaders(httpHeaders: Properties): void;
}

/**
 * Represents a SOAP response received when a SOAP operation is invoked.
 */
declare interface SOAPResponse {
	/**
	 * Gets the full names of all output parameters.
	 */
	getOutParameters(): string[];
	/**
	 * Gets an output parameter value from the response.
	 * @param parameterName 
	 */
	getOutParameter(parameterName: string): any;
	/**
	 * Gets the attribute names from the given output parameter.
	 * @param parameterName 
	 */
	getOutParameterAttributes(parameterName: string): string[];
	/**
	 * Gets the attribute value from the given output parameter and attribute name.
	 * @param parameterName 
	 * @param attributeName 
	 */
	getOutParameterAttribute(parameterName: string, attributeName: string): any;
	/**
	 * Gets the full names of all output headers.
	 */
	getOutHeaders(): string[];
	/**
	 * Gets an output header value from the response.
	 * @param headerName 
	 */
	getOutHeader(headerName: string): any;
	/**
	 * Gets the attribute names from the given output header.
	 * @param headerName 
	 */
	getOutHeaderAttributes(headerName: string): string[];
	/**
	 * Gets the attribute value from the given output header and attribute name.
	 * @param headerName 
	 * @param attributeName 
	 */
	getOutHeaderAttribute(headerName: string, attributeName: string): any;
}

/**
 * Represents a dynamic input parameter object to be used from scripting.
 */
declare interface SOAPDynamicInParameter {
}

/**
 * Represents a dynamic output parameter object to be used from scripting.
 */
declare interface SOAPDynamicOutParameter {
}

/**
 * Manages SOAP host authentication objects.
 */
declare class SOAPAuthenticationManager {
	/**
	 * Returns all the available authentication types.
	 */
	static getSOAPAuthentications(): string[];
	/**
	 * Returns the supported session modes.
	 */
	static getSessionModes(): string[];
	/**
	 * Creates an authentication instance.
	 * @param type 
	 * @param params 
	 */
	static createAuthentication(type: string, params: string[]): SOAPAuthentication;
}

/**
 * Represents authentication information for a SOAP host.
 */
declare interface SOAPAuthentication {
	type: string;
	rawAuthProperties: string[];
	/**
	 * Gets the specified authentication attribute.
	 * @param index 
	 */
	getRawAuthProperty(index: number): string;
}

/**
 * Holds javascript handler functions that intercept the SOAP requests and SOAP responses.
 */
declare interface SOAPInterceptor {
	/**
	 * Sets a handler function to intercept the SOAP request header.
	 * @param requestHeaderInterceptor 
	 */
	setRequestHeaderInterceptor(requestHeaderInterceptor: any): void;
	/**
	 * Sets a handler function to intercept the SOAP request body.
	 * @param requestBodyInterceptor 
	 */
	setRequestBodyInterceptor(requestBodyInterceptor: any): void;
	/**
	 * Sets a handler function to intercept the SOAP response header.
	 * @param responseHeaderInterceptor 
	 */
	setResponseHeaderInterceptor(responseHeaderInterceptor: any): void;
	/**
	 * Sets a handler function to intercept the SOAP response body.
	 * @param responseBodyInterceptor 
	 */
	setResponseBodyInterceptor(responseBodyInterceptor: any): void;
}
