/**
 * Telnet client Wrapper
 */
declare class TelnetClient {
	connectTimeout: number;
	timeout: number;
	tcpNoDelay: boolean;
	soLinger: number;
	constructor()
	/**
	 * @param terminal 
	 */
	constructor(terminal: string)
	/**
	 * Enable SSL
	 * @param on 
	 */
	enableSSL(on: boolean): void;
	/**
	 * Connect the client to an host
	 * @param host 
	 * @param port 
	 */
	connect(host: string, port: number): void;
	/**
	 * Disconnect the client
	 */
	disconnect(): void;
	/**
	 * Get an OID value in the current session
	 * @param val 
	 */
	sendBinary(val: any[]): void;
	/**
	 * Get an OID value in the current session
	 * @param oid 
	 */
	sendString(oid: string): void;
	/**
	 * return true if data are available or false if timeout
	 * @param timeout 
	 */
	waitForData(timeout: number): void;
	/**
	 * Get a response as string
	 */
	receiveAsString(): void;
	/**
	 * Get a response as an arrayof number
	 */
	receiveAsBinary(): void;
	/**
	 * Make various checks on the socket to test if it is available for use
	 */
	isAvailable(): boolean;
	/**
	 * Returns true if the client is currently connected to a server
	 */
	isConnected(): boolean;
}

/**
 * Jakarta Commons Net FTP client wrapper
 */
declare class FTPClient {
	output: void;
	replyCode: void;
	state: void;
	cwd: void;
	connectTimeout: number;
	timeout: number;
	tcpNoDelay: boolean;
	soLinger: number;
	constructor()
	constructor()
	/**
	 * Enable SSL
	 * @param on 
	 */
	enableSSL(on: boolean): void;
	/**
	 * Connect the client to an host
	 * @param host 
	 * @param port 
	 */
	connect(host: string, port: number): void;
	/**
	 * Login on the ftp server
	 * @param username 
	 * @param password 
	 * @param account 
	 */
	login(username: string, password: string, account: string): void;
	/**
	 * Execute command
	 * @param cmd 
	 * @param args 
	 */
	executeCommand(cmd: string, args: string): void;
	/**
	 * Copy a file FROM a remote host TO the VS-O Server.
	 * @param remoteFile 
	 * @param localFile 
	 */
	getFile(remoteFile: string, localFile: string): number;
	/**
	 * Get the content of a remote file.
	 * @param remoteFile 
	 */
	getString(remoteFile: string): string;
	/**
	 * Copy a file TO a remote host FROM the VS-O Server.
	 * @param localFile 
	 * @param remoteFile 
	 */
	putFile(localFile: string, remoteFile: string): number;
	/**
	 * Put a string content to a remote file.
	 * @param content 
	 * @param remoteFile 
	 */
	putString(content: string, remoteFile: string): number;
	/**
	 * List files and directories in path.
	 * @param basePath 
	 */
	listAll(basePath: string): string[];
	/**
	 * List files ONLY in path.
	 * @param basePath 
	 */
	listFile(basePath: string): string[];
	/**
	 * List directories ONLY in path.
	 * @param basePath 
	 */
	listDir(basePath: string): string[];
	/**
	 * Logout and disconnects the current session if open.
	 */
	disconnect(): void;
	/**
	 * The status information returned by the server.
	 */
	getStatus(): string;
	/**
	 * Make various checks on the socket to test if it is available for use
	 */
	isAvailable(): boolean;
	/**
	 * Returns true if the client is currently connected to a server
	 */
	isConnected(): boolean;
}

/**
 * Jakarta Commons Net POP3 client wrapper
 */
declare class POP3Client {
	output: void;
	state: void;
	connectTimeout: number;
	timeout: number;
	tcpNoDelay: boolean;
	soLinger: number;
	constructor()
	constructor()
	/**
	 * Enable SSL
	 * @param on 
	 */
	enableSSL(on: boolean): void;
	/**
	 * Connect the client to an host
	 * @param host 
	 * @param port 
	 */
	connect(host: string, port: number): void;
	/**
	 * Login to the POP3 server with the given username and password.
	 * You must first connect to the server with 'connect()' before attempting to login.
	 * @param username 
	 * @param password 
	 */
	login(username: string, password: string): void;
	/**
	 * Login to the POP3 server with the given username and authentication information.
	 * Use this method when connecting to a server requiring authentication using the APOP command.
	 * Because the timestamp produced in the greeting banner varies from server to server, it is not possible to consistently extract the information.
	 * Therefore, after connecting to the server, you must get the 'output' sttribute and parse out the timestamp information yourself.
	 * @param username 
	 * @param timestamp 
	 * @param secret 
	 */
	loginWithSecret(username: string, timestamp: string, secret: string): void;
	/**
	 * List all messages. If there are no messages, this method returns a zero length array. If the list attempt fails, it returns null.
	 */
	listMessages(): void;
	/**
	 * Logout of the POP3 server.  To fully disconnect from the server you must call 'disconnect()'
	 */
	logout(): void;
	/**
	 * Disconnects the client from the server.
	 */
	disconnect(): void;
	/**
	 * Send a NOOP command to the POP3 server.  This is useful for keeping connection alive.
	 */
	noop(): void;
	/**
	 * Reset the POP3 session.  This is useful for undoing any message deletions that may have been performed.
	 */
	reset(): void;
	/**
	 * Make various checks on the socket to test if it is available for use
	 */
	isAvailable(): boolean;
	/**
	 * Returns true if the client is currently connected to a server
	 */
	isConnected(): boolean;
}

