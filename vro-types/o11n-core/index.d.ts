declare type SecureString = string;

declare module java.util {
	type Map = any;
	type Collection = any;
	type Date = any;
	type UUID = any;
}

declare module java.lang {
	type Iterable = any;
}

declare module java.io {
	type Serializable = any;
}

declare module org.springframework.data.domain {
	type Sort$Order = any;
}

declare interface Trigger {
}

/**
 * Object representing the current instance of a Workflow or undefined if not in a workflow.
 */
declare var workflow: WorkflowToken | undefined;

/**
 * Object used to call custom action in the system.
 */
declare interface Module {
	actionDescriptions: Action[];
	name: string;
	description: string;
}

/**
 * Object containing parameters visible throughtout the workflow run and all sub workflow runs triggered byt it.
 */
declare interface ExecutionContext {
	/**
	 * Return a parameter by its name.
	 * @param name
	 */
	getParameter(name: string): any;
	/**
	 * Check if a parameter with certain name is added to the execution context.
	 * @param name
	 */
	contains(name: string): boolean;
	/**
	 * Return an array containing the names of all parameters in the execution context.
	 */
	parameterNames(): any[];
}

/**
 * JavaScript Date Object.
 */
declare interface Date {
	/**
	 * Get the day of the month. It is returned as a value between 1 and 31.
	 */
	getDate(): number;
	/**
	 * Set the day of the month in the date object as a value from 1 to 31.
	 * @param day
	 */
	setDate(day: number): void;
	/**
	 * Get the day of the week as a value from 0 to 6
	 */
	getDay(): number;
	/**
	 * The value returned is 0 through 23.
	 */
	getHours(): number;
	/**
	 * Set the hours in the date object with a value of 0 through 23.
	 * @param hours
	 */
	setHours(hours: number): void;
	/**
	 * The value returned is 0 through 59.
	 */
	getMinutes(): number;
	/**
	 * Set the minutes in the date object with a value of 0 through 59.
	 * @param minutes
	 */
	setMinutes(minutes: number): void;
	/**
	 * Returns the month from the date object as a value from 0 through 11.
	 */
	getMonth(): number;
	/**
	 * Set the month in the date object as a value of 0 through 11.
	 * @param month
	 */
	setMonth(month: number): void;
	/**
	 * The value returned is 0 through 59.
	 */
	getSeconds(): number;
	/**
	 * Set the seconds in the date object with a value of 0 through 59.
	 * @param seconds
	 */
	setSeconds(seconds: number): void;
	/**
	 * Returns the numeric digit value of the year (fullYear-1900).
	 */
	getYear(): number;
	/**
	 * Returns the numeric four digit value of the year.
	 */
	getFullYear(): number;
	/**
	 * Set the year in the date instance as a 4 digit numeric value.
	 * @param years
	 */
	setYear(years: number): void;
	/**
	 * The number of milliseconds since January 1, 1970. this function allows you to manipulate the date object based on a millisecond value then convert it back to the form you want.
	 */
	getTime(): number;
	/**
	 * Sets time on the basis of number of milliseconds since January 1, 1970. The below example sets the date object to one hour in the future.
	 * @param time
	 */
	setTime(time: number): void;
	/**
	 * Time zone offset in minutes which is the difference between GMT and local time.
	 */
	getTimezoneOffset(): number;
	/**
	 * The number of milliseconds after midnight January 1, 1970 till the given date espressed as a string in the example which is IETF format.
	 */
	parse(): Date;
	/**
	 * Convert date to GMT format in a form similar to "Fri, 29 Sep 2000 06:23:54 GMT".
	 */
	toGMTString(): string;
	/**
	 * Based on a comma delimited string, the number of milliseconds after midnight January 1, 1970 GMT is returned. The syntax of the string is "year, month, day [, hrs]
	 * [, min] [, sec]
	 * ". An example is "2000, 9, 29, 5, 43, 0" for Sept 29, 2000 at 5:43:0. The string is considered to be GMT. The hours, minutes, and seconds are optional.
	 */
	UTC(): Date;
}

/**
 * Represents an action in the system
 */
declare class Action {
	name: string;
	description: string;
	version: string;
	returnType: string;
	script: string;
	versionHistoryItems: VersionHistoryItem[];
	parameters: Parameter[];
	module: Module;
}

/**
 * Object representing a instance of a result of action executed asynchronously
 */
declare class ActionResult {
	/**
	 * Returns the actual result returned by the asynchronous executed action. Blocks until the result is available.
	 */
	getResult(): any;
	/**
	 * Attempts to cancel the execution of the asynchronously executed action.
	 * @param mayInterruptIfRunning
	 */
	cancel(mayInterruptIfRunning: boolean): boolean;
	/**
	 * Returns true if the asynchronous action has completed.
	 */
	isDone(): boolean;
}

/**
 * Describe an attribute
 */
declare interface Attribute {
	name: string;
	description: string;
	type: string;
	value: any;
}

/**
 * AuthorizationElement JavaScript object
 */
declare interface AuthorizationElement {
	name: string;
	description: string;
	ldapElementDn: string;
	ldapElement: LdapGroup;
	status: string;
	references: AuthorizationReference[];
	/**
	 * add an authorization reference on any inventory object and return it
	 * @param object
	 * @param relation
	 */
	addReference(object: any, relation: any): AuthorizationReference;
	/**
	 * remove all reference in this authorization element
	 */
	removeAllReferences(): void;
	/**
	 * remove all reference for a given object and relation.
	 * @param jsObject
	 * @param relation
	 */
	removeReference(jsObject: any, relation: any): void;
}

/**
 * AuthorizationReference JavaScript object
 */
declare interface AuthorizationReference {
	relationName: string;
	returnType: string;
	type: string;
	value: any;
	authorizedObjects: any;
	stringRepresentation: string;
}

/**
 * Executes a command in the host operating system. <p>Commands are dependent on the host operating system default shell.</p> <p>Example: com = new Command("ls"); com.execute(true); // makes the call blocking output = com.getOutput();</p>
 */
declare interface Command {
	output: string;
	result: number;
	input: string;
	/**
	 * Executes the command. <p>Standard output is redirected to the <b>output</b> attribute.</p>
	 * @param wait
	 */
	execute(wait?: any): number;
	/**
	 * Executes the command and logs the standard output to a file.
	 * @param filename
	 */
	executeAndLog(filename: string): number;
}

/**
 * ConfigurationElement
 */
declare interface ConfigurationElement {
	name: string;
	description: string;
	version: string;
	versionHistoryItems: VersionHistoryItem[];
	attributes: Attribute[];
	configurationElementCategory: any;
	/**
	 * Returns the attribute of the configuration element for the specified key or null if not found.
	 * @param key
	 */
	getAttributeWithKey(key: string): Attribute | null;
	/**
	 * Sets the attribute value of the configuration element for the specified key.
	 * @param key
	 * @param value
	 */
	setAttributeWithKey(key: string, value: any, typeHint?: any): void;
	/**
	 * Reloads the values of the attributes of this configuration element.
	 */
	reload(): void;
	/**
	 * Remove the attribute of the configuration element for the specified key.
	 * @param key
	 */
	removeAttributeWithKey(key: string): void;
	/**
	 * Saves a change set in the local version repository.
	 * This function is available with vRA 8
	 */
	saveToVersionRepository(): void;
}

