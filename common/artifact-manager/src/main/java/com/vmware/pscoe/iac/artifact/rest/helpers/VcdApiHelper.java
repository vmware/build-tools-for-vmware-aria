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
package com.vmware.pscoe.iac.artifact.rest.helpers;

import java.nio.charset.Charset;
import java.util.Base64;

import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;

public final class VcdApiHelper {

	private VcdApiHelper() {
	};

	private static final Charset UTF_8 = Charset.forName("UTF-8");

	public static MediaType buildMediaType(String contentType, String apiVersion) {
		if (apiVersion == null) {
			return MediaType.parseMediaType(contentType);
		} else {
			return MediaType.parseMediaType(contentType + ";version=" + apiVersion);
		}
	}

	public static String buildBearerToken(String token) {
		return "Bearer " + token;
	}

	public static String buildBasicAuth(String user, String domain, String password) {
		if (StringUtils.isEmpty(domain)) {
			throw new RuntimeException("Please provide a user with an organization in the form [user@org]");
		}

		String username = user + "@" + domain;
		String auth = Base64.getEncoder().encodeToString((username + ":" + password).getBytes(UTF_8));

		return "Basic " + auth;
	}
}
