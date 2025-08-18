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
package com.vmware.pscoe.iac.artifact.common.configuration;

import java.util.Properties;

import com.vmware.pscoe.iac.artifact.model.PackageType;

public abstract class ConfigurationWithRefreshToken extends Configuration {

	public static final String REFRESH_TOKEN = "refresh.token";

	protected ConfigurationWithRefreshToken(PackageType type, Properties props) {
		super(type, props);
	}

	public abstract String getRefreshToken();
}