/**
 * ConfigurationElementCategory
 */
declare interface ConfigurationElementCategory {
	name: string;
	description: string;
	path: string;
	configurationElements: ConfigurationElement[];
	subCategories: ConfigurationElementCategory[];
	parent: ConfigurationElementCategory;
	allConfigurationElements: ConfigurationElement[];
}

/**
 * Credential object (username / password pair)
 */
declare interface Credential {
	username: string;
	password: string;
	/**
	 * @param otherPassword
	 */
	checkPassword(otherPassword: string): boolean;
}

declare interface Enumeration {
	next: any;
	hasNext: boolean;
}

/**
 * Event generated by an 'OnInit'/'OnExit' event of a Policy item.
 */
declare interface Event {
	source: any;
	when: number;
	/**
	 * @param owner
	 */
	consume(owner: string): void;
}

/**
 * Event generated by an 'OnGauge.xxx' event of a Policy item.
 */
declare interface EventGauge {
	source: any;
	when: number;
	perfKey: string;
	value: number;
	device: string;
	/**
	 * @param owner
	 */
	consume(owner: string): void;
}

/**
 * Event generated by an 'OnExecute' event of a periodic Policy item.
 */
declare interface EventSchedule {
	source: any;
	when: number;
	lastExec: number;
	nextExecutionDate: any;
	nextExec: number;
	lastExecutionDate: any;
	/**
	 * @param owner
	 */
	consume(owner: string): void;
}

/**
 * Representation of a file on a managed computer.
 */
declare class File {
	hostname: string;
	path: string;
	isLocal: boolean;
	exists: boolean;
	length: number;
	directory: string;
	readonly name: string;
	extension: string;
	isDir: boolean;
	constructor(file: string);
	/**
	 * Writes the content to the file. If the file does not exist, it is created.
	 * @param content
	 */
	write(content: string): void;
	/**
	 * Renames the file denoted by this abstract pathname. Many aspects of the behavior of this method are inherently platform-dependent: The rename operation might not be able to move a file from one filesystem to another, it might not be atomic, and it might not succeed if a file with the destination abstract pathname already exists. The return value should always be checked to make sure that the rename operation was successful.
	 * @param destPathName
	 */
	renameTo(destPathName: string): boolean;
	/**
	 * Creates the file if it does not exist. Also creates its directory if needed.
	 */
	createFile(): void;
	/**
	 * Creates the directory structure if it does not exist.
	 */
	createDirectory(): void;
	/**
	 * Deletes the file from the file system.
	 */
	deleteFile(): void;
	/**
	 * List files / directories that end with the specified extension.
	 * @param extension
	 */
	list(extension: any): string[];
	/**
	 * Can read this file
	 */
	canRead(): boolean;
	/**
	 * Can write this file
	 */
	canWrite(): boolean;
}

/**
 * FileHelper wrapper
 */
declare interface FileHelper {
}

/**
 * FileReader
 */
declare class FileReader {
	exists: boolean;
	constructor(file: File);
	/**
	 * Opens the file for reading. <p>Once open an index in the file is maintained. This means you can do succesive <b>readLine</b>.</p>
	 */
	open(): void;
	/**
	 * Reads one line from the opened file. <p>To go back to the start of the file, <b>close</b> it and re-<b>open</b> it.</p>
	 */
	readLine(): string;
	/**
	 * Reads all lines from the opened file.
	 */
	readAll(): string;
}

/**
 * FileWriter
 */
declare class FileWriter {
	lineEndType: number;
	exists: boolean;
	constructor(file: File);
	/**
	 * Opens the file for writing. From this point on the file is locked. Use <b>close</b> to release it.
	 */
	open(): void;
	/**
	 * Reinitializes the length to 0 and sets the file-pointer in the very begining of the file.
	 */
	clean(): void;
	/**
	 * Writes a line to the file. The file must be opened first using <b>open</b>.
	 * @param value
	 */
	writeLine(value: string): void;
	/**
	 * Writes a string to the file. The file must be opened first using <b>open</b>.
	 * @param value
	 */
	write(value: string): void;
	/**
	 * Closes a previously opened file.
	 */
	close(): void;
}

/**
 * LogFileWriter
 */
declare interface LogFileWriter {
	lineEndType: number;
	exists: boolean;
	/**
	 * Opens the file for writing. From this point on the file is locked. Use <b>close</b> to release it.
	 */
	open(): void;
	/**
	 * Reinitializes the length to 0 and sets the file-pointer in the very begining of the file.
	 */
	clean(): void;
	/**
	 * Writes a line to the file. The file must be opened first using <b>open</b>.
	 * @param value
	 */
	writeLine(value: string): void;
	/**
	 * Writes a string to the file. The file must be opened first using <b>open</b>.
	 * @param value
	 */
	write(value: string): void;
	/**
	 * Closes a previously opened file.
	 */
	close(): void;
}

/**
 * LdapUser object
 */
declare interface LdapUser {
	commonName: string;
	userPrincipalName: string;
	displayName: string;
	loginName: string;
	dn: string;
	displayInfo: string;
	groups: LdapGroup[];
	emailAddress: string;
	allGroups: LdapGroup[];
	/**
	 * @param ldapGroup
	 */
	isMemberOfGroup(ldapGroup: any): boolean;
}

/**
 * LdapGroup object
 */
declare interface LdapGroup {
	displayName: string;
	commonName: string;
	displayInfo: string;
	dn: string;
	parentGroups: LdapGroup[];
	subGroups: LdapGroup[];
	users: LdapUser[];
	emailAddress: string;
}

/**
 * Object representing a log event
 */
declare interface LogEvent {
	severity: number;
	logTimeStamp: any;
	shortDescription: string;
	longDescription: string;
	originatorUri: string;
	originatorUserName: string;
	originatorId: string;
	id: string;
}

/**
 * Object representing a log query
 */
declare interface LogQuery {
	fetchLimit: number;
	fromDate: any;
	toDate: any;
	originatorId: string;
	targetUri: string;
	severityThreshold: number;
}

/**
 * Describe a mime attachment.
 */
declare class MimeAttachment {
	/**
	 * You can give a file or a file name as parameter when you instantiate a new MimeAttachment
	 * Example:
	 * 		var att = new MimeAttachment("script.txt") will create instance where
	 * 	    att.name == "script.txt" and att.mimeType == "text/plain"
	 * @param obj - file or file name, if is a string, this will be MimeAttachment file name
	 */
	constructor(obj?: string | any);
	name: string;
	mimeType: string;
	content: string;
	buffer: ByteBuffer;
	/**
	 * Write the content of the mime attachment to file.
	 * @param directory
	 * @param filename
	 */
	write(directory: any, filename: any): void;
}

/**
 * Representation of a not found object (doesn't exist anymore or currently unavailable)
 */
declare interface NotFound {
	originalStringRepresentation: string;
	originalType: string;
}

/**
 * Representation of a package
 */
declare interface Package {
	name: string;
	description: string;
	policyTemplates: PolicyTemplate[];
	actions: Action[];
	workflows: Workflow[];
	resourceElements: ResourceElement[];
	configurationElements: ConfigurationElement[];
	version: string;
	id: string;
	/**
	 * Remove the package
	 */
	remove(): void;
}

/**
 * Describe a parameter
 */
declare interface Parameter {
	name: string;
	description: string;
	type: string;
}

