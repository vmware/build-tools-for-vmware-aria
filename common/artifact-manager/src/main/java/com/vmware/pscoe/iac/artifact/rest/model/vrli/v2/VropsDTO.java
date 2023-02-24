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
@JsonPropertyOrder({"vcopsResourceName", "vcopsCriticality", "autoClearAlertAfterTimeout", "autoClearAlertsTimeoutMinutes"})

public class VropsDTO {
	@JsonProperty("vcopsResourceName")
	private String vcopsResourceName;

	@JsonProperty("vcopsResourceKindKey")
	private String vcopsResourceKindKey;

	@JsonProperty("vcopsCriticality")
	private String vcopsCriticality;

	@JsonProperty("autoClearAlertAfterTimeout")
	private boolean autoClearAlertAfterTimeout;

	@JsonProperty("autoClearAlertsTimeoutMinutes")
	private long autoClearAlertsTimeoutMinutes;

	@JsonProperty("vcopsResourceName")
	public String getVcopsResourceName() {
		return vcopsResourceName;
	}

	@JsonProperty("vcopsResourceName")
	public void setVcopsResourceName(String vcopsResourceName) {
		this.vcopsResourceName = vcopsResourceName;
	}

	@JsonProperty("vcopsCriticality")
	public String getVcopsCriticality() {
		return vcopsCriticality;
	}

	@JsonProperty("vcopsCriticality")
	public void setVcopsCriticality(String vcopsCriticality) {
		this.vcopsCriticality = vcopsCriticality;
	}

	@JsonProperty("autoClearAlertAfterTimeout")
	public boolean isAutoClearAlertAfterTimeout() {
		return autoClearAlertAfterTimeout;
	}

	@JsonProperty("autoClearAlertAfterTimeout")
	public void setAutoClearAlertAfterTimeout(boolean autoClearAlertAfterTimeout) {
		this.autoClearAlertAfterTimeout = autoClearAlertAfterTimeout;
	}

	@JsonProperty("autoClearAlertsTimeoutMinutes")
	public long getAutoClearAlertsTimeoutMinutes() {
		return autoClearAlertsTimeoutMinutes;
	}

	@JsonProperty("autoClearAlertsTimeoutMinutes")
	public void setAutoClearAlertsTimeoutMinutes(long autoClearAlertsTimeoutMinutes) {
		this.autoClearAlertsTimeoutMinutes = autoClearAlertsTimeoutMinutes;
	}
	@JsonProperty("vcopsResourceKindKey")
	public String getVcopsResourceKindKey() {
		return vcopsResourceKindKey;
	}
	@JsonProperty("vcopsResourceKindKey")
	public void setVcopsResourceKindKey(String vcopsResourceKindKey) {
		this.vcopsResourceKindKey = vcopsResourceKindKey;
	}
}
