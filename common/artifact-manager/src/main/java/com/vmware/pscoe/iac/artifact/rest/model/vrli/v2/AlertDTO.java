
package com.vmware.pscoe.iac.artifact.rest.model.vrli.v2;

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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "id", "enabled", "type", "name", "hitCount", "hitOperator", "searchPeriod", "searchInterval",
	"info", "recommendation", "contentPackNamespace", "contentPackName", "recipients", "stats", "ownerUuid",
	"ownerName", "query" })
public class AlertDTO implements Serializable {
    private static final long serialVersionUID = 1913969984744233760L;

    @JsonProperty("id")
    private String id;

    @JsonProperty("enabled")
    private boolean enabled;

	@JsonProperty("type")
	private String type;

	@JsonProperty("name")
	private String name;

	@JsonProperty("hitCount")
	private long hitCount;

	@JsonProperty("hitOperator")
	private String hitOperator;

	@JsonProperty("searchPeriod")
	private long searchPeriod;

	@JsonProperty("searchInterval")
	private long searchInterval;

	@JsonProperty("info")
	private String info;

	@JsonProperty("recommendation")
	private String recommendation;

	@JsonProperty("contentPackNamespace")
	private String contentPackNamespace;

	@JsonProperty("contentPackName")
	private String contentPackName;

	@JsonProperty("recipients")
	private RecipientsDTO recipients;

	@JsonProperty("stats")
	private StatsDTO stats;

	@JsonProperty("ownerUuid")
	private String ownerUuid;

	@JsonProperty("ownerName")
	private String ownerName;

	@JsonProperty("query")
	private QueryDTO query;

	@JsonProperty("id")
	public String getId() {
		return id;
	}

	@JsonProperty("id")
	public void setId(String id) {
		this.id = id;
	}

	@JsonProperty("enabled")
	public boolean isEnabled() {
		return enabled;
	}

	@JsonProperty("enabled")
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@JsonProperty("type")
	public String getType() {
		return type;
	}

	@JsonProperty("type")
	public void setType(String type) {
		this.type = type;
	}

	@JsonProperty("name")
	public String getName() {
		return name;
	}

	@JsonProperty("name")
	public void setName(String name) {
		this.name = name;
	}

	@JsonProperty("hitCount")
	public long getHitCount() {
		return hitCount;
	}

	@JsonProperty("hitCount")
	public void setHitCount(long hitCount) {
		this.hitCount = hitCount;
	}

	@JsonProperty("searchPeriod")
	public long getSearchPeriod() {
		return searchPeriod;
	}

	@JsonProperty("searchPeriod")
	public void setSearchPeriod(long searchPeriod) {
		this.searchPeriod = searchPeriod;
	}

	@JsonProperty("searchInterval")
	public long getSearchInterval() {
		return searchInterval;
	}

	@JsonProperty("searchInterval")
	public void setSearchInterval(long searchInterval) {
		this.searchInterval = searchInterval;
	}

	@JsonProperty("info")
	public String getInfo() {
		return info;
	}

	@JsonProperty("info")
	public void setInfo(String info) {
		this.info = info;
	}

	@JsonProperty("recipients")
	public RecipientsDTO getRecipients() {
		return recipients;
	}

	@JsonProperty("recipients")
	public void setRecipients(RecipientsDTO recipients) {
		this.recipients = recipients;
	}

	@JsonProperty("stats")
	public StatsDTO getStats() {
		return stats;
	}

	@JsonProperty("stats")
	public void setStats(StatsDTO stats) {
		this.stats = stats;
	}

	@JsonProperty("ownerUuid")
	public String getOwnerUuid() {
		return ownerUuid;
	}

	@JsonProperty("ownerUuid")
	public void setOwnerUuid(String ownerUuid) {
		this.ownerUuid = ownerUuid;
	}

	@JsonProperty("query")
	public QueryDTO getQuery() {
		return query;
	}

	@JsonProperty("query")
	public void setQuery(QueryDTO query) {
		this.query = query;
	}

	@JsonProperty("hitOperator")
	public String getHitOperator() {
		return hitOperator;
	}

	@JsonProperty("hitOperator")
	public void setHitOperator(String hitOperator) {
		this.hitOperator = hitOperator;
	}
	@JsonProperty("ownerName")
	public String getOwnerName() {
		return ownerName;
	}
	@JsonProperty("ownerName")
	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}
	@JsonProperty("contentPackNamespace")
	public String getContentPackNamespace() {
		return contentPackNamespace;
	}
	@JsonProperty("contentPackNamespace")
	public void setContentPackNamespace(String contentPackNamespace) {
		this.contentPackNamespace = contentPackNamespace;
	}

	@JsonProperty("contentPackName")
	public String getContentPackName() {
		return contentPackName;
	}
	@JsonProperty("contentPackName")
	public void setContentPackName(String contentPackName) {
		this.contentPackName = contentPackName;
	}
	@JsonProperty("recommendation")
	public String getRecommendation() {
		return recommendation;
	}
	@JsonProperty("recommendation")
	public void setRecommendation(String recommendation) {
		this.recommendation = recommendation;
	}
}
