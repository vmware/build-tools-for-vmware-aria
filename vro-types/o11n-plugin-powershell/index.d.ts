/**
 * Enumeration for the supported authorization modes. The modes are "Shared Session" and "Session per User".
 */
declare interface PowerShellAuthorizationMode {
	/**
	 * Converts a string to an instance of this class. If String is not possible to be converted an IllegalArgumentException is thrown.
	 * @param text 
	 */
	fromString(text: string): PowerShellAuthorizationMode;
}

/**
 * Deprecated. Use AuthorizationModeinstead.<br/>Enumeration for the supported authorization modes. The modes are "Shared Session" and "Session per User".
 */
declare interface PowerShellAutorizationMode {
	/**
	 * Converts a string to an instance of this class. If String is not possible to be converted an IllegalArgumentException is thrown.
	 * @param text 
	 */
	fromString(text: string): PowerShellAutorizationMode;
	/**
	 * Converts a string to an instance of AuthorizationMode class. If String is not possible to be converted an IllegalArgumentException is thrown.
	 * @param mode 
	 */
	getAuthorizationMode(mode: PowerShellAutorizationMode): PowerShellAuthorizationMode;
}

/**
 * Wrapper of a PowerShell cmdlet that resides on the remote Windows machine
 */
declare interface PowerShellCmdlet {
	readonly name: string;
	readonly definition: string;
	readonly commandType: string;
	readonly psSnapin: string;
}

/**
 * Represents a PowerShell command that can be run on the remote PowerShell host.
 */
declare class PowerShellCommand {
	constructor()
	/**
	 * Creates a PowerShellCommand instance that encapsulates the command.
	 * @param command 
	 */
	constructor(command: string)
	/**
	 * Returns the String that this Command object was initialy created.
	 */
	getCommand(): string;
	/**
	 * Returns all Parameters as a Map.
	 */
	getParams(): any;
	/**
	 * Adds a new parameter to the list of params for this PowerShellCommand instance.
	 * @param paramName 
	 * @param value 
	 */
	addParameter(paramName: string, value: any): PowerShellCommand;
}

/**
 * Advanced configuration parameters.
 */
declare interface PowerShellConfigurationParameters {
	shellCodePage: PowerShellShellCodePage;
}

/**
 * Represents a remote host with PowerShell installed on it.
 */
declare interface PowerShellHost {
	readonly name: string;
	readonly id: string;
	readonly type: string;
	readonly port: string;
	readonly transportProtocol: string;
	readonly connectionURL: string;
	readonly username: string;
	/**
	 * Closes the session with the given sessionId
	 * @param sessionId 
	 */
	closeSession(sessionId: string): void;
	/**
	 * Returns the PowerShellHostConfig instance for this PowerShellHost
	 */
	getHostConfig(): PowerShellHostConfig;
	/**
	 * Opens a new PowerShellSession, which can be used to run PowerShell scrits and cmdlets
	 */
	openSession(): PowerShellSession;
	/**
	 * Opens a new PowerShellSession with provided credentials, which can be used to run PowerShell scrits and cmdlets
	 * @param name 
	 * @param password 
	 */
	openSessionAs(name: string, password: string): PowerShellSession;
	/**
	 * Invokes powershell script without adding it to pipeline.
	 * @param script 
	 */
	invokeScript(script: string): PowerShellInvocationResult;
	/**
	 * Returns the inPipeline flag of the session identified by sessionId
	 * @param sessionId 
	 */
	inPipeline(sessionId: string): boolean;
	/**
	 * Starts a pipeline in the context of the session identified by sessionId
	 * @param sessionId 
	 */
	startPipeline(sessionId: string): void;
	/**
	 * Executes the pipeline associated with the session with id==sessionId and clears the pipeline, removing all commands from it.
	 * @param sessionId 
	 */
	endPipeline(sessionId: string): void;
	/**
	 * Returns Session for the given sessionId. Exception is thrown if session does not exist or has been closed.
	 * @param sessionId 
	 */
	getSession(sessionId: string): PowerShellSession;
}

/**
 * Encapsulates the configuration for a remote PowerShell host
 */
declare interface PowerShellHostConfig {
	name: string;
	type: string;
	port: string;
	configurationParameters: PowerShellConfigurationParameters;
	transportProtocol: string;
	authorizationMode: PowerShellAuthorizationMode;
	autorizationMode: PowerShellAutorizationMode;
	connectionURL: string;
	username: string;
	password: string;
	authentication: string;
}

/**
 * Represents the result from an execution fo PowerShell script
 */
