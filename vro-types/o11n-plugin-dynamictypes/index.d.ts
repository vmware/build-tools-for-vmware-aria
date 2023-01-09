/**
 * Provides operations for DynamicNamespaceDefinition objects.
 */
declare interface DynamicTypesDynamicNamespaceDefinition {
	name: string;
	readonly id: string;
	readonly type: string;
}

/**
 * Provides operations for DynamicObject objects.
 */
declare interface DynamicTypesDynamicObject {
	readonly name: string;
	readonly namespace: string;
	readonly type: string;
	readonly id: string;
	readonly cachedProperties: any;
	readonly properties: any;
	/**
	 * Set the value of a given property. Will create a new property if no property with such name exist.
	 * @param property 
	 * @param value 
	 */
	setProperty(property: string, value: any): void;
	/**
	 * Get the value of a given property. Throws an exception if no property with such name exist.
	 * @param property 
	 */
	getProperty(property: string): any;
	/**
	 * Get the value of a given property from the cache. Throws an exception if no property with such name exists.
	 * @param property 
	 */
	getPropertyInCache(property: string): any;
	/**
	 * Set the value of a given property in the cache with expire time limit. Will create a new property if no property with such name exists.
	 * @param property 
	 * @param value 
	 * @param seconds 
	 */
	setPropertyInCache(property: string, value: any, seconds: number): void;
	/**
	 * Get the insertion-ordered list of property names.
	 */
	getOrderedPropertyNames(): string[];
	/**
	 * Get the inserted-ordered list of property names in cache.
	 */
	getOrderedPropertyNamesInCache(): string[];
}

/**
 * Provides operations for DynamicObjectProxy objects.
 */
declare interface DynamicTypesDynamicObjectProxy {
	readonly name: string;
	readonly namespace: string;
	readonly type: string;
	readonly id: string;
	readonly properties: any;
	/**
	 * Set the value of a given property. Will create a new property if no property with such name exist.
	 * @param property 
	 * @param value 
	 */
	setProperty(property: string, value: any): void;
	/**
	 * Get the value of a given property. Throws an exception if no property with such name exist.
	 * @param property 
	 */
	getProperty(property: string): any;
	/**
	 * Check if the proxied object can be resolved.
	 */
	exists(): boolean;
	/**
	 * Get the insertion-ordered list of property names.
	 */
	getOrderedPropertyNames(): string[];
}

/**
 * Provides operations for DynamicTypeDefinition objects.
 */
declare interface DynamicTypesDynamicTypeDefinition {
	name: string;
	properties: string[];
	readonly id: string;
	readonly type: string;
	namespace: string;
	iconResource: string;
	readonly findByIdBinding: string;
	readonly findAllBinding: string;
	readonly hasChildrenInRelationBinding: string;
	readonly findRelationBinding: string;
}

/**
 * Provides operations for RelationDefinition objects.
 */
declare interface DynamicTypesRelationDefinition {
	readonly name: string;
	readonly id: string;
	readonly childType: string;
	readonly parentType: string;
}

declare interface DynamicTypesTypeHierarchyRootFolder {
}

/**
 * Provides operations for all dynamic objects.
 */
