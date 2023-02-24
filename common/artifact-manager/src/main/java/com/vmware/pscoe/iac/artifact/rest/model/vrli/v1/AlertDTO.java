
package com.vmware.pscoe.iac.artifact.rest.model.vrli.v1;

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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "id", "enabled", "emailEnabled", "vcopsEnabled", "alertType", "name", "hitCount", "hitOperator", "searchPeriod", "searchInterval",
        "emails", "info", "recommendation", "vcopsResourceName", "vcopsResourceKindKey", "vcopsCriticality", "lastRanAt", "lastRanAtString", "nextRunAt",
        "nextRunAtString", "runCount", "lastRunTime", "totalRunTime", "lastHitTimestamp", "lastHitTimestampString", "ownerUuid", "ownerName", "webhookEnabled",
        "webhookURLs", "autoClearAlertAfterTimeout", "chartQuery", "messageQuery", "autoClearAlertsTimeoutMinutes" })
public class AlertDTO implements Serializable {
    private static final long serialVersionUID = 1913969984744233760L;

    @JsonProperty("id")
    private String id;

    @JsonProperty("enabled")
    private boolean enabled;

    @JsonProperty("emailEnabled")
    private boolean emailEnabled;

    @JsonProperty("vcopsEnabled")
    private boolean vcopsEnabled;

    @JsonProperty("alertType")
    private String alertType;

    @JsonProperty("name")
    private String name;

    @JsonProperty("hitCount")
    private double hitCount;

    @JsonProperty("hitOperator")
    private String hitOperator;

    @JsonProperty("searchPeriod")
    private long searchPeriod;

    @JsonProperty("searchInterval")
    private long searchInterval;

    @JsonProperty("emails")
    private String emails;

    @JsonProperty("info")
    private String info;

    @JsonProperty("recommendation")
    private String recommendation;

    @JsonProperty("vcopsResourceName")
    private String vcopsResourceName;

    @JsonProperty("vcopsResourceKindKey")
    private String vcopsResourceKindKey;

    @JsonProperty("vcopsCriticality")
    private String vcopsCriticality;

    @JsonProperty("lastRanAt")
    private long lastRanAt;

    @JsonProperty("lastRanAtString")
    private String lastRanAtString;

    @JsonProperty("nextRunAt")
    private long nextRunAt;

    @JsonProperty("nextRunAtString")
    private String nextRunAtString;

    @JsonProperty("runCount")
    private long runCount;

    @JsonProperty("lastRunTime")
    private long lastRunTime;

    @JsonProperty("totalRunTime")
    private long totalRunTime;

    @JsonProperty("lastHitTimestamp")
    private long lastHitTimestamp;

    @JsonProperty("lastHitTimestampString")
    private String lastHitTimestampString;

    @JsonProperty("ownerUuid")
    private String ownerUuid;

    @JsonProperty("ownerName")
    private String ownerName;

    @JsonProperty("webhookEnabled")
    private boolean webhookEnabled;

    @JsonProperty("webhookURLs")
    private String webhookURLs;

    @JsonProperty("autoClearAlertAfterTimeout")
    private boolean autoClearAlertAfterTimeout;

    @JsonProperty("chartQuery")
    private String chartQuery;

    @JsonProperty("messageQuery")
    private String messageQuery;

    @JsonProperty("autoClearAlertsTimeoutMinutes")
    private int autoClearAlertsTimeoutMinutes;

    @JsonIgnore
    private transient Map<String, Object> additionalProperties = new HashMap<>();

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

    @JsonProperty("emailEnabled")
    public boolean isEmailEnabled() {
        return emailEnabled;
    }

    @JsonProperty("emailEnabled")
    public void setEmailEnabled(boolean emailEnabled) {
        this.emailEnabled = emailEnabled;
    }

    @JsonProperty("vcopsEnabled")
    public boolean isVcopsEnabled() {
        return vcopsEnabled;
    }

    @JsonProperty("vcopsEnabled")
    public void setVcopsEnabled(boolean vcopsEnabled) {
        this.vcopsEnabled = vcopsEnabled;
    }

    @JsonProperty("alertType")
    public String getAlertType() {
        return alertType;
    }

