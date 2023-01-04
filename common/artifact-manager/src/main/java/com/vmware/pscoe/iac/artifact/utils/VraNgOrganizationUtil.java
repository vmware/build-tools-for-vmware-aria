package com.vmware.pscoe.iac.artifact.utils;

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