/**
 * Jakarta Commons Net IMAP client wrapper
 */
declare class IMAPClient {
	output: void;
	state: void;
	connectTimeout: number;
	timeout: number;
	tcpNoDelay: boolean;
	soLinger: number;
	constructor()
	constructor()
	/**
	 * Enable SSL
	 * @param on 
	 */
	enableSSL(on: boolean): void;
	/**
	 * Connect the client to an host
	 * @param host 
	 * @param port 
	 */
	connect(host: string, port: number): void;
	/**
	 * Login to the IMAP server with the given username and password.
	 * You must first connect to the server with 'connect()' before attempting to login.
	 * @param username 
	 * @param password 
	 */
	login(username: string, password: string): boolean;
	/**
	 * Logout of the IMAP server.  To fully disconnect from the server you must call 'disconnect()'
	 */
	logout(): boolean;
	/**
	 * Send a CLOSE command to the server.
	 */
	close(): boolean;
	/**
	 * Disconnects the client from the server.
	 */
	disconnect(): void;
	/**
	 * Send a NOOP command to the IMAP server. This is useful for keeping connection alive.
	 */
	noop(): void;
	/**
	 * Make various checks on the socket to test if it is available for use.
	 */
	isAvailable(): boolean;
	/**
	 * Returns true if the client is currently connected to a server.
	 */
	isConnected(): boolean;
	/**
	 * Send an APPEND command to the server.
	 * @param mailboxName 
	 */
	append(mailboxName: string): boolean;
	/**
	 * Send an APPEND command to the server.
	 * @param mailboxName 
	 * @param flags 
	 * @param datetime 
	 */
	appendWithOptions(mailboxName: string, flags: string, datetime: string): boolean;
	/**
	 * Send a CAPABILITY command to the server.
	 */
	capability(): boolean;
	/**
	 * Send a CHECK command to the server
	 */
	check(): boolean;
	/**
	 * Send a COPY command to the server.
	 * @param sequenceSet 
	 * @param mailboxName 
	 */
	copy(sequenceSet: string, mailboxName: string): boolean;
	/**
	 * Send a CREATE command to the server.
	 * @param mailboxName 
	 */
	create(mailboxName: string): boolean;
	/**
	 * Send a DELETE command to the server.
	 * @param mailboxName 
	 */
	delete(mailboxName: string): boolean;
	/**
	 * Send a EXAMINE command to the server.
	 * @param mailboxName 
	 */
	examine(mailboxName: string): boolean;
	/**
	 * Send a EXPUNGE command to the server.
	 */
	expunge(): boolean;
	/**
	 * Send a FETCH command to the server.
	 * @param sequenceSet 
	 * @param itemNames 
	 */
	fetch(sequenceSet: string, itemNames: string): boolean;
	/**
	 * Send a LIST command to the server.
	 * @param refName 
	 * @param mailboxName 
	 */
	list(refName: string, mailboxName: string): boolean;
	/**
	 * Send a LSUB command to the server.
	 * @param refName 
	 * @param mailboxName 
	 */
	lsub(refName: string, mailboxName: string): boolean;
	/**
	 * Send a RENAME command to the server.
	 * @param oldMailboxName 
	 * @param newMailboxName 
	 */
	rename(oldMailboxName: string, newMailboxName: string): boolean;
	/**
	 * Send a SEARCH command to the server.
	 * @param criteria 
	 */
	search(criteria: string): boolean;
	/**
	 * Send a SEARCH command to the server.
	 * @param charset 
	 * @param criteria 
	 */
	searchCharset(charset: string, criteria: string): boolean;
	/**
	 * Send a STATUS command to the server.
	 * @param mailboxName 
	 * @param itemNames 
	 */
	status(mailboxName: string, itemNames: string[]): boolean;
	/**
	 * Send a SELECT command to the server.
	 * @param mailboxName 
	 */
	select(mailboxName: string): boolean;
	/**
	 * Send a STORE command to the server.
	 * @param sequenceSet 
	 * @param itemNames 
	 * @param itemValues 
	 */
	store(sequenceSet: string, itemNames: string, itemValues: string): boolean;
	/**
	 * Send a SUBSCRIBE command to the server.
	 * @param mailboxName 
	 */
	subscribe(mailboxName: string): boolean;
	/**
	 * Send a UNSUBSCRIBE command to the server.
	 * @param mailboxName 
	 */
	unsubscribe(mailboxName: string): boolean;
	/**
	 * Send a UID command to the server.
	 * @param command 
	 */
	uid(command: string): boolean;
	/**
	 * Returns the reply to the last command sent to the server. The value is a single string containing all the reply lines including newlines.
	 */
	getReply(): string;
	/**
	 * Returns an array of lines received as a reply to the last command sent to the server. The lines have end of lines truncated.
	 */
	getReplyLines(): string[];
}

/**
 * POP3 Message
 */
declare interface POP3Message {
	id: void;
	size: void;
	from: void;
	to: void;
	subject: void;
	body: void;
	/**
	 * Get the header value i.e. ("Subject","Date","From",...)
	 * @param key 
	 */
	getHeader(key: string): void;
	/**
	 * Delete a message from the POP3 server.  The message is only marked for deletion by the server.
	 * If you decide to unmark the message, you must issues a 'reset()' command on POP3Client.
	 * Messages marked for deletion are only deleted by the server on POP3Client 'logout()'.
	 */
	deleteFromServer(): void;
}
