/*
 * #%L
 * vro-scripting-api
 * %%
 * Copyright (C) 2023 VMware
 * %%
 * Build Tools for VMware Aria
 * Copyright 2023 VMware, Inc.
 * 
 * This product is licensed to you under the BSD-2 license (the "License"). You may not use this product except in compliance with the BSD-2 License.  
 * 
 * This product may include a number of subcomponents with separate copyright notices and license terms. Your use of these subcomponents is subject to the terms and conditions of the subcomponent's license, as noted in the LICENSE file.
 * #L%
 */
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
