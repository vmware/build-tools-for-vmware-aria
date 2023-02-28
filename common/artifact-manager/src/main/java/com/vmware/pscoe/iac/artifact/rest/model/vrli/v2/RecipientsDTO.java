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
@JsonPropertyOrder({ "emails", "vrops", "webhookIds" })
public class RecipientsDTO {

	@JsonProperty("emails")
	private String[] emails;

	@JsonProperty("vrops")
	private VropsDTO vrops;

	@JsonProperty("webhookIds")
	private String[] webhookIds;

	@JsonProperty("emails")
	public String[] getEmails() {
		return emails;
	}

	@JsonProperty("emails")
	public void setEmails(String[] emails) {
		this.emails = emails;
	}

	@JsonProperty("vrops")
	public VropsDTO getVrops() {
		return vrops;
	}

	@JsonProperty("vrops")
	public void setVrops(VropsDTO vrops) {
		this.vrops = vrops;
	}

	@JsonProperty("webhookIds")
	public String[] getWebhookIds() {
		return webhookIds;
	}

	@JsonProperty("webhookIds")
	public void setWebhookIds(String[] webhookIds) {
		this.webhookIds = webhookIds;
	}
}
