/**
 * File on a remote file system using SSH to access it
 */
declare interface SSHFile {
}

/**
 * Folder on a remote file system using SSH to access it
 */
declare interface SSHFolder {
}

/**
 * SSH Command
 */
declare class SSHCommand {
	exitCode: number;
	cmd: string;
	output: string;
	error: string;
	state: void;
	constructor()
	/**
	 * Create a new SSHCommand
	 * @param host
	 * @param username
	 * @param password
	 */
	constructor(host: string, username: string, password: SecureString)
	/**
	 * Create a new SSHCommand
	 * @param host
	 * @param username
	 * @param password
	 * @param port
	 */
	constructor(host: string, username: string, password: string, port: number)
	/**
	 * Execute a single command and return immediately. This command will connect the session, request an execution channel, execute the command and close the execution channel. The session is open automatically if not opened yet, but will remain open after the command execution. Use disconnect to close the session. Value of the exitCode property is changed to -1 for initializing, 0 if successful, and positive number if an error occured.
	 */
	execute(): void;
	/**
	 * Execute a single command and wait until end. Value of the exitCode property is changed to -1 for initializing, 0 if successful, and positive number if an error occured.
	 * @param file
	 */
	executeAndLog(file: string): void;
	/**
	 * Execute a single command and wait until completed, return the stdout result if sychronous. This command will connect the session, request an execution channel, execute the command and close the execution channel. The session is open automatically if not opened yet, but will remain open after the command execution. Use disconnect to close the session. Value of the exitCode property is changed to -1 for initializing, 0 if successful, and positive number if an error occured.
	 * @param cmd
	 * @param wait
	 */
	executeCommand(cmd: string, wait: boolean): void;
	/**
	 * Sets the encoding that will be used to process the results from executeCommand method.
	 * @param encoding
	 */
	setEncoding(encoding: string): void;
	/**
	 * Returns the encoding used by this SSHCommand
	 */
	getEncoding(): any;
	/**
	 * Copy a file FROM a remote host TO the vCO Server. This command will connect the session, request an execution channel, execute the command and close the execution channel. The session is open automatically if not opened yet, but will remain open after the command execution. Use disconnect to close the session. Returns 0 if successful, or -1 if an error has occurred.
	 * @param remoteFile
	 * @param localFile
	 */
	getFile(remoteFile: string, localFile: string): number;
	/**
	 * Copy a file TO a remote host FROM the vCO Server. Returns 0 if successful, or -1 if an error has occurred. If a remote file name is provided, the destination file name will use it. If only the destination directory is specified, then the destination file will use the source file name. The destination directory must exist.
	 *
	 * @param localFile
	 * @param remoteFile
	 */
	putFile(localFile: string, remoteFile: string): number;
	/**
	 * Recursively search and return matching pattern file and directory. This command will connect the session, request an execution channel, execute the command and close the execution channel. The session is open automatically if not opened yet, but will remain open after the command execution. Use disconnect to close the session.
	 * @param basePath
	 * @param pattern
	 */
	findAll(basePath: string, pattern: string): string[];
	/**
	 * Recursively search and return matching pattern file ONLY. This command will connect the session, request an execution channel, execute the command and close the execution channel. The session is open automatically if not opened yet, but will remain open after the command execution. Use disconnect to close the session.
	 * @param basePath
	 * @param pattern
	 */
	findFile(basePath: string, pattern: string): string[];
	/**
	 * Recursively search and return matching pattern directory ONLY.  This command will connect the session, request an execution channel, execute the command and close the execution channel. The session is open automatically if not opened yet, but will remain open after the command execution. Use disconnect to close the session.
	 * @param basePath
	 * @param pattern
	 */
	findDir(basePath: string, pattern: string): string[];
	/**
	 * List files and directories in path. This command will connect the session, request an execution channel, execute the command and close the execution channel. The session is open automatically if not opened yet, but will remain open after the command execution. Use disconnect to close the session.
	 * @param basePath
	 */
	listAll(basePath: string): string[];
	/**
	 * List files ONLY in path. This command will connect the session, request an execution channel, execute the command and close the execution channel. The session is open automatically if not opened yet, but will remain open after the command execution. Use disconnect to close the session.
	 * @param basePath
	 */
	listFile(basePath: string): string[];
	/**
	 * List directories ONLY in path. This command will connect the session, request an execution channel, execute the command and close the execution channel. The session is open automatically if not opened yet, but will remain open after the command execution. Use disconnect to close the session.
	 * @param basePath
	 */
	listDir(basePath: string): string[];
	/**
	 * Disconnects the current session if open. The session parameters are given in the object constructor (host, username, password); a session is opened automatically when any method is called that requires a session (like execute). Once open, the session remains open; any method uses the same session but opens a new channel and the closes it when done. You need to use this disconnect method to close the session.
	 */
	disconnect(): void;
}

