/**
 * Supported BlockSizes for advanced mode.
 */
declare class OvaTransferAdvBlockSize {
	readonly size: number;

	private constructor();

	public static availableBlockSizes(): string[];
}

/**
 * Possible states: init running done error.
 */
declare class OvaTransferFileEntityState {
	readonly id: string;
	readonly name: string;
	readonly description: string;

	public static readonly init: OvaTransferFileEntityState;
	public static readonly running: OvaTransferFileEntityState;
	public static readonly done: OvaTransferFileEntityState;
	public static readonly error: OvaTransferFileEntityState;

	private constructor();
}

declare class OvaTransferFileImportManager {
	constructor();

	public getState(): OvaTransferImportState;
	public uploadImportFileEntity(): void;
	public getProgressInBytes(): number;
	/**
	 * @param fileEntity 
	 */
	public setImportFileEntity(fileEntity: OvaTransferImportFileEntity): void;
	/**
	 * @param sourcePathType 
	 * @param sourceHostname 
	 * @param sourcePath 
	 * @param sourcePort 
	 * @param useAuth 
	 * @param authType 
	 * @param authUsername 
	 * @param authPassword 
	 * @param advBlockSize 
	 * @param targetUsername 
	 * @param targetPassword 
	 */
	public availablesetSource(sourcePathType: string, sourceHostname: string, sourcePath: string, sourcePort: number, useAuth: boolean, authType: string, authUsername: string, authPassword: string, advBlockSize: string, targetUsername: string, targetPassword: string): void;
	public getCapacity(): number;
	public getProgress(): number;
}

declare class OvaTransferImportFileEntity {
	constructor();

	public getState(): OvaTransferFileEntityState;
	/**
	 * @param size 
	 */
	public setSize(size: number): void;
	/**
	 * @param path 
	 */
	public setPath(path: string): void;
	/**
	 * @param url 
	 */
	public setUrl(url: string): void;
	/**
	 * @param deviceId 
	 */
	public setDeviceId(deviceId: string): void;
	/**
	 * @param chunkSize 
	 */
	public setChunkSize(chunkSize: number): void;
	public getErrorMessage(): string;
	public getDeviceId(): string;
	public getUrl(): string;
}

/**
 * Possible states: init running done error.
 */
declare class OvaTransferImportState {
	readonly id: string;
	readonly name: string;
	readonly description: string;

	public static readonly init: OvaTransferImportState;
	public static readonly running: OvaTransferImportState;
	public static readonly done: OvaTransferImportState;
	public static readonly error: OvaTransferImportState;

	private constructor();
}

/**
 * Possible types: OVF OVA.
 */
declare class OvaTransferImportType {
	readonly id: string;
	readonly name: string;
	readonly description: string;

	public static readonly OVF: OvaTransferImportType;
	public static readonly OVA: OvaTransferImportType;

	private constructor();
}

declare class OvaTransferOvaImportManager {
	constructor();

	/**
	 * @param closeConnection 
	 */
	public getOvfDescriptor(closeConnection: boolean): string;
	public getNextDevice(): OvaTransferImportFileEntity;
	/**
	 * @param device 
	 */
	public uploadImportFileEntity(device: OvaTransferImportFileEntity): void;
	public getState(): OvaTransferImportState;
	/**
	 * @param fileEntity 
	 */
	public addImportFileEntity(fileEntity: OvaTransferImportFileEntity): void;
	/**
	 * @param deviceId 
	 * @param url 
	 */
	public addNfcLeaseDevice(deviceId: string, url: string): boolean;
	/**
	 * @param sourcePathType 
	 * @param sourceHostname 
	 * @param sourcePath 
	 * @param sourcePort 
	 * @param useAuth 
	 * @param authType 
	 * @param authUsername 
	 * @param authPassword 
	 * @param advBlockSize 
	 * @param targetUsername 
	 * @param targetPassword 
	 */
	public setSource(sourcePathType: string, sourceHostname: string, sourcePath: string, sourcePort: number, useAuth: boolean, authType: string, authUsername: string, authPassword: string, advBlockSize: string, targetUsername: string, targetPassword: string): void;
	public getCapacity(): number;
	public getProgress(): number;
}