declare interface PowerShellInvocationResult {
	/**
	 * Returns list of errors reported by powershell engine during script invocation.
	 */
	getErrors(): string[];
	/**
	 * Status of execution of the script. Possible values are (Completed, Failed).
	 */
	getInvocationState(): any;
	/**
	 * Returns output of script execution as it appears on the powershell console.
	 */
	getHostOutput(): string;
	/**
	 * Returns list of objects returned by PowerShell engine after successfull invocation.
	 */
	getResults(): PowerShellRemotePSObject;
}

/**
 * Represents an object on the remote PowerShell machine
 */
declare interface PowerShellPSObject {
	/**
	 * Returns value of specific object property. Tha returned value itself can be of primitive type, ArrayList, Hashtable or another PowerShellPSObject
	 * @param propName 
	 */
	getProperty(propName: string): any;
	/**
	 * Returns an array containing Types representing all the public classes and interfaces that object implements
	 */
	getTypes(): string[];
	/**
	 * Returns the object converted to String.
	 */
	getToString(): string;
	/**
	 * Returns calue of the provided property converting it to string.
	 * @param propName 
	 */
	getPropertyAsString(propName: string): string;
	/**
	 * Returns the value of propName property.
	 * @param propName 
	 */
	getPropertyAsPSObject(propName: string): PowerShellPSObject;
	/**
	 * Returns the value of propName property as a list of PowerShellPSObject
	 * @param propName 
	 */
	getPropertyAsPSObjectList(propName: string): any[];
	/**
	 * Checks if this instance is of specific type
	 * @param type 
	 */
	instanceOf(type: string): boolean;
}

/**
 * Wrapper for the result from an execution fo PowerShell script
 */
declare interface PowerShellRemotePSObject {
	/**
	 * Returns result of PowerShell script invocation converted in corresponding vCO type. The result can be simple type, ArrayList, Properties or PowerShellPSObject
	 */
	getRootObject(): any;
	/**
	 * Returns the object as String that is JSON formated
	 */
	getAsJson(): string;
	/**
	 * Returns the object as String that is XML formated
	 */
	getXml(): string;
}

/**
 * Represents a session to the remote PowerShell machine, that can be used to run scripts and Cmdlets. Also users can benefit from the pipeline concept by demarcating pipeline boundaries with startPipeline() and endPipeline()
 */
declare interface PowerShellSession {
	/**
	 * Returns the session id
	 */
	getSessionId(): string;
	/**
	 * Invokes powershell script without adding it to pipeline.
	 * @param script 
	 */
	invokeScript(script: string): PowerShellInvocationResult;
	/**
	 * Invokes commands currently in pipeline, and remove them from the pipeline.
	 */
	invokePipeline(): PowerShellInvocationResult;
	/**
	 * Adds command to pipeline
	 * @param command 
	 */
	addCommand(command: PowerShellCommand): void;
	/**
	 * Creates command from provided string and adds it to pipeline.
	 * @param command 
	 */
	addCommandFromString(command: string): PowerShellCommand;
}

/**
 * Enumeration for the supported shell code encodings.
 */
declare interface PowerShellShellCodePage {
	/**
	 * Converts a string to an instance of this class. If String is not possible to be converted an IllegalArgumentException is thrown.
	 * @param text 
	 */
	fromString(text: string): PowerShellShellCodePage;
}

/**
 * PowerShell SnapIn wrapper object
 */
declare interface PowerShellSnapIn {
	readonly version: string;
	readonly name: string;
	readonly moduleName: string;
	readonly description: string;
}

/**
 * The root of the SnapIns
 */
declare interface PowerShellSnapInRoot {
	readonly name: string;
}

/**
 * Generates actions for PowerShell Cmdlets and Scripts
 */
declare class PowerShellActionGenerator {
	/**
	 * Generates vCO action for provided PowerShell script.
	 * @param actionName 
	 * @param script 
	 * @param moduleName 
	 * @param generateWorkflow 
	 * @param targetFolder 
	 */
	static createActionForScript(actionName: string, script: string, moduleName: string, generateWorkflow: boolean, targetFolder: any): void;
}

/**
 * Manager for configuring the PowerShell plug-in hosts
 */
declare class PowerShellHostManager {
	/**
	 * Removes the PowerShellHostConfig with the passed in as argument id from the configuration of the plugin.
	 * @param id 
	 */
	static remove(id: string): void;
	/**
	 * Creates a PowerShellHost for the passed in as argument PowerShellHostConfig if it doesn't already exist. In case it already exists the PowerShellHost is updated.
	 * @param hostConfig 
	 */
	static update(hostConfig: PowerShellHostConfig): PowerShellHost;
	/**
	 * Checks if the provided hostConfig parameter contains settings that the plugin can use to successfully connect to a remote PowerShell machine. In case the configurations are not valid the method throws appropriate exception.
	 * @param hostConfig 
	 */
	static validatePowerShellHost(hostConfig: PowerShellHostConfig): void;
}