/**
 * Description of a plug-in module
 */
declare interface PluginModuleDescription {
	name: string;
	description: string;
	version: string;
	types: any[];
}

/**
 * Description of a plug-in type
 */
declare interface PluginTypeDescription {
	name: string;
	kind: string;
	type: string;
	description: string;
}

/**
 * Representation of a policy
 */
declare interface Policy {
	name: string;
	description: string;
	taggedObjects: any[];
	tags: string[];
	owner: any;
	status: string;
	autostart: boolean;
	currentVersion: string;
	credential: any;
	logEvents: LogEvent[];
	/**
	 * Returns the object referenced by 'tag' in the policy (Virtual machine or Hosts)
	 * @param value
	 */
	getObjectByTag(value: string): any;
	/**
	 * Returns the object referenced by 'tag' in the policy (Virtual machine or Hosts)
	 * @param value
	 */
	forTag(value: string): any;
	/**
	 * Exits the policy immediatlly.
	 * @param reason
	 */
	exit(reason: string): void;
	/**
	 * Returns all tags and their corresponding events.
	 */
	getEventsByTag(): Properties[];
	/**
	 * Start the policy
	 */
	start(): void;
	/**
	 * Stop the policy
	 * @param reason
	 */
	stop(reason: string): void;
	/**
	 * Remove the policy
	 */
	remove(): void;
}

/**
 * Representation of a policy template
 */
declare interface PolicyTemplate {
	name: string;
	description: string;
	version: string;
	versionHistoryItems: VersionHistoryItem[];
	currentVersion: string;
	policyTemplateCategory: any;
	id: string;
	/**
	 * Apply the policy template and create a new policy with it.
	 * @param name
	 * @param properties
	 */
	apply(name: string, properties: any): any;
	/**
	 * Returns the object referenced by 'tag' in the policy (Virtual machine or Hosts)
	 * @param value
	 */
	getObjectByTag(value: string): any;
	/**
	 * Returns the object referenced by 'tag' in the policy (Virtual machine or Hosts)
	 * @param value
	 */
	forTag(value: string): any;
}

/**
 * Representation of a policy template category
 */
declare interface PolicyTemplateCategory {
	name: string;
	description: string;
	path: string;
	policyTemplates: PolicyTemplate[];
	subCategories: PolicyTemplateCategory[];
	parent: PolicyTemplateCategory;
	allPolicyTemplates: PolicyTemplate[];
}

declare const Properties: PropertiesConstructor;

declare interface PropertiesConstructor {
	new(value?: any): Properties;
}

/**
 * Properies list. A list of key - values.
 */
declare interface Properties {
	keys: string[];
	/**
	 * Adds a property to the property list.
	 * @param key
	 * @param value
	 */
	put(key: string, value: any): void;
	/**
	 * Remove a property to the property list.
	 * @param key
	 */
	remove(key: string): void;
	/**
	 * Returns the property entry for the given key or null if not found.
	 * @param key
	 */
	get<T>(key: string): T | null;
	/**
	 * Loads properties from a File, an URL or a String object
	 * @param input
	 */
	load(input: any): void;
	[name: string]: any;
}

/**
 * QueryResult is a result for a plugin query
 */
declare interface QueryResult {
	elements: any;
	totalCount: number;
	partial: boolean;
}

/**
 * ResourceElement
 */
declare interface ResourceElement {
	name: string;
	description: string;
	version: string;
	mimeType: string;
	contentSize: number;
	versionHistoryItems: VersionHistoryItem[];
	/**
	 * Writes the content to a file.
	 * @param fileName
	 */
	writeContentToFile(fileName: string): void;
	/**
	 * Returns the content converted to a MimeAttachment.
	 */
	getContentAsMimeAttachment(): MimeAttachment;
	/**
	 * Returns this element's parent category
	 */
	getResourceElementCategory(): ResourceElementCategory;
	/**
	 * Reload resource element
	 */
	reload(): void;
	/**
	 * Sets the content of this resource element.
	 * @param fileName
	 * @param mimeType
	 */
	setContentFromFile(fileName: string, mimeType: any): void;
	/**
	 * Sets the content of this resource element.
	 * @param mimeAttachment
	 */
	setContentFromMimeAttachment(mimeAttachment: any): void;
	/**
	 * Saves a change set in the local version repository.
	 * This function is available with vRA 8
	 */
	saveToVersionRepository(): void;
}

/**
 * ResourceElementCategory
 */
declare interface ResourceElementCategory {
	name: string;
	description: string;
	path: string;
	resourceElements: ResourceElement[];
	subCategories: ResourceElementCategory[];
	parent: ResourceElementCategory;
	allResourceElements: ResourceElement[];
}

/**
 * Representation of a Custom object result comming from a sdk module.
 */
declare interface SDKObject {
	type: string;
	id: string;
}

/**
 * Used to convert any 'Seriallizable' object in a String representation that can be stored.
 */
declare interface StringRepresentation {
	type: string;
	stringValue: string;
	objectValue: any;
	/**
	 * Returns the urlParameter for this object representation (used to send object through URL)
	 */
	toParam(): string;
}

/**
 * Task
 */
declare interface Task {
	state: string;
	operation: string;
	percentCompleted: number;
	error: string;
	executionDate: Date;
	parameters: Properties;
	restartInPast: boolean;
	isRecurrent: boolean;
	name: string;
	workflow: Workflow;
	executions: WorkflowToken[];
	id: string;
	/**
	 * Cancels the task.
	 */
	cancel(): void;
	/**
	 * Suspends the task.
	 */
	suspend(): void;
	/**
	 * Resumes the task.
	 */
	resume(): void;
	/**
	 * Sets the execution parameters.
	 * @param key
	 * @param value
	 */
	addParameter(key: string, value: any): void;
}

/**
 * Object representing a tag query.
 */
declare interface TagQuery {
	/**
	 * Add new has tag conditions to query.
	 * @param tag
	 * @param value
	 */
	hasTag(tag: string, value: string): void;
	/**
	 * Add new has global tag conditions to query.
	 * @param tag
	 * @param value
	 */
	hasGlobalTag(tag: string, value: string): void;
}

/**
 * Usable object to put/get Http requests
 */