declare class OvaTransferOvfDeploymentOption {
	key: string;
	label: string;
	description: string;

	constructor();
	/**
	 * @param key 
	 * @param label 
	 * @param description 
	 */
	constructor(key: string, label: string, description: string);
}

declare class OvaTransferOvfExportFileEntity {
	constructor();

	public getKey(): string;
	public getState(): OvaTransferFileEntityState;
	public getSize(): number;
	public getUrl(): string;
	/**
	 * @param url 
	 */
	public setUrl(url: string): void;
	public getErrorMessage(): string;
	/**
	 * @param key 
	 */
	public setKey(key: string): void;
	/**
	 * @param datastoreKey 
	 */
	public setDatastoreKey(datastoreKey: string): void;
	/**
	 * @param disk 
	 */
	public setDisk(disk: boolean): void;
	/**
	 * @param sslThumbprint 
	 */
	public setSslThumbprint(sslThumbprint: string): void;
	public getTargetId(): string;
	/**
	 * @param targetId 
	 */
	public setTargetId(targetId: string): void;
	public getDiskExportName(): string;
}

/**
 * Possible states: init running done error.
 */
declare class OvaTransferOvfExportState {
	readonly id: string;
	readonly name: string;
	readonly description: string;

	public static readonly init: OvaTransferOvfExportState;
	public static readonly running: OvaTransferOvfExportState;
	public static readonly done: OvaTransferOvfExportState;
	public static readonly error: OvaTransferOvfExportState;

	private constructor();
}

declare class OvaTransferOvfImportManager {
	constructor();

	/**
	 * @param closeConnection 
	 */
	public getOvfDescriptor(closeConnection: boolean): void;
	public getNextDevice(): OvaTransferImportFileEntity;
	/**
	 * @param device 
	 */
	public uploadImportFileEntity(device: OvaTransferImportFileEntity): void;
	public getState(): OvaTransferImportState;
	/**
	 * @param fileEntity 
	 */
	public addImportFileEntity(fileEntity: OvaTransferImportFileEntity): void;
	/**
	 * @param deviceId 
	 * @param url 
	 */
	public addNfcLeaseDevice(deviceId: string, url: string): void;
	/**
	 * @param sourcePathType 
	 * @param sourceHostname 
	 * @param sourcePath 
	 * @param sourcePort 
	 * @param useAuth 
	 * @param authType 
	 * @param authUsername 
	 * @param authPassword 
	 * @param advBlockSize 
	 * @param targetUsername 
	 * @param targetPassword 
	 */
	public setSource(sourcePathType: string, sourceHostname: string, sourcePath: string, sourcePort: number, useAuth: boolean, authType: string, authUsername: string, authPassword: string, advBlockSize: string, targetUsername: string, targetPassword: string): void;
	public getCapacity(): number;
	public getProgress(): number;
}

declare class OvaTransferOvfNetwork {
	name: string;
	desciption: string;

	constructor();
	/**
	 * @param name 
	 * @param desciption 
	 */
	constructor(name: string, desciption: string);
}

declare class OvaTransferOvfProperty {
	defaultValue: string;
	description: string;
	label: string;
	classId: string;
	instanceId: string;
	id: string;
	type: string;

	constructor();
	/**
	 * @param defaultValue 
	 * @param description 
	 * @param label 
	 * @param classId 
	 * @param instanceId 
	 * @param id 
	 * @param type 
	 */
	constructor(defaultValue: string, description: string, label: string, classId: string, instanceId: string, id: string, type: string);

}

declare class OvaTransferPluginInventory {
	constructor();
}

