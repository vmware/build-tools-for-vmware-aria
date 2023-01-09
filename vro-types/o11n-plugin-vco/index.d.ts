/**
 * Provides information about the progress status of a deployment action.
 */
declare interface VCODeploymentStatus {
	/**
	 * Retrieves any additional information about the status of the deployment.
	 */
	getMessages(): any[];
	/**
	 * Retrieves the target machine name for the deployment.
	 */
	getTargetName(): string;
	/**
	 * Retrieves the status with possible values SUCCESSFULL or FAILED>.
	 */
	getStatusAsString(): string;
	/**
	 * Retrieves the element name for the event.
	 */
	getElementName(): string;
}

/**
 * Provides functionality for validating hosts and retrieving certification info.
 */
declare class VCOHostValidator {
	constructor();
	/**
	 * Initializes a HostValidator instance for validating URL and certificate info.
	 * @param host 
	 * @param port 
	 */
	constructor(host: string, port: number);
	/**
	 * Initializes a HostValidator instance for validating URL and certificate info.
	 * @param scheme 
	 * @param host 
	 * @param port 
	 */
	constructor(scheme: string, host: string, port: number);
	/**
	 * Retrieves the server's certificate info as a string.
	 */
	getCertificateInfo(): Properties;
	/**
	 * Install the server's certificate into the JSSE keystore (only the server's specific certificate, not the whole chain).
	 */
	installCertificates(): void;
}

/**
 * Object representing an instance of remote objects of type Action.
 */
declare interface VCORemoteAction {
	readonly id: string;
	readonly name: string;
}

/**
 * Object representing an instance of remote objects of type ActionCategory.
 */
declare interface VCORemoteActionCategory {
	readonly name: string;
	readonly id: string;
}

/**
 * Object representing the root folder containing remote objects of type Action.
 */
declare interface VCORemoteActionsFolder {
	readonly name: string;
	readonly id: string;
}

/**
 * Object representing an instance of remote objects of type Any.
 */
declare interface VCORemoteAny {
	readonly name: string;
	readonly id: string;
}

/**
 * Object representing an instance of remote objects of type ConfigurationElementCategory.
 */
declare interface VCORemoteConfigurationElement {
	readonly id: string;
	readonly name: string;
}

/**
 * Object representing an instance of remote objects of type ConfigurationElementCategory.
 */
declare interface VCORemoteConfigurationElementCategory {
	readonly name: string;
	readonly id: string;
}

/**
 * Object representing the root folder for the remote objects of type Configuration.
 */
declare interface VCORemoteConfigurationsFolder {
	readonly name: string;
	readonly id: string;
}

/**
 * Object representing an instance of remote objects of type Package.
 */
declare interface VCORemotePackage {
	readonly id: string;
	readonly name: string;
}

/**
 * Object representing the root folder for the remote objects of type Package.
 */
declare interface VCORemotePackagesFolder {
	readonly name: string;
	readonly id: string;
}

declare interface VCORemotePlugin {
	readonly name: string;
	readonly id: string;
}

declare interface VCORemotePluginObject {
	readonly id: string;
	readonly name: string;
}

/**
 * Object representing an instance of remote objects of type ResourceElement.
 */
declare interface VCORemoteResourceElement {
	readonly id: string;
	readonly name: string;
}

/**
 * Object representing an instance of remote objects of type ResourceElementCategory.
 */
declare interface VCORemoteResourceElementCategory {
	readonly name: string;
	readonly id: string;
}

/**
 * Object representing the root folder for the remote objects of type Resource.
 */
declare interface VCORemoteResourcesFolder {
	readonly name: string;
	readonly id: string;
}

/**
 * Object representing the connection info for remote vCenter Orchestrator server.
 */
declare interface VCORemoteServer {
	readonly id: string;
	readonly status: string;
	readonly connectionTimeout: number;
	readonly socketTimeout: string;
	readonly shared: boolean;
	readonly ssoEnabled: boolean;
	readonly ssoScheme: string;
	readonly ssoHost: string;
	readonly ssoPort: number;
	readonly retryTimeout: string;
	readonly connectionId: string;
	readonly name: string;
	readonly simpleName: string;
	readonly scheme: string;
	readonly host: string;
	readonly port: number;
	/**
	 * Retrieves a list of available remote packages.
	 */
	findAllPackages(): VCORemotePackage[];
	/**
	 * Retrieves a list of available remote workflows.
	 */
	findAllWorkflows(): VCORemoteWorkflow[];
}

/**
 * Object representing the root folder for the remote system objects.
 */
declare interface VCORemoteSystem {
	readonly name: string;
	readonly id: string;
}

/**
 * Object representing an instance of remote objects of type Task.
 */
declare interface VCORemoteTask {
	readonly name: string;
	readonly id: string;
}

/**
 * Object representing an instance of remote objects of type Workflow.
 */
