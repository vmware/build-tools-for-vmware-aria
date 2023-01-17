package com.vmware.pscoe.iac.artifact.rest;

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
