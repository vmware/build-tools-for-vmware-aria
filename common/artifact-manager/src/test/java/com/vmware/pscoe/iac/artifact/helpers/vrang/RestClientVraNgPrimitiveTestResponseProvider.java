package com.vmware.pscoe.iac.artifact.helpers.vrang;

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

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class RestClientVraNgPrimitiveTestResponseProvider {


	/**
	 * Generates a page of vRA projects
	 * Refer to "/iaas/api/projects"
	 * @param totalElements sum of all elements
	 * @param pageSize maximum elements on a page
	 * @param page page number starting from 0
	 * @return ResponseEntity containing a page with the requested number of projects
	 */
	public static ResponseEntity<String> getPaginatedProjectResponse(int totalElements, int pageSize, int page) {

		StringBuilder builder = new StringBuilder();
		builder.append("{\"content\": [");

		// generate elements for the current page
		int skip = page * pageSize;
		int max = Math.min(skip + pageSize, totalElements);
		for (int i = skip; i < max; i++) {
			builder.append(
				String.format("{\"name\": \"project\", \"id\": \"%d\"}%s", i, (i == max - 1 ? "" : ",")));
		}

		int numberOfElements = Math.min((totalElements - (page * pageSize)), pageSize);

		builder.append("],");
		builder.append(String.format("\"totalElements\": %d,", totalElements));
		builder.append(String.format("\"numberOfElements\": %d", numberOfElements));
		builder.append("}");

		return new ResponseEntity<>(builder.toString(), HttpStatus.OK);
	}
}
