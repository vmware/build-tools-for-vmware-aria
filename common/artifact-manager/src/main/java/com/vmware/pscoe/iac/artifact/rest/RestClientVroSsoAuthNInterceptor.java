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

import com.vmware.pscoe.iac.artifact.configuration.ConfigurationVro;
import com.vmware.pscoe.iac.artifact.rest.auth.VraSsoAuth;
import com.vmware.pscoe.iac.artifact.rest.auth.VraSsoAuth.SsoToken;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

public class RestClientVroSsoAuthNInterceptor extends RestClientRequestInterceptor<ConfigurationVro> {
	private final VraSsoAuth ssoAuth;
	private SsoToken token;
	private static final String SSO_REGISTRY_URL = "/component-registry/endpoints/types/sso";
	private static final String AUTHORIZATION_SERVICE_URL_VRA_8 = "/csp/gateway/am/api/login";
	private static final String AUTHORIZATION_SERVICE_URL_VRA_CLOUD = "/csp/gateway/am/api/auth/api-tokens/authorize";
	private static final String SAAS_URL = "/SAAS/t/";
	private static final String VERSION_URL= "vco/api/about";

	protected RestClientVroSsoAuthNInterceptor(ConfigurationVro configuration, RestTemplate restTemplate) {
		super(configuration, restTemplate);

		this.ssoAuth = new VraSsoAuth(configuration, restTemplate);
	}

	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) {
		try {
			// blacklisted paths should not be intercepted in order to avoid interception loops
			if (!isRequestPathInBlacklist(request.getURI().getPath())) {
				this.token = this.token != null && !this.token.isExpired() ? this.token : this.ssoAuth.acquireToken();
				request.getHeaders().add("Authorization", this.token.getTokenType() + " " + this.token.getValue());
			}

			return execution.execute(request, body);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private boolean isRequestPathInBlacklist(String requestPath) {
		return requestPath.contains(SSO_REGISTRY_URL) || requestPath.contains(SAAS_URL) || requestPath.contains(VERSION_URL)
				|| requestPath.contains(AUTHORIZATION_SERVICE_URL_VRA_8) || requestPath.contains(AUTHORIZATION_SERVICE_URL_VRA_CLOUD);
	}
}
