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

import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class VcfaCatalogItem implements Identifiable {

    private String id;
    private String name;
    private String description;
    private Map<String, Object> type;
    
    // --- ADD THIS FIELD HERE ---
    private String sourceId;
    
    private String sourceProjectId;
    private List<Map<String, Object>> projects;
    private String iconId;
    private Boolean global;
    private Integer bulkRequestLimit;
    private List<Map<String, Object>> additionalActions;
    private Boolean isRequestable;

    public VcfaCatalogItem() {}

    @Override
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    @Override
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    // --- ADD THESE GETTER AND SETTER METHODS ---
    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    // =========================================================================
    // REMAINING GETTERS AND SETTERS
    // =========================================================================

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Map<String, Object> getType() { return type; }
    public void setType(Map<String, Object> type) { this.type = type; }

    public String getSourceProjectId() { return sourceProjectId; }
    public void setSourceProjectId(String sourceProjectId) { this.sourceProjectId = sourceProjectId; }

    public List<Map<String, Object>> getProjects() { return projects; }
    public void setProjects(List<Map<String, Object>> projects) { this.projects = projects; }

    public String getIconId() { return iconId; }
    public void setIconId(String iconId) { this.iconId = iconId; }

    public Boolean getGlobal() { return global; }
    public void setGlobal(Boolean global) { this.global = global; }

    public Integer getBulkRequestLimit() { return bulkRequestLimit; }
    public void setBulkRequestLimit(Integer bulkRequestLimit) { this.bulkRequestLimit = bulkRequestLimit; }

    public List<Map<String, Object>> getAdditionalActions() { return additionalActions; }
    public void setAdditionalActions(List<Map<String, Object>> additionalActions) { this.additionalActions = additionalActions; }

    public Boolean getIsRequestable() { return isRequestable; }
    public void setIsRequestable(Boolean isRequestable) { this.isRequestable = isRequestable; }
}
