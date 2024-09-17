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

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.hc.core5.net.URIBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import com.vmware.pscoe.iac.artifact.configuration.Configuration;

public abstract class RestClient {

	protected abstract Configuration getConfiguration();

	protected URIBuilder getURIBuilder() {
		URIBuilder uriBuilder = new URIBuilder().setScheme("https").setHost(getConfiguration().getHost());
		int port = getConfiguration().getPort();
		if (port != 443) {
			// set the port only if not default, otherwise vRA Cloud complains about invalid
			// Host header
			uriBuilder = uriBuilder.setPort(port);
		}

		return uriBuilder;
	}

	protected URI getURI(URIBuilder builder) {
		try {
			return builder.build();
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	public abstract String getVersion();

	protected static HttpEntity<String> getDefaultHttpEntity() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
		return new HttpEntity<String>(headers);
	}

}
