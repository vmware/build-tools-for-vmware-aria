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
public class VcfaCustomResourceType implements Identifiable {

    private String id;
    private String displayName;
    private String description;
    private String resourceType;
    private String externalType;
    private String status;
    private String orgId;
    private String schemaType;
    private Map<String, Object> mainActions;
    private List<Map<String, Object>> additionalActions;
    private Map<String, Object> properties;

    public VcfaCustomResourceType() {
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

    /**
     * Using resourceType (e.g. 'Custom.LoadBalancer') as the tracking Name.
     * This protects configurations from breaking if a user alters the cosmetic displayName.
     */
    @Override
    public String getName() {
        return resourceType;
    }

    public void setName(String name) {
        this.resourceType = name;
    }

    // =========================================================================
    // GETTERS AND SETTERS FOR REMAINING FIELDS
    // =========================================================================

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getExternalType() {
        return externalType;
    }

    public void setExternalType(String externalType) {
        this.externalType = externalType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getSchemaType() {
        return schemaType;
    }

    public void setSchemaType(String schemaType) {
        this.schemaType = schemaType;
    }

    public Map<String, Object> getMainActions() {
        return mainActions;
    }

    public void setMainActions(Map<String, Object> mainActions) {
        this.mainActions = mainActions;
    }

    public List<Map<String, Object>> getAdditionalActions() {
        return additionalActions;
    }

    public void setAdditionalActions(List<Map<String, Object>> additionalActions) {
        this.additionalActions = additionalActions;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }
}