declare interface OvaTransferWorkflowCreator {
	id: string;
	getId(): string;
	/**
	 * @param name 
	 * @param description 
	 * @param category 
	 * @param sourcePath 
	 * @param sourceType 
	 * @param deploymentOptions 
	 * @param ovfProperties 
	 * @param ovfNetworks 
	 * @param sourcePathType 
	 * @param sourceHostname 
	 * @param sourcePort 
	 * @param useAuthentication 
	 * @param authUsername 
	 * @param authPassword 
	 * @param enableAdvancedOptions 
	 * @param advBlockSize 
	 */
	createImportWizardWorkflow(name?: string, description?: string, category?: WorkflowCategory, sourcePath?: string, sourceType?: OvaTransferImportType, deploymentOptions?: OvaTransferOvfDeploymentOption[], ovfProperties?: OvaTransferOvfProperty[], ovfNetworks?: OvaTransferOvfNetwork[], sourcePathType?: string, sourceHostname?: string, sourcePort?: number, useAuthentication?: boolean, authUsername?: string, authPassword?: string, enableAdvancedOptions?: boolean, advBlockSize?: string): Workflow;
	/**
	 * @param name 
	 * @param description 
	 * @param category 
	 * @param sourcePath 
	 * @param sourceType 
	 * @param deploymentOptions 
	 * @param ovfProperties 
	 * @param ovfNetworks 
	 * @param sourcePathType 
	 * @param sourceHostname 
	 * @param sourcePort 
	 * @param useAuthentication 
	 * @param authUsername 
	 * @param authPassword 
	 * @param enableAdvancedOptions 
	 * @param advBlockSize 
	 */
	createImportWizardWorkflowV8(name?: string, description?: string, category?: WorkflowCategory, sourcePath?: string, sourceType?: OvaTransferImportType, deploymentOptions?: OvaTransferOvfDeploymentOption[], ovfProperties?: OvaTransferOvfProperty[], ovfNetworks?: OvaTransferOvfNetwork[], sourcePathType?: string, sourceHostname?: string, sourcePort?: number, useAuthentication?: boolean, authUsername?: string, authPassword?: string, enableAdvancedOptions?: boolean, advBlockSize?: string): Workflow;
}

declare class SVAImportManagerFactory {
	private constructor();

	/**
	 * Returns de.sva.vco.plugin.ovatransfer.core.TemplateImportManager but the class definition is not available in the API explorer
	 * @param importType 
	 */
	public createImportManager(importType: OvaTransferImportType): any;
	public createFileImportManager(): OvaTransferFileImportManager;

	public static getInstance(): SVAImportManagerFactory;
}

declare class SVAOvfExportManager {
	constructor();

	getState(): OvaTransferOvfExportState;
	getProgress(): number;
	/**
	 * @param destinationPathType 
	 * @param destinationHostname 
	 * @param destinationPath 
	 * @param destinationPort 
	 * @param destinationReqMethod 
	 * @param useAuth 
	 * @param authType 
	 * @param authUsername 
	 * @param authPassword 
	 * @param advBlockSize 
	 */
	setDestination(destinationPathType: string, destinationHostname: string, destinationPath: string, destinationPort: number, destinationReqMethod: string, useAuth: boolean, authType: string, authUsername: string, authPassword: string, advBlockSize: string): void;
	/**
	 * @param capacity 
	 */
	setCapacity(capacity: number): void;
	/**
	 * @param fileEntity 
	 */
	addOvfFileEntity(fileEntity: OvaTransferOvfExportFileEntity): void;
	/**
	 * @param ovfName 
	 */
	setOvfName(ovfName: string): void;
	processNextQueuedDisk(): OvaTransferOvfExportFileEntity;
	/**
	 * @param ovfDescriptor 
	 */
	saveOvfMetadata(ovfDescriptor: string): void;
	/**
	 * @param nfcReadTimeout 
	 */
	setNfcReadTimeout(nfcReadTimeout: number): void;
}
