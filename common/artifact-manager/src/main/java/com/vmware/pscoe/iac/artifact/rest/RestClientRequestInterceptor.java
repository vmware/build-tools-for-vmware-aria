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
package com.vmware.pscoe.iac.artifact.rest;

import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import com.vmware.pscoe.iac.artifact.configuration.Configuration;

public abstract class RestClientRequestInterceptor<T extends Configuration> implements ClientHttpRequestInterceptor {

	private RestTemplate restTemplate;
	private T operationsContext;

	private RestClientRequestInterceptor() {
	}

	protected RestClientRequestInterceptor(T operationsContext, RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
		this.operationsContext = operationsContext;
	}

	protected RestTemplate getRestTemplate() {
		return restTemplate;
	}

	protected T getConfiguration() {
		return operationsContext;
	}

}
