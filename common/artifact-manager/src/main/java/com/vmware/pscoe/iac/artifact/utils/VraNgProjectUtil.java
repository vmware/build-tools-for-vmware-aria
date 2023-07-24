package com.vmware.pscoe.iac.artifact.utils;

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

import com.vmware.pscoe.iac.artifact.rest.RestClientVraNg;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public final class VraNgProjectUtil {

    private VraNgProjectUtil() { }

	/**
	 * Fixes the project id in the given object with the one set in the configuration.
	 * @param restClient
	 * @param customResourceJsonElement
	 * @param projectIdKey
	 */
	public static void changeProjectIdBetweenOrganizations(final RestClientVraNg restClient, final JsonObject customResourceJsonElement, final String projectIdKey) {
		String defaultProjectId = restClient.getProjectId();
		if (defaultProjectId != null
			&& customResourceJsonElement.get(projectIdKey) != null
			&& !customResourceJsonElement.get(projectIdKey).getAsString().equals("")) {
			customResourceJsonElement.remove(projectIdKey);
			customResourceJsonElement.add(projectIdKey, new JsonPrimitive(defaultProjectId));
		} else {
			customResourceJsonElement.remove(projectIdKey);
        }
	}
}
