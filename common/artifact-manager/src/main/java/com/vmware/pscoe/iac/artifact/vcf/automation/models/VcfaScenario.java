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
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class VcfaScenario implements Identifiable {

    @JsonProperty("scenarioId")
    private String id;

    @JsonProperty("scenarioName")
    private String name;

    private Boolean enabled;
    private String scenarioCategory;

    public VcfaScenario() {}

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

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getScenarioCategory() {
        return scenarioCategory;
    }

    public void setScenarioCategory(String scenarioCategory) {
        this.scenarioCategory = scenarioCategory;
    }
}
