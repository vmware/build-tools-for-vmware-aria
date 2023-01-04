namespace vroapi {
    /**
     * vRO ConfigurationElement intrinsic class representation
     */
    export class ConfigurationElement {

        name: string;

        description: string;

        version: string;

        readonly versionHistoryItems: VersionHistoryItem[] = [];

        configurationElementCategory: ConfigurationElementCategory;

        private _attributes: Attribute[];
        get attributes(): Attribute[] {
            return this._attributes || (this._attributes = configurations.getElementAttributes(this.configurationElementCategory.path, this.name))
        }

        /**
        * Returns the attribute of the configuration element for the specified key.
        * @param key
        */
        getAttributeWithKey(key: string): Attribute {
            const attribute = this.attributes.find(a => a.name === key);

            return typeof attribute === 'undefined' ? null : attribute;
        }

        /**
         * Sets the attribute value of the configuration element for the specified key.
         * @param key
         * @param value
         */
        setAttributeWithKey(key: string, value: any, typeHint?: any): void {
            let attr = this.getAttributeWithKey(key);
            if (!attr) {
                attr = new Attribute();
                attr.name = key;
                this._attributes.push(attr);
            }
            attr.value = value;
            attr.type = typeHint;
        }

        /**
         * Remove the attribute of the configuration element for the specified key.
         * @param key
         */
        removeAttributeWithKey(key: string): void {
            const index = this.attributes.findIndex(a => a.name === key);
            this.attributes.splice(index, 1);
        }

        /**
         * Saves a change set in the local version repository.
         * This function is available with vRA 8
         */
        saveToVersionRepository(): void {
            throw new NotSupportedError();
        }

        /**
         * Reloads the values of the attributes of this configuration element.
         */
        reload(): void {
            // Since no changes are made to the file system there should be no action here
            // All changes are made in the runtime and stored in memory
        }
    }

    global.ConfigurationElement = ConfigurationElement;
}