interface URL {
	host: string;
	port: string;
	url: string;
	datas: string;
	requestType: string;
	contentType: string;
	result: string;
	/**
	 * Adds a parameter at the end of the URL.
	 * @param key
	 * @param value
	 */
	addParameter(key: any, value: any): void;
	/**
	 * Adds a path to the URL.
	 * @param value
	 */
	addPath(value: any): void;
	/**
	 * Returns the content of the URL.
	 */
	getContent(): string;
	/**
	 * Posts the content to the URL.
	 * @param content
	 */
	postContent(content: any): string;
	/**
	 * Posts the content defined in the datas property to the URL.
	 */
	post(): string;
	/**
	 * Return the regex pattern string that matches any valid host name or IP address.
	 */
	getHostnameOrIPPatternStr(): string;
	/**
	 * Return the regex pattern string that matches any valid host name.
	 */
	tnamePatternStr(): string;
	getIPAddressPatternStr(): string;
	/**
	 * Return the regex pattern string that matches any valid IPv4 address.
	 */
	getIPv4AddressPatternStr(): string;
	/**
	 * Return the regex pattern string that matches any valid IPv6 address.
	 */
	getIPv6AddressPatternStr(): string;
	/**
	 * Checks if the parameter is valid host name. Example use: <code><pre> new URL().isValidHostname(hostname) </pre></code>
	 * @param hostname
	 */
	isValidHostname(hostname: string): boolean;
	/**
	 * Checks if the parameter is valid host name or IP address (both IPv4 and IPv6). Example use: <code><pre> new URL().isHostnameOrIPAddress(hostOrIP) </pre></code>
	 * @param hostOrIP
	 */
	isValidHostnameOrIPAddress(hostOrIP: string): boolean;
	/**
	 * Checks if the parameter is valid IP address (both IPv4 and IPv6). Example use: <code><pre> new URL().isValidIPAddress(address) </pre></code>
	 * @param ipAddress
	 */
	isValidIPAddress(ipAddress: string): boolean;
	/**
	 * Checks if the parameter is valid IPv4 address. Example use: <code><pre> new URL().isValidIPv4Address(address) </pre></code>
	 * @param ipAddress
	 */
	isValidIPv4Address(ipAddress: string): boolean;
	/**
	 * Checks if the parameter is valid IPv6 address. Example use: <code><pre> new URL().isValidIPv6Address(address) </pre></code>
	 * @param ipAddress
	 */
	isValidIPv6Address(ipAddress: string): boolean;
	/**
	 * Escapes the host if this is needed. Usually you need to escape IPv6 numeric addresses using brackets. If this host does not need to be escaped then the original is returned.
	 * @param ipAddress
	 */
	escapeHost(ipAddress: string): string;
	/**
	 * Un-escapes the host if this is IPv6 address in brackets. Usually you need to escape IPv6 numeric addresses using brackets but some times you need the plain IPv6 address. If this is not escaped IPv6 address the original is returned.
	 * @param ipAddress
	 */
	unescapeHost(ipAddress: string): string;
}

/**
 * VersionHistoryItem
 */
declare interface VersionHistoryItem {
	comment: string;
	date: any;
	version: string;
	user: string;
}

/**
 * Workflow
 */
declare class Workflow {
	name: string;
	description: string;
	workflowCategory: WorkflowCategory;
	version: string;
	firstItem: any;
	numberOfItem: number;
	items: WorkflowItem[];
	inParameters: Parameter[];
	outParameters: Parameter[];
	attributes: Attribute[];
	versionHistoryItems: VersionHistoryItem[];
	parameterInfos: Properties;
	executions: WorkflowToken[];
	logEvents: LogEvent[];
	/**
	 * Save the picture of the workflow in .png format.
	 * @param file
	 */
	saveSchemaImageToFile(file: string): void;
	/**
	 * Executes the workflow.
	 * @param properties
	 * @param uname
	 * @param pwd
	 */
	execute(properties: any, uname?: any, pwd?: string): WorkflowToken;
	/**
	 * Schedules recurrently the workflow.
	 * @param properties
	 * @param recurrencePattern
	 * @param recurrenceCycle
	 * @param startDate
	 * @param endDate
	 * @param uname
	 * @param pwd
	 */
	scheduleRecurrently(properties: any, recurrencePattern: any, recurrenceCycle: any, startDate: any, endDate: any, uname: any, pwd: string): Task;
	/**
	 * Schedules the workflow.
	 * @param properties
	 * @param startDate
	 * @param uname
	 * @param pwd
	 */
	schedule(properties: any, startDate: any, uname: any, pwd: string): Task;
	/**
	 * Generates a URL usable for interacting with workflows in VS-O or its web front end version 3.0.x.
	 * @param secure
	 * @param inAutologon
	 * @param inHost
	 * @param inPort
	 * @param inUrl
	 */
	getExecutionUrl(secure: any, inAutologon: any, inHost: any, inPort: any, inUrl: any): any;
	/**
	 * Generates a URL usable for interacting with workflows in VS-O or its web front end.
	 * @param inWebviewUrlFolder
	 * @param inPage
	 * @param secure
	 * @param inAutologon
	 * @param inHost
	 * @param inPort
	 * @param inUrl
	 */
	getWebviewExecutionUrl(inWebviewUrlFolder: any, inPage: any, secure: any, inAutologon: any, inHost: any, inPort: any, inUrl: any): any;
	/**
	 * Creates a URL usable to access Web-Operator "Schedule Workflow operation" page (version 3.0.x).
	 * @param secure
	 * @param inAutologon
	 * @param inHost
	 * @param inPort
	 * @param inUrl
	 */
	getScheduleUrl(secure: any, inAutologon: any, inHost: any, inPort: any, inUrl: any): any;
	/**
	 * Creates a URL usable to access Web-Operator "Schedule Workflow operation" page.
	 * @param inWebviewUrlFolder
	 * @param inPage
	 * @param secure
	 * @param inAutologon
	 * @param inHost
	 * @param inPort
	 * @param inUrl
	 */
	getWebviewScheduleUrl(inWebviewUrlFolder: any, inPage: any, secure: any, inAutologon: any, inHost: any, inPort: any, inUrl: any): any;
}

/**
 * WorkflowCategory Wrapper
 */
declare interface WorkflowCategory {
	name: string;
	description: string;
	path: string;
	workflows: Workflow[];
	allWorkflows: Workflow[];
	subCategories: WorkflowCategory[];
	parent: WorkflowCategory;
}

/**
 * WorkflowItem abstract class
 */
declare interface WorkflowItem {
	name: string;
	description: string;
	nextItem: any;
}

/**
 * Workflow item for conditions (Custom)
 */
declare interface WorkflowCustomConditionItem {
	name: string;
	description: string;
	nextItem: any;
	nextItemTrue: any;
	nextItemFalse: any;
	script: string;
}

/**
 * Workflow item for conditions (Generic)
 */
declare interface WorkflowGenericConditionItem {
	name: string;
	description: string;
	nextItem: any;
	nextItemTrue: any;
	nextItemFalse: any;
	script: string;
}

/**
 * Workflow item end
 */
declare interface WorkflowItemEnd {
	name: string;
	description: string;
}

/**
 * Workflow item starting a sub-workflow
 */
declare interface WorkflowLinkItem {
	name: string;
	description: string;
	nextItem: any;
	linkedWorkflow: any;
}

/**
 * Workflow item starting multiples workflows
 */
declare interface WorkflowMultipleCallItem {
	name: string;
	description: string;
	nextItem: any;
	linkedWorkflows: any[];
}

/**
 * Workflow item with an associated script
 */
declare interface WorkflowTaskItem {
	name: string;
	description: string;
	nextItem: any;
	script: string;
	isActionCall: boolean;
	isStartWorkflowCall: boolean;
	linkedWorkflow: any;
	usedActions: any;
}

/**
 * Workflow item for user interaction
 */
declare interface WorkflowInputItem {
	name: string;
	description: string;
	nextItem: any;
}

/**
 * Workflow item waiting on an event
 */
declare interface WorkflowItemWaitingEvent {
	name: string;
	description: string;
	nextItem: any;
}

/**
 * Workflow item waiting on a timer
 */
declare interface WorkflowItemWaitingTimer {
	name: string;
	description: string;
	nextItem: any;
}

/**
 * Object representing a instance of a Workflow (running or finished)
 */
