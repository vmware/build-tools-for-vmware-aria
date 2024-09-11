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
package com.vmware.pscoe.iac.artifact.rest.model;

import java.util.List;

public class VraPackageDTO {

	// "{\"name\" : \"Demo3\", \"description\" : \"Demo1 Description\", \"contents\" : [\"5c46fe1f-20a6-471a-bd92-6d9458baad00\"] }"

	private final String name;
	private final String description;
	private final List<String> contents;

	public VraPackageDTO(String name, List<String> contents) {
		this.name = name;
		this.description = "Managed by IaaC for vRealize";
		this.contents = contents;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public List<String> getContents() {
		return contents;
	}

}
