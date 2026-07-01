package com.vmware.pscoe.iac.artifact.vcf.automation.models;

/*-
 * #%L
 * artifact-manager
 * %%
 * Copyright (C) 2023 - 2025 VMware
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
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import com.vmware.pscoe.iac.artifact.common.annotation.SkipExport;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VcfaVroWorkflow implements Identifiable {
    private String id;
    private String name;
    private String description;
    private String content;
    private String styles;
    @SkipExport
    private String projectId;

    @JsonProperty("requestScopeOrg")
    private Boolean requestScopeOrg;

    public VcfaVroWorkflow() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getStyles() {
        return styles;
    }

    public void setStyles(String styles) {
        this.styles = styles;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public Boolean getRequestScopeOrg() {
        return requestScopeOrg;
    }

    public void setRequestScopeOrg(Boolean requestScopeOrg) {
        this.requestScopeOrg = requestScopeOrg;
    }

    /**
     * Converts the blueprint fields into a Map for API payload transmission.
     */
    public Map<String, Object> toMap() {
        java.util.Map<String, Object> map = new java.util.HashMap<>();
        map.put("id", id);
        map.put("name", name);
        map.put("description", description);
        map.put("content", content);
        map.put("styles", styles);
        map.put("projectId", projectId);
        map.put("requestScopeOrg", requestScopeOrg);
        return map;
    }

    /**
     * Returns a Map of all fields, including those normally excluded by
     * annotations.
     */
    public Map<String, Object> asExportMap() {
        return toMap();
    }

}
