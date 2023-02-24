package com.vmware.pscoe.iac.artifact.rest.model.vrli.v2;

/*-
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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"chartQuery", "messageQuery"})

public class QueryDTO {
	@JsonProperty("chartQuery")
	private String chartQuery;

	@JsonProperty("messageQuery")
	private String messageQuery;

	@JsonProperty("chartQuery")
	public String getChartQuery() {
		return chartQuery;
	}

	@JsonProperty("chartQuery")
	public void setChartQuery(String chartQuery) {
		this.chartQuery = chartQuery;
	}

	@JsonProperty("messageQuery")
	public String getMessageQuery() {
		return messageQuery;
	}

	@JsonProperty("messageQuery")
	public void setMessageQuery(String messageQuery) {
		this.messageQuery = messageQuery;
	}
}
