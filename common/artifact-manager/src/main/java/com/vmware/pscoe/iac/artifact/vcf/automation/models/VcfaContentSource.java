package com.vmware.pscoe.iac.artifact.vcf.automation.models;

/*-
 * #%L
 * artifact-manager
 * %%
 * Copyright (C) 2023 - 2026 VMware
 * %%
 * Build Tools for VMware Aria
 * Copyright 2023 VMware, Inc.
 * 
 * This product is licensed to you under the BSD-2 license (the "License"). You may not use this product except in compliance with the BSD-2 License.
 * 
 * This product may include a number of subcomponents with separate copyright notices and license terms. Your use of these subcomponents is subject to the terms and conditions of the subcomponent's license, as noted in the LICENSE file.
 * #L%
 */

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true) 
public class VcfaContentSource implements Identifiable {
    private String id;
    private String name;
    private VcfaSourceType type;
    private String description;
    private String projectId;
    private Map<String, Object> config;

    // --- New VCF 9 Properties Captured from Server ---
    private String sourceProjectId;
    private List<String> projectIds;
    private String createdAt;
    private String createdBy;
    private String lastUpdatedAt;
    private String lastUpdatedBy;
    private String iconId;
    private Integer bulkRequestLimit;
    private Boolean global;
    private Boolean isRequestable;

    public VcfaContentSource() {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class VcfaSourceType {
        private String id;
        private String name;

        public VcfaSourceType() {}
        public VcfaSourceType(String id, String name) {
            this.id = id;
            this.name = name;
        }
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    // --- Standard Accessors ---
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public VcfaSourceType getType() { return type; }
    public void setType(VcfaSourceType type) { this.type = type; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getProjectId() { return projectId; }
    public void setProjectId(String projectId) { this.projectId = projectId; }

    public Map<String, Object> getConfig() { return config; }
    public void setConfig(Map<String, Object> config) { this.config = config; }

    // --- New Accessors ---
    public String getSourceProjectId() { return sourceProjectId; }
    public void setSourceProjectId(String sourceProjectId) { this.sourceProjectId = sourceProjectId; }

    public List<String> getProjectIds() { return projectIds; }
    public void setProjectIds(List<String> projectIds) { this.projectIds = projectIds; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public String getLastUpdatedAt() { return lastUpdatedAt; }
    public void setLastUpdatedAt(String lastUpdatedAt) { this.lastUpdatedAt = lastUpdatedAt; }

    public String getLastUpdatedBy() { return lastUpdatedBy; }
    public void setLastUpdatedBy(String lastUpdatedBy) { this.lastUpdatedBy = lastUpdatedBy; }

    public String getIconId() { return iconId; }
    public void setIconId(String iconId) { this.iconId = iconId; }

    public Integer getBulkRequestLimit() { return bulkRequestLimit; }
    public void setBulkRequestLimit(Integer bulkRequestLimit) { this.bulkRequestLimit = bulkRequestLimit; }

    public Boolean getGlobal() { return global; }
    public void setGlobal(Boolean global) { this.global = global; }

    public Boolean getIsRequestable() { return isRequestable; }
    public void setIsRequestable(Boolean isRequestable) { this.isRequestable = isRequestable; }
}
