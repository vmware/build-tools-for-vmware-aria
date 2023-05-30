package com.vmware.pscoe.iac.artifact.configuration;

/*
 * #%L
 * artifact-manager
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

import java.util.Properties;
import org.springframework.util.StringUtils;

import com.vmware.pscoe.iac.artifact.model.PackageType;

public class ConfigurationIdem extends ConfigurationSsh {

    public static final String IDEM_PLUGIN_INSTALL_FILE = "install.py";
    public static final String PKG_FOLDER = "pkgFolder";
    private static final String EMPTY_PROP = "Empty property";
    public static final String PACKAGE_IMPORT_OVERWRITE_MODE = "packageImportOverwriteMode";
    public static final String MSSING_PROPERTY_EXCEPTION = "Configuration validation failed. Empty IDEM property. Please make sure you have defined the IDEM property '%s'. You may define that in maven 'settings.xml'.";

    protected ConfigurationIdem(Properties props) {
        super(PackageType.IDEM, props);
    }

    @Override
    public void validate(boolean domainOptional) throws ConfigurationException {
        try {
            if (StringUtils.isEmpty(this.properties.getProperty(PKG_FOLDER))) {
                throw new ConfigurationException(EMPTY_PROP);
            }
        } catch (Exception e) {
            throw new ConfigurationException(String.format(MSSING_PROPERTY_EXCEPTION, PKG_FOLDER), e);
        }
        super.validate(domainOptional);
    }

    public static ConfigurationIdem fromProperties(Properties props) throws ConfigurationException {
        ConfigurationIdem config = new ConfigurationIdem(props);

        config.validate(true);

        return config;
    }
}