declare class WorkflowToken {
	name: string;
	rootWorkflow: Workflow;
	currentWorkflow: Workflow;
	state: string;
	exception: string;
	businessState: string;
	workflowInputId: string;
	startDate: string;
	endDate: string;
	startDateAsDate: Date;
	endDateAsDate: Date;
	logEvents: LogEvent[];
	isStillValid: boolean;
	attributesStack: Attribute[];
	id: string;
	/**
	 * Save the picture of the workflowToken in .png format.
	 * @param file
	 */
	saveSchemaImageToFile(file: string): void;
	/**
	 * Cancels the execution of this workflow.
	 */
	cancel(): void;
	/**
	 * Generates a URL usable for answering input in running workflows in VS-O web front end version 3.0.x.
	 * @param secure
	 * @param inAutologon
	 * @param inHost
	 * @param inPort
	 * @param inUrl
	 */
	getAnswerUrl(secure: any, inAutologon: any, inHost: any, inPort: any, inUrl: any): any;
	/**
	 * Generates a URL usable for interacting with running workflows in VS-O web front end version 3.0.x.
	 * @param secure
	 * @param inHost
	 * @param inPort
	 */
	getInteractionUrl(secure: any, inHost: any, inPort: any): any;
	/**
	 * Generates a URL usable for answering input in running workflows in VS-O web front end.
	 * @param inWebviewUrlFolder
	 * @param inPage
	 * @param secure
	 * @param inAutologon
	 * @param inHost
	 * @param inPort
	 * @param inUrl
	 */
	getWebviewAnswerUrl(inWebviewUrlFolder: any, inPage: any, secure: any, inAutologon: any, inHost: any, inPort: any, inUrl: any): any;
	/**
	 * Gets the input parameters of the workflow token (if any).
	 */
	getInputParameters(): Properties;
	/**
	 * Gets the output parameters of the workflow token (if any).
	 */
	getOutputParameters(): Properties;
	/**
	 * Gets the attributes of the workflow token (if any).
	 */
	getAttributes(): Properties;
	/**
	 * Change the running credential for this workflow execution. The credential will be effective in the next workflow's exectution box.
	 * @param credential
	 */
	changeCredential(credential: any): void;
}

declare class ByteBuffer {
	length: number;

    /**
     * Create a ByteBuffer from a base64 string or existing ByteBuffer
     * @param obj - if null or undefined an empty ByteBuffer is created
     */
	constructor(obj?: string | any);
}

/**
 * Object representing a instance of a WorkflowInput
 */
declare interface WorkflowInput {
	name: string;
	state: string;
	startDateAsString: string;
	startDate: any;
	isStillValid: boolean;
	/**
	 * Generates a URL usable for answering input in running workflows in VS-O web front end version 3.0.x.
	 * @param secure
	 * @param inAutologon
	 * @param inHost
	 * @param inPort
	 * @param inUrl
	 */
	getAnswerUrl(secure: any, inAutologon: any, inHost: any, inPort: any, inUrl: any): any;
	/**
	 * Generates a URL usable for interacting with running workflows in VS-O web front end version 3.0.x.
	 * @param secure
	 * @param inHost
	 * @param inPort
	 */
	getInteractionUrl(secure: any, inHost: any, inPort: any): any;
	/**
	 * Generates a URL usable for answering input in running workflows in VS-O web front end.
	 * @param inWebviewUrlFolder
	 * @param inPage
	 * @param secure
	 * @param inAutologon
	 * @param inHost
	 * @param inPort
	 * @param inUrl
	 */
	getWebviewAnswerUrl(inWebviewUrlFolder: any, inPage: any, secure: any, inAutologon: any, inHost: any, inPort: any, inUrl: any): any;
}

/**
 * ZipWriter
 */
declare class ZipWriter {
  /**
   * Creates an instance of ZipWriter.
   * @param {string} file - full path of the file
   * @example const file = new ZipWriter('/var/run/vco/myFile.zip')
   */
  constructor (file: string);
	/**
	 * Add a string element to the specified zip file
	 * @param {string} entryName
	 * @param {string} content
	 * @param {string} encoding
	 */
	addContent(entryName: string, content: string, encoding?: string): void;
	/**
	 * Write the element to the Zip File
	 */
	writeZip(): void;
	/**
	 * Add mime attachement to the specified zip file
	 * @param {MimeAttachment} mimeAttachment
	 */
	addMimeAttachment(mimeAttachment: MimeAttachment): void;
	/**
	 * Reinitializes the length to 0 and sets the file-pointer in the very start of the file.
	 */
	clean(): void;
}

declare type EventCustom = any;

/**
 * General set of functions, always available in the scripting environment.
 */
