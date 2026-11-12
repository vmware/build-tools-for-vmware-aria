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
package com.vmware.pscoe.iac.artifact.vcd.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.web.client.RestTemplate;

import com.vmware.pscoe.iac.artifact.common.configuration.Configuration;
import com.vmware.pscoe.iac.artifact.vcd.configuration.ConfigurationVcd;

/**
 * Unit tests for RestClientVcdBasicAuthInterceptor.
 */
class RestClientVcdBasicAuthInterceptorTest {

	private static final String VRO_HOST = "vro.corp.internal";
	private static final String AUTH_HOST = "vcfa.corp.internal";
	private static final String ORG_TOKEN = "org-scoped-token";

	/**
	 * A provider admin (user@System) working with an external vRO must get an
	 * organization scoped token, and all token related calls must go to the auth
	 * host (VCFA), since the external vRO does not serve cloudapi / oidc.
	 */
	@Test
	void testExternalVroProviderAdminUsesOrgScopedTokenFromAuthHost() throws Exception {
		Properties props = new Properties();
		props.setProperty(Configuration.HOST, VRO_HOST);
		props.setProperty(Configuration.PORT, "443");
		props.setProperty(Configuration.AUTH_HOST, AUTH_HOST);
		props.setProperty(Configuration.AUTH_PORT, "443");
		props.setProperty(Configuration.USERNAME, "admin@System");
		props.setProperty(Configuration.PASSWORD, "secret");
		props.setProperty(ConfigurationVcd.ORGANIZATION_NAME, "vmw-emea-mgmt");
		ConfigurationVcd configuration = ConfigurationVcd.fromProperties(props);

		RestTemplate restTemplate = mock(RestTemplate.class);
		List<URI> calledUris = new ArrayList<>();

		when(restTemplate.exchange(any(URI.class), any(HttpMethod.class), any(HttpEntity.class), eq(String.class)))
				.thenAnswer(invocation -> {
					URI uri = invocation.getArgument(0);
					calledUris.add(uri);
					String path = uri.getPath();
					if (path.endsWith(RestClientVcd.URL_VERSIONS)) {
						HttpHeaders headers = new HttpHeaders();
						headers.setContentType(MediaType.TEXT_HTML);
						return new ResponseEntity<>("<html></html>", headers, HttpStatus.OK);
					} else if (path.endsWith("/cloudapi/1.0.0/sessions/provider")) {
						HttpHeaders headers = new HttpHeaders();
						headers.add("x-vmware-vcloud-access-token", "provider-token");
						return new ResponseEntity<>("{}", headers, HttpStatus.OK);
					} else if (path.endsWith("openIdProvider/relyingParties")) {
						return new ResponseEntity<>(
								"{\"values\":[{\"clientName\":\"automation-relying-party\",\"clientId\":\"cid\"}]}",
								HttpStatus.OK);
					} else if (path.endsWith("/cloudapi/1.0.0/orgs")) {
						return new ResponseEntity<>(
								"{\"values\":[{\"name\":\"vmw-emea-mgmt\",\"id\":\"urn:vcloud:org:123\"}]}",
								HttpStatus.OK);
					} else if (path.endsWith("/oidc/oauth2/token")) {
						return new ResponseEntity<>("{\"access_token\":\"" + ORG_TOKEN + "\"}", HttpStatus.OK);
					}
					throw new IllegalStateException("Unexpected call: " + uri);
				});

		RestClientVcdBasicAuthInterceptor interceptor = new RestClientVcdBasicAuthInterceptor(configuration,
				restTemplate, RestClientVcd.API_VERSION_40, true);

		HttpHeaders requestHeaders = new HttpHeaders();
		HttpRequest request = mock(HttpRequest.class);
		when(request.getURI()).thenReturn(URI.create("https://" + VRO_HOST + ":443/vco/api/packages"));
		when(request.getMethod()).thenReturn(HttpMethod.GET);
		when(request.getHeaders()).thenReturn(requestHeaders);

		ClientHttpRequestExecution execution = mock(ClientHttpRequestExecution.class);

		interceptor.intercept(request, new byte[0], execution);

		assertEquals("Bearer " + ORG_TOKEN, requestHeaders.getFirst("Authorization"));
		// all calls apart from the external vRO detection go to the auth host
		calledUris.stream()
				.filter(uri -> !uri.getPath().endsWith(RestClientVcd.URL_VERSIONS))
				.forEach(uri -> assertEquals(AUTH_HOST, uri.getHost(), "Wrong host for " + uri));
		assertTrue(calledUris.stream().anyMatch(uri -> uri.getPath().endsWith("/oidc/oauth2/token")));
	}
}
