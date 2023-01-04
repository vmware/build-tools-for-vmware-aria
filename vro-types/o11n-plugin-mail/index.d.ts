declare class ConnectionManager {
	/**
	 * Creates a new Connection for the provided parameters.
	 * @param smtpHost 
	 * @param smtpPort 
	 * @param username 
	 * @param password 
	 * @param fromAddress 
	 * @param fromName 
	 */
	static save(smtpHost: string, smtpPort: number, username: string, password: string, fromAddress: string, fromName: string): string;
}

/**
 * Main class to create mail messages
 */
declare class EmailMessage {
	smtpHost: string;
	smtpPort: number;
	toAddress: string;
	fromAddress: string;
	fromName: string;
	ccAddress: string;
	bccAddress: string;
	subject: string;
	username: string;
	password: SecureString;
	useSsl: boolean;
	useStartTls: boolean;

	constructor();
	/**
	 * Send the message.
	 */
	sendMessage(): void;
	/**
	 * Add a mime part to the message.
	 * @param content 
	 * @param mimetype 
	 */
	addMimePart(content: any, mimetype: string): void;
	/**
	 * Set a session property.
	 * @param key 
	 * @param value 
	 */
	setSessionProperty(key: string, value: string): void;
	/**
	 * Get the value of the specified session property.
	 * @param key 
	 */
	getSessionProperty(key: string): string;
}

/**
 * Mail client that can be used to retrieve messages from a mail server. The default protocol is IMAP but POP3 protocol can also be used.
 */
declare class MailClient {
	constructor();
	/**
	 * Enable SSL connection.
	 */
	enableSSL(): void;
	/**
	 * Set connection timeout.
	 * @param timeout 
	 */
	setConnectionTimeout(timeout: number): void;
	/**
	 * Set socket timeout.
	 * @param timeout 
	 */
	setSocketTimeout(timeout: number): void;
	/**
	 * Set a mail protocol to use.
	 * @param protocol 
	 */
	setProtocol(protocol: string): void;
	/**
	 * Connect the client to a mail server.
	 * @param host 
	 * @param port 
	 * @param username 
	 * @param password 
	 */
	connect(host: string, port: number, username: string, password: string): void;
	/**
	 * Enables the IMAP Compatibility mode. It allows the client to avoid all sorts of parsing and protocol bugs in many IMAP servers, but of course it comes at the cost of being less efficient because it is not taking advantage of the IMAP protocol's ability to fetch only the parts of the message that are needed. For complete reference - http://www.oracle.com/technetwork/java/javamail/faq/index.html#imapserverbug
	 */
	enableImapCompatibilityMode(): void;
	/**
	 * Disables the IMAP Compatibility mode.
	 */
	disableImapCompatibilityMode(): void;
	/**
	 * Returns the name of the root folder of the default namespace presented to the user.
	 */
	getDefaultFolder(): void;
	/**
	 * Opens a folder on the mail server.
	 * @param folderName 
	 */
	openFolder(folderName: string): void;
	/**
	 * Opens the root folder of the default namespace presented to the user.
	 */
	openDefaultFolder(): void;
	/**
	 * Returns a list of subfolders of the current folder.
	 */
	getSubFolders(): string[];
	/**
	 * Returns a list of subfolders of the current folder.
	 */
	getSubscribedSubFolders(): string[];
	/**
	 * Returns a set of folders representing the namespaces for a user.
	 * @param userName 
	 */
	getUserNamespaces(userName: string): string[];
	/**
	 * Closes the current folder.
	 */
	closeFolder(): void;
	/**
	 * Returns a message from the mail server.
	 * @param msgNumber 
	 */
	getMessage(msgNumber: string): ReceivedMessage[];
	/**
	 * Returns a list of messages from the mail server which have message numbers within a certain range.
	 * @param startMsgNumber 
	 * @param endMsgNumber 
	 */
	getMessagesBetween(startMsgNumber: string, endMsgNumber: string): ReceivedMessage[];
	/**
	 * Returns all messages in the current folder.
	 */
	getMessages(): ReceivedMessage[];
	/**
	 * Returns all unread messages in the current folder.
	 */
	getUnseenMessages(): ReceivedMessage[];
	/**
	 * Returns all recent messages of the current folder.
	 */
	getRecentMessages(): ReceivedMessage[];
	/**
	 * Returns all deleted messages from the current folder.
	 */
	getDeletedMessages(): ReceivedMessage[];
	/**
	 * Returns all messages that contain a specific string in the "From" address attribute.
	 * @param pattern 
	 */
	searchFromAddress(pattern: string): ReceivedMessage[];
	/**
	 * Returns all messages that contain a specific string in the "To", "Cc" or "Bcc" address attributes.
	 * @param pattern 
	 */
	searchRecipientAddress(pattern: string): ReceivedMessage[];
	/**
	 * Returns all messages that contain a specific string in the message body.
	 * @param pattern 
	 */
	searchBodyMessage(pattern: string): ReceivedMessage[];
	/**
	 * Returns all messages that contain a specific string in the message subject.
	 * @param pattern 
	 */
	searchSubjectMessage(pattern: string): ReceivedMessage[];
	/**
	 * Returns the total number of messages in the current folder.
	 */
	getMessageCount(): number;
	/**
	 * Returns the number of the new messages in the current folder.
	 */
	getNewMessageSize(): number;
	/**
	 * Mark a message for deletion. Messages marked for deletion are deleted by the server only when the folder is closed or by
	 * explicitly invoking an 'expunge' method.
	 * @param msgNumber 
	 */
	deleteMessage(msgNumber: string): void;
	/**
	 * Mark messages for deletion. Messages marked for deletion are deleted by the server only when the folder is closed or by
	 * explicitly invoking an 'expunge' method.
	 * @param msgNumbers 
	 */
	deleteMessages(msgNumbers: string[]): void;
	/**
	 * Expunge (permanently remove) all messages marked for deletion.
	 */
	expungeMessages(): ReceivedMessage[];
	/**
	 * Set MAX allowed body part size in MB
	 * @param size 
	 */
	setMaxBodyPartSize(size: number): void;
	/**
	 * Set a session property.
	 * @param key 
	 * @param value 
	 */
	setSessionProperty(key: string, value: string): void;
	/**
	 * Get the value of the specified session property.
	 * @param key 
	 */
	getSessionProperty(key: string): string;
	/**
	 * Disconnects the client from the mail server. Expunges all messages marked for deletion.
	 */
	close(): void;
}

