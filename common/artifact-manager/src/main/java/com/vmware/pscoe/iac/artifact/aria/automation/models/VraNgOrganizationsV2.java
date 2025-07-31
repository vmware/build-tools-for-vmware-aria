
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
package com.vmware.pscoe.iac.artifact.aria.automation.models;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "results" })
public class VraNgOrganizationsV2 implements Serializable {
	private static final long serialVersionUID = -3313748896114761975L;

	@JsonProperty("results")
	private List<VraNgOrganization> results;

	@JsonProperty("results")
	public List<VraNgOrganization> getResults() {
		return results;
	}

	@JsonProperty("results")
	public void setResults(List<VraNgOrganization> results) {
		this.results = results;
	}
}
