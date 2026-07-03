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

import java.util.Map;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class VcfaPolicy implements Identifiable {

    private String id;
    private String name;
    private String typeId;
    private String enforcementType;
    private String orgId;
    private String projectId;
    private String description;
    private Map<String, Object> scopeCriteria;
    private Map<String, Object> definition;
    private Map<String, Object> criteria;

    public VcfaPolicy() {
    }

    // =========================================================================
    // IDENTIFIABLE INTERFACE OVERRIDES
    // =========================================================================

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // =========================================================================
    // ADDITIONAL GETTERS AND SETTERS
    // =========================================================================

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getEnforcementType() {
        return enforcementType;
    }

    public void setEnforcementType(String enforcementType) {
        this.enforcementType = enforcementType;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public Map<String, Object> getDefinition() {
        return definition;
    }

    public void setDefinition(Map<String, Object> definition) {
        this.definition = definition;
    }

    public Map<String, Object> getCriteria() {
        return criteria;
    }

    public void setCriteria(Map<String, Object> criteria) {
        this.criteria = criteria;
    }

    public Map<String, Object> getScopeCriteria() {
        return scopeCriteria;
    }

    public void setScopeCriteria(Map<String, Object> scopeCriteria) {
        this.scopeCriteria = scopeCriteria;
    }
}