/**
 * SSH Session to username@host:port. This object replace the old 'SSHCommand' object.
 */
declare class SSHSession {
	exitCode: number;
	cmd: void;
	pty: void;
	terminal: void;
	output: void;
	error: void;
	state: void;
	constructor()
	/**
	 * Create a new SSHSession
	 * @param host
	 * @param username
	 */
	constructor(host: string, username: string)
	/**
	 * Create a new SSHSession
	 * @param host
	 * @param username
	 * @param port
	 */
	constructor(host: string, username: string, port: number)
	/**
	 * Connect the session using simple username/password authentification.
	 * @param password
	 */
	connectWithPassword(password: any): void;
	/**
	 * Deprecated use connectWithIdentity(). Connect the session using Public key Authentication.
	 * @param privateKeyPath
	 * @param passphrase
	 */
	connectWithIdentidy(privateKeyPath: any, passphrase: any): void;
	/**
	 * Connect the session using Public key Authentication.
	 * @param privateKeyPath
	 * @param passphrase
	 */
	connectWithIdentity(privateKeyPath: any, passphrase: any): void;
	/**
	 * Connect the session using either password or Public key Authentication.
	 * @param isPassword
	 * @param password
	 * @param privateKeyPath
	 */
	connectWithPasswordOrIdentity(isPassword: boolean, password: any, privateKeyPath: any): void;
	/**
	 * The provided userInfo object is used for callback for the keyboard-interactive authentication and for the other types of messages send from the remote server during authentication. The function signatures should be:
	 *
	 * Example:
	 *
	 *
	 * @param userInfo
	 */
	setUserInfo(userInfo: any): void;
	/**
	 * Fills a property list of environment variables that will be set prior to opening a channel.
	 * @param key
	 * @param value
	 */
	addEnvironment(key: string, value: string): void;
	/**
	 * Sets the encoding that will be used to process the results from executeCommand method
	 * @param encoding
	 */
	setEncoding(encoding: string): void;
	/**
	 * Returns the encoding used by this SSHSession
	 */
	getEncoding(): any;
	/**
	 * Execute a single command and return immediately. This command will connect the session, request an execution channel, execute the command and close the execution channel. The session is open automatically if not opened yet, but will remain open after the command execution. Use disconnect to close the session. Value of the exitCode property is changed to -1 for initializing, 0 if successful, and positive number if an error occured.
	 */
	execute(): void;
	/**
	 * Execute a single command and wait until end. Value of the exitCode property is changed to -1 for initializing, 0 if successful, and positive number if an error occured.
	 * @param file
	 */
	executeAndLog(file: any): void;
	/**
	 * Execute a single command and wait until completed, return the stdout result if sychronous. This command will connect the session, request an execution channel, execute the command and close the execution channel. The session is open automatically if not opened yet, but will remain open after the command execution. Use disconnect to close the session. Value of the exitCode property is changed to -1 for initializing, 0 if successful, and positive number if an error occured.
	 * @param cmd
	 * @param wait
	 */
	executeCommand(cmd: string, wait: boolean): void;
	/**
	 * Copy a file FROM a remote host TO the vCO Server. This command will connect the session, request an execution channel, execute the command and close the execution channel. The session is open automatically if not opened yet, but will remain open after the command execution. Use disconnect to close the session. Returns 0 if successful, or -1 if an error has occurred.
	 * @param remoteFile
	 * @param localFile
	 */
	getFile(remoteFile: any, localFile: any): number;
	/**
	 * Copy a file TO a remote host FROM the vCO Server. Returns 0 if successful, or -1 if an error has occurred. If a remote file name is provided, the destination file name will use it. If only the destination directory is specified, then the destination file will use the source file name. The destination directory must exist.
	 *
	 * @param localFile
	 * @param remoteFile
	 */
	putFile(localFile: any, remoteFile: any): number;
	/**
	 * Recursively search and return matching pattern file and directory. This command will connect the session, request an execution channel, execute the command and close the execution channel. The session is open automatically if not opened yet, but will remain open after the command execution. Use disconnect to close the session.
	 * @param basePath
	 * @param pattern
	 */
	findAll(basePath: any, pattern: string): string[];
	/**
	 * Recursively search and return matching pattern file ONLY. This command will connect the session, request an execution channel, execute the command and close the execution channel. The session is open automatically if not opened yet, but will remain open after the command execution. Use disconnect to close the session.
	 * @param basePath
	 * @param pattern
	 */
	findFile(basePath: any, pattern: string): string[];
	/**
	 * Recursively search and return matching pattern directory ONLY.  This command will connect the session, request an execution channel, execute the command and close the execution channel. The session is open automatically if not opened yet, but will remain open after the command execution. Use disconnect to close the session.
	 * @param basePath
	 * @param pattern
	 */
	findDir(basePath: any, pattern: string): string[];
	/**
	 * List files and directories in path. This command will connect the session, request an execution channel, execute the command and close the execution channel. The session is open automatically if not opened yet, but will remain open after the command execution. Use disconnect to close the session.
	 * @param basePath
	 */
	listAll(basePath: any): string[];
	/**
	 * List files ONLY in path. This command will connect the session, request an execution channel, execute the command and close the execution channel. The session is open automatically if not opened yet, but will remain open after the command execution. Use disconnect to close the session.
	 * @param basePath
	 */
	listFile(basePath: any): string[];
	/**
	 * List directories ONLY in path. This command will connect the session, request an execution channel, execute the command and close the execution channel. The session is open automatically if not opened yet, but will remain open after the command execution. Use disconnect to close the session.
	 * @param basePath
	 */
	listDir(basePath: any): string[];
	/**
	 * Disconnects the current session if open. The session parameters are given in the object constructor (host, username, password); a session is opened automatically when any method is called that requires a session (like execute). Once open, the session remains open; any method uses the same session but opens a new channel and the closes it when done. You need to use this disconnect method to close the session.
	 */
	disconnect(): void;
}