declare class DynamicTypesManager {
	/**
	 * Get object from cache.
	 * @param key 
	 */
	static getFromCache(key: string): any;
	/**
	 * Get a reference to an instance of a given dynamic type.
	 * @param namespace 
	 * @param type 
	 * @param id 
	 */
	static getObject(namespace: string, type: string, id: string): DynamicTypesDynamicObjectProxy;
	static getObject<T>(namespace: string, type: string, id: string): T;
	/**
	 * Get dynamic type given its namesapce and name.
	 * @param namespace 
	 * @param type 
	 */
	static getType(namespace: string, type: string): DynamicTypesDynamicTypeDefinition;
	/**
	 * Remove all dynamic namespaces/types/relations.
	 */
	static removeAll(): void;
	/**
	 * Get a dynamic namespace with a given name.
	 * @param namespace 
	 */
	static getNamespace(namespace: string): DynamicTypesDynamicNamespaceDefinition;
	/**
	 * Notify the system that an object has changed.
	 * @param namespace 
	 * @param type 
	 * @param id 
	 */
	static invalidate(namespace: string, type: string, id: string): void;
	/**
	 * Remove dynamic type definition.
	 * @param namespace 
	 * @param type 
	 */
	static removeType(namespace: string, type: string): void;
	/**
	 * @param parentType 
	 * @param childType 
	 * @param relationName 
	 */
	static defineRelation(parentType: string, childType: string, relationName: string): void;
	/**
	 * @param parentType 
	 * @param childType 
	 */
	static removeRelation(parentType: string, childType: string): void;
	static getAllRelations(): DynamicTypesRelationDefinition[];
	/**
	 * Export configuration to package.
	 * @param namespacesToExport 
	 */
	static exportConfigurationAsPackage(namespacesToExport: DynamicTypesDynamicNamespaceDefinition[]): any;
	/**
	 * Import configuration from package.
	 * @param namespacesToImport 
	 * @param packageContent 
	 */
	static importConfigurationFromPackage(namespacesToImport: string[], packageContent: any): boolean;
	/**
	 * Validate configuration package.
	 * @param packageContent 
	 */
	static validateConfigurationPackage(packageContent: any): boolean;
	/**
	 * Assign workflow/action bindings for a type.
	 * @param namespace 
	 * @param type 
	 * @param findByIdBinding 
	 * @param findAllBinding 
	 * @param hasChildrenInRelationBinding 
	 * @param findRelationBinding 
	 */
	static bindTypeFinderMethods(namespace: string, type: string, findByIdBinding: string, findAllBinding: string, hasChildrenInRelationBinding: string, findRelationBinding: string): void;
	/**
	 * Generate workflow bindings for a type and return their IDs.
	 * @param namespace 
	 * @param type 
	 * @param category 
	 */
	static generateTypeFinderMethods(namespace: string, type: string, category: any): string[];
	/**
	 * Generate dynamic type definitions for complex types defined in a XSD schema.
	 * @param xsdUri 
	 * @param namespace 
	 * @param category 
	 */
	static importTypesFromXSD(xsdUri: string, namespace: string, category: any): void;
	/**
	 * Get a list of all keys in cache.
	 */
	static getCacheKeys(): string[];
	/**
	 * Create new instance of a given dynamic type.
	 * @param namespace 
	 * @param type 
	 * @param id 
	 * @param name 
	 * @param props 
	 */
	static makeObject(namespace: string, type: string, id: string, name: string, props: string[]): DynamicTypesDynamicObject;
	/**
	 * Remove object from cache.
	 * @param key 
	 */
	static removeFromCache(key: string): any;
	/**
	 * Define a new dynamic namespace.
	 * @param namespace 
	 */
	static defineNamespace(namespace: string): DynamicTypesDynamicNamespaceDefinition;
	/**
	 * Get all defined dynamic namespaces.
	 */
	static getAllNamespaces(): DynamicTypesDynamicNamespaceDefinition[];
	/**
	 * Get all defined dynamic namespaces in the configuration package.
	 * @param packageContent 
	 */
	static getAllNamespacesByPackage(packageContent: any): string[];
	/**
	 * @param name 
	 * @param newName 
	 */
	static updateNamespace(name: string, newName: string): DynamicTypesDynamicNamespaceDefinition;
	/**
	 * Remove a dynamic namespace. All dynamic types that belong to this namespace are also removed.
	 * @param namespace 
	 */
	static removeNamespace(namespace: string): void;
	/**
	 * Define a new dynamic type within a given namespace.
	 * @param namespace 
	 * @param type 
	 * @param icon 
	 * @param properties 
	 * @param actions 
	 */
	static defineType(namespace: string, type: string, icon: string, properties: string[], actions: any[]): DynamicTypesDynamicTypeDefinition;
	/**
	 * Get all defined dynamic types.
	 */
	static getAllTypes(): DynamicTypesDynamicTypeDefinition[];
	/**
	 * Add custom property to dynamic type.
	 * @param namespace 
	 * @param type 
	 * @param key 
	 * @param value 
	 */
	static setTypeCustomProperty(namespace: string, type: string, key: string, value: any): void;
	/**
	 * Get custom property by key.
	 * @param namespace 
	 * @param type 
	 * @param key 
	 */
	static getTypeCustomProperty(namespace: string, type: string, key: string): any;
	/**
	 * Remove dynamic type's custom property by key
	 * @param namespace 
	 * @param type 
	 * @param key 
	 */
	static removeTypeCustomProperty(namespace: string, type: string, key: string): void;
	/**
	 * @param namespace 
	 * @param type 
	 * @param newNamespace 
	 * @param newType 
	 * @param newIcon 
	 * @param newProperties 
	 */
	static updateType(namespace: string, type: string, newNamespace: string, newType: string, newIcon: string, newProperties: string[]): DynamicTypesDynamicTypeDefinition;
	/**
	 * Put object in cache.
	 * @param key 
	 * @param value 
	 */
	static putInCache(key: string, value: any): void;
}
