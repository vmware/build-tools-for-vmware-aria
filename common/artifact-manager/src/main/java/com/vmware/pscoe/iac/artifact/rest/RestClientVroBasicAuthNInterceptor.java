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

import java.io.IOException;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import com.vmware.pscoe.iac.artifact.configuration.ConfigurationVro;

public class RestClientVroBasicAuthNInterceptor extends RestClientRequestInterceptor<ConfigurationVro> {

	protected RestClientVroBasicAuthNInterceptor(ConfigurationVro configuration, RestTemplate restTemplate) {
		super(configuration, restTemplate);
	}

	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
		try {
			String username = this.getConfiguration().getUsername();

			// Basic authentication could work with user without domain thus handling this case here.
			if (!StringUtils.isEmpty(this.getConfiguration().getDomain()) && !username.contains("@")) {
				username += "@" + this.getConfiguration().getDomain();
			}

			return new BasicAuthorizationInterceptor(username, this.getConfiguration().getPassword()).intercept(request, body, execution);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
