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
package com.vmware.pscoe.iac.artifact.aria.orchestrator.rest;

import java.io.IOException;
import java.util.Properties;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestTemplate;

import com.vmware.pscoe.iac.artifact.aria.orchestrator.configuration.ConfigurationVro;
import com.vmware.pscoe.iac.artifact.rest.RestClientRequestInterceptor;
import com.vmware.pscoe.iac.artifact.rest.auth.VraSsoAuth;
import com.vmware.pscoe.iac.artifact.rest.auth.VraSsoAuth.SsoToken;
import com.vmware.pscoe.iac.artifact.vcd.configuration.ConfigurationVcd;
import com.vmware.pscoe.iac.artifact.vcd.rest.RestClientVcd;
import com.vmware.pscoe.iac.artifact.vcd.rest.RestClientVcdBasicAuthInterceptor;

public class RestClientVroSsoAuthNInterceptor extends RestClientRequestInterceptor<ConfigurationVro> {
	private final VraSsoAuth ssoAuth;
	private SsoToken token;
	private RestClientVcdBasicAuthInterceptor vcdInterceptor;
	private static final String SSO_REGISTRY_URL = "/component-registry/endpoints/types/sso";
	private static final String AUTHORIZATION_SERVICE_URL_VRA_8 = "/csp/gateway/am/api/login";
	private static final String AUTHORIZATION_SERVICE_URL_VRA_CLOUD = "/csp/gateway/am/api/auth/api-tokens/authorize";
	private static final String SAAS_URL = "/SAAS/t/";
	private static final String VERSION_URL = "vco/api/about";
	private static final String VRA_9_VERSION_PREFIX = "9.";

	public RestClientVroSsoAuthNInterceptor(ConfigurationVro configuration, RestTemplate restTemplate) {
		super(configuration, restTemplate);

		this.ssoAuth = new VraSsoAuth(configuration, restTemplate);
		this.createVcdInterceptor(configuration, restTemplate);
	}

	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) {
		try {
			// blacklisted paths should not be intercepted in order to avoid interception
			//
			// loops

			if (this.ssoAuth.getVersion().startsWith(VRA_9_VERSION_PREFIX)) {
				return this.vcdInterceptor.intercept(request, body, execution);
			}

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

		return requestPath.contains(SSO_REGISTRY_URL)
				|| requestPath.contains(SAAS_URL)
				|| requestPath.contains(VERSION_URL)
				|| requestPath.contains(AUTHORIZATION_SERVICE_URL_VRA_8)
				|| requestPath.contains(AUTHORIZATION_SERVICE_URL_VRA_CLOUD);
	}

	private void createVcdInterceptor(ConfigurationVro configuration, RestTemplate restTemplate) {
		if (!this.ssoAuth.getVersion().startsWith(VRA_9_VERSION_PREFIX)) {
			return;
		}

		Properties properties = new Properties();

		properties.setProperty("username",
				String.format("%s@%s", configuration.getUsername(), configuration.getDomain()));
		properties.setProperty("password", configuration.getPassword());
		properties.setProperty("port", configuration.getPort() + "");
		properties.setProperty("host", configuration.getHost());

		ConfigurationVcd configurationVcd = ConfigurationVcd.fromProperties(properties);

		RestClientVcd versionRestClient = new RestClientVcd(configurationVcd, restTemplate);
		// vCD API version is passed to Content-Type headers.
		// No authentication is required to obtain API version
		String apiVersion = versionRestClient.getVersion();
		this.vcdInterceptor = new RestClientVcdBasicAuthInterceptor(
				configurationVcd, restTemplate, apiVersion);
	}
}
