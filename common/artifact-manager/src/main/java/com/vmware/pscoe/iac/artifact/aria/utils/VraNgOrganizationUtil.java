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
package com.vmware.pscoe.iac.artifact.aria.utils;

import com.vmware.pscoe.iac.artifact.aria.configuration.ConfigurationVraNg;
import com.vmware.pscoe.iac.artifact.aria.models.VraNgOrganization;
import com.vmware.pscoe.iac.artifact.aria.rest.RestClientVraNgPrimitive;
import org.apache.commons.lang3.StringUtils;

public final class VraNgOrganizationUtil {

	private VraNgOrganizationUtil() {
	}

	public static VraNgOrganization getOrganization(RestClientVraNgPrimitive restClient, ConfigurationVraNg config) {
		VraNgOrganization orgByName = null;
		if (StringUtils.isNotEmpty(config.getOrgName())) {
			orgByName = restClient.getOrganizationByName(config.getOrgName());
		}

		if (orgByName == null) {
			throw new RuntimeException(String.format("Couldn't find organization by the provided criteria - Name '%s'.",
					config.getOrgName()));
		}

		return orgByName;
	}
}
