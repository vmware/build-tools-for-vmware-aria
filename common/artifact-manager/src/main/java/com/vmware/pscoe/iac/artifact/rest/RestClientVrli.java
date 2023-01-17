package com.vmware.pscoe.iac.artifact.rest;

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

import java.util.List;

import org.springframework.web.client.RestTemplate;

import com.vmware.pscoe.iac.artifact.configuration.ConfigurationVrli;
import com.vmware.pscoe.iac.artifact.rest.model.vrli.AlertDTO;
import com.vmware.pscoe.iac.artifact.rest.model.vrli.ContentPackDTO;

public class RestClientVrli extends RestClientVrliPrimitive {

    protected RestClientVrli(ConfigurationVrli configuration, RestTemplate restTemplate) {
        super(configuration, restTemplate);
    }

    public String getVrliVersion() {
        return getVersion();
    }

    public List<AlertDTO> getAllAlerts() {
        return getAllAlertsPrimitive();
    }

    public void importAlert(String alertAsJson) {
        importAlertPrimitive(alertAsJson);
    }

    public List<ContentPackDTO> getAllContentPacks() {
        return getAllContentPacksPrimitive();
    }

    public String getContentPack(String contentPackNameSpace) {
        return getContentPackPrimitive(contentPackNameSpace);
    }

    public void importContentPack(String contentPackName, String contentPackJson) {
        importContentPackPrimitive(contentPackName, contentPackJson);
    }

}
