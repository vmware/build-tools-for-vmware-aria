/**
 * Main class to create xml document parser
 */
declare class XMLManager {
	/**
	 * Create a new Empty XML document
	 */
	static newDocument(): XMLDocument;
	/**
	 * Get the document for a given path using the default character encoding
	 * @param path 
	 * @param validate 
	 */
	static loadDocument(path: string, validate: boolean): XMLDocument;
	/**
	 * Get the document for a given path using the specified character encoding
	 * @param path 
	 * @param encoding 
	 * @param validate 
	 */
	static loadDocumentWithEncoding(path: string, encoding: string, validate: boolean): XMLDocument;
	/**
	 * Get the document for a given string content
	 * @param stringContext 
	 */
	static fromString(stringContext: string): XMLDocument;
	/**
	 * Save the document to a given path using the default character encoding
	 * @param document 
	 * @param path 
	 * @param systemDtD 
	 * @param publicDtD 
	 */
	static saveDocument(document: XMLDocument, path: string, systemDtD: string, publicDtD: string): any;
	/**
	 * Save the document to a given path using the default character encoding
	 * @param document 
	 * @param path 
	 * @param encoding 
	 * @param systemDtD 
	 * @param publicDtD 
	 */
	static saveDocumentWithEncoding(document: XMLDocument, path: string, encoding: string, systemDtD: string, publicDtD: string): any;
	/**
	 * Validate a document without saving it
	 * @param document 
	 * @param systemDtD 
	 * @param publicDtD 
	 */
	static validateDocument(document: XMLDocument, systemDtD: string, publicDtD: string): any;
	/**
	 * Get the document as string
	 * @param document 
	 */
	static getDocumentContent(document: XMLDocument): string;
}

/**
 * XML document
 */
declare interface XMLDocument {
	/**
	 * This is a convenience attribute that allows direct access to the child node that is the root element of the document.
	 */
	getDocumentElement(): XMLElement;
	/**
	 * Create a new element with the given name
	 * @param name 
	 */
	createElement(name: string): XMLElement;
	/**
	 * Create a new comment node
	 * @param name 
	 */
	createComment(name: string): XMLNode;
	/**
	 * Create a text node
	 * @param name 
	 */
	createTextNode(name: string): XMLNode;
	/**
	 * Create a new CDATA node
	 * @param name 
	 */
	createCDATASection(name: any): XMLNode;
	/**
	 * Create a ProcessingInstruction node given the specified name and data strings.
	 * @param target 
	 * @param data 
	 */
	createProcessingInstruction(target: string, data: string): XMLNode;
	/**
	 * Normalize the document
	 */
	normalize(): void;
	/**
	 * Returns a XMLNodeList of all the Elements with a given tag name in the order in which they are encountered in a preorder traversal of the XMLDocument tree.
	 * @param name 
	 */
	getElementsByTagName(name: string): void;
	/**
	 * Adds the node newChild to the end of the list of children of this node.
	 * If the newChild is already in the tree, it is first removed.
	 * @param child 
	 */
	appendChild(child: XMLNode): void;
	/**
	 * Removes the child node indicated by oldChild from the list of children, and returns it
	 * @param oldChild 
	 */
	removeChild(oldChild: XMLNode): void;
	/**
	 * Replaces the child node oldChild with newChild in the list of children, and returns the oldChild node.
	 * If the newChild is already in the tree, it is first removed.
	 * @param newNode 
	 * @param oldNode 
	 */
	replaceChild(newNode: XMLNode, oldNode: XMLNode): void;
	/**
	 * Inserts the node newChild before the existing child node refChild.
	 * If refChild is null, insert newChild at the end of the list of children.
	 * If the newChild is already in the tree, it is first removed.
	 * @param newNode 
	 * @param refNode 
	 */
	insertBefore(newNode: XMLNode, refNode: XMLNode): void;
	/**
	 * Clone the node
	 * @param deep 
	 */
	cloneNode(deep: boolean): void;
	/**
	 * Get all children
	 */
	getChildNodes(): void;
}

