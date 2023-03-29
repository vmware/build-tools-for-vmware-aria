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
@JsonPropertyOrder({ "lastRanAt", "nextRunAt", "runCount", "lastRunTime", "totalRunTime", "lastHitAt" })

public class StatsDTO {
	@JsonProperty("lastRanAt")
	private long lastRanAt;

	@JsonProperty("nextRunAt")
	private long nextRunAt;

	@JsonProperty("runCount")
	private long runCount;

	@JsonProperty("lastRunTime")
	private long lastRunTime;

	@JsonProperty("totalRunTime")
	private long totalRunTime;

	@JsonProperty("lastHitAt")
	private long lastHitAt;

	@JsonProperty("lastRanAt")
	public long getLastRanAt() {
		return lastRanAt;
	}

	@JsonProperty("lastRanAt")
	public void setLastRanAt(long lastRanAt) {
		this.lastRanAt = lastRanAt;
	}

	@JsonProperty("nextRunAt")
	public long getNextRunAt() {
		return nextRunAt;
	}

	@JsonProperty("nextRunAt")
	public void setNextRunAt(long nextRunAt) {
		this.nextRunAt = nextRunAt;
	}

	@JsonProperty("runCount")
	public long getRunCount() {
		return runCount;
	}

	@JsonProperty("runCount")
	public void setRunCount(long runCount) {
		this.runCount = runCount;
	}

	@JsonProperty("lastRunTime")
	public long getLastRunTime() {
		return lastRunTime;
	}

	@JsonProperty("lastRunTime")
	public void setLastRunTime(long lastRunTime) {
		this.lastRunTime = lastRunTime;
	}

	@JsonProperty("totalRunTime")
	public long getTotalRunTime() {
		return totalRunTime;
	}

	@JsonProperty("totalRunTime")
	public void setTotalRunTime(long totalRunTime) {
		this.totalRunTime = totalRunTime;
	}

	@JsonProperty("lastHitAt")
	public long getLastHitAt() {
		return lastHitAt;
	}

	@JsonProperty("lastHitAt")
	public void setLastHitAt(long lastHitAt) {
		this.lastHitAt = lastHitAt;
	}
}
