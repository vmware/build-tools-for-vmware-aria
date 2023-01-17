declare interface VAPIClient {
	/**
	 * Close the active vAPI client session(s).
	 */
	close(): void;
}

declare interface VAPIEndpoint {
	readonly username: string;
	readonly password: string;
	readonly endpointUrl: string;
	readonly useSecureConnection: boolean;
	/**
	 * @param username 
	 * @param password 
	 */
	client(username: string, password: string): VAPIClient;
}

declare interface VAPIMetamodel {
	readonly username: string;
	readonly password: string;
	readonly endpointUrl: string;
	readonly useSecureConnection: boolean;
	checkForModifications(): string[];
}

declare class VAPIManager {
	/**
	 * @param endpointUrl 
	 * @param useSecureConnection 
	 * @param username 
	 * @param password 
	 */
	static addEndpoint(endpointUrl: string, useSecureConnection: boolean, username: string, password: string): VAPIEndpoint;
	static getAllEndpoints(): VAPIEndpoint[];
	/**
	 * @param endpointUrl 
	 */
	static removeEndpoint(endpointUrl: string): void;
	/**
	 * @param endpointUrl 
	 */
	static findEndpoint(endpointUrl: string): VAPIEndpoint;
	/**
	 * @param endpointUrl 
	 * @param useSecureConnection 
	 * @param username 
	 * @param password 
	 */
	static importMetamodel(endpointUrl: string, useSecureConnection: boolean, username: string, password: string): void;
	static getAllMetamodels(): VAPIMetamodel[];
	/**
	 * @param endpointUrl 
	 */
	static findMetamodel(endpointUrl: string): VAPIMetamodel;
	/**
	 * @param endpointUrl 
	 */
	static removeMetamodel(endpointUrl: string): void;
	/**
	 * @param data 
	 */
	static stringifyDataValue(data: any): string;
}