/**
 * Set of functions to manage private/public keys.
 */
declare interface KeyPairManager {
	/**
	 * Generate a pair of public/private key. Returns the generated key fingerprint.
	 * @param type
	 * @param path
	 * @param passphrase
	 * @param keySize
	 * @param comment
	 */
	generateKeyPair(type: string, path: any, passphrase: any, keySize: number, comment: string): string;
	/**
	 * Change the passphrase of a private key.
	 * @param path
	 * @param oldPasspharse
	 * @param newPassphrase
	 */
	changePassphrase(path: any, oldPasspharse: any, newPassphrase: any): void;
}

declare class SSHHostConfiguration {
	hostname: string;
	port: number;
	username: string;
	password: any;
	passphrase: any;
	certificatePath: string;
	passwordAuthentication: boolean;
	rootFolders: string[];
	readonly id: string;
	constructor()
	/**
	 * Default constructor for the SshHostConfiguration object.
	 */
	constructor()
}

/**
 * SSHHostManager provides CRUD operations for the SSHHost objects
 */
declare class SSHHostManager {
	/**
	 * Adds a SSH Host
	 * @param config
	 */
	static addSshHost(config: SSHHostConfiguration): SSHHost;
	/**
	 * Removes a SSH Host
	 * @param host
	 */
	static removeSshHost(host: SSHHost): void;
	/**
	 * Updates a SSH Host
	 * @param sshHost
	 * @param sourceConfig
	 */
	static updateSshHost(sshHost: SSHHost, sourceConfig: SSHHostConfiguration): SSHHost;
	/**
	 * Gets a SSH Host
	 * @param id
	 */
	static getSshHost(id: string): SSHHost;
	/**
	 * Gets all SSH Hosts
	 */
	static getSshHosts(): SSHHost[];
	/**
	 * Adds a Root Folder to SSH Host
	 * @param sshHost
	 * @param rootFolder
	 */
	static addRootFolderToHost(sshHost: SSHHost, rootFolder: string): SSHHost;
	/**
	 * Removes a Root Folder from SSH Host
	 * @param sshHost
	 * @param rootFolder
	 */
	static removeRootFolderFromHost(sshHost: SSHHost, rootFolder: string): SSHHost;
}

/**
 * SSH Host object
 */
declare interface SSHHost {
	readonly id: string;
	readonly displayName: string;
	readonly port: number;
	readonly hostname: string;
	readonly username: string;
	readonly rootFolders: string[];
	readonly sshHostConfiguration: SSHHostConfiguration;
}
