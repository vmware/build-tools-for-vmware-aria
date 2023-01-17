package com.vmware.pscoe.iac.artifact.utils;

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

import com.vmware.pscoe.iac.artifact.configuration.ConfigurationVraNg;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgOrganization;
import com.vmware.pscoe.iac.artifact.rest.RestClientVraNgPrimitive;
import org.apache.commons.lang3.StringUtils;

public class VraNgOrganizationUtil {

    private VraNgOrganizationUtil() {}

    public static VraNgOrganization getOrganization(RestClientVraNgPrimitive restClient, ConfigurationVraNg config) {
        VraNgOrganization orgByName = null, orgById = null;
        if (StringUtils.isNotEmpty(config.getOrgId())) {
            orgById = restClient.getOrganizationById(config.getOrgId());
        }
        if (StringUtils.isNotEmpty(config.getOrgName())) {
            orgByName = restClient.getOrganizationByName(config.getOrgName());
        }
        if(orgByName == null && orgById == null) {
            throw new RuntimeException(String.format("Couldn't find organization by the provided criteria - ID '%s' or Name '%s'.",
                    config.getOrgId(), config.getOrgName()));
        }
        if(orgByName != null && orgById != null && !orgByName.getId().equalsIgnoreCase(orgById.getId())) {
            throw new RuntimeException("Organization ID and Organization Name provided from the configuration refer to different Organizations.");
        }

        return orgById != null ? orgById : orgByName;
    }
}