/**
 * Main Element
 */
declare interface XMLElement {
	textContent: void;
	tagName: void;
	/**
	 * A XMLNamedNodeMap containing the attributes of this Element.
	 */
	getAttributes(): void;
	/**
	 * Set a new attribute
	 * @param name 
	 * @param value 
	 */
	setAttribute(name: string, value: string): void;
	/**
	 * Remove atrribute with the given name
	 * @param name 
	 */
	removeAttribute(name: string): void;
	/**
	 * Returns true if the attribute exists
	 * @param name 
	 */
	hasAttribute(name: string): void;
	/**
	 * Returns a XMLNodeList of all the Elements with a given tag name in the order in which they are encountered in a preorder traversal of the current element tree.
	 * @param name 
	 */
	getElementsByTagName(name: string): void;
	/**
	 * Adds the node newChild to the end of the list of children of this node.
	 * If the newChild is already in the tree, it is first removed.
	 * @param child 
	 */
	appendChild(child: XMLNode): void;
	/**
	 * Removes the child node indicated by oldChild from the list of children, and returns it
	 * @param oldChild 
	 */
	removeChild(oldChild: XMLNode): void;
	/**
	 * Replaces the child node oldChild with newChild in the list of children, and returns the oldChild node.
	 * If the newChild is already in the tree, it is first removed.
	 * @param newNode 
	 * @param oldNode 
	 */
	replaceChild(newNode: XMLNode, oldNode: XMLNode): void;
	/**
	 * Inserts the node newChild before the existing child node refChild.
	 * If refChild is null, insert newChild at the end of the list of children.
	 * If the newChild is already in the tree, it is first removed.
	 * @param newNode 
	 * @param refNode 
	 */
	insertBefore(newNode: XMLNode, refNode: XMLNode): void;
	/**
	 * Clone the node
	 * @param deep 
	 */
	cloneNode(deep: boolean): void;
	/**
	 * Normalize the node
	 */
	normalize(): void;
	/**
	 * Get all children
	 */
	getChildNodes(): XMLNodeList;
}

/**
 * Node Element
 */
declare interface XMLNode {
	nodeValue: void;
	nodeName: void;
	parentNode: void;
	/**
	 * Adds the node newChild to the end of the list of children of this node.
	 * If the newChild is already in the tree, it is first removed.
	 * @param child 
	 */
	appendChild(child: XMLNode): void;
	/**
	 * Removes the child node indicated by oldChild from the list of children, and returns it
	 * @param oldChild 
	 */
	removeChild(oldChild: XMLNode): void;
	/**
	 * Replaces the child node oldChild with newChild in the list of children, and returns the oldChild node.
	 * If the newChild is already in the tree, it is first removed.
	 * @param newNode 
	 * @param oldNode 
	 */
	replaceChild(newNode: XMLNode, oldNode: XMLNode): void;
	/**
	 * Inserts the node newChild before the existing child node refChild.
	 * If refChild is null, insert newChild at the end of the list of children.
	 * If the newChild is already in the tree, it is first removed.
	 * @param newNode 
	 * @param refNode 
	 */
	insertBefore(newNode: XMLNode, refNode: XMLNode): void;
	/**
	 * Clone the node
	 * @param deep 
	 */
	cloneNode(deep: boolean): void;
	/**
	 * Normalize the node
	 */
	normalize(): void;
	/**
	 * Get all children
	 */
	getChildNodes(): XMLNodeList;
}

/**
 * Node map, usually used for attributes of an element
 */
declare interface XMLNamedNodeMap {
	length: void;
	/**
	 * Get child at index
	 * @param index 
	 */
	item(index: number): XMLNode;
	/**
	 * Retrieves a node specified by name.
	 * @param name 
	 */
	getNamedItem(name: string): XMLNode;
}

/**
 * NodeList Element
 */
declare interface XMLNodeList {
	length: void;
	/**
	 * Get child at index
	 * @param index 
	 */
	item(index: number): void;
}