declare namespace System {
	/**
	 * Log a text in the standard output console. Use this for debug only.
	 * @param text text to log
	 */
	function stdout(text: string): void;
	/**
	 * Log a text in the system with a INFO threshold.
	 * @param text text to log
	 */
	function log(text: string): void;
	/**
	 * Creates an empty file in the temporary directory of vRealize Orchestrator (app-server/temp), using the given suffix to generate its name.
	 * @param suffix The suffix string to be used in generating the file's name; may be null or omitted, in which case the suffix ".tmp" will be used.
	 */
	function createTempFile(suffix: string): File;
	/**
	 * Log a text in the system with a ERROR threshold.
	 * @param text text to log
	 */
	function error(text: string): void;
	/**
	 * Log a text in the system with a WARNING threshold.
	 * @param text text to log
	 */
	function warn(text: string): void;
	/**
	 * Log a text in the system with a DEBUG threshold.
	 * @param text text to log
	 */
	function debug(text: string): void;
	/**
	 * Get current server time.
	 */
	function getCurrentTime(): number;
	/**
	 * Pause the current script context execution and wait for a given date to continue.
	 * @param waitDate Date to wait
	 * @param milli Checking delay in milliseconds
	 */
	function waitUntil(waitDate: Date, milli: number): boolean;
	/**
	 * Wait for an external custom event (with an expiration date).
	 * @param text "internal" or "external" string. if Internal, this custom event cannot be triggered by an http post
	 * @param text Custom event key
	 * @param endDate Expiration date
	 */
	function waitCustomEventUntil(text: string, endDate: Date): EventCustom;
	/**
	 * Send a custom event to all Policies and Workflows.
	 * @param eventKey Custom event name
	 */
	function sendCustomEvent(eventKey: string): void;
	/**
	 * Return an string url usable to generate HTTP custom event.
	 * @param eventName Custom event name
	 * @param secure Use https if true, http otherwise
	 */
	function customEventUrl(eventName: string, secure: boolean): URL;
	/**
	 * Return an string url usable to generate HTTP custom event.
	 * The 'host' and 'port' parameters are useful if you want to be able to post events from outside a firewall.
	 * In this case, indicate the host and port values on the firewall and make the firewall transfer the requests to the VMO server, usually on port 8080.
	 * @param eventName Custom event name
	 * @param host Host to use in URL
	 * @param port Port to use in URL
	 * @param secure Use https if true, http otherwise
	 */
	function customEventUrlforServer(eventName: string, host: string, port: string, secure: boolean): URL;
	/**
	 * Generate a unique ID.
	 */
	function nextUUID(): string;
	/**
	 * Sleep for a given time.
	 * @param ms Time to sleep in miliseconds
	 */
	function sleep(ms: number): boolean;
	/**
	 * Return an action module with the given name.
	 * @param name The module name
	 */
	function getModule(name: string): any;
	/**
	 * Get the context object for the current workflow run.
	 */
	function getContext(): ExecutionContext;
	/**
	 * Return all action modules.
	 */
	function getAllModules(): Module[];
	/**
	 * Return server running os
	 */
	function getOsName(): string;
	/**
	 * Convert a hexadecimal string value to a number.
	 * @param value Hexadecimal value
	 */
	function hexToDecimal(value: string): number;
	/**
	 * Convert a decimal number value to a hexadecimal string.
	 * @param value Decimal value
	 */
	function decimalToHex(value: number): string;
	/**
	 * Extract the directory part from a file path.
	 * @param fullPath String,Path or File object
	 */
	function extractDirectory(fullPath: any): string;
	/**
	 * Extract the file name part from a file path.
	 * @param fullPath String,Path or File object
	 */
	function extractFileName(fullPath: any): string;
	/**
	 * Extract the file extension part from a file path.
	 * @param fullPath String,Path or File object
	 */
	function extractFileNameExtension(fullPath: any): string;
	/**
	 * Extract the file without extension part from a file path.
	 * @param fullPath String,Path or File object
	 */
	function extractFileNameWithoutExtension(fullPath: any): string;
	/**
	 * Append a path fragment to another. (i.e. C:/temp and subpath/myfile.txt -> C:/temp/subpath/myfile.txt)
	 * @param rootPath String,Path or File object
	 * @param toAdd Part to add
	 */
	function appendToPath(rootPath: any, toAdd: string): string;
	/**
	 * Used to populate additional log info to every scripting log.
	 * @param logMarker Log info that will appear in every scripting log
	 */
	function setLogMarker(logMarker: string): void;
	/**
	 * Format a given number in a human readable binary size format. (i.e 2048 -> 2K Bytes, 4718592 -> 4.5M Bytes)
	 * @param value Number to format
	 */
	function formatBinaryValue(value: number): string;
	/**
	 * Return the default temporary directory on the server
	 */
	function getTempDirectory(): string;
	/**
	 * Format a given date in a given format.
	 * Date and time formats are specified by date and time pattern  strings. Within date and time pattern strings, unquoted letters from 'A' to 'Z' and from 'a' to 'z' are interpreted as pattern letters representing the components of a date or time string. Text can be quoted using single quotes (') to avoid interpretation. "''" represents a single quote. All other characters are not interpreted; they're simply copied into the output string during formatting or matched against the input string during parsing.
	 * The following pattern letters are defined (all other characters from 'A' to 'Z' and from 'a' to 'z' are reserved):
	 *
	 * G 	Era designator (AD)
	 * y 	Year (1996; 96)
	 * M 	Month in year (July; Jul; 07)
	 * w 	Week in year
	 * W 	Week in month
	 * D 	Day in year
	 * d 	Day in month
	 * F 	Day of week in month
	 * E 	Day in week 	(Tuesday; Tue)
	 * a 	Am/pm marker 	(PM)
	 * H 	Hour in day (0-23)
	 * k 	Hour in day (1-24)
	 * K 	Hour in am/pm (0-11
	 * h 	Hour in am/pm (1-12)
	 * m 	Minute in hour
	 * s 	Second in minute
	 * S 	Millisecond
	 * z 	Time zone 	General time zone 	(Pacific Standard Time; PST; GMT-08:00)
	 * Z 	Time zone 	RFC 822 time zone 	(-0800)
	 * Examples
	 *
	 * The following examples show how date and time patterns are interpreted in the U.S. locale. The given date and time are 2001-07-04 12:08:56 local time in the U.S. Pacific Time time zone.
	 * Date and Time Pattern 	Result
	 * "yyyy.MM.dd G 'at' HH:mm:ss z" --> "2001.07.04 AD at 12:08:56 PDT"
	 * "EEE, MMM d, ''yy" --> "Wed, Jul 4, '01"
	 * "h:mm a" --> "12:08 PM"
	 * "hh 'o''clock' a, zzzz" --> "12 o'clock PM, Pacific Daylight Time"
	 * "K:mm a, z" --> "0:08 PM, PDT"
	 * "yyyyy.MMMMM.dd GGG hh:mm aaa" --> "02001.July.04 AD 12:08 PM"
	 * "EEE, d MMM yyyy HH:mm:ss Z" --> "Wed, 4 Jul 2001 12:08:56 -0700"
	 * "yyMMddHHmmssZ" --> "010704120856-0700"
	 * @param aDate Date to format
	 * @param pattern Format pattern
	 */
	function formatDate(aDate: Date, pattern: string): string;
	/**
	 * Format a number
	 * @param aNumber Number to format
	 * @param pattern Format pattern
	 */
	function formatNumber(aNumber: number, pattern: string): string;
	/**
	 * Test whether that address is reachable. Best effort is made by the implementation to try to reach the host, but firewalls and server configuration may block requests resulting in a unreachable status while some specific ports may be accessible. A typical implementation will use ICMP ECHO REQUESTs if the privilege can be obtained, otherwise it will try to establish a TCP connection on port 7 (Echo) of the destination host.
	 * @param hostOrIp Host or IP address
	 * @param timeout Timeout in milliseconds
	 */
	function isHostReachable(hostOrIp: string, timeout: number): boolean;
	/**
	 * Returns the IP address given a host name, using a DNS lookup. DNS must be configured on the VMO server. Failure to lookup the host will throw an exception.
	 * @param hostName Host name
	 */
	function resolveHostName(hostName: string): string;
	/**
	 * Returns the host name given an IP Address (in standard dotted notation, for example "127.0.0.1"), using a DNS lookup. DNS must be configured on the VMO server. Failure to lookup the hostname, or badly formatted IP addresses will throw an exception
	 * @param ipAddress The Ip address to lookup the hostname for
	 */
	function resolveIpAddress(ipAddress: string): string;
	/**
	 * compare two version number with format "v1.v2.v3 .. vn" and return the index of the change with positive and negative value depending what version is greater
	 * @param v1 First version
	 * @param v2 Second version
	 */
	function compareVersionNumber(v1: string, v2: string): number;
	/**
	 * Return the class name of any scripting object that typeof(obj) returns "object".
	 * @param obj Object to get the class name
	 */
	function getObjectClassName(obj: any): string;
	/**
	 * Return the plugin name of any scripting object. Base objects return "Server" as plugin
	 * @param obj Object to get the plugin name
	 */
	function getObjectPluginName(obj: any): string;
	/**
	 * Return true is object is a "NotFound" object
	 * @param obj Object to check
	 */
	function isNotFound(obj: any): boolean;
	/**
	 * Filter non authorized objects for the current user. If the parameter is an object the result will be null if the user is not authorized to use it, if it is an array or property, a recursive check is done and the unauthorized objects are removed.
	 * @param obj Object to check
	 */
	function filterAuthorized(obj: any): any;
	/**
	 * Construct a new date from a natural input and a reference date.
	 * @param inputString Natural human input text
	 * @param refDate (Optional) reference date, now if not set
	 */
	function getDate(inputString: string, refDate: Date): Date;
	/**
	 * Return the VS-O 'type' for the given object
	 * @param obj Object to get the type
	 */
	function getObjectType(obj: any): string;
	/**
	 * Return the VS-O 'id' for the given object, some object have no ids
	 * @param obj Object to get the id
	 */
	function getObjectId(obj: any): string;
	/**
	 * Format a given millisecond number in a human readable format
	 * @param milisecond Millisecond number to format
	 * @param showMili (Optional) Show millisecond if true. Default value is false.
	 * @param showZero (Optional) Show leading zero values if true. Default value is false.
	 */
	function formatDuration(milisecond: number, showMili: boolean, showZero: boolean): string;
	/**
	 * Parses the given string to produce a date.
	 * @param date String to parse
	 * @param pattern (Optional) Format pattern. Default value is 'yyyy-MM-dd HH:mm:ss'.
	 */
	function getDateFromFormat(date: string, pattern: string): string;
}

