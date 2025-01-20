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
package com.vmware.pscoe.iac.artifact.helpers.vro;

import com.vmware.pscoe.iac.artifact.aria.orchestrator.configuration.ConfigurationVro;
import com.vmware.pscoe.iac.artifact.aria.orchestrator.rest.RestClientVro;

import org.springframework.web.client.RestTemplate;

import java.util.Properties;

public class RestClientVroTestDouble extends RestClientVro {
	public RestClientVroTestDouble(ConfigurationVro configuration, RestTemplate restTemplate) {
		super(configuration, restTemplate);
	}

	@Override
	public Properties getInputParametersTypes(String workflowId) {
		return super.getInputParametersTypes(workflowId);
	}

	@Override
	public String buildParametersJson(Properties params, Properties inputParametersTypes) {
		return super.buildParametersJson(params, inputParametersTypes);
	}
}
