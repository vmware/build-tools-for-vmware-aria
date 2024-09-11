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
package com.vmware.pscoe.iac.artifact.utils;

import com.vmware.pscoe.iac.artifact.model.vrang.VraNgIntegration;
import com.vmware.pscoe.iac.artifact.rest.RestClientVraNg;

import java.util.List;
import java.util.Optional;

public class VraNgIntegrationUtils {
	public static final String DEFAULT_INTEGRATION_NAME = "embedded-VRO";
	private static final VraNgIntegrationUtils instance = new VraNgIntegrationUtils();

	private Optional<VraNgIntegration> retVal = Optional.empty();

	private VraNgIntegrationUtils() {}

	public static VraNgIntegrationUtils getInstance() {
		return instance;
	}

	public VraNgIntegration getDefaultVraIntegration(RestClientVraNg restClient) {
		if(this.retVal.isPresent()) {
			return this.retVal.get();
		}

		List<VraNgIntegration> integrations = restClient.getVraWorkflowIntegrations();

		this.retVal = integrations.stream().filter(integration -> DEFAULT_INTEGRATION_NAME.equalsIgnoreCase(integration.getName()))
				.findFirst();

		return this.retVal.orElse(new VraNgIntegration());
	}

}