/**
 * Represents a received email message from a mail server.
 */
declare interface ReceivedMessage {
	from: string[];
	to: string[];
	subject: void;
	cc: string[];
	bcc: string[];
	replyTo: string[];
	id: void;
	contentType: void;
	/**
	 * Returns all message headers as properties.
	 */
	getHeaders(): Properties;
	/**
	 * Returns a value of a specific header key.
	 * @param key 
	 */
	getHeader(key: string): string;
	/**
	 * Returns the folder from which this message was obtained.
	 */
	getFolder(): string;
	/**
	 * Returns the content of the message as a raw string.
	 */
	getContent(): string;
	/**
	 * Returns the content of the message as MessageMultiPart if the content is of a multipart type.
	 */
	getMultiPartContent(): MessageMultiPart;
	/**
	 * Shows whether the content is a multipart object.
	 */
	isContentMultiPart(): boolean;
	/**
	 * Returns the date on which this message was received.
	 */
	getReceivedDate(): Date;
	/**
	 * Returns the date on which this message was sent.
	 */
	getSentDate(): Date;
	/**
	 * Checks whether this message is expunged.
	 */
	isExpunged(): boolean;
	/**
	 * Marks this message for deletion
	 */
	delete(): void;
}

/**
 * Container that holds multiple body parts
 */
declare interface MessageMultiPart {
	/**
	 * Returns the number of the body parts.
	 */
	getPartsCount(): number;
	/**
	 * Returns a specific body part from the multipart message.
	 * @param index 
	 */
	getBodyPart(index: number): MessageBodyPart;
}

/**
 * Part of the message that contains a set of attributes and a content
 */
declare interface MessageBodyPart {
	contentType: void;
	disposition: void;
	fileName: void;
	/**
	 * Returns the content of this body part as a raw string.
	 */
	getContent(): string;
	/**
	 * Returns the content as MessageMultiPart if the body part is of a multipart type.
	 */
	getMultiPartContent(): MessageMultiPart;
	/**
	 * Shows whether the content is a multipart object.
	 */
	isContentMultiPart(): boolean;
	/**
	 * Return the size of the content of this part in bytes.
	 */
	getSize(): number;
	/**
	 * Returns true if this part is an attachment.
	 */
	isAttachment(): boolean;
	/**
	 * Returns the content as MimeAttachment object.
	 */
	getAsMimeAttachment(): MimeAttachment;
}
