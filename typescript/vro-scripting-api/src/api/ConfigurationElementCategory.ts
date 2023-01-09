namespace vroapi {
    /**
     * vRO ConfigurationElementCategory intrinsic class representation
     */
    export class ConfigurationElementCategory {
        name: string;

        description: string;

        path: string;

        parent: ConfigurationElementCategory | undefined;

        private _configurationElements: ConfigurationElement[];
        get configurationElements(): ConfigurationElement[] {
            return this._configurationElements || (this._configurationElements = configurations.getElements(this.path));
        }

        get allConfigurationElements(): ConfigurationElement[] {
            return this.configurationElements;
        }

        private _subCategories: ConfigurationElementCategory[];
        get subCategories(): ConfigurationElementCategory[] {
            return this._subCategories || (this._subCategories = configurations.getCategories(this.path));
        }

        invalidateElements() {
            this._configurationElements = null;
        }

        invalidateSubCategories() {
            this._subCategories = null;
        }
    }

    global.ConfigurationElementCategory = ConfigurationElementCategory;
}