/**
 * Set of functions to interact with the VMO server.
 */
declare namespace Server {
	/**
	 * Check if the Credential is a valid LDAP Credential.
	 * @param credential Credential object
	 */
	function isValidLdapCredential(credential: Credential): boolean;
	/**
	 * Log a text in the server with a INFO threshold. The log will be stored in the database.
	 * @param text text to log
	 * @param info Additional info (optional)
	 */
	function log(text: string, info?: string): void;
	/**
	 * Log a text in the server with a ERROR threshold. The log will be stored in the database.
	 * @param text text to log
	 * @param info Additional info (optional)
	 */
	function error(text: string, info?: string): void;
	/**
	 * Log a text in the server with a WARN threshold. The log will be stored in the database.
	 * @param text text to log
	 * @param info Additional info (optional)
	 */
	function warn(text: string, info?: string): void;
	/**
	 * Return all the objects of the given type and matching the query expression.
	 * @param type The type name
	 * @param queryExp If 'type' is a vCO type, 'query' is for internal use only. If 'type' is a plug-in type, the optional 'query' parameter is a custom query depending on plug-in implementation.
	 * @param maxCount (Optional) Maximum number of elements , by default all.
	 */
	function query(type: string, queryExp: string, maxCount?: number): any[];
	/**
	 * Return all the objects of the given type.
	 * @param type The type name
	 * @param query (Optional) Custom query depending on plug-in implementation.
	 */
	function findAllForType(type: string, query?: string): any[];
	/**
	 * Return the object of the given type that has the given id.
	 * @param type The type name
	 * @param id The object id
	 */
	function findForType(type: string, id: string): any;
	/**
	 * Convert a Server's Object to an URI.
	 * @param object Object to convert
	 */
	function toUri(object: any): string;
	/**
	 * Get a Server's Object by it URI.
	 * @param uri Object uri
	 */
	function fromUri(uri: string): any;
	/**
	 * Convert a Server's Object to a String representation.
	 * @param object Object to convert
	 */
	function toStringRepresentation(object: any): StringRepresentation;
	/**
	 * Get a Server's Object by it String representation.
	 * @param rep StringRepresentation object
	 */
	function fromStringRepresentation(rep: StringRepresentation): any;
	/**
	 * Get a Workflow with a givent ID.
	 * @param id Workflow id
	 */
	function getWorkflowWithId(id: string): Workflow;
	/**
	 * Return an ldap element with the given DN
	 * @param dn Distinguished name
	 */
	function getLdapElement(dn: string): any;
	/**
	 * Return all ldap users matching the query's name
	 * @param query Query string (i.e. 'test*')
	 * @param limit Max number of records to retrieve
	 */
	function searchLdapUsers(query: string, limit: number): LdapUser[];
	/**
	 * Return all ldap groups matching the query's name
	 * @param query Query string (i.e. 'test*')
	 * @param limit Max number of records to retrieve
	 */
	function searchLdapGroups(query: string, limit: number): LdapGroup[];
	/**
	 * Return the ldap user associated with the running script
	 */
	function getCurrentLdapUser(): LdapUser;
	/**
	 * Return the credential associated with the running script
	 */
	function getCredential(): Credential;
	/**
	 * Return list of all registered plugin types in the server.
	 */
	function getAllPluginTypes(): any[];
	/**
	 * Return list of all attribute names for the given type.
	 * @param type The type name
	 */
	function getAllNamesForType(type: string): string[];
	/**
	 * Return list of all attribute display name for the given type.
	 * @param type The type name
	 */
	function getAllDisplayNamesForType(type: string): string[];
	/**
	 * Return list of all attribute description for the given type.
	 * @param type The type name
	 */
	function getAllDescriptionsForType(type: string): string[];
	/**
	 * Return wokflow token state.
	 * @param token_id The id of the workflow token.
	 */
	function getWorkflowTokenState(token_id: string): string;
	/**
	 * Return all workflow categories.
	 */
	function getAllWorkflowCategories(): WorkflowCategory[];
	/**
	 * Return a workflow category matching the given path or null if not found.
	 * @param path The path to the workflow category using forward slash(/) as separator.
	 */
	function getWorkflowCategoryWithPath(path: string): WorkflowCategory | null;
	/**
	 * Return a configuration element category matching the given path or null if not found.
	 * @param path The path to the configuration element category using forward slash(/) as separator.
	 */
	function getConfigurationElementCategoryWithPath(path: string): ConfigurationElementCategory | null;
	/**
	 * Return a resource element category matching the given path or null if not found.
	 * @param path The path to the resource element category using forward slash(/) as separator.
	 */
	function getResourceElementCategoryWithPath(path: string): ResourceElementCategory | null;
	/**
	 * Return a package given its name or null if not found.
	 * @param name The package name.
	 */
	function getPackageWithName(name: string): Package | null;
	/**
	 * Return a policy template category matching the given path or null if not found.
	 * @param path The path to the policy template category using forward slash(/) as separator.
	 */
	function getPolicyTemplateCategoryWithPath(path: string): PolicyTemplateCategory | null;
	/**
	 * Return all policy template categories.
	 */
	function getAllPolicyTemplateCategories(): PolicyTemplateCategory[];
	/**
	 * Return all Plugin modules information.
	 */
	function getAllPluginInfo(): PluginModuleDescription[];
	/**
	 * Return a plugin type info for a given type if exists.
	 * @param type Full SDK type info (i.e. module:type)
	 */
	function getPluginTypeInfo(type: string): PluginTypeDescription;
	/**
	 * Set a custom property to any plugin object.
	 * @param target Holder object
	 * @param key property key
	 * @param value Property value
	 */
	function setCustomProperty(target: any, key: string, value: any): void;
	/**
	 * Get a custom property to any plugin object.
	 * @param target Holder object
	 * @param key property key
	 */
	function getCustomProperty(target: any, key: string): any;
	/**
	 * Remove a custom property to any plugin object.
	 * @param target Holder object
	 * @param key property key
	 */
	function removeCustomProperty(target: any, key: string): void;
	/**
	 * Return all custom property keys available for a plugn object.
	 * @param target Holder object
	 */
	function getCustomPropertyKeys(target: any): string[];
	/**
	 * Return all plugin objects with a given key.
	 * @param key Property key
	 */
	function getObjectsWithCustomPropertyKey(key: string): any[];
	/**
	 * Return all plugin objects with a given key.
	 * @param key Property key
	 */
	function getObjectsURIWithCustomPropertyKey(key: string): string[];
	/**
	 * Remove all custom properties for a given object.
	 * @param target Holder object
	 */
	function removeAllCustomPropertiesForObject(target: any): void;
	/**
	 * Remove all custom properties for a given object TYPE.
	 * @param type Holder object type
	 */
	function removeAllCustomPropertiesForType(type: string): void;
	/**
	 * Remove all custom properties for a given object TYPE and KEY.
	 * @param type Holder object type
	 * @param key Property key
	 */
	function removeAllCustomPropertiesForTypeAndKey(type: string, key: string): void;
	/**
	 * Returns the running user display name.
	 */
	function getRunningUser(): string;
	/**
	 * Returns all configuration element categories.
	 */
	function getAllConfigurationElementCategories(): ConfigurationElementCategory[];
	/**
	 * Returns all resource element categories.
	 */
	function getAllResourceElementCategories(): ResourceElementCategory[];
	/**
	 * Returns authorization element with the given name.
	 * @param name Element name
	 */
	function getAuthorizationElementForName(name: string): AuthorizationElement;
	/**
	 * Returns authorization elements for the given ldap element.
	 * @param ldapElement LDAP Element
	 */
	function getAuthorizationElementsForLdapElement(ldapElement: any): AuthorizationElement[];
	/**
	 * Create a new Authorization Element.
	 * @param name Authorization Element name
	 * @param ldapElement Authorization Element name
	 * @param description (Optional) Element name
	 */
	function createAuthorizationElement(name: string, ldapElement: any, description: string): AuthorizationElement;
	/**
	 * Remove an existing Authorization Element.
	 * @param authorizationElement Authorization element to remove
	 */
	function removeAuthorizationElement(authorizationElement: AuthorizationElement): void;
	/**
	 * Create a new resource element.
	 * @param category The category. ResourceElementCategory or path (if the path does not exist it will be created)
	 * @param name The name of the element
	 * @param fileNameOrMime (Optional) The full path filename (type:string) or the mime attachment (type:MimeAttachment)
	 * @param mimeType (Optional) The mime type of the content (if a fileName is passed in argument)
	 */
	function createResourceElement(category: string | ResourceElementCategory, name: string, fileNameOrMime?: any, mimeType?: string): ResourceElement;
	/**
	 * Create a new configuration element. Since 7.0.
	 * @param category The category. ConfigurationElementCategory or path (if the path does not exist it will be created)
	 * @param name The name of the element
	 */
	function createConfigurationElement(category: string | ConfigurationElementCategory, name: string): ConfigurationElement;
	/**
	 * Remove an existing configuration element.
	 * @param configurationElement Configuration element to remove
	 */
	function removeConfigurationElement(configurationElement: ConfigurationElement): void;
	/**
	 * Remove an existing configuration element folder.
	 * @param configurationElementCategory Configuration element folder to remove
	 */
	function removeConfigurationElementCategory(configurationElementCategory: ConfigurationElementCategory): void;
	/**
	 * Remove an existing resource element.
	 * @param resourceElement Resource element to remove
	 */
	function removeResourceElement(resourceElement: ResourceElement): void;
	/**
	 * Set the access rights of an element.
	 * @param owner The element
	 * @param user User or group
	 * @param rights Rights (r,x,i)
	 */
	function setAccessRights(owner: any, user: LdapGroup | LdapUser | string, rights: string): void;
	/**
	 * Remove the access rights of an element.
	 * @param owner The element
	 * @param user User or group
	 */
	function removeAccessRights(owner: any, user: LdapGroup | LdapUser | string): void;
	/**
	 * Returns the signature owner of an element.
	 * @param element The element
	 */
	function getSignatureOwner(element: any): string;
	/**
	 * Returns the signature finger print of an element.
	 * @param element The element
	 */
	function getSignatureFingerPrint(element: any): string;
	/**
	 * Returns the log events matching the query.
	 * @param query The log query
	 */
	function fetchLogEvents(query: LogQuery): LogEvent[];
	/**
	 * Tags an object with a specific tag. The created tag is private and is visible only to the user who created the tag. The tag name must be between 3 and 64 characters.
	 * @param object Tagged object. All system types (worlfow, action, configurations) and plug-in types are supported.
	 * @param tag Tag name
	 * @param value Tag value
	 */
	function tag(object: any, tag: string, value: string): void;
	/**
	 * Tags an object with a specific tag. The created tag is global and is visible to all users. Global tags can be created only by users with administrator privileges. The tag name must be between 3 and 64 characters.
	 * @param object Tagged object. All system types (worlfow, action, configurations) and plug-in types are supported.
	 * @param tag Tag name
	 * @param value Tag value
	 */
	function tagGlobally(object: any, tag: string, value: string): void;
	/**
	 * Removes a private tag from an object.
	 * @param object Tagged object. All system types (worlfow, action, configurations) and plug-in types are supported.
	 * @param tags List of tags to be removed
	 */
	function untag(object: any, tags: string[]): void;
	/**
	 * Removes a global tag from an object.  Global tags can be removed only by users with administrator privileges.
	 * @param object Tagged object. All system types (worlfow, action, configurations) and plug-in types are supported.
	 * @param tags List of tags to be removed
	 */
	function untagGlobally(object: any, tags: string[]): void;
	/**
	 * Get the distinct tag names. Including tags names created by other users.
	 */
	function findTagsInUse(): string[];
	/**
	 * Return objects of the given type that are tagged with provided tags. To search for global tags prefix the tag name with ":".
	 * @param tagQuery Tag query in following format [{'tag':'tagname', 'value':'tagvalue'},{'tag':':anothertag', 'value':'tagvalue'}]
	 * @param type Used to filter returned objects by provided type
	 */
	function queryByTags(tagQuery: any, type: string): any[];
	/**
	 * Returns private tags associated with an object.
	 * @param Properties Tagged object. All system types (worlfow, action, configurations) and plug-in types are supported.
	 */
	function findTagsForObject(Properties: any): Properties;
	/**
	 * Returns global tags associated with an object.
	 * @param Properties Tagged object. All system types (worlfow, action, configurations) and plug-in types are supported.
	 */
	function findGlobalTagsForObject(Properties: any): Properties;
}

