/**
 * Specifies the object Type VRA:HOST.
 */
declare class VraHost {
	readonly displayName: string;
	readonly name: string;
	readonly cloudHost: string;
	readonly id: string;
	readonly vraHost: string;
	readonly sessionMode: string;
	readonly connectionType: string;
	readonly user: string;
	/**
	 * Validates the Host Connection.
	 */
	validate(): boolean;

	/**
	 * Creates a generic REST client for Automation Host
	 */
	createRestClient(): VraGenericRestClient;

}

declare class VraGenericRestClient {
	readonly host: VraHost;

	/**
	 * Method to execute rest operation by setting Request object. Request object can hold information (http method, resource url, request payload)
	 * @param restRequest
	 */
	execute(restRequest: VraRestRequest): VraRestResponse;

	/**
	 * Method to create HTTP rest Request. It holds parameter (HTTP Method (GET/PUT/POST/DELETE/PATCH), Resource Path URI, Request Payload (Stringified JSON)).
	 * @param method
	 * @param path
	 * @param requestPayload
	 */
	createRequest(method: string, path: string, requestPayload: string): VraRestRequest;

}

declare class VraRestRequest {
	readonly path: string;
	readonly method: string;
	readonly payload: string;

	/**
	 * Sets Headers to the Http Request Object.
	 * @param key
	 * @param value
	 */
	setHeader(key: string,value:string): void;

	/**
	 * Gets Http Header value for the key from the Http Request.
	 * @param header
	 */
	getHeader(header: string): string;
}

declare class VraRestResponse {
	readonly contentAsString: string;
	readonly allHeaders: { [key: string]: string };
	readonly contentLength: number;
	readonly statusMessage: string;
	readonly statusCode: number;
	
	/**
	 * Retrieves the server's response header values per header with specific name.
	 * @param headerName
	 */
	getHeaderValues(headerName: string): string;
}