declare interface VCORemoteWorkflow {
	readonly id: string;
	readonly name: string;
	readonly description: string;
	/**
	 * Deletes the completed workflow tokens.
	 */
	deleteCompletedWorkflowTokens(): void;
}

/**
 * Object representing an instance of remote objects of type WorkflowCategory.
 */
declare interface VCORemoteWorkflowCategory {
	readonly id: string;
	readonly name: string;
}

/**
 * Object representing an instance of a remote workflow, either running or finished.
 */
declare interface VCORemoteWorkflowToken {
	readonly id: string;
	readonly name: string;
	readonly state: string;
	/**
	 * Retrieves the input parameters of the target workflow after completion.
	 */
	getInputParameters(): Properties;
	/**
	 * Retrieves the output parameters of the target workflow after completion.
	 */
	getOutputParameters(): Properties;
	/**
	 * Retrieves the exception raised while running the target workflow after the workflow has completed.
	 */
	getException(): string;
	/**
	 * Retrieves the attributes of the target workflow after completion.
	 */
	getAttributes(): Properties;
	/**
	 * Requests a cancellation of the token.
	 */
	cancel(): void;
}

/**
 * Object representing the root folder for the remote workflows.
 */
declare interface VCORemoteWorkflowsFolder {
	readonly name: string;
	readonly id: string;
}

/**
 * A configuration data object class for the Orchestrator server objects.
 */
declare interface VCOServerConfiguration {
	shared: boolean;
	password: string;
	connectionTimeout: number;
	socketTimeout: number;
	user: string;
	retryTimeout: number;
	host: string;
	port: number;
}

/**
 * Provides the core set of functions for deploying and deleting vCenter Orchestrator packages and workflows.
 */
declare class VCODeploymentManager {
	/**
	 * Deletes a package on a remote Orchestrator server.
	 * @param pckg 
	 * @param keepShared 
	 */
	static deletePackageWithContent(pckg: VCORemotePackage, keepShared: boolean): void;
	/**
	 * Deletes a package on a remote Orchestrator server by package name.
	 * @param server 
	 * @param name 
	 * @param keepShared 
	 */
	static deletePackageWithContentByName(server: VCORemoteServer, name: string, keepShared: boolean): void;
	/**
	 * Deploys a specified workflow on target servers. The workflow information is exported from the local Orchestrator server.
	 * @param server 
	 * @param source 
	 * @param path 
	 * @param override 
	 */
	static deployWorkflow(server: VCORemoteServer, source: any, path: string, override: boolean): void;
	/**
	 * Deploys a specified workflow on target servers. The workflow information is exported from a remote Orchestrator server.
	 * @param server 
	 * @param source 
	 * @param path 
	 * @param override 
	 */
	static deployRemoteWorkflow(server: VCORemoteServer, source: VCORemoteWorkflow, path: string, override: boolean): void;
	/**
	 * Deploy specified package on list of target servers.Package information is exported from local server.
	 * @param pkg 
	 * @param servers 
	 * @param override 
	 */
	static deployPackage(pkg: any, servers: VCORemoteServer[], override: boolean): VCODeploymentStatus[];
	/**
	 * Deploys a specified package on s list of target servers. The package information is exported from the provided remote package.
	 * @param pkg 
	 * @param targets 
	 * @param override 
	 */
	static deployRemotePackage(pkg: VCORemotePackage, targets: VCORemoteServer[], override: boolean): VCODeploymentStatus[];
	/**
	 * Deletes a workflow on a remote server.
	 * @param rmtWorkflow 
	 * @param force 
	 */
	static deleteWorkflow(rmtWorkflow: VCORemoteWorkflow, force: boolean): void;
}

/**
 * Provides the core set of functions for accessing information about configured remote servers.
 */
declare class VCOPlugin {
	static readonly name: string;
	static readonly id: string;
	/**
	 * Retrieves a list of accessible remote servers.
	 */
	static getAvailableRemoteServers(): VCORemoteServer[];
	/**
	 * Retrieves a remote server by host.
	 * @param host 
	 */
	static findRemoteServersByHost(host: string): VCORemoteServer[];
	/**
	 * Retrieves a remote server by name.
	 * @param name 
	 */
	static findRemoteServerByName(name: string): VCORemoteServer;
	/**
	 * Retrieves a list of configured remote servers.
	 */
	static getRemoteServers(): VCORemoteServer[];
}

/**
 * Manages the creation and execution of the proxies for the remote workflows.
 */
