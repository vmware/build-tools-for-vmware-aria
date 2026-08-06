package com.vmware.pscoe.iac.artifact.vcf.automation.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vmware.pscoe.iac.artifact.common.annotation.SkipExport;

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

@JsonIgnoreProperties(ignoreUnknown = true)
public class VcfaOrganizationSharing {

    private String orgId;
    @SkipExport
    private String organization;

    public VcfaOrganizationSharing() {
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }
}