/**
 * Set of debugging functions.
 */
declare namespace Debug {
	/**
	 * Dump the current variables
	 */
	function dumpVariables(): void;
	/**
	 * Watch changes on a given variable.
	 * @param variable Variable to watch
	 */
	function watch(variable: string): void;
	/**
	 * Stop watching a given variable.
	 * @param variable Variable to stop watching
	 */
	function unwatch(variable: string): void;
}

/**
 * Locking functions, always available in the scripting environment.
 */
declare namespace LockingSystem {
	/**
	 * Try to acquire a lock. Returns true if the lock is acquired, false otherwise.
	 * @param lockId The lock id (what to lock)
	 * @param owner The lock owner (who is locking)
	 */
	function lock(lockId: string, owner: string): boolean;
	/**
	 * Try to acquire a lock and wait until lock is acquired.
	 * @param lockId The lock id (what to lock)
	 * @param owner The lock owner (who is locking)
	 */
	function lockAndWait(lockId: string, owner: string): void;
	/**
	 * Release a lock.
	 * @param lockId The lock id (what to unlock)
	 * @param owner The lock owner (who is unlocking)
	 */
	function unlock(lockId: string, owner: string): void;
	/**
	 * Release all locks.
	 */
	function unlockAll(): void;
	/**
	 * Retrieve all locks.
	 */
	function retrieveAll(): string[];
}

declare namespace Config {
	/**
	 * Gets the list of keystores
	 */
	function getKeystores(): any
}