declare class VCOProxyWorkflowManager {
	/**
	 * Creates a workflow to make calls to a remote workflow.
	 * @param remoteWorkflowId 
	 * @param synchronous 
	 */
	static createProxy(remoteWorkflowId: string, synchronous: boolean): void;
	/**
	 * Creates a workflow to make calls to a remote workflow for all workflows available on remote server.
	 * @param server 
	 * @param synchronos 
	 */
	static createAllProxies(server: VCORemoteServer, synchronos: boolean): void;
	/**
	 * Creates a workflow to make calls to a remote workflow for all workflows under a specified remote folder.
	 * @param remotePathId 
	 * @param recursive 
	 * @param synchronos 
	 */
	static createProxies(remotePathId: string, recursive: boolean, synchronos: boolean): void;
	/**
	 * Generates a proxy action for a running remote workflow.
	 * @param actionName 
	 * @param moduleName 
	 * @param workflow 
	 */
	static createProxyActionForRemote(actionName: string, moduleName: string, workflow: VCORemoteWorkflow): void;
	/**
	 * Generates a proxy action for a running remote workflow based on the local workflow definition.
	 * @param actionName 
	 * @param moduleName 
	 * @param workflow 
	 */
	static createProxyActionForLocal(actionName: string, moduleName: string, workflow: any): void;
	/**
	 * Process the workflow runs in waiting-state to reflect the remote workflow state.
	 * @param remoteServer 
	 */
	static processStaleProxyWorkflowExecutions(remoteServer: VCORemoteServer): void;
	/**
	 * Starts an asynchronous run of the remote workflow. Validates that the remote workflow and the parameters are from the same remote Orchestrator server, and ensures that the arguments are of the correct type.
	 * @param connectionId 
	 * @param workflowId 
	 * @param parameters 
	 */
	static executeAsynchronousProxy(connectionId: string, workflowId: string, parameters: any): VCORemoteWorkflowToken;
	/**
	 * Used for running a synchronous workflow proxies.Starts a synchronous run of the remote workflow. Validates that the remote workflow and the parameters are from the same remote Orchestrator server, and ensures that the arguments are of the correct type.
	 * @param connectionId 
	 * @param workflowId 
	 * @param parameters 
	 */
	static executeSynchronousProxy(connectionId: string, workflowId: string, parameters: any): Properties;
	/**
	 * Used for running an asynchronous workflow proxies. Starts an asynchronous run of the remote workflow. Validates that the remote workflow and the parameters are from the same remote Orchestrator server, and ensures that the arguments are of the correct type.
	 * @param workflowId 
	 * @param params 
	 */
	static executeAsynchronousProxies(workflowId: string, params: any): VCORemoteWorkflowToken[];
	/**
	 * Deletes the proxy workflows generated for a remote server.
	 * @param server 
	 * @param alsoFolders 
	 */
	static deleteProxies(server: VCORemoteServer, alsoFolders: boolean): void;
}

/**
 * Provides the core set of functions for adding,updating, and deleting remote Orchestrator servers.
 */
declare class VCOServerManager {
	/**
	 * Adds a remote Orchestrator server.
	 * @param config 
	 */
	static addOrchestrator(config: VCOServerConfiguration): VCORemoteServer;
	/**
	 * Updates a remote Orchestrator server.
	 * @param src 
	 * @param config 
	 */
	static updateOrchestrator(src: VCORemoteServer, config: VCOServerConfiguration): VCORemoteServer;
	/**
	 * Updates a remote Orchestrator server.
	 * @param src 
	 * @param host 
	 * @param port 
	 * @param isShared 
	 * @param user 
	 * @param password 
	 * @param connectionTimeout 
	 * @param socketTimeout 
	 * @param retryTimeout 
	 * @param ssoEnabled 
	 * @param ssoScheme 
	 * @param ssoHost 
	 * @param ssoPort 
	 * @param ssoSameAsVco 
	 */
	static updateServer(src: VCORemoteServer, host: string, port: number, isShared: boolean, user: string, password: string, connectionTimeout: number, socketTimeout: number, retryTimeout: number, ssoEnabled: boolean, ssoScheme: string, ssoHost: string, ssoPort: number, ssoSameAsVco: boolean): VCORemoteServer;
	/**
	 * Deletes a remote Orchestrator server.
	 * @param srv 
	 */
	static deleteOrchestrator(srv: VCORemoteServer): void;
	/**
	 * Deletes a remote Orchestrator server.
	 * @param srv 
	 */
	static deleteServer(srv: VCORemoteServer): void;
	/**
	 * Adds a remote Orchestrator server.
	 * @param host 
	 * @param port 
	 * @param isShared 
	 * @param user 
	 * @param password 
	 * @param connectionTimeout 
	 * @param socketTimeout 
	 * @param retryTimeout 
	 * @param ssoEnabled 
	 * @param ssoScheme 
	 * @param ssoHost 
	 * @param ssoPort 
	 * @param ssoSameAsVco 
	 */
	static addServer(host: string, port: number, isShared: boolean, user: string, password: string, connectionTimeout: number, socketTimeout: number, retryTimeout: number, ssoEnabled: boolean, ssoScheme: string, ssoHost: string, ssoPort: number, ssoSameAsVco: boolean): VCORemoteServer;
}
