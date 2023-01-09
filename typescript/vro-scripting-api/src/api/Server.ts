namespace vroapi {
    /**
     * vRO Server intrinsic class representation
     */
    export class Server {
        /**
         * Create a new Authorization Element.
         * @param name Authorization Element name
         * @param ldapElement Authorization Element name
         * @param description (Optional) Element name
         */
        static createAuthorizationElement(name: string, ldapElement: any, description: string): AuthorizationElement {
            throw new NotSupportedError();
        }

        /**
         * Create a new configuration element. Since 7.0.
         * @param category The category. ConfigurationElementCategory or path (if the path does not exist it will be created)
         * @param name The name of the element
         */
        static createConfigurationElement(category: string | ConfigurationElementCategory, name: string): ConfigurationElement {
            const categoryPath = typeof category === "string" ? category : category.path;
            return configurations.createElement(categoryPath, name);
        }

        /**
         * Create a new resource element.
         * @param category The category. ResourceElementCategory or path (if the path does not exist it will be created)
         * @param name The name of the element
         * @param fileNameOrMime (Optional) The full path filename (type:string) or the mime attachment (type:MimeAttachment)
         * @param mimeType (Optional) The mime type of the content (if a fileName is passed in argument)
         */
        static createResourceElement(category: string | ResourceElementCategory, name: string, fileNameOrMime?: any, mimeType?: string): ResourceElement {
            const categoryPath = typeof category === "string" ? category : category.path;
            const element = resources.createElement(categoryPath, name);
            if (fileNameOrMime) {
                if (fileNameOrMime instanceof MimeAttachment) {
                    element.setContentFromMimeAttachment(fileNameOrMime);
                }
            }
            if (mimeType) {
                element.mimeType = mimeType;
            }
            return element;

        }

        /**
         * Log a text in the server with a ERROR threshold. The log will be stored in the database.
         * @param text text to log
         * @param info Additional info (optional)
         */
        static error(text: string, info?: string): void {
            throw new NotSupportedError();
        }

        /**
         * Returns the log events matching the query.
         * @param query The log query
         */
        static fetchLogEvents(query: LogQuery): LogEvent[] {
            throw new NotSupportedError();
        }

        /**
         * Return all the objects of the given type.
         * @param type The type name
         * @param query (Optional) Custom query depending on plug-in implementation.
         */
        static findAllForType(type: string, query?: string): any[] {
            throw new NotSupportedError();
        }

        /**
         * Return the object of the given type that has the given id.
         * @param type The type name
         * @param id The object id
         */
        static findForType(type: string, id: string): any {
            throw new NotSupportedError();
        }

        /**
         * Returns global tags associated with an object.
         * @param Properties Tagged object. All system types (worlfow, action, configurations) and plug-in types are supported.
         */
        static findGlobalTagsForObject(Properties: any): Properties {
            throw new NotSupportedError();
        }

        /**
         * Returns private tags associated with an object.
         * @param Properties Tagged object. All system types (worlfow, action, configurations) and plug-in types are supported.
         */
        static findTagsForObject(Properties: any): Properties {
            throw new NotSupportedError();
        }

        /**
         * Get the distinct tag names. Including tags names created by other users.
         */
        static findTagsInUse(): string[] {
            throw new NotSupportedError();
        }

        /**
         * Get a Server's Object by it String representation.
         * @param rep StringRepresentation object
         */
        static fromStringRepresentation(rep: String): any {
            throw new NotSupportedError();
        }

        /**
         * Get a Server's Object by it URI.
         * @param uri Object uri
         */
        static fromUri(uri: string): any {
            throw new NotSupportedError();
        }

        /**
         * Returns all configuration element categories. (top level ones)
         */
        static getAllConfigurationElementCategories(): ConfigurationElementCategory[] {
            return configurations.getRootCategories();
        }

        /**
         * Return list of all attribute description for the given type.
         * @param type The type name
         */
        static getAllDescriptionsForType(type: string): string[] {
            throw new NotSupportedError();
        }

        /**
         * Return list of all attribute display name for the given type.
         * @param type The type name
         */
        static getAllDisplayNamesForType(type: string): string[] {
            throw new NotSupportedError();
        }

        /**
         * Return list of all attribute names for the given type.
         * @param type The type name
         */
        static getAllNamesForType(type: string): string[] {
            throw new NotSupportedError();
        }

        /**
         * Return all Plugin modules information.
         */
        static getAllPluginInfo(): PluginModuleDescription[] {
            throw new NotSupportedError();
        }

        /**
         * Return list of all registered plugin types in the server.
         */
        static getAllPluginTypes(): any[] {
            throw new NotSupportedError();
        }

        /**
         * Return all policy template categories.
         */
        static getAllPolicyTemplateCategories(): PolicyTemplateCategory[] {
            throw new NotSupportedError();
        }

        /**
         * Returns all resource element categories.
         */
        static getAllResourceElementCategories(): ResourceElementCategory[] {
            return resources.getRootCategories();
        }

        /**
         * Return all workflow categories.
         */
        static getAllWorkflowCategories(): WorkflowCategory[] {
            throw new NotSupportedError();
        }

        /**
         * Returns authorization element with the given name.
         * @param name Element name
         */
        static getAuthorizationElementForName(name: string): AuthorizationElement {
            throw new NotSupportedError();
        }

        /**
         * Returns authorization elements for the given ldap element.
         * @param ldapElement LDAP Element
         */
        static getAuthorizationElementsForLdapElement(ldapElement: any): AuthorizationElement[] {
            throw new NotSupportedError();
        }

        /**
         * Return a configuration element category matching the given path or null if not found.
         * @param path The path to the configuration element category using forward slash(/) as separator.
         */
        static getConfigurationElementCategoryWithPath(path: string): ConfigurationElementCategory {
            return configurations.getCategory(path);
        }

        /**
         * Return the credential associated with the running script
         */
        static getCredential(): Credential {
            throw new NotSupportedError();
        }

        /**
         * Return the ldap user associated with the running script
         */
        static getCurrentLdapUser(): LdapUser {
            throw new NotSupportedError();
        }

        /**
         * Get a custom property to any plugin object.
         * @param target Holder object
         * @param key property key
         */
        static getCustomProperty(target: any, key: string): any {
            throw new NotSupportedError();
        }

        /**
         * Return all custom property keys available for a plugn object.
         * @param target Holder object
         */
        static getCustomPropertyKeys(target: any): string[] {
            throw new NotSupportedError();
        }

        /**
         * Return an ldap element with the given DN
         * @param dn Distinguished name
         */
        static getLdapElement(dn: string): any {
            throw new NotSupportedError();
        }

        /**
         * Return all plugin objects with a given key.
         * @param key Property key
         */
        static getObjectsURIWithCustomPropertyKey(key: string): string[] {
            throw new NotSupportedError();
        }

        /**
         * Return all plugin objects with a given key.
         * @param key Property key
         */
        static getObjectsWithCustomPropertyKey(key: string): any[] {
            throw new NotSupportedError();
        }

        /**
         * Return a package given its name or null if not found.
         * @param name The package name.
         */
        static getPackageWithName(name: string): Package {
            throw new NotSupportedError();
        }

        /**
         * Return a plugin type info for a given type if exists.
         * @param type Full SDK type info (i.e. module:type)
         */
        static getPluginTypeInfo(type: string): PluginTypeDescription {
            throw new NotSupportedError();
        }

        /**
         * Return a policy template category matching the given path or null if not found.
         * @param path The path to the policy template category using forward slash(/) as separator.
         */
        static getPolicyTemplateCategoryWithPath(path: string): PolicyTemplateCategory {
            throw new NotSupportedError();
        }

        /**
         * Return a resource element category matching the given path or null if not found.
         * @param path The path to the resource element category using forward slash(/) as separator.
         */
        static getResourceElementCategoryWithPath(path: string): ResourceElementCategory {
            return resources.getCategory(path);
        }

        /**
         * Returns the running user display name.
         */
        static getRunningUser(): string {
            throw new NotSupportedError();
        }

        /**
         * Returns the signature finger print of an element.
         * @param element The element
         */
        static getSignatureFingerPrint(element: any): string {
            throw new NotSupportedError();
        }

        /**
         * Returns the signature owner of an element.
         * @param element The element
         */
        static getSignatureOwner(element: any): string {
            throw new NotSupportedError();
        }

        /**
         * Return a workflow category matching the given path or null if not found.
         * @param path The path to the workflow category using forward slash(/) as separator.
         */
        static getWorkflowCategoryWithPath(path: string): WorkflowCategory {
            throw new NotSupportedError();
        }

        /**
         * Return wokflow token state.
         * @param token_id The id of the workflow token.
         */
        static getWorkflowTokenState(token_id: string): string {
            throw new NotSupportedError();
        }

        /**
         * Get a Workflow with a givent ID.
         * @param id Workflow id
         */
        static getWorkflowWithId(id: string): Workflow {
            throw new NotSupportedError();
        }

        /**
         * Check if the Credential is a valid LDAP Credential.
         * @param credential Credential object
         */
        static isValidLdapCredential(credential: Credential): boolean {
            throw new NotSupportedError();
        }

        /**
         * Log a text in the server with a INFO threshold. The log will be stored in the database.
         * @param text text to log
         * @param info Additional info (optional)
         */
        static log(text: string, info?: string): void {
            throw new NotSupportedError();
        }

        /**
         * Return all the objects of the given type and matching the query expression.
         * @param type The type name
         * @param queryExp If 'type' is a vCO type, 'query' is for internal use only. If 'type' is a plug-in type, the optional 'query' parameter is a custom query depending on plug-in implementation.
         * @param maxCount (Optional) Maximum number of elements , by default all.
         */
        static query(type: string, queryExp: string, maxCount?: number): any[] {
            throw new NotSupportedError();
        }

        /**
         * Return objects of the given type that are tagged with provided tags. To search for global tags prefix the tag name with ":".
         * @param tagQuery Tag query in following format [{'tag':'tagname', 'value':'tagvalue'},{'tag':':anothertag', 'value':'tagvalue'}]
         * @param type Used to filter returned objects by provided type
         */
        static queryByTags(tagQuery: any, type: string): any[] {
            throw new NotSupportedError();
        }

        /**
         * Remove the access rights of an element.
         * @param owner The element
         * @param user User or group
         */
        static removeAccessRights(owner: any, user: LdapGroup | LdapUser | string): void {
            throw new NotSupportedError();
        }

        /**
         * Remove all custom properties for a given object.
         * @param target Holder object
         */
        static removeAllCustomPropertiesForObject(target: any): void {
            throw new NotSupportedError();
        }

        /**
         * Remove all custom properties for a given object TYPE.
         * @param type Holder object type
         */
        static removeAllCustomPropertiesForType(type: string): void {
            throw new NotSupportedError();
        }

        /**
         * Remove all custom properties for a given object TYPE and KEY.
         * @param type Holder object type
         * @param key Property key
         */
        static removeAllCustomPropertiesForTypeAndKey(type: string, key: string): void {
            throw new NotSupportedError();
        }

        /**
         * Remove an existing configuration element.
         * @param configurationElement Configuration element to remove
         */
        static removeConfigurationElement(configurationElement: ConfigurationElement): void {
            configurations.removeElement(configurationElement.configurationElementCategory.path, configurationElement.name);
        }

        /**
         * Remove an existing configuration element folder.
         * @param configurationElementCategory Configuration element folder to remove
         */
        static removeConfigurationElementCategory(configurationElementCategory: ConfigurationElementCategory): void {
            configurations.removeCategory(configurationElementCategory.path);
        }

        /**
         * Remove an existing Authorization Element.
         * @param authorizationElement Authorization element to remove
         */
        static removeAuthorizationElement(authorizationElement: AuthorizationElement): void {
            throw new NotSupportedError();
        }

        /**
         * Remove a custom property to any plugin object.
         * @param target Holder object
         * @param key property key
         */
        static removeCustomProperty(target: any, key: string): void {
            throw new NotSupportedError();
        }

        /**
         * Remove an existing resource element.
         * @param resourceElement Resource element to remove
         */
        static removeResourceElement(resourceElement: ResourceElement): void {
            resources.removeElement(resourceElement.resourceElementCategory.path, resourceElement.name);
        }

        /**
         * Return all ldap groups matching the query's name
         * @param query Query string (i.e. 'test*')
         * @param limit Max number of records to retrieve
         */
        static searchLdapGroups(query: string, limit: number): LdapGroup[] {
            throw new NotSupportedError();
        }

        /**
         * Return all ldap users matching the query's name
         * @param query Query string (i.e. 'test*')
         * @param limit Max number of records to retrieve
         */
        static searchLdapUsers(query: string, limit: number): LdapUser[] {
            throw new NotSupportedError();
        }

        /**
         * Set the access rights of an element.
         * @param owner The element
         * @param user User or group
         * @param rights Rights (r,x,i)
         */
        static setAccessRights(owner: any, user: LdapGroup | LdapUser | string, rights: string): void {
            throw new NotSupportedError();
        }

        /**
         * Set a custom property to any plugin object.
         * @param target Holder object
         * @param key property key
         * @param value Property value
         */
        static setCustomProperty(target: any, key: string, value: any): void {
            throw new NotSupportedError();
        }

        /**
         * Tags an object with a specific tag. The created tag is private and is visible only to the user who created the tag. The tag name must be between 3 and 64 characters.
         * @param object Tagged object. All system types (worlfow, action, configurations) and plug-in types are supported.
         * @param tag Tag name
         * @param value Tag value
         */
        static tag(object: any, tag: string, value: string): void {
            throw new NotSupportedError();
        }

        /**
         * Tags an object with a specific tag. The created tag is global and is visible to all users. Global tags can be created only by users with administrator privileges. The tag name must be between 3 and 64 characters.
         * @param object Tagged object. All system types (worlfow, action, configurations) and plug-in types are supported.
         * @param tag Tag name
         * @param value Tag value
         */
        static tagGlobally(object: any, tag: string, value: string): void {
            throw new NotSupportedError();
        }

        /**
         * Convert a Server's Object to a String representation.
         * @param object Object to convert
         */
        static toStringRepresentation(object: any): String {
            throw new NotSupportedError();
        }

        /**
         * Convert a Server's Object to an URI.
         * @param object Object to convert
         */
        static toUri(object: any): string {
            throw new NotSupportedError();
        }

        /**
         * Removes a private tag from an object.
         * @param object Tagged object. All system types (worlfow, action, configurations) and plug-in types are supported.
         * @param tags List of tags to be removed
         */
        static untag(object: any, tags: string[]): void {
            throw new NotSupportedError();
        }

        /**
         * Removes a global tag from an object.  Global tags can be removed only by users with administrator privileges.
         * @param object Tagged object. All system types (worlfow, action, configurations) and plug-in types are supported.
         * @param tags List of tags to be removed
         */
        static untagGlobally(object: any, tags: string[]): void {
            throw new NotSupportedError();
        }

        /**
         * Log a text in the server with a WARN threshold. The log will be stored in the database.
         * @param text text to log
         * @param info Additional info (optional)
         */
        static warn(text: string, info?: string): void {
            throw new NotSupportedError();
        }
    }

    global.Server = Server as any;
}