    @JsonProperty("alertType")
    public void setAlertType(String alertType) {
        this.alertType = alertType;
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
    public double getHitCount() {
        return hitCount;
    }

    @JsonProperty("hitCount")
    public void setHitCount(double hitCount) {
        this.hitCount = hitCount;
    }

    @JsonProperty("hitOperator")
    public String getHitOperator() {
        return hitOperator;
    }

    @JsonProperty("hitOperator")
    public void setHitOperator(String hitOperator) {
        this.hitOperator = hitOperator;
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

    @JsonProperty("emails")
    public String getEmails() {
        return emails;
    }

    @JsonProperty("emails")
    public void setEmails(String emails) {
        this.emails = emails;
    }

    @JsonProperty("info")
    public String getInfo() {
        return info;
    }

    @JsonProperty("info")
    public void setInfo(String info) {
        this.info = info;
    }

    @JsonProperty("recommendation")
    public String getRecommendation() {
        return recommendation;
    }

    @JsonProperty("recommendation")
    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }

    @JsonProperty("vcopsResourceName")
    public String getVcopsResourceName() {
        return vcopsResourceName;
    }

    @JsonProperty("vcopsResourceName")
    public void setVcopsResourceName(String vcopsResourceName) {
        this.vcopsResourceName = vcopsResourceName;
    }

    @JsonProperty("vcopsResourceKindKey")
    public String getVcopsResourceKindKey() {
        return vcopsResourceKindKey;
    }

    @JsonProperty("vcopsResourceKindKey")
    public void setVcopsResourceKindKey(String vcopsResourceKindKey) {
        this.vcopsResourceKindKey = vcopsResourceKindKey;
    }

    @JsonProperty("vcopsCriticality")
    public String getVcopsCriticality() {
        return vcopsCriticality;
    }

    @JsonProperty("vcopsCriticality")
    public void setVcopsCriticality(String vcopsCriticality) {
        this.vcopsCriticality = vcopsCriticality;
    }

    @JsonProperty("lastRanAt")
    public long getLastRanAt() {
        return lastRanAt;
    }

    @JsonProperty("lastRanAt")
    public void setLastRanAt(long lastRanAt) {
        this.lastRanAt = lastRanAt;
    }

    @JsonProperty("lastRanAtString")
    public String getLastRanAtString() {
        return lastRanAtString;
    }

    @JsonProperty("lastRanAtString")
    public void setLastRanAtString(String lastRanAtString) {
        this.lastRanAtString = lastRanAtString;
    }

    @JsonProperty("nextRunAt")
    public long getNextRunAt() {
        return nextRunAt;
    }

    @JsonProperty("nextRunAt")
    public void setNextRunAt(long nextRunAt) {
        this.nextRunAt = nextRunAt;
    }

    @JsonProperty("nextRunAtString")
    public String getNextRunAtString() {
        return nextRunAtString;
    }

    @JsonProperty("nextRunAtString")
    public void setNextRunAtString(String nextRunAtString) {
        this.nextRunAtString = nextRunAtString;
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

    @JsonProperty("lastHitTimestamp")
    public long getLastHitTimestamp() {
        return lastHitTimestamp;
    }

    @JsonProperty("lastHitTimestamp")
    public void setLastHitTimestamp(long lastHitTimestamp) {
        this.lastHitTimestamp = lastHitTimestamp;
    }

    @JsonProperty("lastHitTimestampString")
    public String getLastHitTimestampString() {
        return lastHitTimestampString;
    }

    @JsonProperty("lastHitTimestampString")
    public void setLastHitTimestampString(String lastHitTimestampString) {
        this.lastHitTimestampString = lastHitTimestampString;
    }

    @JsonProperty("ownerUuid")
    public String getOwnerUuid() {
        return ownerUuid;
    }

    @JsonProperty("ownerUuid")
    public void setOwnerUuid(String ownerUuid) {
        this.ownerUuid = ownerUuid;
    }

    @JsonProperty("ownerName")
    public String getOwnerName() {
        return ownerName;
    }

    @JsonProperty("ownerName")
    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    @JsonProperty("webhookEnabled")
    public boolean isWebhookEnabled() {
        return webhookEnabled;
    }

    @JsonProperty("webhookEnabled")
    public void setWebhookEnabled(boolean webhookEnabled) {
        this.webhookEnabled = webhookEnabled;
    }

    public String getWebhookURLs() {
        return webhookURLs;
    }

    public void setWebhookURLs(String webhookURLs) {
        this.webhookURLs = webhookURLs;
    }

    @JsonProperty("autoClearAlertAfterTimeout")
    public boolean isAutoClearAlertAfterTimeout() {
        return autoClearAlertAfterTimeout;
    }

    @JsonProperty("autoClearAlertAfterTimeout")
    public void setAutoClearAlertAfterTimeout(boolean autoClearAlertAfterTimeout) {
        this.autoClearAlertAfterTimeout = autoClearAlertAfterTimeout;
    }

    @JsonProperty("chartQuery")
    public String getChartQuery() {
        return chartQuery;
    }

    @JsonProperty("chartQuery")
    public void setChartQuery(String chartQuery) {
        this.chartQuery = chartQuery;
    }

    public String getMessageQuery() {
        return messageQuery;
    }

    public void setMessageQuery(String messageQuery) {
        this.messageQuery = messageQuery;
    }

    public int getAutoClearAlertsTimeoutMinutes() {
        return autoClearAlertsTimeoutMinutes;
    }

    public void setAutoClearAlertsTimeoutMinutes(int autoClearAlertsTimeoutMinutes) {
        this.autoClearAlertsTimeoutMinutes = autoClearAlertsTimeoutMinutes;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperties(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
}
